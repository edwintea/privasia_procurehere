package com.privasia.procurehere.core.dao;

import java.util.Date;
import java.util.List;

import com.privasia.procurehere.core.entity.FinancePo;
import com.privasia.procurehere.core.entity.Po;
import com.privasia.procurehere.core.entity.Supplier;
import com.privasia.procurehere.core.enums.FinancePoStatus;
import com.privasia.procurehere.core.enums.FinancePoType;
import com.privasia.procurehere.core.pojo.TableDataInput;

public interface PoFinanceDao extends GenericDao<FinancePo, String> {
	/**
	 * @param prId
	 * @param supId
	 * @param loggedInUserTenantId
	 * @return
	 */
	FinancePo getPoFinanceByPrIdAndSupID(String prId, String supId, String loggedInUserTenantId);

	List<FinancePo> findAllSearchFilterPoForFinance(String loggedInUserTenantId, TableDataInput input, Date startDate, Date endDate, FinancePoStatus approved, String selectedSupplier, FinancePoType financePoType);

	long findTotalSearchFilterPoForFinance(String loggedInUserTenantId, TableDataInput input, Date startDate, Date endDate, FinancePoStatus approved, String selectedSupplier, FinancePoType financePoType);

	long findTotalPoForFinance(String loggedInUserTenantId, FinancePoType financePoType);

	List<Supplier> findAllSupplierGloballyForFinance(String searchVal, String tenantId);

	long findTotalCountPoForFinanceByStatus(String tenantId, FinancePoStatus status, FinancePoType financePoType);

	List<Po> findAllPrPoGloballyForFinance(String searchVal, String tenantId);

	FinancePo getPoFinanceForSupplier(String poId, String tenantId);

	/**
	 * @param prArr
	 * @return
	 */
	List<FinancePo> findFinancePoByIds(String[] prArr);

	/**
	 * @param poId
	 * @return
	 */
	List<FinancePo> findFinancePoByIdsAndStatus(String poId);

	/**
	 * 
	 * @param id
	 */
	void deletePo(String id);

}
