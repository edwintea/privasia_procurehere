package com.privasia.procurehere.core.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public class SapContractPojo {

    private String docNumber;
    private String compCode;
    private String docType;
    private String vendor;
    private String purchOrg;
    private String purchGroup;
    private String currency;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd.MM.yyyy")
    private Date docDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd.MM.yyyy")
    private Date contStartDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd.MM.yyyy")
    private Date contEndDate;
    private BigDecimal contValue;
    private String ourRef;
    private String jenisKerja;

    private String decimal;

    private List<ItemContractPojo> itemContractList;

    public SapContractPojo(String docNumber, String compCode, String docType, String vendor, String purchOrg,
                           String purchGroup, String currency, Date docDate, Date contStartDate,
                           Date contEndDate, BigDecimal contValue, String ourRef, String jenisKerja, List<ItemContractPojo> itemContractList) {
        this.docNumber = docNumber;
        this.compCode = compCode;
        this.docType = docType;
        this.vendor = vendor;
        this.purchOrg = purchOrg;
        this.purchGroup = purchGroup;
        this.currency = currency;
        this.docDate = docDate;
        this.contStartDate = contStartDate;
        this.contEndDate = contEndDate;
        this.contValue = contValue;
        this.ourRef = ourRef;
        this.jenisKerja = jenisKerja;
        this.itemContractList = itemContractList;
    }

    public SapContractPojo() {

    }

    public String getDocNumber() {
        return docNumber;
    }

    public void setDocNumber(String docNumber) {
        this.docNumber = docNumber;
    }

    public String getCompCode() {
        return compCode;
    }

    public void setCompCode(String compCode) {
        this.compCode = compCode;
    }

    public String getDocType() {
        return docType;
    }

    public void setDocType(String docType) {
        this.docType = docType;
    }

    public String getVendor() {
        return vendor;
    }

    public void setVendor(String vendor) {
        this.vendor = vendor;
    }

    public String getPurchOrg() {
        return purchOrg;
    }

    public void setPurchOrg(String purchOrg) {
        this.purchOrg = purchOrg;
    }

    public String getPurchGroup() {
        return purchGroup;
    }

    public void setPurchGroup(String purchGroup) {
        this.purchGroup = purchGroup;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Date getDocDate() {
        return docDate;
    }

    public void setDocDate(Date docDate) {
        this.docDate = docDate;
    }

    public Date getContStartDate() {
        return contStartDate;
    }

    public void setContStartDate(Date contStartDate) {
        this.contStartDate = contStartDate;
    }

    public Date getContEndDate() {
        return contEndDate;
    }

    public void setContEndDate(Date contEndDate) {
        this.contEndDate = contEndDate;
    }

    public BigDecimal getContValue() {
        return contValue;
    }

    public void setContValue(BigDecimal contValue) {
        this.contValue = contValue;
    }

    public String getOurRef() {
        return ourRef;
    }

    public void setOurRef(String ourRef) {
        this.ourRef = ourRef;
    }

    public String getJenisKerja() {
        return jenisKerja;
    }

    public void setJenisKerja(String jenisKerja) {
        this.jenisKerja = jenisKerja;
    }

    public List<ItemContractPojo> getItemContractList() {
        return itemContractList;
    }

    public void setItemContractList(List<ItemContractPojo> itemContractList) {
        this.itemContractList = itemContractList;
    }

    public String getDecimal() {
        return decimal;
    }

    public void setDecimal(String decimal) {
        this.decimal = decimal;
    }

    @Override
    public String toString() {
        return "SapContractPojo{" +
                "docNumber='" + docNumber + '\'' +
                ", compCode='" + compCode + '\'' +
                ", docType='" + docType + '\'' +
                ", vendor='" + vendor + '\'' +
                ", purchOrg='" + purchOrg + '\'' +
                ", purchGroup='" + purchGroup + '\'' +
                ", currency='" + currency + '\'' +
                ", docDate=" + docDate +
                ", contStartDate=" + contStartDate +
                ", contEndDate=" + contEndDate +
                ", contValue=" + contValue +
                ", ourRef='" + ourRef + '\'' +
                ", jenisKerja='" + jenisKerja + '\'' +
                ", decimal='" + decimal + '\'' +
                ", itemContractList=" + itemContractList +
                '}';
    }
}
