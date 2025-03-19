package com.privasia.procurehere.core.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

/**
 * @author sana
 */

@Entity
@Table(name = "PROC_DO_REPORT")
public class DoReport implements Serializable {

	private static final long serialVersionUID = 8823771425328753640L;

	@Id
	@GenericGenerator(name = "idGen", strategy = "uuid.hex")
	@GeneratedValue(generator = "idGen")
	@Column(name = "ID", length = 64)
	private String id;

	@Lob
	@Type(type = "org.hibernate.type.BinaryType")
	@Column(name = "FILE_DATA")
	private byte[] fileData;

	@Column(name = "FILE_NAME", length = 500)
	private String fileName;

	@Column(name = "DO_NUMBER", length = 64)
	private String doNumber;

	@Column(name = "TENANT_ID", nullable = false, length = 64)
	private String tenantId;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "DO_ID", nullable = false, foreignKey = @ForeignKey(name = "FK_REP_DO_ID"))
	private DeliveryOrder deliveryOrder;

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
	 * @return the doNumber
	 */
	public String getDoNumber() {
		return doNumber;
	}

	/**
	 * @param doNumber the doNumber to set
	 */
	public void setDoNumber(String doNumber) {
		this.doNumber = doNumber;
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
	 * @return the deliveryOrder
	 */
	public DeliveryOrder getDeliveryOrder() {
		return deliveryOrder;
	}

	/**
	 * @param deliveryOrder the deliveryOrder to set
	 */
	public void setDeliveryOrder(DeliveryOrder deliveryOrder) {
		this.deliveryOrder = deliveryOrder;
	}

	@Override
	public String toString() {
		return "DoReport [id=" + id + ", fileName=" + fileName + ", doNumber=" + doNumber + ", tenantId=" + tenantId + ", deliveryOrder=" + deliveryOrder + "]";
	}

}
