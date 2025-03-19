package com.privasia.procurehere.core.entity;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * @author sudesha
 */
@Entity
@Table(name = "PROC_SOURCING_FORM_REQ_TEAM")
public class SourcingFormTeamMember extends EventTeamMember implements Serializable {

	private static final long serialVersionUID = -7275645102028806610L;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, cascade = { CascadeType.REFRESH }, optional = false)
	@JoinColumn(name = "FORM_ID", foreignKey = @ForeignKey(name = "FK_SOURCING_FORM_TEAM"))
	private SourcingFormRequest sourcingFormRequest;

	public SourcingFormRequest getSourcingFormRequest() {
		return sourcingFormRequest;
	}

	public void setSourcingFormRequest(SourcingFormRequest sourcingFormRequest) {
		this.sourcingFormRequest = sourcingFormRequest;
	}

	public SourcingFormTeamMember copyFrom() {
		SourcingFormTeamMember newTm = new SourcingFormTeamMember();
		newTm.setUser(getUser());
		newTm.setTeamMemberType(getTeamMemberType());
		return newTm;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((sourcingFormRequest == null) ? 0 : sourcingFormRequest.hashCode());
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
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "SourcingFormTeamMember [sourcingFormRequest=" + sourcingFormRequest + "]";
	}

}
