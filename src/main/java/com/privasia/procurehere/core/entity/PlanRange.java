package com.privasia.procurehere.core.entity;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.GenericGenerator;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * @author parveen
 */
@Entity
@Table(name = "PROC_PLAN_RANGE")
public class PlanRange implements Serializable {

	private static final long serialVersionUID = 1012158777471115285L;

	@Id
	@GenericGenerator(name = "idGen", strategy = "uuid.hex")
	@GeneratedValue(generator = "idGen")
	@Column(name = "ID", length = 64, nullable = false)
	private String id;

	@Column(name = "DISPLAY_LABEL", length = 100)
	private String displayLabel;

	@Column(name = "RANGE_START", length = 4, nullable = false)
	private Integer rangeStart;

	@Column(name = "RANGE_END", length = 4, nullable = false)
	private Integer rangeEnd;

	@NotNull
	// @DecimalMax("9999")
	@Column(name = "PRICE", precision = 20, scale = 4, nullable = false)
	private BigDecimal price;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "BUYER_PLAN_ID", foreignKey = @ForeignKey(name = "FK_BUY_PLAN_RANGE"))
	private BuyerPlan buyerPlan;

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the displayLabel
	 */
	public String getDisplayLabel() {
		return displayLabel;
	}

	/**
	 * @param displayLabel
	 *            the displayLabel to set
	 */
	public void setDisplayLabel(String displayLabel) {
		this.displayLabel = displayLabel;
	}

	/**
	 * @return the rangeStart
	 */
	public Integer getRangeStart() {
		return rangeStart;
	}

	/**
	 * @param rangeStart
	 *            the rangeStart to set
	 */
	public void setRangeStart(Integer rangeStart) {
		this.rangeStart = rangeStart;
	}

	/**
	 * @return the rangeEnd
	 */
	public Integer getRangeEnd() {
		return rangeEnd;
	}

	/**
	 * @param rangeEnd
	 *            the rangeEnd to set
	 */
	public void setRangeEnd(Integer rangeEnd) {
		this.rangeEnd = rangeEnd;
	}

	/**
	 * @return the price
	 */
	public BigDecimal getPrice() {
		return price;
	}

	/**
	 * @param price
	 *            the price to set
	 */
	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	/**
	 * 
	 * @return
	 */
	public BuyerPlan getBuyerPlan() {
		return buyerPlan;
	}

	/**
	 * 
	 * @param buyerPlan
	 */
	public void setBuyerPlan(BuyerPlan buyerPlan) {
		this.buyerPlan = buyerPlan;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((displayLabel == null) ? 0 : displayLabel.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((price == null) ? 0 : price.hashCode());
		result = prime * result + ((rangeEnd == null) ? 0 : rangeEnd.hashCode());
		result = prime * result + ((rangeStart == null) ? 0 : rangeStart.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PlanRange other = (PlanRange) obj;
		if (displayLabel == null) {
			if (other.displayLabel != null)
				return false;
		} else if (!displayLabel.equals(other.displayLabel))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (price == null) {
			if (other.price != null)
				return false;
		} else if (!price.equals(other.price))
			return false;
		if (rangeEnd == null) {
			if (other.rangeEnd != null)
				return false;
		} else if (!rangeEnd.equals(other.rangeEnd))
			return false;
		if (rangeStart == null) {
			if (other.rangeStart != null)
				return false;
		} else if (!rangeStart.equals(other.rangeStart))
			return false;
		return true;
	}

}
