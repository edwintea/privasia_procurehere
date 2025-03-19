package com.privasia.procurehere.core.pojo;

import com.privasia.procurehere.core.entity.RfiSorItem;
import lombok.Data;

import java.util.List;

@Data
public class RfiSorItemResponsePojo {
    private static final long serialVersionUID = 8899847753512917293L;

    private List<RfiSorItem> bqItemList;

    private List<BqItemPojo> leveLOrderList;

    private long totalBqItemCount;
}
