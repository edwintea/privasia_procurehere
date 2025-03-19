package com.privasia.procurehere.core.pojo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModelProperty;

/**
 * @author ravi
 */
public class ProductContractOutBoundPojo implements Serializable {

	private static final long serialVersionUID = -7745673087736650317L;

	@ApiModelProperty(notes = "Business Unit Code")
	@JsonProperty("Unit_code")
	private String businessUnit;

	@ApiModelProperty(notes = "Agreement Type")
	@JsonProperty("Agreement_Type")
	private String agreementType;

	@ApiModelProperty(notes = "Supplier Code")
	@JsonProperty("Supplier_code")
	private String supplierCode;

	@ApiModelProperty(notes = "Group Code")
	@JsonProperty("Group_Code")
	private String groupCode;

	@ApiModelProperty(notes = "Base Currency")
	@JsonProperty("Base_Currency")
	private String baseCurrency;

	@JsonProperty("Document_Date")
	private String documentDate;

	@JsonProperty("Contract_Start_Date")
	private String contractStartDate;

	@JsonProperty("Contract_End_Date")
	private String contractEndDate;

	@ApiModelProperty(notes = "Contract Value")
	@JsonProperty("Contract_Value")
	private BigDecimal contractValue;

	@JsonProperty("Contract_ID")
	private String contractId;

	// @ApiModelProperty(notes = "Contract Reference Number")
	// @JsonProperty("Reference")
	// private String reference;

	@JsonProperty("Item")
	private List<ProductContractItemsOutBoundPojo> items;

	@JsonProperty("Item_Cond_Validity")
	private List<ProductContractItemsCondValidityOutBoundPojo> validity;

	@JsonProperty("Item_Condition")
	private List<ProductContractItemsConditionOutBoundPojo> conditions;

	/**
	 * @return the businessUnit
	 */
	public String getBusinessUnit() {
		return businessUnit;
	}

	/**
	 * @param businessUnit the businessUnit to set
	 */
	public void setBusinessUnit(String businessUnit) {
		this.businessUnit = businessUnit;
	}

	/**
	 * @return the agreementType
	 */
	public String getAgreementType() {
		return agreementType;
	}

	/**
	 * @param agreementType the agreementType to set
	 */
	public void setAgreementType(String agreementType) {
		this.agreementType = agreementType;
	}

	/**
	 * @return the supplierCode
	 */
	public String getSupplierCode() {
		return supplierCode;
	}

	/**
	 * @param supplierCode the supplierCode to set
	 */
	public void setSupplierCode(String supplierCode) {
		this.supplierCode = supplierCode;
	}

	/**
	 * @return the groupCode
	 */
	public String getGroupCode() {
		return groupCode;
	}

	/**
	 * @param groupCode the groupCode to set
	 */
	public void setGroupCode(String groupCode) {
		this.groupCode = groupCode;
	}

	/**
	 * @return the baseCurrency
	 */
	public String getBaseCurrency() {
		return baseCurrency;
	}

	/**
	 * @param baseCurrency the baseCurrency to set
	 */
	public void setBaseCurrency(String baseCurrency) {
		this.baseCurrency = baseCurrency;
	}

	public String getDocumentDate() {
		return documentDate;
	}

	public void setDocumentDate(String documentDate) {
		this.documentDate = documentDate;
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

	/**
	 * @return the contractValue
	 */
	public BigDecimal getContractValue() {
		return contractValue;
	}

	/**
	 * @param contractValue the contractValue to set
	 */
	public void setContractValue(BigDecimal contractValue) {
		this.contractValue = contractValue;
	}

	public String getContractId() {
		return contractId;
	}

	public void setContractId(String contractId) {
		this.contractId = contractId;
	}

	/**
	 * @return the items
	 */
	public List<ProductContractItemsOutBoundPojo> getItems() {
		return items;
	}

	/**
	 * @param items the items to set
	 */
	public void setItems(List<ProductContractItemsOutBoundPojo> items) {
		this.items = items;
	}

	public List<ProductContractItemsCondValidityOutBoundPojo> getValidity() {
		return validity;
	}

	public void setValidity(List<ProductContractItemsCondValidityOutBoundPojo> validity) {
		this.validity = validity;
	}

	public List<ProductContractItemsConditionOutBoundPojo> getConditions() {
		return conditions;
	}

	public void setConditions(List<ProductContractItemsConditionOutBoundPojo> conditions) {
		this.conditions = conditions;
	}

}
