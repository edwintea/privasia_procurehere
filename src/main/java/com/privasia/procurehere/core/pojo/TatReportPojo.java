/**
 * 
 */
package com.privasia.procurehere.core.pojo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.privasia.procurehere.core.enums.EventStatus;
import com.privasia.procurehere.core.enums.SourcingFormStatus;
import com.privasia.procurehere.core.utils.CustomDateTimeSerializer;

/**
 * @author jayshree
 */
public class TatReportPojo implements Serializable {

	private static final long serialVersionUID = -6942425079636229785L;

	private String id;

	private String formId;

	private SourcingFormStatus requestStatus;

	private String eventId;

	private EventStatus status;

	private Date eventStart;

	private Date eventEnd;

	private String tenantId;

	private String userId;

	private SourcingFormStatus requeststatus = SourcingFormStatus.FINISHED;

	private String eventid;

	private EventStatus eventstatus;

	private String sourcingrequestid;

	private String awardedSupplier;

	private String sapPoId;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	private Date lastApprovedDate;

	private BigDecimal paperApprovalDaysCount;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	private Date eventFirstApprovedDate;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	private Date eventLastApprovedDate;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	private Date eventFinishDate;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	private Date evaluationCompletedDate;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	private Date eventFirstMeetingDate;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	private Date firstEnvelopOpenDate;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	private Date finishDate;

	public TatReportPojo() {

	}

	public TatReportPojo(String id) {
		this.id = id;
	}

	public TatReportPojo(String id, Date firstEnvelopOpenDate) {
		this.id = id;
		this.firstEnvelopOpenDate = firstEnvelopOpenDate;
	}

	public TatReportPojo(String id, Date evaluationCompletedDate, Date eventFirstMeetingDate) {
		this.id = id;
		this.evaluationCompletedDate = evaluationCompletedDate;
		this.eventFirstMeetingDate = eventFirstMeetingDate;
	}

	public TatReportPojo(String id, Date finishDate, SourcingFormStatus requestStatus) {
		this.id = id;
		this.finishDate = finishDate;
		this.requestStatus = requestStatus;
	}

	public TatReportPojo(String id, String formId, String eventId, String tenantId, String sourcingrequestid, String awardedSupplier, String sapPoId, Date lastApprovedDate, BigDecimal paperApprovalDaysCount, Date eventFirstApprovedDate, Date eventLastApprovedDate, Date eventFinishDate) {
		super();
		this.id = id;
		this.formId = formId;
		this.eventId = eventId;
		this.tenantId = tenantId;
		this.sourcingrequestid = sourcingrequestid;
		this.awardedSupplier = awardedSupplier;
		this.sapPoId = sapPoId;
		this.lastApprovedDate = lastApprovedDate;
		this.paperApprovalDaysCount = paperApprovalDaysCount;
		this.eventFirstApprovedDate = eventFirstApprovedDate;
		this.eventLastApprovedDate = eventLastApprovedDate;
		this.eventFinishDate = eventFinishDate;
	}

	public TatReportPojo(String id, Date eventStart, Date eventEnd, EventStatus eventstatus) {
		this.id = id;
		this.eventStart = eventStart;
		this.eventEnd = eventEnd;
		this.eventstatus =eventstatus;
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
	 * @return the formId
	 */
	public String getFormId() {
		return formId;
	}

	/**
	 * @param formId the formId to set
	 */
	public void setFormId(String formId) {
		this.formId = formId;
	}

	/**
	 * @return the requestStatus
	 */
	public SourcingFormStatus getRequestStatus() {
		return requestStatus;
	}

	/**
	 * @param requestStatus the requestStatus to set
	 */
	public void setRequestStatus(SourcingFormStatus requestStatus) {
		this.requestStatus = requestStatus;
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
	 * @return the eventStart
	 */
	public Date getEventStart() {
		return eventStart;
	}

	/**
	 * @param eventStart the eventStart to set
	 */
	public void setEventStart(Date eventStart) {
		this.eventStart = eventStart;
	}

	/**
	 * @return the eventEnd
	 */
	public Date getEventEnd() {
		return eventEnd;
	}

	/**
	 * @param eventEnd the eventEnd to set
	 */
	public void setEventEnd(Date eventEnd) {
		this.eventEnd = eventEnd;
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
	 * @return the userId
	 */
	public String getUserId() {
		return userId;
	}

	/**
	 * @param userId the userId to set
	 */
	public void setUserId(String userId) {
		this.userId = userId;
	}

	/**
	 * @return the requeststatus
	 */
	public SourcingFormStatus getRequeststatus() {
		return requeststatus;
	}

	/**
	 * @param requeststatus the requeststatus to set
	 */
	public void setRequeststatus(SourcingFormStatus requeststatus) {
		this.requeststatus = requeststatus;
	}

	/**
	 * @return the eventid
	 */
	public String getEventid() {
		return eventid;
	}

	/**
	 * @param eventid the eventid to set
	 */
	public void setEventid(String eventid) {
		this.eventid = eventid;
	}

	/**
	 * @return the eventstatus
	 */
	public EventStatus getEventstatus() {
		return eventstatus;
	}

	/**
	 * @param eventstatus the eventstatus to set
	 */
	public void setEventstatus(EventStatus eventstatus) {
		this.eventstatus = eventstatus;
	}

	/**
	 * @return the sourcingrequestid
	 */
	public String getSourcingrequestid() {
		return sourcingrequestid;
	}

	/**
	 * @param sourcingrequestid the sourcingrequestid to set
	 */
	public void setSourcingrequestid(String sourcingrequestid) {
		this.sourcingrequestid = sourcingrequestid;
	}

	public String getAwardedSupplier() {
		return awardedSupplier;
	}

	public void setAwardedSupplier(String awardedSupplier) {
		this.awardedSupplier = awardedSupplier;
	}

	public String getSapPoId() {
		return sapPoId;
	}

	public void setSapPoId(String sapPoId) {
		this.sapPoId = sapPoId;
	}

	public Date getLastApprovedDate() {
		return lastApprovedDate;
	}

	public void setLastApprovedDate(Date lastApprovedDate) {
		this.lastApprovedDate = lastApprovedDate;
	}

	public BigDecimal getPaperApprovalDaysCount() {
		return paperApprovalDaysCount;
	}

	public void setPaperApprovalDaysCount(BigDecimal paperApprovalDaysCount) {
		this.paperApprovalDaysCount = paperApprovalDaysCount;
	}

	public Date getEventFirstApprovedDate() {
		return eventFirstApprovedDate;
	}

	public void setEventFirstApprovedDate(Date eventFirstApprovedDate) {
		this.eventFirstApprovedDate = eventFirstApprovedDate;
	}

	public Date getEventLastApprovedDate() {
		return eventLastApprovedDate;
	}

	public void setEventLastApprovedDate(Date eventLastApprovedDate) {
		this.eventLastApprovedDate = eventLastApprovedDate;
	}

	public Date getEventFinishDate() {
		return eventFinishDate;
	}

	public void setEventFinishDate(Date eventFinishDate) {
		this.eventFinishDate = eventFinishDate;
	}

	public Date getEvaluationCompletedDate() {
		return evaluationCompletedDate;
	}

	public void setEvaluationCompletedDate(Date evaluationCompletedDate) {
		this.evaluationCompletedDate = evaluationCompletedDate;
	}

	public Date getEventFirstMeetingDate() {
		return eventFirstMeetingDate;
	}

	public void setEventFirstMeetingDate(Date eventFirstMeetingDate) {
		this.eventFirstMeetingDate = eventFirstMeetingDate;
	}

	public Date getFirstEnvelopOpenDate() {
		return firstEnvelopOpenDate;
	}

	public void setFirstEnvelopOpenDate(Date firstEnvelopOpenDate) {
		this.firstEnvelopOpenDate = firstEnvelopOpenDate;
	}

	public Date getFinishDate() {
		return finishDate;
	}

	public void setFinishDate(Date finishDate) {
		this.finishDate = finishDate;
	}

	public String toLogString() {
		return "TatReportPojo [id=" + id + ", formId=" + formId + ", requestStatus=" + requestStatus + ", eventId=" + eventId + ", status=" + status + ", eventStart=" + eventStart + ", eventEnd=" + eventEnd + ", tenantId=" + tenantId + ", userId=" + userId + ", requeststatus=" + requeststatus + ", eventid=" + eventid + ", eventstatus=" + eventstatus + ", sourcingrequestid=" + sourcingrequestid + "]";
	}

}