package com.privasia.procurehere.core.pojo;

import java.io.Serializable;
import java.util.List;

public class EvaluationSorPojo implements Serializable {
    private static final long serialVersionUID = 4893039821776188027L;

    private String name;
    private String supplierName;
    private String supplierName1;
    private String supplierName2;
    private List<EvaluationSorItemPojo> bqItems;
    private List<EvaluationSuppliersPojo> leadComments;
    private String title;
    private String remark;
    private List<RequestBqItemPojo> requestItems;

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the bqItems
     */
    public List<EvaluationSorItemPojo> getBqItems() {
        return bqItems;
    }

    /**
     * @param bqItems the bqItems to set
     */
    public void setBqItems(List<EvaluationSorItemPojo> bqItems) {
        this.bqItems = bqItems;
    }

    /**
     * @return the leadComments
     */
    public List<EvaluationSuppliersPojo> getLeadComments() {
        return leadComments;
    }

    /**
     * @param leadComments the leadComments to set
     */
    public void setLeadComments(List<EvaluationSuppliersPojo> leadComments) {
        this.leadComments = leadComments;
    }

    /**
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param title the title to set
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "EvaluationBqPojo [name=" + name + ", bqItems=" + bqItems + "]";
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public String getSupplierName1() {
        return supplierName1;
    }

    public void setSupplierName1(String supplierName1) {
        this.supplierName1 = supplierName1;
    }

    public String getSupplierName2() {
        return supplierName2;
    }

    public void setSupplierName2(String supplierName2) {
        this.supplierName2 = supplierName2;
    }

    /**
     * @return the remark
     */
    public String getRemark() {
        return remark;
    }

    /**
     * @param remark the remark to set
     */
    public void setRemark(String remark) {
        this.remark = remark;
    }

    public List<RequestBqItemPojo> getRequestItems() {
        return requestItems;
    }

    public void setRequestItems(List<RequestBqItemPojo> requestItems) {
        this.requestItems = requestItems;
    }
}
