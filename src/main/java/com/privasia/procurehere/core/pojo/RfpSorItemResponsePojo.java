package com.privasia.procurehere.core.pojo;

import com.privasia.procurehere.core.entity.RfpSorItem;
import lombok.Data;

import java.util.List;

@Data
public class RfpSorItemResponsePojo {
    private static final long serialVersionUID = 8899847753512817298L;

    private List<RfpSorItem> bqItemList;

    private List<BqItemPojo> leveLOrderList;

    private long totalBqItemCount;
}
