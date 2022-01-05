package com.hand.along.dispatch.sql.plugin;

import com.hand.along.dispatch.common.infra.job.AbstractJob;
import com.hand.along.dispatch.common.infra.job.BaseJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
public class SqlJob extends AbstractJob {
    /**
     * 处理详情
     * 成功不用管
     * 如果失败就直接抛个异常
     */
    @Override
    public void handle() {
        Map<String, Object> params = getParams();
        log.info("参数为：{}", params.toString());
    }

    /**
     * 处理之前
     */
    @Override
    public void before() {

    }

    /**
     * 处理之后
     */
    @Override
    public void after() {

    }

    @Override
    public BaseJob getInstance() {
        return new SqlJob();
    }
}
