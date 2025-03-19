package com.privasia.procurehere.core.pojo;

import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EvaluationSorItemPojo implements Serializable {
    private static final long serialVersionUID = 3597002524187211427L;

    private String level;
    private String description;
    private String itemName;
    private String uom;
    private BigDecimal amount;
    private BigDecimal totalAmt;
    private List<EvaluationBqItemComments> review;
    private String decimal;
    private String imgPath;

    private BigDecimal supplier1TotalAmt;
    private BigDecimal supplier2TotalAmt;

    private String supplierName;
    private String supplier1Name;
    private String supplier2Name;

    private Boolean revisedBidSubmitted;
    private Boolean revisedBidSubmitted1;
    private Boolean revisedBidSubmitted2;

    private String remark;

    private String reviews;
}
