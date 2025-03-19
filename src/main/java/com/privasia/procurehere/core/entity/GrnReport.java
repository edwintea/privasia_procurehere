package com.privasia.procurehere.core.entity;

import java.io.Serializable;
import java.util.Arrays;

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
 * @author pooja
 */

@Entity
@Table(name = "PROC_GRN_REPORT")
public class GrnReport implements Serializable {

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

	@Column(name = "GRN_NUMBER", length = 64)
	private String grnId;

	@Column(name = "TENANT_ID", nullable = false, length = 64)
	private String tenantId;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "GRN_ID", nullable = false, foreignKey = @ForeignKey(name = "FK_REP_GRN_ID"))
	private GoodsReceiptNote goodsReceiptNote;

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
	 * @return the grnId
	 */
	public String getGrnId() {
		return grnId;
	}

	/**
	 * @param grnId the grnId to set
	 */
	public void setGrnId(String grnId) {
		this.grnId = grnId;
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
	 * @return the goodsReceiptNote
	 */
	public GoodsReceiptNote getGoodsReceiptNote() {
		return goodsReceiptNote;
	}

	/**
	 * @param goodsReceiptNote the goodsReceiptNote to set
	 */
	public void setGoodsReceiptNote(GoodsReceiptNote goodsReceiptNote) {
		this.goodsReceiptNote = goodsReceiptNote;
	}

	@Override
	public String toString() {
		return "GrnReport [id=" + id + ", fileData=" + Arrays.toString(fileData) + ", fileName=" + fileName + ", grnId=" + grnId + ", tenantId=" + tenantId + ", goodsReceiptNote=" + goodsReceiptNote + "]";
	}

}
