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

import org.hibernate.annotations.GenericGenerator;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.Type;

/**
 * @author Jayshree
 */
@Entity
@Table(name = "PROC_SUP_PER_FORM_CRITERIA")
public class SupplierPerformanceFormCriteria extends SupplierPerformanceCriteriaBase implements Serializable {

	private static final long serialVersionUID = 3156855894547307107L;

	@Id
	@GenericGenerator(name = "idGen", strategy = "uuid.hex")
	@GeneratedValue(generator = "idGen")
	@Column(name = "ID", length = 64, nullable = false)
	private String id;

	@Column(name = "ALLW_TO_UPDATE_SECT_WEIGHTAGE")
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean allowToUpdateSectionWeightage = Boolean.FALSE;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.REFRESH }, optional = false)
	@JoinColumn(name = "FORM_ID", nullable = false, foreignKey = @ForeignKey(name = "FK_SP_FRM_CRT_FORM_ID"))
	private SupplierPerformanceForm form;

	@JsonIgnore
	@ManyToOne(optional = true, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "PARENT_ID", nullable = true, foreignKey = @ForeignKey(name = "FK_SP_FRM_CRTR_PARENT_ID"))
	private SupplierPerformanceFormCriteria parent;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "parent", orphanRemoval = true, cascade = CascadeType.ALL)
	@OrderBy("level, order")
	private List<SupplierPerformanceFormCriteria> children;

	@Column(name = "TOTAL_SCORE", precision = 5, scale = 2)
	private BigDecimal totalScore = BigDecimal.ZERO;

	@Column(name = "AVG_SCORE", precision = 5, scale = 2)
	private BigDecimal averageScore = BigDecimal.ZERO;

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
	 * @return the allowToUpdateSectionWeightage
	 */
	public Boolean getAllowToUpdateSectionWeightage() {
		return allowToUpdateSectionWeightage;
	}

	/**
	 * @param allowToUpdateSectionWeightage the allowToUpdateSectionWeightage to set
	 */
	public void setAllowToUpdateSectionWeightage(Boolean allowToUpdateSectionWeightage) {
		this.allowToUpdateSectionWeightage = allowToUpdateSectionWeightage;
	}

	/**
	 * @return the parent
	 */
	public SupplierPerformanceFormCriteria getParent() {
		return parent;
	}

	/**
	 * @param parent the parent to set
	 */
	public void setParent(SupplierPerformanceFormCriteria parent) {
		this.parent = parent;
	}

	/**
	 * @return the children
	 */
	public List<SupplierPerformanceFormCriteria> getChildren() {
		return children;
	}

	/**
	 * @param children the children to set
	 */
	public void setChildren(List<SupplierPerformanceFormCriteria> children) {
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
	 * @return the totalScore
	 */
	public BigDecimal getTotalScore() {
		return totalScore;
	}

	/**
	 * @param totalScore the totalScore to set
	 */
	public void setTotalScore(BigDecimal totalScore) {
		this.totalScore = totalScore;
	}

	/**
	 * @return the averageScore
	 */
	public BigDecimal getAverageScore() {
		return averageScore;
	}

	/**
	 * @param averageScore the averageScore to set
	 */
	public void setAverageScore(BigDecimal averageScore) {
		this.averageScore = averageScore;
	}

	public SupplierPerformanceCriteria copyFormsCriteria() {
		SupplierPerformanceCriteria newCriteria = new SupplierPerformanceCriteria();
		newCriteria.setName(getName());
		newCriteria.setDescription(getDescription());
		newCriteria.setOrder(getOrder());
		newCriteria.setLevel(getLevel());
		newCriteria.setMaximumScore(getMaximumScore());
		newCriteria.setWeightage(getWeightage());
		return newCriteria;
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
		SupplierPerformanceFormCriteria other = (SupplierPerformanceFormCriteria) obj;
		return Objects.equals(id, other.id);
	}

	public String toLogString() {
		return "SupplierPerformanceFormCriteria [id=" + id + ", toString()=" + super.toString() + "]";
	}

}