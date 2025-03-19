package com.privasia.procurehere.core.entity;

import java.io.Serializable;
import java.util.Date;
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
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.GenericGenerator;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.privasia.procurehere.core.enums.RequestAssociateBuyerStatus;
import com.privasia.procurehere.core.utils.CustomDateTimeSerializer;

/**
 * @author pooja
 */
@Entity
@Table(name = "PROC_REQUEST_ASSOCIATED_BUYER")
public class RequestedAssociatedBuyer implements Serializable {

	private static final long serialVersionUID = -8380590679794747745L;

	@Id
	@GenericGenerator(name = "idGen", strategy = "uuid.hex")
	@GeneratedValue(generator = "idGen")
	@Column(name = "ID", length = 64)
	private String id;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, optional = true)
	@JoinColumn(name = "BUYER_ID", nullable = true, foreignKey = @ForeignKey(name = "FK_SUPP_REQ_BUYER_ID"))
	private Buyer buyer;

	@ManyToOne(fetch = FetchType.LAZY, optional = true, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "SUPPLIER_ID", nullable = true, foreignKey = @ForeignKey(name = "FK_SUPP_REQ_SUPPL_ID"))
	private Supplier supplier;

	@ManyToMany(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.REFRESH })
	@JoinTable(name = "PROC_REQUESTED_SUPP_IND_CAT", joinColumns = @JoinColumn(name = "REQ_ASS_SUPP_ID"), inverseJoinColumns = @JoinColumn(name = "IND_CAT_ID"))
	private List<IndustryCategory> industryCategory;

	@Enumerated(EnumType.STRING)
	@Column(name = "REQUESTED_STATUS")
	private RequestAssociateBuyerStatus status;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "REQUESTED_DATE")
	private Date requestedDate;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "REJECTED_DATE")
	private Date rejectedDate;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "ASSOCITED_DATE")
	private Date associatedDate;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "REQUESTED_BY", nullable = true, foreignKey = @ForeignKey(name = "FK_SUPP_REQUESTED_BY"))
	private User requestedBy;

	@Column(name = "SUPPLIER_REMARK", length = 500)
	private String supplierRemark;

	@Column(name = "BUYER_REMARK", length = 500)
	private String buyerRemark;

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

	/**
	 * @return the industryCategory
	 */
	public List<IndustryCategory> getIndustryCategory() {
		return industryCategory;
	}

	/**
	 * @param industryCategory the industryCategory to set
	 */
	public void setIndustryCategory(List<IndustryCategory> industryCategory) {
		this.industryCategory = industryCategory;
	}

	/**
	 * @return the status
	 */
	public RequestAssociateBuyerStatus getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(RequestAssociateBuyerStatus status) {
		this.status = status;
	}

	/**
	 * @return the requestedDate
	 */
	public Date getRequestedDate() {
		return requestedDate;
	}

	/**
	 * @param requestedDate the requestedDate to set
	 */
	public void setRequestedDate(Date requestedDate) {
		this.requestedDate = requestedDate;
	}

	/**
	 * @return the rejectedDate
	 */
	public Date getRejectedDate() {
		return rejectedDate;
	}

	/**
	 * @param rejectedDate the rejectedDate to set
	 */
	public void setRejectedDate(Date rejectedDate) {
		this.rejectedDate = rejectedDate;
	}

	/**
	 * @return the associatedDate
	 */
	public Date getAssociatedDate() {
		return associatedDate;
	}

	/**
	 * @param associatedDate the associatedDate to set
	 */
	public void setAssociatedDate(Date associatedDate) {
		this.associatedDate = associatedDate;
	}

	/**
	 * @return the requestedBy
	 */
	public User getRequestedBy() {
		return requestedBy;
	}

	/**
	 * @param requestedBy the requestedBy to set
	 */
	public void setRequestedBy(User requestedBy) {
		this.requestedBy = requestedBy;
	}

	/**
	 * @return the supplierRemark
	 */
	public String getSupplierRemark() {
		return supplierRemark;
	}

	/**
	 * @param supplierRemark the supplierRemark to set
	 */
	public void setSupplierRemark(String supplierRemark) {
		this.supplierRemark = supplierRemark;
	}

	/**
	 * @return the buyerRemark
	 */
	public String getBuyerRemark() {
		return buyerRemark;
	}

	/**
	 * @param buyerRemark the buyerRemark to set
	 */
	public void setBuyerRemark(String buyerRemark) {
		this.buyerRemark = buyerRemark;
	}

}
