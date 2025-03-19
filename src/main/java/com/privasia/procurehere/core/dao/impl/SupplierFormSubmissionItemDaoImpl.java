package com.privasia.procurehere.core.dao.impl;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import com.privasia.procurehere.core.dao.SupplierFormSubmissionItemDao;
import com.privasia.procurehere.core.entity.SupplierFormSubmissionItem;
import com.privasia.procurehere.core.utils.CollectionUtil;

/**
 * @author sana
 */
@Repository
public class SupplierFormSubmissionItemDaoImpl extends GenericDaoImpl<SupplierFormSubmissionItem, String> implements SupplierFormSubmissionItemDao {

	@SuppressWarnings("unchecked")
	@Override
	public List<SupplierFormSubmissionItem> getAllSubFormById(String formId) {
		final Query query = getEntityManager().createQuery("select f from SupplierFormSubmissionItem f inner join fetch f.supplierFormSubmition sub inner join fetch f.supplierFormItem item left outer join fetch item.formOptions op where sub.id= :formId order by item.level, item.order");
		query.setParameter("formId", formId);
		List<SupplierFormSubmissionItem> list = query.getResultList();
		LinkedHashMap<String, SupplierFormSubmissionItem> map = new LinkedHashMap<String, SupplierFormSubmissionItem>();
		if (CollectionUtil.isNotEmpty(list)) {
			for (SupplierFormSubmissionItem item : list) {
				map.put(item.getId(), item);
			}
		}
		return new ArrayList<SupplierFormSubmissionItem>(map.values());
	}

	@SuppressWarnings("unchecked")
	@Override
	public SupplierFormSubmissionItem findFormSubmissionItem(String formId, String itemId) {
		final Query query = getEntityManager().createQuery("from SupplierFormSubmissionItem si inner join fetch si.supplierFormItem item inner join fetch si.supplierFormSubmition sub where si.id= :itemId and sub.id= :formId");
		query.setParameter("formId", formId);
		query.setParameter("itemId", itemId);
		List<SupplierFormSubmissionItem> uList = query.getResultList();
		if (CollectionUtil.isNotEmpty(uList)) {
			return uList.get(0);
		} else {
			return null;
		}
	}

	@Override
	public boolean resetAttachement(String formSubId, String formSubItemId) {
		final Query query=getEntityManager().createQuery("update SupplierFormSubmissionItem item set fileName = null, fileData = null, contentType = null where item.supplierFormSubmition.id = :formSubId and item.id = :formSubItemId");
		query.setParameter("formSubId", formSubId);
		query.setParameter("formSubItemId", formSubItemId);
		int returnValue = query.executeUpdate();
		return returnValue != 0 ? true : false;
	}
}