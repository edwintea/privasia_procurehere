package com.privasia.procurehere.core.entity;

import com.privasia.procurehere.core.enums.BqUserTypes;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@MappedSuperclass
@Data
public class Sor implements Serializable {
    private static final long serialVersionUID = 8446708672025636557L;

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

    @Transient
    Integer headerCount;

    public String toLogString() {
        return "Schedule of Rate [name=" + name + ", createdDate=" + createdDate + ", modifiedDate=" + modifiedDate + "]";
    }
}
