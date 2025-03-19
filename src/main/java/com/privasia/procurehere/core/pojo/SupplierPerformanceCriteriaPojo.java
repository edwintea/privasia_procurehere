package com.privasia.procurehere.core.pojo;

import java.io.Serializable;
import java.math.BigDecimal;

import com.privasia.procurehere.core.entity.SupplierPerformanceTemplateCriteria;

/**
 * @author Jayshree
 */
public class SupplierPerformanceCriteriaPojo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1411032583341989287L;
	
	private String id;
	private String templateId;
	private String criteriaName;
	private String description;
	private BigDecimal weightage;
	private Boolean allowToUpdateSectionWeightage;
	private String parent;
	private BigDecimal maximumScore;

	
	public SupplierPerformanceCriteriaPojo() {
	}

	public SupplierPerformanceCriteriaPojo(SupplierPerformanceTemplateCriteria templateCriteria) {
		this.criteriaName = templateCriteria.getName();
		this.description = templateCriteria.getDescription();
		this.templateId = templateCriteria.getTemplate().getId();
		this.weightage = templateCriteria.getWeightage();
		this.allowToUpdateSectionWeightage = templateCriteria.getAllowToUpdateSectionWeightage();
//		this.parent = templateCriteria.getParent() != null ? templateCriteria.getParent().getId() : null;
		this.maximumScore = templateCriteria.getMaximumScore();
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

	/**
	 * @return the criteriaName
	 */
	public String getCriteriaName() {
		return criteriaName;
	}

	/**
	 * @param criteriaName the criteriaName to set
	 */
	public void setCriteriaName(String criteriaName) {
		this.criteriaName = criteriaName;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return the weightage
	 */
	public BigDecimal getWeightage() {
		return weightage;
	}

	/**
	 * @param weightage the weightage to set
	 */
	public void setWeightage(BigDecimal weightage) {
		this.weightage = weightage;
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
	 * @return the parent
	 */
	public String getParent() {
		return parent;
	}

	/**
	 * @param parent the parent to set
	 */
	public void setParent(String parent) {
		this.parent = parent;
	}

	/**
	 * @return the maximumScore
	 */
	public BigDecimal getMaximumScore() {
		return maximumScore;
	}

	/**
	 * @param maximumScore the maximumScore to set
	 */
	public void setMaximumScore(BigDecimal maximumScore) {
		this.maximumScore = maximumScore;
	}

	public String toLogString() {
		return "SupplierPerformanceCriteriaPojo [id=" + id + ", templateId=" + templateId + ", criteriaName=" + criteriaName + ", description=" + description + ", weightage=" + weightage + ", allowToUpdateSectionWeightage=" + allowToUpdateSectionWeightage + ", parent=" + parent + ", maximumScore=" + maximumScore + "]";
	}

}
