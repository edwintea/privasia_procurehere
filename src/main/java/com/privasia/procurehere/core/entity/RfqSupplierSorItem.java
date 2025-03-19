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
@Table(name = "PROC_RFQ_EVENT_SOR_SUP_ITEM")
public class RfqSupplierSorItem extends SorItem implements Serializable {

    private static final long serialVersionUID = -5313960510390409671L;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "EVENT_ID", foreignKey = @ForeignKey(name = "FK_RFQ_EVENT_SOR_SUP_ITEM"))
    private RfqEvent event;


    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, optional = true, cascade = {CascadeType.REFRESH})
    @JoinColumn(name = "SUPPLIER_ID", foreignKey = @ForeignKey(name = "FK_RFQ_EVENT_SOR_SUP_ID"))
    private Supplier supplier;


    @ManyToOne(optional = true, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "PARENT_ID", nullable = true, foreignKey = @ForeignKey(name = "FK_RFQ_EVNT_SUP_SOR_PARENT"))
    private RfqSupplierSorItem parent;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "parent")
    @OrderBy("level, order")
    private List<RfqSupplierSorItem> children;

    @ManyToOne(optional = false, cascade = {CascadeType.REFRESH})
    @JoinColumn(name = "SOR_ID", nullable = false, foreignKey = @ForeignKey(name = "FK_RFQ_SUP_SORI_EVT_SOR"))
    private RfqEventSor sor;

    @ManyToOne(optional = false, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "SUPP_SOR_ID", nullable = false, foreignKey = @ForeignKey(name = "FK_RFQ_EV_SUP_SOR"))
    private RfqSupplierSor supplierBq;


    @JsonIgnore
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "supplierSorItem", cascade = CascadeType.ALL)
    private List<RfqSupplierComment> comments;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "SOR_ITEM_ID", foreignKey = @ForeignKey(name = "FK_SUP_RFQ_EVENT_SOR_ITEM_ID"))
    private RfqSorItem sorItem;


    public RfqSupplierSorItem() {

    }

    public RfqSupplierSorItem(RfqSorItem item, Supplier supplier, RfqSupplierSor supplierBq) {
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
            this.children = new ArrayList<RfqSupplierSorItem>();
            for (RfqSorItem child : item.getChildren()) {
                RfqSupplierSorItem childBqItem = new RfqSupplierSorItem(child, supplier, supplierBq);
                childBqItem.setParent(this);
                this.children.add(childBqItem);
            }
        }
    }

    public RfqEvent getEvent() {
        return event;
    }

    public void setEvent(RfqEvent event) {
        this.event = event;
    }

    public Supplier getSupplier() {
        return supplier;
    }

    public void setSupplier(Supplier supplier) {
        this.supplier = supplier;
    }

    public RfqSupplierSorItem getParent() {
        return parent;
    }

    public void setParent(RfqSupplierSorItem parent) {
        this.parent = parent;
    }

    public List<RfqSupplierSorItem> getChildren() {
        return children;
    }

    public void setChildren(List<RfqSupplierSorItem> children) {
        this.children = children;
    }

    public RfqEventSor getSor() {
        return sor;
    }

    public void setSor(RfqEventSor sor) {
        this.sor = sor;
    }

    public RfqSupplierSor getSupplierBq() {
        return supplierBq;
    }

    public void setSupplierBq(RfqSupplierSor supplierBq) {
        this.supplierBq = supplierBq;
    }

    public List<RfqSupplierComment> getComments() {
        return comments;
    }

    public void setComments(List<RfqSupplierComment> comments) {
        this.comments = comments;
    }

    public RfqSorItem getSorItem() {
        return sorItem;
    }

    public void setSorItem(RfqSorItem sorItem) {
        this.sorItem = sorItem;
    }

    public RfqSupplierSorItem createSearchShallowCopy() {
        RfqSupplierSorItem ic = new RfqSupplierSorItem();
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
            RfqSupplierSorItem parent = new RfqSupplierSorItem();
            parent.setId(getParent().getId());
            ic.setParent(parent);
        }

        return ic;
    }

    public RfqSupplierSorItem createShallowCopy() {
        RfqSupplierSorItem ic = new RfqSupplierSorItem();
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

    public RfqSupplierSorItem copyForRfq() {
        RfqSupplierSorItem supBqItem = new RfqSupplierSorItem();
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
