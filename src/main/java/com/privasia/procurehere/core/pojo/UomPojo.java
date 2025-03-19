package com.privasia.procurehere.core.pojo;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.privasia.procurehere.core.entity.Uom;
import com.privasia.procurehere.core.enums.OperationType;
import com.privasia.procurehere.core.enums.Status;

import io.swagger.annotations.ApiModelProperty;

public class UomPojo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3453442773357853852L;

	@ApiModelProperty(required = false, hidden = true)
	private String id;

	@ApiModelProperty(notes = "Uom", allowableValues = "range[1, 64]", required = true)
	private String uom;

	@ApiModelProperty(notes = "Uom Description", allowableValues = "range[1, 400]", required = true)
	private String uomDescription;

	@ApiModelProperty(notes = "Status", required = false, hidden = true)
	private Status status;

	@ApiModelProperty(required = false, hidden = true)
	private String createdBy;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm a")
	@ApiModelProperty(required = false, hidden = true)
	private Date createdDate;

	@ApiModelProperty(required = false, hidden = true)
	private String modifiedBy;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm a")
	@ApiModelProperty(required = false, hidden = true)
	private Date modifiedDate;

	@ApiModelProperty(notes = "Operation", required = true)
	private OperationType operation;

	private String eventIds;

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

	public UomPojo() {
	}

	public UomPojo(Uom uom) {

		this.id = uom.getId();
		this.uom = uom.getUom();
		this.uomDescription = uom.getUomDescription();
		this.status = uom.getStatus();
		this.createdBy = uom.getCreatedBy() != null ? uom.getCreatedBy().getLoginId() : null;
		this.createdDate = uom.getCreatedDate();
		this.modifiedBy = uom.getModifiedBy() != null ? uom.getModifiedBy().getLoginId() : null;
		this.modifiedDate = uom.getModifiedDate();
	}

	public UomPojo(String id, String uom, String uomDescription, Status status) {

		this.id = id;
		this.uom = uom;
		this.uomDescription = uomDescription;
		this.status = status;
	}

	public UomPojo(String id, String uom, String uomDescription) {
		this.id = id;
		this.uom = uom;
		this.uomDescription = uomDescription;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUom() {
		return uom;
	}

	public void setUom(String uom) {
		this.uom = uom;
	}

	public String getUomDescription() {
		return uomDescription;
	}

	public void setUomDescription(String uomDescription) {
		this.uomDescription = uomDescription;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public String getModifiedBy() {
		return modifiedBy;
	}

	public void setModifiedBy(String modifiedBy) {
		this.modifiedBy = modifiedBy;
	}

	public Date getModifiedDate() {
		return modifiedDate;
	}

	public void setModifiedDate(Date modifiedDate) {
		this.modifiedDate = modifiedDate;
	}

	public void setEventIds(String eventIds) {
		this.eventIds = eventIds;
	}

	public String getEventIds() {
		return eventIds;
	}

}
