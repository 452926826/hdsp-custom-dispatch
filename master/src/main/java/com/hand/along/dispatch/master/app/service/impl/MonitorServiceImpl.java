package com.hand.along.dispatch.master.app.service.impl;

import com.hand.along.dispatch.common.constants.CommonConstant;
import com.hand.along.dispatch.common.domain.BaseMessage;
import com.hand.along.dispatch.common.domain.ExecutorInfo;
import com.hand.along.dispatch.common.utils.CommonUtil;
import com.hand.along.dispatch.common.utils.JSON;
import com.hand.along.dispatch.common.utils.RedisHelper;
import com.hand.along.dispatch.master.app.service.MonitorService;
import com.hand.along.dispatch.master.domain.monitor.MasterMonitorInfo;
import com.hand.along.dispatch.master.domain.monitor.SlaveMonitorInfo;
import com.hand.along.dispatch.master.infra.election.ElectionConfiguration;
import com.hand.along.dispatch.master.infra.handler.GraphUtil;
import com.hand.along.dispatch.master.infra.netty.NettyServer;
import com.hand.along.dispatch.master.infra.netty.NettyServerHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.hand.along.dispatch.common.constants.CommonConstant.STANDBY_MASTER;

@Service
public class MonitorServiceImpl implements MonitorService {
    private final RedisHelper redisHelper;

    @Value("${netty.server.serverPort:31020}")
    private Integer serverPort;
    @Value("${netty.server.ip-pattern}")
    private String pattern;

    public MonitorServiceImpl(RedisHelper redisHelper) {
        this.redisHelper = redisHelper;
    }

    @Override
    public List<MasterMonitorInfo> monitorInfo(Long tenantId) {
        final List<MasterMonitorInfo> masterMonitorInfoList = new ArrayList<>();
        String ip = CommonUtil.getIp(pattern);
        String currentUrl = String.format("%s:%s", ip, serverPort);
        Map<String, ExecutorInfo> slaveExecutorInfoMap = NettyServerHandler.slaveExecutorInfoMap;
        List<SlaveMonitorInfo> slaveMonitorInfoList = new ArrayList<>();
        slaveExecutorInfoMap.forEach((k, v) -> {
            slaveMonitorInfoList.add(SlaveMonitorInfo.builder()
                    .ipAddr(k)
                    .executorInfoList(Collections.singletonList(v))
                    .build());
        });
        if (ElectionConfiguration.isMaster) {
            BaseMessage baseMessage = new BaseMessage(CommonConstant.INFO);
            NettyServer.sendAll(JSON.toJson(baseMessage));
            masterMonitorInfoList.add(MasterMonitorInfo.builder()
                    .executorInfoList(Collections.singletonList(GraphUtil.getExecutorInfo()))
                    .ipAddr(currentUrl)
                    .master(true)
                    .standby(false)
                    .slaveMonitorInfoList(slaveMonitorInfoList)
                    .build());
        } else {
            Set<String> set = redisHelper.setMembers(STANDBY_MASTER);
            set.forEach(s -> {
                masterMonitorInfoList.add(MasterMonitorInfo.builder()
                        .ipAddr(s)
                        .master(false)
                        .standby(true)
                        .slaveMonitorInfoList(slaveMonitorInfoList)
                        .build());
            });

        }
        return masterMonitorInfoList;
    }
}
