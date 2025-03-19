/**
 * 
 */
package com.privasia.procurehere.core.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.privasia.procurehere.core.enums.AuditTypes;
import com.privasia.procurehere.core.enums.ModuleType;
import com.privasia.procurehere.core.enums.converter.ModuleTypeCoverter;
import com.privasia.procurehere.core.utils.CustomDateTimeSerializer;

/**
 * @author Ravi
 */
@MappedSuperclass
public class AuditTrail implements Serializable {

	private static final long serialVersionUID = -4497624526536555476L;

	@Id
	@GenericGenerator(name = "idGen", strategy = "uuid.hex")
	@GeneratedValue(generator = "idGen")
	@Column(name = "ID", length = 64)
	private String id;

	@Enumerated(EnumType.STRING)
	@Column(name = "ACTIVITY", length = 16)
	private AuditTypes activity;

	@Column(name = "DESCRIPTION", length = 3000)
	private String description;

	@Column(name = "TENANT_ID", nullable = false, length = 64)
	private String tenantId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ACTION_BY", nullable = true)
	private User actionBy;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	@Column(name = "ACTION_DATE", nullable = true)
	private Date actionDate;

	@Convert(converter = ModuleTypeCoverter.class)
	@Column(name = "Module_Type", length = 50)
	private ModuleType moduleType;

	@Transient
	private String activityStr;

	@Transient
	private String moduleTypeName;

	/**
	 * @return the moduleTypeName
	 */
	public String getModuleTypeName() {
		return moduleTypeName;
	}

	/**
	 * @param moduleTypeName the moduleTypeName to set
	 */
	public void setModuleTypeName(String moduleTypeName) {
		this.moduleTypeName = moduleTypeName;
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
	 * @return the activity
	 */
	public AuditTypes getActivity() {
		return activity;
	}

	/**
	 * @param activity the activity to set
	 */
	public void setActivity(AuditTypes activity) {
		this.activity = activity;
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
	 * @return the tenantid
	 */
	public String getTenantId() {
		return tenantId;
	}

	/**
	 * @param tenantid the tenantid to set
	 */
	public void setTenantId(String tenantid) {
		this.tenantId = tenantid;
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
	 * @return the moduleType
	 */
	public ModuleType getModuleType() {
		return moduleType;
	}

	/**
	 * @param moduleType the moduleType to set
	 */
	public void setModuleType(ModuleType moduleType) {
		this.moduleType = moduleType;
	}
	
	public String getActivityStr() {
		return activity.getValue();
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((activity == null) ? 0 : activity.hashCode());
		result = prime * result + ((actionDate == null) ? 0 : actionDate.hashCode());
		result = prime * result + ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
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
		AuditTrail other = (AuditTrail) obj;
		if (activity != other.activity)
			return false;
		if (actionDate == null) {
			if (other.actionDate != null)
				return false;
		} else if (!actionDate.equals(other.actionDate))
			return false;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	public String toLogString() {
		return "AuditTrail [activity=" + activity + ", description=" + description + ", actionDate=" + actionDate + "]";
	}

}
