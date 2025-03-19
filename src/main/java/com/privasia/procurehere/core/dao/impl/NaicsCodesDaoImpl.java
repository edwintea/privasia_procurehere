package com.privasia.procurehere.core.dao.impl;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.persistence.Query;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.privasia.procurehere.core.dao.NaicsCodesDao;
import com.privasia.procurehere.core.entity.NaicsCodes;
import com.privasia.procurehere.core.entity.Supplier;
import com.privasia.procurehere.core.entity.SupplierProjects;
import com.privasia.procurehere.core.entity.User;
import com.privasia.procurehere.core.enums.Status;
import com.privasia.procurehere.core.pojo.ColumnParameter;
import com.privasia.procurehere.core.pojo.OrderParameter;
import com.privasia.procurehere.core.pojo.SearchVo;
import com.privasia.procurehere.core.pojo.TableDataInput;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.StringUtils;

@Repository("naicsCodesDao")
public class NaicsCodesDaoImpl extends GenericDaoImpl<NaicsCodes, String> implements NaicsCodesDao {

	private static final Logger LOG = LogManager.getLogger(NaicsCodesDaoImpl.class);

	@Resource
	MessageSource messagesource;

	@Override
	public List<NaicsCodes> findAssignedNaicsCodes(String supplierId) {
		StringBuilder hsql = new StringBuilder("from Supplier where id = :id");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", supplierId);
		Supplier supplier = (Supplier) query.getSingleResult();
		return (supplier != null ? supplier.getNaicsCodes() : null);
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<NaicsCodes> findIndustryCategoryByIds(String[] industryCategoryIds) {
		StringBuilder hsql = new StringBuilder("from NaicsCodes n left outer join fetch n.modifiedBy m inner join fetch n.createdBy c where n.id in (:industryCategoryIds) order by n.level, n.categoryCode");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("industryCategoryIds", Arrays.asList(industryCategoryIds));
		List<NaicsCodes> returnList = query.getResultList();
		// Fully initialize the objects instead of proxies...
		for (NaicsCodes ic : returnList) {
			ic = (NaicsCodes) getEntityManager().unwrap(SessionImplementor.class).getPersistenceContext().unproxy(ic);
		}
		return returnList;
	}

	@Override
	public List<NaicsCodes> findAssignedIndustryCategoryForProject(String projectId) {
		StringBuilder hsql = new StringBuilder("from SupplierProjects where id = :id");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", projectId);
		SupplierProjects supplierProject = (SupplierProjects) query.getSingleResult();
		return (supplierProject != null ? supplierProject.getProjectIndustries() : null);
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<NaicsCodes> findParentIndustryCategories() {

		StringBuilder hsql = new StringBuilder("from NaicsCodes as ic left outer join fetch ic.modifiedBy m inner join fetch ic.createdBy c where ic.parent is null and ic.status = :status order by ic.categoryCode");
		final Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("status", Status.ACTIVE);
		return query.getResultList();
	}

  

	@Override
	@Transactional(readOnly = false, noRollbackFor = ConstraintViolationException.class)
	public void loadIndustryCatgoryData(List<NaicsCodes> list, User createdBy) {
		NaicsCodes problem = null;
		try {
			final Date createdDate = new Date();
			for (NaicsCodes ic : list) {
				problem = ic;
				NaicsCodes dbIc = findByCategoryCode(ic.getCategoryCode());
				if (dbIc == null) {
					ic.setCreatedBy(createdBy);
					ic.setCreatedDate(createdDate);
					save(ic);
					// LOG.info("Saved Level " + ic.getLevel() + " Category with code : " + ic.getCategoryCode() + ",
					// Name : " + ic.getCategoryName());
					if (CollectionUtil.isNotEmpty(ic.getChildren())) { // Level 2
						for (NaicsCodes childIc : ic.getChildren()) {
							problem = childIc;
							childIc.setParent(ic);
							childIc.setCreatedBy(createdBy);
							childIc.setCreatedDate(createdDate);
							save(childIc);
							// LOG.info("Saved Level " + childIc.getLevel() + " Category with code : " +
							// childIc.getCategoryCode() + ", Name : " + childIc.getCategoryName());
							if (CollectionUtil.isNotEmpty(childIc.getChildren())) { // Level 3
								for (NaicsCodes childChildIc : childIc.getChildren()) {
									problem = childChildIc;
									childChildIc.setParent(childIc);
									childChildIc.setCreatedBy(createdBy);
									childChildIc.setCreatedDate(createdDate);
									save(childChildIc);
									// LOG.info("Saved Level " + childChildIc.getLevel() + " Category with code : " +
									// childChildIc.getCategoryCode() + ", Name : " + childChildIc.getCategoryName());
									if (CollectionUtil.isNotEmpty(childChildIc.getChildren())) { // Level 4
										for (NaicsCodes childChildChildIc : childChildIc.getChildren()) {
											problem = childChildChildIc;
											childChildChildIc.setParent(childChildIc);
											childChildChildIc.setCreatedBy(createdBy);
											childChildChildIc.setCreatedDate(createdDate);
											save(childChildChildIc);
											// LOG.info("Saved Level " + childChildChildIc.getLevel() + " Category with
											// code : " + childChildChildIc.getCategoryCode() + ", Name : " +
											// childChildChildIc.getCategoryName());
											if (CollectionUtil.isNotEmpty(childChildChildIc.getChildren())) { // Level 5
												for (NaicsCodes childChildChildChildIc : childChildChildIc.getChildren()) {
													problem = childChildChildChildIc;
													childChildChildChildIc.setParent(childChildChildIc);
													childChildChildChildIc.setCreatedBy(createdBy);
													childChildChildChildIc.setCreatedDate(createdDate);
													save(childChildChildChildIc);
													// LOG.info("Saved Level " + childChildChildChildIc.getLevel() + "
													// Category with code : " + childChildChildChildIc.getCategoryCode()
													// + ", Name : " + childChildChildChildIc.getCategoryName());
												}
											}
										}
									}
								}
							}
						}
					}
				} else {
					LOG.info("Level " + ic.getLevel() + " NAICS  with code : " + ic.getCategoryCode() + " already exists.... ");
				}
			}
		} catch (Exception e) {
			if (problem != null) {
				LOG.error("Error by : " + problem.toLogString());
			}
			LOG.error("Error loading NaicsCodes : " + e.getMessage(), e);
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<NaicsCodes> findAllActiveIndustryCategory(int start, int length, String order) {
		String[] sortValue = null;
		if (StringUtils.checkString(order).length() > 0 && StringUtils.checkString(order).contains(",")) {
			sortValue = order.split(",");
		}
		StringBuilder hsql = new StringBuilder("from NaicsCodes as ic left outer join fetch ic.modifiedBy m inner join fetch ic.createdBy c left outer join fetch ic.parent as c left outer join fetch ic.children ");
		if (sortValue != null && sortValue.length >= 2) {
			hsql.append(" order by ic." + sortValue[0] + " " + sortValue[1]);
		}

		final Query query = getEntityManager().createQuery(hsql.toString());
		query.setFirstResult(start);
		query.setMaxResults(length);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<NaicsCodes> findAll(String id) {
		LOG.info("Get all industry Category");
		StringBuilder hsql = new StringBuilder("from NaicsCodes as r left outer join fetch r.modifiedBy m inner join fetch r.createdBy c left outer join fetch r.parent as c left outer join fetch r.children order by r.categoryCode");
		if (StringUtils.checkString(id).length() > 0) {
			hsql.append(" where r.id <> :id");
		}
		final Query query = getEntityManager().createQuery(hsql.toString());
		if (StringUtils.checkString(id).length() > 0) {
			query.setParameter("id", id);
		}
		// LOG.info("Get all industry Category list result : "+query.getResultList());
		return query.getResultList();

	}

	@SuppressWarnings("unchecked")
	@Override
	public NaicsCodes findByCategoryCode(Integer code) {
		NaicsCodes naicsCode = null;

		StringBuilder hsql = new StringBuilder("from NaicsCodes as ic left outer join fetch ic.modifiedBy m inner join fetch ic.createdBy c where ic.categoryCode = :categoryCode order by ic.categoryCode");
		final Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("categoryCode", code);
		List<NaicsCodes> scList = query.getResultList();
		if (CollectionUtil.isNotEmpty(scList)) {
			naicsCode = scList.get(0);
		}
		return naicsCode;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean isExists(NaicsCodes naicsCode) {
		StringBuilder hsql = new StringBuilder("from NaicsCodes as ic where ic.categoryCode = :categoryCode and ic.status = :status");
		if (StringUtils.checkString(naicsCode.getId()).length() > 0) {
			hsql.append(" and ic.id <> :id");
		}
		final Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("categoryCode", naicsCode.getCategoryCode());
		query.setParameter("status", naicsCode.getStatus());
	
		if (StringUtils.checkString(naicsCode.getId()).length() > 0) {
			query.setParameter("id", naicsCode.getId());
		}
		List<NaicsCodes> scList = query.getResultList();
		return CollectionUtil.isNotEmpty(scList);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<NaicsCodes> findForLevel(Integer level) {
		StringBuilder hsql = new StringBuilder("from NaicsCodes as r left outer join fetch r.parent as c left outer join fetch r.children where r.level <=:level order by r.categoryCode");
		final Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("level", level);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<NaicsCodes> getAllIndustryCategoryPojo() {
		StringBuilder hsql = new StringBuilder("from NaicsCodes as ic left outer join fetch ic.parent as p left outer join fetch ic.children left outer join fetch ic.createdBy as cb left outer join fetch ic.modifiedBy as mb order by ic.categoryName");
		final Query query = getEntityManager().createQuery(hsql.toString());
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<NaicsCodes> findChildForId(SearchVo searchVo) {
		StringBuilder hsql = new StringBuilder("from NaicsCodes as r where r.parent.id =:id order by r.categoryCode");
		final Query query = getEntityManager().createQuery(hsql.toString());
		if (StringUtils.checkString(searchVo.getId()).length() > 0) {
			query.setParameter("id", searchVo.getId());
		}
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<NaicsCodes> searchForCategories(String search) {
		StringBuilder hsql = new StringBuilder("from NaicsCodes as r left outer join fetch r.modifiedBy m inner join fetch r.createdBy c where upper(r.categoryName) like :categoryName order by r.level, r.categoryCode");
		final Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("categoryName", "%" + StringUtils.checkString(search).toUpperCase() + "%");
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<NaicsCodes> findLeafIndustryCategoryByName(String searchValue) {
		StringBuilder hsql = new StringBuilder("select distinct ic from NaicsCodes as ic left join ic.children c where ic.children IS EMPTY");
		if (StringUtils.checkString(searchValue).length() > 0) {
			hsql.append(" AND upper(ic.categoryName) like :categoryName");
		}
		hsql.append(" order by ic.categoryCode");
		final Query query = getEntityManager().createQuery(hsql.toString());
		
		if (StringUtils.checkString(searchValue).length() > 0) {
			query.setParameter("categoryName", "%" + StringUtils.checkString(searchValue).toUpperCase() + "%");
		}
		return query.getResultList();
	}

	@Override
	public NaicsCodes getIndustryCategoryForRftById(String id) {
		StringBuilder hsql = new StringBuilder("from NaicsCodes as ic left outer join fetch ic.modifiedBy m inner join fetch ic.createdBy c where ic.id = :id ");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", id);
		NaicsCodes naicsCode = (NaicsCodes) query.getSingleResult();
		return naicsCode;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<NaicsCodes> findNaicsQuery(String tenantId, TableDataInput tableParams) {
		final Query query = constructNaicsQuery(tenantId, tableParams, false);
		query.setFirstResult(tableParams.getStart());
		query.setMaxResults(tableParams.getLength());
		return query.getResultList();
	}

	@Override
	public long findTotalFilteredNaics(String tenantId, TableDataInput tableParams) {
		final Query query = constructNaicsQuery(tenantId, tableParams, true);
		return ((Number) query.getSingleResult()).longValue();
	}

	@Override
	public long findTotalNaics(String tenantId) {
		StringBuilder hql = new StringBuilder("select count (nc) from NaicsCodes nc where nc.status =:status ");
		final Query query = getEntityManager().createQuery(hql.toString());
		query.setParameter("status", Status.ACTIVE);
		//query.setParameter("tenantId", tenantId);
		return ((Number) query.getSingleResult()).longValue();
	}
	
	/**
	 * 
	 * @param tenantId
	 * @param tableParams
	 * @param isCount
	 * @return
	 */
	private Query constructNaicsQuery(String tenantId, TableDataInput tableParams, boolean isCount) {

		String hql = "";

		// If count query is enabled, then add the select count(*) clause
		if (isCount) {
			hql += "select count(nc) ";
		}

		hql += " from NaicsCodes nc ";

		// If this is not a count query, only then add the join fetch. Count query does not require its
		if (!isCount) {
			hql += " left outer join fetch nc.parent as p left outer join fetch nc.children left outer join fetch nc.createdBy as cb left outer join fetch nc.modifiedBy as mb ";
		}

		hql += " where 1=1  ";
		boolean isStatusFilterOn = false;

		// Add on search filter conditions
		for (ColumnParameter cp : tableParams.getColumns()) {
			if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
				if (cp.getData().equals("status")) {
					hql += " and nc.status = (:" + cp.getData() + ")";
				} else if(cp.getData().equals("categoryCode")) {
					hql += " and str(nc." + cp.getData() + ") like (:" + cp.getData().replace(".", "") + ")";
				} else {
					hql += " and upper(nc." + cp.getData() + ") like (:" + cp.getData().replace(".", "") + ")";
				}
			}
		}
		
		if(!isStatusFilterOn) {
			hql += " and nc.status = :status ";
		}
		
		// If it is not a count query then add order by clause
		if (!isCount) {
			List<OrderParameter> orderList = tableParams.getOrder();
			if (CollectionUtil.isNotEmpty(orderList)) {
				hql += " order by ";
				for (OrderParameter order : orderList) {
					String orderColumn = tableParams.getColumns().get(order.getColumn()).getData();
					String dir = order.getDir();
					hql += " nc." + orderColumn + " " + dir + ",";
				}
				if (hql.lastIndexOf(",") == hql.length() - 1) {
					hql = hql.substring(0, hql.length() - 1);
				}
			}
		}

//		LOG.info("HQL : " + hql);

		final Query query = getEntityManager().createQuery(hql.toString());
		//query.setParameter("tenantId", tenantId);
		

		// Apply search filter values
		for (ColumnParameter cp : tableParams.getColumns()) {
			if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
				if (cp.getData().equals("status")) {
					isStatusFilterOn = true;
					query.setParameter("status", Status.valueOf(cp.getSearch().getValue()));
				} else {
					query.setParameter(cp.getData().replace(".", ""), "%" + cp.getSearch().getValue().toUpperCase() + "%");
				}
			}
		}
		// If status search filter is not ON then by default return only active records.
		if (!isStatusFilterOn) {
			query.setParameter("status", Status.ACTIVE);
		}
		return query;
	}

	@Override
	public NaicsCodes getIndustryCategoryCodeAndNameById(String id) {
		StringBuilder hsql = new StringBuilder("select new com.privasia.procurehere.core.entity.NaicsCodes(ic.id, ic.categoryCode, ic.categoryName) from NaicsCodes as ic where ic.id = :id ");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", id);
		NaicsCodes naicsCode = (NaicsCodes) query.getSingleResult();
		return naicsCode;
	}
}