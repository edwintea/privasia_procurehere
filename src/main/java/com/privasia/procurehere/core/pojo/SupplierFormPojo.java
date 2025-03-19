package com.privasia.procurehere.core.pojo;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.privasia.procurehere.core.enums.SupplierFormsStatus;
import com.privasia.procurehere.core.utils.CustomDateTimeSerializer;
import com.privasia.procurehere.core.utils.StringUtils;

public class SupplierFormPojo implements Serializable {
	private static final long serialVersionUID = 1L;
	private String id;
	private String name;
	private String description;
	private String createdBy;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	private Date createdDate;

	private String modifiedBy;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	private Date modifiedDate;

	private SupplierFormsStatus status;

	private Long pendingCount;

	private Long submittedCount;

	private Long acceptedCount;

	public SupplierFormPojo(String id, String name, String description, String createdBy, Date createdDate, String modifiedBy, Date modifiedDate, SupplierFormsStatus status, Long pendingCount, Long submittedCount, Long acceptedCount) {
		super();
		this.id = id;
		this.name = StringUtils.checkString(name);
		this.description = StringUtils.checkString(description);
		this.createdBy = createdBy;
		this.createdDate = createdDate;
		this.modifiedBy = modifiedBy;
		this.modifiedDate = modifiedDate;
		this.status = status;
		this.pendingCount = pendingCount != null ? pendingCount : 0;
		this.submittedCount = submittedCount != null ? submittedCount : 0;
		this.acceptedCount = acceptedCount != null ? acceptedCount : 0;
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return the createdBy
	 */
	public String getCreatedBy() {
		return createdBy;
	}

	/**
	 * @param createdBy the createdBy to set
	 */
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	/**
	 * @return the createdDate
	 */
	public Date getCreatedDate() {
		return createdDate;
	}

	/**
	 * @param createdDate the createdDate to set
	 */
	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	/**
	 * @return the modifiedBy
	 */
	public String getModifiedBy() {
		return modifiedBy;
	}

	/**
	 * @param modifiedBy the modifiedBy to set
	 */
	public void setModifiedBy(String modifiedBy) {
		this.modifiedBy = modifiedBy;
	}

	/**
	 * @return the modifiedDate
	 */
	public Date getModifiedDate() {
		return modifiedDate;
	}

	/**
	 * @param modifiedDate the modifiedDate to set
	 */
	public void setModifiedDate(Date modifiedDate) {
		this.modifiedDate = modifiedDate;
	}

	/**
	 * @return the status
	 */
	public SupplierFormsStatus getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(SupplierFormsStatus status) {
		this.status = status;
	}

	/**
	 * @return the pendingCount
	 */
	public Long getPendingCount() {
		return pendingCount;
	}

	/**
	 * @param pendingCount the pendingCount to set
	 */
	public void setPendingCount(Long pendingCount) {
		this.pendingCount = pendingCount;
	}

	/**
	 * @return the submittedCount
	 */
	public Long getSubmittedCount() {
		return submittedCount;
	}

	/**
	 * @param submittedCount the submittedCount to set
	 */
	public void setSubmittedCount(Long submittedCount) {
		this.submittedCount = submittedCount;
	}

	/**
	 * @return the acceptedCount
	 */
	public Long getAcceptedCount() {
		return acceptedCount;
	}

	/**
	 * @param acceptedCount the acceptedCount to set
	 */
	public void setAcceptedCount(Long acceptedCount) {
		this.acceptedCount = acceptedCount;
	}

}