/**
 * 
 */
package com.privasia.procurehere.job;

import java.util.Date;
import java.util.List;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.privasia.procurehere.core.dao.BuyerAuditTrailDao;
import com.privasia.procurehere.core.dao.SupplierPerformanceFormDao;
import com.privasia.procurehere.core.entity.BuyerAuditTrail;
import com.privasia.procurehere.core.entity.SupplierPerformanceAudit;
import com.privasia.procurehere.core.entity.SupplierPerformanceForm;
import com.privasia.procurehere.core.enums.AuditTypes;
import com.privasia.procurehere.core.enums.ModuleType;
import com.privasia.procurehere.core.enums.SupplierPerformanceAuditActionType;
import com.privasia.procurehere.core.enums.SupplierPerformanceFormStatus;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.StringUtils;
import com.privasia.procurehere.service.SupplierPerformanceAuditService;
import com.privasia.procurehere.service.SupplierPerformanceFormService;

/**
 * @author Jayshree
 */
@Component
public class SpFormActiveStatusMonitorJob implements BaseSchedulerJob {

	private static final Logger LOG = LogManager.getLogger(SpFormActiveStatusMonitorJob.class);

	@Autowired
	SupplierPerformanceFormDao supplierPerformanceFormDao;

	@Autowired
	SupplierPerformanceFormService supplierPerformanceFormService;

	@Autowired
	MessageSource messageSource;

	@Autowired
	SupplierPerformanceAuditService supplierPerformanceAuditService;

	@Autowired
	JmsTemplate jmsTemplate;

	@Autowired
	BuyerAuditTrailDao buyerAuditTrailDao;

	@Override
	@Transactional(readOnly = false)
	public void execute(JobExecutionContext ctx) {
		LOG.debug("Start  Supplier Performance Form ACTIVE status Job - " + System.currentTimeMillis());
		changeFromScheduledToActive();
		LOG.debug("END  Supplier Performance Form ACTIVE status Job - " + System.currentTimeMillis());
	}

	private void changeFromScheduledToActive() {
		try {
			List<SupplierPerformanceForm> formList = supplierPerformanceFormDao.getAllScheduledEventsforJob();
			if (CollectionUtil.isNotEmpty(formList)) {
				Date now = new Date();
				SupplierPerformanceFormStatus formStatus = SupplierPerformanceFormStatus.ACTIVE;
				// String timeZone = "GMT+8:00";
				for (SupplierPerformanceForm form : formList) {
					LOG.debug("Form Id " + form.getFormId() + " Start Date : " + form.getEvaluationStartDate());
					String id = form.getId();
					if (form.getEvaluationStartDate() != null && now.after(form.getEvaluationStartDate())) {
						LOG.info("Changing Form status to ACTIVE - " + form.getFormName());
						// jmsTemplate.setDefaultDestinationName("QUEUE.EVENT.ACTIVE");

						supplierPerformanceFormService.updateImmediately(id, formStatus);
						if (form.getOldFormStatus() == null || SupplierPerformanceFormStatus.DRAFT == form.getOldFormStatus() || SupplierPerformanceFormStatus.SCHEDULED == form.getOldFormStatus()) {
							SupplierPerformanceAudit audit = new SupplierPerformanceAudit(form, null, form.getFormOwner(), new Date(), SupplierPerformanceAuditActionType.Active, messageSource.getMessage("sp.audit.active", new Object[] { StringUtils.checkString(form.getFormId()) }, Global.LOCALE), Boolean.TRUE, Boolean.TRUE, Boolean.TRUE);
							supplierPerformanceAuditService.save(audit);

							try {
								BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.ACTIVE, messageSource.getMessage("sp.audit.active", new Object[] { StringUtils.checkString(form.getFormId()) }, Global.LOCALE), form.getBuyer().getId(), form.getFormOwner(), new Date(), ModuleType.PerformanceEvaluation);
								buyerAuditTrailDao.save(buyerAuditTrail);
							} catch (Exception e) {
								LOG.error("Error while recording active audit " + e.getMessage(), e);
							}
						}

						// ONLY SEND THE START NOTIFICATION IF THE FORM IS BECOMING ACTIVE FOR THE FIRST TIME - AND NOT
						// AFTER A SUSPEND
						if (form.getOldFormStatus() == SupplierPerformanceFormStatus.DRAFT || form.getOldFormStatus() == SupplierPerformanceFormStatus.SCHEDULED) {
							try {
								jmsTemplate.send("QUEUE.SPF.ACTIVE", new MessageCreator() {
									@Override
									public Message createMessage(Session session) throws JMSException {
										TextMessage objectMessage = session.createTextMessage();
										objectMessage.setText(id);
										return objectMessage;
									}
								});
							} catch (Exception e) {
								LOG.error("Error sending message to queue : " + e.getMessage(), e);
							}
						}

					}
				}
			}
		} catch (Exception e) {
			LOG.error("Error during change form status from SCHEDULED to ACTIVE : " + e.getMessage(), e);
		}
	}

}
