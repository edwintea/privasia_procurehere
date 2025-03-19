package com.privasia.procurehere.service;

import java.util.List;

import com.privasia.procurehere.core.entity.*;

/**
 * @author Teja
 */
public interface EventMessageService {

	/**
	 * @param rftEventMessage
	 * @return
	 */
	RftEventMessage saveEventMessage(RftEventMessage rftEventMessage);

	/**
	 * @param eventId
	 * @param page
	 * @param size
	 * @param search
	 * @return
	 */
	List<RftEventMessage> getRftEventMessages(String eventId, int page, int size, String search);

	/**
	 * @param eventId
	 * @param search
	 * @return
	 */
	long getTotalFilteredRftEventMessageCount(String eventId, String search);

	/**
	 * @param eventId
	 * @return
	 */
	long getTotalRftEventMessageCount(String eventId);

	/**
	 * @param rftEventMessage
	 */
	void sendDashboardNotificationForEventMessage(RftEventMessage rftEventMessage);

	/**
	 * @param poEventMessage
	 */
	void sendNotificationForPoEventMessage(PoEventMessage poEventMessage,Boolean isBuyer);

	/**
	 * @param eventId
	 * @param id
	 * @param page
	 * @param size
	 * @param search
	 * @return
	 */
	List<RftEventMessage> getRftEventMessagesForSupplier(String eventId, String id, int page, int size, String search);

	/**
	 * @param eventId
	 * @param supplierId
	 * @param search
	 * @return
	 */
	long getTotalFilteredRftEventMessageCountForSupplier(String eventId, String supplierId, String search);

	/**
	 * @param eventId
	 * @param supplierId
	 * @return
	 */
	long getTotalRftEventMessageCountForSupplier(String eventId, String supplierId);

	/**
	 * @param eventMessage
	 * @return
	 */
	RfpEventMessage saveEventMessage(RfpEventMessage eventMessage);

	/**
	 * @param eventMessage
	 * @return
	 */
	RfiEventMessage saveEventMessage(RfiEventMessage eventMessage);

	/**
	 * @param eventMessage
	 * @return
	 */
	RfqEventMessage saveEventMessage(RfqEventMessage eventMessage);

	/**
	 * @param eventMessage
	 * @return
	 */
	RfaEventMessage saveEventMessage(RfaEventMessage eventMessage);

	/**
	 * @param eventId
	 * @param search
	 * @return
	 */
	long getTotalFilteredRfpEventMessageCount(String eventId, String search);

	/**
	 * @param eventId
	 * @return
	 */
	long getTotalRfpEventMessageCount(String eventId);

	/**
	 * @param eventId
	 * @param supplierId
	 * @param page
	 * @param size
	 * @param search
	 * @return
	 */
	List<RfpEventMessage> getRfpEventMessagesForSupplier(String eventId, String supplierId, int page, int size, String search);

	/**
	 * @param eventId
	 * @param supplierId
	 * @param search
	 * @return
	 */
	long getTotalFilteredRfpEventMessageCountForSupplier(String eventId, String supplierId, String search);

	/**
	 * @param eventId
	 * @param supplierId
	 * @return
	 */
	long getTotalRfpEventMessageCountForSupplier(String eventId, String supplierId);

	/**
	 * @param eventId
	 * @param search
	 * @return
	 */
	long getTotalFilteredRfqEventMessageCount(String eventId, String search);

	/**
	 * @param eventId
	 * @return
	 */
	long getTotalRfqEventMessageCount(String eventId);

	/**
	 * @param eventId
	 * @param supplierId
	 * @param page
	 * @param size
	 * @param search
	 * @return
	 */
	List<RfqEventMessage> getRfqEventMessagesForSupplier(String eventId, String supplierId, int page, int size, String search);

	/**
	 * @param eventId
	 * @param supplierId
	 * @param search
	 * @return
	 */
	long getTotalFilteredRfqEventMessageCountForSupplier(String eventId, String supplierId, String search);

	/**
	 * @param eventId
	 * @param supplierId
	 * @return
	 */
	long getTotalRfqEventMessageCountForSupplier(String eventId, String supplierId);

	/**
	 * @param eventId
	 * @param search
	 * @return
	 */
	long getTotalFilteredRfiEventMessageCount(String eventId, String search);

	/**
	 * @param eventId
	 * @return
	 */
	long getTotalRfiEventMessageCount(String eventId);

	/**
	 * @param eventId
	 * @param supplierId
	 * @param page
	 * @param size
	 * @param search
	 * @return
	 */
	List<RfiEventMessage> getRfiEventMessagesForSupplier(String eventId, String supplierId, int page, int size, String search);

	/**
	 * @param eventId
	 * @param supplierId
	 * @param search
	 * @return
	 */
	long getTotalFilteredRfiEventMessageCountForSupplier(String eventId, String supplierId, String search);

	/**
	 * @param eventId
	 * @param supplierId
	 * @return
	 */
	long getTotalRfiEventMessageCountForSupplier(String eventId, String supplierId);

	/**
	 * @param eventId
	 * @param search
	 * @return
	 */
	long getTotalFilteredRfaEventMessageCount(String eventId, String search);

	/**
	 * @param eventId
	 * @return
	 */
	long getTotalRfaEventMessageCount(String eventId);

	/**
	 * @param eventId
	 * @param supplierId
	 * @param page
	 * @param size
	 * @param search
	 * @return
	 */
	List<RfaEventMessage> getRfaEventMessagesForSupplier(String eventId, String supplierId, int page, int size, String search);

	/**
	 * @param eventId
	 * @param supplierId
	 * @param search
	 * @return
	 */
	long getTotalFilteredRfaEventMessageCountForSupplier(String eventId, String supplierId, String search);

	/**
	 * @param eventId
	 * @param supplierId
	 * @return
	 */
	long getTotalRfaEventMessageCountForSupplier(String eventId, String supplierId);

	/**
	 * @param eventMessage
	 */
	void sendDashboardNotificationForEventMessage(RfpEventMessage eventMessage);

	/**
	 * @param eventMessage
	 */
	void sendDashboardNotificationForEventMessage(RfiEventMessage eventMessage);

	/**
	 * @param eventMessage
	 */
	void sendDashboardNotificationForEventMessage(RfqEventMessage eventMessage);

	/**
	 * @param eventMessage
	 */
	void sendDashboardNotificationForEventMessage(RfaEventMessage eventMessage);

	/**
	 * @param eventId
	 * @param page
	 * @param size
	 * @param search
	 * @return
	 */
	List<RfaEventMessage> getRfaEventMessages(String eventId, int page, int size, String search);

	/**
	 * @param eventId
	 * @param page
	 * @param size
	 * @param search
	 * @return
	 */
	List<RfiEventMessage> getRfiEventMessages(String eventId, int page, int size, String search);

	/**
	 * @param eventId
	 * @param page
	 * @param size
	 * @param search
	 * @return
	 */
	List<RfpEventMessage> getRfpEventMessages(String eventId, int page, int size, String search);

	/**
	 * @param eventId
	 * @param page
	 * @param size
	 * @param search
	 * @return
	 */
	List<RfqEventMessage> getRfqEventMessages(String eventId, int page, int size, String search);

	/**
	 * @param messageId
	 * @return
	 */
	RftEventMessage getRftEventMessageById(String messageId);

	/**
	 * @param messageId
	 * @return
	 */
	RfqEventMessage getRfqEventMessageById(String messageId);

	/**
	 * @param messageId
	 * @return
	 */
	RfpEventMessage getRfpEventMessageById(String messageId);

	/**
	 * @param messageId
	 * @return
	 */
	RfiEventMessage getRfiEventMessageById(String messageId);

	/**
	 * @param messageId
	 * @return
	 */
	RfaEventMessage getRfaEventMessageById(String messageId);

	List<RfiEventMessage> getRfiEventMessagesByEventId(String eventId, int page, int size, String search);

	List<RftEventMessage> getRftEventMessagesByEventId(String eventId, int page, int size, String search);

	List<RfqEventMessage> getRfqEventMessagesByEventId(String eventId, int page, int size, String search);

	List<RfpEventMessage> getRfpEventMessagesByEventId(String eventId, int page, int size, String search);

	List<RfaEventMessage> getRfaEventMessagesByEventId(String eventId, int page, int size, String search);

	List<PoEventMessage> getPoEventMessagesByEventId(String eventId, int page, int size, String search);

	List<RfiEventMessage> getRfiEventMessagesForSupplierByEventId(String eventId, String supplierId, int page, int size, String search);

	List<RfaEventMessage> getRfaEventMessagesForSupplierByEventId(String eventId, String id, int page, int size, String search);

	List<RfpEventMessage> getRfpEventMessagesForSupplierByEventId(String eventId, String id, int page, int size, String search);

	List<RfqEventMessage> getRfqEventMessagesForSupplierByEventId(String eventId, String id, int page, int size, String search);

	List<RftEventMessage> getRftEventMessagesForSupplierByEventId(String eventId, String id, int page, int size, String search);

	//PH-4113
	List<PoEventMessage> getPoEventMessagesForSupplierByEventId(String eventId, String id, int page, int size, String search);

	/**
	 * @param eventId
	 * @param search
	 * @return
	 */
	long getTotalFilteredPoEventMessageCount(String eventId, String search);

	/**
	 * @param eventId
	 * @return
	 */
	long getTotalPoEventMessageCount(String eventId);

	/**
	 * @param poEventMessage
	 * @return
	 */
	PoEventMessage saveEventMessage(PoEventMessage poEventMessage);

	/**
	 * @param messageId
	 * @return
	 */
	PoEventMessage getPoEventMessageById(String messageId);

	/**
	 * @param eventId
	 * @param supplierId
	 * @return
	 */
	long getTotalPoEventMessageCountForSupplier(String eventId, String supplierId);

	/**
	 * @param eventId
	 * @param supplierId
	 * @param search
	 * @return
	 */
	long getTotalFilteredPoEventMessageCountForSupplier(String eventId, String supplierId, String search);
}
