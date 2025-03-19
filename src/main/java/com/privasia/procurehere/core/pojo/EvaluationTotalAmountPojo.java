/**
 * 
 */
package com.privasia.procurehere.core.pojo;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author Giridhar
 */
public class EvaluationTotalAmountPojo implements Serializable {

	private static final long serialVersionUID = -3204573042237565264L;

	private String supplierId;
	private String bqId;
	private BigDecimal totalAmount;
	private BigDecimal subtotal;
	/**
	 * @return the subtotal
	 */
	public BigDecimal getSubtotal() {
		return subtotal;
	}

	/**
	 * @param subtotal the subtotal to set
	 */
	public void setSubtotal(BigDecimal subtotal) {
		this.subtotal = subtotal;
	}

	private boolean commentExisit = false;

	public EvaluationTotalAmountPojo() {
		this.commentExisit = false;
	}

	/**
	 * @return the supplierId
	 */
	public String getSupplierId() {
		return supplierId;
	}

	/**
	 * @param supplierId the supplierId to set
	 */
	public void setSupplierId(String supplierId) {
		this.supplierId = supplierId;
	}

	/**
	 * @return the bqId
	 */
	public String getBqId() {
		return bqId;
	}

	/**
	 * @param bqId the bqId to set
	 */
	public void setBqId(String bqId) {
		this.bqId = bqId;
	}

	/**
	 * @return the totalAmount
	 */
	public BigDecimal getTotalAmount() {
		return totalAmount;
	}

	/**
	 * @param totalAmount the totalAmount to set
	 */
	public void setTotalAmount(BigDecimal totalAmount) {
		this.totalAmount = totalAmount;
	}

	/**
	 * @return the commentExisit
	 */
	public boolean isCommentExisit() {
		return commentExisit;
	}

	/**
	 * @param commentExisit the commentExisit to set
	 */
	public void setCommentExisit(boolean commentExisit) {
		this.commentExisit = commentExisit;
	}

}
