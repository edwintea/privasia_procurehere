/**
 * 
 */
package com.privasia.procurehere.core.entity;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

/**
 * @author ravi
 */
@Entity
@Table(name = "PROC_SUP_FORM_ITEM_OPT", indexes = { @Index(columnList = "TENANT_ID", name = "INDEX_SFO_TENANT_ID") })
public class SupplierFormItemOption implements Comparable<SupplierFormItemOption>, Serializable {

	private static final long serialVersionUID = 6998981647521624244L;

	@Id
	@GenericGenerator(name = "idGen", strategy = "uuid.hex")
	@GeneratedValue(generator = "idGen")
	@Column(name = "ID", length = 64)
	private String id;

	@Column(name = "OPTION_VALUE", length = 250)
	private String value;

	@Column(name = "OPTION_ORDER", length = 2)
	private Integer order;

	@Column(name = "SCORING", length = 3)
	private Integer scoring;

	@ManyToOne(optional = false, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "ITEM_ID", nullable = false, foreignKey = @ForeignKey(name = "FK_SFI_OPT_ITM_ID"))
	private SupplierFormItem supplierFormItem;

	@Column(name = "TENANT_ID", nullable = false, length = 64)
	private String tenantId;

	public SupplierFormItemOption copyFrom(SupplierFormItem newCqItem, String tenantId) {
		SupplierFormItemOption newOption = new SupplierFormItemOption();
		newOption.setOrder(getOrder());
		newOption.setScoring(getScoring());
		newOption.setValue(getValue());
		newOption.setSupplierFormItem(newCqItem);
		newOption.setTenantId(tenantId);
		return newOption;
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
	 * @return the value
	 */
	public String getValue() {
		return value;
	}

	/**
	 * @param value the value to set
	 */
	public void setValue(String value) {
		this.value = value;
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
	 * @return the scoring
	 */
	public Integer getScoring() {
		return scoring;
	}

	/**
	 * @param scoring the scoring to set
	 */
	public void setScoring(Integer scoring) {
		this.scoring = scoring;
	}

	/**
	 * @return the supplierFormItem
	 */
	public SupplierFormItem getSupplierFormItem() {
		return supplierFormItem;
	}

	/**
	 * @param supplierFormItem the supplierFormItem to set
	 */
	public void setSupplierFormItem(SupplierFormItem supplierFormItem) {
		this.supplierFormItem = supplierFormItem;
	}

	/**
	 * @return the tenantId
	 */
	public String getTenantId() {
		return tenantId;
	}

	/**
	 * @param tenantId the tenantId to set
	 */
	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((order == null) ? 0 : order.hashCode());
		result = prime * result + ((tenantId == null) ? 0 : tenantId.hashCode());
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SupplierFormItemOption other = (SupplierFormItemOption) obj;
		if (order == null) {
			if (other.order != null)
				return false;
		} else if (!order.equals(other.order))
			return false;
		if (tenantId == null) {
			if (other.tenantId != null)
				return false;
		} else if (!tenantId.equals(other.tenantId))
			return false;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}

	public String toLogString() {
		return "SupplierFormOption [id=" + id + ", value=" + value + ", order=" + order + ", scoring=" + scoring + "]";
	}

	@Override
	public int compareTo(SupplierFormItemOption o) {
		// asc
		if (this.order != null && o.order != null) {
			return this.order - o.order;
		} else {
			return 0;
		}
	}

}
