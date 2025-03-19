package com.privasia.procurehere.web.controller;

import java.io.IOException;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.privasia.procurehere.core.entity.FinanceCompany;
import com.privasia.procurehere.core.entity.FinanceNotificationMessage;
import com.privasia.procurehere.core.entity.FinancePo;
import com.privasia.procurehere.core.entity.Po;
import com.privasia.procurehere.core.entity.Pr;
import com.privasia.procurehere.core.entity.PrDocument;
import com.privasia.procurehere.core.entity.PrItem;
import com.privasia.procurehere.core.entity.Supplier;
import com.privasia.procurehere.core.entity.User;
import com.privasia.procurehere.core.enums.FinanceCompanyStatus;
import com.privasia.procurehere.core.enums.FinancePoStatus;
import com.privasia.procurehere.core.enums.NotificationType;
import com.privasia.procurehere.core.enums.PrStatus;
import com.privasia.procurehere.core.pojo.SearchVo;
import com.privasia.procurehere.core.pojo.TableData;
import com.privasia.procurehere.core.pojo.TableDataInput;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.SecurityLibrary;
import com.privasia.procurehere.core.utils.StringUtils;
import com.privasia.procurehere.service.DashboardNotificationService;
import com.privasia.procurehere.service.FinanceCompanyService;
import com.privasia.procurehere.service.NotificationService;
import com.privasia.procurehere.service.PoFinanceService;
import com.privasia.procurehere.service.PoService;
import com.privasia.procurehere.service.PrService;
import com.privasia.procurehere.service.SupplierSettingsService;
import com.privasia.procurehere.service.UserService;
import com.privasia.procurehere.service.supplier.SupplierService;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperPrint;

@Controller
@RequestMapping(value = "/owner")
public class FinanceCompanyController implements Serializable {

	private static final long serialVersionUID = 4111792274944673898L;

	@Value("${app.url}")
	String APP_URL;

	@Autowired
	FinanceCompanyService financeCompanyService;

	@Resource
	MessageSource messageSource;

	@Autowired
	UserService userService;

	@Autowired
	PrService prService;

	@Autowired
	PoFinanceService poFinanceService;

	@Autowired
	SupplierService supplierService;

	@Autowired
	SupplierSettingsService supplierSettingsService;

	@Autowired
	NotificationService notificationService;

	@Autowired
	DashboardNotificationService dashboardNotificationService;

	@Autowired
	PoService poService;

	private static final Logger LOG = LogManager.getLogger(Global.FINANCE_COMPANY_LOG);

	@RequestMapping(value = "/financeCompanyList", method = RequestMethod.GET)
	public String financeComapnyList(Model model) {
		List<FinanceCompany> list = financeCompanyService.searchFinanceCompany(FinanceCompanyStatus.PENDING.toString(), "Newest", null);
		if (CollectionUtil.isNotEmpty(list)) {
			LOG.info("LIST SIZE : " + list.size());
		}
		model.addAttribute("financeCompanyList", list);
		model.addAttribute("financeCompanyObj", new FinanceCompany());
		return "financeCompanyList";
	}

	@RequestMapping(value = "searchFinance", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<List<FinanceCompany>> searchFinance(@RequestBody SearchVo searchVo) {
		List<FinanceCompany> list = null;
		try {
			LOG.info("Search Finance Company by status " + FinanceCompanyStatus.valueOf(StringUtils.checkString(searchVo.getStatus()).toUpperCase()) + " Order " + searchVo.getOrder() + " Global search : " + searchVo.getGlobalSreach());
			list = financeCompanyService.searchFinanceCompany(searchVo.getStatus(), searchVo.getOrder(), searchVo.getGlobalSreach());
		} catch (Exception e) {
			LOG.error("Error while search Finance Company " + e.getMessage(), e);
			HttpHeaders headers = new HttpHeaders();
			headers.add("error", messageSource.getMessage("finance.error.save", new Object[] { e.getMessage() }, Global.LOCALE));
			return new ResponseEntity<List<FinanceCompany>>(headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		return new ResponseEntity<List<FinanceCompany>>(list, HttpStatus.OK);
	}

	@RequestMapping(value = "/financeDetails/{financeId}", method = RequestMethod.GET)
	public String showFinanceDetails(@PathVariable(name = "financeId") String financeId, Model model) throws JsonProcessingException {
		FinanceCompany financeCompany = financeCompanyService.getFinanceCompanyById(financeId);
		LOG.info("" + financeCompany.toLogString());
		User user = userService.getUserByLoginId(financeCompany.getLoginEmail());
		model.addAttribute("adminUser", user);
		model.addAttribute("financeObj", financeCompany);

		return "financeDetails";
	}

	@RequestMapping(value = "/resendFinanceActivationLink/{financeId}", method = RequestMethod.POST)
	public ResponseEntity<String> resendActivationLinkEmail(@PathVariable(name = "financeId") String financeId, Model model) {
		LOG.info("Request for re-send of Finance Activation email request received for financeId Id : " + financeId + " from User : " + SecurityLibrary.getLoggedInUserLoginId());
		try {
			FinanceCompany financeCompany = financeCompanyService.getFinanceCompanyById(financeId);

			User adminUser = userService.getUserByLoginId(financeCompany.getLoginEmail());

			if (adminUser == null) {
				LOG.warn("Admin user for financeId " + financeId + " is not present!!!");
				HttpHeaders headers = new HttpHeaders();
				headers.add("error", "Error while sending activation email : " + "Admin user for financeId " + financeId + " is not present. Contact the administrator.");
				return new ResponseEntity<String>("{\"msg\":\"All is not good\"}", headers, HttpStatus.INTERNAL_SERVER_ERROR);
			} else {
				LOG.info("Found admin account for financeId : " + financeId + " with login email : " + adminUser.getLoginId());
			}

			financeCompanyService.sentFinanaceCompanyCreationMail(financeCompany, adminUser);

			HttpHeaders headers = new HttpHeaders();
			headers.add("info", "Email sent successfully");
			return new ResponseEntity<String>("{\"msg\":\"All is good\"}", headers, HttpStatus.OK);
		} catch (Exception e) {
			LOG.error("Error while sending activation email : " + e.getMessage(), e);
			HttpHeaders headers = new HttpHeaders();
			headers.add("error", "Error while sending activation email : " + e.getMessage());
			return new ResponseEntity<String>("{\"msg\":\"All is not good\"}", headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(value = "/updateFinanceComunicationEmail/{financeid}/{emailId:.+}", method = RequestMethod.POST)
	public ResponseEntity<String> updateComunicationEmail(@PathVariable(name = "financeid") String financeid, @PathVariable(name = "emailId") String emailId) {
		LOG.info("Request for Update Finance comunication email to (" + emailId + ") received for Finance Id : " + financeid + " from User : " + SecurityLibrary.getLoggedInUserLoginId());
		try {
			FinanceCompany financeCompany = financeCompanyService.getFinanceCompanyById(financeid);
			User adminUser = userService.getUserByLoginIdNoTouch(financeCompany.getLoginEmail());
			if (adminUser == null) {
				LOG.warn("Admin user for Finance " + financeid + " is not present!!!");
				HttpHeaders headers = new HttpHeaders();
				headers.add("error", "Error while comunication email : " + "Admin user for Finance " + financeid + " is not present. Contact the administrator.");
				return new ResponseEntity<String>("{\"msg\":\"All is not good\"}", headers, HttpStatus.INTERNAL_SERVER_ERROR);
			} else {
				LOG.info("Found admin account for Finance : " + financeid + " with login email : " + adminUser.getLoginId());

				financeCompany.setCommunicationEmail(emailId);
				LOG.info("Updated emailId : " + emailId);
				financeCompany.setActionDate(new Date());
				financeCompanyService.updateFinanceCompany(financeCompany);
			}

			HttpHeaders headers = new HttpHeaders();
			headers.add("success", "Email updated successfully");
			return new ResponseEntity<String>("{\"msg\":\"All is good\"}", headers, HttpStatus.OK);
		} catch (Exception e) {
			LOG.error("Error while updated comunication email : " + e.getMessage(), e);
			HttpHeaders headers = new HttpHeaders();
			headers.add("error", "Error while updating comunication email : " + e.getMessage());
			return new ResponseEntity<String>("{\"msg\":\"All is not good\"}", headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(value = "/suspendFinance", method = RequestMethod.POST)
	public String suspendFinance(@RequestParam String financeid, @RequestParam String remarks, RedirectAttributes redir) {
		LOG.info("Request for Susspend Finance for Finance Id : " + financeid + " from User : " + SecurityLibrary.getLoggedInUserLoginId());
		try {
			FinanceCompany financeCompany = financeCompanyService.getFinanceCompanyById(financeid);
			User adminUser = userService.getPlainUserByLoginId(financeCompany.getLoginEmail());
			if (adminUser == null) {
				LOG.warn("Admin user for Finance " + financeid + " is not present!!!");
				redir.addFlashAttribute("error", messageSource.getMessage("flasherror.while.suspend.finance", new Object[] { financeid }, Global.LOCALE));
				return "redirect:financeDetails/" + financeid;
			} else {
				LOG.info("Found admin account for Finance : " + financeid + " with login email : " + adminUser.getLoginId());
				financeCompany.setStatus(FinanceCompanyStatus.SUSPENDED);
				financeCompany.setActionDate(new Date());
				financeCompany.setActionBy(SecurityLibrary.getLoggedInUser());
				financeCompany.setSuspendedRemarks(remarks);
				financeCompanyService.updateFinanceCompany(financeCompany);
				LOG.info("sending mail...................");
				sendSusspendFinanceEmailToSupplier(financeCompany);
				LOG.info("sending mail...................");
			}

			redir.addFlashAttribute("success", messageSource.getMessage("flashsuccess.finance.suspend", new Object[] {}, Global.LOCALE));
			return "redirect:financeDetails/" + financeid;
		} catch (Exception e) {
			LOG.error("Error while updated Susspend Finance  : " + e.getMessage(), e);
			redir.addFlashAttribute("success", messageSource.getMessage("flashsuccess.updated.suspend.finance", new Object[] { e.getMessage() }, Global.LOCALE));
			return "redirect:financeDetails/" + financeid;
		}
	}

	private void sendSusspendFinanceEmailToSupplier(FinanceCompany financeCompany) {
		LOG.info("finding supplier list...................");
		List<FinancePo> list = supplierService.findFinanceSuppliers(financeCompany.getId());

		for (FinancePo financePo : list) {
			LOG.info("found supplier list...................");
			if (financePo.getSupplier() != null) {

				sendEmailSupplier(financePo.getSupplier(), financeCompany);
			}
		}

	}

	private String getTimeZoneBySupplierSettings(String tenantId, String timeZone) {
		try {
			if (StringUtils.checkString(tenantId).length() > 0) {
				String time = supplierSettingsService.getSupplierTimeZoneByTenantId(tenantId);
				if (time != null) {
					timeZone = time;
				}
			}
		} catch (Exception e) {
			LOG.error("Error while fetching supplier time zone :" + e.getMessage(), e);
		}
		return timeZone;
	}

	private void sendEmailSupplier(Supplier mailTo, FinanceCompany financeCompany) {
		// TODO Auto-generated method stub

		LOG.info("Sending Suspended email to--------------------------------> (" + mailTo.getCompanyName() + ") : " + mailTo.getCommunicationEmail());
		try {
			String subject = "Finance Company Suspended By Admin";
			String url = APP_URL;
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("userName", mailTo.getCompanyName());
			map.put("financeCompany", financeCompany.getCompanyName());
			map.put("message", "Following Finance Company Suspended By Admin");

			SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm a (z)");
			String timeZone = "GMT+8:00";
			timeZone = getTimeZoneBySupplierSettings(mailTo.getId(), timeZone);
			df.setTimeZone(TimeZone.getTimeZone(timeZone));
			map.put("date", df.format(new Date()));
			map.put("loginUrl", APP_URL + "/login");
			map.put("appUrl", url);
			User user = userService.getDetailsOfLoggedinUser(mailTo.getLoginEmail());
			if (StringUtils.checkString(mailTo.getCommunicationEmail()).length() > 0 && user.getEmailNotifications()) {
				sendEmail(mailTo.getCommunicationEmail(), subject, map, Global.FINANCE_SUSPENDED_TEMPLATE);
			} else {
				LOG.warn("No communication email configured for supplier : " + mailTo.getCommunicationEmail() + "... Not going to send email notification");
			}

			sendDashboardNotificationForSupplier(mailTo, url, subject, "Finance Company Suspended By Admin", NotificationType.CREATED_MESSAGE);
		} catch (Exception e) {
			LOG.error("Error while Sending Suspended email to supplier :" + e.getMessage(), e);
		}
	}

	private void sendDashboardNotificationForSupplier(Supplier messageTo, String url, String subject, String notificationMessage, NotificationType notificationType) {
		FinanceNotificationMessage message = new FinanceNotificationMessage();
		message.setCreatedBy(null);
		message.setCreatedDate(new Date());
		message.setMessage(notificationMessage);
		message.setNotificationType(notificationType);
		message.setMessageTo(userService.getUserByLoginId(messageTo.getLoginEmail()));
		message.setSubject(subject);
		message.setTenantId(messageTo.getId());
		message.setUrl(url);
		dashboardNotificationService.saveFinanceNotification(message);
	}

	private void sendEmail(String mailTo, String subject, Map<String, Object> map, String template) {
		if (StringUtils.checkString(mailTo).length() > 0) {
			try {
				notificationService.sendEmail(mailTo, subject, map, template);
			} catch (Exception e) {
				LOG.info("ERROR while Sending mail :" + e.getMessage(), e);
			}
		} else {
			LOG.warn("No communication email configured for user... Not going to send email notification");
		}
	}

	@RequestMapping(value = "/activateFinance", method = RequestMethod.POST)
	public String activateFinance(@RequestParam String financeid, @RequestParam String remarks, RedirectAttributes redir) {
		LOG.info("Request for Activate Finance for Finance Id : " + financeid + " from User : " + SecurityLibrary.getLoggedInUserLoginId());
		try {
			FinanceCompany financeCompany = financeCompanyService.getFinanceCompanyById(financeid);
			User adminUser = userService.getPlainUserByLoginId(financeCompany.getLoginEmail());
			if (adminUser == null) {
				LOG.warn("Admin user for Finance " + financeid + " is not present!!!");
				redir.addFlashAttribute("error", messageSource.getMessage("flasherror.while.activate.finance", new Object[] { financeid }, Global.LOCALE));
				return "redirect:financeDetails/" + financeid;
			} else {
				LOG.info("Found admin account for Finance : " + financeid + " with login email : " + adminUser.getLoginId());
				financeCompany.setStatus(FinanceCompanyStatus.ACTIVE);
				financeCompany.setActionDate(new Date());
				financeCompany.setActionBy(SecurityLibrary.getLoggedInUser());
				financeCompany.setSuspendedRemarks(remarks);
				financeCompanyService.updateFinanceCompany(financeCompany);
			}

			redir.addFlashAttribute("success", messageSource.getMessage("flashsuccess.activate.finance", new Object[] {}, Global.LOCALE));
			return "redirect:financeDetails/" + financeid;
		} catch (Exception e) {
			LOG.error("Error while updated Activate Finance  : " + e.getMessage(), e);
			redir.addFlashAttribute("success", messageSource.getMessage("flashsuccess.updated.activate.finance", new Object[] { e.getMessage() }, Global.LOCALE));
			return "redirect:financeDetails/" + financeid;
		}
	}

	@RequestMapping(value = "/toggleFinanceAdminAccountStatus", method = RequestMethod.POST)
	public String toggleFinanceAdminAccountStatus(@ModelAttribute("adminUser") User adminUser, @RequestParam(name = "financeid") String financeid, Model model, RedirectAttributes redir) {
		try {
			userService.toggleFinanceAdminAccountStatus(adminUser, financeid);
			redir.addFlashAttribute("success", messageSource.getMessage("flashsuccess.account.status.change", new Object[] {}, Global.LOCALE));
		} catch (Exception e) {
			redir.addFlashAttribute("success", messageSource.getMessage("flashsuccess.change.account.status", new Object[] { e }, Global.LOCALE));
		}
		return "redirect:financeDetails/" + financeid;
	}

	/********************************* finance **************************************************/

	@RequestMapping(path = "/financePoList", method = RequestMethod.GET)
	public String poList(Model model) {
		List<FinancePo> suppliers = supplierService.findFinanceSuppliers(SecurityLibrary.getLoggedInUser().getTenantId());
		model.addAttribute("supplierList", suppliers);

		return "financePoListForAdmin";
	}

	// @RequestMapping(path = "/financepoReports", method = RequestMethod.POST)
	// public void downloadFinancePoReports(HttpSession session, HttpServletRequest request,
	// HttpServletResponse response) {
	//
	// LOG.info("==========financepoReports=========");
	//// try {
	////
	//// List<FinancePo> PO = supplierService
	//// .findFinancePO(SecurityLibrary.getLoggedInUser().getTenantId());
	//// TableDataInput input = new TableDataInput();
	//// input.setStart(0);
	//// input.setLength(5000);
	//// supplierService.downloadPoReports(suppliers, response, session);
	//// } catch (Exception e) {
	//// LOG.error("Error While Filter po list :" + e.getMessage(), e);
	//// }
	//
	// }

	@RequestMapping(path = "/poListData", method = RequestMethod.GET)
	public ResponseEntity<TableData<Pr>> poData(TableDataInput input, @RequestParam(required = false) String dateTimeRange, @RequestParam(required = false) String selectedSupplier, HttpSession session, HttpServletResponse response) {
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
			List<Pr> poList = supplierService.findAllSearchFilterPoForOwner(input, startDate, endDate, PrStatus.APPROVED, selectedSupplier);
			TableData<Pr> data = new TableData<Pr>(poList);
			data.setDraw(input.getDraw());
			long recordFiltered = supplierService.findTotalSearchFilterPoForOwner(input, startDate, endDate, PrStatus.APPROVED, selectedSupplier);
			long totalCount = supplierService.findTotalPoForOwner();
			LOG.info(" totalCount : " + totalCount);
			data.setRecordsFiltered(recordFiltered);
			data.setRecordsTotal(totalCount);
			return new ResponseEntity<TableData<Pr>>(data, HttpStatus.OK);
		} catch (Exception e) {
			LOG.error("Error fetching Po List For Finance: " + e.getMessage(), e);
			HttpHeaders headers = new HttpHeaders();
			headers.add("error", "Error fetching Po List For Finance : " + e.getMessage());
			return new ResponseEntity<TableData<Pr>>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * @param prId
	 * @param model
	 * @param eventPermissions
	 * @return
	 */
	public Pr constructPrSummaryAttributesForFinanceView(String prId, Model model) {
		LOG.info("----------------------------------------------");
		Pr pr = supplierService.getPrByIdForFinanceView(prId);
		model.addAttribute("pr", pr);
		List<PrItem> prItemlist = prService.findAllPrItemByPrIdForSummary(prId);
		model.addAttribute("prItemlist", prItemlist);
		model.addAttribute("listDocs", prService.findAllPlainPrDocsbyPrId(prId));
		model.addAttribute("listPoDocs", prService.findAllPlainPoDocsbyPrId(prId));

		LOG.info("----------------------------------------------");
		model.addAttribute("financePoStatus", FinancePoStatus.values());

		LOG.info("----------------------------------------------");

		FinancePo financePo = poFinanceService.getPoFinanceByPrIdAndSupID(prId, pr.getSupplier().getId(), SecurityLibrary.getLoggedInUserTenantId());

		model.addAttribute("pofinance", financePo);

		return pr;
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
			// Pr pr = supplierService.getPrByIdForFinanceView(prId);
			Po po = poService.findPoById(prId);
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

	@RequestMapping(path = "/financepoReports", method = RequestMethod.POST)
	public void downloadpoReports(@RequestParam("dateTimeRange") String dateTimeRange, HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		try {

			prService.downloadFinancePoReportsForAdmin(dateTimeRange, response, session);
		} catch (Exception e) {
			LOG.error("Error While Filter po list :" + e.getMessage(), e);
		}

	}
}
