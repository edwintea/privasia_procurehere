package com.privasia.procurehere.core.entity;

import java.io.Serializable;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * @author Priyanka Singh
 */

@Entity
@Table(name = "PROC_RFP_EVENT_AWARD")
public class RfpEventAward extends EventAward implements Serializable {

	private static final long serialVersionUID = -5023012895574297728L;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, optional = true)
	@JoinColumn(name = "EVENT_ID", foreignKey = @ForeignKey(name = "FK_RFP_EVENT_AWD"))
	private RfpEvent rfxEvent;

	@ManyToOne(optional = false, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "BQ_ID", nullable = false, foreignKey = @ForeignKey(name = "FK_RFP_EVNT_AWD_BQ"))
	private RfpEventBq bq;

	@JsonIgnore
	@OrderBy("bqItem ASC")
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "eventAward",  orphanRemoval = true, cascade = CascadeType.ALL)
	List<RfpEventAwardDetails> rfxAwardDetails;
	
	@Transient
	private List<RfpEventAwardApproval> awardApprovals;
	
	@Transient
	private List<RfpAwardComment> awardComments;

	/**
	 * @return the rfxEvent
	 */
	public RfpEvent getRfxEvent() {
		return rfxEvent;
	}

	/**
	 * @param rfxEvent the rfxEvent to set
	 */
	public void setRfxEvent(RfpEvent rfxEvent) {
		this.rfxEvent = rfxEvent;
	}

	/**
	 * @return the bq
	 */
	public RfpEventBq getBq() {
		return bq;
	}

	/**
	 * @param bq the bq to set
	 */
	public void setBq(RfpEventBq bq) {
		this.bq = bq;
	}

	/**
	 * @return the rfxAwardDetails
	 */
	public List<RfpEventAwardDetails> getRfxAwardDetails() {
		return rfxAwardDetails;
	}

	/**
	 * @param rfxAwardDetails the rfxAwardDetails to set
	 */
	public void setRfxAwardDetails(List<RfpEventAwardDetails> rfxAwardDetails) {
		this.rfxAwardDetails = rfxAwardDetails;
	}
	
	/**
	 * @return the awardApprovals
	 */
	public List<RfpEventAwardApproval> getAwardApprovals() {
		return awardApprovals;
	}

	/**
	 * @param awardApprovals the awardApprovals to set
	 */
	public void setAwardApprovals(List<RfpEventAwardApproval> awardApprovals) {
		this.awardApprovals = awardApprovals;
	}
	
	/**
	 * @return the awardComments
	 */
	public List<RfpAwardComment> getAwardComments() {
		return awardComments;
	}

	/**
	 * @param awardComments the awardComments to set
	 */
	public void setAwardComments(List<RfpAwardComment> awardComments) {
		this.awardComments = awardComments;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "RfpEventAward [rfxEvent=" + rfxEvent + ", bq=" + bq + ", rfxAwardDetails=" + rfxAwardDetails + "]";
	}

}
