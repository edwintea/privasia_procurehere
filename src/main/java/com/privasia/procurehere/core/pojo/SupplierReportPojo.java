package com.privasia.procurehere.core.pojo;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.privasia.procurehere.core.enums.PlanType;
import com.privasia.procurehere.core.enums.SubscriptionStatus;
import com.privasia.procurehere.core.enums.SupplierStatus;
import com.privasia.procurehere.core.utils.CustomDateTimeSerializer;
import com.privasia.procurehere.core.utils.StringUtils;

public class SupplierReportPojo implements Serializable {

	private static final long serialVersionUID = -6560512936324388511L;

	private String id;

	private String companyName;

	private String companyRegistrationNumber;

	private String companyType;

	private Integer yearOfEstablished;

	private String taxRegistrationNumber;

	private String companyAddress;

	private String country;

	private String state;

	private String companyContactNumber;

	private String faxNumber;

	private String companyWebsite;

	private String loginEmail;

	private String communicationEmail;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	private Date companyRegDate;

	private String registrationDate;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	private Date registrationCompleteDate;

	private String designation;

	private String mobileNumber;

	private SupplierStatus status;

	private SubscriptionStatus subscriptionStatus;

	private String currentSubPlan;

	private Date subscriptionEndDate;

	private PlanType planType;

	private Date createdDate;

	private String subscribeEndDate;

	private long totalEventsInvited;

	private long totalEventParticipated;

	private long totalEventAwarded;

	private long trackRecordUpdated;

	private String financialInformation;

	private String otherDocument;

	private String organazationalDetails;

	private String promoCode;

	private String companyDocument;

	private String geographicalCoverage;

	private String associatedBuyers;

	private String industrySector;

	private String regCompleteDate;

	private String fullName;

	// private Boolean companyProfile = Boolean.FALSE;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	private Date approvedDate;

	private String approveDate;

	public SupplierReportPojo() {
	}

	public SupplierReportPojo(String id, String companyName) {
		super();
		this.id = id;
		this.companyName = companyName;
	}

	public SupplierReportPojo(String id, String companyName, String registrationNumber, String country, String companyType, SubscriptionStatus subscriptionStatus, SupplierStatus supplierStatus, Date registrationDate, Date approvedDate) {
		super();
		this.id = id;
		this.companyName = companyName;
		this.companyRegistrationNumber = registrationNumber;
		this.country = country;
		this.companyType = companyType;
		this.subscriptionStatus = subscriptionStatus;
		this.status = supplierStatus;
		this.companyRegDate = registrationDate;
		this.approvedDate = approvedDate;
	}

	public SupplierReportPojo(String id, String companyName, String registrationNumber, String companyType, Integer yearOfEstablished, String taxRegistrationNumber, String line1, String line2, String city, String postcode, String country, String state, String companyContactNumber, String faxNumber, String companyWebsite, String loginEmail, String communicationEmail, Date registrationDate, Date registrationCompleteDate, String designation, String mobileNumber, SupplierStatus supplierStatus, SubscriptionStatus subscriptionStatus, String currentSubPlan, Date subscriptionEndDate, String promoCode, String fullName, Date approvedDate) {
		super();
		this.id = id;
		this.companyName = companyName;
		this.companyRegistrationNumber = registrationNumber;
		this.companyType = companyType;
		this.yearOfEstablished = yearOfEstablished;
		this.taxRegistrationNumber = taxRegistrationNumber;
		if (StringUtils.checkString(line1).length() > 0) {
			this.companyAddress = line1;
		}
		if (StringUtils.checkString(this.companyAddress).length() == 0 && StringUtils.checkString(line2).length() > 0) {
			this.companyAddress = line2;
		}
		if (StringUtils.checkString(this.companyAddress).length() > 0 && StringUtils.checkString(line2).length() > 0) {
			this.companyAddress += " " + line2;
		}
		if (StringUtils.checkString(this.companyAddress).length() == 0 && StringUtils.checkString(city).length() > 0) {
			this.companyAddress = city;
		}
		if (StringUtils.checkString(this.companyAddress).length() > 0 && StringUtils.checkString(city).length() > 0) {
			this.companyAddress += " " + city;
		}
		if (StringUtils.checkString(this.companyAddress).length() == 0 && StringUtils.checkString(postcode).length() > 0) {
			this.companyAddress = postcode;
		}
		if (StringUtils.checkString(this.companyAddress).length() > 0 && StringUtils.checkString(postcode).length() > 0) {
			this.companyAddress += " " + postcode;
		}

		// this.companyAddress = line1 + " " + line2 + " " + city + " " + postcode;
		this.country = country;
		this.state = state;
		this.companyContactNumber = companyContactNumber;
		this.faxNumber = faxNumber;
		this.companyWebsite = companyWebsite;
		this.loginEmail = loginEmail;
		this.communicationEmail = communicationEmail;
		this.companyRegDate = registrationDate;
		this.registrationCompleteDate = registrationCompleteDate;
		this.designation = designation;
		this.mobileNumber = mobileNumber;
		this.status = supplierStatus;
		this.subscriptionStatus = subscriptionStatus;
		this.currentSubPlan = currentSubPlan;
		this.subscriptionEndDate = subscriptionEndDate;
		this.promoCode = promoCode;
		this.fullName = fullName;
		// this.companyProfile = (supplierCompanyProfile > 0);
		this.approvedDate = approvedDate;

	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getCompanyRegistrationNumber() {
		return companyRegistrationNumber;
	}

	public void setCompanyRegistrationNumber(String companyRegistrationNumber) {
		this.companyRegistrationNumber = companyRegistrationNumber;
	}

	public String getCompanyType() {
		return companyType;
	}

	public void setCompanyType(String companyType) {
		this.companyType = companyType;
	}

	public Integer getYearOfEstablished() {
		return yearOfEstablished;
	}

	public void setYearOfEstablished(Integer yearOfEstablished) {
		this.yearOfEstablished = yearOfEstablished;
	}

	public String getTaxRegistrationNumber() {
		return taxRegistrationNumber;
	}

	public void setTaxRegistrationNumber(String taxRegistrationNumber) {
		this.taxRegistrationNumber = taxRegistrationNumber;
	}

	public String getCompanyAddress() {
		return companyAddress;
	}

	public void setCompanyAddress(String companyAddress) {
		this.companyAddress = companyAddress;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getCompanyContactNumber() {
		return companyContactNumber;
	}

	public void setCompanyContactNumber(String companyContactNumber) {
		this.companyContactNumber = companyContactNumber;
	}

	public String getFaxNumber() {
		return faxNumber;
	}

	public void setFaxNumber(String faxNumber) {
		this.faxNumber = faxNumber;
	}

	public String getCompanyWebsite() {
		return companyWebsite;
	}

	public void setCompanyWebsite(String companyWebsite) {
		this.companyWebsite = companyWebsite;
	}

	public String getLoginEmail() {
		return loginEmail;
	}

	public void setLoginEmail(String loginEmail) {
		this.loginEmail = loginEmail;
	}

	public String getCommunicationEmail() {
		return communicationEmail;
	}

	public void setCommunicationEmail(String communicationEmail) {
		this.communicationEmail = communicationEmail;
	}

	public String getRegistrationDate() {
		return registrationDate;
	}

	public void setRegistrationDate(String registrationDate) {
		this.registrationDate = registrationDate;
	}

	public Date getRegistrationCompleteDate() {
		return registrationCompleteDate;
	}

	public void setRegistrationCompleteDate(Date registrationCompleteDate) {
		this.registrationCompleteDate = registrationCompleteDate;
	}

	public String getDesignation() {
		return designation;
	}

	public void setDesignation(String designation) {
		this.designation = designation;
	}

	public String getMobileNumber() {
		return mobileNumber;
	}

	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}

	public SupplierStatus getStatus() {
		return status;
	}

	public void setStatus(SupplierStatus status) {
		this.status = status;
	}

	public SubscriptionStatus getSubscriptionStatus() {
		return subscriptionStatus;
	}

	public void setSubscriptionStatus(SubscriptionStatus subscriptionStatus) {
		this.subscriptionStatus = subscriptionStatus;
	}

	public String getCurrentSubPlan() {
		return currentSubPlan;
	}

	public void setCurrentSubPlan(String currentSubPlan) {
		this.currentSubPlan = currentSubPlan;
	}

	public Date getSubscriptionEndDate() {
		return subscriptionEndDate;
	}

	public void setSubscriptionEndDate(Date subscriptionEndDate) {
		this.subscriptionEndDate = subscriptionEndDate;
	}

	public PlanType getPlanType() {
		return planType;
	}

	public void setPlanType(PlanType planType) {
		this.planType = planType;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public String getSubscribeEndDate() {
		return subscribeEndDate;
	}

	public void setSubscribeEndDate(String subscribeEndDate) {
		this.subscribeEndDate = subscribeEndDate;
	}

	public Date getCompanyRegDate() {
		return companyRegDate;
	}

	public void setCompanyRegDate(Date companyRegDate) {
		this.companyRegDate = companyRegDate;
	}

	public long getTotalEventsInvited() {
		return totalEventsInvited;
	}

	public void setTotalEventsInvited(long totalEventsInvited) {
		this.totalEventsInvited = totalEventsInvited;
	}

	public long getTotalEventParticipated() {
		return totalEventParticipated;
	}

	public void setTotalEventParticipated(long totalEventParticipated) {
		this.totalEventParticipated = totalEventParticipated;
	}

	public long getTotalEventAwarded() {
		return totalEventAwarded;
	}

	public void setTotalEventAwarded(long totalEventAwarded) {
		this.totalEventAwarded = totalEventAwarded;
	}

	public long getTrackRecordUpdated() {
		return trackRecordUpdated;
	}

	public void setTrackRecordUpdated(long trackRecordUpdated) {
		this.trackRecordUpdated = trackRecordUpdated;
	}

	public String getFinancialInformation() {
		return financialInformation;
	}

	public void setFinancialInformation(String financialInformation) {
		this.financialInformation = financialInformation;
	}

	public String getOtherDocument() {
		return otherDocument;
	}

	public void setOtherDocument(String otherDocument) {
		this.otherDocument = otherDocument;
	}

	public String getOrganazationalDetails() {
		return organazationalDetails;
	}

	public void setOrganazationalDetails(String organazationalDetails) {
		this.organazationalDetails = organazationalDetails;
	}

	public String getPromoCode() {
		return promoCode;
	}

	public void setPromoCode(String promoCode) {
		this.promoCode = promoCode;
	}

	public String getCompanyDocument() {
		return companyDocument;
	}

	public void setCompanyDocument(String companyDocument) {
		this.companyDocument = companyDocument;
	}

	public String getGeographicalCoverage() {
		return geographicalCoverage;
	}

	public void setGeographicalCoverage(String geographicalCoverage) {
		this.geographicalCoverage = geographicalCoverage;
	}

	public String getAssociatedBuyers() {
		return associatedBuyers;
	}

	public void setAssociatedBuyers(String associatedBuyers) {
		this.associatedBuyers = associatedBuyers;
	}

	public String getIndustrySector() {
		return industrySector;
	}

	public void setIndustrySector(String industrySector) {
		this.industrySector = industrySector;
	}

	public String getRegCompleteDate() {
		return regCompleteDate;
	}

	public void setRegCompleteDate(String regCompleteDate) {
		this.regCompleteDate = regCompleteDate;
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

	public Date getApprovedDate() {
		return approvedDate;
	}

	public void setApprovedDate(Date approvedDate) {
		this.approvedDate = approvedDate;
	}

	public String getApproveDate() {
		return approveDate;
	}

	public void setApproveDate(String approveDate) {
		this.approveDate = approveDate;
	}

	// /**
	// * @return the companyProfile
	// */
	// public Boolean getCompanyProfile() {
	// return companyProfile;
	// }
	//
	// /**
	// * @param companyProfile the companyProfile to set
	// */
	// public void setCompanyProfile(Boolean companyProfile) {
	// this.companyProfile = companyProfile;
	// }

}
