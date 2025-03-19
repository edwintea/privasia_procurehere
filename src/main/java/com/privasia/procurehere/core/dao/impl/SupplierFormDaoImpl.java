package com.privasia.procurehere.core.dao.impl;

import java.util.List;

import javax.persistence.Query;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Repository;

import com.privasia.procurehere.core.dao.SupplierFormDao;
import com.privasia.procurehere.core.entity.SupplierForm;
import com.privasia.procurehere.core.entity.User;
import com.privasia.procurehere.core.enums.EventStatus;
import com.privasia.procurehere.core.enums.SupplierFormsStatus;
import com.privasia.procurehere.core.pojo.ColumnParameter;
import com.privasia.procurehere.core.pojo.OrderParameter;
import com.privasia.procurehere.core.pojo.SupplierFormPojo;
import com.privasia.procurehere.core.pojo.TableDataInput;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.SecurityLibrary;
import com.privasia.procurehere.core.utils.StringUtils;

@Repository("supplierFormDao")
public class SupplierFormDaoImpl extends GenericDaoImpl<SupplierForm, String> implements SupplierFormDao {

	protected static final Logger LOG = LogManager.getLogger(SupplierFormDaoImpl.class);

	@SuppressWarnings("unchecked")
	@Override
	public List<SupplierFormPojo> findSupplierFormsByTeantId(String loggedInUserTenantId, TableDataInput input) {
		final Query query = constructeSupplierFormForTenantQuery(loggedInUserTenantId, input, false);
		query.setFirstResult(input.getStart());
		query.setMaxResults(input.getLength());
		return query.getResultList();
	}

	@Override
	public long findTotalFilteredSupplierFormsForTenant(String loggedInUserTenantId, TableDataInput input) {
		final Query query = constructeSupplierFormForTenantQuery(loggedInUserTenantId, input, true);
		return ((Number) query.getSingleResult()).longValue();

	}

	@Override
	public long findTotalFilteredSupplierFormsForTenant(String loggedInUserTenantId) {
		StringBuilder hql = new StringBuilder("select count(s) from SupplierForm s where  s.tenantId =:tenantId");
		final Query query = getEntityManager().createQuery(hql.toString());
		query.setParameter("tenantId", loggedInUserTenantId);
		return ((Number) query.getSingleResult()).longValue();
	}

	private Query constructeSupplierFormForTenantQuery(String tenantId, TableDataInput input, boolean isCount) {
		String hql = "";
		if (isCount) {
			hql += "select count(distinct s.id) ";
		}
		if (!isCount) {
			hql += "select distinct NEW com.privasia.procurehere.core.pojo.SupplierFormPojo(s.id, s.name, s.description, cb.loginId, s.createdDate, mb.loginId, s.modifiedDate, s.status,s.pendingCount,s.submittedCount,s.acceptedCount)";
		}
		hql += " from SupplierForm s";

		if (!isCount) {
			hql += " left outer join s.createdBy cb left outer join  s.modifiedBy as mb ";
		}
		hql += " where s.tenantId = :tenantId";

		boolean isStatusFilterOn = false;
		// Add on search filter conditions
		for (ColumnParameter cp : input.getColumns()) {
			if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
				if (cp.getData().equals("status")) {
					hql += " and s.status = (:" + cp.getData() + ")";
				} else if (cp.getData().equals("createdBy")) {
					hql += " and upper(s.createdBy.loginId) like (:" + cp.getData() + ")";
				} else if (cp.getData().equals("modifiedBy")) {
					hql += " and upper(s.modifiedBy.loginId) like (:" + cp.getData() + ")";
				} else if (cp.getData().equals("createdBy") || cp.getData().equals("modifiedBy")) {
					hql += " and upper(s." + cp.getData() + ".loginId" + ") like (:" + cp.getData().replace(".", "") + ")";
				} else {
					hql += " and upper(s." + cp.getData() + ") like (:" + cp.getData().replace(".", "") + ")";
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
					hql += " s." + orderColumn + " " + dir + ",";
				}
				if (hql.lastIndexOf(",") == hql.length() - 1) {
					hql = hql.substring(0, hql.length() - 1);
				}
			} else {
				hql += " order by s.createdDate desc";
			}
		}
		final Query query = getEntityManager().createQuery(hql.toString());
		query.setParameter("tenantId", tenantId);
		// Apply search filter values
		for (ColumnParameter cp : input.getColumns()) {
			if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
				if (cp.getData().equals("status")) {
					query.setParameter("status", SupplierFormsStatus.valueOf(cp.getSearch().getValue()));
				} else if (cp.getData().equals("createdBy") || cp.getData().equals("modifiedBy")) {
					query.setParameter(cp.getData().replace(".", ""), "%" + cp.getSearch().getValue().toUpperCase() + "%");
				} else {
					query.setParameter(cp.getData().replace(".", ""), "%" + cp.getSearch().getValue().toUpperCase() + "%");
				}
			}
		}

		// if (!isStatusFilterOn) {
		// query.setParameter("status", Status.ACTIVE);
		// }
		return query;

	}

	@SuppressWarnings("unchecked")
	@Override
	public SupplierForm getFormById(String id) {
		StringBuilder hql = new StringBuilder("select distinct s from SupplierForm s where s.id =:id");
		final Query query = getEntityManager().createQuery(hql.toString());
		query.setParameter("id", id);
		List<SupplierForm> formList = query.getResultList();
		if (CollectionUtil.isNotEmpty(formList)) {
			return formList.get(0);
		} else {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<SupplierForm> getSupplierFormListByTenantId(String loggedInUserTenantId) {
		final Query query = getEntityManager().createQuery("select new com.privasia.procurehere.core.entity.SupplierForm(s.id,s.name) from SupplierForm s where s.tenantId = :tenantId and s.status =:status");
		query.setParameter("tenantId", loggedInUserTenantId);
		query.setParameter("status", SupplierFormsStatus.ACTIVE);
		return query.getResultList();

	}

	@Override
	public boolean isFormNameExists(SupplierForm supplierFormObj, String loggedInUserTenantId) {
		String hql = "select count(*) from SupplierForm s where s.tenantId = :tenantId and upper(s.name) = :formName";
		if (StringUtils.checkString(supplierFormObj.getId()).length() > 0) {
			hql += " and s.id <> :id";
		}

		final Query query = getEntityManager().createQuery(hql);
		query.setParameter("formName", supplierFormObj.getName().toUpperCase());
		query.setParameter("tenantId", loggedInUserTenantId);
		if (StringUtils.checkString(supplierFormObj.getId()).length() > 0) {
			query.setParameter("id", supplierFormObj.getId());
		}

		return ((Number) query.getSingleResult()).intValue() > 0;
	}

	@SuppressWarnings("unchecked")
	@Override
	public SupplierForm getSupplierFormByTenantAndId(String formId, String tenantId) {
		StringBuilder hql = new StringBuilder("select distinct s from SupplierForm s where s.id =:formId and s.tenantId = :tenantId");
		final Query query = getEntityManager().createQuery(hql.toString());
		query.setParameter("formId", formId);
		query.setParameter("tenantId", tenantId);
		List<SupplierForm> formList = query.getResultList();
		if (CollectionUtil.isNotEmpty(formList)) {
			return formList.get(0);
		} else {
			return null;
		}
	}

	@Override
	public void transferOwnership(String fromUserId, String toUserId) {

		//limit to the user's tenant as a precautionary measure
		String tenantId = SecurityLibrary.getLoggedInUserTenantId();
		User sourceUser = getEntityManager().find(User.class, fromUserId);
		User targetUser = getEntityManager().find(User.class, toUserId);

		Query query = getEntityManager().createNativeQuery("UPDATE PROC_SUPPLIER_FORM SET CREATED_BY = :toUserId where CREATED_BY =:fromUserId");
		query.setParameter("toUserId", toUserId);
		query.setParameter("fromUserId", fromUserId);
		int recordsUpdated = query.executeUpdate();
		LOG.info("Creators Supplier transferred: {}", recordsUpdated);

		//transfer ownership of approvals only for RFS with status of DRAFT and PENDING
		Query query5 = getEntityManager().createQuery(
				"UPDATE SupplierFormApprovalUser sfaur " +
						"SET sfaur.user = :targetUser " +
						"WHERE sfaur.user = :sourceUser " +
						"AND sfaur.approval IN (SELECT ar FROM SupplierFormApproval ar, SupplierForm sr " +
						"WHERE ar = sfaur.approval AND ar.supplierForm = sr " +
						"AND sr.tenantId =:tenantId " +
						"AND sr.status in ('" +
						SupplierFormsStatus.DRAFT + "'))"
		);
		query5.setParameter("sourceUser", sourceUser);
		query5.setParameter("targetUser", targetUser);
		query5.setParameter("tenantId", tenantId);
		recordsUpdated = query5.executeUpdate();
		LOG.info("Approval user transferred: {}", recordsUpdated);
		Query query6 = getEntityManager().createQuery(
				"UPDATE SupplierFormSubmitionApprovalUser sfaur " +
						"SET sfaur.user = :targetUser " +
						"WHERE sfaur.user = :sourceUser " +
						"AND sfaur.approval IN (SELECT ar FROM SupplierFormSubmitionApproval ar, SupplierFormSubmition sr " +
						"WHERE ar = sfaur.approval AND ar.supplierFormSubmition = sr " +
						") "

		);
		query6.setParameter("sourceUser", sourceUser);
		query6.setParameter("targetUser", targetUser);
		//query5.setParameter("tenantId", tenantId);
		//LOG.info("tenantId supplier transfer ownership >>> "+ tenantId);
		//LOG.info("tenantId targetUser  transfer ownership >>> "+ targetUser);
		//LOG.info("tenantId sourceUser  transfer ownership >>> "+ sourceUser);
		//LOG.info("hello query for supplier template transfer ownership >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> "+ query5);
		recordsUpdated = query6.executeUpdate();
		LOG.info("issue 15 P2 Functional bugs user transferred: {}", recordsUpdated);


	}

}
