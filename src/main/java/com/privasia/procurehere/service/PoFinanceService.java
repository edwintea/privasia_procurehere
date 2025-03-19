package com.privasia.procurehere.service;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.privasia.procurehere.core.entity.FinancePo;
import com.privasia.procurehere.core.entity.FinancePoAudit;
import com.privasia.procurehere.core.entity.User;
import com.privasia.procurehere.core.enums.FinancePoStatus;
import com.privasia.procurehere.core.enums.FinancePoType;
import com.privasia.procurehere.core.pojo.TableDataInput;

public interface PoFinanceService {
	/**
	 * @param prId
	 * @param supId
	 * @param loggedInUserTenantId
	 * @return
	 */
	FinancePo getPoFinanceByPrIdAndSupID(String prId, String supId, String loggedInUserTenantId);

	FinancePo saveFinancePo(FinancePo pofinance);

	List<FinancePoAudit> getAuditForFinancePo(String prId);

	/**
	 * @param id
	 * @return
	 */
	FinancePo findById(String id);

	List<FinancePo> findAllSearchFilterPoForFinance(String loggedInUserTenantId, TableDataInput input, Date startDate, Date endDate, FinancePoStatus approved, String selectedSupplier, FinancePoType financePoType);

	long findTotalSearchFilterPoForFinance(String loggedInUserTenantId, TableDataInput input, Date startDate, Date endDate, FinancePoStatus approved, String selectedSupplier, FinancePoType financePoType);

	long findTotalPoForFinance(String loggedInUserTenantId, FinancePoType financePoType);

	FinancePo updateFinancePo(FinancePo persistFinancePo, User loggedInUser);

	long findTotalCountPoForFinanceByStatus(String tenantId, FinancePoStatus status, FinancePoType financePoType);

	FinancePo getPoFinanceForSupplier(String poId, String loggedInUserTenantId);

	/**
	 * @param prArr
	 * @param response
	 * @param session
	 */
	void downloadFinancePoReports(String[] prArr, HttpServletResponse response, HttpSession session);

}
