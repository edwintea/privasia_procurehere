package com.privasia.procurehere.core.pojo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModelProperty;

/**
 * @author ravi
 */
@JsonInclude(Include.NON_NULL)
public class ProductContractChangeOutBoundPojo implements Serializable {

	private static final long serialVersionUID = -7745673087736650317L;

	@JsonProperty("SAP_Contract_Number")
	private String sapContractNumber;

	@JsonProperty("Contract_Start_Date")
	private String contractStartDate;

	@JsonProperty("Contract_End_Date")
	private String contractEndDate;

	@ApiModelProperty(notes = "Contract Value")
	@JsonProperty("Contract_Value")
	private BigDecimal contractValue;

	@JsonProperty("Item")
	private List<ProductContractItemsChangeOutBoundPojo> items;

	public String getSapContractNumber() {
		return sapContractNumber;
	}

	public void setSapContractNumber(String sapContractNumber) {
		this.sapContractNumber = sapContractNumber;
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

	public BigDecimal getContractValue() {
		return contractValue;
	}

	public void setContractValue(BigDecimal contractValue) {
		this.contractValue = contractValue;
	}

	public List<ProductContractItemsChangeOutBoundPojo> getItems() {
		return items;
	}

	public void setItems(List<ProductContractItemsChangeOutBoundPojo> items) {
		this.items = items;
	}

}
