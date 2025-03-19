package com.privasia.procurehere.core.pojo;

import java.io.Serializable;
import java.util.List;

/**
 * @author yogesh
 */
public class TransferAwardBqPojo implements Serializable {

	private static final long serialVersionUID = -6799774403194889140L;

	private String bqName;

	List<AwardDetailsErpPojo> bqItems;

	/**
	 * @return the bqItems
	 */
	public List<AwardDetailsErpPojo> getBqItems() {
		return bqItems;
	}

	/**
	 * @param bqItems the bqItems to set
	 */
	public void setBqItems(List<AwardDetailsErpPojo> bqItems) {
		this.bqItems = bqItems;
	}

	/**
	 * @return the bqName
	 */
	public String getBqName() {
		return bqName;
	}

	/**
	 * @param bqName the bqName to set
	 */
	public void setBqName(String bqName) {
		this.bqName = bqName;
	}

}
