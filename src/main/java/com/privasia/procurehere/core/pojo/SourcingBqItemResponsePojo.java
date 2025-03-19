package com.privasia.procurehere.core.pojo;

import java.io.Serializable;
import java.util.List;

import com.privasia.procurehere.core.entity.SourcingFormRequestBqItem;

/**
 * @author pooja
 */
public class SourcingBqItemResponsePojo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6786318306712331135L;

	private List<SourcingFormRequestBqItem> bqItemList;

	private List<SourcingBqItemPojo> leveLOrderList;

	private long totalBqItemCount;

	/**
	 * @return the bqItemList
	 */
	public List<SourcingFormRequestBqItem> getBqItemList() {
		return bqItemList;
	}

	/**
	 * @param bqItemList the bqItemList to set
	 */
	public void setBqItemList(List<SourcingFormRequestBqItem> bqItemList) {
		this.bqItemList = bqItemList;
	}

	/**
	 * @return the leveLOrderList
	 */
	public List<SourcingBqItemPojo> getLeveLOrderList() {
		return leveLOrderList;
	}

	/**
	 * @param leveLOrderList the leveLOrderList to set
	 */
	public void setLeveLOrderList(List<SourcingBqItemPojo> leveLOrderList) {
		this.leveLOrderList = leveLOrderList;
	}

	/**
	 * @return the totalBqItemCount
	 */
	public long getTotalBqItemCount() {
		return totalBqItemCount;
	}

	/**
	 * @param totalBqItemCount the totalBqItemCount to set
	 */
	public void setTotalBqItemCount(long totalBqItemCount) {
		this.totalBqItemCount = totalBqItemCount;
	}

}
