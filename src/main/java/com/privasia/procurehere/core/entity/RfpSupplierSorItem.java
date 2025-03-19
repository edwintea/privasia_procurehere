package com.privasia.procurehere.core.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.privasia.procurehere.core.utils.CollectionUtil;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "PROC_RFP_EVENT_SOR_SUP_ITEM")
public class RfpSupplierSorItem extends SorItem implements Serializable {
    private static final long serialVersionUID = -5313960510390409671L;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "EVENT_ID", foreignKey = @ForeignKey(name = "FK_RFP_EVENT_SOR_SUP_ITEM"))
    private RfpEvent event;


    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, optional = true, cascade = {CascadeType.REFRESH})
    @JoinColumn(name = "SUPPLIER_ID", foreignKey = @ForeignKey(name = "FK_Rfp_EVENT_SOR_SUP_ID"))
    private Supplier supplier;


    @ManyToOne(optional = true, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "PARENT_ID", nullable = true, foreignKey = @ForeignKey(name = "FK_Rfp_EVNT_SUP_SOR_PARENT"))
    private RfpSupplierSorItem parent;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "parent")
    @OrderBy("level, order")
    private List<RfpSupplierSorItem> children;

    @ManyToOne(optional = false, cascade = {CascadeType.REFRESH})
    @JoinColumn(name = "SOR_ID", nullable = false, foreignKey = @ForeignKey(name = "FK_Rfp_SUP_SORI_EVT_SOR"))
    private RfpEventSor sor;

    @ManyToOne(optional = false, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "SUPP_SOR_ID", nullable = false, foreignKey = @ForeignKey(name = "FK_Rfp_EV_SUP_SOR"))
    private RfpSupplierSor supplierBq;


    @JsonIgnore
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "supplierSorItem", cascade = CascadeType.ALL)
    private List<RfpSupplierComment> comments;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "SOR_ITEM_ID", foreignKey = @ForeignKey(name = "FK_SUP_Rfp_EVENT_SOR_ITEM_ID"))
    private RfpSorItem sorItem;


    public RfpSupplierSorItem() {

    }

    public RfpSupplierSorItem(RfpSorItem item, Supplier supplier, RfpSupplierSor supplierBq) {
        this.setSorItem(item);
        this.setSupplierBq(supplierBq);
        this.setSor(item.getSor());
        this.event = item.getRfxEvent();
        this.setSupplier(supplier);
        this.setField1(item.getField1());

        this.setItemDescription(item.getItemDescription());
        this.setItemName(item.getItemName());
        this.setLevel(item.getLevel());
        this.setOrder(item.getOrder());
        this.setUom(item.getUom());

        if (CollectionUtil.isNotEmpty(item.getChildren())) {
            this.children = new ArrayList<RfpSupplierSorItem>();
            for (RfpSorItem child : item.getChildren()) {
                RfpSupplierSorItem childBqItem = new RfpSupplierSorItem(child, supplier, supplierBq);
                childBqItem.setParent(this);
                this.children.add(childBqItem);
            }
        }
    }


    public RfpEvent getEvent() {
        return event;
    }

    public void setEvent(RfpEvent event) {
        this.event = event;
    }

    public Supplier getSupplier() {
        return supplier;
    }

    public void setSupplier(Supplier supplier) {
        this.supplier = supplier;
    }

    public RfpSupplierSorItem getParent() {
        return parent;
    }

    public void setParent(RfpSupplierSorItem parent) {
        this.parent = parent;
    }

    public List<RfpSupplierSorItem> getChildren() {
        return children;
    }

    public void setChildren(List<RfpSupplierSorItem> children) {
        this.children = children;
    }

    public RfpEventSor getSor() {
        return sor;
    }

    public void setSor(RfpEventSor sor) {
        this.sor = sor;
    }

    public RfpSupplierSor getSupplierBq() {
        return supplierBq;
    }

    public void setSupplierBq(RfpSupplierSor supplierBq) {
        this.supplierBq = supplierBq;
    }

    public List<RfpSupplierComment> getComments() {
        return comments;
    }

    public void setComments(List<RfpSupplierComment> comments) {
        this.comments = comments;
    }

    public RfpSorItem getSorItem() {
        return sorItem;
    }

    public void setSorItem(RfpSorItem sorItem) {
        this.sorItem = sorItem;
    }


    public RfpSupplierSorItem createSearchShallowCopy() {
        RfpSupplierSorItem ic = new RfpSupplierSorItem();
        ic.setItemDescription(getItemDescription());
        if (getUom() != null) {
            ic.setUom(getUom().createShallowCopy());
        }
        ic.setItemName(getItemName());
        //ic.setUom(getUom());
        ic.setLevel(getLevel());
        ic.setOrder(getOrder());
        ic.setTotalAmount(getTotalAmount());
        ic.setId(getId());
        ic.setField1(getField1());

        ic.setSorItem(getSorItem());
        ic.setSupplierBq(getSupplierBq());
        if (getParent() != null) {
            RfpSupplierSorItem parent = new RfpSupplierSorItem();
            parent.setId(getParent().getId());
            ic.setParent(parent);
        }

        return ic;
    }

    public RfpSupplierSorItem createShallowCopy() {
        RfpSupplierSorItem ic = new RfpSupplierSorItem();
        ic.setItemDescription(getItemDescription());
        if (getUom() != null) {
            ic.setUom(getUom().createShallowCopy());
        }
        ic.setItemName(getItemName());
        ic.setLevel(getLevel());
        ic.setOrder(getOrder());
        ic.setTotalAmount(getTotalAmount());
        ic.setId(getId());
        ic.setField1(getField1());

        ic.setEvent(getEvent());

        return ic;
    }

    public RfpSupplierSorItem copyForRfp() {
        RfpSupplierSorItem supBqItem = new RfpSupplierSorItem();
        supBqItem.setItemDescription(getItemDescription());
        if (getUom() != null) {
            supBqItem.setUom(getUom());
        }
        supBqItem.setItemName(getItemName());
        supBqItem.setLevel(getLevel());
        supBqItem.setOrder(getOrder());
        supBqItem.setTotalAmount(getTotalAmount());
        supBqItem.setField1(getField1());

        return supBqItem;
    }
}
