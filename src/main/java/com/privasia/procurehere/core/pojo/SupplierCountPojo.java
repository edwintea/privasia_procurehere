package com.privasia.procurehere.core.pojo;

import java.io.Serializable;

public class SupplierCountPojo implements Serializable {

	private static final long serialVersionUID = -2577106683181342606L;

	private Integer supplierCount;

	private String industryCategoryName;

	public SupplierCountPojo() {

	}

	public SupplierCountPojo(Integer supplierCount, String industryCategoryName) {
		super();
		this.supplierCount = supplierCount;
		this.industryCategoryName = industryCategoryName;
	}

	/**
	 * @return the supplierCount
	 */
	public Integer getSupplierCount() {
		return supplierCount;
	}

	/**
	 * @param supplierCount the supplierCount to set
	 */
	public void setSupplierCount(Integer supplierCount) {
		this.supplierCount = supplierCount;
	}

	/**
	 * @return the industryCategoryName
	 */
	public String getIndustryCategoryName() {
		return industryCategoryName;
	}

	/**
	 * @param industryCategoryName the industryCategoryName to set
	 */
	public void setIndustryCategoryName(String industryCategoryName) {
		this.industryCategoryName = industryCategoryName;
	}

}
