package com.privasia.procurehere.core.pojo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author sana
 */
public class SupplierFormSubmissionItemPojo implements Serializable {

	private static final long serialVersionUID = 6341852308910925567L;

	List<SupplierFormSubItem> itemList = new ArrayList<SupplierFormSubItem>();

	private String formId;

	/**
	 * @return the itemList
	 */
	public List<SupplierFormSubItem> getItemList() {
		return itemList;
	}

	/**
	 * @param itemList the itemList to set
	 */
	public void setItemList(List<SupplierFormSubItem> itemList) {
		this.itemList = itemList;
	}

	/**
	 * @return the formId
	 */
	public String getFormId() {
		return formId;
	}

	/**
	 * @param formId the formId to set
	 */
	public void setFormId(String formId) {
		this.formId = formId;
	}

}
