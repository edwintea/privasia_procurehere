package com.privasia.procurehere.core.pojo;

import java.io.Serializable;
import java.util.List;

import com.privasia.procurehere.core.entity.RfaBqItem;

/**
 * @author parveen
 */
public class RfaBqItemResponsePojo implements Serializable {

	private static final long serialVersionUID = -3046965212851027307L;

	private List<RfaBqItem> bqItemList;

	private List<BqItemPojo> leveLOrderList;

	private long totalBqItemCount;

	/**
	 * @return the bqItemList
	 */
	public List<RfaBqItem> getBqItemList() {
		return bqItemList;
	}

	/**
	 * @param bqItemList the bqItemList to set
	 */
	public void setBqItemList(List<RfaBqItem> bqItemList) {
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
