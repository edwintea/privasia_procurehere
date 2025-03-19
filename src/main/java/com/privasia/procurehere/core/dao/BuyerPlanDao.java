package com.privasia.procurehere.core.dao;

import java.util.List;

import com.privasia.procurehere.core.entity.BuyerPlan;
import com.privasia.procurehere.core.enums.PlanStatus;
import com.privasia.procurehere.core.pojo.TableDataInput;

/**
 * @author parveen
 */
public interface BuyerPlanDao extends GenericDao<BuyerPlan, String> {
	/**
	 * @param input
	 * @return
	 */
	List<BuyerPlan> findBuyerPlans(TableDataInput input);

	/**
	 * @param input
	 * @return
	 */
	long findTotalFilteredBuyerPlans(TableDataInput input);

	/**
	 * @return
	 */
	long findTotalActiveBuyerPlans();

	/**
	 * @param planId
	 * @return
	 */
	BuyerPlan getBuyerPlanForEditById(String planId);

	/**
	 * @param status
	 * @return
	 */
	List<BuyerPlan> findAllBuyerPlansByStatus(PlanStatus status);

	/**
	 * @param planStatus
	 * @return
	 */
	List<BuyerPlan> findAllBuyerPlansByStatuses(PlanStatus[] planStatus);

	/**
	 * @param planId TODO
	 * @return
	 */
	boolean checkBuyerPlanInUse(String planId);

	/**
	 * @param planName
	 * @param status
	 * @return
	 */
	BuyerPlan getPlanByPlanName(String planName, PlanStatus status);

	/**
	 * @return
	 */
	BuyerPlan findUserBasedBuyerPlansByStatus();

	/**
	 * @return
	 */
	BuyerPlan findEventBasedBuyerPlansByStatus();

}
