package com.ns.flow.model;

/**
 * @author bioxygenrich25
 * @description
 * @since 2024/1/12
 */
public class ProcessNode {

    String nodeId;
    String nodeName;
    String userId;
    String userName;
    String roleCode;
    Integer nodeLevel; //(i + 1);
    /**
     * {@link com.ns.flow.consts.RejectMode}
     */
    String rejectMode;

    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }

    public String getNodeName() {
        return nodeName;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getRoleCode() {
        return roleCode;
    }

    public void setRoleCode(String roleCode) {
        this.roleCode = roleCode;
    }

    public Integer getNodeLevel() {
        return nodeLevel;
    }

    public void setNodeLevel(Integer nodeLevel) {
        this.nodeLevel = nodeLevel;
    }

    public String getRejectMode() {
        return rejectMode;
    }

    public void setRejectMode(String rejectMode) {
        this.rejectMode = rejectMode;
    }
}
