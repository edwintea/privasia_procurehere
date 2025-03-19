/**
 * 
 */
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
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.hibernate.validator.constraints.Email;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.privasia.procurehere.core.entity.Supplier.SupplierSignup;
import com.privasia.procurehere.core.entity.Supplier.SupplierStep1;
import com.privasia.procurehere.core.enums.PaymentMethod;
import com.privasia.procurehere.core.enums.TransactionStatus;
import com.privasia.procurehere.core.enums.TransactionType;
import com.privasia.procurehere.core.utils.CustomDateTimeSerializer;

/**
 * @author Nitin Otageri
 */
@Entity
@Table(name = "PROC_PAYMENT_TRANSACTION")
public class PaymentTransaction implements Serializable {

	private static final long serialVersionUID = 6992578711666925898L;

	@Id
	@GenericGenerator(name = "idGen", strategy = "uuid.hex")
	@GeneratedValue(generator = "idGen")
	@Column(name = "ID", length = 64)
	private String id;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, optional = true, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "BUYER_ID", nullable = true, foreignKey = @ForeignKey(name = "FK_TRANS_BUYER_ID"))
	private Buyer buyer;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, optional = true, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "SUPPLIER_ID", nullable = true, foreignKey = @ForeignKey(name = "FK_TRANS_SUPPLIER_ID"))
	private Supplier supplier;

	@Size(min = 1, max = 128, message = "{common.companyName.length}")
	@Column(name = "COMPANY_NAME", length = 128, nullable = true)
	private String companyName;

	@Size(min = 1, max = 128, message = "{common.fullName.length}")
	@Column(name = "FULL_NAME", length = 128, nullable = true)
	private String fullName;

	@Size(min = 1, max = 128, message = "{supplier.designation.length}", groups = { SupplierSignup.class })
	@Column(name = "DESIGNATION", length = 128)
	private String designation;

	@Size(min = 6, max = 16, message = "{supplier.mobileNumber.length}", groups = { SupplierStep1.class, SupplierSignup.class })
	@Column(name = "MOBILE_NUMBER", length = 16)
	private String mobileNumber;

	@Size(min = 8, max = 64, message = "{common.password.length}")
	@Column(name = "PASSWORD", length = 64)
	private String password;

	@Email(message = "{common.email.valid}")
	@Size(min = 6, max = 128, message = "{common.communicationEmail.length}")
	@Column(name = "COMMUNICATION_EMAIL", length = 128)
	private String communicationEmail;

	@Email(message = "{common.loginId.valid}")
	@Size(min = 6, max = 128, message = "{common.loginId.length}")
	@Column(name = "LOGIN_EMAIL", length = 128)
	private String loginEmail;

	@Size(min = 6, max = 16, message = "{common.companyContactNumber.length}")
	@Column(name = "COMPANY_CONTACT_NUMBER", length = 16)
	private String companyContactNumber;

	@JsonIgnore
	@NotNull(message = "{common.registrationOfCountry.empty}")
	@ManyToOne(fetch = FetchType.LAZY, optional = true)
	@JoinColumn(name = "REGISTRATION_COUNTRY", nullable = true, foreignKey = @ForeignKey(name = "FK_PAY_TX_REG_COUNTRY"))
	private Country country;

	@NotNull(message = "{common.companyRegistrationNumber.empty}")
	@Size(min = 1, max = 128, message = "{buyer.companyRegistrationNumber.length}")
	@Column(name = "COMPANY_REGISTRATION_NUMBER", length = 128, nullable = true)
	private String companyRegistrationNumber;

	@ManyToOne(fetch = FetchType.LAZY, optional = true)
	@JoinColumn(name = "SUPPLIER_PLAN_ID", nullable = true, foreignKey = @ForeignKey(name = "FK_PAY_TX_SUP_PLAN_ID"))
	private SupplierPlan supplierPlan;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, optional = true)
	@JoinColumn(name = "SUPPLIER_SUBSCRIPTION_ID", nullable = true, foreignKey = @ForeignKey(name = "FK_TRANS_SUP_SUBSC_ID"))
	private SupplierSubscription supplierSubscription;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, optional = true)
	@JoinColumn(name = "BUYER_SUBSCRIPTION_ID", nullable = true, foreignKey = @ForeignKey(name = "FK_TRANS__BUY_SUBSC_ID"))
	private BuyerSubscription buyerSubscription;

	@ManyToOne(fetch = FetchType.LAZY, optional = true)
	@JoinColumn(name = "BUYER_PLAN_ID", nullable = true, foreignKey = @ForeignKey(name = "FK_PAY_TX_BUYER_PLAN_ID"))
	private BuyerPlan buyerPlan;

	@Column(name = "REFRENCE_NUMBER", length = 64)
	private String refrenceNumber;

	@Column(name = "REFERENCE_TRANSACTION_ID", length = 64)
	private String referenceTransactionId;

	@Column(name = "PAYMENT_TOKEN", length = 150)
	private String paymentToken;

	@Enumerated(EnumType.STRING)
	@Column(name = "TRANSACTION_TYPE")
	private TransactionType type;

	@Column(name = "CURRENCY_CODE", length = 3)
	private String currencyCode;

	@Column(name = "AMOUNT", precision = 10, scale = 2)
	private BigDecimal amount;

	@Column(name = "PAYMENT_FEE", precision = 10, scale = 2)
	private BigDecimal paymentFee;

	@Enumerated(EnumType.STRING)
	@Column(name = "TRANSACTION_STATUS")
	private TransactionStatus status;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CREATED_DATE")
	private Date createdDate;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CONFIRMATION_DATE")
	private Date confirmationDate;

	@Column(name = "ERROR_CODE", length = 64)
	private String errorCode;

	@Column(name = "ERROR_MESSAGE", length = 2000)
	private String errorMessage;

	@Column(name = "WARN_MESSAGE", length = 2000)
	private String warnMessage;

	@Column(name = "PAYMENT_REMARKS", length = 2000)
	private String paymentRemarks;

	@Enumerated(EnumType.STRING)
	@Column(name = "PAYMENT_METHOD")
	private PaymentMethod paymentMethod;

	@Size(min = 0, max = 2050)
	@Column(name = "REMARKS", length = 2050)
	private String remarks;

	@ManyToOne(fetch = FetchType.LAZY, optional = true, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "PROMOTIONAL_CODE_ID", nullable = true, foreignKey = @ForeignKey(name = "FK__PAY_TRANS_PROMO_ID"))
	private PromotionalCode promoCode;

	@Column(name = "PRICE_AMOUNT", precision = 20, scale = 4, nullable = true)
	private BigDecimal priceAmount;

	@Column(name = "PRICE_DISCOUNT", precision = 20, scale = 4, nullable = true)
	private BigDecimal priceDiscount;

	@Column(name = "PROMO_CODE_DISCOUNT", precision = 20, scale = 4, nullable = true)
	private BigDecimal promoCodeDiscount;

	@Column(name = "TOTAL_PRICE_AMOUNT", precision = 20, scale = 4, nullable = true)
	private BigDecimal totalPriceAmount;

	@Column(name = "ADDITIONAL_TAX_DESC", length = 200)
	private String additionalTaxDesc;

	@Column(name = "ADDITIONAL_TAX", precision = 20, scale = 4, nullable = true)
	private BigDecimal additionalTax;

	@Column(name = "EXCHANGE_RATE", length = 20)
	private String exchangeRate;

	@Column(name = "IS_CAPTURE_PAYMENT")
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean isCapturePayment = Boolean.TRUE;

	@Column(name = "EVENT_QUANTITY", length = 6)
	private Integer eventQuantity;

	@Column(name = "USER_QUANTITY", length = 6)
	private Integer userQuantity;

	@Transient
	private String countryCode;

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
	 * @return the refrenceNumber
	 */
	public String getRefrenceNumber() {
		return refrenceNumber;
	}

	/**
	 * @param refrenceNumber the refrenceNumber to set
	 */
	public void setRefrenceNumber(String refrenceNumber) {
		this.refrenceNumber = refrenceNumber;
	}

	/**
	 * @return the referenceTransactionId
	 */
	public String getReferenceTransactionId() {
		return referenceTransactionId;
	}

	/**
	 * @param referenceTransactionId the referenceTransactionId to set
	 */
	public void setReferenceTransactionId(String referenceTransactionId) {
		this.referenceTransactionId = referenceTransactionId;
	}

	/**
	 * @return the type
	 */
	public TransactionType getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(TransactionType type) {
		this.type = type;
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
	 * @return the amount
	 */
	public BigDecimal getAmount() {
		return amount;
	}

	/**
	 * @param amount the amount to set
	 */
	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	/**
	 * @return the status
	 */
	public TransactionStatus getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(TransactionStatus status) {
		this.status = status;
	}

	/**
	 * @return the errorCode
	 */
	public String getErrorCode() {
		return errorCode;
	}

	/**
	 * @param errorCode the errorCode to set
	 */
	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	/**
	 * @return the paymentMethod
	 */
	public PaymentMethod getPaymentMethod() {
		return paymentMethod;
	}

	/**
	 * @param paymentMethod the paymentMethod to set
	 */
	public void setPaymentMethod(PaymentMethod paymentMethod) {
		this.paymentMethod = paymentMethod;
	}

	/**
	 * @return the paymentToken
	 */
	public String getPaymentToken() {
		return paymentToken;
	}

	/**
	 * @param paymentToken the paymentToken to set
	 */
	public void setPaymentToken(String paymentToken) {
		this.paymentToken = paymentToken;
	}

	/**
	 * @return the paymentFee
	 */
	public BigDecimal getPaymentFee() {
		return paymentFee;
	}

	/**
	 * @param paymentFee the paymentFee to set
	 */
	public void setPaymentFee(BigDecimal paymentFee) {
		this.paymentFee = paymentFee;
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
	 * @return the confirmationDate
	 */
	public Date getConfirmationDate() {
		return confirmationDate;
	}

	/**
	 * @param confirmationDate the confirmationDate to set
	 */
	public void setConfirmationDate(Date confirmationDate) {
		this.confirmationDate = confirmationDate;
	}

	/**
	 * @return the errorMessage
	 */
	public String getErrorMessage() {
		return errorMessage;
	}

	/**
	 * @param errorMessage the errorMessage to set
	 */
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	/**
	 * @return the warnMessage
	 */
	public String getWarnMessage() {
		return warnMessage;
	}

	/**
	 * @param warnMessage the warnMessage to set
	 */
	public void setWarnMessage(String warnMessage) {
		this.warnMessage = warnMessage;
	}

	/**
	 * @return the paymentRemarks
	 */
	public String getPaymentRemarks() {
		return paymentRemarks;
	}

	/**
	 * @param paymentRemarks the paymentRemarks to set
	 */
	public void setPaymentRemarks(String paymentRemarks) {
		this.paymentRemarks = paymentRemarks;
	}

	/**
	 * @return the companyName
	 */
	public String getCompanyName() {
		return companyName;
	}

	/**
	 * @param companyName the companyName to set
	 */
	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	/**
	 * @return the fullName
	 */
	public String getFullName() {
		return fullName;
	}

	/**
	 * @param fullName the fullName to set
	 */
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * @param password the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * @return the communicationEmail
	 */
	public String getCommunicationEmail() {
		return communicationEmail;
	}

	/**
	 * @param communicationEmail the communicationEmail to set
	 */
	public void setCommunicationEmail(String communicationEmail) {
		this.communicationEmail = communicationEmail;
	}

	/**
	 * @return the loginEmail
	 */
	public String getLoginEmail() {
		return loginEmail;
	}

	/**
	 * @param loginEmail the loginEmail to set
	 */
	public void setLoginEmail(String loginEmail) {
		this.loginEmail = loginEmail;
	}

	/**
	 * @return the companyContactNumber
	 */
	public String getCompanyContactNumber() {
		return companyContactNumber;
	}

	/**
	 * @param companyContactNumber the companyContactNumber to set
	 */
	public void setCompanyContactNumber(String companyContactNumber) {
		this.companyContactNumber = companyContactNumber;
	}

	/**
	 * @return the country
	 */
	public Country getCountry() {
		return country;
	}

	/**
	 * @param country the country to set
	 */
	public void setCountry(Country country) {
		this.country = country;
	}

	/**
	 * @return the companyRegistrationNumber
	 */
	public String getCompanyRegistrationNumber() {
		return companyRegistrationNumber;
	}

	/**
	 * @param companyRegistrationNumber the companyRegistrationNumber to set
	 */
	public void setCompanyRegistrationNumber(String companyRegistrationNumber) {
		this.companyRegistrationNumber = companyRegistrationNumber;
	}

	/**
	 * @return the supplierPlan
	 */
	public SupplierPlan getSupplierPlan() {
		return supplierPlan;
	}

	/**
	 * @param supplierPlan the supplierPlan to set
	 */
	public void setSupplierPlan(SupplierPlan supplierPlan) {
		this.supplierPlan = supplierPlan;
	}

	/**
	 * @return the supplierSubscription
	 */
	public SupplierSubscription getSupplierSubscription() {
		return supplierSubscription;
	}

	/**
	 * @param supplierSubscription the supplierSubscription to set
	 */
	public void setSupplierSubscription(SupplierSubscription supplierSubscription) {
		this.supplierSubscription = supplierSubscription;
	}

	/**
	 * @return the designation
	 */
	public String getDesignation() {
		return designation;
	}

	/**
	 * @param designation the designation to set
	 */
	public void setDesignation(String designation) {
		this.designation = designation;
	}

	/**
	 * @return the mobileNumber
	 */
	public String getMobileNumber() {
		return mobileNumber;
	}

	/**
	 * @param mobileNumber the mobileNumber to set
	 */
	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}

	/**
	 * @return the remarks
	 */
	public String getRemarks() {
		return remarks;
	}

	/**
	 * @param remarks the remarks to set
	 */
	public void setRemarks(String remarks) {
		this.remarks = remarks;
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
	 * @return the buyerSubscription
	 */
	public BuyerSubscription getBuyerSubscription() {
		return buyerSubscription;
	}

	/**
	 * @param buyerSubscription the buyerSubscription to set
	 */
	public void setBuyerSubscription(BuyerSubscription buyerSubscription) {
		this.buyerSubscription = buyerSubscription;
	}

	/**
	 * @return the buyerPlan
	 */
	public BuyerPlan getBuyerPlan() {
		return buyerPlan;
	}

	/**
	 * @param buyerPlan the buyerPlan to set
	 */
	public void setBuyerPlan(BuyerPlan buyerPlan) {
		this.buyerPlan = buyerPlan;
	}

	/**
	 * @return the promoCode
	 */
	public PromotionalCode getPromoCode() {
		return promoCode;
	}

	/**
	 * @param promoCode the promoCode to set
	 */
	public void setPromoCode(PromotionalCode promoCode) {
		this.promoCode = promoCode;
	}

	/**
	 * @return the priceAmount
	 */
	public BigDecimal getPriceAmount() {
		return priceAmount;
	}

	/**
	 * @param priceAmount the priceAmount to set
	 */
	public void setPriceAmount(BigDecimal priceAmount) {
		this.priceAmount = priceAmount;
	}

	/**
	 * @return the priceDiscount
	 */
	public BigDecimal getPriceDiscount() {
		return priceDiscount;
	}

	/**
	 * @param priceDiscount the priceDiscount to set
	 */
	public void setPriceDiscount(BigDecimal priceDiscount) {
		this.priceDiscount = priceDiscount;
	}

	/**
	 * @return the promoCodeDiscount
	 */
	public BigDecimal getPromoCodeDiscount() {
		return promoCodeDiscount;
	}

	/**
	 * @param promoCodeDiscount the promoCodeDiscount to set
	 */
	public void setPromoCodeDiscount(BigDecimal promoCodeDiscount) {
		this.promoCodeDiscount = promoCodeDiscount;
	}

	/**
	 * @return the totalPriceAmount
	 */
	public BigDecimal getTotalPriceAmount() {
		return totalPriceAmount;
	}

	/**
	 * @param totalPriceAmount the totalPriceAmount to set
	 */
	public void setTotalPriceAmount(BigDecimal totalPriceAmount) {
		this.totalPriceAmount = totalPriceAmount;
	}

	/**
	 * @return the additionalTaxDesc
	 */
	public String getAdditionalTaxDesc() {
		return additionalTaxDesc;
	}

	/**
	 * @param additionalTaxDesc the additionalTaxDesc to set
	 */
	public void setAdditionalTaxDesc(String additionalTaxDesc) {
		this.additionalTaxDesc = additionalTaxDesc;
	}

	/**
	 * @return the additionalTax
	 */
	public BigDecimal getAdditionalTax() {
		return additionalTax;
	}

	/**
	 * @param additionalTax the additionalTax to set
	 */
	public void setAdditionalTax(BigDecimal additionalTax) {
		this.additionalTax = additionalTax;
	}

	/**
	 * @return the exchangeRate
	 */
	public String getExchangeRate() {
		return exchangeRate;
	}

	/**
	 * @param exchangeRate the exchangeRate to set
	 */
	public void setExchangeRate(String exchangeRate) {
		this.exchangeRate = exchangeRate;
	}

	/**
	 * @return the isCapturePayment
	 */
	public Boolean getIsCapturePayment() {
		return isCapturePayment;
	}

	/**
	 * @param isCapturePayment the isCapturePayment to set
	 */
	public void setIsCapturePayment(Boolean isCapturePayment) {
		this.isCapturePayment = isCapturePayment;
	}

	public Integer getEventQuantity() {
		return eventQuantity;
	}

	public void setEventQuantity(Integer eventQuantity) {
		this.eventQuantity = eventQuantity;
	}

	public Integer getUserQuantity() {
		return userQuantity;
	}

	public void setUserQuantity(Integer userQuantity) {
		this.userQuantity = userQuantity;
	}

	public String getCountryCode() {
		try {
			if (this.country != null) {
				this.countryCode = this.country.getCountryCode();
			}
		} catch (Exception e) {
		}
		return countryCode;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

}
