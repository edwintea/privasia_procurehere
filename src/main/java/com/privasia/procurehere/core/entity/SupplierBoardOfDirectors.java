package com.privasia.procurehere.core.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.privasia.procurehere.core.utils.CustomDateTimeSerializer;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.validator.constraints.Email;

@Entity
@Table(name = "BOARD_OF_DIRS")
public class SupplierBoardOfDirectors implements Serializable {

	private static final long serialVersionUID = 9003345004437400863L;

	@Id
	@GenericGenerator(name = "idGen", strategy = "uuid.hex")
	@GeneratedValue(generator = "idGen")
	@Column(name = "ID", length = 64)
	private String id;

	@Size(min = 0, max = 64, message = "{supplier.bod.name.length}")
	@Pattern(regexp = "^[A-Za-z.\\'-\\/' ']*$", message = "{supplier.bod.name.pattern}")
	@Column(name = "DIR_NAME", nullable = false)
	private String directorName;

	@Column(name = "IDENTIFICATION_TYPE", nullable = false)
	private String idType;

	@Size(min = 0, max = 32, message = "{supplier.idnumber.length}")
	@Pattern(regexp = "^[A-Za-z0-9-\\/]*$", message = "{supplier.idnumber.pattern}")
	@Column(name = "IDENTIFICATION_NUMBER", nullable = false)
	private String idNumber;

	@Column(name = "DIRECTOR_TYPE", length = 32, nullable = false)
	private String dirType;

	@Email
	@Column(name = "DIRECTOR_EMAIL", length = 64)
	private String dirEmail;

	@Pattern(regexp = "^[0-9-+]*$", message = "{supplier.idnumber.pattern}")
	@Column(name = "DIRECTOR_CONTACT", length = 24)
	private String dirContact;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "SUPPLIER_ID", nullable = false, foreignKey = @ForeignKey(name = "FK_SBD_SUPPLIER_ID"))
	private Supplier supplier;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "CREATED_BY", nullable = true, foreignKey = @ForeignKey(name = "FK_SBD_CREATED_BY"))
	private User createdBy;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CREATED_DATE")
	private Date createdDate;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "MODIFIED_BY", nullable = true, foreignKey = @ForeignKey(name = "FK_SBD_MODIFIED_BY"))
	private User modifiedBy;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "MODIFIED_DATE")
	private Date modifiedDate;

	public SupplierBoardOfDirectors(String id, String directorName, String idType, String idNumber, String dirType,
			String dirEmail, String dirContact) {
		super();
		this.id = id;
		this.directorName = directorName;
		this.idType = idType;
		this.idNumber = idNumber;
		this.dirType = dirType;
		this.dirEmail = dirEmail;
		this.dirContact = dirContact;
	}

	public SupplierBoardOfDirectors() {
		super();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDirectorName() {
		return directorName;
	}

	public void setDirectorName(String directorName) {
		this.directorName = directorName;
	}

	public String getIdType() {
		return idType;
	}

	public void setIdType(String idType) {
		this.idType = idType;
	}

	public String getIdNumber() {
		return idNumber;
	}

	public void setIdNumber(String idNumber) {
		this.idNumber = idNumber;
	}

	public String getDirType() {
		return dirType;
	}

	public void setDirType(String dirType) {
		this.dirType = dirType;
	}

	public String getDirEmail() {
		return dirEmail;
	}

	public void setDirEmail(String dirEmail) {
		this.dirEmail = dirEmail;
	}

	public String getDirContact() {
		return dirContact;
	}

	public void setDirContact(String dirContact) {
		this.dirContact = dirContact;
	}

	public Supplier getSupplier() {
		return supplier;
	}

	public void setSupplier(Supplier supplier) {
		this.supplier = supplier;
	}

	public User getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(User createdBy) {
		this.createdBy = createdBy;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public User getModifiedBy() {
		return modifiedBy;
	}

	public void setModifiedBy(User modifiedBy) {
		this.modifiedBy = modifiedBy;
	}

	public Date getModifiedDate() {
		return modifiedDate;
	}

	public void setModifiedDate(Date modifiedDate) {
		this.modifiedDate = modifiedDate;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((dirType == null) ? 0 : dirType.hashCode());
		result = prime * result + ((directorName == null) ? 0 : directorName.hashCode());
		result = prime * result + ((idNumber == null) ? 0 : idNumber.hashCode());
		result = prime * result + ((idType == null) ? 0 : idType.hashCode());
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
		SupplierBoardOfDirectors other = (SupplierBoardOfDirectors) obj;
		if (dirType == null) {
			if (other.dirType != null)
				return false;
		} else if (!dirType.equals(other.dirType))
			return false;
		if (directorName == null) {
			if (other.directorName != null)
				return false;
		} else if (!directorName.equals(other.directorName))
			return false;
		if (idNumber == null) {
			if (other.idNumber != null)
				return false;
		} else if (!idNumber.equals(other.idNumber))
			return false;
		if (idType == null) {
			if (other.idType != null)
				return false;
		} else if (!idType.equals(other.idType))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "SupplierBoardOfDirectors [dirContact=" + dirContact + ", dirEmail=" + dirEmail + ", dirType=" + dirType + ", directorName=" + directorName + ", id=" + id + ", idNumber=" + idNumber + ", idType=" + idType + ", supplier=" + supplier + "]";
	}


}