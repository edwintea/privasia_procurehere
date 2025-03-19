package com.privasia.procurehere.core.dao;

import java.io.Serializable;
import java.util.List;

import com.privasia.procurehere.core.entity.CqItem;
import com.privasia.procurehere.core.exceptions.NotAllowedException;

/**
 * @author Nitin Otageri
 */
public interface GenericCqItemDao<T extends CqItem, PK extends Serializable> extends GenericDao<T, PK> {

	/**
	 * @param cqItem
	 * @param cqId
	 * @param parentId
	 * @return
	 */
	boolean isExists(final T cqItem, String cqId, String parentId);

	/**
	 * @param eventId
	 * @return
	 */
	@Deprecated
	List<T> getCqItemsForEventId(String eventId);

	/**
	 * @param cqId
	 * @return
	 */
	List<T> findCqItemsForCq(String cqId);

	/**
	 * @param cqId
	 * @return
	 */
	List<T> getCqItemLevelOrder(String cqId);

	/**
	 * @param cqId
	 * @return
	 */
	List<T> getCqItemsbyId(String cqId);

	/**
	 * @param id
	 * @return
	 */
	T getCqItembyCqItemId(String id);

	/**
	 * @param cqItemIds
	 * @param cqId TODO
	 * @return
	 * @throws NotAllowedException
	 */
	// String deleteCqItems(String[] cqItemIds, String cqId) throws NotAllowedException;

	/**
	 * @param cqId
	 * @param cqItem
	 * @param oldParent
	 * @param newParent
	 * @param oldOrder
	 * @param newOrder
	 * @param oldLevel
	 * @param newLevel
	 */
	void updateItemOrder(String cqId, T cqItem, String oldParent, String newParent, int oldOrder, int newOrder, int oldLevel, int newLevel);

	/**
	 * @param eventId
	 * @return
	 */
	T getCqItemByEventId(String eventId);

	/**
	 * @param cqItemIds
	 * @param cqId
	 * @throws NotAllowedException
	 */
	void deleteCqItems(String[] cqItemIds, String cqId) throws NotAllowedException;

	/**
	 * @param cqId
	 * @return
	 */
	List<T> getAllCqItemsbycqId(String cqId);

	/**
	 * @param cqId
	 * @param level
	 * @return
	 */
	T getParentbyLevelId(String cqId, Integer level);

	/**
	 * @param cqIds
	 * @return
	 */
	List<T> findCqItemsForCqIds(List<String> cqIds);

	/**
	 * @param cqItemId
	 * @param leadComment
	 * @return
	 */
	boolean updateLeadEvaluatorComment(String cqItemId, String leadComment);
	
	String getLeadEvaluatorComment(String cqItemId);

}
