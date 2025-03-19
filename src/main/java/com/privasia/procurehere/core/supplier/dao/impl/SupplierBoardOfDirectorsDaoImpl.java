package com.privasia.procurehere.core.supplier.dao.impl;

import java.util.List;

import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import com.privasia.procurehere.core.dao.impl.GenericDaoImpl;
import com.privasia.procurehere.core.entity.SupplierBoardOfDirectors;
import com.privasia.procurehere.core.supplier.dao.SupplierBoardOfDirectorsDao;

@Repository
public class SupplierBoardOfDirectorsDaoImpl extends GenericDaoImpl<SupplierBoardOfDirectors, String> implements SupplierBoardOfDirectorsDao {

	@SuppressWarnings("unchecked")
	@Override
	public List<SupplierBoardOfDirectors> findAllBySupplierId(String id) {
		final Query query = getEntityManager().createQuery("select new com.privasia.procurehere.core.entity.SupplierBoardOfDirectors(a.id, a.directorName, a.idType, a.idNumber, a.dirType, a.dirEmail, a.dirContact) from SupplierBoardOfDirectors a inner join a.supplier sp where sp.id =:id");
		query.setParameter("id", id);
		return query.getResultList();
	}

	@Override
	public void deleteById(String id) {
		final Query query = getEntityManager().createQuery("delete from SupplierBoardOfDirectors a where a.id =:id");
		query.setParameter("id", id);
		query.executeUpdate();

	}

	@SuppressWarnings("unchecked")
	@Override
	public List<SupplierBoardOfDirectors> findIfRecordExistsWithDuplicateIdnumber(String idNumber) {
		final Query query = getEntityManager().createQuery("from SupplierBoardOfDirectors a where a.idNumber =:idNumber");
		query.setParameter("idNumber", idNumber);
		return query.getResultList();
	}

	@Override
	public long findTotalBoardOfDirectorBySuppId(String supplierId) {
		StringBuilder hsql = new StringBuilder("select  count( distinct sp.id)  from SupplierBoardOfDirectors sp where sp.supplier.id= :supplierId");
		final Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("supplierId", supplierId);
		return ((Number) query.getSingleResult()).longValue();
	}

}
