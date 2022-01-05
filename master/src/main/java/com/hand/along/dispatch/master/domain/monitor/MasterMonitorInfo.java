package com.hand.along.dispatch.master.domain.monitor;

import com.hand.along.dispatch.common.domain.ExecutorInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MasterMonitorInfo {
    private String ipAddr;
    private Boolean master;
    private Boolean standby;
    private List<ExecutorInfo> executorInfoList;
    private List<SlaveMonitorInfo> slaveMonitorInfoList;
}
