package com.privasia.procurehere.core.pojo;

import java.io.Serializable;
import java.util.List;

import com.privasia.procurehere.core.entity.RfqBqItem;

/**
 * @author parveen
 */
public class RfqBqItemResponsePojo implements Serializable {

	private static final long serialVersionUID = 8899847753512817297L;

	private List<RfqBqItem> bqItemList;

	private List<BqItemPojo> leveLOrderList;

	private long totalBqItemCount;

	/**
	 * @return the bqItemList
	 */
	public List<RfqBqItem> getBqItemList() {
		return bqItemList;
	}

	/**
	 * @param bqItemList the bqItemList to set
	 */
	public void setBqItemList(List<RfqBqItem> bqItemList) {
		this.bqItemList = bqItemList;
	}

	/**
	 * @return the leveLOrderList
	 */
	public List<BqItemPojo> getLeveLOrderList() {
		return leveLOrderList;
	}

	/**
	 * @param leveLOrderList the leveLOrderList to set
	 */
	public void setLeveLOrderList(List<BqItemPojo> leveLOrderList) {
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
