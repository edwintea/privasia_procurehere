package com.privasia.procurehere.service;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpSession;

import com.privasia.procurehere.core.entity.BuyerAuditTrail;
import com.privasia.procurehere.core.pojo.TableDataInput;

import net.sf.jasperreports.engine.JasperPrint;

/**
 * @author Ravi
 */
public interface BuyerAuditTrailService {
	/**
	 * @param tenantId
	 * @param input
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	List<BuyerAuditTrail> findAuditTrailForTenant(String tenantId, TableDataInput input, Date startDate, Date endDate, String moduleType);

	/**
	 * @param loggedInUserTenantId
	 * @param input
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	long findTotalFilteredAuditTrailForTenant(String loggedInUserTenantId, TableDataInput input, Date startDate, Date endDate, String moduleType);

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
	JasperPrint getAuditTrailPdf(String loggedInUserTenantId, TableDataInput input, Date startDate, Date endDate, HttpSession session, String moduleType);

	void save(BuyerAuditTrail ownerAuditTrail);

}
