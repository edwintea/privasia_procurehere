package com.privasia.procurehere.core.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

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
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.privasia.procurehere.core.enums.FinancePoStatus;
import com.privasia.procurehere.core.enums.FinancePoType;
import com.privasia.procurehere.core.utils.CustomDateTimeSerializer;

/**
 * @author yogesh
 */
@Entity
@Table(name = "PROC_FINANCE_PO")
public class FinancePo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3855850746509402627L;

	@Id
	@GenericGenerator(name = "idGen", strategy = "uuid.hex")
	@GeneratedValue(generator = "idGen")
	@Column(name = "ID", length = 64)
	private String id;

	@ManyToOne(fetch = FetchType.LAZY, optional = true, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "SUPPLIER_ID", nullable = true, foreignKey = @ForeignKey(name = "FK_PR_SUPPLIER_FINANCE_ID"))
	private Supplier supplier;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, optional = true, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "FINANCE_ID", nullable = true, foreignKey = @ForeignKey(name = "FK_PR_FIN_COMP_MAP_ID"))
	private FinanceCompany financeCompany;

	@Column(name = "REFERRAL_FEE", precision = 20, scale = 4)
	private BigDecimal referralFee;

	@Column(name = "REMARK", precision = 20, scale = 4)
	private String remark;

	@Column(name = "REFERENCE_NUMBER", length = 64)
	private String referenceNum;

	@Enumerated(EnumType.STRING)
	@Column(name = "STATUS", precision = 20, scale = 4)
	private FinancePoStatus financePoStatus;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, optional = true, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "CREATED_BY", nullable = true)
	private User createdBy;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "MODIFIED_BY", nullable = true)
	private User modifiedBy;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CREATED_DATE", nullable = false)
	private Date createdDate;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "MODIFIED_DATE")
	private Date modifiedDate;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "PAYMENT_DATE")
	private Date paymentDate;

	@Enumerated(EnumType.STRING)
	@Column(name = "PO_TYPE", precision = 20, scale = 4)
	private FinancePoType financePoType;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "SHARED_DATE")
	private Date sharedDate;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "REQUESTED_DATE")
	private Date requestedDate;

	@ManyToOne(fetch = FetchType.LAZY, optional = true, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "FIN_PO_ID", nullable = true, foreignKey = @ForeignKey(name = "FK_FIN_PO_ID"))
	private Po po;

	@Transient
	private String statusValue;

	public FinancePo() {
	}

	public FinancePo(String id, String prId, String supplierName, String poName, String description, String poNumber, FinancePoStatus financePoStatus, Date poCreatedDate, BigDecimal grandTotal, String decimal) {
		this.id = id;

		Supplier supplier = new Supplier();
		supplier.setCompanyName(supplierName);
		this.supplier = supplier;
		Po po = new Po();
		po.setName(poName);
		po.setDescription(description);
		po.setId(prId);
		po.setPoNumber(poNumber);
		po.setCreatedDate(poCreatedDate);
		po.setGrandTotal(grandTotal);
		po.setDecimal(decimal);

		this.po = po;
		this.financePoStatus = financePoStatus;
	}

	public FinancePo(String id, String supplierName, String loginEmail, String communicationEmail, String companyRegistrationNumber, String fullName, String mobileNumber, Date registrationDate) {
		Supplier supplier = new Supplier();
		supplier.setCompanyName(supplierName);
		supplier.setId(id);
		supplier.setLoginEmail(loginEmail);
		supplier.setCommunicationEmail(communicationEmail);
		supplier.setMobileNumber(mobileNumber);
		supplier.setCompanyRegistrationNumber(companyRegistrationNumber);
		supplier.setFullName(fullName);
		supplier.setRegistrationDate(registrationDate);
		this.supplier = supplier;
	}

	/**
	 * @return the financePoStatus
	 */
	public FinancePoStatus getFinancePoStatus() {
		return financePoStatus;
	}

	/**
	 * @param financePoStatus the financePoStatus to set
	 */
	public void setFinancePoStatus(FinancePoStatus financePoStatus) {
		this.financePoStatus = financePoStatus;
	}

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
	 * @return the supplier
	 */
	public Supplier getSupplier() {
		return supplier;
	}

	/**
	 * @param supplier the supplier to set
	 */
	public void setSupplier(Supplier supplier) {
		this.supplier = supplier;
	}

	/**
	 * @return the financeCompany
	 */
	public FinanceCompany getFinanceCompany() {
		return financeCompany;
	}

	/**
	 * @param financeCompany the financeCompany to set
	 */
	public void setFinanceCompany(FinanceCompany financeCompany) {
		this.financeCompany = financeCompany;
	}

	/**
	 * @return the referralFee
	 */
	public BigDecimal getReferralFee() {
		return referralFee;
	}

	/**
	 * @param referralFee the referralFee to set
	 */
	public void setReferralFee(BigDecimal referralFee) {
		this.referralFee = referralFee;
	}

	/**
	 * @return the remark
	 */
	public String getRemark() {
		return remark;
	}

	/**
	 * @param remark the remark to set
	 */
	public void setRemark(String remark) {
		this.remark = remark;
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
	 * @return the paymentDate
	 */
	public Date getPaymentDate() {
		return paymentDate;
	}

	/**
	 * @param paymentDate the paymentDate to set
	 */
	public void setPaymentDate(Date paymentDate) {
		this.paymentDate = paymentDate;
	}

	/**
	 * @return the referenceNum
	 */
	public String getReferenceNum() {
		return referenceNum;
	}

	/**
	 * @param referenceNum the referenceNum to set
	 */
	public void setReferenceNum(String referenceNum) {
		this.referenceNum = referenceNum;
	}

	/**
	 * @return the statusValue
	 */
	public String getStatusValue() {
		return statusValue;
	}

	/**
	 * @param statusValue the statusValue to set
	 */
	public void setStatusValue(String statusValue) {
		this.statusValue = statusValue;
	}

	/**
	 * @return the financePoType
	 */
	public FinancePoType getFinancePoType() {
		return financePoType;
	}

	/**
	 * @param financePoType the financePoType to set
	 */
	public void setFinancePoType(FinancePoType financePoType) {
		this.financePoType = financePoType;
	}

	/**
	 * @return the sharedDate
	 */
	public Date getSharedDate() {
		return sharedDate;
	}

	/**
	 * @param sharedDate the sharedDate to set
	 */
	public void setSharedDate(Date sharedDate) {
		this.sharedDate = sharedDate;
	}

	/**
	 * @return the requestedDate
	 */
	public Date getRequestedDate() {
		return requestedDate;
	}

	/**
	 * @param requestedDate the requestedDate to set
	 */
	public void setRequestedDate(Date requestedDate) {
		this.requestedDate = requestedDate;
	}

	/**
	 * @return the po
	 */
	public Po getPo() {
		return po;
	}

	/**
	 * @param po the po to set
	 */
	public void setPo(Po po) {
		this.po = po;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "FinancePo [id=" + id + " referralFee=" + referralFee + ", remark=" + remark + " createdDate=" + createdDate + ", modifiedDate=" + modifiedDate + ", paymentDate=" + paymentDate + "]";
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((createdBy == null) ? 0 : createdBy.hashCode());
		result = prime * result + ((createdDate == null) ? 0 : createdDate.hashCode());
		result = prime * result + ((financeCompany == null) ? 0 : financeCompany.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((modifiedBy == null) ? 0 : modifiedBy.hashCode());
		result = prime * result + ((modifiedDate == null) ? 0 : modifiedDate.hashCode());
		result = prime * result + ((paymentDate == null) ? 0 : paymentDate.hashCode());
		result = prime * result + ((po == null) ? 0 : po.hashCode());
		result = prime * result + ((referralFee == null) ? 0 : referralFee.hashCode());
		result = prime * result + ((remark == null) ? 0 : remark.hashCode());
		result = prime * result + ((supplier == null) ? 0 : supplier.hashCode());
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
		FinancePo other = (FinancePo) obj;
		if (createdBy == null) {
			if (other.createdBy != null)
				return false;
		} else if (!createdBy.equals(other.createdBy))
			return false;
		if (createdDate == null) {
			if (other.createdDate != null)
				return false;
		} else if (!createdDate.equals(other.createdDate))
			return false;
		if (financeCompany == null) {
			if (other.financeCompany != null)
				return false;
		} else if (!financeCompany.equals(other.financeCompany))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (modifiedBy == null) {
			if (other.modifiedBy != null)
				return false;
		} else if (!modifiedBy.equals(other.modifiedBy))
			return false;
		if (modifiedDate == null) {
			if (other.modifiedDate != null)
				return false;
		} else if (!modifiedDate.equals(other.modifiedDate))
			return false;
		if (paymentDate == null) {
			if (other.paymentDate != null)
				return false;
		} else if (!paymentDate.equals(other.paymentDate))
			return false;

		if (po == null) {
			if (other.po != null)
				return false;
		} else if (!po.equals(other.po))
			return false;
		if (referralFee == null) {
			if (other.referralFee != null)
				return false;
		} else if (!referralFee.equals(other.referralFee))
			return false;
		if (remark == null) {
			if (other.remark != null)
				return false;
		} else if (!remark.equals(other.remark))
			return false;
		if (supplier == null) {
			if (other.supplier != null)
				return false;
		} else if (!supplier.equals(other.supplier))
			return false;
		return true;
	}

}
