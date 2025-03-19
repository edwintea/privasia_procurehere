package com.privasia.procurehere.core.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

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
import java.util.List;

@Entity
@Table(name = "PROC_RFI_EVENT_SOR_ITEM")
@Data
public class RfiSorItem extends SorItem implements Serializable {
    private static final long serialVersionUID = -8425585426947163095L;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "EVENT_ID", nullable = true, foreignKey = @ForeignKey(name = "FK_RFI_EVENT_SOR_ITEM"))
    private RfiEvent rfxEvent;


    @ManyToOne(optional = false, cascade = {CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(name = "SOR_ID", nullable = false, foreignKey = @ForeignKey(name = "FK_RFI_EVNT_SORI_SOR"))
    private RfiEventSor sor;

    @ManyToOne(optional = true, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "PARENT_ID", nullable = true, foreignKey = @ForeignKey(name = "FK_RFI_EVNT_SOR_PARENT"))
    private RfiSorItem parent;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "parent")
    @OrderBy("order")
    private List<RfiSorItem> children;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "bqItem", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<RfiSorEvaluationComments> evaluationComments;

    public RfiSorItem createSearchShallowCopy() {
        RfiSorItem ic = new RfiSorItem();
        ic.setItemDescription(getItemDescription());
        if (getUom() != null) {
            ic.setUom(getUom().createShallowCopy());
        }
        ic.setItemName(getItemName());
        ic.setLevel(getLevel());
        ic.setOrder(getOrder());
        ic.setId(getId());
        ic.setField1(getField1());
        ic.setSor(getSor());
        if (getParent() != null) {
            RfiSorItem parent = new RfiSorItem();
            parent.setId(getParent().getId());
            ic.setParent(parent);
        }
        return ic;
    }

    public RfiSorItem createShallowCopy() {
        RfiSorItem ic = new RfiSorItem();
        ic.setItemDescription(getItemDescription());
        ic.setUom(getUom() != null ? getUom().createShallowCopy() : null);
        ic.setItemName(getItemName());
        ic.setLevel(getLevel());
        ic.setOrder(getOrder());
        ic.setId(getId());
        ic.setField1(getField1());
        ic.setSor(getSor());

        return ic;
    }


    public RfiSorItem copyForRfq(RfiEventSor newBq) {
        RfiSorItem bq = new RfiSorItem();
        bq.setItemName(getItemName());
        bq.setLevel(getLevel());
        bq.setOrder(getOrder());
        bq.setSor(newBq);
        bq.setUom(getUom());
        bq.setItemDescription(getItemDescription());
        bq.setField1(getField1());
        bq.setTotalAmount(getTotalAmount());
        return bq;
    }

    @Override
    public String toString() {
        return "Rfi Sor Item ";
    }
}
