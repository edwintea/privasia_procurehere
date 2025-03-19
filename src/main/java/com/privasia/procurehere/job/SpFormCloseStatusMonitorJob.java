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
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.StringUtils;
import com.privasia.procurehere.service.SupplierPerformanceAuditService;
import com.privasia.procurehere.service.SupplierPerformanceFormService;

/**
 * @author Ravi
 */
@Component
public class SpFormCloseStatusMonitorJob implements BaseSchedulerJob {

	private static final Logger LOG = LogManager.getLogger(SpFormCloseStatusMonitorJob.class);

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
		changeFromActiveToFinish();
	}

	private void changeFromActiveToFinish() {
		try {
			List<SupplierPerformanceForm> list = supplierPerformanceFormDao.getAllActiveEventsforJob();
			Date now = new Date();
			for (SupplierPerformanceForm form : list) {
				String formId = form.getId();
				if (form.getEvaluationEndDate() != null && now.after(form.getEvaluationEndDate())) {
					supplierPerformanceFormService.updateImmediately(formId, SupplierPerformanceFormStatus.CLOSED);
					SupplierPerformanceAudit audit = new SupplierPerformanceAudit(form, null, form.getFormOwner(), new Date(), SupplierPerformanceAuditActionType.Closed, messageSource.getMessage("sp.audit.closed", new Object[] { StringUtils.checkString(form.getFormId()) }, Global.LOCALE), Boolean.TRUE, Boolean.TRUE, Boolean.TRUE);
					supplierPerformanceAuditService.save(audit);

					try {
						BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.CLOSED,  messageSource.getMessage("sp.audit.closed", new Object[] { StringUtils.checkString(form.getFormId()) }, Global.LOCALE), form.getBuyer().getId(), form.getFormOwner(), new Date(), ModuleType.PerformanceEvaluation);
						buyerAuditTrailDao.save(buyerAuditTrail);
					} catch (Exception e) {
						LOG.error("Error while recording active audit " + e.getMessage(), e);
					}

					
					try {
						jmsTemplate.send("QUEUE.SPF.CLOSED", new MessageCreator() {
							@Override
							public Message createMessage(Session session) throws JMSException {
								TextMessage objectMessage = session.createTextMessage();
								objectMessage.setText(formId);
								return objectMessage;
							}
						});
					} catch (Exception e) {
						LOG.error("Error sending message to queue : " + e.getMessage(), e);
					}
				}
			}
		} catch (Exception e) {
			LOG.error("Error during change of SPF status from Active to Close : " + e.getMessage(), e);
		}
	}

}
