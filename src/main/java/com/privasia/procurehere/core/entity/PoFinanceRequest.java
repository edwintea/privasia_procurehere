package com.privasia.procurehere.core.entity;

import java.io.Serializable;
import java.math.BigDecimal;
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
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.GenericGenerator;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.privasia.procurehere.core.enums.FinanceRequestStatus;
import com.privasia.procurehere.core.utils.CustomDateTimeSerializer;

@Entity
@Table(name = "PROC_PO_FINANCE_REQ")
public class PoFinanceRequest implements Serializable {

	private static final long serialVersionUID = -383959592881079470L;

	@Id
	@GenericGenerator(name = "idGen", strategy = "uuid.hex")
	@GeneratedValue(generator = "idGen")
	@Column(name = "ID", length = 64)
	private String id;

	@Column(name = "PO_NAME", length = 128)
	private String poName;

	@Column(name = "PO_NUMBER", length = 64, nullable = false)
	private String poNumber;

	@Column(name = "PO_AMOUNT", precision = 22, scale = 6, nullable = false)
	private BigDecimal poAmount;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "PO_DATE", nullable = false)
	private Date poDate;

	@Column(name = "FUNDER_ID", length = 64)
	private String funder;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "REQUESTED_DATE", nullable = false)
	private Date requestedDate;

	@Enumerated(EnumType.STRING)
	@Column(name = "REQUEST_STATUS", length = 10, nullable = false)
	private FinanceRequestStatus requestStatus;

	@Column(name = "BUYER_NAME", length = 128)
	private String buyerName;

	@Column(name = "BUYER_ROC", length = 128)
	private String buyerRoc;

	@Column(name = "SUPPLIER_NAME", length = 128)
	private String supplierName;

	@Column(name = "SUPPLIER_ROC", length = 128)
	private String supplierRoc;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "APPROVED_DATE")
	private Date approvedDate;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "ACCEPTED_DATE")
	private Date acceptedDate;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DECLINED_DATE")
	private Date declinedDate;

	@Column(name = "PAYMENT_TERMS", length = 3, nullable = false)
	private Integer paymentTerms;

	@Column(name = "APPROVED_AMOUNT", precision = 22, scale = 6)
	private BigDecimal approvedAmount;

	@Column(name = "DRAWDOWN_AMOUNT", precision = 22, scale = 6)
	private BigDecimal drawdownAmount;

	@Column(name = "RECOVERED_AMOUNT", precision = 22, scale = 6)
	private BigDecimal recoveredAmount;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, optional = false, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "REQUESTED_BY", nullable = false, foreignKey = @ForeignKey(name = "FK_IF_PO_REQUESTED_BY"))
	private User requestedBy;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, optional = true, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "APPROVED_BY", nullable = true, foreignKey = @ForeignKey(name = "FK_IF_PO_APPROVED_BY"))
	private User approvedBy;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, optional = true, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "ACCEPTED_BY", nullable = true, foreignKey = @ForeignKey(name = "FK_IF_PO_ACCEPTED_BY"))
	private User acceptedBy;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, optional = true, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "DECLINED_BY", nullable = true, foreignKey = @ForeignKey(name = "FK_IF_PO_DECLINED_BY"))
	private User declinedBy;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, optional = true, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "SUPPLIER_ID", nullable = true, foreignKey = @ForeignKey(name = "FK_PO_FIN_REQ_SUP_ID"))
	private Supplier supplier;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, optional = true, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "BUYER_ID", nullable = true, foreignKey = @ForeignKey(name = "FK_PO_FIN_REQ_BUYER_ID"))
	private Buyer buyer;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, optional = false, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "FIN_BUYER_ID", nullable = false, foreignKey = @ForeignKey(name = "FK_PO_FIN_REQ_FIN_BYR_ID"))
	private OnboardedBuyer finanshereBuyer;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, optional = false, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "FIN_SUPPLIER_ID", nullable = false, foreignKey = @ForeignKey(name = "FK_PO_FIN_REQ_FIN_SUP_ID"))
	private OnboardedSupplier finanshereSupplier;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, optional = true, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "PO_ID", nullable = true, foreignKey = @ForeignKey(name = "FK_PO_FIN_REQ_PO_ID"))
	private Po po;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "PO_CURRENCY_ID", nullable = false, foreignKey = @ForeignKey(name = "FK_PO_FIN_REQ_CURRENCY"))
	private Currency currency;

	@Column(name = "PO_DECIMAL", nullable = false, length = 64)
	private String decimal;

	@Column(name = "FILE_DATA", nullable = false)
	private byte[] fileData;

	@JsonIgnore
	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "poFinanceRequest")
	private List<PoFinanceRequestDocuments> poDocuments;

	public PoFinanceRequest() {
	}

	public PoFinanceRequest(String id, String poNumber, BigDecimal poAmount, Date poDate, String buyerName) {
		this.id = id;
		this.poNumber = poNumber;
		this.poAmount = poAmount;
		this.poDate = poDate;
		this.buyerName = buyerName;
	}

	public PoFinanceRequest(Po po) {
		this.buyer = po.getBuyer();
		this.buyerName = po.getBuyer().getCompanyName();
		this.buyerRoc = po.getBuyer().getCompanyRegistrationNumber();
		this.po = po;
		this.poAmount = po.getGrandTotal();
		this.poDate = po.getOrderedDate();
		this.poNumber = po.getPoNumber();
		this.poName = po.getName();
		this.requestedDate = new Date();
		this.requestStatus = FinanceRequestStatus.REQUESTED;
		this.supplier = po.getSupplier().getSupplier();
		this.supplierName = po.getSupplierName();
		this.supplierRoc = po.getSupplier().getSupplier().getCompanyRegistrationNumber();
		this.currency = po.getCurrency();
		this.decimal = po.getDecimal();
		this.poNumber = po.getPoNumber();
		this.paymentTerms = po.getPaymentTermDays() != null ? po.getPaymentTermDays() : 30;
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
	 * @return the poName
	 */
	public String getPoName() {
		return poName;
	}

	/**
	 * @param poName the poName to set
	 */
	public void setPoName(String poName) {
		this.poName = poName;
	}

	/**
	 * @return the poNumber
	 */
	public String getPoNumber() {
		return poNumber;
	}

	/**
	 * @param poNumber the poNumber to set
	 */
	public void setPoNumber(String poNumber) {
		this.poNumber = poNumber;
	}

	/**
	 * @return the poAmount
	 */
	public BigDecimal getPoAmount() {
		return poAmount;
	}

	/**
	 * @param poAmount the poAmount to set
	 */
	public void setPoAmount(BigDecimal poAmount) {
		this.poAmount = poAmount;
	}

	/**
	 * @return the poDate
	 */
	public Date getPoDate() {
		return poDate;
	}

	/**
	 * @param poDate the poDate to set
	 */
	public void setPoDate(Date poDate) {
		this.poDate = poDate;
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
	 * @return the requestStatus
	 */
	public FinanceRequestStatus getRequestStatus() {
		return requestStatus;
	}

	/**
	 * @param requestStatus the requestStatus to set
	 */
	public void setRequestStatus(FinanceRequestStatus requestStatus) {
		this.requestStatus = requestStatus;
	}

	/**
	 * @return the buyerName
	 */
	public String getBuyerName() {
		return buyerName;
	}

	/**
	 * @param buyerName the buyerName to set
	 */
	public void setBuyerName(String buyerName) {
		this.buyerName = buyerName;
	}

	/**
	 * @return the buyerRoc
	 */
	public String getBuyerRoc() {
		return buyerRoc;
	}

	/**
	 * @param buyerRoc the buyerRoc to set
	 */
	public void setBuyerRoc(String buyerRoc) {
		this.buyerRoc = buyerRoc;
	}

	/**
	 * @return the supplierName
	 */
	public String getSupplierName() {
		return supplierName;
	}

	/**
	 * @param supplierName the supplierName to set
	 */
	public void setSupplierName(String supplierName) {
		this.supplierName = supplierName;
	}

	/**
	 * @return the supplierRoc
	 */
	public String getSupplierRoc() {
		return supplierRoc;
	}

	/**
	 * @param supplierRoc the supplierRoc to set
	 */
	public void setSupplierRoc(String supplierRoc) {
		this.supplierRoc = supplierRoc;
	}

	/**
	 * @return the approvedDate
	 */
	public Date getApprovedDate() {
		return approvedDate;
	}

	/**
	 * @param approvedDate the approvedDate to set
	 */
	public void setApprovedDate(Date approvedDate) {
		this.approvedDate = approvedDate;
	}

	/**
	 * @return the acceptedDate
	 */
	public Date getAcceptedDate() {
		return acceptedDate;
	}

	/**
	 * @param acceptedDate the acceptedDate to set
	 */
	public void setAcceptedDate(Date acceptedDate) {
		this.acceptedDate = acceptedDate;
	}

	/**
	 * @return the paymentTerms
	 */
	public Integer getPaymentTerms() {
		return paymentTerms;
	}

	/**
	 * @param paymentTerms the paymentTerms to set
	 */
	public void setPaymentTerms(Integer paymentTerms) {
		this.paymentTerms = paymentTerms;
	}

	/**
	 * @return the approvedAmount
	 */
	public BigDecimal getApprovedAmount() {
		return approvedAmount;
	}

	/**
	 * @param approvedAmount the approvedAmount to set
	 */
	public void setApprovedAmount(BigDecimal approvedAmount) {
		this.approvedAmount = approvedAmount;
	}

	/**
	 * @return the drawdownAmount
	 */
	public BigDecimal getDrawdownAmount() {
		return drawdownAmount;
	}

	/**
	 * @param drawdownAmount the drawdownAmount to set
	 */
	public void setDrawdownAmount(BigDecimal drawdownAmount) {
		this.drawdownAmount = drawdownAmount;
	}

	/**
	 * @return the recoveredAmount
	 */
	public BigDecimal getRecoveredAmount() {
		return recoveredAmount;
	}

	/**
	 * @param recoveredAmount the recoveredAmount to set
	 */
	public void setRecoveredAmount(BigDecimal recoveredAmount) {
		this.recoveredAmount = recoveredAmount;
	}

	/**
	 * @return the requestedBy
	 */
	public User getRequestedBy() {
		return requestedBy;
	}

	/**
	 * @param requesteBy the requestedBy to set
	 */
	public void setRequestedBy(User requestedBy) {
		this.requestedBy = requestedBy;
	}

	/**
	 * @return the approvedBy
	 */
	public User getApprovedBy() {
		return approvedBy;
	}

	/**
	 * @param approvedBy the approvedBy to set
	 */
	public void setApprovedBy(User approvedBy) {
		this.approvedBy = approvedBy;
	}

	/**
	 * @return the acceptedBy
	 */
	public User getAcceptedBy() {
		return acceptedBy;
	}

	/**
	 * @param acceptedBy the acceptedBy to set
	 */
	public void setAcceptedBy(User acceptedBy) {
		this.acceptedBy = acceptedBy;
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
	 * @return the buyer
	 */
	public Buyer getBuyer() {
		return buyer;
	}

	/**
	 * @param buyer the buyer to set
	 */
	public void setBuyer(Buyer buyer) {
		this.buyer = buyer;
	}

	/**
	 * @return the finanshereBuyer
	 */
	public OnboardedBuyer getFinanshereBuyer() {
		return finanshereBuyer;
	}

	/**
	 * @param finanshereBuyer the finanshereBuyer to set
	 */
	public void setFinanshereBuyer(OnboardedBuyer finanshereBuyer) {
		this.finanshereBuyer = finanshereBuyer;
	}

	/**
	 * @return the finanshereSupplier
	 */
	public OnboardedSupplier getFinanshereSupplier() {
		return finanshereSupplier;
	}

	/**
	 * @param finanshereSupplier the finanshereSupplier to set
	 */
	public void setFinanshereSupplier(OnboardedSupplier finanshereSupplier) {
		this.finanshereSupplier = finanshereSupplier;
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
	 * @return the decimal
	 */
	public String getDecimal() {
		return decimal;
	}

	/**
	 * @param decimal the decimal to set
	 */
	public void setDecimal(String decimal) {
		this.decimal = decimal;
	}

	/**
	 * @return the declinedDate
	 */
	public Date getDeclinedDate() {
		return declinedDate;
	}

	/**
	 * @param declinedDate the declinedDate to set
	 */
	public void setDeclinedDate(Date declinedDate) {
		this.declinedDate = declinedDate;
	}

	/**
	 * @return the declinedBy
	 */
	public User getDeclinedBy() {
		return declinedBy;
	}

	/**
	 * @param declinedBy the declinedBy to set
	 */
	public void setDeclinedBy(User declinedBy) {
		this.declinedBy = declinedBy;
	}

	/**
	 * @return the funder
	 */
	public String getFunder() {
		return funder;
	}

	/**
	 * @param funder the funder to set
	 */
	public void setFunder(String funder) {
		this.funder = funder;
	}

	/**
	 * @return the poDocuments
	 */
	public List<PoFinanceRequestDocuments> getPoDocuments() {
		return poDocuments;
	}

	/**
	 * @param poDocuments the poDocuments to set
	 */
	public void setPoDocuments(List<PoFinanceRequestDocuments> poDocuments) {
		this.poDocuments = poDocuments;
	}

	/**
	 * @return the fileData
	 */
	public byte[] getFileData() {
		return fileData;
	}

	/**
	 * @param fileData the fileData to set
	 */
	public void setFileData(byte[] fileData) {
		this.fileData = fileData;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PoFinanceRequest other = (PoFinanceRequest) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

}
