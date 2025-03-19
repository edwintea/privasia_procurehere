package com.privasia.procurehere.core.pojo;

import java.io.Serializable;
import java.util.List;

public class EvaluationAprovalUsersPojo implements Serializable {

	private static final long serialVersionUID = 7282865333112627902L;

	private String name;
	private String type;
	private String status;
	private String imgPath;
	private Integer level;
	private String comments;
	private String commentDate;
	private List<EvaluationAprovalUsersPojo> approvalList;

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
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}

	/**
	 * @return the imgPath
	 */
	public String getImgPath() {
		return imgPath;
	}

	/**
	 * @param imgPath the imgPath to set
	 */
	public void setImgPath(String imgPath) {
		this.imgPath = imgPath;
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
	 * @return the commentDate
	 */
	public String getCommentDate() {
		return commentDate;
	}

	/**
	 * @param commentDate the commentDate to set
	 */
	public void setCommentDate(String commentDate) {
		this.commentDate = commentDate;
	}

	/**
	 * @return the approvalList
	 */
	public List<EvaluationAprovalUsersPojo> getApprovalList() {
		return approvalList;
	}

	/**
	 * @param approvalList the approvalList to set
	 */
	public void setApprovalList(List<EvaluationAprovalUsersPojo> approvalList) {
		this.approvalList = approvalList;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "EvaluationAprovalUsersPojo [name=" + name + ", type=" + type + ", status=" + status + ", imgPath=" + imgPath + ", level=" + level + ", approvalList=" + approvalList + "]";
	}

}
