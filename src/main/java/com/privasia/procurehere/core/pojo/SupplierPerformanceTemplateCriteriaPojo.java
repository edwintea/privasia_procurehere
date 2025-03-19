package com.privasia.procurehere.core.pojo;

import java.io.Serializable;
import java.math.BigDecimal;


/**
 * @author anshul
 */
public class SupplierPerformanceTemplateCriteriaPojo implements Serializable {

	private static final long serialVersionUID = -1448822527664758357L;

	private String id;
	private Integer order;
	private Integer level;
	private String templateId;
	private String name;
	private String description;
	private BigDecimal maximumScore;
	private BigDecimal weightage;
	private String parent;

	public SupplierPerformanceTemplateCriteriaPojo() {

	}

	public SupplierPerformanceTemplateCriteriaPojo(String id, String templateId, Integer order, Integer level, String name, String decscription, BigDecimal maximumScore, BigDecimal weightage, String parent) {
		this.id = id;
		this.templateId = templateId;
		this.order = order;
		this.level = level;
		this.name = name;
		this.description = decscription;
		this.maximumScore = maximumScore;
		this.weightage = weightage;
		this.parent = parent;
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
	 * @return the order
	 */
	public int getOrder() {
		return order;
	}

	/**
	 * @param order the order to set
	 */
	public void setOrder(int order) {
		this.order = order;
	}

	/**
	 * @return the level
	 */
	public int getLevel() {
		return level;
	}

	/**
	 * @param level the level to set
	 */
	public void setLevel(int level) {
		this.level = level;
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
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
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

}
