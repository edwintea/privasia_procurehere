package com.privasia.procurehere.core.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.privasia.procurehere.core.enums.InboundOutbound;

@Entity
@Table(name = "PROC_COMPOSE_MESSAGE")
public class ComposeMessage implements Serializable {

	
	private static final long serialVersionUID = -8283147047011902455L;
	
	@Id
	@GenericGenerator(name = "idGen", strategy = "uuid.hex")
	@GeneratedValue(generator = "idGen")
	@Column(name="ID",length=64)
	private String id;
	
	@NotNull(message = "{composemessage.user.not.empty}")
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "COMPOSE_MESSAGE_USER",nullable=false , foreignKey = @ForeignKey(name = "FK_COM_MESSAGE_USER") )
	private User user;
	
	@NotNull(message = "{composemessage.subject.not.empty}")
	@Size(min = 1, max = 256, message = "{composemessage.subject.length}")
	@Column(name = "SUBJECT", length = 256 ,nullable=false)
	private String subject;

	@NotNull(message = "{composemessage.message.not.empty}")
	@Size(min = 0, max = 2000, message = "{composemessage.message.length}")
	@Column(name = "MESSAGE", length = 2000)
	private String message;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "INBOUND_OUTBOUND",nullable=false)
	private InboundOutbound inboundOuntbound;
	
	@NotNull(message = "{composemessage.fromuser.not.empty}")
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "FROM_USER",nullable=false , foreignKey = @ForeignKey(name = "FK_COM_MESSAGE_FROMUSER") )
	private User fromUser;
	
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm a")
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CREATED_DATE", nullable = false)
	private Date createdDate;

	@Transient
	private String userList;
	
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
	 * @return the subject
	 */
	public String getSubject() {
		return subject;
	}

	/**
	 * @param subject the subject to set
	 */
	public void setSubject(String subject) {
		this.subject = subject;
	}

	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * @param message the message to set
	 */
	public void setMessage(String message) {
		this.message = message;
	}

	/**
	 * @return the inboundOuntbound
	 */
	public InboundOutbound getInboundOuntbound() {
		return inboundOuntbound;
	}

	/**
	 * @param inboundOuntbound the inboundOuntbound to set
	 */
	public void setInboundOuntbound(InboundOutbound inboundOuntbound) {
		this.inboundOuntbound = inboundOuntbound;
	}

	/**
	 * @return the fromUser
	 */
	public User getFromUser() {
		return fromUser;
	}

	/**
	 * @param fromUser the fromUser to set
	 */
	public void setFromUser(User fromUser) {
		this.fromUser = fromUser;
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
	 * @return the userList
	 */
	public String getUserList() {
		return userList;
	}

	/**
	 * @param userList the userList to set
	 */
	public void setUserList(String userList) {
		this.userList = userList;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((inboundOuntbound == null) ? 0 : inboundOuntbound.hashCode());
		result = prime * result + ((message == null) ? 0 : message.hashCode());
		result = prime * result + ((subject == null) ? 0 : subject.hashCode());
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
		ComposeMessage other = (ComposeMessage) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (inboundOuntbound != other.inboundOuntbound)
			return false;
		if (message == null) {
			if (other.message != null)
				return false;
		} else if (!message.equals(other.message))
			return false;
		if (subject == null) {
			if (other.subject != null)
				return false;
		} else if (!subject.equals(other.subject))
			return false;
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
	@Override
	public String toString() {
		return "ComposeMessage [userLoginId=" + user + ", subject=" + subject + ", message=" + message + ", inboundOuntbound=" + inboundOuntbound + "]";
	}

}
