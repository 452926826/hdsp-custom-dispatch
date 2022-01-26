package com.hand.along.dispatch.common.constants;

public interface CommonConstant {
    public static final String MASTER_LOCK="I_AM_MASTER";
    /**
     * 消息类型
     */
    public static final String JOB="JOB";
    public static final String INFO="INFO";
    public static final String EXECUTE_WORKFLOW="EXECUTE";
    public static final String EXECUTE_SUB_WORKFLOW="EXECUTE_SUB";
    public static final String CRON_WORKFLOW="CRON";
    public static final String STANDBY_INFO="STANDBY";

    public static final String SUCCESS_FORMAT="%s.success";

    public static final String FROM_AND_TO="%s-%s";

    public static final String LINE_SEPARATOR="\n";
    /**
     * 执行状态
     */
    public enum ExecutionStatus {
        /**
         * 准备
         */
        READY,
        /**
         * 运行中
         */
        RUNNING,
        /**
         * 跳过
         */
        SKIP,
        /**
         * 失败
         */
        FAILED,
        /**
         * 成功
         */
        SUCCEEDED,
        /**
         * 停止
         */
        STOP;

        public static boolean isFinished(String status) {
            return ExecutionStatus.STOP.name().equals(status)
                    || ExecutionStatus.FAILED.name().equals(status)
                    || ExecutionStatus.SUCCEEDED.name().equals(status)
                    || ExecutionStatus.SKIP.name().equals(status);
        }

        public static boolean isSuccess(String status) {
            return ExecutionStatus.SUCCEEDED.name().equals(status) || ExecutionStatus.SKIP.name().equals(status);
        }

        public static boolean isFailed(String status) {
            return ExecutionStatus.FAILED.name().equals(status) || ExecutionStatus.STOP.name().equals(status);
        }

        public static boolean isReady(String status) {
            return ExecutionStatus.READY.name().equals(status);
        }

        public static boolean isRunning(String status) {
            return ExecutionStatus.RUNNING.name().equals(status);
        }

        public static boolean isSkipped(String status) {
            return ExecutionStatus.SKIP.name().equals(status);
        }
    }

    public enum NodeMark {
        /*
        开始
         */
        START,
        /*
        结束
         */
        END,
        /*
        唯一
         */
        UNIQUE
    }

    public enum NodeType{
        /**
         * 任务
         */
        JOB,
        /**
         * 任务流
         */
        WORKFLOW;
    }

    public enum GLOBALPARAM{
        CURRENT_DATE_TIME("_p_current_date_time"),

        /**
         * 上次执行时间：${_p_last_date_time:yyyy-MM-dd HH:mm:ss} 默认掩码格式：yyyy-MM-dd HH:mm:ss
         */
        LAST_DATE_TIME("_p_last_date_time"),
        /**
         * 跟上面CURRENT_DATE_TIME 区别在于 _g是全局的
         */
        G_CURRENT_DATE_TIME("_g_current_date_time"),

        G_LAST_DATE_TIME("_g_last_date_time"),

        /**
         * 当前最大id：${_p_current_max_id}
         */
        CURRENT_MAX_ID("_p_current_max_id"),

        /**
         * 上次最大id：${_p_last_max_id}
         */
        LAST_MAX_ID("_p_last_max_id");

        GLOBALPARAM(String value) {
            this.value = value;
        }

        private String value;

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }
}
