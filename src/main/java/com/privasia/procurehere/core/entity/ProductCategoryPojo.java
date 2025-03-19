package com.privasia.procurehere.core.entity;

import java.io.Serializable;

import com.privasia.procurehere.core.enums.OperationType;
import com.privasia.procurehere.core.enums.Status;

import io.swagger.annotations.ApiModelProperty;

public class ProductCategoryPojo implements Serializable {
	/**
	 * @author jayshree
	 */
	private static final long serialVersionUID = 7082227558864073592L;

	@ApiModelProperty(required = false, hidden = true)
	private String id;

	@ApiModelProperty(notes = "Category Code", allowableValues = "range[1, 15]", required = true)
	private String productCode;

	@ApiModelProperty(notes = "Category Name", allowableValues = "range[1, 128]", required = true)
	private String productName;

	@ApiModelProperty(notes = "Status", required = true)
	private Status status;

	@ApiModelProperty(required = false, hidden = true)
	private String createdBy;

	@ApiModelProperty(notes = "Operation", required = true)
	private OperationType operation;

	/**
	 * @return the operation
	 */
	public OperationType getOperation() {
		return operation;
	}

	/**
	 * @param operation the operation to set
	 */
	public void setOperation(OperationType operation) {
		this.operation = operation;
	}

	public ProductCategoryPojo() {
	}

	public ProductCategoryPojo(String productCode, String productName, Status status) {
		this.productCode = productCode;
		this.productName = productName;
		this.status = status;
	}

	public ProductCategoryPojo(String id, String productName) {
		this.id = id;
		this.productName = productName;
	}

	public ProductCategoryPojo(String id, String productName, String productCode) {
		this.id = id;
		this.productName = productName;
		this.productCode = productCode;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getProductCode() {
		return productCode;
	}

	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((createdBy == null) ? 0 : createdBy.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((productCode == null) ? 0 : productCode.hashCode());
		result = prime * result + ((productName == null) ? 0 : productName.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ProductCategoryPojo other = (ProductCategoryPojo) obj;
		if (createdBy == null) {
			if (other.createdBy != null)
				return false;
		} else if (!createdBy.equals(other.createdBy))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (productCode == null) {
			if (other.productCode != null)
				return false;
		} else if (!productCode.equals(other.productCode))
			return false;
		if (productName == null) {
			if (other.productName != null)
				return false;
		} else if (!productName.equals(other.productName))
			return false;
		return true;
	}

}
