/**
 * 
 */
package com.privasia.procurehere.core.entity;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

/**
 * @author ravi
 */
@Entity
@Table(name = "PROC_SUP_FORM_ITEM_ATTA", indexes = { @Index(columnList = "TENANT_ID", name = "INDEX_SFA_TENANT_ID") })
public class SupplierFormItemAttachment implements Serializable {

	private static final long serialVersionUID = 8958561168479159049L;

	@Id
	@GenericGenerator(name = "idGen", strategy = "uuid.hex")
	@GeneratedValue(generator = "idGen")
	@Column(name = "ID", length = 64)
	private String id;

	@ManyToOne(optional = false, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "ITEM_ID", nullable = false, foreignKey = @ForeignKey(name = "FK_SFA_ATTA_ITM_ID"))
	private SupplierFormItem formItem;

	@Basic(fetch = FetchType.LAZY)
	@Lob
	@Type(type = "org.hibernate.type.BinaryType")
	@Column(name = "ATTACHMENT_FILE_DATA")
	private byte[] fileData;

	@Column(name = "ATTACHMENT_FILE_NAME", length = 500)
	private String fileName;

	@Column(name = "ATTACHMENT_CONTENT_TYPE", length = 160)
	private String contentType;

	@Column(name = "TENANT_ID", nullable = false, length = 64)
	private String tenantId;

	public SupplierFormItemAttachment copyFrom(SupplierFormItem newCqItem, String tenantId) {
		SupplierFormItemAttachment newAttach = new SupplierFormItemAttachment();
		newAttach.setFileData(getFileData());
		newAttach.setFileName(getFileName());
		newAttach.setContentType(getContentType());
		newAttach.setFormItem(newCqItem);
		newAttach.setTenantId(tenantId);
		return newAttach;
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
	 * @return the formItem
	 */
	public SupplierFormItem getFormItem() {
		return formItem;
	}

	/**
	 * @param formItem the formItem to set
	 */
	public void setFormItem(SupplierFormItem formItem) {
		this.formItem = formItem;
	}

	/**
	 * @return the fileData
	 */
	public byte[] getFileData() {
		return fileData;
	}

	/**
	 * @param fileData the fileData to set
	 */
	public void setFileData(byte[] fileData) {
		this.fileData = fileData;
	}

	/**
	 * @return the fileName
	 */
	public String getFileName() {
		return fileName;
	}

	/**
	 * @param fileName the fileName to set
	 */
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	/**
	 * @return the contentType
	 */
	public String getContentType() {
		return contentType;
	}

	/**
	 * @param contentType the contentType to set
	 */
	public void setContentType(String contentType) {
		this.contentType = contentType;
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

}
