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
@Table(name = "PROC_RFA_EVENT_SOR_SUP_ITEM")
public class RfaSupplierSorItem extends SorItem implements Serializable {
    private static final long serialVersionUID = -5313960510390409671L;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "EVENT_ID", foreignKey = @ForeignKey(name = "FK_Rfa_EVENT_SOR_SUP_ITEM"))
    private RfaEvent event;


    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, optional = true, cascade = {CascadeType.REFRESH})
    @JoinColumn(name = "SUPPLIER_ID", foreignKey = @ForeignKey(name = "FK_Rfa_EVENT_SOR_SUP_ID"))
    private Supplier supplier;


    @ManyToOne(optional = true, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "PARENT_ID", nullable = true, foreignKey = @ForeignKey(name = "FK_Rfa_EVNT_SUP_SOR_PARENT"))
    private RfaSupplierSorItem parent;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "parent")
    @OrderBy("level, order")
    private List<RfaSupplierSorItem> children;

    @ManyToOne(optional = false, cascade = {CascadeType.REFRESH})
    @JoinColumn(name = "SOR_ID", nullable = false, foreignKey = @ForeignKey(name = "FK_Rfa_SUP_SORI_EVT_SOR"))
    private RfaEventSor sor;

    @ManyToOne(optional = false, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "SUPP_SOR_ID", nullable = false, foreignKey = @ForeignKey(name = "FK_Rfa_EV_SUP_SOR"))
    private RfaSupplierSor supplierBq;


    @JsonIgnore
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "supplierSorItem", cascade = CascadeType.ALL)
    private List<RfaSupplierComment> comments;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "SOR_ITEM_ID", foreignKey = @ForeignKey(name = "FK_SUP_Rfa_EVENT_SOR_ITEM_ID"))
    private RfaSorItem sorItem;


    public RfaSupplierSorItem() {

    }

    public RfaSupplierSorItem(RfaSorItem item, Supplier supplier, RfaSupplierSor supplierBq) {
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
            this.children = new ArrayList<RfaSupplierSorItem>();
            for (RfaSorItem child : item.getChildren()) {
                RfaSupplierSorItem childBqItem = new RfaSupplierSorItem(child, supplier, supplierBq);
                childBqItem.setParent(this);
                this.children.add(childBqItem);
            }
        }
    }


    public RfaEvent getEvent() {
        return event;
    }

    public void setEvent(RfaEvent event) {
        this.event = event;
    }

    public Supplier getSupplier() {
        return supplier;
    }

    public void setSupplier(Supplier supplier) {
        this.supplier = supplier;
    }

    public RfaSupplierSorItem getParent() {
        return parent;
    }

    public void setParent(RfaSupplierSorItem parent) {
        this.parent = parent;
    }

    public List<RfaSupplierSorItem> getChildren() {
        return children;
    }

    public void setChildren(List<RfaSupplierSorItem> children) {
        this.children = children;
    }

    public RfaEventSor getSor() {
        return sor;
    }

    public void setSor(RfaEventSor sor) {
        this.sor = sor;
    }

    public RfaSupplierSor getSupplierBq() {
        return supplierBq;
    }

    public void setSupplierBq(RfaSupplierSor supplierBq) {
        this.supplierBq = supplierBq;
    }

    public List<RfaSupplierComment> getComments() {
        return comments;
    }

    public void setComments(List<RfaSupplierComment> comments) {
        this.comments = comments;
    }

    public RfaSorItem getSorItem() {
        return sorItem;
    }

    public void setSorItem(RfaSorItem sorItem) {
        this.sorItem = sorItem;
    }



    public RfaSupplierSorItem createSearchShallowCopy() {
        RfaSupplierSorItem ic = new RfaSupplierSorItem();
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
            RfaSupplierSorItem parent = new RfaSupplierSorItem();
            parent.setId(getParent().getId());
            ic.setParent(parent);
        }

        return ic;
    }

    public RfaSupplierSorItem createShallowCopy() {
        RfaSupplierSorItem ic = new RfaSupplierSorItem();
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

    public RfaSupplierSorItem copyForRfa() {
        RfaSupplierSorItem supBqItem = new RfaSupplierSorItem();
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
