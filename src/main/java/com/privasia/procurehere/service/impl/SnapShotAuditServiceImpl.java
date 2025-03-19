package com.privasia.procurehere.service.impl;

import javax.servlet.http.HttpSession;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;

import com.privasia.procurehere.core.entity.Po;
import com.privasia.procurehere.core.entity.PoAudit;
import com.privasia.procurehere.core.entity.RfaEvent;
import com.privasia.procurehere.core.entity.RfaEventAudit;
import com.privasia.procurehere.core.entity.RfiEvent;
import com.privasia.procurehere.core.entity.RfiEventAudit;
import com.privasia.procurehere.core.entity.RfpEvent;
import com.privasia.procurehere.core.entity.RfpEventAudit;
import com.privasia.procurehere.core.entity.RfqEvent;
import com.privasia.procurehere.core.entity.RfqEventAudit;
import com.privasia.procurehere.core.entity.RftEvent;
import com.privasia.procurehere.core.entity.RftEventAudit;
import com.privasia.procurehere.core.entity.SupplierPerformanceAudit;
import com.privasia.procurehere.core.entity.SupplierPerformanceForm;
import com.privasia.procurehere.core.entity.User;
import com.privasia.procurehere.core.enums.AuditActionType;
import com.privasia.procurehere.core.enums.PoAuditType;
import com.privasia.procurehere.core.enums.PoAuditVisibilityType;
import com.privasia.procurehere.core.enums.SupplierPerformanceAuditActionType;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.service.EventAuditService;
import com.privasia.procurehere.service.PoAuditService;
import com.privasia.procurehere.service.PoService;
import com.privasia.procurehere.service.RfaEventService;
import com.privasia.procurehere.service.RfiEventService;
import com.privasia.procurehere.service.RfpEventService;
import com.privasia.procurehere.service.RfqEventService;
import com.privasia.procurehere.service.RftEventService;
import com.privasia.procurehere.service.SupplierPerformanceAuditService;
import com.privasia.procurehere.service.SupplierPerformanceEvaluationService;
import com.privasia.procurehere.core.entity.ContractAudit;
import com.privasia.procurehere.core.entity.ProductContract;
import com.privasia.procurehere.service.ProductContractService;
import com.privasia.procurehere.service.ContractAuditService;

import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.fill.JRSwapFileVirtualizer;

/**
 * @author Sarang
 */

@Service
@EnableAsync
public class SnapShotAuditServiceImpl implements SnapShotAuditService {

	@Autowired
	RfqEventService rfqEventService;

	@Autowired
	RfaEventService rfaEventService;

	@Autowired
	RfpEventService rfpEventService;

	@Autowired
	RftEventService rftEventService;

	@Autowired
	RfiEventService rfiEventService;

	@Autowired
	EventAuditService eventAuditService;

	@Autowired
	MessageSource messageSource;

	@Autowired
	PoService poService;

	@Autowired
	PoAuditService poAuditService;

	@Autowired
	SupplierPerformanceAuditService supplierPerformanceAuditService;

	@Autowired
	SupplierPerformanceEvaluationService supplierPerformanceEvaluationService;

	@Autowired
	ProductContractService productContractService;

	@Autowired
	ContractAuditService contractAuditService;

	private static final Logger LOG = LogManager.getLogger(Global.BUYER_LOG);

	@Override
	@Async
	public void doRfqAudit(RfqEvent event, HttpSession session, RfqEvent persistObj, User loginUser, AuditActionType type, String message, JRSwapFileVirtualizer virtualizer) {
		byte[] summarySnapshot = null;
		try {
			JasperPrint eventSummary = rfqEventService.getEvaluationSummaryPdf(event, loginUser, (String) session.getAttribute(Global.SESSION_TIME_ZONE_KEY), virtualizer);
			summarySnapshot = JasperExportManager.exportReportToPdf(eventSummary);

			RfqEventAudit audit = new RfqEventAudit(loginUser.getBuyer(), persistObj, loginUser, new java.util.Date(), type, messageSource.getMessage(message, new Object[] { persistObj.getEventName(), event.getSuspensionType() }, Global.LOCALE), summarySnapshot);
			eventAuditService.save(audit);
		} catch (Exception e) {
			LOG.error("Error while Store summary PDF as byte : " + e.getMessage(), e);
		}

	}

	@Override
	@Async
	public void doRfaAudit(RfaEvent event, HttpSession session, RfaEvent persistObj, User loginUser, AuditActionType type, String message, JRSwapFileVirtualizer virtualizer) {

		byte[] summarySnapshot = null;
		try {
			JasperPrint eventSummary = rfaEventService.getEvaluationSummaryPdf(event, loginUser, (String) session.getAttribute(Global.SESSION_TIME_ZONE_KEY), virtualizer);
			summarySnapshot = JasperExportManager.exportReportToPdf(eventSummary);
			RfaEventAudit audit = new RfaEventAudit(loginUser.getBuyer(), persistObj, loginUser, new java.util.Date(), type, messageSource.getMessage(message, new Object[] { persistObj.getEventName(), event.getSuspendRemarks() }, Global.LOCALE), summarySnapshot);
			eventAuditService.save(audit);
		} catch (Exception e) {
			LOG.error("Error while Store summary PDF as byte : " + e.getMessage(), e);
		}

	}

	@Override
	@Async
	public void doRfpAudit(RfpEvent event, HttpSession session, RfpEvent persistObj, User loginUser, AuditActionType type, String message, JRSwapFileVirtualizer virtualizer) {

		byte[] summarySnapshot = null;
		try {
			JasperPrint eventSummary = rfpEventService.getEvaluationSummaryPdf(event, loginUser, (String) session.getAttribute(Global.SESSION_TIME_ZONE_KEY), virtualizer);
			summarySnapshot = JasperExportManager.exportReportToPdf(eventSummary);
			RfpEventAudit audit = new RfpEventAudit(loginUser.getBuyer(), persistObj, loginUser, new java.util.Date(), type, messageSource.getMessage(message, new Object[] { persistObj.getEventName(), event.getSuspensionType() }, Global.LOCALE), summarySnapshot);
			eventAuditService.save(audit);
		} catch (Exception e) {
			LOG.error("Error while Store summary PDF as byte : " + e.getMessage(), e);
		}

	}

	@Override
	@Async
	public void doRftAudit(RftEvent event, HttpSession session, RftEvent persistObj, User loginUser, AuditActionType type, String message, JRSwapFileVirtualizer virtualizer) {

		byte[] summarySnapshot = null;
		try {
			JasperPrint eventSummary = rftEventService.getEvaluationSummaryPdf(event, loginUser, (String) session.getAttribute(Global.SESSION_TIME_ZONE_KEY), virtualizer);
			summarySnapshot = JasperExportManager.exportReportToPdf(eventSummary);
			RftEventAudit audit = new RftEventAudit(loginUser.getBuyer(), persistObj, loginUser, new java.util.Date(), type, messageSource.getMessage(message, new Object[] { persistObj.getEventName(), event.getSuspensionType() }, Global.LOCALE), summarySnapshot);
			eventAuditService.save(audit);
		} catch (Exception e) {
			LOG.error("Error while Store summary PDF as byte : " + e.getMessage(), e);
		}

	}

	@Override
	@Async
	public void doRfiAudit(RfiEvent event, HttpSession session, RfiEvent persistObj, User loginUser, AuditActionType type, String message, JRSwapFileVirtualizer virtualizer) {

		byte[] summarySnapshot = null;
		try {
			JasperPrint eventSummary = rfiEventService.getEvaluationSummaryPdf(event, loginUser, (String) session.getAttribute(Global.SESSION_TIME_ZONE_KEY), virtualizer);
			summarySnapshot = JasperExportManager.exportReportToPdf(eventSummary);
			RfiEventAudit audit = new RfiEventAudit(loginUser.getBuyer(), persistObj, loginUser, new java.util.Date(), type, messageSource.getMessage(message, new Object[] { persistObj.getEventName(), event.getSuspensionType() }, Global.LOCALE), summarySnapshot);
			eventAuditService.save(audit);
		} catch (Exception e) {
			LOG.error("Error while Store summary PDF as byte : " + e.getMessage(), e);
		}

	}

	@Override
	public void doSupplierPerformanceFormAudit(SupplierPerformanceForm form, HttpSession session, User loginUser, SupplierPerformanceAuditActionType type, String message, JRSwapFileVirtualizer virtualizer) {

		byte[] summarySnapshot = null;
		try {
			JasperPrint formSummary = supplierPerformanceEvaluationService.getPerformancEvaluationSummaryPdf(form, loginUser, (String) session.getAttribute(Global.SESSION_TIME_ZONE_KEY), virtualizer);
			summarySnapshot = JasperExportManager.exportReportToPdf(formSummary);
			SupplierPerformanceAudit audit = new SupplierPerformanceAudit(form, null, loginUser, new java.util.Date(), type, messageSource.getMessage(message, new Object[] { form.getFormId() }, Global.LOCALE), summarySnapshot, Boolean.TRUE, Boolean.TRUE, Boolean.TRUE, Boolean.TRUE, Boolean.FALSE, Boolean.FALSE);
			supplierPerformanceAuditService.save(audit);
		} catch (Exception e) {
			LOG.error("Error while Store summary PDF as byte : " + e.getMessage(), e);
		}

	}

	@Override
	@Async
	public void doPoAudit(Po po, User loginUser, PoAuditType type, String message, JRSwapFileVirtualizer virtualizer, PoAuditVisibilityType visibilityType) {

		byte[] summarySnapshot = null;
		try {
			JasperPrint eventSummary = poService.getBuyerPoPdf(po, virtualizer);
			summarySnapshot = JasperExportManager.exportReportToPdf(eventSummary);
			PoAudit audit = new PoAudit(loginUser.getBuyer(), po, loginUser, new java.util.Date(), type, message, summarySnapshot, visibilityType);
			poAuditService.save(audit);
		} catch (Exception e) {
			LOG.error("Error while Store Po summary PDF as byte : " + e.getMessage(), e);
		}

	}

	@Override
	@Async
	public void doContractAudit(ProductContract contract, HttpSession session, ProductContract persistObj, User loginUser, AuditActionType type, String message, JRSwapFileVirtualizer virtualizer) {

		try {
			String timeZone = "GMT+8:00";
			if (session != null) {
				timeZone = (String) session.getAttribute(Global.SESSION_TIME_ZONE_KEY);
			}

			byte[] summarySnapshot = null;
			if (virtualizer != null) {
				JasperPrint contractSummary = productContractService.getContractSummaryPdf(contract.getId(), loginUser, timeZone, virtualizer);
				summarySnapshot = JasperExportManager.exportReportToPdf(contractSummary);
			}
			ContractAudit audit = new ContractAudit(loginUser.getBuyer(), persistObj, loginUser, new java.util.Date(), type, message, summarySnapshot);
			contractAuditService.save(audit);
		} catch (Exception e) {
			LOG.error("Error while Store summary PDF as byte : " + e.getMessage(), e);
		}

	}

}
