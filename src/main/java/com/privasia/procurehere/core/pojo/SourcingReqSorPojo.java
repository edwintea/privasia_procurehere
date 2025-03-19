package com.privasia.procurehere.core.pojo;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
public class SourcingReqSorPojo {

    private static final long serialVersionUID = 8120655645791624398L;
    private String id;
    private String formId;
    private String sorName;
    private String sorDesc;

    private String searchVal;
    private String filterVal;

    private Integer start;
    private Integer pageLength;
    private Integer pageNo;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFormId() {
        return formId;
    }

    public void setFormId(String formId) {
        this.formId = formId;
    }

    public String getSorName() {
        return sorName;
    }

    public void setSorName(String sorName) {
        this.sorName = sorName;
    }

    public String getSorDesc() {
        return sorDesc;
    }

    public void setSorDesc(String sorDesc) {
        this.sorDesc = sorDesc;
    }

    public String getSearchVal() {
        return searchVal;
    }

    public void setSearchVal(String searchVal) {
        this.searchVal = searchVal;
    }

    public String getFilterVal() {
        return filterVal;
    }

    public void setFilterVal(String filterVal) {
        this.filterVal = filterVal;
    }

    public Integer getStart() {
        return start;
    }

    public void setStart(Integer start) {
        this.start = start;
    }

    public Integer getPageLength() {
        return pageLength;
    }

    public void setPageLength(Integer pageLength) {
        this.pageLength = pageLength;
    }

    public Integer getPageNo() {
        return pageNo;
    }

    public void setPageNo(Integer pageNo) {
        this.pageNo = pageNo;
    }
}
