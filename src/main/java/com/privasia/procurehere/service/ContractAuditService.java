package com.privasia.procurehere.service;

import java.util.List;

import com.privasia.procurehere.core.entity.ContractAudit;

public interface ContractAuditService {

	void save(ContractAudit contractAudit);
	/**
	 * 
	 * @param id
	 * @return
	 */
	List<ContractAudit> getContractAuditByContractId(String id);
	
	ContractAudit getContractAuditById(String id);

}
