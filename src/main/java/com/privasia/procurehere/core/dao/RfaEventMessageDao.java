package com.privasia.procurehere.core.dao;

import java.util.List;

import com.privasia.procurehere.core.entity.RfaEventMessage;
import com.privasia.procurehere.core.entity.Supplier;

public interface RfaEventMessageDao extends GenericDao<RfaEventMessage, String> {

	/**
	 * @param eventId
	 * @param page
	 * @param size
	 * @param search
	 * @return
	 */
	List<RfaEventMessage> getEventMessages(String eventId, int page, int size, String search);

	/**
	 * @param eventId
	 * @param search
	 * @return
	 */
	long getTotalFilteredEventMessageCount(String eventId, String search);

	/**
	 * @param eventId
	 * @return
	 */
	long getTotalEventMessageCount(String eventId);

	/**
	 * @param eventId
	 * @param supplierId
	 * @param page
	 * @param size
	 * @param search
	 * @return
	 */
	List<RfaEventMessage> getEventMessagesForSupplier(String eventId, String supplierId, int page, int size, String search);

	/**
	 * @param eventId
	 * @param supplierId
	 * @param search
	 * @return
	 */
	long getTotalFilteredEventMessageCountForSupplier(String eventId, String supplierId, String search);

	/**
	 * @param eventId
	 * @param supplierId
	 * @return
	 */
	long getTotalEventMessageCountForSupplier(String eventId, String supplierId);

	List<RfaEventMessage> getRfaEventMessagesByEventId(String eventId, int page, int size, String search);

	List<Supplier> getRfaEventMessagesSupplierById(String messageId);

	List<RfaEventMessage> getRfaEventMessagesRepliesByEventId(String eventId);

	List<RfaEventMessage> getEventMessagesForSupplierByEventId(String eventId, String supplierId, int page, int size, String search);
}
