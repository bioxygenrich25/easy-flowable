package com.ns.flow.config;


import com.google.common.collect.Maps;
import com.ns.flow.listeners.GlobalProcessCancelledListener;
import com.ns.flow.listeners.ProcessEndListener;
import org.flowable.common.engine.api.delegate.event.FlowableEngineEventType;
import org.flowable.common.engine.api.delegate.event.FlowableEventListener;
import org.flowable.spring.SpringProcessEngineConfiguration;
import org.flowable.spring.boot.EngineConfigurationConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * flowable 全局监听器配置类
 *
 * @author bioxygenrich25
 * @description
 * @since 2024/1/15
 */
@Configuration
public class FlowableListenerConfig {


    /**
     * 将自定义监听器纳入flowable监听
     *
     * @param
     * @return org.flowable.spring.boot.EngineConfigurationConfigurer<org.flowable.spring.SpringProcessEngineConfiguration>
     */
    @Bean
    public EngineConfigurationConfigurer<SpringProcessEngineConfiguration> globalListenerConfigurer() {
        return engineConfiguration -> {
            engineConfiguration.setTypedEventListeners(this.customFlowableListeners());
        };
    }

    ProcessEndListener processEndListener;

    GlobalProcessCancelledListener processCancelledListener;


    /**
     * 监听类配置 {@link } flowable监听器级别
     *
     * @param
     * @return java.util.Map<java.lang.String, java.util.List < org.flowable.common.engine.api.delegate.event.FlowableEventListener>>
     */
    private Map<String, List<FlowableEventListener>> customFlowableListeners() {
        Map<String, List<FlowableEventListener>> listenerMap = Maps.newHashMap();
        listenerMap.put(FlowableEngineEventType.PROCESS_CANCELLED.name(), new ArrayList<>(Collections.singletonList(processCancelledListener)));
        listenerMap.put(FlowableEngineEventType.PROCESS_COMPLETED.name(), new ArrayList<>(Collections.singletonList(processEndListener)));
        return listenerMap;
    }

}
