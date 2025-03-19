package com.privasia.procurehere.core.pojo;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.privasia.procurehere.core.enums.BuyerStatus;
import com.privasia.procurehere.core.enums.PlanType;
import com.privasia.procurehere.core.enums.SubscriptionStatus;
import com.privasia.procurehere.core.enums.SupplierStatus;
import com.privasia.procurehere.core.utils.CustomDateTimeSerializer;
import com.privasia.procurehere.core.utils.StringUtils;

public class BuyerReportPojo implements Serializable {

	private static final long serialVersionUID = -6560512936324388511L;

	private String id;

	private String companyName;

	private String companyRegistrationNumber;

	private BuyerStatus status;

	private PlanType planType;

	private String companyType;

	private String country;

	private SubscriptionStatus subscriptionStatus;

	private SupplierStatus supplierStatus;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	private Date registrationCompleteDate;

	private Integer yearOfEstablished;

	private String companyAddress;

	private String state;

	private String companyContactNumber;

	private String faxNumber;

	private String companyWebsite;

	private String loginEmail;

	private String communicationEmail;

	private String mobileNumber;

	private String publicContextPath;

	private String currentSubPlan;

	private Date subscriptionEndDate;

	private Integer noOfUsers;

	private Integer noOfEvents;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	private Date createdDate;

	private String registrationDate;

	private String subscribeEndDate;

	private String fullName;

	private Integer actNoOfUsers;

	private Integer actNoOfEvents;
	
	private String createDate;

    public BuyerReportPojo() {

    }

    public String getId() {
		return id;
	}

	public BuyerReportPojo(String id, String companyName, String registrationNumber, String country, String companyType, PlanType planType, BuyerStatus status) {
		super();
		this.id = id;
		this.companyName = companyName;
		this.companyRegistrationNumber = registrationNumber;
		this.country = country;
		this.companyType = companyType;
		this.planType = planType;
		this.status = status;
	}

	public BuyerReportPojo(String id, String companyName, String registrationNumber, String country, String companyType, PlanType planType, BuyerStatus status, Date createdDate) {
		super();
		this.id = id;
		this.companyName = companyName;
		this.companyRegistrationNumber = registrationNumber;
		this.country = country;
		this.companyType = companyType;
		this.planType = planType;
		this.status = status;
		this.createdDate = createdDate;
	}

	public BuyerReportPojo(String id, String companyName, String registrationNumber, String companyType, Integer yearOfEstablished, Date registrationCompleteDate, BuyerStatus status, String line1, String line2, String city, String postcode, String country, String state, String companyContactNumber, String faxNumber, String companyWebsite, String loginEmail, String communicationEmail, String mobileNumber, String publicContextPath, String currentSubPlan, Date subscriptionEndDate, Integer noOfUsers, Integer noOfEvents, Date createdDate, String fullName, Integer actNoOfUsers, Integer actNoOfEvents, PlanType planType) {
		super();
		this.id = id;
		this.companyName = StringUtils.checkString(companyName);
		this.companyRegistrationNumber = StringUtils.checkString(registrationNumber);
		this.companyType = StringUtils.checkString(companyType);
		this.yearOfEstablished = yearOfEstablished;
		this.registrationCompleteDate = registrationCompleteDate;
		this.status = status;
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

		this.country = StringUtils.checkString(country);
		this.state = StringUtils.checkString(state);
		this.companyContactNumber = StringUtils.checkString(companyContactNumber);
		this.faxNumber = StringUtils.checkString(faxNumber);
		this.companyWebsite = StringUtils.checkString(companyWebsite);
		this.loginEmail = StringUtils.checkString(loginEmail);
		this.communicationEmail = StringUtils.checkString(communicationEmail);
		this.mobileNumber = StringUtils.checkString(mobileNumber);
		this.publicContextPath = StringUtils.checkString(publicContextPath);
		this.currentSubPlan = StringUtils.checkString(currentSubPlan);
		this.subscriptionEndDate = subscriptionEndDate;
		this.noOfUsers = noOfUsers;
		this.noOfEvents = noOfEvents;
		this.createdDate = createdDate;
		this.fullName = fullName;
		this.actNoOfUsers = actNoOfUsers;
		this.actNoOfEvents = actNoOfEvents;
		this.planType = planType;
	}

	/**
	 * @return the users
	 */
	public String getUserDetails() {
		return (actNoOfUsers != null ? actNoOfUsers : 0) + "/" + (noOfUsers != null ? noOfUsers : 0);
	}

	/**
	 * @return the users
	 */
	public String getEventDetails() {
		return (actNoOfEvents != null ? actNoOfEvents : 0) + "/" + (noOfEvents != null ? noOfEvents : 0);
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

	public BuyerStatus getStatus() {
		return status;
	}

	public void setStatus(BuyerStatus status) {
		this.status = status;
	}

	public PlanType getPlanType() {
		return planType;
	}

	public void setPlanType(PlanType planType) {
		this.planType = planType;
	}

	public String getCompanyType() {
		return companyType;
	}

	public void setCompanyType(String companyType) {
		this.companyType = companyType;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public SubscriptionStatus getSubscriptionStatus() {
		return subscriptionStatus;
	}

	public void setSubscriptionStatus(SubscriptionStatus subscriptionStatus) {
		this.subscriptionStatus = subscriptionStatus;
	}

	public SupplierStatus getSupplierStatus() {
		return supplierStatus;
	}

	public void setSupplierStatus(SupplierStatus supplierStatus) {
		this.supplierStatus = supplierStatus;
	}

	public Date getRegistrationCompleteDate() {
		return registrationCompleteDate;
	}

	public void setRegistrationCompleteDate(Date registrationCompleteDate) {
		this.registrationCompleteDate = registrationCompleteDate;
	}

	public Integer getYearOfEstablished() {
		return yearOfEstablished;
	}

	public void setYearOfEstablished(Integer yearOfEstablished) {
		this.yearOfEstablished = yearOfEstablished;
	}

	public String getCompanyAddress() {
		return companyAddress;
	}

	public void setCompanyAddress(String companyAddress) {
		this.companyAddress = companyAddress;
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

	public String getMobileNumber() {
		return mobileNumber;
	}

	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}

	public String getPublicContextPath() {
		return publicContextPath;
	}

	public void setPublicContextPath(String publicContextPath) {
		this.publicContextPath = publicContextPath;
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

	public Integer getNoOfUsers() {
		return noOfUsers;
	}

	public void setNoOfUsers(Integer noOfUsers) {
		this.noOfUsers = noOfUsers;
	}

	public Integer getNoOfEvents() {
		return noOfEvents;
	}

	public void setNoOfEvents(Integer noOfEvents) {
		this.noOfEvents = noOfEvents;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public String getRegistrationDate() {
		return registrationDate;
	}

	public void setRegistrationDate(String registrationDate) {
		this.registrationDate = registrationDate;
	}

	public String getSubscribeEndDate() {
		return subscribeEndDate;
	}

	public void setSubscribeEndDate(String subscribeEndDate) {
		this.subscribeEndDate = subscribeEndDate;
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
	 * @return the actNoOfUsers
	 */
	public Integer getActNoOfUsers() {
		return actNoOfUsers;
	}

	/**
	 * @param actNoOfUsers the actNoOfUsers to set
	 */
	public void setActNoOfUsers(Integer actNoOfUsers) {
		this.actNoOfUsers = actNoOfUsers;
	}

	/**
	 * @return the actNoOfEvents
	 */
	public Integer getActNoOfEvents() {
		return actNoOfEvents;
	}

	/**
	 * @param actNoOfEvents the actNoOfEvents to set
	 */
	public void setActNoOfEvents(Integer actNoOfEvents) {
		this.actNoOfEvents = actNoOfEvents;
	}

	public String getCreateDate() {
		return createDate;
	}

	public void setCreateDate(String createDate) {
		this.createDate = createDate;
	}

	@Override
	public String toString() {
		return "BuyerReportPojo [id=" + id + ", companyName=" + companyName + ", createdDate=" + createdDate + ", createDate=" + createDate + "]";
	}

	
}
