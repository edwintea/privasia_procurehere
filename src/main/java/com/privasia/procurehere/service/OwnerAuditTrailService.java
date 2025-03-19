package com.privasia.procurehere.service;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpSession;

import com.privasia.procurehere.core.entity.OwnerAuditTrail;
import com.privasia.procurehere.core.pojo.TableDataInput;

import net.sf.jasperreports.engine.JasperPrint;

/**
 * @author parveen
 */
public interface OwnerAuditTrailService {
	/**
	 * @param tenantId
	 * @param input
	 * @param startDate
	 * @param endDate
	 * @param moduleType 
	 * @return
	 */
	List<OwnerAuditTrail> findOwnerAuditTrailForTenant(String tenantId, TableDataInput input, Date startDate, Date endDate, String moduleType);

	/**
	 * @param loggedInUserTenantId
	 * @param input
	 * @param startDate
	 * @param endDate
	 * @param moduleType 
	 * @return
	 */
	long findTotalFilteredOwnerAuditTrailForTenant(String loggedInUserTenantId, TableDataInput input, Date startDate, Date endDate, String moduleType);

	/**
	 * @param loggedInUserTenantId
	 * @return
	 */
	long findTotalOwnerAuditTrailForTenant(String loggedInUserTenantId);

	/**
	 * @param loggedInUserTenantId
	 * @param input
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	JasperPrint getAuditTrailPdf(String loggedInUserTenantId, TableDataInput input, Date startDate, Date endDate, HttpSession session,String moduleType);

	void save(OwnerAuditTrail ownerAudit);

}
