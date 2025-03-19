package com.privasia.procurehere.core.dao;

import java.util.List;

import com.privasia.procurehere.core.entity.SourcingFormRequestCqItem;
import com.privasia.procurehere.core.entity.SourcingTemplateCqItem;
import com.privasia.procurehere.core.exceptions.NotAllowedException;

/**
 * @author sarang
 */
public interface SourcingFormCqItemDao extends GenericCqItemDao<SourcingTemplateCqItem, String> {

	/**
	 * @param name
	 * @param name2
	 * @return
	 */
	boolean isCqExists(String name, String name2);

	/**
	 * @param itemId
	 * @return
	 */
	SourcingTemplateCqItem getCqItembyItemId(String itemId);

	/**
	 * @param id
	 * @return
	 */
	List<SourcingFormRequestCqItem> getSourcingFormRequestCqItemByFormId(String id);

	/**
	 * @param cqItemIds
	 * @param cqId
	 * @throws NotAllowedException
	 */
	void deleteCqItemss(String[] cqItemIds, String cqId) throws NotAllowedException;

	/**
	 * @param item
	 * @param cqid
	 * @param parentId
	 * @return
	 */
	boolean isExistsItem(SourcingTemplateCqItem item, String cqid, String parentId);

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
	void updateRequestItemOrder(String cqId, SourcingTemplateCqItem cqItem, String oldParent, String newParent, int oldOrder, int newOrder, int oldLevel, int newLevel);

	List<SourcingTemplateCqItem> getAllSourcingCqItemsbycqId(String cqId);

}
