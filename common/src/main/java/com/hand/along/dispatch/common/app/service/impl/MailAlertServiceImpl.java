package com.hand.along.dispatch.common.app.service.impl;

import com.hand.along.dispatch.common.app.service.AlertService;
import com.hand.along.dispatch.common.domain.AlertInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.mail.internet.MimeMessage;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.Date;

import static com.hand.along.dispatch.common.constants.CommonConstant.ALERT_SUCCESS;
import static com.hand.along.dispatch.common.constants.CommonConstant.JOB;

/**
 * 邮件提醒
 */
@ConditionalOnProperty(prefix = "alert", name = "type", havingValue = "mail")
@Service
@Slf4j
public class MailAlertServiceImpl implements AlertService {
    @Autowired
    private JavaMailSender javaMailSender;
    @Value("${spring.mail.username}")
    private String sendMailer;

    @Override
    public void alert(AlertInfo info) {

        MimeMessage message = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            //邮件发件人
            helper.setFrom(sendMailer);
            //邮件收件人 1或多个
            helper.setTo(info.getMailList().split(";"));
            //邮件主题
            helper.setSubject(info.getSubject());
            //邮件内容
            helper.setText(mailTemplate(info.getWorkflowName(),
                    info.getJobName(),
                    info.getAlertDate().toString(),
                    info.getAlertObject(),
                    info.getAlertType()), true);
            //邮件发送时间
            helper.setSentDate(new Date());
            File tmpLog = File.createTempFile(String.format("%s-%s", info.getWorkflowName(),info.getJobName()),".log");
            if (StringUtils.hasText(info.getLog())) {
                FileUtils.writeStringToFile(tmpLog,info.getLog(), StandardCharsets.UTF_8);
                helper.addAttachment(tmpLog.getName(),tmpLog);
            }
            javaMailSender.send(message);
            log.info("发送邮件成功:{}->{}", sendMailer, info.getMailList());
            FileUtils.deleteQuietly(tmpLog);
        } catch (Exception e) {
            log.error("发送邮件时发生异常！", e);
        }
    }

    private String mailTemplate(String workflow, String job, String date, String alertObject, String alertType) {
        if (ALERT_SUCCESS.equals(alertType)) {
            if (JOB.equals(alertObject)) {
                return String.format("您关注的任务[%s]运行成功，成功时间为：[%s],所属任务流为：[%s]", job, date, workflow);
            } else {
                return String.format("您关注的任务流[%s]运行成功，成功时间为：[%s]", workflow, date);
            }
        } else {
            if (JOB.equals(alertObject)) {
                return String.format("您关注的任务[%s]运行失败，失败时间为：[%s],所属任务流为：[%s]", job, date, workflow);
            } else {
                return String.format("您关注的任务流[%s]运行失败，失败时间为：[%s]", workflow, date);
            }
        }
    }
}
