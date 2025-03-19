package com.privasia.procurehere.core.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.Type;

@Entity
@Table(name = "PROC_RFS_TEMPLATE_DOCUMENTS")
public class RfsTemplateDocument extends EventDocument implements Serializable {

    private static final long serialVersionUID = 7078972320588693888L;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "RFS_TEMPLATE_ID", foreignKey = @ForeignKey(name = "FK_RFS_TEMPLATE_DOCUMENT"))
    private SourcingFormTemplate sourcingFormTemplate;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DOC_UPLOAD_BY", nullable = true, foreignKey = @ForeignKey(name = "FK_RFS_TEMPLATE_DOC_UPLD_BY"))
    private User uploadBy;

    @Transient
    private String uploadByName;

    @Column(name = "IS_INTERNAL ")
    @Type(type = "org.hibernate.type.NumericBooleanType")
    private Boolean internal = Boolean.TRUE;

    public RfsTemplateDocument() {
    }

    public RfsTemplateDocument(String id, String fileName, String description, Date uploadDate, String credContentType, Integer fileSizeInKb, SourcingFormTemplate sourcingFormTemplate, String uploadBy) {
        this.sourcingFormTemplate = sourcingFormTemplate;
        if (sourcingFormTemplate != null) {
            sourcingFormTemplate.getFormName();
        }
        setCredContentType(credContentType);
        setDescription(description);
        setFileName(fileName);
        setId(id);
        setUploadDate(uploadDate);
        setFileSizeInKb(fileSizeInKb);
        setUploadByName(uploadBy);
    }

    public RfsTemplateDocument(String id, String fileName, String description, Date uploadDate, String credContentType, Integer fileSizeInKb, SourcingFormTemplate sourcingFormTemplate, String uploadById, String uploadByLoginId) {
        this.sourcingFormTemplate = sourcingFormTemplate;
        if (sourcingFormTemplate != null) {
            sourcingFormTemplate.getFormName();
        }
        setCredContentType(credContentType);
        setDescription(description);
        setFileName(fileName);
        setId(id);
        setUploadDate(uploadDate);
        setFileSizeInKb(fileSizeInKb);
        User uploadBy = new User();
        uploadBy.setId(uploadById);
        uploadBy.setLoginId(uploadByLoginId);
        this.uploadBy = uploadBy;
        setUploadBy(uploadBy);
    }

    public RfsTemplateDocument copyFrom() {
        RfsTemplateDocument newDoc = new RfsTemplateDocument();
        newDoc.setCredContentType(getCredContentType());
        newDoc.setDescription(getDescription());
        newDoc.setFileData(getFileData());
        newDoc.setFileName(getFileName());
        newDoc.setUploadDate(getUploadDate());
        newDoc.setUploadBy(getUploadBy());
        return newDoc;
    }

    public RfsTemplateDocument(String id, String fileName, Integer fileSizeInKb) {
        setId(id);
        setFileName(fileName);
        setFileSizeInKb(fileSizeInKb);
    }

    public RfsTemplateDocument(String id, String fileName, Integer fileSizeInKb, String credContentType) {
        setId(id);
        setFileName(fileName);
        setFileSizeInKb(fileSizeInKb);
        setCredContentType(credContentType);
    }

    public RfsTemplateDocument createMobileShallowCopy() {
        RfsTemplateDocument ic = new RfsTemplateDocument();
        ic.setId(getId());
        ic.setFileName(getFileName());
        ic.setFileSize(getFileSizeInKb());
        ic.setCredContentType(getCredContentType());
        return ic;
    }


    /**
     * @return the uploadBy
     */
    public User getUploadBy() {
        return uploadBy;
    }

    /**
     * @param uploadBy the uploadBy to set
     */
    public void setUploadBy(User uploadBy) {
        this.uploadBy = uploadBy;
    }

    public SourcingFormTemplate getSourcingFormTemplate() {
        return sourcingFormTemplate;
    }

    public void setSourcingFormTemplate(SourcingFormTemplate sourcingFormTemplate) {
        this.sourcingFormTemplate = sourcingFormTemplate;
    }

    public String getUploadByName() {
        return uploadByName;
    }

    public void setUploadByName(String uploadByName) {
        this.uploadByName = uploadByName;
    }

    public Boolean getInternal() {
        return internal;
    }

    public void setInternal(Boolean internal) {
        this.internal = internal;
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



}
