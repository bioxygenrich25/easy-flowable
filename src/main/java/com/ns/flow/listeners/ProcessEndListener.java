package com.ns.flow.listeners;

import cn.hutool.extra.spring.SpringUtil;
import com.alibaba.fastjson2.JSON;
import lombok.extern.slf4j.Slf4j;
import org.flowable.bpmn.model.BpmnModel;
import org.flowable.bpmn.model.EndEvent;
import org.flowable.bpmn.model.FlowNode;
import org.flowable.common.engine.api.delegate.event.FlowableEvent;
import org.flowable.common.engine.api.delegate.event.FlowableEventListener;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.delegate.event.impl.FlowableEntityEventImpl;
import org.flowable.engine.impl.util.ProcessDefinitionUtil;
import org.flowable.engine.runtime.Execution;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author bioxygenrich25
 * @description 全局监听器，判断流程是不是运行到了最后一个EndEvent
 * @since 2024/1/15
 */
@Slf4j
@Component
public class ProcessEndListener implements FlowableEventListener {


    @Override
    public void onEvent(FlowableEvent event) {
        log.info("ProcessEndListener:{}", JSON.toJSONString(event.getType()));
        TaskService taskService = SpringUtil.getBean(TaskService.class);
        RuntimeService runtimeService = SpringUtil.getBean(RuntimeService.class);
        FlowableEntityEventImpl flowableEntityEvent = ((FlowableEntityEventImpl) event);
        ProcessInstance pi = (ProcessInstance) flowableEntityEvent.getEntity();
        List<Task> tasks = taskService.createTaskQuery().active().processInstanceId(pi.getId()).list();
        Task t = tasks.get(0);
        String bizKey = pi.getBusinessKey();
        log.info("ProcessEndListener#bizKey:{}", bizKey);
        String procDefId = pi.getProcessDefinitionId();
        List<Execution> exes = runtimeService.createExecutionQuery().processInstanceId(pi.getProcessInstanceId()).processDefinitionId(procDefId).executionId(t.getExecutionId()).list();
        String curActId = exes.get(0).getActivityId();
        BpmnModel bm = ProcessDefinitionUtil.getBpmnModel(t.getProcessDefinitionId());
        FlowNode flowNode = (FlowNode) bm.getFlowElement(curActId);
        if (flowNode instanceof EndEvent) {
            if (flowNode.getId().equals("end")) {
                //todo 通知回调
                log.info("当前节点是结束节点:{}！！", JSON.toJSONString(flowNode));
            }
        }
    }

    @Override
    public boolean isFailOnException() {
        return false;
    }

    @Override
    public boolean isFireOnTransactionLifecycleEvent() {
        return false;
    }

    @Override
    public String getOnTransaction() {
        return null;
    }


}

