package com.privasia.procurehere.core.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.privasia.procurehere.core.enums.PlanStatus;
import com.privasia.procurehere.core.enums.PlanType;
import com.privasia.procurehere.core.utils.CustomDateTimeSerializer;

/**
 * @author parveen
 */
@Entity
@Table(name = "PROC_BUYER_PLAN")
public class BuyerPlan implements Serializable {

	private static final long serialVersionUID = 1012158777471115285L;

	@Id
	@GenericGenerator(name = "idGen", strategy = "uuid.hex")
	@GeneratedValue(generator = "idGen")
	@Column(name = "ID", length = 64, nullable = false)
	private String id;

	@NotNull
	@Digits(integer = 3, fraction = 0)
	@Column(name = "PLAN_ORDER", length = 3, nullable = false)
	private Integer planOrder;

	@NotNull
	@Size(min = 1, max = 128)
	@Column(name = "PLAN_NAME", length = 128, nullable = false)
	private String planName;

	@NotNull
	@Size(min = 1, max = 150)
	@Column(name = "SHORT_DESCRIPTION", length = 150, nullable = false)
	private String shortDescription;

	@Size(min = 0, max = 2000)
	@Column(name = "DESCRIPTION", length = 2000, nullable = true)
	private String description;

	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(name = "PLAN_TYPE", length = 32, nullable = false)
	private PlanType planType;

	@JsonIgnore
	@NotNull
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "CURRENCY_CODE", foreignKey = @ForeignKey(name = "FK_BUY_PLAN_CURRENCY"), nullable = false)
	private Currency currency;

	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(name = "PLAN_STATUS", length = 32, nullable = false)
	private PlanStatus planStatus;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "buyerPlan", orphanRemoval = true, cascade = CascadeType.ALL)
	@OrderBy("rangeStart ASC")
	private List<PlanRange> rangeList;

	@JsonIgnore
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "buyerPlan", orphanRemoval = true, cascade = CascadeType.ALL)
	@OrderBy("planDuration")
	private List<PlanPeriod> planPeriodList;

	@ManyToOne(fetch = FetchType.LAZY, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "CREATED_BY", nullable = false, foreignKey = @ForeignKey(name = "FK_BUY_PLAN_CREATED_BY"))
	private User createdBy;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CREATED_DATE", nullable = false)
	private Date createdDate;

	@ManyToOne(fetch = FetchType.LAZY, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "MODIFIED_BY", nullable = true, foreignKey = @ForeignKey(name = "FK_BUY_PLAN_MODIFIED_BY"))
	private User modifiedBy;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "MODIFIED_DATE")
	private Date modifiedDate;

	@Column(name = "BASE_PRICE", precision = 20, scale = 4)
	private BigDecimal basePrice;

	@Column(name = "BASE_USERS", length = 4)
	private Integer baseUsers;

	@Column(name = "TAX", nullable = true, precision = 20, scale = 4)
	private BigDecimal tax;

	@Transient
	private String currencyCode;

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the planOrder
	 */
	public Integer getPlanOrder() {
		return planOrder;
	}

	/**
	 * @param planOrder the planOrder to set
	 */
	public void setPlanOrder(Integer planOrder) {
		this.planOrder = planOrder;
	}

	/**
	 * @return the planName
	 */
	public String getPlanName() {
		return planName;
	}

	/**
	 * @param planName the planName to set
	 */
	public void setPlanName(String planName) {
		this.planName = planName;
	}

	/**
	 * @return the shortDescription
	 */
	public String getShortDescription() {
		return shortDescription;
	}

	/**
	 * @param shortDescription the shortDescription to set
	 */
	public void setShortDescription(String shortDescription) {
		this.shortDescription = shortDescription;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return the planType
	 */
	public PlanType getPlanType() {
		return planType;
	}

	/**
	 * @param planType the planType to set
	 */
	public void setPlanType(PlanType planType) {
		this.planType = planType;
	}

	/**
	 * @return the currency
	 */
	public Currency getCurrency() {
		return currency;
	}

	/**
	 * @param currency the currency to set
	 */
	public void setCurrency(Currency currency) {
		this.currency = currency;
	}

	/**
	 * @return the planStatus
	 */
	public PlanStatus getPlanStatus() {
		return planStatus;
	}

	/**
	 * @param planStatus the planStatus to set
	 */
	public void setPlanStatus(PlanStatus planStatus) {
		this.planStatus = planStatus;
	}

	/**
	 * @return the rangeList
	 */
	public List<PlanRange> getRangeList() {
		return rangeList;
	}

	/**
	 * @param rangeList the rangeList to set
	 */
	public void setRangeList(List<PlanRange> rangeList) {
		if (this.rangeList == null) {
			this.rangeList = new ArrayList<PlanRange>();
		} else {
			this.rangeList.clear();
		}
		if (rangeList != null) {
			this.rangeList.addAll(rangeList);
		}
	}

	/**
	 * @return the planPeriodList
	 */
	public List<PlanPeriod> getPlanPeriodList() {
		return planPeriodList;
	}

	/**
	 * @param planPeriodList the planPeriodList to set
	 */
	public void setPlanPeriodList(List<PlanPeriod> planPeriodList) {
		if (this.planPeriodList == null) {
			this.planPeriodList = new ArrayList<PlanPeriod>();
		} else {
			this.planPeriodList.clear();
		}
		if (planPeriodList != null) {
			this.planPeriodList.addAll(planPeriodList);
		}
	}

	/**
	 * @return the createdBy
	 */
	public User getCreatedBy() {
		return createdBy;
	}

	/**
	 * @param createdBy the createdBy to set
	 */
	public void setCreatedBy(User createdBy) {
		this.createdBy = createdBy;
	}

	/**
	 * @return the createdDate
	 */
	public Date getCreatedDate() {
		return createdDate;
	}

	/**
	 * @param createdDate the createdDate to set
	 */
	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	/**
	 * @return the modifiedBy
	 */
	public User getModifiedBy() {
		return modifiedBy;
	}

	/**
	 * @param modifiedBy the modifiedBy to set
	 */
	public void setModifiedBy(User modifiedBy) {
		this.modifiedBy = modifiedBy;
	}

	/**
	 * @return the modifiedDate
	 */
	public Date getModifiedDate() {
		return modifiedDate;
	}

	/**
	 * @param modifiedDate the modifiedDate to set
	 */
	public void setModifiedDate(Date modifiedDate) {
		this.modifiedDate = modifiedDate;
	}

	/**
	 * @return the currencyCode
	 */
	public String getCurrencyCode() {
		return currencyCode;
	}

	/**
	 * @param currencyCode the currencyCode to set
	 */
	public void setCurrencyCode(String currencyCode) {
		this.currencyCode = currencyCode;
	}

	/**
	 * @return the basePrice
	 */
	public BigDecimal getBasePrice() {
		return basePrice;
	}

	/**
	 * @param basePrice the basePrice to set
	 */
	public void setBasePrice(BigDecimal basePrice) {
		this.basePrice = basePrice;
	}

	/**
	 * @return the baseUsers
	 */
	public Integer getBaseUsers() {
		return baseUsers;
	}

	/**
	 * @param baseUsers the baseUsers to set
	 */
	public void setBaseUsers(Integer baseUsers) {
		this.baseUsers = baseUsers;
	}

	/**
	 * @return the tax
	 */
	public BigDecimal getTax() {
		return tax;
	}

	/**
	 * @param tax the tax to set
	 */
	public void setTax(BigDecimal tax) {
		this.tax = tax;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((createdDate == null) ? 0 : createdDate.hashCode());
		result = prime * result + ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((modifiedDate == null) ? 0 : modifiedDate.hashCode());
		result = prime * result + ((planName == null) ? 0 : planName.hashCode());
		result = prime * result + ((planOrder == null) ? 0 : planOrder.hashCode());
		result = prime * result + ((planStatus == null) ? 0 : planStatus.hashCode());
		result = prime * result + ((planType == null) ? 0 : planType.hashCode());
		result = prime * result + ((shortDescription == null) ? 0 : shortDescription.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
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
		BuyerPlan other = (BuyerPlan) obj;
		if (createdDate == null) {
			if (other.createdDate != null)
				return false;
		} else if (!createdDate.equals(other.createdDate))
			return false;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (modifiedDate == null) {
			if (other.modifiedDate != null)
				return false;
		} else if (!modifiedDate.equals(other.modifiedDate))
			return false;
		if (planName == null) {
			if (other.planName != null)
				return false;
		} else if (!planName.equals(other.planName))
			return false;
		if (planOrder == null) {
			if (other.planOrder != null)
				return false;
		} else if (!planOrder.equals(other.planOrder))
			return false;
		if (planStatus != other.planStatus)
			return false;
		if (planType != other.planType)
			return false;
		if (shortDescription == null) {
			if (other.shortDescription != null)
				return false;
		} else if (!shortDescription.equals(other.shortDescription))
			return false;
		return true;
	}

}
