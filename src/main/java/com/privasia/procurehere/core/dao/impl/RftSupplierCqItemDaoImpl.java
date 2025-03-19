package com.privasia.procurehere.core.dao.impl;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Repository;

import com.privasia.procurehere.core.dao.RftSupplierCqItemDao;
import com.privasia.procurehere.core.entity.RftCq;
import com.privasia.procurehere.core.entity.RftCqItem;
import com.privasia.procurehere.core.entity.RftSupplierCqItem;
import com.privasia.procurehere.core.entity.RftSupplierCqOption;
import com.privasia.procurehere.core.entity.Supplier;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.Global;

/**
 * @author Vipul
 */
@Repository
public class RftSupplierCqItemDaoImpl extends GenericDaoImpl<RftSupplierCqItem, String> implements RftSupplierCqItemDao {

	private static final Logger LOG = LogManager.getLogger(Global.SUPPLIER_LOG);

	@SuppressWarnings("unchecked")
	@Override
	public List<RftSupplierCqItem> findSupplierCqItemListByCqId(String cqId, String supplierId) {
		final Query query = getEntityManager().createQuery("select a from RftSupplierCqItem a inner join a.supplier s inner join fetch a.cqItem ci left outer join fetch ci.cqOptions cqOp inner join fetch ci.cq cq inner join fetch a.event re where cq.id = :cqId and s.id =:supplierId order by ci.level, ci.order");
		query.setParameter("cqId", cqId);
		query.setParameter("supplierId", supplierId);
		List<RftSupplierCqItem> list = query.getResultList();
		// Remove duplicates as you cannot use Distinct on a table with LOB column in oracle.
		// see -
		// http://stackoverflow.com/questions/9357974/error-ora-00932-inconsistent-datatypes-expected-got-blob-in-join-statement
		LinkedHashMap<String, RftSupplierCqItem> map = new LinkedHashMap<String, RftSupplierCqItem>();
		if (CollectionUtil.isNotEmpty(list)) {
			for (RftSupplierCqItem item : list) {
				map.put(item.getId(), item);
			}
		}
		return new ArrayList<RftSupplierCqItem>(map.values());
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<RftSupplierCqItem> findSupplierCqItemByCqIdandSupplierId(String cqId, String supplierId) {
		final Query query = getEntityManager().createQuery("select new com.privasia.procurehere.core.entity.RftSupplierCqItem(a.id, a.fileName, a.textAnswers,  ci.itemName, ci.itemDescription, ci.level, ci.order, ci.cqType) from RftSupplierCqItem a inner join a.supplier s inner join a.cqItem ci inner join ci.cq cq where cq.id = :cqId and s.id =:supplierId order by ci.level, ci.order");
		query.setParameter("cqId", cqId);
		query.setParameter("supplierId", supplierId);
		List<RftSupplierCqItem> list = query.getResultList();
		// Remove duplicates as you cannot use Distinct on a table with LOB column in oracle.
		// see -
		// http://stackoverflow.com/questions/9357974/error-ora-00932-inconsistent-datatypes-expected-got-blob-in-join-statement
		LinkedHashMap<String, RftSupplierCqItem> map = new LinkedHashMap<String, RftSupplierCqItem>();
		if (CollectionUtil.isNotEmpty(list)) {
			for (RftSupplierCqItem item : list) {
				map.put(item.getId(), item);
			}
		}
		return new ArrayList<RftSupplierCqItem>(map.values());
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<RftCqItem> getCqItemsbyId(String cqId) {
		final Query query = getEntityManager().createQuery("select distinct a from RftCqItem a inner join fetch a.cq cq left outer join fetch a.children c where cq.id =:id order by a.level, a.order");
		query.setParameter("id", cqId);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public RftSupplierCqItem findCqByEventIdAndCqName(String eventId, String cqId) {
		final Query query = getEntityManager().createQuery("from RftSupplierCqItem rc inner join fetch rc.event as r inner join fetch rc.cq as cq where r.id = :eventId and rc.id = :cqId");
		query.setParameter("eventId", eventId);
		query.setParameter("cqId", cqId);
		List<RftSupplierCqItem> uList = query.getResultList();
		if (CollectionUtil.isNotEmpty(uList)) {
			return uList.get(0);
		} else {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<RftSupplierCqItem> findSupplierCqItemsByCqItemIdAndEventId(String cqItemId, String eventId, List<Supplier> suppliers) {
		final Query query = getEntityManager().createQuery("select a from RftSupplierCqItem a inner join fetch a.supplier s left outer join fetch a.cqItem ci left outer join ci.evaluationComments ec left outer join fetch ci.cqOptions cqOp inner join ci.cq cq inner join a.event re where ci.id = :cqItemId and re.id =:eventId and s in (:suppliers) order by a.supplier.companyName");
		query.setParameter("cqItemId", cqItemId);
		query.setParameter("eventId", eventId);
		query.setParameter("suppliers", suppliers);
		List<RftSupplierCqItem> list = query.getResultList();

		// Remove duplicates as you cannot use Distinct on a table with LOB column in oracle.
		// see -
		// http://stackoverflow.com/questions/9357974/error-ora-00932-inconsistent-datatypes-expected-got-blob-in-join-statement
		LinkedHashMap<String, RftSupplierCqItem> map = new LinkedHashMap<String, RftSupplierCqItem>();
		if (CollectionUtil.isNotEmpty(list)) {
			for (RftSupplierCqItem item : list) {
				map.put(item.getId(), item);
			}
		}
		return new ArrayList<RftSupplierCqItem>(map.values());
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<RftSupplierCqItem> findSupplierCqItemsByCqItemIdAndEventIdNew(String cqItemId, String eventId, List<Supplier> suppliers) {
		final Query query = getEntityManager().createQuery("select new com.privasia.procurehere.core.entity.RftSupplierCqItem(a.id, a.fileName, a.textAnswers,  ci.itemName, ci.itemDescription, ci.level, ci.order, ci.cqType, ci.totalScore, s.id, s.companyName) from RftSupplierCqItem as a inner join a.supplier as s inner join  a.cqItem as ci left outer join ci.cqOptions as cqOp inner join ci.cq as cq inner join  a.event as re where ci.id = :cqItemId and re.id =:eventId and s in (:suppliers) order by s.companyName");
		query.setParameter("cqItemId", cqItemId);
		query.setParameter("eventId", eventId);
		query.setParameter("suppliers", suppliers);
		List<RftSupplierCqItem> list = query.getResultList();
		// Remove duplicates as you cannot use Distinct on a table with LOB column in oracle.
		// see -
		// http://stackoverflow.com/questions/9357974/error-ora-00932-inconsistent-datatypes-expected-got-blob-in-join-statement
		LinkedHashMap<String, RftSupplierCqItem> map = new LinkedHashMap<String, RftSupplierCqItem>();
		if (CollectionUtil.isNotEmpty(list)) {
			for (RftSupplierCqItem item : list) {
				map.put(item.getId(), item);
			}
		}
		return new ArrayList<RftSupplierCqItem>(map.values());
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<RftSupplierCqItem> findSupplierCqItemsByCqItemIdAndEventIdAndSuppliers(String cqItemId, String eventId, List<Supplier> suppliers) {
		final Query query = getEntityManager().createQuery("select distinct NEW com.privasia.procurehere.core.entity.RftSupplierCqItem(a.id,a.fileName,a.textAnswers,  ci.itemName, ci.itemDescription, ci.level, ci.order, ci.cqType , s.id, s.companyName) from RftSupplierCqItem as a inner join a.supplier as s inner join  a.cqItem as ci left outer join  ci.cqOptions as cqOp inner join ci.cq as cq inner join  a.event as re where ci.id = :cqItemId and re.id =:eventId and s in (:suppliers) order by s.companyName");
		query.setParameter("cqItemId", cqItemId);
		query.setParameter("eventId", eventId);
		query.setParameter("suppliers", suppliers);

		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<String> findSumOfScoringForSupplier(String cqId, String eventId, List<Supplier> suppliers) {
		List<String> data = new ArrayList<String>();

		StringBuilder hsql = new StringBuilder("select sum(e.scoring), s.companyName from RftSupplierCqOption e left outer join e.cqItem c inner join c.event event inner join c.supplier s where e.cqItem.id = c.id and c.supplier.id = s.id and c.cq.id = :cqIds and event.id =:eventId and s in (:suppliers) group by s.companyName order by s.companyName");

		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("cqIds", cqId);
		query.setParameter("eventId", eventId);
		query.setParameter("suppliers", suppliers);

		List<Object[]> list = query.getResultList();

		for (Object[] values : list) {
			if (values[0] != null) {
				data.add(String.valueOf(values[0]));
			}
		}
		return data;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<RftSupplierCqItem> getSupplierCqItemsbySupplierId(String eventId, String supplierId, String cqId) {
		final Query query = getEntityManager().createQuery("select a from RftSupplierCqItem a inner join fetch a.cqItem ci inner join fetch ci.cq cq inner join fetch a.event re where re.id = :eventId and a.supplier.id = :supplierId and cq.id = :cqId order by ci.level, ci.order");
		query.setParameter("eventId", eventId);
		query.setParameter("supplierId", supplierId);
		query.setParameter("cqId", cqId);

		List<RftSupplierCqItem> list = query.getResultList();

		// Remove duplicates as you cannot use Distinct on a table with LOB column in oracle.
		// see -
		// http://stackoverflow.com/questions/9357974/error-ora-00932-inconsistent-datatypes-expected-got-blob-in-join-statement
		LinkedHashMap<String, RftSupplierCqItem> map = new LinkedHashMap<String, RftSupplierCqItem>();
		if (CollectionUtil.isNotEmpty(list)) {
			for (RftSupplierCqItem item : list) {
				map.put(item.getId(), item);
			}
		}
		return new ArrayList<RftSupplierCqItem>(map.values());
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<RftSupplierCqItem> getSupplierCqItemsbySupplier(String supplierId, String cqId) {
		final Query query = getEntityManager().createQuery("select NEW com.privasia.procurehere.core.entity.RftSupplierCqItem(a.id, ci.level, ci.order,  a.fileName, a.fileData) from RftSupplierCqItem a inner join a.cqItem ci inner join ci.cq cq where a.supplier.id = :supplierId and cq.id = :cqId and a.fileData is not null order by ci.level, ci.order");
		query.setParameter("supplierId", supplierId);
		query.setParameter("cqId", cqId);

		List<RftSupplierCqItem> list = query.getResultList();

		// Remove duplicates as you cannot use Distinct on a table with LOB column in oracle.
		// see -
		// http://stackoverflow.com/questions/9357974/error-ora-00932-inconsistent-datatypes-expected-got-blob-in-join-statement
		LinkedHashMap<String, RftSupplierCqItem> map = new LinkedHashMap<String, RftSupplierCqItem>();
		if (CollectionUtil.isNotEmpty(list)) {
			for (RftSupplierCqItem item : list) {
				map.put(item.getId(), item);
			}
		}
		return new ArrayList<RftSupplierCqItem>(map.values());
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<RftSupplierCqItem> findRequiredSupplierCqItemListByEventId(String supplierId, String eventId) {
		final Query query = getEntityManager().createQuery("select a from RftSupplierCqItem a where a.event.id = :eventId and a.supplier.id =:supplierId and a.cqItem.optional = true");
		query.setParameter("eventId", eventId);
		query.setParameter("supplierId", supplierId);
		List<RftSupplierCqItem> list = query.getResultList();
		return list;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<RftSupplierCqOption> findRequiredOptionValueByCqItemIdAndSupplierId(String id) {
		final Query query = getEntityManager().createQuery("select a from RftSupplierCqOption a  where a.cqItem.id = :id");
		query.setParameter("id", id);
		return query.getResultList();
	}

	@Override
	public int CountAllMandatorySupplierCqItemByEventId(String supplierId, String eventId) {
		StringBuilder hsql = new StringBuilder("select count(r) as tcount from RftSupplierCqItem r where r.event.id =:eventId and r.supplier.id =:supplierId and r.cqItem.optional = true");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("supplierId", supplierId);
		query.setParameter("eventId", eventId);
		return ((Number) query.getSingleResult()).intValue();
	}

	@Override
	public boolean resetAttachment(String eventId, String cqId) {
		final Query query = getEntityManager().createQuery("update RftSupplierCqItem rc set  fileName =null, fileData = null, credContentType = null where rc.event.id = :eventId and rc.id = :cqId");
		query.setParameter("eventId", eventId);
		query.setParameter("cqId", cqId);
		int returnValue = query.executeUpdate();
		return returnValue != 0 ? true : false;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<RftCq> getAllCqsBySupplierId(String eventId, String supplierId) {
		final Query query = getEntityManager().createQuery("select distinct a.cq from RftSupplierCqItem a where a.event.id = :eventId and a.supplier.id =:supplierId");
		query.setParameter("eventId", eventId);
		query.setParameter("supplierId", supplierId);
		List<RftCq> list = query.getResultList();
		return list;
	}

	@Override
	public void deleteSupplierCqItemsForEvent(String eventId) {

		Query query = getEntityManager().createQuery("delete from RftSupplierCqOption a where a.cqItem.id in (select b.id from RftSupplierCqItem b where b.event.id = :eventId) ");
		query.setParameter("eventId", eventId);
		query.executeUpdate();

		query = getEntityManager().createQuery("delete from RftSupplierCqItem a where a.event.id = :eventId");
		query.setParameter("eventId", eventId);
		query.executeUpdate();

		// final Query query = getEntityManager().createQuery("from RftSupplierCqItem a where a.event.id = :eventId");
		// query.setParameter("eventId", eventId);
		// List<RftSupplierCqItem> items = query.getResultList();
		// if (CollectionUtil.isNotEmpty(items)) {
		// for (RftSupplierCqItem item : items) {
		// getEntityManager().remove(item);
		// }
		// }
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<RftSupplierCqItem> getSupplierCqItemsbySupplierIdAndEventId(String eventId, String supplierId) {
		final Query query = getEntityManager().createQuery("select distinct a from RftSupplierCqItem a inner join a.supplier s inner join fetch a.cqItem ci left outer join fetch ci.cqOptions cqOp inner join fetch ci.cq cq inner join fetch a.event re where re.id = :eventId and s.id =:supplierId order by cq.cqOrder, ci.level, ci.order");
		query.setParameter("eventId", eventId);
		query.setParameter("supplierId", supplierId);
		List<RftSupplierCqItem> list = query.getResultList();

		// Remove duplicates as you cannot use Distinct on a table with LOB column in oracle.
		// see -
		// http://stackoverflow.com/questions/9357974/error-ora-00932-inconsistent-datatypes-expected-got-blob-in-join-statement
		LinkedHashMap<String, RftSupplierCqItem> map = new LinkedHashMap<String, RftSupplierCqItem>();
		if (CollectionUtil.isNotEmpty(list)) {
			for (RftSupplierCqItem item : list) {
				map.put(item.getId(), item);
			}
		}
		return new ArrayList<RftSupplierCqItem>(map.values());
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<RftCq> findRequiredSupplierCqCountByEventId(String supplierId, String eventId) {
		final Query query = getEntityManager().createQuery("select new RftCq(cq.name, count(a)) from RftSupplierCqItem a join a.cq cq where a.event.id = :eventId and a.supplier.id =:supplierId and a.cqItem.optional = true and a.cqItem.order <> 0 and (a.textAnswers is not null or (select count(*) from a.listAnswers) > 0) group by cq.name");
		query.setParameter("eventId", eventId);
		query.setParameter("supplierId", supplierId);
		List<RftCq> list = query.getResultList();
		return list;
	}

	@Override
	public String findFileNameById(String attachId, String supplierId) {
		try {
			final Query query = getEntityManager().createQuery("select a.fileName from RftSupplierCqItem a where a.cqItem.id = :id and a.supplier.id = :supplierId");
			query.setParameter("id", attachId);
			query.setParameter("supplierId", supplierId);
			return (String) query.getSingleResult();
		} catch (NoResultException e) {
			LOG.info("Error while getting File Name : " + e.getMessage(), e);
			return null;
		}
	}

	@Override
	public int findRftRequiredCqCountBySupplierId(String supplierId, String eventId) {
		StringBuilder hsql = new StringBuilder("select count(r.id) as tcount from RftSupplierCqItem r where r.event.id =:eventId and r.supplier.id =:supplierId and r.cqItem.isSupplierAttachRequired = true and (r.fileData is null or r.fileName is null) ");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("supplierId", supplierId);
		query.setParameter("eventId", eventId);

		return ((Number) query.getSingleResult()).intValue();
	}

	@Override
	public int findRftRequiredCqCountBySupplierIdAndCqId(String supplierId, String eventId, String cqId) {
		StringBuilder hsql = new StringBuilder("select count(r.id) as tcount from RftSupplierCqItem r where r.event.id =:eventId and r.supplier.id =:supplierId and r.cqItem.isSupplierAttachRequired = true and (r.fileData is null or r.fileName is null) and r.cq.id =:cqId");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("supplierId", supplierId);
		query.setParameter("eventId", eventId);
		query.setParameter("cqId", cqId);

		return ((Number) query.getSingleResult()).intValue();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<String> findRequiredCqsBySupplierId(String supplierId, String eventId, String cqId) {
		StringBuilder hsql = new StringBuilder("select concat(r.cqItem.level, '.', r.cqItem.order) from RftSupplierCqItem r where r.event.id =:eventId and r.supplier.id =:supplierId and r.cqItem.isSupplierAttachRequired = true and (r.fileData is null or r.fileName is null) and r.cq.id =:cqId ");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("supplierId", supplierId);
		query.setParameter("eventId", eventId);
		query.setParameter("cqId", cqId);
		return query.getResultList();
	}

}
