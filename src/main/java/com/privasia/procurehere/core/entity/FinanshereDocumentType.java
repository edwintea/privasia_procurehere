/**
 * 
 */
package com.privasia.procurehere.core.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

/**
 * @author nitin
 */
@Entity
@Table(name = "PROC_FIN_DOC_TYPE")
public class FinanshereDocumentType implements Serializable {

	private static final long serialVersionUID = -6740287311038824051L;

	@Id
	@GenericGenerator(name = "idGen", strategy = "uuid.hex")
	@GeneratedValue(generator = "idGen")
	@Column(name = "ID", length = 64)
	private String id;

	@Column(name = "DOCUMENT_TYPE", length = 128, nullable = false)
	private String documentType;

	@Column(name = "FOR_SUPPLIER")
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean visibleToSupplier = Boolean.FALSE;

	@Column(name = "FOR_BUYER")
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean visibleToBuyer = Boolean.FALSE;

	@Column(name = "FOR_FUNDER")
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean visibleToFunder = Boolean.FALSE;

	@Column(name = "FOR_BACKOFFICE_ADMIN")
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean visibleToBOA = Boolean.FALSE;

	public FinanshereDocumentType() {
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
	 * @return the documentType
	 */
	public String getDocumentType() {
		return documentType;
	}

	/**
	 * @param documentType the documentType to set
	 */
	public void setDocumentType(String documentType) {
		this.documentType = documentType;
	}

	/**
	 * @return the visibleToSupplier
	 */
	public Boolean getVisibleToSupplier() {
		return visibleToSupplier;
	}

	/**
	 * @param visibleToSupplier the visibleToSupplier to set
	 */
	public void setVisibleToSupplier(Boolean visibleToSupplier) {
		this.visibleToSupplier = visibleToSupplier;
	}

	/**
	 * @return the visibleToBuyer
	 */
	public Boolean getVisibleToBuyer() {
		return visibleToBuyer;
	}

	/**
	 * @param visibleToBuyer the visibleToBuyer to set
	 */
	public void setVisibleToBuyer(Boolean visibleToBuyer) {
		this.visibleToBuyer = visibleToBuyer;
	}

	/**
	 * @return the visibleToFunder
	 */
	public Boolean getVisibleToFunder() {
		return visibleToFunder;
	}

	/**
	 * @param visibleToFunder the visibleToFunder to set
	 */
	public void setVisibleToFunder(Boolean visibleToFunder) {
		this.visibleToFunder = visibleToFunder;
	}

	/**
	 * @return the visibleToBOA
	 */
	public Boolean getVisibleToBOA() {
		return visibleToBOA;
	}

	/**
	 * @param visibleToBOA the visibleToBOA to set
	 */
	public void setVisibleToBOA(Boolean visibleToBOA) {
		this.visibleToBOA = visibleToBOA;
	}

}
