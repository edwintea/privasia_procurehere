/**
 * 
 */
package com.privasia.procurehere.core.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.privasia.procurehere.core.enums.EnvelopType;
import com.privasia.procurehere.core.utils.CollectionUtil;

/**
 * @author Ravi
 */

@Entity
@Table(name = "PROC_RFP_ENVELOP")
public class RfpEnvelop extends Envelop implements Serializable {

	private static final long serialVersionUID = 8022392685863245000L;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, optional = true, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "EVENT_ID", foreignKey = @ForeignKey(name = "FK_ENV_RFP_EVENT_ID"))
	private RfpEvent rfxEvent;

	@ManyToMany(fetch = FetchType.LAZY, cascade = { CascadeType.REFRESH })
	@JoinTable(name = "PROC_RFP_ENV_BQ", joinColumns = @JoinColumn(name = "EVENT_ID"), inverseJoinColumns = @JoinColumn(name = "BQ_ID"))
	private List<RfpEventBq> bqList;

	@ManyToMany(fetch = FetchType.LAZY, cascade = { CascadeType.REFRESH })
	@JoinTable(name = "PROC_RFP_ENV_SOR", joinColumns = @JoinColumn(name = "EVENT_ID"), inverseJoinColumns = @JoinColumn(name = "SOR_ID"))
	private List<RfpEventSor> sorList;

	@ManyToMany(fetch = FetchType.LAZY, cascade = { CascadeType.REFRESH })
	@JoinTable(name = "PROC_RFP_ENV_CQ", joinColumns = @JoinColumn(name = "EVENT_ID"), inverseJoinColumns = @JoinColumn(name = "CQ_ID"))
	private List<RfpCq> cqList;

	@JsonIgnore
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "envelope", orphanRemoval = true, cascade = CascadeType.ALL)
	private List<RfpEvaluatorUser> evaluators;

	@JsonIgnore
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "envelope", orphanRemoval = true, cascade = CascadeType.ALL)
	private List<RfpEvaluatorDeclaration> evaluatorDeclaration;

	@Transient
	private Integer envelopEmpty;

	@JsonIgnore
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "envelope", orphanRemoval = true, cascade = CascadeType.ALL)
	private List<RfpEnvelopeOpenerUser> openerUsers;

	public RfpEnvelop() {
		this.setIsOpen(Boolean.FALSE);
	}

	public RfpEnvelop(String id, String envelopTitle, EnvelopType envelopType, String userId, String name, String communicationEmail, boolean emailNotifications, String tenantId) {
		setId(id);
		setEnvelopTitle(envelopTitle);
		setEnvelopType(envelopType);
		User opener = new User(userId, name, communicationEmail, emailNotifications, tenantId);
		setOpener(opener);
	}

	public RfpEnvelop(String id, String envelopTitle, Boolean evaluationCompletedPrematurely) {
		setId(id);
		setEnvelopTitle(envelopTitle);
		setEvaluationCompletedPrematurely(evaluationCompletedPrematurely);

	}

	public RfpEnvelop copyFrom() {
		RfpEnvelop newEnvelop = new RfpEnvelop();
		newEnvelop.setEnvelopTitle(getEnvelopTitle());
		newEnvelop.setEnvelopSequence(getEnvelopSequence());
		newEnvelop.setPreFix(getPreFix());
		newEnvelop.setDescription(getDescription());
		newEnvelop.setLeadEvaluater(getLeadEvaluater());
		newEnvelop.setOpener(getOpener());
		newEnvelop.setEnvelopType(getEnvelopType());
		// it is for:PH 219
		if (EnvelopType.OPEN == newEnvelop.getEnvelopType()) {
			newEnvelop.setIsOpen(Boolean.TRUE);
			newEnvelop.setOpener(null);
		}

		if (CollectionUtil.isNotEmpty(getEvaluators())) {
			newEnvelop.setEvaluators(new ArrayList<RfpEvaluatorUser>());
			for (RfpEvaluatorUser eval : getEvaluators()) {
				RfpEvaluatorUser evalUser = eval.copyFrom();
				newEnvelop.getEvaluators().add(evalUser);
			}
		}

		if (CollectionUtil.isNotEmpty(getCqList())) {
			newEnvelop.setCqList(new ArrayList<RfpCq>());
			for (RfpCq cqList : getCqList()) {
				RfpCq newCqList = cqList.copyFrom();
				newEnvelop.getCqList().add(newCqList);
			}
		}

		if (CollectionUtil.isNotEmpty(getBqList())) {
			newEnvelop.setBqList(new ArrayList<RfpEventBq>());
			for (RfpEventBq bqList : getBqList()) {
				RfpEventBq newBqList = bqList.copyForRfp(false);
				newEnvelop.getBqList().add(newBqList);
			}
		}

		if (CollectionUtil.isNotEmpty(getSorList())) {
			newEnvelop.setSorList(new ArrayList<RfpEventSor>());
			for (RfpEventSor sorList : getSorList()) {
				RfpEventSor newSorList = sorList.copyForRfq(false);
				newEnvelop.getSorList().add(newSorList);
			}
		}

		if (CollectionUtil.isNotEmpty(getOpenerUsers())) {
			newEnvelop.setOpenerUsers(new ArrayList<RfpEnvelopeOpenerUser>());
			for (RfpEnvelopeOpenerUser opener : getOpenerUsers()) {
				RfpEnvelopeOpenerUser openerUser = opener.copyFrom();
				newEnvelop.getOpenerUsers().add(openerUser);
			}
		}
		return newEnvelop;
	}

	public RfpEnvelop createMobileShallowCopy() {
		RfpEnvelop env = new RfpEnvelop();
		env.setId(getId());
		env.setEnvelopTitle(getEnvelopTitle());
		env.setEnvelopSequence(getEnvelopSequence());
		env.setEnvelopType(getEnvelopType());
		env.setIsOpen(getIsOpen());
		env.setOpenDate(getOpenDate());
		env.setEvaluationStatus(getEvaluationStatus());
		env.setAllowOpen(isAllowOpen());
		env.setEnvelopEvaluationOwner(getLeadEvaluater() != null ? getLeadEvaluater().getName() : null);
		if (env.getEnvelopType() == EnvelopType.CLOSED) {
			env.setEnvelopOpenerName(getOpener() != null ? getOpener().getName() : null);
		}
		if (CollectionUtil.isNotEmpty(getBqList())) {
			env.setBqs(new ArrayList<Bq>());
			for (RfpEventBq bqList : getBqList()) {
				RfpEventBq newBqList = bqList.createMobileEnvelopShallowCopy();
				env.getBqs().add(newBqList);
			}
		}

		if (CollectionUtil.isNotEmpty(getCqList())) {
			env.setCqs(new ArrayList<Cq>());
			for (RfpCq cqList : getCqList()) {
				RfpCq newCqList = cqList.createMobileEnvelopShallowCopy();
				env.getCqs().add(newCqList);
			}
		}

		if (CollectionUtil.isNotEmpty(getEvaluators())) {
			env.setEvaluatorUser(new ArrayList<EvaluatorUser>());
			for (RfpEvaluatorUser evaluator : getEvaluators()) {
				RfpEvaluatorUser evaluatorUser = evaluator.shallowCopyMoblileEvaluator();
				env.getEvaluatorUser().add(evaluatorUser);
			}
		}

		return env;
	}

	/**
	 * @return the evaluators
	 */
	public List<RfpEvaluatorUser> getEvaluators() {
		return evaluators;
	}

	/**
	 * @param evaluators the evaluators to set
	 */
	public void setEvaluators(List<RfpEvaluatorUser> evaluators) {
		if (this.evaluators == null) {
			this.evaluators = new ArrayList<RfpEvaluatorUser>();
		} else {
			if (evaluators != null) {
				for (RfpEvaluatorUser existingUser : this.evaluators) {
					for (RfpEvaluatorUser newUser : evaluators) {
						if (newUser.getId() == null) {
							continue;
						}
						if (newUser.getId().equals(existingUser.getId())) {
							newUser.setEvaluationDate(existingUser.getEvaluationDate());
							newUser.setEvaluationStatus(existingUser.getEvaluationStatus());
						}
					}
				}
			}
			if (this.evaluators != evaluators) {
				this.evaluators.clear();
			}
		}
		if (this.evaluators != evaluators) {
			if (evaluators != null) {
				this.evaluators.addAll(evaluators);
			}
		}
	}

	/**
	 * @return the rfxEvent
	 */
	public RfpEvent getRfxEvent() {
		return rfxEvent;
	}

	/**
	 * @param rfxEvent the rfxEvent to set
	 */
	public void setRfxEvent(RfpEvent rfxEvent) {
		this.rfxEvent = rfxEvent;
	}

	/**
	 * @return the bqList
	 */
	public List<RfpEventBq> getBqList() {
		return bqList;
	}

	/**
	 * @param bqList the bqList to set
	 */
	public void setBqList(List<RfpEventBq> bqList) {
		this.bqList = bqList;
	}

	/**
	 * @return the cqList
	 */
	public List<RfpCq> getCqList() {
		return cqList;
	}

	/**
	 * @param cqList the cqList to set
	 */
	public void setCqList(List<RfpCq> cqList) {
		this.cqList = cqList;
	}

	/**
	 * @return the evaluatorDeclaration
	 */
	public List<RfpEvaluatorDeclaration> getEvaluatorDeclaration() {
		return evaluatorDeclaration;
	}

	/**
	 * @param evaluatorDeclaration the evaluatorDeclaration to set
	 */
	public void setEvaluatorDeclaration(List<RfpEvaluatorDeclaration> evaluatorDeclaration) {
		this.evaluatorDeclaration = evaluatorDeclaration;
	}

	/**
	 * @return the envelopEmpty
	 */
	public Integer getEnvelopEmpty() {
		return envelopEmpty;
	}

	/**
	 * @param envelopEmpty the envelopEmpty to set
	 */
	public void setEnvelopEmpty(Integer envelopEmpty) {
		this.envelopEmpty = envelopEmpty;
	}

	/**
	 * @return the openerUsers
	 */
	public List<RfpEnvelopeOpenerUser> getOpenerUsers() {
		return openerUsers;
	}

	/**
	 * @param openerUsers the openerUsers to set
	 */
	public void setOpenerUsers(List<RfpEnvelopeOpenerUser> openerUsers) {
		if (this.openerUsers != null) {
			if (this.openerUsers != openerUsers) {
				this.openerUsers.clear();
				if (CollectionUtil.isNotEmpty(openerUsers)) {
					this.openerUsers.addAll(openerUsers);
				}
			}
		} else {
			this.openerUsers = openerUsers;
		}
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return super.hashCode();
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		return super.equals(obj);
	}


	public List<RfpEventSor> getSorList() {
		return sorList;
	}

	public void setSorList(List<RfpEventSor> sorList) {
		this.sorList = sorList;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "RfpEnvelop [" + super.toLogString() + "]";
	}

}
