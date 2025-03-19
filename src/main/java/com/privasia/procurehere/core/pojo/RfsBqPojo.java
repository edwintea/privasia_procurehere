package com.privasia.procurehere.core.pojo;

import java.io.Serializable;

import com.privasia.procurehere.core.entity.SourcingFormRequestBq;
import com.privasia.procurehere.core.entity.SourcingFormRequestSor;

/**
 * @author sana
 */
public class RfsBqPojo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3862261023800908353L;
	private String id;
	private String eventId;
	private String bqName;
	private String bqDesc;

	private String field1Label;

	private String field2Label;

	private String field3Label;

	private String field4Label;

	private String field5Label;

	private String field6Label;

	private String field7Label;

	private String field8Label;

	private String field9Label;

	private String field10Label;

	private String searchVal;
	private String filterVal;

	private Integer start;
	private Integer pageLength;
	private Integer pageNo;

	public RfsBqPojo() {
	}

	public RfsBqPojo(String id, String bqName) {
		this.id = id;
		this.bqName = bqName;
	}

	public RfsBqPojo(SourcingFormRequestBq rfsBq) {
		this.field1Label = rfsBq.getField1Label();
		this.field2Label = rfsBq.getField2Label();
		this.field3Label = rfsBq.getField3Label();
		this.field4Label = rfsBq.getField4Label();
		this.field5Label = rfsBq.getField5Label();
		this.field6Label = rfsBq.getField6Label();
		this.field7Label = rfsBq.getField7Label();
		this.field8Label = rfsBq.getField8Label();
		this.field9Label = rfsBq.getField9Label();
		this.field10Label = rfsBq.getField10Label();
	}


	public RfsBqPojo(SourcingFormRequestSor rfsBq) {
		this.field1Label = rfsBq.getField1Label();
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the eventId
	 */
	public String getEventId() {
		return eventId;
	}

	/**
	 * @param eventId the eventId to set
	 */
	public void setEventId(String eventId) {
		this.eventId = eventId;
	}

	/**
	 * @return the bqName
	 */
	public String getBqName() {
		return bqName;
	}

	/**
	 * @param bqName the bqName to set
	 */
	public void setBqName(String bqName) {
		this.bqName = bqName;
	}

	/**
	 * @return the bqDesc
	 */
	public String getBqDesc() {
		return bqDesc;
	}

	/**
	 * @param bqDesc the bqDesc to set
	 */
	public void setBqDesc(String bqDesc) {
		this.bqDesc = bqDesc;
	}

	/**
	 * @return the field1Label
	 */
	public String getField1Label() {
		return field1Label;
	}

	/**
	 * @param field1Label the field1Label to set
	 */
	public void setField1Label(String field1Label) {
		this.field1Label = field1Label;
	}

	/**
	 * @return the field2Label
	 */
	public String getField2Label() {
		return field2Label;
	}

	/**
	 * @param field2Label the field2Label to set
	 */
	public void setField2Label(String field2Label) {
		this.field2Label = field2Label;
	}

	/**
	 * @return the field3Label
	 */
	public String getField3Label() {
		return field3Label;
	}

	/**
	 * @param field3Label the field3Label to set
	 */
	public void setField3Label(String field3Label) {
		this.field3Label = field3Label;
	}

	/**
	 * @return the field4Label
	 */
	public String getField4Label() {
		return field4Label;
	}

	/**
	 * @param field4Label the field4Label to set
	 */
	public void setField4Label(String field4Label) {
		this.field4Label = field4Label;
	}

	/**
	 * @return the field5Label
	 */
	public String getField5Label() {
		return field5Label;
	}

	/**
	 * @param field5Label the field5Label to set
	 */
	public void setField5Label(String field5Label) {
		this.field5Label = field5Label;
	}

	/**
	 * @return the field6Label
	 */
	public String getField6Label() {
		return field6Label;
	}

	/**
	 * @param field6Label the field6Label to set
	 */
	public void setField6Label(String field6Label) {
		this.field6Label = field6Label;
	}

	/**
	 * @return the field7Label
	 */
	public String getField7Label() {
		return field7Label;
	}

	/**
	 * @param field7Label the field7Label to set
	 */
	public void setField7Label(String field7Label) {
		this.field7Label = field7Label;
	}

	/**
	 * @return the field8Label
	 */
	public String getField8Label() {
		return field8Label;
	}

	/**
	 * @param field8Label the field8Label to set
	 */
	public void setField8Label(String field8Label) {
		this.field8Label = field8Label;
	}

	/**
	 * @return the field9Label
	 */
	public String getField9Label() {
		return field9Label;
	}

	/**
	 * @param field9Label the field9Label to set
	 */
	public void setField9Label(String field9Label) {
		this.field9Label = field9Label;
	}

	/**
	 * @return the field10Label
	 */
	public String getField10Label() {
		return field10Label;
	}

	/**
	 * @param field10Label the field10Label to set
	 */
	public void setField10Label(String field10Label) {
		this.field10Label = field10Label;
	}

	/**
	 * @return the searchVal
	 */
	public String getSearchVal() {
		return searchVal;
	}

	/**
	 * @param searchVal the searchVal to set
	 */
	public void setSearchVal(String searchVal) {
		this.searchVal = searchVal;
	}

	/**
	 * @return the filterVal
	 */
	public String getFilterVal() {
		return filterVal;
	}

	/**
	 * @param filterVal the filterVal to set
	 */
	public void setFilterVal(String filterVal) {
		this.filterVal = filterVal;
	}

	/**
	 * @return the start
	 */
	public Integer getStart() {
		return start;
	}

	/**
	 * @param start the start to set
	 */
	public void setStart(Integer start) {
		this.start = start;
	}

	/**
	 * @return the pageLength
	 */
	public Integer getPageLength() {
		return pageLength;
	}

	/**
	 * @param pageLength the pageLength to set
	 */
	public void setPageLength(Integer pageLength) {
		this.pageLength = pageLength;
	}

	/**
	 * @return the pageNo
	 */
	public Integer getPageNo() {
		return pageNo;
	}

	/**
	 * @param pageNo the pageNo to set
	 */
	public void setPageNo(Integer pageNo) {
		this.pageNo = pageNo;
	}

	public String toLogString() {
		return "RfsBqPojo [eventId=" + eventId + ", bqName=" + bqName + ", field1Label=" + field1Label + ", field2Label=" + field2Label + ", field3Label=" + field3Label + ", field4Label=" + field4Label + ", field5Label=" + field5Label + ", field6Label=" + field6Label + ", field7Label=" + field7Label + ", field8Label=" + field8Label + ", field9Label=" + field9Label + ", field10Label=" + field10Label + ", searchVal=" + searchVal + ", filterVal=" + filterVal + "]";
	}

}
