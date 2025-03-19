package com.privasia.procurehere.core.pojo;

import com.privasia.procurehere.core.entity.RftBqItem;
import com.privasia.procurehere.core.enums.PricingTypes;
import com.privasia.procurehere.core.enums.UnitPricingTypes;

import java.io.Serializable;
import java.math.BigDecimal;

public class SorItemPojo implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 1901153985924663302L;

	private String id;
	private String itemName;
	private Integer level;
	private Integer order;
	private BigDecimal quantity;
	private BigDecimal unitPrice;
	private String uom;
	private String sor;
	private String parent;
	private String rftEvent;
	private String itemDescription;
	private PricingTypes priceType;
	private UnitPricingTypes unitPriceType;
	private String field1;


	private String searchVal;
	private String filterVal;

	private Integer start;
	private Integer pageLength;
	private Integer pageNo;

	public SorItemPojo() {
	}

	public SorItemPojo(Integer level, Integer order) {
		this.level = level;
		this.order = order;
	}


	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getItemName() {
		return itemName;
	}

	public void setItemName(String itemName) {
		this.itemName = itemName;
	}

	public Integer getLevel() {
		return level;
	}

	public void setLevel(Integer level) {
		this.level = level;
	}

	public Integer getOrder() {
		return order;
	}

	public void setOrder(Integer order) {
		this.order = order;
	}

	public BigDecimal getQuantity() {
		return quantity;
	}

	public void setQuantity(BigDecimal quantity) {
		this.quantity = quantity;
	}

	public BigDecimal getUnitPrice() {
		return unitPrice;
	}

	public void setUnitPrice(BigDecimal unitPrice) {
		this.unitPrice = unitPrice;
	}

	public String getUom() {
		return uom;
	}

	public void setUom(String uom) {
		this.uom = uom;
	}

	public String getSor() {
		return sor;
	}

	public void setSor(String sor) {
		this.sor = sor;
	}

	public String getParent() {
		return parent;
	}

	public void setParent(String parent) {
		this.parent = parent;
	}

	public String getRftEvent() {
		return rftEvent;
	}

	public void setRftEvent(String rftEvent) {
		this.rftEvent = rftEvent;
	}

	public String getItemDescription() {
		return itemDescription;
	}

	public void setItemDescription(String itemDescription) {
		this.itemDescription = itemDescription;
	}

	public PricingTypes getPriceType() {
		return priceType;
	}

	public void setPriceType(PricingTypes priceType) {
		this.priceType = priceType;
	}

	public UnitPricingTypes getUnitPriceType() {
		return unitPriceType;
	}

	public void setUnitPriceType(UnitPricingTypes unitPriceType) {
		this.unitPriceType = unitPriceType;
	}

	public String getField1() {
		return field1;
	}

	public void setField1(String field1) {
		this.field1 = field1;
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
