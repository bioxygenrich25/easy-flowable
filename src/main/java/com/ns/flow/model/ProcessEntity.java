package com.ns.flow.model;

import java.util.List;

/**
 * @author bioxygenrich25
 * @description
 * @since 2024/1/12
 */
public class ProcessEntity {

    /**
     * 流程节点配置类
     */
    private List<ProcessNode> processNodeList;


    /**
     * 自定义流程类别Key(不得重复,用作deploymentKey)
     */
    private String typeKey;

    /**
     * 自定义流程类别名称(不得重复,用作deploymentName)
     */
    private String typeName;

    public List<ProcessNode> getProcessNodeList() {
        return processNodeList;
    }

    public void setProcessNodeList(List<ProcessNode> processNodeList) {
        this.processNodeList = processNodeList;
    }


    public String getTypeKey() {
        return typeKey;
    }

    public void setTypeKey(String typeKey) {
        this.typeKey = typeKey;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }
}
