/**
 * 
 */
package com.privasia.procurehere.web.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.privasia.procurehere.core.entity.RfaSorEvaluationComments;
import com.privasia.procurehere.core.entity.RfiSorEvaluationComments;
import com.privasia.procurehere.core.entity.RfpSorEvaluationComments;
import com.privasia.procurehere.core.entity.RfqSorEvaluationComments;
import com.privasia.procurehere.core.entity.RftSorEvaluationComments;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.support.ByteArrayMultipartFileEditor;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.privasia.procurehere.core.dao.BuyerAuditTrailDao;
import com.privasia.procurehere.core.entity.BuyerAuditTrail;
import com.privasia.procurehere.core.entity.Envelop;
import com.privasia.procurehere.core.entity.ErpSetup;
import com.privasia.procurehere.core.entity.Event;
import com.privasia.procurehere.core.entity.RfaBqEvaluationComments;
import com.privasia.procurehere.core.entity.RfaBqTotalEvaluationComments;
import com.privasia.procurehere.core.entity.RfaCqEvaluationComments;
import com.privasia.procurehere.core.entity.RfaEnvelop;
import com.privasia.procurehere.core.entity.RfaEvaluatorDeclaration;
import com.privasia.procurehere.core.entity.RfaEvaluatorUser;
import com.privasia.procurehere.core.entity.RfaEvent;
import com.privasia.procurehere.core.entity.RfaEventAudit;
import com.privasia.procurehere.core.entity.RfaEventSupplier;
import com.privasia.procurehere.core.entity.RfaSupplierBq;
import com.privasia.procurehere.core.entity.RfaUnMaskedUser;
import com.privasia.procurehere.core.entity.RfiCqEvaluationComments;
import com.privasia.procurehere.core.entity.RfiEnvelop;
import com.privasia.procurehere.core.entity.RfiEvaluatorDeclaration;
import com.privasia.procurehere.core.entity.RfiEvaluatorUser;
import com.privasia.procurehere.core.entity.RfiEvent;
import com.privasia.procurehere.core.entity.RfiEventAudit;
import com.privasia.procurehere.core.entity.RfiEventSupplier;
import com.privasia.procurehere.core.entity.RfiUnMaskedUser;
import com.privasia.procurehere.core.entity.RfpBqEvaluationComments;
import com.privasia.procurehere.core.entity.RfpBqTotalEvaluationComments;
import com.privasia.procurehere.core.entity.RfpCqEvaluationComments;
import com.privasia.procurehere.core.entity.RfpEnvelop;
import com.privasia.procurehere.core.entity.RfpEvaluatorDeclaration;
import com.privasia.procurehere.core.entity.RfpEvaluatorUser;
import com.privasia.procurehere.core.entity.RfpEvent;
import com.privasia.procurehere.core.entity.RfpEventAudit;
import com.privasia.procurehere.core.entity.RfpEventSupplier;
import com.privasia.procurehere.core.entity.RfpSupplierBq;
import com.privasia.procurehere.core.entity.RfpUnMaskedUser;
import com.privasia.procurehere.core.entity.RfqBqEvaluationComments;
import com.privasia.procurehere.core.entity.RfqBqTotalEvaluationComments;
import com.privasia.procurehere.core.entity.RfqCqEvaluationComments;
import com.privasia.procurehere.core.entity.RfqEnvelop;
import com.privasia.procurehere.core.entity.RfqEvaluatorDeclaration;
import com.privasia.procurehere.core.entity.RfqEvaluatorUser;
import com.privasia.procurehere.core.entity.RfqEvent;
import com.privasia.procurehere.core.entity.RfqEventAudit;
import com.privasia.procurehere.core.entity.RfqEventSupplier;
import com.privasia.procurehere.core.entity.RfqSupplierBq;
import com.privasia.procurehere.core.entity.RfqUnMaskedUser;
import com.privasia.procurehere.core.entity.RftBqEvaluationComments;
import com.privasia.procurehere.core.entity.RftBqTotalEvaluationComments;
import com.privasia.procurehere.core.entity.RftCqEvaluationComments;
import com.privasia.procurehere.core.entity.RftEnvelop;
import com.privasia.procurehere.core.entity.RftEvaluatorDeclaration;
import com.privasia.procurehere.core.entity.RftEvaluatorUser;
import com.privasia.procurehere.core.entity.RftEvent;
import com.privasia.procurehere.core.entity.RftEventAudit;
import com.privasia.procurehere.core.entity.RftEventSupplier;
import com.privasia.procurehere.core.entity.RftSupplierBq;
import com.privasia.procurehere.core.entity.RftUnMaskedUser;
import com.privasia.procurehere.core.entity.Supplier;
import com.privasia.procurehere.core.enums.AuditActionType;
import com.privasia.procurehere.core.enums.AuditTypes;
import com.privasia.procurehere.core.enums.EvaluationStatus;
import com.privasia.procurehere.core.enums.ModuleType;
import com.privasia.procurehere.core.enums.RfxTypes;
import com.privasia.procurehere.core.pojo.AuthenticatedUser;
import com.privasia.procurehere.core.pojo.EventEvaluationPojo;
import com.privasia.procurehere.core.pojo.EventPermissions;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.MaskUtils;
import com.privasia.procurehere.core.utils.SecurityLibrary;
import com.privasia.procurehere.core.utils.StringUtils;
import com.privasia.procurehere.service.ErpSetupService;
import com.privasia.procurehere.service.EventAuditService;
import com.privasia.procurehere.service.RfaEventService;
import com.privasia.procurehere.service.RfiEventService;
import com.privasia.procurehere.service.RfpEventService;
import com.privasia.procurehere.service.RfqEventService;
import com.privasia.procurehere.service.RftEventService;
import com.privasia.procurehere.service.UserService;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.fill.JRSwapFileVirtualizer;
import net.sf.jasperreports.engine.util.JRSwapFile;

/**
 * @author Arc
 */
@Controller
@RequestMapping(value = "/buyer")
public class EventEvaluationController extends EventEvaluationBase implements Serializable {

	private static final long serialVersionUID = 1898884380040555905L;

	@Autowired
	UserService userService;

	@Autowired
	EventAuditService auditService;

	@Autowired
	ErpSetupService erpSetupService;

	@Autowired
	RfiEventService rfieventService;

	@Autowired
	RfqEventService rfqeventService;

	@Autowired
	RfpEventService rfpeventService;

	@Autowired
	RftEventService rfteventService;

	@Autowired
	RfaEventService rfaeventService;

	@Autowired
	EventAuditService eventAuditService;

	@Autowired
	BuyerAuditTrailDao buyerAuditTrailDao;

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		StringTrimmerEditor stringtrimmer = new StringTrimmerEditor(true);
		binder.registerCustomEditor(String.class, stringtrimmer);
		binder.registerCustomEditor(byte[].class, new ByteArrayMultipartFileEditor());

	}

	@RequestMapping(path = "/submissionReport/{eventType}/{eventId}/{evelopId}", method = RequestMethod.GET)
	public String submissionReport(Model model, @PathVariable("eventType") RfxTypes eventType, @PathVariable("eventId") String eventId, @PathVariable("evelopId") String evelopId, HttpSession session) throws JsonProcessingException {

		String strTimeZone = (String) session.getAttribute(Global.SESSION_TIME_ZONE_KEY);
		TimeZone timeZone = TimeZone.getDefault();
		if (strTimeZone != null) {
			timeZone = TimeZone.getTimeZone(strTimeZone);
		}
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm");
		sdf.setTimeZone(timeZone);
		boolean leadEvaluator = false;
		Event event = null;
		Envelop envelop = null;
		List<EventEvaluationPojo> cqList = null;
		List<EventEvaluationPojo> bqList = null;
		List<EventEvaluationPojo> sorList = null;
		List<Supplier> selectedSuppliers = null;
		List<Supplier> suppliers = null;
		Boolean withTax = null;
		EventPermissions eventPermissions = null;
		switch (eventType) {
		case RFA:
			eventPermissions = rfaEventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), eventId);
			envelop = rfaEnvelopService.getEnvelopForEvaluationById(evelopId, SecurityLibrary.getLoggedInUser());
			if (envelop != null && envelop.getLeadEvaluater() != null && envelop.getLeadEvaluater().getId().equals(SecurityLibrary.getLoggedInUser().getId())) {
				leadEvaluator = true;
				if (envelop.getEvaluatorSummaryDate() != null) {
					envelop.setEvaluatorSummaryDateStr(sdf.format(envelop.getEvaluatorSummaryDate()));
				}
			}
			List<RfaEvaluatorUser> evaluationSummaryList = rfaEnvelopService.getEvaluationSummaryRemarks(eventId, evelopId, (leadEvaluator ? null : SecurityLibrary.getLoggedInUser()));
			for (RfaEvaluatorUser rfaEvaluatorUser : evaluationSummaryList) {
				if (rfaEvaluatorUser.getEvaluatorSummaryDate() != null) {
					rfaEvaluatorUser.setSummaryDate(sdf.format(rfaEvaluatorUser.getEvaluatorSummaryDate()));
				}
			}

			event = ((RfaEnvelop) envelop).getRfxEvent();
			cqList = rfaSupplierCqItemService.getCqEvaluationData(eventId, evelopId, selectedSuppliers, SecurityLibrary.getLoggedInUser());
			bqList = rfaSupplierBqItemService.getBqEvaluationData(eventId, evelopId, SecurityLibrary.getLoggedInUser(), "0", selectedSuppliers, null, null, null, 0, SecurityLibrary.getLoggedInUser().getBqPageLength());
			sorList = rfaSupplierSorItemService.getSorEvaluationData(eventId, evelopId, selectedSuppliers, SecurityLibrary.getLoggedInUser(), "0", null, null, null, 0, SecurityLibrary.getLoggedInUser().getBqPageLength());
			suppliers = rfaEventSupplierService.getEventSuppliersForEvaluation(eventId);

			RfaEvent rfaevent = rfaeventService.getRfaEventByeventId(eventId);
			for (RfaUnMaskedUser unmask : rfaevent.getUnMaskedUsers()) {
				if (event.getViewSupplerName() != null && !event.getViewSupplerName() && !unmask.getUserUnmasked()) {
					for (Supplier supplier : suppliers) {
						supplier.setCompanyName(MaskUtils.maskName(envelop.getPreFix(), supplier.getId(), envelop.getId()));
					}
				}
				/*
				 * Collections.sort(suppliers, new Comparator<Supplier>() { public int compare(Supplier o1, Supplier o2)
				 * { if (o1.getCompanyName() == null || o2.getCompanyName() == null) { return 0; } return
				 * o1.getCompanyName().compareTo(o2.getCompanyName()); } });
				 */
			}

			model.addAttribute("eventSuppliers", suppliers);
			model.addAttribute("eventPermissions", eventPermissions);
			model.addAttribute("evaluationSummaryList", evaluationSummaryList);
			if (CollectionUtil.isNotEmpty(bqList)) {
				withTax = bqList.get(0).getWithTax();
			}

			try {
				RfaEventAudit rfaEventAudit = new RfaEventAudit();
				rfaEventAudit.setActionDate(new Date());
				rfaEventAudit.setActionBy(SecurityLibrary.getLoggedInUser());
				rfaEventAudit.setAction(AuditActionType.View);
				rfaEventAudit.setDescription("Envelope '" + envelop.getEnvelopTitle() + "' is viewed");
				rfaEventAudit.setEvent((RfaEvent) event);
				auditService.save(rfaEventAudit);
			} catch (Exception e) {
				LOG.error(e.getMessage(), e);
			}

			try {
				LOG.info("--------------------------BEFORE AUDIT---------------------------------------");
				BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.VIEW, "Envelope '" + envelop.getEnvelopTitle() + "' is viewed for Event '" + event.getEventId() + "' ", SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser(), new Date(), ModuleType.RFA);
				buyerAuditTrailDao.save(buyerAuditTrail);
				LOG.info("--------------------------AFTER AUDIT---------------------------------------");
			} catch (Exception e) {
				LOG.error("Error while recording auction event Resume " + e.getMessage(), e);
			}
			break;
		case RFI: {
			LOG.info("Starting RFI Submission ");
			envelop = rfiEnvelopService.getEnvelopForEvaluationById(evelopId, SecurityLibrary.getLoggedInUser());
			if (envelop != null && envelop.getLeadEvaluater() != null && envelop.getLeadEvaluater().getId().equals(SecurityLibrary.getLoggedInUser().getId())) {
				leadEvaluator = true;
				if (envelop.getEvaluatorSummaryDate() != null) {
					envelop.setEvaluatorSummaryDateStr(sdf.format(envelop.getEvaluatorSummaryDate()));
				}
			}
			event = ((RfiEnvelop) envelop).getRfxEvent();

			eventPermissions = rfiEventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), event.getId());
			List<RfiEvaluatorUser> rfiEvaluationSummaryList = rfiEnvelopService.getEvaluationSummaryRemarks(eventId, evelopId, (leadEvaluator) ? null : SecurityLibrary.getLoggedInUser());
			for (RfiEvaluatorUser evaluatorUser : rfiEvaluationSummaryList) {
				if (evaluatorUser.getEvaluatorSummaryDate() != null) {
					evaluatorUser.setSummaryDate(sdf.format(evaluatorUser.getEvaluatorSummaryDate()));
				}

			}
			LOG.info("Getting CQ Details");
			cqList = rfiSupplierCqItemService.getCqEvaluationData(eventId, evelopId, selectedSuppliers, SecurityLibrary.getLoggedInUser());
			LOG.info("COmpleted CQ Details");
			sorList = rfiSupplierSorItemService.getSorEvaluationData(eventId, evelopId, selectedSuppliers, SecurityLibrary.getLoggedInUser(), "0", null, null, null, 0, SecurityLibrary.getLoggedInUser().getBqPageLength());

			suppliers = rfiEventSupplierService.getEventSuppliersForEvaluation(eventId);
			RfiEvent rfievent = rfieventService.getRfiEventByeventId(eventId);
			for (RfiUnMaskedUser unmask : rfievent.getUnMaskedUsers()) {
				if (event.getViewSupplerName() != null && !event.getViewSupplerName() && !unmask.getUserUnmasked()) {
					for (Supplier supplier : suppliers) {
						supplier.setCompanyName(MaskUtils.maskName(envelop.getPreFix(), supplier.getId(), envelop.getId()));
					}

				}
				/*
				 * Collections.sort(suppliers, new Comparator<Supplier>() { public int compare(Supplier o1, Supplier o2)
				 * { if (o1.getCompanyName() == null || o2.getCompanyName() == null) { return 0; } return
				 * o1.getCompanyName().compareTo(o2.getCompanyName()); } });
				 */
			}

			model.addAttribute("eventSuppliers", suppliers);
			model.addAttribute("evaluationSummaryList", rfiEvaluationSummaryList);
			model.addAttribute("eventPermissions", eventPermissions);

			try {
				RfiEventAudit rfiEventAudit = new RfiEventAudit();
				rfiEventAudit.setActionDate(new Date());
				rfiEventAudit.setActionBy(SecurityLibrary.getLoggedInUser());
				rfiEventAudit.setAction(AuditActionType.View);
				rfiEventAudit.setDescription("Envelope '" + envelop.getEnvelopTitle() + "' is viewed");
				rfiEventAudit.setEvent((RfiEvent) event);
				auditService.save(rfiEventAudit);
			} catch (Exception e) {
				LOG.error(e.getMessage(), e);
			}
			try {
				LOG.info("--------------------------BEFORE AUDIT---------------------------------------");
				BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.VIEW, "Envelop '" + envelop.getEnvelopTitle() + "' is viewed for Event '" + event.getEventId() + "' ", SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser(), new Date(), ModuleType.RFI);
				buyerAuditTrailDao.save(buyerAuditTrail);
				LOG.info("--------------------------AFTER AUDIT---------------------------------------");
			} catch (Exception e) {
				LOG.error("Error while recording auction event Resume " + e.getMessage(), e);
			}
			break;
		}
		case RFP: {
			envelop = rfpEnvelopService.getEnvelopForEvaluationById(evelopId, SecurityLibrary.getLoggedInUser());
			if (envelop != null && envelop.getLeadEvaluater() != null && envelop.getLeadEvaluater().getId().equals(SecurityLibrary.getLoggedInUser().getId())) {
				leadEvaluator = true;
				if (envelop.getEvaluatorSummaryDate() != null) {
					envelop.setEvaluatorSummaryDateStr(sdf.format(envelop.getEvaluatorSummaryDate()));
				}
				LOG.info(envelop.getFileName());
			}
			event = ((RfpEnvelop) envelop).getRfxEvent();
			eventPermissions = rfpEventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), event.getId());
			List<RfpEvaluatorUser> rfpEvaluationSummaryList = rfpEnvelopService.getEvaluationSummaryRemarks(eventId, evelopId, (leadEvaluator ? null : SecurityLibrary.getLoggedInUser()));
			for (RfpEvaluatorUser evaluatorUser : rfpEvaluationSummaryList) {
				if (evaluatorUser.getEvaluatorSummaryDate() != null) {
					evaluatorUser.setSummaryDate(sdf.format(evaluatorUser.getEvaluatorSummaryDate()));
				}
			}

			cqList = rfpSupplierCqItemService.getCqEvaluationData(eventId, evelopId, selectedSuppliers, SecurityLibrary.getLoggedInUser());
			bqList = rfpSupplierBqItemService.getBqEvaluationData(eventId, evelopId, selectedSuppliers, SecurityLibrary.getLoggedInUser(), "0", null, null, null, 0, SecurityLibrary.getLoggedInUser().getBqPageLength());
			sorList = rfpSupplierSorItemService.getSorEvaluationData(eventId, evelopId, selectedSuppliers, SecurityLibrary.getLoggedInUser(), "0", null, null, null, 0, SecurityLibrary.getLoggedInUser().getBqPageLength());
			suppliers = rfpEventSupplierService.getEventSuppliersForEvaluation(eventId);

			RfpEvent rfpevent = rfpeventService.getRfpEventByeventId(eventId);
			for (RfpUnMaskedUser unmask : rfpevent.getUnMaskedUsers()) {
				if (event.getViewSupplerName() != null && !event.getViewSupplerName() && !unmask.getUserUnmasked()) {
					for (Supplier supplier : suppliers) {
						supplier.setCompanyName(MaskUtils.maskName(envelop.getPreFix(), supplier.getId(), envelop.getId()));
					}
				}
				/*
				 * Collections.sort(suppliers, new Comparator<Supplier>() { public int compare(Supplier o1, Supplier o2)
				 * { if (o1.getCompanyName() == null || o2.getCompanyName() == null) { return 0; } return
				 * o1.getCompanyName().compareTo(o2.getCompanyName()); } });
				 */
			}
			model.addAttribute("eventSuppliers", suppliers);
			model.addAttribute("eventPermissions", eventPermissions);
			model.addAttribute("evaluationSummaryList", rfpEvaluationSummaryList);
			if (CollectionUtil.isNotEmpty(bqList)) {
				withTax = bqList.get(0).getWithTax();
			}
			try {
				RfpEventAudit rfpEventAudit = new RfpEventAudit();
				rfpEventAudit.setActionDate(new Date());
				rfpEventAudit.setActionBy(SecurityLibrary.getLoggedInUser());
				rfpEventAudit.setAction(AuditActionType.View);
				rfpEventAudit.setDescription("Envelope '" + envelop.getEnvelopTitle() + "' is viewed");
				rfpEventAudit.setEvent((RfpEvent) event);
				auditService.save(rfpEventAudit);
			} catch (Exception e) {
				LOG.error(e.getMessage(), e);
			}

			try {
				LOG.info("--------------------------BEFORE AUDIT---------------------------------------");
				BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.VIEW, "Envelop '" + envelop.getEnvelopTitle() + "' is viewed for Event '" + event.getEventId() + "' ", SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser(), new Date(), ModuleType.RFP);
				buyerAuditTrailDao.save(buyerAuditTrail);
				LOG.info("--------------------------AFTER AUDIT---------------------------------------");
			} catch (Exception e) {
				LOG.error("Error while recording auction event Resume " + e.getMessage(), e);
			}
			break;
		}
		case RFQ: {
			envelop = rfqEnvelopService.getEnvelopForEvaluationById(evelopId, SecurityLibrary.getLoggedInUser());
			if (envelop.getLeadEvaluater() != null && envelop.getLeadEvaluater().getId().equals(SecurityLibrary.getLoggedInUser().getId())) {
				leadEvaluator = true;
				if (envelop.getEvaluatorSummaryDate() != null) {
					envelop.setEvaluatorSummaryDateStr(sdf.format(envelop.getEvaluatorSummaryDate()));
				}
			}
			event = ((RfqEnvelop) envelop).getRfxEvent();
			eventPermissions = rfqEventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), event.getId());
			List<RfqEvaluatorUser> rfqEvaluationSummaryList = rfqEnvelopService.getEvaluationSummaryRemarks(eventId, evelopId, (leadEvaluator ? null : SecurityLibrary.getLoggedInUser()));
			for (RfqEvaluatorUser evaluatorUser : rfqEvaluationSummaryList) {
				if (evaluatorUser.getEvaluatorSummaryDate() != null) {
					evaluatorUser.setSummaryDate(sdf.format(evaluatorUser.getEvaluatorSummaryDate()));
				}
			}
			cqList = rfqSupplierCqItemService.getCqEvaluationData(eventId, evelopId, selectedSuppliers, SecurityLibrary.getLoggedInUser());
			bqList = rfqSupplierBqItemService.getBqEvaluationData(eventId, evelopId, selectedSuppliers, SecurityLibrary.getLoggedInUser(), "0", null, null, null, 0, SecurityLibrary.getLoggedInUser().getBqPageLength());
			sorList = rfqSupplierSorItemService.getSorEvaluationData(eventId, evelopId, selectedSuppliers, SecurityLibrary.getLoggedInUser(), "0", null, null, null, 0, SecurityLibrary.getLoggedInUser().getBqPageLength());
			suppliers = rfqEventSupplierService.getEventSuppliersForEvaluation(eventId);

			RfqEvent rfqevent = rfqeventService.getRfqEventByeventId(eventId);
			for (RfqUnMaskedUser unmask : rfqevent.getUnMaskedUsers()) {
				if (event.getViewSupplerName() != null && !event.getViewSupplerName() && !unmask.getUserUnmasked()) {
					for (Supplier supplier : suppliers) {
						supplier.setCompanyName(MaskUtils.maskName(envelop.getPreFix(), supplier.getId(), envelop.getId()));
					}
				}

				/*
				 * Collections.sort(suppliers, new Comparator<Supplier>() { public int compare(Supplier o1, Supplier o2)
				 * { if (o1.getCompanyName() == null || o2.getCompanyName() == null) { return 0; } return
				 * o1.getCompanyName().compareTo(o2.getCompanyName()); } });
				 */
			}

			model.addAttribute("eventSuppliers", suppliers);
			model.addAttribute("eventPermissions", eventPermissions);
			model.addAttribute("evaluationSummaryList", rfqEvaluationSummaryList);
			if (CollectionUtil.isNotEmpty(bqList)) {
				withTax = bqList.get(0).getWithTax();
			}
			try {
				RfqEventAudit rfqEventAudit = new RfqEventAudit();
				rfqEventAudit.setActionDate(new Date());
				rfqEventAudit.setActionBy(SecurityLibrary.getLoggedInUser());
				rfqEventAudit.setAction(AuditActionType.View);
				rfqEventAudit.setDescription("Envelope '" + envelop.getEnvelopTitle() + "' is viewed");
				rfqEventAudit.setEvent((RfqEvent) event);
				auditService.save(rfqEventAudit);
			} catch (Exception e) {
				LOG.error(e.getMessage(), e);
			}
			try {
				LOG.info("--------------------------BEFORE AUDIT---------------------------------------");
				BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.VIEW, "Envelop '" + envelop.getEnvelopTitle() + "' is viewed for Event '" + event.getEventId() + "' ", SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser(), new Date(), ModuleType.RFQ);
				buyerAuditTrailDao.save(buyerAuditTrail);
				LOG.info("--------------------------AFTER AUDIT---------------------------------------");
			} catch (Exception e) {
				LOG.error("Error while recording auction event Resume " + e.getMessage(), e);
			}
			break;
		}
		case RFT: {
			envelop = rftEnvelopService.getRftEnvelopForEvaluationById(evelopId, SecurityLibrary.getLoggedInUser());
			if (envelop.getLeadEvaluater() != null && envelop.getLeadEvaluater().getId().equals(SecurityLibrary.getLoggedInUser().getId())) {
				leadEvaluator = true;
				if (envelop.getEvaluatorSummaryDate() != null) {
					envelop.setEvaluatorSummaryDateStr(sdf.format(envelop.getEvaluatorSummaryDate()));
				}
			}
			event = ((RftEnvelop) envelop).getRfxEvent();
			eventPermissions = rftEventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), event.getId());
			List<RftEvaluatorUser> rftEvaluationSummaryList = rftEnvelopService.getEvaluationSummaryRemarks(eventId, evelopId, (leadEvaluator ? null : SecurityLibrary.getLoggedInUser()));
			for (RftEvaluatorUser evaluatorUser : rftEvaluationSummaryList) {
				if (evaluatorUser.getEvaluatorSummaryDate() != null) {
					evaluatorUser.setSummaryDate(sdf.format(evaluatorUser.getEvaluatorSummaryDate()));
				}
			}

			cqList = rftSupplierCqItemService.getCqEvaluationData(eventId, evelopId, selectedSuppliers, SecurityLibrary.getLoggedInUser());
			bqList = rftSupplierBqItemService.getBqEvaluationData(eventId, evelopId, selectedSuppliers, SecurityLibrary.getLoggedInUser(), "0", null, null, null, 0, SecurityLibrary.getLoggedInUser().getBqPageLength());
			sorList = rftSupplierSorItemService.getSorEvaluationData(eventId, evelopId, selectedSuppliers, SecurityLibrary.getLoggedInUser(), "0", null, null, null, 0, SecurityLibrary.getLoggedInUser().getBqPageLength());
			suppliers = rftEventSupplierService.getEventSuppliersForEvaluation(eventId);

			RftEvent rftevent = rfteventService.getRftEventByeventId(eventId);
			for (RftUnMaskedUser unmask : rftevent.getUnMaskedUsers()) {
				if (event.getViewSupplerName() != null && !event.getViewSupplerName() && !unmask.getUserUnmasked()) {
					for (Supplier supplier : suppliers) {
						supplier.setCompanyName(MaskUtils.maskName(envelop.getPreFix(), supplier.getId(), envelop.getId()));
					}
				}
				/*
				 * Collections.sort(suppliers, new Comparator<Supplier>() { public int compare(Supplier o1, Supplier o2)
				 * { if (o1.getCompanyName() == null || o2.getCompanyName() == null) { return 0; } return
				 * o1.getCompanyName().compareTo(o2.getCompanyName()); } });
				 */
			}
			model.addAttribute("evaluationSummaryList", rftEvaluationSummaryList);
			model.addAttribute("eventSuppliers", suppliers);
			model.addAttribute("eventPermissions", eventPermissions);
			if (CollectionUtil.isNotEmpty(bqList)) {
				withTax = bqList.get(0).getWithTax();
			}
			try {
				RftEventAudit rftEventAudit = new RftEventAudit();
				rftEventAudit.setActionDate(new Date());
				rftEventAudit.setActionBy(SecurityLibrary.getLoggedInUser());
				rftEventAudit.setAction(AuditActionType.View);
				rftEventAudit.setDescription("Envelope '" + envelop.getEnvelopTitle() + "' is viewed");
				rftEventAudit.setEvent((RftEvent) event);
				auditService.save(rftEventAudit);
			} catch (Exception e) {
				LOG.error(e.getMessage(), e);
			}
			try {
				LOG.info("--------------------------BEFORE AUDIT---------------------------------------");
				BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.VIEW, "Envelop '" + envelop.getEnvelopTitle() + "' is viewed for Event '" + event.getEventId() + "' ", SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser(), new Date(), ModuleType.RFT);
				buyerAuditTrailDao.save(buyerAuditTrail);
				LOG.info("--------------------------AFTER AUDIT---------------------------------------");
			} catch (Exception e) {
				LOG.error("Error while recording auction event Resume " + e.getMessage(), e);
			}
			break;
		}
		default:
			break;

		}

		if (CollectionUtil.isNotEmpty(suppliers)) {
			int count = 0;
			List<String> list = new ArrayList<String>();
			for (Supplier sup : suppliers) {
				if (!sup.isDisqualified()) {
					count++;
				}
				list.add(sup.getId());
			}
			if (count == 0) {
				model.addAttribute("showComparision", true);
			} else {
				model.addAttribute("showComparision", false);
			}
			model.addAttribute("selectedSuppliers", list);
		}

		ErpSetup setup = erpSetupService.getErpConfigBytenantId(SecurityLibrary.getLoggedInUserTenantId());
		if (setup != null) {
			model.addAttribute("erpEnable", setup.getIsErpEnable());
		} else {
			model.addAttribute("erpEnable", false);
		}

		model.addAttribute("withTax", withTax);
		model.addAttribute("withOrWithoutTax", "0");
		model.addAttribute("evaluation", cqList);
		model.addAttribute("bqEvaluation", bqList);
		model.addAttribute("sorEvaluation", sorList);

		model.addAttribute("event", event);
		model.addAttribute("envelop", envelop);
		model.addAttribute("leadEvaluator", leadEvaluator);

		return "submissionReport";
	}

	@RequestMapping(path = "/submissionReport/{eventType}/{eventId}/{evelopId}", method = RequestMethod.POST)
	public String filterSubmissionReport(Model model, @PathVariable("eventType") RfxTypes eventType, @PathVariable("eventId") String eventId, @PathVariable("evelopId") String envelopId, @RequestParam(required = false, name = "selectedSuppliers") List<String> suppliers, @RequestParam(required = false, name = "withOrWithoutTax") String withOrWithoutTax) throws JsonProcessingException {
		Event event = null;
		LOG.info(suppliers);
		Envelop envelop = null;
		List<EventEvaluationPojo> cqList = null;
		List<EventEvaluationPojo> bqList = null;
		List<EventEvaluationPojo> sorList = null;
		List<Supplier> selectedSuppliers = null;
		List<Supplier> eventSuppliers = null;
		Boolean withTax = null;
		LOG.info("suppliers : " + (suppliers != null ? suppliers.size() : 0));
		if (CollectionUtil.isNotEmpty(suppliers)) {
			selectedSuppliers = supplierService.getAllSupplierFromIds(suppliers);
			LOG.info("size  of supplier List " + selectedSuppliers.size());
		}
		switch (eventType) {
		case RFA: {
			envelop = rfaEnvelopService.getEnvelopForEvaluationById(envelopId, SecurityLibrary.getLoggedInUser());
			event = ((RfaEnvelop) envelop).getRfxEvent();
			cqList = rfaSupplierCqItemService.getCqEvaluationData(eventId, envelopId, selectedSuppliers, SecurityLibrary.getLoggedInUser());
			bqList = rfaSupplierBqItemService.getBqEvaluationData(eventId, envelopId, SecurityLibrary.getLoggedInUser(), withOrWithoutTax, selectedSuppliers, null, null, null, 0, SecurityLibrary.getLoggedInUser().getBqPageLength());
			sorList = rfaSupplierSorItemService.getSorEvaluationData(eventId, envelopId, selectedSuppliers, SecurityLibrary.getLoggedInUser(), "0", null, null, null, 0, SecurityLibrary.getLoggedInUser().getBqPageLength());
			eventSuppliers = rfaEventSupplierService.getEventSuppliersForEvaluation(eventId);
			if (event.getViewSupplerName() != null && !event.getViewSupplerName() && !event.getDisableMasking()) {
				for (Supplier supplier : eventSuppliers) {
					supplier.setCompanyName(MaskUtils.maskName(envelop.getPreFix(), supplier.getId(), envelop.getId()));
				}
			}
			model.addAttribute("eventSuppliers", eventSuppliers);
			model.addAttribute("eventPermissions", rfaEventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), eventId));
			if (CollectionUtil.isNotEmpty(bqList)) {
				withTax = bqList.get(0).getWithTax();
			}
			break;
		}
		case RFI: {
			envelop = rfiEnvelopService.getEnvelopForEvaluationById(envelopId, SecurityLibrary.getLoggedInUser());
			event = ((RfiEnvelop) envelop).getRfxEvent();
			cqList = rfiSupplierCqItemService.getCqEvaluationData(eventId, envelopId, selectedSuppliers, SecurityLibrary.getLoggedInUser());
			sorList = rfiSupplierSorItemService.getSorEvaluationData(eventId, envelopId, selectedSuppliers, SecurityLibrary.getLoggedInUser(), "0", null, null, null, 0, SecurityLibrary.getLoggedInUser().getBqPageLength());
			eventSuppliers = rfiEventSupplierService.getEventSuppliersForEvaluation(eventId);
			if (event.getViewSupplerName() != null && !event.getViewSupplerName() && !event.getDisableMasking()) {
				for (Supplier supplier : eventSuppliers) {
					supplier.setCompanyName(MaskUtils.maskName(envelop.getPreFix(), supplier.getId(), envelop.getId()));
				}
			}
			model.addAttribute("eventSuppliers", eventSuppliers);
			model.addAttribute("eventPermissions", rfiEventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), eventId));
			break;
		}
		case RFP: {
			envelop = rfpEnvelopService.getEnvelopForEvaluationById(envelopId, SecurityLibrary.getLoggedInUser());
			event = ((RfpEnvelop) envelop).getRfxEvent();
			cqList = rfpSupplierCqItemService.getCqEvaluationData(eventId, envelopId, selectedSuppliers, SecurityLibrary.getLoggedInUser());
			bqList = rfpSupplierBqItemService.getBqEvaluationData(eventId, envelopId, selectedSuppliers, SecurityLibrary.getLoggedInUser(), withOrWithoutTax, null, null, null, 0, SecurityLibrary.getLoggedInUser().getBqPageLength());
			sorList = rfpSupplierSorItemService.getSorEvaluationData(eventId, envelopId, selectedSuppliers, SecurityLibrary.getLoggedInUser(), "0", null, null, null, 0, SecurityLibrary.getLoggedInUser().getBqPageLength());
			eventSuppliers = rfpEventSupplierService.getEventSuppliersForEvaluation(eventId);
			if (event.getViewSupplerName() != null && !event.getViewSupplerName() && !event.getDisableMasking()) {
				for (Supplier supplier : eventSuppliers) {
					supplier.setCompanyName(MaskUtils.maskName(envelop.getPreFix(), supplier.getId(), envelop.getId()));
				}
			}
			model.addAttribute("eventSuppliers", eventSuppliers);
			model.addAttribute("eventPermissions", rfpEventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), eventId));
			break;
		}
		case RFQ: {
			envelop = rfqEnvelopService.getEnvelopForEvaluationById(envelopId, SecurityLibrary.getLoggedInUser());
			event = ((RfqEnvelop) envelop).getRfxEvent();
			cqList = rfqSupplierCqItemService.getCqEvaluationData(eventId, envelopId, selectedSuppliers, SecurityLibrary.getLoggedInUser());
			bqList = rfqSupplierBqItemService.getBqEvaluationData(eventId, envelopId, selectedSuppliers, SecurityLibrary.getLoggedInUser(), withOrWithoutTax, null, null, null, 0, SecurityLibrary.getLoggedInUser().getBqPageLength());
			sorList = rfqSupplierSorItemService.getSorEvaluationData(eventId, envelopId, selectedSuppliers, SecurityLibrary.getLoggedInUser(), "0", null, null, null, 0, SecurityLibrary.getLoggedInUser().getBqPageLength());
			eventSuppliers = rfqEventSupplierService.getEventSuppliersForEvaluation(eventId);

			if (event.getViewSupplerName() != null && !event.getViewSupplerName() && !event.getDisableMasking()) {
				for (Supplier supplier : eventSuppliers) {
					supplier.setCompanyName(MaskUtils.maskName(envelop.getPreFix(), supplier.getId(), envelop.getId()));
				}
			}
			model.addAttribute("eventSuppliers", eventSuppliers);
			model.addAttribute("eventPermissions", rfqEventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), eventId));
			break;
		}
		case RFT: {
			envelop = rftEnvelopService.getRftEnvelopForEvaluationById(envelopId, SecurityLibrary.getLoggedInUser());
			event = ((RftEnvelop) envelop).getRfxEvent();
			cqList = rftSupplierCqItemService.getCqEvaluationData(eventId, envelopId, selectedSuppliers, SecurityLibrary.getLoggedInUser());
			bqList = rftSupplierBqItemService.getBqEvaluationData(eventId, envelopId, selectedSuppliers, SecurityLibrary.getLoggedInUser(), withOrWithoutTax, null, null, null, 0, SecurityLibrary.getLoggedInUser().getBqPageLength());
			sorList = rftSupplierSorItemService.getSorEvaluationData(eventId, envelopId, selectedSuppliers, SecurityLibrary.getLoggedInUser(), "0", null, null, null, 0, SecurityLibrary.getLoggedInUser().getBqPageLength());
			eventSuppliers = rftEventSupplierService.getEventSuppliersForEvaluation(eventId);
			if (event.getViewSupplerName() != null && !event.getViewSupplerName() && !event.getDisableMasking()) {
				for (Supplier supplier : eventSuppliers) {
					supplier.setCompanyName(MaskUtils.maskName(envelop.getPreFix(), supplier.getId(), envelop.getId()));
				}
			}
			model.addAttribute("eventSuppliers", eventSuppliers);
			model.addAttribute("eventPermissions", rftEventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), eventId));
			break;
		}
		default:
			break;

		}

		if (CollectionUtil.isNotEmpty(selectedSuppliers)) {
			int count = 0;
			for (Supplier sup : eventSuppliers) {
				if (!sup.isDisqualified()) {
					count++;
				}
			}
			if (count == 0) {
				model.addAttribute("showComparision", true);
			} else {
				model.addAttribute("showComparision", false);
			}

			model.addAttribute("selectedSuppliers", suppliers);
		} else {
			List<String> list = new ArrayList<String>();
			for (Supplier sup : eventSuppliers) {
				list.add(sup.getId());
			}
			model.addAttribute("selectedSuppliers", list);
		}

		model.addAttribute("withOrWithoutTax", withOrWithoutTax);
		model.addAttribute("evaluation", cqList);
		model.addAttribute("bqEvaluation", bqList);
		model.addAttribute("sorEvaluation", sorList);
		model.addAttribute("event", event);
		model.addAttribute("envelop", envelop);
		model.addAttribute("withTax", withTax);

		return "submissionReport";
	}

	@RequestMapping(path = "/getSupplierComments/{eventType}/{eventId}/{itemId}/{supplierId}", method = RequestMethod.GET)
	public ResponseEntity<List<?>> getSupplierComments(@PathVariable("eventType") RfxTypes eventType, @PathVariable("eventId") String eventId, @PathVariable("itemId") String itemId, @PathVariable("supplierId") String supplierId) throws JsonProcessingException {
		LOG.info("EventType : " + eventType + " Event Id : " + eventId + " Item Id : " + itemId + " Supplie Id : " + supplierId);
		HttpHeaders header = new HttpHeaders();
		List<?> comment = null;
		EventPermissions eventPermissions = null;
		try {

			switch (eventType) {
			case RFA:
				eventPermissions = rfaEventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), eventId);
				// comment =
				// rfaCqEvaluationCommentsService.getCommentsForSupplier(supplierService.findSuppById(supplierId),
				// eventId, itemId, ((eventPermissions != null && eventPermissions.isLeadEvaluator()) ? null :
				// SecurityLibrary.getLoggedInUser()));
				comment = rfaCqEvaluationCommentsService.getCommentsForSupplierNew(supplierService.findSuppById(supplierId), eventId, itemId, ((eventPermissions != null && eventPermissions.isLeadEvaluator()) ? null : SecurityLibrary.getLoggedInUser()), SecurityLibrary.getLoggedInUser());
				break;
			case RFI:
				eventPermissions = rfiEventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), eventId);
				// comment =
				// rfiCqEvaluationCommentsService.getCommentsForSupplier(supplierService.findSuppById(supplierId),
				// eventId, itemId, ((eventPermissions != null && eventPermissions.isLeadEvaluator()) ? null :
				// SecurityLibrary.getLoggedInUser()));
				comment = rfiCqEvaluationCommentsService.getCommentsForSupplierNew(supplierService.findSuppById(supplierId), eventId, itemId, ((eventPermissions != null && eventPermissions.isLeadEvaluator()) ? null : SecurityLibrary.getLoggedInUser()), SecurityLibrary.getLoggedInUser());
				break;
			case RFP:
				eventPermissions = rfpEventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), eventId);
				// comment =
				// rfpCqEvaluationCommentsService.getCommentsForSupplier(supplierService.findSuppById(supplierId),
				// eventId, itemId, ((eventPermissions != null && eventPermissions.isLeadEvaluator()) ? null :
				// SecurityLibrary.getLoggedInUser()));
				comment = rfpCqEvaluationCommentsService.getCommentsForSupplierNew(supplierService.findSuppById(supplierId), eventId, itemId, ((eventPermissions != null && eventPermissions.isLeadEvaluator()) ? null : SecurityLibrary.getLoggedInUser()), SecurityLibrary.getLoggedInUser());
				break;
			case RFQ:
				eventPermissions = rfqEventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), eventId);
				// comment =
				// rfqCqEvaluationCommentsService.getCommentsForSupplier(supplierService.findSuppById(supplierId),
				// eventId, itemId, ((eventPermissions != null && eventPermissions.isLeadEvaluator()) ? null :
				// SecurityLibrary.getLoggedInUser()));
				comment = rfqCqEvaluationCommentsService.getCommentsForSupplierNew(supplierService.findSuppById(supplierId), eventId, itemId, ((eventPermissions != null && eventPermissions.isLeadEvaluator()) ? null : SecurityLibrary.getLoggedInUser()), SecurityLibrary.getLoggedInUser());
				break;
			case RFT:
				eventPermissions = rftEventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), eventId);
				// comment =
				// rftCqEvaluationCommentsService.getCommentsForSupplier(supplierService.findSuppById(supplierId),
				// eventId, itemId, ((eventPermissions != null && eventPermissions.isLeadEvaluator()) ? null :
				// SecurityLibrary.getLoggedInUser()));
				comment = rftCqEvaluationCommentsService.getCommentsForSupplierNew(supplierService.findSuppById(supplierId), eventId, itemId, ((eventPermissions != null && eventPermissions.isLeadEvaluator()) ? null : SecurityLibrary.getLoggedInUser()), SecurityLibrary.getLoggedInUser());

				break;
			default:
				break;
			}

		} catch (Exception e) {
			LOG.error("Error : " + e.getMessage(), e);
			header.add("error", "Error showing Remarks : " + e.getMessage());
			return new ResponseEntity<List<?>>(comment, header, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<List<?>>(comment, header, HttpStatus.OK);
	}

	@RequestMapping(path = "addComments/{eventType}", method = RequestMethod.POST)
	public ResponseEntity<List<?>> addComments(@PathVariable("eventType") RfxTypes eventType, @RequestParam("eventId") String eventId, @RequestParam("itemId") String itemId, @RequestParam("supplierId") String supplierId, @RequestParam String comments) {
		LOG.info("EventType : " + eventType + " Event Id : " + eventId + " Item Id : " + itemId + " Supplie Id : " + supplierId + " Cooments : " + comments);

		HttpHeaders header = new HttpHeaders();
		List<?> comment = null;
		EventPermissions eventPermissions = null;

		try {
			switch (eventType) {
			case RFA:
				RfaCqEvaluationComments cmnt = new RfaCqEvaluationComments();
				cmnt.setComment(comments);
				cmnt.setCqItem(rfaCqService.getCqItembyCqItemId(itemId));
				cmnt.setEvent(rfaEventService.getRfaEventByeventId(eventId));
				cmnt.setSupplier(supplierService.findSuppById(supplierId));
				cmnt.setCreatedBy(SecurityLibrary.getLoggedInUser());
				cmnt.setCreatedDate(new Date());
				rfaCqEvaluationCommentsService.SaveComment(cmnt);
				eventPermissions = rfaEventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), eventId);
				comment = rfaCqEvaluationCommentsService.getCommentsForSupplier(supplierService.findSuppById(supplierId), eventId, itemId, ((eventPermissions != null && eventPermissions.isLeadEvaluator()) ? null : SecurityLibrary.getLoggedInUser()));

				break;
			case RFI: {
				RfiCqEvaluationComments coment = new RfiCqEvaluationComments();
				coment.setComment(comments);
				coment.setCqItem(rfiCqService.getCqItembyCqItemId(itemId));
				coment.setEvent(rfiEventService.getRfiEventById(eventId));
				coment.setSupplier(supplierService.findSuppById(supplierId));
				coment.setCreatedBy(SecurityLibrary.getLoggedInUser());
				coment.setCreatedDate(new Date());
				rfiCqEvaluationCommentsService.SaveComment(coment);
				eventPermissions = rfiEventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), eventId);
				comment = rfiCqEvaluationCommentsService.getCommentsForSupplier(supplierService.findSuppById(supplierId), eventId, itemId, ((eventPermissions != null && eventPermissions.isLeadEvaluator()) ? null : SecurityLibrary.getLoggedInUser()));

				break;
			}
			case RFP: {
				RfpCqEvaluationComments coment = new RfpCqEvaluationComments();
				coment.setComment(comments);
				coment.setCqItem(rfpCqService.getCqItembyCqItemId(itemId));
				coment.setEvent(rfpEventService.getEventById(eventId));
				coment.setSupplier(supplierService.findSuppById(supplierId));
				coment.setCreatedBy(SecurityLibrary.getLoggedInUser());
				coment.setCreatedDate(new Date());
				rfpCqEvaluationCommentsService.SaveComment(coment);
				eventPermissions = rfpEventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), eventId);
				comment = rfpCqEvaluationCommentsService.getCommentsForSupplier(supplierService.findSuppById(supplierId), eventId, itemId, ((eventPermissions != null && eventPermissions.isLeadEvaluator()) ? null : SecurityLibrary.getLoggedInUser()));

				break;
			}
			case RFQ: {
				RfqCqEvaluationComments coment = new RfqCqEvaluationComments();
				coment.setComment(comments);
				coment.setCqItem(rfqCqService.getCqItembyCqItemId(itemId));
				coment.setEvent(rfqEventService.getEventById(eventId));
				coment.setSupplier(supplierService.findSuppById(supplierId));
				coment.setCreatedBy(SecurityLibrary.getLoggedInUser());
				coment.setCreatedDate(new Date());
				rfqCqEvaluationCommentsService.SaveComment(coment);
				eventPermissions = rfqEventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), eventId);
				comment = rfqCqEvaluationCommentsService.getCommentsForSupplier(supplierService.findSuppById(supplierId), eventId, itemId, ((eventPermissions != null && eventPermissions.isLeadEvaluator()) ? null : SecurityLibrary.getLoggedInUser()));

				break;
			}
			case RFT: {
				RftCqEvaluationComments coment = new RftCqEvaluationComments();
				coment.setComment(comments);
				coment.setCqItem(rftCqService.getCqItembyCqItemId(itemId));
				coment.setEvent(rftEventService.getRftEventById(eventId));
				coment.setSupplier(supplierService.findSuppById(supplierId));
				coment.setCreatedBy(SecurityLibrary.getLoggedInUser());
				coment.setCreatedDate(new Date());
				rftCqEvaluationCommentsService.SaveComment(coment);
				eventPermissions = rftEventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), eventId);
				comment = rftCqEvaluationCommentsService.getCommentsForSupplier(supplierService.findSuppById(supplierId), eventId, itemId, ((eventPermissions != null && eventPermissions.isLeadEvaluator()) ? null : SecurityLibrary.getLoggedInUser()));

				break;
			}
			default:
				break;
			}

		} catch (Exception e) {
			LOG.error("Error : " + e.getMessage(), e);
			header.add("error", "Error showing Remarks : " + e.getMessage());
			return new ResponseEntity<List<?>>(comment, header, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<List<?>>(comment, header, HttpStatus.OK);
	}

	@RequestMapping(value = "/getCqComment/{eventType}/{itemId}", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<String> getCqComment(@PathVariable("eventType") RfxTypes eventType, @PathVariable("itemId") String itemId) throws JsonProcessingException {

		LOG.info("itemId  :" + itemId + " Event Type :: " + eventType);

		HttpHeaders headers = new HttpHeaders();
		String comment = null;
		try {

			switch (eventType) {
			case RFA:
				comment = rfaCqService.getLeadEvaluatorComment(itemId);
				break;
			case RFI:
				comment = rfiCqService.getLeadEvaluatorComment(itemId);
				break;
			case RFP:
				comment = rfpCqService.getLeadEvaluatorComment(itemId);
				break;
			case RFQ:
				comment = rfqCqService.getLeadEvaluatorComment(itemId);
				break;
			case RFT:
				comment = rftCqService.getLeadEvaluatorComment(itemId);
				break;
			default:
				break;
			}

		} catch (Exception e) {
			LOG.error("Error : " + e.getMessage(), e);
			headers.add("error", "Error showing Remarks : " + e.getMessage());
			return new ResponseEntity<String>(null, headers, HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<String>(comment, headers, HttpStatus.OK);
	}

	@RequestMapping(value = "/addCqComment", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<String> addCqComment(@RequestParam("itemId") String itemId, @RequestParam(name = "leadEvaluationComment", required = true) String leadEvaluationComment, @RequestParam("eventType") RfxTypes eventType, RedirectAttributes redir) {
		LOG.info("itemId  :" + itemId + " Event Type :: " + eventType + " leadEvaluationComment :: " + leadEvaluationComment);
		HttpHeaders headers = new HttpHeaders();
		switch (eventType) {
		case RFA:
			try {
				boolean success = rfaCqService.updateLeadEvaluatorComment(itemId, leadEvaluationComment);
				if (success) {
					headers.add("success", messageSource.getMessage("rfx.leadEval.comment.success", new Object[] { null }, Global.LOCALE));
				} else {
					headers.add("error", messageSource.getMessage("rfx.leadEval.comment.error", new Object[] { "Could not update comment" }, Global.LOCALE));
				}
			} catch (Exception e) {
				LOG.error("Error while cancel meeting : " + e.getMessage(), e);
				headers.add("error", messageSource.getMessage("rfx.leadEval.comment.error", new Object[] { e.getMessage() }, Global.LOCALE));
				return new ResponseEntity<String>(null, headers, HttpStatus.BAD_REQUEST);
			}
			break;
		case RFI:
			try {
				boolean success = rfiCqService.updateLeadEvaluatorComment(itemId, leadEvaluationComment);
				if (success) {
					LOG.info("success");
					headers.add("success", messageSource.getMessage("rfx.leadEval.comment.success", new Object[] { null }, Global.LOCALE));
				} else {
					LOG.info("error");
					headers.add("error", messageSource.getMessage("rfx.leadEval.comment.error", new Object[] { "Could not update comment" }, Global.LOCALE));
				}
			} catch (Exception e) {
				LOG.error("Error while cancel meeting : " + e.getMessage(), e);
				headers.add("error", messageSource.getMessage("rfx.leadEval.comment.error", new Object[] { e.getMessage() }, Global.LOCALE));
				return new ResponseEntity<String>(null, headers, HttpStatus.BAD_REQUEST);
			}
			break;
		case RFP:
			try {
				boolean success = rfpCqService.updateLeadEvaluatorComment(itemId, leadEvaluationComment);
				if (success) {
					LOG.info("success");
					headers.add("success", messageSource.getMessage("rfx.leadEval.comment.success", new Object[] { null }, Global.LOCALE));
				} else {
					LOG.info("error");
					headers.add("error", messageSource.getMessage("rfx.leadEval.comment.error", new Object[] { "Could not update comment" }, Global.LOCALE));
				}
			} catch (Exception e) {
				LOG.error("Error while cancel meeting : " + e.getMessage(), e);
				headers.add("error", messageSource.getMessage("rfx.leadEval.comment.error", new Object[] { e.getMessage() }, Global.LOCALE));
				return new ResponseEntity<String>(null, headers, HttpStatus.BAD_REQUEST);
			}
			break;
		case RFQ:
			try {
				boolean success = rfqCqService.updateLeadEvaluatorComment(itemId, leadEvaluationComment);
				if (success) {
					LOG.info("success");
					headers.add("success", messageSource.getMessage("rfx.leadEval.comment.success", new Object[] { null }, Global.LOCALE));
				} else {
					LOG.info("error");
					headers.add("error", messageSource.getMessage("rfx.leadEval.comment.error", new Object[] { "Could not update comment" }, Global.LOCALE));
				}
			} catch (Exception e) {
				LOG.error("Error while cancel meeting : " + e.getMessage(), e);
				headers.add("error", messageSource.getMessage("rfx.leadEval.comment.error", new Object[] { e.getMessage() }, Global.LOCALE));
				return new ResponseEntity<String>(null, headers, HttpStatus.BAD_REQUEST);
			}
			break;
		case RFT:
			try {
				boolean success = rftCqService.updateLeadEvaluatorComment(itemId, leadEvaluationComment);
				if (success) {
					LOG.info("success");
					headers.add("success", messageSource.getMessage("rfx.leadEval.comment.success", new Object[] { null }, Global.LOCALE));
				} else {
					LOG.info("error");
					headers.add("error", messageSource.getMessage("rfx.leadEval.comment.error", new Object[] { "Could not update comment" }, Global.LOCALE));
				}
			} catch (Exception e) {
				LOG.error("Error while cancel meeting : " + e.getMessage(), e);
				headers.add("error", messageSource.getMessage("rfx.leadEval.comment.error", new Object[] { e.getMessage() }, Global.LOCALE));
				return new ResponseEntity<String>(null, headers, HttpStatus.BAD_REQUEST);
			}
			break;
		default:
			break;

		}
		return new ResponseEntity<String>("success", headers, HttpStatus.OK);
	}

	@RequestMapping(path = "/removeComments/{eventType}/{eventId}/{itemId}/{supplierId}/{commentId}", method = RequestMethod.POST)
	public ResponseEntity<List<?>> removeComments(@PathVariable("eventType") RfxTypes eventType, @PathVariable("eventId") String eventId, @PathVariable("itemId") String itemId, @PathVariable("supplierId") String supplierId, @PathVariable("commentId") String commentId) throws JsonProcessingException {
		LOG.info("EventType : " + eventType + " Event Id : " + eventId + " Item Id : " + itemId + " Supplie Id : " + supplierId);
		HttpHeaders header = new HttpHeaders();
		List<?> comment = null;
		EventPermissions eventPermissions = null;

		try {

			switch (eventType) {
			case RFA:
				rfaCqEvaluationCommentsService.deleteComment(rfaCqEvaluationCommentsService.findComment(commentId));
				eventPermissions = rfaEventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), eventId);
				comment = rfaCqEvaluationCommentsService.getCommentsForSupplier(supplierService.findSuppById(supplierId), eventId, itemId, ((eventPermissions != null && eventPermissions.isLeadEvaluator()) ? null : SecurityLibrary.getLoggedInUser()));
				break;
			case RFI:
				rfiCqEvaluationCommentsService.deleteComment(rfiCqEvaluationCommentsService.findComment(commentId));
				eventPermissions = rfiEventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), eventId);
				comment = rfiCqEvaluationCommentsService.getCommentsForSupplier(supplierService.findSuppById(supplierId), eventId, itemId, ((eventPermissions != null && eventPermissions.isLeadEvaluator()) ? null : SecurityLibrary.getLoggedInUser()));
				break;
			case RFP:
				rfpCqEvaluationCommentsService.deleteComment(rfpCqEvaluationCommentsService.findComment(commentId));
				eventPermissions = rfpEventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), eventId);
				comment = rfpCqEvaluationCommentsService.getCommentsForSupplier(supplierService.findSuppById(supplierId), eventId, itemId, ((eventPermissions != null && eventPermissions.isLeadEvaluator()) ? null : SecurityLibrary.getLoggedInUser()));
				break;
			case RFQ:
				rfqCqEvaluationCommentsService.deleteComment(rfqCqEvaluationCommentsService.findComment(commentId));
				eventPermissions = rfqEventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), eventId);
				comment = rfqCqEvaluationCommentsService.getCommentsForSupplier(supplierService.findSuppById(supplierId), eventId, itemId, ((eventPermissions != null && eventPermissions.isLeadEvaluator()) ? null : SecurityLibrary.getLoggedInUser()));
				break;
			case RFT:
				rftCqEvaluationCommentsService.deleteComment(rftCqEvaluationCommentsService.findComment(commentId));
				eventPermissions = rftEventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), eventId);
				comment = rftCqEvaluationCommentsService.getCommentsForSupplier(supplierService.findSuppById(supplierId), eventId, itemId, ((eventPermissions != null && eventPermissions.isLeadEvaluator()) ? null : SecurityLibrary.getLoggedInUser()));
				break;
			default:
				break;
			}

		} catch (Exception e) {
			LOG.error("Error : " + e.getMessage(), e);
			header.add("error", "Error showing Remarks : " + e.getMessage());
			return new ResponseEntity<List<?>>(comment, header, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<List<?>>(comment, header, HttpStatus.OK);
	}

	@RequestMapping(path = "/generateCqComparisonTable/{eventType}/{eventId}/{evelopId}", method = RequestMethod.GET)
	public void generateCqComparisonTable(@PathVariable("eventType") RfxTypes eventType, @PathVariable("eventId") String eventId, @PathVariable("evelopId") String evelopId, HttpServletResponse response) throws IOException {
		XSSFWorkbook workbook = new XSSFWorkbook();
		List<EventEvaluationPojo> list = null;
		String envTitle = "";
		switch (eventType) {
		case RFA:
			list = rfaSupplierCqItemService.getEvaluationDataForCqComparison(eventId, evelopId);
			try {
				envTitle = rftEnvelopService.getEnvelipeTitleById(evelopId, eventType.name());
				RfaEventAudit audit = new RfaEventAudit();
				audit.setAction(AuditActionType.Download);
				audit.setActionBy(SecurityLibrary.getLoggedInUser());
				audit.setActionDate(new Date());
				RfaEvent rfaEvent = new RfaEvent();
				rfaEvent.setId(eventId);
				audit.setEvent(rfaEvent);
				audit.setDescription("Cq comparison table  for Envelope '" + envTitle + " ' downloaded");
				eventAuditService.save(audit);
			} catch (Exception e) {
				LOG.error(e.getMessage(), e);
			}
			try {
				RfaEvent rfaEvent = rfaEventService.getPlainEventById(eventId);
				BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.DOWNLOAD, "Comparison table is downloaded for Envelope '" + envTitle + "' of event '" + rfaEvent.getEventId() + "' ", SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser(), new Date(), ModuleType.RFA);
				buyerAuditTrailDao.save(buyerAuditTrail);
			} catch (Exception e) {
				LOG.error("Error while recording auction event Resume " + e.getMessage(), e);
			}

			break;
		case RFI:
			list = rfiSupplierCqItemService.getEvaluationDataForCqComparison(eventId, evelopId);
			try {
				envTitle = rftEnvelopService.getEnvelipeTitleById(evelopId, eventType.name());
				RfiEventAudit audit = new RfiEventAudit();
				audit.setAction(AuditActionType.Download);
				audit.setActionBy(SecurityLibrary.getLoggedInUser());
				audit.setActionDate(new Date());
				RfiEvent rfiEvent = new RfiEvent();
				rfiEvent.setId(eventId);
				audit.setEvent(rfiEvent);
				audit.setDescription("Cq comparison table  for Envelope '" + envTitle + " ' downloaded");
				eventAuditService.save(audit);
			} catch (Exception e) {
				LOG.error(e.getMessage(), e);
			}
			try {
				RfiEvent rfiEvent = rfiEventService.getPlainEventById(eventId);
				BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.DOWNLOAD, "Comparison table is downloaded for envelop '" + envTitle + "' of event '" + rfiEvent.getEventId() + "' ", SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser(), new Date(), ModuleType.RFI);
				buyerAuditTrailDao.save(buyerAuditTrail);
			} catch (Exception e) {
				LOG.error("Error while recording auction event Resume " + e.getMessage(), e);
			}

			break;
		case RFP:
			list = rfpSupplierCqItemService.getEvaluationDataForCqComparison(eventId, evelopId);
			try {
				envTitle = rftEnvelopService.getEnvelipeTitleById(evelopId, eventType.name());
				RfpEventAudit audit = new RfpEventAudit();
				audit.setAction(AuditActionType.Download);
				audit.setActionBy(SecurityLibrary.getLoggedInUser());
				audit.setActionDate(new Date());
				RfpEvent rfpEvent = new RfpEvent();
				rfpEvent.setId(eventId);
				audit.setEvent(rfpEvent);
				audit.setDescription("Cq comparison table  for Envelope '" + envTitle + " ' downloaded");
				eventAuditService.save(audit);
			} catch (Exception e) {
				LOG.error(e.getMessage(), e);
			}
			try {
				RfpEvent rfpEvent = rfpEventService.getPlainEventById(eventId);
				BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.DOWNLOAD, "Comparison table is downloaded for Envelope '" + envTitle + "' of event '" + rfpEvent.getEventId() + "' ", SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser(), new Date(), ModuleType.RFP);
				buyerAuditTrailDao.save(buyerAuditTrail);
			} catch (Exception e) {
				LOG.error("Error while recording auction event Resume " + e.getMessage(), e);
			}

			break;
		case RFQ:
			list = rfqSupplierCqItemService.getEvaluationDataForCqComparison(eventId, evelopId);
			try {
				envTitle = rftEnvelopService.getEnvelipeTitleById(evelopId, eventType.name());
				RfqEventAudit audit = new RfqEventAudit();
				audit.setAction(AuditActionType.Download);
				audit.setActionBy(SecurityLibrary.getLoggedInUser());
				audit.setActionDate(new Date());
				RfqEvent rfqEvent = new RfqEvent();
				rfqEvent.setId(eventId);
				audit.setEvent(rfqEvent);
				audit.setDescription("Cq comparison table  for Envelope '" + envTitle + " ' downloaded");
				eventAuditService.save(audit);
			} catch (Exception e) {
				LOG.error(e.getMessage(), e);
			}
			try {
				RfqEvent rfqEvent = rfqEventService.getPlainEventById(eventId);
				BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.DOWNLOAD, "Comparison table is downloaded for Envelope '" + envTitle + "' of event '" + rfqEvent.getEventId() + "' ", SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser(), new Date(), ModuleType.RFQ);
				buyerAuditTrailDao.save(buyerAuditTrail);
			} catch (Exception e) {
				LOG.error("Error while recording auction event Resume " + e.getMessage(), e);
			}
			break;
		case RFT:
			list = rftSupplierCqItemService.getEvaluationDataForCqComparison(eventId, evelopId);
			try {
				envTitle = rftEnvelopService.getEnvelipeTitleById(evelopId, eventType.name());
				RftEventAudit audit = new RftEventAudit();
				audit.setAction(AuditActionType.Download);
				audit.setActionBy(SecurityLibrary.getLoggedInUser());
				audit.setActionDate(new Date());
				RftEvent rftEvent = new RftEvent();
				rftEvent.setId(eventId);
				audit.setEvent(rftEvent);
				audit.setDescription("Cq comparison table  for Envelope '" + envTitle + " ' downloaded");
				eventAuditService.save(audit);
			} catch (Exception e) {
				LOG.error(e.getMessage(), e);
			}
			try {
				RftEvent rftEvent = rftEventService.getPlainEventById(eventId);
				BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.DOWNLOAD, "Comparison table is downloaded for Envelope '" + envTitle + "' of event '" + rftEvent.getEventId() + "' ", SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser(), new Date(), ModuleType.RFT);
				buyerAuditTrailDao.save(buyerAuditTrail);
			} catch (Exception e) {
				LOG.error("Error while recording auction event Resume " + e.getMessage(), e);
			}
			break;
		default:
			break;
		}
		rfaEventService.buildCqComparisionFile(workbook, list, response);

		// super.buildCqComparisionFile(workbook, list, response);
	}

	@RequestMapping(path = "/generateBqComparisonTable/{eventType}/{eventId}/{evelopId}", method = RequestMethod.GET)
	public void generateBqComparisonTable(@PathVariable("eventType") RfxTypes eventType, @PathVariable("eventId") String eventId, @PathVariable("evelopId") String evelopId, HttpServletResponse response) throws IOException {
		XSSFWorkbook workbook = new XSSFWorkbook();
		List<EventEvaluationPojo> list = null;
		String envTitle = "";
		try {
			switch (eventType) {
			case RFA:
				list = rfaSupplierBqItemService.getEvaluationDataForBqComparisonReport(eventId, evelopId);
				try {
					envTitle = rftEnvelopService.getEnvelipeTitleById(evelopId, eventType.name());
					RfaEventAudit audit = new RfaEventAudit();
					audit.setAction(AuditActionType.Download);
					audit.setActionBy(SecurityLibrary.getLoggedInUser());
					audit.setActionDate(new Date());
					RfaEvent rfaEvent = new RfaEvent();
					rfaEvent.setId(eventId);
					audit.setEvent(rfaEvent);
					audit.setDescription("Bq comparison table  for Envelope '" + envTitle + " ' downloaded");
					eventAuditService.save(audit);
				} catch (Exception e) {
					LOG.error(e.getMessage(), e);
				}
				try {
					RfaEvent rfaEvent = rfaEventService.getPlainEventById(eventId);
					LOG.info("--------------------------BEFORE AUDIT---------------------------------------");
					BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.DOWNLOAD, "Comparison table is downloaded for Envelope '" + envTitle + "' of event '" + rfaEvent.getEventId() + "' ", SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser(), new Date(), ModuleType.RFA);
					buyerAuditTrailDao.save(buyerAuditTrail);
					LOG.info("--------------------------AFTER AUDIT---------------------------------------");
				} catch (Exception e) {
					LOG.error("Error while recording auction event Resume " + e.getMessage(), e);
				}
				break;
			case RFI:
				break;
			case RFP:
				list = rfpSupplierBqItemService.getEvaluationDataForBqComparisonReport(eventId, evelopId);
				try {
					envTitle = rftEnvelopService.getEnvelipeTitleById(evelopId, eventType.name());
					RfpEventAudit audit = new RfpEventAudit();
					audit.setAction(AuditActionType.Download);
					audit.setActionBy(SecurityLibrary.getLoggedInUser());
					audit.setActionDate(new Date());
					RfpEvent rfpEvent = new RfpEvent();
					rfpEvent.setId(eventId);
					audit.setEvent(rfpEvent);
					audit.setDescription("Bq comparison table  for Envelope '" + envTitle + " ' downloaded");
					eventAuditService.save(audit);
				} catch (Exception e) {
					LOG.error(e.getMessage(), e);
				}
				try {
					RfpEvent event = rfpEventService.getPlainEventById(eventId);
					BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.DOWNLOAD, "Comparison table is downloaded for Envelope '" + envTitle + "' of event '" + event.getEventId() + "' ", SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser(), new Date(), ModuleType.RFP);
					buyerAuditTrailDao.save(buyerAuditTrail);
				} catch (Exception e) {
					LOG.error("Error while recording auction event Resume " + e.getMessage(), e);
				}

				break;
			case RFQ:
				list = rfqSupplierBqItemService.getEvaluationDataForBqComparisonReport(eventId, evelopId);
				try {
					envTitle = rftEnvelopService.getEnvelipeTitleById(evelopId, eventType.name());
					RfqEventAudit audit = new RfqEventAudit();
					audit.setAction(AuditActionType.Download);
					audit.setActionBy(SecurityLibrary.getLoggedInUser());
					audit.setActionDate(new Date());
					RfqEvent rfaEvent = new RfqEvent();
					rfaEvent.setId(eventId);
					audit.setEvent(rfaEvent);
					audit.setDescription("Bq comparison table  for Envelope '" + envTitle + " ' downloaded");
					eventAuditService.save(audit);
				} catch (Exception e) {
					LOG.error(e.getMessage(), e);
				}

				try {
					RfqEvent event = rfqEventService.getPlainEventById(eventId);
					BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.DOWNLOAD, "Comparison table is downloaded for Envelope '" + envTitle + "' of event '" + event.getEventId() + "' ", SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser(), new Date(), ModuleType.RFQ);
					buyerAuditTrailDao.save(buyerAuditTrail);
				} catch (Exception e) {
					LOG.error("Error while recording auction event Resume " + e.getMessage(), e);
				}

				break;
			case RFT:
				list = rftSupplierBqItemService.getEvaluationDataForBqComparisonReport(eventId, evelopId);
				try {
					envTitle = rftEnvelopService.getEnvelipeTitleById(evelopId, eventType.name());
					RftEventAudit audit = new RftEventAudit();
					audit.setAction(AuditActionType.Download);
					audit.setActionBy(SecurityLibrary.getLoggedInUser());
					audit.setActionDate(new Date());
					RftEvent rftEvent = new RftEvent();
					rftEvent.setId(eventId);
					audit.setEvent(rftEvent);
					audit.setDescription("Bq comparison table  for Envelope '" + envTitle + " ' downloaded");
					eventAuditService.save(audit);
				} catch (Exception e) {
					LOG.error(e.getMessage(), e);
				}

				try {
					RftEvent event = rftEventService.getPlainEventById(eventId);
					BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.DOWNLOAD, "Comparison table is downloaded for Envelope '" + envTitle + "' of event '" + event.getEventId() + "' ", SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser(), new Date(), ModuleType.RFT);
					buyerAuditTrailDao.save(buyerAuditTrail);
				} catch (Exception e) {
					LOG.error("Error while recording auction event Resume " + e.getMessage(), e);
				}

				break;
			default:
				break;
			}
		} catch (Exception e) {
			LOG.error("Error while generating Bq comparision table : " + e.getMessage(), e);
		}
		rfaEventService.buildBqComparisionFile(workbook, list, response, eventId, evelopId, eventType);
		// super.buildBqComparisionFile(workbook, list, response, eventId, evelopId, eventType);
	}

	@RequestMapping(path = "/generateSorComparisonTable/{eventType}/{eventId}/{evelopId}", method = RequestMethod.GET)
	public void generateSorComparisonTable(@PathVariable("eventType") RfxTypes eventType, @PathVariable("eventId") String eventId, @PathVariable("evelopId") String evelopId, HttpServletResponse response) throws IOException {
		XSSFWorkbook workbook = new XSSFWorkbook();
		List<EventEvaluationPojo> list = null;
		String envTitle = "";
		try {
			switch (eventType) {
				case RFA:
					list = rfaSupplierSorItemService.getEvaluationDataForSorComparisonReport(eventId, evelopId);
					try {
						envTitle = rftEnvelopService.getEnvelipeTitleById(evelopId, eventType.name());
						RfaEventAudit audit = new RfaEventAudit();
						audit.setAction(AuditActionType.Download);
						audit.setActionBy(SecurityLibrary.getLoggedInUser());
						audit.setActionDate(new Date());
						RfaEvent rfaEvent = new RfaEvent();
						rfaEvent.setId(eventId);
						audit.setEvent(rfaEvent);
						audit.setDescription("Sor comparison table  for Envelope '" + envTitle + " ' downloaded");
						eventAuditService.save(audit);
					} catch (Exception e) {
						LOG.error(e.getMessage(), e);
					}
					try {
						RfaEvent rfaEvent = rfaEventService.getPlainEventById(eventId);
						LOG.info("--------------------------BEFORE AUDIT---------------------------------------");
						BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.DOWNLOAD, "Comparison table is downloaded for Envelope '" + envTitle + "' of event '" + rfaEvent.getEventId() + "' ", SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser(), new Date(), ModuleType.RFA);
						buyerAuditTrailDao.save(buyerAuditTrail);
						LOG.info("--------------------------AFTER AUDIT---------------------------------------");
					} catch (Exception e) {
						LOG.error("Error while recording auction event Resume " + e.getMessage(), e);
					}
					break;
				case RFI:
					list = rfiSupplierSorItemService.getEvaluationDataForSorComparisonReport(eventId, evelopId);
					try {
						envTitle = rfiEnvelopService.getEnvelipeTitleById(evelopId, eventType.name());
						RfiEventAudit audit = new RfiEventAudit();
						audit.setAction(AuditActionType.Download);
						audit.setActionBy(SecurityLibrary.getLoggedInUser());
						audit.setActionDate(new Date());
						RfiEvent rfaEvent = new RfiEvent();
						rfaEvent.setId(eventId);
						audit.setEvent(rfaEvent);
						audit.setDescription("Sor comparison table  for Envelope '" + envTitle + " ' downloaded");
						eventAuditService.save(audit);
					} catch (Exception e) {
						LOG.error(e.getMessage(), e);
					}

					try {
						RfiEvent event = rfiEventService.getPlainEventById(eventId);
						BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.DOWNLOAD, "Comparison table is downloaded for Envelope '" + envTitle + "' of event '" + event.getEventId() + "' ", SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser(), new Date(), ModuleType.RFQ);
						buyerAuditTrailDao.save(buyerAuditTrail);
					} catch (Exception e) {
						LOG.error("Error while recording auction event Resume " + e.getMessage(), e);
					}

					break;
				case RFP:
					list = rfpSupplierSorItemService.getEvaluationDataForSorComparisonReport(eventId, evelopId);
					try {
						envTitle = rftEnvelopService.getEnvelipeTitleById(evelopId, eventType.name());
						RfpEventAudit audit = new RfpEventAudit();
						audit.setAction(AuditActionType.Download);
						audit.setActionBy(SecurityLibrary.getLoggedInUser());
						audit.setActionDate(new Date());
						RfpEvent rfpEvent = new RfpEvent();
						rfpEvent.setId(eventId);
						audit.setEvent(rfpEvent);
						audit.setDescription("Sor comparison table  for Envelope '" + envTitle + " ' downloaded");
						eventAuditService.save(audit);
					} catch (Exception e) {
						LOG.error(e.getMessage(), e);
					}
					try {
						RfpEvent event = rfpEventService.getPlainEventById(eventId);
						BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.DOWNLOAD, "Comparison table is downloaded for Envelope '" + envTitle + "' of event '" + event.getEventId() + "' ", SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser(), new Date(), ModuleType.RFP);
						buyerAuditTrailDao.save(buyerAuditTrail);
					} catch (Exception e) {
						LOG.error("Error while recording auction event Resume " + e.getMessage(), e);
					}

					break;
				case RFQ:
					list = rfqSupplierSorItemService.getEvaluationDataForSorComparisonReport(eventId, evelopId);

					try {
						envTitle = rftEnvelopService.getEnvelipeTitleById(evelopId, eventType.name());
						RfqEventAudit audit = new RfqEventAudit();
						audit.setAction(AuditActionType.Download);
						audit.setActionBy(SecurityLibrary.getLoggedInUser());
						audit.setActionDate(new Date());
						RfqEvent rfaEvent = new RfqEvent();
						rfaEvent.setId(eventId);
						audit.setEvent(rfaEvent);
						audit.setDescription("Sor comparison table  for Envelope '" + envTitle + " ' downloaded");
						eventAuditService.save(audit);
					} catch (Exception e) {
						LOG.error(e.getMessage(), e);
					}

					try {
						RfqEvent event = rfqEventService.getPlainEventById(eventId);
						BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.DOWNLOAD, "Comparison table is downloaded for Envelope '" + envTitle + "' of event '" + event.getEventId() + "' ", SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser(), new Date(), ModuleType.RFQ);
						buyerAuditTrailDao.save(buyerAuditTrail);
					} catch (Exception e) {
						LOG.error("Error while recording auction event Resume " + e.getMessage(), e);
					}

					break;
				case RFT:
					list = rftSupplierSorItemService.getEvaluationDataForSorComparisonReport(eventId, evelopId);
					try {
						envTitle = rftEnvelopService.getEnvelipeTitleById(evelopId, eventType.name());
						RftEventAudit audit = new RftEventAudit();
						audit.setAction(AuditActionType.Download);
						audit.setActionBy(SecurityLibrary.getLoggedInUser());
						audit.setActionDate(new Date());
						RftEvent rftEvent = new RftEvent();
						rftEvent.setId(eventId);
						audit.setEvent(rftEvent);
						audit.setDescription("Sor comparison table  for Envelope '" + envTitle + " ' downloaded");
						eventAuditService.save(audit);
					} catch (Exception e) {
						LOG.error(e.getMessage(), e);
					}

					try {
						RftEvent event = rftEventService.getPlainEventById(eventId);
						BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.DOWNLOAD, "Comparison table is downloaded for Envelope '" + envTitle + "' of event '" + event.getEventId() + "' ", SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser(), new Date(), ModuleType.RFT);
						buyerAuditTrailDao.save(buyerAuditTrail);
					} catch (Exception e) {
						LOG.error("Error while recording auction event Resume " + e.getMessage(), e);
					}

					break;
				default:
					break;
			}
		} catch (Exception e) {
			LOG.error("Error while generating Bq comparision table : " + e.getMessage(), e);
		}
		rfaEventService.buildSorComparisionFile(workbook, list, response, eventId, evelopId, eventType);
	}

	@RequestMapping(path = "/evaluationSubmissionReport/{eventType}/{eventId}/{evenvelopId}/{envelopTitle}", method = RequestMethod.POST)
	public void evaluationSubmissionReport(@PathVariable("eventType") RfxTypes eventType, @PathVariable("eventId") String eventId, @PathVariable("evenvelopId") String evenvelopId, @PathVariable("envelopTitle") String envelopTitle, HttpServletResponse response, HttpSession session) throws Exception {
		LOG.info("EventType : " + eventType + " Event Id : " + eventId + " evenvelop Id : " + evenvelopId);
		String filename = "";
		JasperPrint jasperPrint = null;
		// Virtualizar - To increase the performance
		JRSwapFileVirtualizer virtualizer = null;
		virtualizer = new JRSwapFileVirtualizer(200, new JRSwapFile(System.getProperty("java.io.tmpdir"), 2048, 1024), false);

		try {
			switch (eventType) {
			case RFA:
				filename = envelopTitle + "_RFA_SubmissionReport.pdf";
				jasperPrint = rfaEnvelopService.generateEvaluationSubmissionReport(evenvelopId, eventId, session, virtualizer);
				break;
			case RFI:
				filename = envelopTitle + "_RFI_SubmissionReport.pdf";
				jasperPrint = rfiEnvelopService.generateEvaluationSubmissionReport(evenvelopId, eventId, session, virtualizer);
				break;
			case RFP:
				filename = envelopTitle + "_RFP_SubmissionReport.pdf";
				jasperPrint = rfpEnvelopService.generateEvaluationSubmissionReport(evenvelopId, eventId, session, virtualizer);
				break;
			case RFQ:
				filename = envelopTitle + "_RFQ_SubmissionReport.pdf";
				jasperPrint = rfqEnvelopService.generateEvaluationSubmissionReport(evenvelopId, eventId, session, virtualizer);
				break;
			case RFT:
				filename = envelopTitle + "_RFT_SubmissionReport.pdf";
				jasperPrint = rftEnvelopService.generateEvaluationSubmissionReport(evenvelopId, eventId, session, virtualizer);

				break;
			default:
				break;
			}

			if (jasperPrint != null) {
				streamReport(jasperPrint, filename, response);
			}
		} catch (Exception e) {
			LOG.error("ERROR : " + e.getMessage(), e);
		} finally {
			if (virtualizer != null) {
				virtualizer.cleanup();
			}
		}
	}

	private void streamReport(JasperPrint jasperPrint, String filename, HttpServletResponse response) throws JRException, IOException {
		response.setContentType("application/pdf");
		response.addHeader("Content-Disposition", "attachment; filename=" + filename);

		JasperExportManager.exportReportToPdfStream(jasperPrint, response.getOutputStream());
		response.getOutputStream().flush();
		response.getOutputStream().close();
	}

	@RequestMapping(path = "/downloadEnvelopeSubmissionZip/{eventType}/{eventId}/{evenvelopId}", method = RequestMethod.POST)
	public void downloadEnvelopeSubmissionZip(@PathVariable("eventType") RfxTypes eventType, @PathVariable("eventId") String eventId, @PathVariable("evenvelopId") String evenvelopId, HttpServletResponse response, HttpSession session) throws Exception {

		LOG.info("Downloading ZIP for EventType : " + eventType + " Event Id : " + eventId + " evenvelop Id : " + evenvelopId);
		String fileName = "";
		String envTitle = "";
		JRSwapFileVirtualizer virtualizer = null;
		// ByteArrayOutputStream baos = new ByteArrayOutputStream();
		File zipFile = File.createTempFile(eventId + evenvelopId, "" + new Date().getTime());
		FileOutputStream fos = new FileOutputStream(zipFile);
		try (ZipOutputStream zos = new ZipOutputStream(fos)) {
			virtualizer = new JRSwapFileVirtualizer(100, new JRSwapFile(System.getProperty("java.io.tmpdir"), 2048, 1024), false);
			switch (eventType) {
			case RFA:
				fileName = rfaEnvelopService.generateEnvelopeZip(eventId, evenvelopId, zos, false, session, virtualizer);
				try {
					envTitle = rftEnvelopService.getEnvelipeTitleById(evenvelopId, eventType.name());
					RfaEventAudit audit = new RfaEventAudit();
					RfaEvent rfaEvent = new RfaEvent();
					rfaEvent.setId(eventId);
					audit.setEvent(rfaEvent);
					audit.setAction(AuditActionType.Download);
					audit.setActionBy(SecurityLibrary.getLoggedInUser());
					audit.setActionDate(new Date());
					audit.setDescription("Entire envelope '" + envTitle + " ' downloaded");
					eventAuditService.save(audit);

				} catch (Exception e) {
					LOG.error(e.getMessage(), e);
				}
				try {
					RfaEvent rfaEvent = rfaEventService.getPlainEventById(eventId);
					BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.DOWNLOAD, "Envelope '" + envTitle + "' is downloaded successfully for event '" + rfaEvent.getEventId() + "' ", SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser(), new Date(), ModuleType.RFA);
					buyerAuditTrailDao.save(buyerAuditTrail);
				} catch (Exception e) {
					LOG.error("Error while recording auction event Resume " + e.getMessage(), e);
				}
				break;
			case RFI:
				String strTimeZone = (String) session.getAttribute(Global.SESSION_TIME_ZONE_KEY);
				fileName = rfiEnvelopService.generateEnvelopeZip(eventId, evenvelopId, zos, false, session, virtualizer, strTimeZone);
				try {
					envTitle = rftEnvelopService.getEnvelipeTitleById(evenvelopId, eventType.name());
					RfiEventAudit audit = new RfiEventAudit();
					RfiEvent rfiEvent = new RfiEvent();
					rfiEvent.setId(eventId);
					audit.setEvent(rfiEvent);
					audit.setAction(AuditActionType.Download);
					audit.setActionBy(SecurityLibrary.getLoggedInUser());
					audit.setActionDate(new Date());
					audit.setDescription("Entire envelope '" + envTitle + " ' downloaded");
					eventAuditService.save(audit);

				} catch (Exception e) {
					LOG.error(e.getMessage(), e);
				}

				try {
					RfiEvent event = rfiEventService.getPlainEventById(eventId);
					BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.DOWNLOAD, "Envelope '" + envTitle + "' is downloaded successfully for event '" + event.getEventId() + "' ", SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser(), new Date(), ModuleType.RFI);
					buyerAuditTrailDao.save(buyerAuditTrail);
				} catch (Exception e) {
					LOG.error("Error while recording auction event Resume " + e.getMessage(), e);
				}

				break;
			case RFP:
				fileName = rfpEnvelopService.generateEnvelopeZip(eventId, evenvelopId, zos, false, session, virtualizer);
				try {
					envTitle = rftEnvelopService.getEnvelipeTitleById(evenvelopId, eventType.name());
					RfpEventAudit audit = new RfpEventAudit();
					RfpEvent rfpEvent = new RfpEvent();
					rfpEvent.setId(eventId);
					audit.setEvent(rfpEvent);
					audit.setAction(AuditActionType.Download);
					audit.setActionBy(SecurityLibrary.getLoggedInUser());
					audit.setActionDate(new Date());
					audit.setDescription("Entire envelope '" + envTitle + " ' downloaded");
					eventAuditService.save(audit);

				} catch (Exception e) {
					LOG.error(e.getMessage(), e);
				}

				try {
					RfpEvent event = rfpEventService.getPlainEventById(eventId);
					BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.DOWNLOAD, "Envelope '" + envTitle + "' is downloaded successfully for event '" + event.getEventId() + " ", SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser(), new Date(), ModuleType.RFP);
					buyerAuditTrailDao.save(buyerAuditTrail);
				} catch (Exception e) {
					LOG.error("Error while recording auction event Resume " + e.getMessage(), e);
				}
				break;
			case RFQ:
				fileName = rfqEnvelopService.generateEnvelopeZip(eventId, evenvelopId, zos, false, session, virtualizer);
				try {
					envTitle = rftEnvelopService.getEnvelipeTitleById(evenvelopId, eventType.name());
					RfqEventAudit audit = new RfqEventAudit();
					RfqEvent rfqEvent = new RfqEvent();
					rfqEvent.setId(eventId);
					audit.setEvent(rfqEvent);
					audit.setAction(AuditActionType.Download);
					audit.setActionBy(SecurityLibrary.getLoggedInUser());
					audit.setActionDate(new Date());
					audit.setDescription("Entire envelope '" + envTitle + " ' downloaded");
					eventAuditService.save(audit);

				} catch (Exception e) {
					LOG.error(e.getMessage(), e);
				}

				try {
					RfqEvent event = rfqEventService.getPlainEventById(eventId);
					BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.DOWNLOAD, "Envelope '" + envTitle + "' is downloaded successfully for event '" + event.getEventId() + " ", SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser(), new Date(), ModuleType.RFP);
					buyerAuditTrailDao.save(buyerAuditTrail);
				} catch (Exception e) {
					LOG.error("Error while recording auction event Resume " + e.getMessage(), e);
				}

				break;
			case RFT:
				fileName = rftEnvelopService.generateEnvelopeZip(eventId, evenvelopId, zos, false, session, virtualizer);
				try {
					envTitle = rftEnvelopService.getEnvelipeTitleById(evenvelopId, eventType.name());
					RftEventAudit audit = new RftEventAudit();
					RftEvent rftEvent = new RftEvent();
					rftEvent.setId(eventId);
					audit.setEvent(rftEvent);
					audit.setAction(AuditActionType.Download);
					audit.setActionBy(SecurityLibrary.getLoggedInUser());
					audit.setActionDate(new Date());
					audit.setDescription("Entire envelope '" + envTitle + " ' downloaded");
					eventAuditService.save(audit);

				} catch (Exception e) {
					LOG.error(e.getMessage(), e);
				}

				try {
					RftEvent event = rftEventService.getPlainEventById(eventId);
					BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.DOWNLOAD, "Envelope '" + envTitle + "' is downloaded successfully for event '" + event.getEventId() + " ", SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser(), new Date(), ModuleType.RFT);
					buyerAuditTrailDao.save(buyerAuditTrail);
				} catch (Exception e) {
					LOG.error("Error while recording auction event Resume " + e.getMessage(), e);
				}

				break;
			}
			zos.flush();
			zos.close();
			fos.flush();
			fos.close();

			FileInputStream fin = new FileInputStream(zipFile);
			long fileSize = fin.available();

			response.setContentType("application/zip,application/octet-stream");
			response.addHeader("Content-Disposition", "attachment; filename=" + fileName);
			// response.setHeader("Transfer-Encoding", "chunked");
			response.setHeader("Content-Length", "" + fileSize);

			byte[] data = new byte[1000];
			int read = 0;
			while ((read = fin.read(data)) != -1) {
				response.getOutputStream().write(data, 0, read);
			}

			response.getOutputStream().flush();

			fin.close();
			response.getOutputStream().close();

		} catch (Exception e) {
			LOG.error("Error zipping event evaluation report for download : " + e.getMessage(), e);
		} finally {
			if (virtualizer != null) {
				virtualizer.cleanup();
			}
		}
	}

	@RequestMapping(path = "/getSupplierCommentsForBq/{eventType}/{eventId}/{itemId}/{supplierId}", method = RequestMethod.GET)
	public ResponseEntity<List<?>> getSupplierCommentsForBq(@PathVariable("eventType") RfxTypes eventType, @PathVariable("eventId") String eventId, @PathVariable("itemId") String itemId, @PathVariable("supplierId") String supplierId) throws JsonProcessingException {
		LOG.info("EventType : " + eventType + " Event Id : " + eventId + " Item Id : " + itemId + " Supplie Id : " + supplierId);
		HttpHeaders header = new HttpHeaders();
		List<?> comment = null;
		EventPermissions eventPermissions = null;
		try {
			switch (eventType) {
			case RFA:
				eventPermissions = rfaEventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), eventId);
				// comment =
				// rfaBqEvaluationCommentsService.getCommentsForSupplier(supplierService.findSuppById(supplierId),
				// eventId, itemId, ((eventPermissions != null && eventPermissions.isLeadEvaluator()) ? null :
				// SecurityLibrary.getLoggedInUser()));
				comment = rfaBqEvaluationCommentsService.getCommentsForSupplierNew(supplierService.findSuppById(supplierId), eventId, itemId, ((eventPermissions != null && eventPermissions.isLeadEvaluator()) ? null : SecurityLibrary.getLoggedInUser()), SecurityLibrary.getLoggedInUser());
				break;
			case RFI:
				break;
			case RFP:
				eventPermissions = rfpEventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), eventId);
				// comment =
				// rfpBqEvaluationCommentsService.getCommentsForSupplier(supplierService.findSuppById(supplierId),
				// eventId, itemId, ((eventPermissions != null && eventPermissions.isLeadEvaluator()) ? null :
				// SecurityLibrary.getLoggedInUser()));
				comment = rfpBqEvaluationCommentsService.getCommentsForSupplierNew(supplierService.findSuppById(supplierId), eventId, itemId, ((eventPermissions != null && eventPermissions.isLeadEvaluator()) ? null : SecurityLibrary.getLoggedInUser()), SecurityLibrary.getLoggedInUser());
				break;
			case RFQ:
				eventPermissions = rfqEventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), eventId);
				// comment =
				// rfqBqEvaluationCommentsService.getCommentsForSupplier(supplierService.findSuppById(supplierId),
				// eventId, itemId, ((eventPermissions != null && eventPermissions.isLeadEvaluator()) ? null :
				// SecurityLibrary.getLoggedInUser()));
				comment = rfqBqEvaluationCommentsService.getCommentsForSupplierNew(supplierService.findSuppById(supplierId), eventId, itemId, ((eventPermissions != null && eventPermissions.isLeadEvaluator()) ? null : SecurityLibrary.getLoggedInUser()), SecurityLibrary.getLoggedInUser());
				break;
			case RFT:
				eventPermissions = rftEventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), eventId);
				// comment =
				// rftBqEvaluationCommentsService.getCommentsForSupplier(supplierService.findSuppById(supplierId),
				// eventId, itemId, ((eventPermissions != null && eventPermissions.isLeadEvaluator()) ? null :
				// SecurityLibrary.getLoggedInUser()));
				comment = rftBqEvaluationCommentsService.getCommentsForSupplierNew(supplierService.findSuppById(supplierId), eventId, itemId, ((eventPermissions != null && eventPermissions.isLeadEvaluator()) ? null : SecurityLibrary.getLoggedInUser()), SecurityLibrary.getLoggedInUser());
				break;
			default:
				break;
			}

		} catch (Exception e) {
			LOG.error("Error : " + e.getMessage(), e);
			header.add("error", "Error showing SupplierBqComment : " + e.getMessage());
			return new ResponseEntity<List<?>>(comment, header, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<List<?>>(comment, header, HttpStatus.OK);
	}

	@RequestMapping(path = "addCommentsForBq/{eventType}", method = RequestMethod.POST)
	public ResponseEntity<List<?>> addCommentsForBq(@PathVariable("eventType") RfxTypes eventType, @RequestParam("eventId") String eventId, @RequestParam("itemId") String itemId, @RequestParam("supplierId") String supplierId, @RequestParam String comments) {
		LOG.info("EventType : " + eventType + " Event Id : " + eventId + " Item Id : " + itemId + " Supplie Id : " + supplierId + " Cooments : " + comments);

		HttpHeaders header = new HttpHeaders();
		List<?> comment = null;
		try {
			switch (eventType) {
			case RFA: {
				RfaBqEvaluationComments coment = new RfaBqEvaluationComments();
				coment.setComment(comments);
				coment.setBqItem(rfaBqService.getBqItembyBqItemId(itemId));
				coment.setEvent(rfaEventService.getRfaEventById(eventId));
				coment.setSupplier(supplierService.findSuppById(supplierId));
				coment.setCreatedBy(SecurityLibrary.getLoggedInUser());
				coment.setCreatedDate(new Date());
				rfaBqEvaluationCommentsService.SaveComment(coment);
				comment = rfaBqEvaluationCommentsService.getCommentsForSupplier(supplierService.findSuppById(supplierId), eventId, itemId, null);
				break;
			}
			case RFI: {
				break;
			}
			case RFP: {
				RfpBqEvaluationComments coment = new RfpBqEvaluationComments();
				coment.setComment(comments);
				coment.setBqItem(rfpBqService.getBqItembyBqItemId(itemId));
				coment.setEvent(rfpEventService.getEventById(eventId));
				coment.setSupplier(supplierService.findSuppById(supplierId));
				coment.setCreatedBy(SecurityLibrary.getLoggedInUser());
				coment.setCreatedDate(new Date());
				rfpBqEvaluationCommentsService.SaveComment(coment);
				comment = rfpBqEvaluationCommentsService.getCommentsForSupplier(supplierService.findSuppById(supplierId), eventId, itemId, null);

				break;
			}
			case RFQ: {
				RfqBqEvaluationComments coment = new RfqBqEvaluationComments();
				coment.setComment(comments);
				coment.setBqItem(rfqBqService.getBqItembyBqItemId(itemId));
				coment.setEvent(rfqEventService.getEventById(eventId));
				coment.setSupplier(supplierService.findSuppById(supplierId));
				coment.setCreatedBy(SecurityLibrary.getLoggedInUser());
				coment.setCreatedDate(new Date());
				rfqBqEvaluationCommentsService.SaveComment(coment);
				comment = rfqBqEvaluationCommentsService.getCommentsForSupplier(supplierService.findSuppById(supplierId), eventId, itemId, null);

				break;
			}
			case RFT: {
				RftBqEvaluationComments coment = new RftBqEvaluationComments();
				coment.setComment(comments);
				coment.setBqItem(rftBqService.getBqItembyBqItemId(itemId));
				coment.setEvent(rftEventService.getRftEventById(eventId));
				coment.setSupplier(supplierService.findSuppById(supplierId));
				coment.setCreatedBy(SecurityLibrary.getLoggedInUser());
				coment.setCreatedDate(new Date());
				rftBqEvaluationCommentsService.SaveComment(coment);
				comment = rftBqEvaluationCommentsService.getCommentsForSupplier(supplierService.findSuppById(supplierId), eventId, itemId, null);

				break;
			}
			default:
				break;
			}

		} catch (Exception e) {
			LOG.error("Error : " + e.getMessage(), e);
			header.add("error", "Error showing Remarks : " + e.getMessage());
			return new ResponseEntity<List<?>>(comment, header, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<List<?>>(comment, header, HttpStatus.OK);
	}


	@RequestMapping(path = "/removeCommentsSor/{eventType}/{eventId}/{itemId}/{supplierId}/{commentId}", method = RequestMethod.POST)
	public ResponseEntity<List<?>> removeCommentsSor(@PathVariable("eventType") RfxTypes eventType, @PathVariable("eventId") String eventId, @PathVariable("itemId") String itemId, @PathVariable("supplierId") String supplierId, @PathVariable("commentId") String commentId) throws JsonProcessingException {
		LOG.info("EventType : " + eventType + " Event Id : " + eventId + " Item Id : " + itemId + " Supplie Id : " + supplierId + " commentId : " + commentId);
		HttpHeaders header = new HttpHeaders();
		boolean showError = false;
		List<?> comment = null;
		EventPermissions eventPermissions = null;
		try {

			switch (eventType) {
				case RFA:
					RfaSorEvaluationComments evaluationComment = rfaSorEvaluationCommentsService.findComment(commentId);
					if (evaluationComment.getCreatedBy() != null && evaluationComment.getCreatedBy().getId().equals(SecurityLibrary.getLoggedInUser().getId())) {
						rfaSorEvaluationCommentsService.deleteComment(evaluationComment);
					} else {
						showError = true;
					}
					eventPermissions = rfaEventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), eventId);
					comment = rfaSorEvaluationCommentsService.getCommentsForSupplier(supplierService.findSuppById(supplierId), eventId, itemId, ((eventPermissions != null && eventPermissions.isLeadEvaluator()) ? null : SecurityLibrary.getLoggedInUser()));
					break;
				case RFI:
					RfiSorEvaluationComments evaluationCommentRfi = rfiSorEvaluationCommentsService.findComment(commentId);
					if (evaluationCommentRfi.getCreatedBy() != null && evaluationCommentRfi.getCreatedBy().getId().equals(SecurityLibrary.getLoggedInUser().getId())) {
						rfiSorEvaluationCommentsService.deleteComment(evaluationCommentRfi);
					} else {
						showError = true;
					}
					eventPermissions = rfiEventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), eventId);
					comment = rfiSorEvaluationCommentsService.getCommentsForSupplier(supplierService.findSuppById(supplierId), eventId, itemId, ((eventPermissions != null && eventPermissions.isLeadEvaluator()) ? null : SecurityLibrary.getLoggedInUser()));
					break;
				case RFP:
					RfpSorEvaluationComments rfpevaluationComment = rfpSorEvaluationCommentsService.findComment(commentId);
					LOG.info("........................." + rfpevaluationComment.getComment() + "........." + rfpevaluationComment.getCreatedBy().getName());
					if (rfpevaluationComment.getCreatedBy() != null && rfpevaluationComment.getCreatedBy().getId().equals(SecurityLibrary.getLoggedInUser().getId())) {
						rfpSorEvaluationCommentsService.deleteComment(rfpSorEvaluationCommentsService.findComment(commentId));
					} else {
						showError = true;
					}
					eventPermissions = rfpEventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), eventId);
					comment = rfpSorEvaluationCommentsService.getCommentsForSupplier(supplierService.findSuppById(supplierId), eventId, itemId, ((eventPermissions != null && eventPermissions.isLeadEvaluator()) ? null : SecurityLibrary.getLoggedInUser()));
					break;
				case RFQ:
					RfqSorEvaluationComments rfqevaluationComment = rfqSorEvaluationCommentsService.findComment(commentId);
					LOG.info("........................." + rfqevaluationComment.getComment() + "........." + rfqevaluationComment.getCreatedBy().getName());
					if (rfqevaluationComment.getCreatedBy() != null && rfqevaluationComment.getCreatedBy().getId().equals(SecurityLibrary.getLoggedInUser().getId())) {
						rfqSorEvaluationCommentsService.deleteComment(rfqSorEvaluationCommentsService.findComment(commentId));
					} else {
						showError = true;
					}
					eventPermissions = rfqEventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), eventId);
					comment = rfqSorEvaluationCommentsService.getCommentsForSupplier(supplierService.findSuppById(supplierId), eventId, itemId, ((eventPermissions != null && eventPermissions.isLeadEvaluator()) ? null : SecurityLibrary.getLoggedInUser()));
					break;
				case RFT:
					RftSorEvaluationComments rftevaluationComment = rftSorEvaluationCommentsService.findComment(commentId);
					LOG.info("........................." + rftevaluationComment.getComment() + "........." + rftevaluationComment.getCreatedBy().getName());
					if (rftevaluationComment.getCreatedBy() != null && rftevaluationComment.getCreatedBy().getId().equals(SecurityLibrary.getLoggedInUser().getId())) {
						rftSorEvaluationCommentsService.deleteComment(rftSorEvaluationCommentsService.findComment(commentId));
					} else {
						showError = true;
					}
					eventPermissions = rftEventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), eventId);
					comment = rftSorEvaluationCommentsService.getCommentsForSupplier(supplierService.findSuppById(supplierId), eventId, itemId, ((eventPermissions != null && eventPermissions.isLeadEvaluator()) ? null : SecurityLibrary.getLoggedInUser()));
					break;
				default:
					break;
			}

		} catch (Exception e) {
			LOG.error("Error : " + e.getMessage(), e);
			header.add("error", "Error showing Remarks : " + e.getMessage());
			return new ResponseEntity<List<?>>(comment, header, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		if (showError) {
			header.add("error", "You can not delete other evalutors comments");
			return new ResponseEntity<List<?>>(comment, header, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		return new ResponseEntity<List<?>>(comment, header, HttpStatus.OK);
	}


	@RequestMapping(path = "addCommentsForSor/{eventType}", method = RequestMethod.POST)
	public ResponseEntity<List<?>> addCommentsForSor(@PathVariable("eventType") RfxTypes eventType, @RequestParam("eventId") String eventId, @RequestParam("itemId") String itemId, @RequestParam("supplierId") String supplierId, @RequestParam String comments) {
		LOG.info("EventType : " + eventType + " Event Id : " + eventId + " Item Id : " + itemId + " Supplie Id : " + supplierId + " Cooments : " + comments);

		HttpHeaders header = new HttpHeaders();
		List<?> comment = null;
		try {
			switch (eventType) {
				case RFA: {
					RfaSorEvaluationComments coment = new RfaSorEvaluationComments();
					coment.setComment(comments);
					coment.setBqItem(rfaSorService.getSorItemsbySorId(itemId));
					coment.setEvent(rfaEventService.getRfaEventById(eventId));
					coment.setSupplier(supplierService.findSuppById(supplierId));
					coment.setCreatedBy(SecurityLibrary.getLoggedInUser());
					coment.setCreatedDate(new Date());
					rfaSorEvaluationCommentsService.saveComment(coment);
					comment = rfaSorEvaluationCommentsService.getCommentsForSupplier(supplierService.findSuppById(supplierId), eventId, itemId, null);
					break;
				}
				case RFI: {
					RfiSorEvaluationComments coment = new RfiSorEvaluationComments();
					coment.setComment(comments);
					coment.setBqItem(rfiSorService.getSorItemsbySorId(itemId));
					coment.setEvent(rfiEventService.getRfiEventById(eventId));
					coment.setSupplier(supplierService.findSuppById(supplierId));
					coment.setCreatedBy(SecurityLibrary.getLoggedInUser());
					coment.setCreatedDate(new Date());
					rfiSorEvaluationCommentsService.saveComment(coment);
					comment = rfiSorEvaluationCommentsService.getCommentsForSupplier(supplierService.findSuppById(supplierId), eventId, itemId, null);
					break;
				}
				case RFP: {
					RfpSorEvaluationComments coment = new RfpSorEvaluationComments();
					coment.setComment(comments);
					coment.setBqItem(rfpSorService.getSorItemsbySorId(itemId));
					coment.setEvent(rfpEventService.getEventById(eventId));
					coment.setSupplier(supplierService.findSuppById(supplierId));
					coment.setCreatedBy(SecurityLibrary.getLoggedInUser());
					coment.setCreatedDate(new Date());
					rfpSorEvaluationCommentsService.saveComment(coment);
					comment = rfpSorEvaluationCommentsService.getCommentsForSupplier(supplierService.findSuppById(supplierId), eventId, itemId, null);

					break;
				}
				case RFQ: {
					RfqSorEvaluationComments coment = new RfqSorEvaluationComments();
					coment.setComment(comments);
					coment.setBqItem(rfqSorService.getSorItemsbySorId(itemId));
					coment.setEvent(rfqEventService.getEventById(eventId));
					coment.setSupplier(supplierService.findSuppById(supplierId));
					coment.setCreatedBy(SecurityLibrary.getLoggedInUser());
					coment.setCreatedDate(new Date());
					rfqSorEvaluationCommentsService.saveComment(coment);
					comment = rfqSorEvaluationCommentsService.getCommentsForSupplier(supplierService.findSuppById(supplierId), eventId, itemId, null);

					break;
				}
				case RFT: {
					RftSorEvaluationComments coment = new RftSorEvaluationComments();
					coment.setComment(comments);
					coment.setBqItem(rftSorService.getSorItemsbySorId(itemId));
					coment.setEvent(rftEventService.getRftEventById(eventId));
					coment.setSupplier(supplierService.findSuppById(supplierId));
					coment.setCreatedBy(SecurityLibrary.getLoggedInUser());
					coment.setCreatedDate(new Date());
					rftSorEvaluationCommentsService.saveComment(coment);
					comment = rftSorEvaluationCommentsService.getCommentsForSupplier(supplierService.findSuppById(supplierId), eventId, itemId, null);

					break;
				}
				default:
					break;
			}

		} catch (Exception e) {
			LOG.error("Error : " + e.getMessage(), e);
			header.add("error", "Error showing Remarks : " + e.getMessage());
			return new ResponseEntity<List<?>>(comment, header, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<List<?>>(comment, header, HttpStatus.OK);
	}

	@RequestMapping(path = "/removeCommentsBq/{eventType}/{eventId}/{itemId}/{supplierId}/{commentId}", method = RequestMethod.POST)
	public ResponseEntity<List<?>> removeCommentsBq(@PathVariable("eventType") RfxTypes eventType, @PathVariable("eventId") String eventId, @PathVariable("itemId") String itemId, @PathVariable("supplierId") String supplierId, @PathVariable("commentId") String commentId) throws JsonProcessingException {
		LOG.info("EventType : " + eventType + " Event Id : " + eventId + " Item Id : " + itemId + " Supplie Id : " + supplierId + " commentId : " + commentId);
		HttpHeaders header = new HttpHeaders();
		boolean showError = false;
		List<?> comment = null;
		EventPermissions eventPermissions = null;
		try {

			switch (eventType) {
			case RFA:
				RfaBqEvaluationComments evaluationComment = rfaBqEvaluationCommentsService.findComment(commentId);
				if (evaluationComment.getCreatedBy() != null && evaluationComment.getCreatedBy().getId().equals(SecurityLibrary.getLoggedInUser().getId())) {
					rfaBqEvaluationCommentsService.deleteComment(rfaBqEvaluationCommentsService.findComment(commentId));
				} else {
					showError = true;
				}
				eventPermissions = rfaEventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), eventId);
				comment = rfaBqEvaluationCommentsService.getCommentsForSupplier(supplierService.findSuppById(supplierId), eventId, itemId, ((eventPermissions != null && eventPermissions.isLeadEvaluator()) ? null : SecurityLibrary.getLoggedInUser()));
				break;
			case RFI:
				break;
			case RFP:
				RfpBqEvaluationComments rfpevaluationComment = rfpBqEvaluationCommentsService.findComment(commentId);
				LOG.info("........................." + rfpevaluationComment.getComment() + "........." + rfpevaluationComment.getCreatedBy().getName());
				if (rfpevaluationComment.getCreatedBy() != null && rfpevaluationComment.getCreatedBy().getId().equals(SecurityLibrary.getLoggedInUser().getId())) {
					rfpBqEvaluationCommentsService.deleteComment(rfpBqEvaluationCommentsService.findComment(commentId));
				} else {
					showError = true;
				}
				eventPermissions = rfpEventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), eventId);
				comment = rfpBqEvaluationCommentsService.getCommentsForSupplier(supplierService.findSuppById(supplierId), eventId, itemId, ((eventPermissions != null && eventPermissions.isLeadEvaluator()) ? null : SecurityLibrary.getLoggedInUser()));
				break;
			case RFQ:
				RfqBqEvaluationComments rfqevaluationComment = rfqBqEvaluationCommentsService.findComment(commentId);
				LOG.info("........................." + rfqevaluationComment.getComment() + "........." + rfqevaluationComment.getCreatedBy().getName());
				if (rfqevaluationComment.getCreatedBy() != null && rfqevaluationComment.getCreatedBy().getId().equals(SecurityLibrary.getLoggedInUser().getId())) {
					rfqBqEvaluationCommentsService.deleteComment(rfqBqEvaluationCommentsService.findComment(commentId));
				} else {
					showError = true;
				}
				eventPermissions = rfqEventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), eventId);
				comment = rfqBqEvaluationCommentsService.getCommentsForSupplier(supplierService.findSuppById(supplierId), eventId, itemId, ((eventPermissions != null && eventPermissions.isLeadEvaluator()) ? null : SecurityLibrary.getLoggedInUser()));
				break;
			case RFT:
				RftBqEvaluationComments rftevaluationComment = rftBqEvaluationCommentsService.findComment(commentId);
				LOG.info("........................." + rftevaluationComment.getComment() + "........." + rftevaluationComment.getCreatedBy().getName());
				if (rftevaluationComment.getCreatedBy() != null && rftevaluationComment.getCreatedBy().getId().equals(SecurityLibrary.getLoggedInUser().getId())) {
					rftBqEvaluationCommentsService.deleteComment(rftBqEvaluationCommentsService.findComment(commentId));
				} else {
					showError = true;
				}
				eventPermissions = rftEventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), eventId);
				comment = rftBqEvaluationCommentsService.getCommentsForSupplier(supplierService.findSuppById(supplierId), eventId, itemId, ((eventPermissions != null && eventPermissions.isLeadEvaluator()) ? null : SecurityLibrary.getLoggedInUser()));
				break;
			default:
				break;
			}

		} catch (Exception e) {
			LOG.error("Error : " + e.getMessage(), e);
			header.add("error", "Error showing Remarks : " + e.getMessage());
			return new ResponseEntity<List<?>>(comment, header, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		if (showError) {
			header.add("error", "You can not delete other evalutors comments");
			return new ResponseEntity<List<?>>(comment, header, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		return new ResponseEntity<List<?>>(comment, header, HttpStatus.OK);
	}

	@RequestMapping(value = "/getBqTotalComment/{eventType}/{bqId}/{supplierId}/{eventId}", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<List<?>> getBqTotalComment(@PathVariable("eventType") RfxTypes eventType, @PathVariable("bqId") String bqId, @PathVariable("supplierId") String supplierId, @PathVariable("eventId") String eventId) throws JsonProcessingException {

		LOG.info("BqIdId  :" + bqId + " Event Type :: " + eventType + " BqId : " + bqId + " Supplier Id : " + supplierId + " Event Id : " + eventId);

		HttpHeaders headers = new HttpHeaders();
		List<?> comment = null;
		EventPermissions eventPermissions = null;
		try {

			switch (eventType) {
			case RFA:
				eventPermissions = rfaEventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), eventId);
				// comment = rfaBqTotalEvaluationCommentsService.getComments(supplierId, eventId, bqId,
				// ((eventPermissions != null && eventPermissions.isLeadEvaluator()) ? null :
				// SecurityLibrary.getLoggedInUser()));
				comment = rfaBqTotalEvaluationCommentsService.getCommentsNew(supplierId, eventId, bqId, ((eventPermissions != null && eventPermissions.isLeadEvaluator()) ? null : SecurityLibrary.getLoggedInUser()), SecurityLibrary.getLoggedInUser());
				break;
			case RFI:
				break;
			case RFP:
				eventPermissions = rfpEventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), eventId);
				// comment = rfpBqTotalEvaluationCommentsService.getComments(supplierId, eventId, bqId,
				// ((eventPermissions != null && eventPermissions.isLeadEvaluator()) ? null :
				// SecurityLibrary.getLoggedInUser()));
				comment = rfpBqTotalEvaluationCommentsService.getCommentsNew(supplierId, eventId, bqId, ((eventPermissions != null && eventPermissions.isLeadEvaluator()) ? null : SecurityLibrary.getLoggedInUser()), SecurityLibrary.getLoggedInUser());
				break;
			case RFQ:
				eventPermissions = rfqEventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), eventId);
				// comment = rfqBqTotalEvaluationCommentsService.getComments(supplierId, eventId, bqId,
				// ((eventPermissions != null && eventPermissions.isLeadEvaluator()) ? null :
				// SecurityLibrary.getLoggedInUser()));
				comment = rfqBqTotalEvaluationCommentsService.getCommentsNew(supplierId, eventId, bqId, ((eventPermissions != null && eventPermissions.isLeadEvaluator()) ? null : SecurityLibrary.getLoggedInUser()), SecurityLibrary.getLoggedInUser());
				break;
			case RFT:
				eventPermissions = rftEventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), eventId);
				// comment = rftBqTotalEvaluationCommentsService.getComments(supplierId, eventId, bqId,
				// ((eventPermissions != null && eventPermissions.isLeadEvaluator()) ? null :
				// SecurityLibrary.getLoggedInUser()));
				comment = rftBqTotalEvaluationCommentsService.getCommentsNew(supplierId, eventId, bqId, ((eventPermissions != null && eventPermissions.isLeadEvaluator()) ? null : SecurityLibrary.getLoggedInUser()), SecurityLibrary.getLoggedInUser());
				break;
			default:
				break;
			}

		} catch (Exception e) {
			LOG.error("Error : " + e.getMessage(), e);
			headers.add("error", "Error showing Remarks : " + e.getMessage());
			return new ResponseEntity<List<?>>(null, headers, HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<List<?>>(comment, headers, HttpStatus.OK);
	}

	@RequestMapping(path = "addTotalCommentsForBq/{eventType}", method = RequestMethod.POST)
	public ResponseEntity<List<?>> addTotalCommentsForBq(@PathVariable("eventType") RfxTypes eventType, @RequestParam("eventId") String eventId, @RequestParam("bqId") String bqId, @RequestParam("supplierId") String supplierId, @RequestParam String comments) {
		LOG.info("EventType : " + eventType + " Event Id : " + eventId + " Item Id : " + bqId + " Supplie Id : " + supplierId + " Cooments : " + comments);

		HttpHeaders header = new HttpHeaders();
		List<?> comment = null;
		try {
			switch (eventType) {
			case RFA: {
				RfaBqTotalEvaluationComments coment = new RfaBqTotalEvaluationComments();
				coment.setComment(comments);
				coment.setBq(rfaBqService.getRfaBqById(bqId));
				coment.setEvent(rfaEventService.getPlainEventById(eventId));
				coment.setSupplier(supplierService.findSuppById(supplierId));
				coment.setCreatedBy(SecurityLibrary.getLoggedInUser());
				coment.setCreatedDate(new Date());
				rfaBqTotalEvaluationCommentsService.SaveComment(coment);
				comment = rfaBqTotalEvaluationCommentsService.getComments(supplierId, eventId, bqId, SecurityLibrary.getLoggedInUser());

				break;
			}
			case RFI: {
				break;
			}
			case RFP: {
				RfpBqTotalEvaluationComments coment = new RfpBqTotalEvaluationComments();
				coment.setComment(comments);
				coment.setBq(rfpBqService.getBqById(bqId));
				coment.setEvent(rfpEventService.getPlainEventById(eventId));
				coment.setSupplier(supplierService.findSuppById(supplierId));
				coment.setCreatedBy(SecurityLibrary.getLoggedInUser());
				coment.setCreatedDate(new Date());
				rfpBqTotalEvaluationCommentsService.SaveComment(coment);
				comment = rfpBqTotalEvaluationCommentsService.getComments(supplierId, eventId, bqId, SecurityLibrary.getLoggedInUser());

				break;
			}
			case RFQ: {
				RfqBqTotalEvaluationComments coment = new RfqBqTotalEvaluationComments();
				coment.setComment(comments);
				coment.setBq(rfqBqService.getBqById(bqId));
				coment.setEvent(rfqEventService.getPlainEventById(eventId));
				coment.setSupplier(supplierService.findSuppById(supplierId));
				coment.setCreatedBy(SecurityLibrary.getLoggedInUser());
				coment.setCreatedDate(new Date());
				rfqBqTotalEvaluationCommentsService.SaveComment(coment);
				comment = rfqBqTotalEvaluationCommentsService.getComments(supplierId, eventId, bqId, SecurityLibrary.getLoggedInUser());

				break;
			}
			case RFT: {
				RftBqTotalEvaluationComments coment = new RftBqTotalEvaluationComments();
				coment.setComment(comments);
				coment.setBq(rftBqService.getRftBqById(bqId));
				coment.setEvent(rftEventService.getPlainEventById(eventId));
				coment.setSupplier(supplierService.findSuppById(supplierId));
				coment.setCreatedBy(SecurityLibrary.getLoggedInUser());
				coment.setCreatedDate(new Date());
				rftBqTotalEvaluationCommentsService.SaveComment(coment);
				comment = rftBqTotalEvaluationCommentsService.getComments(supplierId, eventId, bqId, SecurityLibrary.getLoggedInUser());

				break;
			}
			default:
				break;
			}

		} catch (Exception e) {
			LOG.error("Error : " + e.getMessage(), e);
			header.add("error", "Error showing Remarks : " + e.getMessage());
			return new ResponseEntity<List<?>>(comment, header, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<List<?>>(comment, header, HttpStatus.OK);
	}

	@RequestMapping(path = "/deleteBqTotalComent/{eventType}/{eventId}/{bqId}/{supplierId}/{commentId}", method = RequestMethod.POST)
	public ResponseEntity<List<?>> deleteBqTotalComent(@PathVariable("eventType") RfxTypes eventType, @PathVariable("eventId") String eventId, @PathVariable("bqId") String bqId, @PathVariable("supplierId") String supplierId, @PathVariable("commentId") String commentId) throws JsonProcessingException {
		LOG.info("EventType : " + eventType + " Event Id : " + eventId + " Item Id : " + bqId + " Supplie Id : " + supplierId + " commentId : " + commentId);
		HttpHeaders header = new HttpHeaders();
		List<?> comment = null;
		try {
			switch (eventType) {
			case RFA:
				rfaBqTotalEvaluationCommentsService.deleteComment(commentId);
				comment = rfaBqTotalEvaluationCommentsService.getComments(supplierId, eventId, bqId, SecurityLibrary.getLoggedInUser());
				break;
			case RFI:
				break;
			case RFP:
				rfpBqTotalEvaluationCommentsService.deleteComment(commentId);
				comment = rfpBqTotalEvaluationCommentsService.getComments(supplierId, eventId, bqId, SecurityLibrary.getLoggedInUser());
				break;
			case RFQ:
				rfqBqTotalEvaluationCommentsService.deleteComment(commentId);
				comment = rfqBqTotalEvaluationCommentsService.getComments(supplierId, eventId, bqId, SecurityLibrary.getLoggedInUser());
				break;
			case RFT:
				rftBqTotalEvaluationCommentsService.deleteComment(commentId);
				comment = rftBqTotalEvaluationCommentsService.getComments(supplierId, eventId, bqId, SecurityLibrary.getLoggedInUser());
				break;
			default:
				break;
			}

		} catch (Exception e) {
			LOG.error("Error : " + e.getMessage(), e);
			header.add("error", "Error showing Remarks : " + e.getMessage());
			return new ResponseEntity<List<?>>(comment, header, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<List<?>>(comment, header, HttpStatus.OK);
	}

	@RequestMapping(path = "/getSupplierCommentsForSor/{eventType}/{eventId}/{itemId}/{supplierId}", method = RequestMethod.GET)
	public ResponseEntity<List<?>> getSupplierCommentsForSor(@PathVariable("eventType") RfxTypes eventType, @PathVariable("eventId") String eventId, @PathVariable("itemId") String itemId, @PathVariable("supplierId") String supplierId) throws JsonProcessingException {
		LOG.info("EventType : " + eventType + " Event Id : " + eventId + " Item Id : " + itemId + " Supplie Id : " + supplierId);
		HttpHeaders header = new HttpHeaders();
		List<?> comment = null;
		EventPermissions eventPermissions = null;
		try {
			switch (eventType) {
				case RFA:
					eventPermissions = rfaEventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), eventId);
					comment = rfaSorEvaluationCommentsService.getCommentsForSupplierNew(supplierService.findSuppById(supplierId), eventId, itemId, ((eventPermissions != null && eventPermissions.isLeadEvaluator()) ? null : SecurityLibrary.getLoggedInUser()), SecurityLibrary.getLoggedInUser());
					break;
				case RFI:
					eventPermissions = rfiEventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), eventId);
					comment = rfiSorEvaluationCommentsService.getCommentsForSupplierNew(supplierService.findSuppById(supplierId), eventId, itemId, ((eventPermissions != null && eventPermissions.isLeadEvaluator()) ? null : SecurityLibrary.getLoggedInUser()), SecurityLibrary.getLoggedInUser());
					break;
				case RFP:
					eventPermissions = rfpEventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), eventId);
					comment = rfpSorEvaluationCommentsService.getCommentsForSupplierNew(supplierService.findSuppById(supplierId), eventId, itemId, ((eventPermissions != null && eventPermissions.isLeadEvaluator()) ? null : SecurityLibrary.getLoggedInUser()), SecurityLibrary.getLoggedInUser());
					break;
				case RFQ:
					eventPermissions = rfqEventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), eventId);
					comment = rfqSorEvaluationCommentsService.getCommentsForSupplierNew(supplierService.findSuppById(supplierId), eventId, itemId, ((eventPermissions != null && eventPermissions.isLeadEvaluator()) ? null : SecurityLibrary.getLoggedInUser()), SecurityLibrary.getLoggedInUser());
					break;
				case RFT:
					eventPermissions = rftEventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), eventId);
					comment = rftSorEvaluationCommentsService.getCommentsForSupplierNew(supplierService.findSuppById(supplierId), eventId, itemId, ((eventPermissions != null && eventPermissions.isLeadEvaluator()) ? null : SecurityLibrary.getLoggedInUser()), SecurityLibrary.getLoggedInUser());
					break;
				default:
					break;
			}

		} catch (Exception e) {
			LOG.error("Error : " + e.getMessage(), e);
			header.add("error", "Error showing SupplierSorComment : " + e.getMessage());
			return new ResponseEntity<List<?>>(comment, header, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<List<?>>(comment, header, HttpStatus.OK);
	}

	@RequestMapping(path = "/disqualifySupplier/{eventType}", method = RequestMethod.POST)
	public String disQualifiySupplier(Model model, @PathVariable("eventType") RfxTypes eventType, @RequestParam("eventId") String eventId, @RequestParam("envelopId") String envelopId, @RequestParam("supplier") String supplier, @RequestParam("remarks") String remarks, @RequestParam("qualify") Boolean qualify, RedirectAttributes rAttributes) throws JsonProcessingException {
		LOG.info("DISQUALIFIY SUPPLIER : " + supplier + " Event Id : " + eventId + " Type : " + eventType + " qualify " + qualify);
		Event event = null;
		Envelop envelop = null;
		switch (eventType) {
		case RFA: {
			envelop = rfaEnvelopService.getEnvelopForEvaluationById(envelopId, SecurityLibrary.getLoggedInUser());
			event = ((RfaEnvelop) envelop).getRfxEvent();
			RfaEvent rfaEvent = rfaEventService.getPlainEventById(eventId);
			RfaEventSupplier eventSupplier = rfaEventSupplierService.findSupplierByIdAndEventId(supplier, eventId);
			if (eventSupplier != null) {
				if (!qualify) {
					eventSupplier.setDisqualify(Boolean.TRUE);
					eventSupplier.setDisqualifiedBy(SecurityLibrary.getLoggedInUser());
					eventSupplier.setDisqualifiedTime(new Date());
					eventSupplier.setDisqualifyRemarks(remarks);
					eventSupplier.setDisqualifiedEnvelope((RfaEnvelop) envelop);
					if (event.getViewSupplerName() != null && !event.getViewSupplerName() && !event.getDisableMasking()) {
						eventSupplier.setSupplierCompanyName(MaskUtils.maskName(envelop.getPreFix(), eventSupplier.getSupplier().getId(), envelop.getId()));
						rAttributes.addFlashAttribute("success", eventSupplier.getSupplierCompanyName() + " Supplier Disqualified");
					} else {
						rAttributes.addFlashAttribute("success", eventSupplier.getSupplier().getCompanyName() + " Supplier Disqualified");
					}

					try {
						RfaEventAudit audit = new RfaEventAudit();
						audit.setActionBy(SecurityLibrary.getLoggedInUser());
						audit.setActionDate(new Date());
						audit.setDescription("Supplier '" + eventSupplier.getSupplierCompanyName() + " 'is disqualified from Envelope '" + envelop.getEnvelopTitle() + " '");
						audit.setAction(AuditActionType.Disqualified);
						audit.setEvent(((RfaEnvelop) envelop).getRfxEvent());
						eventAuditService.save(audit);
					} catch (Exception e) {
						LOG.error(e.getMessage(), e);
					}
					try {
						BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.DISQUALIFIED, "Supplier '" + eventSupplier.getSupplierCompanyName() + "' is disqualified in Envelope '" + envelop.getEnvelopTitle() + "' for event '" + rfaEvent.getEventId() + "' ", SecurityLibrary.getLoggedInUser().getTenantId(), SecurityLibrary.getLoggedInUser(), new Date(), ModuleType.RFA);
						buyerAuditTrailDao.save(buyerAuditTrail);
					} catch (Exception e) {
						LOG.error("Error while recording auction event Resume " + e.getMessage(), e);
					}

				} else {
					eventSupplier.setDisqualify(Boolean.FALSE);
					eventSupplier.setDisqualifiedBy(null);
					eventSupplier.setReQualifiedTime(new Date());
					eventSupplier.setDisqualifiedTime(null);
					eventSupplier.setDisqualifyRemarks(remarks);
					eventSupplier.setDisqualifyRemarks(remarks);
					eventSupplier.setDisqualifiedEnvelope(null);
					if (event.getViewSupplerName() != null && !event.getViewSupplerName() && !event.getDisableMasking()) {
						eventSupplier.setSupplierCompanyName(MaskUtils.maskName(envelop.getPreFix(), eventSupplier.getSupplier().getId(), envelop.getId()));
						rAttributes.addFlashAttribute("success", eventSupplier.getSupplierCompanyName() + " Supplier Re Qualified");
					} else {
						rAttributes.addFlashAttribute("success", eventSupplier.getSupplier().getCompanyName() + " Supplier Re Qualified");
					}

					try {
						RfaEventAudit audit = new RfaEventAudit();
						audit.setActionBy(SecurityLibrary.getLoggedInUser());
						audit.setActionDate(new Date());
						audit.setDescription("Supplier '" + eventSupplier.getSupplierCompanyName() + " 'is requalified for Envelope '" + envelop.getEnvelopTitle() + " '");
						audit.setAction(AuditActionType.Requalified);
						audit.setEvent(((RfaEnvelop) envelop).getRfxEvent());
						eventAuditService.save(audit);
					} catch (Exception e) {
						LOG.error(e.getMessage(), e);
					}
					try {
						BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.REQUALIFIED, "Supplier '" + eventSupplier.getSupplierCompanyName() + "' is requalified in Envelope '" + envelop.getEnvelopTitle() + " ' for Event " + rfaEvent.getEventId() + "' ", SecurityLibrary.getLoggedInUser().getTenantId(), SecurityLibrary.getLoggedInUser(), new Date(), ModuleType.RFA);
						buyerAuditTrailDao.save(buyerAuditTrail);
					} catch (Exception e) {
						LOG.error("Error while recording auction event Resume " + e.getMessage(), e);
					}
				}
				rfaEventSupplierService.updateRfaEventSuppliers(eventSupplier);
			}
			model.addAttribute("eventPermissions", rfaEventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), eventId));
			break;
		}
		case RFI: {
			envelop = rfiEnvelopService.getEnvelopForEvaluationById(envelopId, SecurityLibrary.getLoggedInUser());
			event = ((RfiEnvelop) envelop).getRfxEvent();
			RfiEvent rfiEvent = rfiEventService.getPlainEventById(eventId);
			RfiEventSupplier eventSupplier = rfiEventSupplierService.findSupplierByIdAndEventId(supplier, eventId);
			if (eventSupplier != null) {
				if (!qualify) {
					eventSupplier.setDisqualify(Boolean.TRUE);
					eventSupplier.setDisqualifiedBy(SecurityLibrary.getLoggedInUser());
					eventSupplier.setDisqualifiedTime(new Date());
					eventSupplier.setDisqualifyRemarks(remarks);
					eventSupplier.setDisqualifiedEnvelope((RfiEnvelop) envelop);
					if (event.getViewSupplerName() != null && !event.getViewSupplerName() && !event.getDisableMasking()) {
						eventSupplier.setSupplierCompanyName(MaskUtils.maskName(envelop.getPreFix(), eventSupplier.getSupplier().getId(), envelop.getId()));
						rAttributes.addFlashAttribute("success", eventSupplier.getSupplierCompanyName() + " Supplier Disqualified");
					} else {
						rAttributes.addFlashAttribute("success", eventSupplier.getSupplier().getCompanyName() + " Supplier Disqualified");
					}
					try {
						RfiEventAudit audit = new RfiEventAudit();
						audit.setActionBy(SecurityLibrary.getLoggedInUser());
						audit.setActionDate(new Date());
						audit.setDescription("Supplier '" + eventSupplier.getSupplierCompanyName() + " ' is disqualified from Envelope '" + envelop.getEnvelopTitle() + "' for Event '" + rfiEvent.getEventId() + "' ");
						audit.setAction(AuditActionType.Disqualified);
						audit.setEvent(((RfiEnvelop) envelop).getRfxEvent());
						eventAuditService.save(audit);
					} catch (Exception e) {
						LOG.error(e.getMessage(), e);
					}

					try {
						BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.DISQUALIFIED, "Supplier '" + eventSupplier.getSupplierCompanyName() + "' is disqualified in Envelope '" + envelop.getEnvelopTitle() + "' for event '" + rfiEvent.getEventId() + "' ", SecurityLibrary.getLoggedInUser().getTenantId(), SecurityLibrary.getLoggedInUser(), new Date(), ModuleType.RFI);
						buyerAuditTrailDao.save(buyerAuditTrail);
					} catch (Exception e) {
						LOG.error("Error while recording auction event Resume " + e.getMessage(), e);
					}

				} else {
					eventSupplier.setDisqualify(Boolean.FALSE);
					eventSupplier.setDisqualifiedBy(null);
					eventSupplier.setDisqualifiedTime(null);
					eventSupplier.setDisqualifyRemarks(remarks);
					eventSupplier.setReQualifiedTime(new Date());
					eventSupplier.setDisqualifiedEnvelope(null);
					if (event.getViewSupplerName() != null && !event.getViewSupplerName() && !event.getDisableMasking()) {
						eventSupplier.setSupplierCompanyName(MaskUtils.maskName(envelop.getPreFix(), eventSupplier.getSupplier().getId(), envelop.getId()));
						rAttributes.addFlashAttribute("success", eventSupplier.getSupplierCompanyName() + " Supplier Re Qualified");
					} else {
						rAttributes.addFlashAttribute("success", eventSupplier.getSupplier().getCompanyName() + " Supplier Re Qualified");
					}

					try {
						RfiEventAudit audit = new RfiEventAudit();
						audit.setActionBy(SecurityLibrary.getLoggedInUser());
						audit.setActionDate(new Date());
						audit.setDescription("Supplier '" + eventSupplier.getSupplierCompanyName() + " ' is requalified for Envelope '" + envelop.getEnvelopTitle() + " '");
						audit.setAction(AuditActionType.Requalified);
						audit.setEvent(((RfiEnvelop) envelop).getRfxEvent());
						eventAuditService.save(audit);
					} catch (Exception e) {
						LOG.error(e.getMessage(), e);
					}
					try {
						BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.REQUALIFIED, "Supplier '" + eventSupplier.getSupplierCompanyName() + "' is requalified in Envelope'" + envelop.getEnvelopTitle() + "' for Event " + rfiEvent.getEventId() + "' ", SecurityLibrary.getLoggedInUser().getTenantId(), SecurityLibrary.getLoggedInUser(), new Date(), ModuleType.RFI);
						buyerAuditTrailDao.save(buyerAuditTrail);
					} catch (Exception e) {
						LOG.error("Error while recording auction event Resume " + e.getMessage(), e);
					}
				}
				rfiEventSupplierService.updateEventSuppliers(eventSupplier);
			}
			model.addAttribute("eventPermissions", rfiEventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), eventId));
			break;
		}
		case RFP: {
			envelop = rfpEnvelopService.getEnvelopForEvaluationById(envelopId, SecurityLibrary.getLoggedInUser());
			event = ((RfpEnvelop) envelop).getRfxEvent();
			RfpEvent rfpEvent = rfpEventService.getPlainEventById(eventId);
			RfpEventSupplier eventSupplier = rfpEventSupplierService.findSupplierByIdAndEventId(supplier, eventId);
			if (eventSupplier != null) {
				if (!qualify) {
					eventSupplier.setDisqualify(Boolean.TRUE);
					eventSupplier.setDisqualifiedBy(SecurityLibrary.getLoggedInUser());
					eventSupplier.setDisqualifiedTime(new Date());
					eventSupplier.setDisqualifyRemarks(remarks);
					eventSupplier.setDisqualifiedEnvelope((RfpEnvelop) envelop);
					if (event.getViewSupplerName() != null && !event.getViewSupplerName() && !event.getDisableMasking()) {
						eventSupplier.setSupplierCompanyName(MaskUtils.maskName(envelop.getPreFix(), eventSupplier.getSupplier().getId(), envelop.getId()));
						rAttributes.addFlashAttribute("success", eventSupplier.getSupplierCompanyName() + " Supplier Disqualified");
					} else {
						rAttributes.addFlashAttribute("success", eventSupplier.getSupplier().getCompanyName() + " Supplier Disqualified");
					}
					try {
						RfpEventAudit audit = new RfpEventAudit();
						audit.setActionBy(SecurityLibrary.getLoggedInUser());
						audit.setActionDate(new Date());
						audit.setDescription("Supplier '" + eventSupplier.getSupplierCompanyName() + " ' is disqualified from Envelope '" + envelop.getEnvelopTitle() + "' for Event '" + rfpEvent.getEventId() + "' ");
						audit.setAction(AuditActionType.Disqualified);
						audit.setEvent(((RfpEnvelop) envelop).getRfxEvent());
						eventAuditService.save(audit);
					} catch (Exception e) {
						LOG.error(e.getMessage(), e);
					}
					try {
						BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.DISQUALIFIED, "Supplier '" + eventSupplier.getSupplierCompanyName() + "' is disqualified in Envelope '" + envelop.getEnvelopTitle() + "' for event '" + rfpEvent.getEventId() + "' ", SecurityLibrary.getLoggedInUser().getTenantId(), SecurityLibrary.getLoggedInUser(), new Date(), ModuleType.RFP);
						buyerAuditTrailDao.save(buyerAuditTrail);
					} catch (Exception e) {
						LOG.error("Error while recording auction event Resume " + e.getMessage(), e);
					}

				} else {
					eventSupplier.setDisqualify(Boolean.FALSE);
					eventSupplier.setDisqualifiedBy(null);
					eventSupplier.setDisqualifiedTime(null);
					eventSupplier.setDisqualifyRemarks(remarks);
					eventSupplier.setDisqualifiedEnvelope(null);
					eventSupplier.setReQualifiedTime(new Date());
					if (event.getViewSupplerName() != null && !event.getViewSupplerName() && !event.getDisableMasking()) {
						eventSupplier.setSupplierCompanyName(MaskUtils.maskName(envelop.getPreFix(), eventSupplier.getSupplier().getId(), envelop.getId()));
						rAttributes.addFlashAttribute("success", eventSupplier.getSupplierCompanyName() + " Supplier Re Qualified");
					} else {
						rAttributes.addFlashAttribute("success", eventSupplier.getSupplier().getCompanyName() + " Supplier Re Qualified");
					}

					try {
						RfpEventAudit audit = new RfpEventAudit();
						audit.setActionBy(SecurityLibrary.getLoggedInUser());
						audit.setActionDate(new Date());
						audit.setDescription("Supplier '" + eventSupplier.getSupplierCompanyName() + " ' is requalified for Envelope '" + envelop.getEnvelopTitle() + " '");
						audit.setAction(AuditActionType.Requalified);
						audit.setEvent(((RfpEnvelop) envelop).getRfxEvent());
						eventAuditService.save(audit);
					} catch (Exception e) {
						LOG.error(e.getMessage(), e);
					}

					try {
						BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.REQUALIFIED, "Supplier '" + eventSupplier.getSupplierCompanyName() + "' is requalified in Envelope '" + envelop.getEnvelopTitle() + "' for Event '" + rfpEvent.getEventId() + "' ", SecurityLibrary.getLoggedInUser().getTenantId(), SecurityLibrary.getLoggedInUser(), new Date(), ModuleType.RFP);
						buyerAuditTrailDao.save(buyerAuditTrail);
					} catch (Exception e) {
						LOG.error("Error while recording auction event Resume " + e.getMessage(), e);
					}
				}
				rfpEventSupplierService.updateEventSuppliers(eventSupplier);
			}
			model.addAttribute("eventPermissions", rfpEventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), eventId));
			break;
		}
		case RFQ: {
			envelop = rfqEnvelopService.getEnvelopForEvaluationById(envelopId, SecurityLibrary.getLoggedInUser());
			event = ((RfqEnvelop) envelop).getRfxEvent();
			RfqEvent rfqEvent = rfqEventService.getPlainEventById(eventId);
			RfqEventSupplier eventSupplier = rfqEventSupplierService.findSupplierByIdAndEventId(supplier, eventId);
			if (eventSupplier != null) {
				if (!qualify) {
					eventSupplier.setDisqualify(Boolean.TRUE);
					eventSupplier.setDisqualifiedBy(SecurityLibrary.getLoggedInUser());
					eventSupplier.setDisqualifiedTime(new Date());
					eventSupplier.setDisqualifyRemarks(remarks);
					eventSupplier.setDisqualifiedEnvelope((RfqEnvelop) envelop);
					if (event.getViewSupplerName() != null && !event.getViewSupplerName() && !event.getDisableMasking()) {
						eventSupplier.setSupplierCompanyName(MaskUtils.maskName(envelop.getPreFix(), eventSupplier.getSupplier().getId(), envelop.getId()));
						rAttributes.addFlashAttribute("success", eventSupplier.getSupplierCompanyName() + " Supplier Disqualified");
					} else {
						rAttributes.addFlashAttribute("success", eventSupplier.getSupplier().getCompanyName() + " Supplier Disqualified");
					}

					try {
						RfqEventAudit audit = new RfqEventAudit();
						audit.setActionBy(SecurityLibrary.getLoggedInUser());
						audit.setActionDate(new Date());
						audit.setDescription("Supplier '" + eventSupplier.getSupplierCompanyName() + " ' is disqualified from Envelope '" + envelop.getEnvelopTitle() + "' for Event '" + rfqEvent.getEventId() + "' ");
						audit.setAction(AuditActionType.Disqualified);
						audit.setEvent(((RfqEnvelop) envelop).getRfxEvent());
						eventAuditService.save(audit);
					} catch (Exception e) {
						LOG.error(e.getMessage(), e);
					}
					try {
						BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.DISQUALIFIED, "Supplier '" + eventSupplier.getSupplierCompanyName() + "' is disqualified in Envelope '" + envelop.getEnvelopTitle() + "' for event '" + rfqEvent.getEventId() + "' ", SecurityLibrary.getLoggedInUser().getTenantId(), SecurityLibrary.getLoggedInUser(), new Date(), ModuleType.RFQ);
						buyerAuditTrailDao.save(buyerAuditTrail);
					} catch (Exception e) {
						LOG.error("Error while recording auction event Resume " + e.getMessage(), e);
					}

				} else {
					eventSupplier.setDisqualify(Boolean.FALSE);
					eventSupplier.setDisqualifiedBy(null);
					eventSupplier.setDisqualifiedTime(null);
					eventSupplier.setDisqualifyRemarks(remarks);
					eventSupplier.setDisqualifiedEnvelope(null);
					eventSupplier.setReQualifiedTime(new Date());
					if (event.getViewSupplerName() != null && !event.getViewSupplerName() && !event.getDisableMasking()) {
						eventSupplier.setSupplierCompanyName(MaskUtils.maskName(envelop.getPreFix(), eventSupplier.getSupplier().getId(), envelop.getId()));
						rAttributes.addFlashAttribute("success", eventSupplier.getSupplierCompanyName() + " Supplier Re Qualified");
					} else {
						rAttributes.addFlashAttribute("success", eventSupplier.getSupplier().getCompanyName() + " Supplier Re Qualified");
					}
					try {
						RfqEventAudit audit = new RfqEventAudit();
						audit.setActionBy(SecurityLibrary.getLoggedInUser());
						audit.setActionDate(new Date());
						audit.setDescription("Supplier '" + eventSupplier.getSupplierCompanyName() + " ' is requalified for Envelope '" + envelop.getEnvelopTitle() + " '");
						audit.setAction(AuditActionType.Requalified);
						audit.setEvent(((RfqEnvelop) envelop).getRfxEvent());
						eventAuditService.save(audit);
					} catch (Exception e) {
						LOG.error(e.getMessage(), e);
					}
					try {
						BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.REQUALIFIED, "Supplier '" + eventSupplier.getSupplierCompanyName() + "' is requalified in Envelope'" + envelop.getEnvelopTitle() + "' for Event '" + rfqEvent.getEventId() + "' ", SecurityLibrary.getLoggedInUser().getTenantId(), SecurityLibrary.getLoggedInUser(), new Date(), ModuleType.RFQ);
						buyerAuditTrailDao.save(buyerAuditTrail);
					} catch (Exception e) {
						LOG.error("Error while recording auction event Resume " + e.getMessage(), e);
					}
				}
				rfqEventSupplierService.updateEventSuppliers(eventSupplier);
			}
			model.addAttribute("eventPermissions", rfqEventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), eventId));
			break;
		}
		case RFT: {
			envelop = rftEnvelopService.getRftEnvelopForEvaluationById(envelopId, SecurityLibrary.getLoggedInUser());
			event = ((RftEnvelop) envelop).getRfxEvent();
			RftEvent rftEvent = rftEventService.getPlainEventById(eventId);
			RftEventSupplier eventSupplier = rftEventSupplierService.findSupplierByIdAndEventId(supplier, eventId);
			if (eventSupplier != null) {
				if (!qualify) {
					eventSupplier.setDisqualify(Boolean.TRUE);
					eventSupplier.setDisqualifiedBy(SecurityLibrary.getLoggedInUser());
					eventSupplier.setDisqualifiedTime(new Date());
					eventSupplier.setDisqualifyRemarks(remarks);
					eventSupplier.setDisqualifiedEnvelope((RftEnvelop) envelop);
					if (event.getViewSupplerName() != null && !event.getViewSupplerName() && !event.getDisableMasking()) {
						eventSupplier.setSupplierCompanyName(MaskUtils.maskName(envelop.getPreFix(), eventSupplier.getSupplier().getId(), envelop.getId()));
						rAttributes.addFlashAttribute("success", eventSupplier.getSupplierCompanyName() + " Supplier Disqualified");
					} else {
						rAttributes.addFlashAttribute("success", eventSupplier.getSupplier().getCompanyName() + " Supplier Disqualified");
					}

					try {
						RftEventAudit audit = new RftEventAudit();
						audit.setActionBy(SecurityLibrary.getLoggedInUser());
						audit.setActionDate(new Date());
						audit.setDescription("Supplier '" + eventSupplier.getSupplierCompanyName() + " ' is disqualified in Envelope '" + envelop.getEnvelopTitle() + "' for Event '" + rftEvent.getEventId() + "' ");
						audit.setAction(AuditActionType.Disqualified);
						audit.setEvent(((RftEnvelop) envelop).getRfxEvent());
						eventAuditService.save(audit);
					} catch (Exception e) {
						LOG.error(e.getMessage(), e);
					}
					try {
						BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.DISQUALIFIED, "Supplier '" + eventSupplier.getSupplierCompanyName() + "' is disqualified in Envelope '" + envelop.getEnvelopTitle() + "' for event '" + rftEvent.getEventId() + "' ", SecurityLibrary.getLoggedInUser().getTenantId(), SecurityLibrary.getLoggedInUser(), new Date(), ModuleType.RFT);
						buyerAuditTrailDao.save(buyerAuditTrail);
					} catch (Exception e) {
						LOG.error("Error while recording auction event Resume " + e.getMessage(), e);
					}

				} else {
					eventSupplier.setDisqualify(Boolean.FALSE);
					eventSupplier.setDisqualifiedBy(null);
					eventSupplier.setDisqualifiedTime(null);
					eventSupplier.setDisqualifyRemarks(remarks);
					eventSupplier.setDisqualifiedEnvelope(null);
					eventSupplier.setReQualifiedTime(new Date());
					if (event.getViewSupplerName() != null && !event.getViewSupplerName() && !event.getDisableMasking()) {
						eventSupplier.setSupplierCompanyName(MaskUtils.maskName(envelop.getPreFix(), eventSupplier.getSupplier().getId(), envelop.getId()));
						rAttributes.addFlashAttribute("success", eventSupplier.getSupplierCompanyName() + " Supplier Re Qualified");
					} else {
						rAttributes.addFlashAttribute("success", eventSupplier.getSupplier().getCompanyName() + " Supplier Re Qualified");
					}
					try {
						RftEventAudit audit = new RftEventAudit();
						audit.setActionBy(SecurityLibrary.getLoggedInUser());
						audit.setActionDate(new Date());
						audit.setDescription("Supplier '" + eventSupplier.getSupplierCompanyName() + " ' is requalified for Envelope '" + envelop.getEnvelopTitle() + " '");
						audit.setAction(AuditActionType.Requalified);
						audit.setEvent(((RftEnvelop) envelop).getRfxEvent());
						eventAuditService.save(audit);
					} catch (Exception e) {
						LOG.error(e.getMessage(), e);
					}
					try {
						BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.REQUALIFIED, "Supplier '" + eventSupplier.getSupplierCompanyName() + "' is requalified in Envelope '" + envelop.getEnvelopTitle() + "' for Event '" + rftEvent.getEventId() + "' ", SecurityLibrary.getLoggedInUser().getTenantId(), SecurityLibrary.getLoggedInUser(), new Date(), ModuleType.RFT);
						buyerAuditTrailDao.save(buyerAuditTrail);
					} catch (Exception e) {
						LOG.error("Error while recording auction event Resume " + e.getMessage(), e);
					}
				}
				rftEventSupplierService.updateEventSuppliers(eventSupplier);
			}
			model.addAttribute("eventPermissions", rftEventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), eventId));
			break;
		}
		default:
			break;

		}

		return "redirect:/buyer/submissionReport/" + eventType.name() + "/" + eventId + "/" + envelopId;
	}

	@RequestMapping(path = "/finishEvaluation/{eventType}/{eventId}/{envelopId}", method = RequestMethod.POST)
	public String finishEvaluation(Model model, @PathVariable("eventType") RfxTypes eventType, @PathVariable("eventId") String eventId, @PathVariable("envelopId") String envelopId, RedirectAttributes attributes) throws JsonProcessingException {
		switch (eventType) {
		case RFA: {
			try {
				rfaEnvelopService.updateEnvelopeStatus(envelopId, SecurityLibrary.getLoggedInUser());
				attributes.addFlashAttribute("success", messageSource.getMessage("flashsuccess.completed.evaluation", new Object[] {}, Global.LOCALE));
			} catch (Exception e) {
				LOG.error("Error while finishing evaluation " + e.getMessage(), e);
				attributes.addFlashAttribute("error", messageSource.getMessage("flasherror.while.finishing.evaluation.error", new Object[] { e.getMessage() }, Global.LOCALE));
			}
			model.addAttribute("eventPermissions", rfaEventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), eventId));
			break;
		}
		case RFI: {
			try {
				rfiEnvelopService.updateEnvelopeStatus(envelopId, SecurityLibrary.getLoggedInUser());
				attributes.addFlashAttribute("success", messageSource.getMessage("flashsuccess.completed.evaluation", new Object[] {}, Global.LOCALE));
			} catch (Exception e) {
				LOG.error("Error while finishing evaluation " + e.getMessage(), e);
				attributes.addFlashAttribute("error", messageSource.getMessage("flasherror.while.finishing.evaluation.error", new Object[] { e.getMessage() }, Global.LOCALE));
			}
			model.addAttribute("eventPermissions", rfiEventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), eventId));
			break;
		}
		case RFP: {
			try {
				rfpEnvelopService.updateEnvelopeStatus(envelopId, SecurityLibrary.getLoggedInUser());
				attributes.addFlashAttribute("success", messageSource.getMessage("flashsuccess.completed.evaluation", new Object[] {}, Global.LOCALE));
			} catch (Exception e) {
				LOG.error("Error while finishing evaluation " + e.getMessage(), e);
				attributes.addFlashAttribute("error", messageSource.getMessage("flasherror.while.finishing.evaluation.error", new Object[] { e.getMessage() }, Global.LOCALE));
			}
			model.addAttribute("eventPermissions", rfpEventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), eventId));
			break;
		}
		case RFQ: {
			try {
				rfqEnvelopService.updateEnvelopeStatus(envelopId, SecurityLibrary.getLoggedInUser());
				attributes.addFlashAttribute("success", messageSource.getMessage("flashsuccess.completed.evaluation", new Object[] {}, Global.LOCALE));
			} catch (Exception e) {
				LOG.error("Error while finishing evaluation " + e.getMessage(), e);
				attributes.addFlashAttribute("error", messageSource.getMessage("flasherror.while.finishing.evaluation.error", new Object[] { e.getMessage() }, Global.LOCALE));
			}
			model.addAttribute("eventPermissions", rfqEventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), eventId));
			break;
		}
		case RFT: {
			try {
				rftEnvelopService.updateEnvelopeStatus(envelopId, SecurityLibrary.getLoggedInUser());
				attributes.addFlashAttribute("success", messageSource.getMessage("flashsuccess.completed.evaluation", new Object[] {}, Global.LOCALE));
			} catch (Exception e) {
				LOG.error("Error while finishing evaluation " + e.getMessage(), e);
				attributes.addFlashAttribute("error", messageSource.getMessage("flasherror.while.finishing.evaluation.error", new Object[] { e.getMessage() }, Global.LOCALE));
			}
			model.addAttribute("eventPermissions", rftEventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), eventId));
			break;
		}
		default:
			break;

		}
		return "redirect:/buyer/submissionReport/" + eventType.name() + "/" + eventId + "/" + envelopId;
	}

	@RequestMapping(path = "/downloadSupplierBqs/{eventType}/{envelopId}/{supplierId}", method = RequestMethod.GET)
	public void downloadSupplierBqs(@PathVariable("eventType") RfxTypes eventType, @PathVariable("envelopId") String envelopId, @PathVariable("supplierId") String supplierId, HttpServletResponse response) throws Exception {

		LOG.info("EventType : " + eventType + " supplier Id : " + supplierId + " evenvelop Id : " + envelopId);
		// Virtualizar - To increase the performance
		JRSwapFileVirtualizer virtualizer = null;
		virtualizer = new JRSwapFileVirtualizer(200, new JRSwapFile(System.getProperty("java.io.tmpdir"), 2048, 1024), false);
		String filename = eventType + " SupplierBQs.pdf";
		JasperPrint jasperPrint = null;
		try {
			switch (eventType) {
			case RFA:
				try {
					jasperPrint = rfaEnvelopService.generateSupplierBqPdfForEnvelope(envelopId, supplierId, 0, virtualizer);
				} catch (Exception e) {
					LOG.error("Could not generate Supplier CQ Report. " + e.getMessage(), e);
				}
				break;
			case RFI:
				break;
			case RFP:
				try {
					jasperPrint = rfpEnvelopService.generateSupplierBqPdfForEnvelope(envelopId, supplierId, 0, virtualizer);
				} catch (Exception e) {
					LOG.error("Could not generate Supplier CQ Report. " + e.getMessage(), e);
				}
				break;
			case RFQ:
				try {
					jasperPrint = rfqEnvelopService.generateSupplierBqPdfForEnvelope(envelopId, supplierId, 0, virtualizer);
				} catch (Exception e) {
					LOG.error("Could not generate Supplier CQ Report. " + e.getMessage(), e);
				}
				break;
			case RFT:
				try {
					jasperPrint = rftEnvelopService.generateSupplierBqPdfForEnvelope(envelopId, supplierId, 0, virtualizer);
				} catch (Exception e) {
					LOG.error("Could not generate Supplier CQ Report. " + e.getMessage(), e);
				}
				break;
			default:
				break;
			}
			if (jasperPrint != null) {
				streamReport(jasperPrint, filename, response);
			}
		} catch (JRException e) {
		} catch (IOException e) {
		} finally {
			if (virtualizer != null) {
				virtualizer.cleanup();
			}
		}
	}

	@RequestMapping(path = "/downloadSupplierCqs/{eventType}/{envelopId}/{supplierId}", method = RequestMethod.GET)
	public void downloadSupplierCqs(@PathVariable("eventType") RfxTypes eventType, @PathVariable("envelopId") String envelopId, @PathVariable("supplierId") String supplierId, HttpServletResponse response) throws Exception {
		LOG.info("EventType : " + eventType + " supplier Id : " + supplierId + " evenvelop Id : " + envelopId);
		String filename = eventType + " SupplierCQs.pdf";
		JasperPrint jasperPrint = null;
		switch (eventType) {
		case RFA:
			try {
				jasperPrint = rfaEnvelopService.generateSupplierCqPdfForEnvelope(envelopId, supplierId, 0);
			} catch (Exception e) {
				LOG.error("Could not generate Supplier BQ Report. " + e.getMessage(), e);
			}
			break;
		case RFI:
			try {
				jasperPrint = rfiEnvelopService.generateSupplierCqPdfForEnvelope(envelopId, supplierId, 0);
			} catch (Exception e) {
				LOG.error("Could not generate Supplier BQ Report. " + e.getMessage(), e);
			}
			break;
		case RFP:
			try {
				jasperPrint = rfpEnvelopService.generateSupplierCqPdfForEnvelope(envelopId, supplierId, 0);
			} catch (Exception e) {
				LOG.error("Could not generate Supplier BQ Report. " + e.getMessage(), e);
			}
			break;
		case RFQ:
			try {
				jasperPrint = rfqEnvelopService.generateSupplierCqPdfForEnvelope(envelopId, supplierId, 0);
			} catch (Exception e) {
				LOG.error("Could not generate Supplier BQ Report. " + e.getMessage(), e);
			}
			break;
		case RFT:
			try {
				jasperPrint = rftEnvelopService.generateSupplierCqPdfForEnvelope(envelopId, supplierId, 0);
			} catch (Exception e) {
				LOG.error("Could not generate Supplier BQ Report. " + e.getMessage(), e);
			}
			break;
		default:
			break;

		}
		if (jasperPrint != null) {
			streamReport(jasperPrint, filename, response);
		}
	}

	@RequestMapping(path = "/getBqItemForSearchFilter/{eventType}/{eventId}/{evelopId}", method = RequestMethod.POST)
	public ResponseEntity<List<EventEvaluationPojo>> getBqItemForSearchFilter(@PathVariable("eventType") RfxTypes eventType, @PathVariable("eventId") String eventId, @PathVariable("evelopId") String evelopId, @RequestParam(name = "filterVal", required = false) String filterVal, @RequestParam(name = "searchVal", required = false) String searchVal, @RequestParam(name = "pageLength", required = false) Integer pageLength, @RequestParam(name = "pageNo", required = false) Integer pageNo, @RequestParam(name = "supplierList", required = false) String[] supplierList) {
		LOG.info(" getBqItemForSearchFilter eventId :" + eventId + " filterVal  :" + filterVal + " searchVal : " + searchVal + " pageNo : " + pageNo + " pageLength :" + pageLength);

		List<EventEvaluationPojo> bqList = null;
		HttpHeaders headers = new HttpHeaders();
		try {
			Integer itemLevel = null;
			Integer itemOrder = null;
			Integer start = null;
			Integer length = null;
			if (StringUtils.checkString(filterVal).length() == 1) {
				filterVal = "";
			}
			if (StringUtils.checkString(filterVal).length() > 0) {
				itemLevel = 0;
				itemOrder = 0;
				String[] values = filterVal.split("\\.");
				itemLevel = Integer.parseInt(values[0]);
				itemOrder = Integer.parseInt(values[1]);
			}
			start = 0;
			length = pageLength;
			if (pageNo != null) {
				start = pageNo - 1;
			}
			if (length != null) {
				start = start * length;
			}
			LOG.info(" itemOrder : " + itemOrder + " itemLevel :" + itemLevel);

			switch (eventType) {
			case RFA:
				bqList = rfaSupplierBqItemService.getBqSearchFilterEvaluationData(eventId, evelopId, SecurityLibrary.getLoggedInUser(), "0", itemLevel, itemOrder, searchVal, start, length, supplierList);
				break;
			case RFP:
				bqList = rfpSupplierBqItemService.getBqSearchFilterEvaluationData(eventId, evelopId, SecurityLibrary.getLoggedInUser(), "0", itemLevel, itemOrder, searchVal, start, length, supplierList);
				break;
			case RFQ:
				bqList = rfqSupplierBqItemService.getBqSearchFilterEvaluationData(eventId, evelopId, SecurityLibrary.getLoggedInUser(), "0", itemLevel, itemOrder, searchVal, start, length, supplierList);
				break;
			case RFT:
				bqList = rftSupplierBqItemService.getBqSearchFilterEvaluationData(eventId, evelopId, SecurityLibrary.getLoggedInUser(), "0", itemLevel, itemOrder, searchVal, start, length, supplierList);
				break;
			default:
				break;
			}

			if (pageLength != SecurityLibrary.getLoggedInUser().getBqPageLength()) {
				userService.updateUserBqPageLength(pageLength, SecurityLibrary.getLoggedInUser().getId());
				updateSecurityLibraryUser(pageLength);
			}

			return new ResponseEntity<List<EventEvaluationPojo>>(bqList, headers, HttpStatus.OK);
		} catch (Exception e) {
			LOG.error("Error during Search Reset Bill Of Quantity :" + e.getMessage(), e);
			headers.add("error", "Error during Search reset Bill Of Quantity ");
			return new ResponseEntity<List<EventEvaluationPojo>>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}


	@RequestMapping(path = "/getSorItemForSearchFilter/{eventType}/{eventId}/{evelopId}", method = RequestMethod.POST)
	public ResponseEntity<List<EventEvaluationPojo>> getSorItemForSearchFilter(@PathVariable("eventType") RfxTypes eventType, @PathVariable("eventId") String eventId, @PathVariable("evelopId") String evelopId, @RequestParam(name = "filterVal", required = false) String filterVal, @RequestParam(name = "searchVal", required = false) String searchVal, @RequestParam(name = "pageLength", required = false) Integer pageLength, @RequestParam(name = "pageNo", required = false) Integer pageNo, @RequestParam(name = "supplierList", required = false) String[] supplierList) {
		LOG.info(" getSorItemForSearchFilter eventId :" + eventId + " filterVal  :" + filterVal + " searchVal : " + searchVal + " pageNo : " + pageNo + " pageLength :" + pageLength);

		List<EventEvaluationPojo> sorList = null;
		HttpHeaders headers = new HttpHeaders();
		List<Supplier> supplierList1 = new ArrayList<>();
		try {
			Integer itemLevel = null;
			Integer itemOrder = null;
			Integer start = null;
			Integer length = null;
			if (StringUtils.checkString(filterVal).length() == 1) {
				filterVal = "";
			}
			if (StringUtils.checkString(filterVal).length() > 0) {
				itemLevel = 0;
				itemOrder = 0;
				String[] values = filterVal.split("\\.");
				itemLevel = Integer.parseInt(values[0]);
				itemOrder = Integer.parseInt(values[1]);
			}
			start = 0;
			length = pageLength;
			if (pageNo != null) {
				start = pageNo - 1;
			}
			if (length != null) {
				start = start * length;
			}
			LOG.info(" itemOrder : " + itemOrder + " itemLevel :" + itemLevel);

			if(supplierList != null) {
				for(String supplierId : supplierList) {
					Supplier supplier1 = new Supplier();
					supplier1.setId(supplierId);
					supplierList1.add(supplier1);
				}
			}

			switch (eventType) {
				case RFA:
					sorList = rfaSupplierSorItemService.getSorEvaluationData(eventId, evelopId, supplierList1, SecurityLibrary.getLoggedInUser(), null, itemLevel, itemOrder, searchVal, start, length);
					break;
				case RFP:
					sorList = rfpSupplierSorItemService.getSorEvaluationData(eventId, evelopId, supplierList1, SecurityLibrary.getLoggedInUser(), null, itemLevel, itemOrder, searchVal, start, length);
					break;
				case RFQ:
					sorList = rfqSupplierSorItemService.getSorEvaluationData(eventId, evelopId, supplierList1, SecurityLibrary.getLoggedInUser(), null, itemLevel, itemOrder, searchVal, start, length);
					break;
				case RFT:
					sorList = rftSupplierSorItemService.getSorEvaluationData(eventId, evelopId, supplierList1, SecurityLibrary.getLoggedInUser(), null, itemLevel, itemOrder, searchVal, start, length);
					break;
				case RFI:
					sorList = rfiSupplierSorItemService.getSorEvaluationData(eventId, evelopId, supplierList1, SecurityLibrary.getLoggedInUser(), null, itemLevel, itemOrder, searchVal, start, length);
					break;
				default:
					break;
			}

			if (pageLength != SecurityLibrary.getLoggedInUser().getBqPageLength()) {
				userService.updateUserBqPageLength(pageLength, SecurityLibrary.getLoggedInUser().getId());
				updateSecurityLibraryUser(pageLength);
			}

			return new ResponseEntity<List<EventEvaluationPojo>>(sorList, headers, HttpStatus.OK);
		} catch (Exception e) {
			LOG.error("Error during Search Reset Schedule Of rate :" + e.getMessage(), e);
			headers.add("error", "Error during Search reset  Schedule Of rate ");
			return new ResponseEntity<List<EventEvaluationPojo>>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * @param pageLength
	 */
	public void updateSecurityLibraryUser(Integer pageLength) {
		/*
		 * UPDATE THE SECURITY CONTEXT AS THE BQ Page Length IS NOW CHANGED.
		 */
		// gonna need this to get user from Acegi
		SecurityContext ctx = SecurityContextHolder.getContext();
		Authentication auth = ctx.getAuthentication();
		// get user obj
		AuthenticatedUser authUser = (AuthenticatedUser) auth.getPrincipal();
		// update the bq Page length on the user obj
		authUser.setBqPageLength(pageLength);
		UsernamePasswordAuthenticationToken upat = new UsernamePasswordAuthenticationToken(authUser, auth.getCredentials(), authUser.getAuthorities());
		upat.setDetails(auth.getDetails());
		ctx.setAuthentication(upat);
	}

	@RequestMapping(path = "/saveSumamryRemark/{eventType}/{eventId}/{envelopId}", method = RequestMethod.POST)
	public String saveSumamryRemark(Model model, @RequestParam("file") MultipartFile file, @PathVariable("eventType") RfxTypes eventType, @PathVariable("eventId") String eventId, @PathVariable("envelopId") String envelopId, @RequestParam("evaluatorSummary") String evaluatorSummary, RedirectAttributes attributes, HttpSession session) {
		String fileName = null;

		LOG.info("======>" + evaluatorSummary + "..lenght " + evaluatorSummary.length() + " ======>" + eventId + "=========>" + envelopId);

		if (StringUtils.checkString(evaluatorSummary).length() == 0) {
			attributes.addFlashAttribute("error", messageSource.getMessage("flasherror.no.evaluator.summary", new Object[] {}, Global.LOCALE));
			return "redirect:/buyer/submissionReport/" + eventType.name() + "/" + eventId + "/" + envelopId;
		}

		switch (eventType) {
		case RFA: {
			try {
				fileName = file.getOriginalFilename();
				byte[] bytes = file.getBytes();
				LOG.info("FILE CONTENT" + bytes);

				RfaEvaluatorUser evaluatorUser = rfaEnvelopService.findEvaluatorUser(envelopId, SecurityLibrary.getLoggedInUser().getId());
				if (evaluatorUser != null && EvaluationStatus.PENDING == evaluatorUser.getEvaluationStatus()) {
					evaluatorUser.setCredContentType(file.getContentType());
					evaluatorUser.setFileName(fileName);
					evaluatorUser.setFileData(bytes);
					evaluatorUser.setUploadDate(new Date());
					evaluatorUser.setFileSizeInKb(bytes.length > 0 ? bytes.length / 1024 : 0);
					evaluatorUser.setEvaluatorSummary(evaluatorSummary);
					evaluatorUser.setEvaluatorSummaryDate(new Date());
					rfaEnvelopService.updateEvaluatorUser(evaluatorUser);
					// attributes.addFlashAttribute("success", "Sucessfully added summary in Evaluation");
					try {
						RfaEnvelop envelop = rfaEnvelopService.getEnvelopForEvaluationById(envelopId, SecurityLibrary.getLoggedInUser());
						RfaEventAudit audit = new RfaEventAudit();
						audit.setActionDate(new Date());
						audit.setActionBy(SecurityLibrary.getLoggedInUser());
						audit.setAction(AuditActionType.Remark);
						audit.setDescription("Remark is added for envelope '" + envelop.getEnvelopTitle() + " '");
						RfaEvent event = new RfaEvent();
						event.setId(eventId);
						audit.setEvent(event);
						eventAuditService.save(audit);

						RfaEvent rfaEvent = rfaEventService.getPlainEventById(eventId);
						LOG.info("--------------------------BEFORE AUDIT---------------------------------------");
						BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.REMARK, "Remark is added for envelope '" + envelop.getEnvelopTitle() + "' of Event '" + rfaEvent.getEventId() + "' ", SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser(), new Date(), ModuleType.RFA);
						buyerAuditTrailDao.save(buyerAuditTrail);
						LOG.info("--------------------------AFTER AUDIT---------------------------------------");

					} catch (Exception e) {
						LOG.error(e.getMessage(), e);
					}

					attributes.addFlashAttribute("success", messageSource.getMessage("flashsuccess.added.summary.evaluation", new Object[] {}, Global.LOCALE));
					LOG.info("............" + evaluatorUser.getEvaluatorSummary() + "::::::::::" + evaluatorUser.getUser().getName());
				} else {
					RfaEnvelop envelop = rfaEnvelopService.getEnvelopForEvaluationById(envelopId, SecurityLibrary.getLoggedInUser());
					RfaEvent rfaEvent = rfaEventService.loadRfaEventById(eventId);
					if (envelop != null && envelop.getLeadEvaluater() != null && envelop.getLeadEvaluater().getId().equals(SecurityLibrary.getLoggedInUser().getId())) {
						envelop.setLeadEvaluatorSummary(evaluatorSummary);
						envelop.setEvaluatorSummaryDate(new Date());
						envelop.setCredContentType(file.getContentType());
						envelop.setFileName(fileName);
						envelop.setFileData(bytes);
						envelop.setUploadDate(new Date());
						envelop.setFileSizeInKb(bytes.length > 0 ? bytes.length / 1024 : 0);
						rfaEnvelopService.updateLeadEvaluatorSummary(envelop);
						attributes.addFlashAttribute("success", messageSource.getMessage("flashsuccess.added.summary.evaluation", new Object[] {}, Global.LOCALE));

						try {
							RfaEventAudit audit = new RfaEventAudit();
							audit.setActionDate(new Date());
							audit.setActionBy(SecurityLibrary.getLoggedInUser());
							audit.setAction(AuditActionType.Remark);
							audit.setDescription("Remark is added for envelope '" + envelop.getEnvelopTitle() + " '");
							RfaEvent event = new RfaEvent();
							event.setId(eventId);
							audit.setEvent(event);
							eventAuditService.save(audit);
							
							LOG.info("--------------------------BEFORE AUDIT---------------------------------------");
							BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.REMARK, "Remark is added for envelope '" + envelop.getEnvelopTitle() + "' of Event '" + rfaEvent.getEventId() + "' ", SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser(), new Date(), ModuleType.RFA);
							buyerAuditTrailDao.save(buyerAuditTrail);
							LOG.info("--------------------------AFTER AUDIT---------------------------------------");

						} catch (Exception e) {
							LOG.error(e.getMessage(), e);
						}
						
					} else {
						attributes.addFlashAttribute("error", messageSource.getMessage("flasherror.no.evaluator.found", new Object[] {}, Global.LOCALE));
					}
				}
			} catch (Exception e) {
				LOG.error("Error while adding summary in evaluation " + e.getMessage(), e);
				attributes.addFlashAttribute("error", messageSource.getMessage("flasherror.while.adding.summaryevaluation", new Object[] {}, Global.LOCALE));
			}
			break;
		}
		case RFI: {
			try {
				fileName = file.getOriginalFilename();
				byte[] bytes = file.getBytes();
				LOG.info("FILE CONTENT" + bytes);
				RfiEvaluatorUser evaluatorUser = rfiEnvelopService.findEvaluatorUser(envelopId, SecurityLibrary.getLoggedInUser().getId());
				if (evaluatorUser != null && EvaluationStatus.PENDING == evaluatorUser.getEvaluationStatus()) {
					evaluatorUser.setEvaluatorSummary(evaluatorSummary);
					evaluatorUser.setEvaluatorSummaryDate(new Date());
					evaluatorUser.setCredContentType(file.getContentType());
					evaluatorUser.setFileName(fileName);
					evaluatorUser.setFileData(bytes);
					evaluatorUser.setUploadDate(new Date());
					evaluatorUser.setFileSizeInKb(bytes.length > 0 ? bytes.length / 1024 : 0);
					rfiEnvelopService.updateEvaluatorUser(evaluatorUser);
					try {
						RfiEnvelop envelop = rfiEnvelopService.getEnvelopForEvaluationById(envelopId, SecurityLibrary.getLoggedInUser());
						RfiEventAudit audit = new RfiEventAudit();
						audit.setActionDate(new Date());
						audit.setActionBy(SecurityLibrary.getLoggedInUser());
						audit.setAction(AuditActionType.Remark);
						audit.setDescription("Remark is added for envelope '" + envelop.getEnvelopTitle() + " '");
						RfiEvent event = new RfiEvent();
						event.setId(eventId);
						audit.setEvent(event);
						eventAuditService.save(audit);
						
						RfiEvent rfiEvent = rfiEventService.getPlainEventById(eventId);
						LOG.info("--------------------------BEFORE AUDIT---------------------------------------");
						BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.REMARK, "Remark is added for envelope '" + envelop.getEnvelopTitle() + "' of Event '" + rfiEvent.getEventId() + "' ", SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser(), new Date(), ModuleType.RFI);
						buyerAuditTrailDao.save(buyerAuditTrail);
						LOG.info("--------------------------AFTER AUDIT---------------------------------------");
						
					} catch (Exception e) {
						LOG.error(e.getMessage(), e);
					}

					attributes.addFlashAttribute("success", messageSource.getMessage("flashsuccess.added.summary.evaluation", new Object[] {}, Global.LOCALE));
				} else {
					RfiEnvelop envelop = rfiEnvelopService.getEnvelopForEvaluationById(envelopId, SecurityLibrary.getLoggedInUser());
					if (envelop != null && envelop.getLeadEvaluater() != null && envelop.getLeadEvaluater().getId().equals(SecurityLibrary.getLoggedInUser().getId())) {
						envelop.setLeadEvaluatorSummary(evaluatorSummary);
						envelop.setEvaluatorSummaryDate(new Date());
						envelop.setCredContentType(file.getContentType());
						envelop.setFileName(fileName);
						envelop.setFileData(bytes);
						envelop.setUploadDate(new Date());
						envelop.setFileSizeInKb(bytes.length > 0 ? bytes.length / 1024 : 0);
						rfiEnvelopService.updateLeadEvaluatorSummary(envelop);
						attributes.addFlashAttribute("success", "Sucessfully added summary in Evaluation");

						try {
							RfiEventAudit audit = new RfiEventAudit();
							audit.setActionDate(new Date());
							audit.setActionBy(SecurityLibrary.getLoggedInUser());
							audit.setAction(AuditActionType.Remark);
							audit.setDescription("Remark is added for envelope '" + envelop.getEnvelopTitle() + " '");
							RfiEvent event = new RfiEvent();
							event.setId(eventId);
							audit.setEvent(event);
							eventAuditService.save(audit);
							
							RfiEvent rfiEvent = rfiEventService.getPlainEventById(eventId);
							LOG.info("--------------------------BEFORE AUDIT---------------------------------------");
							BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.REMARK, "Remark is added for envelope '" + envelop.getEnvelopTitle() + "' of Event '" + rfiEvent.getEventId() + "' ", SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser(), new Date(), ModuleType.RFI);
							buyerAuditTrailDao.save(buyerAuditTrail);
							LOG.info("--------------------------AFTER AUDIT---------------------------------------");
							
						} catch (Exception e) {
							LOG.error(e.getMessage(), e);
						}
					} else {
						attributes.addFlashAttribute("error", messageSource.getMessage("flasherror.no.evaluator.found", new Object[] {}, Global.LOCALE));
					}
				}
			} catch (Exception e) {
				LOG.error("Error while adding summary in evaluation " + e.getMessage(), e);
				attributes.addFlashAttribute("error", messageSource.getMessage("flasherror.while.adding.summaryevaluation", new Object[] {}, Global.LOCALE));
			}
			break;
		}
		case RFP: {
			try {
				fileName = file.getOriginalFilename();
				byte[] bytes = file.getBytes();
				LOG.info("FILE CONTENT" + bytes);
				RfpEvaluatorUser evaluatorUser = rfpEnvelopService.findEvaluatorUser(envelopId, SecurityLibrary.getLoggedInUser().getId());
				if (evaluatorUser != null && EvaluationStatus.PENDING == evaluatorUser.getEvaluationStatus()) {
					evaluatorUser.setEvaluatorSummary(evaluatorSummary);
					evaluatorUser.setEvaluatorSummaryDate(new Date());
					evaluatorUser.setCredContentType(file.getContentType());
					evaluatorUser.setFileName(fileName);
					evaluatorUser.setFileData(bytes);
					evaluatorUser.setUploadDate(new Date());
					evaluatorUser.setFileSizeInKb(bytes.length > 0 ? bytes.length / 1024 : 0);
					rfpEnvelopService.updateEvaluatorUser(evaluatorUser);
					attributes.addFlashAttribute("success", messageSource.getMessage("flashsuccess.added.summary.evaluation", new Object[] {}, Global.LOCALE));
					try {
						RfpEnvelop envelop = rfpEnvelopService.getEnvelopForEvaluationById(envelopId, SecurityLibrary.getLoggedInUser());
						RfpEventAudit audit = new RfpEventAudit();
						audit.setActionDate(new Date());
						audit.setActionBy(SecurityLibrary.getLoggedInUser());
						audit.setAction(AuditActionType.Remark);
						audit.setDescription("Remark is added for envelope '" + envelop.getEnvelopTitle() + " '");
						RfpEvent event = new RfpEvent();
						event.setId(eventId);
						audit.setEvent(event);
						eventAuditService.save(audit);
						
						RfpEvent rfpEvent = rfpEventService.getPlainEventById(eventId);
						LOG.info("--------------------------BEFORE AUDIT---------------------------------------");
						BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.REMARK, "Remark is added for envelope '" + envelop.getEnvelopTitle() + "' of Event '" + rfpEvent.getEventId() + "' ", SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser(), new Date(), ModuleType.RFI);
						buyerAuditTrailDao.save(buyerAuditTrail);
						LOG.info("--------------------------AFTER AUDIT---------------------------------------");
					} catch (Exception e) {
						LOG.error(e.getMessage(), e);
					}

				} else {
					RfpEnvelop envelop = rfpEnvelopService.getEnvelopForEvaluationById(envelopId, SecurityLibrary.getLoggedInUser());
					if (envelop != null && envelop.getLeadEvaluater() != null && envelop.getLeadEvaluater().getId().equals(SecurityLibrary.getLoggedInUser().getId())) {
						envelop.setLeadEvaluatorSummary(evaluatorSummary);
						envelop.setEvaluatorSummaryDate(new Date());
						envelop.setCredContentType(file.getContentType());
						envelop.setFileName(fileName);
						envelop.setFileData(bytes);
						envelop.setUploadDate(new Date());
						envelop.setFileSizeInKb(bytes.length > 0 ? bytes.length / 1024 : 0);
						rfpEnvelopService.updateLeadEvaluatorSummary(envelop);
						attributes.addFlashAttribute("success", messageSource.getMessage("flashsuccess.added.summary.evaluation", new Object[] {}, Global.LOCALE));

						try {
							RfpEventAudit audit = new RfpEventAudit();
							audit.setActionDate(new Date());
							audit.setActionBy(SecurityLibrary.getLoggedInUser());
							audit.setAction(AuditActionType.Remark);
							audit.setDescription("Remark is added for envelope '" + envelop.getEnvelopTitle() + " '");
							RfpEvent event = new RfpEvent();
							event.setId(eventId);
							audit.setEvent(event);
							eventAuditService.save(audit);
							
							RfpEvent rfpEvent = rfpEventService.getPlainEventById(eventId);
							LOG.info("--------------------------BEFORE AUDIT---------------------------------------");
							BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.REMARK, "Remark is added for envelope '" + envelop.getEnvelopTitle() + "' of Event '" + rfpEvent.getEventId() + "' ", SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser(), new Date(), ModuleType.RFP);
							buyerAuditTrailDao.save(buyerAuditTrail);
							LOG.info("--------------------------AFTER AUDIT---------------------------------------");
							
						} catch (Exception e) {
							LOG.error(e.getMessage(), e);
						}
						
					} else {
						attributes.addFlashAttribute("error", messageSource.getMessage("flasherror.no.evaluator.found", new Object[] {}, Global.LOCALE));
					}
				}

			} catch (Exception e) {
				LOG.error("Error while adding summary in evaluation " + e.getMessage(), e);
				attributes.addFlashAttribute("error", messageSource.getMessage("flasherror.while.adding.summaryevaluation", new Object[] {}, Global.LOCALE));
			}
			break;
		}
		case RFQ: {
			try {
				fileName = file.getOriginalFilename();
				byte[] bytes = file.getBytes();
				LOG.info("FILE CONTENT" + bytes);
				RfqEvaluatorUser evaluatorUser = rfqEnvelopService.findEvaluatorUser(envelopId, SecurityLibrary.getLoggedInUser().getId());
				if (evaluatorUser != null && EvaluationStatus.PENDING == evaluatorUser.getEvaluationStatus()) {
					LOG.info("==============================");
					evaluatorUser.setEvaluatorSummary(evaluatorSummary);
					evaluatorUser.setEvaluatorSummaryDate(new Date());
					evaluatorUser.setCredContentType(file.getContentType());
					evaluatorUser.setFileName(fileName);
					evaluatorUser.setFileData(bytes);
					evaluatorUser.setUploadDate(new Date());
					evaluatorUser.setFileSizeInKb(bytes.length > 0 ? bytes.length / 1024 : 0);
					rfqEnvelopService.updateEvaluatorUser(evaluatorUser);
					attributes.addFlashAttribute("success", messageSource.getMessage("flashsuccess.added.summary.evaluation", new Object[] {}, Global.LOCALE));

					try {
						RfqEnvelop envelop = rfqEnvelopService.getEnvelopForEvaluationById(envelopId, SecurityLibrary.getLoggedInUser());
						RfqEventAudit audit = new RfqEventAudit();
						audit.setActionDate(new Date());
						audit.setActionBy(SecurityLibrary.getLoggedInUser());
						audit.setAction(AuditActionType.Remark);
						audit.setDescription("Remark is added for envelope '" + envelop.getEnvelopTitle() + " '");
						RfqEvent event = new RfqEvent();
						event.setId(eventId);
						audit.setEvent(event);
						eventAuditService.save(audit);
						
						
						RfqEvent rfqEvent = rfqEventService.getPlainEventById(eventId);
						LOG.info("--------------------------BEFORE AUDIT---------------------------------------");
						BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.REMARK, "Remark is added for envelope '" + envelop.getEnvelopTitle() + "' of Event '" + rfqEvent.getEventId() + "' ", SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser(), new Date(), ModuleType.RFQ);
						buyerAuditTrailDao.save(buyerAuditTrail);
						LOG.info("--------------------------AFTER AUDIT---------------------------------------");
						
					} catch (Exception e) {
						LOG.error(e.getMessage(), e);
					}

				} else {
					RfqEnvelop envelop = rfqEnvelopService.getEnvelopForEvaluationById(envelopId, SecurityLibrary.getLoggedInUser());
					if (envelop != null && envelop.getLeadEvaluater() != null && envelop.getLeadEvaluater().getId().equals(SecurityLibrary.getLoggedInUser().getId())) {
						envelop.setLeadEvaluatorSummary(evaluatorSummary);
						envelop.setEvaluatorSummaryDate(new Date());
						envelop.setCredContentType(file.getContentType());
						envelop.setFileName(fileName);
						envelop.setFileData(bytes);
						envelop.setUploadDate(new Date());
						envelop.setFileSizeInKb(bytes.length > 0 ? bytes.length / 1024 : 0);
						LOG.info("==============================");
						rfqEnvelopService.updateLeadEvaluatorSummary(envelop);
						attributes.addFlashAttribute("success", messageSource.getMessage("flashsuccess.added.summary.evaluation", new Object[] {}, Global.LOCALE));
						try {
							RfqEventAudit audit = new RfqEventAudit();
							audit.setActionDate(new Date());
							audit.setActionBy(SecurityLibrary.getLoggedInUser());
							audit.setAction(AuditActionType.Remark);
							audit.setDescription("Remark is added for envelope '" + envelop.getEnvelopTitle() + " '");
							RfqEvent event = new RfqEvent();
							event.setId(eventId);
							audit.setEvent(event);
							eventAuditService.save(audit);
							
							RfqEvent rfqEvent = rfqEventService.getPlainEventById(eventId);
							LOG.info("--------------------------BEFORE AUDIT---------------------------------------");
							BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.REMARK, "Remark is added for envelope '" + envelop.getEnvelopTitle() + "' of Event '" + rfqEvent.getEventId() + "' ", SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser(), new Date(), ModuleType.RFQ);
							buyerAuditTrailDao.save(buyerAuditTrail);
							LOG.info("--------------------------AFTER AUDIT---------------------------------------");
							
						} catch (Exception e) {
							LOG.error(e.getMessage(), e);
						}
						
					} else {
						attributes.addFlashAttribute("error", messageSource.getMessage("flasherror.no.evaluator.found", new Object[] {}, Global.LOCALE));
					}
				}
				LOG.info("==============================");
			} catch (Exception e) {
				LOG.error("Error while adding summary in evaluation " + e.getMessage(), e);
				attributes.addFlashAttribute("error", messageSource.getMessage("flasherror.while.adding.summaryevaluation", new Object[] {}, Global.LOCALE));
			}
			break;
		}
		case RFT: {
			try {
				fileName = file.getOriginalFilename();
				byte[] bytes = file.getBytes();
				LOG.info("FILE CONTENT" + bytes);

				RftEvaluatorUser evaluatorUser = rftEnvelopService.findEvaluatorUser(envelopId, SecurityLibrary.getLoggedInUser().getId());
				if (evaluatorUser != null && EvaluationStatus.PENDING == evaluatorUser.getEvaluationStatus()) {
					evaluatorUser.setEvaluatorSummary(evaluatorSummary);
					evaluatorUser.setEvaluatorSummaryDate(new Date());
					evaluatorUser.setCredContentType(file.getContentType());
					evaluatorUser.setFileName(fileName);
					evaluatorUser.setFileData(bytes);
					evaluatorUser.setUploadDate(new Date());
					evaluatorUser.setFileSizeInKb(bytes.length > 0 ? bytes.length / 1024 : 0);
					rftEnvelopService.updateEvaluatorUser(evaluatorUser);
					attributes.addFlashAttribute("success", messageSource.getMessage("flashsuccess.added.summary.evaluation", new Object[] {}, Global.LOCALE));

					try {
						RftEnvelop envelop = rftEnvelopService.getRftEnvelopById(envelopId);
						RftEventAudit audit = new RftEventAudit();
						audit.setActionDate(new Date());
						audit.setActionBy(SecurityLibrary.getLoggedInUser());
						audit.setAction(AuditActionType.Remark);
						audit.setDescription("Remark is added for envelope '" + envelop.getEnvelopTitle() + " '");
						RftEvent event = new RftEvent();
						event.setId(eventId);
						audit.setEvent(event);
						eventAuditService.save(audit);
						
						RftEvent rftEvent = rftEventService.getPlainEventById(eventId);
						LOG.info("--------------------------BEFORE AUDIT---------------------------------------");
						BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.REMARK, "Remark is added for envelope '" + envelop.getEnvelopTitle() + "' of Event '" + rftEvent.getEventId() + "' ", SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser(), new Date(), ModuleType.RFT);
						buyerAuditTrailDao.save(buyerAuditTrail);
						LOG.info("--------------------------AFTER AUDIT---------------------------------------");
						
					} catch (Exception e) {
						LOG.error(e.getMessage(), e);
					}

				} else {
					RftEnvelop envelop = rftEnvelopService.getRftEnvelopById(envelopId);
					if (envelop != null && envelop.getLeadEvaluater() != null && envelop.getLeadEvaluater().getId().equals(SecurityLibrary.getLoggedInUser().getId())) {
						envelop.setLeadEvaluatorSummary(evaluatorSummary);
						envelop.setEvaluatorSummaryDate(new Date());
						envelop.setCredContentType(file.getContentType());
						envelop.setFileName(fileName);
						envelop.setFileData(bytes);
						envelop.setUploadDate(new Date());
						envelop.setFileSizeInKb(bytes.length > 0 ? bytes.length / 1024 : 0);
						rftEnvelopService.updateLeadEvaluatorSummary(envelop);
						attributes.addFlashAttribute("success", messageSource.getMessage("flashsuccess.added.summary.evaluation", new Object[] {}, Global.LOCALE));
						try {
							RftEventAudit audit = new RftEventAudit();
							audit.setActionDate(new Date());
							audit.setActionBy(SecurityLibrary.getLoggedInUser());
							audit.setAction(AuditActionType.Remark);
							audit.setDescription("Remark is added for envelope '" + envelop.getEnvelopTitle() + " '");
							RftEvent event = new RftEvent();
							event.setId(eventId);
							audit.setEvent(event);
							eventAuditService.save(audit);
							
							RftEvent rftEvent = rftEventService.getPlainEventById(eventId);
							LOG.info("--------------------------BEFORE AUDIT---------------------------------------");
							BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.REMARK, "Remark is added for envelope '" + envelop.getEnvelopTitle() + "' of Event '" + rftEvent.getEventId() + "' ", SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser(), new Date(), ModuleType.RFT);
							buyerAuditTrailDao.save(buyerAuditTrail);
							LOG.info("--------------------------AFTER AUDIT---------------------------------------");
							
						} catch (Exception e) {
							LOG.error(e.getMessage(), e);
						}

					} else {
						attributes.addFlashAttribute("error", messageSource.getMessage("flasherror.no.evaluator.found", new Object[] {}, Global.LOCALE));
					}
				}
			} catch (Exception e) {
				LOG.error("Error while adding summary in evaluation " + e.getMessage(), e);
				attributes.addFlashAttribute("error", messageSource.getMessage("flasherror.while.adding.summaryevaluation", new Object[] {}, Global.LOCALE));
			}
			break;
		}
		default:
			break;

		}
		return "redirect:/buyer/submissionReport/" + eventType.name() + "/" + eventId + "/" + envelopId;
	}

	@RequestMapping(path = "/removeSumamryRemark/{eventType}/{eventId}/{envelopId}", method = RequestMethod.GET)
	public String removeSumamryRemark(Model model, @PathVariable("eventType") RfxTypes eventType, @PathVariable("eventId") String eventId, @PathVariable("envelopId") String envelopId, RedirectAttributes attributes) {

		switch (eventType) {
		case RFA: {
			try {

				RfaEvaluatorUser evaluatorUser = rfaEnvelopService.findEvaluatorUser(envelopId, SecurityLibrary.getLoggedInUser().getId());
				if (evaluatorUser != null && EvaluationStatus.PENDING == evaluatorUser.getEvaluationStatus()) {
					LOG.error("======>" + eventId + "=========>" + envelopId);
					evaluatorUser.setEvaluatorSummary(null);
					evaluatorUser.setEvaluatorSummaryDate(null);
					evaluatorUser.setCredContentType(null);
					evaluatorUser.setFileName(null);
					evaluatorUser.setFileData(null);
					evaluatorUser.setUploadDate(null);
					evaluatorUser.setFileSizeInKb(null);
					rfaEnvelopService.updateEvaluatorUser(evaluatorUser);
					// attributes.addFlashAttribute("success", "Sucessfully deleted summary in Evaluation");
					attributes.addFlashAttribute("success", messageSource.getMessage("flashsuccess.deleted.summary.evaluation", new Object[] {}, Global.LOCALE));
				} else {
					LOG.error("======>" + eventId + "=========>" + envelopId);
					RfaEnvelop envelop = rfaEnvelopService.getEnvelopForEvaluationById(envelopId, SecurityLibrary.getLoggedInUser());
					if (envelop != null && envelop.getLeadEvaluater() != null && envelop.getLeadEvaluater().getId().equals(SecurityLibrary.getLoggedInUser().getId())) {
						envelop.setLeadEvaluatorSummary(null);
						envelop.setEvaluatorSummaryDate(null);
						envelop.setCredContentType(null);
						envelop.setFileName(null);
						envelop.setFileData(null);
						envelop.setUploadDate(null);
						envelop.setFileSizeInKb(null);
						rfaEnvelopService.updateLeadEvaluatorSummary(envelop);
						attributes.addFlashAttribute("success", messageSource.getMessage("flashsuccess.deleted.summary.evaluation", new Object[] {}, Global.LOCALE));
					} else {
						attributes.addFlashAttribute("error", messageSource.getMessage("flasherror.no.evaluator.found", new Object[] {}, Global.LOCALE));
					}
				}
				// model.addAttribute("eventPermissions", eventPermissions);

			} catch (Exception e) {
				LOG.error("Error while adding summary in evaluation " + e.getMessage(), e);
				attributes.addFlashAttribute("error", messageSource.getMessage("flasherror.while.deleting.summaryevaluation", new Object[] {}, Global.LOCALE));
			}
			break;
		}
		case RFI: {
			try {
				RfiEvaluatorUser evaluatorUser = rfiEnvelopService.findEvaluatorUser(envelopId, SecurityLibrary.getLoggedInUser().getId());
				if (evaluatorUser != null && EvaluationStatus.PENDING == evaluatorUser.getEvaluationStatus()) {
					evaluatorUser.setEvaluatorSummary(null);
					evaluatorUser.setEvaluatorSummaryDate(null);
					evaluatorUser.setCredContentType(null);
					evaluatorUser.setFileName(null);
					evaluatorUser.setFileData(null);
					evaluatorUser.setUploadDate(null);
					evaluatorUser.setFileSizeInKb(null);
					rfiEnvelopService.updateEvaluatorUser(evaluatorUser);
					attributes.addFlashAttribute("success", messageSource.getMessage("flashsuccess.deleted.summary.evaluation", new Object[] {}, Global.LOCALE));
				} else {
					RfiEnvelop envelop = rfiEnvelopService.getEnvelopForEvaluationById(envelopId, SecurityLibrary.getLoggedInUser());
					if (envelop != null && envelop.getLeadEvaluater() != null && envelop.getLeadEvaluater().getId().equals(SecurityLibrary.getLoggedInUser().getId())) {
						envelop.setLeadEvaluatorSummary(null);
						envelop.setEvaluatorSummaryDate(null);
						envelop.setCredContentType(null);
						envelop.setFileName(null);
						envelop.setFileData(null);
						envelop.setUploadDate(null);
						envelop.setFileSizeInKb(null);
						rfiEnvelopService.updateLeadEvaluatorSummary(envelop);
						attributes.addFlashAttribute("success", messageSource.getMessage("flashsuccess.deleted.summary.evaluation", new Object[] {}, Global.LOCALE));
					} else {
						attributes.addFlashAttribute("error", messageSource.getMessage("flasherror.no.evaluator.found", new Object[] {}, Global.LOCALE));
					}
				}

			} catch (Exception e) {
				LOG.error("Error while adding summary in evaluation " + e.getMessage(), e);
				attributes.addFlashAttribute("error", messageSource.getMessage("flasherror.while.deleting.summaryevaluation", new Object[] {}, Global.LOCALE));
			}
			break;
		}
		case RFP: {
			try {
				RfpEvaluatorUser evaluatorUser = rfpEnvelopService.findEvaluatorUser(envelopId, SecurityLibrary.getLoggedInUser().getId());
				if (evaluatorUser != null && EvaluationStatus.PENDING == evaluatorUser.getEvaluationStatus()) {
					evaluatorUser.setEvaluatorSummary(null);
					evaluatorUser.setEvaluatorSummaryDate(new Date());
					evaluatorUser.setCredContentType(null);
					evaluatorUser.setFileName(null);
					evaluatorUser.setFileData(null);
					evaluatorUser.setUploadDate(null);
					evaluatorUser.setFileSizeInKb(null);
					rfpEnvelopService.updateEvaluatorUser(evaluatorUser);
					attributes.addFlashAttribute("success", messageSource.getMessage("flashsuccess.deleted.summary.evaluation", new Object[] {}, Global.LOCALE));
				} else {
					RfpEnvelop envelop = rfpEnvelopService.getEnvelopForEvaluationById(envelopId, SecurityLibrary.getLoggedInUser());
					if (envelop != null && envelop.getLeadEvaluater() != null && envelop.getLeadEvaluater().getId().equals(SecurityLibrary.getLoggedInUser().getId())) {
						envelop.setLeadEvaluatorSummary(null);
						envelop.setEvaluatorSummaryDate(new Date());
						envelop.setCredContentType(null);
						envelop.setFileName(null);
						envelop.setFileData(null);
						envelop.setUploadDate(null);
						envelop.setFileSizeInKb(null);
						rfpEnvelopService.updateLeadEvaluatorSummary(envelop);
						attributes.addFlashAttribute("success", messageSource.getMessage("flashsuccess.deleted.summary.evaluation", new Object[] {}, Global.LOCALE));
					} else {
						attributes.addFlashAttribute("error", messageSource.getMessage("flasherror.no.evaluator.found", new Object[] {}, Global.LOCALE));
					}
				}

			} catch (Exception e) {
				LOG.error("Error while adding summary in evaluation " + e.getMessage(), e);
				attributes.addFlashAttribute("error", messageSource.getMessage("flasherror.while.deleting.summaryevaluation", new Object[] {}, Global.LOCALE));
			}
			break;
		}
		case RFQ: {
			try {
				RfqEvaluatorUser evaluatorUser = rfqEnvelopService.findEvaluatorUser(envelopId, SecurityLibrary.getLoggedInUser().getId());
				if (evaluatorUser != null && EvaluationStatus.PENDING == evaluatorUser.getEvaluationStatus()) {
					evaluatorUser.setEvaluatorSummary(null);
					evaluatorUser.setEvaluatorSummaryDate(new Date());
					evaluatorUser.setCredContentType(null);
					evaluatorUser.setFileName(null);
					evaluatorUser.setFileData(null);
					evaluatorUser.setUploadDate(null);
					evaluatorUser.setFileSizeInKb(null);
					rfqEnvelopService.updateEvaluatorUser(evaluatorUser);
					attributes.addFlashAttribute("success", messageSource.getMessage("flashsuccess.deleted.summary.evaluation", new Object[] {}, Global.LOCALE));
				} else {
					RfqEnvelop envelop = rfqEnvelopService.getEnvelopForEvaluationById(envelopId, SecurityLibrary.getLoggedInUser());
					if (envelop != null && envelop.getLeadEvaluater() != null && envelop.getLeadEvaluater().getId().equals(SecurityLibrary.getLoggedInUser().getId())) {
						envelop.setLeadEvaluatorSummary(null);
						envelop.setEvaluatorSummaryDate(new Date());
						envelop.setCredContentType(null);
						envelop.setFileName(null);
						envelop.setFileData(null);
						envelop.setUploadDate(null);
						envelop.setFileSizeInKb(null);
						rfqEnvelopService.updateLeadEvaluatorSummary(envelop);
						attributes.addFlashAttribute("success", messageSource.getMessage("flashsuccess.deleted.summary.evaluation", new Object[] {}, Global.LOCALE));
					} else {
						attributes.addFlashAttribute("error", messageSource.getMessage("flasherror.no.evaluator.found", new Object[] {}, Global.LOCALE));
					}
				}

			} catch (Exception e) {
				LOG.error("Error while adding summary in evaluation " + e.getMessage(), e);
				attributes.addFlashAttribute("error", messageSource.getMessage("flasherror.while.deleting.summaryevaluation", new Object[] {}, Global.LOCALE));
			}
			break;
		}
		case RFT: {
			try {
				RftEvaluatorUser evaluatorUser = rftEnvelopService.findEvaluatorUser(envelopId, SecurityLibrary.getLoggedInUser().getId());
				if (evaluatorUser != null && EvaluationStatus.PENDING == evaluatorUser.getEvaluationStatus()) {
					evaluatorUser.setEvaluatorSummary(null);
					evaluatorUser.setEvaluatorSummaryDate(null);
					evaluatorUser.setCredContentType(null);
					evaluatorUser.setFileName(null);
					evaluatorUser.setFileData(null);
					evaluatorUser.setUploadDate(null);
					evaluatorUser.setFileSizeInKb(null);
					rftEnvelopService.updateEvaluatorUser(evaluatorUser);
					attributes.addFlashAttribute("success", messageSource.getMessage("flashsuccess.deleted.summary.evaluation", new Object[] {}, Global.LOCALE));
				} else {
					RftEnvelop envelop = rftEnvelopService.getRftEnvelopForEvaluationById(envelopId, SecurityLibrary.getLoggedInUser());
					if (envelop != null && envelop.getLeadEvaluater() != null && envelop.getLeadEvaluater().getId().equals(SecurityLibrary.getLoggedInUser().getId())) {
						envelop.setLeadEvaluatorSummary(null);
						envelop.setEvaluatorSummaryDate(null);
						envelop.setCredContentType(null);
						envelop.setFileName(null);
						envelop.setFileData(null);
						envelop.setUploadDate(null);
						envelop.setFileSizeInKb(null);
						rftEnvelopService.updateLeadEvaluatorSummary(envelop);
						attributes.addFlashAttribute("success", messageSource.getMessage("flashsuccess.deleted.summary.evaluation", new Object[] {}, Global.LOCALE));
					} else {
						attributes.addFlashAttribute("error", messageSource.getMessage("flasherror.no.evaluator.found", new Object[] {}, Global.LOCALE));
					}
				}

			} catch (Exception e) {
				LOG.error("Error while adding summary in evaluation " + e.getMessage(), e);
				attributes.addFlashAttribute("error", messageSource.getMessage("flasherror.while.deleting.summaryevaluation", new Object[] {}, Global.LOCALE));
			}
			break;
		}
		default:
			break;

		}
		return "redirect:/buyer/submissionReport/" + eventType.name() + "/" + eventId + "/" + envelopId;
	}

	@RequestMapping(path = "/downloadSubmissionReport/{eventType}/{eventId}/{evenvelopId}/{envelopTitle}", method = RequestMethod.POST)
	public void downloadSubmissionReport(@PathVariable("eventType") RfxTypes eventType, @PathVariable("eventId") String eventId, @PathVariable("evenvelopId") String evenvelopId, @PathVariable("envelopTitle") String envelopTitle, HttpServletResponse response, HttpSession session) throws Exception {
		LOG.info("EventType : " + eventType + " Event Id : " + eventId + " evenvelop Id : " + evenvelopId);
		String filename = "";
		JasperPrint jasperPrint = null;
		String envTitle = "";
		JRSwapFileVirtualizer virtualizer = null;
		try {
			switch (eventType) {
			case RFA:
				try {
					virtualizer = new JRSwapFileVirtualizer(100, new JRSwapFile(System.getProperty("java.io.tmpdir"), 2048, 1024), false);
					jasperPrint = rfaEventService.generateSubmissionReport(evenvelopId, eventId, (String) session.getAttribute(Global.SESSION_TIME_ZONE_KEY), virtualizer);
					envTitle = rftEnvelopService.getEnvelipeTitleById(evenvelopId, eventType.name());
					filename = envTitle + "_RFA_SubmissionReport.pdf";
					try {
						RfaEvent rfaEvent = rfaEventService.getPlainEventById(eventId);
						BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.DOWNLOAD, "Submission Report is successfully downloaded for Envelope '" + envTitle + "' for Event '" + rfaEvent.getEventId() + "' ", SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser(), new Date(), ModuleType.RFA);
						buyerAuditTrailDao.save(buyerAuditTrail);
					} catch (Exception e) {
						LOG.error("Error while recording auction event Resume " + e.getMessage(), e);
					}
					break;
				} catch (Exception e2) {
				}
			case RFI:
				jasperPrint = rfiEventService.generateSubmissionReport(evenvelopId, eventId, (String) session.getAttribute(Global.SESSION_TIME_ZONE_KEY), virtualizer);
				envTitle = rftEnvelopService.getEnvelipeTitleById(evenvelopId, eventType.name());
				filename = envTitle + "_RFI_SubmissionReport.pdf";
				try {
					RfiEvent rfiEvent = rfiEventService.getPlainEventById(eventId);
					BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.DOWNLOAD, "Submission Report is successfully downloaded for Envelope '" + envTitle + "' for Event '" + rfiEvent.getEventId() + "' ", SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser(), new Date(), ModuleType.RFI);
					buyerAuditTrailDao.save(buyerAuditTrail);
				} catch (Exception e) {
					LOG.error("Error while recording auction event Resume " + e.getMessage(), e);
				}
				break;
			case RFP:
				jasperPrint = rfpEventService.generateSubmissionReport(evenvelopId, eventId, (String) session.getAttribute(Global.SESSION_TIME_ZONE_KEY), virtualizer);
				envTitle = rftEnvelopService.getEnvelipeTitleById(evenvelopId, eventType.name());
				filename = envTitle + "_RFP_SubmissionReport.pdf";
				try {
					RfpEvent rfpEvent = rfpEventService.getPlainEventById(eventId);
					BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.DOWNLOAD, "Submission Report is successfully downloaded for Envelope '" + envTitle + "' for Event '" + rfpEvent.getEventId() + "' ", SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser(), new Date(), ModuleType.RFP);
					buyerAuditTrailDao.save(buyerAuditTrail);
				} catch (Exception e) {
					LOG.error("Error while recording auction event Resume " + e.getMessage(), e);
				}
				break;
			case RFQ:
				try {
					virtualizer = new JRSwapFileVirtualizer(100, new JRSwapFile(System.getProperty("java.io.tmpdir"), 2048, 1024), false);
					jasperPrint = rfqEventService.generateSubmissionReport(evenvelopId, eventId, (String) session.getAttribute(Global.SESSION_TIME_ZONE_KEY), virtualizer);
					envTitle = rftEnvelopService.getEnvelipeTitleById(evenvelopId, eventType.name());
					filename = envTitle + "_RFQ_SubmissionReport.pdf";
					try {
						RfqEvent rfqEvent = rfqEventService.getPlainEventById(eventId);
						BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.DOWNLOAD, "Submission Report is successfully downloaded for Envelope '" + envTitle + "' for Event '" + rfqEvent.getEventId() + "' ", SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser(), new Date(), ModuleType.RFQ);
						buyerAuditTrailDao.save(buyerAuditTrail);
					} catch (Exception e) {
						LOG.error("Error while recording auction event Resume " + e.getMessage(), e);
					}
				} catch (Exception e1) {
				}
				break;
			case RFT:
				try {
					virtualizer = new JRSwapFileVirtualizer(100, new JRSwapFile(System.getProperty("java.io.tmpdir"), 2048, 1024), false);
					jasperPrint = rftEventService.generateSubmissionReport(evenvelopId, eventId, (String) session.getAttribute(Global.SESSION_TIME_ZONE_KEY), virtualizer);
					envTitle = rftEnvelopService.getEnvelipeTitleById(evenvelopId, eventType.name());
					filename = envTitle + "_RFT_SubmissionReport.pdf";
					try {
						RftEvent rftEvent = rftEventService.getPlainEventById(eventId);
						BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.DOWNLOAD, "Submission Report is successfully downloaded for Envelope '" + envTitle + "' for Event '" + rftEvent.getEventId() + "' ", SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser(), new Date(), ModuleType.RFT);
						buyerAuditTrailDao.save(buyerAuditTrail);
					} catch (Exception e) {
						LOG.error("Error while recording auction event Resume " + e.getMessage(), e);
					}
				} catch (Exception e) {
				}
				break;
			default:
				break;
			}

			if (jasperPrint != null) {
				streamReport(jasperPrint, filename, response);

				switch (eventType) {
				case RFA:
					try {
						RfaEventAudit audit = new RfaEventAudit();
						audit.setAction(AuditActionType.Download);
						audit.setActionBy(SecurityLibrary.getLoggedInUser());
						audit.setActionDate(new Date());
						RfaEvent rfaEvent = new RfaEvent();
						rfaEvent.setId(eventId);
						audit.setEvent(rfaEvent);
						audit.setDescription("Submission Report is downloaded for Envelope '" + envTitle + " '");
						eventAuditService.save(audit);
					} catch (Exception e) {
						LOG.error(e.getMessage(), e);
					}
					break;
				case RFI:
					try {
						RfiEventAudit audit = new RfiEventAudit();
						audit.setAction(AuditActionType.Download);
						audit.setActionBy(SecurityLibrary.getLoggedInUser());
						audit.setActionDate(new Date());
						RfiEvent rfiEvent = new RfiEvent();
						rfiEvent.setId(eventId);
						audit.setEvent(rfiEvent);
						audit.setDescription("Submission Report is downloaded for Envelope '" + envTitle + " '");
						eventAuditService.save(audit);
					} catch (Exception e) {
						LOG.error(e.getMessage(), e);
					}
					break;
				case RFP:
					try {
						RfpEventAudit audit = new RfpEventAudit();
						audit.setAction(AuditActionType.Download);
						audit.setActionBy(SecurityLibrary.getLoggedInUser());
						audit.setActionDate(new Date());
						RfpEvent rfpEvent = new RfpEvent();
						rfpEvent.setId(eventId);
						audit.setEvent(rfpEvent);
						audit.setDescription("Submission Report is downloaded for Envelope '" + envTitle + " '");
						eventAuditService.save(audit);
					} catch (Exception e) {
						LOG.error(e.getMessage(), e);
					}
					break;
				case RFQ:
					try {
						RfqEventAudit audit = new RfqEventAudit();
						audit.setAction(AuditActionType.Download);
						audit.setActionBy(SecurityLibrary.getLoggedInUser());
						audit.setActionDate(new Date());
						RfqEvent rfqEvent = new RfqEvent();
						rfqEvent.setId(eventId);
						audit.setEvent(rfqEvent);
						audit.setDescription("Submission Report is downloaded for Envelope '" + envTitle + " '");
						eventAuditService.save(audit);
					} catch (Exception e) {
						LOG.error(e.getMessage(), e);
					}
					break;
				case RFT:
					try {
						RftEventAudit audit = new RftEventAudit();
						audit.setAction(AuditActionType.Download);
						audit.setActionBy(SecurityLibrary.getLoggedInUser());
						audit.setActionDate(new Date());
						RftEvent rftEvent = new RftEvent();
						rftEvent.setId(eventId);
						audit.setEvent(rftEvent);
						audit.setDescription("Submission Report is downloaded for Envelope '" + envTitle + " '");
						eventAuditService.save(audit);
					} catch (Exception e) {
						LOG.error(e.getMessage(), e);
					}
					break;
				default:
					break;
				}
			}
		} catch (Exception e) {
		} finally {
			if (virtualizer != null) {
				virtualizer.cleanup();
			}
		}
	}

	@RequestMapping(value = "/getSupplierRemarkComment/{eventType}/{bqId}/{supplierId}/{eventId}", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<String> getSupplierRemarkComment(@PathVariable("eventType") RfxTypes eventType, @PathVariable("bqId") String bqId, @PathVariable("supplierId") String supplierId, @PathVariable("eventId") String eventId) throws JsonProcessingException {

		LOG.info("BqIdId  :" + bqId + " Event Type :: " + eventType + " BqId : " + bqId + " Supplier Id : " + supplierId + " Event Id : " + eventId);

		HttpHeaders headers = new HttpHeaders();
		String comment = null;
		// EventPermissions eventPermissions = null;
		try {

			switch (eventType) {
			case RFA:

				RfaSupplierBq bq = rfaSupplierBqService.getSupplierBqByBqAndSupplierId(bqId, supplierId);
				comment = bq != null ? bq.getRemark() : "N/A";
				break;
			case RFI:
				break;
			case RFP:
				RfpSupplierBq re = rfpSupplierBqService.getSupplierBqByBqAndSupplierId(bqId, supplierId);
				comment = re != null ? re.getRemark() : "N/A";
				break;
			case RFQ:
				RfqSupplierBq re2 = rfqSupplierBqService.getSupplierBqByBqAndSupplierId(bqId, supplierId);
				comment = re2 != null ? re2.getRemark() : "N/A";
				break;
			case RFT:
				RftSupplierBq re3 = rftSupplierBqService.getSupplierBqByBqAndSupplierId(bqId, supplierId);
				comment = re3 != null ? re3.getRemark() : "N/A";
				break;
			default:
				break;
			}

		} catch (Exception e) {
			LOG.error("Error : " + e.getMessage(), e);
			headers.add("error", "Error showing Remarks : " + e.getMessage());
			return new ResponseEntity<String>(null, headers, HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<String>(comment, headers, HttpStatus.OK);
	}

	@RequestMapping(path = "/acceptEvaluationDeclaration/{eventType}", method = RequestMethod.POST)
	public String acceptEvaluationDeclaration(@PathVariable("eventType") RfxTypes eventType, @RequestParam("envelopId") String envelopId, @RequestParam("eventId") String eventId, RedirectAttributes redir) {
		try {
			LOG.info("Accepted evluation declaration for user:" + SecurityLibrary.getLoggedInUser().getLoginId() + "for event type:" + eventType);
			switch (eventType) {
			case RFA:
				RfaEvaluatorDeclaration rfaEvaluationDeclarationObj = new RfaEvaluatorDeclaration();
				RfaEnvelop rfaEnvelop = rfaEnvelopService.getRfaEnvelopById(envelopId);
				RfaEvent rfaEvent = new RfaEvent();
				rfaEvent.setId(eventId);
				rfaEvaluationDeclarationObj.setAcceptedDate(new Date());
				if (rfaEnvelop.getLeadEvaluater() != null && rfaEnvelop.getLeadEvaluater().getId().equals(SecurityLibrary.getLoggedInUser().getId())) {
					rfaEvaluationDeclarationObj.setIsLeadEvaluator(Boolean.TRUE);
				}
				rfaEvaluationDeclarationObj.setEnvelope(rfaEnvelop);
				rfaEvaluationDeclarationObj.setEvent(rfaEvent);
				rfaEvaluationDeclarationObj.setUser(SecurityLibrary.getLoggedInUser());
				rfaEnvelopService.saveEvaluatorDeclaration(rfaEvaluationDeclarationObj);
				try {
					RfaEventAudit audit = new RfaEventAudit();
					audit.setAction(AuditActionType.Accepted);
					audit.setActionDate(rfaEvaluationDeclarationObj.getAcceptedDate());
					audit.setDescription(messageSource.getMessage("envelop.acceped.eval.declaration.audit", new Object[] { rfaEnvelop.getEnvelopTitle() }, Global.LOCALE));
					audit.setActionBy(SecurityLibrary.getLoggedInUser());
					audit.setEvent(rfaEvent);
					eventAuditService.save(audit);
				} catch (Exception e) {
					LOG.error("Error while saving audit trail:" + e.getMessage(), e);

				}
				try {
					RfaEvent event = rfaEventService.getPlainEventById(eventId);
					LOG.info("--------------------------BEFORE AUDIT---------------------------------------");
					BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.ACCEPTED, "Evaluation Declaration has been accepted for Envelope '" + rfaEnvelop.getEnvelopTitle() + "' for Event '"+event.getEventId()+"' ", SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser(), new Date(), ModuleType.RFA);
					buyerAuditTrailDao.save(buyerAuditTrail);
					LOG.info("--------------------------AFTER AUDIT---------------------------------------");
				} catch (Exception e) {
					LOG.error("Error while recording auction event Resume " + e.getMessage(), e);
				}
				break;
			case RFI:
				RfiEvaluatorDeclaration rfiEvaluationDeclarationObj = new RfiEvaluatorDeclaration();
				RfiEnvelop rfiEnvelop = rfiEnvelopService.getRfiEnvelopById(envelopId);
				RfiEvent rfiEvent = new RfiEvent();
				rfiEvent.setId(eventId);
				rfiEvaluationDeclarationObj.setAcceptedDate(new Date());
				if (rfiEnvelop.getLeadEvaluater() != null && rfiEnvelop.getLeadEvaluater().getId().equals(SecurityLibrary.getLoggedInUser().getId())) {
					rfiEvaluationDeclarationObj.setIsLeadEvaluator(Boolean.TRUE);
				}
				rfiEvaluationDeclarationObj.setEnvelope(rfiEnvelop);
				rfiEvaluationDeclarationObj.setUser(SecurityLibrary.getLoggedInUser());
				rfiEvaluationDeclarationObj.setEvent(rfiEvent);
				rfiEnvelopService.saveEvaluatorDeclaration(rfiEvaluationDeclarationObj);
				try {
					RfiEventAudit audit = new RfiEventAudit();
					audit.setAction(AuditActionType.Accepted);
					audit.setActionDate(rfiEvaluationDeclarationObj.getAcceptedDate());
					audit.setDescription(messageSource.getMessage("envelop.acceped.eval.declaration.audit", new Object[] { rfiEnvelop.getEnvelopTitle() }, Global.LOCALE));
					audit.setActionBy(SecurityLibrary.getLoggedInUser());
					audit.setEvent(rfiEvent);
					eventAuditService.save(audit);
				} catch (Exception e) {
					LOG.error("Error while saving audit trail:" + e.getMessage(), e);

				}
				try {
					RfiEvent event = rfiEventService.getPlainEventById(eventId);
					LOG.info("--------------------------BEFORE AUDIT---------------------------------------");
					BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.ACCEPTED, "Evaluation Declaration has been accepted for Envelope '" + rfiEnvelop.getEnvelopTitle() + "' for Event '"+event.getEventId()+ "' ", SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser(), new Date(), ModuleType.RFI);
					buyerAuditTrailDao.save(buyerAuditTrail);
					LOG.info("--------------------------AFTER AUDIT---------------------------------------");
				} catch (Exception e) {
					LOG.error("Error while recording auction event Resume " + e.getMessage(), e);
				}
				break;
			case RFP:
				RfpEvaluatorDeclaration rfpEvaluationDeclarationObj = new RfpEvaluatorDeclaration();
				RfpEnvelop rfpEnvelop = rfpEnvelopService.getRfpEnvelopById(envelopId);
				RfpEvent rfpEvent = new RfpEvent();
				rfpEvent.setId(eventId);
				rfpEvaluationDeclarationObj.setAcceptedDate(new Date());
				if (rfpEnvelop.getLeadEvaluater() != null && rfpEnvelop.getLeadEvaluater().getId().equals(SecurityLibrary.getLoggedInUser().getId())) {
					rfpEvaluationDeclarationObj.setIsLeadEvaluator(Boolean.TRUE);
				}
				rfpEvaluationDeclarationObj.setEnvelope(rfpEnvelop);
				rfpEvaluationDeclarationObj.setUser(SecurityLibrary.getLoggedInUser());
				rfpEvaluationDeclarationObj.setEvent(rfpEvent);
				rfpEnvelopService.saveEvaluatorDeclaration(rfpEvaluationDeclarationObj);
				try {
					RfpEventAudit audit = new RfpEventAudit();
					audit.setAction(AuditActionType.Accepted);
					audit.setActionDate(rfpEvaluationDeclarationObj.getAcceptedDate());
					audit.setDescription(messageSource.getMessage("envelop.acceped.eval.declaration.audit", new Object[] { rfpEnvelop.getEnvelopTitle() }, Global.LOCALE));
					audit.setActionBy(SecurityLibrary.getLoggedInUser());
					audit.setEvent(rfpEvent);
					eventAuditService.save(audit);
				} catch (Exception e) {
					LOG.error("Error while saving audit trail:" + e.getMessage(), e);

				}
				try {
					RfpEvent event = rfpEventService.getPlainEventById(eventId);
					LOG.info("--------------------------BEFORE AUDIT---------------------------------------");
					BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.ACCEPTED, "Evaluation Declaration has been accepted for Envelope '" + rfpEnvelop.getEnvelopTitle() + "' for Event '"+event.getEventId()+"' ", SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser(), new Date(), ModuleType.RFP);
					buyerAuditTrailDao.save(buyerAuditTrail);
					LOG.info("--------------------------AFTER AUDIT---------------------------------------");
				} catch (Exception e) {
					LOG.error("Error while recording auction event Resume " + e.getMessage(), e);
				}
				break;
			case RFQ:
				RfqEvaluatorDeclaration rfqEvaluationDeclarationObj = new RfqEvaluatorDeclaration();
				RfqEnvelop rfqEnvelop = rfqEnvelopService.getRfqEnvelopById(envelopId);
				RfqEvent rfqEvent = new RfqEvent();
				rfqEvent.setId(eventId);
				rfqEvaluationDeclarationObj.setAcceptedDate(new Date());
				if (rfqEnvelop.getLeadEvaluater() != null && rfqEnvelop.getLeadEvaluater().getId().equals(SecurityLibrary.getLoggedInUser().getId())) {
					rfqEvaluationDeclarationObj.setIsLeadEvaluator(Boolean.TRUE);
				}
				rfqEvaluationDeclarationObj.setEnvelope(rfqEnvelop);
				rfqEvaluationDeclarationObj.setUser(SecurityLibrary.getLoggedInUser());
				rfqEvaluationDeclarationObj.setEvent(rfqEvent);
				rfqEnvelopService.saveEvaluatorDeclaration(rfqEvaluationDeclarationObj);
				try {
					RfqEventAudit audit = new RfqEventAudit();
					audit.setAction(AuditActionType.Accepted);
					audit.setActionDate(rfqEvaluationDeclarationObj.getAcceptedDate());
					audit.setDescription(messageSource.getMessage("envelop.acceped.eval.declaration.audit", new Object[] { rfqEnvelop.getEnvelopTitle() }, Global.LOCALE));
					audit.setActionBy(SecurityLibrary.getLoggedInUser());
					audit.setEvent(rfqEvent);
					eventAuditService.save(audit);
				} catch (Exception e) {
					LOG.error("Error while saving audit trail:" + e.getMessage(), e);

				}
				try {
					RfqEvent event = rfqEventService.getPlainEventById(eventId);
					LOG.info("--------------------------BEFORE AUDIT---------------------------------------");
					BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.ACCEPTED, "Evaluation Declaration has been accepted for Envelope '" + rfqEnvelop.getEnvelopTitle() + "' for Event '"+event.getEventId()+ "' ", SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser(), new Date(), ModuleType.RFQ);
					buyerAuditTrailDao.save(buyerAuditTrail);
					LOG.info("--------------------------AFTER AUDIT---------------------------------------");
				} catch (Exception e) {
					LOG.error("Error while recording auction event Resume " + e.getMessage(), e);
				}
				break;
			case RFT:
				RftEvaluatorDeclaration rftEvaluationDeclarationObj = new RftEvaluatorDeclaration();
				RftEnvelop rftEnvelop = rftEnvelopService.getRftEnvelopById(envelopId);
				RftEvent rftEvent = new RftEvent();
				rftEvent.setId(eventId);
				rftEvaluationDeclarationObj.setAcceptedDate(new Date());
				if (rftEnvelop.getLeadEvaluater() != null && rftEnvelop.getLeadEvaluater().getId().equals(SecurityLibrary.getLoggedInUser().getId())) {
					rftEvaluationDeclarationObj.setIsLeadEvaluator(Boolean.TRUE);
				}
				rftEvaluationDeclarationObj.setEnvelope(rftEnvelop);
				rftEvaluationDeclarationObj.setUser(SecurityLibrary.getLoggedInUser());
				rftEvaluationDeclarationObj.setEvent(rftEvent);
				rftEnvelopService.saveEvaluatorDeclaration(rftEvaluationDeclarationObj);
				try {
					RftEventAudit audit = new RftEventAudit();
					audit.setAction(AuditActionType.Accepted);
					audit.setActionDate(rftEvaluationDeclarationObj.getAcceptedDate());
					audit.setDescription(messageSource.getMessage("envelop.acceped.eval.declaration.audit", new Object[] { rftEnvelop.getEnvelopTitle() }, Global.LOCALE));
					audit.setActionBy(SecurityLibrary.getLoggedInUser());
					audit.setEvent(rftEvent);
					eventAuditService.save(audit);
				} catch (Exception e) {
					LOG.error("Error while saving audit trail:" + e.getMessage(), e);

				}
				try {
					RftEvent event = rftEventService.getPlainEventById(eventId);
					LOG.info("--------------------------BEFORE AUDIT---------------------------------------");
					BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.ACCEPTED, "Evaluation Declaration has been accepted for Envelope '" + rftEnvelop.getEnvelopTitle() + "' for Event '"+event.getEventId()+ "' ", SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser(), new Date(), ModuleType.RFT);
					buyerAuditTrailDao.save(buyerAuditTrail);
					LOG.info("--------------------------AFTER AUDIT---------------------------------------");
				} catch (Exception e) {
					LOG.error("Error while recording auction event Resume " + e.getMessage(), e);
				}
				break;
			default:
				break;
			}
			return "redirect:/buyer/submissionReport/" + eventType.name() + "/" + eventId + "/" + envelopId;
		} catch (Exception e) {
			LOG.error("Error while accepting evaluator declaration for envelop : " + e.getMessage(), e);
			redir.addFlashAttribute("error", e.getMessage());
			return "redirect:/buyer/" + eventType.name() + "/envelopList/" + eventId;
		}
	}

	@RequestMapping(path = "/downloadEvaluationDocument/{eventType}/{id}", method = RequestMethod.GET)
	public void downloadEvaluationDocument(@PathVariable("eventType") RfxTypes eventType, @PathVariable("id") String id, HttpServletResponse response, HttpSession session) throws Exception {
		LOG.info("EventType : " + eventType + " evenvelop Id : " + id);
		try {
			switch (eventType) {
			case RFA:
				try {
					rfaEventService.downloadRfaEvaluatorDocument(id, response);
				} catch (Exception e) {
					LOG.error("Error while downloaded RFA event Document : " + e.getMessage(), e);
				}
			case RFI:
				try {
					rfiEventService.downloadRfiEvaluatorDocument(id, response);
				} catch (Exception e) {
					LOG.error("Error while downloaded RFI event Document : " + e.getMessage(), e);
				}
				break;
			case RFP:
				try {
					rfpEventService.downloadRfpEvaluatorDocument(id, response);
				} catch (Exception e) {
					LOG.error("Error while downloaded RFP event Document : " + e.getMessage(), e);
				}
				break;
			case RFQ:
				try {
					rfqEventService.downloadRfqEvaluatorDocument(id, response);
				} catch (Exception e) {
					LOG.error("Error while downloaded RFQ event Document : " + e.getMessage(), e);
				}
				break;
			case RFT:
				try {
					rftEventService.downloadRftEvaluatorDocument(id, response);
				} catch (Exception e) {
					LOG.error("Error while downloaded RFT event Document : " + e.getMessage(), e);
				}
				break;
			default:
				break;
			}
		} catch (Exception e) {
		}
	}

	@RequestMapping(path = "/downloadLeadEvaluationDocument/{eventType}/{envelopId}", method = RequestMethod.GET)
	public void downloadLeadEvaluationDocument(@PathVariable("eventType") RfxTypes eventType, @PathVariable("envelopId") String envelopId, HttpServletResponse response, HttpSession session) throws Exception {
		LOG.info("EventType : " + eventType + " evenvelop Id : " + envelopId);
		try {
			switch (eventType) {
			case RFA:
				try {
					rfaEventService.downloadRfaLeadEvaluatorDocument(envelopId, response);
				} catch (Exception e) {
					LOG.error("Error while downloaded RFA event Document : " + e.getMessage(), e);
				}
			case RFI:
				try {
					rfiEventService.downloadRfiLeadEvaluatorDocument(envelopId, response);
				} catch (Exception e) {
					LOG.error("Error while downloaded RFI event Document : " + e.getMessage(), e);
				}
				break;
			case RFP:
				try {
					rfpEventService.downloadRfpLeadEvaluatorDocument(envelopId, response);
				} catch (Exception e) {
					LOG.error("Error while downloaded RFP event Document : " + e.getMessage(), e);
				}
				break;
			case RFQ:
				try {
					rfqEventService.downloadRfqLeadEvaluatorDocument(envelopId, response);
				} catch (Exception e) {
					LOG.error("Error while downloaded RFQ event Document : " + e.getMessage(), e);
				}
				break;
			case RFT:
				try {
					rftEventService.downloadRftLeadEvaluatorDocument(envelopId, response);
				} catch (Exception e) {
					LOG.error("Error while downloaded RFT event Document : " + e.getMessage(), e);
				}
				break;
			default:
				break;
			}
		} catch (Exception e) {
		}
	}
}
