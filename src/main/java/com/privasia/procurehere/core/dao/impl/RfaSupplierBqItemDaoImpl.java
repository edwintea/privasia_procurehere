package com.privasia.procurehere.core.dao.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Repository;

import com.privasia.procurehere.core.dao.RfaSupplierBqItemDao;
import com.privasia.procurehere.core.entity.RfaBqItem;
import com.privasia.procurehere.core.entity.RfaSupplierBq;
import com.privasia.procurehere.core.entity.RfaSupplierBqItem;
import com.privasia.procurehere.core.entity.RftSupplierBqItem;
import com.privasia.procurehere.core.entity.Supplier;
import com.privasia.procurehere.core.enums.PricingTypes;
import com.privasia.procurehere.core.pojo.BqItemPojo;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.StringUtils;

/**
 * @author VIPUL
 * @author RT-Kapil
 * @author Ravi
 */
@Repository
public class RfaSupplierBqItemDaoImpl extends GenericDaoImpl<RfaSupplierBqItem, String> implements RfaSupplierBqItemDao {

	private static final Logger LOG = LogManager.getLogger(Global.SUPPLIER_LOG);

	@SuppressWarnings("unchecked")
	@Override
	public List<RfaSupplierBqItem> findSupplierBqItemListByBqIdAndSupplierId(String bqId, String supplierId) {
		final Query query = getEntityManager().createQuery("select distinct a from RfaSupplierBqItem a inner join fetch a.supplier as s inner join fetch a.event as ev inner join fetch a.supplierBq as b inner join fetch b.bq bq left outer join fetch a.children as ch where s.id= :supplierId and bq.id= :bqId and a.parent is null order by a.level, a.order");
		query.setParameter("bqId", bqId);
		query.setParameter("supplierId", supplierId);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<RfaBqItem> getBqItemsbyId(String bqId) {
		final Query query = getEntityManager().createQuery("select distinct a from RfaBqItem a inner join fetch a.bq sp left outer join fetch a.children c left outer join fetch a.uom uom where sp.id =:id and a.parent is null order by a.level, a.order");
		query.setParameter("id", bqId);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public RfaSupplierBqItem getBqItemByBqItemId(String itemId, String supplierId) {
		try {
			LOG.info("Item Id : " + itemId + " Supplier Id : " + supplierId);
			final Query query = getEntityManager().createQuery("select distinct rb from RfaSupplierBqItem rb inner join fetch rb.event as ev inner join fetch rb.supplierBq as s left outer join fetch rb.uom as uom left outer join fetch rb.supplier as sup where rb.id =:itemId and sup.id= :supplierId");
			query.setParameter("itemId", itemId);
			query.setParameter("supplierId", supplierId);
			List<RfaSupplierBqItem> uList = query.getResultList();
			if (CollectionUtil.isNotEmpty(uList)) {
				return uList.get(0);
			} else {
				return null;
			}
		} catch (NoResultException nr) {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<RfaBqItem> getBqItemsToStoreInSupplierById(String bqId) {
		final Query query = getEntityManager().createQuery("select distinct a from RfaBqItem a inner join fetch a.bq sp left outer join fetch a.children c left outer join fetch a.uom uom where sp.id =:id order by a.level, a.order");
		query.setParameter("id", bqId);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<RfaSupplierBqItem> findSupplierBqItemsByBqItemIdAndEventId(String bqItemId, String eventId, List<Supplier> suppliers) {
		LOG.info("Event Id : " + eventId + " Bq Id : " + bqItemId + " Suppliers : " + suppliers.size());
		final Query query = getEntityManager().createQuery("select distinct a from RfaSupplierBqItem a inner join fetch a.supplier s inner join a.event re where a.bqItem.id = :bqItemId and re.id =:eventId and s in (:suppliers) order by s.companyName");
		query.setParameter("bqItemId", bqItemId);
		query.setParameter("eventId", eventId);
		query.setParameter("suppliers", suppliers);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<String> findSumOfTotalAmountWithTaxForSupplier(String bqId, String eventId, List<Supplier> suppliers) {
		List<String> data = new ArrayList<String>();

		StringBuilder hsql = new StringBuilder("select sum(b.totalAmountWithTax), s.companyName, b.event.decimal, s.id from RfaSupplierBqItem b  inner join b.supplierBq bq inner join b.supplier s where  b.supplier.id = s.id and bq.bq.id = :bqId  and b.event.id =:eventId and s in (:suppliers)");
		hsql.append("group by  s.companyName, b.event.decimal,s.id order by s.companyName");

		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("bqId", bqId);
		query.setParameter("eventId", eventId);
		query.setParameter("suppliers", suppliers);
		List<Object[]> list = query.getResultList();
		for (Object[] values : list) {
			data.add(values[0] != null ? String.valueOf(values[0] + "-" + bqId + "-" + values[3]) : "");
		}
		return data;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<String> findSumOfTotalAmountForSupplier(String bqId, String eventId, List<Supplier> suppliers, Boolean withTax) {
		List<String> data = new ArrayList<String>();

		StringBuilder hsql = new StringBuilder("select  sum(b.totalAmount), sum(b.totalAmountWithTax - b.totalAmount), sum(b.totalAmountWithTax), s.companyName, b.event.decimal,bq.grandTotal from RfaSupplierBqItem b  inner join b.supplierBq bq inner join b.supplier s where  b.supplier.id = s.id and bq.bq.id = :bqId  and b.event.id =:eventId and s in (:suppliers)");
		hsql.append("group by  s.companyName, b.event.decimal,bq.grandTotal order by s.companyName");

		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("bqId", bqId);
		query.setParameter("eventId", eventId);
		query.setParameter("suppliers", suppliers);
		List<Object[]> list = query.getResultList();
		for (Object[] values : list) {
			if (Boolean.TRUE == withTax) {
				data.add("");
				data.add("");
				data.add("");
				data.add(values[5] != null ? String.valueOf(new BigDecimal(String.valueOf(values[5])).setScale(Integer.parseInt(String.valueOf(values[4])), RoundingMode.HALF_UP)) : "");
			} else {
				data.add("");
				data.add(values[5] != null ? String.valueOf(new BigDecimal(String.valueOf(values[5])).setScale(Integer.parseInt(String.valueOf(values[4])), RoundingMode.HALF_UP)) : "");

			}
		}
		return data;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<String> findAddtionalTaxForView(String bqId, String eventId, List<Supplier> suppliers) {
		List<String> data = new ArrayList<String>();

		StringBuilder hsql = new StringBuilder("select  distinct bq.taxDescription , bq.additionalTax , s.companyName, b.event.decimal from RfaSupplierBqItem b  inner join b.supplierBq bq inner join b.supplier s where  b.supplier.id = s.id and bq.bq.id = :bqId and b.event.id =:eventId and s in (:suppliers)");
		hsql.append(" order by s.companyName");

		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("bqId", bqId);
		query.setParameter("eventId", eventId);
		query.setParameter("suppliers", suppliers);
		List<Object[]> list = query.getResultList();
		for (Object[] values : list) {
			DecimalFormat df = null;
			if (values[3].equals("1")) {
				df = new DecimalFormat("#,###,###,##0.0");
			} else if (values[3].equals("2")) {
				df = new DecimalFormat("#,###,###,##0.00");
			} else if (values[3].equals("3")) {
				df = new DecimalFormat("#,###,###,##0.000");
			} else if (values[3].equals("4")) {
				df = new DecimalFormat("#,###,###,##0.0000");
			} else if (values[3].equals("5")) {
				df = new DecimalFormat("#,###,###,##0.00000");
			} else if (values[3].equals("6")) {
				df = new DecimalFormat("#,###,###,##0.000000");
			} else {
				df = new DecimalFormat("#,###,###,##0.00");
			}
			if (!StringUtils.checkString(String.valueOf(values[1])).equalsIgnoreCase("null")) {
				data.add(StringUtils.checkString(String.valueOf(values[1])).length() > 0 ? df.format((BigDecimal) values[1]) : "");
			} else {
				data.add("0");
			}
		}
		return data;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<String> findGrandTotalsForView(String bqId, String eventId, List<Supplier> suppliers) {
		List<String> data = new ArrayList<String>();

		StringBuilder hsql = new StringBuilder("select sum(b.totalAmountWithTax), bq.totalAfterTax, s.companyName, b.event.decimal, s.id from RfaSupplierBqItem b  inner join b.supplierBq bq inner join b.supplier s where  b.supplier.id = s.id and bq.bq.id = :bqId  and b.event.id =:eventId and s in (:suppliers)");
		hsql.append("group by  bq.totalAfterTax, s.companyName, b.event.decimal, s.id order by s.companyName");

		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("bqId", bqId);
		query.setParameter("eventId", eventId);
		query.setParameter("suppliers", suppliers);
		List<Object[]> list = query.getResultList();
		for (Object[] values : list) {
			LOG.info("values[0] : " + values[0] + " values[2] : " + values[2]);
			// data.add(values[0] != null ? String.valueOf(new
			// BigDecimal(String.valueOf(values[0])).setScale(Integer.parseInt(String.valueOf(values[2])),
			// RoundingMode.HALF_UP)) : "");
			data.add(values[0] != null ? String.valueOf(values[0] + "-" + bqId + "-" + values[4]) : "");
		}

		// StringBuilder hsql = new StringBuilder("select
		// sum(b.totalAmountWithTax), bq.totalAfterTax , b.event.decimal,
		// s.companyName from RfaSupplierBqItem b inner join b.supplierBq bq
		// inner join b.supplier s where b.supplier.id = s.id and bq.bq.id =
		// :bqId and b.event.id =:eventId and s in (:suppliers)");
		// hsql.append("group by bq.totalAfterTax , b.event.decimal,
		// s.companyName order by s.companyName");
		//
		// Query query = getEntityManager().createQuery(hsql.toString());
		// query.setParameter("bqId", bqId);
		// query.setParameter("eventId", eventId);
		// query.setParameter("suppliers", suppliers);
		// List<Object[]> list = query.getResultList();
		// for (Object[] values : list) {
		// BigDecimal grandTotal = BigDecimal.ZERO;
		// if (values[1] != null) {
		// grandTotal = new
		// BigDecimal(String.valueOf(values[1])).setScale(Integer.parseInt(String.valueOf(values[2])),
		// RoundingMode.HALF_UP);
		// } else {
		// grandTotal = new
		// BigDecimal(String.valueOf(values[0])).setScale(Integer.parseInt(String.valueOf(values[2])),
		// RoundingMode.HALF_UP);
		// }
		// data.add(grandTotal);
		// }
		return data;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<String> findAddtionalTax(String bqId, String eventId, List<Supplier> suppliers) {
		List<String> data = new ArrayList<String>();

		StringBuilder hsql = new StringBuilder("select  distinct bq.taxDescription , bq.additionalTax , s.companyName, b.event.decimal from RfaSupplierBqItem b  inner join b.supplierBq bq inner join b.supplier s where  b.supplier.id = s.id and bq.bq.id = :bqId and b.event.id =:eventId and s in (:suppliers)");
		hsql.append(" order by s.companyName");

		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("bqId", bqId);
		query.setParameter("eventId", eventId);
		query.setParameter("suppliers", suppliers);
		List<Object[]> list = query.getResultList();
		for (Object[] values : list) {
			data.add("");
			data.add("");
			data.add(values[0] != null ? StringUtils.checkString(String.valueOf(values[0])) : "");
			// For Financial Standard
			DecimalFormat df = null;
			if (values[3].equals("1")) {
				df = new DecimalFormat("#,###,###,##0.0");
			} else if (values[3].equals("2")) {
				df = new DecimalFormat("#,###,###,##0.00");
			} else if (values[3].equals("3")) {
				df = new DecimalFormat("#,###,###,##0.000");
			} else if (values[3].equals("4")) {
				df = new DecimalFormat("#,###,###,##0.0000");
			} else if (values[3].equals("5")) {
				df = new DecimalFormat("#,###,###,##0.00000");
			} else if (values[3].equals("6")) {
				df = new DecimalFormat("#,###,###,##0.000000");
			} else {
				df = new DecimalFormat("#,###,###,##0.00");
			}
			LOG.info("values[1] :" + StringUtils.checkString(String.valueOf(values[1])));
			if (!StringUtils.checkString(String.valueOf(values[1])).equalsIgnoreCase("null")) {
				data.add(StringUtils.checkString(String.valueOf(values[1])).length() > 0 ? df.format((BigDecimal) values[1]) : "");
			} else {
				data.add("");
			}
		}
		return data;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<String> findGrandTotals(String bqId, String eventId, List<Supplier> suppliers, Boolean withTax) {
		List<String> data = new ArrayList<String>();

		StringBuilder hsql = new StringBuilder("select  distinct sum(b.totalAmountWithTax), bq.totalAfterTax , b.event.decimal, s.companyName,bq.grandTotal from RfaSupplierBqItem b  inner join b.supplierBq bq inner join b.supplier s where  b.supplier.id = s.id and bq.bq.id = :bqId and b.event.id =:eventId and s in (:suppliers)");
		hsql.append(" group by bq.totalAfterTax , b.event.decimal, s.companyName, s.id ,bq.grandTotal  order by s.companyName");

		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("bqId", bqId);
		query.setParameter("eventId", eventId);
		query.setParameter("suppliers", suppliers);
		List<Object[]> list = query.getResultList();
		for (Object[] values : list) {
			if (Boolean.TRUE == withTax) {
				data.add("");
				data.add("");
			}
			data.add("");
			BigDecimal grandTotal = BigDecimal.ZERO;
			if (values[1] != null) {
				grandTotal = new BigDecimal(String.valueOf(values[1])).setScale(Integer.parseInt(String.valueOf(values[2])), RoundingMode.HALF_UP);
			} else {
				grandTotal = new BigDecimal(String.valueOf(values[4])).setScale(Integer.parseInt(String.valueOf(values[2])), RoundingMode.HALF_UP);
			}
			data.add(String.valueOf(grandTotal));
		}
		return data;
	}

	@SuppressWarnings("unchecked")
	@Override
	public RftSupplierBqItem getBqItemByBqItemIdAndSuplier(String itemId, String supplierId) {
		try {
			final Query query = getEntityManager().createQuery("select rb from RfaSupplierBqItem rb where rb.id =:itemId and rb.supplier.id= :supplierId");
			query.setParameter("itemId", itemId);
			query.setParameter("supplierId", supplierId);
			List<RftSupplierBqItem> uList = query.getResultList();
			if (CollectionUtil.isNotEmpty(uList)) {
				return uList.get(0);
			} else {
				return null;
			}
		} catch (NoResultException nr) {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<RfaSupplierBqItem> findAllSupplierBqItemListByBqId(String bqId) {
		final Query query = getEntityManager().createQuery("select  a from RfaSupplierBqItem a inner join fetch a.bq bq left outer join fetch a.children as ch where bq.id= :bqId and a.parent is null order by a.level, a.order");
		query.setParameter("bqId", bqId);
		return query.getResultList();
	}

	@Override
	public Integer getCountOfAllRfaSupplierBqItem(String supplierId, String eventId) {
		StringBuilder hsql = new StringBuilder("select count(r) as tcount from RfaSupplierBqItem r where r.event.id =:eventId and r.supplier.id =:supplierId");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("supplierId", supplierId);
		query.setParameter("eventId", eventId);
		return ((Number) query.getSingleResult()).intValue();
	}

	@Override
	public int countNumberOfBqAnsweredByBqIdAndSupplierId(String eventId, String supplierId) {
		StringBuilder hsql = new StringBuilder("select sum(r.totalAmountWithTax) as tcount from RfaSupplierBqItem r where r.event.id =:eventId and r.supplier.id =:supplierId ");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("eventId", eventId);
		query.setParameter("supplierId", supplierId);
		Number number = (Number) query.getSingleResult();
		return number != null ? number.intValue() : 0;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<RfaSupplierBq> getAllBqsBySupplierId(String eventId, String supplierId) {
		final Query query = getEntityManager().createQuery("select distinct a.supplierBq from RfaSupplierBqItem a where a.event.id = :eventId and a.supplier.id =:supplierId order by a.supplierBq.bqOrder");
		query.setParameter("eventId", eventId);
		query.setParameter("supplierId", supplierId);
		List<RfaSupplierBq> list = query.getResultList();
		return list;
	}

	@Override
	public Integer getCountOfSupplierBqItem(String eventId) {
		StringBuilder hsql = new StringBuilder("select count(r) as tcount from RfaSupplierBqItem r where r.event.id =:eventId");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("eventId", eventId);
		return ((Number) query.getSingleResult()).intValue();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<RfaSupplierBqItem> getBqItemForSearchFilterForSupplier(String eventBqId, String supplierId, String searchVal, Integer start, Integer pageLength, Integer itemLevel, Integer itemOrder) {
		StringBuilder hql = new StringBuilder("select distinct a from RfaSupplierBqItem a inner join fetch a.supplier as s inner join fetch a.supplierBq as b inner join fetch b.bq bq left outer join fetch a.children as ch left outer join fetch a.parent p where s.id= :supplierId and bq.id= :bqId ");

		if (searchVal != null && StringUtils.checkString(searchVal).length() > 0) {
			hql.append(" and (upper(a.itemName) like :searchValue or upper(a.itemDescription) like :searchValue) ");
		}
		if (itemLevel != null && itemOrder != null) {
			hql.append(" and a.level =:itemLevel and a.order =:itemOrder ");
		}
		hql.append(" order by a.level, a.order ");
		final Query query = getEntityManager().createQuery(hql.toString());

		if (searchVal != null && StringUtils.checkString(searchVal).length() > 0) {
			query.setParameter("searchValue", "%" + searchVal.toUpperCase() + "%");
		}
		if (itemLevel != null && itemOrder != null) {
			query.setParameter("itemLevel", itemLevel);
			query.setParameter("itemOrder", itemOrder);
		}
		query.setParameter("bqId", eventBqId);
		query.setParameter("supplierId", supplierId);

		if (start != null && pageLength != null) {
			query.setFirstResult(start);
			query.setMaxResults(pageLength);
		}
		LOG.info("  hql  :  " + hql);
		List<RfaSupplierBqItem> list = query.getResultList();
		LOG.info(" uList Size :" + list.size());
		return list;

	}

	@SuppressWarnings("unchecked")
	@Override
	public List<BqItemPojo> getAllLevelOrderBqItemByBqId(String bqId, String supplierId, String searchVal) {
		LOG.info(" bqId  :" + bqId);
		StringBuilder hql = new StringBuilder("select distinct new com.privasia.procurehere.core.pojo.BqItemPojo(a.level, a.order) from RfaSupplierBqItem a where a.supplier.id= :supplierId and a.supplierBq.bq.id= :bqId ");

		if (searchVal != null && StringUtils.checkString(searchVal).length() > 0) {
			hql.append(" and (upper(a.itemName) like :searchValue or upper(a.itemDescription) like :searchValue) ");
		}
		hql.append(" order by a.level, a.order ");
		final Query query = getEntityManager().createQuery(hql.toString());

		if (searchVal != null && StringUtils.checkString(searchVal).length() > 0) {
			query.setParameter("searchValue", "%" + searchVal.toUpperCase() + "%");
		}
		query.setParameter("bqId", bqId);
		query.setParameter("supplierId", supplierId);
		List<BqItemPojo> list = query.getResultList();
		return list;
	}

	@Override
	public long totalBqItemCountByBqId(String eventBqId, String supplierId, String searchVal) {
		StringBuilder hql = new StringBuilder("select count(a) from RfaSupplierBqItem a where a.supplier.id= :supplierId and a.supplierBq.bq.id= :bqId ");

		if (searchVal != null && StringUtils.checkString(searchVal).length() > 0) {
			hql.append(" and (upper(a.itemName) like :searchValue or upper(a.itemDescription) like :searchValue) ");
		}

		final Query query = getEntityManager().createQuery(hql.toString());

		if (searchVal != null && StringUtils.checkString(searchVal).length() > 0) {
			query.setParameter("searchValue", "%" + searchVal.toUpperCase() + "%");
		}
		query.setParameter("bqId", eventBqId);
		query.setParameter("supplierId", supplierId);

		return ((Number) query.getSingleResult()).intValue();
	}

	@SuppressWarnings("unchecked")
	@Override
	public RfaSupplierBqItem getSupplierBqItemByBqItemId(String itemId, String supplierId) {
		try {
			final Query query = getEntityManager().createQuery("select rb from RfaSupplierBqItem rb inner join rb.bqItem as bi left outer join rb.supplier as sup where bi.id = :itemId and sup.id = :supplierId");
			query.setParameter("itemId", itemId);
			query.setParameter("supplierId", supplierId);
			List<RfaSupplierBqItem> uList = query.getResultList();
			if (CollectionUtil.isNotEmpty(uList)) {
				return uList.get(0);
			} else {
				return null;
			}
		} catch (NoResultException nr) {
			LOG.info("Error while getting SupplierBQ Items : " + nr.getMessage(), nr);
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public RfaSupplierBqItem getBqItemByRfaBqItemId(String rftBqItemId, String supplierId) {
		try {
			final Query query = getEntityManager().createQuery("select rb from RfaSupplierBqItem rb left outer join fetch rb.children c where rb.bqItem.id =:itemId and rb.supplier.id= :supplierId");
			query.setParameter("itemId", rftBqItemId);
			query.setParameter("supplierId", supplierId);
			List<RfaSupplierBqItem> uList = query.getResultList();
			if (CollectionUtil.isNotEmpty(uList)) {
				return uList.get(0);
			} else {
				return null;
			}
		} catch (NoResultException nr) {
			LOG.info("Error while getting SupplierBQ Items : " + nr.getMessage(), nr);
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<RfaSupplierBqItem> findBqItemListByBqIdAndSupplierId(String bqId, String supplierId) {
		final Query query = getEntityManager().createQuery("select distinct a from RfaSupplierBqItem a inner join fetch a.supplier as s inner join fetch a.supplierBq as b inner join fetch b.bq bq left outer join fetch a.children as ch where s.id= :supplierId and b.id= :bqId  order by a.level, a.order");
		query.setParameter("bqId", bqId);
		query.setParameter("supplierId", supplierId);
		return query.getResultList();
	}

	@Override
	public PricingTypes getPriceTypeByBqItemId(String id) {
		LOG.info(" Bq Id : " + id);
		final Query query = getEntityManager().createQuery("select rb from RfaSupplierBqItem rb where rb.id = :itemId");
		query.setParameter("itemId", id);
		RfaSupplierBqItem q = (RfaSupplierBqItem) query.getSingleResult();
		if (q != null) {
			return q.getPriceType();
		} else {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<String> findGrandTotalsForEvaluationView(String bqId, String eventId, List<Supplier> suppliers) {
		List<String> data = new ArrayList<String>();
		StringBuilder hsql = new StringBuilder("select b.grandTotal, b.totalAfterTax, s.companyName, b.event.decimal, s.id, b.remark from RfaSupplierBq b inner join b.bq bq inner join b.supplier s where  b.supplier.id = s.id and bq.id = :bqId and b.event.id = :eventId and s in (:suppliers) order by s.companyName");

		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("bqId", bqId);
		query.setParameter("eventId", eventId);
		query.setParameter("suppliers", suppliers);
		List<Object[]> list = query.getResultList();
		for (Object[] values : list) {
			LOG.info("values[0] : " + values[0] + " values[2] : " + values[2] + "values[3] : " + values[3] + " values[4] : " + values[4] + "values[5] : " + values[5]);
			data.add(values[0] != null && values[1] != null ? String.valueOf(values[0] + "-" + values[1] + "-" + bqId + "-" + values[3] + "-" + values[4] + "-" + StringUtils.checkString((String) values[5])) : "");
		}

		return data;
	}

	@Override
	public int countIncompleteBqItemByBqIdsForSupplier(String bqId, String supplierId) {
		final Query query = getEntityManager().createQuery("select count(*) from RfaSupplierBqItem a inner join a.supplier as s inner join a.supplierBq as b inner join b.bq bq where s.id = :supplierId and bq.id = :bqId and a.level <> 0 and (a.totalAmountWithTax = 0 or a.totalAmountWithTax is null)");
		query.setParameter("bqId", bqId);
		query.setParameter("supplierId", supplierId);
		return ((Number) query.getSingleResult()).intValue();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<RfaSupplierBqItem> findSupplierBqItemListByBqIdAndSupplierIds(String bqId, String supplierIds) {
		final Query query = getEntityManager().createQuery("select distinct a from RfaSupplierBqItem a inner join fetch a.supplier as s inner join fetch a.supplierBq as b inner join fetch b.bq bq left outer join fetch a.children as ch where s.id =:supplierId and bq.id= :bqId order by a.level, a.order");
		query.setParameter("bqId", bqId);
		query.setParameter("supplierId", supplierIds);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<RfaSupplierBqItem> findBqItemListAndBqListByBqIdAndSupplierId(String bqId, String supplierId) {
		final Query query = getEntityManager().createQuery("select distinct a from RfaSupplierBqItem a inner join fetch a.supplier as s inner join fetch a.supplierBq as b inner join fetch b.bq bq where s.id = :supplierId and bq.id = :bqId and a.parent is not null order by a.level, a.order");
		query.setParameter("bqId", bqId);
		query.setParameter("supplierId", supplierId);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public RfaSupplierBqItem getSupplierBqItemByEventBqItemIdAndSupplierId(String itemId, String supplierId) {
		try {
			final Query query = getEntityManager().createQuery("select rb from RfaSupplierBqItem rb where rb.bqItem.id =:itemId and rb.supplier.id= :supplierId");
			query.setParameter("itemId", itemId);
			query.setParameter("supplierId", supplierId);
			List<RfaSupplierBqItem> uList = query.getResultList();
			if (CollectionUtil.isNotEmpty(uList)) {
				return uList.get(0);
			} else {
				return null;
			}
		} catch (NoResultException nr) {
			return null;
		}
	}

}
