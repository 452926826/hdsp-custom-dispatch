package com.hand.along.dispatch.common.domain.monitor;

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
public class SlaveMonitorInfo{
    private String ipAddr;
    private List<ExecutorInfo> executorInfoList;
}
