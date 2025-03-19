package com.privasia.procurehere.core.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.privasia.procurehere.core.enums.EvaluationStatus;
import com.privasia.procurehere.core.utils.CustomDateTimeSerializer;
import org.hibernate.annotations.Type;

@MappedSuperclass
public class EvaluatorUser {

	@Id
	@GenericGenerator(name = "idGen", strategy = "uuid.hex")
	@GeneratedValue(generator = "idGen")
	@Column(name = "ID", length = 64)
	protected String id;

	@Enumerated(EnumType.STRING)
	@Column(name = "EVALUATION_STATUS")
	private EvaluationStatus evaluationStatus;

	@Column(name = "EVALUATION_DATE", nullable = true)
	private Date evaluationDate;

	@Size(max = 5100)
	@Lob
	@Type(type = "org.hibernate.type.TextType")
	@Column(name = "EVALUATOR_SUMMARY", nullable = false, length = 5100)
	private String evaluatorSummary;

	@Column(name = "EVALUATOR_SUMMARY_DATE", nullable = true)
	private Date evaluatorSummaryDate;

	@Lob
	@Type(type = "org.hibernate.type.BinaryType")
	@Column(name = "FILE_DATA")
	private byte[] fileData;

	@Column(name = "FILE_NAME", length = 500)
	private String fileName;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "UPLOAD_DATE")
	private Date uploadDate;

	@Column(name = "CONTENT_TYPE", length = 160)
	private String credContentType;

	@Column(name = "FILE_SIZE", length = 10)
	private Integer fileSizeInKb;

	@Transient
	private String summaryDate;

	@Transient
	private User evalUser;

	public EvaluatorUser() {
		this.evaluationStatus = EvaluationStatus.PENDING;
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
	 * @return the evaluationStatus
	 */
	public EvaluationStatus getEvaluationStatus() {
		return evaluationStatus;
	}

	/**
	 * @param evaluationStatus the evaluationStatus to set
	 */
	public void setEvaluationStatus(EvaluationStatus evaluationStatus) {
		this.evaluationStatus = evaluationStatus;
	}

	/**
	 * @return the evaluationDate
	 */
	public Date getEvaluationDate() {
		return evaluationDate;
	}

	/**
	 * @param evaluationDate the evaluationDate to set
	 */
	public void setEvaluationDate(Date evaluationDate) {
		this.evaluationDate = evaluationDate;
	}

	/**
	 * @return the evalUser
	 */
	public User getEvalUser() {
		return evalUser;
	}

	/**
	 * @param evalUser the evalUser to set
	 */
	public void setEvalUser(User evalUser) {
		this.evalUser = evalUser;
	}

	public String getEvaluatorSummary() {
		return evaluatorSummary;
	}

	public void setEvaluatorSummary(String evaluatorSummary) {
		this.evaluatorSummary = evaluatorSummary;
	}

	/**
	 * @return the evaluatorSummaryDate
	 */
	public Date getEvaluatorSummaryDate() {
		return evaluatorSummaryDate;
	}

	/**
	 * @param evaluatorSummaryDate the evaluatorSummaryDate to set
	 */
	public void setEvaluatorSummaryDate(Date evaluatorSummaryDate) {
		this.evaluatorSummaryDate = evaluatorSummaryDate;
	}

	/**
	 * @return the summaryDate
	 */
	public String getSummaryDate() {
		return summaryDate;
	}

	/**
	 * @param summaryDate the summaryDate to set
	 */
	public void setSummaryDate(String summaryDate) {
		this.summaryDate = summaryDate;
	}

	public byte[] getFileData() {
		return fileData;
	}

	public void setFileData(byte[] fileData) {
		this.fileData = fileData;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public Date getUploadDate() {
		return uploadDate;
	}

	public void setUploadDate(Date uploadDate) {
		this.uploadDate = uploadDate;
	}

	public String getCredContentType() {
		return credContentType;
	}

	public void setCredContentType(String credContentType) {
		this.credContentType = credContentType;
	}

	public Integer getFileSizeInKb() {
		return fileSizeInKb;
	}

	public void setFileSizeInKb(Integer fileSizeInKb) {
		this.fileSizeInKb = fileSizeInKb;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((evaluationDate == null) ? 0 : evaluationDate.hashCode());
		result = prime * result + ((evaluationStatus == null) ? 0 : evaluationStatus.hashCode());
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
		EvaluatorUser other = (EvaluatorUser) obj;
		if (evaluationDate == null) {
			if (other.evaluationDate != null)
				return false;
		} else if (!evaluationDate.equals(other.evaluationDate))
			return false;
		if (evaluationStatus != other.evaluationStatus)
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	public String toLogString() {
		return "EvaluatorUser [id=" + id + ", evaluationStatus=" + evaluationStatus + ", evaluationDate=" + evaluationDate + "]";
	}

}