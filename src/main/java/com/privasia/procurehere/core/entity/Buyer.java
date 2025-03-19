/**
 * 
 */
package com.privasia.procurehere.core.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.privasia.procurehere.core.pojo.Coverage;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.hibernate.validator.constraints.Email;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.privasia.procurehere.core.enums.BuyerStatus;
import com.privasia.procurehere.core.utils.CustomDateTimeSerializer;

/**
 * @author Nitin Otageri
 */
@Entity
@Table(name = "PROC_BUYER")
public class Buyer implements Serializable {
	public interface BuyerBasicProfile {
	}

	public interface BuyerIntermediateProfile {
	}

	public interface BuyerCompleteProfile {
	}

	private static final long serialVersionUID = 8129929205793174406L;

	@Id
	@GenericGenerator(name = "idGen", strategy = "uuid.hex")
	@GeneratedValue(generator = "idGen")
	@Column(name = "BUYER_ID", length = 64)
	private String id;

	@NotNull(message = "{common.companyName.empty}", groups = { BuyerBasicProfile.class, BuyerIntermediateProfile.class, BuyerCompleteProfile.class })
	@Size(min = 1, max = 128, message = "{common.companyName.length}", groups = { BuyerBasicProfile.class, BuyerIntermediateProfile.class, BuyerCompleteProfile.class })
	@Column(name = "COMPANY_NAME", length = 128, nullable = false)
	private String companyName;

	@NotNull(message = "{common.fullName.empty}", groups = { BuyerBasicProfile.class, BuyerIntermediateProfile.class, BuyerCompleteProfile.class })
	@Size(min = 4, max = 128, message = "{common.fullName.length}", groups = { BuyerBasicProfile.class, BuyerIntermediateProfile.class, BuyerCompleteProfile.class })
	@Column(name = "FULL_NAME", length = 128, nullable = false)
	private String fullName;

	@NotNull(message = "{common.password.empty}", groups = { BuyerIntermediateProfile.class, BuyerIntermediateProfile.class, BuyerCompleteProfile.class })
	@Size(min = 8, max = 64, message = "{common.password.length}", groups = { BuyerIntermediateProfile.class, BuyerIntermediateProfile.class, BuyerCompleteProfile.class })
	@Column(name = "PASSWORD", length = 64)
	private String password;

	@NotNull(message = "{common.email.empty}", groups = { BuyerIntermediateProfile.class, BuyerCompleteProfile.class })
	@Email(message = "{common.email.valid}", groups = { BuyerIntermediateProfile.class, BuyerCompleteProfile.class })
	@Size(min = 6, max = 128, message = "{common.communicationEmail.length}", groups = { BuyerIntermediateProfile.class, BuyerCompleteProfile.class })
	@Column(name = "COMMUNICATION_EMAIL", length = 128)
	private String communicationEmail;

	@NotNull(message = "{common.loginId.empty}", groups = { BuyerBasicProfile.class, BuyerIntermediateProfile.class, BuyerCompleteProfile.class })
	@Email(message = "{common.loginId.valid}", groups = { BuyerBasicProfile.class, BuyerIntermediateProfile.class, BuyerCompleteProfile.class })
	@Size(min = 6, max = 128, message = "{common.loginId.length}", groups = { BuyerBasicProfile.class, BuyerIntermediateProfile.class, BuyerCompleteProfile.class })
	@Column(name = "LOGIN_EMAIL", length = 128)
	private String loginEmail;

	@NotNull(message = "{common.companyContactNumber.empty}", groups = { BuyerBasicProfile.class, BuyerIntermediateProfile.class, BuyerCompleteProfile.class })
	@Size(min = 6, max = 16, message = "{common.companyContactNumber.length}", groups = { BuyerBasicProfile.class, BuyerIntermediateProfile.class, BuyerCompleteProfile.class })
	@Column(name = "COMPANY_CONTACT_NUMBER", length = 16)
	private String companyContactNumber;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "BUYER_PLAN_ID", nullable = true, foreignKey = @ForeignKey(name = "FK_BUYER_PLAN_ID"))
	private BuyerPlan plan;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "BUYER_SUBSCRIPTION_ID", nullable = true, foreignKey = @ForeignKey(name = "FK_BUYER_SUBS_ID"))
	private BuyerSubscription currentSubscription;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, cascade = { CascadeType.ALL })
	@JoinColumn(name = "BUYER_PACKAGE_ID", nullable = true, foreignKey = @ForeignKey(name = "FK_BUY_PACK_ID"))
	private BuyerPackage buyerPackage;

	@JsonIgnore
	@NotNull(message = "{common.registrationOfCountry.empty}", groups = { BuyerIntermediateProfile.class, BuyerCompleteProfile.class })
	@ManyToOne(fetch = FetchType.LAZY, optional = true)
	@JoinColumn(name = "REGISTRATION_COUNTRY", nullable = true, foreignKey = @ForeignKey(name = "FK_BUY_REGISTERED_COUNTRY"))
	private Country registrationOfCountry;

	@NotNull(message = "{common.companyRegistrationNumber.empty}", groups = { BuyerIntermediateProfile.class, BuyerCompleteProfile.class })
	@Size(min = 1, max = 128, message = "{buyer.companyRegistrationNumber.length}", groups = { BuyerIntermediateProfile.class, BuyerCompleteProfile.class })
	@Column(name = "COMPANY_REGISTRATION_NUMBER", length = 128, nullable = true)
	private String companyRegistrationNumber;

	@Enumerated(EnumType.STRING)
	@Column(name = "BUYER_STATUS")
	private BuyerStatus status;

	// @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm a")
	@JsonSerialize(using = CustomDateTimeSerializer.class)
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "SUBSCRIPTION_DATE")
	private Date subscriptionDate;

	// @NotNull(message = "{buyer.numberofusers.empty}", groups = { BuyerBasicProfile.class,
	// BuyerIntermediateProfile.class })
	@Digits(integer = 3, fraction = 0, message = "{buyer.numberofusers.length}", groups = { BuyerBasicProfile.class, BuyerIntermediateProfile.class })
	@Column(name = "NO_OF_USERS")
	private Integer noOfUsers;

	// @NotNull(message = "{buyer.numberofevents.empty}", groups = { BuyerBasicProfile.class,
	// BuyerIntermediateProfile.class })
	@Digits(integer = 9, fraction = 0, message = "{buyer.numberofevents.length}", groups = { BuyerBasicProfile.class, BuyerIntermediateProfile.class })
	@Column(name = "NO_OF_EVENTS")
	private Integer noOfEvents;

	// @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm a")
	@JsonSerialize(using = CustomDateTimeSerializer.class)
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "SUBSCRIPTION_FROM")
	private Date subscriptionFrom;

	// @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm a")
	@JsonSerialize(using = CustomDateTimeSerializer.class)
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "SUBSCRIPTION_TO")
	private Date subscriptionTo;

	@Size(min = 6, max = 16, message = "{buyer.mobileNumber.length}", groups = { BuyerIntermediateProfile.class, BuyerCompleteProfile.class })
	@Column(name = "MOBILE_NUMBER", length = 16)
	private String mobileNumber;

	@Digits(integer = 4, fraction = 0, message = "{buyer.year.estab.length}")
	@Column(name = "YEAR_OF_ESTABLISHED", length = 4)
	private Integer yearOfEstablished;

	@Size(min = 0, max = 16, message = "{buyer.fax.length}")
	@Column(name = "FAX_NUMBER", length = 16)
	private String faxNumber;

	@Size(max = 128, message = "{buyer.website.length}")
	@Column(name = "COMPANY_WEBSITE", length = 128)
	private String companyWebsite;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "COMPANY_STATUS", foreignKey = @ForeignKey(name = "FK_BUY_COMP_STATUS"))
	private CompanyStatus companyStatus;

	@Size(min = 1, max = 250, message = "{buyer.addressone.length}", groups = { BuyerCompleteProfile.class })
	@Column(name = "ADRESS_LINE1", length = 250)
	private String line1;

	@Size(min = 1, max = 250, message = "{buyer.addresstwo.length}", groups = { BuyerCompleteProfile.class })
	@Column(name = "ADRESS_LINE2", length = 250)
	private String line2;

	@Size(min = 1, max = 250, message = "{buyer.city.length}", groups = { BuyerCompleteProfile.class })
	@Column(name = "CITY", length = 250)
	private String city;

	@Size(min = 1, max = 15, message = "{buyer.postcode.length}", groups = { BuyerCompleteProfile.class })
	@Column(name = "POSTCODE", length = 15)
	private String postcode;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, optional = true)
	@JoinColumn(name = "STATE_ID", foreignKey = @ForeignKey(name = "FK_BUY_STATE"))
	private State state;

	@JsonIgnore
	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "buyer")
	private List<Notes> notes;

	@Column(name = "IS_REGISTRATION_COMPLETE")
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean registrationComplete = Boolean.FALSE;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "REGISTRATION_COMPLETE_DATE")
	private Date registrationCompleteDate;

	@Column(name = "TERMS_OF_USE_ACCEPTED")
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean termsOfUseAccepted = Boolean.FALSE;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "TERMS_OF_USE_ACCEPTED_DATE")
	private Date termsOfUseAcceptedDate;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "ACTION_DATE")
	private Date actionDate;

	@JsonIgnore
	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "buyer")
	private List<FavouriteSupplier> favouriteSuppliers;

	@JsonIgnore
	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "buyer")
	private List<BuyerSubscription> subscriptionHistory;

	@Column(name = "SUSPENDED_REMARKS", length = 250)
	private String suspendedRemarks;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ACTION_BY", nullable = true, foreignKey = @ForeignKey(name = "FK_BUYER_ACTION_BY"))
	private User actionBy;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CREATED_DATE")
	private Date createdDate;

	@Column(name = "ALLOW_SUPPLIER_UPLOAD")
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean allowSupplierUpload = Boolean.FALSE;

	@Column(name = "ERP_ENABLE")
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean erpEnable = Boolean.FALSE;

	@Column(name = "ENABLE_MAIL_BOX", nullable = true)
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean enableMailbox = Boolean.FALSE;

	@Column(name = "MAIL_BOX_EMAIL", nullable = true)
	private String mailBoxEmail;

	@Column(name = "PUBLIC_CONTEXT_PATH", length = 100, nullable = true)
	private String publicContextPath;

	@Column(name = "ENABLE_EVENT_PUBLISHING", nullable = true)
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean enableEventPublishing = Boolean.FALSE;

	@Column(name = "ENABLE_EVENT_USER_CONTROL", nullable = true)
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean enableEventUserControle = Boolean.FALSE;

	@Column(name = "ENABLE_SMS", nullable = true)
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean enableSms = Boolean.FALSE;

	@Column(name = "ENABLE_FAX", nullable = true)
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean enableFax = Boolean.FALSE;

	@Column(name = "IS_PUBLISHED_PROFILE", nullable = true)
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean publishedProfile = Boolean.FALSE;

	@Email(message = "{common.email.valid}", groups = { BuyerCompleteProfile.class })
	@Size(max = 128, message = "{common.communicationEmail.length}", groups = { BuyerCompleteProfile.class })
	@Column(name = "PUB_PROF_COMMUNICATION_EMAIL", length = 128, nullable = true)
	private String publishedProfileCommunicationEmail;

	@Size(max = 20, message = "{common.publishprofile.companyContactNumber.length}", groups = { BuyerCompleteProfile.class })
	@Column(name = "PUB_PROF_CONTACT_NUMBER", length = 20, nullable = true)
	private String publishedProfileContactNumber;

	@Size(max = 128, message = "{common.publishprofile.companyContactPerson.length}", groups = { BuyerCompleteProfile.class })
	@Column(name = "PUB_PROF_CONTACT_PERSON", length = 128, nullable = true)
	private String publishedProfileContactPerson;

	@Size(max = 64, message = "{buyer.publishprofile.website.length}")
	@Column(name = "PUB_PROF_WEBSITE", length = 64, nullable = true)
	private String publishedProfileWebsite;

	@Size(max = 500, message = "{buyer.publishprofile.info.length}")
	@Column(name = "PUB_PROF_INFO_TO_SUPPLIERS", length = 500, nullable = true)
	private String publishedProfileInfoToSuppliers;

	@Column(name = "PUB_PROF_IS_ALLOW_CATEGORIES", nullable = true)
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean publishedProfileIsAllowIndustryCat = Boolean.FALSE;

	@Column(name = "PUB_PROF_MINIMUM_CATEGORIES", nullable = true)
	private Integer publishedProfileMinimumCategories;

	@Column(name = "PUB_PROF_MAXIMUM_CATEGORIES", nullable = true)
	private Integer publishedProfileMaximumCategories;

	@Column(name = "IS_ENA_PREQULI_FORM", nullable = true)
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean isEnablePrequalificationForm = Boolean.FALSE;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, optional = true, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "SUPPLIER_FORM_ID", nullable = true, foreignKey = @ForeignKey(name = "FK_BUYER_SUPPLIER_FORM"))
	private SupplierForm supplierForm;

	@Transient
	private String subscriptionDateRange;

	@Transient
	private String registrationCountryName;

	@Transient
	private byte[] fileAttatchment;

	@Transient
	private SupplierTags supplierTagName;

	@Transient
	private Coverage coverage;

	public Buyer() {
		this.allowSupplierUpload = Boolean.FALSE;
		this.enableEventPublishing = Boolean.FALSE;
		this.enableEventUserControle = Boolean.FALSE;
		this.enableSms = Boolean.FALSE;
		this.enableFax = Boolean.FALSE;
	}

	// use for get buyer
	public Buyer(String tenantId) {
		this.id = tenantId;
	}

	public Buyer(String companyName, String fullName, String communicationEmail, String companyContactNumber, String mobileNumber, String faxNumber, Boolean allowSupplierUpload) {
		this.allowSupplierUpload = allowSupplierUpload;
		this.companyName = companyName;
		this.fullName = fullName;
		this.communicationEmail = communicationEmail;
		this.companyContactNumber = companyContactNumber;
		this.mobileNumber = mobileNumber;
		this.faxNumber = faxNumber;
	}

	public Buyer createShallowCopy() {
		Buyer buyer = new Buyer();
		buyer.setId(getId());
		buyer.setFullName(getFullName());
		buyer.setCompanyName(getCompanyName());
		buyer.setCommunicationEmail(getCommunicationEmail());
		buyer.setCompanyContactNumber(getCompanyContactNumber());
		return buyer;
	}

	public Buyer(String companyName, Date registrationCompleteDate, String registrationCountryName, String companyRegistrationNumber) {
		this.companyName = companyName;
		this.registrationCompleteDate = registrationCompleteDate;
		this.registrationCountryName = registrationCountryName;
		this.companyRegistrationNumber = companyRegistrationNumber;
	}

	public Buyer(String id, String companyName, String registrationCountryName, BuyerStatus status, String companyRegistrationNumber, Date registrationCompleteDate, String fullName, String companyContactNumber, String line1, String line2, String city) {
		this.id = id;
		this.companyName = companyName;
		this.registrationCountryName = registrationCountryName;
		this.status = status;
		this.companyRegistrationNumber = companyRegistrationNumber;
		this.registrationCompleteDate = registrationCompleteDate;
		this.fullName = fullName;
		this.companyContactNumber = companyContactNumber;
		this.line1 = line1;
		this.line2 = line2;
		this.city = city;
	}
	
	public Buyer(String id, String companyName, String fullName) {
		this.id = id;
		this.companyName = companyName;
		this.fullName = fullName;
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
	 * @return the fullName
	 */
	public String getFullName() {
		return fullName;
	}

	/**
	 * @param fullName the fullName to set
	 */
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	/**
	 * @return the communicationEmail
	 */
	public String getCommunicationEmail() {
		return communicationEmail;
	}

	/**
	 * @param communicationEmail the communicationEmail to set
	 */
	public void setCommunicationEmail(String communicationEmail) {
		this.communicationEmail = communicationEmail;
	}

	/**
	 * @return the companyContactNumber
	 */
	public String getCompanyContactNumber() {
		return companyContactNumber;
	}

	/**
	 * @param companyContactNumber the companyContactNumber to set
	 */
	public void setCompanyContactNumber(String companyContactNumber) {
		this.companyContactNumber = companyContactNumber;
	}

	/**
	 * @return the loginEmail
	 */
	public String getLoginEmail() {
		return loginEmail;
	}

	/**
	 * @param loginEmail the loginEmail to set
	 */
	public void setLoginEmail(String loginEmail) {
		this.loginEmail = loginEmail;
	}

	/**
	 * @return the plan
	 */
	public BuyerPlan getPlan() {
		return plan;
	}

	/**
	 * @param plan the plan to set
	 */
	public void setPlan(BuyerPlan plan) {
		this.plan = plan;
	}

	/**
	 * @return the status
	 */
	public BuyerStatus getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(BuyerStatus status) {
		this.status = status;
	}

	/**
	 * @return the registrationOfCountry
	 */
	public Country getRegistrationOfCountry() {
		return registrationOfCountry;
	}

	/**
	 * @param registrationOfCountry the registrationOfCountry to set
	 */
	public void setRegistrationOfCountry(Country registrationOfCountry) {
		this.registrationOfCountry = registrationOfCountry;
	}

	/**
	 * @return the companyRegistrationNumber
	 */
	public String getCompanyRegistrationNumber() {
		return companyRegistrationNumber;
	}

	/**
	 * @param companyRegistrationNumber the companyRegistrationNumber to set
	 */
	public void setCompanyRegistrationNumber(String companyRegistrationNumber) {
		this.companyRegistrationNumber = companyRegistrationNumber;
	}

	/**
	 * @return the subscriptionDate
	 */
	public Date getSubscriptionDate() {
		return subscriptionDate;
	}

	/**
	 * @param subscriptionDate the subscriptionDate to set
	 */
	public void setSubscriptionDate(Date subscriptionDate) {
		this.subscriptionDate = subscriptionDate;
	}

	/**
	 * @return the noOfUsers
	 */
	public Integer getNoOfUsers() {
		return noOfUsers;
	}

	/**
	 * @param noOfUsers the noOfUsers to set
	 */
	public void setNoOfUsers(Integer noOfUsers) {
		this.noOfUsers = noOfUsers;
	}

	/**
	 * @return the noOfEvents
	 */
	public Integer getNoOfEvents() {
		return noOfEvents;
	}

	/**
	 * @param noOfEvents the noOfEvents to set
	 */
	public void setNoOfEvents(Integer noOfEvents) {
		this.noOfEvents = noOfEvents;
	}

	/**
	 * @return the subscriptionFrom
	 */
	public Date getSubscriptionFrom() {
		return subscriptionFrom;
	}

	/**
	 * @param subscriptionFrom the subscriptionFrom to set
	 */
	public void setSubscriptionFrom(Date subscriptionFrom) {
		this.subscriptionFrom = subscriptionFrom;
	}

	/**
	 * @return the subscriptionTo
	 */
	public Date getSubscriptionTo() {
		return subscriptionTo;
	}

	/**
	 * @param subscriptionTo the subscriptionTo to set
	 */
	public void setSubscriptionTo(Date subscriptionTo) {
		this.subscriptionTo = subscriptionTo;
	}

	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * @param password the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * @return the mobileNumber
	 */
	public String getMobileNumber() {
		return mobileNumber;
	}

	/**
	 * @param mobileNumber the mobileNumber to set
	 */
	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}

	/**
	 * @return the yearOfEstablished
	 */
	public Integer getYearOfEstablished() {
		return yearOfEstablished;
	}

	/**
	 * @param yearOfEstablished the yearOfEstablished to set
	 */
	public void setYearOfEstablished(Integer yearOfEstablished) {
		this.yearOfEstablished = yearOfEstablished;
	}

	/**
	 * @return the faxNumber
	 */
	public String getFaxNumber() {
		return faxNumber;
	}

	/**
	 * @param faxNumber the faxNumber to set
	 */
	public void setFaxNumber(String faxNumber) {
		this.faxNumber = faxNumber;
	}

	/**
	 * @return the companyWebsite
	 */
	public String getCompanyWebsite() {
		return companyWebsite;
	}

	/**
	 * @param companyWebsite the companyWebsite to set
	 */
	public void setCompanyWebsite(String companyWebsite) {
		this.companyWebsite = companyWebsite;
	}

	/**
	 * @return the companyStatus
	 */
	public CompanyStatus getCompanyStatus() {
		return companyStatus;
	}

	/**
	 * @param companyStatus the companyStatus to set
	 */
	public void setCompanyStatus(CompanyStatus companyStatus) {
		this.companyStatus = companyStatus;
	}

	/**
	 * @return the line1
	 */
	public String getLine1() {
		return line1;
	}

	/**
	 * @param line1 the line1 to set
	 */
	public void setLine1(String line1) {
		this.line1 = line1;
	}

	/**
	 * @return the line2
	 */
	public String getLine2() {
		return line2;
	}

	/**
	 * @param line2 the line2 to set
	 */
	public void setLine2(String line2) {
		this.line2 = line2;
	}

	/**
	 * @return the city
	 */
	public String getCity() {
		return city;
	}

	/**
	 * @param city the city to set
	 */
	public void setCity(String city) {
		this.city = city;
	}

	/**
	 * @return the state
	 */
	public State getState() {
		return state;
	}

	/**
	 * @param state the state to set
	 */
	public void setState(State state) {
		this.state = state;
	}

	/**
	 * @return the notes
	 */
	public List<Notes> getNotes() {
		return notes;
	}

	/**
	 * @param notes the notes to set
	 */
	public void setNotes(List<Notes> notes) {
		this.notes = notes;
	}

	/**
	 * @return the registrationComplete
	 */
	public Boolean getRegistrationComplete() {
		return registrationComplete;
	}

	/**
	 * @param registrationComplete the registrationComplete to set
	 */
	public void setRegistrationComplete(Boolean registrationComplete) {
		this.registrationComplete = registrationComplete;
	}

	/**
	 * @return the registrationCompleteDate
	 */
	public Date getRegistrationCompleteDate() {
		return registrationCompleteDate;
	}

	/**
	 * @param registrationCompleteDate the registrationCompleteDate to set
	 */
	public void setRegistrationCompleteDate(Date registrationCompleteDate) {
		this.registrationCompleteDate = registrationCompleteDate;
	}

	/**
	 * @return the termsOfUseAccepted
	 */
	public Boolean getTermsOfUseAccepted() {
		return termsOfUseAccepted;
	}

	/**
	 * @param termsOfUseAccepted the termsOfUseAccepted to set
	 */
	public void setTermsOfUseAccepted(Boolean termsOfUseAccepted) {
		this.termsOfUseAccepted = termsOfUseAccepted;
	}

	/**
	 * @return the termsOfUseAcceptedDate
	 */
	public Date getTermsOfUseAcceptedDate() {
		return termsOfUseAcceptedDate;
	}

	/**
	 * @param termsOfUseAcceptedDate the termsOfUseAcceptedDate to set
	 */
	public void setTermsOfUseAcceptedDate(Date termsOfUseAcceptedDate) {
		this.termsOfUseAcceptedDate = termsOfUseAcceptedDate;
	}

	/**
	 * @return the favouriteSuppliers
	 */
	public List<FavouriteSupplier> getFavouriteSuppliers() {
		return favouriteSuppliers;
	}

	/**
	 * @param favouriteSuppliers the favouriteSuppliers to set
	 */
	public void setFavouriteSuppliers(List<FavouriteSupplier> favouriteSuppliers) {
		this.favouriteSuppliers = favouriteSuppliers;
	}

	/**
	 * @return the subscriptionDateRange
	 */
	public String getSubscriptionDateRange() {
		return subscriptionDateRange;
	}

	/**
	 * @param subscriptionDateRange the subscriptionDateRange to set
	 */
	public void setSubscriptionDateRange(String subscriptionDateRange) {
		this.subscriptionDateRange = subscriptionDateRange;
	}

	/**
	 * @return the actionDate
	 */
	public Date getActionDate() {
		return actionDate;
	}

	/**
	 * @param actionDate the actionDate to set
	 */
	public void setActionDate(Date actionDate) {
		this.actionDate = actionDate;
	}

	/**
	 * @return the currentSubscription
	 */
	public BuyerSubscription getCurrentSubscription() {
		return currentSubscription;
	}

	/**
	 * @param currentSubscription the currentSubscription to set
	 */
	public void setCurrentSubscription(BuyerSubscription currentSubscription) {
		this.currentSubscription = currentSubscription;
	}

	/**
	 * @return the subscriptionHistory
	 */
	public List<BuyerSubscription> getSubscriptionHistory() {
		return subscriptionHistory;
	}

	/**
	 * @param subscriptionHistory the subscriptionHistory to set
	 */
	public void setSubscriptionHistory(List<BuyerSubscription> subscriptionHistory) {
		this.subscriptionHistory = subscriptionHistory;
	}

	/**
	 * @return the registrationCountryName
	 */
	public String getRegistrationCountryName() {
		try {
			if (registrationOfCountry != null) {
				this.registrationCountryName = registrationOfCountry.getCountryName();
			}
		} catch (Exception e) {
		}
		return registrationCountryName;
	}

	/**
	 * @param registrationCountryName the registrationCountryName to set
	 */
	public void setRegistrationCountryName(String registrationCountryName) {
		this.registrationCountryName = registrationCountryName;
	}

	/**
	 * @return the buyerPackage
	 */
	public BuyerPackage getBuyerPackage() {
		return buyerPackage;
	}

	/**
	 * @param buyerPackage the buyerPackage to set
	 */
	public void setBuyerPackage(BuyerPackage buyerPackage) {
		this.buyerPackage = buyerPackage;
	}

	/**
	 * @return the suspendedRemarks
	 */
	public String getSuspendedRemarks() {
		return suspendedRemarks;
	}

	/**
	 * @param suspendedRemarks the suspendedRemarks to set
	 */
	public void setSuspendedRemarks(String suspendedRemarks) {
		this.suspendedRemarks = suspendedRemarks;
	}

	/**
	 * @return the actionBy
	 */
	public User getActionBy() {
		return actionBy;
	}

	/**
	 * @param actionBy the actionBy to set
	 */
	public void setActionBy(User actionBy) {
		this.actionBy = actionBy;
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
	 * @return the allowSupplierUpload
	 */
	public Boolean getAllowSupplierUpload() {
		return allowSupplierUpload;
	}

	/**
	 * @param allowSupplierUpload the allowSupplierUpload to set
	 */
	public void setAllowSupplierUpload(Boolean allowSupplierUpload) {
		this.allowSupplierUpload = allowSupplierUpload;
	}

	/**
	 * @return
	 */
	public byte[] getFileAttatchment() {
		return fileAttatchment;
	}

	/**
	 * @param fileAttatchment
	 */
	public void setFileAttatchment(byte[] fileAttatchment) {
		this.fileAttatchment = fileAttatchment;
	}

	/**
	 * @return the erpEnable
	 */
	public Boolean getErpEnable() {
		return erpEnable;
	}

	/**
	 * @param erpEnable the erpEnable to set
	 */
	public void setErpEnable(Boolean erpEnable) {
		this.erpEnable = erpEnable;
	}

	/**
	 * @return the postcode
	 */
	public String getPostcode() {
		return postcode;
	}

	/**
	 * @param postcode the postcode to set
	 */
	public void setPostcode(String postcode) {
		this.postcode = postcode;
	}

	/**
	 * @return the enableMailbox
	 */
	public Boolean getEnableMailbox() {
		return enableMailbox;
	}

	/**
	 * @param enableMailbox the enableMailbox to set
	 */
	public void setEnableMailbox(Boolean enableMailbox) {
		this.enableMailbox = enableMailbox;
	}

	/**
	 * @return the mailBoxEmail
	 */
	public String getMailBoxEmail() {
		return mailBoxEmail;
	}

	/**
	 * @param mailBoxEmail the mailBoxEmail to set
	 */
	public void setMailBoxEmail(String mailBoxEmail) {
		this.mailBoxEmail = mailBoxEmail;
	}

	/**
	 * @return the publicContextPath
	 */
	public String getPublicContextPath() {
		return publicContextPath;
	}

	/**
	 * @param publicContextPath the publicContextPath to set
	 */
	public void setPublicContextPath(String publicContextPath) {
		this.publicContextPath = publicContextPath;
	}

	/**
	 * @return the enableEventPublishing
	 */
	public Boolean getEnableEventPublishing() {
		return enableEventPublishing;
	}

	/**
	 * @param enableEventPublishing the enableEventPublishing to set
	 */
	public void setEnableEventPublishing(Boolean enableEventPublishing) {
		this.enableEventPublishing = enableEventPublishing;
	}

	/**
	 * @return the supplierTagName
	 */
	public SupplierTags getSupplierTagName() {
		return supplierTagName;
	}

	/**
	 * @param supplierTagName the supplierTagName to set
	 */
	public void setSupplierTagName(SupplierTags supplierTagName) {
		this.supplierTagName = supplierTagName;
	}

	public Coverage getCoverage() {
		return coverage;
	}

	public void setCoverage(Coverage coverage) {
		this.coverage = coverage;
	}

	public Boolean getEnableEventUserControle() {
		return enableEventUserControle;
	}

	public void setEnableEventUserControle(Boolean enableEventUserControle) {
		this.enableEventUserControle = enableEventUserControle;
	}

	/**
	 * @return the enableSms
	 */
	public Boolean getEnableSms() {
		return enableSms;
	}

	/**
	 * @param enableSms the enableSms to set
	 */
	public void setEnableSms(Boolean enableSms) {
		this.enableSms = enableSms;
	}

	/**
	 * @return the enableFax
	 */
	public Boolean getEnableFax() {
		return enableFax;
	}

	/**
	 * @param enableFax the enableFax to set
	 */
	public void setEnableFax(Boolean enableFax) {
		this.enableFax = enableFax;
	}

	/**
	 * @return the publishedProfile
	 */
	public Boolean getPublishedProfile() {
		return publishedProfile;
	}

	/**
	 * @param publishedProfile the publishedProfile to set
	 */
	public void setPublishedProfile(Boolean publishedProfile) {
		this.publishedProfile = publishedProfile;
	}

	/**
	 * @return the publishedProfileCommunicationEmail
	 */
	public String getPublishedProfileCommunicationEmail() {
		return publishedProfileCommunicationEmail;
	}

	/**
	 * @param publishedProfileCommunicationEmail the publishedProfileCommunicationEmail to set
	 */
	public void setPublishedProfileCommunicationEmail(String publishedProfileCommunicationEmail) {
		this.publishedProfileCommunicationEmail = publishedProfileCommunicationEmail;
	}

	/**
	 * @return the publishedProfileContactNumber
	 */
	public String getPublishedProfileContactNumber() {
		return publishedProfileContactNumber;
	}

	/**
	 * @param publishedProfileContactNumber the publishedProfileContactNumber to set
	 */
	public void setPublishedProfileContactNumber(String publishedProfileContactNumber) {
		this.publishedProfileContactNumber = publishedProfileContactNumber;
	}

	/**
	 * @return the publishedProfileContactPerson
	 */
	public String getPublishedProfileContactPerson() {
		return publishedProfileContactPerson;
	}

	/**
	 * @param publishedProfileContactPerson the publishedProfileContactPerson to set
	 */
	public void setPublishedProfileContactPerson(String publishedProfileContactPerson) {
		this.publishedProfileContactPerson = publishedProfileContactPerson;
	}

	/**
	 * @return the publishedProfileWebsite
	 */
	public String getPublishedProfileWebsite() {
		return publishedProfileWebsite;
	}

	/**
	 * @param publishedProfileWebsite the publishedProfileWebsite to set
	 */
	public void setPublishedProfileWebsite(String publishedProfileWebsite) {
		this.publishedProfileWebsite = publishedProfileWebsite;
	}

	/**
	 * @return the publishedProfileInfoToSuppliers
	 */
	public String getPublishedProfileInfoToSuppliers() {
		return publishedProfileInfoToSuppliers;
	}

	/**
	 * @param publishedProfileInfoToSuppliers the publishedProfileInfoToSuppliers to set
	 */
	public void setPublishedProfileInfoToSuppliers(String publishedProfileInfoToSuppliers) {
		this.publishedProfileInfoToSuppliers = publishedProfileInfoToSuppliers;
	}

	/**
	 * @return the publishedProfileIsAllowIndustryCat
	 */
	public Boolean getPublishedProfileIsAllowIndustryCat() {
		return publishedProfileIsAllowIndustryCat;
	}

	/**
	 * @param publishedProfileIsAllowIndustryCat the publishedProfileIsAllowIndustryCat to set
	 */
	public void setPublishedProfileIsAllowIndustryCat(Boolean publishedProfileIsAllowIndustryCat) {
		this.publishedProfileIsAllowIndustryCat = publishedProfileIsAllowIndustryCat;
	}

	/**
	 * @return the publishedProfileMinimumCategories
	 */
	public Integer getPublishedProfileMinimumCategories() {
		return publishedProfileMinimumCategories;
	}

	/**
	 * @param publishedProfileMinimumCategories the publishedProfileMinimumCategories to set
	 */
	public void setPublishedProfileMinimumCategories(Integer publishedProfileMinimumCategories) {
		this.publishedProfileMinimumCategories = publishedProfileMinimumCategories;
	}

	/**
	 * @return the publishedProfileMaximumCategories
	 */
	public Integer getPublishedProfileMaximumCategories() {
		return publishedProfileMaximumCategories;
	}

	/**
	 * @param publishedProfileMaximumCategories the publishedProfileMaximumCategories to set
	 */
	public void setPublishedProfileMaximumCategories(Integer publishedProfileMaximumCategories) {
		this.publishedProfileMaximumCategories = publishedProfileMaximumCategories;
	}

	/**
	 * @return the isEnablePrequalificationForm
	 */
	public Boolean getIsEnablePrequalificationForm() {
		return isEnablePrequalificationForm;
	}

	/**
	 * @param isEnablePrequalificationForm the isEnablePrequalificationForm to set
	 */
	public void setIsEnablePrequalificationForm(Boolean isEnablePrequalificationForm) {
		this.isEnablePrequalificationForm = isEnablePrequalificationForm;
	}

	/**
	 * @return the supplierForm
	 */
	public SupplierForm getSupplierForm() {
		return supplierForm;
	}

	/**
	 * @param supplierForm the supplierForm to set
	 */
	public void setSupplierForm(SupplierForm supplierForm) {
		this.supplierForm = supplierForm;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		/*
		 * LOG.info("***************1****************" + companyContactNumber.hashCode());
		 * LOG.info("***************2****************" + companyName.hashCode());
		 * LOG.info("***************3****************" + loginEmail.hashCode());
		 * LOG.info("***************4****************" + status.hashCode());
		 */
		result = prime * result + ((companyContactNumber == null) ? 0 : companyContactNumber.hashCode());
		result = prime * result + ((companyName == null) ? 0 : companyName.hashCode());
		result = prime * result + ((loginEmail == null) ? 0 : loginEmail.hashCode());
		result = prime * result + ((status == null) ? 0 : status.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Buyer other = (Buyer) obj;
		if (companyContactNumber == null) {
			if (other.companyContactNumber != null)
				return false;
		} else if (!companyContactNumber.equals(other.companyContactNumber))
			return false;
		if (companyName == null) {
			if (other.companyName != null)
				return false;
		} else if (!companyName.equals(other.companyName))
			return false;
		if (loginEmail == null) {
			if (other.loginEmail != null)
				return false;
		} else if (!loginEmail.equals(other.loginEmail))
			return false;
		if (status != other.status)
			return false;
		return true;
	}

	public String toLogString() {
		return "Buyer [id=" + id + ", companyName=" + companyName + ", fullName=" + fullName + ", communicationEmail=" + communicationEmail + ", loginEmail=" + loginEmail + ", companyContactNumber=" + companyContactNumber + ", plan=" + plan + ", registrationOfCountry=" + registrationOfCountry + ", companyRegistrationNumber=" + companyRegistrationNumber + ", status=" + status + ", subscriptionDate=" + subscriptionDate + ", noOfUsers=" + noOfUsers + ", noOfEvents=" + noOfEvents + ", subscriptionFrom=" + subscriptionFrom + ", subscriptionExpir=" + subscriptionTo + ", mobileNumber=" + mobileNumber + ", yearOfEstablished=" + yearOfEstablished + ", faxNumber=" + faxNumber + ", companyWebsite=" + companyWebsite + ", companyStatus=" + companyStatus + ", city=" + city + ", state=" + state + "]";
	}

}
