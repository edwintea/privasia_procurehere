/**
 * 
 */
package com.privasia.procurehere.service;

import java.util.List;

import com.privasia.procurehere.core.entity.Plan;
import com.privasia.procurehere.core.enums.PlanStatus;
import com.privasia.procurehere.core.pojo.TableDataInput;

/**
 * @author Nitin Otageri
 */
public interface PlanService {

	/**
	 * @param planStatus
	 * @return
	 */
	List<Plan> findAllPlansByStatus(PlanStatus planStatus);

	/**
	 * @param plan
	 * @return
	 */
	boolean isExists(Plan plan);

	/**
	 * @param planId
	 * @return
	 */
	Plan getPlanForEditById(String planId);

	/**
	 * @param plan
	 * @return
	 */
	Plan savePlan(Plan plan);

	/**
	 * @param plan
	 * @return
	 */
	Plan updatePlan(Plan plan);

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
	List<Plan> findPlans(TableDataInput tableParams);

	/**
	 * @param plan
	 */
	void deletePlan(Plan plan);

	/**
	 * @param planStatuses
	 * @return
	 */
	List<Plan> findAllPlansByStatuses(PlanStatus[] planStatuses);

	/**
	 * @param planStatus
	 * @return
	 */
	List<Plan> findAllPlansByStatusForIntegration(PlanStatus planStatus);

}
