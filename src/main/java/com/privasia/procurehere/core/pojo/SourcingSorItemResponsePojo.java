package com.privasia.procurehere.core.pojo;

import com.privasia.procurehere.core.entity.SourcingFormRequestSorItem;
import lombok.Data;

import java.util.List;

@Data
public class SourcingSorItemResponsePojo {
    private static final long serialVersionUID = 7786318306712331135L;

    private List<SourcingFormRequestSorItem> bqItemList;

    private List<SourcingSorItemPojo> leveLOrderList;

    private long totalBqItemCount;
}
