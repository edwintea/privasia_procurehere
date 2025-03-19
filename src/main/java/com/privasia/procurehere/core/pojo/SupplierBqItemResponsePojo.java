package com.privasia.procurehere.core.pojo;

import java.io.Serializable;
import java.util.List;

/**
 * @author Vipul
 */
public class SupplierBqItemResponsePojo implements Serializable {

	private static final long serialVersionUID = 1370116894347524479L;

	private List<?> supplierBqItemList;

	private List<BqItemPojo> levelOrderList;
	
	private long totalBqItemCount;

	public SupplierBqItemResponsePojo() {
		super();
	}

	/**
	 * @return the supplierBqItemList
	 */
	public List<?> getSupplierBqItemList() {
		return supplierBqItemList;
	}

	/**
	 * @param supplierBqItemList the supplierBqItemList to set
	 */
	public void setSupplierBqItemList(List<?> supplierBqItemList) {
		this.supplierBqItemList = supplierBqItemList;
	}

	/**
	 * @return the levelOrderList
	 */
	public List<BqItemPojo> getLevelOrderList() {
		return levelOrderList;
	}

	/**
	 * @param levelOrderList the levelOrderList to set
	 */
	public void setLevelOrderList(List<BqItemPojo> levelOrderList) {
		this.levelOrderList = levelOrderList;
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
