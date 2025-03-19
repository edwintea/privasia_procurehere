package com.privasia.procurehere.core.pojo;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.privasia.procurehere.core.entity.Buyer;
import com.privasia.procurehere.core.entity.Country;
import com.privasia.procurehere.core.entity.IndustryCategory;
import com.privasia.procurehere.core.entity.Supplier;
import com.privasia.procurehere.core.enums.FavouriteSupplierStatus;
import com.privasia.procurehere.core.enums.RequestAssociateBuyerStatus;
import com.privasia.procurehere.core.utils.CustomDateTimeSerializer;

/**
 * @author pooja
 */

public class RequestedAssociatedBuyerPojo implements Serializable {

	private static final long serialVersionUID = -4081304002746630127L;

	private String id;

	private Buyer buyer;

	private String buyerName;

	private String countryName;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	private Date associatedDate;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	private Date requestedDate;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	private Date rejectedDate;

	private RequestAssociateBuyerStatus status;

	private Supplier supplier;

	private String supplierRemark;

	private String buyerId;

	private String infoToSupplier;

	private String website;

	private String communicationEmail;

	private String contactPerson;

	private String contactNumber;

	private String buyerRemark;

	private String buyerCompanyName;

	private Country buyerCountry;

	private Integer minimumCategories;

	private Integer maximumCategories;

	private Boolean isAllowCategories;

	private List<IndustryCategory> categories;

	private List<String> industryCategories;

	private String indCat[];

	private FavouriteSupplierStatus favStatus;

	private String searchCountryName;

	private String searchCompanyName;

	private Boolean publishedProfile;

	private boolean favSupp;

	private String supplierFormSubId;

	public String[] getIndCat() {
		return indCat;
	}

	public void setIndCat(String[] indCat) {
		this.indCat = indCat;
	}

	public RequestedAssociatedBuyerPojo() {

	}

	/**
	 * For List of Associated Buyer for supplier
	 */
	public RequestedAssociatedBuyerPojo(String favId, String buyerId, String buyerCompanyName, String countryName, FavouriteSupplierStatus status, Date requestedDate, Date associatedDate) {
		super();
		this.id = favId;
		this.buyerId = buyerId;
		this.buyerCompanyName = buyerCompanyName;
		this.countryName = countryName;
		this.favStatus = status;
		this.requestedDate = requestedDate;
		this.associatedDate = associatedDate;
	}

	/**
	 * For View Request
	 */
	public RequestedAssociatedBuyerPojo(String id, String buyerId, String companyName, String communicationEmail, String contactNumber, String contactPerson, String website, String infoToSupplier, Integer minimumCategories, Integer maximumCategories, RequestAssociateBuyerStatus status, Date requestedDate, Date rejectedDate, Date associatedDate, String buyerRemark, String supplierRemark) {
		super();
		this.id = id;
		this.buyerId = buyerId;
		this.buyerCompanyName = companyName;
		this.communicationEmail = communicationEmail;
		this.contactNumber = contactNumber;
		this.contactPerson = contactPerson;
		this.website = website;
		this.infoToSupplier = infoToSupplier;
		this.minimumCategories = minimumCategories;
		this.maximumCategories = maximumCategories;
		this.status = status;
		this.requestedDate = requestedDate;
		this.rejectedDate = rejectedDate;
		this.associatedDate = associatedDate;
		this.buyerRemark = buyerRemark;
		this.supplierRemark = supplierRemark;
	}
	
	/**
	 * get Buyer
	 */
	public RequestedAssociatedBuyerPojo(String buyerId, String companyName, String communicationEmail, String contactNumber, String contactPerson, String website, String infoToSupplier, Integer minimumCategories, Integer maximumCategories, Boolean isAllowCategories) {
		super();
		this.buyerId = buyerId;
		this.buyerCompanyName = companyName;
		this.communicationEmail = communicationEmail;
		this.contactNumber = contactNumber;
		this.contactPerson = contactPerson;
		this.website = website;
		this.infoToSupplier = infoToSupplier;
		this.minimumCategories = minimumCategories;
		this.maximumCategories = maximumCategories;
		this.isAllowCategories = isAllowCategories;
	}

	/**
	 * For Available
	 */

	public RequestedAssociatedBuyerPojo(String buyerId, String buyerCompanyName, String countryName) {
		super();
		this.buyerId = buyerId;
		this.buyerCompanyName = buyerCompanyName;
		this.countryName = countryName;
	}

	/**
	 * For Global Search
	 */
	public RequestedAssociatedBuyerPojo(String buyerId, String buyerCompanyName, String countryName, Boolean publishedProfile) {
		super();
		this.buyerId = buyerId;
		this.buyerCompanyName = buyerCompanyName;
		this.countryName = countryName;
		this.publishedProfile = publishedProfile;
	}

	public RequestedAssociatedBuyerPojo(String buyerId, String buyerCompanyName, String countryName, Date createdDate) {
		super();
		this.buyerId = buyerId;
		this.buyerCompanyName = buyerCompanyName;
		this.countryName = countryName;
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
	 * @return the buyerName
	 */
	public String getBuyerName() {
		return buyerName;
	}

	/**
	 * @param buyerName the buyerName to set
	 */
	public void setBuyerName(String buyerName) {
		this.buyerName = buyerName;
	}

	/**
	 * @return the countryName
	 */
	public String getCountryName() {
		return countryName;
	}

	/**
	 * @param countryName the countryName to set
	 */
	public void setCountryName(String countryName) {
		this.countryName = countryName;
	}

	/**
	 * @return the associatedDate
	 */
	public Date getAssociatedDate() {
		return associatedDate;
	}

	/**
	 * @param associatedDate the associatedDate to set
	 */
	public void setAssociatedDate(Date associatedDate) {
		this.associatedDate = associatedDate;
	}

	/**
	 * @return the requestedDate
	 */
	public Date getRequestedDate() {
		return requestedDate;
	}

	/**
	 * @param requestedDate the requestedDate to set
	 */
	public void setRequestedDate(Date requestedDate) {
		this.requestedDate = requestedDate;
	}

	/**
	 * @return the rejectedDate
	 */
	public Date getRejectedDate() {
		return rejectedDate;
	}

	/**
	 * @param rejectedDate the rejectedDate to set
	 */
	public void setRejectedDate(Date rejectedDate) {
		this.rejectedDate = rejectedDate;
	}

	/**
	 * @return the status
	 */
	public RequestAssociateBuyerStatus getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(RequestAssociateBuyerStatus status) {
		this.status = status;
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
	 * @return the supplierRemark
	 */
	public String getSupplierRemark() {
		return supplierRemark;
	}

	/**
	 * @param supplierRemark the supplierRemark to set
	 */
	public void setSupplierRemark(String supplierRemark) {
		this.supplierRemark = supplierRemark;
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

	/**
	 * @return the infoToSupplier
	 */
	public String getInfoToSupplier() {
		return infoToSupplier;
	}

	/**
	 * @param infoToSupplier the infoToSupplier to set
	 */
	public void setInfoToSupplier(String infoToSupplier) {
		this.infoToSupplier = infoToSupplier;
	}

	/**
	 * @return the website
	 */
	public String getWebsite() {
		return website;
	}

	/**
	 * @param website the website to set
	 */
	public void setWebsite(String website) {
		this.website = website;
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
	 * @return the contactPerson
	 */
	public String getContactPerson() {
		return contactPerson;
	}

	/**
	 * @param contactPerson the contactPerson to set
	 */
	public void setContactPerson(String contactPerson) {
		this.contactPerson = contactPerson;
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
	 * @return the buyerRemark
	 */
	public String getBuyerRemark() {
		return buyerRemark;
	}

	/**
	 * @param buyerRemark the buyerRemark to set
	 */
	public void setBuyerRemark(String buyerRemark) {
		this.buyerRemark = buyerRemark;
	}

	/**
	 * @return the buyerCompanyName
	 */
	public String getBuyerCompanyName() {
		return buyerCompanyName;
	}

	/**
	 * @param buyerCompanyName the buyerCompanyName to set
	 */
	public void setBuyerCompanyName(String buyerCompanyName) {
		this.buyerCompanyName = buyerCompanyName;
	}

	/**
	 * @return the buyerCountry
	 */
	public Country getBuyerCountry() {
		return buyerCountry;
	}

	/**
	 * @param buyerCountry the buyerCountry to set
	 */
	public void setBuyerCountry(Country buyerCountry) {
		this.buyerCountry = buyerCountry;
	}

	/**
	 * @return the minimumCategories
	 */
	public Integer getMinimumCategories() {
		return minimumCategories;
	}

	/**
	 * @param minimumCategories the minimumCategories to set
	 */
	public void setMinimumCategories(Integer minimumCategories) {
		this.minimumCategories = minimumCategories;
	}

	/**
	 * @return the maximumCategories
	 */
	public Integer getMaximumCategories() {
		return maximumCategories;
	}

	/**
	 * @param maximumCategories the maximumCategories to set
	 */
	public void setMaximumCategories(Integer maximumCategories) {
		this.maximumCategories = maximumCategories;
	}

	/**
	 * @return the isAllowCategories
	 */
	public Boolean getIsAllowCategories() {
		return isAllowCategories;
	}

	/**
	 * @param isAllowCategories the isAllowCategories to set
	 */
	public void setIsAllowCategories(Boolean isAllowCategories) {
		this.isAllowCategories = isAllowCategories;
	}

	/**
	 * @return the categories
	 */
	public List<IndustryCategory> getCategories() {
		return categories;
	}

	/**
	 * @param categories the categories to set
	 */
	public void setCategories(List<IndustryCategory> categories) {
		this.categories = categories;
	}

	/**
	 * @return the industryCategories
	 */
	public List<String> getIndustryCategories() {
		return industryCategories;
	}

	/**
	 * @param industryCategories the industryCategories to set
	 */
	public void setIndustryCategories(List<String> industryCategories) {
		this.industryCategories = industryCategories;
	}

	/**
	 * @return the favStatus
	 */
	public FavouriteSupplierStatus getFavStatus() {
		return favStatus;
	}

	/**
	 * @param favStatus the favStatus to set
	 */
	public void setFavStatus(FavouriteSupplierStatus favStatus) {
		this.favStatus = favStatus;
	}

	/**
	 * @return the searchCountryName
	 */
	public String getSearchCountryName() {
		return searchCountryName;
	}

	/**
	 * @param searchCountryName the searchCountryName to set
	 */
	public void setSearchCountryName(String searchCountryName) {
		this.searchCountryName = searchCountryName;
	}

	/**
	 * @return the searchCompanyName
	 */
	public String getSearchCompanyName() {
		return searchCompanyName;
	}

	/**
	 * @param searchCompanyName the searchCompanyName to set
	 */
	public void setSearchCompanyName(String searchCompanyName) {
		this.searchCompanyName = searchCompanyName;
	}

	/**
	 * @return the publishedProfile
	 */
	public Boolean getPublishedProfile() {
		return publishedProfile;
	}

	/**
	 * @param publishedProfile the publishedProfile to set
	 */
	public void setPublishedProfile(Boolean publishedProfile) {
		this.publishedProfile = publishedProfile;
	}

	/**
	 * @return the favSupp
	 */
	public boolean isFavSupp() {
		return favSupp;
	}

	/**
	 * @param favSupp the favSupp to set
	 */
	public void setFavSupp(boolean favSupp) {
		this.favSupp = favSupp;
	}

	/**
	 * @return the supplierFormSubId
	 */
	public String getSupplierFormSubId() {
		return supplierFormSubId;
	}

	/**
	 * @param supplierFormSubId the supplierFormSubId to set
	 */
	public void setSupplierFormSubId(String supplierFormSubId) {
		this.supplierFormSubId = supplierFormSubId;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((buyerId == null) ? 0 : buyerId.hashCode());
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
		RequestedAssociatedBuyerPojo other = (RequestedAssociatedBuyerPojo) obj;
		if (buyerId == null) {
			if (other.buyerId != null)
				return false;
		} else if (!id.equals(other.buyerId))
			return false;
		return true;
	}

}
