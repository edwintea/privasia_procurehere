package com.privasia.procurehere.core.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public class ItemContractPojo {

    private String itemNumber;
    private String deleteIndex;
    private String material;
    private String shortText;
    private String maltGroup;
    private String plant;
    private String storageLoc;
    private BigDecimal targetQty;
    private String poUnit;
    private BigDecimal netPrice;
    private BigDecimal priceUnit;

    public ItemContractPojo(String itemNumber, String deleteIndex, String material, String shortText,
                            String maltGroup, String plant, String storageLoc, BigDecimal targetQty, String poUnit, BigDecimal netPrice, BigDecimal priceUnit) {
        this.itemNumber = itemNumber;
        this.deleteIndex = deleteIndex;
        this.material = material;
        this.shortText = shortText;
        this.maltGroup = maltGroup;
        this.plant = plant;
        this.storageLoc = storageLoc;
        this.targetQty = targetQty;
        this.poUnit = poUnit;
        this.netPrice = netPrice;
        this.priceUnit = priceUnit;
    }

    public ItemContractPojo() {

    }

    public String getItemNumber() {
        return itemNumber;
    }

    public void setItemNumber(String itemNumber) {
        this.itemNumber = itemNumber;
    }

    public String getDeleteIndex() {
        return deleteIndex;
    }

    public void setDeleteIndex(String deleteIndex) {
        this.deleteIndex = deleteIndex;
    }

    public String getMaterial() {
        return material;
    }

    public void setMaterial(String material) {
        this.material = material;
    }

    public String getShortText() {
        return shortText;
    }

    public void setShortText(String shortText) {
        this.shortText = shortText;
    }

    public String getMaltGroup() {
        return maltGroup;
    }

    public void setMaltGroup(String maltGroup) {
        this.maltGroup = maltGroup;
    }

    public String getPlant() {
        return plant;
    }

    public void setPlant(String plant) {
        this.plant = plant;
    }

    public String getStorageLoc() {
        return storageLoc;
    }

    public void setStorageLoc(String storageLoc) {
        this.storageLoc = storageLoc;
    }

    public BigDecimal getTargetQty() {
        return targetQty;
    }

    public void setTargetQty(BigDecimal targetQty) {
        this.targetQty = targetQty;
    }

    public String getPoUnit() {
        return poUnit;
    }

    public void setPoUnit(String poUnit) {
        this.poUnit = poUnit;
    }

    public BigDecimal getNetPrice() {
        return netPrice;
    }

    public void setNetPrice(BigDecimal netPrice) {
        this.netPrice = netPrice;
    }

    public BigDecimal getPriceUnit() {
        return priceUnit;
    }

    public void setPriceUnit(BigDecimal priceUnit) {
        this.priceUnit = priceUnit;
    }

    @Override
    public String toString() {
        return "ItemContractPojo{" +
                "itemNumber='" + itemNumber + '\'' +
                ", deleteIndex='" + deleteIndex + '\'' +
                ", material='" + material + '\'' +
                ", shortText='" + shortText + '\'' +
                ", maltGroup='" + maltGroup + '\'' +
                ", plant='" + plant + '\'' +
                ", storageLoc='" + storageLoc + '\'' +
                ", targetQty=" + targetQty +
                ", poUnit='" + poUnit + '\'' +
                ", netPrice=" + netPrice +
                ", priceUnit=" + priceUnit +
                '}';
    }
}
