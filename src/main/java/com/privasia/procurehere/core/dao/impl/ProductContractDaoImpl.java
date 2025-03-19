package com.privasia.procurehere.core.dao.impl;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.persistence.Query;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.privasia.procurehere.core.dao.ProductContractDao;
import com.privasia.procurehere.core.dao.UserDao;
import com.privasia.procurehere.core.entity.ContractApproval;
import com.privasia.procurehere.core.entity.ContractApprovalUser;
import com.privasia.procurehere.core.entity.ContractComment;
import com.privasia.procurehere.core.entity.ContractTeamMember;
import com.privasia.procurehere.core.entity.ProductContract;
import com.privasia.procurehere.core.entity.ProductItem;
import com.privasia.procurehere.core.entity.User;
import com.privasia.procurehere.core.enums.ApprovalStatus;
import com.privasia.procurehere.core.enums.ContractStatus;
import com.privasia.procurehere.core.enums.EventStatus;
import com.privasia.procurehere.core.enums.TeamMemberType;
import com.privasia.procurehere.core.enums.UserType;
import com.privasia.procurehere.core.pojo.ColumnParameter;
import com.privasia.procurehere.core.pojo.ContractPojo;
import com.privasia.procurehere.core.pojo.ContractProductItemPojo;
import com.privasia.procurehere.core.pojo.EventPermissions;
import com.privasia.procurehere.core.pojo.OrderParameter;
import com.privasia.procurehere.core.pojo.ProductContractPojo;
import com.privasia.procurehere.core.pojo.TableDataInput;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.SecurityLibrary;
import com.privasia.procurehere.core.utils.StringUtils;

@Repository
public class ProductContractDaoImpl extends GenericDaoImpl<ProductContract, String> implements ProductContractDao {

	private static final Logger LOG = LogManager.getLogger(Global.ADMIN_LOG);

	@Autowired
	UserDao userDao;

	@SuppressWarnings("unchecked")
	@Override
	public ProductContract findByContractByReferenceNumber(String sapContractNumber, String tenantId) {
		LOG.info("contractReferenceNumber" + sapContractNumber);
		StringBuilder hsql = new StringBuilder("select distinct t from ProductContract as t left outer join fetch t.productContractItem pci join fetch pci.productItem pi where t.status = :status and t.sapContractNumber = :sapContractNumber and t.buyer.id = :tenantId");
		final Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("status", ContractStatus.ACTIVE);
		query.setParameter("sapContractNumber", sapContractNumber);
		query.setParameter("tenantId", tenantId);
		try {
			List<ProductContract> list = query.getResultList();
			if (CollectionUtil.isNotEmpty(list)) {
				return list.get(0);
			}
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ContractPojo> findProductContractListForTenant(String tenantId, String userId, TableDataInput tableParams, Date startDate, Date endDate) {
		final Query query = constructProductListForTenantQuery(tenantId, userId, tableParams, false, startDate, endDate);
		query.setFirstResult(tableParams.getStart());
		query.setMaxResults(tableParams.getLength());
		return query.getResultList();
	}

	@Override
	public List<ContractPojo> findProductContractListForBizUnit(String tenantId, String userId, TableDataInput tableParams, Date startDate, Date endDate,List<String> businessUnitIds) {
		//final Query query = constructProductListForTenantQuery(tenantId, userId, tableParams, false, startDate, endDate);
		final Query query = constructProductListForBizUnitQuery(tenantId, userId, tableParams, false, startDate, endDate,businessUnitIds);
		query.setFirstResult(tableParams.getStart());
		query.setMaxResults(tableParams.getLength());
		return query.getResultList();
	}

	@Override
	@Transactional(readOnly = false)
	public int updateStatusFromApprovedToActive() {

		// Bring the time to Malaysia timezone.
		Calendar now = Calendar.getInstance();
		now.add(Calendar.HOUR, 8);

		final Query query = getEntityManager().createQuery("update ProductContract c set c.status = :newStatus where c.status = :oldStatus and c.contractStartDate <= :currentTime");
		query.setParameter("oldStatus", ContractStatus.APPROVED);
		query.setParameter("newStatus", ContractStatus.ACTIVE);
		query.setParameter("currentTime", now.getTime());
		return query.executeUpdate();
	}

	@Override
	@Transactional(readOnly = false)
	public int updateStatusFromActiveToExpired() {
		// Bring the time to Malaysia timezone.
		Calendar now = Calendar.getInstance();
		now.add(Calendar.HOUR, 8);

		final Query query = getEntityManager().createQuery("update ProductContract c set c.status = :newStatus where c.status = :oldStatus and c.contractEndDate <= :currentTime");
		query.setParameter("oldStatus", ContractStatus.ACTIVE);
		query.setParameter("newStatus", ContractStatus.EXPIRED);
		query.setParameter("currentTime", now.getTime());
		return query.executeUpdate();
	}

	private Query constructProductListForTenantQuery(String tenantId, String userId, TableDataInput tableParams, boolean isCount, Date startDate, Date endDate) {

		String hql = "";

		// If count query is enabled, then add the select count(*) clause
		if (isCount) {
			hql += "select count(distinct t) ";
		}
		if (!isCount) {
			hql += "select distinct new com.privasia.procurehere.core.pojo.ContractPojo(t.id, t.contractName, t.eventId, t.contractReferenceNumber, t.groupCodeStr, t.contractStartDate, t.contractEndDate, t.contractValue, t.status, t.createdDate, t.modifiedDate, cc.loginId, mb.loginId, s.companyName, fs.vendorCode, bu.unitName, cb.communicationEmail, bu.unitCode,  t.decimal, t.contractId, c.currencyCode) from ProductContract t ";
		} else {
			hql += " from ProductContract t ";
		}
		// If this is not a count query, only then add the join fetch. Count query does not require its
		if (!isCount) {
			hql += " left outer join t.contractCreator cc left outer join t.contractCreator cb left outer join t.modifiedBy mb left outer join t.supplier fs left outer join fs.supplier s left outer join t.businessUnit bu left outer join t.currency c left outer join t.teamMembers tm left outer join tm.user tmu ";
		} else {
			hql += " left outer join t.contractCreator cc left outer join t.contractCreator cb left outer join t.modifiedBy mb left outer join t.supplier fs left outer join fs.supplier s left outer join t.businessUnit bu left outer join t.currency c left outer join t.teamMembers tm left outer join tm.user tmu ";
		}

		hql += " where t.buyer.id = :tenantId ";

		if (userId != null) {
			hql += " and ( cb.id = :userId  or tmu.id = :userId )";
		}
		// Add on search filter conditions
		for (ColumnParameter cp : tableParams.getColumns()) {
			if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
				LOG.info(">>>>>>>>>>>>>>>>>>>> " + cp.getData() + ".................. " + cp.getSearch().getValue());

				if (cp.getData().equals("status")) {
					hql += " and t.status = (:" + cp.getData() + ")";
				} else if (cp.getData().equals("typeString")) {
					hql += " and t.type = (:type)";
				} else if (cp.getData().equals("vendorCode") || cp.getData().equals("companyName")) {
					hql += " and upper(s.companyName) like (:" + cp.getData() + ")";
				} else if (cp.getData().equals("createdBy")) {
					hql += " and upper(cb.loginId) like (:" + cp.getData() + ")";
				} else if (cp.getData().equals("modifiedBy")) {
					hql += " and upper(mb.loginId) like (:" + cp.getData() + ")";
				} else if (cp.getData().equals("eventIds")) {
					hql += " and t.id in (:eventIds) ";
				} else if (cp.getData().equals("referenceNumber")) {
					hql += " and upper(t.contractReferenceNumber)  like (:" + cp.getData() + ")";
				} else if (cp.getData().equals("groupcode")) {
					hql += " and upper(t.groupCodeStr) like (:" + cp.getData() + ")";
				} else if (cp.getData().equals("currencyCode")) {
					hql += " and upper(c.currencyCode) like (:" + cp.getData() + ")";
				} else if (cp.getData().equals("supplier")) {
					hql += " and upper(s.companyName) like (:" + cp.getData() + ")";
				} else if (cp.getData().equals("businessUnit")) {
					hql += " and upper(bu.unitName) like (:" + cp.getData() + ")";
				} else if (cp.getData().equals("creatorEmail")) {
					hql += " and upper(cb.communicationEmail) like (:" + cp.getData() + ")";
				} else if (cp.getData().equals("contractValue")) {
					try {
						new BigDecimal(cp.getSearch().getValue());
						hql += " and t." + cp.getData() + " = (:" + cp.getData().replace(".", "") + ")";
					} catch (Exception e) {
					}
				} else {
					hql += " and upper(t." + cp.getData() + ") like (:" + cp.getData().replace(".", "") + ")";
				}
			}
		}
		// search with Date range
		if (startDate != null && endDate != null) {
			hql += " and  t.createdDate between :startDate and :endDate ";
		}

		// If it is not a count query then add order by clause
		if (!isCount) {
			List<OrderParameter> orderList = tableParams.getOrder();
			if (CollectionUtil.isNotEmpty(orderList)) {
				hql += " order by ";
				for (OrderParameter order : orderList) {
					String orderColumn = tableParams.getColumns().get(order.getColumn()).getData();
					String dir = order.getDir();
					if (orderColumn.equals("vendorCode") || orderColumn.equals("companyName")) {
						hql += " s.companyName " + dir + ",";
					} else if (orderColumn.equals("createBy")) {
						hql += " cb.loginId " + dir + ",";
					} else if (orderColumn.equals("modifiedBy")) {
						hql += " mb.loginId " + dir + ",";
					} else if (orderColumn.equals("currencyCode")) {
						hql += " c.currencyCode " + dir + ",";
					} else {
						hql += " t." + orderColumn + " " + dir + ",";
					}
				}
				if (hql.lastIndexOf(",") == hql.length() - 1) {
					hql = hql.substring(0, hql.length() - 1);
				}
			} else {
				hql += " order by t.contractReferenceNumber ";
			}
		}

		LOG.info("HQL : " + hql);

		final Query query = getEntityManager().createQuery(hql.toString());
		query.setParameter("tenantId", tenantId);
		if (userId != null) {
			query.setParameter("userId", userId);
		}

		// Apply search filter values
		for (ColumnParameter cp : tableParams.getColumns()) {
			if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
				if (cp.getData().equals("status")) {
					query.setParameter("status", ContractStatus.valueOf(cp.getSearch().getValue()));
				} else if (cp.getData().equals("contractValue")) {
					LOG.info("Contract value  : " + cp.getSearch().getValue());
					try {
						new BigDecimal(cp.getSearch().getValue());
						query.setParameter(cp.getData().replace(".", ""), new BigDecimal(cp.getSearch().getValue()));
					} catch (Exception e) {
					}
				} else if (cp.getData().equals("eventIds")) {
					query.setParameter("eventIds", Arrays.asList(cp.getSearch().getValue().split(",")));
				} else {
					query.setParameter(cp.getData().replace(".", ""), "%" + cp.getSearch().getValue().toUpperCase() + "%");
				}
			}
		}
		// set parameter Date range
		if (startDate != null && endDate != null) {
			query.setParameter("startDate", startDate);
			query.setParameter("endDate", endDate);
		}
		return query;
	}

	private Query constructProductListForBizUnitQuery(String tenantId, String userId, TableDataInput tableParams, boolean isCount, Date startDate, Date endDate,List<String> businessUnitIds) {
		LOG.info("Biz Unit Contract query ? "+businessUnitIds);
		String hql = "";

		// If count query is enabled, then add the select count(*) clause
		if (isCount) {
			hql += "select count(distinct t) ";
		}
		if (!isCount) {
			hql += "select distinct new com.privasia.procurehere.core.pojo.ContractPojo(t.id, t.contractName, t.eventId, t.contractReferenceNumber, t.groupCodeStr, t.contractStartDate, t.contractEndDate, t.contractValue, t.status, t.createdDate, t.modifiedDate, cc.loginId, mb.loginId, s.companyName, fs.vendorCode, bu.unitName, cb.communicationEmail, bu.unitCode,  t.decimal, t.contractId, c.currencyCode) from ProductContract t ";
		} else {
			hql += " from ProductContract t ";
		}
		// If this is not a count query, only then add the join fetch. Count query does not require its
		if (!isCount) {
			hql += " left outer join t.contractCreator cc left outer join t.contractCreator cb left outer join t.modifiedBy mb left outer join t.supplier fs left outer join fs.supplier s left outer join t.businessUnit bu left outer join t.currency c left outer join t.teamMembers tm left outer join tm.user tmu ";
		} else {
			hql += " left outer join t.contractCreator cc left outer join t.contractCreator cb left outer join t.modifiedBy mb left outer join t.supplier fs left outer join fs.supplier s left outer join t.businessUnit bu left outer join t.currency c left outer join t.teamMembers tm left outer join tm.user tmu ";
		}

		hql += " where t.buyer.id = :tenantId ";

		if (userId != null) {
			//hql += " and ( cb.id = :userId  or tmu.id = :userId )";
			//4105 uc 1.1 logic
			if (businessUnitIds != null && !businessUnitIds.isEmpty()) {
				LOG.info("businessUnitIds NOT EMPTYY");
				hql += " AND bu.id IN (:businessUnitIds) " ;
			}
		}
		// Add on search filter conditions
		for (ColumnParameter cp : tableParams.getColumns()) {
			if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
				LOG.info(">>>>>>>>>>>>>>>>>>>> " + cp.getData() + ".................. " + cp.getSearch().getValue());

				if (cp.getData().equals("status")) {
					hql += " and t.status = (:" + cp.getData() + ")";
				} else if (cp.getData().equals("typeString")) {
					hql += " and t.type = (:type)";
				} else if (cp.getData().equals("vendorCode") || cp.getData().equals("companyName")) {
					hql += " and upper(s.companyName) like (:" + cp.getData() + ")";
				} else if (cp.getData().equals("createdBy")) {
					hql += " and upper(cb.loginId) like (:" + cp.getData() + ")";
				} else if (cp.getData().equals("modifiedBy")) {
					hql += " and upper(mb.loginId) like (:" + cp.getData() + ")";
				} else if (cp.getData().equals("eventIds")) {
					hql += " and t.id in (:eventIds) ";
				} else if (cp.getData().equals("referenceNumber")) {
					hql += " and upper(t.contractReferenceNumber)  like (:" + cp.getData() + ")";
				} else if (cp.getData().equals("groupcode")) {
					hql += " and upper(t.groupCodeStr) like (:" + cp.getData() + ")";
				} else if (cp.getData().equals("currencyCode")) {
					hql += " and upper(c.currencyCode) like (:" + cp.getData() + ")";
				} else if (cp.getData().equals("supplier")) {
					hql += " and upper(s.companyName) like (:" + cp.getData() + ")";
				} else if (cp.getData().equals("businessUnit")) {
					hql += " and upper(bu.unitName) like (:" + cp.getData() + ")";
				} else if (cp.getData().equals("creatorEmail")) {
					hql += " and upper(cb.communicationEmail) like (:" + cp.getData() + ")";
				} else if (cp.getData().equals("contractValue")) {
					try {
						new BigDecimal(cp.getSearch().getValue());
						hql += " and t." + cp.getData() + " = (:" + cp.getData().replace(".", "") + ")";
					} catch (Exception e) {
					}
				} else {
					hql += " and upper(t." + cp.getData() + ") like (:" + cp.getData().replace(".", "") + ")";
				}
			}
		}
		// search with Date range
		if (startDate != null && endDate != null) {
			hql += " and  t.createdDate between :startDate and :endDate ";
		}

		// If it is not a count query then add order by clause
		if (!isCount) {
			List<OrderParameter> orderList = tableParams.getOrder();
			if (CollectionUtil.isNotEmpty(orderList)) {
				hql += " order by ";
				for (OrderParameter order : orderList) {
					String orderColumn = tableParams.getColumns().get(order.getColumn()).getData();
					String dir = order.getDir();
					if (orderColumn.equals("vendorCode") || orderColumn.equals("companyName")) {
						hql += " s.companyName " + dir + ",";
					} else if (orderColumn.equals("createBy")) {
						hql += " cb.loginId " + dir + ",";
					} else if (orderColumn.equals("modifiedBy")) {
						hql += " mb.loginId " + dir + ",";
					} else if (orderColumn.equals("currencyCode")) {
						hql += " c.currencyCode " + dir + ",";
					} else {
						hql += " t." + orderColumn + " " + dir + ",";
					}
				}
				if (hql.lastIndexOf(",") == hql.length() - 1) {
					hql = hql.substring(0, hql.length() - 1);
				}
			} else {
				hql += " order by t.contractReferenceNumber ";
			}
		}

		LOG.info("HQL : " + hql);

		final Query query = getEntityManager().createQuery(hql.toString());
		query.setParameter("tenantId", tenantId);
		if (userId != null) {
			if (businessUnitIds != null && !businessUnitIds.isEmpty()) {
				query.setParameter("businessUnitIds", businessUnitIds);
			}
		}

		// Apply search filter values
		for (ColumnParameter cp : tableParams.getColumns()) {
			if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
				if (cp.getData().equals("status")) {
					query.setParameter("status", ContractStatus.valueOf(cp.getSearch().getValue()));
				} else if (cp.getData().equals("contractValue")) {
					LOG.info("Contract value  : " + cp.getSearch().getValue());
					try {
						new BigDecimal(cp.getSearch().getValue());
						query.setParameter(cp.getData().replace(".", ""), new BigDecimal(cp.getSearch().getValue()));
					} catch (Exception e) {
					}
				} else if (cp.getData().equals("eventIds")) {
					query.setParameter("eventIds", Arrays.asList(cp.getSearch().getValue().split(",")));
				} else {
					query.setParameter(cp.getData().replace(".", ""), "%" + cp.getSearch().getValue().toUpperCase() + "%");
				}
			}
		}
		// set parameter Date range
		if (startDate != null && endDate != null) {
			query.setParameter("startDate", startDate);
			query.setParameter("endDate", endDate);
		}
		return query;
	}

	@Override
	public long findTotalFilteredProductListForTenant(String loggedInUserTenantId, String userId, TableDataInput input, Date startDate, Date endDate) {
		final Query query = constructProductListForTenantQuery(loggedInUserTenantId, userId, input, true, startDate, endDate);
		return ((Number) query.getSingleResult()).longValue();
	}

	@Override
	public long findTotalProductListForTenant(String tenantId, String userId) {
		StringBuilder hql = new StringBuilder("select count (distinct t) from ProductContract t left outer join t.contractCreator cb left outer join t.teamMembers tm left outer join tm.user tmu where t.buyer.id = :tenantId ");
		if (userId != null) {
			hql.append(" and (cb.id = :userId or tmu.id = :userId )");
		}
		final Query query = getEntityManager().createQuery(hql.toString());
		// query.setParameter("status", Status.ACTIVE);
		query.setParameter("tenantId", tenantId);
		if (userId != null) {
			query.setParameter("userId", userId);
		}
		return ((Number) query.getSingleResult()).longValue();
	}

	@Override
	public ProductContract findProductContractById(String id, String tenantId) {
		LOG.info("Contract Id " + id + " Tenant Id " + tenantId);
		StringBuilder hsql = new StringBuilder("select t from ProductContract as t left outer join fetch t.currency c left outer join fetch t.groupCode g left outer join fetch t.productContractItem left outer join fetch t.businessUnit bu left outer join fetch t.supplier left outer join fetch t.agreementType agt left outer join t.contractLoaAndAgreement loa left outer join fetch t.createdBy where t.id = :id and t.buyer.id = :tenantId");
		final Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", id);
		query.setParameter("tenantId", tenantId);
		return (ProductContract) query.getSingleResult();
	}

	@Override
	@SuppressWarnings("unchecked")
	public void deleteProductContractbyTenanatId(String tenantId) {

		StringBuilder hql = new StringBuilder("select t.id from ProductContract t where t.buyer.id = :tenantId ");
		final Query query = getEntityManager().createQuery(hql.toString());
		query.setParameter("tenantId", tenantId);

		List<String> list = query.getResultList();
		for (String id : list) {
			StringBuilder hsql = new StringBuilder("delete FROM PROC_PRODUCT_CONTRACT_ITEM pc where pc.CONTRACT_ID = :id");
			Query query1 = getEntityManager().createNativeQuery(hsql.toString());
			query1.setParameter("id", id);
			query1.executeUpdate();
		}

		StringBuilder hsql = new StringBuilder("delete FROM PROC_PRODUCT_CONTRACT pc where pc.TENANT_ID= :tenantId");
		Query query1 = getEntityManager().createNativeQuery(hsql.toString());
		query1.setParameter("tenantId", tenantId);
		query1.executeUpdate();

	}

	@Override
	@SuppressWarnings("unchecked")
	public List<ProductContract> contractExpiryNotificationReminderBefore30Days() {
		final Query query = getEntityManager().createQuery("select t from ProductContract t where t.status =:status and t.remBefore30Day = :remBefore30Day");
		query.setParameter("status", ContractStatus.ACTIVE);
		query.setParameter("remBefore30Day", Boolean.FALSE);
		return query.getResultList();
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<ProductContract> contractExpiryNotificationReminderBefore90Days() {
		final Query query = getEntityManager().createQuery("select t from ProductContract t where t.status =:status and t.remBefore90Day = :remBefore90Day");
		query.setParameter("status", ContractStatus.ACTIVE);
		query.setParameter("remBefore90Day", Boolean.FALSE);
		return query.getResultList();
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<ProductContract> contractExpiryNotificationReminderBefore180Days() {
		final Query query = getEntityManager().createQuery("select t from ProductContract t where t.status =:status and t.remBefore180Day = :remBefore180Day");
		query.setParameter("status", ContractStatus.ACTIVE);
		query.setParameter("remBefore180Day", Boolean.FALSE);
		return query.getResultList();
	}

	@Override
	public long findNewUpcomingContractByTeanantId(String tenantId, String userId) {
		String sql = "";
		if (userId != null) {
			sql = "select count(distinct t) from ProductContract t left outer join t.teamMembers tm left outer join tm.user tmu join t.contractCreator cc where t.status = :status and t.buyer.id = :tenantId and t.contractStartDate > :currentDate and ( cc.id = :userId  or tmu.id = :userId ) ";
		} else {
			sql = "select count(distinct t) from ProductContract t where t.status = :status and t.buyer.id = :tenantId and t.contractStartDate > :currentDate ";
		}
		final Query query = getEntityManager().createQuery(sql);
		query.setParameter("status", ContractStatus.APPROVED);
		query.setParameter("currentDate", new Date());
		query.setParameter("tenantId", tenantId);
		if (userId != null) {
			query.setParameter("userId", userId);
		}
		return (long) query.getSingleResult();
	}

	@Override
	public long findContractBefore30DayExpireByTeanantId(String tenantId, String userId) {
		String sql = "";
		if (userId != null) {
			sql = "select count(distinct t) from ProductContract t left outer join t.teamMembers tm left outer join tm.user tmu join t.contractCreator cc where t.status = :status and t.buyer.id = :tenantId and t.contractEndDate >= :currentDate and t.contractEndDate <= :expiredDate and ( cc.id = :userId  or tmu.id = :userId ) ";
		} else {
			sql = "select count(distinct t) from ProductContract t where t.status = :status and t.buyer.id = :tenantId and t.contractEndDate >= :currentDate and t.contractEndDate <= :expiredDate ";
		}
		final Query query = getEntityManager().createQuery(sql);
		query.setParameter("currentDate", new Date());
		query.setParameter("expiredDate", getExpiredDate(30));
		query.setParameter("status", ContractStatus.ACTIVE);
		query.setParameter("tenantId", tenantId);
		if (userId != null) {
			query.setParameter("userId", userId);
		}
		return (long) query.getSingleResult();
	}

	@Override
	public long findContractBefore90DayExpireByTeanantId(String tenantId, String userId) {
		String sql = "";
		if (userId != null) {
			sql = "select count(distinct t) from ProductContract t left outer join t.teamMembers tm left outer join tm.user tmu join t.contractCreator cc where t.status = :status and t.buyer.id = :tenantId and t.contractEndDate >= :currentDate and t.contractEndDate <= :expiredDate and ( cc.id = :userId  or tmu.id = :userId ) ";
		} else {
			sql = "select count(distinct t) from ProductContract t where t.status = :status and t.buyer.id = :tenantId and t.contractEndDate >= :currentDate and t.contractEndDate <= :expiredDate  ";
		}
		final Query query = getEntityManager().createQuery(sql);
		query.setParameter("status", ContractStatus.ACTIVE);
		query.setParameter("currentDate", getDate(30));
		query.setParameter("expiredDate", getExpiredDate(90));
		query.setParameter("tenantId", tenantId);
		if (userId != null) {
			query.setParameter("userId", userId);
		}
		return (long) query.getSingleResult();
	}

	@Override
	public long findContractBefore180DayExpireByTeanantId(String tenantId, String userId) {
		String sql = "";
		if (userId != null) {
			sql = "select count(distinct t) from ProductContract t left outer join t.teamMembers tm left outer join tm.user tmu join t.contractCreator cc where t.status = :status and t.buyer.id = :tenantId and t.contractEndDate >= :currentDate and t.contractEndDate <= :expiredDate and ( cc.id = :userId  or tmu.id = :userId ) ";
		} else {
			sql = "select count(distinct t) from ProductContract t where t.status = :status and t.buyer.id = :tenantId and t.contractEndDate >= :currentDate and t.contractEndDate <= :expiredDate  ";
		}
		final Query query = getEntityManager().createQuery(sql);
		query.setParameter("status", ContractStatus.ACTIVE);
		query.setParameter("currentDate", getDate(90));
		query.setParameter("expiredDate", getExpiredDate(180));
		query.setParameter("tenantId", tenantId);
		if (userId != null) {
			query.setParameter("userId", userId);
		}
		return (long) query.getSingleResult();
	}

	@Override
	public long findContractGreaterThanSixMonthExpireByTeanantId(String tenantId, String userId) {
		String sql = "";
		if (userId != null) {
			sql = "select count(distinct t) from ProductContract t left outer join t.teamMembers tm left outer join tm.user tmu join t.contractCreator cc where t.status = :status and t.buyer.id = :tenantId and t.contractEndDate > :currentDate and ( cc.id = :userId  or tmu.id = :userId ) ";
		} else {
			sql = "select count(distinct t) from ProductContract t where t.status = :status and t.buyer.id = :tenantId and t.contractEndDate > :currentDate ";
		}
		final Query query = getEntityManager().createQuery(sql);
		query.setParameter("status", ContractStatus.ACTIVE);
		query.setParameter("currentDate", getDate(180));
		query.setParameter("tenantId", tenantId);
		if (userId != null) {
			query.setParameter("userId", userId);
		}
		return (long) query.getSingleResult();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ProductContractPojo> findContractListByExpiredDaysBetweenForTenant(String tenantId, String userId, TableDataInput input, Date currentDate, Date expiredDate, boolean isNewUpcoming, boolean isbetween, boolean greaterThanSixMonth, boolean isExpired, Date startDate, Date endDate, ContractStatus contractStatus) {
		final Query query = constructContractListByExpiredDaysBetweenForTenant(tenantId, userId, input, false, currentDate, expiredDate, isNewUpcoming, isbetween, greaterThanSixMonth, isExpired, startDate, endDate, contractStatus);
		query.setFirstResult(input.getStart());
		query.setMaxResults(input.getLength());
		return query.getResultList();
	}

	@Override
	public long findTotalFilteredContractByExpiredDaysBetweenForTenant(String tenantId, String userId, TableDataInput input, Date currentDate, Date expiredDate, boolean isNewUpcoming, boolean isbetween, boolean greaterThanSixMonth, boolean isExpired, Date startDate, Date endDate, ContractStatus contractStatus) {
		final Query query = constructContractListByExpiredDaysBetweenForTenant(tenantId, userId, input, true, currentDate, expiredDate, isNewUpcoming, isbetween, greaterThanSixMonth, isExpired, startDate, endDate, contractStatus);
		return ((Number) query.getSingleResult()).longValue();
	}

	private Query constructContractListByExpiredDaysBetweenForTenant(String tenantId, String userId, TableDataInput tableParams, boolean isCount, Date currentDate, Date expiredDate, boolean isNewUpcoming, boolean isbetween, boolean greaterThanSixMonth, boolean isExpired, Date startDate, Date endDate, ContractStatus contractStatus) {

		String hql = "";

		// If count query is enabled, then add the select count(*) clause
		if (isCount) {
			hql += "select count(distinct t) ";
		}
		if (!isCount) {
			hql += "select distinct new com.privasia.procurehere.core.pojo.ProductContractPojo(t.id, t.contractName, t.eventId, t.contractReferenceNumber, t.groupCodeStr, t.contractStartDate, t.contractEndDate, t.contractValue, t.status, t.createdDate, t.modifiedDate, cb.loginId, mb.loginId, s.companyName, fs.vendorCode, cb.communicationEmail, bu.unitName, bu.unitCode, t.decimal, t.contractId, c.currencyCode) from ProductContract t ";
		} else {
			hql += " from ProductContract t ";
		}
		// If this is not a count query, only then add the join fetch. Count query does not require its
		if (!isCount) {
			hql += " left outer join t.contractCreator cb left outer join t.modifiedBy mb left outer join t.supplier fs left outer join fs.supplier s left outer join t.businessUnit bu left outer join t.currency c left outer join t.teamMembers tm left outer join tm.user tmu ";
		} else {
			hql += " left outer join t.contractCreator cb left outer join t.modifiedBy mb left outer join t.supplier fs left outer join fs.supplier s left outer join t.businessUnit bu left outer join t.currency c left outer join t.teamMembers tm left outer join tm.user tmu ";
		}

		hql += " where t.buyer.id = :tenantId ";

		if (userId != null) {
			hql += ("and (cb.id = :userId or tmu.id = :userId ) ");
		}

		if (isNewUpcoming) {
			hql += " and t.contractStartDate > :currentDate";
			hql += " and t.status = :status ";
		}
		if (isbetween) {
			hql += " and t.contractEndDate >= :currentDate and t.contractEndDate <= :expiredDate";
			hql += " and t.status = :status ";
		}
		if (greaterThanSixMonth) {
			hql += " and  t.contractEndDate > :currentDate";
			hql += " and t.status = :status ";
		}
		if (isExpired) {
			hql += " and t.status = :status ";
		}
		if (contractStatus != null) {
			hql += " and t.status = :status ";
		}

		// Add on search filter conditions
		for (ColumnParameter cp : tableParams.getColumns()) {
			if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
				if (cp.getData().equals("status")) {
					hql += " and t.status = (:" + cp.getData() + ")";
				} else if (cp.getData().equals("typeString")) {
					hql += " and t.type = (:type)";
				} else if (cp.getData().equals("vendorCode") || cp.getData().equals("companyName")) {
					hql += " and upper(s.companyName) like (:" + cp.getData() + ")";
				} else if (cp.getData().equals("createdBy")) {
					hql += " and upper(cb.loginId) like (:" + cp.getData() + ")";
				} else if (cp.getData().equals("modifiedBy")) {
					hql += " and upper(mb.loginId) like (:" + cp.getData() + ")";
				} else if (cp.getData().equals("referenceNumber")) {
					hql += " and upper(t.contractReferenceNumber)  like (:" + cp.getData() + ")";
				} else if (cp.getData().equals("groupcode")) {
					hql += " and upper(t.groupCodeStr) like (:" + cp.getData() + ")";
				} else if (cp.getData().equals("currencyCode")) {
					hql += " and upper(c.currencyCode) like (:" + cp.getData() + ")";
				} else if (cp.getData().equals("businessUnit")) {
					hql += " and upper(bu.unitName) like (:" + cp.getData() + ")";
				} else if (cp.getData().equals("creatorEmail")) {
					hql += " and upper(cb.communicationEmail) like (:" + cp.getData() + ")";
				} else if (cp.getData().equals("contractValue")) {
					try {
						new BigDecimal(cp.getSearch().getValue());
						hql += " and t." + cp.getData() + " = (:" + cp.getData().replace(".", "") + ")";
					} catch (Exception e) {
					}
				} else {
					hql += " and upper(t." + cp.getData() + ") like (:" + cp.getData().replace(".", "") + ")";
				}
			}
		}

		// search with Date range
		if (startDate != null && endDate != null) {
			hql += " and  t.createdDate between :startDate and :endDate ";
		}
		// If it is not a count query then add order by clause
		if (!isCount) {
			List<OrderParameter> orderList = tableParams.getOrder();
			if (CollectionUtil.isNotEmpty(orderList)) {
				hql += " order by ";
				for (OrderParameter order : orderList) {
					String orderColumn = tableParams.getColumns().get(order.getColumn()).getData();
					String dir = order.getDir();
					if (orderColumn.equals("vendorCode") || orderColumn.equals("companyName")) {
						hql += " s.companyName " + dir + ",";
					} else if (orderColumn.equals("createBy")) {
						hql += " cb.loginId " + dir + ",";
					} else if (orderColumn.equals("modifiedBy")) {
						hql += " mb.loginId " + dir + ",";
					} else if (orderColumn.equals("currencyCode")) {
						hql += " c.currencyCode " + dir + ",";
					} else {
						hql += " t." + orderColumn + " " + dir + ",";
					}
				}
				if (hql.lastIndexOf(",") == hql.length() - 1) {
					hql = hql.substring(0, hql.length() - 1);
				}
			} else {
				hql += " order by t.contractReferenceNumber ";
			}
		}

		//LOG.info("HQL : " + hql + "  Tenant Id : " + tenantId);

		final Query query = getEntityManager().createQuery(hql.toString());
		query.setParameter("tenantId", tenantId);
		if (userId != null) {
			query.setParameter("userId", userId);
		}

		if (isNewUpcoming) {
			query.setParameter("currentDate", currentDate);
			query.setParameter("status", ContractStatus.APPROVED);
		}

		if (isbetween) {
			query.setParameter("currentDate", currentDate);
			query.setParameter("expiredDate", expiredDate);
			query.setParameter("status", ContractStatus.ACTIVE);
		}
		if (greaterThanSixMonth) {
			query.setParameter("currentDate", currentDate);
			query.setParameter("status", ContractStatus.ACTIVE);
		}
		if (isExpired) {
			query.setParameter("status", ContractStatus.EXPIRED);
		}
		if (contractStatus != null) {
			query.setParameter("status", contractStatus);
		}

		// Apply search filter values
		for (ColumnParameter cp : tableParams.getColumns()) {
			if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
				if (cp.getData().equals("status")) {
					query.setParameter("status", ContractStatus.valueOf(cp.getSearch().getValue()));
				} else if (cp.getData().equals("contractValue")) {
					LOG.info("Amount value  : " + cp.getSearch().getValue());
					try {
						new BigDecimal(cp.getSearch().getValue());
						query.setParameter(cp.getData().replace(".", ""), new BigDecimal(cp.getSearch().getValue()));
					} catch (Exception e) {
					}
				} else {
					LOG.info("Search by : " + cp.getData() + " value : " + cp.getSearch().getValue().toUpperCase());
					query.setParameter(cp.getData().replace(".", ""), "%" + cp.getSearch().getValue().toUpperCase() + "%");
				}
			}
		}
		// set parameter Date range
		if (startDate != null && endDate != null) {
			query.setParameter("startDate", startDate);
			query.setParameter("endDate", endDate);
		}
		return query;
	}

	private static Date getDate(int current) {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_MONTH, current);
		return cal.getTime();

	}

	private static Date getExpiredDate(int expired) {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_MONTH, expired);
		return cal.getTime();

	}

	@Override
	public long findTotalContractEventCountForCsv(String tenantId) {
		String sql = "";
		sql += " select count(distinct t) from ProductContract t ";
		sql += " where t.buyer.id =:tenantId";

		Query query = getEntityManager().createQuery(sql);
		query.setParameter("tenantId", tenantId);

		try {
			return ((Number) query.getSingleResult()).longValue();
		} catch (Exception e) {
			LOG.error(e.getMessage());
			return 0;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ProductContractPojo> findProductContractListForCsvTenantRecords(String tenantId, String userId, Date startDate, Date endDate, boolean isbetween, boolean greaterThanSixMonth, ContractStatus status, Date expiryFrom, Date expiryTo) {
		final Query query = constructContractListByExpiredDaysBetweenForTenantForCsv(tenantId, userId, startDate, endDate, isbetween, greaterThanSixMonth, status, expiryFrom, expiryTo);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean isExists(String contractReferenceNumber, String buyerId, String contractId) {
		StringBuilder hsql = new StringBuilder("from ProductContract as pc inner join fetch pc.buyer as b where upper(pc.contractReferenceNumber) = :contractReferenceNumber and b.id = :buyerId");
		if (StringUtils.checkString(contractId).length() > 0) {
			hsql.append(" and pc.id <> :id");
		}
		final Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("contractReferenceNumber", contractReferenceNumber.toUpperCase());
		query.setParameter("buyerId", buyerId);
		if (StringUtils.checkString(contractId).length() > 0) {
			query.setParameter("id", contractId);
		}
		List<ProductItem> scList = query.getResultList();
		if (CollectionUtil.isNotEmpty(scList)) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public ContractProductItemPojo getProductItemListByProductItemId(String productItemId) {
		StringBuilder hsql = new StringBuilder("select distinct new com.privasia.procurehere.core.pojo.ContractProductItemPojo(t.brand, t.tax, um.id, pct.id, pct.productCode, pct.productName, t.unitPrice, t.productItemType, t.productCode) from ProductItem t left outer join t.productCategory pct left outer join t.uom um  where t.id = :productItemId");

		final Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("productItemId", productItemId);
		List<ContractProductItemPojo> scList = query.getResultList();
		if (CollectionUtil.isNotEmpty(scList)) {
			return scList.get(0);
		} else {
			return null;
		}

	}

	@Override
	public long findDraftContractByTenantId(String tenantId, String userId) {
		String sql = "";
		if (userId != null) {
			sql = "select count(distinct t) from ProductContract t left outer join t.teamMembers tm left outer join tm.user tmu join t.contractCreator cc where t.status = :status and t.buyer.id = :tenantId and ( cc.id = :userId  or tmu.id = :userId ) ";
		} else {
			sql = "select count(distinct t) from ProductContract t where t.status = :status and t.buyer.id = :tenantId ";
		}
		final Query query = getEntityManager().createQuery(sql);
		query.setParameter("status", ContractStatus.DRAFT);
		query.setParameter("tenantId", tenantId);
		if (userId != null) {
			query.setParameter("userId", userId);
		}
		return (long) query.getSingleResult();
	}

	@Override
	public long findPendingContractByTenantId(String tenantId, String userId) {
		String sql = "";
		if (userId != null) {
			sql = "select count(distinct t) from ProductContract t left outer join t.teamMembers tm left outer join tm.user tmu join t.contractCreator cc where t.status = :status and t.buyer.id = :tenantId and ( cc.id = :userId  or tmu.id = :userId ) ";
		} else {
			sql = "select count(distinct t) from ProductContract t where t.status = :status and t.buyer.id = :tenantId ";
		}
		final Query query = getEntityManager().createQuery(sql);
		query.setParameter("status", ContractStatus.PENDING);
		query.setParameter("tenantId", tenantId);
		if (userId != null) {
			query.setParameter("userId", userId);
		}
		return (long) query.getSingleResult();
	}

	@Override
	public long findContractByStatusForTeanant(String tenantId, String userId, ContractStatus contractStatus) {
		String sql = "";
		if (userId != null) {
			sql = "select count(distinct t) from ProductContract t left outer join t.teamMembers tm left outer join tm.user tmu join t.contractCreator cc where t.status = :status and t.buyer.id = :tenantId and ( cc.id = :userId  or tmu.id = :userId ) ";
		} else {
			sql = "select count(distinct t) from ProductContract t where t.status = :status and t.buyer.id = :tenantId ";
		}
		final Query query = getEntityManager().createQuery(sql);
		query.setParameter("status", contractStatus);
		query.setParameter("tenantId", tenantId);
		if (userId != null) {
			query.setParameter("userId", userId);
		}
		return (long) query.getSingleResult();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ContractPojo> findProductContractListForTenantForCsv(String tenantId, String userId, TableDataInput tableParams, Date startDate, Date endDate) {
		final Query query = constructProductListForTenantForCsvQuery(tenantId, userId, tableParams, false, startDate, endDate);
		query.setFirstResult(tableParams.getStart());
		query.setMaxResults(tableParams.getLength());
		return query.getResultList();
	}

	// download csv query
	private Query constructProductListForTenantForCsvQuery(String tenantId, String userId, TableDataInput tableParams, boolean isCount, Date startDate, Date endDate) {

		String hql = "";

		// If count query is enabled, then add the select count(*) clause
		if (isCount) {
			hql += "select count(distinct t) ";
		}
		if (!isCount) {
			hql += "select distinct new com.privasia.procurehere.core.pojo.ContractPojo(t.id, t.contractId, t.contractName, t.eventId, t.contractReferenceNumber, t.previousContractNo, t.renewalContract, s.companyName, fs.vendorCode, bu.unitName, bu.unitCode, t.groupCodeStr, pc.procurementCategories, at.agreementType, c.currencyCode, t.contractValue, t.contractStartDate, t.contractEndDate, t.contractReminderDates, cla.loaDate, cla.agreementDate, cb.loginId, t.createdDate, mb.loginId, t.modifiedDate, t.status,  t.decimal, cla.loaFileName, cla.agreementFileName) from ProductContract t ";
		} else {
			hql += " from ProductContract t ";
		}
		// If this is not a count query, only then add the join fetch. Count query does not require its
		if (!isCount) {
			hql += " left outer join t.contractCreator cb left outer join t.modifiedBy mb left outer join t.supplier fs left outer join fs.supplier s left outer join t.businessUnit bu left outer join t.currency c left outer join t.procurementCategory pc left outer join t.agreementType at left outer join t.contractLoaAndAgreement cla left outer join t.teamMembers tm left outer join tm.user tmu  ";
		} else {
			hql += " left outer join t.contractCreator cb left outer join t.modifiedBy mb left outer join t.supplier fs left outer join fs.supplier s left outer join t.businessUnit bu left outer join t.currency c left outer join t.procurementCategory pc left outer join t.agreementType at left outer join t.contractLoaAndAgreement cla left outer join t.teamMembers tm left outer join tm.user tmu ";
		}

		hql += " where t.buyer.id = :tenantId ";

		if (userId != null) {
			hql += " and (cb.id = :userId or tmu.id = :userId ) ";
		}

		if (startDate != null && endDate != null) {
			hql += " and t.createdDate between :startDate and :endDate ";
		}

		// Add on search filter conditions
		for (ColumnParameter cp : tableParams.getColumns()) {
			if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
				LOG.info(">>>>>>>>>>>>>>>>>>>> " + cp.getData() + ".................. " + cp.getSearch().getValue());

				if (cp.getData().equals("status")) {
					hql += " and t.status = (:" + cp.getData() + ")";
				} else if (cp.getData().equals("typeString")) {
					hql += " and t.type = (:type)";
				} else if (cp.getData().equals("vendorCode") || cp.getData().equals("companyName")) {
					hql += " and upper(s.companyName) like (:" + cp.getData() + ")";
				} else if (cp.getData().equals("createdBy")) {
					hql += " and upper(cb.loginId) like (:" + cp.getData() + ")";
				} else if (cp.getData().equals("modifiedBy")) {
					hql += " and upper(mb.loginId) like (:" + cp.getData() + ")";
				} else if (cp.getData().equals("eventIds")) {
					hql += " and t.id in (:eventIds) ";
				} else if (cp.getData().equals("referenceNumber")) {
					hql += " and upper(t.contractReferenceNumber)  like (:" + cp.getData() + ")";
				} else if (cp.getData().equals("groupcode")) {
					hql += " and upper(t.groupCodeStr) like (:" + cp.getData() + ")";
				} else if (cp.getData().equals("currencyCode")) {
					hql += " and upper(c.currencyCode) like (:" + cp.getData() + ")";
				} else if (cp.getData().equals("supplier")) {
					hql += " and upper(s.companyName) like (:" + cp.getData() + ")";
				} else if (cp.getData().equals("businessUnit")) {
					hql += " and upper(bu.unitName) like (:" + cp.getData() + ")";
				} else if (cp.getData().equals("creatorEmail")) {
					hql += " and upper(cb.communicationEmail) like (:" + cp.getData() + ")";
				} else if (cp.getData().equals("contractValue")) {
					try {
						new BigDecimal(cp.getSearch().getValue());
						hql += " and t." + cp.getData() + " = (:" + cp.getData().replace(".", "") + ")";
					} catch (Exception e) {
					}
				} else {
					hql += " and upper(t." + cp.getData() + ") like (:" + cp.getData().replace(".", "") + ")";
				}
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
					if (orderColumn.equals("vendorCode") || orderColumn.equals("companyName")) {
						hql += " s.companyName " + dir + ",";
					} else if (orderColumn.equals("createBy")) {
						hql += " cb.loginId " + dir + ",";
					} else if (orderColumn.equals("modifiedBy")) {
						hql += " mb.loginId " + dir + ",";
					} else if (orderColumn.equals("currencyCode")) {
						hql += " c.currencyCode " + dir + ",";
					} else {
						hql += " t." + orderColumn + " " + dir + ",";
					}
				}
				if (hql.lastIndexOf(",") == hql.length() - 1) {
					hql = hql.substring(0, hql.length() - 1);
				}
			} else {
				hql += " order by t.contractReferenceNumber ";
			}
		}

		LOG.info("HQL : " + hql);

		final Query query = getEntityManager().createQuery(hql.toString());
		query.setParameter("tenantId", tenantId);
		if (userId != null) {
			query.setParameter("userId", userId);
		}
		if (startDate != null && endDate != null) {
			query.setParameter("startDate", startDate);
			query.setParameter("endDate", endDate);
		}

		// Apply search filter values
		for (ColumnParameter cp : tableParams.getColumns()) {
			if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
				if (cp.getData().equals("status")) {
					query.setParameter("status", ContractStatus.valueOf(cp.getSearch().getValue()));
				} else if (cp.getData().equals("contractValue")) {
					LOG.info("Contract value  : " + cp.getSearch().getValue());
					try {
						new BigDecimal(cp.getSearch().getValue());
						query.setParameter(cp.getData().replace(".", ""), new BigDecimal(cp.getSearch().getValue()));
					} catch (Exception e) {
					}
				} else if (cp.getData().equals("eventIds")) {
					query.setParameter("eventIds", Arrays.asList(cp.getSearch().getValue().split(",")));
				} else {
					query.setParameter(cp.getData().replace(".", ""), "%" + cp.getSearch().getValue().toUpperCase() + "%");
				}
			}
		}
		return query;
	}

	/**
	 * @param tenantId
	 * @param userId
	 * @param startDate
	 * @param endDate
	 * @param isbetween
	 * @param greaterThanSixMonth
	 * @param status TODO
	 * @param expiryFrom TODO
	 * @param expiryTo TODO
	 * @param tableParams
	 * @param isCount
	 * @return
	 */
	private Query constructContractListByExpiredDaysBetweenForTenantForCsv(String tenantId, String userId, Date startDate, Date endDate, boolean isbetween, boolean greaterThanSixMonth, ContractStatus status, Date expiryFrom, Date expiryTo) {

		String hql = "";

		hql += "select distinct new com.privasia.procurehere.core.pojo.ProductContractPojo(t.id, t.contractId, t.contractName, t.eventId, t.contractReferenceNumber, t.previousContractNo, t.renewalContract, s.companyName, fs.vendorCode, bu.unitName, bu.unitCode, t.groupCodeStr, pc.procurementCategories, at.agreementType, c.currencyCode, t.contractValue, t.contractStartDate, t.contractEndDate, t.contractReminderDates, cla.loaDate, cla.agreementDate, cb.loginId, t.createdDate, mb.loginId, t.modifiedDate, t.status,  t.decimal, cla.loaFileName, cla.agreementFileName) from ProductContract t ";
		hql += " left outer join t.contractCreator cb left outer join t.modifiedBy mb left outer join t.supplier fs left outer join fs.supplier s left outer join t.businessUnit bu left outer join t.currency c left outer join t.procurementCategory pc left outer join t.agreementType at left outer join t.contractLoaAndAgreement cla left outer join t.teamMembers tm left outer join tm.user tmu ";
		hql += " where t.buyer.id = :tenantId ";

		if (userId != null) {
			hql += " and (cb.id = :userId or tmu.id = :userId) ";
		}
		if (isbetween) {
			hql += " and t.contractEndDate >= :expiryFrom and t.contractEndDate <= :expiryTo";
		}
		if (greaterThanSixMonth) {
			hql += " and  t.contractEndDate > :expiryFrom";
		}
		if (startDate != null && endDate != null) {
			hql += " and t.createdDate between :startDate and :endDate";
		}

		if (status != null) {
			hql += " and t.status = :status ";
		}

		hql += " order by t.contractReferenceNumber ";

		LOG.info("HQL : " + hql + "  Tenant Id : " + tenantId);

		final Query query = getEntityManager().createQuery(hql.toString());
		query.setParameter("tenantId", tenantId);
		if (userId != null) {
			query.setParameter("userId", userId);
		}

		if (isbetween) {
			query.setParameter("expiryFrom", expiryFrom);
			query.setParameter("expiryTo", expiryTo);
		}
		if (greaterThanSixMonth) {
			query.setParameter("expiryFrom", expiryFrom);
		}
		if (startDate != null && endDate != null) {
			query.setParameter("startDate", startDate);
			query.setParameter("endDate", endDate);
		}

		if (status != null) {
			query.setParameter("status", status);
		}
		return query;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ContractTeamMember> getPlainTeamMembersForContract(String contractId) {
		final Query query = getEntityManager().createQuery("select distinct new com.privasia.procurehere.core.entity.ContractTeamMember(tm.id, tm.teamMemberType, u.id, u.loginId, u.name, u.communicationEmail, u.emailNotifications, u.tenantId, u.deleted)from ContractTeamMember tm left outer join tm.user u where tm.productContract.id =:contractId");
		query.setParameter("contractId", contractId);
		List<ContractTeamMember> list = query.getResultList();
		return list;
	}

	@SuppressWarnings("unchecked")
	@Override
	public ContractTeamMember getContractTeamMemberByUserIdAndContractId(String contractId, String userId) {
		StringBuilder hsql = new StringBuilder("from ContractTeamMember tm where tm.productContract.id = :contractId and tm.user.id = :userId");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("contractId", contractId);
		query.setParameter("userId", userId);
		List<ContractTeamMember> uList = query.getResultList();
		if (CollectionUtil.isNotEmpty(uList)) {
			return uList.get(0);
		} else {
			return null;
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<ContractApprovalUser> fetchAllApprovalUsersByContractId(String id) {
		final Query query = getEntityManager().createQuery("select distinct apu from ContractApproval pa left outer join pa.approvalUsers apu inner join apu.user us where pa.productContract.id =:id");
		query.setParameter("id", id);
		return query.getResultList();
	}

	@Override
	public ProductContract findProductContractForApprovalById(String id) {
		StringBuilder hsql = new StringBuilder("from ProductContract p left outer join fetch p.approvals ap join fetch p.createdBy cb left outer join fetch p.modifiedBy mb where p.id= :id");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", id);
		ProductContract productContract = (ProductContract) query.getSingleResult();
		return productContract;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ContractComment> findAllContractCommentsByContractId(String id) {
		StringBuilder hsql = new StringBuilder("select distinct cc from ContractComment cc inner join fetch cc.productContract p left outer join fetch cc.createdBy where p.id= :id order by cc.createdDate");
		final Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", id);
		List<ContractComment> contractCommentList = query.getResultList();
		return contractCommentList;
	}

	@Override
	public EventPermissions getUserPemissionsForContract(String userId, String contractId) {
		LOG.debug("userId :" + userId + " contractId: " + contractId);
		EventPermissions permissions = new EventPermissions();

		User loggedInUser = userDao.findById(userId);
		LOG.info("Login id********:" + loggedInUser.getLoginId());
		if (UserType.APPROVER_USER == loggedInUser.getUserType()) {
			LOG.info("*************Checking userType");
			permissions.setApproverUser(true);
		}

		// Event Owner
		ProductContract contract = findById(contractId);
		if (contract != null && contract.getStatus() == ContractStatus.DRAFT) {
			permissions.setPrDraft(true);
			permissions.setPrId(contract.getId());
		}
		if (contract.getContractCreator() != null && contract.getContractCreator().getId().equals(userId)) {
			permissions.setOwner(true);
		} else {
			// Viewer Editor
			List<ContractTeamMember> teamMembers = contract.getTeamMembers();
			for (ContractTeamMember member : teamMembers) {
				if (member.getUser().getId().equals(userId)) {
					if (member.getTeamMemberType() == TeamMemberType.Viewer) {
						permissions.setViewer(true);
					}
					if (member.getTeamMemberType() == TeamMemberType.Editor) {
						permissions.setEditor(true);
						permissions.setViewer(false);
						// break;
					}
					if (member.getTeamMemberType() == TeamMemberType.Associate_Owner) {
						permissions.setEditor(false);
						permissions.setViewer(false);
						permissions.setOwner(true);
						break;
					}
				}
			}
		}

		// Approver
		List<ContractApproval> approvals = contract.getApprovals();
		for (ContractApproval approval : approvals) {
			if (CollectionUtil.isNotEmpty(approval.getApprovalUsers())) {
				List<ContractApprovalUser> users = approval.getApprovalUsers();
				for (ContractApprovalUser user : users) {
					if (user.getUser().getId().equals(userId)) {
						permissions.setApprover(true);
						if (approval.isActive() && ApprovalStatus.PENDING == user.getApprovalStatus()) {
							permissions.setActiveApproval(true);
							break;
						}
					}
				}
			}
		}
		LOG.debug("permissions : " + permissions.toLogString());
		return permissions;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ProductContractPojo> getAllContractForApproval(String tenantId, String userId, TableDataInput input) {
		final Query query = constructContractApprovalQueryByTenant(input, tenantId, userId, false);
		query.setFirstResult(input.getStart());
		query.setMaxResults(input.getLength());
		return query.getResultList();
	}

	@Override
	public long findCountOfContractPendingApprovals(String tenantId, String userId, TableDataInput input) {
		String hql = "select count(distinct b) from ProductContract b left outer join b.approvals app left outer join app.approvalUsers usr where b.buyer.id =:tenantId and b.status =:contractStatus and (app.productContract.id = b.id and usr.user.id = :userId and usr.approvalStatus = :approvalStatus and app.active = true )";
		final Query query = getEntityManager().createQuery(hql);
		query.setParameter("tenantId", tenantId);
		query.setParameter("userId", userId);
		query.setParameter("approvalStatus", ApprovalStatus.PENDING);
		query.setParameter("contractStatus", ContractStatus.PENDING);
		return ((Number) query.getSingleResult()).longValue();
	}

	@Override
	public long findTotalFilteredContracForApproval(String tenantId, String userId, TableDataInput input) {
		final Query query = constructContractApprovalQueryByTenant(input, tenantId, userId, true);
		return ((Number) query.getSingleResult()).longValue();
	}

	private Query constructContractApprovalQueryByTenant(TableDataInput input, String tenantId, String userId, boolean isCount) {
		String sql = "";

		if (isCount) {
			sql += "select count(distinct t) ";
		}
		if (!isCount) {
			sql += "select distinct new com.privasia.procurehere.core.pojo.ProductContractPojo(t.id, t.contractId, t.contractName, t.eventId, t.contractReferenceNumber, t.groupCodeStr, t.contractStartDate, t.contractEndDate, s.companyName, fs.vendorCode, bu.unitName, bu.unitCode, c.currencyCode, t.contractValue, cc.name, t.createdDate, t.status) from ProductContract t ";
		} else {
			sql += " from ProductContract t ";
		}
		// If this is not a count query, only then add the join fetch. Count query does not require its
		if (!isCount) {
			sql += " left outer join t.supplier fs left outer join fs.supplier s left outer join t.businessUnit bu left outer join t.approvals app left outer join app.approvalUsers usr left outer join t.currency c left join t.contractCreator cc ";
		} else {
			sql += " left outer join t.supplier fs left outer join fs.supplier s left outer join t.businessUnit bu left outer join t.approvals app left outer join app.approvalUsers usr  ";
		}

		sql += " where t.buyer.id =:tenantId and t.status =:contractStatus and (app.productContract.id = t.id and usr.user.id = :userId and usr.approvalStatus = :approvalStatus and app.active = true) ";

		// Add on search filter conditions
		for (ColumnParameter cp : input.getColumns()) {
			if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
				LOG.info("INPUT :: " + cp.getData() + "VALUE :: " + cp.getSearch().getValue());
				if (cp.getData().equals("supplier")) {
					sql += " and upper(s.companyName) like (:" + cp.getData() + ")";
				} else if (cp.getData().equals("referenceNumber")) {
					sql += " and upper(t.contractReferenceNumber)  like (:" + cp.getData() + ")";
				} else if (cp.getData().equals("groupcode")) {
					sql += " and upper(t.groupCodeStr) like (:" + cp.getData() + ")";
				} else if (cp.getData().equals("businessUnit")) {
					sql += " and upper(bu.unitName) like (:" + cp.getData() + ")";
				} else if (cp.getData().equals("creatorEmail")) {
					sql += " and upper(cb.communicationEmail) like (:" + cp.getData() + ")";
				} else if (cp.getData().equals("contractValue")) {
					try {
						new BigDecimal(cp.getSearch().getValue());
						sql += " and t." + cp.getData() + " = (:" + cp.getData().replace(".", "") + ")";
					} catch (Exception e) {
					}
				} else {
					sql += " and upper(t." + cp.getData() + ") like (:" + cp.getData().replace(".", "") + ")";
				}
			}
		}

		if (!isCount) {
			List<OrderParameter> orderList = input.getOrder();
			if (CollectionUtil.isNotEmpty(orderList)) {
				sql += " order by ";
				for (OrderParameter order : orderList) {
					String orderColumn = input.getColumns().get(order.getColumn()).getData();
					String dir = order.getDir();
					if (orderColumn.equals("businessUnitName")) {
						orderColumn = " t.businessUnit.unitName";
					} else if (orderColumn.equals("costCenterName")) {
						orderColumn = " t.costCenter.costCenter";
					} else if (orderColumn.equals("supplier") || orderColumn.equals("companyName")) {
						orderColumn = " s.companyName";
					} else if (orderColumn.equals("vendorCode")) {
						orderColumn = " fs.vendorCode";
					} else if (orderColumn.equals("currencyCode")) {
						orderColumn = " c.currencyCode";
					} else {
						orderColumn = " t." + orderColumn;
					}

					sql += " " + orderColumn + " " + dir + ",";

					if (sql.lastIndexOf(",") == sql.length() - 1) {
						sql = sql.substring(0, sql.length() - 1);
					}
				}
			}
		}

		LOG.info("SQL : " + sql);

		final Query query = getEntityManager().createQuery(sql);
		query.setParameter("tenantId", tenantId);
		query.setParameter("userId", userId);
		query.setParameter("approvalStatus", ApprovalStatus.PENDING);
		query.setParameter("contractStatus", ContractStatus.PENDING);

		for (ColumnParameter cp : input.getColumns()) {
			if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
				query.setParameter(cp.getData().replace(".", ""), "%" + cp.getSearch().getValue().toUpperCase() + "%");
			}
		}
		if (!isCount) {
			query.setFirstResult(input.getStart());
			query.setMaxResults(input.getLength());
		}
		return query;
	}

	@Override
	public long findTotalCountContractForApproval(String tenantId, String userId, TableDataInput input) {
		final Query query = constructPendingCountContractQueryByTenant(input, tenantId, userId, true, null, null);
		return ((Number) query.getSingleResult()).longValue();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ProductContractPojo> findDraftContractListForTenant(String tenantId, String userId, TableDataInput input, Date startDate, Date endDate) {
		final Query query = constructDraftContractQueryByTenant(input, tenantId, userId, false, startDate, endDate);
		query.setFirstResult(input.getStart());
		query.setMaxResults(input.getLength());
		return query.getResultList();
	}

	@Override
	public long findTotalFilteredDraftContractByTenant(String tenantId, String userId, TableDataInput input, Date startDate, Date endDate) {
		final Query query = constructDraftContractQueryByTenant(input, tenantId, userId, true, startDate, endDate);
		return ((Number) query.getSingleResult()).longValue();
	}

	private Query constructDraftContractQueryByTenant(TableDataInput input, String tenantId, String userId, boolean isCount, Date startDate, Date endDate) {
		String hql = "";

		// If count query is enabled, then add the select count(*) clause
		if (isCount) {
			hql += "select count(distinct t) ";
		}
		if (!isCount) {
			hql += "select distinct new com.privasia.procurehere.core.pojo.ProductContractPojo(t.id, t.contractId, t.contractName, t.eventId, t.contractReferenceNumber, t.previousContractNo, t.renewalContract, s.companyName, fs.vendorCode, bu.unitName, bu.unitCode, t.groupCodeStr, pc.procurementCategories, at.agreementType, c.currencyCode, t.contractValue, t.contractStartDate, t.contractEndDate, t.contractReminderDates, cla.loaDate, cla.agreementDate, cb.loginId, t.createdDate, mb.loginId, t.modifiedDate, t.status,  t.decimal, cla.loaFileName, cla.agreementFileName) from ProductContract t ";
		} else {
			hql += " from ProductContract t ";
		}
		// If this is not a count query, only then add the join fetch. Count query does not require its
		if (!isCount) {
			hql += " left outer join t.contractCreator cb left outer join t.modifiedBy mb left outer join t.supplier fs left outer join fs.supplier s left outer join t.businessUnit bu left outer join t.currency c left outer join t.procurementCategory pc left outer join t.agreementType at left outer join t.contractLoaAndAgreement cla left outer join t.teamMembers tm left outer join tm.user tmu ";
		} else {
			hql += " left outer join t.contractCreator cb left outer join t.modifiedBy mb left outer join t.supplier fs left outer join fs.supplier s left outer join t.businessUnit bu left outer join t.currency c left outer join t.procurementCategory pc left outer join t.agreementType at left outer join t.contractLoaAndAgreement cla left outer join t.teamMembers tm left outer join tm.user tmu ";
		}

		hql += " where t.buyer.id = :tenantId ";

		if (userId != null) {
			hql += ("and ( cb.id = :userId or tmu.id = :userId ) ");
		}

		boolean isStatusFilterOn = false;

		// Add on search filter conditions
		for (ColumnParameter cp : input.getColumns()) {
			if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
				if (cp.getData().equals("status")) {
					hql += " and t.status = (:" + cp.getData() + ")";
				} else if (cp.getData().equals("typeString")) {
					hql += " and t.type = (:type)";
				} else if (cp.getData().equals("vendorCode") || cp.getData().equals("companyName")) {
					hql += " and upper(s.companyName) like (:" + cp.getData() + ")";
				} else if (cp.getData().equals("createdBy")) {
					hql += " and upper(cb.loginId) like (:" + cp.getData() + ")";
				} else if (cp.getData().equals("modifiedBy")) {
					hql += " and upper(mb.loginId) like (:" + cp.getData() + ")";
				} else if (cp.getData().equals("referenceNumber")) {
					hql += " and upper(t.contractReferenceNumber)  like (:" + cp.getData() + ")";
				} else if (cp.getData().equals("groupcode")) {
					hql += " and upper(t.groupCodeStr) like (:" + cp.getData() + ")";
				} else if (cp.getData().equals("currencyCode")) {
					hql += " and upper(c.currencyCode) like (:" + cp.getData() + ")";
				} else if (cp.getData().equals("businessUnit")) {
					hql += " and upper(bu.unitName) like (:" + cp.getData() + ")";
				} else if (cp.getData().equals("creatorEmail")) {
					hql += " and upper(cb.communicationEmail) like (:" + cp.getData() + ")";
				} else if (cp.getData().equals("contractValue")) {
					try {
						new BigDecimal(cp.getSearch().getValue());
						hql += " and t." + cp.getData() + " = (:" + cp.getData().replace(".", "") + ")";
					} catch (Exception e) {
					}
				} else {
					hql += " and upper(t." + cp.getData() + ") like (:" + cp.getData().replace(".", "") + ")";
				}
			}
		}

		if (!isStatusFilterOn) {
			hql += " and t.status = :status ";
		}
		// search with Date range
		if (startDate != null && endDate != null) {
			hql += " and  t.createdDate between :startDate and :endDate ";
		}

		// If it is not a count query then add order by clause
		if (!isCount) {
			List<OrderParameter> orderList = input.getOrder();
			if (CollectionUtil.isNotEmpty(orderList)) {
				hql += " order by ";
				for (OrderParameter order : orderList) {
					String orderColumn = input.getColumns().get(order.getColumn()).getData();
					String dir = order.getDir();
					if (orderColumn.equals("vendorCode") || orderColumn.equals("companyName")) {
						hql += " s.companyName " + dir + ",";
					} else if (orderColumn.equals("createBy")) {
						hql += " cb.loginId " + dir + ",";
					} else if (orderColumn.equals("modifiedBy")) {
						hql += " mb.loginId " + dir + ",";
					} else if (orderColumn.equals("currencyCode")) {
						hql += " c.currencyCode " + dir + ",";
					} else {
						hql += " t." + orderColumn + " " + dir + ",";
					}
				}
				if (hql.lastIndexOf(",") == hql.length() - 1) {
					hql = hql.substring(0, hql.length() - 1);
				}
			} else {
				hql += " order by t.contractReferenceNumber ";
			}
		}

		LOG.info("HQL : " + hql + "  Tenant Id : " + tenantId);

		final Query query = getEntityManager().createQuery(hql.toString());
		query.setParameter("tenantId", tenantId);
		if (userId != null) {
			query.setParameter("userId", userId);
		}

		// Apply search filter values
		for (ColumnParameter cp : input.getColumns()) {
			if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
				if (cp.getData().equals("status")) {
					isStatusFilterOn = true;
					query.setParameter("status", ContractStatus.valueOf(cp.getSearch().getValue()));
				} else if (cp.getData().equals("contractValue")) {
					try {
						new BigDecimal(cp.getSearch().getValue());
						query.setParameter(cp.getData().replace(".", ""), new BigDecimal(cp.getSearch().getValue()));
					} catch (Exception e) {
					}
				} else {
					LOG.info("Search by : " + cp.getData() + " value : " + cp.getSearch().getValue().toUpperCase());
					query.setParameter(cp.getData().replace(".", ""), "%" + cp.getSearch().getValue().toUpperCase() + "%");
				}
			}
		}
		// set parameter Date range
		if (startDate != null && endDate != null) {
			query.setParameter("startDate", startDate);
			query.setParameter("endDate", endDate);
		}
		// If status search filter is not ON then by default return only active records.
		if (!isStatusFilterOn) {
			query.setParameter("status", ContractStatus.DRAFT);
		}
		return query;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ProductContractPojo> findPendingContractListByTenant(String tenantId, String userId, TableDataInput input, Date startDate, Date endDate) {
		final Query query = constructPendingCountContractQueryByTenant(input, tenantId, userId, false, startDate, endDate);
		query.setFirstResult(input.getStart());
		query.setMaxResults(input.getLength());
		return query.getResultList();
	}

	@Override
	public long findTotalFilteredPendingContractByTenant(String tenantId, String userId, TableDataInput input, Date startDate, Date endDate) {
		final Query query = constructPendingCountContractQueryByTenant(input, tenantId, userId, true, startDate, endDate);
		return ((Number) query.getSingleResult()).longValue();
	}

	private Query constructPendingCountContractQueryByTenant(TableDataInput input, String tenantId, String userId, boolean isCount, Date startDate, Date endDate) {
		String hql = "";

		// If count query is enabled, then add the select count(*) clause
		if (isCount) {
			hql += "select count(distinct t) ";
		}
		if (!isCount) {
			hql += "select distinct new com.privasia.procurehere.core.pojo.ProductContractPojo(t.id, t.contractId, t.contractName, t.eventId, t.contractReferenceNumber, t.previousContractNo, t.renewalContract, s.companyName, fs.vendorCode, bu.unitName, bu.unitCode, t.groupCodeStr, pc.procurementCategories, at.agreementType, c.currencyCode, t.contractValue, t.contractStartDate, t.contractEndDate, t.contractReminderDates, cla.loaDate, cla.agreementDate, cb.communicationEmail, t.createdDate, mb.loginId, t.modifiedDate, t.status,  t.decimal, cla.loaFileName, cla.agreementFileName) from ProductContract t ";
		} else {
			hql += " from ProductContract t ";
		}
		// If this is not a count query, only then add the join fetch. Count query does not require its
		if (!isCount) {
			hql += " left outer join t.contractCreator cb left outer join t.modifiedBy mb left outer join t.supplier fs left outer join fs.supplier s left outer join t.businessUnit bu left outer join t.currency c left outer join t.procurementCategory pc left outer join t.agreementType at left outer join t.contractLoaAndAgreement cla left outer join t.teamMembers tm left outer join tm.user tmu ";
		} else {
			hql += " left outer join t.contractCreator cb left outer join t.modifiedBy mb left outer join t.supplier fs left outer join fs.supplier s left outer join t.businessUnit bu left outer join t.currency c left outer join t.procurementCategory pc left outer join t.agreementType at left outer join t.contractLoaAndAgreement cla left outer join t.teamMembers tm left outer join tm.user tmu ";
		}

		hql += " where t.buyer.id = :tenantId ";

		if (userId != null) {
			hql += (" and (cb.id = :userId or tmu.id = :userId ) ");
		}

		boolean isStatusFilterOn = false;

		// Add on search filter conditions
		for (ColumnParameter cp : input.getColumns()) {
			if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
				if (cp.getData().equals("status")) {
					hql += " and t.status = (:" + cp.getData() + ")";
				} else if (cp.getData().equals("typeString")) {
					hql += " and t.type = (:type)";
				} else if (cp.getData().equals("vendorCode") || cp.getData().equals("companyName")) {
					hql += " and upper(s.companyName) like (:" + cp.getData() + ")";
				} else if (cp.getData().equals("createdBy")) {
					hql += " and upper(cb.loginId) like (:" + cp.getData() + ")";
				} else if (cp.getData().equals("modifiedBy")) {
					hql += " and upper(mb.loginId) like (:" + cp.getData() + ")";
				} else if (cp.getData().equals("referenceNumber")) {
					hql += " and upper(t.contractReferenceNumber)  like (:" + cp.getData() + ")";
				} else if (cp.getData().equals("groupcode")) {
					hql += " and upper(t.groupCodeStr) like (:" + cp.getData() + ")";
				} else if (cp.getData().equals("currencyCode")) {
					hql += " and upper(c.currencyCode) like (:" + cp.getData() + ")";
				} else if (cp.getData().equals("businessUnit")) {
					hql += " and upper(bu.unitName) like (:" + cp.getData() + ")";
				} else if (cp.getData().equals("creatorEmail")) {
					hql += " and upper(cb.communicationEmail) like (:" + cp.getData() + ")";
				} else if (cp.getData().equals("contractValue")) {
					try {
						new BigDecimal(cp.getSearch().getValue());
						hql += " and t." + cp.getData() + " = (:" + cp.getData().replace(".", "") + ")";
					} catch (Exception e) {
					}
				} else {
					hql += " and upper(t." + cp.getData() + ") like (:" + cp.getData().replace(".", "") + ")";
				}
			}
		}

		if (!isStatusFilterOn) {
			hql += " and t.status = :status ";
		}
		// search with Date range
		if (startDate != null && endDate != null) {
			hql += " and  t.createdDate between :startDate and :endDate ";
		}

		// If it is not a count query then add order by clause
		if (!isCount) {
			List<OrderParameter> orderList = input.getOrder();
			if (CollectionUtil.isNotEmpty(orderList)) {
				hql += " order by ";
				for (OrderParameter order : orderList) {
					String orderColumn = input.getColumns().get(order.getColumn()).getData();
					String dir = order.getDir();
					if (orderColumn.equals("vendorCode") || orderColumn.equals("companyName")) {
						hql += " s.companyName " + dir + ",";
					} else if (orderColumn.equals("createBy")) {
						hql += " cb.loginId " + dir + ",";
					} else if (orderColumn.equals("modifiedBy")) {
						hql += " mb.loginId " + dir + ",";
					} else if (orderColumn.equals("currencyCode")) {
						hql += " c.currencyCode " + dir + ",";
					} else {
						hql += " t." + orderColumn + " " + dir + ",";
					}
				}
				if (hql.lastIndexOf(",") == hql.length() - 1) {
					hql = hql.substring(0, hql.length() - 1);
				}
			} else {
				hql += " order by t.contractReferenceNumber ";
			}
		}

		LOG.info("HQL : " + hql + "  Tenant Id : " + tenantId);

		final Query query = getEntityManager().createQuery(hql.toString());
		query.setParameter("tenantId", tenantId);
		if (userId != null) {
			query.setParameter("userId", userId);
		}

		// Apply search filter values
		for (ColumnParameter cp : input.getColumns()) {
			if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
				if (cp.getData().equals("status")) {
					isStatusFilterOn = true;
					query.setParameter("status", ContractStatus.valueOf(cp.getSearch().getValue()));
				} else if (cp.getData().equals("contractValue")) {
					LOG.info("Amount value  : " + cp.getSearch().getValue());
					try {
						new BigDecimal(cp.getSearch().getValue());
						query.setParameter(cp.getData().replace(".", ""), new BigDecimal(cp.getSearch().getValue()));
					} catch (Exception e) {
					}
				} else {
					LOG.info("Search by : " + cp.getData() + " value : " + cp.getSearch().getValue().toUpperCase());
					query.setParameter(cp.getData().replace(".", ""), "%" + cp.getSearch().getValue().toUpperCase() + "%");
				}
			}
		}
		// If status search filter is not ON then by default return only active records.
		if (!isStatusFilterOn) {
			query.setParameter("status", ContractStatus.PENDING);
		}

		if (startDate != null && endDate != null) {
			query.setParameter("startDate", startDate);
			query.setParameter("endDate", endDate);
		}
		return query;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ProductContractPojo> findTerminatedContractListByTenant(String tenantId, String userId, TableDataInput input, Date startDate, Date endDate) {
		final Query query = constructTerminatedCountContractQueryByTenant(input, tenantId, userId, false, startDate, endDate);
		query.setFirstResult(input.getStart());
		query.setMaxResults(input.getLength());
		return query.getResultList();
	}

	@Override
	public long findTotalFilteredTerminatedContractByTenant(String tenantId, String userId, TableDataInput input, Date startDate, Date endDate) {
		final Query query = constructTerminatedCountContractQueryByTenant(input, tenantId, userId, true, startDate, endDate);
		return ((Number) query.getSingleResult()).longValue();
	}

	private Query constructTerminatedCountContractQueryByTenant(TableDataInput input, String tenantId, String userId, boolean isCount, Date startDate, Date endDate) {
		String hql = "";

		// If count query is enabled, then add the select count(*) clause
		if (isCount) {
			hql += "select count(distinct t) ";
		}
		if (!isCount) {
			hql += "select distinct new com.privasia.procurehere.core.pojo.ProductContractPojo(t.id, t.contractId, t.contractName, t.eventId, t.contractReferenceNumber, t.previousContractNo, t.renewalContract, s.companyName, fs.vendorCode, bu.unitName, bu.unitCode, t.groupCodeStr, pc.procurementCategories, at.agreementType, c.currencyCode, t.contractValue, t.contractStartDate, t.contractEndDate, t.contractReminderDates, cla.loaDate, cla.agreementDate, cb.communicationEmail, t.createdDate, mb.loginId, t.modifiedDate, t.status,  t.decimal, cla.loaFileName, cla.agreementFileName) from ProductContract t ";
		} else {
			hql += " from ProductContract t ";
		}
		// If this is not a count query, only then add the join fetch. Count query does not require its
		if (!isCount) {
			hql += " left outer join t.contractCreator cb left outer join t.modifiedBy mb left outer join t.supplier fs left outer join fs.supplier s left outer join t.businessUnit bu left outer join t.currency c left outer join t.procurementCategory pc left outer join t.agreementType at left outer join t.contractLoaAndAgreement cla left outer join t.teamMembers tm left outer join tm.user tmu ";
		} else {
			hql += " left outer join t.contractCreator cb left outer join t.modifiedBy mb left outer join t.supplier fs left outer join fs.supplier s left outer join t.businessUnit bu left outer join t.currency c left outer join t.procurementCategory pc left outer join t.agreementType at left outer join t.contractLoaAndAgreement cla left outer join t.teamMembers tm left outer join tm.user tmu ";
		}

		hql += " where t.buyer.id = :tenantId ";

		if (userId != null) {
			hql += " and (cb.id = :userId or tmu.id = :userId ) ";
		}

		boolean isStatusFilterOn = false;

		// Add on search filter conditions
		for (ColumnParameter cp : input.getColumns()) {
			if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
				if (cp.getData().equals("status")) {
					hql += " and t.status = (:" + cp.getData() + ")";
				} else if (cp.getData().equals("typeString")) {
					hql += " and t.type = (:type)";
				} else if (cp.getData().equals("vendorCode") || cp.getData().equals("companyName")) {
					hql += " and upper(s.companyName) like (:" + cp.getData() + ")";
				} else if (cp.getData().equals("createdBy")) {
					hql += " and upper(cb.loginId) like (:" + cp.getData() + ")";
				} else if (cp.getData().equals("modifiedBy")) {
					hql += " and upper(mb.loginId) like (:" + cp.getData() + ")";
				} else if (cp.getData().equals("referenceNumber")) {
					hql += " and upper(t.contractReferenceNumber)  like (:" + cp.getData() + ")";
				} else if (cp.getData().equals("groupcode")) {
					hql += " and upper(t.groupCodeStr) like (:" + cp.getData() + ")";
				} else if (cp.getData().equals("currencyCode")) {
					hql += " and upper(c.currencyCode) like (:" + cp.getData() + ")";
				} else if (cp.getData().equals("businessUnit")) {
					hql += " and upper(bu.unitName) like (:" + cp.getData() + ")";
				} else if (cp.getData().equals("creatorEmail")) {
					hql += " and upper(cb.communicationEmail) like (:" + cp.getData() + ")";
				} else if (cp.getData().equals("contractValue")) {
					try {
						new BigDecimal(cp.getSearch().getValue());
						hql += " and t." + cp.getData() + " = (:" + cp.getData().replace(".", "") + ")";
					} catch (Exception e) {
					}
				} else {
					hql += " and upper(t." + cp.getData() + ") like (:" + cp.getData().replace(".", "") + ")";
				}
			}
		}

		if (!isStatusFilterOn) {
			hql += " and t.status = :status ";
		}

		// search with Date range
		if (startDate != null && endDate != null) {
			hql += " and  t.createdDate between :startDate and :endDate ";
		}

		// If it is not a count query then add order by clause
		if (!isCount) {
			List<OrderParameter> orderList = input.getOrder();
			if (CollectionUtil.isNotEmpty(orderList)) {
				hql += " order by ";
				for (OrderParameter order : orderList) {
					String orderColumn = input.getColumns().get(order.getColumn()).getData();
					String dir = order.getDir();
					if (orderColumn.equals("vendorCode") || orderColumn.equals("companyName")) {
						hql += " s.companyName " + dir + ",";
					} else if (orderColumn.equals("createBy")) {
						hql += " cb.loginId " + dir + ",";
					} else if (orderColumn.equals("modifiedBy")) {
						hql += " mb.loginId " + dir + ",";
					} else if (orderColumn.equals("currencyCode")) {
						hql += " c.currencyCode " + dir + ",";
					} else {
						hql += " t." + orderColumn + " " + dir + ",";
					}
				}
				if (hql.lastIndexOf(",") == hql.length() - 1) {
					hql = hql.substring(0, hql.length() - 1);
				}
			} else {
				hql += " order by t.contractReferenceNumber ";
			}
		}

		LOG.info("HQL : " + hql + "  Tenant Id : " + tenantId);

		final Query query = getEntityManager().createQuery(hql.toString());
		query.setParameter("tenantId", tenantId);
		if (userId != null) {
			query.setParameter("userId", userId);
		}

		// Apply search filter values
		for (ColumnParameter cp : input.getColumns()) {
			if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
				if (cp.getData().equals("status")) {
					isStatusFilterOn = true;
					query.setParameter("status", ContractStatus.valueOf(cp.getSearch().getValue()));
				} else if (cp.getData().equals("contractValue")) {
					LOG.info("Amount value  : " + cp.getSearch().getValue());
					try {
						new BigDecimal(cp.getSearch().getValue());
						query.setParameter(cp.getData().replace(".", ""), new BigDecimal(cp.getSearch().getValue()));
					} catch (Exception e) {
					}
				} else {
					LOG.info("Search by : " + cp.getData() + " value : " + cp.getSearch().getValue().toUpperCase());
					query.setParameter(cp.getData().replace(".", ""), "%" + cp.getSearch().getValue().toUpperCase() + "%");
				}
			}
		}
		// If status search filter is not ON then by default return only active records.
		if (!isStatusFilterOn) {
			query.setParameter("status", ContractStatus.TERMINATED);
		}

		if (startDate != null && endDate != null) {
			query.setParameter("startDate", startDate);
			query.setParameter("endDate", endDate);
		}
		return query;
	}

	@Override
	public ProductContract findProductContractByBuyerId(String sapContractNumber, String tenantId) {
		LOG.info("contractReferenceNumber" + sapContractNumber);
		StringBuilder hsql = new StringBuilder("select distinct t from ProductContract as t " +
				" where t.sapContractNumber = :sapContractNumber and t.buyer.id = :tenantId");
		final Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("sapContractNumber", sapContractNumber);
		query.setParameter("tenantId", tenantId);
		try {
			List<ProductContract> list = query.getResultList();
			if (CollectionUtil.isNotEmpty(list)) {
				return list.get(0);
			}
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
		return null;
	}

	@Override
	public void deleteProductContractById(String productContractId) {
		StringBuilder hsql = new StringBuilder("DELETE FROM ProductContract pc WHERE pc.id = :id");
		final Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", productContractId);
		try {
			query.executeUpdate();
		}
		catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
	}

	@Override
	public void transferOwnership(String fromUserId, String toUserId) {

		//limit to the user's tenant as a precautionary measure
		String tenantId = SecurityLibrary.getLoggedInUserTenantId();
		User sourceUser = getEntityManager().find(User.class, fromUserId);
		User targetUser = getEntityManager().find(User.class, toUserId);

		Query query = getEntityManager().createNativeQuery("UPDATE PROC_PRODUCT_CONTRACT SET CREATED_BY = :toUserId where CREATED_BY =:fromUserId");
		query.setParameter("toUserId", toUserId);
		query.setParameter("fromUserId", fromUserId);
		int recordsUpdated = query.executeUpdate();
		LOG.info("Creators transferred: {}", recordsUpdated);

		Query query1 = getEntityManager().createNativeQuery("UPDATE PROC_PRODUCT_CONTRACT SET CONTRACT_CREATOR = :toUserId where CONTRACT_CREATOR =:fromUserId");
		query1.setParameter("toUserId", toUserId);
		query1.setParameter("fromUserId", fromUserId);
		recordsUpdated = query1.executeUpdate();
		LOG.info("Contract Creators transferred: {}", recordsUpdated);


		//transfer the user to team members too
		Query query2 = getEntityManager().createQuery(
				"UPDATE ContractTeamMember team " +
						"SET team.user = :targetUser " +
						"WHERE team.user = :sourceUser "
		);
		query2.setParameter("sourceUser", sourceUser);
		query2.setParameter("targetUser", targetUser);
		recordsUpdated = query2.executeUpdate();
		LOG.info("Form Owners transferred: {}", recordsUpdated);

		//transfer the user to notification team members too
		Query query2a = getEntityManager().createQuery(
				"UPDATE ProductContractNotifyUsers team " +
						"SET team.user = :targetUser " +
						"WHERE team.user = :sourceUser "
		);
		query2a.setParameter("sourceUser", sourceUser);
		query2a.setParameter("targetUser", targetUser);
		recordsUpdated = query2a.executeUpdate();
		LOG.info("Notification Users transferred: {}", recordsUpdated);


		//transfer ownership of approvals only for RFS with status of DRAFT and PENDING
		Query query5 = getEntityManager().createQuery(
				"UPDATE ContractApprovalUser sfaur " +
						"SET sfaur.user = :targetUser " +
						"WHERE sfaur.user = :sourceUser "
		);
		query5.setParameter("sourceUser", sourceUser);
		query5.setParameter("targetUser", targetUser);
		recordsUpdated = query5.executeUpdate();
		LOG.info("Approval user transferred: {}", recordsUpdated);


	}

}
