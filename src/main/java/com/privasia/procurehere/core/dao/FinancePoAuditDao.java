package com.privasia.procurehere.core.dao;

import java.util.List;

import com.privasia.procurehere.core.entity.FinancePoAudit;

public interface FinancePoAuditDao extends GenericDao<FinancePoAudit, String> {

	List<FinancePoAudit> getAuditForFinancePo(String prId);

	
}
