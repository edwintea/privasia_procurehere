package com.privasia.procurehere.core.entity;

/**
 * @author RT-Kapil
 */

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Digits;

import org.hibernate.annotations.GenericGenerator;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.privasia.procurehere.core.utils.CustomDateTimeWithSecSerializer;
import org.hibernate.annotations.Type;

@Entity
@Table(name = "PROC_AUCTION_BIDS")
public class AuctionBids implements Serializable {

	private static final long serialVersionUID = 8072409111227661261L;

	@Id
	@GenericGenerator(name = "idGen", strategy = "uuid.hex")
	@GeneratedValue(generator = "idGen")
	@Column(name = "ID", length = 64)
	private String id;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, cascade = { CascadeType.REFRESH }, optional = false)
	@JoinColumn(name = "EVENT_ID", foreignKey = @ForeignKey(name = "FK_RFA_AUCTION_BID_AUCTION_ID"))
	private RfaEvent event;

	@Column(name = "BID_AMOUNT", length = 22, precision = 18, scale = 6)
	private BigDecimal amount;

	@Lob
	@Type(type = "org.hibernate.type.TextType")
	@Column(name = "BID_DETAILS")
	private String details;

	@ManyToOne(fetch = FetchType.LAZY, cascade = { CascadeType.REFRESH }, optional = false)
	@JoinColumn(name = "BID_BY_SUPPLIER_ID", foreignKey = @ForeignKey(name = "FK_BID_BY_SUPPLIER"))
	private Supplier bidBySupplier;

	@JsonSerialize(using = CustomDateTimeWithSecSerializer.class)
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "BID_SUBMISSION_DATE")
	private Date bidSubmissionDate;

	@Column(name = "RANK_AT_BID", length = 3)
	@Digits(integer = 3, fraction = 0)
	private Integer rankForBid;

	@Column(name = "IS_REVERTED", nullable = true)
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean isReverted = Boolean.FALSE;

	@Column(name = "IP_ADDRESS", length = 50, nullable = true)
	private String ipAddress;

	@Column(name = "REMARK", length = 500, nullable = true)
	private String remark;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "REVERTED_BY", nullable = true, foreignKey = @ForeignKey(name = "FK_REVERTED_BID_BY"))
	private User revertedBy;

	/**
	 * 
	 */
	public AuctionBids() {
		super();
		this.isReverted = Boolean.FALSE;
	}

	/**
	 * @param amount
	 * @param bidSubmissionDate
	 * @param rankForBid
	 * @param id TODO
	 */
	public AuctionBids(String id, BigDecimal amount, Date bidSubmissionDate, Integer rankForBid) {
		super();
		this.id = id;
		this.amount = amount;
		this.bidSubmissionDate = bidSubmissionDate;
		this.rankForBid = rankForBid;
	}

	public AuctionBids(String id, BigDecimal amount, Date bidSubmissionDate, Integer rankForBid, String companyName) {
		super();
		this.id = id;
		this.amount = amount;
		this.bidSubmissionDate = bidSubmissionDate;
		this.rankForBid = rankForBid;
		this.bidBySupplier = new Supplier();
		this.bidBySupplier.setCompanyName(companyName);
	}

	public AuctionBids(String id, BigDecimal amount, Date bidSubmissionDate, Integer rankForBid, String companyName, boolean isReverted,String remark, String name) {
		super();
		this.id = id;
		this.amount = amount;
		this.bidSubmissionDate = bidSubmissionDate;
		this.rankForBid = rankForBid;
		this.bidBySupplier = new Supplier();
		this.bidBySupplier.setCompanyName(companyName);
		this.isReverted = isReverted;
		this.remark = remark;
		this.revertedBy = new User();
		this.revertedBy.setName(name);
	}

	public AuctionBids(String id, BigDecimal amount, Date bidSubmissionDate, Integer rankForBid, String companyName, String supplierId) {
		super();
		this.id = id;
		this.amount = amount;
		this.bidSubmissionDate = bidSubmissionDate;
		this.rankForBid = rankForBid;
		this.bidBySupplier = new Supplier();
		this.bidBySupplier.setId(supplierId);
		this.bidBySupplier.setCompanyName(companyName);
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
	 * @return the event
	 */
	public RfaEvent getEvent() {
		return event;
	}

	/**
	 * @param event the event to set
	 */
	public void setEvent(RfaEvent event) {
		this.event = event;
	}

	/**
	 * @return the amount
	 */
	public BigDecimal getAmount() {
		return amount;
	}

	/**
	 * @param amount the amount to set
	 */
	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	/**
	 * @return the bidBySupplier
	 */
	public Supplier getBidBySupplier() {
		return bidBySupplier;
	}

	/**
	 * @param bidBySupplier the bidBySupplier to set
	 */
	public void setBidBySupplier(Supplier bidBySupplier) {
		this.bidBySupplier = bidBySupplier;
	}

	/**
	 * @return the bidSubmissionDate
	 */
	public Date getBidSubmissionDate() {
		return bidSubmissionDate;
	}

	/**
	 * @param bidSubmissionDate the bidSubmissionDate to set
	 */
	public void setBidSubmissionDate(Date bidSubmissionDate) {
		this.bidSubmissionDate = bidSubmissionDate;
	}

	/**
	 * @return the details
	 */
	public String getDetails() {
		return details;
	}

	/**
	 * @param details the details to set
	 */
	public void setDetails(String details) {
		this.details = details;
	}

	/**
	 * @return the rankForBid
	 */
	public Integer getRankForBid() {
		return rankForBid;
	}

	/**
	 * @param rankForBid the rankForBid to set
	 */
	public void setRankForBid(Integer rankForBid) {
		this.rankForBid = rankForBid;
	}

	/**
	 * @return the isReverted
	 */
	public Boolean getIsReverted() {
		return isReverted;
	}

	/**
	 * @param isReverted the isReverted to set
	 */
	public void setIsReverted(Boolean isReverted) {
		this.isReverted = isReverted;
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

	/**
	 * @return the remark
	 */
	public String getRemark() {
		return remark;
	}

	/**
	 * @param remark the remark to set
	 */
	public void setRemark(String remark) {
		this.remark = remark;
	}

	/**
	 * @return the revertedBy
	 */
	public User getRevertedBy() {
		return revertedBy;
	}

	/**
	 * @param revertedBy the revertedBy to set
	 */
	public void setRevertedBy(User revertedBy) {
		this.revertedBy = revertedBy;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "AuctionBids [amount=" + amount + ", bidSubmissionDate=" + bidSubmissionDate + "]";
	}

	public String toLogString() {
		return "AuctionBids [event=" + event + ", amount=" + amount + ", details=" + details + ", bidSubmissionDate=" + bidSubmissionDate + ", rankForBid=" + rankForBid + ", isReverted=" + isReverted + "]";
	}

}
