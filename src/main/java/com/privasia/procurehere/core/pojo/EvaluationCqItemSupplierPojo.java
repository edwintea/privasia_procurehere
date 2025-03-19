/**
 * 
 */
package com.privasia.procurehere.core.pojo;

import java.io.Serializable;
import java.util.List;

/**
 * @author Nitin Otageri
 */
public class EvaluationCqItemSupplierPojo implements Serializable {

	private static final long serialVersionUID = -1310639066895549502L;

	private String supplierName;
	private String answer;
	private String attachments;
	private List<EvaluationCqItemSupplierCommentsPojo> comments;
	private String evaluatorComments;

	private String evalComment;
	private Integer scores;

	/**
	 * @return the supplierName
	 */
	public String getSupplierName() {
		return supplierName;
	}

	/**
	 * @param supplierName the supplierName to set
	 */
	public void setSupplierName(String supplierName) {
		this.supplierName = supplierName;
	}

	/**
	 * @return the answer
	 */
	public String getAnswer() {
		return answer;
	}

	/**
	 * @param answer the answer to set
	 */
	public void setAnswer(String answer) {
		this.answer = answer;
	}

	/**
	 * @return the attachments
	 */
	public String getAttachments() {
		return attachments;
	}

	/**
	 * @param attachments the attachments to set
	 */
	public void setAttachments(String attachments) {
		this.attachments = attachments;
	}

	/**
	 * @return the comments
	 */
	public List<EvaluationCqItemSupplierCommentsPojo> getComments() {
		return comments;
	}

	/**
	 * @param comments the comments to set
	 */
	public void setComments(List<EvaluationCqItemSupplierCommentsPojo> comments) {
		this.comments = comments;
	}

	/**
	 * @return the evaluatorComments
	 */
	public String getEvaluatorComments() {
		return evaluatorComments;
	}

	/**
	 * @param evaluatorComments the evaluatorComments to set
	 */
	public void setEvaluatorComments(String evaluatorComments) {
		this.evaluatorComments = evaluatorComments;
	}

	/**
	 * @return the evalComment
	 */
	public String getEvalComment() {
		return evalComment;
	}

	/**
	 * @param evalComment the evalComment to set
	 */
	public void setEvalComment(String evalComment) {
		this.evalComment = evalComment;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "EvaluationCqItemSupplierPojo [supplierName=" + supplierName + ", answer=" + answer + ", attachments=" + attachments + ", comments=" + comments + "]";
	}

	/**
	 * @return the scores
	 */
	public Integer getScores() {
		return scores;
	}

	/**
	 * @param scores the scores to set
	 */
	public void setScores(Integer scores) {
		this.scores = scores;
	}

}
