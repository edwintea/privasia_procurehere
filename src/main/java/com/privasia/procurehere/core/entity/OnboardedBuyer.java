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

import org.hibernate.annotations.GenericGenerator;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.privasia.procurehere.core.utils.CustomDateTimeSerializer;
import org.hibernate.annotations.Type;

@Entity
@Table(name = "PROC_BUYER_FIN_DETAILS")
public class OnboardedBuyer implements Serializable {

	private static final long serialVersionUID = -185630390521845849L;

	@Id
	@GenericGenerator(name = "idGen", strategy = "uuid.hex")
	@GeneratedValue(generator = "idGen")
	@Column(name = "ID", length = 64)
	private String id;

	@Column(name = "FULL_NAME", length = 128, nullable = false)
	private String name;

	@Column(name = "DESIGNATION", length = 160, nullable = false)
	private String designation;

	@Column(name = "PHONE_NUMBER", length = 32)
	private String phoneNumber;

	@Column(name = "RATING", length = 16)
	private String rating;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "ONBOARDING_DATE", nullable = false)
	private Date onboardingDate;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "ONBOARDED_BY", nullable = false, foreignKey = @ForeignKey(name = "FK_FB_OB_BY"))
	private User onboardingBy;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, optional = true, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "BUYER_ID", nullable = true, foreignKey = @ForeignKey(name = "FK_PROC_BUYER_ID"))
	private Buyer buyer;

	@Column(name = "ACCOUNT_ACTIVE")
	@Type(type = "org.hibernate.type.NumericBooleanType")
private boolean active = true;

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
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
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
	 * @return the phoneNumber
	 */
	public String getPhoneNumber() {
		return phoneNumber;
	}

	/**
	 * @param phoneNumber the phoneNumber to set
	 */
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	/**
	 * @return the rating
	 */
	public String getRating() {
		return rating;
	}

	/**
	 * @param rating the rating to set
	 */
	public void setRating(String rating) {
		this.rating = rating;
	}

	/**
	 * @return the onboardingDate
	 */
	public Date getOnboardingDate() {
		return onboardingDate;
	}

	/**
	 * @param onboardingDate the onboardingDate to set
	 */
	public void setOnboardingDate(Date onboardingDate) {
		this.onboardingDate = onboardingDate;
	}

	/**
	 * @return the onboardingBy
	 */
	public User getOnboardingBy() {
		return onboardingBy;
	}

	/**
	 * @param onboardingBy the onboardingBy to set
	 */
	public void setOnboardingBy(User onboardingBy) {
		this.onboardingBy = onboardingBy;
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
	 * @return the active
	 */
	public boolean isActive() {
		return active;
	}

	/**
	 * @param active the active to set
	 */
	public void setActive(boolean active) {
		this.active = active;
	}

}
