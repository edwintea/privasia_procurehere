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
@Table(name = "PROC_RFI_EVENT_SOR_SUP_ITEM")
public class RfiSupplierSorItem extends SorItem implements Serializable {
    private static final long serialVersionUID = -5313960510390409671L;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "EVENT_ID", foreignKey = @ForeignKey(name = "FK_Rfi_EVENT_SOR_SUP_ITEM"))
    private RfiEvent event;


    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, optional = true, cascade = {CascadeType.REFRESH})
    @JoinColumn(name = "SUPPLIER_ID", foreignKey = @ForeignKey(name = "FK_Rfi_EVENT_SOR_SUP_ID"))
    private Supplier supplier;


    @ManyToOne(optional = true, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "PARENT_ID", nullable = true, foreignKey = @ForeignKey(name = "FK_Rfi_EVNT_SUP_SOR_PARENT"))
    private RfiSupplierSorItem parent;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "parent")
    @OrderBy("level, order")
    private List<RfiSupplierSorItem> children;

    @ManyToOne(optional = false, cascade = {CascadeType.REFRESH})
    @JoinColumn(name = "SOR_ID", nullable = false, foreignKey = @ForeignKey(name = "FK_Rfi_SUP_SORI_EVT_SOR"))
    private RfiEventSor sor;

    @ManyToOne(optional = false, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "SUPP_SOR_ID", nullable = false, foreignKey = @ForeignKey(name = "FK_Rfi_EV_SUP_SOR"))
    private RfiSupplierSor supplierBq;


//    @JsonIgnore
//    @OneToMany(fetch = FetchType.LAZY, mappedBy = "supplierSorItem", cascade = CascadeType.ALL)
//    private List<RfiSupplierComment> comments;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "SOR_ITEM_ID", foreignKey = @ForeignKey(name = "FK_SUP_Rfi_EVENT_SOR_ITEM_ID"))
    private RfiSorItem sorItem;


    public RfiSupplierSorItem() {

    }

    public RfiSupplierSorItem(RfiSorItem item, Supplier supplier, RfiSupplierSor supplierBq) {
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
            this.children = new ArrayList<RfiSupplierSorItem>();
            for (RfiSorItem child : item.getChildren()) {
                RfiSupplierSorItem childBqItem = new RfiSupplierSorItem(child, supplier, supplierBq);
                childBqItem.setParent(this);
                this.children.add(childBqItem);
            }
        }
    }


    public RfiEvent getEvent() {
        return event;
    }

    public void setEvent(RfiEvent event) {
        this.event = event;
    }

    public Supplier getSupplier() {
        return supplier;
    }

    public void setSupplier(Supplier supplier) {
        this.supplier = supplier;
    }

    public RfiSupplierSorItem getParent() {
        return parent;
    }

    public void setParent(RfiSupplierSorItem parent) {
        this.parent = parent;
    }

    public List<RfiSupplierSorItem> getChildren() {
        return children;
    }

    public void setChildren(List<RfiSupplierSorItem> children) {
        this.children = children;
    }

    public RfiEventSor getSor() {
        return sor;
    }

    public void setSor(RfiEventSor sor) {
        this.sor = sor;
    }

    public RfiSupplierSor getSupplierBq() {
        return supplierBq;
    }

    public void setSupplierBq(RfiSupplierSor supplierBq) {
        this.supplierBq = supplierBq;
    }

//    public List<RfiSupplierComment> getComments() {
//        return comments;
//    }
//
//    public void setComments(List<RfiSupplierComment> comments) {
//        this.comments = comments;
//    }

    public RfiSorItem getSorItem() {
        return sorItem;
    }

    public void setSorItem(RfiSorItem sorItem) {
        this.sorItem = sorItem;
    }


    public RfiSupplierSorItem createSearchShallowCopy() {
        RfiSupplierSorItem ic = new RfiSupplierSorItem();
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
            RfiSupplierSorItem parent = new RfiSupplierSorItem();
            parent.setId(getParent().getId());
            ic.setParent(parent);
        }

        return ic;
    }

    public RfiSupplierSorItem createShallowCopy() {
        RfiSupplierSorItem ic = new RfiSupplierSorItem();
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

    public RfiSupplierSorItem copyForRfi() {
        RfiSupplierSorItem supBqItem = new RfiSupplierSorItem();
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
