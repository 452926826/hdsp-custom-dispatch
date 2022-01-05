package com.hand.along.dispatch.master.infra.configuration;

import com.hand.along.dispatch.master.infra.quartz.CronJob;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.spi.JobFactory;
import org.quartz.spi.TriggerFiredBundle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.boot.autoconfigure.quartz.SchedulerFactoryBeanCustomizer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.scheduling.quartz.SpringBeanJobFactory;

import javax.sql.DataSource;

@Configuration
public class QuartzConfiguration implements SchedulerFactoryBeanCustomizer {


    @Autowired
    private DataSource dataSource;

    @Override
    public void customize(SchedulerFactoryBean schedulerFactoryBean) {
        // 启动延时
        schedulerFactoryBean.setStartupDelay(10);
        // 自动启动任务调度
        schedulerFactoryBean.setAutoStartup(true);
        // 是否覆盖现有作业定义
        schedulerFactoryBean.setOverwriteExistingJobs(true);
        // 配置数据源
        schedulerFactoryBean.setDataSource(dataSource);
    }


}
