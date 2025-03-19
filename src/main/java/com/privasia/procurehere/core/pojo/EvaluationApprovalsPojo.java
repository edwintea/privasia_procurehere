package com.privasia.procurehere.core.pojo;

import java.io.Serializable;
import java.util.List;

/**
 * @author Giridhar
 */
public class EvaluationApprovalsPojo implements Serializable {

	private static final long serialVersionUID = 562886950696108480L;
	private Integer level;
	private String active;
	private List<EvaluationAprovalUsersPojo> approvalUsers;

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
	 * @return the active
	 */
	public String getActive() {
		return active;
	}

	/**
	 * @param active the active to set
	 */
	public void setActive(String active) {
		this.active = active;
	}

	/**
	 * @return the approvalUsers
	 */
	public List<EvaluationAprovalUsersPojo> getApprovalUsers() {
		return approvalUsers;
	}

	/**
	 * @param approvalUsers the approvalUsers to set
	 */
	public void setApprovalUsers(List<EvaluationAprovalUsersPojo> approvalUsers) {
		this.approvalUsers = approvalUsers;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "EvaluationApprovalsPojo [level=" + level + ", active=" + active + ", approvalUsers=" + approvalUsers + "]";
	}

}
