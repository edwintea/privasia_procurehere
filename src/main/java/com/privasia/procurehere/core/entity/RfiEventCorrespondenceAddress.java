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
@Table(name = "PROC_RFI_EVENT_ADDRESS")
public class RfiEventCorrespondenceAddress extends EventCorrespondenceAddress implements Serializable {

	private static final long serialVersionUID = 4615867409084451629L;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, optional = true)
	@JoinColumn(name = "EVENT_ID", foreignKey = @ForeignKey(name = "FK_RFI_EVENT_ADDRESS") )
	private RfiEvent rfxEvent;

	
	public RfiEventCorrespondenceAddress copyFrom(RfiEvent rfiEvent) {
		RfiEventCorrespondenceAddress correspondenceAddress = new RfiEventCorrespondenceAddress();
		correspondenceAddress.setCity(getCity());
		correspondenceAddress.setLine1(getLine1());
		correspondenceAddress.setLine2(getLine2());
		correspondenceAddress.setLine3(getLine3());
		correspondenceAddress.setRfxEvent(rfiEvent);
		correspondenceAddress.setState(getState());
		correspondenceAddress.setZip(getZip());
		return correspondenceAddress;
	}
	


	/**
	 * @return the rfxEvent
	 */
	public RfiEvent getRfxEvent() {
		return rfxEvent;
	}



	/**
	 * @param rfxEvent the rfxEvent to set
	 */
	public void setRfxEvent(RfiEvent rfxEvent) {
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
		return "RfiEventAddress [ " + toLogString() + "]";
	}

}
