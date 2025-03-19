package com.privasia.procurehere.core.pojo;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.privasia.procurehere.core.enums.RfxTypes;

import java.io.Serializable;
import java.util.List;

public class RfxErpPojoAward implements Serializable {

    private static final long serialVersionUID = 8903208397781717557L;
    @JsonInclude(JsonInclude.Include.ALWAYS)
    private String prType;
    private String HeaderText;

    private String eventId;

    private RfxTypes eventType;

    private List<ItemListPojo> items;

    public RfxErpPojoAward(String prType, String headerText, List<ItemListPojo> items) {
        this.prType = prType;
        HeaderText = headerText;
        this.items = items;
    }

    public RfxErpPojoAward() {

    }

    public String getPrType() {
        return prType;
    }

    public void setPrType(String prType) {
        this.prType = prType;
    }

    public String getHeaderText() {
        return HeaderText;
    }

    public void setHeaderText(String headerText) {
        HeaderText = headerText;
    }

    public List<ItemListPojo> getItems() {
        return items;
    }

    public void setItems(List<ItemListPojo> items) {
        this.items = items;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public RfxTypes getEventType() {
        return eventType;
    }

    public void setEventType(RfxTypes eventType) {
        this.eventType = eventType;
    }
}
