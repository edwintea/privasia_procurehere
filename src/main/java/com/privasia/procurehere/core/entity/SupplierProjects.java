package com.privasia.procurehere.core.entity;

import java.io.Serializable;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.privasia.procurehere.core.pojo.Coverage;

@Entity
@Table(name = "PROC_SUPPLIER_PROJECTS")
public class SupplierProjects implements Serializable {

	private static final long serialVersionUID = 193215998320339823L;

	@Id
	@GenericGenerator(name = "idGen", strategy = "uuid.hex")
	@GeneratedValue(generator = "idGen")
	@Column(name = "ID", length = 64)
	private String id;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "SUPPLIER_ID", nullable = false, foreignKey = @ForeignKey(name = "FK_SUP_PROJ_SUPPLIER"))
	private Supplier supplier;

	@Column(name = "PROJECT_NAME", length = 250)
	private String projectName;

	@Column(name = "CLIENT_NAME", length = 200)
	private String clientName;

	@Column(name = "YEAR", length = 5)
	private Integer year;

	@Column(name = "CONTACT_VALUE", length = 60)
	private String contactValue;

	@Column(name = "CLIENT_EMAIL", length = 160)
	private String clientEmail;

	// added by yogesh for the jasper report for supplier profile
	@Transient
	String projectIndustrie;

	@JsonIgnore
	@ManyToMany(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.REFRESH })
	@JoinTable(name = "PROC_SUP_PROJ_IND_CATEGORY", joinColumns = @JoinColumn(name = "ID"), inverseJoinColumns = @JoinColumn(name = "CATEGORY_ID"))
	@OrderBy("level")
	private List<NaicsCodes> projectIndustries;

	@JsonIgnore
	@ManyToMany(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.REFRESH })
	@JoinTable(name = "PROC_SUPPLIER_PROJ_COUNTRIES", joinColumns = @JoinColumn(name = "PROJECT_ID"), inverseJoinColumns = @JoinColumn(name = "COUNTRY_ID"))
	private List<Country> assignedCountries;

	@JsonIgnore
	@ManyToMany(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.REFRESH })
	@JoinTable(name = "PROC_SUPPLIER_PROJ_STATES", joinColumns = @JoinColumn(name = "PROJECT_ID"), inverseJoinColumns = @JoinColumn(name = "STATE_ID"))
	private List<State> assignedStates;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.REFRESH })
	@JoinColumn(name = "CURRENCY_ID", nullable = true, foreignKey = @ForeignKey(name = "FK_SUP_PROJ_CURRENCY"))
	private Currency currency;

	@Transient
	private List<Coverage> tracRecordCoverages;

	@Transient
	private String supplierId;

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
	 * @return the supplier
	 */
	public Supplier getSupplier() {
		return supplier;
	}

	/**
	 * @param supplier the supplier to set
	 */
	public void setSupplier(Supplier supplier) {
		this.supplier = supplier;
	}

	/**
	 * @return the projectName
	 */
	public String getProjectName() {
		return projectName;
	}

	/**
	 * @param projectName the projectName to set
	 */
	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	/**
	 * @return the clientName
	 */
	public String getClientName() {
		return clientName;
	}

	/**
	 * @param clientName the clientName to set
	 */
	public void setClientName(String clientName) {
		this.clientName = clientName;
	}

	/**
	 * @return the year
	 */
	public Integer getYear() {
		return year;
	}

	/**
	 * @param year the year to set
	 */
	public void setYear(Integer year) {
		this.year = year;
	}

	/**
	 * @return the contactValue
	 */
	public String getContactValue() {
		return contactValue;
	}

	/**
	 * @param contactValue the contactValue to set
	 */
	public void setContactValue(String contactValue) {
		this.contactValue = contactValue;
	}

	/**
	 * @return the clientEmail
	 */
	public String getClientEmail() {
		return clientEmail;
	}

	/**
	 * @param clientEmail the clientEmail to set
	 */
	public void setClientEmail(String clientEmail) {
		this.clientEmail = clientEmail;
	}

	/**
	 * @return the supplierId
	 */
	public String getSupplierId() {
		return supplierId;
	}

	/**
	 * @param supplierId the supplierId to set
	 */
	public void setSupplierId(String supplierId) {
		this.supplierId = supplierId;
	}

	/**
	 * @return the projectIndustries
	 */
	public List<NaicsCodes> getProjectIndustries() {
		return projectIndustries;
	}

	/**
	 * @param projectIndustries the projectIndustries to set
	 */
	public void setProjectIndustries(List<NaicsCodes> projectIndustries) {
		this.projectIndustries = projectIndustries;
	}

	/**
	 * @return the assignedCountries
	 */
	public List<Country> getAssignedCountries() {
		return assignedCountries;
	}

	/**
	 * @param assignedCountries the assignedCountries to set
	 */
	public void setAssignedCountries(List<Country> assignedCountries) {
		this.assignedCountries = assignedCountries;
	}

	/**
	 * @return the assignedStates
	 */
	public List<State> getAssignedStates() {
		return assignedStates;
	}

	/**
	 * @param assignedStates the assignedStates to set
	 */
	public void setAssignedStates(List<State> assignedStates) {
		this.assignedStates = assignedStates;
	}

	/**
	 * @return the currency
	 */
	public Currency getCurrency() {
		return currency;
	}

	/**
	 * @param currency the currency to set
	 */
	public void setCurrency(Currency currency) {
		this.currency = currency;
	}

	/**
	 * @return the tracRecordCoverages
	 */
	public List<Coverage> getTracRecordCoverages() {
		return tracRecordCoverages;
	}

	/**
	 * @param tracRecordCoverages the tracRecordCoverages to set
	 */
	public void setTracRecordCoverages(List<Coverage> tracRecordCoverages) {
		this.tracRecordCoverages = tracRecordCoverages;
	}

	public String getProjectIndustrie() {
		return projectIndustrie;
	}

	public void setProjectIndustrie(String projectIndustrie) {
		this.projectIndustrie = projectIndustrie;
	}

}
