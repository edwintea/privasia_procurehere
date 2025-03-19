package com.privasia.procurehere.core.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "PROC_RFP_EVENT_SOR_ITEM")
@Data
public class RfpSorItem extends SorItem implements Serializable {
    private static final long serialVersionUID = -8425589426647163094L;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "EVENT_ID", nullable = true, foreignKey = @ForeignKey(name = "FK_RFP_EVENT_SOR_ITEM"))
    private RfpEvent rfxEvent;


    @ManyToOne(optional = false, cascade = {CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(name = "SOR_ID", nullable = false, foreignKey = @ForeignKey(name = "FK_RFP_EVNT_SORI_SOR"))
    private RfpEventSor sor;

    @ManyToOne(optional = true, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "PARENT_ID", nullable = true, foreignKey = @ForeignKey(name = "FK_RFP_EVNT_SOR_PARENT"))
    private RfpSorItem parent;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "parent")
    @OrderBy("order")
    private List<RfpSorItem> children;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "bqItem", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<RfpSorEvaluationComments> evaluationComments;


    public RfpSorItem createSearchShallowCopy() {
        RfpSorItem ic = new RfpSorItem();
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
            RfpSorItem parent = new RfpSorItem();
            parent.setId(getParent().getId());
            ic.setParent(parent);
        }
        return ic;
    }

    public RfpSorItem createShallowCopy() {
        RfpSorItem ic = new RfpSorItem();
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


    public RfpSorItem copyForRfq(RfpEventSor newBq) {
        RfpSorItem bq = new RfpSorItem();
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
        return "Rfp Sor Item ";
    }
}
