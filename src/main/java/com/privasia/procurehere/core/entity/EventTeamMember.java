package com.privasia.procurehere.core.entity;

/**
 * @author RT-Kapil
 */

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;

import org.hibernate.annotations.GenericGenerator;

import com.privasia.procurehere.core.enums.TeamMemberType;

@MappedSuperclass
public class EventTeamMember implements Serializable {

	private static final long serialVersionUID = -6181503567357228956L;

	@Id
	@GenericGenerator(name = "idGen", strategy = "uuid.hex")
	@GeneratedValue(generator = "idGen")
	@Column(name = "ID", length = 64)
	private String id;

	@ManyToOne(fetch = FetchType.EAGER, cascade = { CascadeType.REFRESH }, optional = false)
	@JoinColumn(name = "USER_ID", nullable = false)
	private User user;

	@Enumerated(EnumType.STRING)
	@Column(name = "MEMBER_TYPE", nullable = false)
	private TeamMemberType teamMemberType;

	public EventTeamMember() {
	}
	
	public EventTeamMember(String id, TeamMemberType teamMemberType, String userId, String loginId, String name, String communicationEmail, boolean emailNotifications, String tenantId, boolean deleted) {
		this.id = id;
		this.user = new User(userId, loginId, name, communicationEmail, emailNotifications, tenantId, deleted);
		this.teamMemberType = teamMemberType;
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
	 * @return the teamMemberType
	 */
	public TeamMemberType getTeamMemberType() {
		return teamMemberType;
	}

	/**
	 * @param teamMemberType the teamMemberType to set
	 */
	public void setTeamMemberType(TeamMemberType teamMemberType) {
		this.teamMemberType = teamMemberType;
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
		result = prime * result + ((teamMemberType == null) ? 0 : teamMemberType.hashCode());
	//	result = prime * result + ((user == null) ? 0 : user.hashCode());
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
		EventTeamMember other = (EventTeamMember) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (teamMemberType != other.teamMemberType)
			return false;
		/*if (user == null) {
			if (other.user != null)
				return false;
		} else if (!user.equals(other.user))
			return false;*/
		return true;
	}

	public String toLogString() {
		return "EventTeamMember [id=" + id + ", teamMemberType=" + teamMemberType + "]";
	}

}
