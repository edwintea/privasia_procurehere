package com.privasia.procurehere.core.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * @author parveen
 */
@Entity
@Table(name = "PROC_PLAN_PERIOD")
public class PlanPeriod implements Serializable {

	private static final long serialVersionUID = -6515663946542263817L;

	@Id
	@GenericGenerator(name = "idGen", strategy = "uuid.hex")
	@GeneratedValue(generator = "idGen")
	@Column(name = "ID", length = 64, nullable = false)
	private String id;

	@Column(name = "PLAN_DURATION", length = 2, nullable = false)
	private Integer planDuration;

	@Column(name = "PLAN_DISCOUNT", length = 2, nullable = false)
	private Integer planDiscount;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "BUYER_PLAN_ID", foreignKey = @ForeignKey(name = "FK_BUY_PLAN_PERIOD"))
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
	 * @return the planDuration
	 */
	public Integer getPlanDuration() {
		return planDuration;
	}

	/**
	 * @param planDuration
	 *            the planDuration to set
	 */
	public void setPlanDuration(Integer planDuration) {
		this.planDuration = planDuration;
	}

	/**
	 * @return the planDiscount
	 */
	public Integer getPlanDiscount() {
		return planDiscount;
	}

	/**
	 * @param planDiscount
	 *            the planDiscount to set
	 */
	public void setPlanDiscount(Integer planDiscount) {
		this.planDiscount = planDiscount;
	}

	/**
	 * @return the buyerPlan
	 */
	public BuyerPlan getBuyerPlan() {
		return buyerPlan;
	}

	/**
	 * @param buyerPlan
	 *            the buyerPlan to set
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
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((planDiscount == null) ? 0 : planDiscount.hashCode());
		result = prime * result + ((planDuration == null) ? 0 : planDuration.hashCode());
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
		PlanPeriod other = (PlanPeriod) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (planDiscount == null) {
			if (other.planDiscount != null)
				return false;
		} else if (!planDiscount.equals(other.planDiscount))
			return false;
		if (planDuration == null) {
			if (other.planDuration != null)
				return false;
		} else if (!planDuration.equals(other.planDuration))
			return false;
		return true;
	}

}
