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

import org.hibernate.annotations.GenericGenerator;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.privasia.procurehere.core.enums.FinanceRequestStatus;
import com.privasia.procurehere.core.utils.CustomDateTimeSerializer;

@Entity
@Table(name = "PROC_INV_FINANCE_REQ")
public class InvoiceFinanceRequest implements Serializable {

	private static final long serialVersionUID = -1794187796001152748L;

	@Id
	@GenericGenerator(name = "idGen", strategy = "uuid.hex")
	@GeneratedValue(generator = "idGen")
	@Column(name = "ID", length = 64)
	private String id;

	@Column(name = "INVOICE_TITLE", length = 128)
	private String invoiceTitle;

	@Column(name = "INVOICE_NUMBER", length = 64, nullable = false)
	private String invoiceNumber;

	@Column(name = "PO_NUMBER", length = 64, nullable = true)
	private String poNumber;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "INVOICE_CURRENCY_ID", foreignKey = @ForeignKey(name = "FK_INV_FIN_REQ_CURRENCY"))
	private Currency currency;

	@Column(name = "INVOICE_DECIMAL", length = 64)
	private String decimal;

	@Column(name = "FUNDER_ID", length = 64)
	private String funder;

	@Column(name = "INVOICE_AMOUNT", precision = 22, scale = 6, nullable = false)
	private BigDecimal invoiceAmount;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "INVOICE_DATE", nullable = false)
	private Date invoiceDate;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "REQUESTED_DATE", nullable = false)
	private Date requestedDate;

	@Enumerated(EnumType.STRING)
	@Column(name = "REQUEST_STATUS", length = 10)
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

	@Column(name = "APPROVED_AMOUNT", precision = 22, scale = 6)
	private BigDecimal approvedAmount;

	@Column(name = "DRAWDOWN_AMOUNT", precision = 22, scale = 6)
	private BigDecimal drawdownAmount;

	@Column(name = "RECOVERED_AMOUNT", precision = 22, scale = 6)
	private BigDecimal recoveredAmount;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, optional = false, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "REQUESTED_BY", nullable = false, foreignKey = @ForeignKey(name = "FK_IF_REQUESTED_BY"))
	private User requesteBy;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, optional = true, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "APPROVED_BY", nullable = true, foreignKey = @ForeignKey(name = "FK_IF_APPROVED_BY"))
	private User approvedBy;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, optional = false, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "ACCEPTED_BY", nullable = false, foreignKey = @ForeignKey(name = "FK_IF_ACCEPTED_BY"))
	private User acceptedBy;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, optional = true, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "SUPPLIER_ID", nullable = true, foreignKey = @ForeignKey(name = "FK_INV_FIN_REQ_SUP_ID"))
	private Supplier supplier;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, optional = true, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "BUYER_ID", nullable = true, foreignKey = @ForeignKey(name = "FK_INV_FIN_REQ_BUYER_ID"))
	private Buyer buyer;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, optional = true, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "FIN_BUYER_ID", nullable = true, foreignKey = @ForeignKey(name = "FK_INV_FIN_REQ_FIN_BYR_ID"))
	private OnboardedBuyer finanshereBuyer;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, optional = true, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "INVOICE_ID", nullable = true, foreignKey = @ForeignKey(name = "FK_INV_FIN_REQ_INV_ID"))
	private Invoice invoice;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, optional = true, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "PO_REQUEST_ID", nullable = true, foreignKey = @ForeignKey(name = "FK_INV_FIN_PO_REQ_ID"))
	private PoFinanceRequest poRequest;

	@Column(name = "PAYMENT_TERMS", length = 3, nullable = true)
	private Integer paymentTerms;

	public InvoiceFinanceRequest() {
	}

	public InvoiceFinanceRequest(String id, String invoiceNumber, BigDecimal invoiceAmount, Date invoiceDate, String buyerName) {
		this.id = id;
		this.invoiceNumber = invoiceNumber;
		this.invoiceAmount = invoiceAmount;
		this.invoiceDate = invoiceDate;
		this.buyerName = buyerName;
	}

	public InvoiceFinanceRequest(Invoice invoice) {
		this.buyer = invoice.getBuyer();
		this.buyerName = invoice.getBuyer().getCompanyName();
		this.buyerRoc = invoice.getBuyer().getCompanyRegistrationNumber();
		this.invoice = invoice;
		this.invoiceAmount = invoice.getGrandTotal();
		this.invoiceDate = invoice.getInvoiceSendDate();
		this.invoiceNumber = invoice.getInvoiceId();
		this.invoiceTitle = invoice.getName();
		this.requestedDate = new Date();
		this.requestStatus = FinanceRequestStatus.REQUESTED;
		this.supplier = invoice.getSupplier();
		this.supplierName = invoice.getSupplierName();
		this.supplierRoc = invoice.getSupplier().getCompanyRegistrationNumber();
		this.currency = invoice.getCurrency();
		this.decimal = invoice.getDecimal();
		this.poNumber = invoice.getPo().getPoNumber();

		this.paymentTerms = invoice.getPaymentTermDays() != null ? invoice.getPaymentTermDays() : 30;
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
	 * @return the invoiceTitle
	 */
	public String getInvoiceTitle() {
		return invoiceTitle;
	}

	/**
	 * @param invoiceTitle the invoiceTitle to set
	 */
	public void setInvoiceTitle(String invoiceTitle) {
		this.invoiceTitle = invoiceTitle;
	}

	/**
	 * @return the invoiceNumber
	 */
	public String getInvoiceNumber() {
		return invoiceNumber;
	}

	/**
	 * @param invoiceNumber the invoiceNumber to set
	 */
	public void setInvoiceNumber(String invoiceNumber) {
		this.invoiceNumber = invoiceNumber;
	}

	/**
	 * @return the invoiceAmount
	 */
	public BigDecimal getInvoiceAmount() {
		return invoiceAmount;
	}

	/**
	 * @param invoiceAmount the invoiceAmount to set
	 */
	public void setInvoiceAmount(BigDecimal invoiceAmount) {
		this.invoiceAmount = invoiceAmount;
	}

	/**
	 * @return the invoiceDate
	 */
	public Date getInvoiceDate() {
		return invoiceDate;
	}

	/**
	 * @param invoiceDate the invoiceDate to set
	 */
	public void setInvoiceDate(Date invoiceDate) {
		this.invoiceDate = invoiceDate;
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
	 * @return the requesteBy
	 */
	public User getRequesteBy() {
		return requesteBy;
	}

	/**
	 * @param requesteBy the requesteBy to set
	 */
	public void setRequesteBy(User requesteBy) {
		this.requesteBy = requesteBy;
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
	 * @return the invoice
	 */
	public Invoice getInvoice() {
		return invoice;
	}

	/**
	 * @param invoice the invoice to set
	 */
	public void setInvoice(Invoice invoice) {
		this.invoice = invoice;
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
	 * @return the poRequest
	 */
	public PoFinanceRequest getPoRequest() {
		return poRequest;
	}

	/**
	 * @param poRequest the poRequest to set
	 */
	public void setPoRequest(PoFinanceRequest poRequest) {
		this.poRequest = poRequest;
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
		InvoiceFinanceRequest other = (InvoiceFinanceRequest) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

}
