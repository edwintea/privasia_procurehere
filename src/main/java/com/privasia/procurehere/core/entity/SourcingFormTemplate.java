/**
 * 
 */
package com.privasia.procurehere.core.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
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
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.privasia.procurehere.core.enums.SourcingStatus;
import com.privasia.procurehere.core.pojo.TemplateFieldPojo;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.CustomDateTimeSerializer;
import org.hibernate.annotations.Type;

/**
 * @author pooja
 */
@Entity
@Table(name = "PROC_SOURCING_FORM_TEMPLATE")
public class SourcingFormTemplate implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 679495127958162430L;

	@Id
	@GenericGenerator(name = "idGen", strategy = "uuid.hex")
	@GeneratedValue(generator = "idGen")
	@Column(name = "ID", length = 64)
	private String id;

	@Column(name = "FORM_NAME", length = 250)
	@Size(min = 1, max = 64)
	private String formName;

	@Size(max = 1000)
	@Column(name = "DESCRIPTION", length = 1050)
	private String description;

	@JsonIgnore
	@Column(name = "TENANT_ID", nullable = false, length = 64)
	private String tenantId;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, optional = true, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "CREATED_BY", nullable = true)
	private User createdBy;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "MODIFIED_BY", nullable = true)
	private User modifiedBy;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CREATED_DATE", nullable = false)
	private Date createdDate;

	@JsonIgnore
	@JsonSerialize(using = CustomDateTimeSerializer.class)
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "MODIFIED_DATE")
	private Date modifiedDate;

	@JsonIgnore
	@Enumerated(EnumType.STRING)
	@Column(name = "STATUS")
	private SourcingStatus status;

	@JsonIgnore
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "sourcingForm", orphanRemoval = true, cascade = CascadeType.ALL)
	@OrderBy("level")
	private List<SourcingTemplateApproval> sourcingFormApproval;

	@JsonIgnore
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "sourcingForm", cascade = CascadeType.ALL)
	private List<SourcingTemplateCq> cqs;

	@Column(name = "IS_EVENT_DETAIL_COMPLETED")
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean eventDetailCompleted = Boolean.FALSE;

	@Column(name = "IS_CQ_COMPLETED")
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean cqCompleted = Boolean.FALSE;

	@Column(name = "BUYER_SET_DECIMAL", length = 8)
	private String decimal;

	@Column(name = "IS_TEMPLATE_USED", nullable = false)
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean isTemplateUsed = Boolean.FALSE;

	@Column(name = "APPROVALS_COUNT", nullable = false)
	private Integer approvalsCount;

	@Column(name = "ALLOW_TO_ADD_ADDI_APPROVAL", nullable = true)
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean addAdditionalApprovals = Boolean.FALSE;

	@Transient
	TemplateFieldPojo templateFieldBinding;

	@JsonIgnore
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "template", cascade = { CascadeType.ALL }, orphanRemoval = true)
	private List<SourcingTemplateField> fields;

	@JsonIgnore
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "sourcingForm", orphanRemoval = true, cascade = CascadeType.ALL)
	private List<TemplateSourcingTeamMembers> teamMembers;

	@NotNull
	@Column(name = "READ_ONLY_TEAM_MEMBER", nullable = true)
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean readOnlyTeamMember = Boolean.FALSE;

	@Transient
	private String createdByName;

	@JsonIgnore
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "sourcingFormTemplate")
	private List<RfsTemplateDocument> rfsTemplateDocuments;

	@Column(name = "IS_DOCUMENT_COMPLETED")
	private Boolean documentCompleted = Boolean.FALSE;
	
	public SourcingFormTemplate(String id, String formName, String description) {
		this.id = id;
		this.formName = formName;
		this.description = description;
	}
	

	public String getCreatedByName() {
		return createdByName;
	}

	public void setCreatedByName(String createdByName) {
		this.createdByName = createdByName;
	}

	public SourcingFormTemplate() {

		this.isTemplateUsed = Boolean.FALSE;
	}

	public String getDecimal() {
		return decimal;
	}

	public void setDecimal(String decimal) {
		this.decimal = decimal;
	}

	public Boolean getCqCompleted() {
		return cqCompleted;
	}

	public void setCqCompleted(Boolean cqCompleted) {
		this.cqCompleted = cqCompleted;
	}

	public Boolean getEventDetailCompleted() {
		return eventDetailCompleted;
	}

	public void setEventDetailCompleted(Boolean eventDetailCompleted) {
		this.eventDetailCompleted = eventDetailCompleted;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getFormName() {
		return formName;
	}

	public void setFormName(String formName) {
		this.formName = formName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getTenantId() {
		return tenantId;
	}

	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
	}

	public User getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(User createdBy) {
		this.createdBy = createdBy;
	}

	public User getModifiedBy() {
		return modifiedBy;
	}

	public void setModifiedBy(User modifiedBy) {
		this.modifiedBy = modifiedBy;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public Date getModifiedDate() {
		return modifiedDate;
	}

	public void setModifiedDate(Date modifiedDate) {
		this.modifiedDate = modifiedDate;
	}

	public SourcingStatus getStatus() {
		return status;
	}

	public void setStatus(SourcingStatus status) {
		this.status = status;
	}

	public List<SourcingTemplateApproval> getSourcingFormApproval() {
		return sourcingFormApproval;
	}

	public Boolean getIsTemplateUsed() {
		return isTemplateUsed;
	}

	public void setIsTemplateUsed(Boolean isTemplateUsed) {
		this.isTemplateUsed = isTemplateUsed;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public void setSourcingFormApproval(List<SourcingTemplateApproval> sourcingFormApproval) {
		this.sourcingFormApproval = sourcingFormApproval;
	}

	public List<SourcingTemplateCq> getCqs() {
		return cqs;
	}

	public void setCqs(List<SourcingTemplateCq> cqs) {
		this.cqs = cqs;
	}

	/**
	 * @return the approvalsCount
	 */
	public Integer getApprovalsCount() {
		return approvalsCount;
	}

	/**
	 * @param approvalsCount the approvalsCount to set
	 */
	public void setApprovalsCount(Integer approvalsCount) {
		this.approvalsCount = approvalsCount;
	}

	/**
	 * @return the templateFieldBinding
	 */
	public TemplateFieldPojo getTemplateFieldBinding() {
		return templateFieldBinding;
	}

	/**
	 * @param templateFieldBinding the templateFieldBinding to set
	 */
	public void setTemplateFieldBinding(TemplateFieldPojo templateFieldBinding) {
		this.templateFieldBinding = templateFieldBinding;
	}

	/**
	 * @return the fields
	 */
	public List<SourcingTemplateField> getFields() {
		return fields;
	}

	/**
	 * @param fields the fields to set
	 */
	public void setFields(List<SourcingTemplateField> fields) {
		this.fields = fields;
	}

	/**
	 * @return the addAdditionalApprovals
	 */
	public Boolean getAddAdditionalApprovals() {
		return addAdditionalApprovals;
	}

	/**
	 * @param addAdditionalApprovals the addAdditionalApprovals to set
	 */
	public void setAddAdditionalApprovals(Boolean addAdditionalApprovals) {
		this.addAdditionalApprovals = addAdditionalApprovals;
	}

	public Boolean getTemplateUsed() {
		return isTemplateUsed;
	}

	public void setTemplateUsed(Boolean templateUsed) {
		isTemplateUsed = templateUsed;
	}

	public Boolean getDocumentCompleted() {
		return documentCompleted;
	}

	public void setDocumentCompleted(Boolean documentCompleted) {
		this.documentCompleted = documentCompleted;
	}

	public List<TemplateSourcingTeamMembers> getTeamMembers() {
		return teamMembers;
	}

	public void setTeamMembers(List<TemplateSourcingTeamMembers> teamMembers) {
		if (this.teamMembers == null) {
			this.teamMembers = new ArrayList<TemplateSourcingTeamMembers>();
		} else {
			this.teamMembers.clear();
		}
		if (teamMembers != null) {
			this.teamMembers.addAll(teamMembers);
		}
	}
	
	public Boolean getReadOnlyTeamMember() {
		return readOnlyTeamMember;
	}

	public void setReadOnlyTeamMember(Boolean readOnlyTeamMember) {
		this.readOnlyTeamMember = readOnlyTeamMember;
	}

	public List<SourcingTemplateCq> copyCq(SourcingFormTemplate template) {
		List<SourcingTemplateCqItem> cqItemList = new ArrayList<>();
		List<SourcingTemplateCq> cqList = new ArrayList<>();
		for (SourcingTemplateCq cq : template.getCqs()) {
			SourcingTemplateCqItem cqItem = new SourcingTemplateCqItem();
			List<SourcingTemplateCqItem> newCqItemList = cqItem.copyCqItem(cq);
			for (SourcingTemplateCqItem cqItems : newCqItemList) {
				cqItems.setCq(cq);
				cqItemList.add(cqItems);
			}
			cq.setCqItems(cqItemList);
			cqList.add(cq);
		}
		return cqList;
	}

	public SourcingFormTemplate createShallowCopy() {
		SourcingFormTemplate sourcingFormTemplate = new SourcingFormTemplate();
		sourcingFormTemplate.setFormName(formName);
		sourcingFormTemplate.setDescription(description);
		sourcingFormTemplate.setCreatedDate(createdDate);
		sourcingFormTemplate.setCreatedBy(createdBy);
		sourcingFormTemplate.setModifiedDate(modifiedDate);
		sourcingFormTemplate.setModifiedBy(modifiedBy);
		sourcingFormTemplate.setStatus(status);
		sourcingFormTemplate.setId(id);
		return sourcingFormTemplate;
	}

	public void copyCqDetails(SourcingFormTemplate oldForm, SourcingFormTemplate newForm) {
		if (CollectionUtil.isNotEmpty(oldForm.getCqs())) {
			newForm.setCqs(new ArrayList<SourcingTemplateCq>());
			for (SourcingTemplateCq cq : oldForm.getCqs()) {
				newForm.getCqs().add(cq.copyFrom());
			}
		}
	}

	public List<RfsTemplateDocument> getRfsTemplateDocuments() {
		return rfsTemplateDocuments;
	}

	public void setRfsTemplateDocuments(List<RfsTemplateDocument> rfsTemplateDocuments) {
		this.rfsTemplateDocuments = rfsTemplateDocuments;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((formName == null) ? 0 : formName.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SourcingFormTemplate other = (SourcingFormTemplate) obj;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (formName == null) {
			if (other.formName != null)
				return false;
		} else if (!formName.equals(other.formName))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
	
}