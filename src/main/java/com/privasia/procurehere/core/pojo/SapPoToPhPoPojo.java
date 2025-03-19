package com.privasia.procurehere.core.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

public class SapPoToPhPoPojo {

    @JsonProperty("PO_NAME")
    private String poName;

    @JsonProperty("REFERENCE_NUMBER")
    private String referenceNumber;

    @JsonProperty("SUPPLIER_NAME")
    private String supplierName;


    @JsonProperty("COMPANY_REGISTRATION_NUMBER")
    private String supplierRegistrationNumber;

    @JsonProperty("FAV_VENDOR_CODE")
    private String supplierCode;

    @JsonProperty("PARENT_BUSINESS_UNIT")
    private String parentBusinessUnitCode;

    @JsonProperty("CREATED_BY")
    private String poCreator;

    @JsonProperty("REQUESTER")
    private String requester;

    @JsonProperty("BASE_CURRENCY")
    private String baseCurrency;

    @JsonProperty("PO_DECIMAL")
    private Integer decimal;

    @JsonProperty("PAYMENT_TERM")
    private String paymentTerms;

//    @NotNull(message = "PAYMENT_DAYS cannot be null")
//    @JsonProperty("PAYMENT_DAYS")
//    private Integer paymentDays;

    @JsonProperty("CORRESPON_ADDRESS")
    private String correspondenceAddress;

    @JsonProperty("SUPPLIER_INFO")
    private String supplierInfo;


    @JsonProperty("SUPPLIER_ADDRESS")
    private String supplierAddress;

    @JsonProperty("DELIVERY_ADDRESS")
    private String deliveryAddress;

    @JsonProperty("DELIVERY_RECEIVER")
    private String receiver;

    @JsonProperty("DELIVERY_DATE")
    private String deliveryDateTime;

    public String getPoName() {
        return poName;
    }

    public void setPoName(String poName) {
        this.poName = poName;
    }

    public String getReferenceNumber() {
        return referenceNumber;
    }

    public void setReferenceNumber(String referenceNumber) {
        this.referenceNumber = referenceNumber;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public String getSupplierRegistrationNumber() {
        return supplierRegistrationNumber;
    }

    public void setSupplierRegistrationNumber(String supplierRegistrationNumber) {
        this.supplierRegistrationNumber = supplierRegistrationNumber;
    }

    public String getSupplierCode() {
        return supplierCode;
    }

    public void setSupplierCode(String supplierCode) {
        this.supplierCode = supplierCode;
    }

    public String getParentBusinessUnitCode() {
        return parentBusinessUnitCode;
    }

    public void setParentBusinessUnitCode(String parentBusinessUnitCode) {
        this.parentBusinessUnitCode = parentBusinessUnitCode;
    }

    public String getPoCreator() {
        return poCreator;
    }

    public void setPoCreator(String poCreator) {
        this.poCreator = poCreator;
    }

    public String getRequester() {
        return requester;
    }

    public void setRequester(String requester) {
        this.requester = requester;
    }

    public String getBaseCurrency() {
        return baseCurrency;
    }

    public void setBaseCurrency(String baseCurrency) {
        this.baseCurrency = baseCurrency;
    }

    public Integer getDecimal() {
        return decimal;
    }

    public void setDecimal(Integer decimal) {
        this.decimal = decimal;
    }

    public String getPaymentTerms() {
        return paymentTerms;
    }

    public void setPaymentTerms(String paymentTerms) {
        this.paymentTerms = paymentTerms;
    }

    public String getCorrespondenceAddress() {
        return correspondenceAddress;
    }

    public void setCorrespondenceAddress(String correspondenceAddress) {
        this.correspondenceAddress = correspondenceAddress;
    }

    public String getSupplierInfo() {
        return supplierInfo;
    }

    public void setSupplierInfo(String supplierInfo) {
        this.supplierInfo = supplierInfo;
    }

    public String getSupplierAddress() {
        return supplierAddress;
    }

    public void setSupplierAddress(String supplierAddress) {
        this.supplierAddress = supplierAddress;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getDeliveryDateTime() {
        return deliveryDateTime;
    }

    public String getDeliveryAddress() {
        return deliveryAddress;
    }

    public void setDeliveryAddress(String deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }

    public void setDeliveryDateTime(String deliveryDateTime) {
        this.deliveryDateTime = deliveryDateTime;
    }

    @Override
    public String toString() {
        return "SapPoToPhPoPojo{" +
                "poName='" + poName + '\'' +
                ", referenceNumber='" + referenceNumber + '\'' +
                ", supplierName='" + supplierName + '\'' +
                ", supplierRegistrationNumber='" + supplierRegistrationNumber + '\'' +
                ", supplierCode='" + supplierCode + '\'' +
                ", parentBusinessUnitCode='" + parentBusinessUnitCode + '\'' +
                ", poCreator='" + poCreator + '\'' +
                ", requester='" + requester + '\'' +
                ", baseCurrency='" + baseCurrency + '\'' +
                ", decimal=" + decimal +
                ", paymentTerms='" + paymentTerms + '\'' +
                ", correspondenceAddress='" + correspondenceAddress + '\'' +
                ", supplierInfo='" + supplierInfo + '\'' +
                ", supplierAddress='" + supplierAddress + '\'' +
                ", deliveryAddress='" + deliveryAddress + '\'' +
                ", receiver='" + receiver + '\'' +
                ", deliveryDateTime='" + deliveryDateTime + '\'' +
                '}';
    }
}
