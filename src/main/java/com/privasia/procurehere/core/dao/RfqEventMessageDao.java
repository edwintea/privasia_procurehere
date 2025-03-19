package com.privasia.procurehere.core.dao;

import java.util.List;

import com.privasia.procurehere.core.entity.RfqEventMessage;
import com.privasia.procurehere.core.entity.Supplier;

public interface RfqEventMessageDao extends GenericDao<RfqEventMessage, String> {

	/**
	 * @param eventId
	 * @param page
	 * @param size
	 * @param search
	 * @return
	 */
	List<RfqEventMessage> getEventMessages(String eventId, int page, int size, String search);

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
	List<RfqEventMessage> getEventMessagesForSupplier(String eventId, String supplierId, int page, int size, String search);

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

	List<RfqEventMessage> getRfqEventMessagesByEventId(String eventId, int page, int size, String search);

	List<Supplier> getRfqEventMessagesSupplierById(String messageId);

	List<RfqEventMessage> getRfqEventMessagesRepliesByEventId(String eventId);

	List<RfqEventMessage> getEventMessagesForSupplierByEventId(String eventId, String supplierId, int page, int size, String search);
}
