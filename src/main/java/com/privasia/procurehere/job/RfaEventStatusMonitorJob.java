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

import com.privasia.procurehere.core.dao.RfaEventDao;
import com.privasia.procurehere.core.entity.RfaEvent;
import com.privasia.procurehere.core.entity.RfaEventAudit;
import com.privasia.procurehere.core.enums.AuditActionType;
import com.privasia.procurehere.core.enums.EventStatus;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.service.EventAuditService;
import com.privasia.procurehere.service.RfaEventService;
import com.privasia.procurehere.service.TatReportService;

/**
 * @author Ravi
 */
@Component
public class RfaEventStatusMonitorJob implements BaseSchedulerJob {

	private static final Logger LOG = LogManager.getLogger(RfaEventStatusMonitorJob.class);

	@Autowired
	RfaEventDao rfaEventDao;

	@Autowired
	EventAuditService eventAuditService;

	@Autowired
	MessageSource messageSource;

	@Autowired
	RfaEventService rfaEventService;

	@Autowired
	JmsTemplate jmsTemplate;

	@Autowired
	TatReportService tatReportService;

	@Override
	@Transactional(readOnly = false)
	public void execute(JobExecutionContext ctx) {
		LOG.debug("Start  RFA Event Finish status Job - " + System.currentTimeMillis());
		changeFromActiveToFinish();
		LOG.debug("END  RFA Event Finish status Job - " + System.currentTimeMillis());
	}

	private void changeFromActiveToFinish() {
		EventStatus status = EventStatus.CLOSED;
		try {
			List<RfaEvent> list = rfaEventDao.getAllActiveEvents();
			Date now = new Date();
			for (RfaEvent event : list) {
				String eventId = event.getId();
				if (event.getEventEnd() != null && now.after(event.getEventEnd())) {
					LOG.info("Changing Event status to CLOSED for RFA  - " + event.getEventName());

					// For dutch auction there is a possibility for auction without bq and cq. so further no
					// evaluation required after event end
					// so directly setting status to complete...
					if (!event.getBillOfQuantity() && !event.getQuestionnaires()) {
						status = EventStatus.COMPLETE;
					}

					rfaEventService.updateImmediately(eventId, status, event.getAuctionComplitationTime() == null ? new Date() : null);

					RfaEventAudit audit = new RfaEventAudit(event, null, new Date(), AuditActionType.Close, messageSource.getMessage("event.audit.close", new Object[] { event.getEventName() }, Global.LOCALE));
					eventAuditService.save(audit);

					try {
						tatReportService.updateTatReportEventsStartDate(event.getId(), status);
					} catch (Exception e) {
						LOG.info("Error while executing Tat report status monitor job " + e.getMessage(), e);
					}

//					try {
//						tatReportService.updateTatReportEventsStartDate(eventId, EventStatus.CLOSED);
//					} catch (Exception e) {
//						LOG.info("Error while executing Tat report status monitor job " + e.getMessage(), e);
//					}

					try {
						// jmsTemplate.setDefaultDestinationName("QUEUE.EVENT.CLOSED");
						jmsTemplate.send("QUEUE.EVENT.CLOSED", new MessageCreator() {
							@Override
							public Message createMessage(Session session) throws JMSException {
								TextMessage objectMessage = session.createTextMessage();
								objectMessage.setText(eventId);
								return objectMessage;
							}
						});
					} catch (Exception e) {
						LOG.error("Error sending message to queue : " + e.getMessage(), e);
					}
				}
			}
		} catch (Exception e) {
			LOG.error("Error during change of event status from Active to Close : " + e.getMessage(), e);
		}
	}

}
