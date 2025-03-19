package com.privasia.procurehere.core.pojo;

import java.io.Serializable;
import java.util.List;

/**
 * @author yogesh
 */
public class SupplierMaskingPojo implements Serializable {

	private static final long serialVersionUID = 2951082022696801685L;

	private String supplierName;
	private List<SupplierMaskingCodePojo> supplierMaskingCodes;

	/**
	 * @return the supplierName
	 */
	public String getSupplierName() {
		return supplierName;
	}

	/**
	 * @param supplierName the supplierName to set
	 */
	public void setSupplierName(String supplierName) {
		this.supplierName = supplierName;
	}

	/**
	 * @return the supplierMaskingCodes
	 */
	public List<SupplierMaskingCodePojo> getSupplierMaskingCodes() {
		return supplierMaskingCodes;
	}

	/**
	 * @param supplierMaskingCodes the supplierMaskingCodes to set
	 */
	public void setSupplierMaskingCodes(List<SupplierMaskingCodePojo> supplierMaskingCodes) {
		this.supplierMaskingCodes = supplierMaskingCodes;
	}

}
