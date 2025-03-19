package com.privasia.procurehere.core.dao;

import java.util.List;

import com.privasia.procurehere.core.entity.RftEventMessage;
import com.privasia.procurehere.core.entity.Supplier;

public interface RftEventMessageDao extends GenericDao<RftEventMessage, String> {

	/**
	 * @param eventId
	 * @param page
	 * @param size
	 * @param search
	 * @return
	 */
	List<RftEventMessage> getEventMessages(String eventId, int page, int size, String search);

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
	List<RftEventMessage> getEventMessagesForSupplier(String eventId, String supplierId, int page, int size, String search);

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

	List<RftEventMessage> getRftEventMessagesByEventId(String eventId, int page, int size, String search);

	List<Supplier> getRftEventMessagesSupplierById(String messageId);

	List<RftEventMessage> getRftEventMessagesRepliesByEventId(String eventId);

	List<RftEventMessage> getEventMessagesForSupplierByEventId(String eventId, String supplierId, int page, int size, String search);
}
