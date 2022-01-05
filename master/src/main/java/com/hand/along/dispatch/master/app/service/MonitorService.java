package com.hand.along.dispatch.master.app.service;

import com.hand.along.dispatch.common.domain.monitor.MasterMonitorInfo;

import java.util.List;

public interface MonitorService {
    List<MasterMonitorInfo> monitorInfo(Long tenantId);
}
