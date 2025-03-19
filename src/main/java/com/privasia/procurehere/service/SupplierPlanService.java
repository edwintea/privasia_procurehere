/**
 * 
 */
package com.privasia.procurehere.service;

import java.util.List;

import javax.servlet.http.HttpServletResponse;

import com.privasia.procurehere.core.entity.SupplierPlan;
import com.privasia.procurehere.core.enums.PlanStatus;
import com.privasia.procurehere.core.pojo.TableDataInput;

/**
 * @author Nitin Otageri
 */
public interface SupplierPlanService {

	/**
	 * @param planStatus
	 * @return
	 */
	List<SupplierPlan> findAllPlansByStatus(PlanStatus planStatus);

	/**
	 * @param plan
	 * @return
	 */
	boolean isExists(SupplierPlan plan);

	/**
	 * @param planId
	 * @return
	 */
	SupplierPlan getPlanForEditById(String planId);

	/**
	 * @param plan
	 * @return
	 */
	SupplierPlan savePlan(SupplierPlan plan);

	/**
	 * @param plan
	 * @return
	 */
	SupplierPlan updatePlan(SupplierPlan plan);

	/**
	 * @param tableParams
	 * @return
	 */
	long findTotalFilteredPlans(TableDataInput tableParams);

	/**
	 * @return
	 */
	long findTotalPlans();

	/**
	 * @param tableParams
	 * @return
	 */
	List<SupplierPlan> findPlans(TableDataInput tableParams);

	/**
	 * @param plan
	 */
	void deletePlan(SupplierPlan plan);

	/**
	 * @param planStatuses
	 * @return
	 */
	List<SupplierPlan> findAllPlansByStatuses(PlanStatus[] planStatuses);

	/**
	 * @param planStatus
	 * @return
	 */
	List<SupplierPlan> findAllPlansByStatusForIntegration(PlanStatus planStatus);

	/**
	 * @param active
	 * @param planId
	 * @return
	 */
	List<SupplierPlan> findAllPlansForUpgradeByStatus(PlanStatus active, String planId);

	/**
	 * @return
	 */
	SupplierPlan getAllBuyerSupplierPlan();


	void downloadPaymentTransactionExcel(HttpServletResponse response, String loggedInUserTenantId);

	SupplierPlan getPlanById(String planId);

}
