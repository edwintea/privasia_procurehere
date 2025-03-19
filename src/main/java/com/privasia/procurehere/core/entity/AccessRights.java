/**
 * 
 */
package com.privasia.procurehere.core.entity;

import java.io.Serializable;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.Type;

/**
 * @author Arc
 */
@Entity
@Table(name = "PROC_ACCESS_RIGHTS")
public class AccessRights implements Serializable {

	private static final long serialVersionUID = -1911497390027717407L;

	@Id
	@GenericGenerator(name = "idGen", strategy = "assigned")
	@Column(name = "ACL_VALUE", nullable = false, length = 80)
	private String aclValue;

	@Column(name = "ACL_NAME", nullable = false, length = 104)
	private String aclName;

	@JsonIgnore
	@ManyToOne(optional = true, cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	@JoinColumn(name = "PARENT_ID", nullable = true, foreignKey = @ForeignKey(name = "FK_ACL_ACL_PARENT"))
	private AccessRights parent;

	@JsonIgnore
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "parent")
	@OrderBy("aclOrder")
	private List<AccessRights> children;

	/*
	 * @Column(name = "IS_MENU_LINK", nullable = false) @Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean isMenuLink;
	 */
	@Column(name = "MENU_URL", nullable = true)
	private String menuLink;

	@Column(name = "FOR_SUPPLIER")
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean supplier = Boolean.FALSE;

	@Column(name = "FOR_BUYER")
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean buyer = Boolean.FALSE;

	@Column(name = "FOR_OWNER")
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean owner = Boolean.FALSE;

	@Column(name = "FOR_FINANCE_COMPANY")
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean financeCompany = Boolean.FALSE;

	@Column(name = "ACL_ORDER", length = 3)
	private Integer aclOrder;

	@Transient
	private String parentAclName;

	public AccessRights() {
		this.supplier = Boolean.FALSE;
		this.buyer = Boolean.FALSE;
		this.owner = Boolean.FALSE;
	}

	public String getAclValue() {
		return aclValue;
	}

	public void setAclValue(String aclValue) {
		this.aclValue = aclValue;
	}

	public String getAclName() {
		return aclName;
	}

	public void setAclName(String aclName) {
		this.aclName = aclName;
	}

	public AccessRights getParent() {
		return parent;
	}

	public void setParent(AccessRights parent) {
		this.parent = parent;
	}

	public List<AccessRights> getChildren() {
		return children;
	}

	public void setChildren(List<AccessRights> children) {
		this.children = children;
	}

	public String getMenuLink() {
		return menuLink;
	}

	public void setMenuLink(String menuLink) {
		this.menuLink = menuLink;
	}

	public String getParentAclName() {
		return parentAclName;
	}

	public void setParentAclName(String parentAclName) {
		this.parentAclName = parentAclName;
	}

	/**
	 * @return the supplier
	 */
	public Boolean getSupplier() {
		return supplier;
	}

	/**
	 * @param supplier the supplier to set
	 */
	public void setSupplier(Boolean supplier) {
		this.supplier = supplier;
	}

	/**
	 * @return the buyer
	 */
	public Boolean getBuyer() {
		return buyer;
	}

	/**
	 * @param buyer the buyer to set
	 */
	public void setBuyer(Boolean buyer) {
		this.buyer = buyer;
	}

	/**
	 * @return the owner
	 */
	public Boolean getOwner() {
		return owner;
	}

	/**
	 * @param owner the owner to set
	 */
	public void setOwner(Boolean owner) {
		this.owner = owner;
	}

	/**
	 * @return the aclOrder
	 */
	public Integer getAclOrder() {
		return aclOrder;
	}

	/**
	 * @param aclOrder the aclOrder to set
	 */
	public void setAclOrder(Integer aclOrder) {
		this.aclOrder = aclOrder;
	}

	public Boolean getFinanceCompany() {
		return financeCompany;
	}

	public void setFinanceCompany(Boolean financeCompany) {
		this.financeCompany = financeCompany;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((aclValue == null) ? 0 : aclValue.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AccessRights other = (AccessRights) obj;
		if (aclValue == null) {
			if (other.aclValue != null)
				return false;
		} else if (!aclValue.equals(other.aclValue))
			return false;
		return true;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return aclName;
	}

	public AccessRights createShallowCopy() {
		AccessRights ar = new AccessRights();
		ar.setAclName(aclName);
		ar.setAclOrder(aclOrder);
		ar.setAclValue(aclValue);
		ar.setBuyer(buyer);
		ar.setOwner(owner);
		ar.setSupplier(supplier);
		return ar;
	}
}
