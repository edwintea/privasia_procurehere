package com.privasia.procurehere.core.pojo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Ravi
 */
public class SupplierCqItemPojo implements Serializable {

	private static final long serialVersionUID = -3318428014472621079L;

	List<SupplierCqItem> itemList = new ArrayList<SupplierCqItem>();

	private String cqId;

	/**
	 * @return the itemList
	 */
	public List<SupplierCqItem> getItemList() {
		return itemList;
	}

	/**
	 * @param itemList the itemList to set
	 */
	public void setItemList(List<SupplierCqItem> itemList) {
		this.itemList = itemList;
	}

	/**
	 * @return the cqId
	 */
	public String getCqId() {
		return cqId;
	}

	/**
	 * @param cqId the cqId to set
	 */
	public void setCqId(String cqId) {
		this.cqId = cqId;
	}

}
