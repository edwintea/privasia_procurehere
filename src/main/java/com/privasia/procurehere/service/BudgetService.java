package com.privasia.procurehere.service;

import java.math.BigDecimal;
import java.util.List;

import com.privasia.procurehere.core.entity.Budget;
import com.privasia.procurehere.core.entity.BudgetDocument;
import com.privasia.procurehere.core.entity.BuyerAuditTrail;
import com.privasia.procurehere.core.enums.BudgetStatus;
import com.privasia.procurehere.core.exceptions.ApplicationException;
import com.privasia.procurehere.core.pojo.BudgetPojo;
import com.privasia.procurehere.core.pojo.TableDataInput;

public interface BudgetService {

	Budget saveBudget(Budget budget, BudgetPojo budgetModelPojo) throws ApplicationException;

	List<BudgetPojo> getAllBudgetForTenantId(String loggedInUserTenantId, TableDataInput input);

	long findTotalBudgetForTenantId(String loggedInUserTenantId, TableDataInput input);

	BudgetPojo findBudgetById(String id);

	long findfilteredTotalCountBudgetForTenantId(String loggedInUserTenantId, TableDataInput input);

	Budget findById(String id);

	Budget updateBudget(Budget budget);

	Budget findBudgetByBusinessUnit(String id);

	long findTotalCountBudgetForTenantId(String loggedInUserTenantId, TableDataInput input);

	List<BudgetPojo> getAllBudgetForApproval(String loggedInUserTenantId, String id, TableDataInput input);

	long findTotalBudgetForApproval(String loggedInUserTenantId, String id, TableDataInput input);

	long findTotalCountBudgetForApproval(String loggedInUserTenantId, String id, TableDataInput input);

	Budget saveBudgetAndDocuments(Budget budget);

	Budget findBudgetAndDocumentById(String id);

	void deleteDocumentById(String budgetId, String docId);

	int updateImmediately(String id, BudgetStatus status);

	BudgetDocument findBudgetDocById(String docId);

	Boolean isCombinationOfBuAndCcExists(BudgetPojo budgetPojo);

	Budget findBudgetByBusinessUnitAndCostCenter(String businessUnitId, String costCenterId);

	void addAdditionalApprovers(BudgetPojo budgetPojo, String id);

	void sendBudgetUtilizedNotifications(String string, BigDecimal percentageUsed);

	void sendBudgetOverrunNotification(String id);

	Budget findTransferToBudgetById(String id);

	void saveBuyerAuditTrail(BuyerAuditTrail audit);

}
