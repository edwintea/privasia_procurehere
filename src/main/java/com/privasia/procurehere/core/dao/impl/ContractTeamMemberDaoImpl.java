package com.privasia.procurehere.core.dao.impl;

import java.util.List;

import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import com.privasia.procurehere.core.dao.ContractTeamMemberDao;
import com.privasia.procurehere.core.entity.ContractTeamMember;

@Repository
public class ContractTeamMemberDaoImpl extends GenericDaoImpl<ContractTeamMember, String> implements ContractTeamMemberDao {

	@SuppressWarnings("unchecked")
	@Override
	public List<ContractTeamMember> findContractTeamMemberById(String contractId) {
		final Query query = getEntityManager().createQuery("select new com.privasia.procurehere.core.entity.ContractTeamMember(a.teamMemberType, u.id,u.loginId, u.name) from ContractTeamMember a inner join a.productContract p inner join a.user u where p.id =:id ");
		query.setParameter("id", contractId);
		return query.getResultList();
	}

}
