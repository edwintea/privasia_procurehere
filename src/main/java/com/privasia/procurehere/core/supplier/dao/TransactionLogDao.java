package com.privasia.procurehere.core.supplier.dao;

import java.util.List;

import com.privasia.procurehere.core.dao.GenericDao;
import com.privasia.procurehere.core.entity.TransactionLog;
import com.privasia.procurehere.core.pojo.TableDataInput;
import com.privasia.procurehere.core.pojo.TransactionLogPojo;

public interface TransactionLogDao extends GenericDao<TransactionLog, String> {

	List<TransactionLogPojo> getAlltransactionLogsForTenantId(String loggedInUserTenantId, TableDataInput input);

	long findfilteredTotalCountTransactionLogForTenantId(String loggedInUserTenantId, TableDataInput input);

	long findTotalTransactionLogForTenantId(String loggedInUserTenantId, TableDataInput input);

	/**
	 * @param loggedInUserTenantId
	 * @return
	 */
	List<TransactionLogPojo> getAlltransactionLogsForTenantId(String loggedInUserTenantId);

	/**
	 * @param tenantId
	 * @return
	 */
	long getTotalTransactionLogsByTenantIdForCsv(String tenantId);

	/**
	 * @param tenantId
	 * @param pageSize
	 * @param pageNo
	 * @return
	 */
	List<TransactionLogPojo> findAllActiveSupplierByTenantIdAndStatusForCsv(String tenantId, int pageSize, int pageNo);

}
