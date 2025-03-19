package com.privasia.procurehere.core.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.privasia.procurehere.core.enums.BqUserTypes;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.StringUtils;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "PROC_RFP_EVENT_SOR")
public class RfpEventSor extends Sor implements Serializable {
    private static final long serialVersionUID = -8425585426647163095L;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "EVENT_ID", foreignKey = @ForeignKey(name = "FK_RFP_EVENT_SOR"))
    private RfpEvent rfxEvent;


    @JsonIgnore
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "sor", orphanRemoval = true)
    @OrderBy("level, order")
    private List<RfpSorItem> sorItems;

    public RfpEventSor createMobileEnvelopShallowCopy() {
        RfpEventSor bq = new RfpEventSor();
        bq.setId(getId());
        bq.setName(getName());
        bq.setSorOrder(getSorOrder());
        bq.setDescription(getDescription());
        bq.setField1ToShowSupplier(null);
        bq.setField1Required(null);
        return bq;
    }

    public RfpEventSor copyForRfq(boolean isSupplierInvited) {
        RfpEventSor newBq = new RfpEventSor();
        if (isSupplierInvited)
            newBq.setId(this.getId()); // take it for now. We will erase it later
        newBq.setName(getName());
        newBq.setSorOrder(getSorOrder());
        newBq.setDescription(getDescription());
        newBq.setCreatedDate(getCreatedDate());
        newBq.setModifiedDate(getModifiedDate());
        newBq.setField1Label(getField1Label());
        newBq.setField1FilledBy(getField1FilledBy());
        newBq.setField1ToShowSupplier(getField1ToShowSupplier());
        newBq.setField1Required(getField1Required());
        if (CollectionUtil.isNotEmpty(getSorItems())) {
            newBq.setSorItems(new ArrayList<RfpSorItem>());
            for (RfpSorItem bqItem : getSorItems()) {
                RfpSorItem newBqItem = bqItem.copyForRfq(newBq);
                if (isSupplierInvited)
                    newBqItem.setId(bqItem.getId()); // take it for now. We will erase it later
                newBq.getSorItems().add(newBqItem);
            }
        }
        return newBq;
    }


    public RfpEventSor() {

    }

    public RfpEventSor(SourcingFormRequestSor bq) {
        this.setName(bq.getName());
        this.setSorOrder(1);
        this.setDescription(bq.getDescription());
        this.setCreatedDate(bq.getCreatedDate());
        this.setModifiedDate(bq.getModifiedDate());
        this.setField1Label(bq.getField1Label());
        if (StringUtils.checkString(bq.getField1Label()).length() > 0) {
            this.setField1FilledBy(BqUserTypes.BUYER);
        }
    }


    public RfpEvent getRfxEvent() {
        return rfxEvent;
    }

    public void setRfxEvent(RfpEvent rfxEvent) {
        this.rfxEvent = rfxEvent;
    }

    public List<RfpSorItem> getSorItems() {
        return sorItems;
    }

    public void setSorItems(List<RfpSorItem> sorItems) {
        this.sorItems = sorItems;
    }

    public String toLogString() {
        return "RfqEventScheduleOfRate [ " + super.toLogString() + "]";
    }
}
