package com.hand.along.dispatch.master.app.service.impl;

import com.hand.along.dispatch.common.constants.CommonConstant;
import com.hand.along.dispatch.common.domain.BaseMessage;
import com.hand.along.dispatch.common.domain.ExecutorInfo;
import com.hand.along.dispatch.common.domain.monitor.MasterMonitorInfo;
import com.hand.along.dispatch.common.domain.monitor.SlaveMonitorInfo;
import com.hand.along.dispatch.common.utils.JSON;
import com.hand.along.dispatch.master.app.service.ExecuteService;
import com.hand.along.dispatch.master.app.service.MonitorService;
import com.hand.along.dispatch.master.infra.election.CurrentMasterService;
import com.hand.along.dispatch.master.infra.election.ElectionConfiguration;
import com.hand.along.dispatch.master.infra.handler.GraphUtil;
import com.hand.along.dispatch.master.infra.netty.NettyServer;
import com.hand.along.dispatch.master.infra.netty.NettyServerHandler;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
public class MonitorServiceImpl implements MonitorService {
    private final CurrentMasterService currentMasterService;
    private final ExecuteService executeService;

    public MonitorServiceImpl(CurrentMasterService currentMasterService,
                              ExecuteService executeService) {
        this.currentMasterService = currentMasterService;
        this.executeService = executeService;
    }

    @Override
    public List<MasterMonitorInfo> monitorInfo(Long tenantId) {
        String currentUrl = currentMasterService.getCurrentIp();
        final List<MasterMonitorInfo> masterMonitorInfoList = new ArrayList<>();
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
                    .executorInfoList(Collections.singletonList(executeService.getExecutorInfo()))
                    .ipAddr(currentUrl)
                    .master(true)
                    .standby(false)
                    .slaveMonitorInfoList(slaveMonitorInfoList)
                    .build());
        } else {
            Map<String, MasterMonitorInfo> standby = currentMasterService.getStandby();
            masterMonitorInfoList.addAll(standby.values());
        }
        return masterMonitorInfoList;
    }
}
