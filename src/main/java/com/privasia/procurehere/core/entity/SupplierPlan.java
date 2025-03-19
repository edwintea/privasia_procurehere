/**
 * 
 */
package com.privasia.procurehere.core.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
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
import com.privasia.procurehere.core.enums.ChargeModel;
import com.privasia.procurehere.core.enums.PeriodUnitType;
import com.privasia.procurehere.core.enums.PlanStatus;
import com.privasia.procurehere.core.enums.converter.ChargeModelCoverter;
import com.privasia.procurehere.core.pojo.RecaptchaForm;
import com.privasia.procurehere.core.utils.CustomDateTimeSerializer;

/**
 * @author Nitin Otageri
 */
@Entity
@Table(name = "PROC_SUPPLIER_PLAN")
public class SupplierPlan extends RecaptchaForm implements Serializable {

	private static final long serialVersionUID = 6481963153383990379L;

	@Id
	@GenericGenerator(name = "idGen", strategy = "uuid.hex")
	@GeneratedValue(generator = "idGen")
	@Column(name = "ID", length = 64)
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
	@Digits(integer = 10, fraction = 0)
	@Column(name = "PRICE", length = 10, nullable = false)
	private Integer price;

	@JsonIgnore
	@NotNull
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "CURRENCY_CODE", foreignKey = @ForeignKey(name = "FK_SUP_PLAN_CURRENCY"), nullable = false)
	private Currency currency;

	@NotNull
	@Digits(integer = 6, fraction = 0)
	@Column(name = "PLAN_PERIOD", length = 6, nullable = false)
	private Integer period;

	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(name = "PLAN_PERIOD_UNIT")
	private PeriodUnitType periodUnit;

	@NotNull
	@Convert(converter = ChargeModelCoverter.class)
	@Column(name = "CHARGE_MODEL", length = 50, nullable = false)
	private ChargeModel chargeModel;

	@Column(name = "BUYER_LIMIT", length = 6, nullable = false)
	private Integer buyerLimit;

	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(name = "PLAN_STATUS", nullable = false)
	private PlanStatus planStatus;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "ARCHIVE_DATE", nullable = true)
	private Date archiveDate;

	@ManyToOne(fetch = FetchType.LAZY, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "CREATED_BY", nullable = false, foreignKey = @ForeignKey(name = "FK_SUP_PLAN_CREATED_BY"))
	private User createdBy;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CREATED_DATE", nullable = false)
	private Date createdDate;

	@ManyToOne(fetch = FetchType.LAZY, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "MODIFIED_BY", nullable = true, foreignKey = @ForeignKey(name = "FK_SUP_PLAN_MODIFIED_BY"))
	private User modifiedBy;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "MODIFIED_DATE")
	private Date modifiedDate;
	
	@Column(name = "TAX", nullable = true, precision = 20, scale = 4)
	private BigDecimal tax;

	@Transient
	private String planUrl;

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
	 * @return the price
	 */
	public Integer getPrice() {
		return price;
	}

	/**
	 * @param price the price to set
	 */
	public void setPrice(Integer price) {
		this.price = price;
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
	 * @return the period
	 */
	public Integer getPeriod() {
		return period;
	}

	/**
	 * @param period the period to set
	 */
	public void setPeriod(Integer period) {
		this.period = period;
	}

	/**
	 * @return the periodUnit
	 */
	public PeriodUnitType getPeriodUnit() {
		return periodUnit;
	}

	/**
	 * @param periodUnit the periodUnit to set
	 */
	public void setPeriodUnit(PeriodUnitType periodUnit) {
		this.periodUnit = periodUnit;
	}

	/**
	 * @return the chargeModel
	 */
	public ChargeModel getChargeModel() {
		return chargeModel;
	}

	/**
	 * @param chargeModel the chargeModel to set
	 */
	public void setChargeModel(ChargeModel chargeModel) {
		this.chargeModel = chargeModel;
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
	 * @return the archiveDate
	 */
	public Date getArchiveDate() {
		return archiveDate;
	}

	/**
	 * @param archiveDate the archiveDate to set
	 */
	public void setArchiveDate(Date archiveDate) {
		this.archiveDate = archiveDate;
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
	 * @return the planUrl
	 */
	public String getPlanUrl() {
		return planUrl;
	}

	/**
	 * @param planUrl the planUrl to set
	 */
	public void setPlanUrl(String planUrl) {
		this.planUrl = planUrl;
	}

	/**
	 * @return the buyerLimit
	 */
	public Integer getBuyerLimit() {
		return buyerLimit;
	}

	/**
	 * @param buyerLimit the buyerLimit to set
	 */
	public void setBuyerLimit(Integer buyerLimit) {
		this.buyerLimit = buyerLimit;
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
		result = prime * result + ((archiveDate == null) ? 0 : archiveDate.hashCode());
		result = prime * result + ((chargeModel == null) ? 0 : chargeModel.hashCode());
		result = prime * result + ((period == null) ? 0 : period.hashCode());
		result = prime * result + ((periodUnit == null) ? 0 : periodUnit.hashCode());
		result = prime * result + ((planName == null) ? 0 : planName.hashCode());
		result = prime * result + ((planStatus == null) ? 0 : planStatus.hashCode());
		result = prime * result + ((price == null) ? 0 : price.hashCode());
		result = prime * result + ((planOrder == null) ? 0 : planOrder.hashCode());
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
		SupplierPlan other = (SupplierPlan) obj;
		if (archiveDate == null) {
			if (other.archiveDate != null)
				return false;
		} else if (!archiveDate.equals(other.archiveDate))
			return false;
		if (chargeModel != other.chargeModel)
			return false;
		if (period == null) {
			if (other.period != null)
				return false;
		} else if (!period.equals(other.period))
			return false;
		if (periodUnit != other.periodUnit)
			return false;
		if (planOrder == null) {
			if (other.planOrder != null)
				return false;
		} else if (!planOrder.equals(other.planOrder))
			return false;
		if (planName == null) {
			if (other.planName != null)
				return false;
		} else if (!planName.equals(other.planName))
			return false;
		if (planStatus != other.planStatus)
			return false;
		if (price == null) {
			if (other.price != null)
				return false;
		} else if (!price.equals(other.price))
			return false;
		return true;
	}

	public String toLogString() {
		return "Plan [id=" + id + ", planOrder=" + planOrder + ", planName=" + planName + ", shortDescription=" + shortDescription + ", description=" + description + ", price=" + price + ", period=" + period + ", periodUnit=" + periodUnit + ", chargeModel=" + chargeModel + ", planStatus=" + planStatus + ", archiveDate=" + archiveDate + ", createdDate=" + createdDate + ", modifiedDate=" + modifiedDate + "]";
	}

}
