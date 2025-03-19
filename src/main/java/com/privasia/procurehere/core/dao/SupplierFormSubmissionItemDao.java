package com.privasia.procurehere.core.dao;

import java.util.List;

import com.privasia.procurehere.core.entity.SupplierFormSubmissionItem;

/**
 * @author sana
 */
public interface SupplierFormSubmissionItemDao extends GenericDao<SupplierFormSubmissionItem, String> {

	/**
	 * @param formId
	 * @return
	 */
	List<SupplierFormSubmissionItem> getAllSubFormById(String formId);

	/**
	 * @param formId
	 * @param itemId
	 * @return
	 */
	SupplierFormSubmissionItem findFormSubmissionItem(String formId, String itemId);

	/**
	 * @param formSubId
	 * @param formSubItemId
	 * @return
	 */
	boolean resetAttachement(String formSubId, String formSubItemId);

}
