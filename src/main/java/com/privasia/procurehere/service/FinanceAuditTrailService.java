package com.privasia.procurehere.service;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpSession;

import com.privasia.procurehere.core.entity.BuyerAuditTrail;
import com.privasia.procurehere.core.entity.FinanceAuditTrail;
import com.privasia.procurehere.core.pojo.TableDataInput;

import net.sf.jasperreports.engine.JasperPrint;

/**
 * @author Ravi
 */
public interface FinanceAuditTrailService {
	/**
	 * @param tenantId
	 * @param input
	 * @param startDate 
	 * @param endDate 
	 * @return
	 */
	List<FinanceAuditTrail> findAuditTrailForTenant(String tenantId, TableDataInput input, Date startDate, Date endDate);

	/**
	 * @param loggedInUserTenantId
	 * @param input
	 * @param startDate 
	 * @param endDate
	 * @return
	 */
	long findTotalFilteredAuditTrailForTenant(String loggedInUserTenantId, TableDataInput input, Date startDate, Date endDate);

	/**
	 * @param loggedInUserTenantId
	 * @return
	 */
	long findTotalAuditTrailForTenant(String loggedInUserTenantId);

	/**
	 * @param loggedInUserTenantId
	 * @param input
	 * @param startDate
	 * @param endDate
	 * @param session
	 * @return
	 */
	JasperPrint getAuditTrailPdf(String loggedInUserTenantId, TableDataInput input, Date startDate, Date endDate, HttpSession session);

	void save(FinanceAuditTrail financeAuditTrail);

}
