/**
 * 
 */
package com.privasia.procurehere.core.pojo;

import java.io.Serializable;
import java.util.List;

/**
 * @author anshul
 */
public class ScoreCardPojo implements Serializable {

	private static final long serialVersionUID = 567636468969154844L;

	private String criteria;
	private Integer order;
	private Integer level;
	private List<String> score;
	private String average;

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
	 * @return the score
	 */
	public List<String> getScore() {
		return score;
	}

	/**
	 * @param score the score to set
	 */
	public void setScore(List<String> score) {
		this.score = score;
	}

	/**
	 * @return the level
	 */
	public Integer getLevel() {
		return level;
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
	 * @param level the level to set
	 */
	public void setLevel(Integer level) {
		this.level = level;
	}

	/**
	 * @return the average
	 */
	public String getAverage() {
		return average;
	}

	/**
	 * @param average the average to set
	 */
	public void setAverage(String average) {
		this.average = average;
	}

	@Override
	public String toString() {
		return "ScoreCardPojo [criteria=" + criteria + ", order=" + order + ", level=" + level + ", score=" + score + ", average=" + average + "]";
	}

}
