package com.ns.flow.api;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.ns.flow.handle.FlowHandler;
import com.ns.flow.model.AuditFlowPara;
import com.ns.flow.model.CuzActiveTaskEntity;
import com.ns.flow.model.ProcessEntity;
import com.ns.flow.model.StartFlowPara;
import org.flowable.engine.HistoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.flowable.task.api.TaskQuery;
import org.flowable.task.api.history.HistoricTaskInstance;
import org.flowable.task.api.history.HistoricTaskInstanceQuery;
import org.flowable.variable.api.history.HistoricVariableInstance;
import org.flowable.variable.api.history.HistoricVariableInstanceQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;

/**
 * @author bioxygenrich25
 * @description
 * @since 2024/1/15
 */
@Component
public class FlowCuzApi {


    private static final Logger log = LoggerFactory.getLogger(FlowCuzApi.class);


    @Resource
    FlowHandler flowHandler;

    @Resource
    private TaskService taskService;


    @Resource
    private HistoryService historyService;

    @Resource
    RuntimeService runtimeService;


    /**
     * 创建一个流程
     *
     * @param processEntity
     * @return
     */
    public String createFlow(ProcessEntity processEntity) {
        return flowHandler.createProcessDefinition(processEntity);
    }


    /**
     * 开启一个流程
     *
     * @param para
     * @return
     */
    public Boolean startFlow(StartFlowPara para) throws Exception {
        List tmp = historyService.createHistoricTaskInstanceQuery().processInstanceBusinessKey(para.getBusinessKey()).list();
        if (CollUtil.isNotEmpty(tmp)) {
            throw new RuntimeException("业务流水号重复");
        }
        // 判断是否启动流程
        boolean isStartProcess = null != runtimeService.createProcessInstanceQuery()
                .processDefinitionKey(para.getProcessKey())
                .processInstanceBusinessKey(para.getBusinessKey())
                .singleResult();
        if (!isStartProcess) {
            //记录开启流程的用户
            HashMap<String, Object> variable = MapUtil.newHashMap();
            variable.put("businessKey", para.getBusinessKey());
            variable.put("category", para.getProcessKey());
            variable.put("submitInfo", JSON.toJSONString(para));
            //启动流程,启动流程变量variable
            ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(para.getProcessKey(), para.getBusinessKey(), variable);
            log.info("启动流程实例成功:processInstanceId={}", processInstance.getId());

        }
        return !isStartProcess;

    }


    /**
     * 当前激活的待审批任务
     *
     * @param type
     * @param roleCode
     * @param userId
     * @return
     */
    public List<CuzActiveTaskEntity> queryActiveTaskListByParam(String type, String roleCode, String userId) {
        List<CuzActiveTaskEntity> list = new ArrayList<>();
        TaskQuery tq = taskService.createTaskQuery();
        if (StrUtil.isNotBlank(type)) {
            tq.taskCategory(type);
        }
        if (StrUtil.isNotBlank(roleCode)) {
            tq.taskCandidateGroupIn(Arrays.asList(roleCode));
        }
        if (StrUtil.isNotBlank(userId)) {
            tq.taskAssignee(userId);
        }
        List<Task> teacher = tq.active().list();
        for (Task task : teacher) {
            CuzActiveTaskEntity map = new CuzActiveTaskEntity();
            map.setTaskName(task.getName());
            map.setExecutionId(task.getExecutionId());
            map.setCategory(task.getCategory());
            map.setProcessInstanceId(task.getProcessInstanceId());
            map.setTaskId(task.getId());
            map.setProcessDefinitionId(task.getProcessDefinitionId());
            list.add(map);
        }
        return list;
    }


    /**
     * 携带参数的历史审批记录
     *
     * @param type
     * @param roleCode
     * @param userId
     * @param bizKey
     * @return
     */
    public Map<String, Object> queryHistoryTaskListByParam(String type, String roleCode, String userId, String bizKey) {
        HistoricTaskInstanceQuery tq = historyService.createHistoricTaskInstanceQuery();
        if (StrUtil.isNotBlank(type)) {
            tq.taskCategory(type);
        }
        if (StrUtil.isNotBlank(roleCode)) {
            tq.taskCandidateGroupIn(Arrays.asList(roleCode));
        }
        if (StrUtil.isNotBlank(userId)) {
            tq.taskAssignee(userId);
        }
        if (StrUtil.isNotBlank(bizKey)) {
            tq.processInstanceBusinessKey(bizKey);
        }
        List<HistoricTaskInstance> hisTasks = tq.finished().list();
        Map<String, Object> map = new HashMap<>();
        if (CollUtil.isNotEmpty(hisTasks)) {
            HistoricVariableInstanceQuery variableInstanceQuery = historyService.createHistoricVariableInstanceQuery();
            List<HistoricVariableInstance> paraList = variableInstanceQuery.processInstanceId(hisTasks.get(0).getProcessInstanceId()).list();
            if (CollUtil.isNotEmpty(paraList)) {
                paraList.forEach(x -> {
                    map.put(x.getVariableName(), x.getValue());
                });
            }
        }
        return map;
    }

    public void auditFlow(AuditFlowPara para) {
        List<Task> taskList = taskService.createTaskQuery().taskId(para.getTaskId()).taskCategory(para.getCategory()).list();
        Map<String, Object> auditMap = new HashMap<>();
        auditMap.put("auditResult", para.getAuditResult());
        for (Task task : taskList) {
            Map<String, Object> tmp = MapUtil.<String, Object>builder()
                    .put("name", task.getName())
                    .put("auditParams", para.getParams())
                    .put("operateTime", DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss"))
                    .put("auditResult", para.getAuditResult())
                    .put("assignee", para.getUserId()).build();
            auditMap.put(para.getTaskId(), JSON.toJSONString(tmp));
            taskService.complete(task.getId(), auditMap);
        }
    }


}
