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
 * @author Ravi
 */
@Entity
@Table(name = "PROC_RFP_EVENT_ADDRESS")
public class RfpEventCorrespondenceAddress extends EventCorrespondenceAddress implements Serializable {

	private static final long serialVersionUID = -4459826104024499376L;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, optional = true)
	@JoinColumn(name = "EVENT_ID", foreignKey = @ForeignKey(name = "FK_RFP_EVENT_ADDRESS") )
	private RfpEvent rfxEvent;

	public RfpEventCorrespondenceAddress copyFrom(RfpEvent rfpEvent) {
		RfpEventCorrespondenceAddress correspondenceAddress = new RfpEventCorrespondenceAddress();
		correspondenceAddress.setCity(getCity());
		correspondenceAddress.setLine1(getLine1());
		correspondenceAddress.setLine2(getLine2());
		correspondenceAddress.setLine3(getLine3());
		correspondenceAddress.setRfxEvent(rfpEvent);
		correspondenceAddress.setState(getState());
		correspondenceAddress.setZip(getZip());
		return correspondenceAddress;
	}

	public RfpEventCorrespondenceAddress copyFrom(RftEvent copyEvent) {
		RfpEventCorrespondenceAddress correspondenceAddress = new RfpEventCorrespondenceAddress();
		correspondenceAddress.setCity(getCity());
		correspondenceAddress.setLine1(getLine1());
		correspondenceAddress.setLine2(getLine2());
		correspondenceAddress.setLine3(getLine3());
		//correspondenceAddress.setRfxEvent(rfpEvent);
		correspondenceAddress.setState(getState());
		correspondenceAddress.setZip(getZip());
		return correspondenceAddress;
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
		return "RfpEventAddress [ " + toLogString() + "]";
	}

	

}
