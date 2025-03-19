/**
 * 
 */
package com.privasia.procurehere.core.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.privasia.procurehere.core.enums.ChargeModel;
import com.privasia.procurehere.core.enums.PeriodUnitType;
import com.privasia.procurehere.core.enums.PlanStatus;
import com.privasia.procurehere.core.enums.TrialPeriodUnitType;
import com.privasia.procurehere.core.enums.converter.ChargeModelCoverter;
import com.privasia.procurehere.core.utils.CustomDateTimeSerializer;
import org.hibernate.annotations.Type;

/**
 * @author Nitin Otageri
 */
//@Entity
//@Table(name = "PROC_PLAN")
public class Plan implements Serializable {

	private static final long serialVersionUID = -3515286329199965479L;

	@Id
	@GenericGenerator(name = "idGen", strategy = "uuid.hex")
	@GeneratedValue(generator = "idGen")
	@Column(name = "ID", length = 64)
	private String id;

	@NotNull
	@Digits(integer = 3, fraction = 0)
	@Column(name = "PLAN_ORDER", length = 3, nullable = false)
	private Integer planOrder;

	@Size(min = 0, max = 128)
	@Column(name = "PLAN_GROUP", length = 128, nullable = true)
	private String planGroup;

	@NotNull
	@Size(min = 1, max = 128)
	@Column(name = "PLAN_NAME", length = 128, nullable = false)
	private String planName;

	@Size(min = 0, max = 128)
	@Column(name = "INVOICE_NAME", length = 128)
	private String invoiceName;

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
	@JoinColumn(name = "CURRENCY_CODE", foreignKey = @ForeignKey(name = "FK_PLAN_CURRENCY"), nullable = false)
	private Currency currency;

	@NotNull
	@Digits(integer = 6, fraction = 0)
	@Column(name = "PLAN_PERIOD", length = 6, nullable = false)
	private Integer period;

	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(name = "PLAN_PERIOD_UNIT")
	private PeriodUnitType periodUnit;

	@Max(99)
	@Column(name = "TRIAL_PERIOD", length = 2, nullable = true)
	private Integer trialPeriod;

	@Enumerated(EnumType.STRING)
	@Column(name = "TRIAL_PERIOD_UNIT", nullable = true)
	private TrialPeriodUnitType trialPeriodUnit;

	@NotNull
	@Convert(converter = ChargeModelCoverter.class)
	@Column(name = "CHARGE_MODEL", length = 50, nullable = false)
	private ChargeModel chargeModel;

	@Column(name = "FREE_QUANTITY", length = 6, nullable = true)
	private Integer freeQuantity;

	@Column(name = "USER_LIMIT", length = 6, nullable = false)
	private Integer userLimit;

	@Column(name = "EVENT_LIMIT", length = 6)
	private Integer eventLimit;

	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(name = "PLAN_STATUS", nullable = false)
	private PlanStatus planStatus;

	@Column(name = "BILLING_CYCLES", length = 6)
	private Integer billingCycles;

	@Column(name = "TAX_CODE", length = 3)
	private String taxCode;

	@NotNull
	@Column(name = "IS_TAXABLE", nullable = false)
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean taxable = Boolean.FALSE;

	@Size(min = 0, max = 250)
	@Column(name = "INVOICE_NOTES", length = 250, nullable = true)
	private String invoiceNotes;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "ARCHIVE_DATE", nullable = true)
	private Date archiveDate;

	@NotNull
	@Column(name = "CARRY_FORWARD_UNITS", nullable = false)
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean carryForwardUnits = Boolean.FALSE;

	@ManyToOne(fetch = FetchType.LAZY, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "CREATED_BY", nullable = false, foreignKey = @ForeignKey(name = "FK_PLAN_CREATED_BY"))
	private User createdBy;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CREATED_DATE", nullable = false)
	private Date createdDate;

	@ManyToOne(fetch = FetchType.LAZY, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "MODIFIED_BY", nullable = true, foreignKey = @ForeignKey(name = "FK_PLAN_MODIFIED_BY"))
	private User modifiedBy;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "MODIFIED_DATE")
	private Date modifiedDate;

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
	 * @return the invoiceName
	 */
	public String getInvoiceName() {
		return invoiceName;
	}

	/**
	 * @param invoiceName the invoiceName to set
	 */
	public void setInvoiceName(String invoiceName) {
		this.invoiceName = invoiceName;
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
	 * @return the trialPeriod
	 */
	public Integer getTrialPeriod() {
		return trialPeriod;
	}

	/**
	 * @param trialPeriod the trialPeriod to set
	 */
	public void setTrialPeriod(Integer trialPeriod) {
		this.trialPeriod = trialPeriod;
	}

	/**
	 * @return the trialPeriodUnit
	 */
	public TrialPeriodUnitType getTrialPeriodUnit() {
		return trialPeriodUnit;
	}

	/**
	 * @param trialPeriodUnit the trialPeriodUnit to set
	 */
	public void setTrialPeriodUnit(TrialPeriodUnitType trialPeriodUnit) {
		this.trialPeriodUnit = trialPeriodUnit;
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
	 * @return the freeQuantity
	 */
	public Integer getFreeQuantity() {
		return freeQuantity;
	}

	/**
	 * @param freeQuantity the freeQuantity to set
	 */
	public void setFreeQuantity(Integer freeQuantity) {
		this.freeQuantity = freeQuantity;
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
	 * @return the billingCycles
	 */
	public Integer getBillingCycles() {
		return billingCycles;
	}

	/**
	 * @param billingCycles the billingCycles to set
	 */
	public void setBillingCycles(Integer billingCycles) {
		this.billingCycles = billingCycles;
	}

	/**
	 * @return the taxCode
	 */
	public String getTaxCode() {
		return taxCode;
	}

	/**
	 * @param taxCode the taxCode to set
	 */
	public void setTaxCode(String taxCode) {
		this.taxCode = taxCode;
	}

	/**
	 * @return the taxable
	 */
	public Boolean getTaxable() {
		return taxable;
	}

	/**
	 * @param taxable the taxable to set
	 */
	public void setTaxable(Boolean taxable) {
		this.taxable = taxable;
	}

	/**
	 * @return the invoiceNotes
	 */
	public String getInvoiceNotes() {
		return invoiceNotes;
	}

	/**
	 * @param invoiceNotes the invoiceNotes to set
	 */
	public void setInvoiceNotes(String invoiceNotes) {
		this.invoiceNotes = invoiceNotes;
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
	 * @return the carryForwardUnits
	 */
	public Boolean getCarryForwardUnits() {
		return carryForwardUnits;
	}

	/**
	 * @param carryForwardUnits the carryForwardUnits to set
	 */
	public void setCarryForwardUnits(Boolean carryForwardUnits) {
		this.carryForwardUnits = carryForwardUnits;
	}

	/**
	 * @return the userLimit
	 */
	public Integer getUserLimit() {
		return userLimit;
	}

	/**
	 * @param userLimit the userLimit to set
	 */
	public void setUserLimit(Integer userLimit) {
		this.userLimit = userLimit;
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
	 * @return the planGroup
	 */
	public String getPlanGroup() {
		return planGroup;
	}

	/**
	 * @param planGroup the planGroup to set
	 */
	public void setPlanGroup(String planGroup) {
		this.planGroup = planGroup;
	}

	/**
	 * @return the eventLimit
	 */
	public Integer getEventLimit() {
		return eventLimit;
	}

	/**
	 * @param eventLimit the eventLimit to set
	 */
	public void setEventLimit(Integer eventLimit) {
		this.eventLimit = eventLimit;
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

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((archiveDate == null) ? 0 : archiveDate.hashCode());
		result = prime * result + ((billingCycles == null) ? 0 : billingCycles.hashCode());
		result = prime * result + ((chargeModel == null) ? 0 : chargeModel.hashCode());
		result = prime * result + ((freeQuantity == null) ? 0 : freeQuantity.hashCode());
		result = prime * result + ((period == null) ? 0 : period.hashCode());
		result = prime * result + ((periodUnit == null) ? 0 : periodUnit.hashCode());
		result = prime * result + ((planName == null) ? 0 : planName.hashCode());
		result = prime * result + ((planStatus == null) ? 0 : planStatus.hashCode());
		result = prime * result + ((price == null) ? 0 : price.hashCode());
		result = prime * result + ((userLimit == null) ? 0 : userLimit.hashCode());
		result = prime * result + ((eventLimit == null) ? 0 : eventLimit.hashCode());
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
		Plan other = (Plan) obj;
		if (archiveDate == null) {
			if (other.archiveDate != null)
				return false;
		} else if (!archiveDate.equals(other.archiveDate))
			return false;
		if (billingCycles == null) {
			if (other.billingCycles != null)
				return false;
		} else if (!billingCycles.equals(other.billingCycles))
			return false;
		if (chargeModel != other.chargeModel)
			return false;
		if (freeQuantity == null) {
			if (other.freeQuantity != null)
				return false;
		} else if (!freeQuantity.equals(other.freeQuantity))
			return false;
		if (userLimit == null) {
			if (other.userLimit != null)
				return false;
		} else if (!userLimit.equals(other.userLimit))
			return false;
		if (eventLimit == null) {
			if (other.eventLimit != null)
				return false;
		} else if (!eventLimit.equals(other.eventLimit))
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
		return "Plan [id=" + id + ", planOrder=" + planOrder + ", planGroup=" + planGroup + ", planName=" + planName + ", invoiceName=" + invoiceName + ", shortDescription=" + shortDescription + ", description=" + description + ", price=" + price + ", userLimit=" + userLimit + ", eventLimit=" + eventLimit + ", period=" + period + ", periodUnit=" + periodUnit + ", trialPeriod=" + trialPeriod + ", trialPeriodUnit=" + trialPeriodUnit + ", chargeModel=" + chargeModel + ", freeQuantity=" + freeQuantity + ", userLimit=" + userLimit + ", planStatus=" + planStatus + ", billingCycles=" + billingCycles + ", taxCode=" + taxCode + ", taxable=" + taxable + ", invoiceNotes=" + invoiceNotes + ", archiveDate=" + archiveDate + ", carryForwardUnits=" + carryForwardUnits + ", createdDate=" + createdDate + ", modifiedDate=" + modifiedDate + "]";
	}

}
