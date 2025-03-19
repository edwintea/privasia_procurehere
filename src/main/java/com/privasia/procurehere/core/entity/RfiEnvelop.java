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
@Table(name = "PROC_RFI_ENVELOP")
public class RfiEnvelop extends Envelop implements Serializable {

	private static final long serialVersionUID = -1157956874845742685L;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, optional = true, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "EVENT_ID", foreignKey = @ForeignKey(name = "FK_ENV_RFI_EVENT_ID"))
	private RfiEvent rfxEvent;

	@ManyToMany(fetch = FetchType.LAZY, cascade = { CascadeType.REFRESH })
	@JoinTable(name = "PROC_RFI_ENV_CQ", joinColumns = @JoinColumn(name = "EVENT_ID"), inverseJoinColumns = @JoinColumn(name = "CQ_ID"))
	private List<RfiCq> cqList;


	@ManyToMany(fetch = FetchType.LAZY, cascade = { CascadeType.REFRESH })
	@JoinTable(name = "PROC_RFI_ENV_SOR", joinColumns = @JoinColumn(name = "EVENT_ID"), inverseJoinColumns = @JoinColumn(name = "SOR_ID"))
	private List<RfiEventSor> sorList;

	@JsonIgnore
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "envelope", orphanRemoval = true, cascade = CascadeType.ALL)
	private List<RfiEvaluatorUser> evaluators;

	@JsonIgnore
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "envelope", orphanRemoval = true, cascade = CascadeType.ALL)
	private List<RfiEvaluatorDeclaration> evaluatorDeclaration;

	@Transient
	private Integer envelopEmpty;

	@JsonIgnore
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "envelope", orphanRemoval = true, cascade = CascadeType.ALL)
	private List<RfiEnvelopeOpenerUser> openerUsers;

	public RfiEnvelop() {
		this.setIsOpen(Boolean.FALSE);
	}

	public RfiEnvelop(String id, String envelopTitle, EnvelopType envelopType, String userId, String name, String communicationEmail, boolean emailNotifications,  String tenantId) {
		setId(id);
		setEnvelopTitle(envelopTitle);
		setEnvelopType(envelopType);
		User opener = new User(userId, name, communicationEmail, emailNotifications, tenantId);
		setOpener(opener);
	}

	public RfiEnvelop(String id, String envelopTitle, Boolean evaluationCompletedPrematurely) {
		setId(id);
		setEnvelopTitle(envelopTitle);
		setEvaluationCompletedPrematurely(evaluationCompletedPrematurely);

	}

	public RfiEnvelop copyFrom(RfiEvent rfiEvent) {
		RfiEnvelop newEnvelop = new RfiEnvelop();
		newEnvelop.setEnvelopTitle(getEnvelopTitle());
		newEnvelop.setEnvelopSequence(getEnvelopSequence());
		newEnvelop.setPreFix(getPreFix());
		newEnvelop.setDescription(getDescription());
		// newEnvelop.setIsOpen(getIsOpen());
		newEnvelop.setLeadEvaluater(getLeadEvaluater());
		newEnvelop.setOpener(getOpener());
		newEnvelop.setEnvelopType(getEnvelopType());
		// it is for:PH 219
		if (EnvelopType.OPEN == newEnvelop.getEnvelopType()) {
			newEnvelop.setIsOpen(Boolean.TRUE);
			newEnvelop.setOpener(null);
		}

		if (CollectionUtil.isNotEmpty(getEvaluators())) {
			newEnvelop.setEvaluators(new ArrayList<RfiEvaluatorUser>());
			for (RfiEvaluatorUser eval : getEvaluators()) {
				RfiEvaluatorUser evalUser = eval.copyFrom();
				newEnvelop.getEvaluators().add(evalUser);
			}
		}
		if (CollectionUtil.isNotEmpty(getCqList())) {
			newEnvelop.setCqList(new ArrayList<RfiCq>());
			for (RfiCq cqList : getCqList()) {
				RfiCq newCqList = cqList.copyFrom();
				newEnvelop.getCqList().add(newCqList);
			}
		}

		if (CollectionUtil.isNotEmpty(getSorList())) {
			newEnvelop.setSorList(new ArrayList<RfiEventSor>());
			for (RfiEventSor sorList : getSorList()) {
				RfiEventSor newSorList = sorList.copyForRfq(false);
				newEnvelop.getSorList().add(newSorList);
			}
		}

		if (CollectionUtil.isNotEmpty(getOpenerUsers())) {
			newEnvelop.setOpenerUsers(new ArrayList<RfiEnvelopeOpenerUser>());
			for (RfiEnvelopeOpenerUser opener : getOpenerUsers()) {
				RfiEnvelopeOpenerUser openerUser = opener.copyFrom();
				newEnvelop.getOpenerUsers().add(openerUser);
			}
		}

		return newEnvelop;
	}

	public RfiEnvelop createMobileShallowCopy() {
		RfiEnvelop env = new RfiEnvelop();
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

		if (CollectionUtil.isNotEmpty(getCqList())) {
			env.setCqs(new ArrayList<Cq>());
			for (RfiCq cqList : getCqList()) {
				RfiCq newCqList = cqList.createMobileEnvelopShallowCopy();
				env.getCqs().add(newCqList);
			}
		}

		if (CollectionUtil.isNotEmpty(getEvaluators())) {
			env.setEvaluatorUser(new ArrayList<EvaluatorUser>());
			for (RfiEvaluatorUser evaluator : getEvaluators()) {
				RfiEvaluatorUser evaluatorUser = evaluator.shallowCopyMoblileEvaluator();
				env.getEvaluatorUser().add(evaluatorUser);
			}
		}

		return env;
	}

	/**
	 * @return the rfxEvent
	 */
	public RfiEvent getRfxEvent() {
		return rfxEvent;
	}

	/**
	 * @param rfxEvent the rfxEvent to set
	 */
	public void setRfxEvent(RfiEvent rfxEvent) {
		this.rfxEvent = rfxEvent;
	}

	/**
	 * @return the cqList
	 */
	public List<RfiCq> getCqList() {
		return cqList;
	}

	/**
	 * @param cqList the cqList to set
	 */
	public void setCqList(List<RfiCq> cqList) {
		this.cqList = cqList;
	}

	/**
	 * @return the evaluators
	 */
	public List<RfiEvaluatorUser> getEvaluators() {
		return evaluators;
	}

	/**
	 * @param evaluators the evaluators to set
	 */
	public void setEvaluators(List<RfiEvaluatorUser> evaluators) {
		if (this.evaluators == null) {
			this.evaluators = new ArrayList<RfiEvaluatorUser>();
		} else {
			if (evaluators != null) {
				for (RfiEvaluatorUser existingUser : this.evaluators) {
					for (RfiEvaluatorUser newUser : evaluators) {
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
	 * @return the evaluatorDeclaration
	 */
	public List<RfiEvaluatorDeclaration> getEvaluatorDeclaration() {
		return evaluatorDeclaration;
	}

	/**
	 * @param evaluatorDeclaration the evaluatorDeclaration to set
	 */
	public void setEvaluatorDeclaration(List<RfiEvaluatorDeclaration> evaluatorDeclaration) {
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
	public List<RfiEnvelopeOpenerUser> getOpenerUsers() {
		return openerUsers;
	}


	public List<RfiEventSor> getSorList() {
		return sorList;
	}

	public void setSorList(List<RfiEventSor> sorList) {
		this.sorList = sorList;
	}

	/**
	 * @param openerUsers the openerUsers to set
	 */
	public void setOpenerUsers(List<RfiEnvelopeOpenerUser> openerUsers) {
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
		return "RfiEnvelop [" + super.toLogString() + "]";
	}

}
