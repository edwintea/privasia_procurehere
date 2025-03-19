package com.privasia.procurehere.core.dao.impl;

import java.util.Arrays;
import java.util.List;

import javax.persistence.Query;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.privasia.procurehere.core.dao.SupplierFormApprovalDao;
import com.privasia.procurehere.core.dao.SupplierFormSubmissionDao;
import com.privasia.procurehere.core.entity.SupplierFormApproval;
import com.privasia.procurehere.core.entity.SupplierFormApprovalUser;
import com.privasia.procurehere.core.entity.SupplierFormItemAttachment;
import com.privasia.procurehere.core.entity.SupplierFormSubmition;
import com.privasia.procurehere.core.entity.SupplierFormSubmitionApproval;
import com.privasia.procurehere.core.enums.ApprovalStatus;
import com.privasia.procurehere.core.enums.RfxTypes;
import com.privasia.procurehere.core.enums.SupplierFormSubmitionStatus;
import com.privasia.procurehere.core.pojo.ColumnParameter;
import com.privasia.procurehere.core.pojo.OrderParameter;
import com.privasia.procurehere.core.pojo.SupplierFormSubmissionPojo;
import com.privasia.procurehere.core.pojo.TableDataInput;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.StringUtils;

/**
 * @author sana
 */
@Repository
public class SupplierFormSubmissionDaoImpl extends GenericDaoImpl<SupplierFormSubmition, String> implements SupplierFormSubmissionDao {

	private static final Logger LOG = LogManager.getLogger(Global.SUPPLIER_LOG);

	@Autowired
	SupplierFormApprovalDao supplierFormApprovalDao;
	
	@Override
	public long pendingBuyerFormCount(String tenantId, List<SupplierFormSubmitionStatus> status) {
		StringBuilder hql = new StringBuilder("select count(a) from SupplierFormSubmition a left outer join a.favouriteSupplier as s where s.supplier.id = :tenantId and a.status in (:status)");
		Query query = getEntityManager().createQuery(hql.toString());
		query.setParameter("tenantId", tenantId);
		query.setParameter("status", status);
		return ((Number) query.getSingleResult()).longValue();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<SupplierFormSubmissionPojo> findAllSearchFilterPendingFormByStatus(String tenantId, TableDataInput input, SupplierFormSubmitionStatus status) {
		final Query query = constructFormForTenantQueryForSupplierByStatus(tenantId, input, status, false);
		query.setFirstResult(input.getStart());
		query.setMaxResults(input.getLength());
		return query.getResultList();
	}

	@Override
	public long findTotalPendingFormByStatus(String tenantId, TableDataInput input, SupplierFormSubmitionStatus status) {
		final Query query = constructFormForTenantQueryForSupplierByStatus(tenantId, input, status, true);
		return ((Number) query.getSingleResult()).longValue();
	}

	@Override
	public long findTotalFormOfSupplier(String tenantId) {
		StringBuilder hsql = new StringBuilder("select count(distinct f) from SupplierFormSubmition f left outer join f.favouriteSupplier as s where s.supplier.id = :tenantId and f.status in (:status) ");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("tenantId", tenantId);
		query.setParameter("status", Arrays.asList(SupplierFormSubmitionStatus.SUBMITTED, SupplierFormSubmitionStatus.ACCEPTED));
		return ((Number) query.getSingleResult()).longValue();
	}

	private Query constructFormForTenantQueryForSupplierByStatus(String tenantId, TableDataInput tableParams, SupplierFormSubmitionStatus status, boolean isCount) {
		String hql = "";
		if (isCount) {
			hql += "select count(distinct f)";
		} else {
			hql += "select distinct new com.privasia.procurehere.core.pojo.SupplierFormSubmissionPojo(f.id, b.companyName, f.name, f.requestedDate, req.name, f.submitedDate, sub.name, f.status) ";
		}
		hql += "  from SupplierFormSubmition f ";
		hql += " left outer join f.requestedBy as req";
		hql += " left outer join f.submittedBy as sub";
		hql += " left outer join f.buyer as b ";
		hql += " left outer join f.favouriteSupplier as fs";
		hql += " where fs.supplier.id = :tenantId";

		Boolean selectStatusFilter = false;

		// Add on search filter conditions
		for (ColumnParameter cp : tableParams.getColumns()) {
			if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
				if (cp.getData().equals("companyName")) {
					hql += " and upper(f.buyer.companyName) like (:" + cp.getData().replace(".", "") + ")";
				} else if (cp.getData().equals("status")) {
					if (status == null) {
						selectStatusFilter = true;
						hql += " and f.status in(:status) ";
					}
				} else {
					hql += " and upper(f." + cp.getData() + ") like (:" + cp.getData().replace(".", "") + ") ";
				}
			}
		}
		if (status != null) {
			hql += " and f.status = :status ";
		} else {
			if (!selectStatusFilter) {
				hql += " and f.status in(:status)";
			}
		}
		// If it is not a count query then add order by clause
		if (!isCount) {
			List<OrderParameter> orderList = tableParams.getOrder();
			if (CollectionUtil.isNotEmpty(orderList)) {
				hql += " order by ";
				for (OrderParameter order : orderList) {
					String orderColumn = tableParams.getColumns().get(order.getColumn()).getData();
					String dir = order.getDir();
					if (orderColumn.equalsIgnoreCase("companyName")) {
						hql += " f.buyer.companyName " + dir + ",";
					} else if (orderColumn.equalsIgnoreCase("submittedDate")) {
						hql += " f.submitedDate " + dir + ",";
					} else {
						hql += " f." + orderColumn + " " + dir + ",";
					}
				}
				if (hql.lastIndexOf(",") == hql.length() - 1) {
					hql = hql.substring(0, hql.length() - 1);
				}
			}
		}
		LOG.info("HQL : " + hql);

		final Query query = getEntityManager().createQuery(hql.toString());

		query.setParameter("tenantId", tenantId);

		// Apply search filter values
		for (ColumnParameter cp : tableParams.getColumns()) {
			if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {

				if (cp.getData().equals("status")) {
					if (status == null) {
						if (cp.getSearch().getValue().equalsIgnoreCase("ALL")) {
							query.setParameter("status", Arrays.asList(SupplierFormSubmitionStatus.SUBMITTED, SupplierFormSubmitionStatus.ACCEPTED));
						} else {
							query.setParameter("status", SupplierFormSubmitionStatus.fromString(cp.getSearch().getValue().toUpperCase()));
						}
					}
				} else {
					query.setParameter(cp.getData().replace(".", ""), "%" + cp.getSearch().getValue().toUpperCase() + "%");
				}
			}
		}
		if (status != null) {
			query.setParameter("status", status);
		} else {
			if (!selectStatusFilter) {
				query.setParameter("status", Arrays.asList(SupplierFormSubmitionStatus.SUBMITTED, SupplierFormSubmitionStatus.ACCEPTED));
			}
		}
		return query;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<SupplierFormSubmissionPojo> findAllSearchFilterFormBySuppAndBuyerId(String tenantId, TableDataInput input, String supplierId) {
		final Query query = constructFormForTenantQueryForBuyerBySuppAndBuyerId(tenantId, input, supplierId, false);
		query.setFirstResult(input.getStart());
		query.setMaxResults(input.getLength());
		return query.getResultList();

	}

	@Override
	public long findTotalSearchFilterFormBySuppAndBuyerId(String tenantId, TableDataInput input, String supplierId) {
		final Query query = constructFormForTenantQueryForBuyerBySuppAndBuyerId(tenantId, input, supplierId, true);
		return ((Number) query.getSingleResult()).longValue();
	}

	private Query constructFormForTenantQueryForBuyerBySuppAndBuyerId(String tenantId, TableDataInput tableParams, String supplierId, boolean isCount) {
		String hql = "";
		if (isCount) {
			hql += "select count(distinct f)";
		} else {
			hql += "select distinct new com.privasia.procurehere.core.pojo.SupplierFormSubmissionPojo(f.id, f.name, f.requestedDate, req.name, f.submitedDate, sub.name,f.description,f.status) ";
		}
		hql += "  from SupplierFormSubmition f ";
		hql += " left outer join f.requestedBy as req";
		hql += " left outer join f.submittedBy as sub";
		hql += " left outer join f.buyer as b ";
		hql += " left outer join f.favouriteSupplier as fs";
		hql += " where fs.supplier.id = :supplierId";
		hql += " and fs.buyer.id = :tenantId and f.favouriteSupplier is not null";

		boolean isSelectFilterOn = false;
		// Add on search filter conditions
		for (ColumnParameter cp : tableParams.getColumns()) {
			if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
				if (cp.getData().equals("requestedBy")) {
					hql += " and upper(f.requestedBy.name) like (:" + cp.getData().replace(".", "") + ")";
				} else if (cp.getData().equals("status")) {
					hql += " and f.status in (:status) ";
					isSelectFilterOn = true;
				} else if (cp.getData().equals("submittedBy")) {
					hql += " and upper(f.submittedBy.name) like (:" + cp.getData().replace(".", "") + ")";
				} else {
					hql += " and upper(f." + cp.getData() + ") like (:" + cp.getData().replace(".", "") + ") ";
				}
			}
		}
		if (!isSelectFilterOn) {
			hql += " and f.status in (:statuses) ";
		}

		// If it is not a count query then add order by clause
		if (!isCount) {
			List<OrderParameter> orderList = tableParams.getOrder();
			if (CollectionUtil.isNotEmpty(orderList)) {
				hql += " order by ";
				for (OrderParameter order : orderList) {
					String orderColumn = tableParams.getColumns().get(order.getColumn()).getData();
					String dir = order.getDir();
					if (orderColumn.equalsIgnoreCase("requestedBy")) {
						hql += " f.requestedBy.name " + dir + ",";
					} else if (orderColumn.equalsIgnoreCase("submittedBy")) {
						hql += " f.submittedBy.name " + dir + ",";
					} else {
						hql += " f." + orderColumn + " " + dir + ",";
					}
				}
				if (hql.lastIndexOf(",") == hql.length() - 1) {
					hql = hql.substring(0, hql.length() - 1);
				}
			}
		}
		LOG.info("HQL : " + hql);

		final Query query = getEntityManager().createQuery(hql.toString());

		query.setParameter("tenantId", tenantId);
		query.setParameter("supplierId", supplierId);

		// Apply search filter values
		for (ColumnParameter cp : tableParams.getColumns()) {
			if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
				if (cp.getData().equals("status")) {
					if (cp.getSearch().getValue().equalsIgnoreCase("ALL")) {
						List<SupplierFormSubmitionStatus> statuss = Arrays.asList(SupplierFormSubmitionStatus.PENDING, SupplierFormSubmitionStatus.SUBMITTED, SupplierFormSubmitionStatus.ACCEPTED, SupplierFormSubmitionStatus.REJECTED);
						query.setParameter("status", statuss);
					} else {
						query.setParameter("status", SupplierFormSubmitionStatus.fromString(cp.getSearch().getValue().toUpperCase()));
					}
				} else {
					query.setParameter(cp.getData().replace(".", ""), "%" + cp.getSearch().getValue().toUpperCase() + "%");
				}
			}
		}
		if (!isSelectFilterOn) {
			query.setParameter("statuses", Arrays.asList(SupplierFormSubmitionStatus.PENDING, SupplierFormSubmitionStatus.SUBMITTED, SupplierFormSubmitionStatus.ACCEPTED, SupplierFormSubmitionStatus.REJECTED));
		}
		return query;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<SupplierFormSubmissionPojo> myPendingRequestList(String loggedInUserTenantId, String id, TableDataInput input) {

		StringBuilder hql = new StringBuilder("select distinct new com.privasia.procurehere.core.pojo.SupplierFormSubmissionPojo(s.id, s.name, s.description, s.submitedDate, s.submittedBy, sup.companyName,s.status) from SupplierFormSubmition s ");
		hql.append(" left outer join  s.supplier sup left outer join s.approvals app left outer join app.approvalUsers usr ");
		hql.append(" where s.buyer.id =:tenantId and s.status =:status and ( app.supplierFormSubmition.id = s.id and usr.user.id = :userId and usr.approvalStatus = :approvalStatus and app.active = true)");

		// Add on search filter conditions
		for (ColumnParameter cp : input.getColumns()) {
			if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
				LOG.info("INPUT :: " + cp.getData() + "VALUE :: " + cp.getSearch().getValue());
				if (cp.getData().equals("submittedBy")) {
					hql.append(" and upper(s.submittedBy.name) like (:" + cp.getData() + ")");
				} else if (cp.getData().equals("companyName")) {
					hql.append(" and upper(s.supplier.companyName) like (:" + cp.getData() + ")");
				} else {
					hql.append(" and upper(s." + cp.getData() + ") like (:" + cp.getData() + ") ");
				}
			}
		}
		// Implement order by
		List<OrderParameter> orderList = input.getOrder();
		if (CollectionUtil.isNotEmpty(orderList)) {
			hql.append(" order by ");
			for (OrderParameter order : orderList) {
				String orderColumn = input.getColumns().get(order.getColumn()).getData();
				String dir = order.getDir();
				if (orderColumn.equals("submittedBy")) {
					orderColumn = "s.submittedBy.name";
				} else if (orderColumn.equals("companyName")) {
					orderColumn = "sup.companyName";
				}
				hql.append(" " + orderColumn + " " + dir);
			}
			if (hql.lastIndexOf(",") == hql.length() - 1) {
				hql.substring(0, hql.length() - 1);
			}
		} else {
			hql.append(" order by s.submitedDate DESC");
		}

		final Query query = getEntityManager().createQuery(hql.toString());

		// final Query query = getEntityManager().createQuery();
		query.setParameter("tenantId", loggedInUserTenantId);
		query.setParameter("userId", id);
		query.setParameter("approvalStatus", ApprovalStatus.PENDING);
		query.setParameter("status", SupplierFormSubmitionStatus.SUBMITTED);
		for (ColumnParameter cp : input.getColumns()) {
			if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
				// LOG.info("INPUT :: " + cp.getData() + "VALUE :: " +
				// cp.getSearch().getValue());
				if (cp.getData().equals("type")) {
					query.setParameter(cp.getData(), RfxTypes.valueOf(cp.getSearch().getValue()).getValue());
				} else {
					query.setParameter(cp.getData(), "%" + cp.getSearch().getValue().toUpperCase() + "%");
				}
			}
		}
		query.setFirstResult(input.getStart());
		query.setMaxResults(input.getLength());

		return query.getResultList();

	}

	@Override
	public long myPendingSupplierFormListCount(String loggedInUserTenantId, String userid, TableDataInput input) {

		StringBuilder hql = new StringBuilder("select count(distinct s) from SupplierFormSubmition s  left outer join  s.supplier sup  left outer join  s.requestedBy req left outer join  s.submittedBy  sub left outer join s.approvals app left outer join app.approvalUsers usr ");
		hql.append(" where s.buyer.id =:tenantId and s.status =:status and (app.supplierFormSubmition.id = s.id and usr.user.id = :userId and usr.approvalStatus = :approvalStatus and app.active = true)");

		// Add on search filter conditions
		if (input != null) {
			for (ColumnParameter cp : input.getColumns()) {
				if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
					if (cp.getData().equals("requestedBy")) {
						hql.append(" and upper(s.requestedBy.name) like (:" + cp.getData() + ")");
					} else if (cp.getData().equals("submittedBy")) {
						hql.append(" and upper(s.submittedBy.name) like (:" + cp.getData() + ")");
					} else if (cp.getData().equals("companyName")) {
						hql.append(" and upper(s.supplier.companyName) like (:" + cp.getData() + ")");
					} else {
						hql.append(" and upper(s." + cp.getData() + ") like (:" + cp.getData() + ") ");
					}
				}
			}
		}

		final Query query = getEntityManager().createQuery(hql.toString());

		query.setParameter("tenantId", loggedInUserTenantId);
		query.setParameter("userId", userid);
		query.setParameter("approvalStatus", ApprovalStatus.PENDING);
		query.setParameter("status", SupplierFormSubmitionStatus.SUBMITTED);

		if (input != null) {
			for (ColumnParameter cp : input.getColumns()) {
				if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
					if (cp.getData().equals("type")) {
						query.setParameter(cp.getData(), RfxTypes.valueOf(cp.getSearch().getValue()).getValue());
					} else {
						query.setParameter(cp.getData(), "%" + cp.getSearch().getValue().toUpperCase() + "%");
					}
				}
			}
		}
		return ((Number) query.getSingleResult()).longValue();
	}

	@Override
	public long findTotalFormBySuppAndBuyerId(String loggedInUserTenantId, String supplierId) {
		String hql = "select count(distinct f.id) from SupplierFormSubmition f left outer join f.favouriteSupplier as fs  where fs.supplier.id = :supplierId and f.buyer.id = :buyerId";
		final Query query = getEntityManager().createQuery(hql);
		query.setParameter("supplierId", supplierId);
		query.setParameter("buyerId", loggedInUserTenantId);
		return ((Number) query.getSingleResult()).longValue();

	}

	@SuppressWarnings("unchecked")
	@Override
	public List<SupplierFormSubmissionPojo> findAllSearchFilterFormByBuyerIdAndStatus(String tenantId, TableDataInput input, List<SupplierFormSubmitionStatus> status) {
		final Query query = constructFormForTenantQueryForBuyerByBuyerIdAndStatus(tenantId, input, status, false);
		query.setFirstResult(input.getStart());
		query.setMaxResults(input.getLength());
		return query.getResultList();

	}

	@Override
	public long findTotalSearchFilterFormByBuyerIdAndStatus(String tenantId, TableDataInput input, List<SupplierFormSubmitionStatus> status) {
		final Query query = constructFormForTenantQueryForBuyerByBuyerIdAndStatus(tenantId, input, status, true);
		return ((Number) query.getSingleResult()).longValue();
	}

	private Query constructFormForTenantQueryForBuyerByBuyerIdAndStatus(String tenantId, TableDataInput tableParams, List<SupplierFormSubmitionStatus> status, boolean isCount) {
		String hql = "";
		if (isCount) {
			hql += "select count(distinct f)";
		} else {
			hql += "select distinct new com.privasia.procurehere.core.pojo.SupplierFormSubmissionPojo(f.id, f.name, f.requestedDate, req.name, f.submitedDate, sub.name,f.description,s.companyName,f.status) ";
		}
		hql += "  from SupplierFormSubmition f ";
		hql += " left outer join f.requestedBy as req";
		hql += " left outer join f.submittedBy as sub";
		hql += " left outer join f.buyer as b ";
		hql += " left outer join f.favouriteSupplier as fs";
		hql += " left outer join fs.supplier as s";
		hql += " where f.buyer.id = :tenantId and f.favouriteSupplier is not null ";

		boolean isSelectFilterOn = false;
		// Add on search filter conditions
		for (ColumnParameter cp : tableParams.getColumns()) {
			if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
				if (cp.getData().equals("requestedBy")) {
					hql += " and upper(f.requestedBy.name) like (:" + cp.getData().replace(".", "") + ")";
				} else if (cp.getData().equals("status")) {
					hql += " and f.status in (:status) ";
					isSelectFilterOn = true;
				} else if (cp.getData().equals("submittedBy")) {
					hql += " and upper(f.submittedBy.name) like (:" + cp.getData().replace(".", "") + ")";
				} else if (cp.getData().equals("supplierCompanyName")) {
					hql += " and upper(fs.supplier.companyName) like (:" + cp.getData().replace(".", "") + ")";
				} else {
					hql += " and upper(f." + cp.getData() + ") like (:" + cp.getData().replace(".", "") + ") ";
				}
			}
		}
		if (!isSelectFilterOn) {
			hql += " and f.status in (:statuses)";
		}

		// If it is not a count query then add order by clause
		if (!isCount) {
			List<OrderParameter> orderList = tableParams.getOrder();
			if (CollectionUtil.isNotEmpty(orderList)) {
				hql += " order by ";
				for (OrderParameter order : orderList) {
					String orderColumn = tableParams.getColumns().get(order.getColumn()).getData();
					String dir = order.getDir();
					if (orderColumn.equalsIgnoreCase("requestedBy")) {
						hql += " f.requestedBy.name " + dir + ",";
					} else if (orderColumn.equalsIgnoreCase("submittedBy")) {
						hql += " f.submittedBy.name " + dir + ",";
					} else if (orderColumn.equalsIgnoreCase("supplierCompanyName")) {
						hql += " fs.supplier.companyName " + dir + ",";
					} else {
						hql += " f." + orderColumn + " " + dir + ",";
					}
				}
				if (hql.lastIndexOf(",") == hql.length() - 1) {
					hql = hql.substring(0, hql.length() - 1);
				}
			}
		}
		LOG.info("HQL : " + hql);

		final Query query = getEntityManager().createQuery(hql.toString());

		query.setParameter("tenantId", tenantId);

		// Apply search filter values
		for (ColumnParameter cp : tableParams.getColumns()) {
			if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
				if (cp.getData().equals("status")) {
					query.setParameter("status", SupplierFormSubmitionStatus.fromString(cp.getSearch().getValue().toUpperCase()));
				} else {
					query.setParameter(cp.getData().replace(".", ""), "%" + cp.getSearch().getValue().toUpperCase() + "%");
				}
			}
		}
		if (!isSelectFilterOn) {
			query.setParameter("statuses", status);
		}
		return query;
	}

	@Override
	public long findTotalFormByBuyerIdAnStatus(String loggedInUserTenantId, List<SupplierFormSubmitionStatus> status) {
		String hql = "select count(distinct f) from SupplierFormSubmition f left outer join f.favouriteSupplier as fs  where f.status in (:status) and f.buyer.id = :buyerId  and f.favouriteSupplier is not null";
		final Query query = getEntityManager().createQuery(hql);
		query.setParameter("status", status);
		query.setParameter("buyerId", loggedInUserTenantId);
		return ((Number) query.getSingleResult()).longValue();
	}

	@SuppressWarnings("unchecked")
	@Override
	public SupplierFormSubmition findOnBoardingFormAvailable(String buyerId, String supplierId) {
		StringBuilder hql = new StringBuilder("from SupplierFormSubmition sub left outer join fetch sub.approvals sfa where sub.buyer.id = :buyerId and sub.supplier.id = :supplierId and sub.isOnboardingForm = :onBorad");
		final Query query = getEntityManager().createQuery(hql.toString());
		query.setParameter("buyerId", buyerId);
		query.setParameter("supplierId", supplierId);
		query.setParameter("onBorad", true);
		List<SupplierFormSubmition> uList = query.getResultList();
		if (CollectionUtil.isNotEmpty(uList)) {
			return uList.get(0);
		} else {
			return null;
		}
	}

	@Override
	public long findFormSubBySuppAndBuyerId(String buyerId, String supplierId) {
		StringBuilder hsql = new StringBuilder("select count(distinct sub.id) from SupplierFormSubmition sub sub.buyer.id = :buyerId and sub.supplier.id = :supplierId and sub.isOnboardingForm = :onBorad");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("buyerId", buyerId);
		query.setParameter("supplierId", supplierId);
		query.setParameter("onBorad", true);
		return ((Number) query.getSingleResult()).longValue();
	}

	@Override
	public long findFormSubBySuppAndFormId(String formId, String favsupplierid) {
		StringBuilder hsql = new StringBuilder("select count(distinct f) from SupplierFormSubmition f left outer join f.favouriteSupplier as s where s.id = :favsupplierid and f.supplierForm.id = :formId");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("formId", formId);
		query.setParameter("favsupplierid", favsupplierid);
		return ((Number) query.getSingleResult()).longValue();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<SupplierFormItemAttachment> findAllFormDocsByFormItemId(String id) {
		final Query query = getEntityManager().createQuery("select t from SupplierFormItemAttachment t inner join t.formItem item where item.id = :id");
		query.setParameter("id", id);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public SupplierFormItemAttachment findSupplierformItemAttachment(String documentId) {
		final Query query = getEntityManager().createQuery("from SupplierFormItemAttachment si inner join fetch si.formItem item where si.id= :documentId");
		query.setParameter("documentId", documentId);
		List<SupplierFormItemAttachment> uList = query.getResultList();
		if (CollectionUtil.isNotEmpty(uList)) {
			return uList.get(0);
		} else {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public SupplierFormSubmition findFormById(String formId) {
		StringBuilder hql = new StringBuilder("from SupplierFormSubmition sub where sub.id = :formId");
		final Query query = getEntityManager().createQuery(hql.toString());
		query.setParameter("formId", formId);
		List<SupplierFormSubmition> uList = query.getResultList();
		if (CollectionUtil.isNotEmpty(uList)) {
			return uList.get(0);
		} else {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public SupplierFormSubmition getAssignedSupplierForm(String supplierId, String buyerId) {
		StringBuilder hql = new StringBuilder("from SupplierFormSubmition sub where sub.buyer.id = :buyerId and sub.supplier.id = :supplierId");
		final Query query = getEntityManager().createQuery(hql.toString());
		query.setParameter("supplierId", supplierId);
		query.setParameter("buyerId", buyerId);
		List<SupplierFormSubmition> uList = query.getResultList();
		if (CollectionUtil.isNotEmpty(uList)) {
			return uList.get(0);
		} else {
			return null;
		}
	}

	@Override
	public boolean isFormAssigned(String formId) {
		Query query = getEntityManager().createQuery("select count(s.id) from SupplierFormSubmition s where s.supplierForm.id = :formId");
		query.setParameter("formId", formId);
		return ((Number) query.getSingleResult()).intValue() > 0;
	}

	@SuppressWarnings("unchecked")
	@Override
	public SupplierFormSubmition findOnboardingFormSubmitionByFavSuppIdAndBuyerId(String favSuppId, String buyerId) {
		StringBuilder hql = new StringBuilder("select a from SupplierFormSubmition a left outer join a.favouriteSupplier as s where s.id = :favSuppId and a.buyer.id = :buyerId and a.isOnboardingForm =:isOnboardingForm");
		Query query = getEntityManager().createQuery(hql.toString());
		query.setParameter("favSuppId", favSuppId);
		query.setParameter("buyerId", buyerId);
		query.setParameter("isOnboardingForm", Boolean.TRUE);
		List<SupplierFormSubmition> uList = query.getResultList();
		if (CollectionUtil.isNotEmpty(uList)) {
			return uList.get(0);
		} else {
			return null;
		}

	}

	@Override
	public void updateFavSupplier(String id, String favSupplierId) {
		String hql = "update SupplierFormSubmition sf set sf.favouriteSupplier.id = :favSupplierId where sf.id = :id ";
		Query query = getEntityManager().createQuery(hql);
		query.setParameter("favSupplierId", favSupplierId);
		query.setParameter("id", id);
		try {
			query.executeUpdate();
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public SupplierFormSubmition getSupplierFormForAdditionalApproverById(String formId) {
		try {
			final Query query = getEntityManager().createQuery("select distinct s from SupplierFormSubmition s left outer join fetch s.approvals sfa   where s.id =:formId order by sfa.batchNo asc");
			query.setParameter("formId", formId);
			List<SupplierFormSubmition> uList = query.getResultList();
			if (CollectionUtil.isNotEmpty(uList)) {
				return uList.get(0);
			} else {

				return null;
			}
		} catch (Exception e) {
			LOG.error("Error while getting user : " + e.getMessage(), e);
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public SupplierFormSubmitionApproval getSupplierFormActiveApproverById(String formId) {
		try {
			final Query query = getEntityManager().createQuery("select distinct s from SupplierFormSubmitionApproval s left outer join fetch s.approvalUsers sfa  left outer join fetch s.supplierFormSubmition f where f.id = :formId and s.active = :isActive");
			query.setParameter("formId", formId);
			query.setParameter("isActive", Boolean.TRUE);
			List<SupplierFormSubmitionApproval> uList = query.getResultList();
			if (CollectionUtil.isNotEmpty(uList)) {
				return uList.get(0);
			} else {

				return null;
			}
		} catch (Exception e) {
			LOG.error("Error while getting user : " + e.getMessage(), e);
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public SupplierFormSubmition getSupplierFormSubmitionById(String formId) {
		try {
			final Query query = getEntityManager().createQuery("select s from SupplierFormSubmition s left outer join fetch s.requestedBy sfa  where s.id =:formId");
			query.setParameter("formId", formId);
			List<SupplierFormSubmition> uList = query.getResultList();
			if (CollectionUtil.isNotEmpty(uList)) {
				return uList.get(0);
			} else {
				return null;
			}
		} catch (Exception e) {
			LOG.error("Error while getting user : " + e.getMessage(), e);
			return null;
		}
	}

	@Override
	@Transactional(readOnly = true)
	public List<SupplierFormApproval> getAllApprovalListByFormId(String formId) {
		List<SupplierFormApproval> list = supplierFormApprovalDao.getAllApprovalListByFormId(formId);
		if (CollectionUtil.isNotEmpty(list)) {
			for (SupplierFormApproval sfa : list) {
				if (sfa.getApprovalUsers() != null) {
					for (SupplierFormApprovalUser u : sfa.getApprovalUsers()) {
						if (u.getUser() != null) {
							u.getUser().getLoginId();
						}
					}
				}
			}
		}
		return list;
	}

	@SuppressWarnings("unchecked")
	@Override
	public SupplierFormSubmition findOnBoardingFormForSupplier( String supplierId) {
		StringBuilder hql = new StringBuilder("from SupplierFormSubmition sub  where sub.supplier.id = :supplierId and sub.isOnboardingForm = :onBorad");
		final Query query = getEntityManager().createQuery(hql.toString());
		query.setParameter("supplierId", supplierId);
		query.setParameter("onBorad", true);
		List<SupplierFormSubmition> uList = query.getResultList();
		if (CollectionUtil.isNotEmpty(uList)) {
			return uList.get(0);
		} else {
			return null;
		}
	}
}