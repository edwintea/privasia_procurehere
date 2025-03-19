/**
 * 
 */
package com.privasia.procurehere.core.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.logging.log4j.*;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * @author Jayshree
 *
 */
@Entity
@Table(name = "PROC_SP_TMPLATE_CRITERIA")
public class SupplierPerformanceTemplateCriteria extends SupplierPerformanceCriteriaBase implements Serializable {
	
	private static final Logger LOG = LogManager.getLogger(SupplierPerformanceTemplateCriteria.class);
	
	private static final long serialVersionUID = 8446708672025636557L;

	@Id
	@GenericGenerator(name = "idGen", strategy = "uuid.hex")
	@GeneratedValue(generator = "idGen")
	@Column(name = "ID", length = 64)
	private String id;

	@Column(name = "ALLW_TO_UPDATE_SECT_WEIGHTAGE")
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean allowToUpdateSectionWeightage = Boolean.FALSE;
	
	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, optional = false, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "TEMPLATE_ID", nullable = true, foreignKey = @ForeignKey(name = "FK_SP_TEMP_CRTR_TEMP_ID"))
	private SupplierPerformanceTemplate template;
	
	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, optional = true, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "PARENT_ID", nullable = true, foreignKey = @ForeignKey(name = "FK_SP_TEMP_CRTR_PARENT_ID"))
	private SupplierPerformanceTemplateCriteria parent;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "parent", orphanRemoval = true, cascade = CascadeType.ALL)
	@OrderBy("level, order")
	private List<SupplierPerformanceTemplateCriteria> children;
	
	@Transient
	private String templateId;
	
	public SupplierPerformanceTemplateCriteria createShallowCopy() {
		SupplierPerformanceTemplateCriteria ic = new SupplierPerformanceTemplateCriteria();
		ic.setId(getId());
		ic.setName(getName());
		ic.setDescription(getDescription());
		ic.setLevel(getLevel());
		ic.setOrder(getOrder());
		ic.setMaximumScore(getMaximumScore());
		ic.setWeightage(getWeightage());
		ic.setAllowToUpdateSectionWeightage(getAllowToUpdateSectionWeightage());

		try {
			if (this.getTemplate() != null) {
				ic.setTemplate(this.getTemplate());
			}
		} catch (Exception e) {
			LOG.error("" + e.getMessage(), e);
		}

		try {
			if (this.getParent() != null) {
				ic.setParent(this.getParent());
			}
		} catch (Exception e) {
			LOG.error("" + e.getMessage(), e);
		}
		return ic;
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
	 * @return the allowToUpdateSectionWeightage
	 */
	public Boolean getAllowToUpdateSectionWeightage() {
		return allowToUpdateSectionWeightage;
	}

	/**
	 * @param allowToUpdateSectionWeightage the allowToUpdateSectionWeightage to set
	 */
	public void setAllowToUpdateSectionWeightage(Boolean allowToUpdateSectionWeightage) {
		this.allowToUpdateSectionWeightage = allowToUpdateSectionWeightage;
	}

	/**
	 * @return the template
	 */
	public SupplierPerformanceTemplate getTemplate() {
		return template;
	}

	/**
	 * @param template the template to set
	 */
	public void setTemplate(SupplierPerformanceTemplate template) {
		this.template = template;
	}
	
	/**
	 * @return the parent
	 */
	public SupplierPerformanceTemplateCriteria getParent() {
		return parent;
	}

	/**
	 * @param parent the parent to set
	 */
	public void setParent(SupplierPerformanceTemplateCriteria parent) {
		this.parent = parent;
	}

	/**
	 * @return the children
	 */
	public List<SupplierPerformanceTemplateCriteria> getChildren() {
		return children;
	}

	/**
	 * @param children the children to set
	 */
	public void setChildren(List<SupplierPerformanceTemplateCriteria> children) {
		this.children = children;
	}

	/**
	 * @return the templateId
	 */
	public String getTemplateId() {
		return templateId;
	}

	/**
	 * @param templateId the templateId to set
	 */
	public void setTemplateId(String templateId) {
		this.templateId = templateId;
	}

	public SupplierPerformanceTemplateCriteria copyFrom() {
		SupplierPerformanceTemplateCriteria newCriteria = new SupplierPerformanceTemplateCriteria();
		newCriteria.setName(getName());
		newCriteria.setDescription(getDescription());
		newCriteria.setOrder(getOrder());
		newCriteria.setLevel(getLevel());
		newCriteria.setMaximumScore(getMaximumScore());
		newCriteria.setWeightage(getWeightage());
		newCriteria.setAllowToUpdateSectionWeightage(getAllowToUpdateSectionWeightage());
		return newCriteria;
	}

	public SupplierPerformanceFormCriteria copyFromTemplate() {
		SupplierPerformanceFormCriteria newCriteria = new SupplierPerformanceFormCriteria();
		newCriteria.setName(getName());
		newCriteria.setDescription(getDescription());
		newCriteria.setOrder(getOrder());
		newCriteria.setLevel(getLevel());
		newCriteria.setMaximumScore(getMaximumScore());
		newCriteria.setWeightage(getWeightage());
		newCriteria.setAllowToUpdateSectionWeightage(getAllowToUpdateSectionWeightage());
		newCriteria.setAverageScore(BigDecimal.ZERO);
		return newCriteria;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SupplierPerformanceTemplateCriteria other = (SupplierPerformanceTemplateCriteria) obj;
		return Objects.equals(id, other.id);
	}

	public String toLogString() {
		return "SupplierPerformanceTemplateCriteria [id=" + id + ", toString()=" + super.toString() + "]";
	}

}