package com.privasia.procurehere.core.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.ColumnResult;
import javax.persistence.ConstructorResult;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SqlResultSetMapping;
import javax.persistence.Table;
import javax.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.privasia.procurehere.core.enums.Status;
import com.privasia.procurehere.core.pojo.EventSupplierPojo;
import com.privasia.procurehere.core.utils.CustomDateTimeSerializer;

/**
 * @author Javed Ahmed
 */
@Entity
@Table(name = "PROC_PRODUCT_CATEGORY")
@SqlResultSetMapping(name = "favSupplierResult", classes = { @ConstructorResult(targetClass = EventSupplierPojo.class, columns = { @ColumnResult(name = "id"), @ColumnResult(name = "companyName") }) })
public class ProductCategory implements Serializable {

	private static final long serialVersionUID = -51337264073448794L;

	public interface ProductCategoryInt {
	}

	@Id
	@GenericGenerator(name = "idGen", strategy = "uuid.hex")
	@GeneratedValue(generator = "idGen")
	@Column(name = "ID", length = 64)
	private String id;

	@NotEmpty(message = "{product.productCode.empty}", groups = ProductCategoryInt.class)
	@Size(min = 1, max = 15, message = "{product.productCategoryCode.length}", groups = ProductCategoryInt.class)
	@Column(name = "PRODUCT_CODE", length = 15, nullable = false)
	private String productCode;

	@NotEmpty(message = "{product.productName.empty}", groups = ProductCategoryInt.class)
	@Size(min = 1, max = 128, message = "{product.productCategoryName.length}", groups = ProductCategoryInt.class)
	@Column(name = "PRODUCT_NAME", length = 128, nullable = false)
	private String productName;

	@Enumerated(EnumType.STRING)
	@Column(name = "PRODUCT_ACTIVE")
	private Status status;

	@ManyToOne(fetch = FetchType.LAZY, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "CREATED_BY", nullable = true, foreignKey = @ForeignKey(name = "FK_PROD_CAT_CREATED_BY"))
	private User createdBy;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	@Column(name = "CREATION_DATE", nullable = true, length = 20)
	private Date createdDate;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	@Column(name = "MODIFIED_TIME", nullable = true, length = 20)
	private Date modifiedDate;

	@ManyToOne(fetch = FetchType.LAZY, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "MODIFIED_BY", nullable = true, foreignKey = @ForeignKey(name = "FK_PROD_CAT_MODIFIED_BY"))
	private User modifiedBy;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, optional = true)
	@JoinColumn(name = "TENANT_ID", nullable = true, foreignKey = @ForeignKey(name = "FK_PROD_CAT_BUYER_ID"))
	private Buyer buyer;

	public ProductCategory createShallowCopy() {
		ProductCategory ic = new ProductCategory();
		ic.setProductName(getProductName());
		ic.setProductCode(getProductCode());
		ic.setId(getId());
		if (getCreatedBy() != null) {
			User user = getCreatedBy();
			if (user != null) {
				user.getName();
			}
			if (user.getBuyer() != null) {
				Buyer b = new Buyer();
				b.setId(user.getBuyer().getId());
				user.setBuyer(b);
			}
			ic.setCreatedBy(user);
		}

		if (getModifiedBy() != null) {
			User user = getModifiedBy();
			if (user != null) {
				user.getName();
			}
			if (user != null && user.getBuyer() != null) {
				Buyer b = new Buyer();
				b.setId(user.getBuyer().getId());
				user.setBuyer(b);
			}
			ic.setModifiedBy(user);
		} else {
			ic.setModifiedBy(null);
		}
		ic.setCreatedDate(getCreatedDate());
		ic.setModifiedDate(getModifiedDate());

		return ic;
	}

	public ProductCategory() {

	}

	public ProductCategory(String id, String productCode, String productName, Status status) {
		super();
		this.id = id;
		this.productCode = productCode;
		this.productName = productName;
		this.status = status;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getProductCode() {
		return productCode;
	}

	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	/**
	 * @return the createdBy
	 */
	public User getCreatedBy() {
		return createdBy;
	}

	/**
	 * @param createdBy the createdBy to set
	 */
	public void setCreatedBy(User createdBy) {
		this.createdBy = createdBy;
	}

	/**
	 * @return the createdDate
	 */
	public Date getCreatedDate() {
		return createdDate;
	}

	/**
	 * @param createdDate the createdDate to set
	 */
	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	/**
	 * @return the modifiedDate
	 */
	public Date getModifiedDate() {
		return modifiedDate;
	}

	/**
	 * @param modifiedDate the modifiedDate to set
	 */
	public void setModifiedDate(Date modifiedDate) {
		this.modifiedDate = modifiedDate;
	}

	/**
	 * @return the modifiedBy
	 */
	public User getModifiedBy() {
		return modifiedBy;
	}

	/**
	 * @param modifiedBy the modifiedBy to set
	 */
	public void setModifiedBy(User modifiedBy) {
		this.modifiedBy = modifiedBy;
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((productCode == null) ? 0 : productCode.hashCode());
		result = prime * result + ((productName == null) ? 0 : productName.hashCode());
		result = prime * result + ((status == null) ? 0 : status.hashCode());
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
		ProductCategory other = (ProductCategory) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (productCode == null) {
			if (other.productCode != null)
				return false;
		} else if (!productCode.equals(other.productCode))
			return false;
		if (productName == null) {
			if (other.productName != null)
				return false;
		} else if (!productName.equals(other.productName))
			return false;
		if (status != other.status)
			return false;
		return true;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return productCode + "-" + productName;
	}

	public String toLogString() {
		return "ProductCategory [id=" + id + ", productCode=" + productCode + ", productName=" + productName + ", status=" + status + ", createdDate=" + createdDate + ", modifiedDate=" + modifiedDate + "]";
	}
}
