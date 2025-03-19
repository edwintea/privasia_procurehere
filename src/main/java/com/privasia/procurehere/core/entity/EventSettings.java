/**
 * 
 */
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
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.privasia.procurehere.core.utils.CustomDateTimeSerializer;
import org.hibernate.annotations.Type;

/**
 * @author nitin
 */

@Entity
@Table(name = "PROC_EVENT_SETTINGS", indexes = { @Index(columnList = "TENANT_ID", name = "IDX_EVT_SETT_TENANT_ID") })
public class EventSettings implements Serializable {

	private static final long serialVersionUID = -9165680319849869387L;

	@Id
	@GenericGenerator(name = "idGen", strategy = "uuid.hex")
	@GeneratedValue(generator = "idGen")
	@Column(name = "ID", length = 64)
	private String id;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, optional = false, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "TENANT_ID", nullable = false, foreignKey = @ForeignKey(name = "FK_EVT_SETT_TENANT_ID"))
	private Buyer buyer;
	
	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "MODIFIED_BY", nullable = true, foreignKey = @ForeignKey(name = "FK_EVT_SETT_MODIFIED_BY_ID"))
	private User modifiedBy;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "MODIFIED_DATE", nullable = true)
	private Date modifiedDate;

	@Column(name = "RFI_CREATE_FRM_BLANK")
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean rfiCreateFromBlank = Boolean.TRUE;

	@Column(name = "RFI_COPY_FRM_PREV")
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean rfiCopyFromPrevious = Boolean.TRUE;

	@Column(name = "RFQ_CREATE_FRM_BLANK")
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean rfqCreateFromBlank = Boolean.TRUE;

	@Column(name = "RFQ_COPY_FRM_PREV")
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean rfqCopyFromPrevious = Boolean.TRUE;

	@Column(name = "RFP_CREATE_FRM_BLANK")
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean rfpCreateFromBlank = Boolean.TRUE;

	@Column(name = "RFP_COPY_FRM_PREV")
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean rfpCopyFromPrevious = Boolean.TRUE;

	@Column(name = "RFT_CREATE_FRM_BLANK")
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean rftCreateFromBlank = Boolean.TRUE;

	@Column(name = "RFT_COPY_FRM_PREV")
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean rftCopyFromPrevious = Boolean.TRUE;

	@Column(name = "RFA_CREATE_FRM_BLANK")
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean rfaCreateFromBlank = Boolean.TRUE;

	@Column(name = "RFA_COPY_FRM_PREV")
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean rfaCopyFromPrevious = Boolean.TRUE;

	@Column(name = "RFS_COPY_FRM_PREV")
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean rfsCopyFromPrevious = Boolean.TRUE;

	@Column(name = "PR_COPY_FRM_PREV")
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean prCopyFromPrevious = Boolean.TRUE;

	@Transient
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean autoCreatePo = Boolean.FALSE;

	@Transient
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean autoPublishPo = Boolean.FALSE;

	public EventSettings() {
		this.rfiCreateFromBlank = Boolean.TRUE;
		this.rfiCopyFromPrevious = Boolean.TRUE;
		this.rfqCreateFromBlank = Boolean.TRUE;
		this.rfqCopyFromPrevious = Boolean.TRUE;
		this.rfpCreateFromBlank = Boolean.TRUE;
		this.rfpCopyFromPrevious = Boolean.TRUE;
		this.rftCreateFromBlank = Boolean.TRUE;
		this.rftCopyFromPrevious = Boolean.TRUE;
		this.rfaCreateFromBlank = Boolean.TRUE;
		this.rfaCopyFromPrevious = Boolean.TRUE;
		this.rfsCopyFromPrevious = Boolean.TRUE;
		this.prCopyFromPrevious = Boolean.TRUE;
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
	 * @return the rfiCreateFromBlank
	 */
	public Boolean getRfiCreateFromBlank() {
		return rfiCreateFromBlank;
	}

	/**
	 * @param rfiCreateFromBlank the rfiCreateFromBlank to set
	 */
	public void setRfiCreateFromBlank(Boolean rfiCreateFromBlank) {
		this.rfiCreateFromBlank = rfiCreateFromBlank;
	}

	/**
	 * @return the rfiCopyFromPrevious
	 */
	public Boolean getRfiCopyFromPrevious() {
		return rfiCopyFromPrevious;
	}

	/**
	 * @param rfiCopyFromPrevious the rfiCopyFromPrevious to set
	 */
	public void setRfiCopyFromPrevious(Boolean rfiCopyFromPrevious) {
		this.rfiCopyFromPrevious = rfiCopyFromPrevious;
	}

	/**
	 * @return the rfqCreateFromBlank
	 */
	public Boolean getRfqCreateFromBlank() {
		return rfqCreateFromBlank;
	}

	/**
	 * @param rfqCreateFromBlank the rfqCreateFromBlank to set
	 */
	public void setRfqCreateFromBlank(Boolean rfqCreateFromBlank) {
		this.rfqCreateFromBlank = rfqCreateFromBlank;
	}

	/**
	 * @return the rfqCopyFromPrevious
	 */
	public Boolean getRfqCopyFromPrevious() {
		return rfqCopyFromPrevious;
	}

	/**
	 * @param rfqCopyFromPrevious the rfqCopyFromPrevious to set
	 */
	public void setRfqCopyFromPrevious(Boolean rfqCopyFromPrevious) {
		this.rfqCopyFromPrevious = rfqCopyFromPrevious;
	}

	/**
	 * @return the rfpCreateFromBlank
	 */
	public Boolean getRfpCreateFromBlank() {
		return rfpCreateFromBlank;
	}

	/**
	 * @param rfpCreateFromBlank the rfpCreateFromBlank to set
	 */
	public void setRfpCreateFromBlank(Boolean rfpCreateFromBlank) {
		this.rfpCreateFromBlank = rfpCreateFromBlank;
	}

	/**
	 * @return the rfpCopyFromPrevious
	 */
	public Boolean getRfpCopyFromPrevious() {
		return rfpCopyFromPrevious;
	}

	/**
	 * @param rfpCopyFromPrevious the rfpCopyFromPrevious to set
	 */
	public void setRfpCopyFromPrevious(Boolean rfpCopyFromPrevious) {
		this.rfpCopyFromPrevious = rfpCopyFromPrevious;
	}

	/**
	 * @return the rftCreateFromBlank
	 */
	public Boolean getRftCreateFromBlank() {
		return rftCreateFromBlank;
	}

	/**
	 * @param rftCreateFromBlank the rftCreateFromBlank to set
	 */
	public void setRftCreateFromBlank(Boolean rftCreateFromBlank) {
		this.rftCreateFromBlank = rftCreateFromBlank;
	}

	/**
	 * @return the rftCopyFromPrevious
	 */
	public Boolean getRftCopyFromPrevious() {
		return rftCopyFromPrevious;
	}

	/**
	 * @param rftCopyFromPrevious the rftCopyFromPrevious to set
	 */
	public void setRftCopyFromPrevious(Boolean rftCopyFromPrevious) {
		this.rftCopyFromPrevious = rftCopyFromPrevious;
	}

	/**
	 * @return the rfaCreateFromBlank
	 */
	public Boolean getRfaCreateFromBlank() {
		return rfaCreateFromBlank;
	}

	/**
	 * @param rfaCreateFromBlank the rfaCreateFromBlank to set
	 */
	public void setRfaCreateFromBlank(Boolean rfaCreateFromBlank) {
		this.rfaCreateFromBlank = rfaCreateFromBlank;
	}

	/**
	 * @return the rfaCopyFromPrevious
	 */
	public Boolean getRfaCopyFromPrevious() {
		return rfaCopyFromPrevious;
	}

	/**
	 * @param rfaCopyFromPrevious the rfaCopyFromPrevious to set
	 */
	public void setRfaCopyFromPrevious(Boolean rfaCopyFromPrevious) {
		this.rfaCopyFromPrevious = rfaCopyFromPrevious;
	}

	/**
	 * @return the rfsCopyFromPrevious
	 */
	public Boolean getRfsCopyFromPrevious() {
		return rfsCopyFromPrevious;
	}

	/**
	 * @param rfsCopyFromPrevious the rfsCopyFromPrevious to set
	 */
	public void setRfsCopyFromPrevious(Boolean rfsCopyFromPrevious) {
		this.rfsCopyFromPrevious = rfsCopyFromPrevious;
	}

	/**
	 * @return the prCopyFromPrevious
	 */
	public Boolean getPrCopyFromPrevious() {
		return prCopyFromPrevious;
	}

	/**
	 * @param prCopyFromPrevious the prCopyFromPrevious to set
	 */
	public void setPrCopyFromPrevious(Boolean prCopyFromPrevious) {
		this.prCopyFromPrevious = prCopyFromPrevious;
	}

	/**
	 * @return the autoCreatePo
	 */
	public Boolean getAutoCreatePo() {
		return autoCreatePo;
	}

	/**
	 * @param autoCreatePo the autoCreatePo to set
	 */
	public void setAutoCreatePo(Boolean autoCreatePo) {
		this.autoCreatePo = autoCreatePo;
	}

	/**
	 * @return the autoPublishPo
	 */
	public Boolean getAutoPublishPo() {
		return autoPublishPo;
	}

	/**
	 * @param autoPublishPo the autoPublishPo to set
	 */
	public void setAutoPublishPo(Boolean autoPublishPo) {
		this.autoPublishPo = autoPublishPo;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
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
		EventSettings other = (EventSettings) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "EventSettings [id=" + id + ", modifiedDate=" + modifiedDate + ", rfiCreateFromBlank="
				+ rfiCreateFromBlank + ", rfiCopyFromPrevious=" + rfiCopyFromPrevious + ", rfqCreateFromBlank="
				+ rfqCreateFromBlank + ", rfqCopyFromPrevious=" + rfqCopyFromPrevious + ", rfpCreateFromBlank="
				+ rfpCreateFromBlank + ", rfpCopyFromPrevious=" + rfpCopyFromPrevious + ", rftCreateFromBlank="
				+ rftCreateFromBlank + ", rftCopyFromPrevious=" + rftCopyFromPrevious + ", rfaCreateFromBlank="
				+ rfaCreateFromBlank + ", rfaCopyFromPrevious=" + rfaCopyFromPrevious + ", rfsCopyFromPrevious="
				+ rfsCopyFromPrevious + ", prCopyFromPrevious=" + prCopyFromPrevious + "]";
	}

}
