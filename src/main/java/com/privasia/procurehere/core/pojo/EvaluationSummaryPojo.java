package com.privasia.procurehere.core.pojo;

import java.io.Serializable;
import java.util.List;

import com.privasia.procurehere.core.entity.RfaEventSupplier;

/**
 * @author madhuri
 */
public class EvaluationSummaryPojo implements Serializable {

	private static final long serialVersionUID = -8650204823048365415L;
	private String title;
	private String timelineDescription;
	private String participationDescription;
	private String estimationDescription;
	private String summaryLowHighestTitle;
	private String resultDescription;
	private String eventType;
	private boolean bqAvailable = false;
	private String decimal;
	private boolean bidsSubmitted = false;
	private String companyName;

	private List<SupplierListPojo> supplierList;
	private List<RfaEventSupplier> suppliersList;

	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * @return the timelineDescription
	 */
	public String getTimelineDescription() {
		return timelineDescription;
	}

	/**
	 * @param timelineDescription the timelineDescription to set
	 */
	public void setTimelineDescription(String timelineDescription) {
		this.timelineDescription = timelineDescription;
	}

	/**
	 * @return the participationDescription
	 */
	public String getParticipationDescription() {
		return participationDescription;
	}

	/**
	 * @param participationDescription the participationDescription to set
	 */
	public void setParticipationDescription(String participationDescription) {
		this.participationDescription = participationDescription;
	}

	/**
	 * @return the estimationDescription
	 */
	public String getEstimationDescription() {
		return estimationDescription;
	}

	/**
	 * @param estimationDescription the estimationDescription to set
	 */
	public void setEstimationDescription(String estimationDescription) {
		this.estimationDescription = estimationDescription;
	}

	/**
	 * @return the summaryLowHighestTitle
	 */
	public String getSummaryLowHighestTitle() {
		return summaryLowHighestTitle;
	}

	/**
	 * @param summaryLowHighestTitle the summaryLowHighestTitle to set
	 */
	public void setSummaryLowHighestTitle(String summaryLowHighestTitle) {
		this.summaryLowHighestTitle = summaryLowHighestTitle;
	}

	/**
	 * @return the resultDescription
	 */
	public String getResultDescription() {
		return resultDescription;
	}

	/**
	 * @param resultDescription the resultDescription to set
	 */
	public void setResultDescription(String resultDescription) {
		this.resultDescription = resultDescription;
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
	 * @return the supplierList
	 */
	public List<SupplierListPojo> getSupplierList() {
		return supplierList;
	}

	/**
	 * @param supplierList the supplierList to set
	 */
	public void setSupplierList(List<SupplierListPojo> supplierList) {
		this.supplierList = supplierList;
	}

	/**
	 * @return the suppliersList
	 */
	public List<RfaEventSupplier> getSuppliersList() {
		return suppliersList;
	}

	/**
	 * @param suppliersList the suppliersList to set
	 */
	public void setSuppliersList(List<RfaEventSupplier> suppliersList) {
		this.suppliersList = suppliersList;
	}

	public boolean isBqAvailable() {
		return bqAvailable;
	}

	public void setBqAvailable(boolean bqAvailable) {
		this.bqAvailable = bqAvailable;
	}

	public String getDecimal() {
		return decimal;
	}

	public void setDecimal(String decimal) {
		this.decimal = decimal;
	}

	public boolean isBidsSubmitted() {
		return bidsSubmitted;
	}

	public void setBidsSubmitted(boolean isBidsSubmitted) {
		this.bidsSubmitted = isBidsSubmitted;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

}
