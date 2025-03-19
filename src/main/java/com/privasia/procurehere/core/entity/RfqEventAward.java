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
@Table(name = "PROC_RFQ_EVENT_AWARD")
public class RfqEventAward extends EventAward implements Serializable {

	private static final long serialVersionUID = -5023012895574297728L;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, optional = true)
	@JoinColumn(name = "EVENT_ID", foreignKey = @ForeignKey(name = "FK_RFQ_EVENT_AWD"))
	private RfqEvent rfxEvent;

	@ManyToOne(optional = false, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "BQ_ID", nullable = false, foreignKey = @ForeignKey(name = "FK_RFQ_EVNT_AWD_BQ"))
	private RfqEventBq bq;

	@JsonIgnore
	@OrderBy("bqItem ASC")
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "eventAward",  orphanRemoval = true, cascade = CascadeType.ALL)
	List<RfqEventAwardDetails> rfxAwardDetails;
	
	@Transient
	private List<RfqEventAwardApproval> awardApprovals;

	/**
	 * @return the rfxEvent
	 */
	public RfqEvent getRfxEvent() {
		return rfxEvent;
	}

	/**
	 * @param rfxEvent the rfxEvent to set
	 */
	public void setRfxEvent(RfqEvent rfxEvent) {
		this.rfxEvent = rfxEvent;
	}

	/**
	 * @return the bq
	 */
	public RfqEventBq getBq() {
		return bq;
	}

	/**
	 * @param bq the bq to set
	 */
	public void setBq(RfqEventBq bq) {
		this.bq = bq;
	}

	/**
	 * @return the rfxAwardDetails
	 */
	public List<RfqEventAwardDetails> getRfxAwardDetails() {
		return rfxAwardDetails;
	}

	/**
	 * @param rfxAwardDetails the rfxAwardDetails to set
	 */
	public void setRfxAwardDetails(List<RfqEventAwardDetails> rfxAwardDetails) {
		this.rfxAwardDetails = rfxAwardDetails;
	}
	
	/**
	 * @return the awardApprovals
	 */
	public List<RfqEventAwardApproval> getAwardApprovals() {
		return awardApprovals;
	}

	/**
	 * @param awardApprovals the awardApprovals to set
	 */
	public void setAwardApprovals(List<RfqEventAwardApproval> awardApprovals) {
		this.awardApprovals = awardApprovals;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "RfqEventAward [rfxEvent=" + rfxEvent + ", bq=" + bq + ", rfxAwardDetails=" + rfxAwardDetails + "]";
	}

}
