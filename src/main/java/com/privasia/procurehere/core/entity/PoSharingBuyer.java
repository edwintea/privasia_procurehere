package com.privasia.procurehere.core.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.GenericGenerator;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.privasia.procurehere.core.utils.CustomDateTimeSerializer;

/**
 * 
 * @author Yogesh Jadhav
 *
 */
@Entity
@Table(name = "PROC_PO_SHARE_BUYER")
public class PoSharingBuyer implements Serializable {

	

	/**
	 * 
	 */
	private static final long serialVersionUID = 7533941929494743421L;

	@Id
	@GenericGenerator(name = "idGen", strategy = "uuid.hex")
	@GeneratedValue(generator = "idGen")
	@Column(name = "ID", length = 64)
	private String id;

	@ManyToOne(fetch = FetchType.LAZY, optional = true, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "SUPPLIER_ID", nullable = true, foreignKey = @ForeignKey(name = "FK_PR_SUP_FC_ID"))
	private Supplier supplier;

//	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, optional = true)
	@JoinColumn(name = "FINANCE_ID", nullable = true, foreignKey = @ForeignKey(name = "FK_PR_FIN_COM_MAP_ID"))
	private FinanceCompany financeCompany;

	@ManyToOne(fetch = FetchType.LAZY, optional = true)
	@JoinColumn(name = "BUYER_ID", nullable = true, foreignKey = @ForeignKey(name = "FK_BUYER_FC_ID"))
	private Buyer buyer;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, optional = true, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "CREATED_BY", nullable = true)
	private User createdBy;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "MODIFIED_BY", nullable = true)
	private User modifiedBy;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CREATED_DATE", nullable = false)
	private Date createdDate;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "MODIFIED_DATE")
	private Date modifiedDate;


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
	 * @return the financeCompany
	 */
	public FinanceCompany getFinanceCompany() {
		return financeCompany;
	}

	/**
	 * @param financeCompany the financeCompany to set
	 */
	public void setFinanceCompany(FinanceCompany financeCompany) {
		this.financeCompany = financeCompany;
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

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "PoSharingBuyer [id=" + id + ", createdDate=" + createdDate + ", modifiedDate=" + modifiedDate + "]";
	}

	public PoSharingBuyer createShallowCopy() {
		PoSharingBuyer poSharingBuyer=new PoSharingBuyer();
		poSharingBuyer.setId(getId());
		FinanceCompany financeCompany=new FinanceCompany();
		financeCompany.setCompanyName(getFinanceCompany().getCompanyName());
		poSharingBuyer.setFinanceCompany(financeCompany);
		Buyer buyer=new Buyer();
		buyer.setCompanyName(getBuyer().getCompanyName());
		poSharingBuyer.setBuyer(buyer);
		
		return poSharingBuyer;
	}

	
	public PoSharingBuyer(){
		
	}
	
	public PoSharingBuyer(String id,String financeCompanyName,String buyerCompanyName){
		this.id=id;
		FinanceCompany financeCompany=new FinanceCompany();
		financeCompany.setCompanyName(financeCompanyName);
		this.setFinanceCompany(financeCompany);
		
		Buyer buyer=new Buyer();
		buyer.setCompanyName(buyerCompanyName);
		this.setBuyer(buyer);
		
		
	}
	
	
	
}
