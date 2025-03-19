package com.privasia.procurehere.core.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.ColumnResult;
import javax.persistence.ConstructorResult;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.SqlResultSetMapping;
import javax.persistence.SqlResultSetMappings;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.hibernate.validator.constraints.Email;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.privasia.procurehere.core.enums.FavouriteSupplierStatus;
import com.privasia.procurehere.core.enums.SupplierStatus;
import com.privasia.procurehere.core.pojo.Coverage;
import com.privasia.procurehere.core.pojo.GlobalSearchPojo;
import com.privasia.procurehere.core.pojo.RecaptchaForm;
import com.privasia.procurehere.core.pojo.RequestedAssociatedBuyerPojo;
import com.privasia.procurehere.core.utils.CustomDateTimeSerializer;

@Entity
// @Table(name = "PROC_SUPPLIER", indexes = { @Index(columnList =
// "SUPPLIER_TRACK_DESC", name = "INDEX_SUPL_TRAC_DESC")
// })
@Table(name = "PROC_SUPPLIER")
@SqlResultSetMappings({ @SqlResultSetMapping(name = "globalSearchResult", classes = { @ConstructorResult(targetClass = GlobalSearchPojo.class, columns = { @ColumnResult(name = "id"), @ColumnResult(name = "eventName"), @ColumnResult(name = "eventDescription"), @ColumnResult(name = "referenceNumber"), @ColumnResult(name = "eventEndDate"), @ColumnResult(name = "eventStartDate"), @ColumnResult(name = "status"), @ColumnResult(name = "eventOwner"), @ColumnResult(name = "type"), @ColumnResult(name = "createdBy"), @ColumnResult(name = "eventId"), @ColumnResult(name = "unitName") }) }), @SqlResultSetMapping(name = "globalBuyerSearchResult", classes = { @ConstructorResult(targetClass = RequestedAssociatedBuyerPojo.class, columns = { @ColumnResult(name = "buyerId"), @ColumnResult(name = "buyerCompanyName"), @ColumnResult(name = "countryName") }) }) })
public class Supplier extends RecaptchaForm implements Serializable {

	private static final long serialVersionUID = 7665249952721850014L;

	public interface SupplierSignup {
	}

	public interface SupplierStep1 {
	}

	public interface SupplierStep2 {
	}

	public interface SupplierStep3 {
	}

	@Id
	@GenericGenerator(name = "idGen", strategy = "uuid.hex")
	@GeneratedValue(generator = "idGen")
	@Column(name = "SUPPLIER_ID", length = 64)
	private String id;

	@NotNull(message = "{common.companyName.empty}", groups = { SupplierStep1.class, SupplierSignup.class })
	@Size(min = 1, max = 128, message = "{common.companyName.length}", groups = { SupplierStep1.class, SupplierSignup.class })
	@Column(name = "COMPANY_NAME", length = 128, nullable = false)
	private String companyName;

	@NotNull(message = "{supplier.mobileNumber.empty}", groups = { SupplierStep1.class, SupplierSignup.class })
	@Size(min = 6, max = 16, message = "{supplier.mobileNumber.length}", groups = { SupplierStep1.class, SupplierSignup.class })
	@Column(name = "MOBILE_NUMBER", length = 16)
	private String mobileNumber;

	@NotNull(message = "{common.companyContactNumber.empty}", groups = { SupplierStep1.class, SupplierSignup.class })
	@Size(min = 6, max = 16, message = "{common.companyContactNumber.length}", groups = { SupplierStep1.class, SupplierSignup.class })
	@Column(name = "COMPANY_CONTACT_NUMBER", length = 16)
	private String companyContactNumber;

	@NotNull(message = "{common.registrationOfCountry.empty}", groups = { SupplierStep1.class, SupplierSignup.class })
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "REGISTRATION_COUNTRY", foreignKey = @ForeignKey(name = "FK_SUP_REGISTERED_COUNTRY"))
	private Country registrationOfCountry;

	@NotNull(message = "{common.companyRegistrationNumber.empty}", groups = { SupplierStep1.class, SupplierSignup.class })
	@Size(min = 1, max = 128, message = "{common.companyRegistrationNumber.length}", groups = { SupplierStep1.class, SupplierSignup.class })
	@Column(name = "COMPANY_REGISTRATION_NUMBER", length = 128, nullable = false)
	private String companyRegistrationNumber;

	@NotNull(message = "{common.fullName.empty}", groups = { SupplierStep1.class, SupplierSignup.class })
	@Size(min = 1, max = 128, message = "{common.fullName.length}", groups = { SupplierStep1.class, SupplierSignup.class })
	@Column(name = "FULL_NAME", length = 128, nullable = false)
	private String fullName;

	@NotNull(message = "{common.loginId.empty}", groups = { SupplierSignup.class })
	@Email(message = "{common.loginId.valid}", groups = { SupplierSignup.class })
	@Size(min = 1, max = 128, message = "{common.loginId.length}", groups = { SupplierSignup.class })
	@Column(name = "LOGIN_EMAIL", length = 128)
	private String loginEmail;

	@NotNull(message = "{supplier.communicationEmail.empty}", groups = { SupplierStep1.class, SupplierSignup.class })
	@Email(message = "{supplier.communicationEmail.valid}", groups = { SupplierStep1.class, SupplierSignup.class })
	@Size(min = 1, max = 128, message = "{supplier.communicationEmail.length}", groups = { SupplierStep1.class, SupplierSignup.class })
	@Column(name = "COMMUNICATION_EMAIL", length = 128)
	private String communicationEmail;

	@NotNull(message = "{supplier.designation.empty}", groups = { SupplierSignup.class })
	@Size(min = 1, max = 128, message = "{supplier.designation.length}", groups = { SupplierSignup.class })
	@Column(name = "DESIGNATION", length = 128)
	private String designation;

	@NotNull(message = "{common.password.empty}", groups = { SupplierSignup.class })
	@Size(min = 8, max = 64, message = "{common.password.length}", groups = { SupplierSignup.class })
	@Column(name = "PASSWORD", length = 64)
	private String password;

	@Size(min = 0, max = 2050)
	@Column(name = "REMARKS", length = 2050)
	private String remarks;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "CREATED_BY", nullable = true, foreignKey = @ForeignKey(name = "FK_SUP_CREATED_BY"))
	private User createdBy;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CREATED_DATE")
	private Date createdDate;

	@Enumerated(EnumType.STRING)
	@Column(name = "SUPPLIER_STATUS")
	private SupplierStatus status;

	@NotNull(groups = SupplierStep1.class, message = "{supplier.year.estab.empty}")
	@Digits(integer = 4, fraction = 0, groups = SupplierStep1.class, message = "{supplier.year.estab.length}")
	@Column(name = "YEAR_OF_ESTABLISHED", length = 4)
	private Integer yearOfEstablished;

	@NotNull(groups = SupplierStep1.class, message = "{supplier.fax.empty}")
	@Size(min = 0, max = 16, message = "{supplier.fax.length}", groups = SupplierStep1.class)
	@Column(name = "FAX_NUMBER", length = 16)
	private String faxNumber;

	@Size(max = 128, groups = SupplierStep1.class, message = "{supplier.website.length}")
	@Column(name = "COMPANY_WEBSITE", length = 128)
	private String companyWebsite;

	@JsonIgnore
	@NotNull(groups = SupplierStep1.class, message = "{supplier.companystatus.empty}")
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "COMPANY_STATUS", foreignKey = @ForeignKey(name = "FK_SUP_COMP_STATUS"))
	private CompanyStatus companyStatus;

	@NotNull(message = "{supplier.addressone.empty}", groups = { SupplierStep1.class })
	@Size(min = 1, max = 64, message = "{supplier.addressone.length}", groups = { SupplierStep1.class })
	@Column(name = "ADRESS_LINE1", length = 250)
	private String line1;

	@NotNull(message = "{supplier.addresstwo.empty}", groups = { SupplierStep1.class })
	@Size(min = 1, max = 64, message = "{supplier.addresstwo.length}", groups = { SupplierStep1.class })
	@Column(name = "ADRESS_LINE2", length = 250)
	private String line2;

	@NotNull(message = "{supplier.city.empty}", groups = { SupplierStep1.class })
	@Size(min = 1, max = 64, message = "{supplier.city.length}", groups = { SupplierStep1.class })
	@Column(name = "CITY", length = 250)
	private String city;

	@JsonIgnore
	@NotNull(message = "{supplier.state.empty}", groups = { SupplierStep1.class })
	@ManyToOne(fetch = FetchType.LAZY, optional = true)
	@JoinColumn(name = "STATE_ID", foreignKey = @ForeignKey(name = "FK_SUPP_STATE"))
	private State state;

	@JsonIgnore
	@NotNull(message = "{supplier.industry.empty}", groups = { SupplierStep2.class })
	@ManyToMany(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.REFRESH })
	@JoinTable(name = "PROC_SUPP_NAICS_CODE_MAPPING", joinColumns = @JoinColumn(name = "SUPPLIER_ID"), inverseJoinColumns = @JoinColumn(name = "NAICS_CODE_ID"))
	@OrderBy("CATEGORY_LEVEL, CATEGORY_CODE")
	private List<NaicsCodes> naicsCodes;

	@NotNull(groups = SupplierStep3.class, message = "{supplier.declaration.empty}")
	@Column(name = "IS_DECLARATION")
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean declaration = Boolean.FALSE;

	@JsonIgnore
	@OneToMany(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.REFRESH }, mappedBy = "supplier")
	private List<SupplierProjects> supplierProjects;

	@JsonIgnore
	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "supplier")
	private List<SupplierOtherCredentials> supplierOtherCredentials;

	@JsonIgnore
	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "supplier")
	private List<SupplierOtherDocuments> supplierOtherDocuments;

	@Column(name = "SUPPLIER_TRACK_DESC", length = 1050)
	private String supplierTrackDesc;

	@JsonIgnore
	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "supplier")
	private List<SupplierCompanyProfile> supplierCompanyProfile;

	// @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm
	// a z")
	@JsonSerialize(using = CustomDateTimeSerializer.class)
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "REGISTRATION_DATE")
	private Date registrationDate;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "ACTION_DATE")
	private Date actionDate;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ACTION_BY", nullable = true, foreignKey = @ForeignKey(name = "FK_SUP_ACTION_BY"))
	private User actionBy;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "PROFILE_UPDATED_DATE")
	private Date profileUpdatedDate;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "SUBSCRIPTION_DATE")
	private Date subscriptionDate;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "PROFILE_UPDATED_BY", nullable = true, foreignKey = @ForeignKey(name = "FK_SUP_PROFILE_UPD_BY"))
	private User profileUpdatedBy;

	@Column(name = "IS_REGISTRATION_COMPLETE")
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean registrationComplete = Boolean.FALSE;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "REGISTRATION_COMPLETE_DATE")
	private Date registrationCompleteDate;

	@JsonIgnore
	@ManyToMany(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.REFRESH })
	@JoinTable(name = "PROC_SUPPLIER_COUNTRIES", joinColumns = @JoinColumn(name = "SUPPLIER_ID"), inverseJoinColumns = @JoinColumn(name = "COUNTRY_ID"))
	private List<Country> countries;

	@JsonIgnore
	@ManyToMany(fetch = FetchType.LAZY, cascade = { CascadeType.REFRESH })
	@JoinTable(name = "PROC_SUPPLIER_STATES", joinColumns = @JoinColumn(name = "SUPPLIER_ID"), inverseJoinColumns = @JoinColumn(name = "STATE_ID"))
	private List<State> states;

	@JsonIgnore
	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "supplier")
	private List<Notes> notes;

	@Column(name = "TERMS_OF_USE_ACCEPTED")
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean termsOfUseAccepted = Boolean.FALSE;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "TERMS_OF_USE_ACCEPTED_DATE")
	private Date termsOfUseAcceptedDate;

	@Size(min = 0, max = 2050)
	@Column(name = "REJECT_REMARKS", length = 2050)
	private String rejectRemarks;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, cascade = { CascadeType.ALL })
	@JoinColumn(name = "SUPPLIER_SUBSCRIPTION_ID", nullable = true, foreignKey = @ForeignKey(name = "FK_SUP_SUBS_ID"))
	private SupplierSubscription supplierSubscription;

	@JsonIgnore
	@ManyToMany(fetch = FetchType.LAZY, cascade = { CascadeType.REFRESH })
	@JoinTable(name = "PROC_SUPPLIER_BUYER_MAPPING", joinColumns = @JoinColumn(name = "SUPPLIER_ID"), inverseJoinColumns = @JoinColumn(name = "BUYER_ID"))
	private List<Buyer> associatedBuyers;

	@JsonIgnore
	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "supplier")
	private List<SupplierSubscription> subscriptionHistory;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, cascade = { CascadeType.ALL })
	@JoinColumn(name = "SUPPLIER_PACKAGE_ID", nullable = true, foreignKey = @ForeignKey(name = "FK_SUPPLIER_PACK_ID"))
	private SupplierPackage supplierPackage;

	@Size(min = 1, max = 15, message = "{supplier.postal.code.length}", groups = { SupplierStep1.class })
	@Column(name = "POSTAL_CODE", length = 15)
	private String postalCode;

	@Size(min = 0, max = 17, message = "{supplier.tax.length}")
	@Pattern(regexp = "^[A-Za-z0-9-\\/]*$", message = "{supplier.tax.alphanumeric}")
	@Column(name = "TAX_REGISTRATION_NO", length = 17, nullable = true)
	private String taxRegistrationNumber;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "CURRENCY_CODE", nullable = true, foreignKey = @ForeignKey(name = "FK_SUP_REG_CURRENCY"))
	private Currency currency;

	@Size(min = 0, max = 32, message = "{supplier.paidupcapital.length}")
	@Column(name = "PAID_UP_CAPITAL", nullable = true)
	private String paidUpCapital;

	@JsonIgnore
	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "supplier")
	private List<SupplierFinanicalDocuments> supplierFinancialDocuments;

	@JsonIgnore
	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "supplier")
	private List<SupplierBoardOfDirectors> supplierBoardOfDirectors;

	@Size(min = 0, max = 128, message = "{common.companyName.length}", groups = { SupplierStep1.class, SupplierSignup.class })
	@Column(name = "FORMER_COMPANY_NAME", length = 128, nullable = true)
	private String formerCompanyName;

	@Size(min = 1, max = 128, message = "{common.companyRegistrationNumber.length}", groups = { SupplierStep1.class, SupplierSignup.class })
	@Column(name = "FORMER_REGISTRATION_NUMBER", length = 128, nullable = true)
	private String formerRegistrationNumber;

	@Column(name = "BUYER_ACCOUNT", nullable = false)
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean buyerAccount = Boolean.FALSE;

	@Column(name = "IS_ONBOARDING_FORM")
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean isOnboardingForm = Boolean.FALSE;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "FORM_SUBMITED_DATE")
	private Date onBoardingFromsubmitedDate;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "FORM_SUBMITED_BY", nullable = true, foreignKey = @ForeignKey(name = "FK_SUP_SFB_USER_SUB_BY"))
	private User onBoardingFormSubmittedBy;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "APPROVED_DATE")
	private Date approvedDate;
	
	@Transient
	private CommonsMultipartFile companyProfile;

	@Transient
	private CommonsMultipartFile financialDocuments;

	@Transient
	private List<Coverage> coverages;

	@Transient
	private FavouriteSupplierStatus activeInactive;

	@Transient
	IndustryCategory industryCategory;

	@Transient
	ProductCategory productCategory;;

	@Transient
	@Type(type = "org.hibernate.type.NumericBooleanType")
private boolean disqualified = false;

	@Transient
	@Type(type = "org.hibernate.type.NumericBooleanType")
private boolean inMeeting = false;

	@Transient
	private String disqualifiedRemarks;

	// this is used for the save used supplier promo code with the registration form
	@Transient
	private String promocode;

	@Transient
	private String vendorCode;

	@Transient
	private String favSupplierStatus;

	@Transient
	private BigDecimal favSupplierRatings;

	@Transient
	SupplierTags supplierTags;;

	@Transient
	private BigDecimal totalAfterTax; // this is used for winner supplier

	@Transient
	private List<SupplierTags> supplierTagsList;

	@Transient
	private List<IndustryCategory> industryCategories;

	/**
	 * @return the postalCode
	 */
	public String getPostalCode() {
		return postalCode;
	}

	/**
	 * @param postalCode the postalCode to set
	 */
	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}

	public BigDecimal getFavSupplierRatings() {
		return favSupplierRatings;
	}

	public void setFavSupplierRatings(BigDecimal favSupplierRatings) {
		this.favSupplierRatings = favSupplierRatings;
	}

	public Supplier(String id) {
		this.id = id;
	}

	// this is used for find winner supplier
	public Supplier(String id, BigDecimal totalAfterTax, Date createdDate) {
		this.id = id;
		this.totalAfterTax = totalAfterTax;
		this.createdDate = createdDate;// this is bid submitted time
	}

	public Supplier(String id, String companyName, Date registrationDate, String companyRegistrationNumber, String fullName, String companyContactNumber) {
		this.id = id;
		this.companyName = companyName;
		this.companyRegistrationNumber = companyRegistrationNumber;
		this.fullName = fullName;
		this.registrationDate = registrationDate;
		this.companyContactNumber = companyContactNumber;
	}

	/**
	 * Used during supplier search by Buyer
	 */
	@Transient
	@Type(type = "org.hibernate.type.NumericBooleanType")
private boolean favourite = false;

	public Supplier createShallowCopy() {

		Supplier sc = new Supplier();
		sc.setId(getId());
		sc.setCompanyName(getCompanyName());
		sc.setFullName(getFullName());
		sc.setCommunicationEmail(getCommunicationEmail());
		sc.setCompanyContactNumber(getCompanyContactNumber());
		sc.setMobileNumber(getMobileNumber());
		sc.setCompanyRegistrationNumber(getCompanyRegistrationNumber());
		sc.setLoginEmail(getLoginEmail());
		sc.setDesignation(getDesignation());
		sc.setYearOfEstablished(getYearOfEstablished());
		// sc.setRemarks(getRemarks());
		// sc.setCreatedDate(getCreatedDate());
		sc.setStatus(getStatus());
		sc.setFaxNumber(getFaxNumber());
		sc.setCompanyWebsite(getCompanyWebsite());
		sc.setLine1(getLine1());
		sc.setLine2(getLine2());
		sc.setCity(getCity());
		// sc.setSupplierTrackDesc(getSupplierTrackDesc());
		// sc.setRegistrationDate(getRegistrationDate());
		sc.setDeclaration(getDeclaration());
		sc.setRegistrationComplete(getRegistrationComplete());
		return sc;
	}

	public Supplier createShallowCopyWithCountry() {

		Supplier sc = new Supplier();
		sc.setId(getId());
		sc.setCompanyName(getCompanyName());
		sc.setFullName(getFullName());
		sc.setCommunicationEmail(getCommunicationEmail());
		sc.setCompanyContactNumber(getCompanyContactNumber());
		sc.setMobileNumber(getMobileNumber());
		sc.setCompanyRegistrationNumber(getCompanyRegistrationNumber());
		sc.setDesignation(getDesignation());
		sc.setYearOfEstablished(getYearOfEstablished());
		sc.setActiveInactive(getActiveInactive());
		sc.setRegistrationCompleteDate(getRegistrationCompleteDate());
		sc.setRegistrationComplete(getRegistrationComplete());
		sc.setFavSupplierStatus(getFavSupplierStatus());
		// sc.setStatus(getStatus());
		sc.setFavourite(isFavourite());
		sc.setFaxNumber(getFaxNumber());
		try {
			if (getRegistrationOfCountry() != null) {
				sc.setRegistrationOfCountry(getRegistrationOfCountry());
				sc.getRegistrationOfCountry().getCountryName();
				sc.getRegistrationOfCountry().setCreatedBy(null);
				sc.getRegistrationOfCountry().setModifiedBy(null);
			}
		} catch (Exception e) {
		}
		return sc;
	}

	public Supplier() {
		this.declaration = Boolean.FALSE;
		this.registrationComplete = Boolean.FALSE;
		this.buyerAccount = Boolean.FALSE;
		this.isOnboardingForm = Boolean.FALSE;
	}

	/**
	 * @param id
	 * @param companyName
	 */
	public Supplier(String id, String companyName) {
		super();
		this.id = id;
		this.companyName = companyName;
	}

	public Supplier(String id, String companyName, String companyContactNumber, String loginEmail, String fullName) {
		this.id = id;
		this.companyName = companyName;
		this.companyContactNumber = companyContactNumber;
		this.loginEmail = loginEmail;
		this.fullName = fullName;
	}

	public Supplier(String id, String companyName, String companyContactNumber, String companyRegistrationNumber, String loginEmail, SupplierStatus status, String mobileNumber) {
		this.id = id;
		this.companyName = companyName;
		this.companyContactNumber = companyContactNumber;
		this.companyRegistrationNumber = companyRegistrationNumber;
		this.loginEmail = loginEmail;
		this.status = status;
		this.mobileNumber = mobileNumber;
	}

	/**
	 * @return the formerCompanyName
	 */
	public String getFormerCompanyName() {
		return formerCompanyName;
	}

	/**
	 * @param formerCompanyName the formerCompanyName to set
	 */
	public void setFormerCompanyName(String formerCompanyName) {
		this.formerCompanyName = formerCompanyName;
	}

	/**
	 * @return the formerRegistrationNumber
	 */
	public String getFormerRegistrationNumber() {
		return formerRegistrationNumber;
	}

	/**
	 * @param formerRegistrationNumber the formerRegistrationNumber to set
	 */
	public void setFormerRegistrationNumber(String formerRegistrationNumber) {
		this.formerRegistrationNumber = formerRegistrationNumber;
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
	 * @return the supplierOtherCredentials
	 */
	public List<SupplierOtherCredentials> getSupplierOtherCredentials() {
		return supplierOtherCredentials;
	}

	/**
	 * @param supplierOtherCredentials the supplierOtherCredentials to set
	 */
	public void setSupplierOtherCredentials(List<SupplierOtherCredentials> supplierOtherCredentials) {
		this.supplierOtherCredentials = supplierOtherCredentials;
	}

	/**
	 * @return the registrationOfCountry
	 */
	public Country getRegistrationOfCountry() {
		try {
			if (registrationOfCountry != null)
				registrationOfCountry.getCountryName();
			return registrationOfCountry;
		} catch (Exception e) {
		}
		return null;
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
	 * @return the designation
	 */
	public String getDesignation() {
		return designation;
	}

	/**
	 * @param designation the designation to set
	 */
	public void setDesignation(String designation) {
		this.designation = designation;
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
	 * @return the remarks
	 */
	public String getRemarks() {
		return remarks;
	}

	/**
	 * @param remarks the remarks to set
	 */
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	/**
	 * @return the createdBy
	 */
	public User getCreatedBy() {
		return createdBy;
	}

	/**
	 * @param createdBy the createdBy to set
	 */
	public void setCreatedBy(User createdBy) {
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
	 * @return the status
	 */
	public SupplierStatus getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(SupplierStatus status) {
		this.status = status;
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
	 * @return the declaration
	 */
	public Boolean getDeclaration() {
		return declaration;
	}

	/**
	 * @param declaration the declaration to set
	 */
	public void setDeclaration(Boolean declaration) {
		this.declaration = declaration;
	}

	/**
	 * @return the supplierTrackDesc
	 */
	public String getSupplierTrackDesc() {
		return supplierTrackDesc;
	}

	/**
	 * @param supplierTrackDesc the supplierTrackDesc to set
	 */
	public void setSupplierTrackDesc(String supplierTrackDesc) {
		this.supplierTrackDesc = supplierTrackDesc;
	}

	/**
	 * @return the supplierProjects
	 */
	public List<SupplierProjects> getSupplierProjects() {
		return supplierProjects;
	}

	/**
	 * @param supplierProjects the supplierProjects to set
	 */
	public void setSupplierProjects(List<SupplierProjects> supplierProjects) {

		if (supplierProjects != null) {
			if (this.supplierProjects == null) {
				this.supplierProjects = new ArrayList<SupplierProjects>();
			}
			this.getSupplierProjects().clear();
			this.getSupplierProjects().addAll(supplierProjects);
		}
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
	 * @return the profileUpdatedDate
	 */
	public Date getProfileUpdatedDate() {
		return profileUpdatedDate;
	}

	/**
	 * @param profileUpdatedDate the profileUpdatedDate to set
	 */
	public void setProfileUpdatedDate(Date profileUpdatedDate) {
		this.profileUpdatedDate = profileUpdatedDate;
	}

	/**
	 * @return the profileUpdatedBy
	 */
	public User getProfileUpdatedBy() {
		return profileUpdatedBy;
	}

	/**
	 * @param profileUpdatedBy the profileUpdatedBy to set
	 */
	public void setProfileUpdatedBy(User profileUpdatedBy) {
		this.profileUpdatedBy = profileUpdatedBy;
	}

	/**
	 * @return the registrationDate
	 */
	public Date getRegistrationDate() {
		return registrationDate;
	}

	/**
	 * @param registrationDate the registrationDate to set
	 */
	public void setRegistrationDate(Date registrationDate) {
		this.registrationDate = registrationDate;
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
	 * @return the companyProfile
	 */
	public CommonsMultipartFile getCompanyProfile() {
		return companyProfile;
	}

	/**
	 * @param companyProfile the companyProfile to set
	 */
	public void setCompanyProfile(CommonsMultipartFile companyProfile) {
		this.companyProfile = companyProfile;
	}

	/**
	 * @return the naicsCodes
	 */
	public List<NaicsCodes> getNaicsCodes() {
		return naicsCodes;
	}

	/**
	 * @param naicsCodes the naicsCodes to set
	 */
	public void setNaicsCodes(List<NaicsCodes> naicsCodes) {
		this.naicsCodes = naicsCodes;
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
	 * @return the countries
	 */
	public List<Country> getCountries() {
		return countries;
	}

	/**
	 * @param countries the countries to set
	 */
	public void setCountries(List<Country> countries) {
		this.countries = countries;
	}

	/**
	 * @return the states
	 */
	public List<State> getStates() {
		return states;
	}

	/**
	 * @param states the states to set
	 */
	public void setStates(List<State> states) {
		this.states = states;
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
	 * @return the rejectRemarks
	 */
	public String getRejectRemarks() {
		return rejectRemarks;
	}

	/**
	 * @param rejectRemarks the rejectRemarks to set
	 */
	public void setRejectRemarks(String rejectRemarks) {
		this.rejectRemarks = rejectRemarks;
	}

	/**
	 * @return the coverages
	 */
	public List<Coverage> getCoverages() {
		return coverages;
	}

	/**
	 * @param coverages the coverages to set
	 */
	public void setCoverages(List<Coverage> coverages) {
		this.coverages = coverages;
	}

	/**
	 * @return the supplierCompanyProfile
	 */
	public List<SupplierCompanyProfile> getSupplierCompanyProfile() {
		return supplierCompanyProfile;
	}

	/**
	 * @param supplierCompanyProfile the supplierCompanyProfile to set
	 */
	public void setSupplierCompanyProfile(List<SupplierCompanyProfile> supplierCompanyProfile) {
		this.supplierCompanyProfile = supplierCompanyProfile;
	}

	/**
	 * @return the favourite
	 */
	public boolean isFavourite() {
		return favourite;
	}

	/**
	 * @param favourite the favourite to set
	 */
	public void setFavourite(boolean favourite) {
		this.favourite = favourite;
	}

	/**
	 * @return the activeInactive
	 */
	public FavouriteSupplierStatus getActiveInactive() {
		return activeInactive;
	}

	/**
	 * @param activeInactive the activeInactive to set
	 */
	public void setActiveInactive(FavouriteSupplierStatus activeInactive) {
		this.activeInactive = activeInactive;
	}

	/**
	 * @return the disqualified
	 */
	public boolean isDisqualified() {
		return disqualified;
	}

	/**
	 * @param disqualified the disqualified to set
	 */
	public void setDisqualified(boolean disqualified) {
		this.disqualified = disqualified;
	}

	/**
	 * @return the disqualifiedRemarks
	 */
	public String getDisqualifiedRemarks() {
		return disqualifiedRemarks;
	}

	/**
	 * @param disqualifiedRemarks the disqualifiedRemarks to set
	 */
	public void setDisqualifiedRemarks(String disqualifiedRemarks) {
		this.disqualifiedRemarks = disqualifiedRemarks;
	}

	/**
	 * @return the supplierSubscription
	 */
	public SupplierSubscription getSupplierSubscription() {
		return supplierSubscription;
	}

	/**
	 * @param supplierSubscription the supplierSubscription to set
	 */
	public void setSupplierSubscription(SupplierSubscription supplierSubscription) {
		this.supplierSubscription = supplierSubscription;
	}

	/**
	 * @return the associatedBuyers
	 */
	public List<Buyer> getAssociatedBuyers() {
		return associatedBuyers;
	}

	/**
	 * @param associatedBuyers the associatedBuyers to set
	 */
	public void setAssociatedBuyers(List<Buyer> associatedBuyers) {
		this.associatedBuyers = associatedBuyers;
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
	 * productCategory
	 * 
	 * @return
	 */
	public ProductCategory getProductCategory() {
		return productCategory;
	}

	/**
	 * @param productCategory the productCategory to set
	 */
	public void setProductCategory(ProductCategory productCategory) {
		this.productCategory = productCategory;
	}

	/**
	 * @return the industryCategory
	 */
	public IndustryCategory getIndustryCategory() {
		return industryCategory;
	}

	/**
	 * @param industryCategory the industryCategory to set
	 */
	public void setIndustryCategory(IndustryCategory industryCategory) {
		this.industryCategory = industryCategory;
	}

	/**
	 * @return the supplierOtherDocuments
	 */
	public List<SupplierOtherDocuments> getSupplierOtherDocuments() {
		return supplierOtherDocuments;
	}

	/**
	 * @param supplierOtherDocuments the supplierOtherDocuments to set
	 */
	public void setSupplierOtherDocuments(List<SupplierOtherDocuments> supplierOtherDocuments) {
		this.supplierOtherDocuments = supplierOtherDocuments;
	}

	/**
	 * @return the subscriptionHistory
	 */
	public List<SupplierSubscription> getSubscriptionHistory() {
		return subscriptionHistory;
	}

	/**
	 * @param subscriptionHistory the subscriptionHistory to set
	 */
	public void setSubscriptionHistory(List<SupplierSubscription> subscriptionHistory) {
		this.subscriptionHistory = subscriptionHistory;
	}

	/**
	 * @return the supplierPackage
	 */
	public SupplierPackage getSupplierPackage() {
		return supplierPackage;
	}

	/**
	 * @param supplierPackage the supplierPackage to set
	 */
	public void setSupplierPackage(SupplierPackage supplierPackage) {
		this.supplierPackage = supplierPackage;
	}

	/**
	 * @return the promocode
	 */
	public String getPromocode() {
		return promocode;
	}

	/**
	 * @param promocode the promocode to set
	 */
	public void setPromocode(String promocode) {
		this.promocode = promocode;
	}

	/**
	 * @return the vendorCode
	 */
	public String getVendorCode() {
		return vendorCode;
	}

	/**
	 * @param vendorCode the vendorCode to set
	 */
	public void setVendorCode(String vendorCode) {
		this.vendorCode = vendorCode;
	}

	public String getFavSupplierStatus() {
		return favSupplierStatus;
	}

	public void setFavSupplierStatus(String favSupplierStatus) {
		this.favSupplierStatus = favSupplierStatus;
	}

	/**
	 * @return the supplierTags
	 */
	public SupplierTags getSupplierTags() {
		return supplierTags;
	}

	/**
	 * @param supplierTags the supplierTags to set
	 */
	public void setSupplierTags(SupplierTags supplierTags) {
		this.supplierTags = supplierTags;
	}

	public BigDecimal getTotalAfterTax() {
		return totalAfterTax;
	}

	public void setTotalAfterTax(BigDecimal totalAfterTax) {
		this.totalAfterTax = totalAfterTax;
	}

	public boolean isInMeeting() {
		return inMeeting;
	}

	public void setInMeeting(boolean inMeeting) {
		this.inMeeting = inMeeting;
	}

	/**
	 * @return the supplierTagsList
	 */
	public List<SupplierTags> getSupplierTagsList() {
		return supplierTagsList;
	}

	/**
	 * @param supplierTagsList the supplierTagsList to set
	 */
	public void setSupplierTagsList(List<SupplierTags> supplierTagsList) {
		this.supplierTagsList = supplierTagsList;
	}

	public String getTaxRegistrationNumber() {
		return taxRegistrationNumber;
	}

	public void setTaxRegistrationNumber(String taxRegistrationNumber) {
		this.taxRegistrationNumber = taxRegistrationNumber;
	}

	public List<SupplierFinanicalDocuments> getSupplierFinancialDocuments() {
		return supplierFinancialDocuments;
	}

	public void setSupplierFinancialDocuments(List<SupplierFinanicalDocuments> supplierFinancialDocuments) {
		this.supplierFinancialDocuments = supplierFinancialDocuments;
	}

	public List<SupplierBoardOfDirectors> getSupplierBoardOfDirectors() {
		return supplierBoardOfDirectors;
	}

	public void setSupplierBoardOfDirectors(List<SupplierBoardOfDirectors> supplierBoardOfDirectors) {
		this.supplierBoardOfDirectors = supplierBoardOfDirectors;
	}

	public CommonsMultipartFile getFinancialDocuments() {
		return financialDocuments;
	}

	public void setFinancialDocuments(CommonsMultipartFile financialDocuments) {
		this.financialDocuments = financialDocuments;
	}

	public String getPaidUpCapital() {
		return paidUpCapital;
	}

	public void setPaidUpCapital(String paidUpCapital) {
		this.paidUpCapital = paidUpCapital;
	}

	public Currency getCurrency() {
		return currency;
	}

	public void setCurrency(Currency currency) {
		this.currency = currency;
	}

	/**
	 * @return the buyerAccount
	 */
	public Boolean getBuyerAccount() {
		return buyerAccount;
	}

	/**
	 * @param buyerAccount the buyerAccount to set
	 */
	public void setBuyerAccount(Boolean buyerAccount) {
		this.buyerAccount = buyerAccount;
	}

	/**
	 * @return the onBoardingFromsubmitedDate
	 */
	public Date getOnBoardingFromsubmitedDate() {
		return onBoardingFromsubmitedDate;
	}

	/**
	 * @param onBoardingFromsubmitedDate the onBoardingFromsubmitedDate to set
	 */
	public void setOnBoardingFromsubmitedDate(Date onBoardingFromsubmitedDate) {
		this.onBoardingFromsubmitedDate = onBoardingFromsubmitedDate;
	}

	/**
	 * @return the onBoardingFormSubmittedBy
	 */
	public User getOnBoardingFormSubmittedBy() {
		return onBoardingFormSubmittedBy;
	}

	/**
	 * @param onBoardingFormSubmittedBy the onBoardingFormSubmittedBy to set
	 */
	public void setOnBoardingFormSubmittedBy(User onBoardingFormSubmittedBy) {
		this.onBoardingFormSubmittedBy = onBoardingFormSubmittedBy;
	}

	/**
	 * @return the isOnboardingForm
	 */
	public Boolean getIsOnboardingForm() {
		return isOnboardingForm;
	}

	/**
	 * @param isOnboardingForm the isOnboardingForm to set
	 */
	public void setIsOnboardingForm(Boolean isOnboardingForm) {
		this.isOnboardingForm = isOnboardingForm;
	}

	/**
	 * @return the industryCategories
	 */
	public List<IndustryCategory> getIndustryCategories() {
		return industryCategories;
	}

	/**
	 * @param industryCategories the industryCategories to set
	 */
	public void setIndustryCategories(List<IndustryCategory> industryCategories) {
		this.industryCategories = industryCategories;
	}

	public Date getApprovedDate() {
		return approvedDate;
	}

	public void setApprovedDate(Date approvedDate) {
		this.approvedDate = approvedDate;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((companyContactNumber == null) ? 0 : companyContactNumber.hashCode());
		result = prime * result + ((companyName == null) ? 0 : companyName.hashCode());
		result = prime * result + ((companyRegistrationNumber == null) ? 0 : companyRegistrationNumber.hashCode());
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
		Supplier other = (Supplier) obj;
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
		if (companyRegistrationNumber == null) {
			if (other.companyRegistrationNumber != null)
				return false;
		} else if (!companyRegistrationNumber.equals(other.companyRegistrationNumber))
			return false;
		if (loginEmail == null) {
			if (other.loginEmail != null)
				return false;
		} else if (!loginEmail.equals(other.loginEmail))
			return false;
		if (mobileNumber == null) {
			if (other.mobileNumber != null)
				return false;
		} else if (!mobileNumber.equals(other.mobileNumber))
			return false;
		if (status == null) {
			if (other.status != null)
				return false;
		} else if (!status.equals(other.status))
			return false;
		return true;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Supplier [id=" + id + ", companyName=" + companyName + ", mobileNumber=" + mobileNumber + ", companyContactNumber=" + companyContactNumber + ",  companyRegistrationNumber=" + companyRegistrationNumber + ", fullName=" + fullName + ", loginEmail=" + loginEmail + ", communicationEmail=" + communicationEmail + ", designation=" + designation + ",   remarks=" + remarks + ",   status=" + status + ", yearOfEstablished=" + yearOfEstablished + ", faxNumber=" + faxNumber + ", companyWebsite=" + companyWebsite + ",   declaration=" + declaration + ",   registrationDate=" + registrationDate + ", actionDate=" + actionDate + " , profileUpdatedDate=" + profileUpdatedDate + "]";
	}
}
