package com.privasia.procurehere.core.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.privasia.procurehere.core.enums.SupplierBqStatus;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "PROC_Rfa_SUPPLIER_SOR")
public class RfaSupplierSor extends Sor implements Serializable {
    private static final long serialVersionUID = 5867513085042725779L;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "EVENT_ID", foreignKey = @ForeignKey(name = "FK_SUP_Rfa_EVENT_SOR"))
    private RfaEvent event;


    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "SOR_ID", foreignKey = @ForeignKey(name = "FK_SUP_Rfa_EVENT_SOR_ID"))
    private RfaEventSor bq;


    @JsonIgnore
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "supplierBq")
    @OrderBy("level, order")
    private List<RfaSupplierSorItem> supplierSorItems;


    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, optional = true, cascade = {CascadeType.REFRESH})
    @JoinColumn(name = "SUPPLIER_ID", foreignKey = @ForeignKey(name = "FK_Rfa_SOR_SUP_ID"))
    private Supplier supplier;


    @Column(name = "SUPPLIER_REMARK", length = 3000, nullable = true)
    private String remark;


    @Enumerated(EnumType.STRING)
    @Column(name = "SUPPLIER_SOR_STATUS", nullable = true)
    private SupplierBqStatus supplierSorStatus = SupplierBqStatus.PENDING;


    public RfaEvent getEvent() {
        return event;
    }

    public void setEvent(RfaEvent event) {
        this.event = event;
    }

    public RfaEventSor getBq() {
        return bq;
    }

    public void setBq(RfaEventSor bq) {
        this.bq = bq;
    }

    public List<RfaSupplierSorItem> getSupplierSorItems() {
        return supplierSorItems;
    }

    public void setSupplierSorItems(List<RfaSupplierSorItem> supplierSorItems) {
        this.supplierSorItems = supplierSorItems;
    }

    public Supplier getSupplier() {
        return supplier;
    }

    public void setSupplier(Supplier supplier) {
        this.supplier = supplier;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public SupplierBqStatus getSupplierSorStatus() {
        return supplierSorStatus;
    }

    public void setSupplierSorStatus(SupplierBqStatus supplierSorStatus) {
        this.supplierSorStatus = supplierSorStatus;
    }


    public RfaSupplierSor() {

    }
    public RfaSupplierSor(SupplierBqStatus status, String bqId, String id) {
        this.supplierSorStatus = status;
        this.setId(id);
        this.bq = new RfaEventSor();
        this.bq.setId(bqId);
    }

    public RfaSupplierSor(RfaEventSor bq) {
        // this.setAdditionalTax(getAdditionalTax());
        this.setField1FilledBy(bq.getField1FilledBy());
        this.setField1Label(bq.getField1Label());
        this.setField1Required(bq.getField1Required());
        this.setField1ToShowSupplier(bq.getField1ToShowSupplier());

        this.setName(bq.getName());
        this.setModifiedDate(bq.getModifiedDate());
        this.setCreatedDate(bq.getCreatedDate());
        this.setDescription(bq.getDescription());
        this.setBq(bq);
    }

    public RfaSupplierSor createForRfp(RfaEvent newEvent, RfaEventSor newBq) {
        RfaSupplierSor bq = new RfaSupplierSor();
        bq.setCreatedDate(new Date());
        bq.setDescription(this.getDescription());
        bq.setRemark(this.getRemark());
        bq.setName(this.getName());
        bq.setSorOrder(this.getSorOrder());
        bq.setHeaderCount(this.getHeaderCount());

        bq.setField1FilledBy(this.getField1FilledBy());
        bq.setField1Label(this.getField1Label());
        bq.setField1Required(this.getField1Required());
        bq.setField1ToShowSupplier(this.getField1ToShowSupplier());
        bq.setField1FilledBy(this.getField1FilledBy());
        bq.setBq(newBq);
        List<RfaSupplierSorItem> bqItemsCopy = new ArrayList<RfaSupplierSorItem>();
        for (RfaSupplierSorItem rfpSupplierBqItem : this.getSupplierSorItems()) {
            RfaSupplierSorItem item = rfpSupplierBqItem.copyForRfa();
            item.setSupplierBq(bq);
            item.setSor(newBq);
            RfaSorItem newBqItem = null;
            for (RfaSorItem rbi : newBq.getSorItems()) {
                if (rbi.getId().equals(rfpSupplierBqItem.getSorItem().getId())) {
                    newBqItem = rbi;
                    break;
                }
            }
            item.setSorItem(newBqItem);
            item.setEvent(newEvent);
            item.setSupplier(this.supplier);
            bqItemsCopy.add(item);
        }
        bq.setSupplierSorItems(bqItemsCopy);

        bq.setEvent(newEvent);
        bq.setBq(newBq);
        bq.setSupplier(this.supplier);
        return bq;
    }

    public RfaSupplierSor(String name) {
        this.setName(name);
    }
}

