/**
 * 
 */
package com.privasia.procurehere.core.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * @author Arc
 */
@Entity
@Table(name = "PROC_EMAIL_SETTINGS")
public class EmailSettings implements Serializable {

	private static final long serialVersionUID = 7498799747513748593L;

	@Id
	@GenericGenerator(name = "idGen", strategy = "uuid.hex")
	@GeneratedValue(generator = "idGen")
	@Column(name = "ID", length = 64)
	private String id;

	@NotEmpty(message = "{email.adminEmail.empty}")
	@Size(min = 1, max = 500, message = "{email.adminEmail.length}")
	@Column(name = "ADMIN_EMAIL_ACCOUNT", length = 500, nullable = false)
	private String supplierSignupNotificationEmailAccount;

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
	 * @return the supplierSignupNotificationEmailAccount
	 */
	public String getSupplierSignupNotificationEmailAccount() {
		return supplierSignupNotificationEmailAccount;
	}

	/**
	 * @param supplierSignupNotificationEmailAccount the supplierSignupNotificationEmailAccount to set
	 */
	public void setSupplierSignupNotificationEmailAccount(String supplierSignupNotificationEmailAccount) {
		this.supplierSignupNotificationEmailAccount = supplierSignupNotificationEmailAccount;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((supplierSignupNotificationEmailAccount == null) ? 0 : supplierSignupNotificationEmailAccount.hashCode());
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
		EmailSettings other = (EmailSettings) obj;
		if (supplierSignupNotificationEmailAccount == null) {
			if (other.supplierSignupNotificationEmailAccount != null)
				return false;
		} else if (!supplierSignupNotificationEmailAccount.equals(other.supplierSignupNotificationEmailAccount))
			return false;
		return true;
	}

}
