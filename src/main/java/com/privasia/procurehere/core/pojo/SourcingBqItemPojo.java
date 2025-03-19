package com.privasia.procurehere.core.pojo;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.validation.constraints.DecimalMax;

import com.privasia.procurehere.core.entity.SourcingFormRequestBqItem;
import com.privasia.procurehere.core.enums.PricingTypes;

/**
 * @author pooja
 */
/**
 * @author sana
 */
public class SourcingBqItemPojo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 460077298361509725L;

	private String id;
	private String itemName;
	private Integer level;
	private Integer order;
	private BigDecimal quantity;
	private BigDecimal unitPrice;
	private String uom;
	private String bq;
	private String parent;
	private String formId;
	private String itemDescription;
	private PricingTypes priceType;

	private String searchVal;
	private String filterVal;

	private Integer start;
	private Integer pageLength;
	private Integer pageNo;

	private String field1;

	private String field2;

	private String field3;

	private String field4;

	private String field5;

	private String field6;

	private String field7;

	private String field8;

	private String field9;

	private String field10;

	private BigDecimal unitBudgetPrice;

	public SourcingBqItemPojo() {
	}

	public SourcingBqItemPojo(Integer level, Integer order) {
		this.level = level;
		this.order = order;
	}

	public SourcingBqItemPojo(SourcingFormRequestBqItem sourcingBqItem) {
		this.itemName = sourcingBqItem.getId();
		this.itemName = sourcingBqItem.getItemName();
		this.level = sourcingBqItem.getLevel();
		this.order = sourcingBqItem.getOrder();
		this.quantity = sourcingBqItem.getQuantity();
		this.unitPrice = sourcingBqItem.getUnitPrice();
		this.uom = sourcingBqItem.getUom() != null ? sourcingBqItem.getUom().getUom() : "";
		this.bq = sourcingBqItem.getBq().getId();
		this.parent = sourcingBqItem.getParent() != null ? sourcingBqItem.getParent().getId() : null;
		this.formId = sourcingBqItem.getSourcingFormRequest() != null ? sourcingBqItem.getSourcingFormRequest().getId() : null;
		this.itemDescription = sourcingBqItem.getItemDescription();
		this.priceType = sourcingBqItem.getPriceType();
		this.field1 = sourcingBqItem.getField1();
		this.field2 = sourcingBqItem.getField2();
		this.field3 = sourcingBqItem.getField3();
		this.field4 = sourcingBqItem.getField4();
		this.field5 = sourcingBqItem.getField5();
		this.field6 = sourcingBqItem.getField6();
		this.field7 = sourcingBqItem.getField7();
		this.field8 = sourcingBqItem.getField8();
		this.field9 = sourcingBqItem.getField9();
		this.field10 = sourcingBqItem.getField10();
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
	 * @return the itemName
	 */
	public String getItemName() {
		return itemName;
	}

	/**
	 * @param itemName the itemName to set
	 */
	public void setItemName(String itemName) {
		this.itemName = itemName;
	}

	/**
	 * @return the level
	 */
	public Integer getLevel() {
		return level;
	}

	/**
	 * @param level the level to set
	 */
	public void setLevel(Integer level) {
		this.level = level;
	}

	/**
	 * @return the order
	 */
	public Integer getOrder() {
		return order;
	}

	/**
	 * @param order the order to set
	 */
	public void setOrder(Integer order) {
		this.order = order;
	}

	/**
	 * @return the quantity
	 */
	public BigDecimal getQuantity() {
		return quantity;
	}

	/**
	 * @param quantity the quantity to set
	 */
	public void setQuantity(BigDecimal quantity) {
		this.quantity = quantity;
	}

	/**
	 * @return the unitPrice
	 */
	public BigDecimal getUnitPrice() {
		return unitPrice;
	}

	/**
	 * @param unitPrice the unitPrice to set
	 */
	public void setUnitPrice(BigDecimal unitPrice) {
		this.unitPrice = unitPrice;
	}

	/**
	 * @return the uom
	 */
	public String getUom() {
		return uom;
	}

	/**
	 * @param uom the uom to set
	 */
	public void setUom(String uom) {
		this.uom = uom;
	}

	/**
	 * @return the bq
	 */
	public String getBq() {
		return bq;
	}

	/**
	 * @param bq the bq to set
	 */
	public void setBq(String bq) {
		this.bq = bq;
	}

	/**
	 * @return the parent
	 */
	public String getParent() {
		return parent;
	}

	/**
	 * @param parent the parent to set
	 */
	public void setParent(String parent) {
		this.parent = parent;
	}

	/**
	 * @return the formId
	 */
	public String getFormId() {
		return formId;
	}

	/**
	 * @param formId the formId to set
	 */
	public void setFormId(String formId) {
		this.formId = formId;
	}

	/**
	 * @return the itemDescription
	 */
	public String getItemDescription() {
		return itemDescription;
	}

	/**
	 * @param itemDescription the itemDescription to set
	 */
	public void setItemDescription(String itemDescription) {
		this.itemDescription = itemDescription;
	}

	/**
	 * @return the priceType
	 */
	public PricingTypes getPriceType() {
		return priceType;
	}

	/**
	 * @param priceType the priceType to set
	 */
	public void setPriceType(PricingTypes priceType) {
		this.priceType = priceType;
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
	 * @return the field1
	 */
	public String getField1() {
		return field1;
	}

	/**
	 * @param field1 the field1 to set
	 */
	public void setField1(String field1) {
		this.field1 = field1;
	}

	/**
	 * @return the field2
	 */
	public String getField2() {
		return field2;
	}

	/**
	 * @param field2 the field2 to set
	 */
	public void setField2(String field2) {
		this.field2 = field2;
	}

	/**
	 * @return the field3
	 */
	public String getField3() {
		return field3;
	}

	/**
	 * @param field3 the field3 to set
	 */
	public void setField3(String field3) {
		this.field3 = field3;
	}

	/**
	 * @return the field4
	 */
	public String getField4() {
		return field4;
	}

	/**
	 * @param field4 the field4 to set
	 */
	public void setField4(String field4) {
		this.field4 = field4;
	}

	/**
	 * @return the field5
	 */
	public String getField5() {
		return field5;
	}

	/**
	 * @param field5 the field5 to set
	 */
	public void setField5(String field5) {
		this.field5 = field5;
	}

	/**
	 * @return the field6
	 */
	public String getField6() {
		return field6;
	}

	/**
	 * @param field6 the field6 to set
	 */
	public void setField6(String field6) {
		this.field6 = field6;
	}

	/**
	 * @return the field7
	 */
	public String getField7() {
		return field7;
	}

	/**
	 * @param field7 the field7 to set
	 */
	public void setField7(String field7) {
		this.field7 = field7;
	}

	/**
	 * @return the field8
	 */
	public String getField8() {
		return field8;
	}

	/**
	 * @param field8 the field8 to set
	 */
	public void setField8(String field8) {
		this.field8 = field8;
	}

	/**
	 * @return the field9
	 */
	public String getField9() {
		return field9;
	}

	/**
	 * @param field9 the field9 to set
	 */
	public void setField9(String field9) {
		this.field9 = field9;
	}

	/**
	 * @return the field10
	 */
	public String getField10() {
		return field10;
	}

	/**
	 * @param field10 the field10 to set
	 */
	public void setField10(String field10) {
		this.field10 = field10;
	}

	/**
	 * @return the unitBudgetPrice
	 */
	public BigDecimal getUnitBudgetPrice() {
		return unitBudgetPrice;
	}

	/**
	 * @param unitBudgetPrice the unitBudgetPrice to set
	 */
	public void setUnitBudgetPrice(BigDecimal unitBudgetPrice) {
		this.unitBudgetPrice = unitBudgetPrice;
	}

	@Override
	public String toString() {
		return "SourcingBqItemPojo [id=" + id + ", itemName=" + itemName + ", level=" + level + ", order=" + order + ", quantity=" + quantity + ", unitPrice=" + unitPrice + ", uom=" + uom + ", bq=" + bq + ", parent=" + parent + ", formId=" + formId + ", itemDescription=" + itemDescription + ", priceType=" + priceType + ", searchVal=" + searchVal + ", filterVal=" + filterVal + ", start=" + start + ", pageLength=" + pageLength + ", pageNo=" + pageNo + "]";
	}
}
