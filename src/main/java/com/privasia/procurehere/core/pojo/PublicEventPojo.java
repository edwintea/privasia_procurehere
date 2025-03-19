package com.privasia.procurehere.core.pojo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.privasia.procurehere.core.entity.BusinessUnit;
import com.privasia.procurehere.core.entity.Buyer;
import com.privasia.procurehere.core.entity.Country;
import com.privasia.procurehere.core.entity.IndustryCategory;
import com.privasia.procurehere.core.entity.User;
import com.privasia.procurehere.core.enums.MeetingType;
import com.privasia.procurehere.core.enums.RfxTypes;
import com.privasia.procurehere.core.utils.CustomDateTimeSerializer;

public class PublicEventPojo implements Serializable {

	private static final long serialVersionUID = 3327697931619451550L;

	private String id;
	private String eventName;
	private String eventDescription;
	private Country country;
	private String referenceNumber;
	private String companyName;
	@JsonSerialize(using = CustomDateTimeSerializer.class)
	private Date eventStart;
	@JsonSerialize(using = CustomDateTimeSerializer.class)
	private Date eventEndDate;
	private User eventOwner;
	private IndustryCategory industryCategory;
	private BusinessUnit businessUnit;
	private Buyer buyer;
	private RfxTypes type;

	private String referanceNumber;
	private String unitName;
	private String code;
	private String name;

	private Date siteVisitDate;
	private String contactName;
	private String contactNumber;

	private List<IndustryCategory> industryCategories;

	private String industryCategoriesNames;
	private MeetingType meetingType;

	private String siteVisitMeetingDetails;
	private String buyerId;

	private BigDecimal participationFees;

	private String currencyCode;

	public PublicEventPojo() {
	}

	public PublicEventPojo(String id, String eventName, Country country, String referenceNumber, String companyName, Date eventEndDate, User eventOwner, IndustryCategory industryCategory) {
		this.id = id;
		this.eventName = eventName;
		this.country = country;
		this.referenceNumber = referenceNumber;
		this.companyName = companyName;
		this.eventEndDate = eventEndDate;
		this.eventOwner = eventOwner;
		this.industryCategory = industryCategory;
	}

	public PublicEventPojo(String id, String eventName, String referenceNumber, Date eventEndDate, RfxTypes type, IndustryCategory industryCategory, User eventOwner, Buyer buyer, Country country, String eventDescription) {
		this.id = id;
		this.eventName = eventName;
		this.country = country;
		if (country != null) {
			country.getCountryName();
			country.setCreatedBy(null);
		}
		this.referenceNumber = referenceNumber;
		this.buyer = buyer;
		if (buyer != null) {
			this.companyName = buyer.getCompanyName();
		}
		this.eventEndDate = eventEndDate;
		this.type = type;
		this.eventOwner = eventOwner;
		if (eventOwner != null) {
			eventOwner.getName();
		}
		this.industryCategory = industryCategory;
		if (industryCategory != null) {
			industryCategory.getName();
		}
		this.eventDescription = eventDescription;

	}

	public PublicEventPojo(String id, String eventName, String referanceNumber, Date eventStart, Date eventEndDate, RfxTypes type, User eventOwner, Buyer buyer, Country country, String eventDescription, String unitName, String code, String name) {
		this.id = id;
		this.eventName = eventName;
		this.country = country;
		if (country != null) {
			country.getCountryName();
			country.setCreatedBy(null);
		}
		this.referanceNumber = referanceNumber;
		this.buyer = buyer;
		if (buyer != null) {
			this.companyName = buyer.getCompanyName();
		}
		this.eventStart = eventStart;
		this.eventEndDate = eventEndDate;
		this.type = type;
		this.eventOwner = eventOwner;
		if (eventOwner != null) {
			eventOwner.getName();
		}
		/*
		 * this.industryCategory = industryCategory; if (industryCategory != null) { industryCategory.getName(); }
		 */
		this.eventDescription = eventDescription;
		BusinessUnit businessUnit = new BusinessUnit();
		if (businessUnit != null) {
			businessUnit.setUnitName(unitName);
		}
		this.businessUnit = businessUnit;

		IndustryCategory industryCategory = new IndustryCategory();
		if (industryCategory != null) {
			industryCategory.setCode(code);
			industryCategory.setName(name);
		}
		this.industryCategory = industryCategory;

		// if(businessUnit != null){
		// businessUnit.getUnitName();
		// }
	}

	public PublicEventPojo(String id, String eventName, String referanceNumber, Date eventStart, Date eventEnd, String unitName, String name, String code, Date siteVisitDate, String contactName, String meetingType, String type, String contactNumber, String buyerId) {
		this.id = id;
		this.eventName = eventName;
		this.referanceNumber = referanceNumber;
		this.eventStart = eventStart;
		this.eventEndDate = eventEnd;
		this.unitName = unitName;
		this.code = code;
		this.name = name;
		this.siteVisitDate = siteVisitDate;
		this.contactName = contactName;
		this.meetingType = MeetingType.convertFromString(meetingType);
		this.contactNumber = contactNumber;
		this.type = RfxTypes.fromStringToRfxType(type);
		this.buyerId = buyerId;
	}

	public PublicEventPojo(String id, String eventName, String referanceNumber, Date eventStart, Date eventEnd, String unitName, String name, String code, Date siteVisitDate, String contactName, String meetingType, String type, String contactNumber, String buyerId,
						   BigDecimal participationFees, String currencyCode) {
		this.id = id;
		this.eventName = eventName;
		this.referanceNumber = referanceNumber;
		this.eventStart = eventStart;
		this.eventEndDate = eventEnd;
		this.unitName = unitName;
		this.code = code;
		this.name = name;
		this.siteVisitDate = siteVisitDate;
		this.contactName = contactName;
		this.meetingType = MeetingType.convertFromString(meetingType);
		this.contactNumber = contactNumber;
		this.type = RfxTypes.fromStringToRfxType(type);
		this.buyerId = buyerId;
		this.participationFees = participationFees;
		this.currencyCode = currencyCode;
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
	 * @return the eventDescription
	 */
	public String getEventDescription() {
		return eventDescription;
	}

	/**
	 * @param eventDescription the eventDescription to set
	 */
	public void setEventDescription(String eventDescription) {
		this.eventDescription = eventDescription;
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
	 * @return the eventName
	 */
	public String getEventName() {
		return eventName;
	}

	/**
	 * @param eventName the eventName to set
	 */
	public void setEventName(String eventName) {
		this.eventName = eventName;
	}

	/**
	 * @return the country
	 */
	public Country getCountry() {
		return country;
	}

	/**
	 * @param country the country to set
	 */
	public void setCountry(Country country) {
		this.country = country;
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
	 * @return the referenceNumber
	 */
	public String getReferenceNumber() {
		return referenceNumber;
	}

	/**
	 * @param referenceNumber the referenceNumber to set
	 */
	public void setReferenceNumber(String referenceNumber) {
		this.referenceNumber = referenceNumber;
	}

	/**
	 * @return the eventEndDate
	 */
	public Date getEventEndDate() {
		return eventEndDate;
	}

	/**
	 * @param eventEndDate the eventEndDate to set
	 */
	public void setEventEndDate(Date eventEndDate) {
		this.eventEndDate = eventEndDate;
	}

	/**
	 * @return the eventOwner
	 */
	public User getEventOwner() {
		return eventOwner;
	}

	/**
	 * @param eventOwner the eventOwner to set
	 */
	public void setEventOwner(User eventOwner) {
		this.eventOwner = eventOwner;
	}

	/**
	 * @return the industryCategory
	 */
	public IndustryCategory getIndustryCategory() {
		return industryCategory;
	}

	/**
	 * @param industryCategory the industryCategory to set
	 */
	public void setIndustryCategory(IndustryCategory industryCategory) {
		this.industryCategory = industryCategory;
	}

	/**
	 * @return the type
	 */
	public RfxTypes getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(RfxTypes type) {
		this.type = type;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	/**
	 * @return
	 */
	public BusinessUnit getBusinessUnit() {
		return businessUnit;
	}

	/**
	 * @param businessUnit
	 */
	public void setBusinessUnit(BusinessUnit businessUnit) {
		this.businessUnit = businessUnit;
	}

	/**
	 * @return
	 */
	public Date getEventStart() {
		return eventStart;
	}

	/**
	 * @param eventStart
	 */
	public void setEventStart(Date eventStart) {
		this.eventStart = eventStart;
	}

	/**
	 * @return
	 */
	public String getReferanceNumber() {
		return referanceNumber;
	}

	/**
	 * @param referanceNumber
	 */
	public void setReferanceNumber(String referanceNumber) {
		this.referanceNumber = referanceNumber;
	}

	/**
	 * @return
	 */
	public String getUnitName() {
		return unitName;
	}

	/**
	 * @param unitName
	 */
	public void setUnitName(String unitName) {
		this.unitName = unitName;
	}

	/**
	 * @return
	 */
	public String getCode() {
		return code;
	}

	/**
	 * @param code
	 */
	public void setCode(String code) {
		this.code = code;
	}

	/**
	 * @return
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the siteVisitDate
	 */
	public Date getSiteVisitDate() {
		return siteVisitDate;
	}

	/**
	 * @param siteVisitDate the siteVisitDate to set
	 */
	public void setSiteVisitDate(Date siteVisitDate) {
		this.siteVisitDate = siteVisitDate;
	}

	/**
	 * @return the contactName
	 */
	public String getContactName() {
		return contactName;
	}

	/**
	 * @param contactName the contactName to set
	 */
	public void setContactName(String contactName) {
		this.contactName = contactName;
	}

	/**
	 * @return the contactNumber
	 */
	public String getContactNumber() {
		return contactNumber;
	}

	/**
	 * @param contactNumber the contactNumber to set
	 */
	public void setContactNumber(String contactNumber) {
		this.contactNumber = contactNumber;
	}

	/**
	 * @return the industryCategories
	 */
	public List<IndustryCategory> getIndustryCategories() {
		return industryCategories;
	}

	/**
	 * @param industryCategories the industryCategories to set
	 */
	public void setIndustryCategories(List<IndustryCategory> industryCategories) {
		this.industryCategories = industryCategories;
	}

	/**
	 * @return the industryCategoriesNames
	 */
	public String getIndustryCategoriesNames() {
		return industryCategoriesNames;
	}

	/**
	 * @param industryCategoriesNames the industryCategoriesNames to set
	 */
	public void setIndustryCategoriesNames(String industryCategoriesNames) {
		this.industryCategoriesNames = industryCategoriesNames;
	}

	/**
	 * @return the meetingType
	 */
	public MeetingType getMeetingType() {
		return meetingType;
	}

	/**
	 * @param meetingType the meetingType to set
	 */
	public void setMeetingType(MeetingType meetingType) {
		this.meetingType = meetingType;
	}

	/**
	 * @return the siteVisitMeetingDetails
	 */
	public String getSiteVisitMeetingDetails() {
		return siteVisitMeetingDetails;
	}

	/**
	 * @param siteVisitMeetingDetails the siteVisitMeetingDetails to set
	 */
	public void setSiteVisitMeetingDetails(String siteVisitMeetingDetails) {
		this.siteVisitMeetingDetails = siteVisitMeetingDetails;
	}

	/**
	 * @return the buyerId
	 */
	public String getBuyerId() {
		return buyerId;
	}

	/**
	 * @param buyerId the buyerId to set
	 */
	public void setBuyerId(String buyerId) {
		this.buyerId = buyerId;
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
		PublicEventPojo other = (PublicEventPojo) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	public BigDecimal getParticipationFees() {
		return participationFees;
	}

	public void setParticipationFees(BigDecimal participationFees) {
		this.participationFees = participationFees;
	}

	public String getCurrencyCode() {
		return currencyCode;
	}

	public void setCurrencyCode(String currencyCode) {
		this.currencyCode = currencyCode;
	}
}