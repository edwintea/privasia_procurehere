package com.privasia.procurehere.job;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.privasia.procurehere.core.dao.ProductListMaintenanceDao;

/**
 * @author parveen
 */
@Component
public class ProductStatusMonitorJob implements BaseSchedulerJob {

	private static final Logger LOG = LogManager.getLogger(ProductStatusMonitorJob.class);
	
	@Autowired
	ProductListMaintenanceDao productListMaintenanceDao;

	@Override
	public void execute(JobExecutionContext ctx) {
		LOG.info("Running Product Status Monitoring job.... :D ");
		changeFromActiveToInactive();
	}

	private void changeFromActiveToInactive() {
		try {
			productListMaintenanceDao.setStatusInactiveForPastProducts();
			
		} catch (Exception e) {
			LOG.error("Error during Monitoring job change of product status from Active to Inactive" + e.getMessage(), e);
		}
	}
}
