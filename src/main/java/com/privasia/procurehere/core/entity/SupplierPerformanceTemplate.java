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
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.privasia.procurehere.core.enums.SourcingStatus;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.CustomDateTimeSerializer;
import org.hibernate.annotations.Type;

/**
 * @author Jayshree
 */
@Entity
@Table(name = "PROC_SUP_PERFORM_TEMPLATE")
public class SupplierPerformanceTemplate implements Serializable {

	private static final long serialVersionUID = 4407334326910235437L;

	@Id
	@GenericGenerator(name = "idGen", strategy = "uuid.hex")
	@GeneratedValue(generator = "idGen")
	@Column(name = "ID", length = 64)
	private String id;

	@NotNull(message = "{template.name.empty}")
	@Size(min = 1, max = 64, message = "{template.name.length}")
	@Column(name = "TEMPLATE_NAME", length = 64)
	private String templateName;

	@Size(min = 0, max = 250, message = "{template.description.length}")
	@Column(name = "TEMPLATE_DESCRIPTION", length = 250)
	private String templateDescription;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, optional = true)
	@JoinColumn(name = "TENANT_ID", nullable = true, foreignKey = @ForeignKey(name = "FK_SP_TMPLATE_BUYER_ID"))
	private Buyer buyer;

	@ManyToOne(fetch = FetchType.LAZY, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "CREATED_BY", nullable = false, foreignKey = @ForeignKey(name = "FK_SP_TEMPLATE_CREATED_BY"))
	private User createdBy;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CREATED_DATE", nullable = false)
	private Date createdDate;

	@ManyToOne(fetch = FetchType.LAZY, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "MODIFIED_BY", nullable = true, foreignKey = @ForeignKey(name = "FK_SP_TEMPLATE_MODIFIED_BY"))
	private User modifiedBy;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "MODIFIED_DATE")
	private Date modifiedDate;

	@Enumerated(EnumType.STRING)
	@Column(name = "TEMPLATE_STATUS")
	private SourcingStatus status = SourcingStatus.DRAFT;

	@Column(name = "MAXIMUM_SCORE", precision = 3, scale = 0)
	private BigDecimal maximumScore;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "PROCUREMENT_CATEGORY_ID", nullable = false, foreignKey = @ForeignKey(name = "FK_SP_TEMPL_PROC_CAT_ID"))
	private ProcurementCategories procurementCategory;

	@Column(name = "IS_PROCMT_CATEGORY_VISIBLE")
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean procurementCategoryVisible = Boolean.TRUE;

	@Column(name = "IS_PROCMT_CATEGORY_DISABLED")
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean procurementCategoryDisabled = Boolean.FALSE;

	@Column(name = "IS_PROCMT_CATEGORY_OPTIONAL")
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean procurementCategoryOptional = Boolean.FALSE;

	@JsonIgnore
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "template", orphanRemoval = true, cascade = CascadeType.ALL)
	@OrderBy("level, order")
	private List<SupplierPerformanceTemplateCriteria> criteria;

	@Column(name = "IS_DETAIL_COMPLETED")
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean detailCompleted = Boolean.FALSE;

	@Column(name = "IS_PER_CRITERIA_COMPLETED")
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean performanceCriteriaCompleted = Boolean.FALSE;

	public SupplierPerformanceTemplate() {
		this.detailCompleted = Boolean.FALSE;
		this.performanceCriteriaCompleted = Boolean.FALSE;
	}

	public SupplierPerformanceTemplate(String id, String templateName, String templateDescription) {
		this.id = id;
		this.templateName = templateName;
		this.templateDescription = templateDescription;
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
	 * @return the templateName
	 */
	public String getTemplateName() {
		return templateName;
	}

	/**
	 * @param templateName the templateName to set
	 */
	public void setTemplateName(String templateName) {
		this.templateName = templateName;
	}

	/**
	 * @return the templateDescription
	 */
	public String getTemplateDescription() {
		return templateDescription;
	}

	/**
	 * @param templateDescription the templateDescription to set
	 */
	public void setTemplateDescription(String templateDescription) {
		this.templateDescription = templateDescription;
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
	 * @return the createdBy
	 */
	public User getCreatedBy() {
		return createdBy;
	}

	/**
	 * @param createdBy the createdBy to set
	 */
	public void setCreatedBy(User createdBy) {
		this.createdBy = createdBy;
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
	 * @return the modifiedBy
	 */
	public User getModifiedBy() {
		return modifiedBy;
	}

	/**
	 * @param modifiedBy the modifiedBy to set
	 */
	public void setModifiedBy(User modifiedBy) {
		this.modifiedBy = modifiedBy;
	}

	/**
	 * @return the modifiedDate
	 */
	public Date getModifiedDate() {
		return modifiedDate;
	}

	/**
	 * @param modifiedDate the modifiedDate to set
	 */
	public void setModifiedDate(Date modifiedDate) {
		this.modifiedDate = modifiedDate;
	}

	/**
	 * @return the status
	 */
	public SourcingStatus getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(SourcingStatus status) {
		this.status = status;
	}

	/**
	 * @return the maximumScore
	 */
	public BigDecimal getMaximumScore() {
		return maximumScore;
	}

	/**
	 * @param maximumScore the maximumScore to set
	 */
	public void setMaximumScore(BigDecimal maximumScore) {
		this.maximumScore = maximumScore;
	}

	/**
	 * @return the procurementCategory
	 */
	public ProcurementCategories getProcurementCategory() {
		return procurementCategory;
	}

	/**
	 * @param procurementCategory the procurementCategory to set
	 */
	public void setProcurementCategory(ProcurementCategories procurementCategory) {
		this.procurementCategory = procurementCategory;
	}

	/**
	 * @return the procurementCategoryVisible
	 */
	public Boolean getProcurementCategoryVisible() {
		return procurementCategoryVisible;
	}

	/**
	 * @param procurementCategoryVisible the procurementCategoryVisible to set
	 */
	public void setProcurementCategoryVisible(Boolean procurementCategoryVisible) {
		this.procurementCategoryVisible = procurementCategoryVisible;
	}

	/**
	 * @return the procurementCategoryDisabled
	 */
	public Boolean getProcurementCategoryDisabled() {
		return procurementCategoryDisabled;
	}

	/**
	 * @param procurementCategoryDisabled the procurementCategoryDisabled to set
	 */
	public void setProcurementCategoryDisabled(Boolean procurementCategoryDisabled) {
		this.procurementCategoryDisabled = procurementCategoryDisabled;
	}

	/**
	 * @return the procurementCategoryOptional
	 */
	public Boolean getProcurementCategoryOptional() {
		return procurementCategoryOptional;
	}

	/**
	 * @param procurementCategoryOptional the procurementCategoryOptional to set
	 */
	public void setProcurementCategoryOptional(Boolean procurementCategoryOptional) {
		this.procurementCategoryOptional = procurementCategoryOptional;
	}

	/**
	 * @return the criteria
	 */
	public List<SupplierPerformanceTemplateCriteria> getCriteria() {
		return criteria;
	}

	/**
	 * @param criteria the criteria to set
	 */
	public void setCriteria(List<SupplierPerformanceTemplateCriteria> criteria) {
		this.criteria = criteria;
	}

	/**
	 * @return the detailCompleted
	 */
	public Boolean getDetailCompleted() {
		return detailCompleted;
	}

	/**
	 * @param detailCompleted the detailCompleted to set
	 */
	public void setDetailCompleted(Boolean detailCompleted) {
		this.detailCompleted = detailCompleted;
	}

	/**
	 * @return the performanceCriteriaCompleted
	 */
	public Boolean getPerformanceCriteriaCompleted() {
		return performanceCriteriaCompleted;
	}

	/**
	 * @param performanceCriteriaCompleted the performanceCriteriaCompleted to set
	 */
	public void setPerformanceCriteriaCompleted(Boolean performanceCriteriaCompleted) {
		this.performanceCriteriaCompleted = performanceCriteriaCompleted;
	}

	public void copyCriteriaDetails(SupplierPerformanceTemplate oldTemplate, SupplierPerformanceTemplate newTemplate) {
		if (CollectionUtil.isNotEmpty(oldTemplate.getCriteria())) {
			newTemplate.setCriteria(new ArrayList<SupplierPerformanceTemplateCriteria>());
			for (SupplierPerformanceTemplateCriteria oldCriteria : oldTemplate.getCriteria()) {
				newTemplate.getCriteria().add(oldCriteria.copyFrom());
			}
		}
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, templateDescription, templateName);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SupplierPerformanceTemplate other = (SupplierPerformanceTemplate) obj;
		return Objects.equals(id, other.id) && Objects.equals(templateDescription, other.templateDescription) && Objects.equals(templateName, other.templateName);
	}

	public String toLogString() {
		return "SupplierPerformanceTemplate [id=" + id + ", templateName=" + templateName + ", templateDescription=" + templateDescription + ", buyer=" + buyer + ", createdBy=" + createdBy + ", createdDate=" + createdDate + ", modifiedBy=" + modifiedBy + ", modifiedDate=" + modifiedDate + ", status=" + status + "]";
	}

}
