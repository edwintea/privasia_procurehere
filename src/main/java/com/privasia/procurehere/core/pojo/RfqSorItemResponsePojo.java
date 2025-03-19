package com.privasia.procurehere.core.pojo;

import com.privasia.procurehere.core.entity.RfqSorItem;
import lombok.Data;

import java.util.List;

@Data
public class RfqSorItemResponsePojo {
    private static final long serialVersionUID = 8899847753512817298L;

    private List<RfqSorItem> bqItemList;

    private List<BqItemPojo> leveLOrderList;

    private long totalBqItemCount;
}
