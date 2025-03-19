
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
import org.hibernate.annotations.Type;

/**
 * @author pavan
 */
@Entity
@Table(name = "PROC_TEMPLATE_UNMASK_USER")
public class TemplateUnmaskUser implements Serializable {

	private static final long serialVersionUID = 2957683381332540253L;

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

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, optional = true)
	@JoinColumn(name = "RFX_TEMPLATE_ID", foreignKey = @ForeignKey(name = "FK_TMPL_UNMASK_USER_ID"))
	private RfxTemplate rfxTemplate;

	public TemplateUnmaskUser(User user) {
		if (user != null) {
			this.setUser(user);
			this.setId(user.getId());
		}
	}

	public TemplateUnmaskUser() {

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
	 * @return the rfxTemplate
	 */
	public RfxTemplate getRfxTemplate() {
		return rfxTemplate;
	}

	/**
	 * @param rfxTemplate the rfxTemplate to set
	 */
	public void setRfxTemplate(RfxTemplate rfxTemplate) {
		this.rfxTemplate = rfxTemplate;
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
		TemplateUnmaskUser other = (TemplateUnmaskUser) obj;
		if (user == null) {
			if (other.user != null)
				return false;
		} else if (!user.equals(other.user))
			return false;
		return true;
	}

	
}