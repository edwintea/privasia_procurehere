package com.privasia.procurehere.core.dao;

import java.util.List;

import com.privasia.procurehere.core.entity.RfpBqItem;
import com.privasia.procurehere.core.entity.RfpSupplierBqItem;

public interface RfpBqItemDao extends GenericBqItemDao<RfpBqItem, String> {

	/**
	 * @param bqId
	 * @param supplierId
	 * @return
	 */
	List<RfpSupplierBqItem> findSupplierBqItemByListByBqIdAndSupplierId(String bqId, String supplierId);

	/**
	 * @param eventId
	 * @return
	 */
	Integer getCountOfAllRftBqItemByEventId(String eventId);

	String getBqSectionIdByBqLevelAndOrder(Integer level, Integer order, String eventId);

}
