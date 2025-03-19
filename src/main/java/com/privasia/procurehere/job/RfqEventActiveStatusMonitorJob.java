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

import com.privasia.procurehere.core.dao.RfqEventDao;
import com.privasia.procurehere.core.entity.RfqEvent;
import com.privasia.procurehere.core.entity.RfqEventAudit;
import com.privasia.procurehere.core.enums.AuditActionType;
import com.privasia.procurehere.core.enums.EventStatus;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.StringUtils;
import com.privasia.procurehere.service.EventAuditService;
import com.privasia.procurehere.service.RfqEventService;
import com.privasia.procurehere.service.TatReportService;

/**
 * @author Ravi
 */
@Component
public class RfqEventActiveStatusMonitorJob implements BaseSchedulerJob {

	private static final Logger LOG = LogManager.getLogger(RfqEventActiveStatusMonitorJob.class);

	@Autowired
	RfqEventDao rfqEventDao;

	@Autowired
	EventAuditService eventAuditService;

	@Autowired
	MessageSource messageSource;

	@Autowired
	RfqEventService rfqEventService;

	@Autowired
	JmsTemplate jmsTemplate;

	@Autowired
	TatReportService tatReportService;

	@Override
	@Transactional(readOnly = false)
	public void execute(JobExecutionContext ctx) {
		LOG.debug("Start  RFQ Event Active status Job - " + System.currentTimeMillis());
		changeFromApprovedToActive();
		LOG.debug("END  RFQ Event Active status Job - " + System.currentTimeMillis());
	}

	private void changeFromApprovedToActive() {
		try {
			List<RfqEvent> list = rfqEventDao.getAllApprovedEventsforJob();
			if (CollectionUtil.isNotEmpty(list)) {
				Date now = new Date();
				EventStatus status = EventStatus.ACTIVE;
				// String timeZone = "GMT+8:00";
				for (RfqEvent event : list) {
					String id = event.getId();
					if (event.getEventPublishDate() != null && now.after(event.getEventPublishDate())) {
						LOG.info("Changing Event status to ACTIVE for RFQ - " + event.getEventName());
						// jmsTemplate.setDefaultDestinationName("QUEUE.EVENT.ACTIVE");
						rfqEventService.updateImmediately(id, status);
						RfqEventAudit audit = new RfqEventAudit(event, null, new Date(), AuditActionType.Active, messageSource.getMessage("event.audit.active", new Object[] { StringUtils.checkString(event.getEventName()) }, Global.LOCALE));
						eventAuditService.save(audit);

						try {
							tatReportService.updateTatReportEventsStartDate(event.getId(), EventStatus.ACTIVE);
						} catch (Exception e) {
							LOG.info("Error while executing Tat report status monitor job " + e.getMessage(), e);
						}

						try {
							jmsTemplate.send("QUEUE.EVENT.ACTIVE", new MessageCreator() {
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
		} catch (Exception e) {
			LOG.error("Error during change of event status from Approved to Active : " + e.getMessage(), e);
		}
	}

}
