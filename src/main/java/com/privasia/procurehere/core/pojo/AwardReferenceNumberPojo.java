package com.privasia.procurehere.core.pojo;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 
 * @author Priyanka Ghadage
 *
 */
public class AwardReferenceNumberPojo implements Serializable {

	private static final long serialVersionUID = 7307586918621103651L;

	private Integer level;

	private Integer order;

	private String referenceNumber;
	
	private String supllierCompanyName;
	
	private BigDecimal awardedPrice;

	public AwardReferenceNumberPojo() {
	}

	public AwardReferenceNumberPojo(Integer level, Integer order, String referenceNumber) {
		this.level = level;
		this.order = order;
		this.referenceNumber = referenceNumber;
	}
	
	public AwardReferenceNumberPojo(String supllierCompanyName, BigDecimal awardedPrice) {
		this.supllierCompanyName = supllierCompanyName;
		this.awardedPrice = awardedPrice;
	}

	/**
	 * @return the level
	 */
	public Integer getLevel() {
		return level;
	}

	/**
	 * @param level the level to set
	 */
	public void setLevel(Integer level) {
		this.level = level;
	}

	/**
	 * @return the order
	 */
	public Integer getOrder() {
		return order;
	}

	/**
	 * @param order the order to set
	 */
	public void setOrder(Integer order) {
		this.order = order;
	}

	/**
	 * @return the referenceNumber
	 */
	public String getReferenceNumber() {
		return referenceNumber;
	}

	/**
	 * @param referenceNumber the referenceNumber to set
	 */
	public void setReferenceNumber(String referenceNumber) {
		this.referenceNumber = referenceNumber;
	}
	
	/**
	 * @return the supllierCompanyName
	 */
	public String getSupllierCompanyName() {
		return supllierCompanyName;
	}

	/**
	 * @param supllierCompanyName the supllierCompanyName to set
	 */
	public void setSupllierCompanyName(String supllierCompanyName) {
		this.supllierCompanyName = supllierCompanyName;
	}

	/**
	 * @return the awardedPrice
	 */
	public BigDecimal getAwardedPrice() {
		return awardedPrice;
	}

	/**
	 * @param awardedPrice the awardedPrice to set
	 */
	public void setAwardedPrice(BigDecimal awardedPrice) {
		this.awardedPrice = awardedPrice;
	}

	public String toLogString() {
		return "AwardReferenceNumber [level=" + level + ", order=" + order + ", referenceNumber=" + referenceNumber + "]";
	}
}

