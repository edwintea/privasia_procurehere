/**
 * 
 */
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

import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.service.DashboardNotificationService;

/**
 * @author Nitin Otageri
 */
@Component
public class NotificationMonitorJob implements BaseSchedulerJob {

	private static final Logger LOG = LogManager.getLogger(NotificationMonitorJob.class);

	@Autowired
	DashboardNotificationService dashboardNotificationService;

	@Autowired
	JmsTemplate jmsTemplate;

	@Override
	@Transactional(readOnly = false)
	public void execute(JobExecutionContext ctx) {
		List<String> messages = dashboardNotificationService.getUnprocessedNotifications();

		if (CollectionUtil.isNotEmpty(messages)) {
			for (String messageId : messages) {
				LOG.info("Notification Id : " + messageId);
				dashboardNotificationService.updateNotificationAsProccessed(messageId);

				try {
//					jmsTemplate.setDefaultDestinationName("QUEUE.DASHBOARD.NOTIFICATIONS");
//					jmsTemplate.setDeliveryMode(DeliveryMode.PERSISTENT);
					jmsTemplate.send("QUEUE.DASHBOARD.NOTIFICATIONS", new MessageCreator() {
						@Override
						public Message createMessage(Session session) throws JMSException {
							TextMessage objectMessage = session.createTextMessage();
							objectMessage.setText(messageId);
							return objectMessage;
						}
					});
				} catch (Exception e) {
					LOG.error("Error sending dashboard notification message to queue : " + e.getMessage(), e);
				}
			}
		}

	}
}
