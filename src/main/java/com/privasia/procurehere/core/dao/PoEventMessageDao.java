package com.privasia.procurehere.core.dao;

import com.privasia.procurehere.core.entity.PoEventMessage;
import com.privasia.procurehere.core.entity.Supplier;

import java.util.List;

public interface PoEventMessageDao extends GenericDao<PoEventMessage, String> {

	/**
	 * @param eventId
	 * @param page
	 * @param size
	 * @param search
	 * @return
	 */
	List<PoEventMessage> getEventMessages(String eventId, int page, int size, String search);

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
	List<PoEventMessage> getEventMessagesForSupplier(String eventId, String supplierId, int page, int size, String search);

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

	List<PoEventMessage> getPoEventMessagesByEventId(String eventId, int page, int size, String search);

	List<Supplier> getPoEventMessagesSupplierById(String messageId);

	List<PoEventMessage> getPoEventMessagesRepliesByEventId(String eventId);

	List<PoEventMessage> getEventMessagesForSupplierByEventId(String eventId, String supplierId, int page, int size, String search);
}
