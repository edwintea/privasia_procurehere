package com.privasia.procurehere.core.entity;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

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
import org.hibernate.annotations.Type;

/**
 * @author Nitin Otageri
 */
@Entity
@Table(name = "PROC_SECURITY_TOKEN")
public class SecurityToken implements Serializable {

	private static final long serialVersionUID = 8684098701584428155L;

	public enum TokenValidity {
		ONE_HOUR, TWO_HOUR, THREE_HOUR, FOUR_HOUR, FIVE_HOUR, SIX_HOUR, SEVEN_HOUR, EIGHT_HOUR, NINE_HOUR, TEN_HOUR, ELEVEN_HOUR, TWELVE_HOUR, ONE_DAY, TWO_DAY,
		FIVE_DAY, THIRTY_MINUTES
	}

	@Id
	@GenericGenerator(name = "idGen", strategy = "uuid.hex")
	@GeneratedValue(generator = "idGen")
	@Column(name = "TOKEN", length = 64)
	private String token;

	@Column(name = "IS_DELETED", nullable = false)
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean deleted = Boolean.FALSE;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CREATED_DATE", nullable = false)
	private Date createdDate;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "USER_TOKEN", nullable = true, foreignKey = @ForeignKey(name = "FK_SEC_TOKEN_USER"))
	private User user;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "EXPIRY_DATE", nullable = false)
	private Date expiryDate;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "USED_DATE")
	private Date usedDate;

	public SecurityToken() {
		this.deleted = Boolean.FALSE;
	}

	public SecurityToken(TokenValidity tokenValidity) {
		this();
		Calendar exp = Calendar.getInstance();
		switch (tokenValidity) {
		case FIVE_DAY:
			exp.add(Calendar.DATE, 5);
			break;
		case TWO_DAY:
			exp.add(Calendar.DATE, 2);
			break;
		case ONE_DAY:
			exp.add(Calendar.HOUR, 24);
			break;
		case THIRTY_MINUTES:
			exp.add(Calendar.MINUTE, 30);
			break;
		default:
			exp.add(Calendar.HOUR, tokenValidity.ordinal() + 1);
			break;
		}
		this.expiryDate = exp.getTime();
		this.createdDate = new Date();
	}

	public SecurityToken(TokenValidity tokenValidity, User user) {
		this(tokenValidity);
		this.user = user;
		this.createdDate = new Date();
		Calendar exp = Calendar.getInstance();
		switch (tokenValidity) {
		case FIVE_DAY:
			exp.add(Calendar.DATE, 5);
			break;
		case TWO_DAY:
			exp.add(Calendar.DATE, 2);
			break;
		case ONE_DAY:
			exp.add(Calendar.HOUR, 24);
			break;
		case THIRTY_MINUTES:
			exp.add(Calendar.MINUTE, 30);
			break;
		default:
			exp.add(Calendar.HOUR, tokenValidity.ordinal() + 1);
			break;
		}
		this.expiryDate = exp.getTime();
	}

	public SecurityToken(Date expiryDate) {
		this();
		this.expiryDate = expiryDate;
		this.createdDate = new Date();
	}

	public SecurityToken(Date expiryDate, User tokenUser) {
		this(expiryDate);
		this.user = tokenUser;
	}

	/**
	 * @return the token
	 */
	public String getToken() {
		return token;
	}

	/**
	 * @param token the token to set
	 */
	public void setToken(String token) {
		this.token = token;
	}

	/**
	 * @return the deleted
	 */
	public Boolean getDeleted() {
		return deleted;
	}

	/**
	 * @param deleted the deleted to set
	 */
	public void setDeleted(Boolean deleted) {
		this.deleted = deleted;
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

	/**
	 * @return the user
	 */
	public User getUser() {
		return user;
	}

	/**
	 * @param user the user to set
	 */
	public void setUser(User user) {
		this.user = user;
	}

	/**
	 * @return the expiryDate
	 */
	public Date getExpiryDate() {
		return expiryDate;
	}

	/**
	 * @param expiryDate the expiryDate to set
	 */
	public void setExpiryDate(Date expiryDate) {
		this.expiryDate = expiryDate;
	}

	/**
	 * @return the usedDate
	 */
	public Date getUsedDate() {
		return usedDate;
	}

	/**
	 * @param usedDate the usedDate to set
	 */
	public void setUsedDate(Date usedDate) {
		this.usedDate = usedDate;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((token == null) ? 0 : token.hashCode());
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
		SecurityToken other = (SecurityToken) obj;
		if (token == null) {
			if (other.token != null)
				return false;
		} else if (!token.equals(other.token))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "SecurityToken [token=" + token + ", deleted=" + deleted + ", createdDate=" + createdDate + ", user=" + user + ", expiryDate=" + expiryDate + ", usedDate=" + usedDate + "]";
	}

}
