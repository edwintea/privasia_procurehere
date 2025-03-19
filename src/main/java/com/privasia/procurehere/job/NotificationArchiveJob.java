/**
 * 
 */
package com.privasia.procurehere.job;

import java.util.List;

import javax.jms.DeliveryMode;
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
 * @author Ravi
 */
@Component
public class NotificationArchiveJob implements BaseSchedulerJob {

	private static final Logger LOG = LogManager.getLogger(NotificationArchiveJob.class);

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
					jmsTemplate.send("QUEUE.DASHBOARD.NOTIFICATIONS",new MessageCreator() {
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

				// try {
				// if (notificationMessage.getMessageTo() != null) {
				// if (notificationMessage.getMessageTo().getBuyer() != null) {
				// BuyerNotificationMessage message = new BuyerNotificationMessage(notificationMessage);
				// dashboardNotificationService.saveBuyerNotificationMessage(message);
				// } else if (notificationMessage.getMessageTo().getSupplier() != null) {
				// SupplierNotificationMessage message = new SupplierNotificationMessage(notificationMessage);
				// dashboardNotificationService.saveSupplierNotificationMessage(message);
				// } else if (notificationMessage.getMessageTo().getOwner() != null) {
				// OwnerNotificationMessage message = new OwnerNotificationMessage(notificationMessage);
				// dashboardNotificationService.saveOwnerNotificationMessage(message);
				// }
				// } else if (StringUtils.checkString(notificationMessage.getTenantId()).length() > 0) {
				// List<User> userList = userService.fetchAllActiveUsersForTenant(notificationMessage.getTenantId());
				// boolean buyer = false;
				// boolean owner = false;
				// boolean supplier = false;
				// for (User user : userList) {
				//
				// /*
				// * Using boolean flags to avoid too many hibernate queries being fired back to databse each
				// * time for each user. Once a type of tenant is identified, it will remain the same for
				// * other users.
				// */
				// if (!buyer && !supplier && !owner) {
				// if (user.getBuyer() != null) {
				// buyer = true;
				// } else if (user.getSupplier() != null) {
				// supplier = true;
				// } else if (user.getOwner() != null) {
				// owner = true;
				// }
				// }
				//
				// if (buyer) {
				// BuyerNotificationMessage message = new BuyerNotificationMessage(notificationMessage);
				// dashboardNotificationService.saveBuyerNotificationMessage(message);
				// } else if (supplier) {
				// SupplierNotificationMessage message = new SupplierNotificationMessage(notificationMessage);
				// dashboardNotificationService.saveSupplierNotificationMessage(message);
				// } else if (owner) {
				// OwnerNotificationMessage message = new OwnerNotificationMessage(notificationMessage);
				// dashboardNotificationService.saveOwnerNotificationMessage(message);
				// }
				// }
				// }
				//
				// notificationMessage.setProcessed(true);
				// notificationMessage.setProcessedDate(new Date());
				// dashboardNotificationService.update(notificationMessage);
				//
				// } catch (Exception e) {
				// LOG.error("Error processing Notification Message " + notificationMessage.toLogString() + " Error : "
				// + e.getMessage(), e);
				// }
			}
		}

	}
}
