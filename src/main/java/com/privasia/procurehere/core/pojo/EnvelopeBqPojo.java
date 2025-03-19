package com.privasia.procurehere.core.pojo;

import java.io.Serializable;
import java.util.List;

public class EnvelopeBqPojo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3062276684599726481L;

	String bqName;
	List<EvaluationSuppliersBqPojo> supplierBq;

	List<EvaluationSuppliersBqPojo> topSupplierBq;

	public String getBqName() {
		return bqName;
	}

	public void setBqName(String bqName) {
		this.bqName = bqName;
	}

	public List<EvaluationSuppliersBqPojo> getSupplierBq() {
		return supplierBq;
	}

	public void setSupplierBq(List<EvaluationSuppliersBqPojo> supplierBq) {
		this.supplierBq = supplierBq;
	}

	public List<EvaluationSuppliersBqPojo> getTopSupplierBq() {
		return topSupplierBq;
	}

	public void setTopSupplierBq(List<EvaluationSuppliersBqPojo> topSupplierBq) {
		this.topSupplierBq = topSupplierBq;
	}

}
