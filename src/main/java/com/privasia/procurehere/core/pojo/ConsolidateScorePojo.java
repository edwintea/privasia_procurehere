/**
 * 
 */
package com.privasia.procurehere.core.pojo;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author anshul
 */
public class ConsolidateScorePojo implements Serializable {

	private static final long serialVersionUID = -7671606151204330949L;

	private String criteria;

	private BigDecimal totalScore;

	private Integer order;
	private Integer level;

	private BigDecimal averageScore;
	private BigDecimal weightage;

	/**
	 * @return the criteria
	 */
	public String getCriteria() {
		return criteria;
	}

	/**
	 * @param criteria the criteria to set
	 */
	public void setCriteria(String criteria) {
		this.criteria = criteria;
	}

	/**
	 * @return the totalScore
	 */
	public BigDecimal getTotalScore() {
		return totalScore;
	}

	/**
	 * @param totalScore the totalScore to set
	 */
	public void setTotalScore(BigDecimal totalScore) {
		this.totalScore = totalScore;
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
	 * @return the averageScore
	 */
	public BigDecimal getAverageScore() {
		return averageScore;
	}

	/**
	 * @param averageScore the averageScore to set
	 */
	public void setAverageScore(BigDecimal averageScore) {
		this.averageScore = averageScore;
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
		return "ConsolidateScorePojo [criteria=" + criteria + ", totalScore=" + totalScore + ", order=" + order + ", level=" + level + ", averageScore=" + averageScore + ", weightage=" + weightage + "]";
	}

}
