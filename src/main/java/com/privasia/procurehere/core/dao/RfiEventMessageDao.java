package com.privasia.procurehere.core.dao;

import java.util.List;

import com.privasia.procurehere.core.entity.RfiEventMessage;
import com.privasia.procurehere.core.entity.Supplier;

public interface RfiEventMessageDao extends GenericDao<RfiEventMessage, String> {

	/**
	 * @param eventId
	 * @param page
	 * @param size
	 * @param search
	 * @return
	 */
	List<RfiEventMessage> getEventMessages(String eventId, int page, int size, String search);

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
	List<RfiEventMessage> getEventMessagesForSupplier(String eventId, String supplierId, int page, int size, String search);

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

	List<RfiEventMessage> getRfiEventMessagesByEventId(String eventId, int page, int size, String search);

	List<Supplier> getRfiEventMessagesSupplierById(String messageId);

	List<RfiEventMessage> getRfiEventMessagesRepliesByEventId(String eventId);

	List<RfiEventMessage> getEventMessagesForSupplierByEventId(String eventId, String supplierId, int page, int size, String search);
}
