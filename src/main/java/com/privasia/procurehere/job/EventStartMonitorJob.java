package com.privasia.procurehere.job;

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

import com.privasia.procurehere.core.dao.RfaEventAuditDao;
import com.privasia.procurehere.core.dao.RfiEventAuditDao;
import com.privasia.procurehere.core.dao.RfpEventAuditDao;
import com.privasia.procurehere.core.dao.RfqEventAuditDao;
import com.privasia.procurehere.core.dao.RftEventAuditDao;
import com.privasia.procurehere.core.dao.RfxViewDao;
import com.privasia.procurehere.core.entity.RfaEvent;
import com.privasia.procurehere.core.entity.RfaEventAudit;
import com.privasia.procurehere.core.entity.RfiEvent;
import com.privasia.procurehere.core.entity.RfiEventAudit;
import com.privasia.procurehere.core.entity.RfpEvent;
import com.privasia.procurehere.core.entity.RfpEventAudit;
import com.privasia.procurehere.core.entity.RfqEvent;
import com.privasia.procurehere.core.entity.RfqEventAudit;
import com.privasia.procurehere.core.entity.RftEvent;
import com.privasia.procurehere.core.entity.RftEventAudit;
import com.privasia.procurehere.core.entity.RfxView;
import com.privasia.procurehere.core.enums.AuditActionType;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.service.RfaEventService;
import com.privasia.procurehere.service.RfiEventService;
import com.privasia.procurehere.service.RfpEventService;
import com.privasia.procurehere.service.RfqEventService;
import com.privasia.procurehere.service.RftEventService;

/**
 * @author parveen
 */
@Component
public class EventStartMonitorJob implements BaseSchedulerJob {

	private static final Logger LOG = LogManager.getLogger(EventStartMonitorJob.class);

	@Autowired
	MessageSource messageSource;

	@Autowired
	RfxViewDao rfxViewDao;

	@Autowired
	RftEventService rftEventService;

	@Autowired
	RfaEventService rfaEventService;

	@Autowired
	RfiEventService rfiEventService;

	@Autowired
	RfpEventService rfpEventService;

	@Autowired
	RfqEventService rfqEventService;

	@Autowired
	RfaEventAuditDao rfaEventAuditDao;

	@Autowired
	RfpEventAuditDao rfpEventAuditDao;

	@Autowired
	RfqEventAuditDao rfqEventAuditDao;

	@Autowired
	RftEventAuditDao rftEventAuditDao;

	@Autowired
	RfiEventAuditDao rfiEventAuditDao;

	@Autowired
	JmsTemplate jmsTemplate;
	
	@Override
	@Transactional(readOnly = false)
	public void execute(JobExecutionContext ctx) {
//		 LOG.info("Running Event Start Monitoring job.... :D ");
		try {
			checkEventStartNotification();
		} catch (Exception e) {
			LOG.error("Error while executing status monitor job " + e.getMessage(), e);
		}
	}

	public void checkEventStartNotification() {
		List<RfxView> viewList = rfxViewDao.getEventRemindersForNotification();
		if (CollectionUtil.isNotEmpty(viewList)) {
			for (RfxView rfxView : viewList) {
				LOG.info("Sending Event Start Notification : " + rfxView.getId());
				switch (rfxView.getType()) {
				case RFA:
					rfaEventService.updateEventStartMessageFlagImmediately(rfxView.getId());
					RfaEvent rfaEvent = new RfaEvent();
					rfaEvent.setId(rfxView.getId());
					RfaEventAudit rfaAudit = new RfaEventAudit(rfaEvent, null, new java.util.Date(), AuditActionType.Start, messageSource.getMessage("event.audit.start", new Object[] { rfxView.getEventName() }, Global.LOCALE));
					rfaEventAuditDao.save(rfaAudit);
					break;
				case RFI:
					rfiEventService.updateEventStartMessageFlagImmediately(rfxView.getId());
					RfiEvent rfiEvent = new RfiEvent();
					rfiEvent.setId(rfxView.getId());
					RfiEventAudit rfiAudit = new RfiEventAudit(rfiEvent, null, new java.util.Date(), AuditActionType.Start, messageSource.getMessage("event.audit.start", new Object[] { rfxView.getEventName() }, Global.LOCALE));
					rfiEventAuditDao.save(rfiAudit);
					break;
				case RFP:
					rfpEventService.updateEventStartMessageFlagImmediately(rfxView.getId());
					RfpEvent rfpEvent = new RfpEvent();
					rfpEvent.setId(rfxView.getId());
					RfpEventAudit rfpAudit = new RfpEventAudit(rfpEvent, null, new java.util.Date(), AuditActionType.Start, messageSource.getMessage("event.audit.start", new Object[] { rfxView.getEventName() }, Global.LOCALE));
					rfpEventAuditDao.save(rfpAudit);
					break;
				case RFQ:
					rfqEventService.updateEventStartMessageFlagImmediately(rfxView.getId());
					RfqEvent rfqEvent = new RfqEvent();
					rfqEvent.setId(rfxView.getId());
					RfqEventAudit rfqAudit = new RfqEventAudit(rfqEvent, null, new java.util.Date(), AuditActionType.Start, messageSource.getMessage("event.audit.start", new Object[] { rfxView.getEventName() }, Global.LOCALE));
					rfqEventAuditDao.save(rfqAudit);
					break;
				case RFT:
					rftEventService.updateEventStartMessageFlagImmediately(rfxView.getId());
					RftEvent rftEvent = new RftEvent();
					rftEvent.setId(rfxView.getId());
					RftEventAudit rftAudit = new RftEventAudit(rftEvent, null, new java.util.Date(), AuditActionType.Start, messageSource.getMessage("event.audit.start", new Object[] { rfxView.getEventName() }, Global.LOCALE));
					rftEventAuditDao.save(rftAudit);
					break;
				default:
					break;
				}
				
				try {
//					jmsTemplate.setDefaultDestinationName("QUEUE.EVENT.START");
					jmsTemplate.send("QUEUE.EVENT.START", new MessageCreator() {
						@Override
						public Message createMessage(Session session) throws JMSException {
							TextMessage objectMessage = session.createTextMessage();
							objectMessage.setText(rfxView.getId());
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
