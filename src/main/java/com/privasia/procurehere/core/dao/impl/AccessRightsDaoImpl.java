/**
 * 
 */
package com.privasia.procurehere.core.dao.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.privasia.procurehere.core.dao.AccessRightsDao;
import com.privasia.procurehere.core.entity.AccessRights;
import com.privasia.procurehere.core.enums.TenantType;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.StringUtils;

/**
 * @author Ravi
 */
@Repository
public class AccessRightsDaoImpl extends GenericDaoImpl<AccessRights, String> implements AccessRightsDao {

	private static final Logger LOG = LogManager.getLogger(AccessRightsDaoImpl.class);

	@SuppressWarnings("unchecked")
	@Override
	public List<AccessRights> getParentAccessControlList() {
		final Query query = getEntityManager().createQuery("from AccessRights a where a.parent is null");
		List<AccessRights> list = query.getResultList();
		// getEntityManager().getSession().createQuery("from AccessControlList a where a.parent is null order by
		// a.aclName").list();
		if (CollectionUtil.isNotEmpty(list)) {
			for (AccessRights acl : list) {
				acl.getAclName();
				if (acl.getParent() != null) {
					acl.getParent().getAclName();
				}
				// Traverse Child Nodes
				if (CollectionUtil.isNotEmpty(acl.getChildren())) {
					for (AccessRights cacl : acl.getChildren()) {
						cacl.getAclName();
						if (CollectionUtil.isNotEmpty(cacl.getChildren())) {
							for (AccessRights ccacl : cacl.getChildren()) {
								ccacl.getAclName();
								if (CollectionUtil.isNotEmpty(ccacl.getChildren())) {
									for (AccessRights cccacl : ccacl.getChildren()) {
										cccacl.getAclName();
										if (CollectionUtil.isNotEmpty(cccacl.getChildren())) {
											for (AccessRights ccccacl : cccacl.getChildren()) {
												ccccacl.getAclName();
											}
										}
									}
								}
							}
						}
					}
				}
			}
		}

		return list;
	}

	@SuppressWarnings("unchecked")
	@Override
	@Transactional(readOnly = true, propagation = Propagation.REQUIRES_NEW)
	public AccessRights findByAccessControl(String value) {
		try {
			final Query query = getEntityManager().createQuery("from AccessRights a where a.aclValue =:aclValue");
			query.setParameter("aclValue", value);
			List<AccessRights> accesscontrols = query.getResultList();
			if (CollectionUtil.isNotEmpty(accesscontrols)) {
				for (AccessRights acl : accesscontrols) {
					acl.getAclName();
					if (acl.getParent() != null) {
						acl.getParent().getAclName();
					}
					// Traverse Child Nodes
					if (CollectionUtil.isNotEmpty(acl.getChildren())) {
						for (AccessRights cacl : acl.getChildren()) {
							cacl.getAclName();
							if (CollectionUtil.isNotEmpty(cacl.getChildren())) {
								for (AccessRights ccacl : cacl.getChildren()) {
									ccacl.getAclName();
									if (CollectionUtil.isNotEmpty(ccacl.getChildren())) {
										for (AccessRights cccacl : ccacl.getChildren()) {
											cccacl.getAclName();
											if (CollectionUtil.isNotEmpty(cccacl.getChildren())) {
												for (AccessRights ccccacl : cccacl.getChildren()) {
													ccccacl.getAclName();
												}
											}
										}
									}
								}
							}
						}
					}
				}
				return accesscontrols.get(0);
			}
		} catch (NoResultException nr) {
			LOG.info("Error while getting AccessControlList : " + nr.getMessage(), nr);
			return null;
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	@Transactional(readOnly = true)
	public AccessRights findByACLValue(String value) {
		try {
			final Query query = getEntityManager().createQuery("from AccessRights a where a.aclValue =:aclValue");
			query.setParameter("aclValue", value);
			List<AccessRights> accesscontrols = query.getResultList();
			if (CollectionUtil.isNotEmpty(accesscontrols)) {
				return accesscontrols.get(0);
			}
		} catch (NoResultException nr) {
			LOG.info("Error while getting AccessControlList : " + nr.getMessage(), nr);
			return null;
		}
		return null;
	}

	@Override
	@Transactional(readOnly = false)
	public void checkAccessControlListMasterData(List<AccessRights> list) {
		for (AccessRights acl : list) {
			AccessRights check = findByACLValue(acl.getAclValue());
			if (check == null) {
				if (acl.getParent() != null) {
					LOG.info("ACL : " + acl.getAclValue() + " has parent as : " + acl.getParent().getAclValue());
					AccessRights parent = findByACLValue(StringUtils.checkString(acl.getParent().getAclValue()));
					if (parent != null) {
						acl.setParent(parent);
						if (CollectionUtil.isEmpty(parent.getChildren())) {
							parent.setChildren(new ArrayList<AccessRights>());
						}
						LOG.info("ADDING ACL : " + acl.getAclValue() + "  [ Name ] : " + acl.getAclName());
						save(acl);
						parent.getChildren().add(acl);
						LOG.info("PARENT UPDATED : " + parent.getAclValue());
						update(parent);
					} else {
						LOG.warn("Parent is not found... something is wrong with the order of ACL in the excel!!! Parent should be in the top of the list first ");
					}
				} else {
					acl.setParent(null);
					LOG.info("ADDING ACL : " + acl.getAclValue() + "  [ Name ] : " + acl.getAclName());
					save(acl);
				}
			} else {
				check.setAclName(acl.getAclName());
				check.setAclOrder(acl.getAclOrder());

				// LOG.info("finace:" + acl.getAclName() + " :" + check.getFinanceCompany());
				check.setFinanceCompany(acl.getFinanceCompany());
				update(check);
			}
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<AccessRights> getAccessControlListForOwner(boolean includeViewOnly) {
		String sql = "from AccessRights a where a.owner = :owner ";
		if (!includeViewOnly) {
			sql += " and a.aclValue not like '%VIEW_ONLY' and a.aclValue not like '%READONLY'";
		}
		final Query query = getEntityManager().createQuery(sql);
		query.setParameter("owner", Boolean.TRUE);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<AccessRights> getAccessControlListForSupplier(boolean includeViewOnly) {
		String sql = "from AccessRights a where a.supplier = :supplier";
		if (!includeViewOnly) {
			sql += " and a.aclValue not like '%VIEW_ONLY' and a.aclValue not like '%READONLY'";
		}
		final Query query = getEntityManager().createQuery(sql);
		query.setParameter("supplier", Boolean.TRUE);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<AccessRights> getAccessControlListForBuyer(boolean includeViewOnly, boolean isEventBasedSubscription) {
		String sql = "from AccessRights a where a.buyer = :buyer ";
		if (!includeViewOnly) {
			sql += " and a.aclValue not like '%VIEW_ONLY' and a.aclValue not like '%READONLY' and acl_value not like '%PR_CREATE' and acl_value not like '%PO_CREATE' and acl_value not like '%CREATE_PAYMENT_RECORD' and acl_value not like '%ACCEPT_DO' and acl_value not like '%GRN_EDIT' and acl_value not like '%ACCEPT_CN_DN' ";
		}
		if (isEventBasedSubscription) {
			sql += " and a.aclValue not like '%PR_PO' and a.aclValue not like '%VIEW_PR_DRAFT' and a.aclValue not like '%VIEW_PO_LIST' and a.aclValue not like '%PR_CREATE' and a.aclValue not like '%PR_TEMPLATE%' ";
		}
		LOG.info(" QUERY ACCESS ROLE??? >>> "+sql);
		final Query query = getEntityManager().createQuery(sql);
		query.setParameter("buyer", Boolean.TRUE);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<AccessRights> getAccessControlListForFinanceComapany(boolean includeViewOnly) {
		String sql = "from AccessRights a where a.financeCompany = :financeCompany ";
		if (!includeViewOnly) {
			sql += " and a.aclValue not like '%VIEW_ONLY' and a.aclValue not like '%READONLY'";
		}

		final Query query = getEntityManager().createQuery(sql);
		query.setParameter("financeCompany", Boolean.TRUE);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<AccessRights> findChildAccessForId(String aclValue) {
		StringBuilder hsql = new StringBuilder("from AccessRights as a where a.parent.id =:id order by a.aclName");

		final Query query = getEntityManager().createQuery(hsql.toString());
		if (StringUtils.checkString(aclValue).length() > 0) {
			query.setParameter("id", aclValue);
		}
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<AccessRights> getParentAccessControlListForType(TenantType tenantType, boolean isEventBasedSubscription) {
		List<AccessRights> returnList = new ArrayList<AccessRights>();
		LOG.info("getParentAccessControlListForType");
		StringBuilder hql = new StringBuilder("from AccessRights a where a.parent is null ");
		boolean isBuyer = false;
		boolean supplier = false;
		boolean owner = false;
		boolean isfinance = false;
		LOG.info("tenantType "+tenantType);
		if (tenantType == TenantType.OWNER) {
			hql.append(" and a.owner = true");
			owner = true;
		} else if (tenantType == TenantType.SUPPLIER) {
			hql.append(" and a.supplier = true");
			supplier = true;
		} else if (tenantType == TenantType.BUYER) {
			hql.append(" and a.buyer = true");
			isBuyer = true;
		} else if (tenantType == TenantType.FINANCE_COMPANY) {
			hql.append(" and a.financeCompany = true");
			isfinance = true;
		}
		// For event based subscription PR/PO is not allowed. Ref email from Soalen Dt: Sunday 09 July 2017 04:08 PM
		if (isEventBasedSubscription) {
			LOG.info(">>> isEventBasedSubscription");
			hql.append(" and a.aclValue not like 'ROLE_PR_%'");
		}
		hql.append(" order by aclOrder");
		LOG.info("QUERY SUPPLIER >>>>>>>>   "+hql);
		final Query query = getEntityManager().createQuery(hql.toString());
		List<AccessRights> list = query.getResultList(); // getEntityManager().getSession().createQuery("from

		if (CollectionUtil.isNotEmpty(list)) {
			LOG.info(">>>  CollectionUtil.isNotEmpty(list)");
			for (AccessRights acl : list) {
				acl.getAclName();
				AccessRights parent = acl.createShallowCopy();
				returnList.add(parent);
				if (acl.getParent() != null) {
					LOG.info("acl.getParent() != null");
					acl.getParent().getAclName();
				}
				// Traverse Child Nodes Level - 2
				if (CollectionUtil.isNotEmpty(acl.getChildren())) {
					for (AccessRights cacl : acl.getChildren()) {
						cacl.getAclName();
						// if the child does not match the required tenantType, skip it.
						if (!((isBuyer && cacl.getBuyer()) || (isfinance && cacl.getFinanceCompany()) || (owner && cacl.getOwner()) || (supplier && cacl.getSupplier())))
							continue;

						// For event based subscription PR/PO is not allowed. Ref email from Soalen Dt: Sunday 09 July
						// 2017 04:08 PM
						if (isEventBasedSubscription && cacl.getAclValue().startsWith("ROLE_PR_")) {
							continue;
						}

						AccessRights subParent = cacl.createShallowCopy();
						if (parent.getChildren() == null) {
							parent.setChildren(new ArrayList<AccessRights>());
						}
						parent.getChildren().add(subParent);
						// Level - 3
						if (CollectionUtil.isNotEmpty(cacl.getChildren())) {
							for (AccessRights ccacl : cacl.getChildren()) {
								ccacl.getAclName();
								// if the child does not match the required tenantType, skip it.
								if (!((isBuyer && ccacl.getBuyer()) || (isfinance && cacl.getFinanceCompany()) || (owner && ccacl.getOwner()) || (supplier && ccacl.getSupplier())))
									continue;
								AccessRights subSubParent = ccacl.createShallowCopy();
								if (subParent.getChildren() == null) {
									subParent.setChildren(new ArrayList<AccessRights>());
								}
								subParent.getChildren().add(subSubParent);

								// Level - 4
								if (CollectionUtil.isNotEmpty(ccacl.getChildren())) {
									for (AccessRights cccacl : ccacl.getChildren()) {
										cccacl.getAclName();
										// if the child does not match the required tenantType, skip it.
										if (!((isBuyer && cccacl.getBuyer()) || (isfinance && cacl.getFinanceCompany()) || (owner && cccacl.getOwner()) || (supplier && cccacl.getSupplier())))
											continue;
										AccessRights subSubSubParent = cccacl.createShallowCopy();
										if (subSubParent.getChildren() == null) {
											subSubParent.setChildren(new ArrayList<AccessRights>());
										}
										subSubParent.getChildren().add(subSubSubParent);
										// Level - 5
										if (CollectionUtil.isNotEmpty(cccacl.getChildren())) {
											for (AccessRights ccccacl : cccacl.getChildren()) {
												ccccacl.getAclName();
												// if the child does not match the required tenantType, skip it.
												if (!((isBuyer && ccccacl.getBuyer()) || (isfinance && cacl.getFinanceCompany()) || (owner && ccccacl.getOwner()) || (supplier && ccccacl.getSupplier())))
													continue;
												AccessRights subSubSubSubParent = ccccacl.createShallowCopy();
												if (subSubSubParent.getChildren() == null) {
													subSubSubParent.setChildren(new ArrayList<AccessRights>());
												}
												subSubSubParent.getChildren().add(subSubSubSubParent);
											}
										}
									}
								}
							}
						}
					}
				}
			}
		}

		return returnList;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<AccessRights> findCustomeAccessForBuyer(String[] aclValues) {
		StringBuilder hsql = new StringBuilder("from AccessRights as a where a.aclValue in (:aclValues) order by a.aclName");
		final Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("aclValues", new ArrayList<String>(Arrays.asList(aclValues)));
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<AccessRights> getPrAccessControlListForBuyer() {
		String sql = "select distinct a from AccessRights a where a.buyer = :buyer and (a.aclValue like '%PR_PO' or a.aclValue like '%VIEW_PR_DRAFT' or a.aclValue like '%VIEW_PO_LIST' or a.aclValue like '%PR_CREATE' or a.aclValue like '%PR_TEMPLATE%' OR a.acl_value LIKE '%PROC_TO_PAY'  OR a.acl_value LIKE '%ROLE_PO_CREATE' )";
		final Query query = getEntityManager().createQuery(sql);
		query.setParameter("buyer", Boolean.TRUE);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<AccessRights> findCustomeAccessForFinance(String[] aclValues) {
		StringBuilder hsql = new StringBuilder("from AccessRights as a where a.aclValue in (:aclValues) order by a.aclName");
		final Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("aclValues", new ArrayList<String>(Arrays.asList(aclValues)));
		return query.getResultList();
	}
 
}