package com.privasia.procurehere.core.pojo;

import java.io.Serializable;

import io.swagger.annotations.ApiModelProperty;

/**
 * @author ravi
 */
public class PurchaseGroupsPojo implements Serializable {

	private static final long serialVersionUID = -7094290956150607806L;

	@ApiModelProperty(notes = "Purchasing Group Code", allowableValues = "range[1, 9]", required = true)
	private String purchaseGroupCodes;

	/**
	 * @return the purchaseGroupCodes
	 */
	public String getPurchaseGroupCodes() {
		return purchaseGroupCodes;
	}

	/**
	 * @param purchaseGroupCodes the purchaseGroupCodes to set
	 */
	public void setPurchaseGroupCodes(String purchaseGroupCodes) {
		this.purchaseGroupCodes = purchaseGroupCodes;
	}

}
