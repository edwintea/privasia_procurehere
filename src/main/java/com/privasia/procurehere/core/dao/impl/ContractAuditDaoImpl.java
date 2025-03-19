package com.privasia.procurehere.core.dao.impl;

import java.util.List;

import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import com.privasia.procurehere.core.dao.ContractAuditDao;
import com.privasia.procurehere.core.entity.ContractAudit;

/**
 * @author Aishwarya
 */
@Repository
public class ContractAuditDaoImpl extends GenericDaoImpl<ContractAudit, String> implements ContractAuditDao {

	@SuppressWarnings("unchecked")
	@Override
	public List<ContractAudit> getContractAuditByContractId(String contractId) {
		Query query = getEntityManager().createQuery("select distinct new com.privasia.procurehere.core.entity.ContractAudit(pa.id, u.name, pa.actionDate, pa.action, pa.description, pa.hasSnapshot) from ContractAudit pa left outer join pa.actionBy u where pa.productContract.id = :id order by pa.actionDate");
		query.setParameter("id", contractId);
		List<ContractAudit> list = query.getResultList();
		return list;
	}

	public void deleteContractAuditByProductContractId(String productContractId) {
		StringBuilder hsql = new StringBuilder("DELETE FROM ContractAudit ct WHERE ct.productContract.id = :productContractId");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("productContractId", productContractId);
		query.executeUpdate();
	}
	
}
