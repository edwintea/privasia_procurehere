package com.privasia.procurehere.core.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.privasia.procurehere.core.enums.RfxTypes;
import com.privasia.procurehere.core.utils.CustomDateTimeSerializer;
import org.hibernate.annotations.Type;

/**
 * @author yogesh
 */
@Entity
@Table(name = "PROC_ERP_STAGGING")
public class ErpAwardStaging implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3840599104110714931L;

	@Id
	@GenericGenerator(name = "idGen", strategy = "uuid.hex")
	@GeneratedValue(generator = "idGen")
	@Column(name = "ID", length = 64)
	private String id;

	@Column(name = "DOC_NO", length = 64)
	private String docNo;

	@ManyToOne(fetch = FetchType.LAZY, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "ACTION_BY", nullable = false)
	private User actionBy;

	@Column(name = "TENANT_ID", nullable = false)
	private String tenantId;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	@Column(name = "ACTION_DATE", nullable = false)
	private Date actionDate;

	@Enumerated(EnumType.STRING)
	@Column(name = "EVENT_TYPE", nullable = true)
	private RfxTypes eventType;

	@Lob
	@Type(type = "org.hibernate.type.TextType")
	@Basic(fetch = FetchType.LAZY)
	@Column(name = "PAYLOAD", nullable = false)
	private String payload;

	@Column(name = "IS_SENT")
	Boolean sentFlag = Boolean.FALSE;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	@Column(name = "SENT_DATE", nullable = true)
	private Date sentDate;

	public Boolean getSentFlag() {
		return sentFlag;
	}

	public void setSentFlag(Boolean sentFlag) {
		this.sentFlag = sentFlag;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDocNo() {
		return docNo;
	}

	public void setDocNo(String docNo) {
		this.docNo = docNo;
	}

	public User getActionBy() {
		return actionBy;
	}

	public void setActionBy(User actionBy) {
		this.actionBy = actionBy;
	}

	public String getTenantId() {
		return tenantId;
	}

	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
	}

	public Date getActionDate() {
		return actionDate;
	}

	public void setActionDate(Date actionDate) {
		this.actionDate = actionDate;
	}

	public RfxTypes getEventType() {
		return eventType;
	}

	public void setEventType(RfxTypes eventType) {
		this.eventType = eventType;
	}

	public String getPayload() {
		return payload;
	}

	public void setPayload(String payload) {
		this.payload = payload;
	}

	public Date getSentDate() {
		return sentDate;
	}

	public void setSentDate(Date sentDate) {
		this.sentDate = sentDate;
	}

}