package com.privasia.procurehere.core.dao;

import java.util.List;

import com.privasia.procurehere.core.entity.SourcingFormRequestBq;
import com.privasia.procurehere.core.entity.SourcingFormRequestBqItem;
import com.privasia.procurehere.core.pojo.SourcingBqItemPojo;

/**
 * @author Pooja
 */

public interface SourcingFormRequestBqItemDao extends GenericDao<SourcingFormRequestBqItem, String> {

	/**
	 * @param bqId
	 * @param itemLevel
	 * @param itemOrder
	 * @param searchVal
	 * @param start
	 * @param length
	 * @return
	 */
	List<SourcingFormRequestBqItem> getBqItemForSearchFilter(String bqId, Integer itemLevel, Integer itemOrder, String searchVal, Integer start, Integer length);

	/**
	 * @param bqId
	 * @param searchVal
	 * @return
	 */
	List<SourcingBqItemPojo> getAllLevelOrderBqItemByBqId(String bqId, String searchVal);

	/**
	 * @param bqId
	 * @param searchVal
	 * @return
	 */
	long getTotalBqItemCountByBqId(String bqId, String searchVal);

	/**
	 * @param sourcingBqItem
	 * @param bq
	 * @param parent
	 * @return
	 */
	boolean isExist(SourcingFormRequestBqItem sourcingBqItem, String bq, String parent);

	/**
	 * @param bqItemsIds
	 * @param id
	 * @return
	 */
	String deleteBqItems(String[] bqItemsIds, String bqId);

	/**
	 * @param bqItem
	 * @param id
	 * @param object
	 * @param object2
	 * @param oldOrder
	 * @param newOrder
	 * @param oldLevel
	 * @param newLevel
	 */
	void updateItemOrder(SourcingFormRequestBqItem bqItem, String bqId, String oldParent, String newParent, int oldOrder, int newOrder, int oldLevel, int newLevel);

	/**
	 * @param bqItemId
	 * @return
	 */
	SourcingFormRequestBqItem getBqItemByBqItemId(String bqItemId);

	/**
	 * @param id
	 * @return
	 */
	List<SourcingFormRequestBqItem> getBqItemLevelOrder(String bqId);

	List<SourcingFormRequestBqItem> getAllbqItemByBqId(String id);

	SourcingFormRequestBq getAllbqItemsByBqId(String id);

	/**
	 * @param bqId
	 */
	void deleteBqItemsbyBqid(String bqId);

	/**
	 * @param bqId
	 * @param level
	 * @return
	 */
	SourcingFormRequestBqItem getParentbyLevelId(String bqId, Integer level);

}
