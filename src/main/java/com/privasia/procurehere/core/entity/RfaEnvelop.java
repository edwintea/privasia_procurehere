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
 * @author RT-Kapil
 */

@Entity
@Table(name = "PROC_RFA_ENVELOP")
public class RfaEnvelop extends Envelop implements Serializable {

	private static final long serialVersionUID = 424924961642948476L;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, optional = true, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "EVENT_ID", foreignKey = @ForeignKey(name = "FK_ENV_RFA_EVENT_ID"))
	private RfaEvent rfxEvent;

	@ManyToMany(fetch = FetchType.LAZY, cascade = { CascadeType.REFRESH })
	@JoinTable(name = "PROC_RFA_ENV_BQ", joinColumns = @JoinColumn(name = "EVENT_ID"), inverseJoinColumns = @JoinColumn(name = "BQ_ID"))
	private List<RfaEventBq> bqList;

	@ManyToMany(fetch = FetchType.LAZY, cascade = { CascadeType.REFRESH })
	@JoinTable(name = "PROC_RFA_ENV_CQ", joinColumns = @JoinColumn(name = "EVENT_ID"), inverseJoinColumns = @JoinColumn(name = "CQ_ID"))
	private List<RfaCq> cqList;

	@ManyToMany(fetch = FetchType.LAZY, cascade = { CascadeType.REFRESH })
	@JoinTable(name = "PROC_RFA_ENV_SOR", joinColumns = @JoinColumn(name = "EVENT_ID"), inverseJoinColumns = @JoinColumn(name = "SOR_ID"))
	private List<RfaEventSor> sorList;

	@JsonIgnore
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "envelope", orphanRemoval = true, cascade = CascadeType.ALL)
	private List<RfaEvaluatorUser> evaluators;

	@JsonIgnore
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "envelope", orphanRemoval = true, cascade = CascadeType.ALL)
	private List<RfaEvaluatorDeclaration> evaluatorDeclaration;

	@Transient
	private Integer envelopEmpty;

	@JsonIgnore
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "envelope", orphanRemoval = true, cascade = CascadeType.ALL)
	private List<RfaEnvelopeOpenerUser> openerUsers;

	public RfaEnvelop() {
		this.setIsOpen(Boolean.FALSE);
	}

	public RfaEnvelop(String id, String envelopTitle, EnvelopType envelopType, String userId, String name, String communicationEmail, boolean emailNotification,  String tenantId) {
		setId(id);
		setEnvelopTitle(envelopTitle);
		setEnvelopType(envelopType);
		User opener = new User(userId, name, communicationEmail, emailNotification,  tenantId);
		setOpener(opener);
	}

	public RfaEnvelop(String id, String envelopTitle, Boolean evaluationCompletedPrematurely) {
		setId(id);
		setEnvelopTitle(envelopTitle);
		setEvaluationCompletedPrematurely(evaluationCompletedPrematurely);

	}

	/**
	 * @return the evaluators
	 */
	public List<RfaEvaluatorUser> getEvaluators() {
		return evaluators;
	}

	/**
	 * @param evaluators the evaluators to set
	 */
	public void setEvaluators(List<RfaEvaluatorUser> evaluators) {
		if (this.evaluators == null) {
			this.evaluators = new ArrayList<RfaEvaluatorUser>();
		} else {
			if (evaluators != null) {
				for (RfaEvaluatorUser existingUser : this.evaluators) {
					for (RfaEvaluatorUser newUser : evaluators) {
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

	public RfaEnvelop copyFrom(RfaEvent rfaEvent) {
		RfaEnvelop newEnvelop = new RfaEnvelop();
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
			newEnvelop.setEvaluators(new ArrayList<RfaEvaluatorUser>());
			for (RfaEvaluatorUser eval : getEvaluators()) {
				RfaEvaluatorUser evalUser = eval.copyFrom();
				newEnvelop.getEvaluators().add(evalUser);
			}
		}
		if (CollectionUtil.isNotEmpty(getCqList())) {
			newEnvelop.setCqList(new ArrayList<RfaCq>());
			for (RfaCq cqList : getCqList()) {
				RfaCq newCqList = cqList.copyFrom();
				newEnvelop.getCqList().add(newCqList);
			}
		}

		if (CollectionUtil.isNotEmpty(getBqList())) {
			newEnvelop.setBqList(new ArrayList<RfaEventBq>());
			for (RfaEventBq bqList : getBqList()) {
				RfaEventBq newBqList = bqList.copyForRfa(false);
				newEnvelop.getBqList().add(newBqList);
			}
		}

		if (CollectionUtil.isNotEmpty(getSorList())) {
			newEnvelop.setSorList(new ArrayList<RfaEventSor>());
			for (RfaEventSor sorList : getSorList()) {
				RfaEventSor newSorList = sorList.copyForRfq(false);
				newEnvelop.getSorList().add(newSorList);
			}
		}


		if (CollectionUtil.isNotEmpty(getOpenerUsers())) {
			newEnvelop.setOpenerUsers(new ArrayList<RfaEnvelopeOpenerUser>());
			for (RfaEnvelopeOpenerUser opener : getOpenerUsers()) {
				RfaEnvelopeOpenerUser openerUser = opener.copyFrom();
				newEnvelop.getOpenerUsers().add(openerUser);
			}
		}
		return newEnvelop;
	}

	public RfaEnvelop createMobileShallowCopy() {
		RfaEnvelop env = new RfaEnvelop();
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
			for (RfaEventBq bq : getBqList()) {
				env.getBqs().add(bq.createMobileEnvelopShallowCopy());
			}
		}

		if (CollectionUtil.isNotEmpty(getCqList())) {
			env.setCqs(new ArrayList<Cq>());
			for (RfaCq cq : getCqList()) {
				env.getCqs().add(cq.createMobileEnvelopShallowCopy());
			}
		}

		if (CollectionUtil.isNotEmpty(getEvaluators())) {
			env.setEvaluatorUser(new ArrayList<EvaluatorUser>());
			for (RfaEvaluatorUser evaluator : getEvaluators()) {
				RfaEvaluatorUser evaluatorUser = evaluator.shallowCopyMoblileEvaluator();
				env.getEvaluatorUser().add(evaluatorUser);
			}
		}

		return env;
	}

	/**
	 * @return the rfxEvent
	 */
	public RfaEvent getRfxEvent() {
		return rfxEvent;
	}

	/**
	 * @param rfxEvent the rfxEvent to set
	 */
	public void setRfxEvent(RfaEvent rfxEvent) {
		this.rfxEvent = rfxEvent;
	}

	/**
	 * @return the bqList
	 */
	public List<RfaEventBq> getBqList() {
		return bqList;
	}

	/**
	 * @param bqList the bqList to set
	 */
	public void setBqList(List<RfaEventBq> bqList) {
		this.bqList = bqList;
	}

	/**
	 * @return the cqList
	 */
	public List<RfaCq> getCqList() {
		return cqList;
	}

	/**
	 * @param cqList the cqList to set
	 */
	public void setCqList(List<RfaCq> cqList) {
		this.cqList = cqList;
	}

	/**
	 * @return the evaluatorDeclaration
	 */
	public List<RfaEvaluatorDeclaration> getEvaluatorDeclaration() {
		return evaluatorDeclaration;
	}

	/**
	 * @param evaluatorDeclaration the evaluatorDeclaration to set
	 */
	public void setEvaluatorDeclaration(List<RfaEvaluatorDeclaration> evaluatorDeclaration) {
		this.evaluatorDeclaration = evaluatorDeclaration;
	}

	public List<RfaEventSor> getSorList() {
		return sorList;
	}

	public void setSorList(List<RfaEventSor> sorList) {
		this.sorList = sorList;
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
	public List<RfaEnvelopeOpenerUser> getOpenerUsers() {
		return openerUsers;
	}

	/**
	 * @param openerUsers the openerUsers to set
	 */
	public void setOpenerUsers(List<RfaEnvelopeOpenerUser> openerUsers) {
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

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "RfaEnvelop [" + super.toLogString() + "]";
	}

}
