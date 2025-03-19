package com.privasia.procurehere.core.pojo;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.privasia.procurehere.core.entity.Buyer;
import com.privasia.procurehere.core.entity.Owner;
import com.privasia.procurehere.core.entity.User;
import com.privasia.procurehere.core.entity.UserRole;
import com.privasia.procurehere.core.enums.TenantType;
import com.privasia.procurehere.core.enums.UserType;
import com.privasia.procurehere.core.utils.CustomDateTimeSerializer;

public class UserPojo implements Serializable {

	private static final long serialVersionUID = -4698727632515221438L;

	private String id;

	private String loginId;

	private String password;

	private String name;

	private UserRole userRole;

	private String phoneNumber;

	private String tenantId;

	private TenantType tenantType;

	private Buyer buyer;

	private Owner owner;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	private Date createdDate;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	private Date lastLoginTime;

	private Date lastFailedLoginTime;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	private Date modifiedDate;

	private String createdBy;

	private String modifiedBy;

	private boolean locked = false;

	private boolean deleted;

	private boolean active = true;

	private Integer failedAttempts;

	private Date lastPasswordChangedDate;

	private String userType;

	private String communicationEmail;

	private String designation;

	private String role;

	private String status;

	private String isLocked;

	private String prTemplate;

	private String grTemplate;

	private String invoiceTemplate;

	private String rfxTemplate;

	private String lastFailedLoginTimeStr;

	private String lastLoginTimeStr;

	private boolean emailNotifications = true;

	public UserPojo() {

	}

	public UserPojo(User user) {

		this.id = user.getId();
		this.loginId = user.getLoginId();
		this.name = user.getName();
		this.phoneNumber = user.getPhoneNumber();
		this.createdDate = user.getCreatedDate();
		this.lastLoginTime = user.getLastLoginTime();
		this.createdBy = user.getCreatedBy() != null ? user.getCreatedBy().getLoginId() : null;
		this.userRole = user.getUserRole();
		this.modifiedBy = user.getModifiedBy() != null ? user.getModifiedBy().getLoginId() : null;
		this.modifiedDate = user.getModifiedDate();
		this.active = user.isActive();
		this.locked = user.isLocked();

		this.buyer = user.getBuyer();
		this.deleted = user.isDeleted();
		this.failedAttempts = user.getFailedAttempts();
		this.lastFailedLoginTime = user.getLastFailedLoginTime();
		this.lastPasswordChangedDate = user.getLastPasswordChangedDate();
		this.owner = user.getOwner();
		this.password = user.getPassword();
		this.tenantId = user.getTenantId();
		this.tenantType = user.getTenantType();
	}

	public UserPojo(String id, String loginId, String name, String phoneNumber, Date createdDate, Date lastLoginTime, String createdBy, String modifiedBy, String userRole, Date modifiedDate, boolean active, boolean locked, UserType userType, String buyerObj) {
		this.id = id;
		this.loginId = loginId;
		this.name = name;
		this.phoneNumber = phoneNumber;
		this.createdDate = createdDate;
		this.lastLoginTime = lastLoginTime;
		this.createdBy = createdBy;
		if (userRole != null) {
			UserRole ur = new UserRole();
			ur.setRoleName(userRole);
			this.userRole = ur;
		}
		this.modifiedBy = modifiedBy;
		this.modifiedDate = modifiedDate;
		this.active = active;
		this.locked = locked;
		if (buyerObj != null) {
			Buyer by = new Buyer();
			by.setId(buyerObj);
			this.buyer = by;
		}
		this.userType = userType != null ? userType.name() : "";

	}

	public UserPojo(String id, String loginId, String name, String tenantId) {
		this.id = id;
		this.name = name;
		this.loginId = loginId;
		this.tenantId = tenantId;
	}

	public UserPojo(String id, String loginId, String name, String tenantId, Boolean deleted, String communicationEmail,  boolean emailNotifications) {
		this.id = id;
		this.name = name;
		this.loginId = loginId;
		this.tenantId = tenantId;
		this.deleted = deleted;
		this.communicationEmail = communicationEmail;
		this.emailNotifications = emailNotifications;
	}

	/**
	 * @return the prTemplate
	 */
	public String getPrTemplate() {
		return prTemplate;
	}

	/**
	 * @param prTemplate the prTemplate to set
	 */
	public void setPrTemplate(String prTemplate) {
		this.prTemplate = prTemplate;
	}
	/**
	 * @return the rfxTemplate
	 */
	public String getRfxTemplate() {
		return rfxTemplate;
	}





	/**
	 * @return the grTemplate
	 */
	public String getGrTemplate() {
		return grTemplate;
	}

	/**
	 * @param grTemplate the grTemplate to set
	 */
	public void setGrTemplate(String grTemplate) {
		this.grTemplate = grTemplate;
	}


	/**
	 * @return the invoiceTemplate
	 */
	public String getInvoiceTemplate() {
		return invoiceTemplate;
	}

	/**
	 * @param invoiceTemplate the invoiceTemplate to set
	 */
	public void setInvoiceTemplate(String invoiceTemplate) {
		this.invoiceTemplate = invoiceTemplate;
	}


	/**
	 * @param rfxTemplate the rfxTemplate to set
	 */
	public void setRfxTemplate(String rfxTemplate) {
		this.rfxTemplate = rfxTemplate;
	}

	/**
	 * @return the isLocked
	 */
	public String getIsLocked() {
		return isLocked;
	}

	/**
	 * @param isLocked the isLocked to set
	 */
	public void setIsLocked(String isLocked) {
		this.isLocked = isLocked;
	}

	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}

	/**
	 * @return the role
	 */
	public String getRole() {
		return role;
	}

	/**
	 * @param role the role to set
	 */
	public void setRole(String role) {
		this.role = role;
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
		this.loginId = loginId;
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
	 * @return the userType
	 */
	public String getUserType() {
		return userType;
	}

	/**
	 * @param userType the userType to set
	 */
	public void setUserType(String userType) {
		this.userType = userType;
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

	public String getLastFailedLoginTimeStr() {
		return lastFailedLoginTimeStr;
	}

	public void setLastFailedLoginTimeStr(String lastFailedLoginTimeStr) {
		this.lastFailedLoginTimeStr = lastFailedLoginTimeStr;
	}

	public String getLastLoginTimeStr() {
		return lastLoginTimeStr;
	}

	public void setLastLoginTimeStr(String lastLoginTimeStr) {
		this.lastLoginTimeStr = lastLoginTimeStr;
	}

	public boolean isEmailNotifications() {
		return emailNotifications;
	}
	public void setEmailNotifications(boolean emailNotifications) {
		this.emailNotifications = emailNotifications;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((loginId == null) ? 0 : loginId.hashCode());
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
		UserPojo other = (UserPojo) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (loginId == null) {
			if (other.loginId != null)
				return false;
		} else if (!loginId.equals(other.loginId))
			return false;
		return true;
	}

}
