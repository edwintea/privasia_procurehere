/**
 * 
 */
package com.privasia.procurehere.core.pojo;

import java.io.Serializable;
import java.util.List;

import com.privasia.procurehere.core.entity.SupplierPerformanceFormCriteria;

/**
 * @author Jayshree
 *
 */
public class PerformanceEvaluationCriteriaPojo implements Serializable {

	private static final long serialVersionUID = 128084542609804188L;

	private String id;
	
	private String criteriaName;
	
	private String level;
	
	private String maximumScore;
	
	private String weightage;
	
	private String score;
	
	private String totalScore;
	
	private String comments;

	private String overallScoreStr;

	private String rating;

	private String ratingDescription;
	
	private String evaluatorName;
	
	private List<String> scoreList;
	
	public PerformanceEvaluationCriteriaPojo() {
	}

	public PerformanceEvaluationCriteriaPojo(SupplierPerformanceFormCriteria formCriteria) {
		this.criteriaName = formCriteria.getName();
		this.totalScore = formCriteria.getTotalScore() != null ? formCriteria.getTotalScore().toString() : "";
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
	 * @return the level
	 */
	public String getLevel() {
		return level;
	}

	/**
	 * @param level the level to set
	 */
	public void setLevel(String level) {
		this.level = level;
	}

	/**
	 * @return the maximumScore
	 */
	public String getMaximumScore() {
		return maximumScore;
	}

	/**
	 * @param maximumScore the maximumScore to set
	 */
	public void setMaximumScore(String maximumScore) {
		this.maximumScore = maximumScore;
	}

	/**
	 * @return the weightage
	 */
	public String getWeightage() {
		return weightage;
	}

	/**
	 * @param weightage the weightage to set
	 */
	public void setWeightage(String weightage) {
		this.weightage = weightage;
	}

	/**
	 * @return the score
	 */
	public String getScore() {
		return score;
	}

	/**
	 * @param score the score to set
	 */
	public void setScore(String score) {
		this.score = score;
	}

	/**
	 * @return the totalScore
	 */
	public String getTotalScore() {
		return totalScore;
	}

	/**
	 * @param totalScore the totalScore to set
	 */
	public void setTotalScore(String totalScore) {
		this.totalScore = totalScore;
	}

	/**
	 * @return the comments
	 */
	public String getComments() {
		return comments;
	}

	/**
	 * @param comments the comments to set
	 */
	public void setComments(String comments) {
		this.comments = comments;
	}

	/**
	 * @return the overallScoreStr
	 */
	public String getOverallScoreStr() {
		return overallScoreStr;
	}

	/**
	 * @param overallScoreStr the overallScoreStr to set
	 */
	public void setOverallScoreStr(String overallScoreStr) {
		this.overallScoreStr = overallScoreStr;
	}

	/**
	 * @return the rating
	 */
	public String getRating() {
		return rating;
	}

	/**
	 * @param rating the rating to set
	 */
	public void setRating(String rating) {
		this.rating = rating;
	}

	/**
	 * @return the ratingDescription
	 */
	public String getRatingDescription() {
		return ratingDescription;
	}

	/**
	 * @param ratingDescription the ratingDescription to set
	 */
	public void setRatingDescription(String ratingDescription) {
		this.ratingDescription = ratingDescription;
	}
	
	/**
	 * @return the evaluatorName
	 */
	public String getEvaluatorName() {
		return evaluatorName;
	}

	/**
	 * @param evaluatorName the evaluatorName to set
	 */
	public void setEvaluatorName(String evaluatorName) {
		this.evaluatorName = evaluatorName;
	}
	
	/**
	 * @return the scoreList
	 */
	public List<String> getScoreList() {
		return scoreList;
	}

	/**
	 * @param scoreList the scoreList to set
	 */
	public void setScoreList(List<String> scoreList) {
		this.scoreList = scoreList;
	}

	@Override
	public String toString() {
		return "PerformanceEvaluationCriteriaPojo [id=" + id + ", criteriaName=" + criteriaName + ", level=" + level + ", maximumScore=" + maximumScore + ", weightage=" + weightage + "]";
	}
	
}
