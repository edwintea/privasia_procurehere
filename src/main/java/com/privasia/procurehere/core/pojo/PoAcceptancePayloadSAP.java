package com.privasia.procurehere.core.pojo;


import com.fasterxml.jackson.annotation.JsonProperty;

public class PoAcceptancePayloadSAP {
    private static final long serialVersionUID = 8457828183553043053L;

    @JsonProperty("REFERENCE_NUMBER")
    private String REFERENCE_NUMBER;

    @JsonProperty("INDICATOR")
    private String INDICATOR;

    @JsonProperty("SUPPLIER_REMARK")
    private String SUPPLIER_REMARK;

    public String getREFERENCE_NUMBER() {
        return REFERENCE_NUMBER;
    }

    public void setREFERENCE_NUMBER(String REFERENCE_NUMBER) {
        this.REFERENCE_NUMBER = REFERENCE_NUMBER;
    }

    public String getINDICATOR() {
        return INDICATOR;
    }

    public void setINDICATOR(String INDICATOR) {
        this.INDICATOR = INDICATOR;
    }

    public String getSUPPLIER_REMARK() {
        return SUPPLIER_REMARK;
    }

    public void setSUPPLIER_REMARK(String SUPPLIER_REMARK) {
        this.SUPPLIER_REMARK = SUPPLIER_REMARK;
    }

    @Override
    public String toString() {
        return "PoAcceptancePayloadSAP{" +
                "REFERENCE_NUMBER='" + REFERENCE_NUMBER + '\'' +
                ", INDICATOR='" + INDICATOR + '\'' +
                ", SUPPLIER_REMARK='" + SUPPLIER_REMARK + '\'' +
                '}';
    }
}
