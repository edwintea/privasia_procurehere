/**
 * 
 */
package com.privasia.procurehere.job;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.privasia.procurehere.core.dao.ProductContractDao;

/**
 * @author nitin
 */
@Component
public class ContractActiveAndExpiredStatusMonitorJob implements BaseSchedulerJob {

	private static final Logger LOG = LogManager.getLogger(ContractActiveAndExpiredStatusMonitorJob.class);

	@Autowired
	ProductContractDao productContractDao;

	@Override
	@Transactional(readOnly = false)
	public void execute(JobExecutionContext ctx) {
		int updateCount = 0;
		LOG.info("Start Contract ACTIVE status Job - " + System.currentTimeMillis());
		try {
			updateCount = productContractDao.updateStatusFromApprovedToActive();
		} catch (Exception e) {
			LOG.error("Error during change form status from APPROVED to ACTIVE : " + e.getMessage(), e);
		}
		LOG.info("END Contract ACTIVE status Job - Made Active : " + updateCount);

		LOG.info("Start Contract EXPIRE status Job - " + System.currentTimeMillis());
		try {
			updateCount = productContractDao.updateStatusFromActiveToExpired();
		} catch (Exception e) {
			LOG.error("Error during change form status from ACTIVE to EXPIRED : " + e.getMessage(), e);
		}
		LOG.info("END Contract EXPIRE status Job - Made Expired : " + updateCount);

	}

}
