package com.privasia.procurehere.core.pojo;

import java.io.Serializable;
import java.util.List;

import com.privasia.procurehere.core.entity.RfaEnvelopeOpenerUser;

/**
 * @author Giridhar
 */
public class EvaluationEnvelopPojo implements Serializable {

	private static final long serialVersionUID = 2752175085999845842L;
	private String envlopName;
	private String description;
	private List<EvaluationBqPojo> bqs;
	private List<EvaluationCqPojo> cqs;

	private List<EvaluationSorPojo> sors;
	private String imagePath;
	private String type;
	private String owner;
	private String opener;
	private Integer sequence;

	private List<EvaluationEnvelopPojo> evaluator;

	private List<EvaluationEnvelopPojo> openerUsers;

	/**
	 * @return the envlopName
	 */
	public String getEnvlopName() {
		return envlopName;
	}

	/**
	 * @return the sequence
	 */
	public Integer getSequence() {
		return sequence;
	}

	/**
	 * @param sequence the sequence to set
	 */
	public void setSequence(Integer sequence) {
		this.sequence = sequence;
	}

	/**
	 * @param envlopName the envlopName to set
	 */
	public void setEnvlopName(String envlopName) {
		this.envlopName = envlopName;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return the bqs
	 */
	public List<EvaluationBqPojo> getBqs() {
		return bqs;
	}

	/**
	 * @param bqs the bqs to set
	 */
	public void setBqs(List<EvaluationBqPojo> bqs) {
		this.bqs = bqs;
	}

	/**
	 * @return the cqs
	 */
	public List<EvaluationCqPojo> getCqs() {
		return cqs;
	}

	/**
	 * @param cqs the cqs to set
	 */
	public void setCqs(List<EvaluationCqPojo> cqs) {
		this.cqs = cqs;
	}

	/**
	 * @return the imagePath
	 */
	public String getImagePath() {
		return imagePath;
	}

	/**
	 * @param imagePath the imagePath to set
	 */
	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
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
	 * @return the owner
	 */
	public String getOwner() {
		return owner;
	}

	/**
	 * @param owner the owner to set
	 */
	public void setOwner(String owner) {
		this.owner = owner;
	}

	/**
	 * @return the evaluator
	 */
	public List<EvaluationEnvelopPojo> getEvaluator() {
		return evaluator;
	}

	/**
	 * @param evaluator the evaluator to set
	 */
	public void setEvaluator(List<EvaluationEnvelopPojo> evaluator) {
		this.evaluator = evaluator;
	}

	/**
	 * @return the opener
	 */
	public String getOpener() {
		return opener;
	}

	/**
	 * @param opener the opener to set
	 */
	public void setOpener(String opener) {
		this.opener = opener;
	}

	/**
	 * @return the openerUsers
	 */
	public List<EvaluationEnvelopPojo> getOpenerUsers() {
		return openerUsers;
	}

	/**
	 * @param openerUsers the openerUsers to set
	 */
	public void setOpenerUsers(List<EvaluationEnvelopPojo> openerUsers) {
		this.openerUsers = openerUsers;
	}

	public List<EvaluationSorPojo> getSors() {
		return sors;
	}

	public void setSors(List<EvaluationSorPojo> sors) {
		this.sors = sors;
	}
}
