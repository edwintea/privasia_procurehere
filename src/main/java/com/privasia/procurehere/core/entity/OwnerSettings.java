package com.privasia.procurehere.core.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.GenericGenerator;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "PROC_OWNER_SETTINGS")
public class OwnerSettings implements Serializable {

	private static final long serialVersionUID = 3795224719665582985L;

	@Id
	@GenericGenerator(name = "idGen", strategy = "uuid.hex")
	@GeneratedValue(generator = "idGen")
	@Column(name = "ID", length = 64)
	private String id;

	@NotNull(message = "{buyerSettings.timeZone.not.empty}")
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "BUYER_TIME_ZONE", nullable = false, foreignKey = @ForeignKey(name = "FK_OWN_SET_TIMEZONE"))
	private TimeZone timeZone;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "TENANT_ID", nullable = false, foreignKey = @ForeignKey(name = "FK_OWN_SET_OWN_ID"))
	private Owner owner;

	@Column(name = "ADMIN_EMAIL_ACCOUNT", length = 500, nullable = false)
	private String supplierSignupNotificationEmailAccount;

	@Column(name = "FILE_SIZE_LIMIT")
	private Long fileSizeLimit;

	@ElementCollection
	@CollectionTable(name = "PROC_FILE_TYPES", joinColumns = { @JoinColumn(name = "OWNER_ID") })
	@Column(name = "FILE_TYPE", length = 50, nullable = false)
	private List<String> fileTypes;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "USER_LAST_MODIFIED_BY", nullable = true, foreignKey = @ForeignKey(name = "FK_OWN_SET_MOD_BY"))
	private User modifiedBy;

	@Column(name = "MODIFIED_DATE", nullable = true)
	private Date modifiedDate;

	@Column(name = "SUPPLIER_CHARGE_START_DATE", nullable = true)
	private Date supplierChargeStartDate;

	@Column(name = "BUY_SUBS_EXP_REMINDER", length = 6, nullable = true)
	private Integer buyerSubsExpiryReminder;

	@Column(name = "SUPP_SUBS_EXP_REMINDER", length = 6, nullable = true)
	private Integer supplierSubsExpiryReminder;

	@Column(name = "BUY_SUB_NOT_ADMIN_EMAIL", length = 500)
	private String buyerSubscriptionNotificationEmail;

	public OwnerSettings() {

	}

	public OwnerSettings(String id, String supplierSignupNotificationEmailAccount, Long fileSizeLimit, Date supplierChargeStartDate) {
		this.id = id;
		this.supplierSignupNotificationEmailAccount = supplierSignupNotificationEmailAccount;
		this.fileSizeLimit = fileSizeLimit;
		this.supplierChargeStartDate = supplierChargeStartDate;
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
	 * @return the timeZone
	 */
	public TimeZone getTimeZone() {
		return timeZone;
	}

	/**
	 * @param timeZone the timeZone to set
	 */
	public void setTimeZone(TimeZone timeZone) {
		this.timeZone = timeZone;
	}

	/**
	 * @return the owner
	 */
	public Owner getOwner() {
		return owner;
	}

	/**
	 * @param owner the owner to set
	 */
	public void setOwner(Owner owner) {
		this.owner = owner;
	}

	/**
	 * @return the supplierSignupNotificationEmailAccount
	 */
	public String getSupplierSignupNotificationEmailAccount() {
		return supplierSignupNotificationEmailAccount;
	}

	/**
	 * @param supplierSignupNotificationEmailAccount the supplierSignupNotificationEmailAccount to set
	 */
	public void setSupplierSignupNotificationEmailAccount(String supplierSignupNotificationEmailAccount) {
		this.supplierSignupNotificationEmailAccount = supplierSignupNotificationEmailAccount;
	}

	/**
	 * @return the fileSizeLimit
	 */
	public Long getFileSizeLimit() {
		return fileSizeLimit;
	}

	/**
	 * @param fileSizeLimit the fileSizeLimit to set
	 */
	public void setFileSizeLimit(Long fileSizeLimit) {
		this.fileSizeLimit = fileSizeLimit;
	}

	/**
	 * @return the fileTypes
	 */
	public List<String> getFileTypes() {
		return fileTypes;
	}

	/**
	 * @param fileTypes the fileTypes to set
	 */
	public void setFileTypes(List<String> fileTypes) {
		this.fileTypes = fileTypes;
	}

	/**
	 * @return the modifiedBy
	 */
	public User getModifiedBy() {
		return modifiedBy;
	}

	/**
	 * @param modifiedBy the modifiedBy to set
	 */
	public void setModifiedBy(User modifiedBy) {
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
	 * @return the supplierChargeStartDate
	 */
	public Date getSupplierChargeStartDate() {
		return supplierChargeStartDate;
	}

	/**
	 * @param supplierChargeStartDate the supplierChargeStartDate to set
	 */
	public void setSupplierChargeStartDate(Date supplierChargeStartDate) {
		this.supplierChargeStartDate = supplierChargeStartDate;
	}

	/**
	 * @return the buyerSubsExpiryReminder
	 */
	public Integer getBuyerSubsExpiryReminder() {
		return buyerSubsExpiryReminder;
	}

	/**
	 * @param buyerSubsExpiryReminder the buyerSubsExpiryReminder to set
	 */
	public void setBuyerSubsExpiryReminder(Integer buyerSubsExpiryReminder) {
		this.buyerSubsExpiryReminder = buyerSubsExpiryReminder;
	}

	/**
	 * @return the supplierSubsExpiryReminder
	 */
	public Integer getSupplierSubsExpiryReminder() {
		return supplierSubsExpiryReminder;
	}

	/**
	 * @param supplierSubsExpiryReminder the supplierSubsExpiryReminder to set
	 */
	public void setSupplierSubsExpiryReminder(Integer supplierSubsExpiryReminder) {
		this.supplierSubsExpiryReminder = supplierSubsExpiryReminder;
	}

	/**
	 * @return the buyerSubscriptionNotificationEmail
	 */
	public String getBuyerSubscriptionNotificationEmail() {
		return buyerSubscriptionNotificationEmail;
	}

	/**
	 * @param buyerSubscriptionNotificationEmail the buyerSubscriptionNotificationEmail to set
	 */
	public void setBuyerSubscriptionNotificationEmail(String buyerSubscriptionNotificationEmail) {
		this.buyerSubscriptionNotificationEmail = buyerSubscriptionNotificationEmail;
	}

}
