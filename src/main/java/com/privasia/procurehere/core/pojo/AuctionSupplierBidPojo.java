/**
 * 
 */
package com.privasia.procurehere.core.pojo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.privasia.procurehere.core.utils.CustomDateTimeWithSecSerializer;

/**
 * @author user
 */
public class AuctionSupplierBidPojo implements Serializable {

	private static final long serialVersionUID = -474287386475311575L;

	private BigDecimal bidPrice;

	@JsonSerialize(using = CustomDateTimeWithSecSerializer.class)
	private Date bidDateAndTime;

	public AuctionSupplierBidPojo() {
	}
	
	public AuctionSupplierBidPojo(BigDecimal bidPrice, Date bidTime) {
		this.bidPrice = bidPrice;
		this.bidDateAndTime = bidTime;
	}
	
	/**
	 * @return the bidPrice
	 */
	public BigDecimal getBidPrice() {
		return bidPrice;
	}

	/**
	 * @param bidPrice the bidPrice to set
	 */
	public void setBidPrice(BigDecimal bidPrice) {
		this.bidPrice = bidPrice;
	}

	/**
	 * @return the bidDateAndTime
	 */
	public Date getBidDateAndTime() {
		return bidDateAndTime;
	}

	/**
	 * @param bidDateAndTime the bidDateAndTime to set
	 */
	public void setBidDateAndTime(Date bidDateAndTime) {
		this.bidDateAndTime = bidDateAndTime;
	}

}
