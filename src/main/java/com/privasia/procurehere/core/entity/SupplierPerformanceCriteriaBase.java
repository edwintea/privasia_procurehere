/**
 * 
 */
package com.privasia.procurehere.core.entity;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.validation.constraints.Size;

/**
 * @author Jayshree
 *
 */
@MappedSuperclass
public class SupplierPerformanceCriteriaBase implements Serializable {

	private static final long serialVersionUID = 4717474934691846284L;

	@Column(name = "CRITERIA_NAME", nullable = false, length = 120)
	@Size(min = 1, max = 120, message = "{criteria.name.length}")
	private String name;

	@Column(name = "DESCRIPTION", length = 300)
	@Size(max = 300, message = "{criteria.description.length}")
	private String description;

	@Column(name = "CRITERIA_LEVEL", length = 2, nullable = false)
	private Integer level = 0;

	@Column(name = "CRITERIA_ORDER", length = 2, nullable = false)
	private Integer order = 0;

	@Column(name = "MAXIMUM_SCORE", precision = 3, scale = 0)
	private BigDecimal maximumScore;
	
	@Column(name = "WEIGHTAGE", precision = 5, scale = 2)
	private BigDecimal weightage;

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
	 * @return the level
	 */
	public Integer getLevel() {
		return level;
	}

	/**
	 * @param level the level to set
	 */
	public void setLevel(Integer level) {
		this.level = level;
	}

	/**
	 * @return the order
	 */
	public Integer getOrder() {
		return order;
	}

	/**
	 * @param order the order to set
	 */
	public void setOrder(Integer order) {
		this.order = order;
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

	@Override
	public String toString() {
		return "SupplierPerformanceCriteriaBase [name=" + name + ", description=" + description + ", level=" + level + ", order=" + order + ", maximumScore=" + maximumScore + ", weightage=" + weightage + "]";
	}
	
}