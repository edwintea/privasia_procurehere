package com.privasia.procurehere.core.dao.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.persistence.Query;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Repository;

import com.privasia.procurehere.core.dao.BudgetDao;
import com.privasia.procurehere.core.entity.Budget;
import com.privasia.procurehere.core.entity.User;
import com.privasia.procurehere.core.enums.ApprovalStatus;
import com.privasia.procurehere.core.enums.BudgetStatus;
import com.privasia.procurehere.core.enums.EventStatus;
import com.privasia.procurehere.core.enums.RfxTypes;
import com.privasia.procurehere.core.pojo.BudgetPojo;
import com.privasia.procurehere.core.pojo.ColumnParameter;
import com.privasia.procurehere.core.pojo.OrderParameter;
import com.privasia.procurehere.core.pojo.SpentAnalysisPojoForBudget;
import com.privasia.procurehere.core.pojo.TableDataInput;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.SecurityLibrary;
import com.privasia.procurehere.core.utils.StringUtils;

@Repository
public class BudgetDaoImpl extends GenericDaoImpl<Budget, String> implements BudgetDao {

	private static final Logger LOG = LogManager.getLogger(Global.BUDGET_PLANNER);

	@Override
	public Budget saveOrUpdate(Budget transientObject) {
		return super.saveOrUpdate(transientObject);
	}

	@Override
	public Budget saveOrUpdateWithFlush(Budget transientObject) {
		return super.saveOrUpdateWithFlush(transientObject);
	}

	@Override
	public Budget findById(String id) {
		String hql = "select b from Budget b left join fetch b.createdBy cb where b.id = :id";
		final Query query = getEntityManager().createQuery(hql);
		query.setParameter("id", id);
		Budget budget = (Budget) query.getSingleResult();
		return budget;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<BudgetPojo> getAllBudgetForTenantId(String loggedInUserTenantId, TableDataInput input) {
		final Query query = constructBudgetForTenantQuery(loggedInUserTenantId, input, false);
		query.setFirstResult(input.getStart());
		query.setMaxResults(input.getLength());
		return query.getResultList();
	}

	private Query constructBudgetForTenantQuery(String loggedInUserTenantId, TableDataInput input, boolean isCount) {
		String sql = "";
		if (!isCount) {
			sql += "select distinct NEW com.privasia.procurehere.core.pojo.BudgetPojo(b.id, b.budgetId, b.budgetName, b.budgetStatus, bu.unitName, cc.costCenter, b.validFrom, b.validTo, b.totalAmount, b.pendingAmount, b.approvedAmount, b.lockedAmount, b.paidAmount, b.transferAmount, b.remainingAmount ) ";
		}
		if (isCount) {
			sql += "select count(distinct b.id)";
		}
		sql += " from Budget b ";

		sql += " left outer join b.businessUnit as bu ";

		sql += " left outer join b.costCenter as cc ";

		sql += " left outer join b.budgetOwner as bUser ";

		sql += " where b.tenantId = :tenantId ";

		// Add on search filter conditions
		for (ColumnParameter cp : input.getColumns()) {
			if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
				LOG.info("INPUT :: " + cp.getData() + "VALUE :: " + cp.getSearch().getValue());
				if (cp.getData().equals("businessUnitName")) {
					sql += " and upper(bu.unitName) like :" + cp.getData().replace(".", "") + " ";
				} else if (cp.getData().equals("costCenterName")) {
					sql += " and upper(cc.costCenter) like :" + cp.getData().replace(".", "") + " ";
				} else if (cp.getData().equals("budgetStatus")) {
					sql += " and upper(b.budgetStatus) like :" + cp.getData().replace(".", "") + " ";
				} else {
					sql += " and upper(b." + cp.getData().replace(".", "") + ") like :" + cp.getData().replace(".", "") + " ";
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
					if (orderColumn.equals("businessUnitName")) {
						orderColumn = "unitName";
						sql += " bu." + orderColumn + " " + dir + ",";
					} else if (orderColumn.equals("costCenterName")) {
						orderColumn = "costCenter";
						sql += " cc." + orderColumn + " " + dir + ",";
					} else {
						sql += " b." + orderColumn + " " + dir + ",";
					}

				}
				if (sql.lastIndexOf(",") == sql.length() - 1) {
					sql = sql.substring(0, sql.length() - 1);
				}
			} else {
				sql += " order by b.validTo DESC";
			}
		}
//		else {
//			sql += " order by b.validTo DESC";
//		}

		final Query query = getEntityManager().createQuery(sql);

		query.setParameter("tenantId", loggedInUserTenantId);

		for (ColumnParameter cp : input.getColumns()) {
			if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
				if (cp.getData().equals("businessUnitName")) {
					query.setParameter(cp.getData().replace(".", ""), "%" + cp.getSearch().getValue().toUpperCase() + "%");
				} else if (cp.getData().equals("costCenterName")) {
					query.setParameter(cp.getData().replace(".", ""), "%" + cp.getSearch().getValue().toUpperCase() + "%");
				} else if (cp.getData().equals("budgetStatus")) {
					query.setParameter(cp.getData().replace(".", ""), BudgetStatus.valueOf(cp.getSearch().getValue()));
				} else {
					query.setParameter(cp.getData().replace(".", ""), "%" + cp.getSearch().getValue().toUpperCase() + "%");
				}
			}
		}
		return query;

	}

	@Override
	public long findTotalBudgetForTenantId(String loggedInUserTenantId, TableDataInput input) {
		final Query query = constructBudgetForTenantQuery(loggedInUserTenantId, input, true);
		return ((Number) query.getSingleResult()).longValue();
	}

	private String getAllBudgetQuery() {
		String hql = "select distinct NEW com.privasia.procurehere.core.pojo.BudgetPojo(b.id, b.budgetId, b.budgetName, b.budgetStatus, bu.unitName, cc.costCenter, b.validFrom, b.validTo, b.totalAmount, b.pendingAmount, b.approvedAmount, b.lockedAmount, b.paidAmount, b.transferAmount, b.remainingAmount ) ";

		hql += " from Budget b ";

		hql += " left outer join b.businessUnit as bu ";

		hql += " left outer join b.costCenter as cc ";

		hql += " left outer join b.budgetOwner as bUser ";

		hql += " where b.tenantId = :tenantId ";

		return hql;
	}

	@Override
	public Budget findBudgetById(String id) {
		Budget budget = findById(id);
		if (CollectionUtil.isNotEmpty(budget.getBudgetDocuments())) {
			budget.getBudgetDocuments();
		}
		return budget;
	}

	@Override
	public long findfilteredTotalCountBudgetForTenantId(String loggedInUserTenantId, TableDataInput input) {

		String sql = "select count(distinct b.id) from (";
		sql += getAllBudgetQuery();
		sql += " ) b where 1 = 1";
		// Add on search filter conditions
		for (ColumnParameter cp : input.getColumns()) {
			if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
				LOG.info("INPUT :: " + cp.getData() + "VALUE :: " + cp.getSearch().getValue());
				sql += " and upper(b." + cp.getData().replace(".", "") + ") like :" + cp.getData().replace(".", "") + " ";
			}
		}

		// Implement order by
		List<OrderParameter> orderList = input.getOrder();
		if (CollectionUtil.isNotEmpty(orderList)) {
			sql += " order by ";
			for (OrderParameter order : orderList) {
				String orderColumn = input.getColumns().get(order.getColumn()).getData();
				String dir = order.getDir();
				if (orderColumn.equals("unitName")) {
					orderColumn = "businessUnit";
				}
				sql += " b." + orderColumn + " " + dir + ",";
			}
			if (sql.lastIndexOf(",") == sql.length() - 1) {
				sql = sql.substring(0, sql.length() - 1);
			}
		} else {
			sql += " order by b.validTo DESC";
		}

		final Query query = getEntityManager().createQuery(sql);

		query.setParameter("tenantId", loggedInUserTenantId);
		for (ColumnParameter cp : input.getColumns()) {
			if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
				if (cp.getData().equals("type")) {
					query.setParameter(cp.getData().replace(".", ""), RfxTypes.valueOf(cp.getSearch().getValue()).getValue());
				} else {
					query.setParameter(cp.getData().replace(".", ""), "%" + cp.getSearch().getValue().toUpperCase() + "%");
				}
			}
		}
		query.setFirstResult(input.getStart());
		query.setMaxResults(input.getLength());
		return query.executeUpdate();
	}

	@Override
	public Budget findBudgetByBusinessUnit(String id) {
		String hql = "select b from Budget b left join fetch b.businessUnit bu where bu.id = :id";
		final Query query = getEntityManager().createQuery(hql);
		query.setParameter("id", id);
		Budget budget = (Budget) query.getSingleResult();
		return budget;
	}

	@Override
	public long findTotalCountBudgetForTenantId(String loggedInUserTenantId, TableDataInput input) {
		String hql = "select count(distinct b.budgetId) ";
		hql += " from Budget b ";
		hql += " where b.tenantId = :tenantId ";
		final Query query = getEntityManager().createQuery(hql);
		query.setParameter("tenantId", loggedInUserTenantId);
		return ((Number) query.getSingleResult()).longValue();
	}

	@Override
	public List<BudgetPojo> getAllBudgetForApproval(String loggedInUserTenantId, String id, TableDataInput input) {

		String sql = "select distinct NEW com.privasia.procurehere.core.pojo.BudgetPojo(b.id, b.budgetStatus, b.budgetId, b.budgetName, bu.unitName, cc.costCenter, b.validFrom, b.validTo, b.totalAmount)from Budget b " + " left outer join  b.budgetOwner left outer join  b.businessUnit as bu left outer join  b.costCenter as cc " + "left outer join b.approvals app left outer join app.approvalUsers usr where b.tenantId =:tenantId and b.budgetStatus =:budgetStatus and " + "(app.budget.id = b.id and usr.user.id = :userId and usr.approvalStatus = :approvalStatus and app.active = true )";

		// Add on search filter conditions
		for (ColumnParameter cp : input.getColumns()) {
			if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
				LOG.info("INPUT :: " + cp.getData() + "VALUE :: " + cp.getSearch().getValue());
				if (cp.getData().equals("businessUnitName")) {
					LOG.info(cp.getData());
					LOG.info(cp.getSearch().getValue());
					sql += " and upper(b.businessUnit.unitName) like (:" + cp.getData() + ")";
				} else if (cp.getData().equals("costCenterName")) {
					LOG.info(cp.getData());
					LOG.info(cp.getSearch().getValue());
					sql += " and upper(b.costCenter.costCenter) like (:" + cp.getData() + ")";
				} else {
					sql += " and upper(b." + cp.getData().replace(".", "") + ") like :" + cp.getData().replace(".", "") + " ";
				}
			}
		}

		List<OrderParameter> orderList = input.getOrder();
		if (CollectionUtil.isNotEmpty(orderList)) {
			sql += " order by ";
			for (OrderParameter order : orderList) {
				String orderColumn = input.getColumns().get(order.getColumn()).getData();
				String dir = order.getDir();
				if (orderColumn.equals("businessUnitName")) {
					orderColumn = " b.businessUnit.unitName";
				} else if (orderColumn.equals("costCenterName")) {
					orderColumn = " b.costCenter.costCenter";
				} else {
					orderColumn = " b." + orderColumn;
				}

				sql += " " + orderColumn + " " + dir + ",";

				if (sql.lastIndexOf(",") == sql.length() - 1) {
					sql = sql.substring(0, sql.length() - 1);
				}
			}
		}
		final Query query = getEntityManager().createQuery(sql);
		LOG.debug("************+++++++++++++ dashboard budget " + sql);
		query.setParameter("tenantId", loggedInUserTenantId);
		query.setParameter("userId", id);
		query.setParameter("approvalStatus", ApprovalStatus.PENDING);
		query.setParameter("budgetStatus", BudgetStatus.PENDING);

		for (ColumnParameter cp : input.getColumns()) {
			if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
				query.setParameter(cp.getData().replace(".", ""), "%" + cp.getSearch().getValue().toUpperCase() + "%");
			}
		}
		query.setFirstResult(input.getStart());
		query.setMaxResults(input.getLength());
		return query.getResultList();
	}

	@Override
	public void deleteDocumentById(String budgetId, String docId) {
		String sql = "DELETE FROM PROC_BUDGET_DOCUMENT WHERE ID=:docId";
		final Query query = getEntityManager().createNativeQuery(sql);
		query.setParameter("docId", docId);
		query.executeUpdate();
	}

	@Override
	public List<Budget> getAllApprovedBudgetsforJob() {
		final Query query = getEntityManager().createQuery("select distinct new com.privasia.procurehere.core.entity.Budget(b.id, b.validFrom, b.budgetId, b.validTo) from Budget b where b.budgetStatus = :budgetStatus");
		query.setParameter("budgetStatus", BudgetStatus.APPROVED);
		List<Budget> list = query.getResultList();
		return list;
	}

	@Override
	public int updateImmediately(String id, BudgetStatus status) {
		String hql = "update Budget b set b.budgetStatus = :status where b.id = :id ";
		Query query = getEntityManager().createQuery(hql);
		query.setParameter("status", status);
		query.setParameter("id", id);
		try {
			return query.executeUpdate();
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			return 0;
		}
	}

	@Override
	public List<Budget> getAllActiveBudgetsforJob() {
		final Query query = getEntityManager().createQuery("select distinct new com.privasia.procurehere.core.entity.Budget(b.id, b.validFrom, b.budgetId, b.validTo) from Budget b where b.budgetStatus = :budgetStatus");
		query.setParameter("budgetStatus", BudgetStatus.ACTIVE);
		List<Budget> list = query.getResultList();
		return list;
	}

	@Override
	public Budget findBudgetByBusinessUnitAndCostCenter(String businessUnitId, String costCenterId) {
		String hql = "select b from Budget b left join fetch b.businessUnit bu left join fetch b.costCenter cc left join fetch b.baseCurrency bc where 1 = 1  ";

		if (StringUtils.checkString(businessUnitId).length() > 0) {
			hql += " and bu.id = :businessUnitId ";
		}
		if (StringUtils.checkString(costCenterId).length() > 0) {
			hql += " and cc.id=:costCenterId ";
		}
		hql += " and (b.budgetStatus=:approvedStatus or b.budgetStatus=:activeStatus or b.budgetStatus=:pendingStatus)";

		final Query query = getEntityManager().createQuery(hql);
		if (StringUtils.checkString(businessUnitId).length() > 0) {
			query.setParameter("businessUnitId", businessUnitId);
		}
		if (StringUtils.checkString(costCenterId).length() > 0) {
			query.setParameter("costCenterId", costCenterId);
		}
		query.setParameter("approvedStatus", BudgetStatus.APPROVED);
		query.setParameter("activeStatus", BudgetStatus.ACTIVE);
		query.setParameter("pendingStatus", BudgetStatus.PENDING);
		List<Budget> list = query.getResultList();
		if (CollectionUtil.isNotEmpty(list)) {
			return list.get(0);
		}
		return null;
	}

	@Override
	public List<Budget> getAllPendingBudgetsforJob() {
		final Query query = getEntityManager().createQuery("select distinct new com.privasia.procurehere.core.entity.Budget(b.id, b.validFrom, b.budgetId, b.validTo) from Budget b where b.budgetStatus = :budgetStatus");
		query.setParameter("budgetStatus", BudgetStatus.PENDING);
		List<Budget> list = query.getResultList();
		return list;
	}

	@Override
	public Budget findBudgetForAdditionalApprovalsById(String id) {
		final Query query = getEntityManager().createQuery("select distinct b from Budget b left outer join fetch b.approvals ba where b.id =:id order by ba.batchNo asc");
		query.setParameter("id", id);
		List<Budget> uList = query.getResultList();
		if (CollectionUtil.isNotEmpty(uList)) {
			return uList.get(0);
		} else {
			return null;
		}
	}

	@Override
	public Budget findBudgetCreatorAndApprovalsById(String id) {
		String hql = "select b from Budget b left join fetch b.createdBy cb left outer join fetch b.approvals ba where b.id = :id";
		final Query query = getEntityManager().createQuery(hql);
		query.setParameter("id", id);
		Budget budget = (Budget) query.getSingleResult();
		return budget;
	}

	@Override
	public long findTotalBudgetForApproval(String loggedInUserTenantId, String id, TableDataInput input) {
		String hql = "select count(distinct b) from Budget b left outer join b.approvals app left outer join app.approvalUsers usr where b.tenantId =:tenantId and b.budgetStatus =:budgetStatus and (app.budget.id = b.id and usr.user.id = :userId and usr.approvalStatus = :approvalStatus and app.active = true )";
		final Query query = getEntityManager().createQuery(hql);
		query.setParameter("tenantId", loggedInUserTenantId);
		query.setParameter("userId", id);
		query.setParameter("approvalStatus", ApprovalStatus.PENDING);
		query.setParameter("budgetStatus", BudgetStatus.PENDING);
		return ((Number) query.getSingleResult()).longValue();
	}

	@Override
	public Budget findTransferToBudgetById(String id) {
		String hql = "select b from Budget b left join fetch b.toBusinessUnit tbu left join fetch b.toCostCenter tcc where b.id = :id";
		final Query query = getEntityManager().createQuery(hql);
		query.setParameter("id", id);
		Budget budget = (Budget) query.getSingleResult();
		return budget;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<SpentAnalysisPojoForBudget> getBudgetBasedOnCostCenterAndBusinessUnit(String costCenter, String businessUnit, int month, int year) {

		// Calendar cal = Calendar.getInstance();
		// SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		// cal.set(Calendar.MONTH, 0);
		// cal.set(Calendar.YEAR, year);
		// cal.set(Calendar.DAY_OF_MONTH, 1);
		// String startDate = formatter.format(cal.getTime());
		// cal = Calendar.getInstance();
		// int res = cal.getActualMaximum(Calendar.DATE);
		// cal.set(Calendar.MONTH, month);
		// cal.set(Calendar.DAY_OF_MONTH, res);
		// String endDate = formatter.format(cal.getTime());

		String hql = "SELECT pbu.BUSINESS_UNIT_NAME, pcc.COST_CENTER, pb.REMAINING_AMOUNT, pb.TOTAL_AMOUNT FROM PROC_BUDGET pb JOIN PROC_BUSINESS_UNIT pbu ON pb.BUSINESS_UNIT_ID = pbu.ID JOIN PROC_COST_CENTER pcc ON pb.COST_CENTER_ID = pcc.ID WHERE (pb.BUSINESS_UNIT_ID, pb.COST_CENTER_ID ) IN ( SELECT BU, CC FROM ( " + "SELECT pp.BUSINESS_UNIT_ID AS BU, pp.COST_CENTER_ID AS CC FROM PROC_PO pp WHERE pp.BUSINESS_UNIT_ID IS NOT NULL ";
		if (businessUnit != null && businessUnit.length() > 0) {
			hql += "AND pp.BUSINESS_UNIT_ID =:businessUnit ";
		}
		hql += "AND pp.COST_CENTER_ID  IS NOT NULL ";
		if (costCenter != null && costCenter.length() > 0) {
			hql += "AND pp.COST_CENTER_ID =:costCenter ";
		}

		// If date range is required add the following clause
		// AND pp.PO_CREATED_DATE BETWEEN TO_DATE(:startDate , 'YYYY-MM-DD hh24:mi:ss') AND TO_DATE(:endDate , 'YYYY-MM-DD hh24:mi:ss')

		hql += "AND pp.BUYER_ID =:buyerId GROUP BY pp.BUSINESS_UNIT_ID, pp.COST_CENTER_ID ORDER BY SUM(pp.GRAND_TOTAL) DESC ) LIMIT 9 ) GROUP BY pbu.BUSINESS_UNIT_NAME, pcc.COST_CENTER, pb.REMAINING_AMOUNT, pb.TOTAL_AMOUNT";
		final Query query = getEntityManager().createNativeQuery(hql);
		query.setParameter("buyerId", SecurityLibrary.getLoggedInUser().getBuyer().getId());

		// query.setParameter("startDate", startDate);
		// query.setParameter("endDate", endDate);

		if (businessUnit != null && businessUnit.length() > 0) {
			query.setParameter("businessUnit", businessUnit);
		}
		hql += "AND pp.COST_CENTER_ID  IS NOT NULL ";
		if (costCenter != null && costCenter.length() > 0) {
			query.setParameter("costCenter", costCenter);
		}

		List<Object[]> records = query.getResultList();
		List<SpentAnalysisPojoForBudget> dataList = new ArrayList<>();
		for (Object[] result : records) {
			SpentAnalysisPojoForBudget data = new SpentAnalysisPojoForBudget();
			data.setBusinessUnit((String) result[0]);
			data.setCostCenter((String) result[1]);
			data.setValue(((Number) result[2]).intValue());
			data.setTotabBudgetAmount(((Number) result[3]).intValue());
			dataList.add(data);
		}
		return dataList;
	}

	@Override
	public void transferOwnership(String fromUserId, String toUserId) {


		User sourceUser = getEntityManager().find(User.class, fromUserId);
		User targetUser = getEntityManager().find(User.class, toUserId);

		Query query = getEntityManager().createNativeQuery("UPDATE PROC_BUDGET SET CREATED_BY_ID = :toUserId where CREATED_BY_ID =:fromUserId");
		query.setParameter("toUserId", toUserId);
		query.setParameter("fromUserId", fromUserId);
		int recordsUpdated = query.executeUpdate();
		LOG.info("Creators transferred: {}", recordsUpdated);

		Query query2 = getEntityManager().createNativeQuery("UPDATE PROC_BUDGET SET BUDGET_OWNER = :toUserId where BUDGET_OWNER =:fromUserId");
		query2.setParameter("toUserId", toUserId);
		query2.setParameter("fromUserId", fromUserId);
		recordsUpdated = query2.executeUpdate();
		LOG.info("Budget Owner transferred: {}", recordsUpdated);

		//transfer ownership of approvals only for RFS with status of DRAFT and PENDING
		Query query5 = getEntityManager().createQuery(
				"UPDATE BudgetApprovalUser sfaur " +
						"SET sfaur.user = :targetUser " +
						"WHERE sfaur.user = :sourceUser "
		);
		query5.setParameter("sourceUser", sourceUser);
		query5.setParameter("targetUser", targetUser);
		recordsUpdated = query5.executeUpdate();
		LOG.info("Approval user transferred: {}", recordsUpdated);


	}
}
