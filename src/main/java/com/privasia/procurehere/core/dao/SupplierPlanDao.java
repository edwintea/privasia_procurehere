package com.privasia.procurehere.core.dao;

import java.util.List;

import com.privasia.procurehere.core.entity.SupplierPlan;
import com.privasia.procurehere.core.enums.PlanStatus;
import com.privasia.procurehere.core.pojo.TableDataInput;

/**
 * @author Nitin Otageri
 */
public interface SupplierPlanDao extends GenericDao<SupplierPlan, String> {

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
	 * @param planStatuses
	 * @return
	 */
	List<SupplierPlan> findAllPlansByStatuses(PlanStatus[] planStatuses);

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

	/**
	 * @param planName
	 * @return
	 */
	SupplierPlan getPlanByName(String planName);

}
