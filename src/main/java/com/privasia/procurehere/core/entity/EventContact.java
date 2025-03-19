/**
 * 
 */
package com.privasia.procurehere.core.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;

import com.privasia.procurehere.core.entity.Pr.PrSupplierList;
import com.privasia.procurehere.core.entity.Pr.PrSupplierManual;

/**
 * @author Ravi
 */
@MappedSuperclass
public class EventContact implements Serializable {

	private static final long serialVersionUID = 5892307055222176554L;

	@Id
	@GenericGenerator(name = "idGen", strategy = "uuid.hex")
	@GeneratedValue(generator = "idGen")
	@Column(name = "ID", length = 64)
	private String id;

	@Column(name = "TITLE", length = 128)
	private String title;

	@NotNull(message = "{common.contact.name.required}", groups = { PrSupplierManual.class, PrSupplierList.class })
	@Size(message = "{common.contact.name.length}", min = 1, max = 128, groups = { PrSupplierManual.class, PrSupplierList.class })
	@Column(name = "CONTACT_NAME", length = 128)
	private String contactName;

	@Column(name = "COMUNICATION_EMAIL", length = 128)
	private String comunicationEmail;

	@Column(name = "DESIGNATION", length = 128)
	private String designation;

	@Column(name = "CONTACT_NUMBER", length = 128)
	private String contactNumber;

	@Column(name = "MOBILE_NUMBER", length = 128)
	private String mobileNumber;

	@Column(name = "FAX_NUMBER", length = 128)
	private String faxNumber;

	public PrContact copyForPr() {
		PrContact contact = new PrContact();
		contact.setComunicationEmail(getComunicationEmail());
		contact.setContactName(getContactName());
		contact.setContactNumber(getContactNumber());
		contact.setDesignation(getDesignation());
		contact.setFaxNumber(getFaxNumber());
		contact.setMobileNumber(getMobileNumber());
		contact.setTitle(getTitle());
		return contact;
	}

	public RftEventContact copyForRft() {
		RftEventContact contact = new RftEventContact();
		contact.setComunicationEmail(getComunicationEmail());
		contact.setContactName(getContactName());
		contact.setContactNumber(getContactNumber());
		contact.setDesignation(getDesignation());
		contact.setFaxNumber(getFaxNumber());
		contact.setMobileNumber(getMobileNumber());
		contact.setTitle(getTitle());
		return contact;
	}

	public RfaEventContact copyForRfa() {
		RfaEventContact contact = new RfaEventContact();
		contact.setComunicationEmail(getComunicationEmail());
		contact.setContactName(getContactName());
		contact.setContactNumber(getContactNumber());
		contact.setDesignation(getDesignation());
		contact.setFaxNumber(getFaxNumber());
		contact.setMobileNumber(getMobileNumber());
		contact.setTitle(getTitle());
		return contact;
	}

	public RfiEventContact copyForRfi() {
		RfiEventContact contact = new RfiEventContact();
		contact.setComunicationEmail(getComunicationEmail());
		contact.setContactName(getContactName());
		contact.setContactNumber(getContactNumber());
		contact.setDesignation(getDesignation());
		contact.setFaxNumber(getFaxNumber());
		contact.setMobileNumber(getMobileNumber());
		contact.setTitle(getTitle());
		return contact;
	}

	public RfpEventContact copyForRfp() {
		RfpEventContact contact = new RfpEventContact();
		contact.setComunicationEmail(getComunicationEmail());
		contact.setContactName(getContactName());
		contact.setContactNumber(getContactNumber());
		contact.setDesignation(getDesignation());
		contact.setFaxNumber(getFaxNumber());
		contact.setMobileNumber(getMobileNumber());
		contact.setTitle(getTitle());
		return contact;
	}

	public RfqEventContact copyForRfq() {
		RfqEventContact contact = new RfqEventContact();
		contact.setComunicationEmail(getComunicationEmail());
		contact.setContactName(getContactName());
		contact.setContactNumber(getContactNumber());
		contact.setDesignation(getDesignation());
		contact.setFaxNumber(getFaxNumber());
		contact.setMobileNumber(getMobileNumber());
		contact.setTitle(getTitle());
		return contact;
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
	 * @return the comunicationEmail
	 */
	public String getComunicationEmail() {
		return comunicationEmail;
	}

	/**
	 * @param comunicationEmail the comunicationEmail to set
	 */
	public void setComunicationEmail(String comunicationEmail) {
		this.comunicationEmail = comunicationEmail;
	}

	/**
	 * @return the designation
	 */
	public String getDesignation() {
		return designation;
	}

	/**
	 * @param designation the designation to set
	 */
	public void setDesignation(String designation) {
		this.designation = designation;
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

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((comunicationEmail == null) ? 0 : comunicationEmail.hashCode());
		result = prime * result + ((contactName == null) ? 0 : contactName.hashCode());
		result = prime * result + ((contactNumber == null) ? 0 : contactNumber.hashCode());
		result = prime * result + ((designation == null) ? 0 : designation.hashCode());
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
		EventContact other = (EventContact) obj;
		if (comunicationEmail == null) {
			if (other.comunicationEmail != null)
				return false;
		} else if (!comunicationEmail.equals(other.comunicationEmail))
			return false;
		if (contactName == null) {
			if (other.contactName != null)
				return false;
		} else if (!contactName.equals(other.contactName))
			return false;
		if (contactNumber == null) {
			if (other.contactNumber != null)
				return false;
		} else if (!contactNumber.equals(other.contactNumber))
			return false;
		if (designation == null) {
			if (other.designation != null)
				return false;
		} else if (!designation.equals(other.designation))
			return false;

		return true;
	}

	public String toLogString() {
		return "EventContact [  contactName=" + contactName + ", comunicationEmail=" + comunicationEmail + ", designation=" + designation + ", contactNumber=" + contactNumber + "]";
	}

}
