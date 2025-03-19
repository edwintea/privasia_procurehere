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
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.privasia.procurehere.core.dao.SupplierPerformanceEvaluationDao;
import com.privasia.procurehere.core.dao.SupplierPerformanceFormDao;
import com.privasia.procurehere.core.entity.RfaEventMeetingReminder;
import com.privasia.procurehere.core.entity.RfaReminder;
import com.privasia.procurehere.core.entity.RfiEventMeetingReminder;
import com.privasia.procurehere.core.entity.RfiReminder;
import com.privasia.procurehere.core.entity.RfpEventMeetingReminder;
import com.privasia.procurehere.core.entity.RfpReminder;
import com.privasia.procurehere.core.entity.RfqEventMeetingReminder;
import com.privasia.procurehere.core.entity.RfqReminder;
import com.privasia.procurehere.core.entity.RftEventMeetingReminder;
import com.privasia.procurehere.core.entity.RftReminder;
import com.privasia.procurehere.core.entity.SupplierPerformanceReminder;
import com.privasia.procurehere.core.enums.RfxTypes;
import com.privasia.procurehere.service.RfaMeetingService;
import com.privasia.procurehere.service.RfaReminderService;
import com.privasia.procurehere.service.RfiMeetingService;
import com.privasia.procurehere.service.RfiReminderService;
import com.privasia.procurehere.service.RfpMeetingService;
import com.privasia.procurehere.service.RfpReminderService;
import com.privasia.procurehere.service.RfqMeetingService;
import com.privasia.procurehere.service.RfqReminderService;
import com.privasia.procurehere.service.RftMeetingService;
import com.privasia.procurehere.service.RftReminderService;

/**
 * @author Nitin Otageri
 */
@Component
public class ReminderMonitorJob implements BaseSchedulerJob {

	private static final Logger LOG = LogManager.getLogger(ReminderMonitorJob.class);

	@Autowired
	RftMeetingService rftMeetingService;

	@Autowired
	RfpMeetingService rfpMeetingService;

	@Autowired
	RfqMeetingService rfqMeetingService;

	@Autowired
	RfiMeetingService rfiMeetingService;

	@Autowired
	RfaMeetingService rfaMeetingService;

	@Autowired
	RftReminderService rftReminderService;

	@Autowired
	RfpReminderService rfpReminderService;

	@Autowired
	RfqReminderService rfqReminderService;

	@Autowired
	RfiReminderService rfiReminderService;

	@Autowired
	RfaReminderService rfaReminderService;

	@Autowired
	JmsTemplate jmsTemplate;

	@Autowired
	SupplierPerformanceFormDao supplierPerformanceFormDao;

	@Autowired
	SupplierPerformanceEvaluationDao supplierPerformanceEvaluationDao;

	@Override
	@Transactional(readOnly = false)
	public void execute(JobExecutionContext ctx) {
		// LOG.info("Running ReminderMonitorJob.... :D ");
		try {
			checkMeetingReminders();
		} catch (Exception e) {
			LOG.error("Error while Executing meeting reminders job :" + e.getMessage(), e);
		}
		try {
			checkEventReminders();
		} catch (Exception e) {
			LOG.error("Error while Executing Event reminders job :" + e.getMessage(), e);
		}
		try {
			checkSupplierPerformanceFormReminders();
		} catch (Exception e) {
			LOG.error("Error while Executing SPM reminder job :" + e.getMessage(), e);
		}
	}

	private void checkEventReminders() {
		// Sending Mails and Notifications for RFT Events reminders
		// String timeZone = "GMT+8:00";

		List<RftReminder> rftReminders = rftReminderService.getEventRemindersForNotification();
		for (RftReminder reminder : rftReminders) {
			LOG.info("Rft Sending notifocation for Event Reminder Date and time : " + reminder.getReminderDate() + " Event Name : " + reminder.getRftEvent().getEventName());
			rftReminderService.updateImmediately(reminder.getId());

			try {
				jmsTemplate.send("QUEUE.EVENT.REMINDER", new MessageCreator() {
					@Override
					public Message createMessage(Session session) throws JMSException {
						TextMessage objectMessage = session.createTextMessage();
						objectMessage.setText(RfxTypes.RFT.name() + reminder.getId());
						return objectMessage;
					}
				});
			} catch (Exception e) {
				LOG.error("Error sending message to queue : " + e.getMessage(), e);
			}

		}

		// Sending Mails and Notifications for RFP Events reminders
		List<RfpReminder> rfpReminders = rfpReminderService.getEventRemindersForNotification();
		for (RfpReminder reminder : rfpReminders) {
			LOG.info("Rfp Sending notifocation for Event Reminder Date and time : " + reminder.getReminderDate() + " Event Name : " + reminder.getRfxEvent().getEventName());
			rfpReminderService.updateImmediately(reminder.getId());

			try {
				jmsTemplate.send("QUEUE.EVENT.REMINDER", new MessageCreator() {
					@Override
					public Message createMessage(Session session) throws JMSException {
						TextMessage objectMessage = session.createTextMessage();
						objectMessage.setText(RfxTypes.RFP.name() + reminder.getId());
						return objectMessage;
					}
				});
			} catch (Exception e) {
				LOG.error("Error sending message to queue : " + e.getMessage(), e);
			}

		}

		// Sending Mails and Notifications for RFQ Events reminders
		List<RfqReminder> rfqReminders = rfqReminderService.getEventRemindersForNotification();
		for (RfqReminder reminder : rfqReminders) {
			LOG.info("Rfq Sending notifocation for Event Reminder Date and time : " + reminder.getReminderDate() + " Event Name : " + reminder.getRfxEvent().getEventName());
			rfqReminderService.updateImmediately(reminder.getId());

			try {
				// jmsTemplate.setDefaultDestinationName("QUEUE.EVENT.REMINDER");
				// jmsTemplate.setDeliveryMode(DeliveryMode.PERSISTENT);
				jmsTemplate.send("QUEUE.EVENT.REMINDER", new MessageCreator() {
					@Override
					public Message createMessage(Session session) throws JMSException {
						TextMessage objectMessage = session.createTextMessage();
						objectMessage.setText(RfxTypes.RFQ.name() + reminder.getId());
						return objectMessage;
					}
				});
			} catch (Exception e) {
				LOG.error("Error sending message to queue : " + e.getMessage(), e);
			}

		}

		// Sending Mails and Notifications for RFI Events reminders
		List<RfiReminder> rfiReminders = rfiReminderService.getEventRemindersForNotification();
		for (RfiReminder reminder : rfiReminders) {
			LOG.info("Rfi Sending notifocation for Event Reminder Date and time : " + reminder.getReminderDate() + " Event Name : " + reminder.getRfiEvent().getEventName());

			rfiReminderService.updateImmediately(reminder.getId());
			try {
				jmsTemplate.send("QUEUE.EVENT.REMINDER", new MessageCreator() {
					@Override
					public Message createMessage(Session session) throws JMSException {
						TextMessage objectMessage = session.createTextMessage();
						objectMessage.setText(RfxTypes.RFI.name() + reminder.getId());
						return objectMessage;
					}
				});
			} catch (Exception e) {
				LOG.error("Error sending message to queue : " + e.getMessage(), e);
			}

		}

		// Sending Mails and Notifications for RFA Events reminders
		List<RfaReminder> rfaReminders = rfaReminderService.getEventRemindersForNotification();
		for (RfaReminder reminder : rfaReminders) {
			LOG.info("Rfa Sending notifocation for Event Reminder Date and time : " + reminder.getReminderDate() + " Event Name : " + reminder.getRfaEvent().getEventName());

			rfaReminderService.updateImmediately(reminder.getId());

			try {
				jmsTemplate.send("QUEUE.EVENT.REMINDER", new MessageCreator() {
					@Override
					public Message createMessage(Session session) throws JMSException {
						TextMessage objectMessage = session.createTextMessage();
						objectMessage.setText(RfxTypes.RFA.name() + reminder.getId());
						return objectMessage;
					}
				});
			} catch (Exception e) {
				LOG.error("Error sending message to queue : " + e.getMessage(), e);
			}

		}

	}

	private void checkMeetingReminders() {

		// Sending Mails and Notifications for RFT meeting reminders
		List<RftEventMeetingReminder> rftReminders = rftMeetingService.getMeetingRemindersForNotification();
		for (RftEventMeetingReminder reminder : rftReminders) {
			LOG.info("Rft Sending notifocation for Reminder Date and time : " + reminder.getReminderDate() + " Meeting : " + reminder.getRfxEventMeeting().getTitle());
			reminder.setReminderSent(Boolean.TRUE);
			rftMeetingService.updateImmediately(reminder.getId());

			try {
				jmsTemplate.send("QUEUE.MEETING.REMINDER", new MessageCreator() {
					@Override
					public Message createMessage(Session session) throws JMSException {
						TextMessage objectMessage = session.createTextMessage();
						objectMessage.setText(RfxTypes.RFT.name() + reminder.getId());
						return objectMessage;
					}
				});
			} catch (Exception e) {
				LOG.error("Error sending message to queue : " + e.getMessage(), e);
			}

		}

		// Sending Mails and Notifications for RFP meeting reminders
		List<RfpEventMeetingReminder> rfpReminders = rfpMeetingService.getMeetingRemindersForNotification();
		for (RfpEventMeetingReminder reminder : rfpReminders) {
			LOG.info("Rfp Sending notifocation for Reminder Date and time : " + reminder.getReminderDate() + " Meeting : " + reminder.getRfxEventMeeting().getTitle());
			rfpMeetingService.updateImmediately(reminder.getId());

			try {
				jmsTemplate.send("QUEUE.MEETING.REMINDER", new MessageCreator() {
					@Override
					public Message createMessage(Session session) throws JMSException {
						TextMessage objectMessage = session.createTextMessage();
						objectMessage.setText(RfxTypes.RFP.name() + reminder.getId());
						return objectMessage;
					}
				});
			} catch (Exception e) {
				LOG.error("Error sending message to queue : " + e.getMessage(), e);
			}
		}

		// Sending Mails and Notifications for RFQ meeting reminders
		List<RfqEventMeetingReminder> rfqReminders = rfqMeetingService.getMeetingRemindersForNotification();
		for (RfqEventMeetingReminder reminder : rfqReminders) {
			LOG.info("Rfq Sending notifocation for Reminder Date and time : " + reminder.getReminderDate() + " Meeting : " + reminder.getRfxEventMeeting().getTitle());
			// reminder.setReminderSent(Boolean.TRUE);
			rfqMeetingService.updateImmediately(reminder.getId());

			try {
				jmsTemplate.send("QUEUE.MEETING.REMINDER", new MessageCreator() {
					@Override
					public Message createMessage(Session session) throws JMSException {
						TextMessage objectMessage = session.createTextMessage();
						objectMessage.setText(RfxTypes.RFQ.name() + reminder.getId());
						return objectMessage;
					}
				});
			} catch (Exception e) {
				LOG.error("Error sending message to queue : " + e.getMessage(), e);
			}
		}

		// Sending Mails and Notifications for RFI meeting reminders
		List<RfiEventMeetingReminder> rfiReminders = rfiMeetingService.getMeetingRemindersForNotification();
		for (RfiEventMeetingReminder reminder : rfiReminders) {
			LOG.info("Rfi Sending notifocation for Reminder Date and time : " + reminder.getReminderDate() + " Meeting : " + reminder.getRfxEventMeeting().getTitle());
			rfiMeetingService.updateImmediately(reminder.getId());

			try {
				jmsTemplate.send("QUEUE.MEETING.REMINDER", new MessageCreator() {
					@Override
					public Message createMessage(Session session) throws JMSException {
						TextMessage objectMessage = session.createTextMessage();
						objectMessage.setText(RfxTypes.RFI.name() + reminder.getId());
						return objectMessage;
					}
				});
			} catch (Exception e) {
				LOG.error("Error sending message to queue : " + e.getMessage(), e);
			}

		}

		// Sending Mails and Notifications for RFA meeting reminders
		List<RfaEventMeetingReminder> rfaReminders = rfaMeetingService.getMeetingRemindersForNotification();
		for (RfaEventMeetingReminder reminder : rfaReminders) {
			LOG.info("Rfa Sending notifocation for Reminder Date and time : " + reminder.getReminderDate() + " Meeting : " + reminder.getRfaEventMeeting().getTitle());
			rfaMeetingService.updateImmediately(reminder.getId());

			try {
				jmsTemplate.send("QUEUE.MEETING.REMINDER", new MessageCreator() {
					@Override
					public Message createMessage(Session session) throws JMSException {
						TextMessage objectMessage = session.createTextMessage();
						objectMessage.setText(RfxTypes.RFA.name() + reminder.getId());
						return objectMessage;
					}
				});
			} catch (Exception e) {
				LOG.error("Error sending message to queue : " + e.getMessage(), e);
			}

		}

	}

	private void checkSupplierPerformanceFormReminders() {
		// Sending Mails and Notifications for RFA meeting reminders
		List<SupplierPerformanceReminder> reminders = supplierPerformanceEvaluationDao.getSupplierReminderForNotification();
		for (SupplierPerformanceReminder reminder : reminders) {
			LOG.info("Sending Performance Form notification for Reminder : " + reminder.getForm().getFormId() + " Date and time : " + reminder.getReminderDate());
			supplierPerformanceFormDao.updateImmediately(reminder.getId());

			try {
				jmsTemplate.send("QUEUE.SPF.REMINDER", new MessageCreator() {
					@Override
					public Message createMessage(Session session) throws JMSException {
						TextMessage objectMessage = session.createTextMessage();
						objectMessage.setText(reminder.getId());
						return objectMessage;
					}
				});
			} catch (Exception e) {
				LOG.error("Error sending message to queue : " + e.getMessage(), e);
			}

		}
	}

}