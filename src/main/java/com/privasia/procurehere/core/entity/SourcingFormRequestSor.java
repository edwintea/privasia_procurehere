package com.privasia.procurehere.core.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.privasia.procurehere.core.enums.BqUserTypes;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "PROC_SOURCING_FORM_SOR_REQ")
public class SourcingFormRequestSor implements Serializable {
    private static final long serialVersionUID = 4646169699940304299L;

    @Id
    @GenericGenerator(name = "idGen", strategy = "uuid.hex")
    @GeneratedValue(generator = "idGen")
    @Column(name = "ID", length = 64)
    private String id;


    @Column(name = "NAME", length = 128)
    private String name;

    @Column(name = "DESCRIPTION", length = 550)
    private String description;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "CREATED_DATE")
    private Date createdDate;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "MODIFIED_DATE")
    private Date modifiedDate;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "FORM_REQ_ID", foreignKey = @ForeignKey(name = "FK_SF_SOR_FORM_REQ_ID"))
    private SourcingFormRequest sourcingFormRequest;

    @JsonIgnore
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "sor", orphanRemoval = true)
    @OrderBy("level,order")
    private List<SourcingFormRequestSorItem> sorItems;

    @Column(name = "FIELD1_LABEL", nullable = true, length = 32)
    private String field1Label;


    @Enumerated(EnumType.STRING)
    @Column(name = "FIELD1_FILLEDBY")
    private BqUserTypes field1FilledBy;

    @Column(name = "FIELD1_SHOW_SUPPLIER")
    @Type(type = "org.hibernate.type.NumericBooleanType")
    private Boolean field1ToShowSupplier = Boolean.FALSE;

    @Column(name = "FIELD1_REQUIRED")
    @Type(type = "org.hibernate.type.NumericBooleanType")
    private Boolean field1Required = Boolean.FALSE;


    @Column(name = "SOR_ORDER")
    private Integer sorOrder;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Date getModifiedDate() {
        return modifiedDate;
    }

    public void setModifiedDate(Date modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    public SourcingFormRequest getSourcingFormRequest() {
        return sourcingFormRequest;
    }

    public void setSourcingFormRequest(SourcingFormRequest sourcingFormRequest) {
        this.sourcingFormRequest = sourcingFormRequest;
    }

    public List<SourcingFormRequestSorItem> getSorItems() {
        return sorItems;
    }

    public void setSorItems(List<SourcingFormRequestSorItem> sorItems) {
        this.sorItems = sorItems;
    }

    public String getField1Label() {
        return field1Label;
    }

    public void setField1Label(String field1Label) {
        this.field1Label = field1Label;
    }

    public Integer getSorOrder() {
        return sorOrder;
    }

    public void setSorOrder(Integer sorOrder) {
        this.sorOrder = sorOrder;
    }


    public BqUserTypes getField1FilledBy() {
        return field1FilledBy;
    }

    public void setField1FilledBy(BqUserTypes field1FilledBy) {
        this.field1FilledBy = field1FilledBy;
    }

    public Boolean getField1ToShowSupplier() {
        return field1ToShowSupplier;
    }

    public void setField1ToShowSupplier(Boolean field1ToShowSupplier) {
        this.field1ToShowSupplier = field1ToShowSupplier;
    }

    public Boolean getField1Required() {
        return field1Required;
    }

    public void setField1Required(Boolean field1Required) {
        this.field1Required = field1Required;
    }
}
