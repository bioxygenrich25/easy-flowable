package com.ns.flow.consts;

/**
 * @author bioxygenrich25
 * @description
 * @since 2024/1/15
 */
public enum RejectMode {

    /**
     * 驳回后跳转到第一岗重新审批
     */
    REJECT_TO_FIRST_NODE,

    /**
     * 驳回后直接结束当前流程
     */
    REJECT_TO_END_EVENT,

    /**
     * 驳回后跳转上一岗进行审批
     */
    REJECT_TO_PREVIOUS_NODE,

    ;


}
