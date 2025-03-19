/**
 * 
 */
package com.privasia.procurehere.core.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.jfree.util.Log;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.privasia.procurehere.core.enums.SupperPerformanceEvaluatorStatus;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.CustomDateTimeSerializer;

/**
 * @author Jayshree
 */
@Entity
@Table(name = "PROC_SUPP_PERF_EVAL_USER")
public class SupplierPerformanceEvaluatorUser implements Serializable {

	private static final long serialVersionUID = 6741477140630586254L;

	@Id
	@GenericGenerator(name = "idGen", strategy = "uuid.hex")
	@GeneratedValue(generator = "idGen")
	@Column(name = "ID", length = 64, nullable = false)
	protected String id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "EVALUATOR_ID", nullable = false, foreignKey = @ForeignKey(name = "FK_SP_EVAL_USER_ID"))
	private User evaluator;

	@Enumerated(EnumType.STRING)
	@Column(name = "EVALUATION_STATUS", nullable = false)
	private SupperPerformanceEvaluatorStatus evaluationStatus = SupperPerformanceEvaluatorStatus.DRAFT;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.REFRESH }, optional = false)
	@JoinColumn(name = "FORM_ID", nullable = false, foreignKey = @ForeignKey(name = "FK_SP_EVAL_FORM_ID"))
	private SupplierPerformanceForm form;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CREATED_DATE", nullable = false)
	private Date createdDate;

	@JsonIgnore
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "evaluationUser", cascade = CascadeType.ALL)
	@OrderBy("level, order")
	private List<SupplierPerformanceCriteria> criteria;

	@Column(name = "OVERALL_SCORE", precision = 5, scale = 2, nullable = true)
	private BigDecimal overallScore;

	@ManyToOne(fetch = FetchType.LAZY, cascade = { CascadeType.REFRESH }, optional = true)
	@JoinColumn(name = "SCORE_RATING_ID", nullable = true, foreignKey = @ForeignKey(name = "FK_SP_EVAL_SCORE_RATING_ID"))
	private ScoreRating scoreRating;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "PREVIEW_DATE", nullable = true)
	private Date previewDate;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "EVALUATE_DATE", nullable = true)
	private Date evaluateDate;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "APPROVED_DATE", nullable = true)
	private Date approvedDate;

	@Column(name = "ENABLE_PER_EVALUATION_APPROVAL", nullable = false)
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean enablePerformanceEvaluationApproval = Boolean.TRUE;

	@JsonIgnore
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "evalutorUser", orphanRemoval = true, cascade = CascadeType.ALL)
	@OrderBy("level")
	private List<PerformanceEvaluationApproval> evaluationApprovals;

	@JsonIgnore
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "evaluatorUser", cascade = { CascadeType.ALL })
	@OrderBy("createdDate")
	private List<SpFormEvaluationAppComment> evalApprovalComment;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "OLD_EVALUATION_STATUS", nullable = false)
	private SupperPerformanceEvaluatorStatus oldEvaluationStatus = SupperPerformanceEvaluatorStatus.DRAFT;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "TENANT_ID", nullable = false, foreignKey = @ForeignKey(name = "FK_SP_EVAL_USR_BUYER_ID"))
	private Buyer buyer;
	
	@Transient
	private String name;

	public SupplierPerformanceEvaluatorUser() {
		// TODO Auto-generated constructor stub
	}
	
	public SupplierPerformanceEvaluatorUser(String id, String name, BigDecimal overallScore) {
		this.id = id ;
		this.name = name;
		this.overallScore = overallScore;
	}
	
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
	 * @return the evaluator
	 */
	public User getEvaluator() {
		return evaluator;
	}

	/**
	 * @param evaluator the evaluator to set
	 */
	public void setEvaluator(User evaluator) {
		this.evaluator = evaluator;
	}

	/**
	 * @return the evaluationStatus
	 */
	public SupperPerformanceEvaluatorStatus getEvaluationStatus() {
		return evaluationStatus;
	}

	/**
	 * @param evaluationStatus the evaluationStatus to set
	 */
	public void setEvaluationStatus(SupperPerformanceEvaluatorStatus evaluationStatus) {
		this.evaluationStatus = evaluationStatus;
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
	 * @return the createdDate
	 */
	public Date getCreatedDate() {
		return createdDate;
	}

	/**
	 * @param createdDate the createdDate to set
	 */
	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	/**
	 * @return the criteria
	 */
	public List<SupplierPerformanceCriteria> getCriteria() {
		return criteria;
	}

	/**
	 * @param criteria the criteria to set
	 */
	public void setCriteria(List<SupplierPerformanceCriteria> criteria) {
		this.criteria = criteria;
	}

	/**
	 * @return the overallScore
	 */
	public BigDecimal getOverallScore() {
		return overallScore;
	}

	/**
	 * @param overallScore the overallScore to set
	 */
	public void setOverallScore(BigDecimal overallScore) {
		this.overallScore = overallScore;
	}

	/**
	 * @return the scoreRating
	 */
	public ScoreRating getScoreRating() {
		return scoreRating;
	}

	/**
	 * @param scoreRating the scoreRating to set
	 */
	public void setScoreRating(ScoreRating scoreRating) {
		this.scoreRating = scoreRating;
	}

	/**
	 * @return the previewDate
	 */
	public Date getPreviewDate() {
		return previewDate;
	}

	/**
	 * @param previewDate the previewDate to set
	 */
	public void setPreviewDate(Date previewDate) {
		this.previewDate = previewDate;
	}

	/**
	 * @return the evaluateDate
	 */
	public Date getEvaluateDate() {
		return evaluateDate;
	}

	/**
	 * @param evaluateDate the evaluateDate to set
	 */
	public void setEvaluateDate(Date evaluateDate) {
		this.evaluateDate = evaluateDate;
	}

	/**
	 * @return the approvedDate
	 */
	public Date getApprovedDate() {
		return approvedDate;
	}

	/**
	 * @param approvedDate the approvedDate to set
	 */
	public void setApprovedDate(Date approvedDate) {
		this.approvedDate = approvedDate;
	}

	/**
	 * @return the enablePerformanceEvaluationApproval
	 */
	public Boolean getEnablePerformanceEvaluationApproval() {
		return enablePerformanceEvaluationApproval;
	}

	/**
	 * @param enablePerformanceEvaluationApproval the enablePerformanceEvaluationApproval to set
	 */
	public void setEnablePerformanceEvaluationApproval(Boolean enablePerformanceEvaluationApproval) {
		this.enablePerformanceEvaluationApproval = enablePerformanceEvaluationApproval;
	}

	/**
	 * @return the evaluationApprovals
	 */
	public List<PerformanceEvaluationApproval> getEvaluationApprovals() {
		return evaluationApprovals;
	}

	/**
	 * @param evaluationApprovals the evaluationApprovals to set
	 */
	public void setEvaluationApprovals(List<PerformanceEvaluationApproval> evaluationApprovals) {
		if (this.evaluationApprovals == null) {
			this.evaluationApprovals = new ArrayList<PerformanceEvaluationApproval>();
		} else {// Do update only it the passed list is a fresh list and not the same instance
				// list.
			if (evaluationApprovals != null) {
				for (PerformanceEvaluationApproval oldApproval : this.evaluationApprovals) {
					for (PerformanceEvaluationApproval newApproval : evaluationApprovals) {
						if (newApproval.getId() == null)
							continue;
						if (newApproval.getId().equals(oldApproval.getId())) {
							newApproval.setActive(oldApproval.isActive());
							newApproval.setDone(oldApproval.isDone());
							newApproval.setId(null);
							// Preserve individual approval user old state
							for (PerformanceEvaluationApprovalUser oldApprovalUser : oldApproval.getApprovalUsers()) {
								for (PerformanceEvaluationApprovalUser newApprovalUser : newApproval.getApprovalUsers()) {
									if (newApprovalUser.getUser() == null || newApprovalUser.getUser().getId() == null) {
										continue;
									}
									if (newApprovalUser.getUser().getId().equals(oldApprovalUser.getUser().getId())) {
										newApprovalUser.setActionDate(oldApprovalUser.getActionDate());
										newApprovalUser.setApprovalStatus(oldApprovalUser.getApprovalStatus());
										newApprovalUser.setRemarks(oldApprovalUser.getRemarks());
									}
								}
							}
						}
					}
				}
			}
			this.evaluationApprovals.clear();
		}
		if (evaluationApprovals != null) {
			this.evaluationApprovals.addAll(evaluationApprovals);
		}
	}

	/**
	 * @return the evalApprovalComment
	 */
	public List<SpFormEvaluationAppComment> getEvalApprovalComment() {
		return evalApprovalComment;
	}

	/**
	 * @param evalApprovalComment the evalApprovalComment to set
	 */
	public void setEvalApprovalComment(List<SpFormEvaluationAppComment> evalApprovalComment) {
		this.evalApprovalComment = evalApprovalComment;
	}
	
	/**
	 * @return the oldEvaluationStatus
	 */
	public SupperPerformanceEvaluatorStatus getOldEvaluationStatus() {
		return oldEvaluationStatus;
	}

	/**
	 * @param oldEvaluationStatus the oldEvaluationStatus to set
	 */
	public void setOldEvaluationStatus(SupperPerformanceEvaluatorStatus oldEvaluationStatus) {
		this.oldEvaluationStatus = oldEvaluationStatus;
	}
	
	/**
	 * @return the buyer
	 */
	public Buyer getBuyer() {
		return buyer;
	}

	/**
	 * @param buyer the buyer to set
	 */
	public void setBuyer(Buyer buyer) {
		this.buyer = buyer;
	}
	
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	public void copyCriteriaDetails(SupplierPerformanceEvaluatorUser evalUser, SupplierPerformanceForm form) {
		if (CollectionUtil.isNotEmpty(form.getCriteria())) {
			if (evalUser.getCriteria() != null) {
				if(SupperPerformanceEvaluatorStatus.SUSPENDED != evalUser.getEvaluationStatus()) {
					evalUser.getCriteria().clear(); // Resume
				}
			} else {
				evalUser.setCriteria(new ArrayList<SupplierPerformanceCriteria>());
			}
			if(evalUser.getCriteria().size() == 0) {
				for (SupplierPerformanceFormCriteria formCriteria : form.getCriteria()) {
					Log.info("Setting Criteria to evaluator " + formCriteria.getName());
					evalUser.getCriteria().add(formCriteria.copyFormsCriteria());
				}
			}
		}
	}

	@Override
	public int hashCode() {
		return Objects.hash(evaluator, id);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SupplierPerformanceEvaluatorUser other = (SupplierPerformanceEvaluatorUser) obj;
		return Objects.equals(evaluator, other.evaluator) && Objects.equals(id, other.id);
	}

	public String toLogString() {
		return "SupplierPerformanceEvaluatorUser [id=" + id + ", evaluationStatus=" + evaluationStatus + ", createdDate=" + createdDate + ", overallScore=" + overallScore + ", scoreRating=" + scoreRating + ", previewDate=" + previewDate + ", evaluateDate=" + evaluateDate + ", approvedDate=" + approvedDate + ", enablePerformanceEvaluationApproval=" + enablePerformanceEvaluationApproval + "]";
	}

}
