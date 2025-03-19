/**
 * 
 */
package com.privasia.procurehere.core.entity;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.GenericGenerator;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.privasia.procurehere.core.enums.SourcingTemplateFieldName;
import org.hibernate.annotations.Type;

/**
 * @author sudesha
 */
@Entity
@Table(name = "PROC_SOURCING_TEMPLATE_FIELD")
public class SourcingTemplateField implements Serializable {

	private static final long serialVersionUID = 1531536510666186185L;

	@Id
	@GenericGenerator(name = "idGen", strategy = "uuid.hex")
	@GeneratedValue(generator = "idGen")
	@Column(name = "ID", length = 64)
	private String id;

	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(name = "FIELD_NAME", nullable = false, length = 80)
	private SourcingTemplateFieldName fieldName;

	@Column(name = "DEFAULT_VALUE", nullable = true, length = 1000)
	private String defaultValue;

	@NotNull
	@Column(name = "IS_VISIBLE", nullable = false)
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean visible = Boolean.TRUE;

	@NotNull
	@Column(name = "IS_READ_ONLY", nullable = false)
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean readOnly = Boolean.FALSE;

	@NotNull
	@Column(name = "IS_OPTIONAL", nullable = false)
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean optional = Boolean.FALSE;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, optional = false, cascade = { CascadeType.PERSIST, CascadeType.REFRESH })
	@JoinColumn(name = "TENANT_ID", nullable = false, foreignKey = @ForeignKey(name = "FK_SOUR_TMPLATE_TANT_ID"))
	private Buyer buyer;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, optional = false, cascade = { CascadeType.PERSIST, CascadeType.REFRESH })
	@JoinColumn(name = "TEMPLATE_ID", nullable = false, foreignKey = @ForeignKey(name = "FK_SOUR_TMPLATE_BUYR_ID"))
	private SourcingFormTemplate template;

	public SourcingTemplateField() {
		this.optional = Boolean.FALSE;
		this.readOnly = Boolean.FALSE;
		this.visible = Boolean.TRUE;
	}

	public SourcingTemplateField(SourcingTemplateFieldName fieldName, String defaultValue, boolean visible, boolean readonly, boolean optional, Buyer buyer, SourcingFormTemplate template) {
		this.fieldName = fieldName;
		this.defaultValue = defaultValue;
		this.visible = visible;
		this.readOnly = readonly;
		this.optional = optional;
		this.buyer = buyer;
		this.template = template;
	}

	public SourcingTemplateField(SourcingTemplateFieldName fieldName, String defaultValue, boolean readonly, Buyer buyer, SourcingFormTemplate template) {
		this.fieldName = fieldName;
		this.defaultValue = defaultValue;
		this.readOnly = readonly;
		this.buyer = buyer;
		this.template = template;
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
	 * @return the defaultValue
	 */
	public String getDefaultValue() {
		return defaultValue;
	}

	/**
	 * @param defaultValue the defaultValue to set
	 */
	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	/**
	 * @return the visible
	 */
	public Boolean getVisible() {
		return visible;
	}

	/**
	 * @param visible the visible to set
	 */
	public void setVisible(Boolean visible) {
		this.visible = visible;
	}

	/**
	 * @return the readOnly
	 */
	public Boolean getReadOnly() {
		return readOnly;
	}

	/**
	 * @param readOnly the readOnly to set
	 */
	public void setReadOnly(Boolean readOnly) {
		this.readOnly = readOnly;
	}

	/**
	 * @return the optional
	 */
	public Boolean getOptional() {
		return optional;
	}

	/**
	 * @param optional the optional to set
	 */
	public void setOptional(Boolean optional) {
		this.optional = optional;
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
	 * @return the fieldName
	 */
	public SourcingTemplateFieldName getFieldName() {
		return fieldName;
	}

	/**
	 * @param fieldName the fieldName to set
	 */
	public void setFieldName(SourcingTemplateFieldName fieldName) {
		this.fieldName = fieldName;
	}

	/**
	 * @return the template
	 */
	public SourcingFormTemplate getTemplate() {
		return template;
	}

	/**
	 * @param template the template to set
	 */
	public void setTemplate(SourcingFormTemplate template) {
		this.template = template;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((defaultValue == null) ? 0 : defaultValue.hashCode());
		result = prime * result + ((fieldName == null) ? 0 : fieldName.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + (optional ? 1231 : 1237);
		result = prime * result + ((readOnly == null) ? 0 : readOnly.hashCode());
		result = prime * result + ((visible == null) ? 0 : visible.hashCode());
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
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SourcingTemplateField other = (SourcingTemplateField) obj;
		if (defaultValue == null) {
			if (other.defaultValue != null)
				return false;
		} else if (!defaultValue.equals(other.defaultValue))
			return false;
		if (fieldName != other.fieldName)
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (optional != other.optional)
			return false;
		if (readOnly == null) {
			if (other.readOnly != null)
				return false;
		} else if (!readOnly.equals(other.readOnly))
			return false;
		if (visible == null) {
			if (other.visible != null)
				return false;
		} else if (!visible.equals(other.visible))
			return false;
		return true;
	}

	public String toLogString() {
		return "TemplateField [id=" + id + ", fieldName=" + fieldName + ", defaultValue=" + defaultValue + ", visible=" + visible + ", readOnly=" + readOnly + ", optional=" + optional + "]";
	}

}
