package com.privasia.procurehere.core.pojo;

public class CqOptionDTO {
    private Integer order;
    private Integer scoring;
    private String value;
    private SourcingTemplateCqItemDTO formCqItem; // Change to DTO object
    // Getters and setters for all fields

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    public Integer getScoring() {
        return scoring;
    }

    public void setScoring(Integer scoring) {
        this.scoring = scoring;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public SourcingTemplateCqItemDTO getFormCqItem() {
        return formCqItem;
    }

    public void setFormCqItem(SourcingTemplateCqItemDTO formCqItem) {
        this.formCqItem = formCqItem;
    }
}
