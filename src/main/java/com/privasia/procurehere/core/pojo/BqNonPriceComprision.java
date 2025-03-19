package com.privasia.procurehere.core.pojo;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class BqNonPriceComprision implements Serializable {

	private static final long serialVersionUID = 6913869792979194399L;

	private List<String> buyerHeading;

	private List<List<String>> buyerFeildData;

	private List<String> supplierHeading;

	private List<String> supplierName;

	Map<String, List<String>> supplierData;

	/**
	 * @return the buyerHeading
	 */
	public List<String> getBuyerHeading() {
		return buyerHeading;
	}

	/**
	 * @param buyerHeading the buyerHeading to set
	 */
	public void setBuyerHeading(List<String> buyerHeading) {
		this.buyerHeading = buyerHeading;
	}

	/**
	 * @return the buyerFeildData
	 */
	public List<List<String>> getBuyerFeildData() {
		return buyerFeildData;
	}

	/**
	 * @param buyerFeildData the buyerFeildData to set
	 */
	public void setBuyerFeildData(List<List<String>> buyerFeildData) {
		this.buyerFeildData = buyerFeildData;
	}

	/**
	 * @return the supplierHeading
	 */
	public List<String> getSupplierHeading() {
		return supplierHeading;
	}

	/**
	 * @param supplierHeading the supplierHeading to set
	 */
	public void setSupplierHeading(List<String> supplierHeading) {
		this.supplierHeading = supplierHeading;
	}

	/**
	 * @return the supplierName
	 */
	public List<String> getSupplierName() {
		return supplierName;
	}

	/**
	 * @param supplierName the supplierName to set
	 */
	public void setSupplierName(List<String> supplierName) {
		this.supplierName = supplierName;
	}

	/**
	 * @return the supplierData
	 */
	public Map<String, List<String>> getSupplierData() {
		return supplierData;
	}

	/**
	 * @param supplierData the supplierData to set
	 */
	public void setSupplierData(Map<String, List<String>> supplierData) {
		this.supplierData = supplierData;
	}

}
