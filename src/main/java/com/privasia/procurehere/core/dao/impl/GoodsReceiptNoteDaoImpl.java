package com.privasia.procurehere.core.dao.impl;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.persistence.Query;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Repository;

import com.privasia.procurehere.core.dao.GoodsReceiptNoteDao;
import com.privasia.procurehere.core.entity.GoodsReceiptNote;
import com.privasia.procurehere.core.entity.User;
import com.privasia.procurehere.core.enums.EventStatus;
import com.privasia.procurehere.core.enums.GrnStatus;
import com.privasia.procurehere.core.pojo.ColumnParameter;
import com.privasia.procurehere.core.pojo.GoodsReceiptNotePojo;
import com.privasia.procurehere.core.pojo.GoodsReceiptNoteSearchPojo;
import com.privasia.procurehere.core.pojo.OrderParameter;
import com.privasia.procurehere.core.pojo.TableDataInput;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.StringUtils;

@Repository
public class GoodsReceiptNoteDaoImpl extends GenericDaoImpl<GoodsReceiptNote, String> implements GoodsReceiptNoteDao {

	protected static final Logger LOG = LogManager.getLogger(GoodsReceiptNoteDaoImpl.class);

	@SuppressWarnings("unchecked")
	@Override
	public List<GoodsReceiptNotePojo> findAllSearchFilterGrns(String tenantId, TableDataInput input, Date startDate, Date endDate) {
		final Query query = constructGrnSearchFilterForTenantQuery(tenantId, input, false, startDate, endDate);
		query.setFirstResult(input.getStart());
		query.setMaxResults(input.getLength());
		return query.getResultList();
	}

	@Override
	public long findTotalSearchFilterGrns(String tenantId, TableDataInput input, Date startDate, Date endDate) {
		final Query query = constructGrnSearchFilterForTenantQuery(tenantId, input, true, startDate, endDate);
		return ((Number) query.getSingleResult()).longValue();
	}

	private Query constructGrnSearchFilterForTenantQuery(String tenantId, TableDataInput tableParams, boolean isCount, Date startDate, Date endDate) {

		String hql = "";

		// If count query is enabled, then add the select count(*) clause
		if (isCount) {
			hql += "select count(distinct g.id) ";
		} else {
			hql += "select distinct new com.privasia.procurehere.core.pojo.GoodsReceiptNotePojo(g.id, g.grnId, g.grnTitle, c.currencyCode, g.decimal, bu.unitName, s.companyName, g.supplierName, cb.name, g.createdDate, g.grandTotal, g.status, g.referenceNumber, g.grnReceivedDate, g.goodsReceiptDate,p.poNumber) ";
		}

		hql += " from GoodsReceiptNote g ";

		hql += " left outer join g.createdBy as cb";

		hql += " left outer join g.businessUnit as bu";

		hql += " left outer join g.currency as c";

		hql += " left outer join g.buyer b";

		hql += " left outer join g.supplier s";

		hql += " left outer join g.po p";

		hql += " where g.buyer.id = :tenantId ";

		Boolean selectStatusFilter = false;
		// Add on search filter conditions
		for (ColumnParameter cp : tableParams.getColumns()) {
			if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
				if (cp.getData().equals("status")) {
					selectStatusFilter = true;
					hql += " and g.status in (:status) ";
				} else if (cp.getData().equals("businessUnit")) {
					hql += " and upper(g.businessUnit.unitName) like (:" + cp.getData().replace(".", "") + ")";
				} else if (cp.getData().equals("supplierCompanyName")) {
					hql += " and (upper(g.supplierName) like (:" + cp.getData().replace(".", "") + ") or  upper(g.supplier.companyName) like (:" + cp.getData().replace(".", "") + ")) ";
				} else if (cp.getData().equals("currency")) {
					hql += " and upper(g.currency.currencyCode) like (:" + cp.getData().replace(".", "") + ")";
				} else if (cp.getData().equals("createdBy")) {
					hql += " and upper(g.createdBy.name) like (:" + cp.getData().replace(".", "") + ")";
				} else if (cp.getData().equals("poNumber")) {
					hql += " and upper(g.po.poNumber) like (:" + cp.getData().replace(".", "") + ")";
				} else {
					hql += " and upper(g." + cp.getData() + ") like (:" + cp.getData().replace(".", "") + ") ";

				}
			}
		}

		if (!selectStatusFilter) {
			hql += " and g.status in(:status)";
		}
		// search with Date range
		if (startDate != null && endDate != null) {
			hql += " and  g.createdDate between :startDate and :endDate ";
		}

		// If it is not a count query then add order by clause
		if (!isCount) {
			List<OrderParameter> orderList = tableParams.getOrder();
			if (CollectionUtil.isNotEmpty(orderList)) {
				hql += " order by ";
				for (OrderParameter order : orderList) {
					String orderColumn = tableParams.getColumns().get(order.getColumn()).getData();
					String dir = order.getDir();
					if (orderColumn.equalsIgnoreCase("businessUnit")) {
						hql += " g.businessUnit.unitName " + dir + ",";
					} else if (orderColumn.equalsIgnoreCase("currency")) {
						hql += " g.currency.currencyCode " + dir + ",";
					} else if (orderColumn.equalsIgnoreCase("supplierCompanyName")) {
						hql += " g.supplier.companyName " + dir + ", g.supplierName " + dir;
					} else if (orderColumn.equalsIgnoreCase("createdBy")) {
						hql += " g.createdBy.name " + dir + ",";
					} else if (orderColumn.equalsIgnoreCase("poNumber")) {
						hql += " g.po.poNumber " + dir + ",";
					} else {
						hql += " g." + orderColumn + " " + dir + ",";
					}
				}
				if (hql.lastIndexOf(",") == hql.length() - 1) {
					hql = hql.substring(0, hql.length() - 1);
				}
			} else {
				// by default order by created date
				hql += " order by g.createdDate desc ";
			}
		}

		final Query query = getEntityManager().createQuery(hql.toString());

		query.setParameter("tenantId", tenantId);

		// Apply search filter values
		for (ColumnParameter cp : tableParams.getColumns()) {
			if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
				// LOG.info("INPUT 1 :: " + cp.getData() + "VALUE 1 :: " + cp.getSearch().getValue());
				if (cp.getData().equals("status")) {
					if (cp.getSearch().getValue().equalsIgnoreCase("ALL")) {
						query.setParameter("status", Arrays.asList(GrnStatus.DRAFT, GrnStatus.RECEIVED, GrnStatus.CANCELLED));
					} else {
						query.setParameter("status", GrnStatus.fromString(cp.getSearch().getValue().toUpperCase()));
					}
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

		if (!selectStatusFilter) {
			query.setParameter("status", Arrays.asList(GrnStatus.DRAFT, GrnStatus.RECEIVED, GrnStatus.CANCELLED));
		}
		return query;
	}

	@SuppressWarnings("unchecked")
	@Override
	public GoodsReceiptNote findByGrnId(String doId) {
		StringBuilder hsql = new StringBuilder("from GoodsReceiptNote d left outer join fetch d.supplier sp left outer join fetch d.buyer bu join fetch d.createdBy cb left outer join fetch d.modifiedBy mb left outer join fetch d.currency c left outer join fetch d.po p where d.id = :id");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", doId);
		List<GoodsReceiptNote> list = query.getResultList();
		if (CollectionUtil.isNotEmpty(list)) {
			return list.get(0);
		} else {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<GoodsReceiptNotePojo> findAllSearchFilterGrnsForSupplier(String loggedInUserTenantId, TableDataInput input, Date startDate, Date endDate) {
		final Query query = constructGrnSearchFilterForSupplierQuery(loggedInUserTenantId, input, false, startDate, endDate);
		query.setFirstResult(input.getStart());
		query.setMaxResults(input.getLength());
		return query.getResultList();
	}

	@Override
	public List<GoodsReceiptNotePojo> findAllSearchFilterGrnsSupp(String tenantId, TableDataInput input, Date startDate, Date endDate) {
		final Query query = constructGrnSearchForSupplierQuery(tenantId, input, false, startDate, endDate);
		query.setFirstResult(input.getStart());
		query.setMaxResults(input.getLength());
		return query.getResultList();
	}

	@Override
	public long findTotalSearchFilterGrnsSupp(String tenantId, TableDataInput input, Date startDate, Date endDate) {
		final Query query = constructGrnSearchFilterForSupplierQuery(tenantId, input, true, startDate, endDate);
		return ((Number) query.getSingleResult()).longValue();
	}

	@Override
	public void transferOwnership(String fromUserId, String toUserId) {

		Query query = getEntityManager().createNativeQuery("UPDATE PROC_GRN SET CREATED_BY = :toUserId where CREATED_BY =:fromUserId");
		query.setParameter("toUserId", toUserId);
		query.setParameter("fromUserId", fromUserId);
		int recordsUpdated = query.executeUpdate();
		LOG.info("Creators transferred: {}", recordsUpdated);

	}

	private Query constructGrnSearchFilterForSupplierQuery(String tenantId, TableDataInput tableParams, boolean isCount, Date startDate, Date endDate) {

		String hql = "";

		// If count query is enabled, then add the select count(*) clause
		if (isCount) {
			hql += "select count(distinct g.id) ";
		} else {
			hql += "select distinct new com.privasia.procurehere.core.pojo.GoodsReceiptNotePojo(g.id, g.grnId, g.grnTitle,  g.createdDate, c.currencyCode, g.decimal, bu.unitName, b.companyName, g.grandTotal, g.status, g.actionDate) ";
		}

		hql += " from GoodsReceiptNote g ";

		hql += " left outer join g.createdBy as cb";

		hql += " left outer join g.businessUnit as bu";

		hql += " left outer join g.currency as c";

		hql += " left outer join g.buyer b";

		hql += " left outer join g.supplier s";

		hql += " left outer join g.po p";

		hql += " where g.supplier.id = :tenantId ";

		Boolean selectStatusFilter = false;
		// Add on search filter conditions
		for (ColumnParameter cp : tableParams.getColumns()) {
			if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
				if (cp.getData().equals("status")) {
					selectStatusFilter = true;
					hql += " and g.status in (:status) ";
				} else if (cp.getData().equals("businessUnit")) {
					hql += " and upper(g.businessUnit.unitName) like (:" + cp.getData().replace(".", "") + ")";
				} else if (cp.getData().equals("buyerCompanyName")) {
					hql += " and upper(g.buyer.companyName) like (:" + cp.getData().replace(".", "") + ")";
				} else if (cp.getData().equals("currency")) {
					hql += " and upper(g.currency.currencyCode) like (:" + cp.getData().replace(".", "") + ")";
				} else if (cp.getData().equals("createdBy")) {
					hql += " and upper(g.createdBy.name) like (:" + cp.getData().replace(".", "") + ")";
				} else {
					hql += " and upper(g." + cp.getData() + ") like (:" + cp.getData().replace(".", "") + ") ";

				}
			}
		}

		if (!selectStatusFilter) {
			hql += " and g.status in(:status)";
		}
		// search with Date range
		if (startDate != null && endDate != null) {
			hql += " and  g.createdDate between :startDate and :endDate ";
		}

		// If it is not a count query then add order by clause
		if (!isCount) {
			List<OrderParameter> orderList = tableParams.getOrder();
			if (CollectionUtil.isNotEmpty(orderList)) {
				hql += " order by ";
				for (OrderParameter order : orderList) {
					String orderColumn = tableParams.getColumns().get(order.getColumn()).getData();
					String dir = order.getDir();
					if (orderColumn.equalsIgnoreCase("businessUnit")) {
						hql += " g.businessUnit.unitName " + dir + ",";
					} else if (orderColumn.equalsIgnoreCase("currency")) {
						hql += " g.currency.currencyCode " + dir + ",";
					} else if (orderColumn.equalsIgnoreCase("buyerCompanyName")) {
						hql += " g.buyer.companyName " + dir + ",";
					} else if (orderColumn.equalsIgnoreCase("acceptRejectDate")) {
						hql += " g.actionDate " + dir + ",";
					} else if (orderColumn.equalsIgnoreCase("createdBy")) {
						hql += " g.createdBy.name " + dir + ",";
					} else {
						hql += " g." + orderColumn + " " + dir + ",";
					}
				}
				if (hql.lastIndexOf(",") == hql.length() - 1) {
					hql = hql.substring(0, hql.length() - 1);
				}
			} else {
				// by default order by created date
				hql += " order by g.createdDate desc ";
			}
		}

		final Query query = getEntityManager().createQuery(hql.toString());

		query.setParameter("tenantId", tenantId);

		// Apply search filter values
		for (ColumnParameter cp : tableParams.getColumns()) {
			if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
				// LOG.info("INPUT 1 :: " + cp.getData() + "VALUE 1 :: " + cp.getSearch().getValue());
				if (cp.getData().equals("status")) {
					if (cp.getSearch().getValue().equalsIgnoreCase("ALL")) {
						query.setParameter("status", Arrays.asList(GrnStatus.DRAFT, GrnStatus.RECEIVED, GrnStatus.CANCELLED));
					} else {
						query.setParameter("status", GrnStatus.fromString(cp.getSearch().getValue().toUpperCase()));
					}
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

		if (!selectStatusFilter) {
			query.setParameter("status", Arrays.asList(GrnStatus.DRAFT, GrnStatus.RECEIVED, GrnStatus.CANCELLED));
		}
		return query;

	}

	@Override
	public long findTotalSearchFilterGrnForSupplier(String tenantId, TableDataInput input, Date startDate, Date endDate) {
		final Query query = constructGrnSearchFilterForTenantQuery(tenantId, input, true, startDate, endDate);
		return ((Number) query.getSingleResult()).longValue();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<GoodsReceiptNotePojo> getAllBuyerGrnDetailsForExcelReport(String loggedInUserTenantId, String[] grnIds, GoodsReceiptNoteSearchPojo goodsReceiptNotePojo, boolean select_all, Date startDate, Date endDate, SimpleDateFormat sdf) {
		String hql = "";

		hql = constructBuyerQueryForGrnDetailsForExcelReport(loggedInUserTenantId, grnIds, goodsReceiptNotePojo, select_all, startDate, endDate);

		final Query query = getEntityManager().createQuery(hql.toString());

		if (!(select_all)) {
			if (grnIds != null && grnIds.length > 0) {
				query.setParameter("grnIds", Arrays.asList(grnIds));
			}
		}

		if (goodsReceiptNotePojo != null) {
			if (StringUtils.checkString(goodsReceiptNotePojo.getGrnname()).length() > 0) {
				query.setParameter("grnTitle", "%" + goodsReceiptNotePojo.getGrnname().toUpperCase() + "%");
			}
			if (StringUtils.checkString(goodsReceiptNotePojo.getPonumber()).length() > 0) {
				query.setParameter("poNumber", "%" + goodsReceiptNotePojo.getPonumber().toUpperCase() + "%");
			}
			if (StringUtils.checkString(goodsReceiptNotePojo.getReferencenumber()).length() > 0) {
				query.setParameter("referenceNumber", "%" + goodsReceiptNotePojo.getReferencenumber().toUpperCase() + "%");
			}
			if (StringUtils.checkString(goodsReceiptNotePojo.getGrnnumber()).length() > 0) {
				query.setParameter("grnId", "%" + goodsReceiptNotePojo.getGrnnumber().toUpperCase() + "%");
			}

			if (StringUtils.checkString(goodsReceiptNotePojo.getSupplier()).length() > 0) {
				query.setParameter("supplier", "%" + goodsReceiptNotePojo.getSupplier().toUpperCase() + "%");
			}
			if (StringUtils.checkString(goodsReceiptNotePojo.getBusinessunit()).length() > 0) {
				query.setParameter("businessunit", "%" + goodsReceiptNotePojo.getBusinessunit().toUpperCase() + "%");
			}
			if (StringUtils.checkString(goodsReceiptNotePojo.getCreatedby()).length() > 0) {
				query.setParameter("createdby", "%" + goodsReceiptNotePojo.getCreatedby().toUpperCase() + "%");
			}
			if (goodsReceiptNotePojo.getStatus() != null) {
				query.setParameter("status", Arrays.asList(goodsReceiptNotePojo.getStatus()));
			}
		}

		if (startDate != null && endDate != null) {
			query.setParameter("startDate", startDate);
			query.setParameter("endDate", endDate);
		}
		query.setParameter("tenantId", loggedInUserTenantId);

		List<GoodsReceiptNotePojo> finalList = query.getResultList();

		return finalList;
	}

	private String constructBuyerQueryForGrnDetailsForExcelReport(String tenantId, String[] grnIds, GoodsReceiptNoteSearchPojo goodsReceiptNotePojo, boolean select_all, Date startDate, Date endDate) {

		String hql = "";

		hql += "select distinct new com.privasia.procurehere.core.pojo.GoodsReceiptNotePojo(g.id, g.grnId, g.grnTitle, c.currencyCode, g.decimal, bu.unitName, s.companyName, g.supplierName, cb.name, g.createdDate, g.grandTotal, g.status, g.referenceNumber, g.grnReceivedDate, g.goodsReceiptDate,p.poNumber)";

		hql += " from GoodsReceiptNote g ";

		hql += " left outer join g.createdBy as cb";

		hql += " left outer join g.businessUnit as bu";

		hql += " left outer join g.currency as c";

		hql += " left outer join g.supplier s";

		hql += " left outer join g.po p";

		hql += " where g.buyer.id = :tenantId ";

		if (!(select_all)) {
			hql += " and g.id in (:grnIds)";
		}

		if (goodsReceiptNotePojo != null) {
			if (StringUtils.checkString(goodsReceiptNotePojo.getGrnname()).length() > 0) {
				hql += " and upper(g.grnTitle) like :grnTitle";
			}
			if (StringUtils.checkString(goodsReceiptNotePojo.getReferencenumber()).length() > 0) {
				hql += " and upper(g.referenceNumber) like :referenceNumber";
			}
			if (StringUtils.checkString(goodsReceiptNotePojo.getPonumber()).length() > 0) {
				hql += " and upper(p.poNumber) like :poNumber";
			}
			if (StringUtils.checkString(goodsReceiptNotePojo.getGrnnumber()).length() > 0) {
				hql += " and upper(g.grnId) like :grnId";
			}

			if (StringUtils.checkString(goodsReceiptNotePojo.getSupplier()).length() > 0) {
				hql += " and upper(s.companyName) like :supplier  or  upper(g.supplierName) like :supplier";
			}
			if (StringUtils.checkString(goodsReceiptNotePojo.getBusinessunit()).length() > 0) {
				hql += " and upper(bu.unitName) like :businessunit";
			}
			if (StringUtils.checkString(goodsReceiptNotePojo.getCreatedby()).length() > 0) {
				hql += " and upper(cb.name) like :createdby";
			}
			if (goodsReceiptNotePojo.getStatus() != null) {
				hql += " and upper(g.status) like :status";
			}
		}

		if (startDate != null && endDate != null) {
			hql += " and g.createdDate between :startDate and :endDate";
		}

		hql += " order by g.createdDate desc ";

		return hql;

	}

	@SuppressWarnings("unchecked")
	@Override
	public List<GoodsReceiptNotePojo> getAllSupplierGrnDetailsForExcelReport(String loggedInUserTenantId, String[] grnIds, GoodsReceiptNotePojo goodsReceiptNotePojo, boolean select_all, Date startDate, Date endDate, SimpleDateFormat sdf) {
		String hql = "";

		hql = constructSupplierQueryForGrnDetailsForExcelReport(loggedInUserTenantId, grnIds, goodsReceiptNotePojo, select_all, startDate, endDate);

		final Query query = getEntityManager().createQuery(hql.toString());

		if (!(select_all)) {
			if (grnIds != null && grnIds.length > 0) {
				query.setParameter("grnIds", Arrays.asList(grnIds));
			}
		}

		if (goodsReceiptNotePojo != null) {
			if (StringUtils.checkString(goodsReceiptNotePojo.getGrnTitle()).length() > 0) {
				query.setParameter("grnTitle", "%" + goodsReceiptNotePojo.getGrnTitle().toUpperCase() + "%");
			}
			if (StringUtils.checkString(goodsReceiptNotePojo.getGrnId()).length() > 0) {
				query.setParameter("grnId", "%" + goodsReceiptNotePojo.getGrnId().toUpperCase() + "%");
			}

			if (StringUtils.checkString(goodsReceiptNotePojo.getSupplier()).length() > 0) {
				query.setParameter("buyer", "%" + goodsReceiptNotePojo.getBuyer().toUpperCase() + "%");
			}

			if (goodsReceiptNotePojo.getStatus() != null) {
				query.setParameter("status", Arrays.asList(goodsReceiptNotePojo.getStatus()));
			}
		}

		if (startDate != null && endDate != null) {
			query.setParameter("startDate", startDate);
			query.setParameter("endDate", endDate);
		}
		query.setParameter("tenantId", loggedInUserTenantId);

		List<GoodsReceiptNotePojo> finalList = query.getResultList();

		return finalList;
	}

	private String constructSupplierQueryForGrnDetailsForExcelReport(String tenantId, String[] grnIds, GoodsReceiptNotePojo goodsReceiptNotePojo, boolean select_all, Date startDate, Date endDate) {

		String hql = "";

		hql += "select distinct new com.privasia.procurehere.core.pojo.GoodsReceiptNotePojo(g.id, g.grnId, g.grnTitle,  g.createdDate, c.currencyCode, g.decimal, bu.unitName, b.companyName, g.grandTotal, g.status, g.actionDate) ";

		hql += " from GoodsReceiptNote g ";

		hql += " left outer join g.createdBy as cb";

		hql += " left outer join g.businessUnit as bu";

		hql += " left outer join g.currency as c";

		hql += " left outer join g.supplier s";

		hql += " left outer join g.actionBy as ab";

		hql += " where g.supplier.id = :tenantId ";

		if (!(select_all)) {
			hql += " and g.id in (:grnIds)";
		}

		if (goodsReceiptNotePojo != null) {
			if (StringUtils.checkString(goodsReceiptNotePojo.getGrnTitle()).length() > 0) {
				hql += " and upper(g.grnTitle) like :grnTitle";
			}

			if (StringUtils.checkString(goodsReceiptNotePojo.getGrnId()).length() > 0) {
				hql += " and upper(g.grnId) like :grnId";
			}

			if (StringUtils.checkString(goodsReceiptNotePojo.getBuyer()).length() > 0) {
				hql += " and upper(s.companyName) like :buyer";
			}

			if (goodsReceiptNotePojo.getStatus() != null) {
				hql += " and upper(g.status) like :status";
			}
		}

		if (startDate != null && endDate != null) {
			hql += " and g.createdDate between :startDate and :endDate";
		}

		hql += " order by g.createdDate desc ";

		return hql;

	}

	@SuppressWarnings("unchecked")
	@Override
	public List<GoodsReceiptNotePojo> getGrnListByPoIdForBuyer(String poId) {
		String hql = "select distinct new com.privasia.procurehere.core.pojo.GoodsReceiptNotePojo(g.id, g.grnId, g.grnTitle,  g.referenceNumber,  g.decimal,cb.name,  g.grandTotal, g.status,  g.goodsReceiptDate,  g.grnReceivedDate, g.createdDate) from GoodsReceiptNote g  left outer join g.po p left outer join g.createdBy cb where p.id =:poId and g.status in (:status) order by g.createdDate desc";
		final Query query = getEntityManager().createQuery(hql);
		query.setParameter("poId", poId);
		query.setParameter("status", Arrays.asList(GrnStatus.DRAFT, GrnStatus.RECEIVED, GrnStatus.CANCELLED));
		return query.getResultList();
	}

	@Override
	public long findTotalGrnForPo(String poId) {
		StringBuilder hsql = new StringBuilder("select count(distinct g) from GoodsReceiptNote g where g.po.id = :poId and g.status in (:status)");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("poId", poId);
		query.setParameter("status", Arrays.asList(GrnStatus.DRAFT, GrnStatus.RECEIVED, GrnStatus.CANCELLED));
		return ((Number) query.getSingleResult()).longValue();
	}

	@Override
	public long findTotalDraftOrReceivedGrnForPo(String poId) {
		StringBuilder hsql = new StringBuilder("select count(distinct g) from GoodsReceiptNote g where g.po.id = :poId and g.status in (:status)");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("poId", poId);
		query.setParameter("status", Arrays.asList(GrnStatus.DRAFT, GrnStatus.RECEIVED));
		return ((Number) query.getSingleResult()).longValue();
	}

	@Override
	public long findTotalGrnCountByTenantIdForBuyer(String tenantId) {
		StringBuilder hsql = new StringBuilder("select count(distinct g) from GoodsReceiptNote g where g.buyer.id = :tenantId");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("tenantId", tenantId);
		return ((Number) query.getSingleResult()).longValue();
	}

	@Override
	public long findTotalDraftOrReceivedGrnForPoByPoId(String poId) {
		StringBuilder hsql = new StringBuilder("select count(distinct g) from GoodsReceiptNote g where g.po.id = :poId");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("poId", poId);
		return ((Number) query.getSingleResult()).longValue();
	}

	private Query constructGrnSearchForSupplierQuery(String tenantId, TableDataInput tableParams, boolean isCount, Date startDate, Date endDate) {

		String hql = "";

		// If count query is enabled, then add the select count(*) clause
		if (isCount) {
			hql += "select count(distinct g.id) ";
		} else {
			hql += "select distinct new com.privasia.procurehere.core.pojo.GoodsReceiptNotePojo(g.id, g.grnId, g.grnTitle, c.currencyCode, g.decimal, bu.unitName, s.companyName, g.supplierName, cb.name, g.createdDate, g.grandTotal, g.status, g.referenceNumber, g.grnReceivedDate, g.goodsReceiptDate,p.poNumber) ";
		}

		hql += " from GoodsReceiptNote g ";

		hql += " left outer join g.createdBy as cb";

		hql += " left outer join g.businessUnit as bu";

		hql += " left outer join g.currency as c";

		hql += " left outer join g.buyer b";

		hql += " left outer join g.supplier s";

		hql += " left outer join g.po p";

		hql += " where g.supplier.id = :tenantId ";

		Boolean selectStatusFilter = false;
		// Add on search filter conditions
		for (ColumnParameter cp : tableParams.getColumns()) {
			if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
				if (cp.getData().equals("status")) {
					selectStatusFilter = true;
					hql += " and g.status in (:status) ";
				} else if (cp.getData().equals("businessUnit")) {
					hql += " and upper(g.businessUnit.unitName) like (:" + cp.getData().replace(".", "") + ")";
				} else if (cp.getData().equals("supplierCompanyName")) {
					hql += " and (upper(g.supplierName) like (:" + cp.getData().replace(".", "") + ") or  upper(g.supplier.companyName) like (:" + cp.getData().replace(".", "") + ")) ";
				} else if (cp.getData().equals("currency")) {
					hql += " and upper(g.currency.currencyCode) like (:" + cp.getData().replace(".", "") + ")";
				} else if (cp.getData().equals("createdBy")) {
					hql += " and upper(g.createdBy.name) like (:" + cp.getData().replace(".", "") + ")";
				} else if (cp.getData().equals("poNumber")) {
					hql += " and upper(g.po.poNumber) like (:" + cp.getData().replace(".", "") + ")";
				} else {
					hql += " and upper(g." + cp.getData() + ") like (:" + cp.getData().replace(".", "") + ") ";

				}
			}
		}

		if (!selectStatusFilter) {
			hql += " and g.status in(:status)";
		}
		// search with Date range
		if (startDate != null && endDate != null) {
			hql += " and  g.createdDate between :startDate and :endDate ";
		}

		// If it is not a count query then add order by clause
		if (!isCount) {
			List<OrderParameter> orderList = tableParams.getOrder();
			if (CollectionUtil.isNotEmpty(orderList)) {
				hql += " order by ";
				for (OrderParameter order : orderList) {
					String orderColumn = tableParams.getColumns().get(order.getColumn()).getData();
					String dir = order.getDir();
					if (orderColumn.equalsIgnoreCase("businessUnit")) {
						hql += " g.businessUnit.unitName " + dir + ",";
					} else if (orderColumn.equalsIgnoreCase("currency")) {
						hql += " g.currency.currencyCode " + dir + ",";
					} else if (orderColumn.equalsIgnoreCase("supplierCompanyName")) {
						hql += " g.supplier.companyName " + dir + ", g.supplierName " + dir;
					} else if (orderColumn.equalsIgnoreCase("createdBy")) {
						hql += " g.createdBy.name " + dir + ",";
					} else if (orderColumn.equalsIgnoreCase("poNumber")) {
						hql += " g.po.poNumber " + dir + ",";
					} else {
						hql += " g." + orderColumn + " " + dir + ",";
					}
				}
				if (hql.lastIndexOf(",") == hql.length() - 1) {
					hql = hql.substring(0, hql.length() - 1);
				}
			} else {
				// by default order by created date
				hql += " order by g.createdDate desc ";
			}
		}

		final Query query = getEntityManager().createQuery(hql.toString());

		query.setParameter("tenantId", tenantId);

		// Apply search filter values
		for (ColumnParameter cp : tableParams.getColumns()) {
			if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
				// LOG.info("INPUT 1 :: " + cp.getData() + "VALUE 1 :: " + cp.getSearch().getValue());
				if (cp.getData().equals("status")) {
					if (cp.getSearch().getValue().equalsIgnoreCase("ALL")) {
						query.setParameter("status", Arrays.asList(GrnStatus.DRAFT, GrnStatus.RECEIVED, GrnStatus.CANCELLED));
					} else {
						query.setParameter("status", GrnStatus.fromString(cp.getSearch().getValue().toUpperCase()));
					}
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

		if (!selectStatusFilter) {
			query.setParameter("status", Arrays.asList(GrnStatus.DRAFT, GrnStatus.RECEIVED, GrnStatus.CANCELLED));
		}
		return query;
	}
}
