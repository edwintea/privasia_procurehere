/**
 *  
 */
package com.privasia.procurehere.web.controller;

import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.privasia.procurehere.core.entity.BuyerNotificationMessage;
import com.privasia.procurehere.core.entity.NotificationMessage;
import com.privasia.procurehere.core.entity.OwnerNotificationMessage;
import com.privasia.procurehere.core.entity.SupplierNotificationMessage;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.SecurityLibrary;
import com.privasia.procurehere.service.DashboardNotificationService;

/**
 * @author Priyanka Singh
 */
public class DashboardBase {

	@Autowired
	DashboardNotificationService dashboardNotificationService;

	private static final Logger LOG = LogManager.getLogger(Global.BUYER_LOG);

	@RequestMapping(value = "/getNotifications", method = RequestMethod.GET)
	public ResponseEntity<List<NotificationMessage>> getNotifications() {

		List<NotificationMessage> notificationMessages = null;

		if (SecurityLibrary.getLoggedInUser().getBuyer() != null) {
			notificationMessages = dashboardNotificationService.getNotificationsForBuyerUser(SecurityLibrary.getLoggedInUser().getId(), 0, 10);
		}
		if (SecurityLibrary.getLoggedInUser().getSupplier() != null) {
			notificationMessages = dashboardNotificationService.getNotificationsForSupplierUser(SecurityLibrary.getLoggedInUser().getId(), 0, 10);
		}

		if (SecurityLibrary.getLoggedInUser().getOwner() != null) {
			notificationMessages = dashboardNotificationService.getNotificationsForOwnerUser(SecurityLibrary.getLoggedInUser().getId(), 0, 10);
		}
		if (SecurityLibrary.getLoggedInUser().getFinanceCompany() != null) {
			LOG.info("========================================================="+SecurityLibrary.getLoggedInUser().getFinanceCompany().getCompanyName());
			notificationMessages = dashboardNotificationService.getNotificationsForFinanceUser(SecurityLibrary.getLoggedInUser().getId(), 0, 10);
			for (NotificationMessage notificationMessage : notificationMessages) {
				LOG.info("=============="+notificationMessage.getMessage());	
			}
		}

		return new ResponseEntity<List<NotificationMessage>>(notificationMessages, HttpStatus.OK);
	}

	@RequestMapping(value = "/markRead/{messageId}", method = RequestMethod.POST)
	public ResponseEntity<Void> updateNotifications(@PathVariable("messageId") String messageId) {

		if (SecurityLibrary.getLoggedInUser().getBuyer() != null) {
			BuyerNotificationMessage message = dashboardNotificationService.getBuyerNotificationMessageByMessageId(messageId);
			message.setProcessed(true);
			message.setProcessedDate(new Date());
			LOG.info("first "+message.isProcessed());
			dashboardNotificationService.update(message);
			LOG.info("second "+message.isProcessed());
		}
		if (SecurityLibrary.getLoggedInUser().getSupplier() != null) {
			SupplierNotificationMessage message = dashboardNotificationService.getSupplierNotificationMessageByMessageId(messageId);
			message.setProcessed(true);
			message.setProcessedDate(new Date());
			dashboardNotificationService.update(message);
		}

		if (SecurityLibrary.getLoggedInUser().getOwner() != null) {
			OwnerNotificationMessage message = dashboardNotificationService.getOwnerNotificationMessageByMessageId(messageId);

			message.setProcessed(true);
			message.setProcessedDate(new Date());
			dashboardNotificationService.update(message);

		}
		return new ResponseEntity<Void>(HttpStatus.OK);
	}
	
	//TODO another button for notification mark read
//	@RequestMapping(value = "/setAllMarkRead", method = RequestMethod.GET)
//	public ResponseEntity<Void> updateAllNotifications() {
//		LOG.info("==================setAllMarkRead Called====================");
//		if (SecurityLibrary.getLoggedInUser().getBuyer() != null) {
//			dashboardNotificationService.setBuyerNotificationMessageMarkAllRead();
//		}
//		if (SecurityLibrary.getLoggedInUser().getSupplier() != null) {
//			dashboardNotificationService.setSupplierNotificationMessageMarkAllRead();
//			
//		}
//
//		if (SecurityLibrary.getLoggedInUser().getOwner() != null) {
//			dashboardNotificationService.getOwnerNotificationMessageMarkAllRead();
//
//		}
//		return new ResponseEntity<Void>(HttpStatus.OK);
//	}
	
	@RequestMapping(value = "/setAllMarkRead", method = RequestMethod.GET)
	public ResponseEntity<Void> updateAllNotifications() {
		LOG.info("==================setAllMarkRead Called====================");
		if (SecurityLibrary.getLoggedInUser().getBuyer() != null) {
			dashboardNotificationService.clearAllBuyerNotificationMessage(SecurityLibrary.getLoggedInUser().getId());
		}
		if (SecurityLibrary.getLoggedInUser().getSupplier() != null) {
			dashboardNotificationService.clearAllSupplierNotificationMessage(SecurityLibrary.getLoggedInUser().getId());
			
		}

		if (SecurityLibrary.getLoggedInUser().getOwner() != null) {
			dashboardNotificationService.clearAllOwnerNotificationMessage(SecurityLibrary.getLoggedInUser().getId());

		}
		return new ResponseEntity<Void>(HttpStatus.OK);
	}

}
