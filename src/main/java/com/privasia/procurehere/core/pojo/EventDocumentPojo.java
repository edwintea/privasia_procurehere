package com.privasia.procurehere.core.pojo;

import java.io.Serializable;
import java.util.Arrays;

import com.privasia.procurehere.core.entity.EventMeetingDocument;

public class EventDocumentPojo implements Serializable {

	private static final long serialVersionUID = -8285449946737572690L;

	private String id;

	private byte[] fileData;

	private String fileName;

	private String credContentType;

	private String tenantId;

	private int fileSize;

	private String refId;

	public EventDocumentPojo() {
	}

	public EventDocumentPojo(EventMeetingDocument document) {
		this.setId(document.getId());
		this.setFileData(document.getFileData());
		this.setCredContentType(document.getCredContentType());
		this.setFileName(document.getFileName());
		this.setFileSize((document.getFileSizeInKb() != null) ? document.getFileSizeInKb() : 0);
		this.setRefId(document.getRefId());
		this.setTenantId(document.getTenantId());
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
	 * @return the credContentType
	 */
	public String getCredContentType() {
		return credContentType;
	}

	/**
	 * @param credContentType the credContentType to set
	 */
	public void setCredContentType(String credContentType) {
		this.credContentType = credContentType;
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
	 * @return the fileSize
	 */
	public int getFileSize() {
		return fileSize;
	}

	/**
	 * @param fileSize the fileSize to set
	 */
	public void setFileSize(int fileSize) {
		this.fileSize = fileSize;
	}

	/**
	 * @return the refId
	 */
	public String getRefId() {
		return refId;
	}

	/**
	 * @param refId the refId to set
	 */
	public void setRefId(String refId) {
		this.refId = refId;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((credContentType == null) ? 0 : credContentType.hashCode());
		result = prime * result + ((fileName == null) ? 0 : fileName.hashCode());
		result = prime * result + ((tenantId == null) ? 0 : tenantId.hashCode());
		result = prime * result + ((refId == null) ? 0 : refId.hashCode());
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
		EventDocumentPojo other = (EventDocumentPojo) obj;
		if (credContentType == null) {
			if (other.credContentType != null)
				return false;
		} else if (!credContentType.equals(other.credContentType))
			return false;
		if (fileName == null) {
			if (other.fileName != null)
				return false;
		} else if (!fileName.equals(other.fileName))
			return false;
		if (tenantId == null) {
			if (other.tenantId != null)
				return false;
		} else if (!tenantId.equals(other.tenantId))
			return false;
		if (refId == null) {
			if (other.refId != null)
				return false;
		} else if (!refId.equals(other.refId))
			return false;
		return true;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "EventDocumentPojo [id=" + id + ", fileData=" + Arrays.toString(fileData) + ", fileName=" + fileName + ", credContentType=" + credContentType + ", tenantId=" + tenantId + ", refId=" + refId + "]";
	}

}