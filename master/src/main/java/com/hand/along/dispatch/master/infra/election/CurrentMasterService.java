package com.hand.along.dispatch.master.infra.election;

import com.hand.along.dispatch.common.domain.monitor.MasterMonitorInfo;
import com.hand.along.dispatch.common.utils.CommonUtil;
import com.hand.along.dispatch.common.utils.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 当前节点信息
 */
@Component
@Slf4j
public class CurrentMasterService {
    private static final Map<String, MasterMonitorInfo> standbyMap = new HashMap<>();

    @Value("${netty.server.serverPort:31020}")
    private Integer serverPort;
    @Value("${netty.server.ip-pattern}")
    private String pattern;

    public String getCurrentIp(){
        String ip = CommonUtil.getIp(pattern);
        String currentUrl = String.format("%s:%s", ip, serverPort);
        log.info("当前节点信息：{}", currentUrl);
        return currentUrl;
    }

    public void setStandby(String message){
        MasterMonitorInfo masterMonitorInfo = JSON.toObj(message, MasterMonitorInfo.class);
        standbyMap.put(masterMonitorInfo.getIpAddr(), masterMonitorInfo);
    }

    public Map<String, MasterMonitorInfo> getStandby(){
        return standbyMap;
    }
}
