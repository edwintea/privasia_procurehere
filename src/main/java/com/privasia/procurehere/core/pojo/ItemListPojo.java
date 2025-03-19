package com.privasia.procurehere.core.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.Date;

public class ItemListPojo implements Serializable {

    private static final long serialVersionUID = 9003208397781717558L;
    private String PreqItem;
    private String ShortText;
    private String quantity;
    private String Unit;
    private String DelivDate;
    private String Plant;
    private String PurchOrg;
    private String PreqDate;
    private String PriceUnit;
    private String PreqPrice;
    private String DesVendor;
    private String StoreLoc;
    private String Material;
    private String longText;
    private String MatlGroup;
    private String PurGroup;
    private String AcctAssCat;

    public ItemListPojo(String preqItem, String shortText, String quantity,
                        String unit, String delivDate, String plant, String purchOrg, String preqDate, String priceUnit,
                        String preqPrice, String desVendor, String storeLoc, String material, String matlGroup,
                        String purGroup, String acctAssCat, String longText) {
        PreqItem = preqItem;
        ShortText = shortText;
        this.quantity = quantity;
        Unit = unit;
        DelivDate = delivDate;
        Plant = plant;
        PurchOrg = purchOrg;
        PreqDate = preqDate;
        PriceUnit = priceUnit;
        PreqPrice = preqPrice;
        DesVendor = desVendor;
        StoreLoc = storeLoc;
        Material = material;
        this.longText = longText;
        MatlGroup = matlGroup;
        PurGroup = purGroup;
        AcctAssCat = acctAssCat;
    }

    public ItemListPojo() {

    }

    public String getPreqItem() {
        return PreqItem;
    }

    public void setPreqItem(String preqItem) {
        PreqItem = preqItem;
    }

    public String getShortText() {
        return ShortText;
    }

    public void setShortText(String shortText) {
        ShortText = shortText;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getUnit() {
        return Unit;
    }

    public void setUnit(String unit) {
        Unit = unit;
    }

    public String getDelivDate() {
        return DelivDate;
    }

    public void setDelivDate(String delivDate) {
        DelivDate = delivDate;
    }

    public String getPlant() {
        return Plant;
    }

    public void setPlant(String plant) {
        Plant = plant;
    }

    public String getPurchOrg() {
        return PurchOrg;
    }

    public void setPurchOrg(String purchOrg) {
        PurchOrg = purchOrg;
    }

    public String getPreqDate() {
        return PreqDate;
    }

    public void setPreqDate(String preqDate) {
        PreqDate = preqDate;
    }

    public String getPriceUnit() {
        return PriceUnit;
    }

    public void setPriceUnit(String priceUnit) {
        PriceUnit = priceUnit;
    }

    public String getPreqPrice() {
        return PreqPrice;
    }

    public void setPreqPrice(String preqPrice) {
        PreqPrice = preqPrice;
    }

    public String getDesVendor() {
        return DesVendor;
    }

    public void setDesVendor(String desVendor) {
        DesVendor = desVendor;
    }

    public String getStoreLoc() {
        return StoreLoc;
    }

    public void setStoreLoc(String storeLoc) {
        StoreLoc = storeLoc;
    }

    public String getMaterial() {
        return Material;
    }

    public void setMaterial(String material) {
        Material = material;
    }

    public String getMatlGroup() {
        return MatlGroup;
    }

    public void setMatlGroup(String matlGroup) {
        MatlGroup = matlGroup;
    }

    public String getPurGroup() {
        return PurGroup;
    }

    public void setPurGroup(String purGroup) {
        PurGroup = purGroup;
    }

    public String getAcctAssCat() {
        return AcctAssCat;
    }

    public void setAcctAssCat(String acctAssCat) {
        AcctAssCat = acctAssCat;
    }

    public String getLongText() {
        return longText;
    }

    public void setLongText(String longText) {
        this.longText = longText;
    }
}
