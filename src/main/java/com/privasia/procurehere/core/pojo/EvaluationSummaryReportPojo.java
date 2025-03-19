package com.privasia.procurehere.core.pojo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.privasia.procurehere.core.entity.EventSupplier;
import com.privasia.procurehere.core.entity.RftCq;
import com.privasia.procurehere.core.entity.RftEvent;

public class EvaluationSummaryReportPojo implements Serializable {

	private static final long serialVersionUID = 8841971305712037415L;

	private RftEvent rftEvent;

	List<EventSupplier> supplierList = new ArrayList<EventSupplier>();
	List<RftCq> cqList = new ArrayList<RftCq>();

	/**
	 * @return the rftEvent
	 */
	public RftEvent getRftEvent() {
		return rftEvent;
	}

	/**
	 * @param rftEvent the rftEvent to set
	 */
	public void setRftEvent(RftEvent rftEvent) {
		this.rftEvent = rftEvent;
	}

	/**
	 * @return the supplierList
	 */
	public List<EventSupplier> getSupplierList() {
		return supplierList;
	}

	/**
	 * @param supplierList the supplierList to set
	 */
	public void setSupplierList(List<EventSupplier> supplierList) {
		this.supplierList = supplierList;
	}

	/**
	 * @return the cqList
	 */
	public List<RftCq> getCqList() {
		return cqList;
	}

	/**
	 * @param cqList the cqList to set
	 */
	public void setCqList(List<RftCq> cqList) {
		this.cqList = cqList;
	}

}
