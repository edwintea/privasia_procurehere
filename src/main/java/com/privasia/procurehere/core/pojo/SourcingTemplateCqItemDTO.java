package com.privasia.procurehere.core.pojo;

import com.privasia.procurehere.core.entity.CqItem;
import com.privasia.procurehere.core.enums.CqType;

import java.util.List;

public class SourcingTemplateCqItemDTO extends CqItem {
    private String itemDescription;
    private String itemName;
    private Integer level;
    private Integer order;
    private CqType cqType;
    private Boolean attachment;
    private Boolean optional;
    private List<CqOptionDTO> cqOptions;
    private SourcingTemplateCqItemDTO parent; // Added parent field

    private SourcingFormTemplatePojo sourcingForm;

    private SourcingTemplateCqDTO cq;

    // Getters and setters for all fields

    public String getItemDescription() {
        return itemDescription;
    }

    public void setItemDescription(String itemDescription) {
        this.itemDescription = itemDescription;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    public CqType getCqType() {
        return cqType;
    }

    public void setCqType(CqType cqType) {
        this.cqType = cqType;
    }

    public Boolean getAttachment() {
        return attachment;
    }

    public void setAttachment(Boolean attachment) {
        this.attachment = attachment;
    }

    public Boolean getOptional() {
        return optional;
    }

    public void setOptional(Boolean optional) {
        this.optional = optional;
    }

    public List<CqOptionDTO> getCqOptions() {
        return cqOptions;
    }

    public void setCqOptions(List<CqOptionDTO> cqOptions) {
        this.cqOptions = cqOptions;
    }

    public SourcingTemplateCqItemDTO getParent() {
        return parent;
    }

    public void setParent(SourcingTemplateCqItemDTO parent) {
        this.parent = parent;
    }

    public SourcingFormTemplatePojo getSourcingForm() {
        return sourcingForm;
    }

    public void setSourcingForm(SourcingFormTemplatePojo sourcingForm) {
        this.sourcingForm = sourcingForm;
    }

    public SourcingTemplateCqDTO getCq() {
        return cq;
    }

    public void setCq(SourcingTemplateCqDTO cq) {
        this.cq = cq;
    }
}
