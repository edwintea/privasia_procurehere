package com.privasia.procurehere.core.pojo;

import java.io.Serializable;

import com.privasia.procurehere.core.entity.User;

/**
 * @author Nitin Otageri
 */
public class EventPermissions implements Serializable {

	private static final long serialVersionUID = -1483709935552542023L;

	private boolean viewer;
	private boolean editor;
	private boolean owner;
	private boolean opener;
	private boolean leadEvaluator;
	private boolean evaluator;
	private boolean approver;
	private boolean activeApproval;
	private boolean approverUser;
	private boolean requesterUser;
	private boolean unMaskUser;
	private boolean prDraft;
	private String prId;
	private boolean revertBidUser;
	private boolean evaluationConclusionUser;

	private boolean conclusionUser;

	private boolean allUserConcludedPermatury;

	private boolean activeSuspensionApproval;

	private boolean activeAwardApproval;

	private boolean awardApprover;

	public boolean isUnMaskUser() {
		return unMaskUser;
	}

	public void setUnMaskUser(boolean unMaskUser) {
		this.unMaskUser = unMaskUser;
	}

	private User user;

	/**
	 * @return the viewerte
	 */
	public boolean isViewer() {
		return viewer;
	}

	/**
	 * @param viewer the viewer to set
	 */
	public void setViewer(boolean viewer) {
		this.viewer = viewer;
	}

	/**
	 * @return the editor
	 */
	public boolean isEditor() {
		return editor;
	}

	/**
	 * @param editor the editor to set
	 */
	public void setEditor(boolean editor) {
		this.editor = editor;
	}

	/**
	 * @return the owner
	 */
	public boolean isOwner() {
		return owner;
	}

	/**
	 * @param owner the owner to set
	 */
	public void setOwner(boolean owner) {
		this.owner = owner;
	}

	/**
	 * @return the opener
	 */
	public boolean isOpener() {
		return opener;
	}

	/**
	 * @param opener the opener to set
	 */
	public void setOpener(boolean opener) {
		this.opener = opener;
	}

	/**
	 * @return the leadEvaluator
	 */
	public boolean isLeadEvaluator() {
		return leadEvaluator;
	}

	/**
	 * @param leadEvaluator the leadEvaluator to set
	 */
	public void setLeadEvaluator(boolean leadEvaluator) {
		this.leadEvaluator = leadEvaluator;
	}

	/**
	 * @return the evaluator
	 */
	public boolean isEvaluator() {
		return evaluator;
	}

	/**
	 * @param evaluator the evaluator to set
	 */
	public void setEvaluator(boolean evaluator) {
		this.evaluator = evaluator;
	}

	/**
	 * @return the approver
	 */
	public boolean isApprover() {
		return approver;
	}

	/**
	 * @param approver the approver to set
	 */
	public void setApprover(boolean approver) {
		this.approver = approver;
	}

	/**
	 * @return the user
	 */
	public User getUser() {
		return user;
	}

	/**
	 * @param user the user to set
	 */
	public void setUser(User user) {
		this.user = user;
	}

	/**
	 * @return the activeApproval
	 */
	public boolean isActiveApproval() {
		return activeApproval;
	}

	/**
	 * @param activeApproval the activeApproval to set
	 */
	public void setActiveApproval(boolean activeApproval) {
		this.activeApproval = activeApproval;
	}

	/**
	 * @return the approverUser
	 */
	public boolean isApproverUser() {
		return approverUser;
	}

	/**
	 * @param approverUser the approverUser to set
	 */
	public void setApproverUser(boolean approverUser) {
		this.approverUser = approverUser;
	}

	public boolean isRequesterUser() {
		return requesterUser;
	}

	public void setRequesterUser(boolean requesterUser) {
		this.requesterUser = requesterUser;
	}

	public boolean isPrDraft() {
		return prDraft;
	}

	public void setPrDraft(boolean prDraft) {
		this.prDraft = prDraft;
	}

	public String getPrId() {
		return prId;
	}

	public void setPrId(String prId) {
		this.prId = prId;
	}

	public boolean isRevertBidUser() {
		return revertBidUser;
	}

	public void setRevertBidUser(boolean revertBidUser) {
		this.revertBidUser = revertBidUser;
	}

	/**
	 * @return the evaluationConclusionUser
	 */
	public boolean isEvaluationConclusionUser() {
		return evaluationConclusionUser;
	}

	/**
	 * @param evaluationConclusionUser the evaluationConclusionUser to set
	 */
	public void setEvaluationConclusionUser(boolean evaluationConclusionUser) {
		this.evaluationConclusionUser = evaluationConclusionUser;
	}

	/**
	 * @return the conclusionUser
	 */
	public boolean isConclusionUser() {
		return conclusionUser;
	}

	/**
	 * @param conclusionUser the conclusionUser to set
	 */
	public void setConclusionUser(boolean conclusionUser) {
		this.conclusionUser = conclusionUser;
	}

	/**
	 * @return the allUserConcludedPermatury
	 */
	public boolean isAllUserConcludedPermatury() {
		return allUserConcludedPermatury;
	}

	/**
	 * @param allUserConcludedPermatury the allUserConcludedPermatury to set
	 */
	public void setAllUserConcludedPermatury(boolean allUserConcludedPermatury) {
		this.allUserConcludedPermatury = allUserConcludedPermatury;
	}

	/**
	 * @return the activeSuspensionApproval
	 */
	public boolean isActiveSuspensionApproval() {
		return activeSuspensionApproval;
	}

	/**
	 * @param activeSuspensionApproval the activeSuspensionApproval to set
	 */
	public void setActiveSuspensionApproval(boolean activeSuspensionApproval) {
		this.activeSuspensionApproval = activeSuspensionApproval;
	}

	/**
	 * @return the activeAwardApproval
	 */
	public boolean isActiveAwardApproval() {
		return activeAwardApproval;
	}

	/**
	 * @param activeAwardApproval the activeAwardApproval to set
	 */
	public void setActiveAwardApproval(boolean activeAwardApproval) {
		this.activeAwardApproval = activeAwardApproval;
	}

	/**
	 * @return the awardApprover
	 */
	public boolean isAwardApprover() {
		return awardApprover;
	}

	/**
	 * @param awardApprover the awardApprover to set
	 */
	public void setAwardApprover(boolean awardApprover) {
		this.awardApprover = awardApprover;
	}

	public String toLogString() {
		return "EventPermissions [viewer=" + viewer + ", editor=" + editor + ", owner=" + owner + ", opener=" + opener + ", leadEvaluator=" + leadEvaluator + ", evaluator=" + evaluator + ", approver=" + approver + ", activeApproval=" + activeApproval + "]";
	}

}
