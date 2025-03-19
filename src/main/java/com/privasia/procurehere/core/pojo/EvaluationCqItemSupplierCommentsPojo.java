/**
 * 
 */
package com.privasia.procurehere.core.pojo;

import java.io.Serializable;

/**
 * @author Nitin Otageri
 */
public class EvaluationCqItemSupplierCommentsPojo implements Serializable {

	private static final long serialVersionUID = 6387314982126111391L;

	private String commentBy;
	private String comment;

	/**
	 * @return the commentBy
	 */
	public String getCommentBy() {
		return commentBy;
	}

	/**
	 * @param commentBy the commentBy to set
	 */
	public void setCommentBy(String commentBy) {
		this.commentBy = commentBy;
	}

	/**
	 * @return the comment
	 */
	public String getComment() {
		return comment;
	}

	/**
	 * @param comment the comment to set
	 */
	public void setComment(String comment) {
		this.comment = comment;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "EvaluationCqItemSupplierCommentsPojo [commentBy=" + commentBy + ", comment=" + comment + "]";
	}

}
