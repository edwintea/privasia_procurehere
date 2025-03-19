/**
 * 
 */
package com.privasia.procurehere.core.supplier.dao;

import java.util.List;

import com.privasia.procurehere.core.dao.GenericDao;
import com.privasia.procurehere.core.entity.SupplierProjects;

/**
 * @author Giridhar
 */
public interface SupplierProjectsDao extends GenericDao<SupplierProjects, String> {

	/**
	 * @param supplierId
	 * @return
	 */
	List<SupplierProjects> findProjectsForSupplierId(String supplierId);

	/**
	 * @param projectId
	 * @return
	 */
	// SupplierProjects assignedCountriesForProjectId(String projectId);

	/**
	 * @param projectId
	 * @return
	 */
	// SupplierProjects assignedStatesForProjectId(String projectId);

	void deleteById(String id);

	/**
	 * @param supplierId
	 * @return
	 */
	long findTotalTrackRecordCountOfSupplier(String supplierId);
}
