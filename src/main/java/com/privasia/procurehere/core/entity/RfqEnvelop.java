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
@Table(name = "PROC_RFQ_ENVELOP")
public class RfqEnvelop extends Envelop implements Serializable {

	private static final long serialVersionUID = 2192482226191578165L;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, optional = true, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "EVENT_ID", foreignKey = @ForeignKey(name = "FK_ENV_RFQ_EVENT_ID"))
	private RfqEvent rfxEvent;

	@ManyToMany(fetch = FetchType.LAZY, cascade = { CascadeType.REFRESH })
	@JoinTable(name = "PROC_RFQ_ENV_BQ", joinColumns = @JoinColumn(name = "EVENT_ID"), inverseJoinColumns = @JoinColumn(name = "BQ_ID"))
	private List<RfqEventBq> bqList;

	@ManyToMany(fetch = FetchType.LAZY, cascade = { CascadeType.REFRESH })
	@JoinTable(name = "PROC_RFQ_ENV_SOR", joinColumns = @JoinColumn(name = "EVENT_ID"), inverseJoinColumns = @JoinColumn(name = "SOR_ID"))
	private List<RfqEventSor> sorList;

	@ManyToMany(fetch = FetchType.LAZY, cascade = { CascadeType.REFRESH })
	@JoinTable(name = "PROC_RFQ_ENV_CQ", joinColumns = @JoinColumn(name = "EVENT_ID"), inverseJoinColumns = @JoinColumn(name = "CQ_ID"))
	private List<RfqCq> cqList;

	@JsonIgnore
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "envelope", orphanRemoval = true, cascade = CascadeType.ALL)
	private List<RfqEvaluatorUser> evaluators;

	@JsonIgnore
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "envelope", orphanRemoval = true, cascade = CascadeType.ALL)
	private List<RfqEvaluatorDeclaration> evaluatorDeclaration;

	@Transient
	private Integer envelopEmpty;

	@JsonIgnore
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "envelope", orphanRemoval = true, cascade = CascadeType.ALL)
	private List<RfqEnvelopeOpenerUser> openerUsers;

	public RfqEnvelop() {
		this.setIsOpen(Boolean.FALSE);
	}

	public RfqEnvelop(String id, String envelopTitle, EnvelopType envelopType, String userId, String name, String communicationEmail, boolean emailNotifications, String tenantId) {
		setId(id);
		setEnvelopTitle(envelopTitle);
		setEnvelopType(envelopType);
		User opener = new User(userId, name, communicationEmail, emailNotifications, tenantId);
		setOpener(opener);
	}

	public RfqEnvelop(String id, String envelopTitle, Boolean evaluationCompletedPrematurely) {
		setId(id);
		setEnvelopTitle(envelopTitle);
		setEvaluationCompletedPrematurely(evaluationCompletedPrematurely);

	}

	public RfqEnvelop createMobileShallowCopy() {
		RfqEnvelop env = new RfqEnvelop();
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
			for (RfqEventBq bqList : getBqList()) {
				RfqEventBq newBqList = bqList.createMobileEnvelopShallowCopy();
				env.getBqs().add(newBqList);
			}
		}

		if (CollectionUtil.isNotEmpty(getSorList())) {
			env.setSors(new ArrayList<Sor>());
			for (RfqEventSor bqList : getSorList()) {
				RfqEventSor newBqList = bqList.createMobileEnvelopShallowCopy();
				env.getSors().add(newBqList);
			}
		}

		if (CollectionUtil.isNotEmpty(getCqList())) {
			env.setCqs(new ArrayList<Cq>());
			for (RfqCq cqList : getCqList()) {
				RfqCq newCqList = cqList.createMobileEnvelopShallowCopy();
				env.getCqs().add(newCqList);
			}
		}

		if (CollectionUtil.isNotEmpty(getEvaluators())) {
			env.setEvaluatorUser(new ArrayList<EvaluatorUser>());
			for (RfqEvaluatorUser evaluator : getEvaluators()) {
				RfqEvaluatorUser evaluatorUser = evaluator.shallowCopyMoblileEvaluator();
				env.getEvaluatorUser().add(evaluatorUser);
			}
		}

		return env;
	}

	/**
	 * @return the evaluators
	 */
	public List<RfqEvaluatorUser> getEvaluators() {
		return evaluators;
	}

	/**
	 * @param evaluators the evaluators to set
	 */
	public void setEvaluators(List<RfqEvaluatorUser> evaluators) {
		if (this.evaluators == null) {
			this.evaluators = new ArrayList<RfqEvaluatorUser>();
		} else {
			if (evaluators != null) {
				for (RfqEvaluatorUser existingUser : this.evaluators) {
					for (RfqEvaluatorUser newUser : evaluators) {
						if (newUser.getId() == null) {
							continue;
						}
						if (newUser.getId().equals(existingUser.getId())) {
							newUser.setEvaluationDate(existingUser.getEvaluationDate());
							newUser.setEvaluationStatus(existingUser.getEvaluationStatus());
							newUser.setEnvelope(existingUser.getEnvelope());
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

	public RfqEnvelop copyFrom(RfqEvent rftEvent) {
		RfqEnvelop newEnvelop = new RfqEnvelop();
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
			newEnvelop.setEvaluators(new ArrayList<RfqEvaluatorUser>());
			for (RfqEvaluatorUser eval : getEvaluators()) {
				RfqEvaluatorUser evalUser = eval.copyFrom();
				newEnvelop.getEvaluators().add(evalUser);
			}
		}
		if (CollectionUtil.isNotEmpty(getCqList())) {
			newEnvelop.setCqList(new ArrayList<RfqCq>());
			for (RfqCq cqList : getCqList()) {
				RfqCq newCqList = cqList.copyFrom();
				newEnvelop.getCqList().add(newCqList);
			}
		}

		if (CollectionUtil.isNotEmpty(getBqList())) {
			newEnvelop.setBqList(new ArrayList<RfqEventBq>());
			for (RfqEventBq bqList : getBqList()) {
				RfqEventBq newBqList = bqList.copyForRfq(false);
				newEnvelop.getBqList().add(newBqList);
			}
		}

		if (CollectionUtil.isNotEmpty(getSorList())) {
			newEnvelop.setSorList(new ArrayList<RfqEventSor>());
			for (RfqEventSor sorList : getSorList()) {
				RfqEventSor newSorList = sorList.copyForRfq(false);
				newEnvelop.getSorList().add(newSorList);
			}
		}

		if (CollectionUtil.isNotEmpty(getOpenerUsers())) {
			newEnvelop.setOpenerUsers(new ArrayList<RfqEnvelopeOpenerUser>());
			for (RfqEnvelopeOpenerUser opener : getOpenerUsers()) {
				RfqEnvelopeOpenerUser openerUser = opener.copyFrom();
				newEnvelop.getOpenerUsers().add(openerUser);
			}
		}

		return newEnvelop;
	}

	/**
	 * @return the rfxEvent
	 */
	public RfqEvent getRfxEvent() {
		return rfxEvent;
	}

	/**
	 * @param rfxEvent the rfxEvent to set
	 */
	public void setRfxEvent(RfqEvent rfxEvent) {
		this.rfxEvent = rfxEvent;
	}

	/**
	 * @return the bqList
	 */
	public List<RfqEventBq> getBqList() {
		return bqList;
	}

	/**
	 * @param bqList the bqList to set
	 */
	public void setBqList(List<RfqEventBq> bqList) {
		this.bqList = bqList;
	}

	/**
	 * @return the cqList
	 */
	public List<RfqCq> getCqList() {
		return cqList;
	}

	/**
	 * @param cqList the cqList to set
	 */
	public void setCqList(List<RfqCq> cqList) {
		this.cqList = cqList;
	}

	/**
	 * @return the evaluatorDeclaration
	 */
	public List<RfqEvaluatorDeclaration> getEvaluatorDeclaration() {
		return evaluatorDeclaration;
	}

	/**
	 * @param evaluatorDeclaration the evaluatorDeclaration to set
	 */
	public void setEvaluatorDeclaration(List<RfqEvaluatorDeclaration> evaluatorDeclaration) {
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
	public List<RfqEnvelopeOpenerUser> getOpenerUsers() {
		return openerUsers;
	}

	/**
	 * @param openerUsers the openerUsers to set
	 */
	public void setOpenerUsers(List<RfqEnvelopeOpenerUser> openerUsers) {
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


	public List<RfqEventSor> getSorList() {
		return sorList;
	}

	public void setSorList(List<RfqEventSor> sorList) {
		this.sorList = sorList;
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
		return "RfpEnvelop [" + super.toLogString() + "]";
	}

}
