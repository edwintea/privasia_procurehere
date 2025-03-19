package com.privasia.procurehere.core.entity;

import java.io.Serializable;
import java.util.Date;

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
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.hibernate.validator.constraints.Email;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.privasia.procurehere.core.enums.FinanceCompanyStatus;
import com.privasia.procurehere.core.utils.CustomDateTimeSerializer;

@Entity
@Table(name = "PROC_FINANCE_COMPANY")
public class FinanceCompany implements Serializable {

	private static final long serialVersionUID = -4776028973674362934L;

	public interface FinanceCompanyBasicProfile {
	}

	public interface FinanceCompanyIntermediateProfile {
	}

	@Id
	@GenericGenerator(name = "idGen", strategy = "uuid.hex")
	@GeneratedValue(generator = "idGen")
	@Column(name = "FINANCE_COMPANY_ID", length = 64)
	private String id;

	@NotNull(message = "{common.companyName.empty}", groups = { FinanceCompanyBasicProfile.class })
	@Size(min = 1, max = 128, message = "{common.companyName.length}", groups = { FinanceCompanyBasicProfile.class })
	@Column(name = "COMPANY_NAME", length = 128, nullable = false)
	private String companyName;

	@NotNull(message = "{common.fullName.empty}", groups = { FinanceCompanyBasicProfile.class })
	@Size(min = 1, max = 128, message = "{common.fullName.length}", groups = { FinanceCompanyBasicProfile.class })
	@Column(name = "FULL_NAME", length = 128, nullable = false)
	private String fullName;

	@NotNull(message = "{common.password.empty}", groups = { FinanceCompanyIntermediateProfile.class })
	@Size(min = 8, max = 64, message = "{common.password.length}", groups = { FinanceCompanyBasicProfile.class })
	@Column(name = "PASSWORD", length = 64)
	private String password;

	@NotNull(message = "{common.email.empty}", groups = { FinanceCompanyBasicProfile.class })
	@Email(message = "{common.email.valid}", groups = { FinanceCompanyBasicProfile.class })
	@Size(min = 6, max = 128, message = "{common.communicationEmail.length}", groups = { FinanceCompanyBasicProfile.class })
	@Column(name = "COMMUNICATION_EMAIL", length = 128)
	private String communicationEmail;

	@NotNull(message = "{common.loginId.empty}", groups = { FinanceCompanyBasicProfile.class })
	@Email(message = "{common.loginId.valid}", groups = { FinanceCompanyBasicProfile.class })
	@Size(min = 6, max = 128, message = "{common.loginId.length}", groups = { FinanceCompanyBasicProfile.class })
	@Column(name = "LOGIN_EMAIL", length = 128)
	private String loginEmail;

	@NotNull(message = "{common.companyContactNumber.empty}", groups = { FinanceCompanyBasicProfile.class })
	@Size(min = 6, max = 16, message = "{common.companyContactNumber.length}", groups = { FinanceCompanyBasicProfile.class })
	@Column(name = "COMPANY_CONTACT_NUMBER", length = 16)
	private String companyContactNumber;

	@NotNull(message = "{common.companyRegistrationNumber.empty}", groups = { FinanceCompanyBasicProfile.class })
	@Size(min = 1, max = 128, message = "{buyer.companyRegistrationNumber.length}", groups = { FinanceCompanyBasicProfile.class })
	@Column(name = "COMPANY_REGISTRATION_NUMBER", length = 128, nullable = true)
	private String companyRegistrationNumber;

	@JsonIgnore
	@NotNull(message = "{common.registrationOfCountry.empty}", groups = { FinanceCompanyBasicProfile.class })
	@ManyToOne(fetch = FetchType.LAZY, optional = true)
	@JoinColumn(name = "REGISTRATION_COUNTRY", nullable = true, foreignKey = @ForeignKey(name = "FK_FAINACE_REGISTERED_COUNTRY"))
	private Country registrationOfCountry;

	@Enumerated(EnumType.STRING)
	@Column(name = "FINANCE_COMPANY_STATUS")
	private FinanceCompanyStatus status;

	@Size(min = 6, max = 16, message = "{buyer.mobileNumber.length}")
	@Column(name = "MOBILE_NUMBER", length = 16)
	private String mobileNumber;

	@Digits(integer = 4, fraction = 0)
	@Column(name = "YEAR_OF_ESTABLISHED", length = 4)
	private Integer yearOfEstablished;

	@Size(min = 0, max = 16, message = "{buyer.fax.length}")
	@Column(name = "FAX_NUMBER", length = 16)
	private String faxNumber;

	@Size(max = 128, message = "{buyer.website.length}")
	@Column(name = "COMPANY_WEBSITE", length = 128)
	private String companyWebsite;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "COMPANY_STATUS", foreignKey = @ForeignKey(name = "FK_FAINACE_COMP_STATUS"))
	private CompanyStatus companyStatus;

	@Size(min = 1, max = 64, message = "{buyer.addressone.length}")
	@Column(name = "ADRESS_LINE1", length = 250)
	private String line1;

	@Size(min = 1, max = 64, message = "{buyer.addresstwo.length}")
	@Column(name = "ADRESS_LINE2", length = 250)
	private String line2;

	@Size(min = 1, max = 64, message = "{buyer.city.length}")
	@Column(name = "CITY", length = 250)
	private String city;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, optional = true)
	@JoinColumn(name = "STATE_ID", foreignKey = @ForeignKey(name = "FK_FAINACE_STATE"))
	private State state;

	@Column(name = "IS_REGISTRATION_COMPLETE")
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean registrationComplete = Boolean.FALSE;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "REGISTRATION_COMPLETE_DATE")
	private Date registrationCompleteDate;

	@Column(name = "TERMS_OF_USE_ACCEPTED")
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean termsOfUseAccepted = Boolean.FALSE;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "TERMS_OF_USE_ACCEPTED_DATE")
	private Date termsOfUseAcceptedDate;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "ACTION_DATE")
	private Date actionDate;

	@Column(name = "SUSPENDED_REMARKS", length = 250)
	private String suspendedRemarks;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ACTION_BY", nullable = true, foreignKey = @ForeignKey(name = "FK_FAINACE_ACTION_BY"))
	private User actionBy;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CREATED_DATE")
	private Date createdDate;

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
	 * @return the fullName
	 */
	public String getFullName() {
		return fullName;
	}

	/**
	 * @param fullName the fullName to set
	 */
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * @param password the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
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
	 * @return the registrationOfCountry
	 */
	public Country getRegistrationOfCountry() {
		return registrationOfCountry;
	}

	/**
	 * @param registrationOfCountry the registrationOfCountry to set
	 */
	public void setRegistrationOfCountry(Country registrationOfCountry) {
		this.registrationOfCountry = registrationOfCountry;
	}

	/**
	 * @return the status
	 */
	public FinanceCompanyStatus getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(FinanceCompanyStatus status) {
		this.status = status;
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
	 * @return the yearOfEstablished
	 */
	public Integer getYearOfEstablished() {
		return yearOfEstablished;
	}

	/**
	 * @param yearOfEstablished the yearOfEstablished to set
	 */
	public void setYearOfEstablished(Integer yearOfEstablished) {
		this.yearOfEstablished = yearOfEstablished;
	}

	/**
	 * @return the faxNumber
	 */
	public String getFaxNumber() {
		return faxNumber;
	}

	/**
	 * @param faxNumber the faxNumber to set
	 */
	public void setFaxNumber(String faxNumber) {
		this.faxNumber = faxNumber;
	}

	/**
	 * @return the companyWebsite
	 */
	public String getCompanyWebsite() {
		return companyWebsite;
	}

	/**
	 * @param companyWebsite the companyWebsite to set
	 */
	public void setCompanyWebsite(String companyWebsite) {
		this.companyWebsite = companyWebsite;
	}

	/**
	 * @return the companyStatus
	 */
	public CompanyStatus getCompanyStatus() {
		return companyStatus;
	}

	/**
	 * @param companyStatus the companyStatus to set
	 */
	public void setCompanyStatus(CompanyStatus companyStatus) {
		this.companyStatus = companyStatus;
	}

	/**
	 * @return the line1
	 */
	public String getLine1() {
		return line1;
	}

	/**
	 * @param line1 the line1 to set
	 */
	public void setLine1(String line1) {
		this.line1 = line1;
	}

	/**
	 * @return the line2
	 */
	public String getLine2() {
		return line2;
	}

	/**
	 * @param line2 the line2 to set
	 */
	public void setLine2(String line2) {
		this.line2 = line2;
	}

	/**
	 * @return the city
	 */
	public String getCity() {
		return city;
	}

	/**
	 * @param city the city to set
	 */
	public void setCity(String city) {
		this.city = city;
	}

	/**
	 * @return the state
	 */
	public State getState() {
		return state;
	}

	/**
	 * @param state the state to set
	 */
	public void setState(State state) {
		this.state = state;
	}

	/**
	 * @return the registrationComplete
	 */
	public Boolean getRegistrationComplete() {
		return registrationComplete;
	}

	/**
	 * @param registrationComplete the registrationComplete to set
	 */
	public void setRegistrationComplete(Boolean registrationComplete) {
		this.registrationComplete = registrationComplete;
	}

	/**
	 * @return the registrationCompleteDate
	 */
	public Date getRegistrationCompleteDate() {
		return registrationCompleteDate;
	}

	/**
	 * @param registrationCompleteDate the registrationCompleteDate to set
	 */
	public void setRegistrationCompleteDate(Date registrationCompleteDate) {
		this.registrationCompleteDate = registrationCompleteDate;
	}

	/**
	 * @return the termsOfUseAccepted
	 */
	public Boolean getTermsOfUseAccepted() {
		return termsOfUseAccepted;
	}

	/**
	 * @param termsOfUseAccepted the termsOfUseAccepted to set
	 */
	public void setTermsOfUseAccepted(Boolean termsOfUseAccepted) {
		this.termsOfUseAccepted = termsOfUseAccepted;
	}

	/**
	 * @return the termsOfUseAcceptedDate
	 */
	public Date getTermsOfUseAcceptedDate() {
		return termsOfUseAcceptedDate;
	}

	/**
	 * @param termsOfUseAcceptedDate the termsOfUseAcceptedDate to set
	 */
	public void setTermsOfUseAcceptedDate(Date termsOfUseAcceptedDate) {
		this.termsOfUseAcceptedDate = termsOfUseAcceptedDate;
	}

	/**
	 * @return the actionDate
	 */
	public Date getActionDate() {
		return actionDate;
	}

	/**
	 * @param actionDate the actionDate to set
	 */
	public void setActionDate(Date actionDate) {
		this.actionDate = actionDate;
	}

	/**
	 * @return the suspendedRemarks
	 */
	public String getSuspendedRemarks() {
		return suspendedRemarks;
	}

	/**
	 * @param suspendedRemarks the suspendedRemarks to set
	 */
	public void setSuspendedRemarks(String suspendedRemarks) {
		this.suspendedRemarks = suspendedRemarks;
	}

	/**
	 * @return the actionBy
	 */
	public User getActionBy() {
		return actionBy;
	}

	/**
	 * @param actionBy the actionBy to set
	 */
	public void setActionBy(User actionBy) {
		this.actionBy = actionBy;
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

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((actionDate == null) ? 0 : actionDate.hashCode());
		result = prime * result + ((city == null) ? 0 : city.hashCode());
		result = prime * result + ((communicationEmail == null) ? 0 : communicationEmail.hashCode());
		result = prime * result + ((companyContactNumber == null) ? 0 : companyContactNumber.hashCode());
		result = prime * result + ((companyName == null) ? 0 : companyName.hashCode());
		result = prime * result + ((companyRegistrationNumber == null) ? 0 : companyRegistrationNumber.hashCode());
		result = prime * result + ((companyStatus == null) ? 0 : companyStatus.hashCode());
		result = prime * result + ((companyWebsite == null) ? 0 : companyWebsite.hashCode());
		result = prime * result + ((createdDate == null) ? 0 : createdDate.hashCode());
		result = prime * result + ((faxNumber == null) ? 0 : faxNumber.hashCode());
		result = prime * result + ((fullName == null) ? 0 : fullName.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((line1 == null) ? 0 : line1.hashCode());
		result = prime * result + ((line2 == null) ? 0 : line2.hashCode());
		result = prime * result + ((loginEmail == null) ? 0 : loginEmail.hashCode());
		result = prime * result + ((mobileNumber == null) ? 0 : mobileNumber.hashCode());
		result = prime * result + ((password == null) ? 0 : password.hashCode());
		result = prime * result + ((registrationComplete == null) ? 0 : registrationComplete.hashCode());
		result = prime * result + ((registrationCompleteDate == null) ? 0 : registrationCompleteDate.hashCode());
		result = prime * result + ((status == null) ? 0 : status.hashCode());
		result = prime * result + ((suspendedRemarks == null) ? 0 : suspendedRemarks.hashCode());
		result = prime * result + ((termsOfUseAccepted == null) ? 0 : termsOfUseAccepted.hashCode());
		result = prime * result + ((termsOfUseAcceptedDate == null) ? 0 : termsOfUseAcceptedDate.hashCode());
		result = prime * result + ((yearOfEstablished == null) ? 0 : yearOfEstablished.hashCode());
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
		FinanceCompany other = (FinanceCompany) obj;
		if (actionDate == null) {
			if (other.actionDate != null)
				return false;
		} else if (!actionDate.equals(other.actionDate))
			return false;
		if (city == null) {
			if (other.city != null)
				return false;
		} else if (!city.equals(other.city))
			return false;
		if (communicationEmail == null) {
			if (other.communicationEmail != null)
				return false;
		} else if (!communicationEmail.equals(other.communicationEmail))
			return false;
		if (companyContactNumber == null) {
			if (other.companyContactNumber != null)
				return false;
		} else if (!companyContactNumber.equals(other.companyContactNumber))
			return false;
		if (companyName == null) {
			if (other.companyName != null)
				return false;
		} else if (!companyName.equals(other.companyName))
			return false;
		if (companyRegistrationNumber == null) {
			if (other.companyRegistrationNumber != null)
				return false;
		} else if (!companyRegistrationNumber.equals(other.companyRegistrationNumber))
			return false;
		if (companyStatus == null) {
			if (other.companyStatus != null)
				return false;
		} else if (!companyStatus.equals(other.companyStatus))
			return false;
		if (companyWebsite == null) {
			if (other.companyWebsite != null)
				return false;
		} else if (!companyWebsite.equals(other.companyWebsite))
			return false;
		if (createdDate == null) {
			if (other.createdDate != null)
				return false;
		} else if (!createdDate.equals(other.createdDate))
			return false;
		if (faxNumber == null) {
			if (other.faxNumber != null)
				return false;
		} else if (!faxNumber.equals(other.faxNumber))
			return false;
		if (fullName == null) {
			if (other.fullName != null)
				return false;
		} else if (!fullName.equals(other.fullName))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (line1 == null) {
			if (other.line1 != null)
				return false;
		} else if (!line1.equals(other.line1))
			return false;
		if (line2 == null) {
			if (other.line2 != null)
				return false;
		} else if (!line2.equals(other.line2))
			return false;
		if (loginEmail == null) {
			if (other.loginEmail != null)
				return false;
		} else if (!loginEmail.equals(other.loginEmail))
			return false;
		if (mobileNumber == null) {
			if (other.mobileNumber != null)
				return false;
		} else if (!mobileNumber.equals(other.mobileNumber))
			return false;
		if (password == null) {
			if (other.password != null)
				return false;
		} else if (!password.equals(other.password))
			return false;
		if (registrationComplete == null) {
			if (other.registrationComplete != null)
				return false;
		} else if (!registrationComplete.equals(other.registrationComplete))
			return false;
		if (registrationCompleteDate == null) {
			if (other.registrationCompleteDate != null)
				return false;
		} else if (!registrationCompleteDate.equals(other.registrationCompleteDate))
			return false;
		if (status != other.status)
			return false;
		if (suspendedRemarks == null) {
			if (other.suspendedRemarks != null)
				return false;
		} else if (!suspendedRemarks.equals(other.suspendedRemarks))
			return false;
		if (termsOfUseAccepted == null) {
			if (other.termsOfUseAccepted != null)
				return false;
		} else if (!termsOfUseAccepted.equals(other.termsOfUseAccepted))
			return false;
		if (termsOfUseAcceptedDate == null) {
			if (other.termsOfUseAcceptedDate != null)
				return false;
		} else if (!termsOfUseAcceptedDate.equals(other.termsOfUseAcceptedDate))
			return false;
		if (yearOfEstablished == null) {
			if (other.yearOfEstablished != null)
				return false;
		} else if (!yearOfEstablished.equals(other.yearOfEstablished))
			return false;
		return true;
	}

	public String toLogString() {
		return "FinanceCompany [id=" + id + ", companyName=" + companyName + ", fullName=" + fullName + ", password=" + password + ", communicationEmail=" + communicationEmail + ", loginEmail=" + loginEmail + ", companyContactNumber=" + companyContactNumber + ", companyRegistrationNumber=" + companyRegistrationNumber + ", status=" + status + ", mobileNumber=" + mobileNumber + ", yearOfEstablished=" + yearOfEstablished + ", faxNumber=" + faxNumber + ", companyWebsite=" + companyWebsite + ", companyStatus=" + companyStatus + ", line1=" + line1 + ", line2=" + line2 + ", city=" + city + ", registrationComplete=" + registrationComplete + ", registrationCompleteDate=" + registrationCompleteDate + ", termsOfUseAccepted=" + termsOfUseAccepted + ", termsOfUseAcceptedDate=" + termsOfUseAcceptedDate + ", actionDate=" + actionDate + ", suspendedRemarks=" + suspendedRemarks + ", createdDate=" + createdDate + "]";
	}

}
