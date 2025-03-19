/**
 * 
 */
package com.privasia.procurehere.core.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.privasia.procurehere.core.enums.SubmissionStatusType;

/**
 * @author Ravi
 */
@Entity
@Table(name = "PROC_RFP_EVENT_SUPPLIERS")
public class RfpEventSupplier extends EventSupplier implements Serializable {

	private static final long serialVersionUID = 5717588927463767852L;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, optional = true)
	@JoinColumn(name = "EVENT_ID", foreignKey = @ForeignKey(name = "FK_RFP_INV_SUP_EVENT") )
	private RfpEvent rfxEvent;

	@JsonIgnore
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "eventSupplier", orphanRemoval = true, cascade = CascadeType.ALL)
	private List<RfpSupplierTeamMember> teamMembers;
	
	@ManyToOne(fetch = FetchType.LAZY, optional = true, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "DISQUALIFIED_ENVELOPE", nullable = true)
	private RfpEnvelop disqualifiedEnvelope;

	public RfpEventSupplier(String id, SubmissionStatusType submissionStatus) {
		setId(id);
		setSubmissionStatus(submissionStatus);
	}
	
	public RfpEventSupplier() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @return the disqualifiedEnvelope
	 */
	public RfpEnvelop getDisqualifiedEnvelope() {
		return disqualifiedEnvelope;
	}

	/**
	 * @param disqualifiedEnvelope the disqualifiedEnvelope to set
	 */
	public void setDisqualifiedEnvelope(RfpEnvelop disqualifiedEnvelope) {
		this.disqualifiedEnvelope = disqualifiedEnvelope;
	}

	public RfpEventSupplier createCopy() {
		RfpEventSupplier supplier = new RfpEventSupplier();
		supplier.setSupplier(getSupplier());
		return supplier;
	}
	
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
	 * @return the teamMembers
	 */
	public List<RfpSupplierTeamMember> getTeamMembers() {
		return teamMembers;
	}

	/**
	 * @param teamMembers the teamMembers to set
	 */
	public void setTeamMembers(List<RfpSupplierTeamMember> teamMembers) {
		if (this.teamMembers == null) {
			this.teamMembers = new ArrayList<RfpSupplierTeamMember>();
		} else {
			this.teamMembers.clear();
		}
		if (teamMembers != null) {
			this.teamMembers.addAll(teamMembers);
		}
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
		return "RfpEventContact [ " + super.toLogString() + "]";
	}

}
