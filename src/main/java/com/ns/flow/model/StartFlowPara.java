package com.ns.flow.model;

/**
 * @author bioxygenrich25
 * @description
 * @since 2024/1/16
 */
public class StartFlowPara {

    /**
     * 业务流水Key,全流程贯穿的唯一值
     */
    String businessKey;

    /**
     * 流程定义Key
     */
    String processKey;

    /**
     * 提交人ID
     */
    String userId;

    /**
     * 提交参数
     */
    String params;


    public String getBusinessKey() {
        return businessKey;
    }

    public void setBusinessKey(String businessKey) {
        this.businessKey = businessKey;
    }

    public String getProcessKey() {
        return processKey;
    }

    public void setProcessKey(String processKey) {
        this.processKey = processKey;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getParams() {
        return params;
    }

    public void setParams(String params) {
        this.params = params;
    }
}
