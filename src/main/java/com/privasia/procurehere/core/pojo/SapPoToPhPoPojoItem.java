package com.privasia.procurehere.core.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotNull;

public class SapPoToPhPoPojoItem {

    @JsonProperty("NO")
    private String no;

    @JsonProperty("SECTION_NAME")
    private String sectionName;

    @JsonProperty("ITEM_INDICATOR")
    private String itemIndicator;

    @JsonProperty("ITEM_CATEGORY")
    private String itemCategory;

    @JsonProperty("ITEM_NAME")
    private String itemName;

    @JsonProperty("ITEM_DESCRIPTION")
    private String itemDescription;

    @JsonProperty("UOM")
    private String uom;

    @JsonProperty("ITEM_QUANTITY")
    private String quantity;

    @JsonProperty("UNIT_PRICE")
    private String unitPrice;

    @JsonProperty("PRICE_PER_UNIT")
    private String per;

    @JsonProperty("CHILD_BUSINESS_UNIT")
    private String childBusinessUnit;


    public String getSectionName() {
        return sectionName;
    }

    public void setSectionName(String sectionName) {
        this.sectionName = sectionName;
    }

    public String getItemIndicator() {
        return itemIndicator;
    }

    public void setItemIndicator(String itemIndicator) {
        this.itemIndicator = itemIndicator;
    }

    public String getItemCategory() {
        return itemCategory;
    }

    public void setItemCategory(String itemCategory) {
        this.itemCategory = itemCategory;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getItemDescription() {
        return itemDescription;
    }

    public void setItemDescription(String itemDescription) {
        this.itemDescription = itemDescription;
    }

    public String getUom() {
        return uom;
    }

    public void setUom(String uom) {
        this.uom = uom;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(String unitPrice) {
        this.unitPrice = unitPrice;
    }

    public String getPer() {
        return per;
    }

    public void setPer(String per) {
        this.per = per;
    }

    public String getChildBusinessUnit() {
        return childBusinessUnit;
    }

    public void setChildBusinessUnit(String childBusinessUnit) {
        this.childBusinessUnit = childBusinessUnit;
    }


    public String getNo() {
        return no;
    }

    public void setNo(String no) {
        this.no = no;
    }

    @Override
    public String toString() {
        return "SapPoToPhPoPojoItem{" +
                "no='" + no + '\'' +
                ", sectionName='" + sectionName + '\'' +
                ", itemIndicator='" + itemIndicator + '\'' +
                ", itemCategory='" + itemCategory + '\'' +
                ", itemName='" + itemName + '\'' +
                ", itemDescription='" + itemDescription + '\'' +
                ", uom='" + uom + '\'' +
                ", quantity='" + quantity + '\'' +
                ", unitPrice='" + unitPrice + '\'' +
                ", per='" + per + '\'' +
                ", childBusinessUnit='" + childBusinessUnit + '\'' +
                '}';
    }
}
