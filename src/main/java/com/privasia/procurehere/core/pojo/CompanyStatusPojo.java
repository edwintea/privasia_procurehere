package com.privasia.procurehere.core.pojo;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.privasia.procurehere.core.entity.CompanyStatus;

public class CompanyStatusPojo implements Serializable {

	private static final long serialVersionUID = 2382651076922149439L;

	public String id;

	public String companystatus;

	private Boolean active = Boolean.TRUE;

	private String createdBy;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm a")
	private Date createdDate;

	private String modifiedBy;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm a")
	private Date modifiedDate;

	public CompanyStatusPojo(CompanyStatus companyStatus) {
		
		this.active=companyStatus.getActive();
		this.companystatus=companyStatus.getCompanystatus();
		this.createdBy=companyStatus.getCreatedBy()!= null ? companyStatus.getCreatedBy().getLoginId() : null;
		this.createdDate=companyStatus.getCreatedDate();
		this.modifiedBy=companyStatus.getModifiedBy()!= null ? companyStatus.getModifiedBy().getLoginId() : null;
		this.modifiedDate=companyStatus.getModifiedDate();
		this.id=companyStatus.getId();
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
	 * @return the companystatus
	 */
	public String getCompanystatus() {
		return companystatus;
	}

	/**
	 * @param companystatus the companystatus to set
	 */
	public void setCompanystatus(String companystatus) {
		this.companystatus = companystatus;
	}

	/**
	 * @return the active
	 */
	public Boolean getActive() {
		return active;
	}

	/**
	 * @param active the active to set
	 */
	public void setActive(Boolean active) {
		this.active = active;
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

}
