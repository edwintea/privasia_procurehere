package com.privasia.procurehere.core.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Size;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.privasia.procurehere.core.utils.CustomDateTimeSerializer;
import com.privasia.procurehere.core.utils.Global;

/**
 * @author ravi
 */
@Entity
@Table(name = "PROC_DO_ITEM")
public class DeliveryOrderItem implements Serializable {

	private static final long serialVersionUID = 7507552209736253159L;
	private static final Logger LOG = LogManager.getLogger(Global.ADMIN_LOG);

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

	@Column(name = "ITEM_QUANTITY", nullable = true, precision = 22, scale = 6)
	private BigDecimal quantity;

	@Column(name = "UNIT_PRICE", nullable = true, precision = 22, scale = 6)
	private BigDecimal unitPrice;

	@Column(name = "ITEM_DESCRIPTION", length = 2100)
	private String itemDescription;

	@ManyToOne(fetch = FetchType.LAZY, optional = false, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "DO_ID", nullable = false, foreignKey = @ForeignKey(name = "FK_DO_ITEM_INV_ID"))
	private DeliveryOrder deliverOrder;

	@ManyToOne(fetch = FetchType.LAZY, optional = true, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "PARENT_ID", nullable = true, foreignKey = @ForeignKey(name = "FK_DO_ITEM_PARENT_ID"))
	private DeliveryOrderItem parent;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "parent", cascade = { CascadeType.ALL }, orphanRemoval = true)
	@OrderBy("order")
	private List<DeliveryOrderItem> children;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DELIVERY_DATE")
	private Date deliveryDate;

	@ManyToOne(fetch = FetchType.LAZY, optional = true)
	@JoinColumn(name = "DELIVERY_ADDRESS_ID", nullable = true, foreignKey = @ForeignKey(name = "FK_DO_ITEM_DEL_ADD_ID"))
	private BuyerAddress deliveryAddress;

	@Size(max = 150, message = "{invoice.deliveryReceiver.length}")
	@Column(name = "DELIVERY_RECEIVER", length = 150)
	private String deliveryReceiver;

	@Column(name = "ITEM_TAX", nullable = true, precision = 11, scale = 6)
	private BigDecimal itemTax;

	@Digits(integer = 16, fraction = 8, message = "{totalamount.length.error}")
	@Column(name = "TOTAL_AMOUNT", nullable = true, precision = 22, scale = 6)
	private BigDecimal totalAmount;

	@Column(name = "TAX_AMOUNT", nullable = true, precision = 22, scale = 6)
	private BigDecimal taxAmount;

	@Column(name = "TOTAL_AMT_WITH_TAX", nullable = true, precision = 22, scale = 6)
	private BigDecimal totalAmountWithTax;

	@ManyToOne(fetch = FetchType.LAZY, optional = false, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "BUYER_ID", nullable = false, foreignKey = @ForeignKey(name = "FK_DO_ITEM_BUYER_ID"))
	private Buyer buyer;

	@ManyToOne(fetch = FetchType.LAZY, optional = true)
	@JoinColumn(name = "SUPPLIER_ID", nullable = true, foreignKey = @ForeignKey(name = "FK_DO_ITEM_SUP_ID"))
	private Supplier supplier;

	@ManyToOne(fetch = FetchType.LAZY, optional = true, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "UOM_ID", nullable = true, foreignKey = @ForeignKey(name = "FK_DO_ITM_UOM_ID"))
	private Uom unit;

	@ManyToOne(fetch = FetchType.LAZY, optional = true)
	@JoinColumn(name = "COST_CENTER_ID", nullable = true, foreignKey = @ForeignKey(name = "FK_DO_ITM_COSTCEN_ID"))
	private CostCenter costCenter;

	@ManyToOne(fetch = FetchType.LAZY, optional = true)
	@JoinColumn(name = "BUSINESS_UNIT_ID", nullable = true, foreignKey = @ForeignKey(name = "FK_DO_ITM_BUSS_UNIT_ID"))
	private BusinessUnit businessUnit;

	@Column(name = "FIELD1", nullable = true, length = 100)
	private String field1;

	@Column(name = "FIELD2", nullable = true, length = 100)
	private String field2;

	@Column(name = "FIELD3", nullable = true, length = 100)
	private String field3;

	@Column(name = "FIELD4", nullable = true, length = 100)
	private String field4;

	@Column(name = "FIELD5", nullable = true, length = 100)
	private String field5;

	@Column(name = "FIELD6", nullable = true, length = 100)
	private String field6;

	@Column(name = "FIELD7", nullable = true, length = 100)
	private String field7;

	@Column(name = "FIELD8", nullable = true, length = 100)
	private String field8;

	@Column(name = "FIELD9", nullable = true, length = 100)
	private String field9;

	@Column(name = "FIELD10", nullable = true, length = 100)
	private String field10;

	// to check item is free text or selected
	@Column(name = "FREE_ITEM_TEXT_ENTERED")
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean freeTextItemEntered;

	@ManyToOne(fetch = FetchType.LAZY, optional = true, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "PO_ID", nullable = true, foreignKey = @ForeignKey(name = "FK_DO_ITEM_PO_ID"))
	private Po po;

	@ManyToOne(fetch = FetchType.LAZY, optional = true, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "PO_ITEM_ID", nullable = true, foreignKey = @ForeignKey(name = "FK_DO_ITEM_PO_ITEM_ID"))
	private PoItem poItem;

	@Column(name = "DELIVERY_ADDRESS_TITLE", length = 128)
	private String deliveryAddressTitle;

	@Column(name = "DELIVERY_ADDRESS_LINE1", length = 250)
	private String deliveryAddressLine1;

	@Column(name = "DELIVERY_ADDRESS_LINE2", length = 250)
	private String deliveryAddressLine2;

	@Column(name = "DELIVERY_ADDRESS_CITY", length = 250)
	private String deliveryAddressCity;

	@Column(name = "DELIVERY_ADDRESS_STATE", length = 150)
	private String deliveryAddressState;

	@Column(name = "DELIVERY_ADDRESS_ZIP", length = 32)
	private String deliveryAddressZip;

	@Column(name = "DELIVERY_ADDRESS_COUNTRY", length = 128)
	private String deliveryAddressCountry;

	public DeliveryOrderItem createShallowCopy() {
		DeliveryOrderItem ic = new DeliveryOrderItem();
		ic.setDeliverOrder(getDeliverOrder());
		ic.setItemDescription(getItemDescription());
		ic.setQuantity(getQuantity());
		ic.setItemName(getItemName());
		ic.setUnitPrice(getUnitPrice());
		ic.setLevel(getLevel());
		ic.setOrder(getOrder());
		ic.setId(getId());
		ic.setItemTax(getItemTax());
		ic.setTotalAmount(getTotalAmount());
		ic.setTaxAmount(getTaxAmount());
		ic.setTotalAmountWithTax(getTotalAmountWithTax());
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

		ic.setFreeTextItemEntered(getFreeTextItemEntered());

		try {
			if (this.getUnit() != null) {
				ic.setUnit(this.getUnit().createShallowCopy());
			}
		} catch (Exception e) {
			LOG.error("" + e.getMessage(), e);
		}

		return ic;
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
	 * @return the deliverOrder
	 */
	public DeliveryOrder getDeliverOrder() {
		return deliverOrder;
	}

	/**
	 * @param deliverOrder the deliverOrder to set
	 */
	public void setDeliverOrder(DeliveryOrder deliverOrder) {
		this.deliverOrder = deliverOrder;
	}

	/**
	 * @return the parent
	 */
	public DeliveryOrderItem getParent() {
		return parent;
	}

	/**
	 * @param parent the parent to set
	 */
	public void setParent(DeliveryOrderItem parent) {
		this.parent = parent;
	}

	/**
	 * @return the children
	 */
	public List<DeliveryOrderItem> getChildren() {
		return children;
	}

	/**
	 * @param children the children to set
	 */
	public void setChildren(List<DeliveryOrderItem> children) {
		this.children = children;
	}

	/**
	 * @return the deliveryDate
	 */
	public Date getDeliveryDate() {
		return deliveryDate;
	}

	/**
	 * @param deliveryDate the deliveryDate to set
	 */
	public void setDeliveryDate(Date deliveryDate) {
		this.deliveryDate = deliveryDate;
	}

	/**
	 * @return the deliveryAddress
	 */
	public BuyerAddress getDeliveryAddress() {
		return deliveryAddress;
	}

	/**
	 * @param deliveryAddress the deliveryAddress to set
	 */
	public void setDeliveryAddress(BuyerAddress deliveryAddress) {
		this.deliveryAddress = deliveryAddress;
	}

	/**
	 * @return the deliveryReceiver
	 */
	public String getDeliveryReceiver() {
		return deliveryReceiver;
	}

	/**
	 * @param deliveryReceiver the deliveryReceiver to set
	 */
	public void setDeliveryReceiver(String deliveryReceiver) {
		this.deliveryReceiver = deliveryReceiver;
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
	 * @return the taxAmount
	 */
	public BigDecimal getTaxAmount() {
		return taxAmount;
	}

	/**
	 * @param taxAmount the taxAmount to set
	 */
	public void setTaxAmount(BigDecimal taxAmount) {
		this.taxAmount = taxAmount;
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
	 * @return the buyer
	 */
	public Buyer getBuyer() {
		return buyer;
	}

	/**
	 * @param buyer the buyer to set
	 */
	public void setBuyer(Buyer buyer) {
		this.buyer = buyer;
	}

	/**
	 * @return the unit
	 */
	public Uom getUnit() {
		return unit;
	}

	/**
	 * @param unit the unit to set
	 */
	public void setUnit(Uom unit) {
		this.unit = unit;
	}

	/**
	 * @return the costCenter
	 */
	public CostCenter getCostCenter() {
		return costCenter;
	}

	/**
	 * @param costCenter the costCenter to set
	 */
	public void setCostCenter(CostCenter costCenter) {
		this.costCenter = costCenter;
	}

	/**
	 * @return the businessUnit
	 */
	public BusinessUnit getBusinessUnit() {
		return businessUnit;
	}

	/**
	 * @param businessUnit the businessUnit to set
	 */
	public void setBusinessUnit(BusinessUnit businessUnit) {
		this.businessUnit = businessUnit;
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
	 * @return the freeTextItemEntered
	 */
	public Boolean getFreeTextItemEntered() {
		return freeTextItemEntered;
	}

	/**
	 * @param freeTextItemEntered the freeTextItemEntered to set
	 */
	public void setFreeTextItemEntered(Boolean freeTextItemEntered) {
		this.freeTextItemEntered = freeTextItemEntered;
	}

	/**
	 * @return the po
	 */
	public Po getPo() {
		return po;
	}

	/**
	 * @param po the po to set
	 */
	public void setPo(Po po) {
		this.po = po;
	}

	/**
	 * @return the poItem
	 */
	public PoItem getPoItem() {
		return poItem;
	}

	/**
	 * @param poItem the poItem to set
	 */
	public void setPoItem(PoItem poItem) {
		this.poItem = poItem;
	}

	/**
	 * @return the deliveryAddressTitle
	 */
	public String getDeliveryAddressTitle() {
		return deliveryAddressTitle;
	}

	/**
	 * @param deliveryAddressTitle the deliveryAddressTitle to set
	 */
	public void setDeliveryAddressTitle(String deliveryAddressTitle) {
		this.deliveryAddressTitle = deliveryAddressTitle;
	}

	/**
	 * @return the deliveryAddressLine1
	 */
	public String getDeliveryAddressLine1() {
		return deliveryAddressLine1;
	}

	/**
	 * @param deliveryAddressLine1 the deliveryAddressLine1 to set
	 */
	public void setDeliveryAddressLine1(String deliveryAddressLine1) {
		this.deliveryAddressLine1 = deliveryAddressLine1;
	}

	/**
	 * @return the deliveryAddressLine2
	 */
	public String getDeliveryAddressLine2() {
		return deliveryAddressLine2;
	}

	/**
	 * @param deliveryAddressLine2 the deliveryAddressLine2 to set
	 */
	public void setDeliveryAddressLine2(String deliveryAddressLine2) {
		this.deliveryAddressLine2 = deliveryAddressLine2;
	}

	/**
	 * @return the deliveryAddressCity
	 */
	public String getDeliveryAddressCity() {
		return deliveryAddressCity;
	}

	/**
	 * @param deliveryAddressCity the deliveryAddressCity to set
	 */
	public void setDeliveryAddressCity(String deliveryAddressCity) {
		this.deliveryAddressCity = deliveryAddressCity;
	}

	/**
	 * @return the deliveryAddressState
	 */
	public String getDeliveryAddressState() {
		return deliveryAddressState;
	}

	/**
	 * @param deliveryAddressState the deliveryAddressState to set
	 */
	public void setDeliveryAddressState(String deliveryAddressState) {
		this.deliveryAddressState = deliveryAddressState;
	}

	/**
	 * @return the deliveryAddressZip
	 */
	public String getDeliveryAddressZip() {
		return deliveryAddressZip;
	}

	/**
	 * @param deliveryAddressZip the deliveryAddressZip to set
	 */
	public void setDeliveryAddressZip(String deliveryAddressZip) {
		this.deliveryAddressZip = deliveryAddressZip;
	}

	/**
	 * @return the deliveryAddressCountry
	 */
	public String getDeliveryAddressCountry() {
		return deliveryAddressCountry;
	}

	/**
	 * @param deliveryAddressCountry the deliveryAddressCountry to set
	 */
	public void setDeliveryAddressCountry(String deliveryAddressCountry) {
		this.deliveryAddressCountry = deliveryAddressCountry;
	}

	/**
	 * @return the itemTax
	 */
	public BigDecimal getItemTax() {
		return itemTax;
	}

	/**
	 * @param itemTax the itemTax to set
	 */
	public void setItemTax(BigDecimal itemTax) {
		this.itemTax = itemTax;
	}

	/**
	 * @return the supplier
	 */
	public Supplier getSupplier() {
		return supplier;
	}

	/**
	 * @param supplier the supplier to set
	 */
	public void setSupplier(Supplier supplier) {
		this.supplier = supplier;
	}

	public String toLogString() {
		return "DeliveryOrderItem [itemName=" + itemName + ", level=" + level + ", order=" + order + ", quantity=" + quantity + ", unitPrice=" + unitPrice + ", itemTax=" + itemTax + "]";
	}

	
}
