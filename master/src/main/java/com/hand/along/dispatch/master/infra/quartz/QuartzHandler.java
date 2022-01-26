package com.hand.along.dispatch.master.infra.quartz;

import com.hand.along.dispatch.common.exceptions.CommonException;
import com.hand.along.dispatch.common.utils.CommonUtil;
import com.hand.along.dispatch.master.domain.WorkflowSchedule;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.util.*;

@Slf4j
@Component
public class QuartzHandler {
    @Autowired
    private Scheduler scheduler;

    public void startCron(WorkflowSchedule schedule) {
        try {
            String workflowName = schedule.getWorkflowName();
            String workflowCode = schedule.getWorkflowCode();
            TriggerKey triggerKey = TriggerKey.triggerKey(workflowName, workflowCode);
            Trigger trigger = scheduler.getTrigger(triggerKey);
            CronTrigger cronTrigger;
            if (Objects.isNull(trigger)) {
                // 没有定时任务
                // 定义参数
                Map<String, String> params = new HashMap<>();
                params.put("workflowId", String.valueOf(schedule.getWorkflowId()));
                params.put("graph", schedule.getGraph());
                // 构建执行详情
                JobDetail jobDetail = JobBuilder.newJob(CronJob.class).withIdentity(workflowName, workflowCode)
                        .setJobData(new JobDataMap(params)).build();
                //构建触发器
                cronTrigger = TriggerBuilder.newTrigger().withIdentity(workflowName, workflowCode)
                        .withSchedule(CronScheduleBuilder.cronSchedule(schedule.getCronExpression())).build();
                // 启动
                scheduler.scheduleJob(jobDetail, cronTrigger);
                if (scheduler.isShutdown()) {
                    scheduler.start();
                }
            } else {
                // 如果存在则重新配置cron
                cronTrigger = TriggerBuilder.newTrigger().withIdentity(workflowName, workflowCode)
                        .withSchedule(CronScheduleBuilder.cronSchedule(schedule.getCronExpression())).build();
                scheduler.rescheduleJob(triggerKey, cronTrigger);
            }
        } catch (Exception e) {
            log.error("新增调度异常", e);
            throw new CommonException("新增调度异常");
        }
    }

    public void pause(WorkflowSchedule schedule) {
        try {
            TriggerKey triggerKey = TriggerKey.triggerKey(schedule.getWorkflowName(), schedule.getWorkflowCode());
            Trigger trigger = scheduler.getTrigger(triggerKey);
            JobKey jobKey = trigger.getJobKey();
            scheduler.pauseJob(jobKey);
        } catch (SchedulerException e) {
            log.error("暂停调度失败", e);
            throw new CommonException("暂停调度失败");
        }
    }

    public void restart(WorkflowSchedule schedule) {
        try {
            TriggerKey triggerKey = TriggerKey.triggerKey(schedule.getWorkflowName(), schedule.getWorkflowCode());
            Trigger trigger = scheduler.getTrigger(triggerKey);
            scheduler.rescheduleJob(triggerKey, trigger);
        } catch (SchedulerException e) {
            log.error("重启调度失败", e);
            throw new CommonException("重启调度失败");
        }
    }


    public void updateCronExpression(WorkflowSchedule schedule) {
        try {
            TriggerKey triggerKey = TriggerKey.triggerKey(schedule.getWorkflowName(), schedule.getWorkflowCode());
            CronTrigger trigger = (CronTrigger) scheduler.getTrigger(triggerKey);
            CronTrigger cronTrigger = trigger.getTriggerBuilder().withIdentity(triggerKey).withSchedule(CronScheduleBuilder.cronSchedule(schedule.getCronExpression()))
                    .build();
            scheduler.rescheduleJob(triggerKey, cronTrigger);
        } catch (SchedulerException e) {
            log.error("修改调度失败", e);
            throw new CommonException("修改调度失败");
        }

    }

    public void delete(WorkflowSchedule schedule) {
        try {
            TriggerKey triggerKey = TriggerKey.triggerKey(schedule.getWorkflowName(), schedule.getWorkflowCode());
            Trigger trigger = scheduler.getTrigger(triggerKey);
            JobKey jobKey = trigger.getJobKey();
            scheduler.pauseTrigger(triggerKey);
            scheduler.unscheduleJob(triggerKey);
            scheduler.deleteJob(jobKey);
        } catch (SchedulerException e) {
            log.error("删除定时任务失败", e);
            throw new CommonException("删除定时任务失败");
        }
    }

    public boolean hasCron(WorkflowSchedule schedule) {
        TriggerKey triggerKey = TriggerKey.triggerKey(schedule.getWorkflowName(), schedule.getWorkflowCode());
        try {
            return scheduler.checkExists(triggerKey);
        } catch (SchedulerException e) {
            log.error("判断定时任务是否存在失败", e);
            throw new CommonException("判断定时任务是否存在失败");
        }
    }

    public String getStatus(WorkflowSchedule schedule) {
        try {
            TriggerKey triggerKey = TriggerKey.triggerKey(schedule.getWorkflowName(), schedule.getWorkflowCode());
            Trigger.TriggerState triggerState = scheduler.getTriggerState(triggerKey);
            return triggerState.toString();
        } catch (SchedulerException e) {
            log.error("获取定时任务状态失败", e);
            throw new CommonException("获取定时任务状态失败");
        }
    }

    public void startScheduler() {
        try {
            if (scheduler.isShutdown()) {
                scheduler.start();
            }
        } catch (SchedulerException e) {
            log.error("启动调度器失败", e);
            throw new CommonException("启动调度器失败");
        }
    }

    public void stopScheduler() {
        try {
            if (!scheduler.isShutdown()) {
                scheduler.standby();
            }
        } catch (SchedulerException e) {
            log.error("启动调度器失败", e);
            throw new CommonException("启动调度器失败");
        }
    }

    public void getNextFiveFireDate(String cronExpression) {
        List<String> fiveDate = new ArrayList<>();
        try {
            if (StringUtils.isNotEmpty(cronExpression)) {
                CronExpression ce = new CronExpression(cronExpression);
                Date date1 = ce.getNextValidTimeAfter(CommonUtil.now());
                fiveDate.add(CommonUtil.formatDate(date1));
                Date date2 = ce.getNextValidTimeAfter(date1);
                fiveDate.add(CommonUtil.formatDate(date2));
                Date date3 = ce.getNextValidTimeAfter(date2);
                fiveDate.add(CommonUtil.formatDate(date3));
                Date date4 = ce.getNextValidTimeAfter(date3);
                fiveDate.add(CommonUtil.formatDate(date4));
                Date date5 = ce.getNextValidTimeAfter(date4);
                fiveDate.add(CommonUtil.formatDate(date5));
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

}
