package com.privasia.procurehere.core.pojo;

import java.io.Serializable;

public class RfxEnvelopPojo implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	private String rfxEnvelope;
	private Integer rfxSequence;
	/**
	 * @return the rfxEnvelope
	 */
	public String getRfxEnvelope() {
		return rfxEnvelope;
	}
	/**
	 * @param rfxEnvelope the rfxEnvelope to set
	 */
	public void setRfxEnvelope(String rfxEnvelope) {
		this.rfxEnvelope = rfxEnvelope;
	}
	/**
	 * @return the rfxSequence
	 */
	public Integer getRfxSequence() {
		return rfxSequence;
	}
	/**
	 * @param rfxSequence the rfxSequence to set
	 */
	public void setRfxSequence(Integer rfxSequence) {
		this.rfxSequence = rfxSequence;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "RfxEnvelopPojo [rfxEnvelope=" + rfxEnvelope + ", rfxSequence=" + rfxSequence + "]";
	}
	
	
	
	



}
