package com.privasia.procurehere.core.pojo;

import java.io.Serializable;
import java.util.List;

import com.privasia.procurehere.core.entity.RfpBqItem;

/**
 * @author parveen
 */
public class RfpBqItemResponsePojo implements Serializable {

	private static final long serialVersionUID = 6184868008592011624L;

	private List<RfpBqItem> bqItemList;

	private List<BqItemPojo> leveLOrderList;

	private long totalBqItemCount;

	/**
	 * @return the bqItemList
	 */
	public List<RfpBqItem> getBqItemList() {
		return bqItemList;
	}

	/**
	 * @param bqItemList the bqItemList to set
	 */
	public void setBqItemList(List<RfpBqItem> bqItemList) {
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
