package com.privasia.procurehere.core.pojo;

import java.io.Serializable;
import java.math.BigDecimal;

import com.privasia.procurehere.core.entity.BusinessUnit;
import com.privasia.procurehere.core.entity.BuyerAddress;
import com.privasia.procurehere.core.entity.CostCenter;
import com.privasia.procurehere.core.entity.Currency;
import com.privasia.procurehere.core.entity.PaymentTermes;

/**
 * @author parveen
 */
public class PrTemplateFieldPojo implements Serializable {

	private static final long serialVersionUID = 1495143731470745248L;

	private String prName;
	private Boolean prNameDisabled = Boolean.TRUE;

	private String requester;
	private Boolean requesterDisabled = Boolean.TRUE;

	private BuyerAddress correspondenceAddress;
	private Boolean correspondenceAddressDisabled = Boolean.TRUE;

	private Currency baseCurrency;
	private Boolean baseCurrencyDisabled = Boolean.TRUE;

	private String decimal;
	private Boolean decimalDisabled = Boolean.TRUE;

	private CostCenter costCenter;

	private Boolean costCenterVisible = true;
	private Boolean costCenterDisabled = Boolean.TRUE;
	private Boolean costCenterOptional = Boolean.TRUE;

	private BigDecimal availableBudget;
	private Boolean availableBudgetVisible = true;
	private Boolean availableBudgetDisabled = Boolean.TRUE;
	private Boolean availableBudgetOptional = Boolean.TRUE;

	private BusinessUnit businessUnit;
	private Boolean businessUnitDisabled = Boolean.TRUE;

	private PaymentTermes paymentTerms;
	private Boolean paymentTermsDisabled = Boolean.TRUE;

	private String termAndCondition;
	private Boolean termAndConditionDisabled = Boolean.TRUE;

	private Boolean hideOpenSupplier = Boolean.FALSE;

	private BigDecimal conversionRate;
	private Boolean conversionRateDisabled = Boolean.TRUE;
	
	private PaymentTermes paymentTermes;

	public PrTemplateFieldPojo() {
		this.prNameDisabled = Boolean.TRUE;
		this.requesterDisabled = Boolean.TRUE;
		this.correspondenceAddressDisabled = Boolean.TRUE;
		this.baseCurrencyDisabled = Boolean.TRUE;
		this.decimalDisabled = Boolean.TRUE;
		this.costCenterVisible = Boolean.TRUE;
		this.costCenterDisabled = Boolean.TRUE;
		this.costCenterOptional = Boolean.TRUE;
		this.baseCurrencyDisabled = Boolean.TRUE;
		this.paymentTermsDisabled = Boolean.TRUE;
		this.hideOpenSupplier = Boolean.FALSE;
	}

	/**
	 * @param currency
	 * @param decimal
	 */
	public PrTemplateFieldPojo(Currency currency, String decimal) {
		this.baseCurrency = currency;
		this.decimal = decimal;
	}
	
	/**
	 * @return the paymentTermes
	 */
	public PaymentTermes getPaymentTermes() {
		return paymentTermes;
	}

	/**
	 * @param paymentTermes the paymentTermes to set
	 */
	public void setPaymentTermes(PaymentTermes paymentTermes) {
		this.paymentTermes = paymentTermes;
	}

	/**
	 * @return the prName
	 */
	public String getPrName() {
		return prName;
	}

	/**
	 * @param prName the prName to set
	 */
	public void setPrName(String prName) {
		this.prName = prName;
	}

	/**
	 * @return the prNameDisabled
	 */
	public Boolean getPrNameDisabled() {
		return prNameDisabled;
	}

	/**
	 * @param prNameDisabled the prNameDisabled to set
	 */
	public void setPrNameDisabled(Boolean prNameDisabled) {
		this.prNameDisabled = prNameDisabled;
	}

	/**
	 * @return the requester
	 */
	public String getRequester() {
		return requester;
	}

	/**
	 * @param requester the requester to set
	 */
	public void setRequester(String requester) {
		this.requester = requester;
	}

	/**
	 * @return the requesterDisabled
	 */
	public Boolean getRequesterDisabled() {
		return requesterDisabled;
	}

	/**
	 * @param requesterDisabled the requesterDisabled to set
	 */
	public void setRequesterDisabled(Boolean requesterDisabled) {
		this.requesterDisabled = requesterDisabled;
	}

	/**
	 * @return the baseCurrency
	 */
	public Currency getBaseCurrency() {
		return baseCurrency;
	}

	/**
	 * @param baseCurrency the baseCurrency to set
	 */
	public void setBaseCurrency(Currency baseCurrency) {
		this.baseCurrency = baseCurrency;
	}

	/**
	 * @return the baseCurrencyDisabled
	 */
	public Boolean getBaseCurrencyDisabled() {
		return baseCurrencyDisabled;
	}

	/**
	 * @param baseCurrencyDisabled the baseCurrencyDisabled to set
	 */
	public void setBaseCurrencyDisabled(Boolean baseCurrencyDisabled) {
		this.baseCurrencyDisabled = baseCurrencyDisabled;
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
	 * @return the decimalDisabled
	 */
	public Boolean getDecimalDisabled() {
		return decimalDisabled;
	}

	/**
	 * @param decimalDisabled the decimalDisabled to set
	 */
	public void setDecimalDisabled(Boolean decimalDisabled) {
		this.decimalDisabled = decimalDisabled;
	}

	/**
	 * @return the costCenter
	 */
	public CostCenter getCostCenter() {
		return costCenter;
	}

	/**
	 * @param costCenter the costCenter to set
	 */
	public void setCostCenter(CostCenter costCenter) {
		this.costCenter = costCenter;
	}

	/**
	 * @return the costCenterVisible
	 */
	public Boolean getCostCenterVisible() {
		return costCenterVisible;
	}

	/**
	 * @param costCenterVisible the costCenterVisible to set
	 */
	public void setCostCenterVisible(Boolean costCenterVisible) {
		this.costCenterVisible = costCenterVisible;
	}

	/**
	 * @return the costCenterDisabled
	 */
	public Boolean getCostCenterDisabled() {
		return costCenterDisabled;
	}

	/**
	 * @param costCenterDisabled the costCenterDisabled to set
	 */
	public void setCostCenterDisabled(Boolean costCenterDisabled) {
		this.costCenterDisabled = costCenterDisabled;
	}

	/**
	 * @return the costCenterOptional
	 */
	public Boolean getCostCenterOptional() {
		return costCenterOptional;
	}

	/**
	 * @param costCenterOptional the costCenterOptional to set
	 */
	public void setCostCenterOptional(Boolean costCenterOptional) {
		this.costCenterOptional = costCenterOptional;
	}

	/**
	 * @return the businessUnit
	 */
	public BusinessUnit getBusinessUnit() {
		return businessUnit;
	}

	/**
	 * @param businessUnit the businessUnit to set
	 */
	public void setBusinessUnit(BusinessUnit businessUnit) {
		this.businessUnit = businessUnit;
	}

	/**
	 * @return the businessUnitDisabled
	 */
	public Boolean getBusinessUnitDisabled() {
		return businessUnitDisabled;
	}

	/**
	 * @param businessUnitDisabled the businessUnitDisabled to set
	 */
	public void setBusinessUnitDisabled(Boolean businessUnitDisabled) {
		this.businessUnitDisabled = businessUnitDisabled;
	}

	/**
	 * @return the paymentTerms
	 */
	public PaymentTermes getPaymentTerms() {
		return paymentTerms;
	}

	/**
	 * @param paymentTerms the paymentTerms to set
	 */
	public void setPaymentTerms(PaymentTermes paymentTerms) {
		this.paymentTerms = paymentTerms;
	}

	/**
	 * @return the paymentTermsDisabled
	 */
	public Boolean getPaymentTermsDisabled() {
		return paymentTermsDisabled;
	}

	/**
	 * @param paymentTermsDisabled the paymentTermsDisabled to set
	 */
	public void setPaymentTermsDisabled(Boolean paymentTermsDisabled) {
		this.paymentTermsDisabled = paymentTermsDisabled;
	}

	/**
	 * @return the correspondenceAddress
	 */
	public BuyerAddress getCorrespondenceAddress() {
		return correspondenceAddress;
	}

	/**
	 * @param correspondenceAddress the correspondenceAddress to set
	 */
	public void setCorrespondenceAddress(BuyerAddress correspondenceAddress) {
		this.correspondenceAddress = correspondenceAddress;
	}

	/**
	 * @return the correspondenceAddressDisabled
	 */
	public Boolean getCorrespondenceAddressDisabled() {
		return correspondenceAddressDisabled;
	}

	/**
	 * @param correspondenceAddressDisabled the correspondenceAddressDisabled to set
	 */
	public void setCorrespondenceAddressDisabled(Boolean correspondenceAddressDisabled) {
		this.correspondenceAddressDisabled = correspondenceAddressDisabled;
	}

	/**
	 * @return the termAndCondition
	 */
	public String getTermAndCondition() {
		return termAndCondition;
	}

	/**
	 * @param termAndCondition the termAndCondition to set
	 */
	public void setTermAndCondition(String termAndCondition) {
		this.termAndCondition = termAndCondition;
	}

	/**
	 * @return the termAndConditionDisabled
	 */
	public Boolean getTermAndConditionDisabled() {
		return termAndConditionDisabled;
	}

	/**
	 * @param termAndConditionDisabled the termAndConditionDisabled to set
	 */
	public void setTermAndConditionDisabled(Boolean termAndConditionDisabled) {
		this.termAndConditionDisabled = termAndConditionDisabled;
	}

	/**
	 * @return the availableBudget
	 */
	public BigDecimal getAvailableBudget() {
		return availableBudget;
	}

	/**
	 * @param availableBudget the availableBudget to set
	 */
	public void setAvailableBudget(BigDecimal availableBudget) {
		this.availableBudget = availableBudget;
	}

	/**
	 * @return the availableBudgetVisible
	 */
	public Boolean getAvailableBudgetVisible() {
		return availableBudgetVisible;
	}

	/**
	 * @param availableBudgetVisible the availableBudgetVisible to set
	 */
	public void setAvailableBudgetVisible(Boolean availableBudgetVisible) {
		this.availableBudgetVisible = availableBudgetVisible;
	}

	/**
	 * @return the availableBudgetDisabled
	 */
	public Boolean getAvailableBudgetDisabled() {
		return availableBudgetDisabled;
	}

	/**
	 * @param availableBudgetDisabled the availableBudgetDisabled to set
	 */
	public void setAvailableBudgetDisabled(Boolean availableBudgetDisabled) {
		this.availableBudgetDisabled = availableBudgetDisabled;
	}

	/**
	 * @return the availableBudgetOptional
	 */
	public Boolean getAvailableBudgetOptional() {
		return availableBudgetOptional;
	}

	/**
	 * @param availableBudgetOptional the availableBudgetOptional to set
	 */
	public void setAvailableBudgetOptional(Boolean availableBudgetOptional) {
		this.availableBudgetOptional = availableBudgetOptional;
	}

	/**
	 * @return the hideOpenSupplier
	 */
	public Boolean getHideOpenSupplier() {
		return hideOpenSupplier;
	}

	/**
	 * @param hideOpenSupplier the hideOpenSupplier to set
	 */
	public void setHideOpenSupplier(Boolean hideOpenSupplier) {
		this.hideOpenSupplier = hideOpenSupplier;
	}

	public BigDecimal getConversionRate() {
		return conversionRate;
	}

	public void setConversionRate(BigDecimal conversionRate) {
		this.conversionRate = conversionRate;
	}

	public Boolean getConversionRateDisabled() {
		return conversionRateDisabled;
	}

	public void setConversionRateDisabled(Boolean conversionRateDisabled) {
		this.conversionRateDisabled = conversionRateDisabled;
	}

}
