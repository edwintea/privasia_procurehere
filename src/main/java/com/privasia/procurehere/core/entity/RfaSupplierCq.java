/**
 * 
 */
package com.privasia.procurehere.core.entity;

import java.io.Serializable;

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
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.privasia.procurehere.core.enums.SupplierCqStatus;

/**
 * @author jayshree
 *
 */
@Entity
@Table(name = "PROC_RFA_SUPPLIER_CQ")
public class RfaSupplierCq implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -644890615079283105L;
	
	@Id
	@GenericGenerator(name = "idGen", strategy = "uuid.hex")
	@GeneratedValue(generator = "idGen")
	@Column(name = "ID", length = 64)
	private String id;
	
	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, optional = true)
	@JoinColumn(name = "EVENT_ID", foreignKey = @ForeignKey(name = "FK_RFA_EVENT_CQ"))
	private RfaEvent event;
	
	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, optional = true, cascade = CascadeType.REFRESH)
	@JoinColumn(name = "CQ_ID", foreignKey = @ForeignKey(name = "FK_RFA_EVENT_CQ_ID"))
	private RfaCq cq;
	
	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, optional = true, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "SUPPLIER_ID", foreignKey = @ForeignKey(name = "FK_CQ_RFA_EVENT_SUP_ID"))
	private Supplier supplier;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "SUPPLIER_CQ_STATUS", nullable = true)
	private SupplierCqStatus supplierCqStatus = SupplierCqStatus.PENDING;

	public RfaSupplierCq() {
		
	}
	
	/**
	 * @param id
	 * @param event
	 * @param cq
	 * @param supplier
	 * @param supplierCqStatus
	 */
	public RfaSupplierCq(String id, RfaEvent event, RfaCq cq, Supplier supplier, SupplierCqStatus supplierCqStatus) {
		super();
		this.id = id;
		this.event = event;
		this.cq = cq;
		this.supplier = supplier;
		this.supplierCqStatus = supplierCqStatus;
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
	 * @return the event
	 */
	public RfaEvent getEvent() {
		return event;
	}

	/**
	 * @param event the event to set
	 */
	public void setEvent(RfaEvent event) {
		this.event = event;
	}

	/**
	 * @return the cq
	 */
	public RfaCq getCq() {
		return cq;
	}

	/**
	 * @param cq the cq to set
	 */
	public void setCq(RfaCq cq) {
		this.cq = cq;
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
	 * @return the supplierCqStatus
	 */
	public SupplierCqStatus getSupplierCqStatus() {
		return supplierCqStatus;
	}

	/**
	 * @param supplierCqStatus the supplierCqStatus to set
	 */
	public void setSupplierCqStatus(SupplierCqStatus supplierCqStatus) {
		this.supplierCqStatus = supplierCqStatus;
	}

	@Override
	public String toString() {
		return "RfaSupplierCq [id=" + id + ", event=" + event + ", cq=" + cq + ", supplier=" + supplier
				+ ", supplierCqStatus=" + supplierCqStatus + "]";
	}
	
}
