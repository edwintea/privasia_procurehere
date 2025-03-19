package com.privasia.procurehere.core.dao;

import java.util.List;

import com.privasia.procurehere.core.entity.RftBqItem;
import com.privasia.procurehere.core.entity.RftSupplierBqItem;

public interface RftBqItemDao extends GenericBqItemDao<RftBqItem, String> {

	/**
	 * @param bqId
	 * @param supplierId
	 * @return
	 */
	List<RftSupplierBqItem> findSupplierBqItemByListByBqIdAndSupplierId(String bqId, String supplierId);

	/**
	 * @param eventId
	 * @return
	 */
	Integer getCountOfAllRftBqItemByEventId(String eventId);

	String getBqSectionIdByBqLevelAndOrder(Integer level, Integer order, String eventId);


}
