/**
 * 
 */
package com.privasia.procurehere.core.pojo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.privasia.procurehere.core.enums.EventStatus;
import com.privasia.procurehere.core.utils.CustomDateTimeSerializer;

/**
 * @author user
 */
public class BidHistoryPojo implements Serializable {

	private static final long serialVersionUID = -474287386475311575L;

	private String id;

	private BigDecimal minPrice;

	private BigDecimal maxPrice;
	

	private Long duration;

	private EventStatus auctionStatus;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	private Date startDateTime;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	private Date endDateTime;

	private List<AuctionSupplierPojo> supplierAuction;

	private Integer bidNumber;
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
	 * @return the minPrice
	 */
	public BigDecimal getMinPrice() {
		return minPrice;
	}

	/**
	 * @param minPrice the minPrice to set
	 */
	public void setMinPrice(BigDecimal minPrice) {
		this.minPrice = minPrice;
	}

	/**
	 * @return the maxPrice
	 */
	public BigDecimal getMaxPrice() {
		return maxPrice;
	}

	/**
	 * @param maxPrice the maxPrice to set
	 */
	public void setMaxPrice(BigDecimal maxPrice) {
		this.maxPrice = maxPrice;
	}

	/**
	 * @return the duration
	 */
	public Long getDuration() {
		return duration;
	}

	/**
	 * @param duration the duration to set
	 */
	public void setDuration(Long duration) {
		this.duration = duration;
	}

	/**
	 * @return the auctionStatus
	 */
	public EventStatus getAuctionStatus() {
		return auctionStatus;
	}

	/**
	 * @param auctionStatus the auctionStatus to set
	 */
	public void setAuctionStatus(EventStatus auctionStatus) {
		this.auctionStatus = auctionStatus;
	}

	/**
	 * @return the startDateTime
	 */
	public Date getStartDateTime() {
		return startDateTime;
	}

	/**
	 * @param startDateTime the startDateTime to set
	 */
	public void setStartDateTime(Date startDateTime) {
		this.startDateTime = startDateTime;
	}

	/**
	 * @return the endDateTime
	 */
	public Date getEndDateTime() {
		return endDateTime;
	}

	/**
	 * @param endDateTime the endDateTime to set
	 */
	public void setEndDateTime(Date endDateTime) {
		this.endDateTime = endDateTime;
	}

	/**
	 * @return the supplierAuction
	 */
	public List<AuctionSupplierPojo> getSupplierAuction() {
		return supplierAuction;
	}

	/**
	 * @param supplierAuction the supplierAuction to set
	 */
	public void setSupplierAuction(List<AuctionSupplierPojo> supplierAuction) {
		this.supplierAuction = supplierAuction;
	}

	public Integer getBidNumber() {
		return bidNumber;
	}

	public void setBidNumber(Integer bidNumber) {
		this.bidNumber = bidNumber;
	}
	
	

}
