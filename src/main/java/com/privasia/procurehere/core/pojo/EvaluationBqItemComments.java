package com.privasia.procurehere.core.pojo;

import java.io.Serializable;

/**
 * @author Giridhar
 */
public class EvaluationBqItemComments implements Serializable {

	private static final long serialVersionUID = 8120178405423004998L;

	private String commentBy;
	private String comments;
	private String fileName;

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

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

}
