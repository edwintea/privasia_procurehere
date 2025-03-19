/**
 * 
 */
package com.privasia.procurehere.core.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * @author RT-Kapil
 */
@Entity
@Table(name = "PROC_RFA_EVENT_ADDRESS")
public class RfaEventCorrespondenceAddress extends EventCorrespondenceAddress implements Serializable {

	private static final long serialVersionUID = 4137751896034971123L;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, optional = true)
	@JoinColumn(name = "EVENT_ID", foreignKey = @ForeignKey(name = "FK_RFA_EVENT_ADDRESS"))
	private RfaEvent rfaEvent;

	public RfaEventCorrespondenceAddress copyFrom(RfaEvent rfaEvent) {
		RfaEventCorrespondenceAddress correspondenceAddress = new RfaEventCorrespondenceAddress();
		correspondenceAddress.setCity(getCity());
		correspondenceAddress.setLine1(getLine1());
		correspondenceAddress.setLine2(getLine2());
		correspondenceAddress.setLine3(getLine3());
		correspondenceAddress.setRfaEvent(rfaEvent);
		correspondenceAddress.setState(getState());
		correspondenceAddress.setZip(getZip());
		return correspondenceAddress;
	}

	/**
	 * @return the rfaEvent
	 */
	public RfaEvent getRfaEvent() {
		return rfaEvent;
	}

	/**
	 * @param rfaEvent the rfaEvent to set
	 */
	public void setRfaEvent(RfaEvent rfaEvent) {
		this.rfaEvent = rfaEvent;
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
		return "RfaEventAddress [toLogString : " + super.toLogString() + "]";
	}

}
