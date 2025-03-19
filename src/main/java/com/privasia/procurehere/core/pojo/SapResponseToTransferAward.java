package com.privasia.procurehere.core.pojo;

import com.privasia.procurehere.core.enums.RfxTypes;
import com.privasia.procurehere.core.enums.SapToPrResponse;

public class SapResponseToTransferAward {
    private SapToPrResponse type;
    private String message;
    private String prNo;
    private RfxTypes eventType;
    private String eventId;

    public SapResponseToTransferAward() {

    }

    public SapResponseToTransferAward(SapToPrResponse type, String message) {
        this.type = type;
        this.message = message;
    }

    public SapToPrResponse getType() {
        return type;
    }

    public void setType(SapToPrResponse type) {
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getPrNo() {
        return prNo;
    }

    public void setPrNo(String prNo) {
        this.prNo = prNo;
    }

    public RfxTypes getEventType() {
        return eventType;
    }

    public void setEventType(RfxTypes eventType) {
        this.eventType = eventType;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    @Override
    public String toString() {
        return "SapResponseToTransferAward{" +
                "type=" + type +
                ", message='" + message + '\'' +
                ", prNo='" + prNo + '\'' +
                ", eventType=" + eventType +
                ", eventId='" + eventId + '\'' +
                '}';
    }
}
