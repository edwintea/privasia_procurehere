/**
 * 
 */
package com.privasia.procurehere.core.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.ColumnResult;
import javax.persistence.ConstructorResult;
import javax.persistence.Convert;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.SqlResultSetMapping;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.privasia.procurehere.core.enums.EnvelopType;
import com.privasia.procurehere.core.enums.EvaluationStatus;
import com.privasia.procurehere.core.enums.converter.EnvelopTypeConverter;
import com.privasia.procurehere.core.pojo.PendingEventPojo;
import com.privasia.procurehere.core.utils.CustomDateTimeSerializer;
import org.hibernate.annotations.Type;

/**
 * @author Ravi
 */
@MappedSuperclass
@SqlResultSetMapping(name = "myToDoResult", classes = { @ConstructorResult(targetClass = PendingEventPojo.class, columns = { @ColumnResult(name = "id"), @ColumnResult(name = "eventId"), @ColumnResult(name = "eventName"), @ColumnResult(name = "edate"), @ColumnResult(name = "referenceNumber"), @ColumnResult(name = "status"), @ColumnResult(name = "unitName"), @ColumnResult(name = "creatorName"), @ColumnResult(name = "type"), @ColumnResult(name = "urgentEvent", type = Boolean.class) }) })
public class Envelop implements Serializable {

	private static final long serialVersionUID = 7687393407274195475L;

	@Id
	@GenericGenerator(name = "idGen", strategy = "uuid.hex")
	@GeneratedValue(generator = "idGen")
	@Column(name = "ID", length = 64)
	private String id;

	@NotNull(message = "envelop.title.empty")
	@Size(min = 1, max = 160, message = "{envelop.title.length}")
	@Column(name = "ENVELOP_TITLE", length = 160, nullable = false)
	private String envelopTitle;

	@Column(name = "ENVELOP_SEQUENCE", nullable = true)
	private Integer envelopSequence;

	@Size(min = 0, max = 300, message = "{envelop.description.length}")
	@Column(name = "ENVELOP_DESCRIPTION", length = 300, nullable = true)
	private String description;

	@NotNull(message = "envelop.leadevaluater.empty")
	@ManyToOne(optional = false, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "LEAD_EVALUATER", nullable = false)
	private User leadEvaluater;

	@NotNull(message = "envelop.type.empty")
	@Convert(converter = EnvelopTypeConverter.class)
	@Column(name = "ENVELOP_TYPE", length = 64, nullable = false)
	private EnvelopType envelopType;

	@Column(name = "ENVELOP_STATUS")
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean isOpen = Boolean.FALSE;

	// @NotNull(message = "envelop.leadevaluater.empty")
	@ManyToOne(optional = true, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "ENVELOP_OPENER", nullable = true)
	private User opener;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "OPEN_DATE")
	private Date openDate;

	@Enumerated(EnumType.STRING)
	@Column(name = "EVALUATION_STATUS")
	private EvaluationStatus evaluationStatus;

	@Column(name = "EVALUATION_DATE", nullable = true)
	private Date evaluationDate;

	@Size(max = 5100)
	@Lob
	@Type(type = "org.hibernate.type.TextType")
	@Column(name = "LEAD_EVALUATOR_SUMMARY", nullable = false, length = 5100)
	private String leadEvaluatorSummary;

	@Column(name = "EVALUATOR_SUMMARY_DATE", nullable = true)
	private Date evaluatorSummaryDate;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CLOSE_DATE")
	private Date closeDate;

	@Column(name = "PRE_FIX", nullable = true, length = 10)
	private String preFix;

	@Column(name = "IS_EVAL_COMPLETE_PREMATURE")
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean evaluationCompletedPrematurely = Boolean.FALSE;

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
	@Type(type = "org.hibernate.type.NumericBooleanType")
private boolean showOpen;

	@Transient
	@Type(type = "org.hibernate.type.NumericBooleanType")
private boolean showView;

	@Transient
	@Type(type = "org.hibernate.type.NumericBooleanType")
private boolean showFinish;

	@Transient
	@Type(type = "org.hibernate.type.NumericBooleanType")
private boolean allowOpen;

	@Transient
	private String envelopOpenerName;

	@Transient
	private String envelopEvaluationOwner;

	@Transient
	private List<Bq> bqs;

	@Transient
	private List<Sor> sors;

	@Transient
	private List<Cq> cqs;

	@Transient
	private List<EvaluatorUser> evaluatorUser;

	@Transient
	private String evaluatorSummaryDateStr;

	@Transient
	@Type(type = "org.hibernate.type.NumericBooleanType")
private boolean permitToOpenClose = false;

	@Transient
	@Type(type = "org.hibernate.type.NumericBooleanType")
private boolean showEvaluationDeclaration = false;

	@Transient
	private String uploadDateStr;

	public boolean isPermitToOpenClose() {
		return permitToOpenClose;
	}

	public void setPermitToOpenClose(boolean permitToOpenClose) {
		this.permitToOpenClose = permitToOpenClose;
	}

	public Envelop() {
		this.evaluationStatus = EvaluationStatus.PENDING;
	}

	public Envelop(String id, String envelopTitle, User leadEvaluater, EnvelopType envelopType, Boolean isOpen) {
		super();
		this.id = id;
		this.envelopTitle = envelopTitle;
		this.leadEvaluater = leadEvaluater;
		this.envelopType = envelopType;
		this.isOpen = isOpen;
	}

	public Envelop createMobileShallowCopy() {
		Envelop env = new Envelop();
		env.setId(getId());
		env.setEnvelopTitle(getEnvelopTitle());
		env.setEnvelopType(getEnvelopType());
		env.setIsOpen(getIsOpen());
		env.setOpenDate(getOpenDate());
		env.setEvaluationStatus(getEvaluationStatus());
		env.setAllowOpen(isAllowOpen());
		env.setEvaluationCompletedPrematurely(getEvaluationCompletedPrematurely());
		return env;
	}

	/**
	 * @return the envelopSequence
	 */
	public Integer getEnvelopSequence() {
		return envelopSequence;
	}

	/**
	 * @param envelopSequence the envelopSequence to set
	 */
	public void setEnvelopSequence(Integer envelopSequence) {
		this.envelopSequence = envelopSequence;
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
	 * @return the envelopTitle
	 */
	public String getEnvelopTitle() {
		return envelopTitle;
	}

	/**
	 * @param envelopTitle the envelopTitle to set
	 */
	public void setEnvelopTitle(String envelopTitle) {
		this.envelopTitle = envelopTitle;
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
	 * @return the leadEvaluater
	 */
	public User getLeadEvaluater() {
		return leadEvaluater;
	}

	/**
	 * @param leadEvaluater the leadEvaluater to set
	 */
	public void setLeadEvaluater(User leadEvaluater) {
		this.leadEvaluater = leadEvaluater;
	}

	/**
	 * @return the envelopType
	 */
	public EnvelopType getEnvelopType() {
		return envelopType;
	}

	/**
	 * @param envelopType the envelopType to set
	 */
	public void setEnvelopType(EnvelopType envelopType) {
		this.envelopType = envelopType;
	}

	/**
	 * @return the isOpen
	 */
	public Boolean getIsOpen() {
		return isOpen;
	}

	/**
	 * @param isOpen the isOpen to set
	 */
	public void setIsOpen(Boolean isOpen) {
		this.isOpen = isOpen;
	}

	/**
	 * @return the opener
	 */
	public User getOpener() {
		return opener;
	}

	/**
	 * @param opener the opener to set
	 */
	public void setOpener(User opener) {
		this.opener = opener;
	}

	/**
	 * @return the openDate
	 */
	public Date getOpenDate() {
		return openDate;
	}

	/**
	 * @param openDate the openDate to set
	 */
	public void setOpenDate(Date openDate) {
		this.openDate = openDate;
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
	 * @return the showOpne
	 */
	public boolean isShowOpen() {
		return showOpen;
	}

	/**
	 * @param showOpne the showOpne to set
	 */
	public void setShowOpen(boolean showOpen) {
		this.showOpen = showOpen;
	}

	/**
	 * @return the showView
	 */
	public boolean isShowView() {
		return showView;
	}

	/**
	 * @param showView the showView to set
	 */
	public void setShowView(boolean showView) {
		this.showView = showView;
	}

	/**
	 * @return the showFinish
	 */
	public boolean isShowFinish() {
		return showFinish;
	}

	/**
	 * @param showFinish the showFinish to set
	 */
	public void setShowFinish(boolean showFinish) {
		this.showFinish = showFinish;
	}

	/**
	 * @return the allowOpen
	 */
	public boolean isAllowOpen() {
		return allowOpen;
	}

	/**
	 * @param allowOpen the allowOpen to set
	 */
	public void setAllowOpen(boolean allowOpen) {
		this.allowOpen = allowOpen;
	}

	/**
	 * @return the envelopOpenerName
	 */
	public String getEnvelopOpenerName() {
		return envelopOpenerName;
	}

	/**
	 * @param envelopOpenerName the envelopOpenerName to set
	 */
	public void setEnvelopOpenerName(String envelopOpenerName) {
		this.envelopOpenerName = envelopOpenerName;
	}

	/**
	 * @return the envelopEvaluationOwner
	 */
	public String getEnvelopEvaluationOwner() {
		return envelopEvaluationOwner;
	}

	/**
	 * @param envelopEvaluationOwner the envelopEvaluationOwner to set
	 */
	public void setEnvelopEvaluationOwner(String envelopEvaluationOwner) {
		this.envelopEvaluationOwner = envelopEvaluationOwner;
	}

	/**
	 * @return the bqs
	 */
	public List<Bq> getBqs() {
		return bqs;
	}

	/**
	 * @param bqs the bqs to set
	 */
	public void setBqs(List<Bq> bqs) {
		this.bqs = bqs;
	}

	/**
	 * @return the cqs
	 */
	public List<Cq> getCqs() {
		return cqs;
	}

	/**
	 * @param cqs the cqs to set
	 */
	public void setCqs(List<Cq> cqs) {
		this.cqs = cqs;
	}

	/**
	 * @return the evaluatorUser
	 */
	public List<EvaluatorUser> getEvaluatorUser() {
		return evaluatorUser;
	}

	/**
	 * @param evaluatorUser the evaluatorUser to set
	 */
	public void setEvaluatorUser(List<EvaluatorUser> evaluatorUser) {
		this.evaluatorUser = evaluatorUser;
	}

	public String getLeadEvaluatorSummary() {
		return leadEvaluatorSummary;
	}

	public void setLeadEvaluatorSummary(String leadEvaluatorSummary) {
		this.leadEvaluatorSummary = leadEvaluatorSummary;
	}

	public Date getCloseDate() {
		return closeDate;
	}

	public void setCloseDate(Date closeDate) {
		this.closeDate = closeDate;
	}

	/**
	 * @return the evaluationCompletedPrematurely
	 */
	public Boolean getEvaluationCompletedPrematurely() {
		return evaluationCompletedPrematurely;
	}

	/**
	 * @param evaluationCompletedPrematurely the evaluationCompletedPrematurely to set
	 */
	public void setEvaluationCompletedPrematurely(Boolean evaluationCompletedPrematurely) {
		this.evaluationCompletedPrematurely = evaluationCompletedPrematurely;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((envelopTitle == null) ? 0 : envelopTitle.hashCode());
		result = prime * result + ((envelopType == null) ? 0 : envelopType.hashCode());
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
		Envelop other = (Envelop) obj;
		if (envelopTitle == null) {
			if (other.envelopTitle != null)
				return false;
		} else if (!envelopTitle.equals(other.envelopTitle))
			return false;
		if (envelopType != other.envelopType)
			return false;
		return true;
	}

	public String toLogString() {
		return "envelopTitle=" + envelopTitle + ", envelopType=" + envelopType;
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
	 * @return the evaluatorSummaryDateStr
	 */
	public String getEvaluatorSummaryDateStr() {
		return evaluatorSummaryDateStr;
	}

	/**
	 * @param evaluatorSummaryDateStr the evaluatorSummaryDateStr to set
	 */
	public void setEvaluatorSummaryDateStr(String evaluatorSummaryDateStr) {
		this.evaluatorSummaryDateStr = evaluatorSummaryDateStr;
	}

	/**
	 * @return the preFix
	 */
	public String getPreFix() {
		return preFix;
	}

	/**
	 * @param preFix the preFix to set
	 */
	public void setPreFix(String preFix) {
		this.preFix = preFix;
	}

	/**
	 * @return the showEvaluationDeclaration
	 */
	public boolean isShowEvaluationDeclaration() {
		return showEvaluationDeclaration;
	}

	/**
	 * @param showEvaluationDeclaration the showEvaluationDeclaration to set
	 */
	public void setShowEvaluationDeclaration(boolean showEvaluationDeclaration) {
		this.showEvaluationDeclaration = showEvaluationDeclaration;
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

	public String getUploadDateStr() {
		return uploadDateStr;
	}

	public void setUploadDateStr(String uploadDateStr) {
		this.uploadDateStr = uploadDateStr;
	}


	public List<Sor> getSors() {
		return sors;
	}

	public void setSors(List<Sor> sors) {
		this.sors = sors;
	}
}
