package com.privasia.procurehere.core.dao;

import java.util.List;

import com.privasia.procurehere.core.entity.Plan;
import com.privasia.procurehere.core.enums.PlanStatus;
import com.privasia.procurehere.core.pojo.TableDataInput;

/**
 * @author Nitin Otageri
 */
public interface PlanDao extends GenericDao<Plan, String> {

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
	 * @param planStatuses
	 * @return
	 */
	List<Plan> findAllPlansByStatuses(PlanStatus[] planStatuses);

}
