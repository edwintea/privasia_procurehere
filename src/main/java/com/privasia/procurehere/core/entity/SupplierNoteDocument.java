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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.privasia.procurehere.core.enums.TenantType;
import org.hibernate.annotations.Type;

/**
 * @author teja
 */
@Entity
@Table(name = "PROC_SUPPLIER_NOTE_DOCS")
public class SupplierNoteDocument extends EventDocument implements Serializable {

	private static final long serialVersionUID = -2351343255241524172L;

	@Column(name = "UPLOAD_TENANT_ID", nullable = false, length = 64)
	private String uploadTenantId;

	@Enumerated(EnumType.STRING)
	@Column(name = "TENANT_TYPE", length = 30)
	private TenantType tenantType;

	@Column(name = "IS_VISIBLE")
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean visible = Boolean.TRUE;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "SUPPLIER_ID", nullable = true, foreignKey = @ForeignKey(name = "FK_SUP_NOTES_DOCS"))
	private Supplier supplier;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "CREATED_BY", nullable = true, foreignKey = @ForeignKey(name = "FK_SUP_NOTES_CREATED_BY"))
	private User createdBy;

	@Transient
	private String userName;

	public SupplierNoteDocument() {
		super();
	}

	public SupplierNoteDocument(String id, String fileName, String description, Integer fileSizeInKb, String credContentType, Date uploadDate, Boolean visible, String userName, TenantType tenantType) {
		super.setId(id);
		super.setFileName(fileName);
		super.setFileSizeInKb(fileSizeInKb);
		super.setDescription(description);
		super.setCredContentType(credContentType);
		super.setUploadDate(uploadDate);
		this.visible = visible;
		this.userName = userName;
		this.tenantType = tenantType;
	}

	public String getUploadTenantId() {
		return uploadTenantId;
	}

	public void setUploadTenantId(String uploadTenantId) {
		this.uploadTenantId = uploadTenantId;
	}

	public TenantType getTenantType() {
		return tenantType;
	}

	public void setTenantType(TenantType tenantType) {
		this.tenantType = tenantType;
	}

	public Boolean getVisible() {
		return visible;
	}

	public void setVisible(Boolean visible) {
		this.visible = visible;
	}

	public Supplier getSupplier() {
		return supplier;
	}

	public void setSupplier(Supplier supplier) {
		this.supplier = supplier;
	}

	public User getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(User createdBy) {
		this.createdBy = createdBy;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((tenantType == null) ? 0 : tenantType.hashCode());
		result = prime * result + ((uploadTenantId == null) ? 0 : uploadTenantId.hashCode());
		result = prime * result + ((visible == null) ? 0 : visible.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		SupplierNoteDocument other = (SupplierNoteDocument) obj;
		if (tenantType != other.tenantType)
			return false;
		if (uploadTenantId == null) {
			if (other.uploadTenantId != null)
				return false;
		} else if (!uploadTenantId.equals(other.uploadTenantId))
			return false;
		if (visible == null) {
			if (other.visible != null)
				return false;
		} else if (!visible.equals(other.visible))
			return false;
		return true;
	}

}