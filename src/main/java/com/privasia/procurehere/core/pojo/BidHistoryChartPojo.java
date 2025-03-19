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
public class BidHistoryChartPojo implements Serializable {

	private static final long serialVersionUID = 1433382433319322066L;

	private BigDecimal minPrice;

	private BigDecimal maxPrice;
	
	private Integer totalBids;

	private EventStatus auctionStatus;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	private Date startDateTime;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	private Date endDateTime;

	private List<BidHistoryChartSupplierPojo> supplierBids;

	/**
	 * @return the minPrice
	 */
	public BigDecimal getMinPrice() {
		return minPrice;
	}

	/**
	 * @param minPrice
	 *            the minPrice to set
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
	 * @param maxPrice
	 *            the maxPrice to set
	 */
	public void setMaxPrice(BigDecimal maxPrice) {
		this.maxPrice = maxPrice;
	}

	/**
	 * @return the auctionStatus
	 */
	public EventStatus getAuctionStatus() {
		return auctionStatus;
	}

	/**
	 * @param auctionStatus
	 *            the auctionStatus to set
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
	 * @param startDateTime
	 *            the startDateTime to set
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
	 * @param endDateTime
	 *            the endDateTime to set
	 */
	public void setEndDateTime(Date endDateTime) {
		this.endDateTime = endDateTime;
	}

	/**
	 * @return the supplierBids
	 */
	public List<BidHistoryChartSupplierPojo> getSupplierBids() {
		return supplierBids;
	}

	/**
	 * @param supplierBids
	 *            the supplierBids to set
	 */
	public void setSupplierBids(List<BidHistoryChartSupplierPojo> supplierBids) {
		this.supplierBids = supplierBids;
	}

	/**
	 * @return the totalBids
	 */
	public Integer getTotalBids() {
		return totalBids;
	}

	/**
	 * @param totalBids the totalBids to set
	 */
	public void setTotalBids(Integer totalBids) {
		this.totalBids = totalBids;
	}
	
}
