package com.ns.flow.handle;

import com.google.common.collect.Lists;
import com.ns.flow.model.ProcessEntity;
import com.ns.flow.model.ProcessNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flowable.bpmn.model.*;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * @author bioxygenrich25
 * @description 动态创建流程节点
 * @since 2024/1/12
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class FlowableBpmnHandler {
    /**
     * 创立开始节点信息
     *
     * @return
     */
    public static FlowElement createStartFlowElement(String id, String name) {
        StartEvent startEvent = new StartEvent();
        startEvent.setId(id);
        startEvent.setName(name);
        return startEvent;
    }

    /**
     * 创立完结节点信息
     *
     * @param id
     * @param name
     * @return
     */
    public static FlowElement createEndFlowElement(String id, String name) {
        EndEvent endEvent = new EndEvent();
        endEvent.setId(id);
        endEvent.setName(name);
        return endEvent;
    }

    /**
     * 循环创立一般工作节点信息
     *
     * @param processEntity 流程实体
     * @return
     */
    public static List<UserTask> createCommonUserTask(ProcessEntity processEntity) {
        List<ProcessNode> processNodes = processEntity.getProcessNodeList();
        List<UserTask> userTaskList = Lists.newLinkedList();
        for (int i = 0; i < processNodes.size(); i++) {
            ProcessNode node = processNodes.get(i);
            node.setNodeLevel(i + 1);
            node.setNodeId(UUID.randomUUID().toString().replace("-", ""));
            UserTask userTask = new UserTask();
            //此处如果设置了${assignee}这类的占位符，需要传参，所以暂不设定
            userTask.setCandidateGroups(Arrays.asList(node.getRoleCode()));
            userTask.setId(processEntity.getTypeKey() + "_task_" + node.getNodeId());
            userTask.setCategory(processEntity.getTypeKey());
            userTask.setDocumentation(node.getRejectMode());
            userTask.setName(node.getNodeName());
            userTaskList.add(userTask);
        }
        return userTaskList;
    }


    /**
     * 创立排它网关节点
     *
     * @return
     */
    public static ExclusiveGateway createExclusiveGateway(Integer i) {
        ExclusiveGateway gateway = new ExclusiveGateway();
        gateway.setName("gateway_name_" + i);
        gateway.setId("gateway_id_" + i);
        return gateway;
    }
}