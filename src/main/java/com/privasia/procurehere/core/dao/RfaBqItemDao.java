package com.privasia.procurehere.core.dao;

import com.privasia.procurehere.core.entity.RfaBqItem;
import com.privasia.procurehere.core.entity.RfaSupplierBqItem;

public interface RfaBqItemDao extends GenericBqItemDao<RfaBqItem, String> {

	RfaSupplierBqItem getBqItemByBqIItemId(String itemId, String supplierId);

	Integer getCountOfAllRfaBqItemByEventId(String eventId);

	boolean checkIfBqItemExists(String eventId);

	String getBqSectionIdByBqLevelAndOrder(Integer level, Integer order, String eventId);

}
