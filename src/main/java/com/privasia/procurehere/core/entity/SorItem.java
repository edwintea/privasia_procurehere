package com.privasia.procurehere.core.entity;

import com.privasia.procurehere.core.enums.PricingTypes;
import com.privasia.procurehere.core.enums.TaxType;
import com.privasia.procurehere.core.enums.UnitPricingTypes;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@MappedSuperclass
@Data
public class SorItem implements Serializable {
    private static final long serialVersionUID = 8446708672025636559L;

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

    @ManyToOne(optional = true, cascade = { CascadeType.PERSIST, CascadeType.REFRESH })
    @JoinColumn(name = "UOM_ID", nullable = true)
    private Uom uom;

    @Size(max = 2000)
    @Column(name = "ITEM_DESCRIPTION", nullable = true, length = 2000)
    private String itemDescription;


    @Column(name = "FIELD1", nullable = true, length = 100)
    private String field1;


    // Now it is working as Rate
    @DecimalMax("999999999999.999999")
    @Column(name = "TOTAL_AMOUNT", precision = 18, scale = 6, nullable = true)
    private BigDecimal totalAmount;


    @Transient
    private List<String> columnTitles;
}
