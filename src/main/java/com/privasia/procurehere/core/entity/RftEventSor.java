package com.privasia.procurehere.core.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.privasia.procurehere.core.enums.BqUserTypes;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.StringUtils;

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
@Table(name = "PROC_RFT_EVENT_SOR")
public class RftEventSor extends Sor implements Serializable {

    private static final long serialVersionUID = -8425585436647163093L;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "EVENT_ID", foreignKey = @ForeignKey(name = "FK_RFT_EVENT_SOR"))
    private RftEvent rfxEvent;


    @JsonIgnore
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "sor", orphanRemoval = true)
    @OrderBy("level, order")
    private List<RftSorItem> sorItems;

    public RftEventSor createMobileEnvelopShallowCopy() {
        RftEventSor bq = new RftEventSor();
        bq.setId(getId());
        bq.setName(getName());
        bq.setSorOrder(getSorOrder());
        bq.setDescription(getDescription());
        bq.setField1ToShowSupplier(null);
        bq.setField1Required(null);
        return bq;
    }

    public RftEventSor copyForRfq(boolean isSupplierInvited) {
        RftEventSor newBq = new RftEventSor();
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
            newBq.setSorItems(new ArrayList<RftSorItem>());
            for (RftSorItem bqItem : getSorItems()) {
                RftSorItem newBqItem = bqItem.copyForRfq(newBq);
                if (isSupplierInvited)
                    newBqItem.setId(bqItem.getId()); // take it for now. We will erase it later
                newBq.getSorItems().add(newBqItem);
            }
        }
        return newBq;
    }

    public RftEventSor() {

    }

    public RftEventSor(SourcingFormRequestSor bq) {
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


    public RftEvent getRfxEvent() {
        return rfxEvent;
    }

    public void setRfxEvent(RftEvent rfxEvent) {
        this.rfxEvent = rfxEvent;
    }

    public List<RftSorItem> getSorItems() {
        return sorItems;
    }

    public void setSorItems(List<RftSorItem> sorItems) {
        this.sorItems = sorItems;
    }

    public String toLogString() {
        return "RftEventScheduleOfRate [ " + super.toLogString() + "]";
    }
}
