package com.privasia.procurehere.core.pojo;

import com.privasia.procurehere.core.entity.RfaSorItem;
import lombok.Data;

import java.util.List;

@Data
public class RfaSorItemResponsePojo {
    private static final long serialVersionUID = 8899847753512917298L;

    private List<RfaSorItem> bqItemList;

    private List<BqItemPojo> leveLOrderList;

    private long totalBqItemCount;
}
