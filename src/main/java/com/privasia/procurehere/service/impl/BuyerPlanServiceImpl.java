/**
 * 
 */
package com.privasia.procurehere.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.privasia.procurehere.core.dao.BuyerPlanDao;
import com.privasia.procurehere.core.entity.BuyerPlan;
import com.privasia.procurehere.core.entity.PlanPeriod;
import com.privasia.procurehere.core.entity.PlanRange;
import com.privasia.procurehere.core.enums.PlanStatus;
import com.privasia.procurehere.core.pojo.TableDataInput;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.service.BuyerPlanService;

/**
 * @author parveen
 */
@Service
@Transactional(readOnly = true)
public class BuyerPlanServiceImpl implements BuyerPlanService {

	private static final Logger LOG = LogManager.getLogger(BuyerPlanServiceImpl.class);

	@Autowired
	BuyerPlanDao buyerPlanDao;

	@Override
	@Transactional(readOnly = false)
	public void saveBuyerPlan(BuyerPlan buyerPlan) {
		setBuyerPlanRangeAndPeriod(buyerPlan);
		buyerPlanDao.save(buyerPlan);
	}

	/**
	 * @param buyerPlan
	 * @return
	 */
	private BuyerPlan setBuyerPlanRangeAndPeriod(BuyerPlan buyerPlan) {
		if (CollectionUtil.isNotEmpty(buyerPlan.getRangeList())) {
			List<PlanRange> rangeList = new ArrayList<>();
			for (PlanRange planRange : buyerPlan.getRangeList()) {
				LOG.info("Range start:" + planRange.getRangeStart() + " Range End : " + planRange.getRangeEnd() + " plan Price" + planRange.getPrice());
				if (planRange.getRangeStart() != null && planRange.getRangeEnd() != null && planRange.getPrice() != null) {
					planRange.setBuyerPlan(buyerPlan);
					rangeList.add(planRange);
				}
			}
			buyerPlan.setRangeList(rangeList);
		}

		if (CollectionUtil.isNotEmpty(buyerPlan.getPlanPeriodList())) {
			List<PlanPeriod> periodList = new ArrayList<>();
			for (PlanPeriod period : buyerPlan.getPlanPeriodList()) {
				LOG.info("period Duration :" + period.getPlanDuration() + " period Discount :" + period.getPlanDiscount());
				if (period.getPlanDuration() != null && period.getPlanDiscount() != null) {
					period.setBuyerPlan(buyerPlan);
					periodList.add(period);
				}
			}
			buyerPlan.setPlanPeriodList(periodList);
		}
		return buyerPlan;
	}

	@Override
	@Transactional(readOnly = false)
	public void updateBuyerPlan(BuyerPlan buyerPlan) {
		BuyerPlan persistObj = buyerPlanDao.findById(buyerPlan.getId());

		persistObj.setPlanName(buyerPlan.getPlanName());
		persistObj.setPlanOrder(buyerPlan.getPlanOrder());
		persistObj.setShortDescription(buyerPlan.getShortDescription());
		persistObj.setDescription(buyerPlan.getDescription());
		persistObj.setPlanType(buyerPlan.getPlanType());
		persistObj.setCurrency(buyerPlan.getCurrency());
		persistObj.setPlanStatus(buyerPlan.getPlanStatus());

		setBuyerPlanRangeAndPeriod(buyerPlan);
		persistObj.setPlanPeriodList(buyerPlan.getPlanPeriodList());
		persistObj.setRangeList(buyerPlan.getRangeList());

		persistObj.setModifiedBy(buyerPlan.getModifiedBy());
		persistObj.setModifiedDate(buyerPlan.getModifiedDate());

		persistObj.setBasePrice(buyerPlan.getBasePrice());
		persistObj.setBaseUsers(buyerPlan.getBaseUsers());
		persistObj.setTax(buyerPlan.getTax());
		buyerPlanDao.saveOrUpdate(persistObj);
	}

	@Override
	public List<BuyerPlan> findBuyerPlans(TableDataInput input) {
		List<BuyerPlan> list = buyerPlanDao.findBuyerPlans(input);
		if (CollectionUtil.isNotEmpty(list)) {
			for (BuyerPlan plan : list) {
				if (plan.getCreatedBy() != null) {
					plan.getCreatedBy().setCreatedBy(null);
				}
				if (plan.getModifiedBy() != null) {
					plan.getModifiedBy().setCreatedBy(null);
				}
				plan.setPlanPeriodList(null);
				plan.setRangeList(null);
			}
		}
		return list;
	}

	@Override
	public long findTotalFilteredBuyerPlans(TableDataInput input) {
		return buyerPlanDao.findTotalFilteredBuyerPlans(input);
	}

	@Override
	public long findTotalActiveBuyerPlans() {
		return buyerPlanDao.findTotalActiveBuyerPlans();
	}

	@Override
	public BuyerPlan getBuyerPlanForEditById(String id) {
		BuyerPlan bp = buyerPlanDao.getBuyerPlanForEditById(id);
		if (bp != null && CollectionUtil.isNotEmpty(bp.getPlanPeriodList())) {
			for (PlanPeriod period : bp.getPlanPeriodList()) {
				period.getPlanDuration();
			}
		}
		return bp;
	}

	@Override
	@Transactional(readOnly = false)
	public void deleteBuyerPlan(BuyerPlan buyerPlan) {
		buyerPlanDao.delete(buyerPlan);
	}

	@Override
	public List<BuyerPlan> findAllBuyerPlansByStatus(PlanStatus status) {
		List<BuyerPlan> planList = buyerPlanDao.findAllBuyerPlansByStatus(status);
		if (CollectionUtil.isNotEmpty(planList)) {
			for (BuyerPlan buyerPlan : planList) {
				if (CollectionUtil.isNotEmpty(buyerPlan.getPlanPeriodList())) {
					for (PlanPeriod period : buyerPlan.getPlanPeriodList()) {
						period.getPlanDuration();
					}
				}
			}
		}
		return planList;
	}

	@Override
	public List<BuyerPlan> findAllBuyerPlansByStatuses(PlanStatus[] status) {
		List<BuyerPlan> planList = buyerPlanDao.findAllBuyerPlansByStatuses(status);
		if (CollectionUtil.isNotEmpty(planList)) {
			for (BuyerPlan buyerPlan : planList) {
				if (CollectionUtil.isNotEmpty(buyerPlan.getPlanPeriodList())) {
					for (PlanPeriod period : buyerPlan.getPlanPeriodList()) {
						period.getPlanDuration();
					}
				}
			}
		}
		return planList;
	}

	@Override
	public boolean checkBuyerPlanInUse(String planId) {
		return buyerPlanDao.checkBuyerPlanInUse(planId);
	}

	@Override
	public BuyerPlan getPlanByPlanName(String planName, PlanStatus status) {
		return buyerPlanDao.getPlanByPlanName(planName, status);
	}

	@Override
	public BuyerPlan findUserBasedBuyerPlansByStatus() {
		BuyerPlan buyerPlan = buyerPlanDao.findUserBasedBuyerPlansByStatus();
		if (buyerPlan != null)
			if (CollectionUtil.isNotEmpty(buyerPlan.getPlanPeriodList())) {
				for (PlanPeriod period : buyerPlan.getPlanPeriodList()) {
					period.getPlanDuration();
				}
			}
		return buyerPlan;
	}

	@Override
	public BuyerPlan findEventBasedBuyerPlansByStatus() {
		BuyerPlan buyerPlan = buyerPlanDao.findEventBasedBuyerPlansByStatus();
		if (buyerPlan != null)
			if (CollectionUtil.isNotEmpty(buyerPlan.getPlanPeriodList())) {
				for (PlanPeriod period : buyerPlan.getPlanPeriodList()) {
					period.getPlanDuration();
				}
			}
		return buyerPlan;
	}

}
