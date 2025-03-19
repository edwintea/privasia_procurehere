package com.privasia.procurehere.core.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "PROC_RFQ_EVENT_SOR_ITEM")
@Data
public class RfqSorItem extends SorItem implements Serializable {
    private static final long serialVersionUID = -8425585426647163094L;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "EVENT_ID", nullable = true, foreignKey = @ForeignKey(name = "FK_RFQ_EVENT_SOR_ITEM"))
    private RfqEvent rfxEvent;


    @ManyToOne(optional = false, cascade = {CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(name = "SOR_ID", nullable = false, foreignKey = @ForeignKey(name = "FK_RFQ_EVNT_SORI_SOR"))
    private RfqEventSor sor;

    @ManyToOne(optional = true, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "PARENT_ID", nullable = true, foreignKey = @ForeignKey(name = "FK_RFQ_EVNT_SOR_PARENT"))
    private RfqSorItem parent;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "parent")
    @OrderBy("order")
    private List<RfqSorItem> children;


    @OneToMany(fetch = FetchType.LAZY, mappedBy = "bqItem", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<RfqSorEvaluationComments> evaluationComments;


    public RfqSorItem createSearchShallowCopy() {
        RfqSorItem ic = new RfqSorItem();
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
            RfqSorItem parent = new RfqSorItem();
            parent.setId(getParent().getId());
            ic.setParent(parent);
        }
        return ic;
    }

    public RfqSorItem createShallowCopy() {
        RfqSorItem ic = new RfqSorItem();
        ic.setItemDescription(getItemDescription());
        ic.setUom(getUom() != null ? getUom().createShallowCopy() : null);
        ic.setItemName(getItemName());
        ic.setOrder(getOrder());
        ic.setId(getId());
        ic.setField1(getField1());
        ic.setSor(getSor());

        return ic;
    }


    public RfqSorItem copyForRfq(RfqEventSor newBq) {
        RfqSorItem bq = new RfqSorItem();
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
        return "Rfq Sor Item ";
    }
}
