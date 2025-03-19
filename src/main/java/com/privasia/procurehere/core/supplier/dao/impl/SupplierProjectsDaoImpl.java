/**
 * 
 */
package com.privasia.procurehere.core.supplier.dao.impl;

import java.util.List;

import javax.persistence.Query;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Repository;

import com.privasia.procurehere.core.dao.impl.GenericDaoImpl;
import com.privasia.procurehere.core.entity.SupplierProjects;
import com.privasia.procurehere.core.supplier.dao.SupplierProjectsDao;
import com.privasia.procurehere.core.utils.Global;

/**
 * @author Arc
 */
@Repository
public class SupplierProjectsDaoImpl extends GenericDaoImpl<SupplierProjects, String> implements SupplierProjectsDao {

	@SuppressWarnings("unused")
	private static final Logger LOG = LogManager.getLogger(Global.SUPPLIER_LOG);

	@Override
	public SupplierProjects findById(String id) {
		final Query query = getEntityManager().createQuery("from SupplierProjects  a  left outer join fetch a.projectIndustries pi where a.id =:id");
		query.setParameter("id", id);
		return (SupplierProjects) query.getSingleResult();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<SupplierProjects> findProjectsForSupplierId(String supplierId) {
		final Query query = getEntityManager().createQuery("select distinct a from SupplierProjects a left outer join fetch a.projectIndustries pi  where a.supplier.id =:id");
		query.setParameter("id", supplierId);
		return query.getResultList();
	}

	/*
	 * @SuppressWarnings("unchecked")
	 * @Override public SupplierProjects assignedCountriesForProjectId(String projectId) { final Query query =
	 * getEntityManager().
	 * createQuery("from SupplierProjects a left outer join fetch a.assignedCountries ac  where a.id =:id");
	 * query.setParameter("id", projectId); List<SupplierProjects> list = query.getResultList(); return
	 * CollectionUtil.isNotEmpty(list) ? list.get(0) : null; }
	 * @SuppressWarnings("unchecked")
	 * @Override public SupplierProjects assignedStatesForProjectId(String projectId) { final Query query =
	 * getEntityManager().
	 * createQuery("from SupplierProjects a left outer join fetch a.assignedStates as s  where a.id =:id");
	 * query.setParameter("id", projectId); List<SupplierProjects> list = query.getResultList(); return
	 * CollectionUtil.isNotEmpty(list) ? list.get(0) : null; }
	 */

	@Override
	public void deleteById(String id) {
		StringBuilder hsql = new StringBuilder("delete from SupplierProjects sp where sp.id= :projectId");
		final Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("projectId", id);
		query.executeUpdate();
	}

	@Override
	public long findTotalTrackRecordCountOfSupplier(String supplierId) {
		StringBuilder hsql = new StringBuilder("select count( distinct sp.id)  from SupplierProjects sp where sp.supplier.id= :supplierId");
		final Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("supplierId", supplierId);
		return ((Number) query.getSingleResult()).longValue();

	}

}
