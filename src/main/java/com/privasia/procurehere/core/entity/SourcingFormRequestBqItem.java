/**
 * 
 */
package com.privasia.procurehere.core.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.privasia.procurehere.core.dao.SourcingFormRequestBqItemDao;
import com.privasia.procurehere.core.dao.UomDao;
import com.privasia.procurehere.core.enums.PricingTypes;
import com.privasia.procurehere.core.enums.TaxType;
import com.privasia.procurehere.core.enums.UnitPricingTypes;

/**
 * @author Pooja
 */
@Entity
@Table(name = "PROC_SOURCING_FORM_BQ_ITEM_REQ")
public class SourcingFormRequestBqItem implements Serializable {

	private static final long serialVersionUID = 8071104391034850059L;

	@Id
	@GenericGenerator(name = "idGen", strategy = "uuid.hex")
	@GeneratedValue(generator = "idGen")
	@Column(name = "ID", length = 64)
	private String id;

	@Column(name = "ITEM_NAME", nullable = false, length = 250)
	private String itemName;

	@Column(name = "ITEM_LEVEL", length = 2, nullable = false)
	private Integer level = 0;

	@Column(name = "SUB_ORDER", length = 2, nullable = false)
	private Integer order = 0;

	@ManyToOne(optional = true, cascade = { CascadeType.PERSIST, CascadeType.REFRESH })
	@JoinColumn(name = "UOM_ID", nullable = true)
	private Uom uom;

	@Column(name = "UNIT_QUANTITY", precision = 16, scale = 4, nullable = true)
	private BigDecimal quantity;

	@Column(name = "UNIT_PRICE", precision = 16, scale = 4)
	private BigDecimal unitPrice;

	@Enumerated(EnumType.STRING)
	@Column(name = "PRICE_TYPE", nullable = true)
	private UnitPricingTypes unitPriceType;

	@Size(max = 1100)
	@Column(name = "ITEM_DESCRIPTION", nullable = true, length = 1100)
	private String itemDescription;

	@Enumerated(EnumType.STRING)
	@Column(name = "UNIT_PRICE_TYPE", nullable = true)
	private PricingTypes priceType;

	@DecimalMax("9999999999999999.9999")
	@Column(name = "TOTAL_AMOUNT", precision = 16, scale = 4, nullable = true)
	private BigDecimal totalAmount;

	@DecimalMax("9999999999999999.9999")
	@Column(name = "TAX", precision = 16, scale = 4, nullable = true)
	private BigDecimal tax;

	@Enumerated(EnumType.STRING)
	@Column(name = "TAX_TYPE")
	private TaxType taxType;

	@DecimalMax("9999999999999999.9999")
	@Column(name = "TOTAL_AMOUNT_WITH_TAX", precision = 16, scale = 4, nullable = true)
	private BigDecimal totalAmountWithTax;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "FORM_REQ_ID", nullable = true, foreignKey = @ForeignKey(name = "FK_SF_BQ_ITEM_FORM_REQ_ID"))
	private SourcingFormRequest sourcingFormRequest;

	@ManyToOne(optional = false, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "BQ_ID", nullable = false, foreignKey = @ForeignKey(name = "FK_BQ_FORM_REQ_ID"))
	private SourcingFormRequestBq bq;

	@ManyToOne(optional = true, cascade = { CascadeType.MERGE })
	@JoinColumn(name = "PARENT_ID", nullable = true, foreignKey = @ForeignKey(name = "FK_SF_BQ_PARENT"))
	private SourcingFormRequestBqItem parent;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "parent")
	@OrderBy("order")
	private List<SourcingFormRequestBqItem> children;

	@Column(name = "FIELD_1", nullable = true, length = 100)
	private String field1;

	@Column(name = "FIELD_2", nullable = true, length = 100)
	private String field2;

	@Column(name = "FIELD_3", nullable = true, length = 100)
	private String field3;

	@Column(name = "FIELD_4", nullable = true, length = 100)
	private String field4;

	@Column(name = "FIELD_5", nullable = true, length = 100)
	private String field5;

	@Column(name = "FIELD_6", nullable = true, length = 100)
	private String field6;

	@Column(name = "FIELD_7", nullable = true, length = 100)
	private String field7;

	@Column(name = "FIELD_8", nullable = true, length = 100)
	private String field8;

	@Column(name = "FIELD_9", nullable = true, length = 100)
	private String field9;

	@Column(name = "FIELD_10", nullable = true, length = 100)
	private String field10;

	@DecimalMax("9999999999999999.9999")
	@Column(name = "UNIT_BUDGET_PRICE", precision = 16, scale = 4, nullable = true)
	private BigDecimal unitBudgetPrice;

	@Transient
	private String formId;

	@Transient
	private String taxDescription;

	@Transient
	private BigDecimal additionalTax;

	@Transient
	private String bqId;

	@Transient
	private List<String> columnTitles;

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
	 * @return the uom
	 */
	public Uom getUom() {
		return uom;
	}

	/**
	 * @param uom the uom to set
	 */
	public void setUom(Uom uom) {
		this.uom = uom;
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
	 * @return the totalAmount
	 */
	public BigDecimal getTotalAmount() {
		return totalAmount;
	}

	/**
	 * @param totalAmount the totalAmount to set
	 */
	public void setTotalAmount(BigDecimal totalAmount) {
		this.totalAmount = totalAmount;
	}

	/**
	 * @return the tax
	 */
	public BigDecimal getTax() {
		return tax;
	}

	/**
	 * @param tax the tax to set
	 */
	public void setTax(BigDecimal tax) {
		this.tax = tax;
	}

	/**
	 * @return the taxType
	 */
	public TaxType getTaxType() {
		return taxType;
	}

	/**
	 * @param taxType the taxType to set
	 */
	public void setTaxType(TaxType taxType) {
		this.taxType = taxType;
	}

	/**
	 * @return the totalAmountWithTax
	 */
	public BigDecimal getTotalAmountWithTax() {
		return totalAmountWithTax;
	}

	/**
	 * @param totalAmountWithTax the totalAmountWithTax to set
	 */
	public void setTotalAmountWithTax(BigDecimal totalAmountWithTax) {
		this.totalAmountWithTax = totalAmountWithTax;
	}

	/**
	 * @return the serialversionuid
	 */
	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	/**
	 * @return the sourcingFormRequest
	 */
	public SourcingFormRequest getSourcingFormRequest() {
		return sourcingFormRequest;
	}

	/**
	 * @param sourcingFormRequest the sourcingFormRequest to set
	 */
	public void setSourcingFormRequest(SourcingFormRequest sourcingFormRequest) {
		this.sourcingFormRequest = sourcingFormRequest;
	}

	/**
	 * @return the bq
	 */
	public SourcingFormRequestBq getBq() {
		return bq;
	}

	/**
	 * @param bq the bq to set
	 */
	public void setBq(SourcingFormRequestBq bq) {
		this.bq = bq;
	}

	/**
	 * @return the parent
	 */
	public SourcingFormRequestBqItem getParent() {
		return parent;
	}

	/**
	 * @param parent the parent to set
	 */
	public void setParent(SourcingFormRequestBqItem parent) {
		this.parent = parent;
	}

	/**
	 * @return the children
	 */
	public List<SourcingFormRequestBqItem> getChildren() {
		return children;
	}

	/**
	 * @return the unitPriceType
	 */
	public UnitPricingTypes getUnitPriceType() {
		return unitPriceType;
	}

	/**
	 * @param unitPriceType the unitPriceType to set
	 */
	public void setUnitPriceType(UnitPricingTypes unitPriceType) {
		this.unitPriceType = unitPriceType;
	}

	/**
	 * @param children the children to set
	 */
	public void setChildren(List<SourcingFormRequestBqItem> children) {
		this.children = children;
	}

	/**
	 * @return the columnTitles
	 */
	public List<String> getColumnTitles() {
		return columnTitles;
	}

	/**
	 * @param columnTitles the columnTitles to set
	 */
	public void setColumnTitles(List<String> columnTitles) {
		this.columnTitles = columnTitles;
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

	public SourcingFormRequestBqItem createSearchShallowCopy() {

		SourcingFormRequestBqItem ic = new SourcingFormRequestBqItem();
		ic.setId(getId());
		ic.setItemDescription(getItemDescription());
		if (getUom() != null) {
			ic.setUom(getUom().createShallowCopy());
		}
		ic.setQuantity(getQuantity());
		ic.setItemName(getItemName());
		ic.setUnitPrice(getUnitPrice());
		ic.setPriceType(getPriceType());
		ic.setLevel(getLevel());
		ic.setOrder(getOrder());
		ic.setBq(getBq());
		if (getParent() != null) {
			SourcingFormRequestBqItem parent = new SourcingFormRequestBqItem();
			parent.setId(getParent().getId());
			ic.setParent(parent);
		}
		ic.setUnitBudgetPrice(getUnitBudgetPrice());
		ic.setField1(getField1());
		ic.setField2(getField2());
		ic.setField3(getField3());
		ic.setField4(getField4());
		ic.setField5(getField5());
		ic.setField6(getField6());
		ic.setField7(getField7());
		ic.setField8(getField8());
		ic.setField9(getField9());
		ic.setField10(getField10());

		return ic;
	}

	public List<SourcingFormRequestBqItem> copyBqItem(SourcingFormRequestBq bq, SourcingFormRequestBqItemDao bqDao, UomDao uDao) {
		List<SourcingFormRequestBqItem> itemList = bq.getBqItems();
		List<SourcingFormRequestBqItem> newItemList = new ArrayList<>();
		for (SourcingFormRequestBqItem item : itemList) {
			SourcingFormRequestBqItem item1 = item.createShallowCopy1(uDao);
			item1 = bqDao.save(item1);
			newItemList.add(item1);
		}
		return newItemList;
	}

	public SourcingFormRequestBqItem createShallowCopy() {
		SourcingFormRequestBqItem ic = new SourcingFormRequestBqItem();
		ic.setId(getId());
		ic.setItemDescription(getItemDescription());
		if (getUom() != null) {
			ic.setUom(getUom().createShallowCopy());
		}
		ic.setQuantity(getQuantity());
		ic.setItemName(getItemName());
		ic.setUnitPrice(getUnitPrice());
		ic.setPriceType(getPriceType());
		ic.setLevel(getLevel());
		ic.setOrder(getOrder());
		ic.setBq(getBq());
		ic.setUnitBudgetPrice(getUnitBudgetPrice());
		ic.setField1(getField1());
		ic.setField2(getField2());
		ic.setField3(getField3());
		ic.setField4(getField4());
		ic.setField5(getField5());
		ic.setField6(getField6());
		ic.setField7(getField7());
		ic.setField8(getField8());
		ic.setField9(getField9());
		ic.setField10(getField10());

		return ic;
	}

	public SourcingFormRequestBqItem createShallowCopy1(UomDao uomDao) {
		SourcingFormRequestBqItem ic = new SourcingFormRequestBqItem();
		ic.setItemDescription(getItemDescription());

		if (getUom() != null) {
			Uom uom = getUom().createShallowCopy();
			uom = uomDao.save(uom);
			ic.setUom(uom);
		}

		ic.setQuantity(getQuantity());
		ic.setItemName(getItemName());
		ic.setUnitPrice(getUnitPrice());
		ic.setPriceType(getPriceType());
		ic.setUnitPriceType(getUnitPriceType());
		ic.setLevel(getLevel());
		ic.setOrder(getOrder());
		ic.setBq(getBq());
		ic.setSourcingFormRequest(getSourcingFormRequest());
		ic.setUnitBudgetPrice(getUnitBudgetPrice());
		ic.setField1(getField1());
		ic.setField2(getField2());
		ic.setField3(getField3());
		ic.setField4(getField4());
		ic.setField5(getField5());
		ic.setField6(getField6());
		ic.setField7(getField7());
		ic.setField8(getField8());
		ic.setField9(getField9());
		ic.setField10(getField10());
		return ic;
	}

	public RfpBqItem copyForRfp(SourcingFormRequestBqItem newBq) {
		RfpBqItem bq = new RfpBqItem();
		bq.setItemName(newBq.getItemName());
		bq.setLevel(newBq.getLevel());
		bq.setOrder(newBq.getOrder());
		bq.setUom(newBq.getUom());
		bq.setQuantity(newBq.getQuantity());
		bq.setUnitPrice(newBq.getUnitPrice());
		bq.setItemDescription(newBq.getItemDescription());
		bq.setPriceType(newBq.getPriceType());
		bq.setUnitPriceType(newBq.getUnitPriceType());
		bq.setTotalAmount(newBq.getTotalAmount());
		bq.setTax(newBq.getTax());
		bq.setTaxType(newBq.getTaxType());
		bq.setTotalAmountWithTax(newBq.getTotalAmountWithTax());
		bq.setField1(getField1());
		bq.setField2(getField2());
		bq.setField3(getField3());
		bq.setField4(getField4());
		bq.setField5(getField5());
		bq.setField6(getField6());
		bq.setField7(getField7());
		bq.setField8(getField8());
		bq.setField9(getField9());
		bq.setField10(getField10());
		return bq;
	}

	public RfqBqItem copyForRfq(SourcingFormRequestBqItem newBq) {
		RfqBqItem bq = new RfqBqItem();
		bq.setItemName(newBq.getItemName());
		bq.setLevel(newBq.getLevel());
		bq.setOrder(newBq.getOrder());
		bq.setUom(newBq.getUom());
		bq.setQuantity(newBq.getQuantity());
		bq.setUnitPrice(newBq.getUnitPrice());
		bq.setItemDescription(newBq.getItemDescription());
		bq.setPriceType(newBq.getPriceType());
		bq.setTotalAmount(newBq.getTotalAmount());
		bq.setUnitPriceType(getUnitPriceType());
		bq.setTax(newBq.getTax());
		bq.setTaxType(newBq.getTaxType());
		bq.setTotalAmountWithTax(newBq.getTotalAmountWithTax());
		bq.setField1(getField1());
		bq.setField2(getField2());
		bq.setField3(getField3());
		bq.setField4(getField4());
		bq.setField5(getField5());
		bq.setField6(getField6());
		bq.setField7(getField7());
		bq.setField8(getField8());
		bq.setField9(getField9());
		bq.setField10(getField10());

		return bq;
	}

	public RfaBqItem copyForRfa(SourcingFormRequestBqItem newBq) {
		RfaBqItem bq = new RfaBqItem();
		bq.setItemName(newBq.getItemName());
		bq.setLevel(newBq.getLevel());
		bq.setOrder(newBq.getOrder());
		bq.setUom(newBq.getUom());
		bq.setQuantity(newBq.getQuantity());
		bq.setUnitPrice(newBq.getUnitPrice());
		bq.setUnitPriceType(newBq.getUnitPriceType());
		bq.setItemDescription(newBq.getItemDescription());
		bq.setPriceType(newBq.getPriceType());
		bq.setTotalAmount(newBq.getTotalAmount());
		bq.setTax(newBq.getTax());
		bq.setTaxType(newBq.getTaxType());
		bq.setTotalAmountWithTax(newBq.getTotalAmountWithTax());
		bq.setField1(getField1());
		bq.setField2(getField2());
		bq.setField3(getField3());
		bq.setField4(getField4());
		bq.setField5(getField5());
		bq.setField6(getField6());
		bq.setField7(getField7());
		bq.setField8(getField8());
		bq.setField9(getField9());
		bq.setField10(getField10());

		return bq;
	}

	public RftBqItem copyForRft(SourcingFormRequestBqItem newBq) {
		RftBqItem bq = new RftBqItem();
		bq.setItemName(newBq.getItemName());
		bq.setLevel(newBq.getLevel());
		bq.setOrder(newBq.getOrder());
		bq.setUom(newBq.getUom());
		bq.setQuantity(newBq.getQuantity());
		bq.setUnitPrice(newBq.getUnitPrice());
		bq.setItemDescription(newBq.getItemDescription());
		bq.setPriceType(newBq.getPriceType());
		bq.setTotalAmount(newBq.getTotalAmount());
		bq.setTax(newBq.getTax());
		bq.setTaxType(newBq.getTaxType());
		bq.setTotalAmountWithTax(newBq.getTotalAmountWithTax());
		bq.setField1(getField1());
		bq.setField2(getField2());
		bq.setField3(getField3());
		bq.setField4(getField4());
		bq.setField5(getField5());
		bq.setField6(getField6());
		bq.setField7(getField7());
		bq.setField8(getField8());
		bq.setField9(getField9());
		bq.setField10(getField10());

		return bq;
	}

	public SourcingFormRequestBqItem createMobileShallowCopy() {

		SourcingFormRequestBqItem item = new SourcingFormRequestBqItem();
		item.setId(getId());
		item.setItemName(getItemName());
		item.setItemDescription(getItemDescription());
		item.setQuantity(getQuantity());
		item.setUom(getUom() != null ? getUom().createMobileShallowCopy() : null);
		item.setLevel(getLevel());
		item.setOrder(getOrder());
		item.setField1(getField1());
		item.setField2(getField2());
		item.setField3(getField3());
		item.setField4(getField4());
		item.setField5(getField5());
		item.setField6(getField6());
		item.setField7(getField7());
		item.setField8(getField8());
		item.setField9(getField9());
		item.setField10(getField10());
		return item;

	}

}
