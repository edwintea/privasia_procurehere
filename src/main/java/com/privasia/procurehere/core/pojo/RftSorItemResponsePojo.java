package com.privasia.procurehere.core.pojo;

import com.privasia.procurehere.core.entity.RftSorItem;
import lombok.Data;

import java.util.List;

@Data
public class RftSorItemResponsePojo {
    private static final long serialVersionUID = 8899847753512917298L;

    private List<RftSorItem> bqItemList;

    private List<BqItemPojo> leveLOrderList;

    private long totalBqItemCount;
}
