package com.privasia.procurehere.core.pojo;

import java.io.Serializable;
import java.util.List;

/**
 * @author pooja
 */
public class EvaluationConclusionPojo implements Serializable {

	private static final long serialVersionUID = 9036511126477335260L;

	private String id;
	private String eventName;
	private String eventId;
	private String referenceNo;
	private String createdDate;
	private String type;
	private String eventOwner;

	private String eventStart;
	private String eventEnd;
	private String publishDate;

	private Integer envelopeEvaluatedCount;

	private Integer envelopeNonEvaluatedCount;

	private Integer disqualifiedSupplierCount;

	private Integer remainingSupplierCount;

	private String evaluationConclusionOwners;

	private String envelopEvaluted;

	private String envelopNonEvaluted;

	private List<EvaluationConclusionUsersPojo> evaluationConclusionUsersList;

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
	 * @return the eventName
	 */
	public String getEventName() {
		return eventName;
	}

	/**
	 * @param eventName the eventName to set
	 */
	public void setEventName(String eventName) {
		this.eventName = eventName;
	}

	/**
	 * @return the eventId
	 */
	public String getEventId() {
		return eventId;
	}

	/**
	 * @param eventId the eventId to set
	 */
	public void setEventId(String eventId) {
		this.eventId = eventId;
	}

	/**
	 * @return the referenceNo
	 */
	public String getReferenceNo() {
		return referenceNo;
	}

	/**
	 * @param referenceNo the referenceNo to set
	 */
	public void setReferenceNo(String referenceNo) {
		this.referenceNo = referenceNo;
	}

	/**
	 * @return the createdDate
	 */
	public String getCreatedDate() {
		return createdDate;
	}

	/**
	 * @param createdDate the createdDate to set
	 */
	public void setCreatedDate(String createdDate) {
		this.createdDate = createdDate;
	}

	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * @return the eventOwner
	 */
	public String getEventOwner() {
		return eventOwner;
	}

	/**
	 * @param eventOwner the eventOwner to set
	 */
	public void setEventOwner(String eventOwner) {
		this.eventOwner = eventOwner;
	}

	/**
	 * @return the eventStart
	 */
	public String getEventStart() {
		return eventStart;
	}

	/**
	 * @param eventStart the eventStart to set
	 */
	public void setEventStart(String eventStart) {
		this.eventStart = eventStart;
	}

	/**
	 * @return the eventEnd
	 */
	public String getEventEnd() {
		return eventEnd;
	}

	/**
	 * @param eventEnd the eventEnd to set
	 */
	public void setEventEnd(String eventEnd) {
		this.eventEnd = eventEnd;
	}

	/**
	 * @return the publishDate
	 */
	public String getPublishDate() {
		return publishDate;
	}

	/**
	 * @param publishDate the publishDate to set
	 */
	public void setPublishDate(String publishDate) {
		this.publishDate = publishDate;
	}

	/**
	 * @return the envelopeEvaluatedCount
	 */
	public Integer getEnvelopeEvaluatedCount() {
		return envelopeEvaluatedCount;
	}

	/**
	 * @param envelopeEvaluatedCount the envelopeEvaluatedCount to set
	 */
	public void setEnvelopeEvaluatedCount(Integer envelopeEvaluatedCount) {
		this.envelopeEvaluatedCount = envelopeEvaluatedCount;
	}

	/**
	 * @return the envelopeNonEvaluatedCount
	 */
	public Integer getEnvelopeNonEvaluatedCount() {
		return envelopeNonEvaluatedCount;
	}

	/**
	 * @param envelopeNonEvaluatedCount the envelopeNonEvaluatedCount to set
	 */
	public void setEnvelopeNonEvaluatedCount(Integer envelopeNonEvaluatedCount) {
		this.envelopeNonEvaluatedCount = envelopeNonEvaluatedCount;
	}

	/**
	 * @return the disqualifiedSupplierCount
	 */
	public Integer getDisqualifiedSupplierCount() {
		return disqualifiedSupplierCount;
	}

	/**
	 * @param disqualifiedSupplierCount the disqualifiedSupplierCount to set
	 */
	public void setDisqualifiedSupplierCount(Integer disqualifiedSupplierCount) {
		this.disqualifiedSupplierCount = disqualifiedSupplierCount;
	}

	/**
	 * @return the remainingSupplierCount
	 */
	public Integer getRemainingSupplierCount() {
		return remainingSupplierCount;
	}

	/**
	 * @param remainingSupplierCount the remainingSupplierCount to set
	 */
	public void setRemainingSupplierCount(Integer remainingSupplierCount) {
		this.remainingSupplierCount = remainingSupplierCount;
	}

	/**
	 * @return the evaluationConclusionOwners
	 */
	public String getEvaluationConclusionOwners() {
		return evaluationConclusionOwners;
	}

	/**
	 * @param evaluationConclusionOwners the evaluationConclusionOwners to set
	 */
	public void setEvaluationConclusionOwners(String evaluationConclusionOwners) {
		this.evaluationConclusionOwners = evaluationConclusionOwners;
	}

	/**
	 * @return the envelopEvaluted
	 */
	public String getEnvelopEvaluted() {
		return envelopEvaluted;
	}

	/**
	 * @param envelopEvaluted the envelopEvaluted to set
	 */
	public void setEnvelopEvaluted(String envelopEvaluted) {
		this.envelopEvaluted = envelopEvaluted;
	}

	/**
	 * @return the envelopNonEvaluted
	 */
	public String getEnvelopNonEvaluted() {
		return envelopNonEvaluted;
	}

	/**
	 * @param envelopNonEvaluted the envelopNonEvaluted to set
	 */
	public void setEnvelopNonEvaluted(String envelopNonEvaluted) {
		this.envelopNonEvaluted = envelopNonEvaluted;
	}

	/**
	 * @return the evaluationConclusionUsersList
	 */
	public List<EvaluationConclusionUsersPojo> getEvaluationConclusionUsersList() {
		return evaluationConclusionUsersList;
	}

	/**
	 * @param evaluationConclusionUsersList the evaluationConclusionUsersList to set
	 */
	public void setEvaluationConclusionUsersList(List<EvaluationConclusionUsersPojo> evaluationConclusionUsersList) {
		this.evaluationConclusionUsersList = evaluationConclusionUsersList;
	}

}