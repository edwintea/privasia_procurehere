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
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.validator.constraints.Email;

import com.privasia.procurehere.core.entity.Supplier.SupplierSignup;
import com.privasia.procurehere.core.entity.Supplier.SupplierStep1;

/**
 * @author Arc
 */
@Entity
@Table(name = "PROC_OWNER")
public class Owner implements Serializable {

	private static final long serialVersionUID = 3305976308246202074L;

	@Id
	@GenericGenerator(name = "idGen", strategy = "uuid.hex")
	@GeneratedValue(generator = "idGen")
	@Column(name = "OWNER_ID", length = 64)
	private String id;

	@NotNull(message = "{supplier.companyName.empty}", groups = { SupplierStep1.class, SupplierSignup.class })
	@Size(min = 1, max = 128, message = "{supplier.companyName.length}", groups = { SupplierStep1.class, SupplierSignup.class })
	@Column(name = "COMPANY_NAME", length = 124, nullable = false)
	private String companyName;

	@Size(min = 10, max = 16, message = "{supplier.mobileNumber.length}", groups = { SupplierStep1.class, SupplierSignup.class })
	@Column(name = "MOBILE_NUMBER", length = 16)
	private String mobileNumber;

	@NotNull(message = "{supplier.companyContactNumber.empty}", groups = { SupplierStep1.class, SupplierSignup.class })
	@Size(min = 8, max = 16, message = "{supplier.companyContactNumber.length}", groups = { SupplierStep1.class, SupplierSignup.class })
	@Column(name = "COMPANY_CONTACT_NUMBER", length = 16)
	private String companyContactNumber;

	@NotNull(message = "{supplier.companyRegistrationNumber.empty}", groups = { SupplierStep1.class, SupplierSignup.class })
	@Size(min = 1, max = 128, message = "{supplier.companyRegistrationNumber.length}", groups = { SupplierStep1.class, SupplierSignup.class })
	@Column(name = "COMPANY_REGISTRATION_NUMBER", length = 124, nullable = false)
	private String companyRegistrationNumber;

	@NotNull(message = "{supplier.loginId.empty}", groups = { SupplierSignup.class })
	@Email(message = "{supplier.loginId.valid}", groups = { SupplierSignup.class })
	@Size(min = 1, max = 128, message = "{supplier.loginId.length}", groups = { SupplierSignup.class })
	@Column(name = "LOGIN_EMAIL", length = 128, unique = true)
	private String loginEmail;

	@NotNull(message = "{supplier.communicationEmail.empty}", groups = { SupplierStep1.class, SupplierSignup.class })
	@Email(message = "{supplier.communicationEmail.valid}", groups = { SupplierStep1.class, SupplierSignup.class })
	@Size(min = 1, max = 128, message = "{supplier.communicationEmail.length}", groups = { SupplierStep1.class, SupplierSignup.class })
	@Column(name = "COMMUNICATION_EMAIL", length = 128)
	private String communicationEmail;

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
	 * @return the companyName
	 */
	public String getCompanyName() {
		return companyName;
	}

	/**
	 * @param companyName the companyName to set
	 */
	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	/**
	 * @return the mobileNumber
	 */
	public String getMobileNumber() {
		return mobileNumber;
	}

	/**
	 * @param mobileNumber the mobileNumber to set
	 */
	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}

	/**
	 * @return the companyContactNumber
	 */
	public String getCompanyContactNumber() {
		return companyContactNumber;
	}

	/**
	 * @param companyContactNumber the companyContactNumber to set
	 */
	public void setCompanyContactNumber(String companyContactNumber) {
		this.companyContactNumber = companyContactNumber;
	}

	/**
	 * @return the companyRegistrationNumber
	 */
	public String getCompanyRegistrationNumber() {
		return companyRegistrationNumber;
	}

	/**
	 * @param companyRegistrationNumber the companyRegistrationNumber to set
	 */
	public void setCompanyRegistrationNumber(String companyRegistrationNumber) {
		this.companyRegistrationNumber = companyRegistrationNumber;
	}

	/**
	 * @return the loginEmail
	 */
	public String getLoginEmail() {
		return loginEmail;
	}

	/**
	 * @param loginEmail the loginEmail to set
	 */
	public void setLoginEmail(String loginEmail) {
		this.loginEmail = loginEmail;
	}

	/**
	 * @return the communicationEmail
	 */
	public String getCommunicationEmail() {
		return communicationEmail;
	}

	/**
	 * @param communicationEmail the communicationEmail to set
	 */
	public void setCommunicationEmail(String communicationEmail) {
		this.communicationEmail = communicationEmail;
	}

}
