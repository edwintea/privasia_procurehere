package com.privasia.procurehere.core.dao;

import java.util.List;

import com.privasia.procurehere.core.entity.ContractAudit;

public interface ContractAuditDao extends GenericDao<ContractAudit, String>{
	
	List<ContractAudit> getContractAuditByContractId(String prId);

	void deleteContractAuditByProductContractId(String productContractId);

}
