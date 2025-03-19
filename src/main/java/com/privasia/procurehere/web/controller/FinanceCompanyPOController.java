package com.privasia.procurehere.web.controller;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.privasia.procurehere.core.entity.FinancePo;
import com.privasia.procurehere.core.entity.FinancePoAudit;
import com.privasia.procurehere.core.entity.Po;
import com.privasia.procurehere.core.entity.PoItem;
import com.privasia.procurehere.core.entity.PrDocument;
import com.privasia.procurehere.core.entity.Supplier;
import com.privasia.procurehere.core.enums.FinancePoStatus;
import com.privasia.procurehere.core.enums.FinancePoType;
import com.privasia.procurehere.core.pojo.TableData;
import com.privasia.procurehere.core.pojo.TableDataInput;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.SecurityLibrary;
import com.privasia.procurehere.core.utils.StringUtils;
import com.privasia.procurehere.service.PoFinanceService;
import com.privasia.procurehere.service.PoService;
import com.privasia.procurehere.service.PrService;
import com.privasia.procurehere.service.supplier.SupplierService;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperPrint;

@Controller
@RequestMapping(value = "/finance")
public class FinanceCompanyPOController {

	private static final Logger LOG = LogManager.getLogger(Global.FINANCE_COMPANY_LOG);

	@Autowired
	SupplierService supplierService;

	@Autowired
	PrService prService;

	@Autowired
	PoFinanceService poFinanceService;

	@Resource
	MessageSource messageSource;

	@Autowired
	PoService poService;

	@RequestMapping(path = "/financePoList", method = RequestMethod.GET)
	public String poList(Model model) {
		LOG.info("------Suplier List-------------------------------");

		List<FinancePo> suppliers = supplierService.findFinanceSuppliers(SecurityLibrary.getLoggedInUser().getTenantId());
		for (FinancePo financePo : suppliers) {
			LOG.info("--------------" + financePo.getSupplier().getCompanyName());
		}
		model.addAttribute("supplierList", suppliers);

		return "financePoList";
	}

	@RequestMapping(path = "/requestedPoList", method = RequestMethod.GET)
	public String requestedPoList(Model model) {
		LOG.info("------Suplier List-------------------------------");

		List<FinancePo> suppliers = supplierService.findFinanceSuppliers(SecurityLibrary.getLoggedInUser().getTenantId());
		for (FinancePo financePo : suppliers) {
			LOG.info("--------------" + financePo.getSupplier().getCompanyName());
		}
		model.addAttribute("supplierList", suppliers);

		return "financeRequestedPoList";
	}

	@ModelAttribute("financePoStatusList")
	public List<FinancePoStatus> getFinancePoStatusList() {
		List<FinancePoStatus> financePoStatusList = Arrays.asList(FinancePoStatus.SUBMITED, FinancePoStatus.FINANCED, FinancePoStatus.BANK_REJECTED, FinancePoStatus.BANK_COLLECTED, FinancePoStatus.BANK_SETTLED, FinancePoStatus.FINANCE_SETTLED);
		return financePoStatusList;
	}

	@RequestMapping(path = "/poListData", method = RequestMethod.GET)
	public ResponseEntity<TableData<FinancePo>> poData(TableDataInput input, @RequestParam(required = false) String dateTimeRange, @RequestParam(required = false) String selectedSupplier, HttpSession session, HttpServletResponse response) {
		try {

			LOG.info("finance Id :" + SecurityLibrary.getLoggedInUserTenantId() + " user id : " + SecurityLibrary.getLoggedInUser().getId() + "-------------------" + selectedSupplier);
			Date startDate = null;
			Date endDate = null;
			if (StringUtils.checkString(dateTimeRange).length() > 0) {
				TimeZone timeZone = TimeZone.getDefault();
				String strTimeZone = (String) session.getAttribute(Global.SESSION_TIME_ZONE_KEY);
				if (strTimeZone != null) {
					timeZone = TimeZone.getTimeZone(strTimeZone);
				}
				if (StringUtils.checkString(dateTimeRange).length() > 0) {
					String dateTimeArr[] = dateTimeRange.split("-");
					DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
					formatter.setTimeZone(timeZone);
					startDate = (Date) formatter.parse(dateTimeArr[0]);
					endDate = (Date) formatter.parse(dateTimeArr[1]);
				}
			}
			List<FinancePo> poList = poFinanceService.findAllSearchFilterPoForFinance(SecurityLibrary.getLoggedInUserTenantId(), input, startDate, endDate, null, selectedSupplier, FinancePoType.SHARED);
			for (FinancePo financePo : poList) {
				LOG.info("-------------financePo " + financePo.getSupplier().getCompanyName() + "-------------grand " + financePo.getPo().getGrandTotal());
				if (financePo.getPo() != null) {
					financePo.getPo().setCurrency(null);
					financePo.getPo().setCostCenter(null);
					financePo.getPo().setSupplier(null);
					financePo.getPo().setBusinessUnit(null);
					financePo.getPo().setCreatedBy(null);
					financePo.getPo().setModifiedBy(null);
				}
				if (financePo.getSupplier() != null) {
					Supplier supplier = financePo.getSupplier().createShallowCopy();
					financePo.setSupplier(supplier);
				}
				financePo.setStatusValue(financePo.getFinancePoStatus().getValue());
			}

			TableData<FinancePo> data = new TableData<FinancePo>(poList);
			data.setDraw(input.getDraw());
			long recordFiltered = poFinanceService.findTotalSearchFilterPoForFinance(SecurityLibrary.getLoggedInUserTenantId(), input, startDate, endDate, null, selectedSupplier, FinancePoType.SHARED);
			long totalCount = poFinanceService.findTotalPoForFinance(SecurityLibrary.getLoggedInUserTenantId(), FinancePoType.SHARED);
			LOG.info(" totalCount : " + totalCount);
			data.setRecordsFiltered(recordFiltered);
			data.setRecordsTotal(recordFiltered);
			return new ResponseEntity<TableData<FinancePo>>(data, HttpStatus.OK);
		} catch (Exception e) {
			LOG.error("Error fetching Po List For Finance: " + e.getMessage(), e);
			HttpHeaders headers = new HttpHeaders();
			headers.add("error", "Error fetching Po List For Finance : " + e.getMessage());
			return new ResponseEntity<TableData<FinancePo>>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(path = "/requestedPoListData", method = RequestMethod.GET)
	public ResponseEntity<TableData<FinancePo>> requestedPoListData(TableDataInput input, @RequestParam(required = false) String dateTimeRange, @RequestParam(required = false) String selectedSupplier, HttpSession session, HttpServletResponse response) {
		try {

			LOG.info("Supplier Id :" + SecurityLibrary.getLoggedInUserTenantId() + " user id : " + SecurityLibrary.getLoggedInUser().getId() + "-------------------" + selectedSupplier);
			Date startDate = null;
			Date endDate = null;
			if (StringUtils.checkString(dateTimeRange).length() > 0) {
				TimeZone timeZone = TimeZone.getDefault();
				String strTimeZone = (String) session.getAttribute(Global.SESSION_TIME_ZONE_KEY);
				if (strTimeZone != null) {
					timeZone = TimeZone.getTimeZone(strTimeZone);
				}
				if (StringUtils.checkString(dateTimeRange).length() > 0) {
					String dateTimeArr[] = dateTimeRange.split("-");
					DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
					formatter.setTimeZone(timeZone);
					startDate = (Date) formatter.parse(dateTimeArr[0]);
					endDate = (Date) formatter.parse(dateTimeArr[1]);
				}
			}
			List<FinancePo> poList = poFinanceService.findAllSearchFilterPoForFinance(SecurityLibrary.getLoggedInUserTenantId(), input, startDate, endDate, null, selectedSupplier, FinancePoType.REQUESTED);
			for (FinancePo financePo : poList) {
				LOG.info("-------------financePo " + financePo.getSupplier().getCompanyName() + "-------------grand " + financePo.getPo().getGrandTotal());
				if (financePo.getPo() != null) {
					financePo.getPo().setCurrency(null);
					financePo.getPo().setCostCenter(null);
					financePo.getPo().setSupplier(null);
					financePo.getPo().setBusinessUnit(null);
					financePo.getPo().setCreatedBy(null);
					financePo.getPo().setModifiedBy(null);
				}
				if (financePo.getSupplier() != null) {
					Supplier supplier = financePo.getSupplier().createShallowCopy();
					financePo.setSupplier(supplier);
				}
				financePo.setStatusValue(financePo.getFinancePoStatus().getValue());
			}

			TableData<FinancePo> data = new TableData<FinancePo>(poList);
			data.setDraw(input.getDraw());
			long recordFiltered = poFinanceService.findTotalSearchFilterPoForFinance(SecurityLibrary.getLoggedInUserTenantId(), input, startDate, endDate, null, selectedSupplier, FinancePoType.REQUESTED);
			long totalCount = poFinanceService.findTotalPoForFinance(SecurityLibrary.getLoggedInUserTenantId(), FinancePoType.REQUESTED);
			LOG.info(" totalCount : " + totalCount);
			data.setRecordsFiltered(recordFiltered);
			data.setRecordsTotal(recordFiltered);
			return new ResponseEntity<TableData<FinancePo>>(data, HttpStatus.OK);
		} catch (Exception e) {
			LOG.error("Error fetching Po List For Finance: " + e.getMessage(), e);
			HttpHeaders headers = new HttpHeaders();
			headers.add("error", "Error fetching Po List For Finance : " + e.getMessage());
			return new ResponseEntity<TableData<FinancePo>>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(path = "/financePOView/{poId}", method = RequestMethod.GET)
	public String FinancePrView(@PathVariable String poId, Model model, HttpServletRequest request, RedirectAttributes redir) {
		LOG.info("Finance View GET called By pr id :" + poId);
		try {
			constructPrSummaryAttributesForFinanceView(poId, model);
		} catch (Exception e) {
			LOG.info("Error in view Po For Finance:" + e.getMessage(), e);
			model.addAttribute("error", messageSource.getMessage("error.view.po.finance", new Object[] { e.getMessage() }, Global.LOCALE));
		}
		return "financePOView";
	}

	/**
	 * @param poId
	 * @param model
	 * @param eventPermissions
	 * @return
	 */
	public Po constructPrSummaryAttributesForFinanceView(String poId, Model model) {
		LOG.info("----------------------------------------------");

		Po po = poService.findPoById(poId);

		model.addAttribute("po", po);
		List<PoItem> prItemlist = poService.findAllPoItemByPoIdForSummary(poId);
		model.addAttribute("prItemlist", prItemlist);
		model.addAttribute("listDocs", prService.findAllPlainPrDocsbyPrId(poId));
		model.addAttribute("listPoDocs", prService.findAllPlainPoDocsbyPrId(poId));

		LOG.info("----------------------------------------------prId:" + poId + "---------Supplier:" + po.getSupplier().getSupplier().getId() + "  ----TenantId()" + SecurityLibrary.getLoggedInUserTenantId());

		FinancePo financePo = poFinanceService.getPoFinanceByPrIdAndSupID(poId, po.getSupplier().getSupplier().getId(), SecurityLibrary.getLoggedInUserTenantId());
		LOG.info("---------------------fetching PO ------------------------" + financePo.getId());
		model.addAttribute("pofinance", financePo);

		LOG.info("---------------------fetching PO Audit for finance-------------------------");
		List<FinancePoAudit> financeprAuditList = poFinanceService.getAuditForFinancePo(poId);
		model.addAttribute("financeprAuditList", financeprAuditList);

		LOG.info("-------------------financePoStatus---------------------------");
		model.addAttribute("financePoStatus", FinancePoStatus.values());
		if (financePo.getFinancePoType() != null && financePo.getFinancePoType() == FinancePoType.SHARED) {
			model.addAttribute("isShared", true);

		} else {
			model.addAttribute("isShared", false);
		}

		return po;
	}

	@RequestMapping(value = "/downloadPoDocumentForFinance/{docId}", method = RequestMethod.GET)
	public void downloadPoDocumentForFinance(@PathVariable String docId, HttpServletResponse response) throws IOException {
		try {
			LOG.info("PO Download  For Finance : " + docId);
			prService.downloadPoDocument(docId, response);
		} catch (Exception e) {
			LOG.error("Error while downloading PO Document For Finance: " + e.getMessage(), e);
		}
	}

	public void buildDocumentFile(HttpServletResponse response, PrDocument docs) throws IOException {
		response.setContentType(docs.getCredContentType());
		response.setContentLength(docs.getFileData().length);
		response.setHeader("Content-Disposition", "attachment; filename=\"" + docs.getFileName() + "\"");
		FileCopyUtils.copy(docs.getFileData(), response.getOutputStream());
		response.flushBuffer();
		response.setStatus(HttpServletResponse.SC_OK);
	}

	@RequestMapping(path = "/financePoReport/{prId}", method = RequestMethod.GET)
	public void generatePrReport(@PathVariable("prId") String prId, HttpServletResponse response, HttpSession session) throws Exception {
		try {
			LOG.info("Finance PO Report : " + prId);
			String poFilename = "UnknownPO.pdf";
			Po po = poService.loadPoById(prId);

			if (po.getPoNumber() != null) {
				poFilename = (po.getPoNumber()).replace("/", "-") + ".pdf";
			}
			String filename = poFilename;

			JasperPrint jasperPrint = supplierService.getFinancePOSummaryPdf(po, session);
			if (jasperPrint != null) {
				streamReport(jasperPrint, filename, response);
			}

		} catch (Exception e) {
			LOG.error("Could not generate PR Summary Report For Finance View. " + e.getMessage(), e);
		}
	}

	private void streamReport(JasperPrint jasperPrint, String filename, HttpServletResponse response) throws JRException, IOException {
		response.setContentType("application/pdf");
		response.addHeader("Content-Disposition", "attachment; filename=" + filename);
		JasperExportManager.exportReportToPdfStream(jasperPrint, response.getOutputStream());
		response.getOutputStream().flush();
		response.getOutputStream().close();
	}

	@RequestMapping(path = "/financePo", method = RequestMethod.POST)
	public String saveOrUpdateFinancePo(@ModelAttribute FinancePo financePo, @RequestParam("poId") String poId, RedirectAttributes redir) {
		try {

			LOG.info("----------update------------ " + financePo.getId() + " PO ID " + poId);
			FinancePo persistFinancePo = poFinanceService.findById(financePo.getId());
			if (persistFinancePo != null) {
				persistFinancePo.setReferralFee(financePo.getReferralFee());
				persistFinancePo.setFinancePoStatus(financePo.getFinancePoStatus());
				persistFinancePo.setRemark(financePo.getRemark());
				persistFinancePo.setModifiedDate(new Date());
				if (FinancePoStatus.FINANCE_SETTLED == financePo.getFinancePoStatus()) {
					persistFinancePo.setPaymentDate(new Date());
				}

				persistFinancePo.setModifiedBy(SecurityLibrary.getLoggedInUser());
				redir.addFlashAttribute("success", messageSource.getMessage("flashsuccess.po.status.updated", new Object[] {}, Global.LOCALE));
				poFinanceService.updateFinancePo(persistFinancePo, SecurityLibrary.getLoggedInUser());
			} else {
				LOG.info("Finance PO details not found");
				redir.addFlashAttribute("error", "Finance PO details not found");
			}
		} catch (Exception e) {
			LOG.info("error while updating po" + e.getMessage(), e);
			redir.addFlashAttribute("error", messageSource.getMessage("flasherror.while.updating.po", new Object[] { e.getMessage() }, Global.LOCALE));
		}
		return "redirect:/finance/financePOView/" + poId;
	}

	@RequestMapping(path = "/getFinancePoReports", method = RequestMethod.POST)
	public void poFinaceReport(HttpSession session, HttpServletRequest request, HttpServletResponse response, @RequestParam String poIds) {
		LOG.info("==========financepoReports=========");
		try {

			String prArr[] = poIds.split(",");
			TableDataInput input = new TableDataInput();
			input.setStart(0);
			input.setLength(5000);
			poFinanceService.downloadFinancePoReports(prArr, response, session);
		} catch (Exception e) {
			LOG.error("Error While Filter po list :" + e.getMessage(), e);
		}
	}

}
