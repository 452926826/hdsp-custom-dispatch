package com.hand.along.dispatch.master.infra.quartz;

import com.hand.along.dispatch.master.app.service.WorkflowService;
import com.hand.along.dispatch.master.domain.Workflow;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

@Component
public class CronJob extends QuartzJobBean {
    @Autowired
    private WorkflowService workflowService;

    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        JobDataMap jobDataMap = jobExecutionContext.getJobDetail().getJobDataMap();
        workflowService.execute(Workflow.builder()
                .workflowId(jobDataMap.getLong("workflowId"))
                .graph(jobDataMap.getString("graph"))
                .build());
    }
}
