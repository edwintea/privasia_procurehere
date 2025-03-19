package com.privasia.procurehere.core.entity;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.ColumnResult;
import javax.persistence.ConstructorResult;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SqlResultSetMapping;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.privasia.procurehere.core.pojo.DraftEventPojo;

/**
 * @author Edwin
 */
@Entity
@Table(name = "PROC_PO_TEAM")
@SqlResultSetMapping(name = "copyFromPreviousEventPr", classes = { @ConstructorResult(targetClass = DraftEventPojo.class, columns = { @ColumnResult(name = "id"), @ColumnResult(name = "eventName"), @ColumnResult(name = "auctionType"), @ColumnResult(name = "eventStatus"), @ColumnResult(name = "descripiton"), @ColumnResult(name = "templateStatus"), @ColumnResult(name = "referenceNumber"), @ColumnResult(name = "eventId"), @ColumnResult(name = "startDate"), @ColumnResult(name = "endDate"), @ColumnResult(name = "eventCategories") }) })
public class PoTeamMember extends EventTeamMember implements Serializable {

	private static final long serialVersionUID = -7275645102028806610L;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.REFRESH }, optional = false)
	@JoinColumn(name = "PO_ID", foreignKey = @ForeignKey(name = "FK_PO_TEAM"))
	private Po po;

	public PoTeamMember copyFrom() {
		PoTeamMember newTm = new PoTeamMember();
		newTm.setUser(getUser());
		newTm.setTeamMemberType(getTeamMemberType());
		return newTm;
	}

	/**
	 * @return the po
	 */
	public Po getPo() {
		return po;
	}

	/**
	 * @param po the po to set
	 */
	public void setPo(Po po) {
		this.po = po;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((po == null) ? 0 : po.hashCode());
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

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "PoTeamMember [" + super.toLogString() + "]";
	}

}
