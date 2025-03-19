package com.privasia.procurehere.core.dao;

import java.io.Serializable;
import java.util.List;

import com.privasia.procurehere.core.entity.BqItem;
import com.privasia.procurehere.core.exceptions.NotAllowedException;
import com.privasia.procurehere.core.pojo.BqItemPojo;

/**
 * @author Nitin Otageri
 */
public interface GenericBqItemDao<T extends BqItem, PK extends Serializable> extends GenericDao<T, PK> {

	/**
	 * @param bqItem
	 * @param bqId
	 * @param parentId
	 * @return
	 */
	boolean isExists(final T bqItem, String bqId, String parentId);

	/**
	 * @param bqItemIds
	 * @param bqId TODO
	 * @param bqItem
	 * @return
	 * @throws NotAllowedException
	 */
	String deleteBqItems(String[] bqItemIds, String bqId) throws NotAllowedException;

	/**
	 * @param bqItem
	 * @param bqId
	 * @param oldParent
	 * @param newParent
	 * @param oldOrder
	 * @param newOrder
	 * @param oldLevel
	 * @param newLevel
	 */
	void updateItemOrder(T bqItem, String bqId, String oldParent, String newParent, int oldOrder, int newOrder, int oldLevel, int newLevel);

	/**
	 * @param bqId
	 * @return
	 */
	List<T> getBqItemLevelOrder(String bqId);

	/**
	 * @param bqItemId
	 * @return
	 */
	T getBqItemByBqIdAndBqItemId(String bqItemId);

	/**
	 * @param id
	 * @return
	 */
	T getBqItembyBqItemId(String id);

	/**
	 * @param bqId
	 * @param label
	 */
	void deletefieldInBqItems(String bqId, String label);

	/**
	 * @param eventId
	 * @return
	 */
	List<T> findBqItemListByEventId(String eventId);

	/**
	 * @param bqId
	 * @return
	 */
	List<T> getListBqItemsbyId(String bqId);

	/**
	 * @param bqId
	 * @param level
	 * @return
	 */
	T getParentbyLevelId(String bqId, Integer level);

	/**
	 * @param bqId
	 * @return
	 */
	List<T> getAllBqItemsbybqId(String bqId);

	/**
	 * @param bqId
	 */
	void deleteBqItemsbyBqid(String bqId);

	/**
	 * @param bqId
	 * @return
	 */
	List<T> findBqItemsForBq(String bqId);

	/**
	 * @param id
	 * @param label
	 */
	void resetFieldsForFilledBySupplier(String id, List<String> label);

	/**
	 * @param eventId
	 */
	void deleteBqItemsForEventId(String eventId);

	/**
	 * @param itemLevel
	 * @param itemOrder
	 * @param start TODO
	 * @param length TODO
	 * @param id
	 * @return
	 */
	// List<T> getEventBqForChoosenFilterValue(String id , Integer itemLevel, Integer itemOrder);

	List<T> getBqItemForSearchFilter(String bqId, Integer itemLevel, Integer itemOrder, String searchVal, Integer start, Integer length);

	/**
	 * @param bqId
	 * @param searchVal TODO
	 * @return
	 */
	List<BqItemPojo> getAllLevelOrderBqItemByBqId(String bqId, String searchVal);

	/**
	 * @param bqId
	 * @param searchVal TODO
	 * @return
	 */
	long totalBqItemCountByBqId(String bqId, String searchVal);

	/**
	 * @param eventId
	 */
	void deleteBqItemsForErpForEventId(String eventId);

	void deleteChildItems(String parentId, String bqId) throws NotAllowedException;

	void deleteRemaingBqItems(String[] bqItemIds, String bqId) throws NotAllowedException;
}
