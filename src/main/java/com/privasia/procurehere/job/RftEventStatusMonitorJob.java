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

import com.privasia.procurehere.core.dao.RftEventDao;
import com.privasia.procurehere.core.entity.RftEvent;
import com.privasia.procurehere.core.entity.RftEventAudit;
import com.privasia.procurehere.core.enums.AuditActionType;
import com.privasia.procurehere.core.enums.EventStatus;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.service.EventAuditService;
import com.privasia.procurehere.service.RftEventService;
import com.privasia.procurehere.service.TatReportService;

/**
 * @author Ravi
 */
@Component
public class RftEventStatusMonitorJob implements BaseSchedulerJob {

	private static final Logger LOG = LogManager.getLogger(RftEventStatusMonitorJob.class);

	@Autowired
	RftEventDao rftEventDao;

	@Autowired
	EventAuditService eventAuditService;

	@Autowired
	MessageSource messageSource;

	@Autowired
	RftEventService rftEventService;

	@Autowired
	JmsTemplate jmsTemplate;

	@Autowired
	TatReportService tatReportService;

	@Override
	@Transactional(readOnly = false)
	public void execute(JobExecutionContext ctx) {
		LOG.debug("Start  RFT Event Finish status Job - " + System.currentTimeMillis());
		changeFromActiveToFinish();
		LOG.debug("END  RFT Event Finish status Job - " + System.currentTimeMillis());
	}

	private void changeFromActiveToFinish() {
		EventStatus status = EventStatus.CLOSED;
		try {
			List<RftEvent> list = rftEventDao.getAllActiveEvents();
			Date now = new Date();
			for (RftEvent event : list) {
				String eventId = event.getId();
				if (event.getEventEnd() != null && now.after(event.getEventEnd())) {
					LOG.info("Changing Event status to CLOSED for RFT - " + event.getEventName());
					rftEventService.updateImmediately(eventId, status);
					RftEventAudit audit = new RftEventAudit(event, null, new Date(), AuditActionType.Close, messageSource.getMessage("event.audit.close", new Object[] { event.getEventName() }, Global.LOCALE));
					eventAuditService.save(audit);

					try {
						tatReportService.updateTatReportEventsStartDate(event.getId(), status);
					} catch (Exception e) {
						LOG.info("Error while executing Tat report status monitor job " + e.getMessage(), e);
					}

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

	public static void main(String[] args) {
		System.out.println(new Date(1586819430000l));
	}
}
