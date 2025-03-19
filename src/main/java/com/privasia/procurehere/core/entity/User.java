package com.privasia.procurehere.core.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.apache.commons.codec.binary.Base64;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.annotations.GenericGenerator;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.privasia.procurehere.core.enums.TenantType;
import com.privasia.procurehere.core.enums.UserType;
import com.privasia.procurehere.core.utils.CustomDateTimeSerializer;
import org.hibernate.annotations.Type;

/**
 * The persistent class for the ADMIN_USER database table.
 * 
 * @author Nitin Otageri
 */
@Entity
@Table(name = "PROC_USER", indexes = { @Index(columnList = "TENANT_ID", name = "INDEX_USR_TENANT_ID") })
public class User implements Serializable {

	private static final long serialVersionUID = -5162118261757481783L;

	private static final Logger LOG = LogManager.getLogger(User.class);

	@Id
	@GenericGenerator(name = "idGen", strategy = "uuid.hex")
	@GeneratedValue(generator = "idGen")
	@Column(name = "ID", length = 64)
	private String id;

	@NotNull(message = "{user.loginid.not.empty}")
	@Size(min = 5, max = 150, message = "{user.loginid.length}")
	@Column(name = "LOGIN_ID", length = 150, nullable = false)
	private String loginId;

	@JsonIgnore
	@Size(min = 5, max = 64, message = "{user.password.length}")
	@Column(name = "USER_PASSWORD", length = 64, nullable = false)
	private String password;

	@NotNull(message = "{user.username.not.empty}")
	@Size(min = 4, max = 160, message = "{user.username.length}")
	@Column(name = "USER_NAME", nullable = false, length = 160)
	private String name;

	@Column(name = "USER_DSGN", nullable = true, length = 160)
	private String designation;

	@Column(name = "COMMUNICATION_EMAIL", length = 150, nullable = true)
	private String communicationEmail;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "ROLE_ID", nullable = false, foreignKey = @ForeignKey(name = "FK_USER_USER_ROLE"))
	private UserRole userRole;

	@Column(name = "PHONE_NUMBER", length = 32)
	private String phoneNumber;

	@Column(name = "TENANT_ID", nullable = false, length = 64)
	private String tenantId;

	@Column(name = "TENANT_TYPE", length = 30)
	private TenantType tenantType;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, optional = true, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "SUPPLIER_ID", nullable = true, foreignKey = @ForeignKey(name = "FK_USER_SUPPLIER_ID"))
	private Supplier supplier;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, optional = true, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "BUYER_ID", nullable = true, foreignKey = @ForeignKey(name = "FK_USER_BUYER_ID"))
	private Buyer buyer;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, optional = true, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "FINANCE_COMPANY_ID", nullable = true, foreignKey = @ForeignKey(name = "FK_USER_FINANCE_COMPANY_ID"))
	private FinanceCompany financeCompany;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, optional = true, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "OWNER_ID", nullable = true, foreignKey = @ForeignKey(name = "FK_USER_OWNER_ID"))
	private Owner owner;

	@Lob
	@Type(type = "org.hibernate.type.BinaryType")
	@Column(name = "PROFILE_PIC_DATA", nullable = true)
	private byte[] prfPicAttatchment;

	@Column(name = "PROFILE_PIC_NAME", length = 500, nullable = true)
	private String prfPicName;

	@Column(name = "PROFILE_PIC_TYPE", length = 160, nullable = true)
	private String prfPiccontentType;

	@Column(name = "LOGIN_IP_ADDRESS", length = 500, nullable = true)
	private String loginIpAddress;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	@Column(name = "CREATION_DATE", nullable = true, length = 20)
	private Date createdDate;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	@Column(name = "LAST_LOGIN_TIME", nullable = true, length = 20)
	private Date lastLoginTime;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	@Column(name = "LAST_FAILED_LOGIN_TIME", nullable = true, length = 20)
	private Date lastFailedLoginTime;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	@Column(name = "LAST_MODIFIED_TIME", nullable = true, length = 20)
	private Date modifiedDate;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "CREATED_BY", nullable = true, foreignKey = @ForeignKey(name = "FK_USER_CREATED_BY"))
	private User createdBy;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "USER_LAST_MODIFIED_BY", nullable = true, foreignKey = @ForeignKey(name = "FK_USER_MODIFIED_BY"))
	private User modifiedBy;

	@Column(name = "ACCOUNT_LOCKED")
	@Type(type = "org.hibernate.type.NumericBooleanType")
private boolean locked = false;

	@Column(name = "ACCOUNT_DELETED")
	@Type(type = "org.hibernate.type.NumericBooleanType")
private boolean deleted;

	@Column(name = "ACCOUNT_ACTIVE")
	@Type(type = "org.hibernate.type.NumericBooleanType")
private boolean active = true;

	@Column(name = "FAILED_ATTEMPTS", length = 2)
	private Integer failedAttempts;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	@Column(name = "LAST_PASSWORD_CHANGE_DATE")
	private Date lastPasswordChangedDate;

	@JsonIgnore
	@ManyToMany(fetch = FetchType.LAZY, cascade = { CascadeType.REFRESH })
	@JoinTable(name = "PROC_USER_TEMPLATE_MAPPING", joinColumns = @JoinColumn(name = "USER_ID"), inverseJoinColumns = @JoinColumn(name = "TEMPLATE_ID"))
	private List<RfxTemplate> assignedTemplates;

	@JsonIgnore
	@ManyToMany(fetch = FetchType.LAZY, cascade = { CascadeType.REFRESH })
	@JoinTable(name = "PROC_USER_PR_TEMPLATE_MAPPING", joinColumns = @JoinColumn(name = "USER_ID"), inverseJoinColumns = @JoinColumn(name = "TEMPLATE_ID"))
	private List<PrTemplate> assignedPrTemplates;




	
	 @JsonIgnore
	 @ManyToMany(fetch = FetchType.LAZY, cascade = { CascadeType.REFRESH })
	 @JoinTable(name = "PROC_USER_RFS_TEMP_MAPPING", joinColumns = @JoinColumn(name = "USER_ID"), inverseJoinColumns = @JoinColumn(name = "SOURCING_TEMPLATE_ID"))
	 private List<SourcingFormTemplate> assignedSourcingTemplates;

	@JsonIgnore
	@ManyToMany(fetch = FetchType.LAZY, cascade = { CascadeType.REFRESH })
	@JoinTable(name = "PROC_USER_BUSINESS_UNIT_MAPPING", joinColumns = @JoinColumn(name = "USER_ID"), inverseJoinColumns = @JoinColumn(name = "BUSINESS_UNIT_ID"))
	private List<BusinessUnit> assignedBusinessUnits;

	@Column(name = "DEVICE_ID", length = 150, nullable = true)
	private String deviceId;

	@Column(name = "BQ_PAGE_LENGTH", length = 3)
	private Integer bqPageLength;

	@Transient
	boolean checkControl = true; // default true

	@Enumerated(EnumType.STRING)
	@Column(name = "USER_TYPE")
	private UserType userType = UserType.NORMAL_USER;
	
	@Column(name = "SHOW_WIZARD_TUTORIAL", nullable = true)
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean showWizardTutorial = Boolean.FALSE;
	
	@JsonIgnore
	@ManyToMany(fetch = FetchType.LAZY, cascade = { CascadeType.REFRESH })
	@JoinTable(name = "PROC_USER_SP_TEMP_MAPPING", joinColumns = @JoinColumn(name = "USER_ID"), inverseJoinColumns = @JoinColumn(name = "SUPP_PRFMC_TEMPLATE_ID"))
	private List<SupplierPerformanceTemplate> assignedSupplierPerformanceTemplates;

	@Transient
	private Integer passwordExpiryInDays;

	@Transient
	Boolean isBuyerErpEnable = false;

	public User(String name, String communicationMail) {
		this.name = name;
		this.communicationEmail = communicationMail;
	}

	public User(String id) {
		this.id = id;
	}

	@Column(name = "LANGUAGE_CODE", nullable = true, length = 10)
	private String languageCode = Locale.ENGLISH.getLanguage();

	@Column(name = "EMAIL_NOTIFICATIONS ")
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean emailNotifications = true;

	@Override
	public Object clone() throws CloneNotSupportedException {

		User user = new User();
		user.setActive(this.active);
		user.setCheckControl(this.checkControl);
		user.setCommunicationEmail(communicationEmail);
		user.setCreatedDate(createdDate);
		user.setDeleted(deleted);
		user.setDesignation(designation);
		user.setFailedAttempts(failedAttempts);
		user.setId(id);
		user.setLastFailedLoginTime(lastFailedLoginTime);
		user.setLastLoginTime(lastLoginTime);
		user.setLastPasswordChangedDate(lastPasswordChangedDate);
		user.setLocked(locked);
		user.setLoginId(loginId);
		user.setModifiedDate(modifiedDate);
		user.setName(name);
		user.setPhoneNumber(phoneNumber);
		user.setPassword(password);
		user.setTenantId(tenantId);
		user.setTenantType(tenantType);
		user.setLanguageCode(languageCode);
		user.setEmailNotifications(emailNotifications);
		return user;
	}

	public User() {
		this.languageCode = Locale.ENGLISH.getLanguage();
	}

	public User(String id, String name, String communicationEmail, boolean emailNotifications, String tenantId) {
		this.id = id;
		this.name = name;
		this.communicationEmail = communicationEmail;
		this.emailNotifications = emailNotifications;
		this.tenantId = tenantId;
	}

	public User(String id, String loginId, String name, String communicationEmail, boolean emailNotifications, String tenantId) {
		this.id = id;
		this.loginId = loginId;
		this.name = name;
		this.communicationEmail = communicationEmail;
		this.emailNotifications = emailNotifications;
		this.tenantId = tenantId;
	}

	public User(String id, String loginId, String name, String communicationEmail, boolean emailNotifications, String tenantId, boolean deleted) {
		this.id = id;
		this.name = name;
		this.loginId = loginId;
		this.communicationEmail = communicationEmail;
		this.emailNotifications = emailNotifications;
		this.tenantId = tenantId;
		this.deleted = deleted;
	}

	public User(String id, String loginId, String name, String communicationEmail, String designation, String phoneNumber, UserType userType, UserRole userRole, boolean active) {
		this.id = id;
		this.loginId = loginId;
		this.name = name;
		this.communicationEmail = communicationEmail;
		this.designation = designation;
		this.phoneNumber = phoneNumber;
		this.userType = userType;
		this.userRole = userRole;
		// this.assignedTemplates=assignedTemplates;
		// this.assignedPrTemplates=assignedPrTemplates;
		// this.tenantId=tenantId;
		this.active = active;
	}

	// supplier notification for mobile
	public User(String id, String name, String communicationEmail, boolean emailNotifications, String loginId, String tenantId, String deviceId) {
		this.id = id;
		this.name = name;
		this.communicationEmail = communicationEmail;
		this.emailNotifications = emailNotifications;
		this.tenantId = tenantId;
		this.deviceId = deviceId;
		this.loginId = loginId;
	}

	// PH-320
	public User(String id, String name, String loginId) {
		this.id = id;
		this.name = name;
		this.loginId = loginId;
	}

	public String getCreatedByName() {
		try {
			if (this.createdBy != null) {
				return createdBy.getLoginId();
			}
		} catch (Exception e) {
		}
		return null;
	}

	public String getModifiedByName() {
		try {
			if (this.modifiedBy != null) {
				return modifiedBy.getLoginId();
			}
		} catch (Exception e) {
		}
		return null;
	}

	public String getProfilePicture() {
		String base64Encoded = null;
		if (this.prfPicAttatchment != null) {
			byte[] encodeBase64 = Base64.encodeBase64(this.prfPicAttatchment);
			try {
				base64Encoded = new String(encodeBase64, "UTF-8");
			} catch (Exception e) {
			}
		}
		return base64Encoded;
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
	 * @return the loginId
	 */
	public String getLoginId() {
		return loginId;
	}

	/**
	 * @param loginId the loginId to set
	 */
	public void setLoginId(String loginId) {
		this.loginId = loginId.toUpperCase();
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
	 * @return the userRole
	 */
	public UserRole getUserRole() {
		// Doing this to avoid lazy exception during JSON rendering
		try {
			if (userRole != null) {
				userRole.getRoleName();
				userRole.setCreatedBy(null);
				userRole.setModifiedBy(null);
			}
		} catch (Exception e) {
			userRole = null;
		}
		return userRole;
	}

	/**
	 * @param userRole the userRole to set
	 */
	public void setUserRole(UserRole userRole) {
		this.userRole = userRole;
	}

	/**
	 * @return the phoneNumber
	 */
	public String getPhoneNumber() {
		return phoneNumber;
	}

	/**
	 * @param phoneNumber the phoneNumber to set
	 */
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	/**
	 * @return the tenantId
	 */
	public String getTenantId() {
		return tenantId;
	}

	/**
	 * @param tenantId the tenantId to set
	 */
	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
	}

	/**
	 * @return the tenantType
	 */
	public TenantType getTenantType() {
		return tenantType;
	}

	/**
	 * @param tenantType the tenantType to set
	 */
	public void setTenantType(TenantType tenantType) {
		this.tenantType = tenantType;
	}

	/**
	 * @return the creationDate
	 */
	public Date getCreatedDate() {
		return createdDate;
	}

	/**
	 * @param creationDate the creationDate to set
	 */
	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	/**
	 * @return the lastLoginTime
	 */
	public Date getLastLoginTime() {
		return lastLoginTime;
	}

	/**
	 * @param lastLoginTime the lastLoginTime to set
	 */
	public void setLastLoginTime(Date lastLoginTime) {
		this.lastLoginTime = lastLoginTime;
	}

	/**
	 * @return the lastFailedLoginTime
	 */
	public Date getLastFailedLoginTime() {
		return lastFailedLoginTime;
	}

	/**
	 * @param lastFailedLoginTime the lastFailedLoginTime to set
	 */
	public void setLastFailedLoginTime(Date lastFailedLoginTime) {
		this.lastFailedLoginTime = lastFailedLoginTime;
	}

	/**
	 * @return the lastModifiedTime
	 */
	public Date getModifiedDate() {
		return modifiedDate;
	}

	/**
	 * @param lastModifiedTime the lastModifiedTime to set
	 */
	public void setModifiedDate(Date modifiedDate) {
		this.modifiedDate = modifiedDate;
	}

	/**
	 * @return the createdBy
	 */
	public User getCreatedBy() {
		try {
			if (createdBy != null) {
				createdBy.getLoginId();
				// createdBy.setCreatedBy(null);
				// createdBy.setModifiedBy(null);
				// createdBy.setUserRole(null);
			}
		} catch (Exception e) {
			createdBy = null;
			LOG.error(" Error : " + e.getMessage(), e);
		}
		return createdBy;
	}

	/**
	 * @param createdBy the createdBy to set
	 */
	public void setCreatedBy(User createdBy) {
		this.createdBy = createdBy;
	}

	/**
	 * @return the modifiedBy
	 */
	public User getModifiedBy() {
		try {
			if (modifiedBy != null) {
				modifiedBy.getLoginId();
				// modifiedBy.setCreatedBy(null);
				// modifiedBy.setModifiedBy(null);
				// modifiedBy.setUserRole(null);
			}
		} catch (Exception e) {
			modifiedBy = null;
		}
		return modifiedBy;
	}

	/**
	 * @param modifiedBy the modifiedBy to set
	 */
	public void setModifiedBy(User modifiedBy) {
		this.modifiedBy = modifiedBy;
	}

	/**
	 * @return the locked
	 */
	public boolean isLocked() {
		return locked;
	}

	/**
	 * @param locked the locked to set
	 */
	public void setLocked(boolean locked) {
		this.locked = locked;
	}

	/**
	 * @return the deleted
	 */
	public boolean isDeleted() {
		return deleted;
	}

	/**
	 * @param deleted the deleted to set
	 */
	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

	/**
	 * @return the active
	 */
	public boolean isActive() {
		return active;
	}

	/**
	 * @param active the active to set
	 */
	public void setActive(boolean active) {
		this.active = active;
	}

	/**
	 * @return the failedAttempts
	 */
	public Integer getFailedAttempts() {
		return failedAttempts;
	}

	/**
	 * @param failedAttempts the failedAttempts to set
	 */
	public void setFailedAttempts(Integer failedAttempts) {
		this.failedAttempts = failedAttempts;
	}

	/**
	 * @return the lastPasswordChangedDate
	 */
	public Date getLastPasswordChangedDate() {
		return lastPasswordChangedDate;
	}

	/**
	 * @param lastPasswordChangedDate the lastPasswordChangedDate to set
	 */
	public void setLastPasswordChangedDate(Date lastPasswordChangedDate) {
		this.lastPasswordChangedDate = lastPasswordChangedDate;
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
	 * @return the buyer
	 */
	public Buyer getBuyer() {
		return buyer;
	}

	/**
	 * @param buyer the buyer to set
	 */
	public void setBuyer(Buyer buyer) {
		this.buyer = buyer;
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

	public String getDesignation() {
		return designation;
	}

	public void setDesignation(String designation) {
		this.designation = designation;
	}

	/**
	 * @return the prfPicAttatchment
	 */
	public byte[] getPrfPicAttatchment() {
		return prfPicAttatchment;
	}

	/**
	 * @param prfPicAttatchment the prfPicAttatchment to set
	 */
	public void setPrfPicAttatchment(byte[] prfPicAttatchment) {
		this.prfPicAttatchment = prfPicAttatchment;
	}

	/**
	 * @return the prfPicName
	 */
	public String getPrfPicName() {
		return prfPicName;
	}

	/**
	 * @param prfPicName the prfPicName to set
	 */
	public void setPrfPicName(String prfPicName) {
		this.prfPicName = prfPicName;
	}

	/**
	 * @return the prfPiccontentType
	 */
	public String getPrfPiccontentType() {
		return prfPiccontentType;
	}

	/**
	 * @param prfPiccontentType the prfPiccontentType to set
	 */
	public void setPrfPiccontentType(String prfPiccontentType) {
		this.prfPiccontentType = prfPiccontentType;
	}

	/**
	 * @return the assignedTemplates
	 */
	public List<RfxTemplate> getAssignedTemplates() {
		return assignedTemplates;
	}

	/**
	 * @param assignedTemplates the assignedTemplates to set
	 */
	public void setAssignedTemplates(List<RfxTemplate> assignedTemplates) {
		this.assignedTemplates = assignedTemplates;
	}

	/**
	 * @return the assignedPrTemplates
	 */
	public List<PrTemplate> getAssignedPrTemplates() {
		return assignedPrTemplates;
	}

	/**
	 * @param assignedPrTemplates the assignedPrTemplates to set
	 */
	public void setAssignedPrTemplates(List<PrTemplate> assignedPrTemplates) {
		this.assignedPrTemplates = assignedPrTemplates;
	}




	/**
	 * @return the checkControl
	 */
	public boolean isCheckControl() {
		return checkControl;
	}

	/**
	 * @param checkControl the checkControl to set
	 */
	public void setCheckControl(boolean checkControl) {
		this.checkControl = checkControl;
	}

	/**
	 * @return the loginIpAddress
	 */
	public String getLoginIpAddress() {
		return loginIpAddress;
	}

	/**
	 * @param loginIpAddress the loginIpAddress to set
	 */
	public void setLoginIpAddress(String loginIpAddress) {
		this.loginIpAddress = loginIpAddress;
	}

	/**
	 * @return the deviceId
	 */
	public String getDeviceId() {
		return deviceId;
	}

	/**
	 * @param deviceId the deviceId to set
	 */
	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	/**
	 * @return the bqPageLength
	 */
	public Integer getBqPageLength() {
		return bqPageLength;
	}

	/**
	 * @param bqPageLength the bqPageLength to set
	 */
	public void setBqPageLength(Integer bqPageLength) {
		this.bqPageLength = bqPageLength;
	}

	/**
	 * @return the userType
	 */
	public UserType getUserType() {
		return userType;
	}

	/**
	 * @param userType the userType to set
	 */
	public void setUserType(UserType userType) {
		this.userType = userType;
	}

	/**
	 * @return the isBuyerErpEnable
	 */
	public Boolean getIsBuyerErpEnable() {
		return isBuyerErpEnable;
	}

	/**
	 * @param isBuyerErpEnable the isBuyerErpEnable to set
	 */
	public void setIsBuyerErpEnable(Boolean isBuyerErpEnable) {
		this.isBuyerErpEnable = isBuyerErpEnable;
	}

	public FinanceCompany getFinanceCompany() {
		return financeCompany;
	}

	public void setFinanceCompany(FinanceCompany financeCompany) {
		this.financeCompany = financeCompany;
	}

	/**
	 * @return the showWizardTutorial
	 */
	public Boolean getShowWizardTutorial() {
		return showWizardTutorial;
	}

	/**
	 * @param showWizardTutorial the showWizardTutorial to set
	 */
	public void setShowWizardTutorial(Boolean showWizardTutorial) {
		this.showWizardTutorial = showWizardTutorial;
	}

	/**
	 * @return the passwordExpiryInDays
	 */
	public Integer getPasswordExpiryInDays() {
		return passwordExpiryInDays;
	}

	/**
	 * @param passwordExpiryInDays the passwordExpiryInDays to set
	 */
	public void setPasswordExpiryInDays(Integer passwordExpiryInDays) {
		this.passwordExpiryInDays = passwordExpiryInDays;
	}

	/**
	 * @return the emailNotifications
	 */
	public Boolean getEmailNotifications() {
		return emailNotifications;
	}

	public void setEmailNotifications(Boolean emailNotifications) {
		this.emailNotifications = emailNotifications;
	}

	/**
	 * @return the assignedSourcingTemplates
	 */
	public List<SourcingFormTemplate> getAssignedSourcingTemplates() {
		return assignedSourcingTemplates;
	}

	/**
	 * @param assignedSourcingTemplates the assignedSourcingTemplates to set
	 */
	public void setAssignedSourcingTemplates(List<SourcingFormTemplate> assignedSourcingTemplates) {
		this.assignedSourcingTemplates = assignedSourcingTemplates;
	}

	/**
	 * @return the assignedSupplierPerformanceTemplates
	 */
	public List<SupplierPerformanceTemplate> getAssignedSupplierPerformanceTemplates() {
		return assignedSupplierPerformanceTemplates;
	}

	/**
	 * @param assignedSupplierPerformanceTemplates the assignedSupplierPerformanceTemplates to set
	 */
	public void setAssignedSupplierPerformanceTemplates(
			List<SupplierPerformanceTemplate> assignedSupplierPerformanceTemplates) {
		this.assignedSupplierPerformanceTemplates = assignedSupplierPerformanceTemplates;
	}


	/**
	 * @return the assignedBusinessUnit
	 */
	public List<BusinessUnit> getAssignedBusinessUnits() {
		return assignedBusinessUnits;
	}

	/**
	 * @param assignedBusinessUnits the assignedBusinessUnits to set
	 */
	public void setAssignedBusinessUnits(List<BusinessUnit> assignedBusinessUnits) {
		this.assignedBusinessUnits = assignedBusinessUnits;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (deleted ? 1231 : 1237);
		result = prime * result + ((loginId == null) ? 0 : loginId.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		User other = (User) obj;
		if (deleted != other.deleted)
			return false;
		if (loginId == null) {
			if (other.loginId != null)
				return false;
		} else if (!loginId.equals(other.loginId))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	public String toLogString() {
		return "User [id=" + id + ", loginId=" + loginId + ", name=" + name + ", designation=" + designation + ", communicationEmail=" + communicationEmail + ", phoneNumber=" + phoneNumber + "]";
	}

	public User createStripCopy() {
		User copy = new User();
		copy.setId(getId());
		copy.setLoginId(getLoginId());
		copy.setName(getName());
		copy.setCommunicationEmail(getCommunicationEmail());
		copy.setEmailNotifications(getEmailNotifications());
		copy.setPhoneNumber(getPhoneNumber());
		copy.setDesignation(getDesignation());
		copy.setTenantId(getTenantId());
		return copy;
	}

	public User createMobileShallowCopy() {
		User user = new User();
		user.setId(getId());
		user.setName(getName());
		return user;
	}

	public String getLanguageCode() {
		return languageCode;
	}

	public void setLanguageCode(String languageCode) {
		this.languageCode = languageCode;
	}

	@Override
	public String toString() {
		return id;
	}

}
