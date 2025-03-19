/**
 * 
 */
package com.privasia.procurehere.core.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Digits;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.privasia.procurehere.core.enums.SubmissionStatusType;
import com.privasia.procurehere.core.utils.CustomDateTimeSerializer;
import org.hibernate.annotations.Type;

/**
 * @author RT-Kapil
 */
@Entity
@Table(name = "PROC_RFA_EVENT_SUPPLIERS")
public class RfaEventSupplier extends EventSupplier implements Serializable {

	private static final long serialVersionUID = 7670122088966316150L;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, optional = true)
	@JoinColumn(name = "EVENT_ID", foreignKey = @ForeignKey(name = "FK_RFA_INV_SUP_EVENT"))
	private RfaEvent rfxEvent;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, cascade = { CascadeType.REFRESH }, optional = true)
	@JoinColumn(name = "BID_BY_USER", foreignKey = @ForeignKey(name = "FK_BID_BY_USER"))
	private User bidByUser;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "BID_DATE_AND_TIME")
	private Date bidDateAndTime;

	@JsonIgnore
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "eventSupplier", orphanRemoval = true, cascade = CascadeType.ALL)
	private List<RfaSupplierTeamMember> teamMembers;

	@Column(name = "AUCTION_RANKING", length = 3)
	@Digits(integer = 3, fraction = 0)
	private Integer auctionRankingOfSupplier;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "AUCTION_ONINE_DATE_TIME")
	private Date auctionOnlineDateTime;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, cascade = { CascadeType.REFRESH }, optional = true)
	@JoinColumn(name = "REVISED_BID_BY_USER", foreignKey = @ForeignKey(name = "FK_REVISED_BID_BY_USER"))
	private User revisedBidByUser;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "REVISED_BID_DATETIME")
	private Date revisedBidDateAndTime;

	@Column(name = "IS_REVISED_BID_SUBMITTED")
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean revisedBidSubmitted = Boolean.FALSE;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CONFIRM_PRICE_DATETIME")
	private Date confirmPriceDateAndTime;

	@Column(name = "IS_CONFIRM_PRICE_SUBMITTED")
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean confirmPriceSubmitted = Boolean.FALSE;

	@ManyToOne(fetch = FetchType.LAZY, optional = true, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "DISQUALIFIED_ENVELOPE", nullable = true)
	private RfaEnvelop disqualifiedEnvelope;

	public RfaEventSupplier() {
		super();
		this.revisedBidSubmitted = Boolean.FALSE;
		this.confirmPriceSubmitted = Boolean.FALSE;
	}

	public RfaEventSupplier(String SupplierId, Integer auctionRankingOfSupplier) {
		this.auctionRankingOfSupplier = auctionRankingOfSupplier;
		Supplier supplier = new Supplier();
		supplier.setId(SupplierId);
		this.setSupplier(supplier);

	}

	public RfaEventSupplier createMobileRfaShallowCopy() {
		RfaEventSupplier supplier = new RfaEventSupplier();
		supplier.setSupplierCompanyName(getSupplier() != null ? getSupplier().getCompanyName() : null);
		supplier.setSupplierInvitedTime(null);
		supplier.setPreviewTime(getPreviewTime());
		supplier.setSupplierEventReadTime(getSupplierEventReadTime());
		supplier.setSupplierSubmittedTime(getSupplierSubmittedTime());
		supplier.setRevisedBidSubmitted(getRevisedBidSubmitted());
		supplier.setRevisedBidDateAndTime(getRevisedBidDateAndTime());
		supplier.setConfirmPriceDateAndTime(getConfirmPriceDateAndTime());
		supplier.setSubmitted(null);
		supplier.setDisqualify(null);
		supplier.setNumberOfBids(null);
		supplier.setNotificationSent(null);
		supplier.setConfirmPrice(null);
		supplier.setRevisedBidSubmitted(null);
		return supplier;
	}

	public RfaEventSupplier(String id, SubmissionStatusType submissionStatus) {
		setId(id);
		setSubmissionStatus(submissionStatus);
	}

	/**
	 * @return the rfxEvent
	 */
	public RfaEvent getRfxEvent() {
		return rfxEvent;
	}

	/**
	 * @param rfxEvent the rfxEvent to set
	 */
	public void setRfxEvent(RfaEvent rfxEvent) {
		this.rfxEvent = rfxEvent;
	}

	/**
	 * @return the bidByUser
	 */
	public User getBidByUser() {
		return bidByUser;
	}

	/**
	 * @param bidByUser the bidByUser to set
	 */
	public void setBidByUser(User bidByUser) {
		this.bidByUser = bidByUser;
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

	/**
	 * @return the teamMembers
	 */
	public List<RfaSupplierTeamMember> getTeamMembers() {
		return teamMembers;
	}

	/**
	 * @param teamMembers the teamMembers to set
	 */
	public void setTeamMembers(List<RfaSupplierTeamMember> teamMembers) {
		if (this.teamMembers == null) {
			this.teamMembers = new ArrayList<RfaSupplierTeamMember>();
		} else {
			this.teamMembers.clear();
		}
		if (teamMembers != null) {
			this.teamMembers.addAll(teamMembers);
		}
	}

	/**
	 * @return the auctionRankingOfSupplier
	 */
	public Integer getAuctionRankingOfSupplier() {
		return auctionRankingOfSupplier;
	}

	/**
	 * @param auctionRankingOfSupplier the auctionRankingOfSupplier to set
	 */
	public void setAuctionRankingOfSupplier(Integer auctionRankingOfSupplier) {
		this.auctionRankingOfSupplier = auctionRankingOfSupplier;
	}

	/**
	 * @return the auctionOnlineDateTime
	 */
	public Date getAuctionOnlineDateTime() {
		return auctionOnlineDateTime;
	}

	/**
	 * @param auctionOnlineDateTime the auctionOnlineDateTime to set
	 */
	public void setAuctionOnlineDateTime(Date auctionOnlineDateTime) {
		this.auctionOnlineDateTime = auctionOnlineDateTime;
	}

	/**
	 * @return the revisedBidByUser
	 */
	public User getRevisedBidByUser() {
		return revisedBidByUser;
	}

	/**
	 * @param revisedBidByUser the revisedBidByUser to set
	 */
	public void setRevisedBidByUser(User revisedBidByUser) {
		this.revisedBidByUser = revisedBidByUser;
	}

	/**
	 * @return the revisedBidDateAndTime
	 */
	public Date getRevisedBidDateAndTime() {
		return revisedBidDateAndTime;
	}

	/**
	 * @param revisedBidDateAndTime the revisedBidDateAndTime to set
	 */
	public void setRevisedBidDateAndTime(Date revisedBidDateAndTime) {
		this.revisedBidDateAndTime = revisedBidDateAndTime;
	}

	/**
	 * @return the revisedBidSubmitted
	 */
	public Boolean getRevisedBidSubmitted() {
		return revisedBidSubmitted;
	}

	/**
	 * @param revisedBidSubmitted the revisedBidSubmitted to set
	 */
	public void setRevisedBidSubmitted(Boolean revisedBidSubmitted) {
		this.revisedBidSubmitted = revisedBidSubmitted;
	}

	/**
	 * @return the confirmPriceDateAndTime
	 */
	public Date getConfirmPriceDateAndTime() {
		return confirmPriceDateAndTime;
	}

	/**
	 * @param confirmPriceDateAndTime the confirmPriceDateAndTime to set
	 */
	public void setConfirmPriceDateAndTime(Date confirmPriceDateAndTime) {
		this.confirmPriceDateAndTime = confirmPriceDateAndTime;
	}

	/**
	 * @return the confirmPriceSubmitted
	 */
	public Boolean getConfirmPriceSubmitted() {
		return confirmPriceSubmitted;
	}

	/**
	 * @param confirmPriceSubmitted the confirmPriceSubmitted to set
	 */
	public void setConfirmPriceSubmitted(Boolean confirmPriceSubmitted) {
		this.confirmPriceSubmitted = confirmPriceSubmitted;
	}

	/**
	 * @return the disqualifiedEnvelope
	 */
	public RfaEnvelop getDisqualifiedEnvelope() {
		return disqualifiedEnvelope;
	}

	/**
	 * @param disqualifiedEnvelope the disqualifiedEnvelope to set
	 */
	public void setDisqualifiedEnvelope(RfaEnvelop disqualifiedEnvelope) {
		this.disqualifiedEnvelope = disqualifiedEnvelope;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return super.hashCode();
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		return super.equals(obj);
	}

	public String toLogString() {
		return "RfaEventSupplier [toLogString : " + super.toLogString() + "]";
	}

}
