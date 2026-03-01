package com.creditapp.listener;

import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.ExecutionListener;
import org.springframework.stereotype.Component;

/**
 * Process start listener
 */
@Component("applicationStartListener")
@Slf4j
public class ApplicationStartListener implements ExecutionListener {

    @Override
    public void notify(DelegateExecution execution) throws Exception {
        String applicationId = (String) execution.getVariable("applicationId");
        log.info("🚀 Process started for application: {}", applicationId);
        log.info("Process instance ID: {}", execution.getProcessInstanceId());
    }
}
