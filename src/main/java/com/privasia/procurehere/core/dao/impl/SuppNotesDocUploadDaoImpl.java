package com.privasia.procurehere.core.dao.impl;

import java.util.List;

import javax.persistence.Query;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Repository;

import com.privasia.procurehere.core.dao.SuppNotesDocUploadDao;
import com.privasia.procurehere.core.entity.SupplierNoteDocument;
import com.privasia.procurehere.core.enums.TenantType;
import com.privasia.procurehere.core.pojo.ColumnParameter;
import com.privasia.procurehere.core.pojo.OrderParameter;
import com.privasia.procurehere.core.pojo.TableDataInput;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.StringUtils;

@Repository
public class SuppNotesDocUploadDaoImpl extends GenericDaoImpl<SupplierNoteDocument, String> implements SuppNotesDocUploadDao {

	@SuppressWarnings("unused")
	private static final Logger LOG = LogManager.getLogger(EventIdSettingsDaoImpl.class);

	@SuppressWarnings("unchecked")
	@Override
	public List<SupplierNoteDocument> findSuppNotesDocBySuppId(String suppId, String loggedInUserTenantId, TenantType tenantType, TableDataInput tableParams) {
		final Query query = constructSuppNotesDocQuery(suppId, loggedInUserTenantId, tenantType, tableParams, false);
		query.setFirstResult(tableParams.getStart());
		query.setMaxResults(tableParams.getLength());
		return query.getResultList();
	}

	@Override
	public long findTotalFilteredSupNotesDocList(String suppId, String loggedInUserTenantId, TenantType tenantType, TableDataInput tableParams) {
		final Query query = constructSuppNotesDocQuery(suppId, loggedInUserTenantId, tenantType, tableParams, true);
		return ((Number) query.getSingleResult()).longValue();
	}

	@Override
	public long findTotalSupNotesDocList(String suppId, String loggedInUserTenantId, TenantType tenantType) {
		StringBuilder hql = new StringBuilder("select count (snd) from SupplierNoteDocument snd where snd.uploadTenantId = :tenantId  and snd.tenantType =:tenantType and snd.supplier.id =:suppId");
		final Query query = getEntityManager().createQuery(hql.toString());
		query.setParameter("tenantId", loggedInUserTenantId);
		query.setParameter("tenantType", tenantType);
		query.setParameter("suppId", suppId);
		return ((Number) query.getSingleResult()).longValue();
	}

	private Query constructSuppNotesDocQuery(String suppId, String loggedInUserTenantId, TenantType tenantType, TableDataInput tableParams, boolean isCount) {

		String hql = "";
		if (!isCount) {
			hql += "select distinct new com.privasia.procurehere.core.entity.SupplierNoteDocument(c.id, c.fileName, c.description, c.fileSizeInKb, c.credContentType, c.uploadDate, c.visible, cb.name, c.tenantType) ";
		} else { // If count query is enabled, then add the select count(*)
					// clause
			hql += "select count(*) ";
		}
		hql += " from SupplierNoteDocument c ";

		// If this is not a count query, only then add the join fetch. Count
		// query does not require its
		if (!isCount) {
			hql += " left outer join c.createdBy as cb ";
		}
		hql += " where (c.supplier.id =:suppId and c.uploadTenantId =:loggedInUserTenantId) ";

		// Add on search filter conditions
		for (ColumnParameter cp : tableParams.getColumns()) {
			if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
				if (cp.getData().equalsIgnoreCase("userName")) {
					hql += " and upper(c.createdBy.name ) like (:" + cp.getData().replace(".", "") + ")";
				} else {
					hql += " and upper(c." + cp.getData() + ") like (:" + cp.getData().replace(".", "") + ")";
				}
			}
		}

		if (TenantType.BUYER == tenantType) {
			hql += " or (c.tenantType =:tenantType and c.visible =:visible and c.supplier.id =:suppId) ";
		}

		// If it is not a count query then add order by clause
		if (!isCount) {
			List<OrderParameter> orderList = tableParams.getOrder();
			if (CollectionUtil.isNotEmpty(orderList)) {
				hql += " order by ";
				for (OrderParameter order : orderList) {
					String orderColumn = tableParams.getColumns().get(order.getColumn()).getData();
					String dir = order.getDir();
					if (orderColumn.equalsIgnoreCase("userName")) {
						hql += " c.createdBy.name  " + dir + ",";
					} else {
						hql += " c." + orderColumn + " " + dir + ",";
					}
				}
				if (hql.lastIndexOf(",") == hql.length() - 1) {
					hql = hql.substring(0, hql.length() - 1);
				}
			} else {
				// For Order By Default
				hql += " order by c.uploadDate DESC";
			}
		}

		// LOG.info("HQL : " + hql);
		final Query query = getEntityManager().createQuery(hql.toString());
		query.setParameter("loggedInUserTenantId", loggedInUserTenantId);
		query.setParameter("suppId", suppId);

		if (TenantType.BUYER == tenantType) {
			query.setParameter("tenantType", TenantType.OWNER);
			query.setParameter("visible", Boolean.TRUE);
		}

		// Apply search filter values
		for (ColumnParameter cp : tableParams.getColumns()) {
			if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
				query.setParameter(cp.getData().replace(".", ""), "%" + cp.getSearch().getValue().toUpperCase() + "%");
			}
		}
		return query;
	}

	

	@SuppressWarnings("unchecked")
	@Override
	public List<SupplierNoteDocument> findSuppNotesDocBySuppIdWithFile(String suppId, String tenentId) {
		String hql = "";
		hql += "select distinct c ";
		hql += " from SupplierNoteDocument c ";

		hql += " left outer join c.createdBy as cb ";

		hql += " where (c.supplier.id =:suppId and c.uploadTenantId =:loggedInUserTenantId) ";

		final Query query = getEntityManager().createQuery(hql.toString());
		query.setParameter("loggedInUserTenantId", tenentId);
		query.setParameter("suppId", suppId);
		return query.getResultList();
	}
}