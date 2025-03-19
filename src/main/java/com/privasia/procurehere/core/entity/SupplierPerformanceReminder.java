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
 * @author Jayshree
 */
@Entity
@Table(name = "PROC_SUP_PERFORMANCE_REMINDER")
public class SupplierPerformanceReminder extends EventReminder implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5631576615028371116L;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.REFRESH }, optional = false)
	@JoinColumn(name = "FORM_ID", foreignKey = @ForeignKey(name = "FK_SP_REM_FORM_ID"))
	private SupplierPerformanceForm form;

	public SupplierPerformanceReminder() {
	}

	/**
	 * @return the form
	 */
	public SupplierPerformanceForm getForm() {
		return form;
	}

	/**
	 * @param form the form to set
	 */
	public void setForm(SupplierPerformanceForm form) {
		this.form = form;
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

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "SupplierPerformanceReminder [toLogString : " + super.toString() + "]";
	}

}
