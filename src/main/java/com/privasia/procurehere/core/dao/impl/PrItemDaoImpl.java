package com.privasia.procurehere.core.dao.impl;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.privasia.procurehere.core.dao.PrItemDao;
import com.privasia.procurehere.core.entity.PrItem;
import com.privasia.procurehere.core.exceptions.NotAllowedException;
import com.privasia.procurehere.core.pojo.SpentAnalysisPojo;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.SecurityLibrary;
import com.privasia.procurehere.core.utils.StringUtils;

/**
 * @author parveen
 */
@Repository
public class PrItemDaoImpl extends GenericDaoImpl<PrItem, String> implements PrItemDao {

	private static final Logger LOG = LogManager.getLogger(Global.PR_LOG);

	@Override
	public boolean isExists(PrItem prItem) {
		LOG.info("PRITEM ID :" + prItem.getId());
		String hql = "select count(*) from PrItem pi where pi.pr.id = :prId and upper(pi.itemName) = :itemName";
		if (StringUtils.checkString(prItem.getId()).length() > 0) {
			hql += " and pi.id <> :id";
		}
		hql += " and pi.parent is null";
		final Query query = getEntityManager().createQuery(hql);
		query.setParameter("itemName", prItem.getItemName().toUpperCase());
		query.setParameter("prId", prItem.getPr().getId());
		if (StringUtils.checkString(prItem.getId()).length() > 0) {
			query.setParameter("id", prItem.getId());
		}
		LOG.info("hql :" + hql);
		return ((Number) query.getSingleResult()).intValue() > 0;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<PrItem> getAllPrItemByPrId(String prId) {
		StringBuilder hsql = new StringBuilder("select distinct pi from PrItem pi left outer join fetch pi.unit left outer join fetch pi.product pd left outer join fetch pd.uom  left outer join fetch pi.pr p where p.id = :prId order by pi.level, pi.order");
		final Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("prId", prId);
		List<PrItem> prList = query.getResultList();
		return prList;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<PrItem> getPrItemLevelOrder(String prId) {
		final Query query = getEntityManager().createQuery("select distinct a from PrItem a inner join fetch a.pr c where a.parent is null and c.id = :id order by a.level, a.order");
		query.setParameter("id", prId);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public PrItem getPrItembyPrIdAndPrItemId(String prId, String prItemId) {
		final Query query = getEntityManager().createQuery("select distinct a from PrItem a left outer join fetch a.productCategory pcc left outer join fetch pcc.createdBy  cb left outer join fetch cb.buyer left outer join fetch a.unit uomm inner join fetch a.pr c left outer join fetch a.product p  left outer join fetch a.productContractItem pci left outer join fetch p.uom where c.id = :prId and a.id = :prItemId");
		query.setParameter("prId", prId);
		query.setParameter("prItemId", prItemId);
		List<PrItem> uList = query.getResultList();
		if (CollectionUtil.isNotEmpty(uList)) {
			return uList.get(0);
		} else {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW, rollbackFor = { Exception.class })
	public String deletePrItems(String[] prItemIds, String prId) {

		for (String id : prItemIds) {
			Query query = getEntityManager().createQuery("select a.level, a.order, p.id from PrItem a left outer join a.parent p where a.id = :id");
			query.setParameter("id", id);
			List<Object[]> result = query.getResultList();
			if (CollectionUtil.isNotEmpty(result) && result.get(0).length > 1) {
				int level = ((Number) result.get(0)[0]).intValue();
				int order = ((Number) result.get(0)[1]).intValue();
				String parentId = ((String) result.get(0)[2]);
				LOG.info("Reordering Level :" + level + " order :" + order + " parentId : " + parentId);

				// If Parent
				if (StringUtils.checkString(parentId).length() == 0) {
					StringBuilder hql = new StringBuilder("update PrItem i set i.level = (i.level - 1) where i.level > :level and i.pr.id = :prId");
					query = getEntityManager().createQuery(hql.toString());
					query.setParameter("prId", prId);
					query.setParameter("level", level);
					query.executeUpdate();
				} else {
					// If Child
					StringBuilder hql = new StringBuilder("update PrItem i set i.order = (i.order - 1) where i.level = :level and i.order >= :order and i.parent.id = :parentId and i.pr.id = :prId");
					query = getEntityManager().createQuery(hql.toString());
					query.setParameter("prId", prId);
					query.setParameter("level", level);
					query.setParameter("order", order);
					query.setParameter("parentId", parentId);
					query.executeUpdate();
				}
			}
		}

		LOG.info("Going to Delete the selected Pr Items");
		Query query = getEntityManager().createQuery("delete from PrItem item where item.id in (:id) and  item.parent is not null and item.pr.id = :prId");
		query.setParameter("id", Arrays.asList(prItemIds));
		query.setParameter("prId", prId);
		query.executeUpdate();

		query = getEntityManager().createQuery("delete from PrItem item where item.id in (:id) and item.parent is null and item.pr.id = :prId");
		query.setParameter("id", Arrays.asList(prItemIds));
		query.setParameter("prId", prId);
		query.executeUpdate();
		LOG.info("COMPLETED TO DELETE the Selected pr ITems");

		String updateResult = updateOnDeletePrItems(prId);
		LOG.info("updateResult :" + updateResult);
		return prId;
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW, rollbackFor = { Exception.class })
	public String deletePrItemsByPrId(String prId) {

		/*
		 * for (String id : prItemIds) { Query query = getEntityManager().
		 * createQuery("select a.level, a.order, p.id from PrItem a left outer join a.parent p where a.id = :id");
		 * query.setParameter("id", id); List<Object[]> result = query.getResultList(); if
		 * (CollectionUtil.isNotEmpty(result) && result.get(0).length > 1) { int level = ((Number)
		 * result.get(0)[0]).intValue(); int order = ((Number) result.get(0)[1]).intValue(); String parentId = ((String)
		 * result.get(0)[2]); LOG.info("Reordering Level :" + level + " order :" + order + " parentId : " + parentId);
		 * // If Parent if (StringUtils.checkString(parentId).length() == 0) { StringBuilder hql = new
		 * StringBuilder("update PrItem i set i.level = (i.level - 1) where i.level > :level and i.pr.id = :prId");
		 * query = getEntityManager().createQuery(hql.toString()); query.setParameter("prId", prId);
		 * query.setParameter("level", level); query.executeUpdate(); } else { // If Child StringBuilder hql = new
		 * StringBuilder("update PrItem i set i.order = (i.order - 1) where i.level = :level and i.order >= :order and i.parent.id = :parentId and i.pr.id = :prId"
		 * ); query = getEntityManager().createQuery(hql.toString()); query.setParameter("prId", prId);
		 * query.setParameter("level", level); query.setParameter("order", order); query.setParameter("parentId",
		 * parentId); query.executeUpdate(); } }
		 */

		LOG.info("Going to Delete the selected Pr Items");
		Query query = getEntityManager().createQuery("delete from PrItem item where  item.parent is not null and item.pr.id = :prId");
		// query.setParameter("id", Arrays.asList(prItemIds));
		query.setParameter("prId", prId);

		query.executeUpdate();

		Query query1 = getEntityManager().createQuery("delete from PrItem item where  item.parent is null and item.pr.id = :prId");
		// query.setParameter("id", Arrays.asList(prItemIds));
		query1.setParameter("prId", prId);
		query1.executeUpdate();

		LOG.info("COMPLETED TO DELETE the Selected pr ITems");

		// String updateResult = updateOnDeletePrItems(prId);
		// LOG.info("updateResult :" + updateResult);
		return prId;
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW, rollbackFor = { Exception.class })
	public String updateOnDeletePrItems(String prId) {
		Query query = getEntityManager().createQuery("update Pr p set p.total =case when (select sum(b.totalAmountWithTax) from PrItem b where b.pr.id = p.id) is null then 0 else (select sum(b.totalAmountWithTax) from PrItem b where b.pr.id = p.id) end where p.id= :prId");
		query.setParameter("prId", prId);
		int result1 = query.executeUpdate();

		query = getEntityManager().createQuery("update Pr p set p.grandTotal = (p.total + p.additionalTax) where p.id= :prId");
		query.setParameter("prId", prId);
		int result2 = query.executeUpdate();

		return result1 + "==" + result2;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<PrItem> getPrItemsbyId(String prId) {
		final Query query = getEntityManager().createQuery("select distinct a from PrItem a inner join fetch a.pr pr left outer join fetch a.children c where pr.id =:id order by a.level, a.order");
		query.setParameter("id", prId);
		return query.getResultList();
	}

	@Override
	@SuppressWarnings("unchecked")
	public PrItem getPrItembyPrItemId(String itemId) {
		try {
			final Query query = getEntityManager().createQuery("select distinct a from PrItem a inner join fetch a.pr c left outer join fetch a.children left outer join fetch a.parent ch where a.id = :id");
			query.setParameter("id", itemId);
			List<PrItem> uList = query.getResultList();
			if (CollectionUtil.isNotEmpty(uList)) {
				LOG.info(" success ");
				return uList.get(0);
			} else {
				return null;
			}
		} catch (NoResultException nr) {
			LOG.info("Error while getting pr Items : " + nr.getMessage(), nr);
			return null;
		}
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void updateItemOrder(String prId, PrItem prItem, String oldParent, String newParent, int oldOrder, int newOrder, int oldLevel, int newLevel) {
		LOG.info("UPdateing ITEM ORDERE ==> oldParent " + oldParent + " newParent " + newParent + " oldOrder " + oldOrder + " newOrder " + newOrder + " oldLevel " + oldLevel + " newLevel " + newLevel);
		// Top Level
		// Rearrange its new place - Push down
		if (newOrder == 0) {
			LOG.info("Enter 1 :: prId :: " + prId + " newLevel :: " + newLevel);
			StringBuilder hql = new StringBuilder("update PrItem i set i.level = (i.level + 1) where i.pr.id = :prId ");

			if (newLevel > oldLevel && oldOrder == 0) {
				hql.append(" and i.level > :level");
			} else {
				hql.append(" and i.level >= :level");
			}

			Query query = getEntityManager().createQuery(hql.toString());
			query.setParameter("prId", prId);
			query.setParameter("level", newLevel);
			query.executeUpdate();
		}
		// Top Level
		// Rearrange its new place - Pull up
		if (oldOrder == 0) {
			LOG.info("Enter 2 :: prId :: " + prId + " oldLevel :: " + oldLevel);
			StringBuilder hql = new StringBuilder("update PrItem i set i.level = (i.level - 1) where i.level > :level and i.pr.id = :prId");
			Query query = getEntityManager().createQuery(hql.toString());
			query.setParameter("prId", prId);
			query.setParameter("level", oldLevel);
			query.executeUpdate();
		}
		// Child Level
		// Push down
		if (newParent != null) {
			LOG.info("Enter 3 :: bqId :: " + prId + " newParent :: " + newParent + " newOrder " + newOrder);
			StringBuilder hql = new StringBuilder("update PrItem i set i.order = (i.order + 1) where i.parent.id = :parent and i.order >= :order and i.pr.id = :prId");
			if (newOrder > oldOrder && oldParent.equals(newParent)) {
				hql.append(" and i.order > :order");
			} else {
				hql.append("  and i.order >= :order ");
			}
			// Rearrange its new place
			Query query = getEntityManager().createQuery(hql.toString());
			query.setParameter("prId", prId);
			query.setParameter("parent", newParent);
			query.setParameter("order", newOrder);
			query.executeUpdate();
		}
		// Child Level
		// Pull Up
		if (oldParent != null) {
			LOG.info("Enter 4 :: prId :: " + prId + " oldParent :: " + oldParent + " oldOrder " + oldOrder);
			StringBuilder hql = new StringBuilder("update PrItem i set i.order = (i.order - 1) where i.parent.id = :parent and i.order > :order and i.pr.id = :prId");
			Query query = getEntityManager().createQuery(hql.toString());
			query.setParameter("prId", prId);
			query.setParameter("parent", oldParent);
			query.setParameter("order", oldOrder);
			query.executeUpdate();
		}

		// Fetch the parent object again as its position might have changed during above updates.
		if (newParent != null) {
			PrItem newDbParent = getPrItembyPrItemId(newParent);
			LOG.info("newDbParent.getLevel() :" + newDbParent.getLevel());
			prItem.setLevel(newDbParent.getLevel());
		}

		LOG.info("Updating object : " + prItem.toLogString());

		StringBuilder hql = new StringBuilder("update PrItem i set i.order = :order, i.level = :level, i.parent = :newParent where i.id = :prItemId and i.pr.id = :prId");
		Query query = getEntityManager().createQuery(hql.toString());
		query.setParameter("prId", prId);
		query.setParameter("prItemId", prItem.getId());
		query.setParameter("newParent", prItem.getParent());
		query.setParameter("order", prItem.getOrder());
		query.setParameter("level", prItem.getLevel());
		query.executeUpdate();
		LOG.info("Updated successfully");
	}

	@Override
	public void deleteNewFieldPr(String label, String prId) {
		try {
			LOG.info("Pr ID :: " + prId + " label :: " + label);
			StringBuilder hql = new StringBuilder("update PrItem i set i." + label + " = null where i.pr.id = :prId");
			Query query = getEntityManager().createQuery(hql.toString());
			query.setParameter("prId", prId);
			query.executeUpdate();
			LOG.info("pr items columns set deleted succesfully...");

			// delete additional column
			hql = new StringBuilder("update Pr p set p." + label + "Label = null where p.id = :prId");
			query = getEntityManager().createQuery(hql.toString());
			query.setParameter("prId", prId);
			query.executeUpdate();
			LOG.info("pr columns deleted succesfully...");
		} catch (NoResultException nr) {
			LOG.info("Error while deleting new Fields: " + nr.getMessage(), nr);
		}

	}

	@SuppressWarnings("unchecked")
	@Override
	public PrItem getParentbyLevelId(String prId, Integer level) {
		try {
			final Query query = getEntityManager().createQuery("from PrItem a inner join fetch a.pr as b where b.id = :prId and a.level = :level and a.order = 0 ");
			query.setParameter("prId", prId);
			query.setParameter("level", level);
			List<PrItem> uList = query.getResultList();
			if (CollectionUtil.isNotEmpty(uList)) {
				return uList.get(0);
			} else {
				return null;
			}
		} catch (NoResultException nr) {
			LOG.info("Error while getting PR Items : " + nr.getMessage(), nr);
			return null;
		}
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW, rollbackFor = { NotAllowedException.class, Exception.class })
	public void deletePrItemsbyPrid(String prId) {
		try {
			Query query = getEntityManager().createQuery("delete from PrItem item where item.pr.id = :id and item.parent is not null");
			query.setParameter("id", prId);
			query.executeUpdate();

			query = getEntityManager().createQuery("delete from PrItem item where item.pr.id = :id and item.parent is null");
			query.setParameter("id", prId);
			query.executeUpdate();

			StringBuilder hql = new StringBuilder("update Pr p set p.taxDescription = null, p.total = 0,p.additionalTax = 0,p.grandTotal = 0  where p.id = :prId");
			query = getEntityManager().createQuery(hql.toString());
			query.setParameter("prId", prId);
			query.executeUpdate();

		} catch (NoResultException nr) {
			LOG.info("Error while getting BQ Items : " + nr.getMessage(), nr);
		}

	}

	@SuppressWarnings("unchecked")
	@Override
	public List<PrItem> findAllChildPrItemByPrId(String prId) {
		final Query query = getEntityManager().createQuery("select distinct a from PrItem a inner join fetch a.pr c where a.parent is not null and c.id = :id order by a.level, a.order");
		query.setParameter("id", prId);
		return query.getResultList();
	}

	@Override
	public boolean checkProductInUse(String productId) {
		LOG.info("proDuctId :" + productId);
		final Query query = getEntityManager().createQuery("select count(pi) from PrItem pi join pi.product p where p.id = :productId");
		query.setParameter("productId", "productId");
		return ((Number) query.getSingleResult()).intValue() > 0;
	}

	@Override
	public Long findProductCategoryCountByPrId(String prId) {
		final Query query = getEntityManager().createQuery("select count(distinct a.productCategory.id) from PrItem a inner join a.pr c where a.productCategory is not null and c.id = :id ");
		query.setParameter("id", prId);
		Long count = query.getSingleResult() != null ? ((Number) query.getSingleResult()).longValue() : 0l;
		return count;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<SpentAnalysisPojo> findPrItemForSpentAnalysis(int month, int year) throws ParseException {

		Calendar cal = Calendar.getInstance();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		cal.set(Calendar.MONTH, 0);
		cal.set(Calendar.YEAR, year);
		cal.set(Calendar.DAY_OF_MONTH, 1);
		String startDate = formatter.format(cal.getTime()) + " 00:00:00";
		cal = Calendar.getInstance();
		cal.set(Calendar.MONTH, month);
		cal.set(Calendar.YEAR, year);
		int res = cal.getActualMaximum(Calendar.DATE);
		cal.set(Calendar.DAY_OF_MONTH, res);
		String endDate = formatter.format(cal.getTime()) + " 23:59:59";

		LOG.info("PR item by volume for start date : " + startDate + " and end date : " + endDate);

		final Query query = getEntityManager().createNativeQuery("SELECT ppc.PRODUCT_NAME, COUNT(*) FROM PROC_PO_ITEM ppi JOIN PROC_PO pp ON pp.ID = ppi.PO_ID JOIN PROC_PRODUCT_CATEGORY ppc ON ppc.ID = ppi.PRODUCT_CATEGORY_ID WHERE pp.PO_CREATED_DATE BETWEEN TO_DATE(:startDate , 'YYYY-MM-DD hh24:mi:ss') AND TO_DATE(:endDate , 'YYYY-MM-DD hh24:mi:ss') AND pp.BUYER_ID =:buyerId GROUP BY ppc.PRODUCT_NAME ORDER BY COUNT(*) DESC");
		query.setParameter("buyerId", SecurityLibrary.getLoggedInUser().getBuyer().getId());
		query.setParameter("startDate", startDate);
		query.setParameter("endDate", endDate);
		query.setMaxResults(10);

		List<SpentAnalysisPojo> dataList = new ArrayList<>();

		List<Object[]> records = query.getResultList();

		for (Object[] result : records) {
			SpentAnalysisPojo data = new SpentAnalysisPojo();
			data.setName((String) result[0]);
			data.setValue(new BigDecimal(result[1].toString()));
			data.setStartDate(new SimpleDateFormat("MMMM YYYY").format((new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(startDate))));
			data.setEndDate(new SimpleDateFormat("MMMM yyyy").format((new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(endDate))));
			dataList.add(data);
		}

		return dataList;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<SpentAnalysisPojo> findPrItemValueForSpentAnalysis(int month, int year) throws ParseException {

		Calendar cal = Calendar.getInstance();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		cal.set(Calendar.MONTH, 0);
		cal.set(Calendar.YEAR, year);
		cal.set(Calendar.DAY_OF_MONTH, 1);
		String startDate = formatter.format(cal.getTime()) + " 00:00:00";
		cal = Calendar.getInstance();
		cal.set(Calendar.MONTH, month);
		cal.set(Calendar.YEAR, year);
		int res = cal.getActualMaximum(Calendar.DATE);
		cal.set(Calendar.DAY_OF_MONTH, res);
		String endDate = formatter.format(cal.getTime()) + " 23:59:59";

		LOG.info("PR item by value for start date : " + startDate + " and end date : " + endDate);

		final Query query = getEntityManager().createNativeQuery("SELECT ppc.PRODUCT_NAME, SUM(ppi.TOTAL_AMT_WITH_TAX) FROM PROC_PO_ITEM ppi JOIN PROC_PO pp ON pp.ID = ppi.PO_ID JOIN PROC_PRODUCT_CATEGORY ppc ON ppc.ID = ppi.PRODUCT_CATEGORY_ID WHERE pp.PO_CREATED_DATE BETWEEN TO_DATE(:startDate , 'YYYY-MM-DD hh24:mi:ss') AND TO_DATE(:endDate , 'YYYY-MM-DD hh24:mi:ss') AND pp.BUYER_ID =:buyerId GROUP BY ppc.PRODUCT_NAME ORDER BY SUM(ppi.TOTAL_AMT_WITH_TAX) DESC");
		query.setParameter("buyerId", SecurityLibrary.getLoggedInUser().getBuyer().getId());
		query.setParameter("startDate", startDate);
		query.setParameter("endDate", endDate);
		query.setMaxResults(10);

		List<SpentAnalysisPojo> dataList = new ArrayList<>();

		List<Object[]> records = query.getResultList();

		for (Object[] result : records) {
			SpentAnalysisPojo data = new SpentAnalysisPojo();
			data.setName((String) result[0]);
			data.setValue((new BigDecimal(result[1].toString())));
			data.setStartDate(new SimpleDateFormat("MMMM YYYY").format((new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(startDate))));
			data.setEndDate(new SimpleDateFormat("MMMM yyyy").format((new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(endDate))));
			dataList.add(data);
		}

		return dataList;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<SpentAnalysisPojo> findPrItemForSpentAnalysisForSubsidiary(int month, int year) throws ParseException {

		Calendar cal = Calendar.getInstance();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		cal.set(Calendar.MONTH, 0);
		cal.set(Calendar.YEAR, year);
		cal.set(Calendar.DAY_OF_MONTH, 1);
		String startDate = formatter.format(cal.getTime()) + " 00:00:00";
		cal = Calendar.getInstance();
		cal.set(Calendar.MONTH, month);
		cal.set(Calendar.YEAR, year);
		int res = cal.getActualMaximum(Calendar.DATE);
		cal.set(Calendar.DAY_OF_MONTH, res);
		String endDate = formatter.format(cal.getTime()) + " 23:59:59";

		LOG.info("PR item by volume for subsidiary for start date : " + startDate + " and end date : " + endDate);

		final Query query = getEntityManager().createNativeQuery("SELECT ppc.PRODUCT_NAME, count(*) FROM PROC_PO_ITEM ppi JOIN PROC_PO pp ON pp.ID = ppi.PO_ID JOIN PROC_PRODUCT_CATEGORY ppc ON ppc.ID = ppi.PRODUCT_CATEGORY_ID JOIN PROC_FAVOURITE_SUPPLIER pfs ON pfs.FAV_SUPPLIER_ID = pp.PO_SUPPLIER_ID WHERE pp.PO_CREATED_DATE BETWEEN TO_DATE(:startDate , 'YYYY-MM-DD hh24:mi:ss') AND TO_DATE(:endDate , 'YYYY-MM-DD hh24:mi:ss') AND pfs.SUBSIDIARY =:subsidiary AND pp.BUYER_ID =:buyerId GROUP BY ppc.PRODUCT_NAME ORDER BY count(*) DESC");
		query.setParameter("buyerId", SecurityLibrary.getLoggedInUser().getBuyer().getId());
		query.setParameter("startDate", startDate);
		query.setParameter("endDate", endDate);
		query.setParameter("subsidiary", 1);
		query.setMaxResults(10);
		List<SpentAnalysisPojo> dataList = new ArrayList<>();

		List<Object[]> records = query.getResultList();

		for (Object[] result : records) {
			SpentAnalysisPojo data = new SpentAnalysisPojo();
			data.setName((String) result[0]);
			data.setValue(new BigDecimal(result[1].toString()));
			data.setStartDate(new SimpleDateFormat("MMMM YYYY").format((new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(startDate))));
			data.setEndDate(new SimpleDateFormat("MMMM yyyy").format((new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(endDate))));
			dataList.add(data);
		}

		return dataList;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<SpentAnalysisPojo> findPrItemForSpentAnalysisForNonSubsidiary(int month, int year) throws ParseException {

		Calendar cal = Calendar.getInstance();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		cal.set(Calendar.MONTH, 0);
		cal.set(Calendar.YEAR, year);
		cal.set(Calendar.DAY_OF_MONTH, 1);
		String startDate = formatter.format(cal.getTime()) + " 00:00:00";
		cal = Calendar.getInstance();
		cal.set(Calendar.MONTH, month);
		cal.set(Calendar.YEAR, year);
		int res = cal.getActualMaximum(Calendar.DATE);
		cal.set(Calendar.DAY_OF_MONTH, res);
		String endDate = formatter.format(cal.getTime()) + " 23:59:59";

		LOG.info("PR item by volume for non-subsidiary for start date : " + startDate + " and end date : " + endDate);

		final Query query = getEntityManager().createNativeQuery("SELECT ppc.PRODUCT_NAME, count(*) FROM PROC_PO_ITEM ppi JOIN PROC_PO pp ON pp.ID = ppi.PO_ID JOIN PROC_PRODUCT_CATEGORY ppc ON ppc.ID = ppi.PRODUCT_CATEGORY_ID LEFT OUTER JOIN PROC_FAVOURITE_SUPPLIER pfs ON pfs.FAV_SUPPLIER_ID = pp.PO_SUPPLIER_ID WHERE pp.PO_CREATED_DATE BETWEEN TO_DATE(:startDate , 'YYYY-MM-DD hh24:mi:ss') AND TO_DATE(:endDate , 'YYYY-MM-DD hh24:mi:ss') AND (pfs.SUBSIDIARY =:subsidiary or pfs.SUBSIDIARY is null) AND pp.BUYER_ID =:buyerId GROUP BY ppc.PRODUCT_NAME ORDER BY count(*) DESC");
		query.setParameter("buyerId", SecurityLibrary.getLoggedInUser().getBuyer().getId());
		query.setParameter("startDate", startDate);
		query.setParameter("endDate", endDate);
		query.setParameter("subsidiary", 0);
		query.setMaxResults(10);
		List<SpentAnalysisPojo> dataList = new ArrayList<>();

		List<Object[]> records = query.getResultList();

		for (Object[] result : records) {
			SpentAnalysisPojo data = new SpentAnalysisPojo();
			data.setName((String) result[0]);
			data.setValue(new BigDecimal(result[1].toString()));
			data.setStartDate(new SimpleDateFormat("MMMM YYYY").format((new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(startDate))));
			data.setEndDate(new SimpleDateFormat("MMMM yyyy").format((new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(endDate))));
			dataList.add(data);
		}

		return dataList;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<SpentAnalysisPojo> findPrItemValueForSpentAnalysisForSubsidiary(int month, int year) throws ParseException {

		Calendar cal = Calendar.getInstance();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		cal.set(Calendar.MONTH, 0);
		cal.set(Calendar.YEAR, year);
		cal.set(Calendar.DAY_OF_MONTH, 1);
		String startDate = formatter.format(cal.getTime()) + " 00:00:00";
		cal = Calendar.getInstance();
		cal.set(Calendar.MONTH, month);
		cal.set(Calendar.YEAR, year);
		int res = cal.getActualMaximum(Calendar.DATE);
		cal.set(Calendar.DAY_OF_MONTH, res);
		String endDate = formatter.format(cal.getTime()) + " 23:59:59";

		LOG.info("PR item by value for subsidiary for start date : " + startDate + " and end date : " + endDate);

		final Query query = getEntityManager().createNativeQuery("SELECT ppc.PRODUCT_NAME, SUM(ppi.TOTAL_AMT_WITH_TAX) FROM PROC_PO_ITEM ppi JOIN PROC_PO pp ON pp.ID = ppi.PO_ID JOIN PROC_FAVOURITE_SUPPLIER pfs ON pfs.FAV_SUPPLIER_ID = pp.PO_SUPPLIER_ID JOIN PROC_PRODUCT_CATEGORY ppc ON ppc.ID = ppi.PRODUCT_CATEGORY_ID WHERE pp.PO_CREATED_DATE BETWEEN TO_DATE(:startDate , 'YYYY-MM-DD hh24:mi:ss') AND TO_DATE(:endDate , 'YYYY-MM-DD hh24:mi:ss') AND pp.BUYER_ID =:buyerId AND pfs.SUBSIDIARY =:subsidiary GROUP BY ppc.PRODUCT_NAME ORDER BY SUM(ppi.TOTAL_AMT_WITH_TAX) DESC");
		query.setParameter("buyerId", SecurityLibrary.getLoggedInUser().getBuyer().getId());
		query.setParameter("startDate", startDate);
		query.setParameter("endDate", endDate);
		query.setParameter("subsidiary", 1);
		query.setMaxResults(10);
		List<SpentAnalysisPojo> dataList = new ArrayList<>();

		List<Object[]> records = query.getResultList();

		for (Object[] result : records) {
			SpentAnalysisPojo data = new SpentAnalysisPojo();
			data.setName((String) result[0]);
			data.setValue((new BigDecimal(result[1].toString())));
			data.setStartDate(new SimpleDateFormat("MMMM YYYY").format((new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(startDate))));
			data.setEndDate(new SimpleDateFormat("MMMM yyyy").format((new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(endDate))));
			dataList.add(data);
		}

		return dataList;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<SpentAnalysisPojo> findPrItemValueForSpentAnalysisForNonSubsidiary(int month, int year) throws ParseException {

		Calendar cal = Calendar.getInstance();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		cal.set(Calendar.MONTH, 0);
		cal.set(Calendar.YEAR, year);
		cal.set(Calendar.DAY_OF_MONTH, 1);
		String startDate = formatter.format(cal.getTime()) + " 00:00:00";
		cal = Calendar.getInstance();
		cal.set(Calendar.MONTH, month);
		cal.set(Calendar.YEAR, year);
		int res = cal.getActualMaximum(Calendar.DATE);
		cal.set(Calendar.DAY_OF_MONTH, res);
		String endDate = formatter.format(cal.getTime()) + " 23:59:59";

		LOG.info("PR item by value for non-subsidiary for start date : " + startDate + " and end date : " + endDate);

		final Query query = getEntityManager().createNativeQuery("SELECT ppc.PRODUCT_NAME, SUM(ppi.TOTAL_AMT_WITH_TAX) FROM PROC_PO_ITEM ppi JOIN PROC_PO pp ON pp.ID = ppi.PO_ID LEFT OUTER JOIN PROC_FAVOURITE_SUPPLIER pfs ON pfs.FAV_SUPPLIER_ID = pp.PO_SUPPLIER_ID JOIN PROC_PRODUCT_CATEGORY ppc ON ppc.ID = ppi.PRODUCT_CATEGORY_ID WHERE pp.PO_CREATED_DATE BETWEEN TO_DATE(:startDate , 'YYYY-MM-DD hh24:mi:ss') AND TO_DATE(:endDate , 'YYYY-MM-DD hh24:mi:ss') AND pp.BUYER_ID =:buyerId AND (pfs.SUBSIDIARY =:subsidiary or pfs.SUBSIDIARY is null) GROUP BY ppc.PRODUCT_NAME ORDER BY SUM(ppi.TOTAL_AMT_WITH_TAX) DESC");
		query.setParameter("buyerId", SecurityLibrary.getLoggedInUser().getBuyer().getId());
		query.setParameter("startDate", startDate);
		query.setParameter("endDate", endDate);
		query.setParameter("subsidiary", 0);
		query.setMaxResults(10);
		List<SpentAnalysisPojo> dataList = new ArrayList<>();

		List<Object[]> records = query.getResultList();

		for (Object[] result : records) {
			SpentAnalysisPojo data = new SpentAnalysisPojo();
			data.setName((String) result[0]);
			data.setValue((new BigDecimal(result[1].toString())));
			data.setStartDate(new SimpleDateFormat("MMMM YYYY").format((new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(startDate))));
			data.setEndDate(new SimpleDateFormat("MMMM yyyy").format((new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(endDate))));
			dataList.add(data);
		}

		return dataList;
	}
}
