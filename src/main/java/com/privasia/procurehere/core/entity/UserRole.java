/**
 * 
 */
package com.privasia.procurehere.core.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

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
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.privasia.procurehere.core.enums.Status;
import com.privasia.procurehere.core.utils.CustomDateTimeSerializer;

/**
 * @author Nitin Otageri
 */
@Entity
@Table(name = "PROC_USER_ROLE", indexes = { @Index(columnList = "TENANT_ID", name = "INDEX_USR_ROL_TENANT_ID") })
public class UserRole implements Serializable {

	@Transient
	private static final long serialVersionUID = 5587126429647989210L;

	@Id
	@GenericGenerator(name = "idGen", strategy = "uuid.hex")
	@GeneratedValue(generator = "idGen")
	@Column(name = "ROLE_ID", length = 64)
	private String id;

	@NotEmpty(message = "{userRole.name.empty}")
	@Size(min = 1, max = 64, message = "{userRole.name.length}")
	@Column(name = "ROLE_NAME", nullable = false, length = 160)
	private String roleName;

	// @NotEmpty
	@Column(name = "TENANT_ID", length = 64)
	private String tenantId;

	@Column(name = "ROLE_DESCRIPTION", length = 400)
	private String roleDescription;

	@JsonIgnore
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "PROC_ROLE_ACL_MAPPING", joinColumns = { @JoinColumn(name = "ROLE_ID") }, inverseJoinColumns = { @JoinColumn(name = "ACL_ID") })
	private List<AccessRights> accessControlList;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "CREATED_BY", nullable = true, foreignKey = @ForeignKey(name = "FK_USERROLE_CREATED_BY"))
	private User createdBy;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	@Column(name = "CREATED_DATE", nullable = true)
	private Date createdDate;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "USER_LAST_MODIFIED_BY", nullable = true, foreignKey = @ForeignKey(name = "FK_USERROLE_MODIFIED_BY"))
	private User modifiedBy;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	@Column(name = "MODIFIED_DATE", nullable = true)
	private Date modifiedDate;

	@Enumerated(EnumType.STRING)
	@Column(name = "ROLE_STATUS")
	private Status status;

	@Column(name = "ACCOUNT_DELETED")
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean deleted = Boolean.FALSE;
	
	@Column(name = "READ_ONLY")
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean readOnly = Boolean.FALSE;

	public UserRole() {
		this.deleted = Boolean.FALSE;
		this.status = Status.ACTIVE;
		this.readOnly = Boolean.FALSE;
	}

	public String getCreatedByName() {
		try {
			if (this.createdBy != null) {
				return createdBy.getName();
			}
		} catch (Exception e) {
		}
		return "";
	}

	public String getModifiedByName() {
		try {
			if (this.modifiedBy != null) {
				return modifiedBy.getName();
			}
		} catch (Exception e) {
		}
		return "";
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
	 * @return the roleName
	 */
	public String getRoleName() {
		return roleName;
	}

	/**
	 * @param roleName the roleName to set
	 */
	public void setRoleName(String roleName) {
		this.roleName = roleName;
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
	 * @return the roleDescription
	 */
	public String getRoleDescription() {
		return roleDescription;
	}

	/**
	 * @param roleDescription the roleDescription to set
	 */
	public void setRoleDescription(String roleDescription) {
		this.roleDescription = roleDescription;
	}

	/**
	 * @return the accessControlList
	 */
	public List<AccessRights> getAccessControlList() {
		return accessControlList;
	}

	/**
	 * @param accessControlList the accessControlList to set
	 */
	public void setAccessControlList(List<AccessRights> accessControlList) {
		this.accessControlList = accessControlList;
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
	 * @return the status
	 */
	public Status getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(Status status) {
		this.status = status;
	}

	/**
	 * @return the readOnly
	 */
	public Boolean getReadOnly() {
		return readOnly;
	}

	/**
	 * @param readOnly the readOnly to set
	 */
	public void setReadOnly(Boolean readOnly) {
		this.readOnly = readOnly;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((roleName == null) ? 0 : roleName.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof UserRole)) {
			return false;
		}
		UserRole other = (UserRole) obj;
		if (roleName == null) {
			if (other.roleName != null) {
				return false;
			}
		} else if (!roleName.equals(other.roleName)) {
			return false;
		}
		return true;
	}

	public String toLogString() {
		return "UserRole [id=" + id + ", roleName=" + roleName + ", tenantId=" + tenantId + ", roleDescription=" + roleDescription + ", createdDate=" + createdDate + ", modifiedDate=" + modifiedDate + ", status=" + status + "]";
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "UserRole [roleName=" + roleName + "]";
	}

}
