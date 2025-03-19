package com.privasia.procurehere.core.entity;

import java.io.Serializable;
import java.util.Date;

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
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.GenericGenerator;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.privasia.procurehere.core.enums.IdSettingPattern;
import com.privasia.procurehere.core.enums.IdSettingType;
import com.privasia.procurehere.core.utils.CustomDateTimeSerializer;
import org.hibernate.annotations.Type;

/**
 * @author RT-Kapil
 * @author ravi
 */
@Entity
@Table(name = "PROC_ID_SETTINGS", indexes = { @Index(columnList = "TENANT_ID", name = "IDX_IDSETTINGS_TENANT_ID") })
public class IdSettings implements Serializable {

	private static final long serialVersionUID = 4492120771625864297L;

	@Id
	@GenericGenerator(name = "idGen", strategy = "uuid.hex")
	@GeneratedValue(generator = "idGen")
	@Column(name = "ID", length = 64)
	private String id;

	@Column(name = "TENANT_ID", nullable = false, length = 64)
	private String tenantId;

	@Column(name = "ID_TYPE", length = 16)
	private String idType;

	@Column(name = "ID_SEQUENCE")
	private Integer idSequence;

	@Column(name = "ID_PREFIX", length = 64)
	private String idPerfix;

	@Column(name = "ID_DELIMITER", length = 64)
	private String idDelimiter;

	@Column(name = "ID_DATE_PATTERN", length = 64)
	private String idDatePattern;

	@Column(name = "ID_PADDING_LENGTH")
	private Integer paddingLength = null;

	@ManyToOne(fetch = FetchType.LAZY, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "MODIFIED_BY", nullable = true, foreignKey = @ForeignKey(name = "FK_ID_SETTING_MOD_BY"))
	private User modifiedBy;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "MODIFIED_DATE")
	private Date modifiedDate;

	@Enumerated(EnumType.STRING)
	@Column(name = "ID_SETTING_TYPE")
	private IdSettingType idSettingType;

	@Enumerated(EnumType.STRING)
	@Column(name = "ID_SETTING_PATTERN")
	private IdSettingPattern idSettingPattern;

	@Column(name = "IS_ENABLE_SUFFIX")
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean enableSuffix = Boolean.FALSE;

	public IdSettings() {
		this.enableSuffix = false;
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
	 * @return the idSequence
	 */
	public Integer getIdSequence() {
		return idSequence;
	}

	/**
	 * @param idSequence the idSequence to set
	 */
	public void setIdSequence(Integer idSequence) {
		this.idSequence = idSequence;
	}

	/**
	 * @return the idPerfix
	 */
	public String getIdPerfix() {
		return idPerfix;
	}

	/**
	 * @param idPerfix the idSequence to set
	 */
	public void setIdPerfix(String idPerfix) {
		this.idPerfix = idPerfix;
	}

	/**
	 * @return the idDelimiter
	 */
	public String getIdDelimiter() {
		return idDelimiter;
	}

	/**
	 * @param idDelimiter the idDelimiter to set
	 */
	public void setIdDelimiter(String idDelimiter) {
		this.idDelimiter = idDelimiter;
	}

	/**
	 * @return the idDatePattern
	 */
	public String getIdDatePattern() {
		return idDatePattern;
	}

	/**
	 * @param idDatePattern the idDatePattern to set
	 */
	public void setIdDatePattern(String idDatePattern) {
		this.idDatePattern = idDatePattern;
	}

	/**
	 * @return the paddingLength
	 */
	public Integer getPaddingLength() {
		return paddingLength;
	}

	/**
	 * @param paddingLength the paddingLength to set
	 */
	public void setPaddingLength(Integer paddingLength) {
		this.paddingLength = paddingLength;
	}

	/**
	 * @return the idType
	 */
	public String getIdType() {
		return idType;
	}

	/**
	 * @param idType the idType to set
	 */
	public void setIdType(String idType) {
		this.idType = idType;
	}

	public User getModifiedBy() {
		return modifiedBy;
	}

	public void setModifiedBy(User modifiedBy) {
		this.modifiedBy = modifiedBy;
	}

	public Date getModifiedDate() {
		return modifiedDate;
	}

	public void setModifiedDate(Date modifiedDate) {
		this.modifiedDate = modifiedDate;
	}

	/**
	 * @return the idSettingType
	 */
	public IdSettingType getIdSettingType() {
		return idSettingType;
	}

	/**
	 * @param idSettingType the idSettingType to set
	 */
	public void setIdSettingType(IdSettingType idSettingType) {
		this.idSettingType = idSettingType;
	}

	/**
	 * @return the idSettingPattern
	 */
	public IdSettingPattern getIdSettingPattern() {
		return idSettingPattern;
	}

	/**
	 * @param idSettingPattern the idSettingPattern to set
	 */
	public void setIdSettingPattern(IdSettingPattern idSettingPattern) {
		this.idSettingPattern = idSettingPattern;
	}

	public Boolean getEnableSuffix() {
		return enableSuffix;
	}

	public void setEnableSuffix(Boolean enableSuffix) {
		this.enableSuffix = enableSuffix;
	}

	public String toLogString() {
		return "IdSettings [id=" + id + ", tenantId=" + tenantId + ", idType=" + idType + ", idSequence=" + idSequence + ", idPerfix=" + idPerfix + ", idDelimiter=" + idDelimiter + ", idDatePattern=" + idDatePattern + ", paddingLength=" + paddingLength + ", modifiedDate=" + modifiedDate + ", idSettingType=" + idSettingType + ", idSettingPattern=" + idSettingPattern + "]";
	}

}
