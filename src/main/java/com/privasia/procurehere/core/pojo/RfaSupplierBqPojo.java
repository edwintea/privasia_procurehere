/**
 * 
 */
package com.privasia.procurehere.core.pojo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.privasia.procurehere.core.enums.AuctionType;
import com.privasia.procurehere.core.enums.EventStatus;
import com.privasia.procurehere.core.enums.OnlineStatus;
import com.privasia.procurehere.core.enums.SubmissionStatusType;
import com.privasia.procurehere.core.utils.CustomDateTimeSerializer;
import com.privasia.procurehere.core.utils.Global;

/**
 * @author user
 */
public class RfaSupplierBqPojo implements Serializable {

	private static final long serialVersionUID = -474287386475311575L;

	private String id;

	private BigDecimal initialPrice;

	private Integer numberOfBids;

	private BigDecimal currentPrice;

	private BigDecimal differencePercentage;

	private String supplierCompanyName;

	private String supplierId;

	private Integer rankOfSupplier;

	private OnlineStatus onlineStatus;

	private EventStatus currentAuctionStatus;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	private Date startDate;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	private Date endDate;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	private Date resumeDate;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	private Date onlineDate;

	private AuctionType auctionType;

	private Boolean isDisqualify;

	private String disqualifyRemarks;

	private String name;
	private String bqName;

	private String ipAddress;

	private Date disqualifiedTime;

	private BigDecimal revisedGrandTotal;
	private SubmissionStatusType submissionStatus;
	private BigDecimal grandTotal;
	private BigDecimal additionalTax;
	private BigDecimal totalAfterTax;

	private long completeness;
	private long totalItem;
	private Date rejectedTime;
	private Date supplierEventReadTime;
	private Date submissionTime;
	private Integer bqOrder;

	public RfaSupplierBqPojo() {
	}

	public RfaSupplierBqPojo(BigDecimal initialPrice, BigDecimal currentPrice, Integer numberOfBids, BigDecimal differencePercentage, String supplierCompanyName, String supplierId, Integer rankOfSupplier, EventStatus eventStatus, Date startDate, Date endDate, Date resumeDate, Date onlineDate, AuctionType auctionType, Boolean isDisqualify, String disqualifyRemarks) {
		super();
		this.initialPrice = initialPrice;
		this.numberOfBids = numberOfBids;
		this.currentPrice = currentPrice;
		this.differencePercentage = differencePercentage;
		this.supplierCompanyName = supplierCompanyName;
		this.supplierId = supplierId;
		this.rankOfSupplier = rankOfSupplier;
		this.currentAuctionStatus = eventStatus;
		this.auctionType = auctionType;
		this.startDate = startDate;
		this.endDate = endDate;
		this.resumeDate = resumeDate;
		this.onlineDate = onlineDate;
		this.isDisqualify = isDisqualify;
		this.disqualifyRemarks = disqualifyRemarks;

		if (onlineDate != null) {
			long logonSeconds = (new Date().getTime() - onlineDate.getTime()) / 1000;
			if (logonSeconds < Global.ONLINE_USER_DIFF_SEC) {
				this.onlineStatus = OnlineStatus.ONLINE;
			} else {
				this.onlineStatus = OnlineStatus.AWAY;
			}
		} else {
			this.onlineStatus = OnlineStatus.NOT_LOGGEDIN;
		}
	}

	public RfaSupplierBqPojo(Integer rankOfSupplier, String supplierCompanyName, BigDecimal revisedGrandTotal, String supplierId, BigDecimal initialPrice) {

		this.supplierCompanyName = supplierCompanyName;
		this.rankOfSupplier = rankOfSupplier;
		this.revisedGrandTotal = revisedGrandTotal;
		this.supplierId = supplierId;
		this.initialPrice = initialPrice;

	}

	public RfaSupplierBqPojo(String supplierCompanyName, String supplierId, BigDecimal grandTotal, BigDecimal additionalTax, BigDecimal totalAfterTax, String bqName) {

		this.supplierCompanyName = supplierCompanyName;
		this.supplierId = supplierId;
		this.grandTotal = grandTotal;
		this.additionalTax = additionalTax;
		this.totalAfterTax = totalAfterTax;
		this.bqName = bqName;

	}

	public RfaSupplierBqPojo(String supplierCompanyName, String supplierId, BigDecimal totalAfterTax) {

		this.supplierCompanyName = supplierCompanyName;
		this.supplierId = supplierId;
		this.totalAfterTax = totalAfterTax;

	}

	public RfaSupplierBqPojo(Integer rankOfSupplier, String supplierCompanyName, BigDecimal revisedGrandTotal, String supplierId, BigDecimal initialPrice, BigDecimal grandTotal, BigDecimal additionalTax, BigDecimal totalAfterTax) {

		this.supplierCompanyName = supplierCompanyName;
		this.rankOfSupplier = rankOfSupplier;
		this.revisedGrandTotal = revisedGrandTotal;
		this.supplierId = supplierId;
		this.initialPrice = initialPrice;
		this.grandTotal = grandTotal;
		this.additionalTax = additionalTax;
		this.totalAfterTax = totalAfterTax;

	}

	public RfaSupplierBqPojo(String supplierCompanyName, String supplierId, BigDecimal grandTotal, BigDecimal additionalTax, BigDecimal totalAfterTax, String bqName, long completeness, long totalItem) {

		this.supplierCompanyName = supplierCompanyName;
		this.supplierId = supplierId;
		this.grandTotal = grandTotal;
		this.additionalTax = additionalTax;
		this.totalAfterTax = totalAfterTax;
		this.bqName = bqName;
		this.totalItem = totalItem;
		this.completeness = completeness;

	}

	public RfaSupplierBqPojo(Integer rankOfSupplier, String supplierCompanyName, BigDecimal revisedGrandTotal, String supplierId, BigDecimal initialPrice, BigDecimal grandTotal, BigDecimal additionalTax, BigDecimal totalAfterTax, String bqName) {

		this.supplierCompanyName = supplierCompanyName;
		this.rankOfSupplier = rankOfSupplier;
		this.revisedGrandTotal = revisedGrandTotal;
		this.supplierId = supplierId;
		this.initialPrice = initialPrice;
		this.grandTotal = grandTotal;
		this.additionalTax = additionalTax;
		this.totalAfterTax = totalAfterTax;
		this.bqName = bqName;

	}

	public RfaSupplierBqPojo(String supplierCompanyName, BigDecimal revisedGrandTotal, String supplierId, BigDecimal initialPrice, BigDecimal grandTotal, BigDecimal additionalTax, BigDecimal totalAfterTax) {

		this.supplierCompanyName = supplierCompanyName;
		this.revisedGrandTotal = revisedGrandTotal;
		this.supplierId = supplierId;
		this.initialPrice = initialPrice;
		this.grandTotal = grandTotal;
		this.additionalTax = additionalTax;
		this.totalAfterTax = totalAfterTax;

	}

	public RfaSupplierBqPojo(BigDecimal initialPrice, String supplierCompanyName, Integer rankOfSupplier, BigDecimal revisedGrandTotal) {
		this.initialPrice = initialPrice;
		this.supplierCompanyName = supplierCompanyName;
		this.rankOfSupplier = rankOfSupplier;
		this.revisedGrandTotal = revisedGrandTotal;
	}

	public RfaSupplierBqPojo(String id, String supplierId, BigDecimal initialPrice, String supplierCompanyName, Integer rankOfSupplier, BigDecimal revisedGrandTotal, long completeness, long totalItem) {
		this.initialPrice = initialPrice;
		this.supplierCompanyName = supplierCompanyName;
		this.rankOfSupplier = rankOfSupplier;
		this.revisedGrandTotal = revisedGrandTotal;
		this.totalItem = totalItem;
		this.completeness = completeness;
		this.supplierId = supplierId;
		this.id = id;

	}

	public RfaSupplierBqPojo(String ipAddress, String id, String supplierId, BigDecimal initialPrice, String supplierCompanyName, Integer rankOfSupplier, BigDecimal revisedGrandTotal, long completeness, long totalItem) {
		this.initialPrice = initialPrice;
		this.supplierCompanyName = supplierCompanyName;
		this.rankOfSupplier = rankOfSupplier;
		this.revisedGrandTotal = revisedGrandTotal;
		this.totalItem = totalItem;
		this.completeness = completeness;
		this.supplierId = supplierId;
		this.id = id;
		this.ipAddress = ipAddress;

	}

	public RfaSupplierBqPojo(String ipAddress, String id, String supplierId, String supplierCompanyName, Integer rankOfSupplier, Integer numberOfBids, Date rejectedTime, Date supplierEventReadTime, SubmissionStatusType submissionStatus, Boolean isDisqualify, BigDecimal totalAfterTax, long completeness, long totalItem) {
		this.supplierCompanyName = supplierCompanyName;
		this.rankOfSupplier = rankOfSupplier;
		this.totalItem = totalItem;
		this.completeness = completeness;
		this.supplierId = supplierId;
		this.id = id;
		this.ipAddress = ipAddress;
		this.numberOfBids = numberOfBids;
		this.rejectedTime = rejectedTime;
		this.supplierEventReadTime = supplierEventReadTime;
		this.submissionStatus = submissionStatus;
		this.isDisqualify = isDisqualify;
		this.totalAfterTax = totalAfterTax;
	}

	public RfaSupplierBqPojo(String ipAddress, String id, String supplierId, String supplierCompanyName, Integer numberOfBids, Date rejectedTime, Date supplierEventReadTime, SubmissionStatusType submissionStatus, Boolean isDisqualify, BigDecimal totalAfterTax, long completeness, long totalItem) {
		this.supplierCompanyName = supplierCompanyName;
		this.totalItem = totalItem;
		this.completeness = completeness;
		this.supplierId = supplierId;
		this.id = id;
		this.ipAddress = ipAddress;
		this.numberOfBids = numberOfBids;
		this.rejectedTime = rejectedTime;
		this.supplierEventReadTime = supplierEventReadTime;
		this.submissionStatus = submissionStatus;
		this.isDisqualify = isDisqualify;
		this.totalAfterTax = totalAfterTax;

	}

	public RfaSupplierBqPojo(String name, String disqualifyRemarks, Date disqualifiedTime, String id, String supplierId, BigDecimal initialPrice, String supplierCompanyName, Integer rankOfSupplier, BigDecimal revisedGrandTotal, long completeness, long totalItem) {
		this.initialPrice = initialPrice;
		this.supplierCompanyName = supplierCompanyName;
		this.rankOfSupplier = rankOfSupplier;
		this.revisedGrandTotal = revisedGrandTotal;
		this.totalItem = totalItem;
		this.completeness = completeness;
		this.supplierId = supplierId;
		this.id = id;
		this.name = name;
		this.disqualifyRemarks = disqualifyRemarks;
		this.disqualifiedTime = disqualifiedTime;
	}

	public RfaSupplierBqPojo(String supplierCompanyName, String supplierId, EventStatus currentAuctionStatus, AuctionType auctionType, Boolean isDisqualify, Date onlineDate) {
		super();
		this.supplierCompanyName = supplierCompanyName;
		this.supplierId = supplierId;
		this.currentAuctionStatus = currentAuctionStatus;
		this.auctionType = auctionType;
		this.isDisqualify = isDisqualify;
		this.onlineDate = onlineDate;
		if (onlineDate != null) {
			long logonSeconds = (new Date().getTime() - onlineDate.getTime()) / 1000;
			if (logonSeconds < Global.ONLINE_USER_DIFF_SEC) {
				this.onlineStatus = OnlineStatus.ONLINE;
			} else {
				this.onlineStatus = OnlineStatus.AWAY;
			}
		} else {
			this.onlineStatus = OnlineStatus.NOT_LOGGEDIN;
		}
	}

	public RfaSupplierBqPojo(String supplierCompanyName, String supplierId, String id, BigDecimal initialPrice) {
		this.supplierCompanyName = supplierCompanyName;
		this.supplierId = supplierId;
		this.id = id;
		this.initialPrice = initialPrice;
	}

	public RfaSupplierBqPojo(BigDecimal grandTotal, String supplierCompanyName, BigDecimal totalAfterTax) {
		this.initialPrice = grandTotal;
		this.supplierCompanyName = supplierCompanyName;
		this.revisedGrandTotal = totalAfterTax;
	}

	public RfaSupplierBqPojo(String supplierCompanyName, BigDecimal totalAfterTax, String id, BigDecimal grandTotal) {
		this.supplierCompanyName = supplierCompanyName;
		this.totalAfterTax = totalAfterTax;
		this.id = id;
		this.grandTotal = grandTotal;
	}

	public RfaSupplierBqPojo(String ipAddress, String supplierId, BigDecimal revisedGrandTotal, Integer totalItem, long completeness) {
		setId(id);
		setSupplierId(supplierId);
		setRevisedGrandTotal(revisedGrandTotal);
		setTotalItem(totalItem);
		setCompleteness(completeness);
	}

	public RfaSupplierBqPojo(String supplierCompanyName, String supplierId, String ipAddress, BigDecimal totalAfterTax, long completeness, long totalItem) {
		this.supplierCompanyName = supplierCompanyName;
		this.supplierId = supplierId;
		this.ipAddress = ipAddress;
		this.totalAfterTax = totalAfterTax;
		this.completeness = completeness;
		this.totalItem = totalItem;
	}

	public RfaSupplierBqPojo(String name, String disqualifyRemarks, Date disqualifiedTime, String id, String suppId, BigDecimal initialPrice, BigDecimal revisedGrandTotal, long completeness, Integer totalItem) {
		this.name = name;
		this.disqualifyRemarks = disqualifyRemarks;
		this.disqualifiedTime = disqualifiedTime;
		this.id = id;
		this.supplierId = suppId;
		this.initialPrice = initialPrice;
		this.revisedGrandTotal = revisedGrandTotal;
		this.completeness = completeness;
		this.totalItem = totalItem;
	}

	public RfaSupplierBqPojo(String supplierCompanyName, String ipAddress, BigDecimal totalAfterTax, long completeness, long totalItem) {
		super();
		this.supplierCompanyName = supplierCompanyName;
		this.ipAddress = ipAddress;
		this.totalAfterTax = totalAfterTax;
		this.completeness = completeness;
		this.totalItem = totalItem;
	}

	public RfaSupplierBqPojo(String supplierCompanyName, BigDecimal grandTotal, BigDecimal additionalTax, BigDecimal totalAfterTax) {
		super();
		this.supplierCompanyName = supplierCompanyName;
		this.grandTotal = grandTotal;
		this.additionalTax = additionalTax;
		this.totalAfterTax = totalAfterTax;
	}

	// this is used for RFX submission report
	public RfaSupplierBqPojo(String ipAddress, String id, String supplierId, String supplierCompanyName, Integer numberOfBids, Date rejectedTime, Date supplierEventReadTime, SubmissionStatusType submissionStatus, Boolean isDisqualify, BigDecimal totalAfterTax, long completeness, long totalItem, Date submissionTime) {
		this.supplierCompanyName = supplierCompanyName;
		this.totalItem = totalItem;
		this.completeness = completeness;
		this.supplierId = supplierId;
		this.id = id;
		this.ipAddress = ipAddress;
		this.numberOfBids = numberOfBids;
		this.rejectedTime = rejectedTime;
		this.supplierEventReadTime = supplierEventReadTime;
		this.submissionStatus = submissionStatus;
		this.isDisqualify = isDisqualify;
		this.totalAfterTax = totalAfterTax;
		this.submissionTime = submissionTime;
	}

	// this is used for participation tabel
	public RfaSupplierBqPojo(Integer numberOfBids, String supplierCompanyName, String supplierId, Boolean isDisqualify, String ipAddress, SubmissionStatusType submissionStatus, Date rejectedTime, Date supplierEventReadTime, Date submissionTime) {
		super();
		this.numberOfBids = numberOfBids;
		this.supplierCompanyName = supplierCompanyName;
		this.supplierId = supplierId;
		this.isDisqualify = isDisqualify;
		this.ipAddress = ipAddress;
		this.submissionStatus = submissionStatus;
		this.rejectedTime = rejectedTime;
		this.supplierEventReadTime = supplierEventReadTime;
		this.submissionTime = submissionTime;
	}

	// this is used for RFX submission report
	// PH-1575
	public RfaSupplierBqPojo(String ipAddress, String id, String supplierId, String supplierCompanyName, Integer numberOfBids, Date rejectedTime, Date supplierEventReadTime, SubmissionStatusType submissionStatus, Boolean isDisqualify, BigDecimal totalAfterTax, long completeness, long totalItem, Date submissionTime, Integer bqOrder) {
		this.supplierCompanyName = supplierCompanyName;
		this.totalItem = totalItem;
		this.completeness = completeness;
		this.supplierId = supplierId;
		this.id = id;
		this.ipAddress = ipAddress;
		this.numberOfBids = numberOfBids;
		this.rejectedTime = rejectedTime;
		this.supplierEventReadTime = supplierEventReadTime;
		this.submissionStatus = submissionStatus;
		this.isDisqualify = isDisqualify;
		this.totalAfterTax = totalAfterTax;
		this.submissionTime = submissionTime;
		this.bqOrder = bqOrder;
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
	 * @return the initialPrice
	 */
	public BigDecimal getInitialPrice() {
		return initialPrice;
	}

	/**
	 * @param initialPrice the initialPrice to set
	 */
	public void setInitialPrice(BigDecimal initialPrice) {
		this.initialPrice = initialPrice;
	}

	/**
	 * @return the numberOfBids
	 */
	public Integer getNumberOfBids() {
		return numberOfBids;
	}

	/**
	 * @param numberOfBids the numberOfBids to set
	 */
	public void setNumberOfBids(Integer numberOfBids) {
		this.numberOfBids = numberOfBids;
	}

	/**
	 * @return the currentPrice
	 */
	public BigDecimal getCurrentPrice() {
		return currentPrice;
	}

	/**
	 * @param currentPrice the currentPrice to set
	 */
	public void setCurrentPrice(BigDecimal currentPrice) {
		this.currentPrice = currentPrice;
	}

	/**
	 * @return the differencePercentage
	 */
	public BigDecimal getDifferencePercentage() {
		return differencePercentage;
	}

	/**
	 * @param differencePercentage the differencePercentage to set
	 */
	public void setDifferencePercentage(BigDecimal differencePercentage) {
		this.differencePercentage = differencePercentage;
	}

	/**
	 * @return the supplierCompanyName
	 */
	public String getSupplierCompanyName() {
		return supplierCompanyName;
	}

	/**
	 * @param supplierCompanyName the supplierCompanyName to set
	 */
	public void setSupplierCompanyName(String supplierCompanyName) {
		this.supplierCompanyName = supplierCompanyName;
	}

	/**
	 * @return the supplierId
	 */
	public String getSupplierId() {
		return supplierId;
	}

	/**
	 * @param supplierId the supplierId to set
	 */
	public void setSupplierId(String supplierId) {
		this.supplierId = supplierId;
	}

	/**
	 * @return the rankOfSupplier
	 */
	public Integer getRankOfSupplier() {
		return rankOfSupplier;
	}

	/**
	 * @param rankOfSupplier the rankOfSupplier to set
	 */
	public void setRankOfSupplier(Integer rankOfSupplier) {
		this.rankOfSupplier = rankOfSupplier;
	}

	/**
	 * @return the onlineStatus
	 */
	public OnlineStatus getOnlineStatus() {
		return onlineStatus;
	}

	/**
	 * @param onlineStatus the onlineStatus to set
	 */
	public void setOnlineStatus(OnlineStatus onlineStatus) {
		this.onlineStatus = onlineStatus;
	}

	/**
	 * @return the currentAuctionStatus
	 */
	public EventStatus getCurrentAuctionStatus() {
		return currentAuctionStatus;
	}

	/**
	 * @param currentAuctionStatus the currentAuctionStatus to set
	 */
	public void setCurrentAuctionStatus(EventStatus currentAuctionStatus) {
		this.currentAuctionStatus = currentAuctionStatus;
	}

	/**
	 * @return the startDate
	 */
	public Date getStartDate() {
		return startDate;
	}

	/**
	 * @param startDate the startDate to set
	 */
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	/**
	 * @return the endDate
	 */
	public Date getEndDate() {
		return endDate;
	}

	/**
	 * @param endDate the endDate to set
	 */
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	/**
	 * @return the resumeDate
	 */
	public Date getResumeDate() {
		return resumeDate;
	}

	/**
	 * @param resumeDate the resumeDate to set
	 */
	public void setResumeDate(Date resumeDate) {
		this.resumeDate = resumeDate;
	}

	/**
	 * @return the auctionType
	 */
	public AuctionType getAuctionType() {
		return auctionType;
	}

	/**
	 * @param auctionType the auctionType to set
	 */
	public void setAuctionType(AuctionType auctionType) {
		this.auctionType = auctionType;
	}

	/**
	 * @return the onlineDate
	 */
	public Date getOnlineDate() {
		return onlineDate;
	}

	/**
	 * @param onlineDate the onlineDate to set
	 */
	public void setOnlineDate(Date onlineDate) {
		this.onlineDate = onlineDate;
	}

	/**
	 * @return the isDisqualify
	 */
	public Boolean getIsDisqualify() {
		return isDisqualify;
	}

	/**
	 * @param isDisqualify the isDisqualify to set
	 */
	public void setIsDisqualify(Boolean isDisqualify) {
		this.isDisqualify = isDisqualify;
	}

	/**
	 * @return the disqualifyRemarks
	 */
	public String getDisqualifyRemarks() {
		return disqualifyRemarks;
	}

	/**
	 * @param disqualifyRemarks the disqualifyRemarks to set
	 */
	public void setDisqualifyRemarks(String disqualifyRemarks) {
		this.disqualifyRemarks = disqualifyRemarks;
	}

	/**
	 * @return
	 */
	public BigDecimal getRevisedGrandTotal() {
		return revisedGrandTotal;
	}

	/**
	 * @param revisedGrandTotal
	 */
	public void setRevisedGrandTotal(BigDecimal revisedGrandTotal) {
		this.revisedGrandTotal = revisedGrandTotal;
	}

	/**
	 * @return the completeness
	 */
	public long getCompleteness() {
		return completeness;
	}

	/**
	 * @param completeness the completeness to set
	 */
	public void setCompleteness(long completeness) {
		this.completeness = completeness;
	}

	/**
	 * @return the totalItem
	 */
	public long getTotalItem() {
		return totalItem;
	}

	/**
	 * @param totalItem the totalItem to set
	 */
	public void setTotalItem(long totalItem) {
		this.totalItem = totalItem;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the disqualifiedTime
	 */
	public Date getDisqualifiedTime() {
		return disqualifiedTime;
	}

	/**
	 * @param disqualifiedTime the disqualifiedTime to set
	 */
	public void setDisqualifiedTime(Date disqualifiedTime) {
		this.disqualifiedTime = disqualifiedTime;
	}

	/**
	 * @return the ipAddress
	 */
	public String getIpAddress() {
		return ipAddress;
	}

	/**
	 * @param ipAddress the ipAddress to set
	 */
	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public BigDecimal getAdditionalTax() {
		return additionalTax;
	}

	public void setAdditionalTax(BigDecimal additionalTax) {
		this.additionalTax = additionalTax;
	}

	public BigDecimal getTotalAfterTax() {
		return totalAfterTax;
	}

	public void setTotalAfterTax(BigDecimal totalAfterTax) {
		this.totalAfterTax = totalAfterTax;
	}

	public BigDecimal getGrandTotal() {
		return grandTotal;
	}

	public void setGrandTotal(BigDecimal grandTotal) {
		this.grandTotal = grandTotal;
	}

	/**
	 * @return the rejectedTime
	 */
	public Date getRejectedTime() {
		return rejectedTime;
	}

	/**
	 * @param rejectedTime the rejectedTime to set
	 */
	public void setRejectedTime(Date rejectedTime) {
		this.rejectedTime = rejectedTime;
	}

	/**
	 * @return the supplierEventReadTime
	 */
	public Date getSupplierEventReadTime() {
		return supplierEventReadTime;
	}

	/**
	 * @param supplierEventReadTime the supplierEventReadTime to set
	 */
	public void setSupplierEventReadTime(Date supplierEventReadTime) {
		this.supplierEventReadTime = supplierEventReadTime;
	}

	/**
	 * @return the submissionStatus
	 */
	public SubmissionStatusType getSubmissionStatus() {
		return submissionStatus;
	}

	/**
	 * @param submissionStatus the submissionStatus to set
	 */
	public void setSubmissionStatus(SubmissionStatusType submissionStatus) {
		this.submissionStatus = submissionStatus;
	}

	public String getBqName() {
		return bqName;
	}

	public void setBqName(String bqName) {
		this.bqName = bqName;
	}

	public Date getSubmissionTime() {
		return submissionTime;
	}

	public void setSubmissionTime(Date submissionTime) {
		this.submissionTime = submissionTime;
	}

	/**
	 * @return the bqOrder
	 */
	public Integer getBqOrder() {
		return bqOrder;
	}

	/**
	 * @param bqOrder the bqOrder to set
	 */
	public void setBqOrder(Integer bqOrder) {
		this.bqOrder = bqOrder;
	}

}
