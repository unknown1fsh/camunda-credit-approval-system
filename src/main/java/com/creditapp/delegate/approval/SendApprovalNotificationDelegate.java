package com.creditapp.delegate.approval;

import com.creditapp.service.CreditApplicationService;
import com.creditapp.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

/**
 * Send approval notification delegate
 */
@Component("sendApprovalNotificationDelegate")
@RequiredArgsConstructor
@Slf4j
public class SendApprovalNotificationDelegate implements JavaDelegate {

    private final NotificationService notificationService;
    private final CreditApplicationService applicationService;

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        String applicationId = (String) execution.getVariable("applicationId");
        log.info("Sending approval notification for application {}", applicationId);

        String email = (String) execution.getVariable("email");
        String phone = (String) execution.getVariable("phone");
        Double loanAmount = (Double) execution.getVariable("loanAmount");
        String approvedBy = (String) execution.getVariable("approvedBy");

        if (approvedBy == null) {
            approvedBy = execution.getCurrentActivityName();
        }

        // Update application status
        applicationService.approveApplication(applicationId, approvedBy);

        // Send notifications
        notificationService.notifyApproval(email, phone, applicationId, loanAmount);

        log.info("Approval notification sent for application {}", applicationId);
    }
}
