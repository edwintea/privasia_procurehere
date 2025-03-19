package com.privasia.procurehere.core.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

/**
 * @author Nitin Otageri
 */

@MappedSuperclass
public class RfxEvaluationConclusionUser implements Serializable {

	private static final long serialVersionUID = 8268552234489411545L;

	@Id
	@GenericGenerator(name = "idGen", strategy = "uuid.hex")
	@GeneratedValue(generator = "idGen")
	@Column(name = "ID", length = 64)
	private String id;

	@ManyToOne(fetch = FetchType.LAZY, cascade = { CascadeType.REFRESH }, optional = false)
	@JoinColumn(name = "USER_ID", nullable = false)
	private User user;

	@Column(name = "IS_CONCLUDED", nullable = false)
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean concluded = Boolean.FALSE;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CONCLUDED_TIME", nullable = true)
	private Date concludedTime;

	@Column(name = "REMARKS", nullable = true, length = 1024)
	private String remarks;

	@Lob
	@Type(type = "org.hibernate.type.BinaryType")
	@Basic(fetch = FetchType.LAZY)
	@Column(name = "FILE_DATA")
	private byte[] fileData;

	@Column(name = "FILE_NAME", length = 500)
	private String fileName;

	@Column(name = "FILE_DESC", length = 500)
	private String fileDesc;

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
	 * @return the user
	 */
	public User getUser() {
		return user;
	}

	/**
	 * @param user the user to set
	 */
	public void setUser(User user) {
		this.user = user;
	}

	/**
	 * @return the concluded
	 */
	public Boolean getConcluded() {
		return concluded;
	}

	/**
	 * @param concluded the concluded to set
	 */
	public void setConcluded(Boolean concluded) {
		this.concluded = concluded;
	}

	/**
	 * @return the concludedTime
	 */
	public Date getConcludedTime() {
		return concludedTime;
	}

	/**
	 * @param concludedTime the concludedTime to set
	 */
	public void setConcludedTime(Date concludedTime) {
		this.concludedTime = concludedTime;
	}

	/**
	 * @return the remarks
	 */
	public String getRemarks() {
		return remarks;
	}

	/**
	 * @param remarks the remarks to set
	 */
	public void setRemarks(String remarks) {
		this.remarks = remarks;
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
	 * @return the fileDesc
	 */
	public String getFileDesc() {
		return fileDesc;
	}

	/**
	 * @param fileDesc the fileDesc to set
	 */
	public void setFileDesc(String fileDesc) {
		this.fileDesc = fileDesc;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((user == null) ? 0 : user.hashCode());
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
		RfxEvaluationConclusionUser other = (RfxEvaluationConclusionUser) obj;
		if (user == null) {
			if (other.user != null)
				return false;
		} else if (!user.equals(other.user))
			return false;
		return true;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */

	public String toLogString() {
		try {
			return "RfxEvaluationConclusionUser [id=" + id + ", user=" + user.getId() + ", concluded=" + concluded + ", concludedTime=" + concludedTime + ", remarks=" + remarks + "]";
		} catch (Exception e) {
			return "RfxEvaluationConclusionUser [id=" + id + ", concluded=" + concluded + ", concludedTime=" + concludedTime + ", remarks=" + remarks + "]";
		}
	}

}
