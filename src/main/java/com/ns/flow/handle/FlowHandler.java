package com.ns.flow.handle;

import cn.hutool.core.collection.CollUtil;
import com.ns.flow.model.ProcessEntity;
import lombok.extern.slf4j.Slf4j;
import org.flowable.bpmn.model.Process;
import org.flowable.bpmn.model.*;
import org.flowable.engine.ManagementService;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.repository.Deployment;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author bioxygenrich25
 * @description
 * @since 2024/1/15
 */
@Slf4j
@Component
public class FlowHandler {


    @Resource
    private RepositoryService repositoryService;

    @Resource
    private ManagementService managementService;

    public String createProcessDefinition(ProcessEntity processEntity) {
        //数据校验：传入的数据节点字段：流程配置id、节点id、节点程序、节点形容
        if (CollUtil.isEmpty(processEntity.getProcessNodeList())) {
            throw new RuntimeException("流程节点processNodeList不能少于一个，请配置发起人节点和至多一个审批节点");
        }
        List t = repositoryService.createDeploymentQuery()
                .deploymentKey(processEntity.getTypeKey())
                .deploymentCategory(processEntity.getTypeKey())
                .deploymentName(processEntity.getTypeName())
                .list();
        if (CollUtil.isNotEmpty(t)) {
            return "已存在相同类型审批流";
        }
        BpmnModel bpmnModel = getBpmnModel(processEntity);
        Deployment deploy = repositoryService.createDeployment()
                .key(processEntity.getTypeKey())
                .name(processEntity.getTypeName())
                .category(processEntity.getTypeKey())//设置流程分类
                .addBpmnModel(processEntity.getTypeName() + ".bpmn20.xml", bpmnModel)
                .deploy();

        log.info("部署id:{}", deploy.getId());
        return deploy.getId();
    }


    private BpmnModel getBpmnModel(ProcessEntity processEntity) {
        BpmnModel bpmnModel = new BpmnModel();
        Process process = new Process();
        bpmnModel.addProcess(process);

        // 流程标识
        process.setId(processEntity.getTypeKey());
        process.setName(processEntity.getTypeName());
        //开始事件
        FlowElement startEvent = FlowableBpmnHandler.createStartFlowElement("start", "开始");
        process.addFlowElement(startEvent);
        //完结事件--工作失常实现
        FlowElement endEvent = FlowableBpmnHandler.createEndFlowElement("end", "结束");
        process.addFlowElement(endEvent);
        //创立用户节点工作
        List<UserTask> userTaskList = FlowableBpmnHandler.createCommonUserTask(processEntity);
        //构建流程模板
        buildProcessTemplate(process, startEvent, endEvent, userTaskList);
        return bpmnModel;
    }

    private void buildProcessTemplate(Process process, FlowElement startEvent, FlowElement endEvent, List<UserTask> userTaskList) {
        String firstId = null;
        for (int i = 0; i < userTaskList.size(); i++) {
            //用户工作
            UserTask userTask = userTaskList.get(i);
            process.addFlowElement(userTask);
            //创立排它网关节点
            ExclusiveGateway exclusiveGateway = FlowableBpmnHandler.createExclusiveGateway(i);
            process.addFlowElement(exclusiveGateway);
            //开始事件到第一个节点
            if (i == 0) {
                firstId = userTask.getId();
                // 开始节点到第一级节点
                SequenceFlow startSequenceFlow = new SequenceFlow(startEvent.getId(), userTask.getId());
                process.addFlowElement(startSequenceFlow);
            }
            //用户工作到网关节点
            SequenceFlow sequenceFlow = new SequenceFlow(userTask.getId(), exclusiveGateway.getId());
            sequenceFlow.setName(userTask.getName() + "_开始审批");
            process.addFlowElement(sequenceFlow);
            //批准：取下一步用户工作的节点id，可能为完结节点或者用户工作节点
            String nextUserTaskId = endEvent.getId();
            if (userTaskList.size() - i > 1) {
                nextUserTaskId = userTaskList.get(i + 1).getId();
            }
            SequenceFlow sequenceFlowAgree = new SequenceFlow(exclusiveGateway.getId(), nextUserTaskId);
            sequenceFlowAgree.setConditionExpression("${auditResult}");
            sequenceFlowAgree.setName("批准");
            process.addFlowElement(sequenceFlowAgree);
            //不批准：回退到上一步,取上一步的节点id
            String preUserTaskId = userTaskList.get(0).getId();
            String name = null;
            if (i != 0) {
                if ("01".equals(userTask.getDocumentation())) {//驳回至第一岗
                    preUserTaskId = firstId;
                    name = "驳回至第一岗";
                } else if ("02".equals(userTask.getDocumentation())) {//拒绝后结束
                    preUserTaskId = endEvent.getId();
                    name = "拒绝后结束";
                } else if ("03".equals(userTask.getDocumentation())) {//驳回至上一岗
                    preUserTaskId = userTaskList.get(i - 1).getId();
                    name = "驳回至上一岗";
                }
            } else {
                if ("02".equals(userTask.getDocumentation())) {//第一岗拒绝后结束
                    preUserTaskId = endEvent.getId();
                    name = "拒绝后结束";
                }
            }
            SequenceFlow sequenceFlowRefuse = new SequenceFlow(exclusiveGateway.getId(), preUserTaskId);
            sequenceFlowRefuse.setConditionExpression("${!auditResult}");
            sequenceFlowRefuse.setName(name);
            process.addFlowElement(sequenceFlowRefuse);
        }
    }


}
