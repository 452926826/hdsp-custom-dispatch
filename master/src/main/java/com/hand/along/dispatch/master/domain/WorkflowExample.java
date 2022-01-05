package com.hand.along.dispatch.master.domain;

import java.util.ArrayList;
import java.util.List;

public class WorkflowExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public WorkflowExample() {
        oredCriteria = new ArrayList<Criteria>();
    }

    public void setOrderByClause(String orderByClause) {
        this.orderByClause = orderByClause;
    }

    public String getOrderByClause() {
        return orderByClause;
    }

    public void setDistinct(boolean distinct) {
        this.distinct = distinct;
    }

    public boolean isDistinct() {
        return distinct;
    }

    public List<Criteria> getOredCriteria() {
        return oredCriteria;
    }

    public void or(Criteria criteria) {
        oredCriteria.add(criteria);
    }

    public Criteria or() {
        Criteria criteria = createCriteriaInternal();
        oredCriteria.add(criteria);
        return criteria;
    }

    public Criteria createCriteria() {
        Criteria criteria = createCriteriaInternal();
        if (oredCriteria.size() == 0) {
            oredCriteria.add(criteria);
        }
        return criteria;
    }

    protected Criteria createCriteriaInternal() {
        Criteria criteria = new Criteria();
        return criteria;
    }

    public void clear() {
        oredCriteria.clear();
        orderByClause = null;
        distinct = false;
    }

    protected abstract static class GeneratedCriteria {
        protected List<Criterion> criteria;

        protected GeneratedCriteria() {
            super();
            criteria = new ArrayList<Criterion>();
        }

        public boolean isValid() {
            return criteria.size() > 0;
        }

        public List<Criterion> getAllCriteria() {
            return criteria;
        }

        public List<Criterion> getCriteria() {
            return criteria;
        }

        protected void addCriterion(String condition) {
            if (condition == null) {
                throw new RuntimeException("Value for condition cannot be null");
            }
            criteria.add(new Criterion(condition));
        }

        protected void addCriterion(String condition, Object value, String property) {
            if (value == null) {
                throw new RuntimeException("Value for " + property + " cannot be null");
            }
            criteria.add(new Criterion(condition, value));
        }

        protected void addCriterion(String condition, Object value1, Object value2, String property) {
            if (value1 == null || value2 == null) {
                throw new RuntimeException("Between values for " + property + " cannot be null");
            }
            criteria.add(new Criterion(condition, value1, value2));
        }

        public Criteria andWorkflowIdIsNull() {
            addCriterion("workflow_id is null");
            return (Criteria) this;
        }

        public Criteria andWorkflowIdIsNotNull() {
            addCriterion("workflow_id is not null");
            return (Criteria) this;
        }

        public Criteria andWorkflowIdEqualTo(Long value) {
            addCriterion("workflow_id =", value, "workflowId");
            return (Criteria) this;
        }

        public Criteria andWorkflowIdNotEqualTo(Long value) {
            addCriterion("workflow_id <>", value, "workflowId");
            return (Criteria) this;
        }

        public Criteria andWorkflowIdGreaterThan(Long value) {
            addCriterion("workflow_id >", value, "workflowId");
            return (Criteria) this;
        }

        public Criteria andWorkflowIdGreaterThanOrEqualTo(Long value) {
            addCriterion("workflow_id >=", value, "workflowId");
            return (Criteria) this;
        }

        public Criteria andWorkflowIdLessThan(Long value) {
            addCriterion("workflow_id <", value, "workflowId");
            return (Criteria) this;
        }

        public Criteria andWorkflowIdLessThanOrEqualTo(Long value) {
            addCriterion("workflow_id <=", value, "workflowId");
            return (Criteria) this;
        }

        public Criteria andWorkflowIdIn(List<Long> values) {
            addCriterion("workflow_id in", values, "workflowId");
            return (Criteria) this;
        }

        public Criteria andWorkflowIdNotIn(List<Long> values) {
            addCriterion("workflow_id not in", values, "workflowId");
            return (Criteria) this;
        }

        public Criteria andWorkflowIdBetween(Long value1, Long value2) {
            addCriterion("workflow_id between", value1, value2, "workflowId");
            return (Criteria) this;
        }

        public Criteria andWorkflowIdNotBetween(Long value1, Long value2) {
            addCriterion("workflow_id not between", value1, value2, "workflowId");
            return (Criteria) this;
        }

        public Criteria andWorkflowNameIsNull() {
            addCriterion("workflow_name is null");
            return (Criteria) this;
        }

        public Criteria andWorkflowNameIsNotNull() {
            addCriterion("workflow_name is not null");
            return (Criteria) this;
        }

        public Criteria andWorkflowNameEqualTo(String value) {
            addCriterion("workflow_name =", value, "workflowName");
            return (Criteria) this;
        }

        public Criteria andWorkflowNameNotEqualTo(String value) {
            addCriterion("workflow_name <>", value, "workflowName");
            return (Criteria) this;
        }

        public Criteria andWorkflowNameGreaterThan(String value) {
            addCriterion("workflow_name >", value, "workflowName");
            return (Criteria) this;
        }

        public Criteria andWorkflowNameGreaterThanOrEqualTo(String value) {
            addCriterion("workflow_name >=", value, "workflowName");
            return (Criteria) this;
        }

        public Criteria andWorkflowNameLessThan(String value) {
            addCriterion("workflow_name <", value, "workflowName");
            return (Criteria) this;
        }

        public Criteria andWorkflowNameLessThanOrEqualTo(String value) {
            addCriterion("workflow_name <=", value, "workflowName");
            return (Criteria) this;
        }

        public Criteria andWorkflowNameLike(String value) {
            addCriterion("workflow_name like", value, "workflowName");
            return (Criteria) this;
        }

        public Criteria andWorkflowNameNotLike(String value) {
            addCriterion("workflow_name not like", value, "workflowName");
            return (Criteria) this;
        }

        public Criteria andWorkflowNameIn(List<String> values) {
            addCriterion("workflow_name in", values, "workflowName");
            return (Criteria) this;
        }

        public Criteria andWorkflowNameNotIn(List<String> values) {
            addCriterion("workflow_name not in", values, "workflowName");
            return (Criteria) this;
        }

        public Criteria andWorkflowNameBetween(String value1, String value2) {
            addCriterion("workflow_name between", value1, value2, "workflowName");
            return (Criteria) this;
        }

        public Criteria andWorkflowNameNotBetween(String value1, String value2) {
            addCriterion("workflow_name not between", value1, value2, "workflowName");
            return (Criteria) this;
        }

        public Criteria andWorkflowCodeIsNull() {
            addCriterion("workflow_code is null");
            return (Criteria) this;
        }

        public Criteria andWorkflowCodeIsNotNull() {
            addCriterion("workflow_code is not null");
            return (Criteria) this;
        }

        public Criteria andWorkflowCodeEqualTo(String value) {
            addCriterion("workflow_code =", value, "workflowCode");
            return (Criteria) this;
        }

        public Criteria andWorkflowCodeNotEqualTo(String value) {
            addCriterion("workflow_code <>", value, "workflowCode");
            return (Criteria) this;
        }

        public Criteria andWorkflowCodeGreaterThan(String value) {
            addCriterion("workflow_code >", value, "workflowCode");
            return (Criteria) this;
        }

        public Criteria andWorkflowCodeGreaterThanOrEqualTo(String value) {
            addCriterion("workflow_code >=", value, "workflowCode");
            return (Criteria) this;
        }

        public Criteria andWorkflowCodeLessThan(String value) {
            addCriterion("workflow_code <", value, "workflowCode");
            return (Criteria) this;
        }

        public Criteria andWorkflowCodeLessThanOrEqualTo(String value) {
            addCriterion("workflow_code <=", value, "workflowCode");
            return (Criteria) this;
        }

        public Criteria andWorkflowCodeLike(String value) {
            addCriterion("workflow_code like", value, "workflowCode");
            return (Criteria) this;
        }

        public Criteria andWorkflowCodeNotLike(String value) {
            addCriterion("workflow_code not like", value, "workflowCode");
            return (Criteria) this;
        }

        public Criteria andWorkflowCodeIn(List<String> values) {
            addCriterion("workflow_code in", values, "workflowCode");
            return (Criteria) this;
        }

        public Criteria andWorkflowCodeNotIn(List<String> values) {
            addCriterion("workflow_code not in", values, "workflowCode");
            return (Criteria) this;
        }

        public Criteria andWorkflowCodeBetween(String value1, String value2) {
            addCriterion("workflow_code between", value1, value2, "workflowCode");
            return (Criteria) this;
        }

        public Criteria andWorkflowCodeNotBetween(String value1, String value2) {
            addCriterion("workflow_code not between", value1, value2, "workflowCode");
            return (Criteria) this;
        }

        public Criteria andWorkflowDescriptionIsNull() {
            addCriterion("workflow_description is null");
            return (Criteria) this;
        }

        public Criteria andWorkflowDescriptionIsNotNull() {
            addCriterion("workflow_description is not null");
            return (Criteria) this;
        }

        public Criteria andWorkflowDescriptionEqualTo(String value) {
            addCriterion("workflow_description =", value, "workflowDescription");
            return (Criteria) this;
        }

        public Criteria andWorkflowDescriptionNotEqualTo(String value) {
            addCriterion("workflow_description <>", value, "workflowDescription");
            return (Criteria) this;
        }

        public Criteria andWorkflowDescriptionGreaterThan(String value) {
            addCriterion("workflow_description >", value, "workflowDescription");
            return (Criteria) this;
        }

        public Criteria andWorkflowDescriptionGreaterThanOrEqualTo(String value) {
            addCriterion("workflow_description >=", value, "workflowDescription");
            return (Criteria) this;
        }

        public Criteria andWorkflowDescriptionLessThan(String value) {
            addCriterion("workflow_description <", value, "workflowDescription");
            return (Criteria) this;
        }

        public Criteria andWorkflowDescriptionLessThanOrEqualTo(String value) {
            addCriterion("workflow_description <=", value, "workflowDescription");
            return (Criteria) this;
        }

        public Criteria andWorkflowDescriptionLike(String value) {
            addCriterion("workflow_description like", value, "workflowDescription");
            return (Criteria) this;
        }

        public Criteria andWorkflowDescriptionNotLike(String value) {
            addCriterion("workflow_description not like", value, "workflowDescription");
            return (Criteria) this;
        }

        public Criteria andWorkflowDescriptionIn(List<String> values) {
            addCriterion("workflow_description in", values, "workflowDescription");
            return (Criteria) this;
        }

        public Criteria andWorkflowDescriptionNotIn(List<String> values) {
            addCriterion("workflow_description not in", values, "workflowDescription");
            return (Criteria) this;
        }

        public Criteria andWorkflowDescriptionBetween(String value1, String value2) {
            addCriterion("workflow_description between", value1, value2, "workflowDescription");
            return (Criteria) this;
        }

        public Criteria andWorkflowDescriptionNotBetween(String value1, String value2) {
            addCriterion("workflow_description not between", value1, value2, "workflowDescription");
            return (Criteria) this;
        }

        public Criteria andPriorityLevelIsNull() {
            addCriterion("priority_level is null");
            return (Criteria) this;
        }

        public Criteria andPriorityLevelIsNotNull() {
            addCriterion("priority_level is not null");
            return (Criteria) this;
        }

        public Criteria andPriorityLevelEqualTo(String value) {
            addCriterion("priority_level =", value, "priorityLevel");
            return (Criteria) this;
        }

        public Criteria andPriorityLevelNotEqualTo(String value) {
            addCriterion("priority_level <>", value, "priorityLevel");
            return (Criteria) this;
        }

        public Criteria andPriorityLevelGreaterThan(String value) {
            addCriterion("priority_level >", value, "priorityLevel");
            return (Criteria) this;
        }

        public Criteria andPriorityLevelGreaterThanOrEqualTo(String value) {
            addCriterion("priority_level >=", value, "priorityLevel");
            return (Criteria) this;
        }

        public Criteria andPriorityLevelLessThan(String value) {
            addCriterion("priority_level <", value, "priorityLevel");
            return (Criteria) this;
        }

        public Criteria andPriorityLevelLessThanOrEqualTo(String value) {
            addCriterion("priority_level <=", value, "priorityLevel");
            return (Criteria) this;
        }

        public Criteria andPriorityLevelLike(String value) {
            addCriterion("priority_level like", value, "priorityLevel");
            return (Criteria) this;
        }

        public Criteria andPriorityLevelNotLike(String value) {
            addCriterion("priority_level not like", value, "priorityLevel");
            return (Criteria) this;
        }

        public Criteria andPriorityLevelIn(List<String> values) {
            addCriterion("priority_level in", values, "priorityLevel");
            return (Criteria) this;
        }

        public Criteria andPriorityLevelNotIn(List<String> values) {
            addCriterion("priority_level not in", values, "priorityLevel");
            return (Criteria) this;
        }

        public Criteria andPriorityLevelBetween(String value1, String value2) {
            addCriterion("priority_level between", value1, value2, "priorityLevel");
            return (Criteria) this;
        }

        public Criteria andPriorityLevelNotBetween(String value1, String value2) {
            addCriterion("priority_level not between", value1, value2, "priorityLevel");
            return (Criteria) this;
        }
    }

    public static class Criteria extends GeneratedCriteria {

        protected Criteria() {
            super();
        }
    }

    public static class Criterion {
        private String condition;

        private Object value;

        private Object secondValue;

        private boolean noValue;

        private boolean singleValue;

        private boolean betweenValue;

        private boolean listValue;

        private String typeHandler;

        public String getCondition() {
            return condition;
        }

        public Object getValue() {
            return value;
        }

        public Object getSecondValue() {
            return secondValue;
        }

        public boolean isNoValue() {
            return noValue;
        }

        public boolean isSingleValue() {
            return singleValue;
        }

        public boolean isBetweenValue() {
            return betweenValue;
        }

        public boolean isListValue() {
            return listValue;
        }

        public String getTypeHandler() {
            return typeHandler;
        }

        protected Criterion(String condition) {
            super();
            this.condition = condition;
            this.typeHandler = null;
            this.noValue = true;
        }

        protected Criterion(String condition, Object value, String typeHandler) {
            super();
            this.condition = condition;
            this.value = value;
            this.typeHandler = typeHandler;
            if (value instanceof List<?>) {
                this.listValue = true;
            } else {
                this.singleValue = true;
            }
        }

        protected Criterion(String condition, Object value) {
            this(condition, value, null);
        }

        protected Criterion(String condition, Object value, Object secondValue, String typeHandler) {
            super();
            this.condition = condition;
            this.value = value;
            this.secondValue = secondValue;
            this.typeHandler = typeHandler;
            this.betweenValue = true;
        }

        protected Criterion(String condition, Object value, Object secondValue) {
            this(condition, value, secondValue, null);
        }
    }
}