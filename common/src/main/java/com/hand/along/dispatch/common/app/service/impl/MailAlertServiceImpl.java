package com.hand.along.dispatch.common.app.service.impl;

import com.hand.along.dispatch.common.app.service.AlertService;
import com.hand.along.dispatch.common.domain.AlertInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

/**
 * 邮件提醒
 */
@ConditionalOnProperty(prefix = "alert",name = "type",havingValue = "mail")
@Service
@Slf4j
public class MailAlertServiceImpl implements AlertService {
    @Override
    public void alert(AlertInfo info) {
        log.warn("告警待处理");
    }
}
