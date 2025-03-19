package com.privasia.procurehere.core.dao;

import java.util.List;

import com.privasia.procurehere.core.entity.GroupCode;
import com.privasia.procurehere.core.entity.ProcurementMethod;
import com.privasia.procurehere.core.pojo.TableDataInput;

/**
 * @author sana
 */
public interface ProcurementMethodDao extends GenericDao<ProcurementMethod, String> {

	/**
	 * @param tenantId
	 * @param input
	 * @return
	 */
	List<ProcurementMethod> findProcurementMethodsForTenant(String tenantId, TableDataInput input);

	/**
	 * @param tenantId
	 * @param input
	 * @return
	 */
	long findTotalFilteredProcurementMethodsForTenant(String tenantId, TableDataInput input);

	/**
	 * @param tenantId
	 * @return
	 */
	long findCountOfProcurementMethodsForTenant(String tenantId);

	/**
	 * @param procurementMethod
	 * @param tenantId
	 * @return
	 */
	boolean isExists(ProcurementMethod procurementMethod, String tenantId);

	/**
	 * @param tenantId
	 * @return
	 */
	List<ProcurementMethod> getAllProcurementMethodByTenantId(String tenantId);

	List<ProcurementMethod> getAllActiveProcurementMethod(String tenantId);

	/**
	 * @param procurementMethod
	 * @param tenantId
	 * @return
	 */
	ProcurementMethod getByProcurementMethod(String procurementMethod, String tenantId);


}
