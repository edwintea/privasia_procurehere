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
@Table(name = "PROC_RFT_ENVELOP")
public class RftEnvelop extends Envelop implements Serializable {

	private static final long serialVersionUID = -8965035187574333394L;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, optional = true, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "EVENT_ID", foreignKey = @ForeignKey(name = "FK_ENV_RFT_EVENT_ID"))
	private RftEvent rfxEvent;

	@ManyToMany(fetch = FetchType.LAZY, cascade = { CascadeType.REFRESH })
	@JoinTable(name = "PROC_RFT_ENV_BQ", joinColumns = @JoinColumn(name = "ENVELOP_ID"), inverseJoinColumns = @JoinColumn(name = "BQ_ID"))
	private List<RftEventBq> bqList;

	@ManyToMany(fetch = FetchType.LAZY, cascade = { CascadeType.REFRESH })
	@JoinTable(name = "PROC_RFT_ENV_SOR", joinColumns = @JoinColumn(name = "EVENT_ID"), inverseJoinColumns = @JoinColumn(name = "SOR_ID"))
	private List<RftEventSor> sorList;

	@ManyToMany(fetch = FetchType.LAZY, cascade = { CascadeType.REFRESH })
	@JoinTable(name = "PROC_RFT_ENV_CQ", joinColumns = @JoinColumn(name = "ENVELOP_ID"), inverseJoinColumns = @JoinColumn(name = "CQ_ID"))
	private List<RftCq> cqList;

	@JsonIgnore
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "envelope", orphanRemoval = true, cascade = CascadeType.ALL)
	private List<RftEvaluatorUser> evaluators;

	@JsonIgnore
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "envelope", orphanRemoval = true, cascade = CascadeType.ALL)
	private List<RftEvaluatorDeclaration> evaluatorDeclaration;

	@Transient
	private Integer envelopEmpty;

	@JsonIgnore
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "envelope", orphanRemoval = true, cascade = CascadeType.ALL)
	private List<RftEnvelopeOpenerUser> openerUsers;

	public RftEnvelop() {
		this.setIsOpen(Boolean.FALSE);
	}

	public RftEnvelop(String id, String envelopTitle, EnvelopType envelopType, String userId, String name, String communicationEmail, boolean emailNotifications, String tenantId) {
		setId(id);
		setEnvelopTitle(envelopTitle);
		setEnvelopType(envelopType);
		User opener = new User(userId, name, communicationEmail, emailNotifications, tenantId);
		setOpener(opener);
	}

	public RftEnvelop(String id, String envelopTitle, Boolean evaluationCompletedPrematurely) {
		setId(id);
		setEnvelopTitle(envelopTitle);
		setEvaluationCompletedPrematurely(evaluationCompletedPrematurely);

	}

	public RftEnvelop copyFrom() {
		RftEnvelop newEnvelop = new RftEnvelop();
		newEnvelop.setEnvelopTitle(getEnvelopTitle());
		newEnvelop.setEnvelopSequence(getEnvelopSequence());
		newEnvelop.setPreFix(getPreFix());
		newEnvelop.setDescription(getDescription());
		newEnvelop.setLeadEvaluater(getLeadEvaluater());
		newEnvelop.setOpener(getOpener());
		// newEnvelop.setIsOpen(getIsOpen());

		newEnvelop.setEnvelopType(getEnvelopType());
		// it is for:PH 219
		if (EnvelopType.OPEN == newEnvelop.getEnvelopType()) {
			newEnvelop.setIsOpen(Boolean.TRUE);
			newEnvelop.setOpener(null);
		}

		if (CollectionUtil.isNotEmpty(getEvaluators())) {
			newEnvelop.setEvaluators(new ArrayList<RftEvaluatorUser>());
			for (RftEvaluatorUser eval : getEvaluators()) {
				RftEvaluatorUser evalUser = eval.copyFrom();
				evalUser.setEnvelop(newEnvelop);
				newEnvelop.getEvaluators().add(evalUser);
			}
		}

		if (CollectionUtil.isNotEmpty(getCqList())) {
			newEnvelop.setCqList(new ArrayList<RftCq>());
			for (RftCq cqList : getCqList()) {
				RftCq newCqList = cqList.copyFrom();
				newEnvelop.getCqList().add(newCqList);
			}
		}

		if (CollectionUtil.isNotEmpty(getBqList())) {
			newEnvelop.setBqList(new ArrayList<RftEventBq>());
			for (RftEventBq bqList : getBqList()) {
				RftEventBq newBqList = bqList.copyForEnvelop();
				newEnvelop.getBqList().add(newBqList);
			}
		}

		if (CollectionUtil.isNotEmpty(getSorList())) {
			newEnvelop.setSorList(new ArrayList<RftEventSor>());
			for (RftEventSor sorList : getSorList()) {
				RftEventSor newSorList = sorList.copyForRfq(false);
				newEnvelop.getSorList().add(newSorList);
			}
		}

		if (CollectionUtil.isNotEmpty(getOpenerUsers())) {
			newEnvelop.setOpenerUsers(new ArrayList<RftEnvelopeOpenerUser>());
			for (RftEnvelopeOpenerUser opener : getOpenerUsers()) {
				RftEnvelopeOpenerUser openerUser = opener.copyFrom();
				newEnvelop.getOpenerUsers().add(openerUser);
			}
		}
		return newEnvelop;
	}

	public RftEnvelop createMobileShallowCopy() {
		RftEnvelop env = new RftEnvelop();
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
			for (RftEventBq bqList : getBqList()) {
				RftEventBq newBqList = bqList.createMobileEnvelopShallowCopy();
				env.getBqs().add(newBqList);
			}
		}

		if (CollectionUtil.isNotEmpty(getCqList())) {
			env.setCqs(new ArrayList<Cq>());
			for (RftCq cqList : getCqList()) {
				RftCq newCqList = cqList.createMobileEnvelopShallowCopy();
				env.getCqs().add(newCqList);
			}
		}

		if (CollectionUtil.isNotEmpty(getEvaluators())) {
			env.setEvaluatorUser(new ArrayList<EvaluatorUser>());
			for (RftEvaluatorUser evaluator : getEvaluators()) {
				RftEvaluatorUser evaluatorUser = evaluator.shallowCopyMoblileEvaluator();
				env.getEvaluatorUser().add(evaluatorUser);
			}
		}

		return env;
	}

	/**
	 * @return the rfxEvent
	 */
	public RftEvent getRfxEvent() {
		return rfxEvent;
	}

	/**
	 * @param rfxEvent the rfxEvent to set
	 */
	public void setRfxEvent(RftEvent rfxEvent) {
		this.rfxEvent = rfxEvent;
	}

	/**
	 * @return the bqList
	 */
	public List<RftEventBq> getBqList() {
		return bqList;
	}

	/**
	 * @param bqList the bqList to set
	 */
	public void setBqList(List<RftEventBq> bqList) {
		this.bqList = bqList;
	}

	/**
	 * @return the cqList
	 */
	public List<RftCq> getCqList() {
		return cqList;
	}

	/**
	 * @param cqList the cqList to set
	 */
	public void setCqList(List<RftCq> cqList) {
		this.cqList = cqList;
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

	/**
	 * @return the evaluators
	 */
	public List<RftEvaluatorUser> getEvaluators() {
		return evaluators;
	}

	/**
	 * @param evaluators the evaluators to set
	 */
	public void setEvaluators(List<RftEvaluatorUser> evaluators) {
		if (this.evaluators == null) {
			this.evaluators = new ArrayList<RftEvaluatorUser>();
		} else {
			if (evaluators != null) {
				for (RftEvaluatorUser existingUser : this.evaluators) {
					for (RftEvaluatorUser newUser : evaluators) {
						if (newUser.getId() == null) {
							continue;
						}
						if (newUser.getId().equals(existingUser.getId())) {
							newUser.setEvaluationDate(existingUser.getEvaluationDate());
							newUser.setEvaluationStatus(existingUser.getEvaluationStatus());
							newUser.setEnvelop(existingUser.getEnvelop());
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

	public String toLogString() {
		return "RftEnvelop [" + super.toLogString() + "]";
	}

	/**
	 * @return the evaluatorDeclaration
	 */
	public List<RftEvaluatorDeclaration> getEvaluatorDeclaration() {
		return evaluatorDeclaration;
	}

	/**
	 * @param evaluatorDeclaration the evaluatorDeclaration to set
	 */
	public void setEvaluatorDeclaration(List<RftEvaluatorDeclaration> evaluatorDeclaration) {
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
	public List<RftEnvelopeOpenerUser> getOpenerUsers() {
		return openerUsers;
	}


	public List<RftEventSor> getSorList() {
		return sorList;
	}

	public void setSorList(List<RftEventSor> sorList) {
		this.sorList = sorList;
	}

	/**
	 * @param openerUsers the openerUsers to set
	 */
	public void setOpenerUsers(List<RftEnvelopeOpenerUser> openerUsers) {
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

}
