package com.hand.along.dispatch.common.constants;

public interface CommonConstant {
    public static final String MASTER_LOCK="I_AM_MASTER";
    public static final String STANDBY_MASTER="STANDBY_MASTER";
    public static final String JOB="JOB";
    public static final String INFO="INFO";
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
}
