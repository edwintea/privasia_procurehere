/**
 * 
 */
package com.privasia.procurehere.core.dao.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.persistence.Query;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.privasia.procurehere.core.dao.SupplierPerformanceEvaluationDao;
import com.privasia.procurehere.core.dao.UserDao;
import com.privasia.procurehere.core.entity.SupplierPerformanceForm;
import com.privasia.procurehere.core.entity.SupplierPerformanceReminder;
import com.privasia.procurehere.core.entity.User;
import com.privasia.procurehere.core.enums.SupperPerformanceEvaluatorStatus;
import com.privasia.procurehere.core.enums.SupplierPerformanceFormStatus;
import com.privasia.procurehere.core.enums.UserType;
import com.privasia.procurehere.core.pojo.ColumnParameter;
import com.privasia.procurehere.core.pojo.EventPermissions;
import com.privasia.procurehere.core.pojo.OrderParameter;
import com.privasia.procurehere.core.pojo.SearchFilterPerformanceEvaluationPojo;
import com.privasia.procurehere.core.pojo.SupplierPerformanceEvaluationPojo;
import com.privasia.procurehere.core.pojo.TableDataInput;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.StringUtils;

/**
 * @author Jayshree
 */
@Repository
public class SupplierPerformanceEvaluationDaoImpl extends GenericDaoImpl<SupplierPerformanceForm, String> implements SupplierPerformanceEvaluationDao {

	private static final Logger LOG = LogManager.getLogger(SupplierPerformanceEvaluationDaoImpl.class);

	@Autowired
	UserDao userDao;

	@SuppressWarnings("unchecked")
	@Override
	public List<SupplierPerformanceEvaluationPojo> findPendingSPEvaluation(String loggedInUserTenantId, String userId, TableDataInput input) {

		String sql = "SELECT * FROM (";
		sql += getPendingSPEValuationQuery();
		sql += " ) sp where 1 = 1 ";
		// Add on search filter conditions
		for (ColumnParameter cp : input.getColumns()) {
			if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
				sql += " and upper(sp." + cp.getData().replace(".", "") + ") like (:" + cp.getData().replace(".", "") + ") ";
			}
		}

		List<OrderParameter> orderList = input.getOrder();
		if (CollectionUtil.isNotEmpty(orderList)) {
			sql += " order by ";
			for (OrderParameter order : orderList) {
				String orderColumn = input.getColumns().get(order.getColumn()).getData();
				String dir = order.getDir();
				sql += " sp." + orderColumn + " " + dir + ",";
			}
			if (sql.lastIndexOf(",") == sql.length() - 1) {
				sql = sql.substring(0, sql.length() - 1);
			}
		} else {
			sql += " order by sp.createdDate DESC";
		}

		final Query query = getEntityManager().createNativeQuery(sql, "pendingSPResult");
		if (userId != null) {
			query.setParameter("userId", userId);// TBC
		}
		query.setParameter("status", SupplierPerformanceFormStatus.ACTIVE.name());
		query.setParameter("evaluationStatus", Arrays.asList(SupperPerformanceEvaluatorStatus.DRAFT.name(), SupperPerformanceEvaluatorStatus.PENDING.name()));

		for (ColumnParameter cp : input.getColumns()) {
			if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
				query.setParameter(cp.getData().replace(".", ""), "%" + cp.getSearch().getValue().toUpperCase() + "%");
			}
		}

		query.setFirstResult(input.getStart());
		query.setMaxResults(input.getLength());
		List<SupplierPerformanceEvaluationPojo> list = query.getResultList();
		return list;
	}

	@Override
	public long findTotalPendingSPEvaluation(String loggedInUserTenantId, String userId, TableDataInput input) {

		String sql = "select count(sp.id) from (";
		sql += getPendingSPEValuationQuery();
		sql += " ) sp where 1=1 ";
		// Add on search filter conditions
		if (input != null) {
			for (ColumnParameter cp : input.getColumns()) {
				if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
					sql += " and upper(sp." + cp.getData().replace(".", "") + ") like (:" + cp.getData().replace(".", "") + ") ";
				}
			}
		}

		final Query query = getEntityManager().createNativeQuery(sql);
		query.setParameter("userId", userId);
		query.setParameter("status", SupplierPerformanceFormStatus.ACTIVE.name());
		query.setParameter("evaluationStatus", Arrays.asList(SupperPerformanceEvaluatorStatus.DRAFT.name(), SupperPerformanceEvaluatorStatus.PENDING.name()));
		if (input != null) {
			for (ColumnParameter cp : input.getColumns()) {
				if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
					query.setParameter(cp.getData().replace(".", ""), "%" + cp.getSearch().getValue().toUpperCase() + "%");
				}
			}
		}
		return ((Number) query.getSingleResult()).longValue();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<SupplierPerformanceEvaluationPojo> findSPEvaluationPendingApprovals(String loggedInUserTenantId, String userId, TableDataInput input) {
		String sql = "SELECT * FROM (";
		sql += getPendingApprovalSPEValuationQuery();
		sql += " ) sp where 1 = 1 ";
		// Add on search filter conditions
		for (ColumnParameter cp : input.getColumns()) {
			if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
				sql += " and upper(sp." + cp.getData().replace(".", "") + ") like (:" + cp.getData().replace(".", "") + ") ";
			}
		}

		List<OrderParameter> orderList = input.getOrder();
		if (CollectionUtil.isNotEmpty(orderList)) {
			sql += " order by ";
			for (OrderParameter order : orderList) {
				String orderColumn = input.getColumns().get(order.getColumn()).getData();
				String dir = order.getDir();
				sql += " sp." + orderColumn + " " + dir + ",";
			}
			if (sql.lastIndexOf(",") == sql.length() - 1) {
				sql = sql.substring(0, sql.length() - 1);
			}
		} else {
			sql += " order by sp.createdDate DESC";
		}

		final Query query = getEntityManager().createNativeQuery(sql, "pendingSPAppResult");
		if (userId != null) {
			query.setParameter("userId", userId);// TBC
		}
		query.setParameter("status", SupplierPerformanceFormStatus.ACTIVE.name());

		for (ColumnParameter cp : input.getColumns()) {
			if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
				query.setParameter(cp.getData().replace(".", ""), "%" + cp.getSearch().getValue().toUpperCase() + "%");
			}
		}

		query.setFirstResult(input.getStart());
		query.setMaxResults(input.getLength());
		List<SupplierPerformanceEvaluationPojo> list = query.getResultList();
		return list;
	}

	@Override
	public long findCountOfSPEvaluationPendingApprovals(String loggedInUserTenantId, String userId, TableDataInput input) {
		String sql = "select count(sp.id) from (";
		sql += getPendingApprovalSPEValuationQuery();
		sql += " ) sp where 1=1 ";
		// Add on search filter conditions
		if (input != null) {
			for (ColumnParameter cp : input.getColumns()) {
				if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
					sql += " and upper(sp." + cp.getData().replace(".", "") + ") like (:" + cp.getData().replace(".", "") + ") ";
				}
			}
		}

		final Query query = getEntityManager().createNativeQuery(sql);
		query.setParameter("userId", userId);
		query.setParameter("status", SupplierPerformanceFormStatus.ACTIVE.name());
		if (input != null) {
			for (ColumnParameter cp : input.getColumns()) {
				if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
					query.setParameter(cp.getData().replace(".", ""), "%" + cp.getSearch().getValue().toUpperCase() + "%");
				}
			}
		}
		return ((Number) query.getSingleResult()).longValue();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<SupplierPerformanceEvaluationPojo> findSupplierPerformanceEvaluation(String loggedInUserId, TableDataInput input, Date startDate, Date endDate, String tenantId) {
		final Query query = constructSPEvaluationForTenantQuery(loggedInUserId, input, false, startDate, endDate, tenantId);
		// LOG.info("query size "+query.getResultList().size());
		query.setFirstResult(input.getStart());
		query.setMaxResults(input.getLength());
		return query.getResultList();
	}

	@Override
	public List<SupplierPerformanceEvaluationPojo> findSupplierPerformanceEvaluationBizUnit(String loggedInUserId, TableDataInput input, Date startDate, Date endDate, String tenantId,List<String> businessUnitIds) {
		//final Query query = constructSPEvaluationForTenantQuery(loggedInUserId, input, false, startDate, endDate, tenantId);
		final Query query = constructSPEvaluationForTenantBizUnit(loggedInUserId, input, false, startDate, endDate, tenantId,businessUnitIds);
		 LOG.info(">>>>>> HELLO query size "+query.getResultList().size());
		query.setFirstResult(input.getStart());
		query.setMaxResults(input.getLength());
		return query.getResultList();
	}
	private Query constructSPEvaluationForTenantQuery(String userId, TableDataInput input, boolean isCount, Date startDate, Date endDate, String tenantId) {
		String hql = "";

		if (isCount) {
			hql += "select count(*) from ( ";
		}

		// If this is not a count query, only then add the join fetch. Count query does not require its
		if (!isCount) {
			hql += " select * from ( ";
		}
		hql += getSupplierPerformanceEvaluationQuery() + " ) sp where 1=1 ";
		if (StringUtils.checkString(userId).length() > 0) {
			hql += "and sp.formOwnerId = :userId ";
		}

		if (startDate != null && endDate != null) {
			hql += " and sp.createdDate between :startDate and :endDate ";
		}

		// Add on search filter conditions
		for (ColumnParameter cp : input.getColumns()) {
			if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
				if (cp.getData().equals("formStatus")) {
					hql += " and sp.formStatus = (:" + cp.getData() + ")";
				} else {
					hql += " and upper(sp." + cp.getData() + ") like (:" + cp.getData().replace(".", "") + ")";
				}
			}
		}

		// If it is not a count query then add order by clause
		if (!isCount) {
			List<OrderParameter> orderList = input.getOrder();
			if (CollectionUtil.isNotEmpty(orderList)) {
				hql += " order by ";
				for (OrderParameter order : orderList) {
					String orderColumn = input.getColumns().get(order.getColumn()).getData();
					String dir = order.getDir();
					hql += " sp." + orderColumn + " " + dir + ",";
				}
				if (hql.lastIndexOf(",") == hql.length() - 1) {
					hql = hql.substring(0, hql.length() - 1);
				}
			} else {
				hql += " order by sp.createdDate desc ";
			}
		}

		LOG.debug("HQL : " + hql);

		Query query = null;
		if (!isCount) {
			query = getEntityManager().createNativeQuery(hql.toString(), "supplierPerformanceList");
		} else {
			query = getEntityManager().createNativeQuery(hql.toString());
		}
		if (StringUtils.checkString(userId).length() > 0) {
			query.setParameter("userId", userId);
		}
		query.setParameter("buyerId", tenantId);
		if (startDate != null && endDate != null) {
			query.setParameter("startDate", startDate);
			query.setParameter("endDate", endDate);
		}
		// Apply search filter values
		for (ColumnParameter cp : input.getColumns()) {
			if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
				if (cp.getData().equals("formStatus")) {
					query.setParameter("formStatus", SupplierPerformanceFormStatus.valueOf(cp.getSearch().getValue()).name());
				} else {
					query.setParameter(cp.getData().replace(".", ""), "%" + cp.getSearch().getValue().toUpperCase() + "%");
				}
			}
		}
		return query;

	}

	private Query constructSPEvaluationForTenantBizUnit(String userId, TableDataInput input, boolean isCount, Date startDate, Date endDate, String tenantId, List<String> businessUnitIds) {
		String hql = "";

		if (isCount) {
			hql += "select count(*) from ( ";
		}

		// If this is not a count query, only then add the join fetch. Count query does not require its
		if (!isCount) {
			hql += " select * from ( ";
		}
		hql += getSupplierPerformanceEvaluationQuery() + " ) sp where 1=1 ";

		//4105
		// Combine business unit and form owner logic
		if ((businessUnitIds != null && !businessUnitIds.isEmpty()) || StringUtils.checkString(userId).length() > 0) {
			hql += " AND ( ";
			// Business unit filter
			if (businessUnitIds != null && !businessUnitIds.isEmpty()) {
				hql += " sp.BUSINESS_UNIT_ID IN (:businessUnitIds) ";
			}
			// Form owner filter
			if (StringUtils.checkString(userId).length() > 0) {
				if (businessUnitIds != null && !businessUnitIds.isEmpty()) {
					hql += " OR "; // Combine with "OR"
				}
				hql += " sp.formOwnerId = :userId ";
			}
			hql += " ) ";
		}

		if (startDate != null && endDate != null) {
			hql += " and sp.createdDate between :startDate and :endDate ";
		}

		// Add on search filter conditions
		for (ColumnParameter cp : input.getColumns()) {
			if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
				if (cp.getData().equals("formStatus")) {
					hql += " and sp.formStatus = (:" + cp.getData() + ")";
				} else {
					hql += " and upper(sp." + cp.getData() + ") like (:" + cp.getData().replace(".", "") + ")";
				}
			}
		}

		// If it is not a count query then add order by clause
		if (!isCount) {
			List<OrderParameter> orderList = input.getOrder();
			if (CollectionUtil.isNotEmpty(orderList)) {
				hql += " order by ";
				for (OrderParameter order : orderList) {
					String orderColumn = input.getColumns().get(order.getColumn()).getData();
					String dir = order.getDir();
					hql += " sp." + orderColumn + " " + dir + ",";
				}
				if (hql.lastIndexOf(",") == hql.length() - 1) {
					hql = hql.substring(0, hql.length() - 1);
				}
			} else {
				hql += " order by sp.createdDate desc ";
			}
		}

		LOG.debug("HQL : " + hql);

		Query query = null;
		if (!isCount) {
			query = getEntityManager().createNativeQuery(hql.toString(), "supplierPerformanceList");
		} else {
			query = getEntityManager().createNativeQuery(hql.toString());
		}

		if (businessUnitIds != null && !businessUnitIds.isEmpty()) {
			query.setParameter("businessUnitIds", businessUnitIds);
		}
		if (StringUtils.checkString(userId).length() > 0) {
			query.setParameter("userId", userId);
		}
		if (StringUtils.checkString(tenantId).length() > 0) {
			query.setParameter("buyerId", tenantId);
		}

		if (startDate != null && endDate != null) {
			query.setParameter("startDate", startDate);
			query.setParameter("endDate", endDate);
		}
		// Apply search filter values
		for (ColumnParameter cp : input.getColumns()) {
			if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
				if (cp.getData().equals("formStatus")) {
					query.setParameter("formStatus", SupplierPerformanceFormStatus.valueOf(cp.getSearch().getValue()).name());
				} else {
					query.setParameter(cp.getData().replace(".", ""), "%" + cp.getSearch().getValue().toUpperCase() + "%");
				}
			}
		}
		return query;

	}

	@Override
	public long findTotalFilteredSPEvaluation(String loggedInUserId, TableDataInput input, Date startDate, Date endDate, String tenantId) {
		final Query query = constructSPEvaluationForTenantQuery(loggedInUserId, input, true, startDate, endDate, tenantId);
		return ((Number) query.getSingleResult()).longValue();
	}

	@Override
	public long findTotalFilteredSPEvaluationBizUnit(String loggedInUserId, TableDataInput input, Date startDate, Date endDate, String tenantId,List<String> businessUnitIds) {
		//final Query query = constructSPEvaluationForTenantQuery(loggedInUserId, input, true, startDate, endDate, tenantId);
		final Query query = constructSPEvaluationForTenantBizUnit(loggedInUserId, input, true, startDate, endDate, tenantId,businessUnitIds);
		return ((Number) query.getSingleResult()).longValue();
	}

	@Override
	public long findTotalActiveSPEvaluation(String userId) {
		StringBuilder hql = new StringBuilder("select count (sp) from SupplierPerformanceForm sp left outer join sp.formOwner owner where owner.id = :userId ");
		final Query query = getEntityManager().createQuery(hql.toString());
		// query.setParameter("status", SupplierPerformanceFormStatus.DRAFT);sp.formStatus =:status and
		query.setParameter("userId", userId);
		return ((Number) query.getSingleResult()).longValue();
	}

	@Override
	public long findTotalActiveSPEvaluationBizUnit(String userId,List<String> businessUnitIds) {
		LOG.info("businessUnitIds >>>>>>>>>>>>>>>>>>>> "+businessUnitIds);
		StringBuilder hql = new StringBuilder("select count (sp) from SupplierPerformanceForm sp left outer join sp.formOwner owner where sp.businessUnit.id in (:businessUnitIds) ");
		final Query query = getEntityManager().createQuery(hql.toString());
		// query.setParameter("status", SupplierPerformanceFormStatus.DRAFT);sp.formStatus =:status and
		//query.setParameter("userId", userId);
		query.setParameter("businessUnitIds", businessUnitIds);
		return ((Number) query.getSingleResult()).longValue();
	}

	@SuppressWarnings("unchecked")
	@Override
	public SupplierPerformanceForm getSupplierPerformanceFormDetailsByFormId(String formId) {
		LOG.debug("FormId :" + formId);
		StringBuilder hsql = new StringBuilder("from SupplierPerformanceForm p left outer join fetch p.procurementCategory pc left outer join fetch p.awardedSupplier sp left outer join fetch p.businessUnit bu left outer join p.createdBy cb left outer join fetch p.formOwner fo where p.id= :id");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", formId);
		List<SupplierPerformanceForm> uList = query.getResultList();
		if (CollectionUtil.isNotEmpty(uList)) {
			return uList.get(0);
		} else {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<SupplierPerformanceReminder> getAllSpFormRemindersByFormId(String formId) {
		LOG.info("Getting supplier performance reminder list");
		final Query query = getEntityManager().createQuery("from SupplierPerformanceReminder r where r.form.id = :formId order by r.reminderDate");
		query.setParameter("formId", formId);
		List<SupplierPerformanceReminder> reminderList = query.getResultList();
		if (CollectionUtil.isNotEmpty(reminderList)) {
			return reminderList;
		} else {
			return null;
		}
	}

	@Override
	public long findTotalPerformanceEvaluationCountForCsv(String userId) {
		String hql = "select count (sp) from SupplierPerformanceForm sp left outer join sp.formOwner owner where owner.id = :userId ";

		final Query query = getEntityManager().createQuery(hql.toString());
		query.setParameter("userId", userId);
		return ((Number) query.getSingleResult()).longValue();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<SupplierPerformanceForm> getAllPerformanceEvaluationForCsv(String userId, Date startDate, Date endDate, String[] formIdArr, boolean select_all, SearchFilterPerformanceEvaluationPojo searchFilterPerformanceEvaluationPojo, String tenantId) {
		String sql = "select sp from SupplierPerformanceForm sp left outer join sp.procurementCategory pc left outer join sp.awardedSupplier sup left outer join sp.businessUnit bu left outer join sp.scoreRating left outer join sp.formOwner owner left outer join sp.buyer buyer where 1=1 ";

		if (!(select_all)) {
			if (formIdArr != null && formIdArr.length > 0) {
				sql += " and sp.id in (:formIdArr)";
			}
		}

		if (searchFilterPerformanceEvaluationPojo != null) {
			if (StringUtils.checkString(searchFilterPerformanceEvaluationPojo.getFormid()).length() > 0) {
				sql += " and upper(sp.formId) like :formId";
			}
			if (StringUtils.checkString(searchFilterPerformanceEvaluationPojo.getFormname()).length() > 0) {
				sql += " and upper(sp.formName) like :formName";
			}
			if (StringUtils.checkString(searchFilterPerformanceEvaluationPojo.getReferencenumber()).length() > 0) {
				sql += " and upper(sp.referenceNumber) like :referenceNumber";
			}
			if (StringUtils.checkString(searchFilterPerformanceEvaluationPojo.getReferencename()).length() > 0) {
				sql += " and upper(sp.referenceName) like :referenceName";
			}
			if (StringUtils.checkString(searchFilterPerformanceEvaluationPojo.getFormcreator()).length() > 0) {
				sql += " and upper(owner.name) like :formOwner";
			}
			if (StringUtils.checkString(searchFilterPerformanceEvaluationPojo.getProcurementcategory()).length() > 0) {
				sql += " and upper(pc.procurementCategories) like :category";
			}
			if (StringUtils.checkString(searchFilterPerformanceEvaluationPojo.getBusinessunit()).length() > 0) {
				sql += " and upper(bu.unitName) like :businessunit";
			}
			if (StringUtils.checkString(searchFilterPerformanceEvaluationPojo.getSuppliername()).length() > 0) {
				sql += " and upper(sup.companyName) like :suppliername";
			}
			if (StringUtils.checkString(searchFilterPerformanceEvaluationPojo.getStatus()).length() > 0) {
				sql += " and upper(sp.formStatus) like :formStatus";
			}
		}

		if (startDate != null && endDate != null) {
			sql += " and sp.createdDate between :startDate and :endDate ";
		}

		sql += " and buyer.id = :tenantId ";

		if (StringUtils.checkString(userId).length() > 0) {
			sql += " and owner.id = :userId ";
		}
		sql += " order by sp.createdDate desc ";

		LOG.info("SQL " + sql);
		final Query query = getEntityManager().createQuery(sql);
		if (StringUtils.checkString(userId).length() > 0) {
			query.setParameter("userId", userId);
		}
		query.setParameter("tenantId", tenantId);
		if (!(select_all)) {
			if (formIdArr != null && formIdArr.length > 0) {
				query.setParameter("formIdArr", Arrays.asList(formIdArr));
			}
		}

		if (searchFilterPerformanceEvaluationPojo != null) {
			if (StringUtils.checkString(searchFilterPerformanceEvaluationPojo.getFormid()).length() > 0) {
				query.setParameter("formId", "%" + searchFilterPerformanceEvaluationPojo.getFormid().toUpperCase() + "%");
			}
			if (StringUtils.checkString(searchFilterPerformanceEvaluationPojo.getFormname()).length() > 0) {
				query.setParameter("formName", "%" + searchFilterPerformanceEvaluationPojo.getFormname().toUpperCase() + "%");
			}
			if (StringUtils.checkString(searchFilterPerformanceEvaluationPojo.getReferencenumber()).length() > 0) {
				query.setParameter("referenceNumber", "%" + searchFilterPerformanceEvaluationPojo.getReferencenumber().toUpperCase() + "%");
			}
			if (StringUtils.checkString(searchFilterPerformanceEvaluationPojo.getReferencename()).length() > 0) {
				query.setParameter("referenceName", "%" + searchFilterPerformanceEvaluationPojo.getReferencename().toUpperCase() + "%");
			}
			if (StringUtils.checkString(searchFilterPerformanceEvaluationPojo.getFormcreator()).length() > 0) {
				query.setParameter("formOwner", "%" + searchFilterPerformanceEvaluationPojo.getFormcreator().toUpperCase() + "%");
			}
			if (StringUtils.checkString(searchFilterPerformanceEvaluationPojo.getProcurementcategory()).length() > 0) {
				query.setParameter("category", "%" + searchFilterPerformanceEvaluationPojo.getProcurementcategory().toUpperCase() + "%");
			}
			if (StringUtils.checkString(searchFilterPerformanceEvaluationPojo.getBusinessunit()).length() > 0) {
				query.setParameter("businessunit", "%" + searchFilterPerformanceEvaluationPojo.getBusinessunit().toUpperCase() + "%");
			}
			if (StringUtils.checkString(searchFilterPerformanceEvaluationPojo.getSuppliername()).length() > 0) {
				query.setParameter("suppliername", "%" + searchFilterPerformanceEvaluationPojo.getSuppliername().toUpperCase() + "%");
			}
			if (StringUtils.checkString(searchFilterPerformanceEvaluationPojo.getStatus()).length() > 0) {
				query.setParameter("formStatus", SupplierPerformanceFormStatus.valueOf(searchFilterPerformanceEvaluationPojo.getStatus()));
			}
		}

		if (startDate != null && endDate != null) {
			query.setParameter("startDate", startDate);
			query.setParameter("endDate", endDate);
		}

		return query.getResultList();
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<SupplierPerformanceReminder> getSupplierReminderForNotification() {
		StringBuilder hsql = new StringBuilder("from SupplierPerformanceReminder r inner join fetch r.form as e where r.reminderSent = false and r.reminderDate <= :now and e.formStatus in (:status) order by r.reminderDate");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("now", new Date());
		query.setParameter("status", SupplierPerformanceFormStatus.ACTIVE);
		List<SupplierPerformanceReminder> returnList = (List<SupplierPerformanceReminder>) query.getResultList();
		return returnList;
	}

	@Override
	public EventPermissions getUserPemissionsForForm(String id, String formId) {
		LOG.info("userId :" + id + " formId: " + formId);
		EventPermissions permissions = new EventPermissions();

		User loggedInUser = userDao.findById(id);
		if (UserType.APPROVER_USER == loggedInUser.getUserType()) {
			permissions.setApproverUser(true);
		}

		return null;
	}

	@Override
	public void suspendForm(String formId) {
		StringBuilder hsql = new StringBuilder("update SupplierPerformanceForm  set oldFormStatus = formStatus, formStatus = :status where id = :formId ");
		LOG.info("HQL : " + hsql.toString());
		final Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("status", SupplierPerformanceFormStatus.SUSPENDED);
		query.setParameter("formId", formId);
		query.executeUpdate();

		hsql = new StringBuilder("update SupplierPerformanceEvaluatorUser sp set sp.oldEvaluationStatus = sp.evaluationStatus, sp.evaluationStatus = :status where sp.form.id = :formId ");
		LOG.info("HQL : " + hsql.toString());
		final Query query1 = getEntityManager().createQuery(hsql.toString());
		query1.setParameter("status", SupperPerformanceEvaluatorStatus.SUSPENDED);
		query1.setParameter("formId", formId);
		query1.executeUpdate();
	}

	@SuppressWarnings("unchecked")
	@Override
	public SupplierPerformanceForm getFormDetailsBySupplierId(String supplierId) {
		final Query query = getEntityManager().createQuery("select sp from SupplierPerformanceForm sp left outer join sp.scoreRating sr where sp.awardedSupplier.id =:id");
		query.setParameter("id", supplierId);
		List<SupplierPerformanceForm> uList = query.getResultList();
		if (CollectionUtil.isNotEmpty(uList)) {
			return uList.get(0);
		} else {
			return null;
		}
	}

	@Override
	public BigDecimal getSumOfOverallScoreOfSupplierByBuyerId(String tenantId, String supplierId, Date startDate, Date endDate) {
		String hql = "select sum(spf.overallScore) from SupplierPerformanceForm spf where spf.buyer.id = :tenantId and spf.awardedSupplier.id = :supplierId and spf.formStatus = :formStatus and spf.overallScore > 0 ";
		if (startDate != null && endDate != null) {
			hql += " and spf.concludeDate between :startDate and :endDate ";
		}

		final Query query = getEntityManager().createQuery(hql.toString());
		query.setParameter("tenantId", tenantId);
		query.setParameter("supplierId", supplierId);
		query.setParameter("formStatus", SupplierPerformanceFormStatus.CONCLUDED);
		if (startDate != null && endDate != null) {
			query.setParameter("startDate", startDate);
			query.setParameter("endDate", endDate);
		}
		return (BigDecimal) query.getSingleResult();
	}

	@Override
	public Long getCountOfFormsBySupplierByBuyerId(String tenantId, String supplierId, Date startDate, Date endDate) {
		String hql = "select count(*) from SupplierPerformanceForm spf where spf.buyer.id = :tenantId and spf.awardedSupplier.id = :supplierId and spf.formStatus = :formStatus and spf.overallScore > 0";
		if (startDate != null && endDate != null) {
			hql += " and spf.concludeDate between :startDate and :endDate ";
		}

		final Query query = getEntityManager().createQuery(hql.toString());
		query.setParameter("tenantId", tenantId);
		query.setParameter("supplierId", supplierId);
		query.setParameter("formStatus", SupplierPerformanceFormStatus.CONCLUDED);
		if (startDate != null && endDate != null) {
			query.setParameter("startDate", startDate);
			query.setParameter("endDate", endDate);
		}
		return (Long) query.getSingleResult();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<SupplierPerformanceEvaluationPojo> getSumOfOverallScoreOfSupplierByBuyerIdAndBUnit(String buyerId, String supplierId, Date startDate, Date endDate) {
		try {
			String sql = "";

			sql = "select bu.displayName, sum(spf.overallScore), count(*) from SupplierPerformanceForm spf left outer join spf.businessUnit bu where spf.awardedSupplier.id = :supplierId and spf.formStatus = :formStatus ";

			if (startDate != null && endDate != null) {
				sql += " and spf.concludeDate between :startDate and :endDate";
			}
			if (StringUtils.checkString(buyerId).length() > 0) {
				sql += " and spf.buyer.id = :buyerId ";
			}
			sql += " and bu.displayName is not null and spf.overallScore > 0 group by bu.displayName order by sum(spf.overallScore) desc";

			// LOG.info("Query >>>>>>>>>>> " + sql);
			final Query query = getEntityManager().createQuery(sql);

			if (StringUtils.checkString(buyerId).length() > 0) {
				query.setParameter("buyerId", buyerId);
			}
			query.setParameter("supplierId", supplierId);
			query.setParameter("formStatus", SupplierPerformanceFormStatus.CONCLUDED);
			if (startDate != null && endDate != null) {
				query.setParameter("startDate", startDate);
				query.setParameter("endDate", endDate);
			}
			List<SupplierPerformanceEvaluationPojo> dataList = new ArrayList<>();
			List<Object[]> records = query.getResultList();

			for (Object[] result : records) {
				SupplierPerformanceEvaluationPojo data = new SupplierPerformanceEvaluationPojo();
				data.setUnitName((String) result[0]);
				BigDecimal val = new BigDecimal((Long) result[2]);
				data.setOverallScore(((BigDecimal) result[1]).divide(val, 0, RoundingMode.HALF_UP));
				dataList.add(data);
			}
			return dataList;
		} catch (Exception e) {
			LOG.info("Error " + e.getMessage(), e);
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<SupplierPerformanceEvaluationPojo> getOverallScoreOfSupplierByBuyerIdAndSpFormAndBUnit(String tenantId, String supplierId, Date startDate, Date endDate, String businessUnit) {
		String sql = "";

		sql = "select NEW com.privasia.procurehere.core.pojo.SupplierPerformanceEvaluationPojo(spf.id, spf.formId, spf.referenceNumber, spf.overallScore, pc.procurementCategories, bu.displayName, sr.rating, sr.description) from SupplierPerformanceForm spf ";
		sql += " left outer join spf.businessUnit bu";
		sql += " left outer join spf.procurementCategory pc";
		sql += " left outer join spf.scoreRating sr";
		sql += " where spf.buyer.id = :tenantId and spf.awardedSupplier.id = :supplierId and spf.formStatus = :formStatus and spf.overallScore > 0 ";

		if (startDate != null && endDate != null) {
			sql += " and spf.concludeDate between :startDate and :endDate";
		}

		if (StringUtils.checkString(businessUnit).length() > 0) {
			sql += " and bu.id = :businessUnit ";
		}

		sql += " order by spf.formId desc";

		// LOG.info("Query >>>>>>>>>>> " + sql);
		final Query query = getEntityManager().createQuery(sql);

		query.setParameter("tenantId", tenantId);
		query.setParameter("supplierId", supplierId);
		query.setParameter("formStatus", SupplierPerformanceFormStatus.CONCLUDED);
		if (startDate != null && endDate != null) {
			query.setParameter("startDate", startDate);
			query.setParameter("endDate", endDate);
		}
		if (StringUtils.checkString(businessUnit).length() > 0) {
			query.setParameter("businessUnit", businessUnit);
		}
		List<SupplierPerformanceEvaluationPojo> dataList = query.getResultList();

		return dataList;
	}

	@SuppressWarnings("unchecked")
	@Override
	public SupplierPerformanceEvaluationPojo getOverallScoreOfSupplierByBuyerId(Date startDate, Date endDate, String buyerId, String supplierId) {
		String hql = "select avg(spf.overallScore), spf.buyer.companyName from SupplierPerformanceForm spf where spf.awardedSupplier.id = :supplierId and spf.formStatus = :formStatus ";
		if (startDate != null && endDate != null) {
			hql += " and spf.concludeDate between :startDate and :endDate and spf.overallScore > 0";
		}
		if (StringUtils.checkString(buyerId).length() > 0) {
			hql += " and spf.buyer.id = :buyerId ";
		}
		hql += " group by spf.buyer.companyName order by avg(spf.overallScore) desc";

		final Query query = getEntityManager().createQuery(hql.toString());
		if (StringUtils.checkString(buyerId).length() > 0) {
			query.setParameter("buyerId", buyerId);
		}
		query.setParameter("supplierId", supplierId);
		query.setParameter("formStatus", SupplierPerformanceFormStatus.CONCLUDED);
		if (startDate != null && endDate != null) {
			query.setParameter("startDate", startDate);
			query.setParameter("endDate", endDate);
		}
		List<Object[]> records = query.getResultList();

		SupplierPerformanceEvaluationPojo data = new SupplierPerformanceEvaluationPojo();
		for (Object[] result : records) {
			data.setOverallScore(new BigDecimal((double) result[0]));
			if (data.getOverallScore() != null) {
				data.setOverallScore(data.getOverallScore().setScale(0, RoundingMode.HALF_UP));
			}
			data.setBuyerName((String) result[1]);
		}
		return data;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<SupplierPerformanceEvaluationPojo> getOverallScoreOfSupplierByBUnitAndEvent(Date start, Date end, String unitId, String supplierId, String buyerId) {
		try {
			String sql = "";

			sql += " SELECT a.*, ";
			sql += "(SELECT sr.RATING FROM PROC_SCORE_RATING sr WHERE a.avg BETWEEN sr.MINIMUM_SCORE AND sr.MAXIMUM_SCORE AND sr.STATUS = 'ACTIVE' AND sr.TENANT_ID = :buyerId LIMIT 1 ) AS rating, ";
			sql += "(SELECT sr.DESCRIPTION FROM PROC_SCORE_RATING sr WHERE a.avg BETWEEN sr.MINIMUM_SCORE AND sr.MAXIMUM_SCORE AND sr.STATUS = 'ACTIVE' AND sr.TENANT_ID = :buyerId LIMIT 1 ) AS rating_desc ";
			sql += "FROM (SELECT p.ID, p.FORM_ID, p.BUSINESS_UNIT_ID, p.REFERENCE_NUMBER, p.EVENT_TYPE, pbu.DISPLAY_NAME, AVG(p.OVERALL_SCORE) AS avg ";
			sql += "FROM PROC_SUPPLIER_PERFORMANCE_FORM p ";
			sql += "JOIN PROC_BUSINESS_UNIT pbu ON p.BUSINESS_UNIT_ID  = pbu.ID ";
			sql += "WHERE p.AWARDED_SUPPLIER_ID = :supplierId ";

			if (StringUtils.checkString(unitId).length() > 0) {
				sql += " AND p.BUSINESS_UNIT_ID = :unitId ";
			}

			if (StringUtils.checkString(buyerId).length() > 0) {
				sql += " AND p.TENANT_ID = :buyerId ";
			}

			sql += " AND p.FORM_STATUS = 'CONCLUDED' AND p.OVERALL_SCORE > 0 ";

			if (start != null && end != null) {
				sql += "AND p.CONCLUDE_DATE between :startDate and :endDate ";
			}
			sql += "GROUP BY p.REFERENCE_NUMBER, p.BUSINESS_UNIT_ID, p.EVENT_TYPE, pbu.DISPLAY_NAME, p.ID, p.FORM_ID";
			sql += ") a ";

			// LOG.info("Query >>>>>>>>>>> " + sql);
			final Query query = getEntityManager().createNativeQuery(sql);

			if (StringUtils.checkString(unitId).length() > 0) {
				query.setParameter("unitId", unitId);
			}
			query.setParameter("buyerId", buyerId);
			query.setParameter("supplierId", supplierId);
			if (start != null && end != null) {
				query.setParameter("startDate", start);
				query.setParameter("endDate", end);
			}

			List<SupplierPerformanceEvaluationPojo> dataList = new ArrayList<>();
			List<Object[]> records = query.getResultList();

			for (Object[] result : records) {
				SupplierPerformanceEvaluationPojo data = new SupplierPerformanceEvaluationPojo();
				data.setId((String) result[0]);
				data.setFormId((String) result[1]);
				data.setUnitId((String) result[2]);
				data.setEventId((String) result[3]);
				data.setEventType((String) result[4]);
				data.setUnitName((String) result[5]);
				data.setOverallScore(((BigDecimal) result[6]).setScale(0, RoundingMode.HALF_UP));
				data.setRating(((BigDecimal) result[7]).toString());
				data.setRatingDescription((String) result[8]);
				dataList.add(data);
			}

			return dataList;
		} catch (Exception e) {
			LOG.info("Error " + e.getMessage(), e);
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<SupplierPerformanceForm> getEventIdListForSupplierId(String supplierId, String buyerId, Date startDate, Date endDate) {
		String hql = "select new com.privasia.procurehere.core.entity.SupplierPerformanceForm(v.id, v.eventId, v.referenceNumber) from SupplierPerformanceForm v where v.buyer.id = :tenantId and v.awardedSupplier.id = :supplierId and v.formStatus = :formStatus ";

		if (startDate != null && endDate != null) {
			hql += " and v.concludeDate between :startDate and :endDate";
		}

		final Query query = getEntityManager().createQuery(hql);
		query.setParameter("formStatus", SupplierPerformanceFormStatus.CONCLUDED);
		query.setParameter("supplierId", supplierId);
		query.setParameter("tenantId", buyerId);
		if (startDate != null && endDate != null) {
			query.setParameter("startDate", startDate);
			query.setParameter("endDate", endDate);
		}
		List<SupplierPerformanceForm> list = query.getResultList();
		return list;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<SupplierPerformanceEvaluationPojo> getOverallScoreByCriteriaAndEventId(Date start, Date end, String eventId, String buyerId, String supplierId) {
		try {
			String sql = "";
			sql += "SELECT a.*, ";
			sql += "(SELECT sr.RATING FROM PROC_SCORE_RATING sr WHERE a.overall BETWEEN sr.MINIMUM_SCORE AND sr.MAXIMUM_SCORE AND sr.TENANT_ID = :buyerId LIMIT 1 ) AS rating, ";
			sql += "(SELECT sr.DESCRIPTION FROM PROC_SCORE_RATING sr WHERE a.overall BETWEEN sr.MINIMUM_SCORE AND sr.MAXIMUM_SCORE AND sr.TENANT_ID = :buyerId LIMIT 1 ) AS rating_desc ";
			sql += "FROM ( ";
			sql += "SELECT fc.CRITERIA_NAME AS crt, AVG(fc.TOTAL_SCORE) AS total, avg(pf.OVERALL_SCORE) AS overall ";
			sql += "FROM PROC_SUPPLIER_PERFORMANCE_FORM pf ";
			sql += "JOIN PROC_SUP_PER_FORM_CRITERIA fc ON fc.FORM_ID = pf.ID ";
			sql += "WHERE pf.AWARDED_SUPPLIER_ID = :supplierId ";
			sql += "AND pf.TENANT_ID = :buyerId ";
			sql += "AND pf.FORM_STATUS = 'CONCLUDED' AND fc.CRITERIA_ORDER = '0' ";

			if (StringUtils.checkString(eventId).length() > 0) {
				sql += "AND pf.EVENT_ID = :eventId ";
			}

			if (start != null && end != null) {
				sql += "AND pf.CONCLUDE_DATE between :startDate and :endDate ";
			}

			sql += "GROUP BY fc.CRITERIA_NAME ORDER BY fc.CRITERIA_NAME ";
			sql += ")a ";

			// LOG.info("Query >>>>>>>>>>> " + sql);
			final Query query = getEntityManager().createNativeQuery(sql);

			if (StringUtils.checkString(eventId).length() > 0) {
				query.setParameter("eventId", eventId);
			}
			query.setParameter("buyerId", buyerId);
			query.setParameter("supplierId", supplierId);
			if (start != null && end != null) {
				query.setParameter("startDate", start);
				query.setParameter("endDate", end);
			}

			List<SupplierPerformanceEvaluationPojo> dataList = new ArrayList<>();
			List<Object[]> records = query.getResultList();

			for (Object[] result : records) {
				SupplierPerformanceEvaluationPojo data = new SupplierPerformanceEvaluationPojo();
				data.setCriteriaName((String) result[0]);
				data.setOverallScore(((BigDecimal) result[1]).setScale(0, RoundingMode.HALF_UP));
				data.setTotalOverallScore(((BigDecimal) result[2]).setScale(0, RoundingMode.HALF_UP));
				data.setRating(((BigDecimal) result[3]).toString());
				data.setRatingDescription((String) result[4]);
				dataList.add(data);
			}

			return dataList;
		} catch (Exception e) {
			LOG.info("Error " + e.getMessage(), e);
			return null;
		}
	}

}
