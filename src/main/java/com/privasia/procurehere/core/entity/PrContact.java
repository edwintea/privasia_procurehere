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
 * @author Parveen
 */
@Entity
@Table(name = "PROC_PR_CONTACTS")
public class PrContact extends EventContact implements Serializable {

	private static final long serialVersionUID = 6186040575920790774L;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, optional = false, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "PR_ID", foreignKey = @ForeignKey(name = "FK_PR_CONTACT_ID"))
	private Pr pr;

	/**
	 * @return the pr
	 */
	public Pr getPr() {
		return pr;
	}

	/**
	 * @param pr the pr to set
	 */
	public void setPr(Pr pr) {
		this.pr = pr;
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
		return "PrContact [ " + super.toLogString() + "]";
	}

}
