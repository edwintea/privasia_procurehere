/**
 * 
 */
package com.privasia.procurehere.core.entity;

import java.io.Serializable;

/**
 * @author Ravi
 */
public class SupplierBqItem extends BqItem implements Serializable {

	private static final long serialVersionUID = 5514524970325579457L;

	private RfpSupplierBq supplierBq;

	/**
	 * @return the supplierBq
	 */
	public RfpSupplierBq getSupplierBq() {
		return supplierBq;
	}

	/**
	 * @param supplierBq the supplierBq to set
	 */
	public void setSupplierBq(RfpSupplierBq supplierBq) {
		this.supplierBq = supplierBq;
	}


}
