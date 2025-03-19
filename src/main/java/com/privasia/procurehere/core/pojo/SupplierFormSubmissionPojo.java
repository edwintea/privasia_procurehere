package com.privasia.procurehere.core.pojo;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.privasia.procurehere.core.entity.User;
import com.privasia.procurehere.core.enums.SupplierFormSubmitionStatus;
import com.privasia.procurehere.core.utils.CustomDateTimeSerializer;

/**
 * @author sana
 */
public class SupplierFormSubmissionPojo implements Serializable {

	private static final long serialVersionUID = 4326708393116867616L;

	private String id;

	private String name;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	private Date requestedDate;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	private Date submitedDate;

	private String requestedBy;

	private String submittedBy;

	private SupplierFormSubmitionStatus status;

	private String companyName;

	private String description;

	private String supplierCompanyName;

	public SupplierFormSubmissionPojo() {

	}

	public SupplierFormSubmissionPojo(String id, String companyName, String name, Date requestedDate, String requestedBy, Date submittedDate, String submittedBy, SupplierFormSubmitionStatus status) {
		this.id = id;
		this.name = name;
		this.requestedDate = requestedDate;
		this.requestedBy = requestedBy;
		this.submitedDate = submittedDate;
		this.submittedBy = submittedBy;
		this.companyName = companyName;
		this.status = status;
	}

	public SupplierFormSubmissionPojo(String id, String name, Date requestedDate, String requestedBy, Date submittedDate, String submittedBy, String description, SupplierFormSubmitionStatus status) {
		this.id = id;
		this.name = name;
		this.requestedDate = requestedDate;
		this.requestedBy = requestedBy;
		this.submitedDate = submittedDate;
		this.submittedBy = submittedBy;
		this.description = description;
		this.status = status;
	}

	public SupplierFormSubmissionPojo(String id, String name, Date requestedDate, String requestedBy, Date submittedDate, String submittedBy, String description, String supplierCompanyName, SupplierFormSubmitionStatus status) {
		this.id = id;
		this.name = name;
		this.requestedDate = requestedDate;
		this.requestedBy = requestedBy;
		this.submitedDate = submittedDate;
		this.submittedBy = submittedBy;
		this.description = description;
		this.supplierCompanyName = supplierCompanyName;
		this.status = status;
	}

	public SupplierFormSubmissionPojo(String id, String name, String description, Date submittedDate, User submittedBy, String companyName, SupplierFormSubmitionStatus status) {
		this.id = id;
		this.name = name;
		this.description = description;
		this.submitedDate = submittedDate;
		this.submittedBy = submittedBy != null ? submittedBy.getName() : "";
		this.companyName = companyName;
		this.status = status;
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
	 * @return the requestedDate
	 */
	public Date getRequestedDate() {
		return requestedDate;
	}

	/**
	 * @param requestedDate the requestedDate to set
	 */
	public void setRequestedDate(Date requestedDate) {
		this.requestedDate = requestedDate;
	}

	/**
	 * @return the submitedDate
	 */
	public Date getSubmitedDate() {
		return submitedDate;
	}

	/**
	 * @param submitedDate the submitedDate to set
	 */
	public void setSubmitedDate(Date submitedDate) {
		this.submitedDate = submitedDate;
	}

	/**
	 * @return the requestedBy
	 */
	public String getRequestedBy() {
		return requestedBy;
	}

	/**
	 * @param requestedBy the requestedBy to set
	 */
	public void setRequestedBy(String requestedBy) {
		this.requestedBy = requestedBy;
	}

	/**
	 * @return the submittedBy
	 */
	public String getSubmittedBy() {
		return submittedBy;
	}

	/**
	 * @param submittedBy the submittedBy to set
	 */
	public void setSubmittedBy(String submittedBy) {
		this.submittedBy = submittedBy;
	}

	/**
	 * @return the status
	 */
	public SupplierFormSubmitionStatus getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(SupplierFormSubmitionStatus status) {
		this.status = status;
	}

	/**
	 * @return the companyName
	 */
	public String getCompanyName() {
		return companyName;
	}

	/**
	 * @param companyName the companyName to set
	 */
	public void setCompanyName(String companyName) {
		this.companyName = companyName;
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
	 * @return the supplierCompanyName
	 */
	public String getSupplierCompanyName() {
		return supplierCompanyName;
	}

	/**
	 * @param supplierCompanyName the supplierCompanyName to set
	 */
	public void setSupplierCompanyName(String supplierCompanyName) {
		this.supplierCompanyName = supplierCompanyName;
	}

}