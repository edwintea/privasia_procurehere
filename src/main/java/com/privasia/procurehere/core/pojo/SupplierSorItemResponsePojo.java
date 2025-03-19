package com.privasia.procurehere.core.pojo;

import java.util.List;

public class SupplierSorItemResponsePojo {
    private static final long serialVersionUID = 1370116894347524479L;

    private List<?> supplierSorItemList;

    private List<SorItemPojo> levelOrderList;

    private long totalBqItemCount;

    public SupplierSorItemResponsePojo() {
        super();
    }

    public List<?> getSupplierSorItemList() {
        return supplierSorItemList;
    }

    public void setSupplierSorItemList(List<?> supplierSorItemList) {
        this.supplierSorItemList = supplierSorItemList;
    }

    public List<SorItemPojo> getLevelOrderList() {
        return levelOrderList;
    }

    public void setLevelOrderList(List<SorItemPojo> levelOrderList) {
        this.levelOrderList = levelOrderList;
    }

    public long getTotalBqItemCount() {
        return totalBqItemCount;
    }

    public void setTotalBqItemCount(long totalBqItemCount) {
        this.totalBqItemCount = totalBqItemCount;
    }
}
