package com.hand.along.dispatch.common.domain;

import lombok.Data;

import java.util.Date;

@Data
public class BaseDomain {
    public static final String FIELD_CREATION_DATE = "creationDate";
    public static final String FIELD_CREATED_BY = "createdBy";
    public static final String FIELD_LAST_UPDATE_DATE = "lastUpdateDate";
    public static final String FIELD_LAST_UPDATED_BY = "lastUpdatedBy";
    public static final String FIELD_TENANT_ID = "tenantId";

    private Date creationDate;

    private Date lastUpdateDate;

    private Long createdBy;

    private Long lastUpdatedBy;

    private Long objectVersionNumber;

    private Long tenantId;
}
