package com.privasia.procurehere.core.pojo;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author sarang
 */
public class SupplierBqPojo implements Serializable{
	
	private static final long serialVersionUID = 4366753023653147773L;
	private BigDecimal additionalTax;
	private BigDecimal grandTotal;
	private BigDecimal revisedGrandTotal;
	private BigDecimal totalAfterTax;

	public BigDecimal getAdditionalTax() {
		return additionalTax;
	}

	public void setAdditionalTax(BigDecimal additionalTax) {
		this.additionalTax = additionalTax;
	}

	public BigDecimal getGrandTotal() {
		return grandTotal;
	}

	public void setGrandTotal(BigDecimal grandTotal) {
		this.grandTotal = grandTotal;
	}

	public BigDecimal getRevisedGrandTotal() {
		return revisedGrandTotal;
	}

	public void setRevisedGrandTotal(BigDecimal revisedGrandTotal) {
		this.revisedGrandTotal = revisedGrandTotal;
	}

	public BigDecimal getTotalAfterTax() {
		return totalAfterTax;
	}

	public void setTotalAfterTax(BigDecimal totalAfterTax) {
		this.totalAfterTax = totalAfterTax;
	}

}
