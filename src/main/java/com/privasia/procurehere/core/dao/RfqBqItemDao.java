package com.privasia.procurehere.core.dao;

import java.util.List;

import com.privasia.procurehere.core.entity.RfqBqItem;
import com.privasia.procurehere.core.entity.RfqSupplierBqItem;

public interface RfqBqItemDao extends GenericBqItemDao<RfqBqItem, String> {

	/**
	 * @param bqId
	 * @param supplierId
	 * @return
	 */
	List<RfqSupplierBqItem> findSupplierBqItemByListByBqIdAndSupplierId(String bqId, String supplierId);

	/**
	 * @param eventId
	 * @return
	 */
	Integer getCountOfAllRftBqItemByEventId(String eventId);

	String getBqSectionIdByBqLevelAndOrder(Integer level, Integer order, String eventId);

}
