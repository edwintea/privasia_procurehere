package com.privasia.procurehere.core.dao.impl;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.privasia.procurehere.core.dao.PoItemDao;
import com.privasia.procurehere.core.entity.PoItem;
import com.privasia.procurehere.core.entity.PrItem;
import com.privasia.procurehere.core.enums.PoStatus;
import com.privasia.procurehere.core.exceptions.ApplicationException;
import com.privasia.procurehere.core.exceptions.NotAllowedException;
import com.privasia.procurehere.core.pojo.PoItemPojo;
import com.privasia.procurehere.core.pojo.PoItemSupplierPojo;
import com.privasia.procurehere.core.pojo.SearchFilterPoPojo;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.StringUtils;

/**
 * @author parveen
 */
@Repository
public class PoItemDaoImpl extends GenericDaoImpl<PoItem, String> implements PoItemDao {

	private static final Logger LOG = LogManager.getLogger(Global.PR_LOG);



	@SuppressWarnings("unchecked")
	@Override
	public List<PoItem> getAllPoItemByPoId(String poId) {
		StringBuilder hsql = new StringBuilder("select distinct pi from PoItem pi left outer join fetch pi.unit left outer join fetch pi.product pd left outer join fetch pd.uom  left outer join fetch pi.po p where p.id = :poId order by pi.level, pi.order");
		final Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("poId", poId);
		List<PoItem> poList = query.getResultList();
		return poList;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<PoItem> getAllPoItemByPoIdPojo(String poId) {
		StringBuilder hsql = new StringBuilder("select distinct pi from PoItem pi left outer join fetch pi.unit left outer join fetch pi.product pd left outer join fetch pd.uom left outer join fetch pi.po p where p.id = :poId order by pi.level, pi.order");
		final Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("poId", poId);
		List<PoItem> poList = query.getResultList();

		// Initialize the proxy for each PoItem
		for (PoItem poItem : poList) {
			Hibernate.initialize(poItem.getPo().getPr());
			Hibernate.initialize(poItem.getPo().getCurrency());
			Hibernate.initialize(poItem.getPo().getBusinessUnit());
		}

		return poList;
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

	@Override
	public void deletePoItemsbyId(String poId) {
		try {
			Query query = getEntityManager().createQuery("delete from PoItem item where item.po.id = :id and item.parent is not null");
			query.setParameter("id", poId);
			query.executeUpdate();

			query = getEntityManager().createQuery("delete from PoItem item where item.po.id = :id and item.parent is null ");
			query.setParameter("id", poId);
			query.executeUpdate();

		} catch (NoResultException nr) {
			LOG.info("Error while deleting PO Items : " + nr.getMessage(), nr);
		}

	}

	@SuppressWarnings("unchecked")
	@Override
	public PoItem getPoItembyPoIdAndPoItemId(String poId, String poItemId) {
		final Query query = getEntityManager().createQuery("select distinct a from PoItem a left outer join fetch a.productCategory pcc left outer join fetch pcc.createdBy cb left outer join fetch cb.buyer left outer join fetch a.unit uomm inner join fetch a.po c left outer join fetch a.product p left outer join fetch a.productContractItem pci left outer join fetch p.uom where c.id = :poId and a.id = :poItemId");
		query.setParameter("poId", poId);
		query.setParameter("poItemId", poItemId);
		List<PoItem> uList = query.getResultList();
		if (CollectionUtil.isNotEmpty(uList)) {
			return uList.get(0);
		} else {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<PoItem> getPoItemListByPoId(String poId) {
		StringBuilder hsql = new StringBuilder("select distinct pi from PoItem pi left outer join fetch pi.unit left outer join fetch pi.children left outer join fetch pi.product pd left outer join fetch pd.uom left outer join fetch pi.po p where p.id = :poId order by pi.level, pi.order");
		final Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("poId", poId);
		List<PoItem> prList = query.getResultList();
		return prList;
	}

	@SuppressWarnings("unchecked")
	@Override
	@Transactional
	public String deletePoItems(String poItemId, String poId) throws ApplicationException {
		Query cQuery = getEntityManager().createQuery("select count(item) from PoItem item where item.parent is not null and item.po.id = :poId");
		cQuery.setParameter("poId", poId);
		int count = ((Number) cQuery.getSingleResult()).intValue();
		if (count == 1) {
			throw new ApplicationException("PO should consist at least one item");
		}
		int countt = 0;
		int level = 0;
		int order = 0;
		String parentId = "";

		Query query = getEntityManager().createQuery("select a.level, a.order, p.id from PoItem a left outer join a.parent p where a.id = :poItemId");
		query.setParameter("poItemId", poItemId);
		List<Object[]> result = query.getResultList();
		// if (CollectionUtil.isNotEmpty(result) && result.get(0).length > 1) {
		level = ((Number) result.get(0)[0]).intValue();
		order = ((Number) result.get(0)[1]).intValue();
		parentId = ((String) result.get(0)[2]);
		LOG.info("Reordering Level :" + level + " order :" + order + " parentId : " + parentId);

		Query ccQuery = getEntityManager().createQuery("select count(item) from PoItem item where item.po.id = :poId and item.parent.id = :parentId ");
		ccQuery.setParameter("poId", poId);
		ccQuery.setParameter("parentId", parentId);
		countt = ((Number) ccQuery.getSingleResult()).intValue();

		// If Parent
		if (StringUtils.checkString(parentId).length() == 0) {
			StringBuilder hql = new StringBuilder("update PoItem i set i.level = (i.level - 1) where i.level > :level and i.po.id = :poId");
			query = getEntityManager().createQuery(hql.toString());
			query.setParameter("poId", poId);
			query.setParameter("level", level);
			query.executeUpdate();
		} else {
			// If Child
			StringBuilder hql = new StringBuilder("update PoItem i set i.order = (i.order - 1) where i.level = :level and i.order >= :order and i.parent.id = :parentId and i.po.id = :poId");
			query = getEntityManager().createQuery(hql.toString());
			query.setParameter("poId", poId);
			query.setParameter("level", level);
			query.setParameter("order", order);
			query.setParameter("parentId", parentId);
			query.executeUpdate();
		}
		// }

		LOG.info("Going to Delete the PO Item");
		Query query1 = getEntityManager().createQuery("delete from PoItem item where item.id = :id and item.parent is not null and item.po.id = :poId");
		query1.setParameter("id", poItemId);
		query1.setParameter("poId", poId);
		query1.executeUpdate();

		if (countt == 1) {
			LOG.info("Deleting parent");
			StringBuilder hql = new StringBuilder("update PoItem i set i.level = (i.level - 1) where i.level > :level and i.po.id = :poId");
			query = getEntityManager().createQuery(hql.toString());
			query.setParameter("poId", poId);
			query.setParameter("level", level);
			query.executeUpdate();

			query1 = getEntityManager().createQuery("delete from PoItem item where item.id =:parentId and item.po.id = :poId");
			query1.setParameter("parentId", parentId);
			query1.setParameter("poId", poId);
			query1.executeUpdate();

		}
		LOG.info("COMPLETED TO DELETE the po ITem");

		String updateResult = updateOnDeletePoItem(poId);
		LOG.info("updateResult :" + updateResult);

		return poId;
	}

	@Override
	public String updateOnDeletePoItem(String poId) {
		Query query = getEntityManager().createQuery("update Po p set p.total = case when (select sum(b.totalAmountWithTax) from PoItem b where b.po.id = p.id) is null then 0 else (select sum(b.totalAmountWithTax) from PoItem b where b.po.id = p.id) end where p.id= :poId");
		query.setParameter("poId", poId);
		int result1 = query.executeUpdate();

		query = getEntityManager().createQuery("update Po p set p.grandTotal = (p.total + p.additionalTax) where p.id= :poId");
		query.setParameter("poId", poId);
		int result2 = query.executeUpdate();

		return result1 + "==" + result2;
	}

	@Override
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW, rollbackFor = { Exception.class })
	public String deletePoItems(String[] poItemIds, String poId) {

		Query query = getEntityManager().createQuery("select distinct a.parent.id from PoItem a  where a.id in (:id) ");
		query.setParameter("id", Arrays.asList(poItemIds));
		List<String> parentIds = query.getResultList();
		LOG.info(" Parent Ids : " + String.join(",", parentIds));

		for (String id : poItemIds) {
			query = getEntityManager().createQuery("select a.level, a.order, p.id from PoItem a left outer join a.parent p where a.id = :id");
			query.setParameter("id", id);
			List<Object[]> result = query.getResultList();
			if (CollectionUtil.isNotEmpty(result) && result.get(0).length > 1) {
				int level = ((Number) result.get(0)[0]).intValue();
				int order = ((Number) result.get(0)[1]).intValue();
				String parentId = ((String) result.get(0)[2]);
				LOG.info("Reordering Level :" + level + " order :" + order + " parentId : " + parentId);

				// If Parent
				if (StringUtils.checkString(parentId).length() == 0) {
					StringBuilder hql = new StringBuilder("update PoItem i set i.level = (i.level - 1) where i.level > :level and i.po.id = :poId");
					query = getEntityManager().createQuery(hql.toString());
					query.setParameter("poId", poId);
					query.setParameter("level", level);
					query.executeUpdate();
				} else {
					// If Child
					StringBuilder hql = new StringBuilder("update PoItem i set i.order = (i.order - 1) where i.level = :level and i.order >= :order and i.parent.id = :parentId and i.po.id = :poId");
					query = getEntityManager().createQuery(hql.toString());
					query.setParameter("poId", poId);
					query.setParameter("level", level);
					query.setParameter("order", order);
					query.setParameter("parentId", parentId);
					query.executeUpdate();
				}
			}
		}

		LOG.info("Going to Delete the selected Po Items");
		query = getEntityManager().createQuery("delete from PoItem item where item.id in (:id) and  item.parent is not null and item.po.id = :poId");
		query.setParameter("id", Arrays.asList(poItemIds));
		query.setParameter("poId", poId);
		query.executeUpdate();

		query = getEntityManager().createQuery("delete from PoItem item where item.id in (:id) and item.parent is null and item.po.id = :poId");
		query.setParameter("id", Arrays.asList(poItemIds));
		query.setParameter("poId", poId);
		query.executeUpdate();
		LOG.info("COMPLETED TO DELETE the Selected po Items");

		for (String parentId : parentIds) {
			query = getEntityManager().createQuery("select count(*) from PoItem a  where a.parent.id =  :id ");
			query.setParameter("id", parentId);
			int count = ((Number) query.getSingleResult()).intValue();
			LOG.info("Child Count : " + count);
			if (count == 0) {
				query = getEntityManager().createQuery("delete from PoItem item where item.id = :id  and item.parent is null and item.po.id = :poId");
				query.setParameter("id", parentId);
				query.setParameter("poId", poId);
				query.executeUpdate();
				LOG.info("Deleted section which is not having any childs");

			}
		}

		query = getEntityManager().createQuery("update Po p set p.total =case when (select sum(b.totalAmountWithTax) from PoItem b where b.po.id = p.id) is null then 0 else (select sum(b.totalAmountWithTax) from PoItem b where b.po.id = p.id) end where p.id= :poId");
		query.setParameter("poId", poId);
		int result1 = query.executeUpdate();

		query = getEntityManager().createQuery("update Po p set p.grandTotal = (p.total + p.additionalTax) where p.id= :poId");
		query.setParameter("poId", poId);
		int result2 = query.executeUpdate();

		query = getEntityManager().createQuery("select a.id from PoItem a  where a.po.id =  :poId  and a.parent is null  order by a.level, a.order ");
		query.setParameter("poId", poId);
		List<String> itemIds = query.getResultList();
		int index =1;
		for (String id : itemIds) {
			query = getEntityManager().createQuery("select a.level, a.order, p.id from PoItem a left outer join a.parent p where a.id = :id");
			query.setParameter("id", id);
			List<Object[]> result = query.getResultList();
			if (CollectionUtil.isNotEmpty(result) && result.get(0).length > 1) {
				int level = ((Number) result.get(0)[0]).intValue();
				int order = ((Number) result.get(0)[1]).intValue();
				String parentId = ((String) result.get(0)[2]);
				LOG.info("Final Update order Level :" + level + " order :" + order + " parentId : " + parentId + " Id " + id);
				// If Parent
				if (StringUtils.checkString(parentId).length() == 0) {
					StringBuilder hql = new StringBuilder("update PoItem i set i.level = :level where  i.po.id = :poId and (i.id = :id or i.parent.id =:parentId )");
					query = getEntityManager().createQuery(hql.toString());
					query.setParameter("poId", poId);
					query.setParameter("level", index++);
					query.setParameter("id", id);
					query.setParameter("parentId", id);
					query.executeUpdate();
				}
			}
		}

		LOG.info("updateResult :" + result1 + "==" + result2);
		return poId;
	}

	@Override
	public Long findItemCountByPrItemIds(String[] poItemsIds, String poId) {
		final Query query = getEntityManager().createQuery("select count(distinct a.id) from PoItem a where a.id not in ( :poItemsIds ) and a.order > 0 and a.po.id = :poId");
		query.setParameter("poItemsIds", Arrays.asList(poItemsIds));
		query.setParameter("poId", poId);
		Long count = query.getSingleResult() != null ? ((Number) query.getSingleResult()).longValue() : 0l;
		return count;
	}

	@Override
	public List<PoItemPojo> findPoItemForTenantIdForCsv(String tenantId, int pageSize, int pageNo, String[] poIds, SearchFilterPoPojo searchFilterPoPojo, boolean select_all, Date startDate, Date endDate, String loggedInUser,String poType,List<String> businessUnitIds,String status) {
		String hql = "";

		hql += "select distinct NEW com.privasia.procurehere.core.pojo.PoItemPojo(po.id, po.poNumber, po.name, po.status, fs.vendorCode, sup.companyName, bu.unitName, po.createdDate, po.actionDate, cb.name, ob.name, po.orderedDate, po.poRevisedDate, pt.productCode, pc.productName, p.itemName, p.itemDescription, um.uom, p.quantity,p.lockedQuantity,p.balanceQuantity, c.currencyCode, p.unitPrice,p.pricePerUnit, p.totalAmount, p.itemTax, p.taxAmount, p.totalAmountWithTax, pr.prId, p.order, p.level,p.itemIndicator) from PoItem p ";
		
		hql +=" join p.po as po left outer join p.unit as um left outer join p.productCategory as pc left outer join p.product as pt ";
		
		hql += " join po.createdBy as cb left outer join po.modifiedBy mb left outer join p.buyer b left outer join po.orderedBy as o left outer join po.orderedBy as ob" ;

		hql += " left outer join po.businessUnit as bu left outer join po.supplier as fs left outer join fs.supplier as sup left outer join po.currency as c ";

		hql += " left outer join po.pr as pr left outer join po.approvals as pa left outer join pa.approvalUsers as pau left outer join pau.user as au left outer join po.poTeamMembers as ptm";

		hql += " where po.buyer.id = :tenantId ";
		if (!(select_all)) {
			if (poIds != null && poIds.length > 0) {
				hql += " and po.id in(:poArr)";
				LOG.info("select_all " + select_all);
			}
			if(status.equals("REVISION")){
				hql += " and po.revised =:revised ";
			}else{
				hql += " and po.status =:status ";
			}
		}
		else{
			if(status !=null){

				if(status.equals("PENDING")) {
					hql += " and po.revised =:revised ";
					hql += " and po.cancelled =:cancelled ";
				}

				if(status.equals("ONCANCELLATION")) {
					hql += " and po.cancelled =:cancelled ";
				}else if(status.equals("REVISION")){
					hql += " and po.revised =:revised ";
				}else{
					hql += " and po.status =:status ";
				}
			}
		}
		if(poType.equals("me")){
			if (StringUtils.checkString(loggedInUser).length() > 0) {
				hql += " and (po.createdBy.id =:userId or ptm.user.id=:userId or au.id=:userId)";
			}
		}else{
			if (businessUnitIds != null && !businessUnitIds.isEmpty()) {
				hql+= " AND bu.id IN (:businessUnitIds) ";
			}
		}


		if (searchFilterPoPojo != null) {
			if (StringUtils.checkString(searchFilterPoPojo.getPoname()).length() > 0) {
				hql += " and upper(po.name) like :poName";
			}
			if (StringUtils.checkString(searchFilterPoPojo.getSupplier()).length() > 0) {
				hql += " and upper(sup.companyName) like :suppliername";
				LOG.info("getSuppliername" + searchFilterPoPojo.getSupplier());
			}
			if (StringUtils.checkString(searchFilterPoPojo.getPonumber()).length() > 0) { 
				hql += " and upper(po.poNumber) like :ponumber";
			}
			if (StringUtils.checkString(searchFilterPoPojo.getPocreatedby()).length() > 0) {
				hql += " and upper(cb.name) like :pocreatedby";
			}
			if (StringUtils.checkString(searchFilterPoPojo.getBusinessunit()).length() > 0) {
				hql += " and upper(bu.unitName) like :businessunit";
			}
			if (StringUtils.checkString(searchFilterPoPojo.getCurrency()).length() > 0) {
				hql += " and upper(c.currencyCode) like :currency";
			}
			if (StringUtils.checkString(searchFilterPoPojo.getPoorderedby()).length() > 0) {
				hql += " and upper(o.name) like :orderedby";
			}
			if (StringUtils.checkString(searchFilterPoPojo.getPrnumber()).length() > 0) {
				hql += " and upper(pr.prId) like :prnumber";
			}
			if (searchFilterPoPojo.getPostatus() != null) {
				hql += " and upper(po.status) like :postatus";
			} else {
				hql += " and upper(po.status) in(:postatus)";
			}
		}

		if (startDate != null && endDate != null) {
			hql += " and po.createdDate between :startDate and :endDate ";
		}

		hql += " order by po.createdDate desc, po.poNumber, p.level, p.order ";
		
		final Query query = getEntityManager().createQuery(hql);
		LOG.info("hql poItem "+hql);
		query.setParameter("tenantId", tenantId);

		if (!(select_all)) {
			if (poIds != null && poIds.length > 0) {
				query.setParameter("poArr", Arrays.asList(poIds));
			}
		}
		if(poType.equals("me")){
			if (StringUtils.checkString(loggedInUser).length() > 0)
				query.setParameter("userId", loggedInUser);
		}else {
			if (businessUnitIds != null && !businessUnitIds.isEmpty())
				query.setParameter("businessUnitIds", businessUnitIds);
		}

		if (searchFilterPoPojo != null) {
			if (StringUtils.checkString(searchFilterPoPojo.getPoname()).length() > 0) {
				query.setParameter("poName", "%" + searchFilterPoPojo.getPoname().toUpperCase() + "%");
			}
			if (StringUtils.checkString(searchFilterPoPojo.getSupplier()).length() > 0) {
				query.setParameter("suppliername", "%" + searchFilterPoPojo.getSupplier().toUpperCase() + "%");
			}
			if (StringUtils.checkString(searchFilterPoPojo.getPonumber()).length() > 0) {
				query.setParameter("ponumber", "%" + searchFilterPoPojo.getPonumber().toUpperCase() + "%");
			}
			if (StringUtils.checkString(searchFilterPoPojo.getPocreatedby()).length() > 0) {
				query.setParameter("pocreatedby", "%" + searchFilterPoPojo.getPocreatedby().toUpperCase() + "%");
			}
			if (StringUtils.checkString(searchFilterPoPojo.getBusinessunit()).length() > 0) {
				query.setParameter("businessunit", "%" + searchFilterPoPojo.getBusinessunit().toUpperCase() + "%");
			}
			if (StringUtils.checkString(searchFilterPoPojo.getCurrency()).length() > 0) {
				query.setParameter("currency", searchFilterPoPojo.getCurrency());
			}
			if (StringUtils.checkString(searchFilterPoPojo.getPoorderedby()).length() > 0) {
				query.setParameter("orderedby", "%" + searchFilterPoPojo.getPoorderedby().toUpperCase() + "%");
			}
			if (StringUtils.checkString(searchFilterPoPojo.getPrnumber()).length() > 0) {
				query.setParameter("prnumber", "%" + searchFilterPoPojo.getPrnumber().toUpperCase() + "%");
			}
			if (searchFilterPoPojo.getPostatus() != null) {
				query.setParameter("postatus", searchFilterPoPojo.getPostatus());
			} else {
				query.setParameter("postatus", Arrays.asList(PoStatus.DRAFT,PoStatus.SUSPENDED,PoStatus.CLOSED,PoStatus.READY, PoStatus.ORDERED, PoStatus.ACCEPTED, PoStatus.DECLINED, PoStatus.CANCELLED, PoStatus.REVISE, PoStatus.PENDING));
			}
		}
		LOG.info("STATUS EXTRACTED WHEN EXPORT REPORT PO ITEM LIST >>>>>    "+status);
	/*	if(null!=status)
			query.setParameter("status",PoStatus.fromString(status));
		else
			query.setParameter("status", Arrays.asList(PoStatus.DRAFT,PoStatus.SUSPENDED,PoStatus.CLOSED,PoStatus.READY, PoStatus.ORDERED, PoStatus.ACCEPTED, PoStatus.DECLINED, PoStatus.CANCELLED, PoStatus.REVISE, PoStatus.PENDING));
*/

		if (startDate != null && endDate != null) {
			query.setParameter("startDate", startDate);
			query.setParameter("endDate", endDate);
		}
		if (select_all==true) {
			LOG.info("no issue here");
			if (null!=status) {
				if (status.equals("PENDING")) {
					query.setParameter("cancelled", Boolean.FALSE);
					query.setParameter("revised", Boolean.FALSE);
				}
				if (status.equals("ONCANCELLATION")) {
					query.setParameter("cancelled", Boolean.TRUE);
				} else if (status.equals("REVISION")) {
					query.setParameter("revised", Boolean.TRUE);
				} else {
					query.setParameter("status", PoStatus.fromString(status));
				}

			}

			else{
				query.setParameter("status", Arrays.asList(PoStatus.DRAFT,PoStatus.SUSPENDED,PoStatus.CLOSED,PoStatus.READY, PoStatus.ORDERED, PoStatus.ACCEPTED, PoStatus.DECLINED, PoStatus.CANCELLED, PoStatus.REVISE, PoStatus.PENDING));

			}
		}

		else{
			if (status.equals("REVISION")) {
				query.setParameter("revised", Boolean.TRUE);
			} else {
				query.setParameter("status", PoStatus.fromString(status));
			}

		}
		LOG.info("EXPORT PO ITEM LIST here");
		query.setFirstResult((pageSize * pageNo));
		query.setMaxResults(pageSize);
		return query.getResultList();
	}

	@Override
	public List<PoItemSupplierPojo> findPoItemSummaryForSupplierCsvReport(String tenantId, int pageSize, int pageNo, String[] poIds, SearchFilterPoPojo searchFilterPoPojo, boolean select_all, Date startDate, Date endDate) {
		String hql = "";
		hql += "select new com.privasia.procurehere.core.pojo.PoItemSupplierPojo(po.id, po.poNumber, po.name, po.status, b.companyName, bu.unitName, po.createdDate, po.actionDate, p.itemName, p.itemDescription ,um.uom, p.quantity,p.lockedQuantity,p.balanceQuantity, c.currencyCode, p.unitPrice,p.pricePerUnit, p.totalAmount, p.itemTax, p.taxAmount, p.totalAmountWithTax, p.order, p.level,p.itemIndicator, b.id) ";
		hql += " from PoItem p ";
		
		hql += " left outer join p.po as po";

		hql += " left outer join po.supplier as fs";

		hql += " left outer join po.businessUnit as bu";

		hql += " left outer join po.currency as c";

		hql += " left outer join po.buyer b ";
		
		hql += "left outer join p.unit um";

		hql += " where fs.supplier.id = :tenantId";

		if (!(select_all)) {
			if (poIds != null && poIds.length > 0) {
				hql += " and po.id in (:poArr)";
			}
		}
		
		if (searchFilterPoPojo != null) {
			if (StringUtils.checkString(searchFilterPoPojo.getPoname()).length() > 0) {
				hql += " and upper(po.name) like :name";
			}
			if (StringUtils.checkString(searchFilterPoPojo.getBuyer()).length() > 0) {
				hql += " and upper(b.companyName) like :buyerCompanyName";
			}
			if (StringUtils.checkString(searchFilterPoPojo.getPoNumber()).length() > 0) {
				hql += " and upper(po.poNumber) like :poNumber";
			}
			if (StringUtils.checkString(searchFilterPoPojo.getCurrency()).length() > 0) {
				hql += " and upper(c.currencyCode) like :currency";
			}
			if (StringUtils.checkString(searchFilterPoPojo.getBusinessUnit()).length() > 0) {
				hql += " and upper(bu.unitName) like :businessunit";
			}
			if (StringUtils.checkString(searchFilterPoPojo.getDescription()).length() > 0) {
				hql += " and upper(po.description) like :description";
			}
			if (searchFilterPoPojo.getPostatus() != null) {
				hql += " and upper(po.status) like :postatus";

			} else {
				hql += " and upper(po.status) in :postatus)";
			}
		}

		// search with Date range
		if (startDate != null && endDate != null) {
			hql += " and po.createdDate between :startDate and :endDate ";
		}

		hql += " order by b.id, po.createdDate desc, po.poNumber, p.level, p.order";

		LOG.info("findPoItemSummaryForSupplierCsvReport >>>>>>>>."+hql);
		LOG.info("sDate :"+startDate+" endDate :"+endDate);

		final Query query = getEntityManager().createQuery(hql);

		if (!(select_all)) {
			if (poIds != null && poIds.length > 0) {
				query.setParameter("poArr", Arrays.asList(poIds));
			}
		}

		if (searchFilterPoPojo != null) {
			if (StringUtils.checkString(searchFilterPoPojo.getPoname()).length() > 0) {
				query.setParameter("name", "%" + searchFilterPoPojo.getPoname().toUpperCase() + "%");
			}
			if (StringUtils.checkString(searchFilterPoPojo.getBuyer()).length() > 0) {
				query.setParameter("buyerCompanyName", "%" + searchFilterPoPojo.getBuyer().toUpperCase() + "%");
			}
			if (StringUtils.checkString(searchFilterPoPojo.getPoNumber()).length() > 0) {
				query.setParameter("poNumber", "%" + searchFilterPoPojo.getPoNumber().toUpperCase() + "%");
			}
			if (StringUtils.checkString(searchFilterPoPojo.getCurrency()).length() > 0) {
				query.setParameter("currency", "%" + searchFilterPoPojo.getCurrency().toUpperCase() + "%");
			}

			if (StringUtils.checkString(searchFilterPoPojo.getBusinessUnit()).length() > 0) {
				query.setParameter("businessunit", "%" + searchFilterPoPojo.getBusinessUnit().toUpperCase() + "%");
			}
			if (StringUtils.checkString(searchFilterPoPojo.getDescription()).length() > 0) {
				query.setParameter("description", "%" + searchFilterPoPojo.getDescription().toUpperCase() + "%");
			}
			if (searchFilterPoPojo.getPostatus() != null) {
				LOG.info("getPostatus" + searchFilterPoPojo.getPostatus());
				query.setParameter("postatus", searchFilterPoPojo.getPostatus());
			} else {
				query.setParameter("postatus", Arrays.asList(PoStatus.ORDERED, PoStatus.ACCEPTED, PoStatus.DECLINED, PoStatus.CANCELLED, PoStatus.REVISE));
			}

		}

		if (startDate != null && endDate != null) {
			query.setParameter("startDate", startDate);
			query.setParameter("endDate", endDate);
		}
		query.setParameter("tenantId", tenantId);
		query.setFirstResult((pageSize * pageNo));
		query.setMaxResults(pageSize);
		
		return query.getResultList();
	}

	@Override
	public long findTotalPoItemSupplierSummaryCountForCsv(String tenantId, Date startDate, Date endDate) {
		LOG.info("Tenant in DaoImpl for count in supplier........." + tenantId);
		String hql = "select count(p) from Po p";
		hql += " left outer join p.supplier as fs";
		hql += " left outer join p.businessUnit as bu";
		hql += " left outer join p.currency as c";
		hql += " left outer join p.buyer b";
		hql += " where fs.supplier.id = :tenantId";

		final Query query = getEntityManager().createQuery(hql.toString());
		query.setParameter("tenantId", tenantId);
		return ((Number) query.getSingleResult()).longValue();
	}

	@Override
	public List<PoItem> getPoItemLevelOrder(String poId) {
		final Query query = getEntityManager().createQuery("select distinct a from PoItem a inner join fetch a.po c where a.parent is null and c.id = :id order by a.level, a.order");
		query.setParameter("id", poId);
		return query.getResultList();
	}

	@Override
	public boolean isExists(PoItem poItem,boolean isSection) {
		LOG.info("POITEM ID :" + poItem.getId());
		String hql = "select count(*) from PoItem pi where pi.po.id = :poId and upper(pi.itemName) = :itemName";
		if (StringUtils.checkString(poItem.getId()).length() > 0) {
			hql += " and pi.id <> :id";
		}
		if(isSection){
			hql += " and pi.parent is null";
		}

		final Query query = getEntityManager().createQuery(hql);
		query.setParameter("itemName", poItem.getItemName().toUpperCase());
		query.setParameter("poId", poItem.getPo().getId());
		if (StringUtils.checkString(poItem.getId()).length() > 0) {
			query.setParameter("id", poItem.getId());
		}
		LOG.info("hql :" + hql);
		return ((Number) query.getSingleResult()).intValue() > 0;
	}

	@Override
	public List<PoItem> findAllChildPoItemByPoId(String poId) {
		final Query query = getEntityManager().createQuery("select distinct a from PoItem a inner join fetch a.po c where a.parent is not null and c.id = :id order by a.level, a.order");
		query.setParameter("id", poId);
		return query.getResultList();
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW, rollbackFor = { Exception.class })
	public String deletePoItemsByPoId(String poId) {
		LOG.info("Going to Delete the selected Po Items");
		Query query = getEntityManager().createQuery("delete from PoItem item where  item.parent is not null and item.po.id = :poId");
		query.setParameter("poId", poId);
		query.executeUpdate();

		Query query1 = getEntityManager().createQuery("delete from PoItem item where  item.parent is null and item.po.id = :poId");
		query1.setParameter("poId", poId);
		query1.executeUpdate();
		LOG.info("COMPLETED TO DELETE the Selected po ITems");
		return poId;
	}

	@Override
	public List<PoItem> getPoItemsbyId(String poId) {
		final Query query = getEntityManager().createQuery("select distinct a from PoItem a inner join fetch a.po po left outer join fetch a.children c where po.id =:id order by a.level, a.order");
		query.setParameter("id", poId);
		return query.getResultList();
	}

	@Override
	public void deleteNewFieldPo(String label, String poId) {
		try {
			LOG.info("Po ID :: " + poId + " label :: " + label);
			StringBuilder hql = new StringBuilder("update PoItem i set i." + label + " = null where i.po.id = :poId");
			Query query = getEntityManager().createQuery(hql.toString());
			query.setParameter("poId", poId);
			query.executeUpdate();
			LOG.info("po items columns set deleted succesfully...");

			// delete additional column
			hql = new StringBuilder("update Po p set p." + label + "Label = null where p.id = :poId");
			query = getEntityManager().createQuery(hql.toString());
			query.setParameter("poId", poId);
			query.executeUpdate();
			LOG.info("po columns deleted succesfully...");
		} catch (NoResultException nr) {
			LOG.info("Error while deleting new Fields: " + nr.getMessage(), nr);
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public PoItem getPoItembyPoItemId(String itemId) {
		try {
			final Query query = getEntityManager().createQuery("select distinct a from PoItem a inner join fetch a.po c left outer join fetch a.children left outer join fetch a.parent ch where a.id = :id");
			query.setParameter("id", itemId);
			List<PoItem> uList = query.getResultList();
			if (CollectionUtil.isNotEmpty(uList)) {
				LOG.info(" success ");
				return uList.get(0);
			} else {
				return null;
			}
		} catch (NoResultException nr) {
			LOG.info("Error while getting po Items : " + nr.getMessage(), nr);
			return null;
		}
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void updateItemOrder(String poId, PoItem poItem, String oldParent, String newParent, int oldOrder, int newOrder, int oldLevel, int newLevel) {
		LOG.info("Updating ITEM ORDERE ==> oldParent " + oldParent + " newParent " + newParent + " oldOrder " + oldOrder + " newOrder " + newOrder + " oldLevel " + oldLevel + " newLevel " + newLevel);
		// Top Level
		// Rearrange its new place - Push down
		if (newOrder == 0) {
			LOG.info("Enter 1 :: poId :: " + poId + " newLevel :: " + newLevel);
			StringBuilder hql = new StringBuilder("update PoItem i set i.level = (i.level + 1) where i.po.id = :poId ");

			if (newLevel > oldLevel && oldOrder == 0) {
				hql.append(" and i.level > :level");
			} else {
				hql.append(" and i.level >= :level");
			}

			Query query = getEntityManager().createQuery(hql.toString());
			query.setParameter("poId", poId);
			query.setParameter("level", newLevel);
			query.executeUpdate();
		}
		// Top Level
		// Rearrange its new place - Pull up
		if (oldOrder == 0) {
			LOG.info("Enter 2 :: poId :: " + poId + " oldLevel :: " + oldLevel);
			StringBuilder hql = new StringBuilder("update PoItem i set i.level = (i.level - 1) where i.level > :level and i.po.id = :poId");
			Query query = getEntityManager().createQuery(hql.toString());
			query.setParameter("poId", poId);
			query.setParameter("level", oldLevel);
			query.executeUpdate();
		}
		// Child Level
		// Push down
		if (newParent != null) {
			LOG.info("Enter 3 :: bqId :: " + poId + " newParent :: " + newParent + " newOrder " + newOrder);
			StringBuilder hql = new StringBuilder("update PoItem i set i.order = (i.order + 1) where i.parent.id = :parent and i.order >= :order and i.po.id = :poId");
			if (newOrder > oldOrder && oldParent.equals(newParent)) {
				hql.append(" and i.order > :order");
			} else {
				hql.append("  and i.order >= :order ");
			}
			// Rearrange its new place
			Query query = getEntityManager().createQuery(hql.toString());
			query.setParameter("poId", poId);
			query.setParameter("parent", newParent);
			query.setParameter("order", newOrder);
			query.executeUpdate();
		}
		// Child Level
		// Pull Up
		if (oldParent != null) {
			LOG.info("Enter 4 :: poId :: " + poId + " oldParent :: " + oldParent + " oldOrder " + oldOrder);
			StringBuilder hql = new StringBuilder("update PoItem i set i.order = (i.order - 1) where i.parent.id = :parent and i.order > :order and i.po.id = :poId");
			Query query = getEntityManager().createQuery(hql.toString());
			query.setParameter("poId", poId);
			query.setParameter("parent", oldParent);
			query.setParameter("order", oldOrder);
			query.executeUpdate();
		}

		// Fetch the parent object again as its position might have changed during above updates.
		if (newParent != null) {
			PoItem newDbParent = getPoItembyPoItemId(newParent);
			LOG.info("newDbParent.getLevel() :" + newDbParent.getLevel());
			poItem.setLevel(newDbParent.getLevel());
		}

		LOG.info("Updating object : " + poItem.toLogString());

		StringBuilder hql = new StringBuilder("update PoItem i set i.order = :order, i.level = :level, i.parent = :newParent where i.id = :poItemId and i.po.id = :poId");
		Query query = getEntityManager().createQuery(hql.toString());
		query.setParameter("poId", poId);
		query.setParameter("poItemId", poItem.getId());
		query.setParameter("newParent", poItem.getParent());
		query.setParameter("order", poItem.getOrder());
		query.setParameter("level", poItem.getLevel());
		query.executeUpdate();
		LOG.info("Updated successfully");
	}
}
