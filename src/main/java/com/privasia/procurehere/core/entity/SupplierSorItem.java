package com.privasia.procurehere.core.entity;

import java.io.Serializable;

public class SupplierSorItem extends SorItem implements Serializable {
    private static final long serialVersionUID = 5514524970325579467L;

    private RfqSupplierSor supplierSor;

    public RfqSupplierSor getSupplierSor() {
        return supplierSor;
    }

    public void setSupplierSor(RfqSupplierSor supplierSor) {
        this.supplierSor = supplierSor;
    }
}
