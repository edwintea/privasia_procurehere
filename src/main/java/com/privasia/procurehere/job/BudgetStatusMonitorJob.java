package com.privasia.procurehere.job;

import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.privasia.procurehere.core.dao.BudgetDao;
import com.privasia.procurehere.core.entity.Budget;
import com.privasia.procurehere.core.enums.BudgetStatus;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.service.BudgetService;

@Component
public class BudgetStatusMonitorJob implements BaseSchedulerJob {

	private static final Logger LOG = LogManager.getLogger(Global.BUDGET_PLANNER);

	@Autowired
	BudgetDao budgetDao;

	@Autowired
	BudgetService budgetService;

	@Autowired
	JmsTemplate jmsTemplate;

	@Autowired
	MessageSource messageSource;

	@Override
	@Transactional(readOnly = false)
	public void execute(JobExecutionContext ctx) {
//		LOG.info("Start Budget status Job - " + System.currentTimeMillis());
		changeFromApprovedToActive();
		changeFromActiveToExpired();
		changeFromPendingToDrafts();
//		LOG.info("End Budget status Job - " + System.currentTimeMillis());
	}

	private void changeFromPendingToDrafts() {
		try {
			List<Budget> list = budgetDao.getAllPendingBudgetsforJob();
			if (CollectionUtil.isNotEmpty(list)) {
				Date now = new Date();
				BudgetStatus status = BudgetStatus.DRAFT;
				// String timeZone = "GMT+8:00";
				for (Budget budget : list) {
					String id = budget.getId();
					if (budget.getValidTo() != null && now.after(budget.getValidTo())) {
						LOG.info("Changing BUDGET status PENDING to DRAFT " + budget.getId());
						budgetService.updateImmediately(id, status);

					}
				}
			}
		} catch (Exception e) {
			LOG.error("Error during change of budget status from Approved to Active : " + e.getMessage(), e);
		}

	}

	private void changeFromActiveToExpired() {
		try {
			List<Budget> list = budgetDao.getAllActiveBudgetsforJob();
			if (CollectionUtil.isNotEmpty(list)) {
				Date now = new Date();
				BudgetStatus status = BudgetStatus.EXPIRED;
				// String timeZone = "GMT+8:00";
				for (Budget budget : list) {
					String id = budget.getId();
					if (budget.getValidTo() != null && now.after(budget.getValidTo())) {
						LOG.info("Changing BUDGET status ACTIVE to EXPIRE " + budget.getId());
						budgetService.updateImmediately(id, status);

					}
				}
			}
		} catch (Exception e) {
			LOG.error("Error during change of budget status from Approved to Active : " + e.getMessage(), e);
		}

	}

	private void changeFromApprovedToActive() {
		try {
			List<Budget> list = budgetDao.getAllApprovedBudgetsforJob();
			if (CollectionUtil.isNotEmpty(list)) {
				Date now = new Date();
				BudgetStatus status = BudgetStatus.ACTIVE;
				// String timeZone = "GMT+8:00";
				for (Budget budget : list) {
					String id = budget.getId();
					if (budget.getValidFrom() != null && now.after(budget.getValidFrom())) {
						LOG.info("Changing BUDGET status APPROVED to ACTIVE " + budget.getId());
						budgetService.updateImmediately(id, status);

					}
				}
			}
		} catch (Exception e) {
			LOG.error("Error during change of budget status from Approved to Active : " + e.getMessage(), e);
		}

	}

}
