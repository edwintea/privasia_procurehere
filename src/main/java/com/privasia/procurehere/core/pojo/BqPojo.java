package com.privasia.procurehere.core.pojo;

import java.io.Serializable;

import com.privasia.procurehere.core.entity.Bq;
import com.privasia.procurehere.core.entity.RftEventBq;
import com.privasia.procurehere.core.enums.BqUserTypes;

public class BqPojo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3862261023800908353L;
	private String id;
	private String eventId;
	private String bqName;
	private String bqDesc;

	private String field1Label;
	private BqUserTypes field1FilledBy;
	private boolean field1ToShowSupplier;
	private boolean field1Required;

	private String field2Label;
	private BqUserTypes field2FilledBy;
	private boolean field2ToShowSupplier;
	private boolean field2Required;

	private String field3Label;
	private BqUserTypes field3FilledBy;
	private boolean field3ToShowSupplier;
	private boolean field3Required;

	private String field4Label;
	private BqUserTypes field4FilledBy;
	private boolean field4ToShowSupplier;
	private boolean field4Required;

	private String field5Label;
	private BqUserTypes field5FilledBy;
	private boolean field5ToShowSupplier;
	private boolean field5Required;

	private String field6Label;
	private BqUserTypes field6FilledBy;
	private boolean field6ToShowSupplier;
	private boolean field6Required;

	private String field7Label;
	private BqUserTypes field7FilledBy;
	private boolean field7ToShowSupplier;
	private boolean field7Required;

	private String field8Label;
	private BqUserTypes field8FilledBy;
	private boolean field8ToShowSupplier;
	private boolean field8Required;

	private String field9Label;
	private BqUserTypes field9FilledBy;
	private boolean field9ToShowSupplier;
	private boolean field9Required;

	private String field10Label;
	private BqUserTypes field10FilledBy;
	private boolean field10ToShowSupplier;
	private boolean field10Required;

	private String searchVal;
	private String filterVal;

	private Integer start;
	private Integer pageLength;
	private Integer pageNo;
	private Integer bqOrder;

	public BqPojo() {
		
	}
	public BqPojo(String id, String bqName) {
		this.id = id;
		this.bqName = bqName;
	}
	

	public BqPojo(String id, String bqName, Integer bqOrder) {
		this.id = id;
		this.bqName = bqName;
		this.bqOrder = bqOrder;
	}

	public BqPojo(RftEventBq rftEventBq, String eventId) {
		this.id = rftEventBq.getId();
		this.eventId = eventId;
		this.bqName = rftEventBq.getName();
		this.bqDesc = rftEventBq.getDescription();

		this.field1Label = rftEventBq.getField1Label();
		this.field1FilledBy = rftEventBq.getField1FilledBy();
		this.field1ToShowSupplier = rftEventBq.getField1ToShowSupplier();
		this.field1Required = rftEventBq.getField1Required();

		this.field2Label = rftEventBq.getField2Label();
		this.field2FilledBy = rftEventBq.getField2FilledBy();
		this.field2ToShowSupplier = rftEventBq.getField2ToShowSupplier();
		this.field2Required = rftEventBq.getField2Required();

		this.field3Label = rftEventBq.getField3Label();
		this.field3FilledBy = rftEventBq.getField3FilledBy();
		this.field3ToShowSupplier = rftEventBq.getField3ToShowSupplier();
		this.field3Required = rftEventBq.getField3Required();

		this.field4Label = rftEventBq.getField4Label();
		this.field4FilledBy = rftEventBq.getField4FilledBy();
		this.field4ToShowSupplier = rftEventBq.getField4ToShowSupplier();
		this.field4Required = rftEventBq.getField4Required();

		this.field5Label = rftEventBq.getField5Label();
		this.field5FilledBy = rftEventBq.getField5FilledBy();
		this.field5ToShowSupplier = rftEventBq.getField5ToShowSupplier();
		this.field5Required = rftEventBq.getField5Required();

		this.field6Label = rftEventBq.getField6Label();
		this.field6FilledBy = rftEventBq.getField6FilledBy();
		this.field6ToShowSupplier = rftEventBq.getField6ToShowSupplier();
		this.field6Required = rftEventBq.getField6Required();

		this.field7Label = rftEventBq.getField7Label();
		this.field7FilledBy = rftEventBq.getField7FilledBy();
		this.field7ToShowSupplier = rftEventBq.getField7ToShowSupplier();
		this.field7Required = rftEventBq.getField7Required();

		this.field8Label = rftEventBq.getField8Label();
		this.field8FilledBy = rftEventBq.getField8FilledBy();
		this.field8ToShowSupplier = rftEventBq.getField8ToShowSupplier();
		this.field8Required = rftEventBq.getField8Required();

		this.field9Label = rftEventBq.getField9Label();
		this.field9FilledBy = rftEventBq.getField9FilledBy();
		this.field9ToShowSupplier = rftEventBq.getField9ToShowSupplier();
		this.field9Required = rftEventBq.getField9Required();

		this.field10Label = rftEventBq.getField10Label();
		this.field10FilledBy = rftEventBq.getField10FilledBy();
		this.field10ToShowSupplier = rftEventBq.getField10ToShowSupplier();
		this.field10Required = rftEventBq.getField10Required();
	}

	public BqPojo(Bq rftEventBq) {

		this.field1Label = rftEventBq.getField1Label();
		this.field1FilledBy = rftEventBq.getField1FilledBy();
		this.field1ToShowSupplier = rftEventBq.getField1ToShowSupplier();
		this.field1Required = rftEventBq.getField1Required();

		this.field2Label = rftEventBq.getField2Label();
		this.field2FilledBy = rftEventBq.getField2FilledBy();
		this.field2ToShowSupplier = rftEventBq.getField2ToShowSupplier();
		this.field2Required = rftEventBq.getField2Required();

		this.field3Label = rftEventBq.getField3Label();
		this.field3FilledBy = rftEventBq.getField3FilledBy();
		this.field3ToShowSupplier = rftEventBq.getField3ToShowSupplier();
		this.field3Required = rftEventBq.getField3Required();

		this.field4Label = rftEventBq.getField4Label();
		this.field4FilledBy = rftEventBq.getField4FilledBy();
		this.field4ToShowSupplier = rftEventBq.getField4ToShowSupplier();
		this.field4Required = rftEventBq.getField4Required();

		this.field5Label = rftEventBq.getField5Label();
		this.field5FilledBy = rftEventBq.getField5FilledBy();
		this.field5ToShowSupplier = rftEventBq.getField5ToShowSupplier();
		this.field5Required = rftEventBq.getField5Required();

		this.field6Label = rftEventBq.getField6Label();
		this.field6FilledBy = rftEventBq.getField6FilledBy();
		this.field6ToShowSupplier = rftEventBq.getField6ToShowSupplier();
		this.field6Required = rftEventBq.getField6Required();

		this.field7Label = rftEventBq.getField7Label();
		this.field7FilledBy = rftEventBq.getField7FilledBy();
		this.field7ToShowSupplier = rftEventBq.getField7ToShowSupplier();
		this.field7Required = rftEventBq.getField7Required();

		this.field8Label = rftEventBq.getField8Label();
		this.field8FilledBy = rftEventBq.getField8FilledBy();
		this.field8ToShowSupplier = rftEventBq.getField8ToShowSupplier();
		this.field8Required = rftEventBq.getField8Required();

		this.field9Label = rftEventBq.getField9Label();
		this.field9FilledBy = rftEventBq.getField9FilledBy();
		this.field9ToShowSupplier = rftEventBq.getField9ToShowSupplier();
		this.field9Required = rftEventBq.getField9Required();

		this.field10Label = rftEventBq.getField10Label();
		this.field10FilledBy = rftEventBq.getField10FilledBy();
		this.field10ToShowSupplier = rftEventBq.getField10ToShowSupplier();
		this.field10Required = rftEventBq.getField10Required();
	}
	
	public BqPojo(Bq eventBq, String eventId) {
		this.id = eventBq.getId();
		this.eventId = eventId;
		this.bqName = eventBq.getName();
		this.bqDesc = eventBq.getDescription();
		this.bqOrder = eventBq.getBqOrder();
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
	 * @return the field1FilledBy
	 */
	public BqUserTypes getField1FilledBy() {
		return field1FilledBy;
	}

	/**
	 * @param field1FilledBy the field1FilledBy to set
	 */
	public void setField1FilledBy(BqUserTypes field1FilledBy) {
		this.field1FilledBy = field1FilledBy;
	}

	/**
	 * @return the field1ToShowSupplier
	 */
	public boolean isField1ToShowSupplier() {
		return field1ToShowSupplier;
	}

	/**
	 * @param field1ToShowSupplier the field1ToShowSupplier to set
	 */
	public void setField1ToShowSupplier(boolean field1ToShowSupplier) {
		this.field1ToShowSupplier = field1ToShowSupplier;
	}

	/**
	 * @return the field1Required
	 */
	public boolean isField1Required() {
		return field1Required;
	}

	/**
	 * @param field1Required the field1Required to set
	 */
	public void setField1Required(boolean field1Required) {
		this.field1Required = field1Required;
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
	 * @return the field2FilledBy
	 */
	public BqUserTypes getField2FilledBy() {
		return field2FilledBy;
	}

	/**
	 * @param field2FilledBy the field2FilledBy to set
	 */
	public void setField2FilledBy(BqUserTypes field2FilledBy) {
		this.field2FilledBy = field2FilledBy;
	}

	/**
	 * @return the field2ToShowSupplier
	 */
	public boolean isField2ToShowSupplier() {
		return field2ToShowSupplier;
	}

	/**
	 * @param field2ToShowSupplier the field2ToShowSupplier to set
	 */
	public void setField2ToShowSupplier(boolean field2ToShowSupplier) {
		this.field2ToShowSupplier = field2ToShowSupplier;
	}

	/**
	 * @return the field2Required
	 */
	public boolean isField2Required() {
		return field2Required;
	}

	/**
	 * @param field2Required the field2Required to set
	 */
	public void setField2Required(boolean field2Required) {
		this.field2Required = field2Required;
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
	 * @return the field3FilledBy
	 */
	public BqUserTypes getField3FilledBy() {
		return field3FilledBy;
	}

	/**
	 * @param field3FilledBy the field3FilledBy to set
	 */
	public void setField3FilledBy(BqUserTypes field3FilledBy) {
		this.field3FilledBy = field3FilledBy;
	}

	/**
	 * @return the field3ToShowSupplier
	 */
	public boolean isField3ToShowSupplier() {
		return field3ToShowSupplier;
	}

	/**
	 * @param field3ToShowSupplier the field3ToShowSupplier to set
	 */
	public void setField3ToShowSupplier(boolean field3ToShowSupplier) {
		this.field3ToShowSupplier = field3ToShowSupplier;
	}

	/**
	 * @return the field3Required
	 */
	public boolean isField3Required() {
		return field3Required;
	}

	/**
	 * @param field3Required the field3Required to set
	 */
	public void setField3Required(boolean field3Required) {
		this.field3Required = field3Required;
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
	 * @return the field4FilledBy
	 */
	public BqUserTypes getField4FilledBy() {
		return field4FilledBy;
	}

	/**
	 * @param field4FilledBy the field4FilledBy to set
	 */
	public void setField4FilledBy(BqUserTypes field4FilledBy) {
		this.field4FilledBy = field4FilledBy;
	}

	/**
	 * @return the field4ToShowSupplier
	 */
	public boolean isField4ToShowSupplier() {
		return field4ToShowSupplier;
	}

	/**
	 * @param field4ToShowSupplier the field4ToShowSupplier to set
	 */
	public void setField4ToShowSupplier(boolean field4ToShowSupplier) {
		this.field4ToShowSupplier = field4ToShowSupplier;
	}

	/**
	 * @return the field4Required
	 */
	public boolean isField4Required() {
		return field4Required;
	}

	/**
	 * @param field4Required the field4Required to set
	 */
	public void setField4Required(boolean field4Required) {
		this.field4Required = field4Required;
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
	 * @return the field5FilledBy
	 */
	public BqUserTypes getField5FilledBy() {
		return field5FilledBy;
	}

	/**
	 * @param field5FilledBy the field5FilledBy to set
	 */
	public void setField5FilledBy(BqUserTypes field5FilledBy) {
		this.field5FilledBy = field5FilledBy;
	}

	/**
	 * @return the field5ToShowSupplier
	 */
	public boolean isField5ToShowSupplier() {
		return field5ToShowSupplier;
	}

	/**
	 * @param field5ToShowSupplier the field5ToShowSupplier to set
	 */
	public void setField5ToShowSupplier(boolean field5ToShowSupplier) {
		this.field5ToShowSupplier = field5ToShowSupplier;
	}

	/**
	 * @return the field5Required
	 */
	public boolean isField5Required() {
		return field5Required;
	}

	/**
	 * @param field5Required the field5Required to set
	 */
	public void setField5Required(boolean field5Required) {
		this.field5Required = field5Required;
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
	 * @return the field6FilledBy
	 */
	public BqUserTypes getField6FilledBy() {
		return field6FilledBy;
	}

	/**
	 * @param field6FilledBy the field6FilledBy to set
	 */
	public void setField6FilledBy(BqUserTypes field6FilledBy) {
		this.field6FilledBy = field6FilledBy;
	}

	/**
	 * @return the field6ToShowSupplier
	 */
	public boolean isField6ToShowSupplier() {
		return field6ToShowSupplier;
	}

	/**
	 * @param field6ToShowSupplier the field6ToShowSupplier to set
	 */
	public void setField6ToShowSupplier(boolean field6ToShowSupplier) {
		this.field6ToShowSupplier = field6ToShowSupplier;
	}

	/**
	 * @return the field6Required
	 */
	public boolean isField6Required() {
		return field6Required;
	}

	/**
	 * @param field6Required the field6Required to set
	 */
	public void setField6Required(boolean field6Required) {
		this.field6Required = field6Required;
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
	 * @return the field7FilledBy
	 */
	public BqUserTypes getField7FilledBy() {
		return field7FilledBy;
	}

	/**
	 * @param field7FilledBy the field7FilledBy to set
	 */
	public void setField7FilledBy(BqUserTypes field7FilledBy) {
		this.field7FilledBy = field7FilledBy;
	}

	/**
	 * @return the field7ToShowSupplier
	 */
	public boolean isField7ToShowSupplier() {
		return field7ToShowSupplier;
	}

	/**
	 * @param field7ToShowSupplier the field7ToShowSupplier to set
	 */
	public void setField7ToShowSupplier(boolean field7ToShowSupplier) {
		this.field7ToShowSupplier = field7ToShowSupplier;
	}

	/**
	 * @return the field7Required
	 */
	public boolean isField7Required() {
		return field7Required;
	}

	/**
	 * @param field7Required the field7Required to set
	 */
	public void setField7Required(boolean field7Required) {
		this.field7Required = field7Required;
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
	 * @return the field8FilledBy
	 */
	public BqUserTypes getField8FilledBy() {
		return field8FilledBy;
	}

	/**
	 * @param field8FilledBy the field8FilledBy to set
	 */
	public void setField8FilledBy(BqUserTypes field8FilledBy) {
		this.field8FilledBy = field8FilledBy;
	}

	/**
	 * @return the field8ToShowSupplier
	 */
	public boolean isField8ToShowSupplier() {
		return field8ToShowSupplier;
	}

	/**
	 * @param field8ToShowSupplier the field8ToShowSupplier to set
	 */
	public void setField8ToShowSupplier(boolean field8ToShowSupplier) {
		this.field8ToShowSupplier = field8ToShowSupplier;
	}

	/**
	 * @return the field8Required
	 */
	public boolean isField8Required() {
		return field8Required;
	}

	/**
	 * @param field8Required the field8Required to set
	 */
	public void setField8Required(boolean field8Required) {
		this.field8Required = field8Required;
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
	 * @return the field9FilledBy
	 */
	public BqUserTypes getField9FilledBy() {
		return field9FilledBy;
	}

	/**
	 * @param field9FilledBy the field9FilledBy to set
	 */
	public void setField9FilledBy(BqUserTypes field9FilledBy) {
		this.field9FilledBy = field9FilledBy;
	}

	/**
	 * @return the field9ToShowSupplier
	 */
	public boolean isField9ToShowSupplier() {
		return field9ToShowSupplier;
	}

	/**
	 * @param field9ToShowSupplier the field9ToShowSupplier to set
	 */
	public void setField9ToShowSupplier(boolean field9ToShowSupplier) {
		this.field9ToShowSupplier = field9ToShowSupplier;
	}

	/**
	 * @return the field9Required
	 */
	public boolean isField9Required() {
		return field9Required;
	}

	/**
	 * @param field9Required the field9Required to set
	 */
	public void setField9Required(boolean field9Required) {
		this.field9Required = field9Required;
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
	 * @return the field10FilledBy
	 */
	public BqUserTypes getField10FilledBy() {
		return field10FilledBy;
	}

	/**
	 * @param field10FilledBy the field10FilledBy to set
	 */
	public void setField10FilledBy(BqUserTypes field10FilledBy) {
		this.field10FilledBy = field10FilledBy;
	}

	/**
	 * @return the field10ToShowSupplier
	 */
	public boolean isField10ToShowSupplier() {
		return field10ToShowSupplier;
	}

	/**
	 * @param field10ToShowSupplier the field10ToShowSupplier to set
	 */
	public void setField10ToShowSupplier(boolean field10ToShowSupplier) {
		this.field10ToShowSupplier = field10ToShowSupplier;
	}

	/**
	 * @return the field10Required
	 */
	public boolean isField10Required() {
		return field10Required;
	}

	/**
	 * @param field10Required the field10Required to set
	 */
	public void setField10Required(boolean field10Required) {
		this.field10Required = field10Required;
	}

	/**
	 * @return the bqOrder
	 */
	public Integer getBqOrder() {
		return bqOrder;
	}

	/**
	 * @param bqOrder the bqOrder to set
	 */
	public void setBqOrder(Integer bqOrder) {
		this.bqOrder = bqOrder;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toLogString() {
		return "BqPojo [id=" + id + ", eventId=" + eventId + ", bqName=" + bqName + ", bqDesc=" + bqDesc + ", field1Label=" + field1Label + ", field1FilledBy=" + field1FilledBy + ", field1ToShowSupplier=" + field1ToShowSupplier + ", field1Required=" + field1Required + ", field2Label=" + field2Label + ", field2FilledBy=" + field2FilledBy + ", field2ToShowSupplier=" + field2ToShowSupplier + ", field2Required=" + field2Required + ", field3Label=" + field3Label + ", field3FilledBy=" + field3FilledBy + ", field3ToShowSupplier=" + field3ToShowSupplier + ", field3Required=" + field3Required + ", field4Label=" + field4Label + ", field4FilledBy=" + field4FilledBy + ", field4ToShowSupplier=" + field4ToShowSupplier + ", field4Required=" + field4Required + ", field5Label=" + field5Label + ", field5FilledBy=" + field5FilledBy + ", field5ToShowSupplier=" + field5ToShowSupplier + ", field5Required=" + field5Required + ", field6Label=" + field6Label + ", field6FilledBy=" + field6FilledBy + ", field6ToShowSupplier=" + field6ToShowSupplier + ", field6Required=" + field6Required + ", field7Label=" + field7Label + ", field7FilledBy=" + field7FilledBy + ", field7ToShowSupplier=" + field7ToShowSupplier + ", field7Required=" + field7Required + ", field8Label=" + field8Label + ", field8FilledBy=" + field8FilledBy + ", field8ToShowSupplier=" + field8ToShowSupplier + ", field8Required=" + field8Required + ", field9Label=" + field9Label + ", field9FilledBy=" + field9FilledBy + ", field9ToShowSupplier=" + field9ToShowSupplier + ", field9Required=" + field9Required + ", field10Label=" + field10Label + ", field10FilledBy=" + field10FilledBy + ", field10ToShowSupplier=" + field10ToShowSupplier + ", field10Required=" + field10Required + ", searchVal=" + searchVal + ", filterVal=" + filterVal + ", start=" + start + ", pageLength=" + pageLength + ", pageNo=" + pageNo + "]";
	}

}
