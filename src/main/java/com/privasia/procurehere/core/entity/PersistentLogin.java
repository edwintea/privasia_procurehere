/**
 * 
 */
package com.privasia.procurehere.core.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * @author Nitin Otageri
 *
 */
@Entity
@Table(name = "PROC_PERSISTENT_LOGINS")
public class PersistentLogin implements Serializable {

	private static final long serialVersionUID = -5421308628215318939L;

	@Id
	@Column(name = "ID")
	private String series;

	@Column(name = "USERNAME", unique = true, nullable = false)
	private String username;

	@Column(name = "TOKEN", unique = true, nullable = false)
	private String token;

	@Temporal(TemporalType.TIMESTAMP)
	private Date lastUsed;

	public String getSeries() {
		return series;
	}

	public void setSeries(String series) {
		this.series = series;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	/**
	 * @return the lastUsed
	 */
	public Date getLastUsed() {
		return lastUsed;
	}

	/**
	 * @param lastUsed the lastUsed to set
	 */
	public void setLastUsed(Date lastUsed) {
		this.lastUsed = lastUsed;
	}
	
}
