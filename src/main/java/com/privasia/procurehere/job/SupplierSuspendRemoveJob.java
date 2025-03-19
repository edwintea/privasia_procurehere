package com.privasia.procurehere.job;

import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.privasia.procurehere.core.entity.FavouriteSupplier;
import com.privasia.procurehere.core.enums.FavouriteSupplierStatus;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.service.FavoriteSupplierService;
import com.privasia.procurehere.service.FavoutireSupplierAuditService;
import com.privasia.procurehere.service.supplier.SupplierService;

/**
 * @author sarang
 */
@Component("supplierSuspend")
public class SupplierSuspendRemoveJob implements BaseSchedulerJob {
	private static final Logger LOG = LogManager.getLogger(Global.BUYER_LOG);

	@Autowired
	FavoutireSupplierAuditService favSuppAuditService;

	@Autowired
	FavoriteSupplierService favSupplierService;
	@Autowired
	SupplierService supplierService;

	@Override
	public void execute(JobExecutionContext ctx) {
		LOG.info("JOB is Executing ");
		List<FavouriteSupplier> favSuppList = favSupplierService.findAllFavouriteSupplierForSuspension();
		try {
			for (FavouriteSupplier favouriteSupplier : favSuppList) {
				Date suspendStartDate = favouriteSupplier.getSuspendStartDate();
				Date suspendEndDate = favouriteSupplier.getSuspendEndDate();
				if (suspendStartDate != null && new Date().after(suspendStartDate) && favouriteSupplier.getStatus() != FavouriteSupplierStatus.SUSPENDED) {
					LOG.info("suspending ");
					favouriteSupplier.setStatus(FavouriteSupplierStatus.SUSPENDED);
				}
				if (suspendEndDate != null && new Date().after(suspendEndDate) &&  favouriteSupplier.getStatus() == FavouriteSupplierStatus.SUSPENDED) {
					LOG.info("ACTVATING");
					favouriteSupplier.setSuspendStartDate(null);
					favouriteSupplier.setSuspendEndDate(null);
					favouriteSupplier.setIsFutureSuspended(false);
					favouriteSupplier.setStatus(FavouriteSupplierStatus.ACTIVE);
				}

				favSupplierService.saveFavoriteSupplier(favouriteSupplier, null);
				LOG.info("Favourite Supplier Status " + favouriteSupplier.getStatus());
				LOG.info("SUSPENSION START DATE " + suspendStartDate);
				LOG.info("SUSPENSION END DATE " + suspendEndDate);
			}
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}

	}

}
