/**
 * 
 */
package com.privasia.procurehere.core.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * @author Jayshree
 *
 */
@Entity
@Table(name = "PROC_SUP_PER_CRITERIA")
public class SupplierPerformanceCriteria extends SupplierPerformanceCriteriaBase implements Serializable {

	private static final long serialVersionUID = 4717474934691846284L;

	@Id
	@GenericGenerator(name = "idGen", strategy = "uuid.hex")
	@GeneratedValue(generator = "idGen")
	@Column(name = "ID", length = 64, nullable = false)
	private String id;

	@Column(name = "EVALUATOR_SCORE", precision = 3, scale = 0)
	private BigDecimal evaluatorScore;
	
	@Column(name = "EVALUATOR_TOTAL_SCORE", precision = 5, scale = 2)
	private BigDecimal evaluatorTotalScore;

	@JsonIgnore
	@ManyToOne(optional = false, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "SUP_PER_CRT_USR_ID", nullable = true, foreignKey = @ForeignKey(name = "FK_SP_CR_PER_EVAL_USR_ID"))
	private SupplierPerformanceEvaluatorUser evaluationUser;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.REFRESH }, optional = false)
	@JoinColumn(name = "FORM_ID", nullable = false, foreignKey = @ForeignKey(name = "FK_SP_CRT_FORM_ID"))
	private SupplierPerformanceForm form;

	@JsonIgnore
	@ManyToOne(optional = true, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "PARENT_ID", nullable = true, foreignKey = @ForeignKey(name = "FK_SP_CRTR_PARENT_ID"))
	private SupplierPerformanceCriteria parent;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "parent", orphanRemoval = true, cascade = CascadeType.ALL)
	@OrderBy("level, order")
	private List<SupplierPerformanceCriteria> children;
	
	@Column(name = "EVALUATOR_COMMENTS", length = 128)
	@Size(max = 128)
	private String comments;

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the evaluatorScore
	 */
	public BigDecimal getEvaluatorScore() {
		return evaluatorScore;
	}

	/**
	 * @param evaluatorScore the evaluatorScore to set
	 */
	public void setEvaluatorScore(BigDecimal evaluatorScore) {
		this.evaluatorScore = evaluatorScore;
	}

	/**
	 * @return the evaluatorTotalScore
	 */
	public BigDecimal getEvaluatorTotalScore() {
		return evaluatorTotalScore;
	}

	/**
	 * @param evaluatorTotalScore the evaluatorTotalScore to set
	 */
	public void setEvaluatorTotalScore(BigDecimal evaluatorTotalScore) {
		this.evaluatorTotalScore = evaluatorTotalScore;
	}

	/**
	 * @return the evaluationUser
	 */
	public SupplierPerformanceEvaluatorUser getEvaluationUser() {
		return evaluationUser;
	}

	/**
	 * @param evaluationUser the evaluationUser to set
	 */
	public void setEvaluationUser(SupplierPerformanceEvaluatorUser evaluationUser) {
		this.evaluationUser = evaluationUser;
	}

	/**
	 * @return the parent
	 */
	public SupplierPerformanceCriteria getParent() {
		return parent;
	}

	/**
	 * @param parent the parent to set
	 */
	public void setParent(SupplierPerformanceCriteria parent) {
		this.parent = parent;
	}

	/**
	 * @return the children
	 */
	public List<SupplierPerformanceCriteria> getChildren() {
		return children;
	}

	/**
	 * @param children the children to set
	 */
	public void setChildren(List<SupplierPerformanceCriteria> children) {
		this.children = children;
	}
	
	/**
	 * @return the form
	 */
	public SupplierPerformanceForm getForm() {
		return form;
	}

	/**
	 * @param form the form to set
	 */
	public void setForm(SupplierPerformanceForm form) {
		this.form = form;
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

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SupplierPerformanceCriteria other = (SupplierPerformanceCriteria) obj;
		return Objects.equals(id, other.id);
	}

	public String toLogString() {
		return "SupplierPerformanceCriteria [id=" + id + ", toString()=" + super.toString() + "]";
	}

}