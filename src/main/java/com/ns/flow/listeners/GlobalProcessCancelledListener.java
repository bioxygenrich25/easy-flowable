package com.ns.flow.listeners;

import com.alibaba.fastjson2.JSON;
import lombok.extern.slf4j.Slf4j;
import org.flowable.common.engine.api.delegate.event.FlowableEvent;
import org.flowable.common.engine.api.delegate.event.FlowableEventListener;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.event.impl.FlowableProcessCancelledEventImpl;
import org.springframework.stereotype.Component;

/**
 * 全局流程取消监听器
 *
 * @author bioxygenrich25
 */
@Slf4j
@Component
public class GlobalProcessCancelledListener implements FlowableEventListener {


    @Override
    public void onEvent(FlowableEvent flowableEvent) {
        log.info("GlobalProcessCancelledListener:{}", JSON.toJSONString(flowableEvent));
        FlowableProcessCancelledEventImpl processCancelledEvent = (FlowableProcessCancelledEventImpl) flowableEvent;
        DelegateExecution execution = processCancelledEvent.getExecution();
        String eventType = processCancelledEvent.getType().name();

        log.info(">>> 流程取消了........");
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
