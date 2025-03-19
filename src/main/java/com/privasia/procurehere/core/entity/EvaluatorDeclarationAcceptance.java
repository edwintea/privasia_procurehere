package com.privasia.procurehere.core.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.GenericGenerator;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.privasia.procurehere.core.utils.CustomDateTimeSerializer;
import org.hibernate.annotations.Type;

@MappedSuperclass
public class EvaluatorDeclarationAcceptance {

	@Id
	@GenericGenerator(name = "idGen", strategy = "uuid.hex")
	@GeneratedValue(generator = "idGen")
	@Column(name = "ID", length = 64)
	protected String id;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "ACCEPTED_DATE")
	private Date acceptedDate;

	@Column(name = "IS_LEAD_EVALUATOR", nullable = true)
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean isLeadEvaluator = Boolean.FALSE;

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
	 * @return the acceptedDate
	 */
	public Date getAcceptedDate() {
		return acceptedDate;
	}

	/**
	 * @param acceptedDate the acceptedDate to set
	 */
	public void setAcceptedDate(Date acceptedDate) {
		this.acceptedDate = acceptedDate;
	}

	/**
	 * @return the isLeadEvaluator
	 */
	public Boolean getIsLeadEvaluator() {
		return isLeadEvaluator;
	}

	/**
	 * @param isLeadEvaluator the isLeadEvaluator to set
	 */
	public void setIsLeadEvaluator(Boolean isLeadEvaluator) {
		this.isLeadEvaluator = isLeadEvaluator;
	}

}