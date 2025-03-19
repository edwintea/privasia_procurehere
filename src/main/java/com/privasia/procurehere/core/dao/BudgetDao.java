package com.privasia.procurehere.core.dao;

import java.util.List;

import com.privasia.procurehere.core.entity.Budget;
import com.privasia.procurehere.core.enums.BudgetStatus;
import com.privasia.procurehere.core.pojo.BudgetPojo;
import com.privasia.procurehere.core.pojo.SpentAnalysisPojoForBudget;
import com.privasia.procurehere.core.pojo.TableDataInput;

public interface BudgetDao extends GenericDao<Budget, String> {

	List<BudgetPojo> getAllBudgetForTenantId(String loggedInUserTenantId, TableDataInput input);

	long findTotalBudgetForTenantId(String loggedInUserTenantId, TableDataInput input);

	Budget findBudgetById(String id);

	long findfilteredTotalCountBudgetForTenantId(String loggedInUserTenantId, TableDataInput input);

	Budget findBudgetByBusinessUnit(String id);

	long findTotalCountBudgetForTenantId(String loggedInUserTenantId, TableDataInput input);

	List<BudgetPojo> getAllBudgetForApproval(String loggedInUserTenantId, String id, TableDataInput input);

	void deleteDocumentById(String budgetId, String docId);

	List<Budget> getAllApprovedBudgetsforJob();

	int updateImmediately(String id, BudgetStatus status);

	List<Budget> getAllActiveBudgetsforJob();

	Budget findBudgetByBusinessUnitAndCostCenter(String businessUnitId, String costCenterId);

	List<Budget> getAllPendingBudgetsforJob();

	Budget findById(String id);

	Budget findBudgetForAdditionalApprovalsById(String id);

	Budget findBudgetCreatorAndApprovalsById(String id);

	long findTotalBudgetForApproval(String loggedInUserTenantId, String id, TableDataInput input);

	Budget findTransferToBudgetById(String id);

	// For spend analysis
	List<SpentAnalysisPojoForBudget> getBudgetBasedOnCostCenterAndBusinessUnit(String costCenter, String businessUnit, int month, int year);

    void transferOwnership(String fromUserId, String toUserId);
}
