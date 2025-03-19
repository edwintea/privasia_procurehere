package com.privasia.procurehere.core.pojo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.privasia.procurehere.core.entity.Envelop;
import com.privasia.procurehere.core.entity.EventSupplier;
import com.privasia.procurehere.core.entity.RftSupplierCqItem;

public class EnvelopeSubmissionReportPojo implements Serializable {

	private static final long serialVersionUID = 5803109225742111091L;

	private Envelop envelope;

	List<EventSupplier> envelopeSupplier = new ArrayList<EventSupplier>();

	List<RftSupplierCqItem> rftCqItemList = new ArrayList<RftSupplierCqItem>();

	/**
	 * @return the envelope
	 */
	public Envelop getEnvelope() {
		return envelope;
	}

	/**
	 * @param envelope the envelope to set
	 */
	public void setEnvelope(Envelop envelope) {
		this.envelope = envelope;
	}

	/**
	 * @return the envelopeSupplier
	 */
	public List<EventSupplier> getEnvelopeSupplier() {
		return envelopeSupplier;
	}

	/**
	 * @param envelopeSupplier the envelopeSupplier to set
	 */
	public void setEnvelopeSupplier(List<EventSupplier> envelopeSupplier) {
		this.envelopeSupplier = envelopeSupplier;
	}

	/**
	 * @return the rftCqItemList
	 */
	public List<RftSupplierCqItem> getRftCqItemList() {
		return rftCqItemList;
	}

	/**
	 * @param rftCqItemList the rftCqItemList to set
	 */
	public void setRftCqItemList(List<RftSupplierCqItem> rftCqItemList) {
		this.rftCqItemList = rftCqItemList;
	}
}
