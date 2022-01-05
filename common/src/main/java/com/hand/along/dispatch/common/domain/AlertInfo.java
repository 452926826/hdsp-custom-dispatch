package com.hand.along.dispatch.common.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 告警信息
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AlertInfo {
    private String message;
}
