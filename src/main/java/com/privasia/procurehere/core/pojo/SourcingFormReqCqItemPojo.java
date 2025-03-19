package com.privasia.procurehere.core.pojo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Pooja
 */
public class SourcingFormReqCqItemPojo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -456825342236116957L;
	
	List<SourcingFormReqCqItem> itemList = new ArrayList<SourcingFormReqCqItem>();

	private String cqId;

	/**
	 * @return the itemList
	 */
	public List<SourcingFormReqCqItem> getItemList() {
		return itemList;
	}

	/**
	 * @param itemList the itemList to set
	 */
	public void setItemList(List<SourcingFormReqCqItem> itemList) {
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
