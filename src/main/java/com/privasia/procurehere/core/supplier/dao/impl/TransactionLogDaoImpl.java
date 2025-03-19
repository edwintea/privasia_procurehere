package com.privasia.procurehere.core.supplier.dao.impl;

import java.util.List;

import javax.persistence.Query;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Repository;

import com.privasia.procurehere.core.dao.impl.GenericDaoImpl;
import com.privasia.procurehere.core.entity.TransactionLog;
import com.privasia.procurehere.core.enums.TransactionLogStatus;
import com.privasia.procurehere.core.pojo.ColumnParameter;
import com.privasia.procurehere.core.pojo.OrderParameter;
import com.privasia.procurehere.core.pojo.TableDataInput;
import com.privasia.procurehere.core.pojo.TransactionLogPojo;
import com.privasia.procurehere.core.supplier.dao.TransactionLogDao;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.StringUtils;

@Repository
public class TransactionLogDaoImpl extends GenericDaoImpl<TransactionLog, String> implements TransactionLogDao {

	private static Logger LOG = LogManager.getLogger(Global.BUDGET_PLANNER);

	@SuppressWarnings("unchecked")
	@Override
	public List<TransactionLogPojo> getAlltransactionLogsForTenantId(String loggedInUserTenantId, TableDataInput input) {
		final Query query = constructBudgetTransactionsForTenantQuery(loggedInUserTenantId, input, false);
		query.setFirstResult(input.getStart());
		query.setMaxResults(input.getLength());
		return query.getResultList();

	}

	private Query constructBudgetTransactionsForTenantQuery(String loggedInUserTenantId, TableDataInput input, boolean isCount) {
		String sql = "";
		if (!isCount) {
			sql += "select distinct NEW com.privasia.procurehere.core.pojo.TransactionLogPojo( b.budgetId, tl.transactionTimeStamp, bu.unitName, cc.costCenter, tl.newAmount, tl.addAmount, tl.deductAmount, tbu.unitName, fbu.unitName, tl.transactionLogStatus, tl.purchaseOrderAmount, tl.locked, tl.prBaseCurrency, bc.currencyCode, tl.conversionRateAmount, tl.afterConversionAmount, tl.remainingAmount) ";
		}
		if (isCount) {
			sql += "select count(distinct tl.id)";
		}
		sql += " from TransactionLog tl ";

		sql += " left outer join tl.budget as b ";

		sql += " left outer join b.businessUnit as bu ";

		sql += " left outer join tl.toBusinessUnit as tbu ";

		sql += " left outer join tl.fromBusinessUnit as fbu ";

		sql += " left outer join b.baseCurrency as bc ";

		sql += " left outer join b.costCenter as cc ";

		sql += " where tl.tenantId = :tenantId ";

		// Add on search filter conditions
		for (ColumnParameter cp : input.getColumns()) {
			if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
				LOG.info("INPUT :: " + cp.getData() + "VALUE :: " + cp.getSearch().getValue());
				if (cp.getData().equals("referenceNumber")) {
					sql += " and upper(b.budgetId) like :" + cp.getData().replace(".", "") + " ";
				} else if (cp.getData().equals("unitName")) {
					sql += " and upper(bu.unitName) like :" + cp.getData().replace(".", "") + " ";
				} else if (cp.getData().equals("costCenter")) {
					sql += " and upper(cc.costCenter) like :" + cp.getData().replace(".", "") + " ";
				} else if (cp.getData().equals("fromBusinessUnit")) {
					sql += " and upper(fbu.unitName) like :" + cp.getData().replace(".", "") + " ";
				} else if (cp.getData().equals("toBusinessUnit")) {
					sql += " and upper(tbu.unitName) like :" + cp.getData().replace(".", "") + " ";
				} else if (cp.getData().equals("txStatus")) {
					sql += " and upper(tl.transactionLogStatus) like :" + cp.getData().replace(".", "") + " ";
				} else if (cp.getData().equals("budgetBaseCurrency")) {
					sql += " and upper(bc.currencyCode) like :" + cp.getData().replace(".", "") + " ";
				} else {
					sql += " and upper(tl." + cp.getData().replace(".", "") + ") like :" + cp.getData().replace(".", "") + " ";
				}
			}
		}

		// Implement order by
		if (!isCount) {
			List<OrderParameter> orderList = input.getOrder();
			if (CollectionUtil.isNotEmpty(orderList)) {
				sql += " order by ";
				for (OrderParameter order : orderList) {
					String orderColumn = input.getColumns().get(order.getColumn()).getData();
					String dir = order.getDir();
					if (orderColumn.equals("referenceNumber")) {
						orderColumn = "budgetId";
						sql += " b." + orderColumn + " " + dir + ",";
					} else if (orderColumn.equals("unitName")) {
						orderColumn = "unitName";
						sql += " bu." + orderColumn + " " + dir + ",";
					} else if (orderColumn.equals("costCenter")) {
						orderColumn = "costCenter";
						sql += " cc." + orderColumn + " " + dir + ",";
					} else if (orderColumn.equals("fromBusinessUnit")) {
						orderColumn = "unitName";
						sql += " fbu." + orderColumn + " " + dir + ",";
					} else if (orderColumn.equals("toBusinessUnit")) {
						orderColumn = "unitName";
						sql += " tbu." + orderColumn + " " + dir + ",";
					} else if (orderColumn.equals("budgetBaseCurrency")) {
						orderColumn = "currencyCode";
						sql += " bc." + orderColumn + " " + dir + ",";
					} else {
						sql += " tl." + orderColumn + " " + dir + ",";
					}

				}
				if (sql.lastIndexOf(",") == sql.length() - 1) {
					sql = sql.substring(0, sql.length() - 1);
				}
			} else {
				sql += " order by tl.transactionTimeStamp DESC";
			}
		} 
//		else {
//			sql += " order by tl.transactionTimeStamp DESC";
//		}

		final Query query = getEntityManager().createQuery(sql);

		query.setParameter("tenantId", loggedInUserTenantId);

		for (ColumnParameter cp : input.getColumns()) {
			if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
				if (cp.getData().equals("txStatus")) {
					query.setParameter(cp.getData().replace(".", ""), TransactionLogStatus.valueOf(cp.getSearch().getValue()));
				} else {
					query.setParameter(cp.getData().replace(".", ""), "%" + cp.getSearch().getValue().toUpperCase() + "%");
				}
			}
		}
		return query;
	}

	@SuppressWarnings("unused")
	private String getAllTransactionLogQuery() {
		String hql = "select distinct NEW com.privasia.procurehere.core.pojo.TransactionLogPojo( b.budgetId, tl.transactionTimeStamp, bu.unitName, cc.costCenter, tl.newAmount, tl.addAmount, tl.deductAmount, tbu.unitName, fbu.unitName, tl.transactionLogStatus, tl.purchaseOrderAmount, tl.locked, tl.prBaseCurrency, bc.currencyCode, tl.conversionRateAmount, tl.afterConversionAmount, tl.remainingAmount) ";

		hql += " from TransactionLog tl ";

		hql += " left outer join tl.budget as b ";

		hql += " left outer join b.businessUnit as bu ";

		hql += " left outer join tl.toBusinessUnit as tbu ";

		hql += " left outer join tl.fromBusinessUnit as fbu ";

		hql += " left outer join b.baseCurrency as bc ";

		hql += " left outer join b.costCenter as cc ";

		hql += " where tl.tenantId = :tenantId ";

		return hql;
	}

	@Override
	public long findfilteredTotalCountTransactionLogForTenantId(String loggedInUserTenantId, TableDataInput input) {
		final Query query = constructBudgetTransactionsForTenantQuery(loggedInUserTenantId, input, true);
		return ((Number) query.getSingleResult()).longValue();
	}

	@Override
	public long findTotalTransactionLogForTenantId(String loggedInUserTenantId, TableDataInput input) {
		String hql = "select count(tl.id) from TransactionLog tl ";

		hql += " where tl.tenantId = :tenantId ";

		final Query query = getEntityManager().createQuery(hql);
		query.setParameter("tenantId", loggedInUserTenantId);

		return ((Number) query.getSingleResult()).longValue();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<TransactionLogPojo> getAlltransactionLogsForTenantId(String loggedInUserTenantId) {
		String hql = "select distinct NEW com.privasia.procurehere.core.pojo.TransactionLogPojo( b.budgetId, tl.transactionTimeStamp, bu.unitName, cc.costCenter, tl.newAmount, tl.addAmount, tl.deductAmount, tbu.unitName, fbu.unitName, tl.transactionLogStatus, tl.purchaseOrderAmount, tl.locked, tl.prBaseCurrency, bc.currencyCode, tl.conversionRateAmount, tl.afterConversionAmount, tl.remainingAmount)  ";

		hql += " from TransactionLog tl ";

		hql += " left outer join tl.budget as b ";

		hql += " left outer join b.businessUnit as bu ";

		hql += " left outer join tl.toBusinessUnit as tbu ";

		hql += " left outer join tl.fromBusinessUnit as fbu ";

		hql += " left outer join b.baseCurrency as bc ";

		hql += " left outer join b.costCenter as cc ";

		hql += " where tl.tenantId = :tenantId  order by tl.transactionTimeStamp DESC ";

		final Query query = getEntityManager().createQuery(hql);
		query.setParameter("tenantId", loggedInUserTenantId);
		List<TransactionLogPojo> transactionList = query.getResultList();
		if (CollectionUtil.isNotEmpty(transactionList)) {
			return transactionList;
		} else {
			return null;
		}
	}

	@Override
	public long getTotalTransactionLogsByTenantIdForCsv(String tenantId) {
		String hql = "select count(tl.id) from TransactionLog tl ";
		hql += " where tl.tenantId = :tenantId ";

		final Query query = getEntityManager().createQuery(hql);
		query.setParameter("tenantId", tenantId);
		return ((Number) query.getSingleResult()).longValue();
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<TransactionLogPojo> findAllActiveSupplierByTenantIdAndStatusForCsv(String tenantId, int pageSize, int pageNo) {
		String hql = "select distinct NEW com.privasia.procurehere.core.pojo.TransactionLogPojo( b.budgetId, tl.transactionTimeStamp, bu.unitName, cc.costCenter, tl.newAmount, tl.addAmount, tl.deductAmount, tbu.unitName, fbu.unitName, tl.transactionLogStatus, tl.purchaseOrderAmount, tl.locked, tl.prBaseCurrency, bc.currencyCode, tl.conversionRateAmount, tl.afterConversionAmount, tl.remainingAmount)  ";

		hql += " from TransactionLog tl ";

		hql += " left outer join tl.budget as b ";

		hql += " left outer join b.businessUnit as bu ";

		hql += " left outer join tl.toBusinessUnit as tbu ";

		hql += " left outer join tl.fromBusinessUnit as fbu ";

		hql += " left outer join b.baseCurrency as bc ";

		hql += " left outer join b.costCenter as cc ";

		hql += " where tl.tenantId = :tenantId  order by tl.transactionTimeStamp DESC ";

		final Query query = getEntityManager().createQuery(hql);
		query.setParameter("tenantId", tenantId);
		List<TransactionLogPojo> transactionList = query.getResultList();
		if (CollectionUtil.isNotEmpty(transactionList)) {
			return transactionList;
		} else {
			return null;
		}
	}

}
