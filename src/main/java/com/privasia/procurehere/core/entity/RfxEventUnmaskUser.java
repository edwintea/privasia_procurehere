package com.privasia.procurehere.core.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

/**
 * 
 * @author pavan
 *
 */

@MappedSuperclass
public class RfxEventUnmaskUser implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8591926075376479007L;

	@Id
	@GenericGenerator(name = "idGen", strategy = "uuid.hex")
	@GeneratedValue(generator = "idGen")
	@Column(name = "ID", length = 64)
	private String id;

	@ManyToOne(fetch = FetchType.EAGER, cascade = { CascadeType.REFRESH }, optional = false)
	@JoinColumn(name = "USER_ID", nullable = false)
	private User user;

	@Column(name = "USER_UNMASKED", nullable = true)
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean userUnmasked = Boolean.FALSE;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "USER_UNMASKED_TIME", nullable = true)
	private Date userUnmaskedTime;

	
	
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
	 * @return the userUnmasked
	 */
	public Boolean getUserUnmasked() {
		return userUnmasked;
	}

	/**
	 * @param userUnmasked the userUnmasked to set
	 */
	public void setUserUnmasked(Boolean userUnmasked) {
		this.userUnmasked = userUnmasked;
	}

	/**
	 * @return the userUnmaskedTime
	 */
	public Date getUserUnmaskedTime() {
		return userUnmaskedTime;
	}

	/**
	 * @param userUnmaskedTime the userUnmaskedTime to set
	 */
	public void setUserUnmaskedTime(Date userUnmaskedTime) {
		this.userUnmaskedTime = userUnmaskedTime;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((user == null) ? 0 : user.hashCode());
		return result;
	}

	/* (non-Javadoc)
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
		RfxEventUnmaskUser other = (RfxEventUnmaskUser) obj;
		if (user == null) {
			if (other.user != null)
				return false;
		} else if (!user.equals(other.user))
			return false;
		return true;
	}



	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	
	public String toLogString() {
		return "RfxEventUnmaskUser [id=" + id + ", user=" + user + ", userUnmasked=" + userUnmasked + ", userUnmaskedTime=" + userUnmaskedTime + "]";
	}


	
	
	
}
