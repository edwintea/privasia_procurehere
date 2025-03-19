package com.privasia.procurehere.core.pojo;

import java.io.Serializable;
import java.util.List;

import com.privasia.procurehere.core.entity.AuctionRules;
import com.privasia.procurehere.core.entity.Bq;
import com.privasia.procurehere.core.entity.Comments;
import com.privasia.procurehere.core.entity.Envelop;
import com.privasia.procurehere.core.entity.Event;
import com.privasia.procurehere.core.entity.EventApproval;
import com.privasia.procurehere.core.entity.EventDocument;
import com.privasia.procurehere.core.entity.EventSupplier;
import com.privasia.procurehere.core.enums.EventStatus;

/**
 * @author parveen
 */
public class MobileEventPojo implements Serializable {

	private static final long serialVersionUID = -5633294462530701915L;

	private String id;

	private String eventId;

	private String referenceNumber;

	private String eventName;

	private String eventDescription;

	private String eventOwner;

	private String eventType;

	private EventStatus status;

	private String decimal;

	private String currency;

	private String currencyName;

	private List<EventSupplier> suppliers;

	private List<Bq> bqs;

	private List<EventTimelinePojo> timeLine;

	private List<EventDocument> documents;

	private List<Envelop> envelops;

	private List<EventApproval> approvers;

	private List<Comments> comments;

	private AuctionRules auctionRules;

	private boolean hasReviseBid;

	private String templateName;

	private String businessUnit;

	private String costCenter;

	public String getBusinessUnit() {
		return businessUnit;
	}

	public void setBusinessUnit(String businessUnit) {
		this.businessUnit = businessUnit;
	}

	public String getCostCenter() {
		return costCenter;
	}

	public void setCostCenter(String costCenter) {
		this.costCenter = costCenter;
	}

	public MobileEventPojo() {

	}

	public MobileEventPojo(String id, String eventId, String eventType, String status) {
		this.id = id;
		this.eventId = eventId;
		this.eventType = eventType;
		this.status = EventStatus.fromString(status);
	}

	public MobileEventPojo(Event event) {

		this.id = event.getId();
		this.eventId = event.getEventId();
		this.referenceNumber = event.getReferanceNumber();
		this.eventName = event.getEventName();
		if (event.getEventOwner() != null) {
			this.eventOwner = event.getEventOwner().getName();
		}

		if (event.getBaseCurrency() != null) {
			this.currency = event.getBaseCurrency().getCurrencyCode();
			this.currencyName = event.getBaseCurrency() != null ? event.getBaseCurrency().getCurrencyName() : null;
		}
		this.status = event.getStatus();
		this.eventDescription = event.getEventDescription();
		this.decimal = event.getDecimal();
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
	 * @return the referenceNumber
	 */
	public String getReferenceNumber() {
		return referenceNumber;
	}

	/**
	 * @param referenceNumber the referenceNumber to set
	 */
	public void setReferenceNumber(String referenceNumber) {
		this.referenceNumber = referenceNumber;
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
	 * @return the currency
	 */
	public String getCurrency() {
		return currency;
	}

	/**
	 * @param currency the currency to set
	 */
	public void setCurrency(String currency) {
		this.currency = currency;
	}

	/**
	 * @return the eventType
	 */
	public String getEventType() {
		return eventType;
	}

	/**
	 * @param eventType the eventType to set
	 */
	public void setEventType(String eventType) {
		this.eventType = eventType;
	}

	/**
	 * @return the suppliers
	 */
	public List<EventSupplier> getSuppliers() {
		return suppliers;
	}

	/**
	 * @param suppliers the suppliers to set
	 */
	public void setSuppliers(List<EventSupplier> suppliers) {
		this.suppliers = suppliers;
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
	 * @return the timeLine
	 */

	/**
	 * @return the documents
	 */
	public List<EventDocument> getDocuments() {
		return documents;
	}

	public List<EventTimelinePojo> getTimeLine() {
		return timeLine;
	}

	public void setTimeLine(List<EventTimelinePojo> timeLine) {
		this.timeLine = timeLine;
	}

	/**
	 * @param documents the documents to set
	 */
	public void setDocuments(List<EventDocument> documents) {
		this.documents = documents;
	}

	/**
	 * @return the envelops
	 */
	public List<Envelop> getEnvelops() {
		return envelops;
	}

	/**
	 * @param envelops the envelops to set
	 */
	public void setEnvelops(List<Envelop> envelops) {
		this.envelops = envelops;
	}

	/**
	 * @return the status
	 */
	public EventStatus getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(EventStatus status) {
		this.status = status;
	}

	/**
	 * @return the eventDescription
	 */
	public String getEventDescription() {
		return eventDescription;
	}

	/**
	 * @param eventDescription the eventDescription to set
	 */
	public void setEventDescription(String eventDescription) {
		this.eventDescription = eventDescription;
	}

	/**
	 * @return the decimal
	 */
	public String getDecimal() {
		return decimal;
	}

	/**
	 * @param decimal the decimal to set
	 */
	public void setDecimal(String decimal) {
		this.decimal = decimal;
	}

	/**
	 * @return the approvers
	 */
	public List<EventApproval> getApprovers() {
		return approvers;
	}

	/**
	 * @param approvers the approvers to set
	 */
	public void setApprovers(List<EventApproval> approvers) {
		this.approvers = approvers;
	}

	/**
	 * @return the comments
	 */
	public List<Comments> getComments() {
		return comments;
	}

	/**
	 * @param comments the comments to set
	 */
	public void setComments(List<Comments> comments) {
		this.comments = comments;
	}

	/**
	 * @return the auctionRules
	 */
	public AuctionRules getAuctionRules() {
		return auctionRules;
	}

	/**
	 * @param auctionRules the auctionRules to set
	 */
	public void setAuctionRules(AuctionRules auctionRules) {
		this.auctionRules = auctionRules;
	}

	/**
	 * @return the hasReviseBid
	 */
	public boolean isHasReviseBid() {
		return hasReviseBid;
	}

	/**
	 * @param hasReviseBid the hasReviseBid to set
	 */
	public void setHasReviseBid(boolean hasReviseBid) {
		this.hasReviseBid = hasReviseBid;
	}

	/**
	 * @return the currencyName
	 */
	public String getCurrencyName() {
		return currencyName;
	}

	/**
	 * @param currencyName the currencyName to set
	 */
	public void setCurrencyName(String currencyName) {
		this.currencyName = currencyName;
	}

	/**
	 * @return the templateName
	 */
	public String getTemplateName() {
		return templateName;
	}

	/**
	 * @param templateName the templateName to set
	 */
	public void setTemplateName(String templateName) {
		this.templateName = templateName;
	}

}