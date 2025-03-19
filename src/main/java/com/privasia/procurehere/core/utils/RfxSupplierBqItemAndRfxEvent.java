package com.privasia.procurehere.core.utils;

import com.privasia.procurehere.core.entity.*;

import java.util.List;

public class RfxSupplierBqItemAndRfxEvent {
    public List<RftSupplierBqItem> rftSupplierBqItemList;
    public RftEventAward rftEventAward;
    public List<RfaSupplierBqItem> rfaSupplierBqItemList;
    public RfaEventAward rfaEventAward;

    public List<RfpSupplierBqItem> rfpSupplierBqItemList;
    public RfpEventAward rfpEventAward;

    public List<RfqSupplierBqItem> rfqSupplierBqItemList;
    public RfqEventAward rfqEventAward;

    public RfxSupplierBqItemAndRfxEvent() {
    }

    public List<RftSupplierBqItem> getRftSupplierBqItemList() {
        return rftSupplierBqItemList;
    }

    public void setRftSupplierBqItemList(List<RftSupplierBqItem> rftSupplierBqItemList) {
        this.rftSupplierBqItemList = rftSupplierBqItemList;
    }

    public RftEventAward getRftEventAward() {
        return rftEventAward;
    }

    public void setRftEventAward(RftEventAward rftEventAward) {
        this.rftEventAward = rftEventAward;
    }

    public RfaEventAward getRfaEventAward() {
        return rfaEventAward;
    }

    public void setRfaEventAward(RfaEventAward rfaEventAward) {
        this.rfaEventAward = rfaEventAward;
    }

    public List<RfaSupplierBqItem> getRfaSupplierBqItemList() {
        return rfaSupplierBqItemList;
    }

    public void setRfaSupplierBqItemList(List<RfaSupplierBqItem> rfaSupplierBqItemList) {
        this.rfaSupplierBqItemList = rfaSupplierBqItemList;
    }

    public List<RfpSupplierBqItem> getRfpSupplierBqItemList() {
        return rfpSupplierBqItemList;
    }

    public void setRfpSupplierBqItemList(List<RfpSupplierBqItem> rfpSupplierBqItemList) {
        this.rfpSupplierBqItemList = rfpSupplierBqItemList;
    }

    public RfpEventAward getRfpEventAward() {
        return rfpEventAward;
    }

    public void setRfpEventAward(RfpEventAward rfpEventAward) {
        this.rfpEventAward = rfpEventAward;
    }

    public List<RfqSupplierBqItem> getRfqSupplierBqItemList() {
        return rfqSupplierBqItemList;
    }

    public void setRfqSupplierBqItemList(List<RfqSupplierBqItem> rfqSupplierBqItemList) {
        this.rfqSupplierBqItemList = rfqSupplierBqItemList;
    }

    public RfqEventAward getRfqEventAward() {
        return rfqEventAward;
    }

    public void setRfqEventAward(RfqEventAward rfqEventAward) {
        this.rfqEventAward = rfqEventAward;
    }
}
