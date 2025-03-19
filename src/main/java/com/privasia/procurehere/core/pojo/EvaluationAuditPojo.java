package com.privasia.procurehere.core.pojo;

import java.io.Serializable;

public class EvaluationAuditPojo implements Serializable {

	private static final long serialVersionUID = 5824627171967727605L;
	private String auctionDate;
	private String auctionBy;
	private String auction;
	private String description;

	/**
	 * @return the auctionDate
	 */
	public String getAuctionDate() {
		return auctionDate;
	}

	/**
	 * @param auctionDate the auctionDate to set
	 */
	public void setAuctionDate(String auctionDate) {
		this.auctionDate = auctionDate;
	}

	/**
	 * @return the auctionBy
	 */
	public String getAuctionBy() {
		return auctionBy;
	}

	/**
	 * @param auctionBy the auctionBy to set
	 */
	public void setAuctionBy(String auctionBy) {
		this.auctionBy = auctionBy;
	}

	/**
	 * @return the auction
	 */
	public String getAuction() {
		return auction;
	}

	/**
	 * @param auction the auction to set
	 */
	public void setAuction(String auction) {
		this.auction = auction;
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

}
