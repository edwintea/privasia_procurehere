/**
 * 
 */
package com.privasia.procurehere.core.pojo;

import java.util.List;

import com.privasia.procurehere.core.entity.Envelop;
import com.privasia.procurehere.core.entity.RfaEnvelop;
import com.privasia.procurehere.core.entity.RfaEnvelopeOpenerUser;
import com.privasia.procurehere.core.entity.RfaEvaluatorUser;
import com.privasia.procurehere.core.entity.RfiEnvelop;
import com.privasia.procurehere.core.entity.RfiEnvelopeOpenerUser;
import com.privasia.procurehere.core.entity.RfiEvaluatorUser;
import com.privasia.procurehere.core.entity.RfpEnvelop;
import com.privasia.procurehere.core.entity.RfpEnvelopeOpenerUser;
import com.privasia.procurehere.core.entity.RfpEvaluatorUser;
import com.privasia.procurehere.core.entity.RfqEnvelop;
import com.privasia.procurehere.core.entity.RfqEnvelopeOpenerUser;
import com.privasia.procurehere.core.entity.RfqEvaluatorUser;
import com.privasia.procurehere.core.entity.RftEnvelop;
import com.privasia.procurehere.core.entity.RftEnvelopeOpenerUser;
import com.privasia.procurehere.core.entity.RftEvaluatorUser;
import com.privasia.procurehere.core.utils.CollectionUtil;

/**
 * @author Priyanka Singh
 */
public class EnvelopePojo extends Envelop {
	private static final long serialVersionUID = -6883308179846403047L;

	private List<?> assignedEvaluators;

	private List<?> evaluators;

	private List<?> assignedOpeners;
	
	private List<?> openers;

	public EnvelopePojo(RftEnvelop envelope) {
		this.assignedEvaluators = envelope.getEvaluators();
		if (CollectionUtil.isNotEmpty(assignedEvaluators)) {
			for (Object object : assignedEvaluators) {
				((RftEvaluatorUser) object).setEnvelop(null);
			}
		}
		this.assignedOpeners = envelope.getOpenerUsers();
		if (CollectionUtil.isNotEmpty(assignedOpeners)) {
			for (Object object : assignedOpeners) {
				((RftEnvelopeOpenerUser) object).setEnvelope(null);
			}
		}
		super.setLeadEvaluater(envelope.getLeadEvaluater());
		super.setOpener(envelope.getOpener());
		super.setEnvelopTitle(envelope.getEnvelopTitle());
		super.setEnvelopType(envelope.getEnvelopType());
		super.setPreFix(envelope.getPreFix());
		super.setEvaluationStatus(envelope.getEvaluationStatus());
	}

	public EnvelopePojo(RfqEnvelop envelope) {
		this.assignedEvaluators = envelope.getEvaluators();
		if (CollectionUtil.isNotEmpty(assignedEvaluators)) {
			for (Object object : assignedEvaluators) {
				((RfqEvaluatorUser) object).setEnvelope(null);
			}
		}
		this.assignedOpeners = envelope.getOpenerUsers();
		if (CollectionUtil.isNotEmpty(assignedOpeners)) {
			for (Object object : assignedOpeners) {
				((RfqEnvelopeOpenerUser) object).setEnvelope(null);
			}
		}
		super.setLeadEvaluater(envelope.getLeadEvaluater());
		super.setOpener(envelope.getOpener());
		super.setEnvelopTitle(envelope.getEnvelopTitle());
		super.setEnvelopType(envelope.getEnvelopType());
		super.setPreFix(envelope.getPreFix());
		super.setEvaluationStatus(envelope.getEvaluationStatus());
	}

	public EnvelopePojo(RfiEnvelop envelope) {
		this.assignedEvaluators = envelope.getEvaluators();
		if (CollectionUtil.isNotEmpty(assignedEvaluators)) {
			for (Object object : assignedEvaluators) {
				((RfiEvaluatorUser) object).setEnvelop(null);
			}
		}
		this.assignedOpeners = envelope.getOpenerUsers();
		if (CollectionUtil.isNotEmpty(assignedOpeners)) {
			for (Object object : assignedOpeners) {
				((RfiEnvelopeOpenerUser) object).setEnvelope(null);
			}
		}
		super.setLeadEvaluater(envelope.getLeadEvaluater());
		super.setOpener(envelope.getOpener());
		super.setEnvelopTitle(envelope.getEnvelopTitle());
		super.setEnvelopType(envelope.getEnvelopType());
		super.setPreFix(envelope.getPreFix());
		super.setEvaluationStatus(envelope.getEvaluationStatus());
	}

	public EnvelopePojo(RfpEnvelop envelope) {
		this.assignedEvaluators = envelope.getEvaluators();
		if (CollectionUtil.isNotEmpty(assignedEvaluators)) {
			for (Object object : assignedEvaluators) {
				((RfpEvaluatorUser) object).setEnvelope(null);
			}
		}
		this.assignedOpeners = envelope.getOpenerUsers();
		if (CollectionUtil.isNotEmpty(assignedOpeners)) {
			for (Object object : assignedOpeners) {
				((RfpEnvelopeOpenerUser) object).setEnvelope(null);
			}
		}
		super.setLeadEvaluater(envelope.getLeadEvaluater());
		super.setOpener(envelope.getOpener());
		super.setEnvelopTitle(envelope.getEnvelopTitle());
		super.setEnvelopType(envelope.getEnvelopType());
		super.setPreFix(envelope.getPreFix());
		super.setEvaluationStatus(envelope.getEvaluationStatus());
	}

	public EnvelopePojo(RfaEnvelop envelope) {
		this.assignedEvaluators = envelope.getEvaluators();
		if (CollectionUtil.isNotEmpty(assignedEvaluators)) {
			for (Object object : assignedEvaluators) {
				((RfaEvaluatorUser) object).setEnvelope(null);
			}
		}
		this.assignedOpeners = envelope.getOpenerUsers();
		if (CollectionUtil.isNotEmpty(assignedOpeners)) {
			for (Object object : assignedOpeners) {
				((RfaEnvelopeOpenerUser) object).setEnvelope(null);
			}
		}
		super.setLeadEvaluater(envelope.getLeadEvaluater());
		super.setOpener(envelope.getOpener());
		super.setEnvelopTitle(envelope.getEnvelopTitle());
		super.setEnvelopType(envelope.getEnvelopType());
		super.setPreFix(envelope.getPreFix());
		super.setEvaluationStatus(envelope.getEvaluationStatus());
	}

	/**
	 * @return the assignedEvaluators
	 */
	public List<?> getAssignedEvaluators() {
		return assignedEvaluators;
	}

	/**
	 * @param assignedEvaluators the assignedEvaluators to set
	 */
	public void setAssignedEvaluators(List<?> assignedEvaluators) {
		this.assignedEvaluators = assignedEvaluators;
	}

	/**
	 * @return the evaluators
	 */
	public List<?> getEvaluators() {
		return evaluators;
	}

	/**
	 * @param evaluators the evaluators to set
	 */
	public void setEvaluators(List<?> evaluators) {
		this.evaluators = evaluators;
	}

	/**
	 * @return the assignedOpeners
	 */
	public List<?> getAssignedOpeners() {
		return assignedOpeners;
	}

	/**
	 * @param assignedOpeners the assignedOpeners to set
	 */
	public void setAssignedOpeners(List<?> assignedOpeners) {
		this.assignedOpeners = assignedOpeners;
	}

	/**
	 * @return the openers
	 */
	public List<?> getOpeners() {
		return openers;
	}

	/**
	 * @param openers the openers to set
	 */
	public void setOpeners(List<?> openers) {
		this.openers = openers;
	}
	
	
}
