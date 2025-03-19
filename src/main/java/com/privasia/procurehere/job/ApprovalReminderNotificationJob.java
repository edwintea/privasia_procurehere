package com.privasia.procurehere.job;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.privasia.procurehere.service.ApprovalNotificationService;

/**
 * @author ravi
 */
@Component
public class ApprovalReminderNotificationJob implements BaseSchedulerJob {

	private static final Logger LOG = LogManager.getLogger(ApprovalReminderNotificationJob.class);

	@Autowired
	ApprovalNotificationService approvalNotificationService;

	@Override
	public void execute(JobExecutionContext ctx) {
		LOG.info("Running Approva Reminder Notofication Job ");
		try {
			// Sending reminder pr approval notification
			approvalNotificationService.sendPrApprovalNotifications();
		} catch (Exception e) {
			LOG.error("Error while Executing reminder job :" + e.getMessage(), e);
		}

		try {
			// Sending reminder rfa approval notification
			approvalNotificationService.sendRfaApprovalNotifications();
		} catch (Exception e) {
			LOG.error("Error while Executing reminder job :" + e.getMessage(), e);
		}

		try {
			// Sending reminder rft approval notification
			approvalNotificationService.sendRftApprovalNotifications();
		} catch (Exception e) {
			LOG.error("Error while Executing reminder job :" + e.getMessage(), e);
		}

		try {
			// Sending reminder rfi approval notification
			approvalNotificationService.sendRfiApprovalNotifications();
		} catch (Exception e) {
			LOG.error("Error while Executing reminder job :" + e.getMessage(), e);
		}

		try {
			// Sending reminder rfp approval notification
			approvalNotificationService.sendRfpApprovalNotifications();
		} catch (Exception e) {
			LOG.error("Error while Executing reminder job :" + e.getMessage(), e);
		}

		try {
			// Sending reminder rfq approval notification
			approvalNotificationService.sendRfqApprovalNotifications();
		} catch (Exception e) {
			LOG.error("Error while Executing reminder job :" + e.getMessage(), e);
		}

		try {
			// Sending reminder rfs approval notification
			approvalNotificationService.sendRfsApprovalNotifications();
		} catch (Exception e) {
			LOG.error("Error while Executing reminder job :" + e.getMessage(), e);
		}
		
		try {
			// Sending reminder notification to event owner 
			approvalNotificationService.sendPrEscalationNotifications();
		} catch (Exception e) {
			LOG.error("Error while Executing reminder job :" + e.getMessage(), e);
		}
		
		try {
			// Sending reminder notification to event owner 
			approvalNotificationService.sendRfsEscalationNotifications();
		} catch (Exception e) {
			LOG.error("Error while Executing reminder job :" + e.getMessage(), e);
		}
		
		try {
			// Sending reminder notification to event owner 
			approvalNotificationService.sendRfaEscalationNotifications();
		} catch (Exception e) {
			LOG.error("Error while Executing reminder job :" + e.getMessage(), e);
		}
		
		try {
			// Sending reminder notification to event owner 
			approvalNotificationService.sendRfiEscalationNotifications();
		} catch (Exception e) {
			LOG.error("Error while Executing reminder job :" + e.getMessage(), e);
		}
		
		try {
			// Sending reminder notification to event owner 
			approvalNotificationService.sendRfpEscalationNotifications();
		} catch (Exception e) {
			LOG.error("Error while Executing reminder job :" + e.getMessage(), e);
		}
		
		try {
			// Sending reminder notification to event owner 
			approvalNotificationService.sendRfqEscalationNotifications();
		} catch (Exception e) {
			LOG.error("Error while Executing reminder job :" + e.getMessage(), e);
		}
		
		try {
			// Sending reminder notification to event owner 
			approvalNotificationService.sendRftEscalationNotifications();
		} catch (Exception e) {
			LOG.error("Error while Executing reminder job :" + e.getMessage(), e);
		}
		
		try {
			// Sending reminder notification to Po owner 
			approvalNotificationService.sendPoEscalationNotifications();
		} catch (Exception e) {
			LOG.error("Error while Executing reminder job :" + e.getMessage(), e);
		}
		
		try {
			// Sending reminder pr approval notification
			approvalNotificationService.sendPoApprovalNotifications();
		} catch (Exception e) {
			LOG.error("Error while Executing Po reminder job :" + e.getMessage(), e);
		}

		try {
			// Sending reminder Contract approval notification
			approvalNotificationService.sendContractApprovalNotifications();
		} catch (Exception e) {
			LOG.error("Error while Executing Contract reminder job :" + e.getMessage(), e);
		}

	}

}