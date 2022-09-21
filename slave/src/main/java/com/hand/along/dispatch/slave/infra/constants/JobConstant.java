package com.hand.along.dispatch.slave.infra.constants;

public enum JobConstant {
    /**
     * sqoop命令
     */
    SQOOP_COMMAND("sqoop.command"),
    /**
     * datax 目录
     */
    DATAX_HOME("datax.home"),
    /**
     * datax jvm参数
     */
    DATAX_JVM_PARAM("datax.jvmParam"),
    /**
     * datax 脚本文件
     */
    DATAX_SCRIPTS("datax.scripts"),
    /**
     * sql 脚本文件
     */
    SQL_SCRIPTS("sql.scripts"),
    /**
     * sql 租户
     */
    SQL_TENANT_ID("sql.http.tenantId"),
    /**
     * sql 项目ID
     */
    SQL_PROJECT_ID("sql.http.projectId"),
    /**
     * schema
     */
    SQL_SCHEMA("sql.extra.schema"),
    /**
     * sql 数据源编码
     */
    SQL_DATASOURCE_CODE("sql.extra.datasourceCode");

    private String key;
    JobConstant(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}
