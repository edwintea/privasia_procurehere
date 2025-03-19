package com.privasia.procurehere.core.dao;

import java.util.List;

import com.privasia.procurehere.core.entity.DeliveryOrderItem;

/**
 * @author pooja
 */
public interface DeliveryOrderItemDao extends GenericDao<DeliveryOrderItem, String> {
	/**
	 * @param doId
	 * @return
	 */
	List<DeliveryOrderItem> getAllDoItemByDoId(String doId);

	/**
	 * @param ids
	 * @param delievryId TODO
	 */
	void deleteItemsByIds(List<String> ids, String delievryId);

	/**
	 * @param itemId
	 * @return
	 */
	DeliveryOrderItem getDoItemById(String itemId);

}
