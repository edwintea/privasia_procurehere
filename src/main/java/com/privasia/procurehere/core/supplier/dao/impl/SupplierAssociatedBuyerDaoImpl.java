/**
 * 
 */
package com.privasia.procurehere.core.supplier.dao.impl;

import java.util.List;

import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import com.privasia.procurehere.core.dao.impl.GenericDaoImpl;
import com.privasia.procurehere.core.entity.IndustryCategory;
import com.privasia.procurehere.core.entity.RequestedAssociatedBuyer;
import com.privasia.procurehere.core.pojo.RequestedAssociatedBuyerPojo;
import com.privasia.procurehere.core.supplier.dao.SupplierAssociatedBuyerDao;
import com.privasia.procurehere.core.utils.CollectionUtil;

/**
 * @author pooja
 */
@Repository()
public class SupplierAssociatedBuyerDaoImpl extends GenericDaoImpl<RequestedAssociatedBuyer, String> implements SupplierAssociatedBuyerDao {

	@SuppressWarnings("unchecked")
	@Override
	public RequestedAssociatedBuyerPojo getRequestedAssociatedBuyerById(String buyerId, String supplierId) {
		String hql = "select distinct NEW com.privasia.procurehere.core.pojo.RequestedAssociatedBuyerPojo(a.id,b.id,b.companyName,b.publishedProfileCommunicationEmail, b.publishedProfileContactNumber,b.publishedProfileContactPerson,b.publishedProfileWebsite,b.publishedProfileInfoToSuppliers,b.publishedProfileMinimumCategories, b.publishedProfileMaximumCategories,a.status,a.requestedDate,a.rejectedDate,a.associatedDate,a.buyerRemark,a.supplierRemark)";
		hql += " from RequestedAssociatedBuyer a left outer join a.buyer as b where a.supplier.id = :supplierId and a.buyer.id = :buyerId";
		final Query query = getEntityManager().createQuery(hql.toString());
		query.setParameter("supplierId", supplierId);
		query.setParameter("buyerId", buyerId);
		List<RequestedAssociatedBuyerPojo> buyerList = query.getResultList();
		if (CollectionUtil.isNotEmpty(buyerList)) {
			return buyerList.get(0);
		} else {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<IndustryCategory> getIndustryCategoriesById(String requestId) {
		String hql = "select a.industryCategory from RequestedAssociatedBuyer a where a.id = :requestId";
		final Query query = getEntityManager().createQuery(hql.toString());
		query.setParameter("requestId", requestId);
		List<IndustryCategory> categoryList = query.getResultList();
		if (CollectionUtil.isNotEmpty(categoryList)) {
			return categoryList;
		} else {
			return null;
		}
	}

}
