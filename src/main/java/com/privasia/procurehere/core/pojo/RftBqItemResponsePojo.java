package com.privasia.procurehere.core.pojo;

import java.io.Serializable;
import java.util.List;

import com.privasia.procurehere.core.entity.RftBqItem;

/**
 * @author parveen
 */
public class RftBqItemResponsePojo implements Serializable {

	private static final long serialVersionUID = 5513017649184422872L;

	private List<RftBqItem> bqItemList;

	private List<BqItemPojo> leveLOrderList;

	private long totalBqItemCount;

	/**
	 * @return the bqItemList
	 */
	public List<RftBqItem> getBqItemList() {
		return bqItemList;
	}

	/**
	 * @param bqItemList the bqItemList to set
	 */
	public void setBqItemList(List<RftBqItem> bqItemList) {
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

	public long getTotalBqItemCount() {
		return totalBqItemCount;
	}

	public void setTotalBqItemCount(long totalBqItemCount) {
		this.totalBqItemCount = totalBqItemCount;
	}

}
