package com.privasia.procurehere.core.pojo;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ProductContractItemsCondValidityOutBoundPojo implements Serializable {

	private static final long serialVersionUID = -7010974092342023943L;

	@JsonProperty("Item_Number")
	private String itemNumber;

	@JsonProperty("Contract_Start_Date")
	private String contractStartDate;

	@JsonProperty("Contract_End_Date")
	private String contractEndDate;

	public String getItemNumber() {
		return itemNumber;
	}

	public void setItemNumber(String itemNumber) {
		this.itemNumber = itemNumber;
	}

	public String getContractStartDate() {
		return contractStartDate;
	}

	public void setContractStartDate(String contractStartDate) {
		this.contractStartDate = contractStartDate;
	}

	public String getContractEndDate() {
		return contractEndDate;
	}

	public void setContractEndDate(String contractEndDate) {
		this.contractEndDate = contractEndDate;
	}

}
