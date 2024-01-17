package com.ns.flow.model;

/**
 * @author bioxygenrich25
 * @description
 * @since 2024/1/17
 */
public class CuzAuditVariableEntity {

    String taskName;
    String auditParams; //", para.getParams())
    String operateTime;
    Boolean auditResult;
    String assignee;

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getAuditParams() {
        return auditParams;
    }

    public void setAuditParams(String auditParams) {
        this.auditParams = auditParams;
    }

    public String getOperateTime() {
        return operateTime;
    }

    public void setOperateTime(String operateTime) {
        this.operateTime = operateTime;
    }

    public Boolean getAuditResult() {
        return auditResult;
    }

    public void setAuditResult(Boolean auditResult) {
        this.auditResult = auditResult;
    }

    public String getAssignee() {
        return assignee;
    }

    public void setAssignee(String assignee) {
        this.assignee = assignee;
    }
}
