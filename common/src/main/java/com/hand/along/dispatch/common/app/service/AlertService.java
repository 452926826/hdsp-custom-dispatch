package com.hand.along.dispatch.common.app.service;

import com.hand.along.dispatch.common.domain.AlertInfo;

public interface AlertService {
    /**
     * 告警
     *
     * @param info 告警信息
     */
    void alert(AlertInfo info);
}
