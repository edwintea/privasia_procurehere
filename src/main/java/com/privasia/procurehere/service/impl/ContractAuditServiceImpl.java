/**
 * 
 */
package com.privasia.procurehere.service.impl;

import java.util.List;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.privasia.procurehere.core.dao.ContractAuditDao;
import com.privasia.procurehere.core.entity.ContractAudit;
import com.privasia.procurehere.service.ContractAuditService;

/**
 * @author Aishwarya
 */
@Service
@Transactional(readOnly = true)
public class ContractAuditServiceImpl implements ContractAuditService {

	public static final Logger LOG = LogManager.getLogger(ContractAuditServiceImpl.class);
	
	@Autowired
	ContractAuditDao contractAuditDao;

	@Override
	@Transactional(readOnly = false)
	public void save(ContractAudit contractAudit) {
		contractAuditDao.saveOrUpdate(contractAudit);
	}
	
	@Override
	public List<ContractAudit> getContractAuditByContractId(String id) {
		return contractAuditDao.getContractAuditByContractId(id);
	}

	@Override
	public ContractAudit getContractAuditById(String id) {
		return contractAuditDao.findById(id);
	}

}
