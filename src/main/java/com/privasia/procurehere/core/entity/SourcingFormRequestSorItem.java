package com.privasia.procurehere.core.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.privasia.procurehere.core.enums.PricingTypes;
import com.privasia.procurehere.core.enums.TaxType;
import com.privasia.procurehere.core.enums.UnitPricingTypes;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.CascadeType;
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
import javax.persistence.Transient;
import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "PROC_SOURCING_FORM_SOR_ITEM_REQ")
public class SourcingFormRequestSorItem implements Serializable {

    private static final long serialVersionUID = 8071104391034850049L;


    @Id
    @GenericGenerator(name = "idGen", strategy = "uuid.hex")
    @GeneratedValue(generator = "idGen")
    @Column(name = "ID", length = 64)
    private String id;

    @Column(name = "ITEM_NAME", nullable = false, length = 250)
    private String itemName;

    @Column(name = "ITEM_LEVEL", length = 2, nullable = false)
    private Integer level = 0;

    @Column(name = "SUB_ORDER", length = 2, nullable = false)
    private Integer order = 0;

    @ManyToOne(optional = true, cascade = {CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(name = "UOM_ID", nullable = true)
    private Uom uom;


    @Size(max = 2000)
    @Column(name = "ITEM_DESCRIPTION", nullable = true, length = 2000)
    private String itemDescription;

    @DecimalMax("9999999999999999.9999")
    @Column(name = "TOTAL_AMOUNT", precision = 16, scale = 4, nullable = true)
    private BigDecimal totalAmount;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "FORM_REQ_ID", nullable = true, foreignKey = @ForeignKey(name = "FK_SF_SOR_ITEM_FORM_REQ_ID"))
    private SourcingFormRequest sourcingFormRequest;

    @ManyToOne(optional = false, cascade = {CascadeType.REFRESH})
    @JoinColumn(name = "SOR_ID", nullable = false, foreignKey = @ForeignKey(name = "FK_SOR_FORM_REQ_ID"))
    private SourcingFormRequestSor sor;

    @ManyToOne(optional = true, cascade = {CascadeType.MERGE})
    @JoinColumn(name = "PARENT_ID", nullable = true, foreignKey = @ForeignKey(name = "FK_SF_SOR_PARENT"))
    private SourcingFormRequestSorItem parent;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "parent")
    @OrderBy("order")
    private List<SourcingFormRequestSorItem> children;

    @Column(name = "FIELD_1", nullable = true, length = 100)
    private String field1;

    @Transient
    private String sorId;

    @Transient
    private List<String> columnTitles;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public Uom getUom() {
        return uom;
    }

    public void setUom(Uom uom) {
        this.uom = uom;
    }

    public String getItemDescription() {
        return itemDescription;
    }

    public void setItemDescription(String itemDescription) {
        this.itemDescription = itemDescription;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }


    public SourcingFormRequest getSourcingFormRequest() {
        return sourcingFormRequest;
    }

    public void setSourcingFormRequest(SourcingFormRequest sourcingFormRequest) {
        this.sourcingFormRequest = sourcingFormRequest;
    }

    public SourcingFormRequestSor getSor() {
        return sor;
    }

    public void setSor(SourcingFormRequestSor sor) {
        this.sor = sor;
    }

    public SourcingFormRequestSorItem getParent() {
        return parent;
    }

    public void setParent(SourcingFormRequestSorItem parent) {
        this.parent = parent;
    }

    public List<SourcingFormRequestSorItem> getChildren() {
        return children;
    }

    public void setChildren(List<SourcingFormRequestSorItem> children) {
        this.children = children;
    }

    public String getField1() {
        return field1;
    }

    public void setField1(String field1) {
        this.field1 = field1;
    }

    public String getSorId() {
        return sorId;
    }

    public void setSorId(String sorId) {
        this.sorId = sorId;
    }

    public List<String> getColumnTitles() {
        return columnTitles;
    }

    public void setColumnTitles(List<String> columnTitles) {
        this.columnTitles = columnTitles;
    }


    public SourcingFormRequestSorItem createSearchShallowCopy() {

        SourcingFormRequestSorItem ic = new SourcingFormRequestSorItem();
        ic.setId(getId());
        ic.setItemDescription(getItemDescription());
        if (getUom() != null) {
            ic.setUom(getUom().createShallowCopy());
        }
        ic.setItemName(getItemName());
        ic.setLevel(getLevel());
        ic.setOrder(getOrder());
        ic.setSor(getSor());
        if (getParent() != null) {
            SourcingFormRequestSorItem parent = new SourcingFormRequestSorItem();
            parent.setId(getParent().getId());
            ic.setParent(parent);
        }
        ic.setField1(getField1());
        return ic;
    }

    public SourcingFormRequestSorItem createShallowCopy() {
        SourcingFormRequestSorItem ic = new SourcingFormRequestSorItem();
        ic.setId(getId());
        ic.setItemDescription(getItemDescription());
        if (getUom() != null) {
            ic.setUom(getUom().createShallowCopy());
        }
        ic.setItemName(getItemName());
        ic.setLevel(getLevel());
        ic.setOrder(getOrder());
        ic.setSor(getSor());
        ic.setField1(getField1());
        return ic;
    }


    public RfaSorItem copyForRfa(SourcingFormRequestSorItem newSor) {
        RfaSorItem bq = new RfaSorItem();
        bq.setItemName(newSor.getItemName());
        bq.setLevel(newSor.getLevel());
        bq.setOrder(newSor.getOrder());
        bq.setUom(newSor.getUom());
        bq.setItemDescription(newSor.getItemDescription());
        bq.setTotalAmount(newSor.getTotalAmount());
        bq.setField1(getField1());

        return bq;
    }


    public RfpSorItem copyForRfp(SourcingFormRequestSorItem newBq) {
        RfpSorItem bq = new RfpSorItem();
        bq.setItemName(newBq.getItemName());
        bq.setLevel(newBq.getLevel());
        bq.setOrder(newBq.getOrder());
        bq.setUom(newBq.getUom());
        bq.setItemDescription(newBq.getItemDescription());
        bq.setTotalAmount(newBq.getTotalAmount());
        bq.setField1(getField1());
        return bq;
    }

    public RfqSorItem copyForRfq(SourcingFormRequestSorItem newBq) {
        RfqSorItem bq = new RfqSorItem();
        bq.setItemName(newBq.getItemName());
        bq.setLevel(newBq.getLevel());
        bq.setOrder(newBq.getOrder());
        bq.setUom(newBq.getUom());
        bq.setItemDescription(newBq.getItemDescription());
        bq.setTotalAmount(newBq.getTotalAmount());
        bq.setField1(getField1());
        return bq;
    }

    public RftSorItem copyForRft(SourcingFormRequestSorItem newBq) {
        RftSorItem bq = new RftSorItem();
        bq.setItemName(newBq.getItemName());
        bq.setLevel(newBq.getLevel());
        bq.setOrder(newBq.getOrder());
        bq.setUom(newBq.getUom());
        bq.setItemDescription(newBq.getItemDescription());
        bq.setTotalAmount(newBq.getTotalAmount());
        bq.setField1(getField1());
        return bq;
    }


    public RfiSorItem copyForRfi(SourcingFormRequestSorItem newBq) {
        RfiSorItem bq = new RfiSorItem();
        bq.setItemName(newBq.getItemName());
        bq.setLevel(newBq.getLevel());
        bq.setOrder(newBq.getOrder());
        bq.setUom(newBq.getUom());
        bq.setItemDescription(newBq.getItemDescription());
        bq.setTotalAmount(newBq.getTotalAmount());
        bq.setField1(getField1());
        return bq;
    }

}
