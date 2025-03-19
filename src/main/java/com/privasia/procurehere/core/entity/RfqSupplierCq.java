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
@Table(name = "PROC_RFQ_SUPPLIER_CQ")
public class RfqSupplierCq implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 5894007403786849089L;

	@Id
	@GenericGenerator(name = "idGen", strategy = "uuid.hex")
	@GeneratedValue(generator = "idGen")
	@Column(name = "ID", length = 64)
	private String id;
	
	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, optional = true)
	@JoinColumn(name = "EVENT_ID", foreignKey = @ForeignKey(name = "FK_RFQ_EVENT_CQ"))
	private RfqEvent event;
	
	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, optional = true, cascade = CascadeType.REFRESH)
	@JoinColumn(name = "CQ_ID", foreignKey = @ForeignKey(name = "FK_RFQ_EVENT_CQ_ID"))
	private RfqCq cq;
	
	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, optional = true, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "SUPPLIER_ID", foreignKey = @ForeignKey(name = "FK_CQ_RFQ_EVENT_SUP_ID"))
	private Supplier supplier;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "SUPPLIER_CQ_STATUS", nullable = true)
	private SupplierCqStatus supplierCqStatus = SupplierCqStatus.PENDING;

	public RfqSupplierCq() {
		
	}
	
	/**
	 * @param id
	 * @param event
	 * @param cq
	 * @param supplier
	 * @param supplierCqStatus
	 */
	public RfqSupplierCq(String id, RfqEvent event, RfqCq cq, Supplier supplier, SupplierCqStatus supplierCqStatus) {
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
	public RfqEvent getEvent() {
		return event;
	}

	/**
	 * @param event the event to set
	 */
	public void setEvent(RfqEvent event) {
		this.event = event;
	}

	/**
	 * @return the cq
	 */
	public RfqCq getCq() {
		return cq;
	}

	/**
	 * @param cq the cq to set
	 */
	public void setCq(RfqCq cq) {
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
		return "RfqSupplierCq [id=" + id + ", event=" + event + ", cq=" + cq + ", supplier=" + supplier
				+ ", supplierCqStatus=" + supplierCqStatus + "]";
	}
	
}
