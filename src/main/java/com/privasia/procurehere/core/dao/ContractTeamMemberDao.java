package com.privasia.procurehere.core.dao;

import java.util.List;

import com.privasia.procurehere.core.entity.ContractTeamMember;

public interface ContractTeamMemberDao extends GenericDao<ContractTeamMember, String> {

	List<ContractTeamMember> findContractTeamMemberById(String contractId);

}
