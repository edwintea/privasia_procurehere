package com.privasia.procurehere.core.pojo;

import com.privasia.procurehere.core.enums.PricingTypes;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SourcingSorItemPojo {
    private static final long serialVersionUID = 560077298361509725L;

    private String id;
    private String itemName;
    private Integer level;
    private Integer order;
    private BigDecimal quantity;
    private BigDecimal unitPrice;
    private String uom;
    private String sor;
    private String parent;
    private String formId;
    private String itemDescription;
    private PricingTypes priceType;

    private String searchVal;
    private String filterVal;

    private Integer start;
    private Integer pageLength;
    private Integer pageNo;

    private String field1;

    private String field2;

    private String field3;

    private String field4;

    private String field5;

    private String field6;

    private String field7;

    private String field8;

    private String field9;

    private String field10;

    private BigDecimal unitBudgetPrice;


    public SourcingSorItemPojo(Integer level, Integer order) {
        this.level = level;
        this.order = order;
    }
}
