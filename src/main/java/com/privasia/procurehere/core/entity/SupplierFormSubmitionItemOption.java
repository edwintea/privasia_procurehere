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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

/**
 * @author ravi
 */
@Entity
@Table(name = "PROC_SUP_FORM_SUB_ITEM_OPT")
public class SupplierFormSubmitionItemOption implements Comparable<SupplierFormSubmitionItemOption>, Serializable {

	private static final long serialVersionUID = 3543860578225876905L;

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
	@JoinColumn(name = "SUB_ITEM_ID", nullable = false, foreignKey = @ForeignKey(name = "FK_SFS_SUB_OPT_ITM_ID"))
	private SupplierFormSubmissionItem supplierFormSubmitiomItem;

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
	 * @return the supplierFormSubmitiomItem
	 */
	public SupplierFormSubmissionItem getSupplierFormSubmitiomItem() {
		return supplierFormSubmitiomItem;
	}

	/**
	 * @param supplierFormSubmitiomItem the supplierFormSubmitiomItem to set
	 */
	public void setSupplierFormSubmitiomItem(SupplierFormSubmissionItem supplierFormSubmitiomItem) {
		this.supplierFormSubmitiomItem = supplierFormSubmitiomItem;
	}

	public String toLogString() {
		return "SupplierFormOption [id=" + id + ", value=" + value + ", order=" + order + ", scoring=" + scoring + "]";
	}

	@Override
	public int compareTo(SupplierFormSubmitionItemOption o) {
		// asc
		if (this.order != null && o.order != null) {
			return this.order - o.order;
		} else {
			return 0;
		}
	}

}
