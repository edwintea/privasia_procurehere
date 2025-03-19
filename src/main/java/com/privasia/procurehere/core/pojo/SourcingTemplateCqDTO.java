package com.privasia.procurehere.core.pojo;

import java.util.List;

public class SourcingTemplateCqDTO {
    private String name;
    private Integer cqOrder;
    private String description;
    private String createdDate;
    private String modifiedDate;
    private SourcingFormTemplatePojo sourcingForm;
    private List<SourcingTemplateCqItemDTO> cqItems;

    // Getters and setters for all fields

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getCqOrder() {
        return cqOrder;
    }

    public void setCqOrder(Integer cqOrder) {
        this.cqOrder = cqOrder;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public String getModifiedDate() {
        return modifiedDate;
    }

    public void setModifiedDate(String modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    public SourcingFormTemplatePojo getSourcingForm() {
        return sourcingForm;
    }

    public void setSourcingForm(SourcingFormTemplatePojo sourcingForm) {
        this.sourcingForm = sourcingForm;
    }

    public List<SourcingTemplateCqItemDTO> getCqItems() {
        return cqItems;
    }

    public void setCqItems(List<SourcingTemplateCqItemDTO> cqItems) {
        this.cqItems = cqItems;
    }
}
