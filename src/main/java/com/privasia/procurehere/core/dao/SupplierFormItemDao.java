package com.privasia.procurehere.core.dao;

import java.util.List;

import com.privasia.procurehere.core.entity.SupplierFormItem;
import com.privasia.procurehere.core.exceptions.NotAllowedException;

/**
 * @author pooja
 */
public interface SupplierFormItemDao extends GenericDao<SupplierFormItem, String> {
	/**
	 * @param id
	 * @return
	 */
	List<SupplierFormItem> getFormItemLevelOrder(String id);

	/**
	 * @param supplierFormItem
	 * @param formId
	 * @param parentId
	 * @return
	 */
	boolean isExists(SupplierFormItem supplierFormItem, String formId, String parentId);

	/**
	 * @param itemId
	 * @return
	 */
	SupplierFormItem getFormItembyFormItemId(String itemId);

	/**
	 * @param formId
	 * @return
	 */
	List<SupplierFormItem> getFormItemsbyFormId(String formId);

	/**
	 * @param items
	 * @param formId
	 * @throws NotAllowedException
	 */
	void deleteFormItems(String[] items, String formId) throws NotAllowedException;

	/**
	 * @param formId
	 * @return
	 */
	List<SupplierFormItem> getAllFormitemsbyFormId(String formId);

	/**
	 * @param id
	 * @param formItem
	 * @param oldParent
	 * @param newParent
	 * @param oldOrder
	 * @param newOrder
	 * @param oldLevel
	 * @param newLevel
	 */
	void updateItemOrder(String id, SupplierFormItem formItem, String oldParent, String newParent, int oldOrder, int newOrder, int oldLevel, int newLevel);

	/**
	 * @param formId
	 * @return
	 */
	List<String> getNotSectionItemAddedByFormId(String formId);

}