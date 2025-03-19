package com.privasia.procurehere.core.dao.impl;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.Query;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Repository;

import com.privasia.procurehere.core.dao.FavoriteSupplierDao;
import com.privasia.procurehere.core.dao.NaicsCodesDao;
import com.privasia.procurehere.core.entity.Buyer;
import com.privasia.procurehere.core.entity.Country;
import com.privasia.procurehere.core.entity.FavouriteSupplier;
import com.privasia.procurehere.core.entity.IndustryCategory;
import com.privasia.procurehere.core.entity.ProductCategory;
import com.privasia.procurehere.core.entity.Supplier;
import com.privasia.procurehere.core.enums.BuyerStatus;
import com.privasia.procurehere.core.enums.EventStatus;
import com.privasia.procurehere.core.enums.FavouriteSupplierStatus;
import com.privasia.procurehere.core.enums.RfxTypes;
import com.privasia.procurehere.core.enums.SupplierStatus;
import com.privasia.procurehere.core.pojo.ColumnParameter;
import com.privasia.procurehere.core.pojo.EventSupplierPojo;
import com.privasia.procurehere.core.pojo.IndustryCategoryPojo;
import com.privasia.procurehere.core.pojo.OrderParameter;
import com.privasia.procurehere.core.pojo.RequestedAssociatedBuyerPojo;
import com.privasia.procurehere.core.pojo.SearchFilterSupplierPojo;
import com.privasia.procurehere.core.pojo.SearchVo;
import com.privasia.procurehere.core.pojo.SupplierCountPojo;
import com.privasia.procurehere.core.pojo.SupplierPojo;
import com.privasia.procurehere.core.pojo.SupplierSearchPojo;
import com.privasia.procurehere.core.pojo.TableDataInput;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.StringUtils;

/**
 * @author Javed Ahmed
 */
@Repository
@PropertySource(value = { "classpath:application.properties" })
public class FavoriteSupplierDaoImpl extends GenericDaoImpl<FavouriteSupplier, Serializable> implements FavoriteSupplierDao {

	private static final Logger LOG = LogManager.getLogger(Global.ADMIN_LOG);

	@Autowired
	NaicsCodesDao naicsCodesDao;

	@Autowired
	private Environment env;

	@SuppressWarnings("unchecked")
	@Override
	public List<SupplierPojo> searchSuppliers(TableDataInput input, SupplierSearchPojo searchParams, String tenantId) {
		List<SupplierPojo> globalList = new ArrayList<SupplierPojo>();
		if (searchParams.getGlobalSearch() == Boolean.TRUE && (FavouriteSupplierStatus.ACTIVE == searchParams.getStatus() || FavouriteSupplierStatus.INACTIVE == searchParams.getStatus())) {
			Query query = constructSearchSuppliersQuery(input, searchParams, false);
			query.setFirstResult(input.getStart());
			query.setMaxResults(input.getLength());
			globalList = query.getResultList();

			// In global search mode, find matching local suppliers based on global list
			if (CollectionUtil.isNotEmpty(globalList)) {
				List<String> supplierIds = new ArrayList<String>();
				for (SupplierPojo s : globalList) {
					supplierIds.add(s.getId());
				}

				query = constructFetchLocalSuppliersMatchingGlobalQuery(supplierIds, tenantId);
				List<SupplierPojo> localList = query.getResultList();
				for (SupplierPojo fsup : localList) {
					SupplierPojo sup = null;
					int index = globalList.indexOf(fsup);
					LOG.info("index : " + index);
					if (index != -1) {
						sup = globalList.get(index);
						if (sup != null) {
							sup.setFullName(fsup.getFsFullName());
							sup.setStatus(fsup.getFsStatus());
							sup.setFavourite(true);
						}
					}
				}
			}

		} else {
			Query query = constructSearchLocalSuppliersQuery(input, searchParams, false, tenantId);
			query.setFirstResult(input.getStart());
			query.setMaxResults(input.getLength());
			globalList = query.getResultList();
		}

		return globalList;
	}

	private Query constructSearchSuppliersQuery(TableDataInput input, SupplierSearchPojo searchParams, boolean isCount) {
		String hql = "";
		// If count query is enabled, then add the select count(*) clause
		if (isCount) {
			hql += "select count(distinct s.id) ";
		}

		if (!isCount) {
			hql += "select distinct new com.privasia.procurehere.core.pojo.SupplierPojo(s.id, s.companyName, c.countryName, s.yearOfEstablished, s.registrationDate, s.status, s.fullName, s.communicationEmail,s.designation, s.companyContactNumber, s.faxNumber ,s.registrationComplete) ";
		}
		hql += " from Supplier s ";

		// If this is not a count query, only then add the join fetch. Count query does not require its
		if (isCount) {
			hql += " left outer join s.registrationOfCountry as c left outer join s.naicsCodes as nc left outer join s.supplierProjects as sp";
		} else {
			hql += " left outer join s.registrationOfCountry as c left outer join s.naicsCodes as nc left outer join s.supplierProjects as sp";
		}
		hql += " where 1 = 1 ";

		// Add on search filter conditions
		for (ColumnParameter cp : input.getColumns()) {
			if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
				if (cp.getData().equals("activeInactive")) {
					continue;
					/*
					 * isStatusFilterOn = true; hql += " and s.status = (:" + cp.getData() + ")";
					 */
				} else if (cp.getData().equals("countryName")) {
					hql += " and upper(c.countryName ) like (:" + cp.getData().replace(".", "") + ")";
				} else {
					hql += " and upper(s." + cp.getData() + ") like (:" + cp.getData().replace(".", "") + ")";
				}
			}
		}

		// Add pojo params
		if (StringUtils.checkString(searchParams.getCompanyName()).length() > 0 || StringUtils.checkString(searchParams.getCompanyRegistrationNumber()).length() > 0 || StringUtils.checkString(searchParams.getProjectName()).length() > 0 || StringUtils.checkString(searchParams.getNaicsCode()).length() > 0) {
			hql += " and ( 1 = 0 ";

			// Append
			if (StringUtils.checkString(searchParams.getCompanyName()).length() > 0) {
				hql += " or upper(s.companyName) like :companyName";
			}
			if (StringUtils.checkString(searchParams.getCompanyRegistrationNumber()).length() > 0) {
				hql += " or upper(s.companyRegistrationNumber) like :companyRegistrationNumber";
			}
			if (StringUtils.checkString(searchParams.getProjectName()).length() > 0) {
				hql += " or upper(sp.projectName) like :projectName";
			}
			if (StringUtils.checkString(searchParams.getNaicsCode()).length() > 0) {
				hql += " or nc.id =:ncid";
			}

			hql += " )";
		}

		hql += " and s.status = :status ";

		// If it is not a count query then add order by clause
		if (!isCount) {
			List<OrderParameter> orderList = input.getOrder();
			if (CollectionUtil.isNotEmpty(orderList)) {
				hql += " order by ";
				for (OrderParameter order : orderList) {
					String orderColumn = input.getColumns().get(order.getColumn()).getData();
					if (orderColumn.equals("status")) {
						continue;
					}
					String dir = order.getDir();

					if (orderColumn.equals("countryName")) {
						hql += " s.registrationOfCountry.countryName " + dir + ",";
					} else {
						hql += " s." + orderColumn + " " + dir + ",";
					}
				}
				if (hql.lastIndexOf(",") == hql.length() - 1) {
					hql = hql.substring(0, hql.length() - 1);
				}

				if (StringUtils.checkString(searchParams.getOrder()).length() > 0 && (StringUtils.checkString(searchParams.getOrder()).equals("Newest"))) {
					hql += ", s.registrationDate desc";
				} else {
					hql += ", s.registrationDate asc";
				}
			} else {
				if (StringUtils.checkString(searchParams.getOrder()).length() > 0 && (StringUtils.checkString(searchParams.getOrder()).equals("Newest"))) {
					hql += " order by s.registrationDate desc";
				} else {
					hql += " order by s.registrationDate asc";
				}
			}
		}
		final Query query = getEntityManager().createQuery(hql);

		// Apply search filter values
		for (ColumnParameter cp : input.getColumns()) {
			if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
				if (cp.getData().equals("status")) {
					continue;
					// query.setParameter("status", Status.valueOf(cp.getSearch().getValue()));
				} else {
					query.setParameter(cp.getData().replace(".", ""), "%" + cp.getSearch().getValue().toUpperCase() + "%");
				}
			}
		}

		if (StringUtils.checkString(searchParams.getCompanyName()).length() > 0) {
			query.setParameter("companyName", "%" + searchParams.getCompanyName().toUpperCase() + "%");
		}
		if (StringUtils.checkString(searchParams.getCompanyRegistrationNumber()).length() > 0) {
			query.setParameter("companyRegistrationNumber", "%" + searchParams.getCompanyRegistrationNumber().toUpperCase() + "%");
		}

		if (StringUtils.checkString(searchParams.getProjectName()).length() > 0) {
			query.setParameter("projectName", "%" + searchParams.getProjectName().toUpperCase() + "%");
		}

		if (StringUtils.checkString(searchParams.getNaicsCode()).length() > 0) {
			query.setParameter("ncid", searchParams.getNaicsCode());
		}
		query.setParameter("status", SupplierStatus.APPROVED);
		return query;
	}

	private Query constructFetchLocalSuppliersMatchingGlobalQuery(List<String> supplierIds, String tenantId) {
		String sql = "select distinct new com.privasia.procurehere.core.pojo.SupplierPojo(s.id, fs.fullName, fs.status) ";
		sql += " from FavouriteSupplier fs left outer join fs.supplier s where s.id in (:supplierIds) and fs.buyer.id = :tenantId";
		final Query query = getEntityManager().createQuery(sql);
		query.setParameter("tenantId", tenantId);
		query.setParameter("supplierIds", supplierIds);
		return query;
	}

	private Query constructSearchLocalSuppliersQuery(TableDataInput input, SupplierSearchPojo searchParams, boolean isCount, String tenantId) {
		String base = "";
		// If count query is enabled, then add the select count(*) clause
		if (isCount) {
			base += "select count(distinct fs.id) ";
		} else {
			base += "select distinct new com.privasia.procurehere.core.pojo.SupplierPojo(s.id, s.companyName, rc.countryName,s.yearOfEstablished, s.registrationDate, fs.status, s.status, fs.fullName, s.fullName, s.registrationComplete,fs.createdDate) ";
		}

		base += " from FavouriteSupplier ";

		// If this is not a count query, only then add the join fetch. Count query does not require its
		if (isCount) {
			base += " fs left outer join fs.supplier s left outer join s.registrationOfCountry rc left outer join s.supplierProjects as sp left outer join fs.industryCategory ic ";
		} else {
			base += " fs left outer join fs.supplier s left outer join s.registrationOfCountry rc left outer join s.supplierProjects as sp left outer join fs.industryCategory ic  ";
		}
		base += " where fs.buyer.id = :tenantId ";

		if (searchParams.getRegistered() != null && searchParams.getRegistered() == Boolean.TRUE) {
			base += "and s.registrationComplete =:registered ";
		}

		boolean isStatusFilterOn = false;

		// Add on search filter conditions
		for (ColumnParameter cp : input.getColumns()) {
			if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
				String prefix = "s.";
				if (cp.getData().equals("fullName")) {
					prefix = "fs.";
				}

				if (cp.getData().equals("status")) {
					isStatusFilterOn = true;
					base += " and fs.status = (:" + cp.getData() + ")";
				} else if (cp.getData().equals("countryName")) {
					base += " and upper(rc.countryName ) like (:" + cp.getData().replace(".", "") + ")";
				} else {
					base += " and upper(" + prefix + cp.getData() + ") like (:" + cp.getData().replace(".", "") + ")";
				}
			}
		}

		String hql = "";

		// Add pojo params
		if (StringUtils.checkString(searchParams.getCompanyName()).length() > 0 || StringUtils.checkString(searchParams.getCompanyRegistrationNumber()).length() > 0 || StringUtils.checkString(searchParams.getProjectName()).length() > 0 || StringUtils.checkString(searchParams.getNaicsCode()).length() > 0 || StringUtils.checkString(searchParams.getIndustryCategories()).length() > 0) {
			hql += " ( 1 = 0 ";

			// Append
			if (StringUtils.checkString(searchParams.getCompanyName()).length() > 0) {
				hql += " or upper(s.companyName) like :companyName";
			}
			if (StringUtils.checkString(searchParams.getCompanyRegistrationNumber()).length() > 0) {
				hql += " or upper(s.companyRegistrationNumber) like :companyRegistrationNumber";
			}
			if (StringUtils.checkString(searchParams.getProjectName()).length() > 0) {
				hql += " or upper(sp.projectName) like :projectName";
			}
			// if (StringUtils.checkString(searchParams.getNaicsCode()).length() > 0) {
			// hql += " or nc.id =:ncid";
			// }

			if (StringUtils.checkString(searchParams.getIndustryCategories()).length() > 0) {
				hql += " or ic.id = :icid";
			}

			hql += " ) ";
		}

		if (hql.length() > 10) {
			base += " and ";
			base += hql;
		}

		if (!isStatusFilterOn) {
			base += " and fs.status = :status ";
		}
		for (ColumnParameter cp : input.getColumns()) {
			if (cp.getSearch().getValue().equals("SCHEDULED")) {
				base += "and fs.suspendStartDate > :suspendStartDate";
			}

		}

		// If it is not a count query then add order by clause
		if (!isCount) {
			List<OrderParameter> orderList = input.getOrder();
			if (CollectionUtil.isNotEmpty(orderList)) {
				for (OrderParameter order : orderList) {
					String orderColumn = input.getColumns().get(order.getColumn()).getData();

					String dir = order.getDir();

					if (orderColumn.equalsIgnoreCase("companyName")) {
						base += " order by s.companyName " + dir + ",";
					} else {
						if (orderColumn.equals("countryName")) {
							base += " order by s.registrationOfCountry.countryName " + dir + ",";
						} else if (orderColumn.equals("yearOfEstablished")) {
							base += " order by s.yearOfEstablished " + dir + ",";
						} else if (orderColumn.equals("registrationCompleteDate")) {
							base += " order by s.registrationDate " + dir + ",";

						} else if (orderColumn.equals("fullName")) {
							base += " order by s.fullName " + dir + ",";
						}
					}
				}

				if (base.lastIndexOf(",") == base.length() - 1) {
					base = base.substring(0, base.length() - 1);
				}
			} else {
				if (StringUtils.checkString(searchParams.getOrder()).length() > 0 && (StringUtils.checkString(searchParams.getOrder()).equals("Newest"))) {
					if (!isCount) {
						base += " order by fs.createdDate desc";
					}
				} else {
					if (!isCount) {
						base += " order by fs.createdDate asc";
					}
				}
			}
		}

		LOG.info("HQL " + base.toString());
		final Query query = getEntityManager().createQuery(base.toString());
		query.setParameter("tenantId", tenantId);
		// Apply search filter values
		for (ColumnParameter cp : input.getColumns()) {
			if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
				if (cp.getData().equals("status")) {
					LOG.info("Status : " + cp.getSearch().getValue());
					if (cp.getSearch().getValue().equals("SCHEDULED")) {
						cp.getSearch().setValue("ACTIVE");
						query.setParameter("suspendStartDate", new Date());
					}
					query.setParameter("status", FavouriteSupplierStatus.valueOf(cp.getSearch().getValue()));
				} else {
					query.setParameter(cp.getData().replace(".", ""), "%" + cp.getSearch().getValue().toUpperCase() + "%");
				}
			}
		}

		if (StringUtils.checkString(searchParams.getCompanyName()).length() > 0) {
			query.setParameter("companyName", "%" + searchParams.getCompanyName().toUpperCase() + "%");
		}
		if (StringUtils.checkString(searchParams.getCompanyRegistrationNumber()).length() > 0) {
			query.setParameter("companyRegistrationNumber", "%" + searchParams.getCompanyRegistrationNumber().toUpperCase() + "%");
		}

		if (StringUtils.checkString(searchParams.getProjectName()).length() > 0) {
			query.setParameter("projectName", "%" + searchParams.getProjectName().toUpperCase() + "%");
		}

		if (StringUtils.checkString(searchParams.getNaicsCode()).length() > 0) {
			query.setParameter("ncid", searchParams.getNaicsCode());
		}

		if (StringUtils.checkString(searchParams.getIndustryCategories()).length() > 0) {
			query.setParameter("icid", searchParams.getIndustryCategories());
		}
		// If status search filter is not ON then by default return only active records.
		if (!isStatusFilterOn) {
			query.setParameter("status", searchParams.getStatus());
		}
		if (searchParams.getRegistered() != null && searchParams.getRegistered() == Boolean.TRUE) {
			query.setParameter("registered", Boolean.FALSE);

		}
		return query;
	}

	@Override
	public Integer searchSuppliersCount(TableDataInput input, SupplierSearchPojo searchParams, String tenantId) {
		if (searchParams.getGlobalSearch() == Boolean.TRUE) {
			Query query = constructSearchSuppliersQuery(input, searchParams, true);
			return ((Number) query.getSingleResult()).intValue();
		} else {
			Query query = constructSearchLocalSuppliersQuery(input, searchParams, true, tenantId);
			return ((Number) query.getSingleResult()).intValue();
		}

	}

	@SuppressWarnings("unchecked")
	@Override
	public List<FavouriteSupplier> favoriteSuppliersOfBuyer(String buyerId, List<Supplier> invitedList, BigDecimal minGrade, BigDecimal maxGrade) {
		LOG.info("Buyer Id : " + buyerId);
		StringBuilder hsql = new StringBuilder("select distinct fs from FavouriteSupplier as fs inner join fetch fs.supplier s left outer join fetch s.registrationOfCountry c inner join fetch fs.buyer as b where b.id =:id and fs.status = :status ");
		if (CollectionUtil.isNotEmpty(invitedList)) {
			hsql.append(" and s not in (:invitedList)");
		}
		if (minGrade != null) {
			hsql.append(" and fs.ratings >= :minGrade ");
		}
		if (maxGrade != null) {
			hsql.append(" and fs.ratings <= :maxGrade  ");
		}
		final Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", buyerId);
		if (CollectionUtil.isNotEmpty(invitedList)) {
			query.setParameter("invitedList", invitedList);
		}
		if (minGrade != null) {
			query.setParameter("minGrade", minGrade);
		}
		if (maxGrade != null) {
			query.setParameter("maxGrade", maxGrade);
		}
		query.setParameter("status", FavouriteSupplierStatus.ACTIVE);
		return query.getResultList();

	}

	@Override
	public Integer favoriteSuppliersOfBuyerCount(String buyerId, List<Supplier> invitedList) {
		StringBuilder hsql = new StringBuilder("select count(fs) from FavouriteSupplier as fs inner join fs.supplier s inner join fs.buyer as b where b.id =:id and fs.status = :status ");
		if (CollectionUtil.isNotEmpty(invitedList)) {
			hsql.append(" and s not in (:invitedList)");
		}
		final Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", buyerId);
		if (CollectionUtil.isNotEmpty(invitedList)) {
			query.setParameter("invitedList", invitedList);
		}
		query.setParameter("status", FavouriteSupplierStatus.ACTIVE);
		Integer count = ((Number) query.getSingleResult()).intValue();
		return count;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Supplier> searchSupplierz(SupplierSearchPojo searchParams) {
		StringBuffer sb = new StringBuffer("select distinct s from Supplier s left outer join fetch s.registrationOfCountry as rc left outer join fetch s.companyStatus as cs left outer join fetch s.state as st left outer join fetch s.supplierProjects as sp where 1 = 1");
		// Append

		if (StringUtils.checkString(searchParams.getCompanyName()).length() > 0) {
			sb.append(" and s.companyName like :companyName");
		}

		final Query query = getEntityManager().createQuery(sb.toString());

		// set
		if (StringUtils.checkString(searchParams.getCompanyName()).length() > 0) {
			LOG.info("inside searchz dao ===" + searchParams.getCompanyName());
			query.setParameter("companyName", searchParams.getCompanyName());
		}

		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean isExists(FavouriteSupplier favouriteSupplier) {
		StringBuilder hsql = new StringBuilder("from FavouriteSupplier as fs left outer join fetch fs.supplier as s left outer join fetch fs.buyer as b where 1=1");

		if (favouriteSupplier.getSupplier() != null) {
			hsql.append(" and s = :s");
		}

		if (favouriteSupplier.getBuyer() != null) {
			hsql.append(" and b = :b");
		}
		final Query query = getEntityManager().createQuery(hsql.toString());
		if (favouriteSupplier.getSupplier() != null) {
			query.setParameter("s", favouriteSupplier.getSupplier());
		}
		if (favouriteSupplier.getBuyer() != null) {
			query.setParameter("b", favouriteSupplier.getBuyer());
		}
		List<FavouriteSupplier> fsList = query.getResultList();
		return CollectionUtil.isNotEmpty(fsList);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<FavouriteSupplier> searchFavSuppliers(SupplierSearchPojo searchParams) {
		StringBuffer sb = new StringBuffer("select distinct fs from FavouriteSupplier fs left outer join fetch fs.supplier as s left outer join fetch s.supplierProjects as sp where 1 = 0");
		// Append

		if (StringUtils.checkString(searchParams.getCompanyName()).length() > 0) {
			sb.append(" or upper(s.companyName) like :companyName");
		}

		if (StringUtils.checkString(searchParams.getCompanyRegistrationNumber()).length() > 0) {
			sb.append(" or upper(s.companyRegistrationNumber) like :companyRegistrationNumber");
		}

		if (StringUtils.checkString(searchParams.getProjectName()).length() > 0) {
			sb.append(" or upper(sp.projectName) like :projectName");
		}

		final Query query = getEntityManager().createQuery(sb.toString());

		// set
		if (StringUtils.checkString(searchParams.getCompanyName()).length() > 0) {
			query.setParameter("companyName", "%" + searchParams.getCompanyName().toUpperCase() + "%");
		}
		if (StringUtils.checkString(searchParams.getCompanyRegistrationNumber()).length() > 0) {
			query.setParameter("companyRegistrationNumber", "%" + searchParams.getCompanyRegistrationNumber().toUpperCase() + "%");
		}

		if (StringUtils.checkString(searchParams.getProjectName()).length() > 0) {
			query.setParameter("projectName", "%" + searchParams.getProjectName().toUpperCase() + "%");
		}

		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<FavouriteSupplier> searchFavSuppliersOfIndCat(String icId) {
		StringBuilder hsql = new StringBuilder("from FavouriteSupplier as fs inner join fs.industryCategory ic where ic.id =:id");

		final Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", icId);
		return query.getResultList();
	}

	@Override
	public FavouriteSupplier findFavSupplierBySuppId(String sId, String buyerId) {
		final Query query = getEntityManager().createQuery("from FavouriteSupplier as fs inner join fetch fs.supplier s left outer join fetch fs.industryCategory ic inner join fetch fs.buyer as b where s.id =:id and b.id = :buyerId");
		query.setParameter("id", sId);
		query.setParameter("buyerId", buyerId);
		return (FavouriteSupplier) query.getSingleResult();
	}

	@SuppressWarnings("unchecked")
	@Override
	public FavouriteSupplier findFavSupplierByFavSuppId(String fsId, String buyerId) {
		LOG.info("Fav Supplier Id = " + fsId + " : buyerId : " + buyerId);
		final Query query = getEntityManager().createQuery("from FavouriteSupplier as fs inner join fetch fs.supplier s left outer join fetch fs.industryCategory ic inner join fetch fs.buyer as b where fs.id = :id and b.id = :buyerId");
		query.setParameter("id", fsId);
		query.setParameter("buyerId", buyerId);
		// LOG.info("******************query.getSingleResult()*****" +(FavouriteSupplier) query.getSingleResult());
		List<FavouriteSupplier> favList = query.getResultList();
		if (CollectionUtil.isNotEmpty(favList)) {
			return favList.get(0);
		} else {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Supplier> searchSupplierInFavByOder(SearchVo searchVo) {
		StringBuilder hql = new StringBuilder(" select s from FavouriteSupplier fs left outer join fs.industryCategory ic left outer join fs.supplier s left outer join fetch s.registrationOfCountry rc left outer join fetch s.companyStatus cs where  s.status =:status");

		if (StringUtils.checkString(searchVo.getOrder()).length() > 0 && (StringUtils.checkString(searchVo.getOrder()).equals("Newest"))) {
			hql.append(" order by s.registrationDate desc");
		} else {
			hql.append(" order by s.registrationDate asc");
		}

		final Query query = getEntityManager().createQuery(hql.toString());
		if (StringUtils.checkString(searchVo.getStatus()).length() > 0) {
			query.setParameter("status", SupplierStatus.APPROVED);
		}
		return query.getResultList();
	}

	/************ For RFT Event add suppliers *******/

	@SuppressWarnings("unchecked")
	@Override
	public FavouriteSupplier getFavouriteSupplierBySupplierId(String supId, String buyerId) {
		StringBuffer sb = new StringBuffer("select distinct fs from FavouriteSupplier fs left outer join fetch fs.buyer as b left outer join fetch fs.supplier as s where s.id=:sid and b.id=:bid");
		try {
			final Query query = getEntityManager().createQuery(sb.toString());
			query.setParameter("sid", supId);
			query.setParameter("bid", buyerId);
			List<FavouriteSupplier> favList = query.getResultList();
			if (CollectionUtil.isNotEmpty(favList)) {
				return favList.get(0);
			} else {
				return null;
			}
		} catch (NoResultException nr) {
			LOG.info("Error while getting user : " + nr.getMessage(), nr);
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<FavouriteSupplier> searchCustomSupplier(SupplierSearchPojo searchParams, String buyerId) {
		LOG.info("Search Params : " + searchParams.toString());
		StringBuffer sb = new StringBuffer("select distinct fs from FavouriteSupplier fs inner join fetch fs.supplier as s inner join fs.buyer b where b.id =:id");
		// Append

		if (StringUtils.checkString(searchParams.getCompanyName()).length() > 0) {
			sb.append(" and upper(s.companyName) like :companyName");
		}

		if (StringUtils.checkString(searchParams.getCompanyRegistrationNumber()).length() > 0) {
			sb.append(" and upper(s.companyRegistrationNumber) like :companyRegistrationNumber");
		}
		LOG.info("SQL : " + sb.toString());
		final Query query = getEntityManager().createQuery(sb.toString());

		// set
		if (StringUtils.checkString(searchParams.getCompanyName()).length() > 0) {
			query.setParameter("companyName", "%" + searchParams.getCompanyName().toUpperCase() + "%");
		}
		if (StringUtils.checkString(searchParams.getCompanyRegistrationNumber()).length() > 0) {
			query.setParameter("companyRegistrationNumber", "%" + searchParams.getCompanyRegistrationNumber().toUpperCase() + "%");
		}
		query.setParameter("id", buyerId);

		return query.getResultList();

	}

	@SuppressWarnings("unchecked")
	@Override
	public List<FavouriteSupplier> searchFavouriteSupplierByCompanyNameOrRegistrationNo(String searchParam, String buyerId, List<String> invitedList) {
		// LOG.info("Search Params : " + searchParam.toString());
		StringBuffer sb = new StringBuffer("select distinct fs from FavouriteSupplier fs inner join fetch fs.supplier as s inner join fs.buyer b where (upper(s.companyName) like :companyName or upper(s.companyRegistrationNumber) like :companyRegistrationNumber) and b.id =:bid and fs.status = :status ");
		if (CollectionUtil.isNotEmpty(invitedList)) {
			sb.append(" and s.id not in (:invitedList)");
		}
		sb.append(" order by s.companyName ");
		final Query query = getEntityManager().createQuery(sb.toString());
		query.setParameter("status", FavouriteSupplierStatus.ACTIVE);
		query.setParameter("companyName", "%" + searchParam.toUpperCase() + "%");
		query.setParameter("companyRegistrationNumber", "%" + searchParam.toUpperCase() + "%");
		query.setParameter("bid", buyerId);
		if (CollectionUtil.isNotEmpty(invitedList)) {
			query.setParameter("invitedList", invitedList);
		}

		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<FavouriteSupplier> findAllFavouriteSuppliersForSuppliers(List<Supplier> suppliers) {
		StringBuilder hsql = new StringBuilder("from FavouriteSupplier as fs left outer join fetch fs.supplier as s left outer join fetch fs.buyer as b where s in (:s)");
		final Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("s", suppliers);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<FavouriteSupplier> favSuppliersByNameAndTenant(String search, String tenantId) {
		StringBuilder hsql = new StringBuilder("select distinct t from FavouriteSupplier as t inner join fetch t.supplier inner join fetch t.buyer as b where t.status = :status and t.buyer.id = :tenantId ");

		if (StringUtils.checkString(search).length() > 0) {
			hsql.append(" AND upper(t.fullName) like :fullName");
		}
		final Query query = getEntityManager().createQuery(hsql.toString());

		query.setParameter("status", FavouriteSupplierStatus.ACTIVE);
		query.setParameter("tenantId", tenantId);
		if (StringUtils.checkString(search).length() > 0) {
			query.setParameter("fullName", "%" + StringUtils.checkString(search).toUpperCase() + "%");
		}
		List<FavouriteSupplier> returnList = null;
		if (StringUtils.checkString(search).length() == 0) {
			query.setFirstResult(0);
			query.setMaxResults(10);
		}
		returnList = query.getResultList();
		return returnList;
	}

	@SuppressWarnings("unchecked")
	public List<FavouriteSupplier> getAllFavouriteSupplierFromGlobalSearch(String searchVal, String tenantId) {
		LOG.info("   getAllFavouriteSupplierFromGlobalSearch   ");

		final Query query = getEntityManager().createQuery("select distinct fs from FavouriteSupplier fs inner join fetch fs.supplier as s where (upper(s.companyName) like :searchVal) or (upper(s.companyRegistrationNumber) like :searchVal) and fs.buyer.id =:tenantId");
		query.setParameter("searchVal", "%" + searchVal.toUpperCase() + "%");
		query.setParameter("tenantId", tenantId);
		List<FavouriteSupplier> list = query.getResultList();
		return list;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Supplier isSupplierInGlobalList(Country country, String registrationNo, String companyName) {
		final Query query = getEntityManager().createQuery("select s from Supplier s inner join s.registrationOfCountry as c where (c.id =:countryId and s.companyRegistrationNumber =:registrationNo) or (upper(s.companyName) = :companyName and c.id =:countryId)");
		query.setParameter("countryId", country.getId());
		query.setParameter("registrationNo", registrationNo);
		query.setParameter("companyName", companyName.toUpperCase());
		List<Supplier> scList = (List<Supplier>) query.getResultList();
		return (CollectionUtil.isNotEmpty(scList) ? (Supplier) scList.get(0) : null);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Supplier isSupplierInRegNoandCompany(Country country, String registrationNo, String companyName) {
		final Query query = getEntityManager().createQuery("select s from Supplier s inner join s.registrationOfCountry as c where (c.id =:countryId and s.companyRegistrationNumber =:registrationNo) and (upper(s.companyName) = :companyName and c.id =:countryId)");
		query.setParameter("countryId", country.getId());
		query.setParameter("registrationNo", registrationNo);
		query.setParameter("companyName", companyName.toUpperCase());
		List<Supplier> scList = (List<Supplier>) query.getResultList();
		return (CollectionUtil.isNotEmpty(scList) ? (Supplier) scList.get(0) : null);
	}

	@Override
	public boolean isSupplierInFavouriteList(Supplier supplier, String buyerId) {
		final Query query = getEntityManager().createQuery("select count (fs) from FavouriteSupplier fs join fs.supplier s where s.id = :supplierId and fs.buyer.id = :buyerId");
		query.setParameter("supplierId", supplier.getId());
		query.setParameter("buyerId", buyerId);
		long count = ((Number) query.getSingleResult()).longValue();
		return (count > 0 ? true : false);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<FavouriteSupplier> getAllFavouriteSupplierByTenantId(String tenantId) {
		final Query query = getEntityManager().createQuery("select distinct fs from FavouriteSupplier fs inner join fetch fs.supplier as s left outer join fetch s.registrationOfCountry c left outer join fetch fs.industryCategory ic where fs.buyer.id =:tenantId");
		query.setParameter("tenantId", tenantId);
		List<FavouriteSupplier> list = query.getResultList();
		return list;
	}

	@Override
	public long countForFavSupplier(String tenantId) {
		final Query query = getEntityManager().createQuery("select count(fs) from FavouriteSupplier fs where fs.buyer.id = :tenantId");
		query.setParameter("tenantId", tenantId);
		return ((Number) query.getSingleResult()).longValue();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<SupplierCountPojo> getCurrentSupplierCountForTopFiveCategories(String tenantId) {
		String sql = getCurrentSupplierCountForTopFiveCategories();

		// resultSetMapping mapped in FavouriteSupplier.Java
		final Query query = getEntityManager().createNativeQuery(sql, "currentSupplierCount");
		query.setParameter("tenantId", tenantId);
		query.setParameter("status", Arrays.asList(EventStatus.DRAFT.toString(), EventStatus.CANCELED.toString()));
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<FavouriteSupplier> getAllActiveFavouriteSupplierByTenantId(String tenantId) {
		final Query query = getEntityManager().createQuery(" from FavouriteSupplier fs inner join fetch fs.supplier as s where fs.buyer.id =:tenantId and fs.status = :status");
		query.setParameter("tenantId", tenantId);
		query.setParameter("status", FavouriteSupplierStatus.ACTIVE);
		List<FavouriteSupplier> list = query.getResultList();
		return list;
	}

	@SuppressWarnings("unchecked")
	@Override
	public FavouriteSupplier getFavouriteSupplierByCompanyName(String tenantId, String companyName) {
		final Query query = getEntityManager().createQuery(" from FavouriteSupplier fs where fs.buyer.id =:tenantId and upper(fs.supplier.companyName) = :companyName");
		query.setParameter("tenantId", tenantId);
		query.setParameter("companyName", companyName.toUpperCase());
		List<FavouriteSupplier> list = query.getResultList();
		if (CollectionUtil.isNotEmpty(list)) {
			return list.get(0);
		} else {
			return null;
		}

	}

	@SuppressWarnings("unchecked")
	@Override
	public List<FavouriteSupplier> favoriteSuppliersOfBuyerByIndusCategory(String buyerId, List<Supplier> invitedList, List<IndustryCategory> categoryList, BigDecimal minGrade, BigDecimal maxGrade) {
		LOG.info("Buyer Id : " + buyerId);
		StringBuilder hsql = new StringBuilder("select distinct fs from FavouriteSupplier as fs inner join fetch fs.supplier s left outer join fetch s.registrationOfCountry c left outer join fetch fs.industryCategory ic inner join fetch fs.buyer as b where b.id =:id and fs.status = :status ");
		if (CollectionUtil.isNotEmpty(invitedList)) {
			hsql.append(" and s not in (:invitedList)");
		}
		if (CollectionUtil.isNotEmpty(categoryList)) {
			hsql.append(" and ic in (:categoryList)");
		}
		if (minGrade != null) {
			hsql.append(" and fs.ratings >= :minGrade ");
		}
		if (maxGrade != null) {
			hsql.append(" and fs.ratings <= :maxGrade  ");
		}
		LOG.info(" hsql :" + hsql.toString());
		final Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", buyerId);
		if (CollectionUtil.isNotEmpty(invitedList)) {
			query.setParameter("invitedList", invitedList);
		}
		if (CollectionUtil.isNotEmpty(categoryList)) {
			query.setParameter("categoryList", categoryList);
		}
		if (minGrade != null) {
			query.setParameter("minGrade", minGrade);
		}
		if (maxGrade != null) {
			query.setParameter("maxGrade", maxGrade);
		}
		query.setParameter("status", FavouriteSupplierStatus.ACTIVE);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<FavouriteSupplier> searchFavouriteSupplierByCompanyNameOrRegistrationNoByIndusCategory(String searchParam, String buyerId, List<String> invitedList, List<IndustryCategory> categoryList) {
		// LOG.info("Search Params : " + searchParam.toString());
		StringBuffer sb = new StringBuffer("select distinct fs from FavouriteSupplier fs inner join fetch fs.supplier as s inner join fs.buyer b left outer join fetch fs.industryCategory ic where (upper(s.companyName) like :companyName or upper(s.companyRegistrationNumber) like :companyRegistrationNumber) and b.id =:bid and fs.status = :status ");
		if (CollectionUtil.isNotEmpty(invitedList)) {
			sb.append(" and s.id not in (:invitedList)");
		}
		if (CollectionUtil.isNotEmpty(categoryList)) {
			sb.append(" and ic in (:categoryList)");
		}
		sb.append(" order by s.companyName ");
		final Query query = getEntityManager().createQuery(sb.toString());
		query.setParameter("status", FavouriteSupplierStatus.ACTIVE);
		query.setParameter("companyName", "%" + searchParam.toUpperCase() + "%");
		query.setParameter("companyRegistrationNumber", "%" + searchParam.toUpperCase() + "%");
		query.setParameter("bid", buyerId);
		if (CollectionUtil.isNotEmpty(invitedList)) {
			query.setParameter("invitedList", invitedList);
		}
		if (CollectionUtil.isNotEmpty(categoryList)) {
			query.setParameter("categoryList", categoryList);
		}
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<FavouriteSupplier> getAllFavouriteSupplierById(String[] favArr, String loggedInUserTenantId, boolean select_all, SearchFilterSupplierPojo searchFilterSupplierPojo) {

		String hql = "select distinct (fs) from FavouriteSupplier fs inner join fetch fs.supplier as s left outer join fetch s.registrationOfCountry c left outer join fetch fs.industryCategory ic where fs.buyer.id =:tenantId";

		if (!(select_all)) {
			if (favArr != null && favArr.length > 0) {
				hql += " and s.id in (:id)";
			}
		}

		List<FavouriteSupplierStatus> list = new ArrayList<FavouriteSupplierStatus>();
		if (searchFilterSupplierPojo != null) {
			if (StringUtils.checkString(searchFilterSupplierPojo.getCompanyname()).length() > 0) {
				hql += " and upper(s.companyName) like :companyname";
			}
			if (StringUtils.checkString(searchFilterSupplierPojo.getCountry()).length() > 0) {
				hql += " and upper(c.countryName) like :country";
			}
			if (StringUtils.checkString(searchFilterSupplierPojo.getPersonincharge()).length() > 0) {
				hql += " and upper(s.fullName) like :personincharge";
			}

			if (searchFilterSupplierPojo.getIsRegistered() != null && Boolean.TRUE == searchFilterSupplierPojo.getIsRegistered()) {
				hql += " and s.registrationComplete =:registered";
			}

			if (StringUtils.checkString(searchFilterSupplierPojo.getStatus()).length() > 0) {
				hql += " and upper(fs.status) like :status";
				String[] types = searchFilterSupplierPojo.getStatus().split(",");
				if (types != null && types.length > 0) {
					for (String ty : types) {
						list.add(FavouriteSupplierStatus.valueOf(ty));
					}
				}
			}

		}

		final Query query = getEntityManager().createQuery(hql);

		if (!(select_all)) {
			if (favArr != null && favArr.length > 0) {
				query.setParameter("id", Arrays.asList(favArr));
			}
		}

		if (searchFilterSupplierPojo != null) {
			if (StringUtils.checkString(searchFilterSupplierPojo.getCompanyname()).length() > 0) {
				query.setParameter("companyname", "%" + searchFilterSupplierPojo.getCompanyname().toUpperCase() + "%");
			}
			if (StringUtils.checkString(searchFilterSupplierPojo.getCountry()).length() > 0) {
				query.setParameter("country", "%" + searchFilterSupplierPojo.getCountry().toUpperCase() + "%");
			}
			if (StringUtils.checkString(searchFilterSupplierPojo.getPersonincharge()).length() > 0) {
				query.setParameter("personincharge", "%" + searchFilterSupplierPojo.getPersonincharge().toUpperCase() + "%");
			}
			if (StringUtils.checkString(searchFilterSupplierPojo.getStatus()).length() > 0) {
				query.setParameter("status", list);
			}
			if (searchFilterSupplierPojo.getIsRegistered() != null && Boolean.TRUE == searchFilterSupplierPojo.getIsRegistered()) {
				query.setParameter("registered", Boolean.FALSE);

			}
		}
		query.setParameter("tenantId", loggedInUserTenantId);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<FavouriteSupplier> getAllFavouriteSupplierByIdForExport(String loggedInUserTenantId) {
		String sql = "select distinct (fs) from FavouriteSupplier fs inner join fetch fs.supplier as s left outer join fetch s.registrationOfCountry c left outer join fetch fs.industryCategory ic where fs.buyer.id =:tenantId";
		final Query query = getEntityManager().createQuery(sql);
		query.setParameter("tenantId", loggedInUserTenantId);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public FavouriteSupplier getFavouriteSupplierBySupplierIdForReport(String supId, String buyerId) {
		StringBuffer sb = new StringBuffer("select distinct fs from FavouriteSupplier fs left outer join fetch fs.buyer as b left outer join fetch fs.supplier as s left outer join fetch s.naicsCodes as nc   where s.id=:sid and b.id=:bid");
		try {
			final Query query = getEntityManager().createQuery(sb.toString());
			query.setParameter("sid", supId);
			query.setParameter("bid", buyerId);
			List<FavouriteSupplier> favList = query.getResultList();
			if (CollectionUtil.isNotEmpty(favList)) {
				return favList.get(0);
			} else {
				return null;
			}
		} catch (NoResultException nr) {
			LOG.info("Error while getting user : " + nr.getMessage(), nr);
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public FavouriteSupplier getFavouriteSupplierBySupplierIdReport(String supId) {
		StringBuffer sb = new StringBuffer("select distinct fs from FavouriteSupplier fs left outer join fetch fs.buyer as b left outer join fetch fs.supplier as s left outer join fetch s.naicsCodes as nc   where s.id=:sid ");
		try {
			final Query query = getEntityManager().createQuery(sb.toString());
			query.setParameter("sid", supId);
			List<FavouriteSupplier> favList = query.getResultList();
			if (CollectionUtil.isNotEmpty(favList)) {
				return favList.get(0);
			} else {
				return null;
			}
		} catch (NoResultException nr) {
			LOG.info("Error while getting user : " + nr.getMessage(), nr);
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ProductCategory> getSupplierProductCategoryBySupIdORTenantId(String tenantId, String supId) {
		StringBuffer sb = new StringBuffer("select distinct(fs.productCategory) from FavouriteSupplier fs left outer join  fs.productCategory as pc left outer join  fs.buyer as b left outer join  fs.supplier as s   where s.id=:sid and b.id=:bid");
		try {
			final Query query = getEntityManager().createQuery(sb.toString());
			query.setParameter("sid", supId);
			query.setParameter("bid", tenantId);
			return query.getResultList();

		} catch (NoResultException nr) {
			LOG.info("Error while getting user : " + nr.getMessage(), nr);
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<FavouriteSupplier> getAllFavouriteSupplier(String buyerId) {
		String hql = "select distinct fs from FavouriteSupplier  fs where fs.buyer.id=:buyerId";
		final Query query = getEntityManager().createQuery(hql);
		query.setParameter("buyerId", buyerId);
		try {
			List<FavouriteSupplier> favouriteSuppliers = query.getResultList();
			return favouriteSuppliers;
		} catch (Exception e) {
			LOG.error(e.getMessage() + " " + e);
			return null;
		}

	}

	@Override
	public FavouriteSupplier getFavouriteSupplierBySupplierId(String supplierId) {
		String hql = "select distinct fs from  FavouriteSupplier fs where fs.supplier.id=:supplierId";
		Query query = getEntityManager().createQuery(hql);
		query.setParameter("supplierId", supplierId);
		try {
			return (FavouriteSupplier) query.getResultList().get(0);
		} catch (Exception e) {
			LOG.error(e.getMessage() + " " + e);
			return null;
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<FavouriteSupplier> getAllFavSupplierToInActive(String tenantId) {
		String hql = "select distinct fs from  FavouriteSupplier fs where fs.buyer.id =:tenantId";
		Query query = getEntityManager().createQuery(hql);
		query.setParameter("tenantId", tenantId);
		try {
			return query.getResultList();
		} catch (Exception e) {
			LOG.error(e);
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<EventSupplierPojo> favoriteEventSupplierPojosOfBuyerByIndusCategory(String tenantId, boolean includeIndustryCategories, String eventId, RfxTypes eventType, String search, boolean isMinMaxPresent) {

		String hql = constructQueryToFetchSuppliersForSelection(includeIndustryCategories, eventType, search, isMinMaxPresent);
		Query query = getEntityManager().createNativeQuery(hql, "favSupplierResult");
		query.setMaxResults(30);
		query.setParameter("tenantId", tenantId);
		query.setParameter("eventId", eventId);

		if (StringUtils.checkString(search).length() > 0) {
			query.setParameter("search", "%" + search.toUpperCase() + "%");
		}

		try {
			return query.getResultList();
		} catch (Exception e) {
			LOG.error(e);
			return null;
		}
	}

	private String constructQueryToFetchSuppliersForSelection(boolean includeIndustryCategories, RfxTypes eventType, String search, boolean isMinMaxPresent) {
		String hql = "SELECT DISTINCT sup.SUPPLIER_ID as id,sup.COMPANY_NAME as companyName FROM PROC_FAVOURITE_SUPPLIER s,PROC_SUPPLIER sup";
		if (includeIndustryCategories) {
			hql += ",PROC_FAV_SUPP_IND_CAT cat";
		}
		if (isMinMaxPresent) {
			hql += " ,PROC_" + eventType.name() + "_EVENTS e ";
		}

		hql += " WHERE BUYER_ID =:tenantId and s.SUPPLIER_STATUS ='ACTIVE'  AND s.SUPPLIER_ID = sup.SUPPLIER_ID";
		if (includeIndustryCategories) {
			hql += " AND cat.FAV_SUPP_ID = s.FAV_SUPPLIER_ID  AND cat.IND_CAT_ID IN (SELECT i.IND_CAT_ID FROM PROC_" + eventType.name() + "_EVENT_INDUS_CAT i WHERE i.EVENT_ID = :eventId)";
		}

		if (isMinMaxPresent) {
			LOG.info("Min max present:");
			hql += " AND e.id = :eventId ";
			hql += " AND case when e.MINIMUM_SUPPLIER_RATING is not null and e.MAXIMUM_SUPPLIER_RATING is null and (s.RATINGS >= e.MINIMUM_SUPPLIER_RATING or s.RATINGS is null) then 1 ";
			hql += " when e.MAXIMUM_SUPPLIER_RATING is not null and e.MINIMUM_SUPPLIER_RATING is null and (s.RATINGS <= e.MAXIMUM_SUPPLIER_RATING or s.RATINGS is null) then 1 ";
			hql += " when e.MAXIMUM_SUPPLIER_RATING is not null and e.MINIMUM_SUPPLIER_RATING is not null and ((s.RATINGS >= e.MINIMUM_SUPPLIER_RATING and s.RATINGS <= e.MAXIMUM_SUPPLIER_RATING) or (s.RATINGS is null)) then 1 ";
			hql += " else 0 end = 1 ";
		}

		if (StringUtils.checkString(search).length() > 0) {
			hql += " AND upper(sup.COMPANY_NAME) like (:search)";
		}
		if ("POSTGRESQL".equals(env.getRequiredProperty("db.jpa.type"))) {
			hql += " EXCEPT SELECT DISTINCT sup.SUPPLIER_ID as id,sup.COMPANY_NAME as companyName FROM PROC_" + eventType.name() + "_EVENT_SUPPLIERS es,PROC_SUPPLIER sup WHERE sup.SUPPLIER_ID=es.SUPPLIER_ID and es.EVENT_ID = :eventId";
			hql += " order by companyName";
		} else {
			hql += " EXCEPT SELECT DISTINCT sup.SUPPLIER_ID as id,sup.COMPANY_NAME as companyName FROM PROC_" + eventType.name() + "_EVENT_SUPPLIERS es,PROC_SUPPLIER sup WHERE sup.SUPPLIER_ID=es.SUPPLIER_ID and es.EVENT_ID = :eventId ";
			hql += " order by companyName OFFSET 0 ROWS";
		}

		return hql;
	}

	@Override
	public long countFavoriteEventSupplierPojosOfBuyerByIndusCategory(String tenantId, boolean includeIndustryCategories, String eventId, RfxTypes eventType, String search, boolean isMinMaxPresent) {

		String hql = " select count(*) from ( ";
		hql += constructQueryToFetchSuppliersForSelection(includeIndustryCategories, eventType, search, isMinMaxPresent);
		hql += ") a";

		Query query = getEntityManager().createNativeQuery(hql);
		query.setMaxResults(30);
		query.setParameter("tenantId", tenantId);
		query.setParameter("eventId", eventId);

		if (StringUtils.checkString(search).length() > 0) {
			query.setParameter("search", "%" + search.toUpperCase() + "%");
		}

		return ((Number) query.getSingleResult()).intValue();
	}

	@Override
	public FavouriteSupplier findFavSupplierById(String id) {
		final Query query = getEntityManager().createQuery("from FavouriteSupplier as fs where fs.id=:id");
		query.setParameter("id", id);
		try {
			return (FavouriteSupplier) query.getSingleResult();
		} catch (Exception e) {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<FavouriteSupplier> findAllFavouriteSupplierForSuspension() {
		final Query query = getEntityManager().createQuery("from FavouriteSupplier as fs where fs.suspendStartDate is not null ");
		try {
			return query.getResultList();
		} catch (Exception e) {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<FavouriteSupplier> findAllFavouriteSupplierToRemoveSuspension() {
		final Query query = getEntityManager().createQuery("from FavouriteSupplier as fs where fs.suspendEndDate is not null");
		try {
			return query.getResultList();
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public Supplier getSupplierByFavSupplierId(String favSupplierId) {
		String hql = "select fs.supplier from FavouriteSupplier fs where fs.id=:favSupplierId ";
		Query query = getEntityManager().createQuery(hql);
		query.setParameter("favSupplierId", favSupplierId);
		try {
			return (Supplier) query.getSingleResult();
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public SupplierSearchPojo getTotalAndPendingSupplierCountForBuyer(String buyerId) {
		StringBuilder hsql = new StringBuilder("select new com.privasia.procurehere.core.pojo.SupplierSearchPojo(count(fs), sum(case when s.registrationComplete = false then 1 else 0 end)) from FavouriteSupplier as fs inner join fs.supplier s inner join fs.buyer as b where b.id = :id and fs.status = :status ");
		final Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", buyerId);
		query.setParameter("status", FavouriteSupplierStatus.ACTIVE);
		return (SupplierSearchPojo) query.getSingleResult();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<FavouriteSupplier> findInvitedSupplierByIndCat(String sId, Boolean select_all, SupplierSearchPojo searchParams, String buyerId, List<IndustryCategory> industryCategories, Boolean exclusive, Boolean inclusive) {
		String hql = "select distinct (fs) from FavouriteSupplier as fs inner join fetch fs.supplier s left outer join fetch fs.industryCategory ic left outer join s.registrationOfCountry c left outer join s.state sts left outer join fs.supplierTags st inner join fetch fs.buyer as b where b.id = :buyerId and fs.status = :status";
		LOG.info("select_all" + select_all);

		if (!(select_all)) {
			if (sId != null && (StringUtils.checkString(sId).length() > 0)) {
				LOG.info("select_all" + sId);
				hql += " and s.id = :id";
			}
		}
		if (searchParams != null && StringUtils.checkString(searchParams.getRegistrationOfCountry()).length() > 0) {
			hql += " and c.id = :country";
		}
		if (searchParams != null && searchParams.getState() != null && searchParams.getState().length > 0) {
			hql += " and (s.state.id) in (:state)";
		}
		if (CollectionUtil.isNotEmpty(industryCategories)) {
			hql += " and ic in (:industryCategories)";
		}
		if ((searchParams != null && searchParams.getSupplierTagName() != null && searchParams.getSupplierTagName().length > 0) && Boolean.TRUE == inclusive) {
			hql += " and (st.id)  in (:supplierTagName)";
		}
		if ((searchParams != null && searchParams.getSupplierTagName() != null && searchParams.getSupplierTagName().length > 0) && Boolean.TRUE == exclusive) {
			hql += " and (st.id) not in (:supplierTagName)";
		}
		final Query query = getEntityManager().createQuery(hql);

		if (searchParams != null && StringUtils.checkString(searchParams.getRegistrationOfCountry()).length() > 0) {
			query.setParameter("country", searchParams.getRegistrationOfCountry());
		}

		if (searchParams != null && searchParams.getState() != null && searchParams.getState().length > 0) {
			query.setParameter("state", Arrays.asList(searchParams.getState()));
		}
		if (!(select_all)) {
			if (sId != null && (StringUtils.checkString(sId).length() > 0)) {
				query.setParameter("id", sId);
			}
		}
		if (CollectionUtil.isNotEmpty(industryCategories)) {
			query.setParameter("industryCategories", industryCategories);
		}
		if (searchParams != null && (searchParams.getSupplierTagName() != null && searchParams.getSupplierTagName().length > 0) && Boolean.TRUE == inclusive) {
			query.setParameter("supplierTagName", Arrays.asList(searchParams.getSupplierTagName()));
		}
		if (searchParams != null && (searchParams.getSupplierTagName() != null && searchParams.getSupplierTagName().length > 0) && Boolean.TRUE == exclusive) {
			query.setParameter("supplierTagName", Arrays.asList(searchParams.getSupplierTagName()));
		}
		query.setParameter("buyerId", buyerId);
		query.setParameter("status", FavouriteSupplierStatus.ACTIVE);
		try {
			return query.getResultList();
		} catch (Exception e) {
			LOG.error(e);
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<FavouriteSupplier> findInvitedSupplierBySuppId(String sId, Boolean select_all, SupplierSearchPojo searchParams, String buyerId, Boolean exclusive, Boolean inclusive) {
		String hql = "select distinct (fs) from FavouriteSupplier as fs inner join fetch fs.supplier s left outer join fetch fs.industryCategory ic left outer join s.registrationOfCountry c left outer join s.state sts  left outer join fs.supplierTags st inner join fetch fs.buyer as b where b.id = :buyerId and fs.status = :status";

		if (Boolean.FALSE == select_all && StringUtils.checkString(sId).length() > 0) {
			hql += " and s.id = :id";
		}
		if (StringUtils.checkString(searchParams.getRegistrationOfCountry()).length() > 0) {
			hql += " and c.id = :country";
		}
		if (searchParams.getState() != null && searchParams.getState().length > 0) {
			hql += " and s.state.id in (:state)";
		}
		if ((searchParams.getSupplierTagName() != null && searchParams.getSupplierTagName().length > 0) && Boolean.TRUE == inclusive) {
			hql += " and (st.id)  in (:supplierTagName)";
		}
		if ((searchParams.getSupplierTagName() != null && searchParams.getSupplierTagName().length > 0) && Boolean.TRUE == exclusive) {
			hql += " and (st.id) not in (:supplierTagName)";
		}
		LOG.info(" hsql :" + hql.toString());
		final Query query = getEntityManager().createQuery(hql);

		if (Boolean.FALSE == select_all && StringUtils.checkString(sId).length() > 0) {
			query.setParameter("id", sId);
		}
		if (StringUtils.checkString(searchParams.getRegistrationOfCountry()).length() > 0) {
			query.setParameter("country", searchParams.getRegistrationOfCountry());
		}

		if (searchParams.getState() != null && searchParams.getState().length > 0) {
			query.setParameter("state", Arrays.asList(searchParams.getState()));
		}
		if ((searchParams.getSupplierTagName() != null && searchParams.getSupplierTagName().length > 0) && Boolean.TRUE == inclusive) {
			query.setParameter("supplierTagName", Arrays.asList(searchParams.getSupplierTagName()));
		}
		if ((searchParams.getSupplierTagName() != null && searchParams.getSupplierTagName().length > 0) && Boolean.TRUE == exclusive) {
			query.setParameter("supplierTagName", Arrays.asList(searchParams.getSupplierTagName()));
		}
		query.setParameter("buyerId", buyerId);
		query.setParameter("status", FavouriteSupplierStatus.ACTIVE);
		try {
			return query.getResultList();
		} catch (Exception e) {
			LOG.error(e);
			return null;
		}
	}

	@Override
	public boolean isSelfInviteSupplierInFavouriteList(String supplierId, String buyerId) {
		final Query query = getEntityManager().createQuery("select count (fs) from FavouriteSupplier fs where fs.supplier.id =:supplierId and fs.buyer.id =:buyerId and fs.status = :status ");
		query.setParameter("supplierId", supplierId);
		query.setParameter("buyerId", buyerId);
		query.setParameter("status", FavouriteSupplierStatus.ACTIVE);
		return ((Number) query.getSingleResult()).intValue() > 0;
	}

	@Override
	public FavouriteSupplier getActiveFavouriteSupplierBySupplierIdForBuyer(String supplierId, String buyerId) {
		final Query query = getEntityManager().createQuery("select fs from FavouriteSupplier fs where fs.supplier.id =:supplierId and fs.buyer.id =:buyerId and fs.status = :status ");
		query.setParameter("supplierId", supplierId);
		query.setParameter("buyerId", buyerId);
		query.setParameter("status", FavouriteSupplierStatus.ACTIVE);
		return ((FavouriteSupplier) query.getSingleResult());
	}

	@Override
	public boolean existsEventCategoriesInSupplier(String eventId, String supplierId, RfxTypes eventType) {
		String sql = "";
		sql += "SELECT  (CASE WHEN CNT between 1 and (SELECT COUNT(c.IND_CAT_ID) FROM PROC_" + eventType.name() + "_EVENT_INDUS_CAT c WHERE c.EVENT_ID = :eventId) THEN 1 ELSE 0 END) FROM ";
		sql += " ( SELECT COUNT(*) CNT FROM( ";
		sql += " SELECT ic.IND_CAT_ID FROM PROC_" + eventType.name() + "_EVENT_INDUS_CAT ic WHERE ic.EVENT_ID = :eventId ";
		sql += " INTERSECT ";
		sql += " SELECT sc.IND_CAT_ID FROM PROC_FAVOURITE_SUPPLIER fs JOIN PROC_FAV_SUPP_IND_CAT sc ON sc.FAV_SUPP_ID=fs.FAV_SUPPLIER_ID WHERE fs.SUPPLIER_ID = :supplierId ";
		sql += ") a ";
		sql += ") b ";
		final Query query = getEntityManager().createNativeQuery(sql);
		query.setParameter("eventId", eventId);
		query.setParameter("supplierId", supplierId);
		return ((Number) query.getSingleResult()).intValue() > 0;
	}

	@Override
	public boolean isSupplierRatingMatchToEventRating(String supplierId, BigDecimal minRating, BigDecimal maxRating, String buyerId) {
		String hql = "select count (fs) from FavouriteSupplier fs where fs.supplier.id = :supplierId and fs.buyer.id =:buyerId";
		boolean minMax = false;
		if (minRating != null) {
			hql += " and ((fs.ratings >= :minRating ";
			minMax = true;
		}
		if (maxRating != null) {
			hql += " and fs.ratings <= :maxRating)";
			minMax = true;
		}
		if (minMax) {
			hql += " or (fs.ratings is null))";
		}
		final Query query = getEntityManager().createQuery(hql.toString());
		query.setParameter("buyerId", buyerId);
		query.setParameter("supplierId", supplierId);
		if (minRating != null) {
			query.setParameter("minRating", minRating);
		}
		if (maxRating != null) {
			query.setParameter("maxRating", maxRating);
		}
		return ((Number) query.getSingleResult()).intValue() > 0;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<EventSupplierPojo> favoriteEventSupplierPojosOfBuyerByIndusCategoryForAutoSave(String tenantId, boolean includeIndustryCategories, String eventId, RfxTypes eventType, String search, boolean isMinMaxPresent) {

		String hql = constructQueryToFetchSuppliersForSelection(includeIndustryCategories, eventType, search, isMinMaxPresent);
		Query query = getEntityManager().createNativeQuery(hql, "favSupplierResult");
		query.setParameter("tenantId", tenantId);
		query.setParameter("eventId", eventId);

		if (StringUtils.checkString(search).length() > 0) {
			query.setParameter("search", "%" + search.toUpperCase() + "%");
		}

		try {
			return query.getResultList();
		} catch (Exception e) {
			LOG.error(e);
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<IndustryCategoryPojo> getTopTwentyFiveCategory(String tanentId) {
		String sql = getTopTwentyEventCategoryNativeQuery();
		final Query query = getEntityManager().createNativeQuery(sql, "industryCat");
		query.setParameter("tenantId", tanentId);
		return query.getResultList();

	}

	@Override
	public long getScheduledSupplier(String loggedInUserTenantId) {
		String hql = "select count(*) from FavouriteSupplier fs where fs.suspendStartDate is not null and fs.isFutureSuspended=:isFutureSuspended and fs.buyer.id = :tenantId ";
		Query query = getEntityManager().createQuery(hql);
		query.setParameter("isFutureSuspended", Boolean.TRUE);
		query.setParameter("tenantId", loggedInUserTenantId);
		try {
			return ((Number) query.getSingleResult()).longValue();
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			return 0;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<EventSupplierPojo> favoritePrSupplierPojosOfBuyerByIndusCategory(String tenantId, boolean includeIndustryCategories, String prId, String search) {

		String hql = constructQueryToFetchSuppliersForPrSelection(includeIndustryCategories, search, true);
		Query query = getEntityManager().createNativeQuery(hql, "favSupplierResult");
		query.setParameter("tenantId", tenantId);
		if (includeIndustryCategories) {
			query.setParameter("prId", prId);
		}

		if (StringUtils.checkString(search).length() > 0) {
			query.setParameter("search", "%" + search.toUpperCase() + "%");
		}

		try {
			return query.getResultList();
		} catch (Exception e) {
			LOG.error(e);
			return null;
		}
	}

	private String constructQueryToFetchSuppliersForPrSelection(boolean includeIndustryCategories, String search, boolean count) {
		String hql = "SELECT DISTINCT s.FAV_SUPPLIER_ID as id,sup.COMPANY_NAME as companyName FROM PROC_FAVOURITE_SUPPLIER s,PROC_SUPPLIER sup";
		if (includeIndustryCategories) {
			hql += ", PROC_PRODUCT_CATEGORY cat";
		}
		hql += " WHERE BUYER_ID =:tenantId and s.SUPPLIER_STATUS ='ACTIVE'  AND s.SUPPLIER_ID = sup.SUPPLIER_ID";
		if (includeIndustryCategories) {
			hql += " AND cat.ID = s.FAV_SUPPLIER_ID  AND cat.ID IN (SELECT i.PRODUCT_CATEGORY_ID FROM PROC_PR_ITEM i WHERE i.PR_ID = :prId AND i.PRODUCT_CATEGORY_ID IS NOT NULL)";
		}

		if (StringUtils.checkString(search).length() > 0) {
			hql += " AND upper(sup.COMPANY_NAME) like (:search)";
		}
		if (count) {
			hql += " order by companyName";
		}
		return hql;
	}

	@Override
	public long countConstructQueryToFetchSuppliersForPrSelection(String tenantId, boolean includeIndustryCategories, String prId, String search) {

		String hql = " select count(*) from ( ";
		hql += constructQueryToFetchSuppliersForPrSelection(includeIndustryCategories, search, false);
		hql += ") a";

		Query query = getEntityManager().createNativeQuery(hql);
		query.setMaxResults(30);
		query.setParameter("tenantId", tenantId);
		if (includeIndustryCategories) {
			query.setParameter("prId", prId);
		}

		if (StringUtils.checkString(search).length() > 0) {
			query.setParameter("search", "%" + search.toUpperCase() + "%");
		}
		return ((Number) query.getSingleResult()).intValue();
	}

	@SuppressWarnings("unchecked")
	@Override
	public FavouriteSupplier getFavouriteSupplierByVendorCode(String vendorCode, String buyerId) {
		StringBuffer sb = new StringBuffer("select distinct fs from FavouriteSupplier fs left outer join fetch fs.productCategory pc left outer join fetch fs.supplier as s where fs.vendorCode = :vendorCode and fs.buyer.id = :buyerId");
		try {
			final Query query = getEntityManager().createQuery(sb.toString());
			query.setParameter("vendorCode", vendorCode);
			query.setParameter("buyerId", buyerId);
			List<FavouriteSupplier> favList = query.getResultList();
			if (CollectionUtil.isNotEmpty(favList)) {
				return favList.get(0);
			} else {
				return null;
			}
		} catch (NoResultException nr) {
			LOG.info("Error while getting Favourite Supplier based on vendor code : " + nr.getMessage(), nr);
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public Supplier getSupplierByVendorCodeAndBuyerId(String vendorCode, String buyerId) {
		StringBuffer sb = new StringBuffer("select s from FavouriteSupplier fs join fs.supplier s where fs.vendorCode = :vendorCode and fs.buyer.id = :buyerId");
		try {
			final Query query = getEntityManager().createQuery(sb.toString());
			query.setParameter("vendorCode", vendorCode);
			query.setParameter("buyerId", buyerId);
			List<Supplier> favList = query.getResultList();
			if (CollectionUtil.isNotEmpty(favList)) {
				return favList.get(0);
			} else {
				return null;
			}
		} catch (NoResultException nr) {
			LOG.info("Error while getting Supplier based on vendor code and buyer : " + nr.getMessage(), nr);
			return null;
		}
	}

	@Override
	public String getFavouriteSupplierVendorCodeBySupplierIdAndTenant(String supplierId, String tenantId) {
		StringBuffer sb = new StringBuffer("select fs.vendorCode from FavouriteSupplier fs where fs.supplier.id = :supplierId and fs.buyer.id = :tenantId");
		try {
			final Query query = getEntityManager().createQuery(sb.toString());
			query.setParameter("supplierId", supplierId);
			query.setParameter("tenantId", tenantId);
			return ((String) query.getSingleResult());
		} catch (NoResultException nr) {
			LOG.info("Error while getting  Supplier based on vendor code : " + nr.getMessage(), nr);
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<FavouriteSupplier> getAllActiveFavouriteSupplierByTenantIdForAnnouncement(String tenantId) {
		final Query query = getEntityManager().createQuery("select distinct new com.privasia.procurehere.core.entity.FavouriteSupplier(fs.id, fs.companyContactNumber, fs.communicationEmail, fs.favouriteSupplierTaxNumber, s.mobileNumber,s.communicationEmail, s.faxNumber,s.companyName,s.id) from FavouriteSupplier fs inner join fs.supplier as s where fs.buyer.id =:tenantId and fs.status = :status and s.registrationComplete = true ");
		query.setParameter("tenantId", tenantId);
		query.setParameter("status", FavouriteSupplierStatus.ACTIVE);
		List<FavouriteSupplier> list = query.getResultList();
		return list;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<EventSupplierPojo> getAllFavSupplierToSendRfaEventInvitation(String tenantId, String eventId) {
		StringBuilder hsql = new StringBuilder("select distinct new com.privasia.procurehere.core.pojo.EventSupplierPojo(res.id,s.id,ss.timeZone.timeZone, e.tenantId, u.name, u.communicationEmail, u.emailNotifications ,u.deviceId, bu.displayName,u.id,s.companyName,s.faxNumber,s.mobileNumber,res.companyContactNumber,res.favouriteSupplierTaxNumber) from FavouriteSupplier res,User u inner join res.supplier as s inner join u.userRole ur inner join ur.accessControlList acl ,RfaEvent e left outer join e.businessUnit as bu , SupplierSettings ss where ss.supplier.id = s.id and  e.id = :id and acl.aclValue = 'ROLE_ADMIN'  and s.id = u.tenantId and res.buyer.id =:tenantId and res.status=:status and s.registrationComplete = true ");
		final Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", eventId);
		query.setParameter("tenantId", tenantId);
		query.setParameter("status", FavouriteSupplierStatus.ACTIVE);
		return query.getResultList();
	}

	@Override
	public long getSupplierCountBasedOnStatusForBuyer(FavouriteSupplierStatus status, String buyerId) {
		StringBuilder hsql = new StringBuilder("select count(distinct fs.id) from FavouriteSupplier as fs left outer join fs.buyer as b where b.id = :id and fs.status = :status ");
		final Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", buyerId);
		query.setParameter("status", status);
		return ((Number) query.getSingleResult()).longValue();
	}

	private Query constructFavSuppliersQueryBasedOnStatus(TableDataInput input, boolean isCount, FavouriteSupplierStatus status, String tenantId) {
		String base = "";
		// If count query is enabled, then add the select count(*) clause
		if (isCount) {
			base += "select count(distinct fs.id) ";
		} else {
			base += "select distinct new com.privasia.procurehere.core.pojo.SupplierPojo(s.id, s.companyName, rc.countryName,s.yearOfEstablished, s.registrationDate, fs.status, s.status, fs.fullName, s.fullName, s.registrationComplete, fs.createdDate) ";
		}

		base += " from FavouriteSupplier ";

		// If this is not a count query, only then add the join fetch. Count query does not require its
		if (isCount) {
			base += " fs left outer join fs.supplier s left outer join s.registrationOfCountry rc left outer join s.supplierProjects as sp left outer join fs.industryCategory ic ";
		} else {
			base += " fs left outer join fs.supplier s left outer join s.registrationOfCountry rc left outer join s.supplierProjects as sp left outer join fs.industryCategory ic  ";
		}
		base += " where fs.buyer.id = :tenantId and fs.status = :status ";

		// Add on search filter conditions
		for (ColumnParameter cp : input.getColumns()) {
			if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
				String prefix = "s.";
				if (cp.getData().equals("fullName")) {
					prefix = "fs.";
				}

				if (cp.getData().equals("countryName")) {
					base += " and upper(rc.countryName ) like (:" + cp.getData().replace(".", "") + ")";
				} else {
					base += " and upper(" + prefix + cp.getData() + ") like (:" + cp.getData().replace(".", "") + ")";
				}
			}
		}

		// If it is not a count query then add order by clause
		if (!isCount) {
			List<OrderParameter> orderList = input.getOrder();
			if (CollectionUtil.isNotEmpty(orderList)) {
				for (OrderParameter order : orderList) {
					String orderColumn = input.getColumns().get(order.getColumn()).getData();

					String dir = order.getDir();

					if (orderColumn.equalsIgnoreCase("companyName")) {
						base += " order by s.companyName " + dir + ",";
					} else if (orderColumn.equals("countryName")) {
						base += " order by s.registrationOfCountry.countryName " + dir + ",";
					} else if (orderColumn.equals("yearOfEstablished")) {
						base += " order by s.yearOfEstablished " + dir + ",";
					} else if (orderColumn.equals("registrationCompleteDate")) {
						base += " order by s.registrationDate " + dir + ",";

					} else if (orderColumn.equals("fullName")) {
						base += " order by s.fullName " + dir + ",";
					} else {
						base += " s." + orderColumn + " " + dir + ",";
					}
				}

				if (base.lastIndexOf(",") == base.length() - 1) {
					base = base.substring(0, base.length() - 1);
				}
			} else {
				base += " order by fs.createdDate desc";
			}
		}

		final Query query = getEntityManager().createQuery(base);
		query.setParameter("tenantId", tenantId);
		query.setParameter("status", status);
		// Apply search filter values
		for (ColumnParameter cp : input.getColumns()) {
			if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
				query.setParameter(cp.getData().replace(".", ""), "%" + cp.getSearch().getValue().toUpperCase() + "%");
			}
		}

		return query;

	}

	@SuppressWarnings("unchecked")
	@Override
	public List<SupplierPojo> getFavSupplierListBasedOnStatus(TableDataInput input, String tenantId, FavouriteSupplierStatus status) {
		Query query = constructFavSuppliersQueryBasedOnStatus(input, false, status, tenantId);
		query.setFirstResult(input.getStart());
		query.setMaxResults(input.getLength());
		return query.getResultList();

	}

	@Override
	public long getFavSuppliersCountBasedOnStatus(TableDataInput input, String tenantId, FavouriteSupplierStatus status) {
		Query query = constructFavSuppliersQueryBasedOnStatus(input, true, status, tenantId);
		return ((Number) query.getSingleResult()).longValue();
	}

	private Query constructAssociatedBuyerQueryBasedOnStatus(TableDataInput input, boolean isCount, FavouriteSupplierStatus status, String tenantId) {
		String base = "";
		// If count query is enabled, then add the select count(*) clause
		if (isCount) {
			base += "select count(distinct fs.id) ";
		} else {
			base += "select distinct NEW com.privasia.procurehere.core.pojo.RequestedAssociatedBuyerPojo(fs.id,b.id,b.companyName,rc.countryName, fs.status,fs.createdDate,fs.associatedDate) ";
		}
		base += " from FavouriteSupplier ";

		// If this is not a count query, only then add the join fetch. Count query does not require its
		if (isCount) {
			base += " fs left outer join fs.buyer b left outer join b.registrationOfCountry rc";
		} else {
			base += " fs left outer join fs.buyer b left outer join b.registrationOfCountry rc ";
		}
		base += " where fs.supplier.id = :tenantId and fs.status = :status ";

		// Add on search filter conditions
		for (ColumnParameter cp : input.getColumns()) {
			if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
				if (cp.getData().equals("buyerCompanyName")) {
					base += " and upper(b.companyName ) like (:" + cp.getData().replace(".", "") + ")";
				} else if (cp.getData().equals("countryName")) {
					base += " and upper(rc.countryName ) like (:" + cp.getData().replace(".", "") + ")";
				} else {
					base += " and upper(b." + cp.getData() + ") like (:" + cp.getData().replace(".", "") + ")";
				}

			}
		}

		// If it is not a count query then add order by clause
		if (!isCount) {
			List<OrderParameter> orderList = input.getOrder();
			if (CollectionUtil.isNotEmpty(orderList)) {
				for (OrderParameter order : orderList) {
					String orderColumn = input.getColumns().get(order.getColumn()).getData();
					String dir = order.getDir();
					if (orderColumn.equalsIgnoreCase("buyerCompanyName")) {
						base += " order by b.companyName " + dir + ",";
					} else {
						if (orderColumn.equals("countryName")) {
							base += " order by b.registrationOfCountry.countryName " + dir + ",";
						}
						if (status == FavouriteSupplierStatus.PENDING || status == FavouriteSupplierStatus.REJECTED) {
							if (orderColumn.equals("requestedDate")) {
								base += " order by fs.createdDate " + dir + ",";
							}
						}
					}

				}

				if (base.lastIndexOf(",") == base.length() - 1) {
					base = base.substring(0, base.length() - 1);
				}
			} else {
				base += " order by fs.createdDate desc";
			}
		}

		final Query query = getEntityManager().createQuery(base);
		query.setParameter("tenantId", tenantId);
		query.setParameter("status", status);
		// Apply search filter values
		for (ColumnParameter cp : input.getColumns()) {
			if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
				query.setParameter(cp.getData().replace(".", ""), "%" + cp.getSearch().getValue().toUpperCase() + "%");
			}
		}

		return query;

	}

	@SuppressWarnings("unchecked")
	@Override
	public List<RequestedAssociatedBuyerPojo> getAssociatedBuyerListBasedOnStatus(TableDataInput input, String tenantId, FavouriteSupplierStatus status) {
		Query query = constructAssociatedBuyerQueryBasedOnStatus(input, false, status, tenantId);
		query.setFirstResult(input.getStart());
		query.setMaxResults(input.getLength());
		return query.getResultList();

	}

	@Override
	public long getAssociatedBuyersCountBasedOnStatus(TableDataInput input, String tenantId, FavouriteSupplierStatus status) {
		Query query = constructAssociatedBuyerQueryBasedOnStatus(input, true, status, tenantId);
		return ((Number) query.getSingleResult()).longValue();
	}

	@Override
	public long getAssociatedBuyersCountForSupplierBasedOnStatus(String tenantId, FavouriteSupplierStatus status) {
		final Query query = getEntityManager().createQuery("select count(distinct fs.id) from FavouriteSupplier fs  where fs.supplier.id = :tenantId and fs.status = :status");
		query.setParameter("tenantId", tenantId);
		query.setParameter("status", status);
		return ((Number) query.getSingleResult()).longValue();
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<RequestedAssociatedBuyerPojo> getAvailableBuyerList(TableDataInput input, String tenantId) {
		Query query = constructFetchAssociatedBuyersFromFavSuppQuery(tenantId);
		List<String> associatedBuyerIds = query.getResultList();
		final Query query1 = constructAvailbleBuyersFromGlobalQuery(input, tenantId, associatedBuyerIds, false);
		query1.setFirstResult(input.getStart());
		query1.setMaxResults(input.getLength());
		return query1.getResultList();
	}

	private Query constructFetchAssociatedBuyersFromFavSuppQuery(String tenantId) {
		String sql = "select distinct (b.id) from FavouriteSupplier fs left outer join fs.buyer b where fs.supplier.id = :tenantId";
		final Query query = getEntityManager().createQuery(sql);
		query.setParameter("tenantId", tenantId);
		return query;
	}

	private Query constructAvailbleBuyersFromGlobalQuery(TableDataInput input, String tenantId, List<String> buyerIds, boolean isCount) {
		String hql = "";
		// If count query is enabled, then add the select count(*) clause
		if (isCount) {
			hql += "select count(distinct b.id) ";
		}

		if (!isCount) {
			hql += "select distinct NEW com.privasia.procurehere.core.pojo.RequestedAssociatedBuyerPojo(b.id,b.companyName,c.countryName, b.createdDate) ";
		}
		hql += " from Buyer b ";

		// If this is not a count query, only then add the join fetch. Count query does not require its
		if (isCount) {
			hql += " left outer join b.registrationOfCountry as c";
		} else {
			hql += " left outer join b.registrationOfCountry as c";
		}
		hql += " where 1 = 1 ";

		// Add on search filter conditions
		for (ColumnParameter cp : input.getColumns()) {
			if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
				if (cp.getData().equals("buyerCompanyName")) {
					hql += " and upper(b.companyName ) like (:" + cp.getData().replace(".", "") + ")";
				} else if (cp.getData().equals("countryName")) {
					hql += " and upper(c.countryName ) like (:" + cp.getData().replace(".", "") + ")";
				} else {
					hql += " and upper(b." + cp.getData() + ") like (:" + cp.getData().replace(".", "") + ")";
				}
			}
		}

		hql += " and b.status = :status and b.publishedProfile = :publishedProfile  ";

		if (CollectionUtil.isNotEmpty(buyerIds)) {
			if (buyerIds.size() > 900) {
				LOG.info("buyer Ids List size is greater than 900");
				List<String> opeSubList = null;
				int index;
				int increment = 900;
				int parameterIndex = 1;
				for (index = 0; index < buyerIds.size(); index += increment) {
					opeSubList = new ArrayList<String>();
					parameterIndex++;
					int endIndex = index + increment;
					if (endIndex > buyerIds.size()) {
						endIndex = buyerIds.size();
					}
					opeSubList.addAll(new ArrayList<String>(buyerIds.subList(index, endIndex)));

					hql += " and b.id not in (:buyerIds" + parameterIndex + " )";
				}
			} else {
				hql += " and b.id not in (:buyerIds)";
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
					if (orderColumn.equals("buyerCompanyName")) {
						hql += " b.companyName " + dir + ",";
					} else if (orderColumn.equals("countryName")) {
						hql += " b.registrationOfCountry.countryName " + dir + ",";
					} else {
						hql += " b." + orderColumn + " " + dir + ",";
					}
				}
				if (hql.lastIndexOf(",") == hql.length() - 1) {
					hql = hql.substring(0, hql.length() - 1);
				}

			} else {
				if (!isCount) {
					hql += " order by b.createdDate ";
				}
			}
		}
		final Query query = getEntityManager().createQuery(hql);

		// Apply search filter values
		for (ColumnParameter cp : input.getColumns()) {
			if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
				query.setParameter(cp.getData().replace(".", ""), "%" + cp.getSearch().getValue().toUpperCase() + "%");
			}
		}

		query.setParameter("status", BuyerStatus.ACTIVE);
		query.setParameter("publishedProfile", Boolean.TRUE);

		if (CollectionUtil.isNotEmpty(buyerIds)) {
			if (buyerIds.size() > 900) {
				LOG.info("buyer Ids List size is greater than 900");
				List<String> opeSubList = null;
				int index;
				int increment = 900;
				int parameterIndex = 1;
				for (index = 0; index < buyerIds.size(); index += increment) {
					parameterIndex++;
					opeSubList = new ArrayList<String>();
					int endIndex = index + increment;
					if (endIndex > buyerIds.size()) {
						endIndex = buyerIds.size();
					}
					opeSubList.addAll(new ArrayList<String>(buyerIds.subList(index, endIndex)));
					query.setParameter("buyerIds" + parameterIndex, opeSubList);
				}
			} else {
				query.setParameter("buyerIds", buyerIds);
			}
		}
		return query;

	}

	@SuppressWarnings("unchecked")
	@Override
	public long getAvailableBuyerListCount(TableDataInput input, String tenantId) {
		Query query = constructFetchAssociatedBuyersFromFavSuppQuery(tenantId);
		List<String> associatedBuyerIds = query.getResultList();
		final Query query1 = constructAvailbleBuyersFromGlobalQuery(input, tenantId, associatedBuyerIds, true);
		return ((Number) query1.getSingleResult()).longValue();

	}

	@SuppressWarnings("unchecked")
	@Override
	public long getTotalPublishedAvailableBuyerList(String loggedInUserTenantId) {
		Query query = constructFetchAssociatedBuyersFromFavSuppQuery(loggedInUserTenantId);
		List<String> associatedBuyerIds = query.getResultList();
		String hql = "select count(distinct b.id) from Buyer b where b.status =:status and b.publishedProfile = :publishedProfile ";
		if (CollectionUtil.isNotEmpty(associatedBuyerIds)) {
			hql += " and b.id not in (:buyerIds)";
		}
		final Query query1 = getEntityManager().createQuery(hql);
		query1.setParameter("status", BuyerStatus.ACTIVE);
		query1.setParameter("publishedProfile", Boolean.TRUE);
		if (CollectionUtil.isNotEmpty(associatedBuyerIds)) {
			query1.setParameter("buyerIds", associatedBuyerIds);
		}
		return ((Number) query1.getSingleResult()).longValue();
	}

	@Override
	public long isSupplierInBuyerFavList(String buyerId, String tenantId) {
		LOG.info("Buyer : " + buyerId + " Supplier : " + tenantId);
		String sql = "select count (distinct fs.id) from FavouriteSupplier fs left outer join fs.supplier s  left outer join fs.buyer b where  b.id =:buyerId and s.id = :tenantId";
		final Query query = getEntityManager().createQuery(sql);
		query.setParameter("buyerId", buyerId);
		query.setParameter("tenantId", tenantId);
		return ((Number) query.getSingleResult()).longValue();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<SupplierPojo> getTotalSuppliersFromGlobalList(TableDataInput input, String tenantId) {
		// Query localQuery = constructFetchFavSuppliersForBuyerQuery(tenantId);
		// List<String> supplierIdsList = localQuery.getResultList();
		Query globalQuery = constructFetchTotalSuppliersForBuyerQuery(input, tenantId, false, null);
		globalQuery.setFirstResult(input.getStart());
		globalQuery.setMaxResults(input.getLength());

		// List<SupplierPojo> globalList = globalQuery.getResultList();
		// if (CollectionUtil.isNotEmpty(globalList)) {
		// globalQuery.setFirstResult(input.getStart());
		// globalQuery.setMaxResults(input.getLength());
		// }
		return globalQuery.getResultList();

	}

	private Query constructFetchFavSuppliersForBuyerQuery(String tenantId) {
		String sql = "select distinct s.id from FavouriteSupplier fs left outer join fs.supplier s where fs.buyer.id = :tenantId";
		final Query query = getEntityManager().createQuery(sql);
		query.setParameter("tenantId", tenantId);
		return query;

	}

	private Query constructFetchTotalSuppliersForBuyerQuery(TableDataInput input, String tenantId, boolean isCount, List<String> supplierIdsList) {

		String hql = "";
		// If count query is enabled, then add the select count(*) clause
		if (isCount) {
			hql += "select count(distinct s.id) ";
		}

		if (!isCount) {
			hql += "select distinct new com.privasia.procurehere.core.pojo.SupplierPojo(s.id, s.companyName, c.countryName, s.yearOfEstablished, s.registrationDate, s.status, s.fullName, s.communicationEmail,s.designation, s.companyContactNumber, s.faxNumber ,s.registrationComplete) ";
		}
		hql += " from Supplier s ";

		// If this is not a count query, only then add the join fetch. Count query does not require its
		if (isCount) {
			hql += " left outer join s.registrationOfCountry as c left outer join FavouriteSupplier fs on s.id = fs.supplier.id and fs.buyer.id = :tenantId  ";
		} else {
			hql += " left outer join s.registrationOfCountry as c left outer join FavouriteSupplier fs on s.id = fs.supplier.id and fs.buyer.id = :tenantId  ";
		}
		hql += " where fs.id is null ";

		// if (CollectionUtil.isNotEmpty(supplierIdsList)) {
		// if (supplierIdsList.size() > 900) {
		// LOG.info("supplier Ids List size is greater than 900");
		// List<String> opeSubList = null;
		// int index;
		// int increment = 900;
		// int parameterIndex = 1;
		// for (index = 0; index < supplierIdsList.size(); index += increment) {
		// opeSubList = new ArrayList<String>();
		// parameterIndex++;
		// int endIndex = index + increment;
		// if (endIndex > supplierIdsList.size()) {
		// endIndex = supplierIdsList.size();
		// }
		// opeSubList.addAll(new ArrayList<String>(supplierIdsList.subList(index, endIndex)));
		//
		// hql += " and s.id not in (:supplierIds" + parameterIndex + " )";
		// }
		// } else {
		// hql += " and s.id not in (:supplierIds)";
		// }
		// }

		// Add on search filter conditions
		for (ColumnParameter cp : input.getColumns()) {
			if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
				if (cp.getData().equals("countryName")) {
					hql += " and upper(c.countryName ) like (:" + cp.getData().replace(".", "") + ")";
				} else {
					hql += " and upper(s." + cp.getData() + ") like (:" + cp.getData().replace(".", "") + ")";
				}
			}
		}
		hql += " and s.status =:status ";

		// If it is not a count query then add order by clause
		if (!isCount) {
			List<OrderParameter> orderList = input.getOrder();
			if (CollectionUtil.isNotEmpty(orderList)) {
				hql += " order by ";
				for (OrderParameter order : orderList) {
					String orderColumn = input.getColumns().get(order.getColumn()).getData();
					String dir = order.getDir();

					if (orderColumn.equalsIgnoreCase("companyName")) {
						hql += " s.companyName " + dir + ",";
					} else if (orderColumn.equals("countryName")) {
						hql += " s.registrationOfCountry.countryName " + dir + ",";
					} else if (orderColumn.equals("yearOfEstablished")) {
						hql += " s.yearOfEstablished " + dir + ",";
					} else if (orderColumn.equals("registrationCompleteDate")) {
						hql += " s.registrationDate " + dir + ",";
					} else if (orderColumn.equals("fullName")) {
						hql += " s.fullName " + dir + ",";
					} else {
						hql += " s." + orderColumn + " " + dir + ",";
					}
				}
				if (hql.lastIndexOf(",") == hql.length() - 1) {
					hql = hql.substring(0, hql.length() - 1);
				}

			} else {
				hql += " order by s.registrationDate asc";
			}
		}

		final Query query = getEntityManager().createQuery(hql);

		// Apply search filter values
		for (ColumnParameter cp : input.getColumns()) {
			if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
				query.setParameter(cp.getData().replace(".", ""), "%" + cp.getSearch().getValue().toUpperCase() + "%");
			}
		}

		query.setParameter("status", SupplierStatus.APPROVED);
		query.setParameter("tenantId", tenantId);

		// if (CollectionUtil.isNotEmpty(supplierIdsList)) {
		// if (supplierIdsList.size() > 900) {
		// LOG.info("supplier Ids List size is greater than 900");
		// List<String> opeSubList = null;
		// int index;
		// int increment = 900;
		// int parameterIndex = 1;
		// for (index = 0; index < supplierIdsList.size(); index += increment) {
		// parameterIndex++;
		// opeSubList = new ArrayList<String>();
		// int endIndex = index + increment;
		// if (endIndex > supplierIdsList.size()) {
		// endIndex = supplierIdsList.size();
		// }
		// opeSubList.addAll(new ArrayList<String>(supplierIdsList.subList(index, endIndex)));
		// query.setParameter("supplierIds" + parameterIndex, opeSubList);
		// }
		// } else {
		// query.setParameter("supplierIds", supplierIdsList);
		// }
		// }
		return query;

	}

	private Query constructFetchFavSuppliersCountForBuyerQuery(String tenantId) {
		String sql = "select count (distinct fs.id) from FavouriteSupplier fs where fs.buyer.id = :tenantId";
		final Query query = getEntityManager().createQuery(sql);
		query.setParameter("tenantId", tenantId);
		return query;

	}

	@SuppressWarnings("unchecked")
	@Override
	public long getTotalSuppliersCountFromGlobalList(TableDataInput input, String tenantId) {
		// Query localQuery = constructFetchFavSuppliersForBuyerQuery(tenantId);
		// List<String> supplierIdsList = localQuery.getResultList();
		Query globalQuery = constructFetchTotalSuppliersForBuyerQuery(input, tenantId, true, null);
		return ((Number) globalQuery.getSingleResult()).longValue();
	}

	@Override
	public long getTotalSuppliersCountForBuyerGlobalList(String tenantId) {
		Query localQuery = constructFetchFavSuppliersCountForBuyerQuery(tenantId);
		String hql = "select count(distinct s.id) from Supplier s where s.status =:status";
		final Query globalQuery = getEntityManager().createQuery(hql);
		globalQuery.setParameter("status", SupplierStatus.APPROVED);
		long globalSupplier = ((Number) globalQuery.getSingleResult()).longValue();
		long localSupplier = ((Number) localQuery.getSingleResult()).longValue();
		long totalSupplier = globalSupplier - localSupplier;
		return totalSupplier;
	}

	@Override
	public long searchBuyersCount(TableDataInput input, RequestedAssociatedBuyerPojo searchBuyerPojo, String tenantId) {
		if (StringUtils.checkString(searchBuyerPojo.getSearchCompanyName()).length() > 0 || StringUtils.checkString(searchBuyerPojo.getSearchCountryName()).length() > 0) {
			List<String> associatedFavBuyerList = constructQueryForAssociatedBuyer(tenantId);
			List<String> publishedBuyerList = constructQueryForPublishedAvailableBuyer(tenantId, associatedFavBuyerList);
			List<String> buyerIdsList = new ArrayList<String>();
			if (CollectionUtil.isNotEmpty(associatedFavBuyerList)) {
				buyerIdsList.addAll(associatedFavBuyerList);
			}
			if (CollectionUtil.isNotEmpty(publishedBuyerList)) {
				buyerIdsList.addAll(publishedBuyerList);
			}
			Query searchBuyerQuery = constructSearchBuyersFromPublishedQuery(input, tenantId, buyerIdsList, true, searchBuyerPojo);
			return ((Number) searchBuyerQuery.getSingleResult()).longValue();

		} else {
			Query query1 = constructSearchBuyerQuery(input, searchBuyerPojo, true, tenantId);
			return ((Number) query1.getSingleResult()).longValue();

		}

	}

	@SuppressWarnings("unchecked")
	@Override
	public List<RequestedAssociatedBuyerPojo> searchBuyers(TableDataInput input, RequestedAssociatedBuyerPojo searchBuyerPojo, String tenantId) {
		if (StringUtils.checkString(searchBuyerPojo.getSearchCompanyName()).length() > 0 || StringUtils.checkString(searchBuyerPojo.getSearchCountryName()).length() > 0) {
			List<String> associatedFavBuyerList = constructQueryForAssociatedBuyer(tenantId);
			List<String> publishedBuyerList = constructQueryForPublishedAvailableBuyer(tenantId, associatedFavBuyerList);
			List<String> buyerIdsList = new ArrayList<String>();
			if (CollectionUtil.isNotEmpty(associatedFavBuyerList)) {
				buyerIdsList.addAll(associatedFavBuyerList);
			}
			if (CollectionUtil.isNotEmpty(publishedBuyerList)) {
				buyerIdsList.addAll(publishedBuyerList);
			}
			Query searchBuyerQuery = constructSearchBuyersFromPublishedQuery(input, tenantId, buyerIdsList, false, searchBuyerPojo);
			searchBuyerQuery.setFirstResult(input.getStart());
			searchBuyerQuery.setMaxResults(input.getLength());
			return searchBuyerQuery.getResultList();
		} else {
			Query query1 = constructSearchBuyerQuery(input, searchBuyerPojo, false, tenantId);
			query1.setFirstResult(input.getStart());
			query1.setMaxResults(input.getLength());
			return query1.getResultList();

		}

	}

	@SuppressWarnings("unchecked")
	private List<String> constructQueryForAssociatedBuyer(String tenantId) {
		String sql = "select distinct (b.id) from FavouriteSupplier fs left outer join fs.buyer b where fs.supplier.id = :tenantId";
		final Query query = getEntityManager().createQuery(sql);
		query.setParameter("tenantId", tenantId);
		return query.getResultList();

	}

	@SuppressWarnings("unchecked")
	private List<String> constructQueryForPublishedAvailableBuyer(String tenantId, List<String> buyerIdsLst) {
		String sql = "select distinct (b.id) from Buyer b where b.publishedProfile=true and b.status = :status";
		if (CollectionUtil.isNotEmpty(buyerIdsLst)) {
			if (buyerIdsLst.size() > 900) {
				LOG.info("buyer Ids List size is greater than 900");
				List<String> opeSubList = null;
				int index;
				int increment = 900;
				int parameterIndex = 1;
				for (index = 0; index < buyerIdsLst.size(); index += increment) {
					opeSubList = new ArrayList<String>();
					parameterIndex++;
					int endIndex = index + increment;
					if (endIndex > buyerIdsLst.size()) {
						endIndex = buyerIdsLst.size();
					}
					opeSubList.addAll(new ArrayList<String>(buyerIdsLst.subList(index, endIndex)));

					sql += " and b.id not in (:buyerIds" + parameterIndex + " )";
				}
			} else {
				sql += " and b.id not in (:buyerIds)";
			}
		}
		final Query query = getEntityManager().createQuery(sql);
		if (CollectionUtil.isNotEmpty(buyerIdsLst)) {
			if (buyerIdsLst.size() > 900) {
				LOG.info("buyer Ids List size is greater than 900");
				List<String> opeSubList = null;
				int index;
				int increment = 900;
				int parameterIndex = 1;
				for (index = 0; index < buyerIdsLst.size(); index += increment) {
					parameterIndex++;
					opeSubList = new ArrayList<String>();
					int endIndex = index + increment;
					if (endIndex > buyerIdsLst.size()) {
						endIndex = buyerIdsLst.size();
					}
					opeSubList.addAll(new ArrayList<String>(buyerIdsLst.subList(index, endIndex)));
					query.setParameter("buyerIds" + parameterIndex, opeSubList);
				}
			} else {
				query.setParameter("buyerIds", buyerIdsLst);
			}
		}
		query.setParameter("status", BuyerStatus.ACTIVE);
		return query.getResultList();

	}

	private Query constructSearchBuyersFromPublishedQuery(TableDataInput input, String tenantId, List<String> buyerIdsLst, boolean isCount, RequestedAssociatedBuyerPojo searchParams) {
		String hql = "";
		// If count query is enabled, then add the select count(*) clause
		if (isCount) {
			hql += "select count(distinct b.id) ";
		}

		if (!isCount) {
			hql += "select distinct NEW com.privasia.procurehere.core.pojo.RequestedAssociatedBuyerPojo(b.id,b.companyName,c.countryName) ";
		}
		hql += " from Buyer b ";

		// If this is not a count query, only then add the join fetch. Count query does not require its
		if (isCount) {
			hql += " left outer join b.registrationOfCountry as c";
		} else {
			hql += " left outer join b.registrationOfCountry as c";
		}
		hql += " where 1 = 1 ";

		// Add on search filter conditions
		for (ColumnParameter cp : input.getColumns()) {
			if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
				if (cp.getData().equals("buyerCompanyName")) {
					hql += " and upper(b.companyName ) like (:" + cp.getData().replace(".", "") + ")";
				} else if (cp.getData().equals("countryName")) {
					hql += " and upper(c.countryName ) like (:" + cp.getData().replace(".", "") + ")";
				} else {
					hql += " and upper(b." + cp.getData() + ") like (:" + cp.getData().replace(".", "") + ")";
				}
			}
		}

		if (StringUtils.checkString(searchParams.getSearchCompanyName()).length() > 0 || StringUtils.checkString(searchParams.getSearchCountryName()).length() > 0) {
			if (StringUtils.checkString(searchParams.getSearchCompanyName()).length() > 0 && StringUtils.checkString(searchParams.getSearchCountryName()).length() > 0) {
				hql += " and( upper(b.companyName) like  :companyName ";
				hql += " and upper(b.registrationOfCountry.countryName) =  :countryName ";
			} else {
				hql += " and ( 1 = 0 ";
				// Append
				if (StringUtils.checkString(searchParams.getSearchCompanyName()).length() > 0) {
					hql += " or upper(b.companyName) like  :companyName";
				}
				if (StringUtils.checkString(searchParams.getSearchCountryName()).length() > 0) {
					hql += " or upper(b.registrationOfCountry.countryName) = :countryName";
				}
			}
			hql += " ) ";
		}

		if (CollectionUtil.isNotEmpty(buyerIdsLst)) {
			if (buyerIdsLst.size() > 900) {
				LOG.info("buyer Ids List size is greater than 900");
				List<String> opeSubList = null;
				int index;
				int increment = 900;
				int parameterIndex = 1;
				for (index = 0; index < buyerIdsLst.size(); index += increment) {
					opeSubList = new ArrayList<String>();
					parameterIndex++;
					int endIndex = index + increment;
					if (endIndex > buyerIdsLst.size()) {
						endIndex = buyerIdsLst.size();
					}
					opeSubList.addAll(new ArrayList<String>(buyerIdsLst.subList(index, endIndex)));

					hql += " and b.id in (:buyerIds" + parameterIndex + " )";
				}
			} else {
				hql += " and b.id in (:buyerIds)";
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
					if (orderColumn.equals("buyerCompanyName")) {
						hql += " b.companyName " + dir + ",";
					} else if (orderColumn.equals("countryName")) {
						hql += " b.registrationOfCountry.countryName " + dir + ",";
					} else {
						hql += " b." + orderColumn + " " + dir + ",";
					}
				}
				if (hql.lastIndexOf(",") == hql.length() - 1) {
					hql = hql.substring(0, hql.length() - 1);
				}

			}
		}
		final Query query = getEntityManager().createQuery(hql);

		// Apply search filter values
		for (ColumnParameter cp : input.getColumns()) {
			if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
				query.setParameter(cp.getData().replace(".", ""), "%" + cp.getSearch().getValue().toUpperCase() + "%");
			}
		}

		if (StringUtils.checkString(searchParams.getSearchCompanyName()).length() > 0) {
			query.setParameter("companyName", "%" + searchParams.getSearchCompanyName().toUpperCase() + "%");
		}
		if (StringUtils.checkString(searchParams.getSearchCountryName()).length() > 0) {
			query.setParameter("countryName", searchParams.getSearchCountryName().toUpperCase());
		}
		if (CollectionUtil.isNotEmpty(buyerIdsLst)) {
			if (buyerIdsLst.size() > 900) {
				LOG.info("buyer Ids List size is greater than 900");
				List<String> opeSubList = null;
				int index;
				int increment = 900;
				int parameterIndex = 1;
				for (index = 0; index < buyerIdsLst.size(); index += increment) {
					parameterIndex++;
					opeSubList = new ArrayList<String>();
					int endIndex = index + increment;
					if (endIndex > buyerIdsLst.size()) {
						endIndex = buyerIdsLst.size();
					}
					opeSubList.addAll(new ArrayList<String>(buyerIdsLst.subList(index, endIndex)));
					query.setParameter("buyerIds" + parameterIndex, opeSubList);
				}
			} else {
				query.setParameter("buyerIds", buyerIdsLst);
			}
		}
		return query;

	}

	private Query constructSearchBuyerQuery(TableDataInput input, RequestedAssociatedBuyerPojo searchParams, boolean isCount, String tenantId) {

		String base = "";
		// If count query is enabled, then add the select count(*) clause
		if (isCount) {
			base += "select count(distinct fs.id) ";
		} else {
			base += "select distinct NEW com.privasia.procurehere.core.pojo.RequestedAssociatedBuyerPojo(fs.id,b.id,b.companyName,rc.countryName, fs.status,fs.createdDate,fs.associatedDate) ";
		}

		base += " from FavouriteSupplier ";

		// If this is not a count query, only then add the join fetch. Count query does not require its
		if (isCount) {
			base += " fs left outer join fs.buyer b left outer join b.registrationOfCountry rc";
		} else {
			base += " fs left outer join fs.buyer b left outer join b.registrationOfCountry rc";
		}
		base += " where fs.supplier.id = :tenantId  ";

		boolean isStatusFilterOn = false;

		// Add on search filter conditions
		for (ColumnParameter cp : input.getColumns()) {
			if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
				if (cp.getData().equals("buyerCompanyName")) {
					base += " and upper(b.companyName) like (:" + cp.getData().replace(".", "") + ")";
				}
				if (cp.getData().equals("favStatus")) {
					isStatusFilterOn = true;
					base += " and fs.status = (:" + cp.getData() + ")";
				} else if (cp.getData().equals("countryName")) {
					base += " and upper(rc.countryName ) like (:" + cp.getData().replace(".", "") + ")";
				}
			}
		}

		if (!isStatusFilterOn) {
			base += " and fs.status in (:status) ";
		}

		// If it is not a count query then add order by clause
		if (!isCount) {
			List<OrderParameter> orderList = input.getOrder();
			if (CollectionUtil.isNotEmpty(orderList)) {
				for (OrderParameter order : orderList) {
					String orderColumn = input.getColumns().get(order.getColumn()).getData();
					String dir = order.getDir();
					if (orderColumn.equalsIgnoreCase("buyerCompanyName")) {
						base += " order by b.companyName " + dir + ",";
					} else {
						if (orderColumn.equals("countryName")) {
							base += " order by b.registrationOfCountry.countryName " + dir + ",";
						}
					}
				}

				if (base.lastIndexOf(",") == base.length() - 1) {
					base = base.substring(0, base.length() - 1);
				}
			} else {
				base += " order by fs.status asc,fs.createdDate desc";
			}
		}

		final Query query = getEntityManager().createQuery(base.toString());
		query.setParameter("tenantId", tenantId);
		// Apply search filter values
		for (ColumnParameter cp : input.getColumns()) {
			if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
				if (cp.getData().equals("favStatus")) {
					LOG.info("Status : " + cp.getSearch().getValue());
					query.setParameter("status", FavouriteSupplierStatus.valueOf(cp.getSearch().getValue()));
				} else {
					query.setParameter(cp.getData().replace(".", ""), "%" + cp.getSearch().getValue().toUpperCase() + "%");
				}
			}
		}

		if (!isStatusFilterOn && (StringUtils.checkString(searchParams.getSearchCompanyName()).length() == 0 && StringUtils.checkString(searchParams.getSearchCountryName()).length() == 0)) {
			query.setParameter("status", Arrays.asList(FavouriteSupplierStatus.ACTIVE, FavouriteSupplierStatus.PENDING, FavouriteSupplierStatus.BLACKLISTED, FavouriteSupplierStatus.SUSPENDED, FavouriteSupplierStatus.INACTIVE));

		}
		return query;
	}

	@SuppressWarnings("unchecked")
	@Override
	public FavouriteSupplier getFavSupplierDetailsByBuyerAndSupplierId(String supplierId, String buyerId) {
		String sql = "select distinct new com.privasia.procurehere.core.entity.FavouriteSupplier(fs.id,fs.status,fs.createdDate,fs.associatedDate) ";
		sql += " from FavouriteSupplier fs where fs.buyer.id = :buyerId and fs.supplier.id = :supplierId";
		final Query query = getEntityManager().createQuery(sql);
		query.setParameter("buyerId", buyerId);
		query.setParameter("supplierId", supplierId);
		List<FavouriteSupplier> list = query.getResultList();
		if (CollectionUtil.isNotEmpty(list)) {
			return list.get(0);
		} else {
			return null;
		}

	}

	@SuppressWarnings("unchecked")
	@Override
	public List<FavouriteSupplier> getAllFavouriteSupplierByIdAndStatus(String[] favArr, String loggedInUserTenantId, boolean select_all, SearchFilterSupplierPojo searchFilterSupplierPojo, FavouriteSupplierStatus status) {

		String hql = "select distinct (fs) from FavouriteSupplier fs inner join fetch fs.supplier as s left outer join fetch s.registrationOfCountry c left outer join fetch fs.industryCategory ic where fs.buyer.id =:tenantId";

		if (!(select_all)) {
			if (favArr != null && favArr.length > 0) {
				hql += " and s.id in (:id)";
			}
		}
		hql += " and fs.status =:status";

		if (searchFilterSupplierPojo != null) {
			if (StringUtils.checkString(searchFilterSupplierPojo.getCompanyname()).length() > 0) {
				hql += " and upper(s.companyName) like :companyname";
			}
			if (StringUtils.checkString(searchFilterSupplierPojo.getCountry()).length() > 0) {
				hql += " and upper(c.countryName) like :country";
			}
			if (StringUtils.checkString(searchFilterSupplierPojo.getPersonincharge()).length() > 0) {
				hql += " and upper(s.fullName) like :personincharge";
			}

			if (searchFilterSupplierPojo.getIsRegistered() != null && Boolean.TRUE == searchFilterSupplierPojo.getIsRegistered()) {
				hql += " and s.registrationComplete =:registered";
			}

		}

		final Query query = getEntityManager().createQuery(hql);

		if (!(select_all)) {
			if (favArr != null && favArr.length > 0) {
				query.setParameter("id", Arrays.asList(favArr));
			}
		}
		if (searchFilterSupplierPojo != null) {
			if (StringUtils.checkString(searchFilterSupplierPojo.getCompanyname()).length() > 0) {
				query.setParameter("companyname", "%" + searchFilterSupplierPojo.getCompanyname().toUpperCase() + "%");
			}
			if (StringUtils.checkString(searchFilterSupplierPojo.getCountry()).length() > 0) {
				query.setParameter("country", "%" + searchFilterSupplierPojo.getCountry().toUpperCase() + "%");
			}
			if (StringUtils.checkString(searchFilterSupplierPojo.getPersonincharge()).length() > 0) {
				query.setParameter("personincharge", "%" + searchFilterSupplierPojo.getPersonincharge().toUpperCase() + "%");
			}

			if (searchFilterSupplierPojo.getIsRegistered() != null && Boolean.TRUE == searchFilterSupplierPojo.getIsRegistered()) {
				query.setParameter("registered", Boolean.FALSE);

			}
		}
		if (status != null) {
			query.setParameter("status", status);
		}
		query.setParameter("tenantId", loggedInUserTenantId);
		return query.getResultList();

	}

	@Override
	public long getAssociatedBuyersCountForSupplier(String tenantId) {
		final Query query = getEntityManager().createQuery("select count(distinct fs.id) from FavouriteSupplier fs  where fs.supplier.id = :tenantId and fs.status in(:status)");
		query.setParameter("tenantId", tenantId);
		query.setParameter("status", Arrays.asList(FavouriteSupplierStatus.ACTIVE, FavouriteSupplierStatus.INACTIVE, FavouriteSupplierStatus.BLACKLISTED, FavouriteSupplierStatus.SUSPENDED));
		return ((Number) query.getSingleResult()).longValue();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<RequestedAssociatedBuyerPojo> getAssociatedBuyerListForSupplier(TableDataInput input, String tenantId) {
		Query query = constructAssociatedBuyerQueryForSupplier(input, false, tenantId);
		query.setFirstResult(input.getStart());
		query.setMaxResults(input.getLength());
		return query.getResultList();
	}

	@Override
	public long getAssociatedBuyerCountForSupplier(TableDataInput input, String tenantId) {
		Query query = constructAssociatedBuyerQueryForSupplier(input, true, tenantId);
		return ((Number) query.getSingleResult()).longValue();
	}

	private Query constructAssociatedBuyerQueryForSupplier(TableDataInput input, boolean isCount, String tenantId) {
		String base = "";
		// If count query is enabled, then add the select count(*) clause
		if (isCount) {
			base += "select count(distinct fs.id) ";
		} else {
			base += "select distinct NEW com.privasia.procurehere.core.pojo.RequestedAssociatedBuyerPojo(fs.id,b.id,b.companyName,rc.countryName, fs.status,fs.createdDate,fs.associatedDate) ";
		}
		base += " from FavouriteSupplier ";

		// If this is not a count query, only then add the join fetch. Count query does not require its
		if (isCount) {
			base += " fs left outer join fs.buyer b left outer join b.registrationOfCountry rc";
		} else {
			base += " fs left outer join fs.buyer b left outer join b.registrationOfCountry rc ";
		}
		base += " where fs.supplier.id = :tenantId and fs.status in(:status) ";

		// Add on search filter conditions
		for (ColumnParameter cp : input.getColumns()) {
			if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
				if (cp.getData().equals("buyerCompanyName")) {
					base += " and upper(b.companyName ) like (:" + cp.getData().replace(".", "") + ")";
				} else if (cp.getData().equals("countryName")) {
					base += " and upper(rc.countryName ) like (:" + cp.getData().replace(".", "") + ")";
				} else {
					base += " and upper(b." + cp.getData() + ") like (:" + cp.getData().replace(".", "") + ")";
				}

			}
		}

		// If it is not a count query then add order by clause
		if (!isCount) {
			List<OrderParameter> orderList = input.getOrder();
			if (CollectionUtil.isNotEmpty(orderList)) {
				for (OrderParameter order : orderList) {
					String orderColumn = input.getColumns().get(order.getColumn()).getData();
					String dir = order.getDir();
					if (orderColumn.equalsIgnoreCase("buyerCompanyName")) {
						base += " order by b.companyName " + dir + ",";
					} else {
						if (orderColumn.equals("countryName")) {
							base += " order by b.registrationOfCountry.countryName " + dir + ",";
						}
					}

				}

				if (base.lastIndexOf(",") == base.length() - 1) {
					base = base.substring(0, base.length() - 1);
				}
			} else {
				base += " order by fs.createdDate desc";
			}
		}

		final Query query = getEntityManager().createQuery(base);
		query.setParameter("tenantId", tenantId);
		query.setParameter("status", Arrays.asList(FavouriteSupplierStatus.ACTIVE, FavouriteSupplierStatus.INACTIVE, FavouriteSupplierStatus.BLACKLISTED, FavouriteSupplierStatus.SUSPENDED));
		// Apply search filter values
		for (ColumnParameter cp : input.getColumns()) {
			if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
				query.setParameter(cp.getData().replace(".", ""), "%" + cp.getSearch().getValue().toUpperCase() + "%");
			}
		}

		return query;

	}

	@Override
	@SuppressWarnings("unchecked")
	public FavouriteSupplier getFavouritSupplier(SupplierSearchPojo searchParams, String buyerId) {
		LOG.info("Search Params : " + searchParams.toString());
		StringBuffer sb = new StringBuffer("select distinct fs from FavouriteSupplier fs inner join fetch fs.supplier as s inner join fs.buyer b where b.id =:id ");
		// Append

		if (StringUtils.checkString(searchParams.getCompanyName()).length() > 0) {
			sb.append(" and upper(s.companyName) = :companyName");
		}

		if (StringUtils.checkString(searchParams.getCompanyRegistrationNumber()).length() > 0) {
			sb.append(" and upper(s.companyRegistrationNumber) = :companyRegistrationNumber");
		}
		final Query query = getEntityManager().createQuery(sb.toString());

		// set
		if (StringUtils.checkString(searchParams.getCompanyName()).length() > 0) {
			query.setParameter("companyName", StringUtils.checkString(searchParams.getCompanyName()).toUpperCase());
		}
		if (StringUtils.checkString(searchParams.getCompanyRegistrationNumber()).length() > 0) {
			query.setParameter("companyRegistrationNumber", StringUtils.checkString(searchParams.getCompanyRegistrationNumber()).toUpperCase());
		}
		query.setParameter("id", buyerId);

		List<FavouriteSupplier> list = query.getResultList();
		if (CollectionUtil.isNotEmpty(list)) {
			return list.get(0);
		} else {
			return null;
		}

	}

	@SuppressWarnings("unchecked")
	@Override
	public List<SupplierPojo> searchFavouriteSupplier(String tenantId, String search, String id) {
		StringBuffer hql = new StringBuffer("select distinct new com.privasia.procurehere.core.pojo.SupplierPojo(s.id,sup.companyName) from FavouriteSupplier s left outer join s.supplier sup where s.buyer.id = :tenantId and s.status = :status and s.supplier.id = sup.id");
		if (StringUtils.checkString(search).length() > 0) {
			hql.append(" and upper(sup.companyName) like (:search)");
		}
		if (StringUtils.checkString(id).length() > 0) {
			hql.append(" and sup.registrationComplete = 1");
		}
		hql.append(" order by sup.companyName");
		final Query query = getEntityManager().createQuery(hql.toString());
		query.setParameter("tenantId", tenantId);
		query.setParameter("status", FavouriteSupplierStatus.ACTIVE);
		if (StringUtils.checkString(search).length() > 0) {
			query.setParameter("search", "%" + search.toUpperCase() + "%");
		}
		query.setMaxResults(10);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Supplier> searchBuyerSuppliers(String tenantId, String search, String id) {
		StringBuffer hql = new StringBuffer("select distinct new com.privasia.procurehere.core.entity.Supplier(sup.id,sup.companyName) from FavouriteSupplier s join s.supplier sup where s.buyer.id = :tenantId and s.status = :status and s.supplier.id = sup.id");
		if (StringUtils.checkString(search).length() > 0) {
			hql.append(" and upper(sup.companyName) like (:search)");
		}
		if (StringUtils.checkString(id).length() > 0) {
			hql.append(" and sup.registrationComplete = 1");
		}
		hql.append(" order by sup.companyName");
		final Query query = getEntityManager().createQuery(hql.toString());
		query.setParameter("tenantId", tenantId);
		query.setParameter("status", FavouriteSupplierStatus.ACTIVE);
		if (StringUtils.checkString(search).length() > 0) {
			query.setParameter("search", "%" + search.toUpperCase() + "%");
		}
		query.setMaxResults(10);
		return query.getResultList();
	}

	@Override
	public long countConstructQueryToFetchFavouriteSupplier(String tenantId, String id) {
		StringBuffer hql = new StringBuffer("select count(s) from FavouriteSupplier s left outer join s.supplier sup where s.buyer.id = :tenantId and s.status = :status ");
		if (StringUtils.checkString(id).length() > 0) {
			hql.append(" and sup.registrationComplete = 1");
		}
		final Query query = getEntityManager().createQuery(hql.toString());
		query.setParameter("tenantId", tenantId);
		query.setParameter("status", FavouriteSupplierStatus.ACTIVE);
		return ((Number) query.getSingleResult()).intValue();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<FavouriteSupplier> getAllActiveFavouriteSupplierBySuppIds(String[] supplierIds) {
		final Query query = getEntityManager().createQuery("select distinct fs from FavouriteSupplier fs inner join fetch fs.supplier as s where fs.id in (:supplierIds)");
		query.setParameter("supplierIds", Arrays.asList(supplierIds));
		List<FavouriteSupplier> list = query.getResultList();
		return list;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<FavouriteSupplier> getAllActiveFavouriteSupplierByBuyerId(String tenantId) {
		final Query query = getEntityManager().createQuery("select distinct fs from FavouriteSupplier fs inner join fetch fs.supplier as s where fs.buyer.id =:tenantId and fs.status = :status");
		query.setParameter("tenantId", tenantId);
		query.setParameter("status", FavouriteSupplierStatus.ACTIVE);
		List<FavouriteSupplier> list = query.getResultList();
		return list;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<SupplierPojo> getAllSearchFavouriteSupplierByBuyerId(String loggedInUserTenantId, String searchValue) {
		StringBuffer hql = new StringBuffer("select distinct new com.privasia.procurehere.core.pojo.SupplierPojo(fs.id,s.companyName) from FavouriteSupplier fs inner join fetch fs.supplier as s where fs.buyer.id =:tenantId and fs.status = :status");
		if (StringUtils.checkString(searchValue).length() > 0) {
			hql.append(" and upper(fs.supplier.companyName) like (:searchValue) ");
		}
		final Query query = getEntityManager().createQuery(hql.toString());
		query.setParameter("tenantId", loggedInUserTenantId);
		query.setParameter("status", FavouriteSupplierStatus.ACTIVE);
		if (StringUtils.checkString(searchValue).length() > 0) {
			query.setParameter("searchValue", "%" + StringUtils.checkString(searchValue).toUpperCase() + "%");
		}
		query.setMaxResults(10);
		return query.getResultList();

	}

	@Override
	public void deleteIndustryCatAndProductCat(String id) {
		StringBuilder hsql = new StringBuilder("delete from PROC_FAV_SUPP_IND_CAT  where FAV_SUPP_ID = '" + id + "'");
		Query query = getEntityManager().createNativeQuery(hsql.toString());
		query.executeUpdate();

		StringBuilder hsql1 = new StringBuilder("delete from PROC_FAV_SUPP_PRD_CAT  where FAV_SUPP_ID = '" + id + "'");
		Query query1 = getEntityManager().createNativeQuery(hsql1.toString());
		query1.executeUpdate();

		StringBuilder hsql2 = new StringBuilder("delete from PROC_FAV_SUPPLIER_TAGS  where FAV_SUPP_ID = '" + id + "'");
		Query query2 = getEntityManager().createNativeQuery(hsql2.toString());
		query2.executeUpdate();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Buyer> findFavSupplierBySuppId(String supplierId) {
		LOG.info("Supplier Id = " + supplierId);
		final Query query = getEntityManager().createQuery("select b from FavouriteSupplier as fs inner join fs.supplier s inner join fs.buyer as b where s.id = :id ");
		query.setParameter("id", supplierId);
		List<Buyer> favList = query.getResultList();
		if (CollectionUtil.isNotEmpty(favList)) {
			return favList;
		} else {
			return null;
		}
	}

	@Override
	public long getTotalSupplierCountForList(String tenantId, String status) {
		String hql = "";
		hql += "select count(distinct fs.id) ";
		hql += "from FavouriteSupplier fs ";
		hql += "left outer join fs.supplier s left outer join s.registrationOfCountry rc left outer join s.supplierProjects as sp left outer join fs.industryCategory ic ";
		hql += "where fs.buyer.id = :tenantId ";

		if (StringUtils.checkString(status).length() > 0) {
			hql += " and fs.status =:status ";
		}

		LOG.info("HQL " + hql);
		final Query query = getEntityManager().createQuery(hql.toString());
		query.setParameter("tenantId", tenantId);

		if (StringUtils.checkString(status).length() > 0) {
			query.setParameter("status", FavouriteSupplierStatus.valueOf(status));
		}
		return ((Number) query.getSingleResult()).intValue();

	}

	@SuppressWarnings("unchecked")
	@Override
	public List<FavouriteSupplier> findAllActiveSupplierForTenantIdForCsv(String tenantId, int pageSize, int pageNo, String[] favArr, SearchFilterSupplierPojo searchFilterSupplierPojo, boolean select_all) {

		String hql = "";
		hql += "select distinct (fs) from FavouriteSupplier fs inner join fetch fs.supplier as s left outer join fetch s.registrationOfCountry c left outer join fetch fs.industryCategory ic where fs.buyer.id =:tenantId";

		if (!(select_all)) {
			if (favArr != null && favArr.length > 0) {
				hql += " and s.id in (:id)";
			}
		}

		List<FavouriteSupplierStatus> list = new ArrayList<FavouriteSupplierStatus>();
		if (searchFilterSupplierPojo != null) {
			if (StringUtils.checkString(searchFilterSupplierPojo.getCompanyname()).length() > 0) {
				hql += " and upper(s.companyName) like :companyname";
			}
			if (StringUtils.checkString(searchFilterSupplierPojo.getCountry()).length() > 0) {
				hql += " and upper(c.countryName) like :country";
			}
			if (StringUtils.checkString(searchFilterSupplierPojo.getPersonincharge()).length() > 0) {
				hql += " and upper(s.fullName) like :personincharge";
			}

			if (searchFilterSupplierPojo.getIsRegistered() != null && Boolean.TRUE == searchFilterSupplierPojo.getIsRegistered()) {
				hql += " and s.registrationComplete =:registered";
			}

			if (StringUtils.checkString(searchFilterSupplierPojo.getStatus()).length() > 0) {
				hql += " and upper(fs.status) like :status";
				String[] types = searchFilterSupplierPojo.getStatus().split(",");
				if (types != null && types.length > 0) {
					for (String ty : types) {
						list.add(FavouriteSupplierStatus.valueOf(ty));
					}
				}
			}

		}

		LOG.info("Hql >>>>>>>>>>>>" + hql);
		final Query query = getEntityManager().createQuery(hql.toString());

		if (!(select_all)) {
			if (favArr != null && favArr.length > 0) {
				query.setParameter("id", Arrays.asList(favArr));
			}
		}

		if (searchFilterSupplierPojo != null) {
			if (StringUtils.checkString(searchFilterSupplierPojo.getCompanyname()).length() > 0) {
				query.setParameter("companyname", "%" + searchFilterSupplierPojo.getCompanyname().toUpperCase() + "%");
			}
			if (StringUtils.checkString(searchFilterSupplierPojo.getCountry()).length() > 0) {
				query.setParameter("country", "%" + searchFilterSupplierPojo.getCountry().toUpperCase() + "%");
			}
			if (StringUtils.checkString(searchFilterSupplierPojo.getPersonincharge()).length() > 0) {
				query.setParameter("personincharge", "%" + searchFilterSupplierPojo.getPersonincharge().toUpperCase() + "%");
			}
			if (StringUtils.checkString(searchFilterSupplierPojo.getStatus()).length() > 0) {
				query.setParameter("status", list);
			}
			if (searchFilterSupplierPojo.getIsRegistered() != null && Boolean.TRUE == searchFilterSupplierPojo.getIsRegistered()) {
				query.setParameter("registered", Boolean.FALSE);

			}
		}
		query.setParameter("tenantId", tenantId);
		query.setFirstResult((pageSize * pageNo));
		query.setMaxResults(pageSize);
		return query.getResultList();

	}

	@Override
	public long getTotalSupplierCountByIdAndStatusForList(String tenantId, FavouriteSupplierStatus favStatus) {
		String hql = "";
		hql += "select count(distinct fs.id) ";
		hql += "from FavouriteSupplier fs ";
		hql += "left outer join fs.supplier s left outer join s.registrationOfCountry rc left outer join s.supplierProjects as sp left outer join fs.industryCategory ic ";
		hql += "where fs.buyer.id = :tenantId ";
		hql += " and fs.status =:status ";

		LOG.info("HQL " + hql);
		final Query query = getEntityManager().createQuery(hql.toString());
		query.setParameter("tenantId", tenantId);

		query.setParameter("status", favStatus);
		return ((Number) query.getSingleResult()).intValue();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<FavouriteSupplier> findAllActiveSupplierByTenantIdAndStatusForCsv(int pageSize, int pageNo, String[] favArr, SearchFilterSupplierPojo searchFilterSupplierPojo, boolean select_all, FavouriteSupplierStatus favStatus, String tenantId) {
		String hql = "select distinct (fs) from FavouriteSupplier fs inner join fetch fs.supplier as s left outer join fetch s.registrationOfCountry c left outer join fetch fs.industryCategory ic where fs.buyer.id =:tenantId";

		if (!(select_all)) {
			if (favArr != null && favArr.length > 0) {
				hql += " and s.id in (:id)";
			}
		}
		hql += " and fs.status =:status";

		if (searchFilterSupplierPojo != null) {
			if (StringUtils.checkString(searchFilterSupplierPojo.getCompanyname()).length() > 0) {
				hql += " and upper(s.companyName) like :companyname";
			}
			if (StringUtils.checkString(searchFilterSupplierPojo.getCountry()).length() > 0) {
				hql += " and upper(c.countryName) like :country";
			}
			if (StringUtils.checkString(searchFilterSupplierPojo.getPersonincharge()).length() > 0) {
				hql += " and upper(s.fullName) like :personincharge";
			}

			if (searchFilterSupplierPojo.getIsRegistered() != null && Boolean.TRUE == searchFilterSupplierPojo.getIsRegistered()) {
				hql += " and s.registrationComplete =:registered";
			}

		}

		LOG.info("HQL " + hql);
		final Query query = getEntityManager().createQuery(hql.toString());

		if (!(select_all)) {
			if (favArr != null && favArr.length > 0) {
				query.setParameter("id", Arrays.asList(favArr));
			}
		}
		if (searchFilterSupplierPojo != null) {
			if (StringUtils.checkString(searchFilterSupplierPojo.getCompanyname()).length() > 0) {
				query.setParameter("companyname", "%" + searchFilterSupplierPojo.getCompanyname().toUpperCase() + "%");
			}
			if (StringUtils.checkString(searchFilterSupplierPojo.getCountry()).length() > 0) {
				query.setParameter("country", "%" + searchFilterSupplierPojo.getCountry().toUpperCase() + "%");
			}
			if (StringUtils.checkString(searchFilterSupplierPojo.getPersonincharge()).length() > 0) {
				query.setParameter("personincharge", "%" + searchFilterSupplierPojo.getPersonincharge().toUpperCase() + "%");
			}

			if (searchFilterSupplierPojo.getIsRegistered() != null && Boolean.TRUE == searchFilterSupplierPojo.getIsRegistered()) {
				query.setParameter("registered", Boolean.FALSE);

			}
		}
		if (favStatus != null) {
			query.setParameter("status", favStatus);
		}
		query.setParameter("tenantId", tenantId);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public String getSupplierNameByVendorCode(String vendorCode, String buyerId) {
		StringBuffer sb = new StringBuffer("select s.companyName from FavouriteSupplier fs left outer join fs.supplier s where fs.vendorCode = :vendorCode and fs.buyer.id = :buyerId");
		try {
			final Query query = getEntityManager().createQuery(sb.toString());
			query.setParameter("vendorCode", vendorCode);
			query.setParameter("buyerId", buyerId);
			List<String> favList = query.getResultList();
			if (CollectionUtil.isNotEmpty(favList)) {
				return favList.get(0);
			} else {
				return null;
			}
		} catch (NoResultException nr) {
			LOG.info("Error while getting  Supplier Company Name based on vendor code : " + nr.getMessage(), nr);
			return null;
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public FavouriteSupplier getSupplierDetailsBySupplierId(String supplierId, String tenantId) {
		String hql = "select distinct (fs) from FavouriteSupplier fs left outer join fs.supplier as s left outer join fs.supplierTags st where fs.buyer.id =:tenantId and s.id = :supplierId order by fs.createdDate desc";
		final Query query = getEntityManager().createQuery(hql.toString());
//		query.setParameter("status", FavouriteSupplierStatus.ACTIVE);
		query.setParameter("tenantId", tenantId);
		query.setParameter("supplierId", supplierId);
		return query.getResultList() != null ? (FavouriteSupplier) query.getResultList().get(0) : null;
	}


	@Override
	public FavouriteSupplier getFavouriteSupplierBySupplierIdAndBuyerId(String supplierId, String buyerId) {
		String hql = "select distinct fs from  FavouriteSupplier fs where fs.supplier.id=:supplierId and fs.buyer.id =:buyerId";
		Query query = getEntityManager().createQuery(hql);
		query.setParameter("supplierId", supplierId);
		query.setParameter("buyerId", buyerId);
		try {
			return (FavouriteSupplier) query.getResultList().get(0);
		} catch (Exception e) {
			LOG.error(e.getMessage() + " " + e);
			return null;
		}
	}


}