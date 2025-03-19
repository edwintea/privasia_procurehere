package com.privasia.procurehere.core.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.privasia.procurehere.core.enums.FooterType;
import com.privasia.procurehere.core.enums.Status;
import com.privasia.procurehere.core.utils.CustomDateTimeSerializer;
import com.privasia.procurehere.core.utils.StringUtils;

/**
 * @author pooja
 */
@Entity
@Table(name = "PROC_FOOTER", indexes = { @Index(columnList = "SUPPLIER_ID", name = "INDEX_FOOTER_SUPPLIER_ID") })
public class Footer implements Serializable {

	private static final long serialVersionUID = 5091617657889348293L;

	@Id
	@GenericGenerator(name = "idGen", strategy = "uuid.hex")
	@GeneratedValue(generator = "idGen")
	@Column(name = "ID", nullable = false, length = 64)
	private String id;

	@NotEmpty(message = "{footer.title.empty}")
	@Size(min = 1, max = 128, message = "{footer.title.length}")
	@Column(name = "FOOTER_TITLE", nullable = false, length = 128)
	private String title;

	@Size(min = 1, max = 4000, message = "{footer.content.length}")
	@Column(name = "FOOTER_CONTENT", length = 4000)
	private String content;

	@Enumerated(EnumType.STRING)
	@Column(name = "FOOTER_TYPE")
	private FooterType footerType;

	@Enumerated(EnumType.STRING)
	@Column(name = "STATUS")
	private Status status;

	@ManyToOne(fetch = FetchType.LAZY, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "CREATED_BY", nullable = true, foreignKey = @ForeignKey(name = "FK_FOOTER_CREATED_BY"))
	private User createdBy;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CREATED_DATE")
	private Date createdDate;

	@ManyToOne(fetch = FetchType.LAZY, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "MODIFIED_BY", nullable = true, foreignKey = @ForeignKey(name = "FK_FOOTER_MODIFIED_BY"))
	private User modifiedBy;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "MODIFIED_DATE")
	private Date modifiedDate;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, optional = true, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "SUPPLIER_ID", nullable = true, foreignKey = @ForeignKey(name = "FK_FOOTER_SUPPLIER_ID"))
	private Supplier supplier;

	@Transient
	private String footerTypeValue;

	public Footer() {
		footerType = FooterType.DELIVERY_ORDER;
	}

	public Footer(String id, String title) {
		this.id = id;
		this.title = title;
	}

	public Footer(String id, String title, String content) {
		this.id = id;
		this.title = title;
		this.content = content;
	}

	public Footer(String id, String title, FooterType footerType, Status status, String createdBy, Date createdDate, String modifiedBy, Date modifiedDate) {
		this.id = id;
		this.title = title;
		this.footerTypeValue = footerType != null ? footerType.getValue() : "";
		this.status = status;
		User newCreatedBy = new User();
		newCreatedBy.setName(createdBy);
		this.createdBy = newCreatedBy;
		this.createdDate = createdDate;
		User newModifiedBy = new User();
		newModifiedBy.setName(modifiedBy);
		this.modifiedBy = newModifiedBy;
		this.modifiedDate = modifiedDate;

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
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * @return the content
	 */
	public String getContent() {
		return content;
	}

	/**
	 * @param content the content to set
	 */
	public void setContent(String content) {
		if (content != null) {
			this.content = StringUtils.checkString(content).replaceAll("&Acirc;", "").replaceAll("&#160;", " ");
		} else {
			this.content = null;
		}
	}

	/**
	 * @return the footerType
	 */
	public FooterType getFooterType() {
		return footerType;
	}

	/**
	 * @param footerType the footerType to set
	 */
	public void setFooterType(FooterType footerType) {
		this.footerType = footerType;
	}

	/**
	 * @return the status
	 */
	public Status getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(Status status) {
		this.status = status;
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
	 * @return the supplier
	 */
	public Supplier getSupplier() {
		return supplier;
	}

	/**
	 * @param supplier the supplier to set
	 */
	public void setSupplier(Supplier supplier) {
		this.supplier = supplier;
	}

	/**
	 * @return the footerTypeValue
	 */
	public String getFooterTypeValue() {
		return footerTypeValue;
	}

	/**
	 * @param footerTypeValue the footerTypeValue to set
	 */
	public void setFooterTypeValue(String footerTypeValue) {
		this.footerTypeValue = footerTypeValue;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Footer other = (Footer) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return title;
	}

	public String toLogString() {
		return "Declaration [id=" + id + ", title=" + title + ", content=" + content + ", footerType=" + footerType + ", status=" + status + ", createdBy=" + createdBy + ", createdDate=" + createdDate + ", modifiedBy=" + modifiedBy + ", modifiedDate=" + modifiedDate + ", supplier=" + supplier + "]";
	}

}
