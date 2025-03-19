/**
 * 
 */
package com.privasia.procurehere.service.impl;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.privasia.procurehere.core.dao.PlanDao;
import com.privasia.procurehere.core.entity.Plan;
import com.privasia.procurehere.core.enums.PlanStatus;
import com.privasia.procurehere.core.pojo.TableDataInput;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.service.PlanService;

/**
 * @author Nitin Otageri
 */
@Service
@Transactional(readOnly = true)
public class PlanServiceImpl implements PlanService {

	@SuppressWarnings("unused")
	private static Logger LOG = LogManager.getLogger(PlanServiceImpl.class);

	@Autowired
	PlanDao planDao;
	
	@Value("${app.url}")
	String APP_URL;

	@Override
	public List<Plan> findAllPlansByStatus(PlanStatus planStatus) {
		return planDao.findAllPlansByStatus(planStatus);
	}

	@Override
	public List<Plan> findAllPlansByStatusForIntegration(PlanStatus planStatus) {
		List<Plan> planList = planDao.findAllPlansByStatus(planStatus);
		if(CollectionUtil.isNotEmpty(planList)) {
			for (Plan plan : planList) {
				plan.setArchiveDate(null);
				plan.setBillingCycles(null);
				plan.setCarryForwardUnits(null);
				plan.setCreatedBy(null);
				plan.setCreatedDate(null);
				plan.setInvoiceName(null);
				plan.setInvoiceNotes(null);
				plan.setModifiedBy(null);
				plan.setModifiedDate(null);
				plan.setPlanUrl(APP_URL + "/subscription/get/" + plan.getId());
			}
		}
		return planList;
	}
	
	@Override
	public List<Plan> findAllPlansByStatuses(PlanStatus[] planStatuses) {
		return planDao.findAllPlansByStatuses(planStatuses);
	}

	@Override
	public List<Plan> findPlans(TableDataInput tableParams) {
		List<Plan> list = planDao.findPlans(tableParams);
		if(CollectionUtil.isNotEmpty(list)) {
			for (Plan plan : list) {
				if(plan.getCreatedBy() != null) {
					plan.getCreatedBy().setCreatedBy(null);
				}
			}
		}
		return list;
	}

	@Override
	public long findTotalPlans() {
		return planDao.findTotalPlans();
	}

	@Override
	public long findTotalFilteredPlans(TableDataInput tableParams) {
		return planDao.findTotalFilteredPlans(tableParams);
	}

	@Override
	public boolean isExists(Plan plan) {
		return planDao.isExists(plan);
	}

	@Override
	public Plan getPlanForEditById(String planId) {
		return planDao.getPlanForEditById(planId);
	}

	@Override
	@Transactional(readOnly = false)
	public Plan savePlan(Plan plan) {
		return planDao.saveOrUpdate(plan);
	}

	@Override
	@Transactional(readOnly = false)
	public Plan updatePlan(Plan plan) {
		return planDao.update(plan);
	}

	@Override
	@Transactional(readOnly = false)
	public void deletePlan(Plan plan) {
		planDao.delete(plan);
	}
}
