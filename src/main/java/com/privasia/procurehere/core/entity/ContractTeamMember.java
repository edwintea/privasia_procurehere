package com.privasia.procurehere.core.entity;

import java.io.Serializable;

import javax.persistence.CascadeType;
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
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.privasia.procurehere.core.enums.TeamMemberType;

/**
 * @author Aishwarya
 */
@Entity
@Table(name = "PROC_CONTRACT_TEAM_MEMBER")
public class ContractTeamMember implements Serializable {

	private static final long serialVersionUID = -6181503567357228956L;

	@Id
	@GenericGenerator(name = "idGen", strategy = "uuid.hex")
	@GeneratedValue(generator = "idGen")
	@Column(name = "ID", length = 64)
	private String id;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, cascade = { CascadeType.REFRESH }, optional = false)
	@JoinColumn(name = "CONTRACT_ID", nullable = false, foreignKey = @ForeignKey(name = "FK_CONT_TEAM_PROD_CONT_ID"))
	private ProductContract productContract;

	@ManyToOne(fetch = FetchType.LAZY, cascade = { CascadeType.REFRESH }, optional = false)
	@JoinColumn(name = "USER_ID", nullable = false, foreignKey = @ForeignKey(name = "FK_CONT_TEAM_USR_ID"))
	private User user;

	@Enumerated(EnumType.STRING)
	@Column(name = "MEMBER_TYPE", nullable = false)
	private TeamMemberType teamMemberType;

	@Transient
	private String accessType;

	public ContractTeamMember() {
	}

	public ContractTeamMember(String id, TeamMemberType teamMemberType, String userId, String loginId, String name, String communicationEmail, boolean emailNotifications, String tenantId, boolean deleted) {
		this.id = id;
		this.user = new User(userId, loginId, name, communicationEmail, emailNotifications, tenantId, deleted);
		this.teamMemberType = teamMemberType;
	}

	public ContractTeamMember(TeamMemberType teamMemberType, String userId, String loginId, String name) {
		this.user = new User(userId, loginId, name);
		this.accessType = teamMemberType.getValue();
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

	/**
	 * @return the productContract
	 */
	public ProductContract getProductContract() {
		return productContract;
	}

	/**
	 * @param productContract the productContract to set
	 */
	public void setProductContract(ProductContract productContract) {
		this.productContract = productContract;
	}

	public String getAccessType() {
		return accessType;
	}

	public void setAccessType(String accessType) {
		this.accessType = accessType;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	public String toLogString() {
		return "EventTeamMember [id=" + id + ", teamMemberType=" + teamMemberType + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((teamMemberType == null) ? 0 : teamMemberType.hashCode());
		result = prime * result + ((user == null) ? 0 : user.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ContractTeamMember other = (ContractTeamMember) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (user == null) {
			if (other.user != null)
				return false;
		} else if (!user.equals(other.user))
			return false;
		return true;
	}

}
