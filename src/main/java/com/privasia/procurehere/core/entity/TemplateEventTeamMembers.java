package com.privasia.procurehere.core.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * @author Priyanka Singh
 */
@Entity
@Table(name = "PROC_TEMPLATE_EVENT_TEAM")
public class TemplateEventTeamMembers extends EventTeamMember implements Serializable {

	private static final long serialVersionUID = -8867221756717560750L;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, optional = true)
	@JoinColumn(name = "RFX_TEMPLATE_ID", foreignKey = @ForeignKey(name = "FK_TMPL_EVENT_TEAM_MEMBERS_ID"))
	private RfxTemplate rfxTemplate;

	
	/**
	 * @return the rfxTemplate
	 */
	public RfxTemplate getRfxTemplate() {
		return rfxTemplate;
	}

	/**
	 * @param rfxTemplate the rfxTemplate to set
	 */
	public void setRfxTemplate(RfxTemplate rfxTemplate) {
		this.rfxTemplate = rfxTemplate;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return super.hashCode();
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {

		return super.equals(obj);
	}

	public String toLogString() {
		return "TemplateEventApproval [ " + super.toLogString() + "]";
	}

}
