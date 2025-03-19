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
@Table(name = "PROC_RFT_EVENT_SOR_SUP_ITEM")
public class RftSupplierSorItem extends SorItem implements Serializable {
    private static final long serialVersionUID = -5313960510390409671L;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "EVENT_ID", foreignKey = @ForeignKey(name = "FK_Rft_EVENT_SOR_SUP_ITEM"))
    private RftEvent event;


    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, optional = true, cascade = {CascadeType.REFRESH})
    @JoinColumn(name = "SUPPLIER_ID", foreignKey = @ForeignKey(name = "FK_Rft_EVENT_SOR_SUP_ID"))
    private Supplier supplier;


    @ManyToOne(optional = true, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "PARENT_ID", nullable = true, foreignKey = @ForeignKey(name = "FK_Rft_EVNT_SUP_SOR_PARENT"))
    private RftSupplierSorItem parent;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "parent")
    @OrderBy("level, order")
    private List<RftSupplierSorItem> children;

    @ManyToOne(optional = false, cascade = {CascadeType.REFRESH})
    @JoinColumn(name = "SOR_ID", nullable = false, foreignKey = @ForeignKey(name = "FK_Rft_SUP_SORI_EVT_SOR"))
    private RftEventSor sor;

    @ManyToOne(optional = false, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "SUPP_SOR_ID", nullable = false, foreignKey = @ForeignKey(name = "FK_Rft_EV_SUP_SOR"))
    private RftSupplierSor supplierBq;


    @JsonIgnore
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "supplierSorItem", cascade = CascadeType.ALL)
    private List<RftSupplierComment> comments;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "SOR_ITEM_ID", foreignKey = @ForeignKey(name = "FK_SUP_Rft_EVENT_SOR_ITEM_ID"))
    private RftSorItem sorItem;


    public RftSupplierSorItem() {

    }

    public RftSupplierSorItem(RftSorItem item, Supplier supplier, RftSupplierSor supplierBq) {
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
            this.children = new ArrayList<RftSupplierSorItem>();
            for (RftSorItem child : item.getChildren()) {
                RftSupplierSorItem childBqItem = new RftSupplierSorItem(child, supplier, supplierBq);
                childBqItem.setParent(this);
                this.children.add(childBqItem);
            }
        }
    }


    public RftEvent getEvent() {
        return event;
    }

    public void setEvent(RftEvent event) {
        this.event = event;
    }

    public Supplier getSupplier() {
        return supplier;
    }

    public void setSupplier(Supplier supplier) {
        this.supplier = supplier;
    }

    public RftSupplierSorItem getParent() {
        return parent;
    }

    public void setParent(RftSupplierSorItem parent) {
        this.parent = parent;
    }

    public List<RftSupplierSorItem> getChildren() {
        return children;
    }

    public void setChildren(List<RftSupplierSorItem> children) {
        this.children = children;
    }

    public RftEventSor getSor() {
        return sor;
    }

    public void setSor(RftEventSor sor) {
        this.sor = sor;
    }

    public RftSupplierSor getSupplierBq() {
        return supplierBq;
    }

    public void setSupplierBq(RftSupplierSor supplierBq) {
        this.supplierBq = supplierBq;
    }

    public List<RftSupplierComment> getComments() {
        return comments;
    }

    public void setComments(List<RftSupplierComment> comments) {
        this.comments = comments;
    }

    public RftSorItem getSorItem() {
        return sorItem;
    }

    public void setSorItem(RftSorItem sorItem) {
        this.sorItem = sorItem;
    }


    public RftSupplierSorItem createSearchShallowCopy() {
        RftSupplierSorItem ic = new RftSupplierSorItem();
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
            RftSupplierSorItem parent = new RftSupplierSorItem();
            parent.setId(getParent().getId());
            ic.setParent(parent);
        }

        return ic;
    }

    public RftSupplierSorItem createShallowCopy() {
        RftSupplierSorItem ic = new RftSupplierSorItem();
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

    public RftSupplierSorItem copyForRft() {
        RftSupplierSorItem supBqItem = new RftSupplierSorItem();
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
