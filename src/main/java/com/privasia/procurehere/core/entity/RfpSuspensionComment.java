package com.privasia.procurehere.core.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.Type;

@Entity
@Table(name = "PROC_RFP_SUSP_COMMENTS")
public class RfpSuspensionComment extends Comments implements Serializable {
	private static final long serialVersionUID = 4545635016600630844L;

	@Id
	@GenericGenerator(name = "idGen", strategy = "uuid.hex")
	@GeneratedValue(generator = "idGen")
	@Column(name = "ID", nullable = false, length = 64)
	private String id;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "EVENT_ID", foreignKey = @ForeignKey(name = "FK_RFP_SUSP_COMM_ID"))
	private RfpEvent rfxEvent;

	@Column(name = "APPROVED")
	@Type(type = "org.hibernate.type.NumericBooleanType")
private boolean approved = false;

	@Column(name = "APPROVAL_USER_ID", length = 64, nullable = true)
	private String approvalUserId;
	
	public RfpSuspensionComment() {
		this.approved = Boolean.FALSE;
	}

	/**
	 * @return the approved
	 */
	public boolean isApproved() {
		return approved;
	}

	/**
	 * @param approved the approved to set
	 */
	public void setApproved(boolean approved) {
		this.approved = approved;
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
	 * @return the rfxEvent
	 */
	public RfpEvent getRfpEvent() {
		return rfxEvent;
	}

	/**
	 * @param rfxEvent the rfxEvent to set
	 */
	public void setRfxEvent(RfpEvent rfxEvent) {
		this.rfxEvent = rfxEvent;
	}

	/**
	 * @return the approvalUserId
	 */
	public String getApprovalUserId() {
		return approvalUserId;
	}

	/**
	 * @param approvalUserId the approvalUserId to set
	 */
	public void setApprovalUserId(String approvalUserId) {
		this.approvalUserId = approvalUserId;
	}

	/**
	 * @return the rfxEvent
	 */
	public RfpEvent getRfxEvent() {
		return rfxEvent;
	}


}
