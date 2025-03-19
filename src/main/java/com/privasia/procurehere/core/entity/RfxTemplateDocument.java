package com.privasia.procurehere.core.entity;
import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.Type;

@Entity
@Table(name = "PROC_RFX_TEMPLATE_DOCUMENTS")
public class RfxTemplateDocument extends EventDocument implements Serializable {

    private static final long serialVersionUID = 8117893729848219882L;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "TEMPLATE_ID", foreignKey = @ForeignKey(name = "FK_RFX_TEMPLATE_DOC"))
    private RfxTemplate rfxTemplate;

    @Column(name = "IS_INTERNAL ")
    @Type(type = "org.hibernate.type.NumericBooleanType")
    private Boolean internal = Boolean.FALSE;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DOC_UPLOAD_BY", nullable = true, foreignKey = @ForeignKey(name = "FK_RFS_TEMPLATE_DOC_UPLD_BY"))
    private User uploadBy;

    @Transient
    private String uploadByName;

    public RfxTemplateDocument() {
    }

    public RfxTemplateDocument(String id, String fileName, String description, Date uploadDate, String credContentType, Integer fileSizeInKb, RfxTemplate rfxTemplate, Boolean internal, String uploadByName) {
        this.rfxTemplate = rfxTemplate;
        if (rfxTemplate != null) {
            rfxTemplate.getTemplateName();
        }
        setCredContentType(credContentType);
        setDescription(description);
        setFileName(fileName);
        setFileSizeInKb(fileSizeInKb);
        setId(id);
        setUploadDate(uploadDate);
        setInternal(internal);
        setUploadByName(uploadByName);
    }

    public RfxTemplateDocument(String id, String fileName, String description, Date uploadDate, String credContentType, RfxTemplate rfxTemplate, Integer fileSizeInKb) {
        this.rfxTemplate = rfxTemplate;
        if (rfxTemplate != null) {
            rfxTemplate.getTemplateName();
        }
        setCredContentType(credContentType);
        setDescription(description);
        setFileName(fileName);
        setId(id);
        setUploadDate(uploadDate);
        setFileSizeInKb(fileSizeInKb);
    }

    public RfxTemplateDocument copyFrom(RfxTemplate oldTemplate) {
        RfxTemplateDocument newDocs = new RfxTemplateDocument();
        newDocs.setFileData(getFileData());
        newDocs.setFileName(getFileName());
        newDocs.setDescription(getDescription());
        newDocs.setCredContentType(getCredContentType());
        newDocs.setUploadDate(getUploadDate());
        newDocs.setRfxTemplate(oldTemplate);
        return newDocs;
    }

    public RfxTemplateDocument(String id, String fileName, String description, Date uploadDate, String credContentType, Integer fileSizeInKb) {
        setId(id);
        setFileName(fileName);
        setDescription(description);
        setUploadDate(uploadDate);
        setCredContentType(credContentType);
        setFileSizeInKb(fileSizeInKb);
    }


    public RfxTemplateDocument(String id, String fileName, String description, Date uploadDate, String credContentType, RfxTemplate rfxTemplate, Integer fileSizeInKb,Boolean internal) {
        this.rfxTemplate = rfxTemplate;
        if (rfxTemplate != null) {
            rfxTemplate.getTemplateName();
        }
        setCredContentType(credContentType);
        setDescription(description);
        setFileName(fileName);
        setId(id);
        setUploadDate(uploadDate);
        setFileSizeInKb(fileSizeInKb);
        setInternal(internal);
    }

    public RfxTemplate getRfxTemplate() {
        return rfxTemplate;
    }

    public void setRfxTemplate(RfxTemplate rfxTemplate) {
        this.rfxTemplate = rfxTemplate;
    }

    public Boolean getInternal() {
        return internal;
    }

    public void setInternal(Boolean internal) {
        this.internal = internal;
    }

    public User getUploadBy() {
        return uploadBy;
    }

    public void setUploadBy(User uploadBy) {
        this.uploadBy = uploadBy;
    }

    public String getUploadByName() {
        return uploadByName;
    }

    public void setUploadByName(String uploadByName) {
        this.uploadByName = uploadByName;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return super.hashCode();
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    public String toLogString() {
        return "RfxTemplateDocument [ " + toLogString() + "]";
    }

}

