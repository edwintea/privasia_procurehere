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
@Table(name = "PROC_Rfi_SUPPLIER_SOR")
public class RfiSupplierSor extends Sor implements Serializable {
    private static final long serialVersionUID = 5867513085042725779L;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "EVENT_ID", foreignKey = @ForeignKey(name = "FK_SUP_Rfi_EVENT_SOR"))
    private RfiEvent event;


    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "SOR_ID", foreignKey = @ForeignKey(name = "FK_SUP_Rfi_EVENT_SOR_ID"))
    private RfiEventSor bq;


    @JsonIgnore
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "supplierBq")
    @OrderBy("level, order")
    private List<RfiSupplierSorItem> supplierSorItems;


    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, optional = true, cascade = {CascadeType.REFRESH})
    @JoinColumn(name = "SUPPLIER_ID", foreignKey = @ForeignKey(name = "FK_Rfi_SOR_SUP_ID"))
    private Supplier supplier;


    @Column(name = "SUPPLIER_REMARK", length = 3000, nullable = true)
    private String remark;


    @Enumerated(EnumType.STRING)
    @Column(name = "SUPPLIER_SOR_STATUS", nullable = true)
    private SupplierBqStatus supplierSorStatus = SupplierBqStatus.PENDING;


    public RfiEvent getEvent() {
        return event;
    }

    public void setEvent(RfiEvent event) {
        this.event = event;
    }

    public RfiEventSor getBq() {
        return bq;
    }

    public void setBq(RfiEventSor bq) {
        this.bq = bq;
    }

    public List<RfiSupplierSorItem> getSupplierSorItems() {
        return supplierSorItems;
    }

    public void setSupplierSorItems(List<RfiSupplierSorItem> supplierSorItems) {
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


    public RfiSupplierSor() {

    }
    public RfiSupplierSor(SupplierBqStatus status, String bqId, String id) {
        this.supplierSorStatus = status;
        this.setId(id);
        this.bq = new RfiEventSor();
        this.bq.setId(bqId);
    }

    public RfiSupplierSor(RfiEventSor bq) {
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

    public RfiSupplierSor createForRfi(RfiEvent newEvent, RfiEventSor newBq) {
        RfiSupplierSor bq = new RfiSupplierSor();
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
        List<RfiSupplierSorItem> bqItemsCopy = new ArrayList<RfiSupplierSorItem>();
        for (RfiSupplierSorItem rfpSupplierBqItem : this.getSupplierSorItems()) {
            RfiSupplierSorItem item = rfpSupplierBqItem.copyForRfi();
            item.setSupplierBq(bq);
            item.setSor(newBq);
            RfiSorItem newBqItem = null;
            for (RfiSorItem rbi : newBq.getSorItems()) {
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

    public RfiSupplierSor(String name) {
        this.setName(name);
    }
}


