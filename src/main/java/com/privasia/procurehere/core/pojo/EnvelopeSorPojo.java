package com.privasia.procurehere.core.pojo;

import java.io.Serializable;
import java.util.List;

public class EnvelopeSorPojo implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 3062276684599726481L;

    String sorName;
    List<EvaluationSuppliersSorPojo> supplierSor;

    List<EvaluationSuppliersSorPojo> topSupplierSor;


    public String getSorName() {
        return sorName;
    }

    public void setSorName(String sorName) {
        this.sorName = sorName;
    }

    public List<EvaluationSuppliersSorPojo> getSupplierSor() {
        return supplierSor;
    }

    public void setSupplierSor(List<EvaluationSuppliersSorPojo> supplierSor) {
        this.supplierSor = supplierSor;
    }

    public List<EvaluationSuppliersSorPojo> getTopSupplierSor() {
        return topSupplierSor;
    }

    public void setTopSupplierSor(List<EvaluationSuppliersSorPojo> topSupplierSor) {
        this.topSupplierSor = topSupplierSor;
    }
}
