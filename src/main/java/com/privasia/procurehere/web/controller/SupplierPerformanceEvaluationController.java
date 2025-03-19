/**
 * 
 */
package com.privasia.procurehere.web.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.itextpdf.html2pdf.HtmlConverter;
import com.privasia.procurehere.core.dao.BuyerAuditTrailDao;
import com.privasia.procurehere.core.entity.BusinessUnit;
import com.privasia.procurehere.core.entity.BuyerAuditTrail;
import com.privasia.procurehere.core.entity.PerformanceEvaluationApproval;
import com.privasia.procurehere.core.entity.PerformanceEvaluationApprovalUser;
import com.privasia.procurehere.core.entity.ProcurementCategories;
import com.privasia.procurehere.core.entity.Supplier;
import com.privasia.procurehere.core.entity.SupplierPerformanceAudit;
import com.privasia.procurehere.core.entity.SupplierPerformanceEvaluatorUser;
import com.privasia.procurehere.core.entity.SupplierPerformanceForm;
import com.privasia.procurehere.core.entity.SupplierPerformanceFormCriteria;
import com.privasia.procurehere.core.entity.SupplierPerformanceReminder;
import com.privasia.procurehere.core.entity.User;
import com.privasia.procurehere.core.enums.AuditTypes;
import com.privasia.procurehere.core.enums.ModuleType;
import com.privasia.procurehere.core.enums.SupperPerformanceEvaluatorStatus;
import com.privasia.procurehere.core.enums.SupplierPerformanceAuditActionType;
import com.privasia.procurehere.core.enums.SupplierPerformanceFormStatus;
import com.privasia.procurehere.core.enums.UserType;
import com.privasia.procurehere.core.exceptions.ErpIntegrationException;
import com.privasia.procurehere.core.pojo.EventPermissions;
import com.privasia.procurehere.core.pojo.SearchFilterPerformanceEvaluationPojo;
import com.privasia.procurehere.core.pojo.SupplierPerformanceEvaluationPojo;
import com.privasia.procurehere.core.pojo.TableData;
import com.privasia.procurehere.core.pojo.TableDataInput;
import com.privasia.procurehere.core.pojo.UserPojo;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.SecurityLibrary;
import com.privasia.procurehere.core.utils.StringUtils;
import com.privasia.procurehere.service.ApprovalService;
import com.privasia.procurehere.service.BusinessUnitService;
import com.privasia.procurehere.service.BuyerAuditTrailService;
import com.privasia.procurehere.service.FavoriteSupplierService;
import com.privasia.procurehere.service.NotificationService;
import com.privasia.procurehere.service.ProcurementCategoriesService;
import com.privasia.procurehere.service.ScoreRatingService;
import com.privasia.procurehere.service.SupplierPerformanceAuditService;
import com.privasia.procurehere.service.SupplierPerformanceCriteriaService;
import com.privasia.procurehere.service.SupplierPerformanceEvaluationService;
import com.privasia.procurehere.service.SupplierPerformanceEvaluatorUserService;
import com.privasia.procurehere.service.SupplierPerformanceFormCriteriaService;
import com.privasia.procurehere.service.SupplierPerformanceFormService;
import com.privasia.procurehere.service.SupplierPerformanceTemplateCriteriaService;
import com.privasia.procurehere.service.UserService;
import com.privasia.procurehere.service.impl.SnapShotAuditService;
import com.privasia.procurehere.web.editors.BusinessUnitEditor;
import com.privasia.procurehere.web.editors.PerformanceEvaluationApprovalEditor;
import com.privasia.procurehere.web.editors.ProcurementCategoriesEditor;
import com.privasia.procurehere.web.editors.SupplierEditor;

import freemarker.template.Configuration;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.fill.JRSwapFileVirtualizer;
import net.sf.jasperreports.engine.util.JRSwapFile;

/**
 * @author priyanka
 */

@Controller
@RequestMapping("/buyer")
public class SupplierPerformanceEvaluationController {

	private static final Logger LOG = LogManager.getLogger(SupplierPerformanceEvaluationController.class);

	@Value("${app.url}")
	String APP_URL;

	@Autowired
	MessageSource messageSource;

	@Autowired
	UserService userService;

	@Autowired
	SupplierPerformanceEvaluationService supplierPerformanceEvaluationService;

	@Autowired
	BuyerAuditTrailDao buyerAuditTrailDao;

	@Autowired
	ProcurementCategoriesEditor procurementCategoriesEditor;

	@Autowired
	SupplierPerformanceAuditService supplierPerformanceAuditService;

	@Autowired
	SupplierPerformanceTemplateCriteriaService supTemplateCriteriaService;

	@Autowired
	SupplierPerformanceFormService supplierPerformanceFormService;

	@Autowired
	BusinessUnitService businessUnitService;

	@Autowired
	ProcurementCategoriesService procurementCategoriesService;

	@Autowired
	FavoriteSupplierService favoriteSupplierService;

	@Autowired
	BusinessUnitEditor buEditor;

	@Autowired
	SupplierEditor supplierEditor;

	@Autowired
	SupplierPerformanceFormCriteriaService supplierPerformanceFormCriteriaService;

	@Autowired
	SnapShotAuditService snapShotAuditService;

	@Autowired
	SupplierPerformanceCriteriaService supplierPerformanceCriteriaService;

	@Autowired
	SupplierPerformanceEvaluatorUserService supplierPerformanceEvaluatorUserService;

	@Autowired
	PerformanceEvaluationApprovalEditor performanceEvaluationApprovalEditor;

	@Autowired
	ApprovalService approvalService;

	@Autowired
	ScoreRatingService scoreRatingService;

	@Autowired
	NotificationService notificationService;

	@Autowired
	JmsTemplate jmsTemplate;

	@Autowired
	BuyerAuditTrailService buyerAuditTrailService;

	@Autowired
	SimpMessagingTemplate simpMessagingTemplate;

	@Autowired
	Configuration freemarkerConfiguration;

	@InitBinder
	public void InitBinder(WebDataBinder binder, HttpSession session) {
		// binder.registerCustomEditor(User.class, userEditor);
		binder.registerCustomEditor(ProcurementCategories.class, procurementCategoriesEditor);
		binder.registerCustomEditor(BusinessUnit.class, buEditor);
		binder.registerCustomEditor(Supplier.class, supplierEditor);

		TimeZone timeZone = TimeZone.getDefault();
		String strTimeZone = (String) session.getAttribute(Global.SESSION_TIME_ZONE_KEY);
		if (strTimeZone != null) {
			timeZone = TimeZone.getTimeZone(strTimeZone);
		}
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		dateFormat.setTimeZone(timeZone);
		binder.registerCustomEditor(Date.class, "evaluationStartDate", new CustomDateEditor(dateFormat, true));
		binder.registerCustomEditor(Date.class, "evaluationEndDate", new CustomDateEditor(dateFormat, true));

		SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a");
		timeFormat.setTimeZone(timeZone);
		binder.registerCustomEditor(Date.class, "evaluationStartTime", new CustomDateEditor(timeFormat, true));
		binder.registerCustomEditor(Date.class, "evaluationEndTime", new CustomDateEditor(timeFormat, true));

		binder.registerCustomEditor(PerformanceEvaluationApprovalUser.class, performanceEvaluationApprovalEditor);

	}

	@ModelAttribute("statusList")
	public List<SupplierPerformanceFormStatus> getStatusList() {
		return Arrays.asList(SupplierPerformanceFormStatus.values());
	}

	@RequestMapping(path = "/supplierPerformanceList", method = RequestMethod.GET)
	public String supplierPerformanceTemplateList(Model model) {
		LOG.info("Controll...........  ");
		return "supplierPerformanceList";
	}

	@GetMapping("/supplierPerformanceListData") // SupplierPerformanceForm entity
	public ResponseEntity<TableData<SupplierPerformanceEvaluationPojo>> spTemplateListData(HttpSession session, TableDataInput input, @RequestParam(required = false) String dateTimeRange) {
		try {
			LOG.info("Start : " + input.getStart() + " Length : " + input.getLength() + " Sort : " + input.getSort());
			// LOG.info(">>>>>>>>>>>>> Table Data Input : " + input.toString());

			Date startDate = null;
			Date endDate = null;
			List<String> bizUnitIds =null;
			bizUnitIds = businessUnitService.getBusinessUnitIdByUserId(SecurityLibrary.getLoggedInUser().getId());
			LOG.info("RETRIEVE UNIT ID??? >>>>>>>>>>>>>>>   "+bizUnitIds);

			if (StringUtils.checkString(dateTimeRange).length() > 0) {
				TimeZone timeZone = TimeZone.getDefault();
				String strTimeZone = (String) session.getAttribute(Global.SESSION_TIME_ZONE_KEY);
				if (strTimeZone != null) {
					timeZone = TimeZone.getTimeZone(strTimeZone);
				}

				String dateTimeArr[] = dateTimeRange.split("-");
				DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
				formatter.setTimeZone(timeZone);
				startDate = (Date) formatter.parse(dateTimeArr[0]);
				endDate = (Date) formatter.parse(dateTimeArr[1]);
				LOG.info("Start date : " + startDate + " End Date : " + endDate);
			}

			TableData<SupplierPerformanceEvaluationPojo> dataold = new TableData<SupplierPerformanceEvaluationPojo>(supplierPerformanceEvaluationService.findSupplierPerformanceEvaluation(SecurityLibrary.ifAnyGranted("ROLE_ADMIN,ROLE_ADMIN_READONLY") ? null : SecurityLibrary.getLoggedInUser().getId(), input, startDate, endDate, SecurityLibrary.getLoggedInUserTenantId()));
			TableData<SupplierPerformanceEvaluationPojo> data = new TableData<SupplierPerformanceEvaluationPojo>(supplierPerformanceEvaluationService.findSupplierPerformanceEvaluationBizUnit(SecurityLibrary.ifAnyGranted("ROLE_ADMIN,ROLE_ADMIN_READONLY") ? null : SecurityLibrary.getLoggedInUser().getId(), input, startDate, endDate, SecurityLibrary.getLoggedInUserTenantId(),bizUnitIds));
			LOG.info("data size " + data.getData().size());
			data.setDraw(input.getDraw());
			//long totalFilterCountOld = supplierPerformanceEvaluationService.findTotalFilteredSPEvaluation(SecurityLibrary.ifAnyGranted("ROLE_ADMIN,ROLE_ADMIN_READONLY") ? null : SecurityLibrary.getLoggedInUser().getId(), input, startDate, endDate, SecurityLibrary.getLoggedInUserTenantId());
			long totalFilterCount = supplierPerformanceEvaluationService.findTotalFilteredSPEvaluationBizUnit(SecurityLibrary.ifAnyGranted("ROLE_ADMIN,ROLE_ADMIN_READONLY") ? null : SecurityLibrary.getLoggedInUser().getId(), input, startDate, endDate, SecurityLibrary.getLoggedInUserTenantId(),bizUnitIds);
			LOG.info("totalFilterCount " + totalFilterCount);
			data.setRecordsFiltered(totalFilterCount);
			long totalCount=0;

			if (bizUnitIds != null && !bizUnitIds.isEmpty()) {
				totalCount = supplierPerformanceEvaluationService.findTotalActiveSPEvaluationBizUnit(SecurityLibrary.ifAnyGranted("ROLE_ADMIN,ROLE_ADMIN_READONLY") ? null : SecurityLibrary.getLoggedInUser().getId(), bizUnitIds);
			}
			else{
				totalCount = supplierPerformanceEvaluationService.findTotalActiveSPEvaluation(SecurityLibrary.ifAnyGranted("ROLE_ADMIN,ROLE_ADMIN_READONLY") ? null : SecurityLibrary.getLoggedInUser().getId());
			}
			data.setRecordsTotal(totalCount);
			return new ResponseEntity<TableData<SupplierPerformanceEvaluationPojo>>(data, HttpStatus.OK);
		} catch (Exception e) {
			LOG.error("Error fetching supplier performance evaluation list : " + e.getMessage(), e);
			HttpHeaders headers = new HttpHeaders();
			headers.add("error", "Error fetching supplier performance evaluation list : " + e.getMessage());
			return new ResponseEntity<TableData<SupplierPerformanceEvaluationPojo>>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping("/editSupplierPerformanceEvaluation/{id}")
	public String editSupplierPerformanceEvaluation(@PathVariable(name = "id") String id, Model model) {
		LOG.info("Supplier Performance form id: " + id);
		SupplierPerformanceForm spForm = supplierPerformanceFormService.getSupplierPerformanceFormById(id);

		if (spForm.getEvaluationStartDate() != null) {
			spForm.setEvaluationStartTime(spForm.getEvaluationStartDate());
		}
		if (spForm.getEvaluationEndDate() != null) {
			spForm.setEvaluationEndTime(spForm.getEvaluationEndDate());
		}

		model.addAttribute("spForm", spForm);
		List<Supplier> supplierListData = favoriteSupplierService.searchBuyerSuppliers(SecurityLibrary.getLoggedInUserTenantId(), null, null);
		if (spForm.getAwardedSupplier() != null && supplierListData != null && supplierListData.stream().filter(s -> s.getId() != null && spForm.getAwardedSupplier().getId() != null && s.getId().equals(spForm.getAwardedSupplier().getId())).count() == 0) {
			supplierListData.add(0, spForm.getAwardedSupplier());
		}
		List<UserPojo> userList = userService.fetchAllUsersForTenant(SecurityLibrary.getLoggedInUserTenantId(), "", UserType.NORMAL_USER);
		model.addAttribute("Evaluators", userList);

		model.addAttribute("assignedEvaluators", spForm.getEvaluators());

		List<SupplierPerformanceReminder> reminderList = supplierPerformanceEvaluationService.getAllSpFormRemindersByFormId(id);
		model.addAttribute("reminderList", reminderList);

		// List<SupplierPerformanceFormCriteria> spfCriteriaList =
		// supplierPerformanceFormCriteriaService.getSPFormCriteriaByFormId(id);
		// model.addAttribute("spfCriteriaList", spfCriteriaList);
		model.addAttribute("favSupp", supplierListData);
		model.addAttribute("businessUnitList", businessUnitService.getPlainActiveBusinessUnitForTenant(SecurityLibrary.getLoggedInUserTenantId()));
		model.addAttribute("procurementCategoryList", procurementCategoriesService.getAllActiveProcurementCategory(SecurityLibrary.getLoggedInUserTenantId()));
		return "supplierPerformanceEvaluationDetails";
	}

	@RequestMapping(path = "/saveSupplierPerformanceForm", method = RequestMethod.POST)
	public String saveSupplierPerformanceEvaluation(@ModelAttribute SupplierPerformanceForm spForm, @RequestParam(required = false, name = "remindMeDays") String[] remindMeDays, @RequestParam(required = false, name = "reminderSent") Boolean[] reminderSent, Model model, RedirectAttributes redir, HttpSession session) {
		LOG.info("Create SupplierPerformanceForm called. Form Id : " + spForm.getId() + " Reminders : " + Arrays.toString(remindMeDays));
		try {
			TimeZone timeZone = TimeZone.getDefault();
			String strTimeZone = (String) session.getAttribute(Global.SESSION_TIME_ZONE_KEY);
			if (strTimeZone != null) {
				timeZone = TimeZone.getTimeZone(strTimeZone);
			}
			spForm = supplierPerformanceFormService.updateSupplierPerformanceForm(spForm, SecurityLibrary.getLoggedInUser(), timeZone, remindMeDays, reminderSent);
		} catch (Exception e) {
			LOG.error("Error while saving Supplier Performance Form : " + e.getMessage(), e);
			redir.addFlashAttribute("error", messageSource.getMessage("sp.form.update.error", new Object[] { e.getMessage() }, Global.LOCALE));
			return "redirect:editSupplierPerformanceEvaluation/" + spForm.getId();
		}

		return "redirect:supplierPerformanceSummary?id=" + spForm.getId();
	}

	@RequestMapping(path = "/supplierPerformanceSummary", method = RequestMethod.GET)
	public String supSummary(@RequestParam String id, Model model) {
		LOG.info("create Supplier Performance Evaluation Summary GET called sup Id :" + id);
		SupplierPerformanceForm supplierPerformanceForm = supplierPerformanceEvaluationService.getSupplierPerformanceFormByFormId(id);
		model.addAttribute("spForm", supplierPerformanceForm);
		List<SupplierPerformanceFormCriteria> spfCriteriaList = supplierPerformanceFormCriteriaService.getSPFormCriteriaByFormId(id);
		model.addAttribute("spfCriteriaList", spfCriteriaList);
		List<SupplierPerformanceAudit> supplierPerformanceAuditList = supplierPerformanceAuditService.getSupplierPerformanceAuditByFormIdForOwner(id);
		model.addAttribute("supplierPerformanceAuditList", supplierPerformanceAuditList);

		List<SupplierPerformanceReminder> reminderList = null;
		if (CollectionUtil.isNotEmpty(supplierPerformanceForm.getFormReminder())) {
			reminderList = new ArrayList<SupplierPerformanceReminder>();
			for (SupplierPerformanceReminder reminder : supplierPerformanceForm.getFormReminder()) {
				reminderList.add(reminder);
			}
		}
		model.addAttribute("reminderList", reminderList);

		return "supplierPerformanceSummary";
	}

	@RequestMapping(path = "/downloadPerformanceEvaluationSummary/{formId}", method = RequestMethod.POST)
	public void downloadPerformancEvaluationSummary(@PathVariable("formId") String formId, HttpServletResponse response, HttpSession session) throws Exception {
		JRSwapFileVirtualizer virtualizer = null;
		try {
			// Virtualizar - To increase the performance
			virtualizer = new JRSwapFileVirtualizer(200, new JRSwapFile(System.getProperty("java.io.tmpdir"), 2048, 1024), false);
			String filename = "PerformanceEvaluationSummary.pdf";
			SupplierPerformanceForm performanceForm = supplierPerformanceEvaluationService.getSupplierPerformanceFormByFormId(formId);
			if (performanceForm.getEventId() != null) {
				filename = (performanceForm.getFormId()).replace("/", "-") + " Summary.pdf";
			}
			JasperPrint jasperPrint = supplierPerformanceEvaluationService.getPerformancEvaluationSummaryPdf(performanceForm, SecurityLibrary.getLoggedInUser(), (String) session.getAttribute(Global.SESSION_TIME_ZONE_KEY), virtualizer);
			if (jasperPrint != null) {
				streamReport(jasperPrint, filename, response);
			}

		} catch (Exception e) {
			LOG.error("Could not Download Evaluation Summary Report. " + e.getMessage(), e);
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

	@PostMapping("/exportSPEvaluationCsvReport")
	public void downloadPerformanceEvaluationCSVReport(HttpServletResponse response, HttpSession session, RedirectAttributes redir, boolean select_all, @RequestParam(required = false) String formIds, @RequestParam String dateTimeRange, @ModelAttribute("searchFilterPerformanceEvaluationPojo") SearchFilterPerformanceEvaluationPojo searchFilterPerformanceEvaluationPojo) throws IOException {
		try {
			String formIdArr[] = null;
			if (StringUtils.checkString(formIds).length() > 0) {
				formIdArr = formIds.split(",");
			}

			LOG.info(formIdArr.length);
			Date startDate = null;
			Date endDate = null;

			if (StringUtils.checkString(dateTimeRange).length() > 0) {
				TimeZone timeZone = TimeZone.getDefault();
				String strTimeZone = (String) session.getAttribute(Global.SESSION_TIME_ZONE_KEY);
				if (strTimeZone != null) {
					timeZone = TimeZone.getTimeZone(strTimeZone);
				}

				String dateTimeArr[] = dateTimeRange.split("-");
				DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
				formatter.setTimeZone(timeZone);
				startDate = (Date) formatter.parse(dateTimeArr[0]);
				endDate = (Date) formatter.parse(dateTimeArr[1]);
				LOG.info("Start date : " + startDate + " End Date : " + endDate);
			}

			XSSFWorkbook workbook = supplierPerformanceEvaluationService.downloadCsvFileForPerformanceEvaluation(SecurityLibrary.ifAnyGranted("ROLE_ADMIN,ROLE_ADMIN_READONLY") ? null : SecurityLibrary.getLoggedInUser().getId(), startDate, endDate, select_all, formIdArr, searchFilterPerformanceEvaluationPojo, session, SecurityLibrary.getLoggedInUserTenantId());
			String downloadFolder = Global.FILE_PATH;
			String fileName = "PerformanceEvaluationReport.xlsx";
			FileOutputStream out = new FileOutputStream(downloadFolder + "/" + fileName);
			workbook.write(out);
			out.close();
			Path file = Paths.get(downloadFolder, fileName);

			if (Files.exists(file)) {
				response.setContentType("application/vnd.ms-excel");
				response.addHeader("Content-Disposition", "attachment; filename=" + fileName);
				try {
					Files.copy(file, response.getOutputStream());
					response.getOutputStream().flush();
				} catch (IOException e) {
					LOG.error("Error :- " + e.getMessage());
				}
			}

		} catch (Exception e) {
			LOG.error("Error for creating csv report" + e.getMessage(), e);
		}
	}

	@RequestMapping(path = "/addEvaluatorToList", method = RequestMethod.POST)
	public ResponseEntity<List<UserPojo>> addEvaluator(@RequestParam("spFormId") String spFormId, @RequestParam("userId") String userId) {
		HttpHeaders headers = new HttpHeaders();
		LOG.info("addEvaluatorToList----" + " Form Id: " + spFormId + " userId: " + userId);
		try {
			List<SupplierPerformanceEvaluatorUser> assignedEvaluators = supplierPerformanceFormService.findEvaluatorsByFormId(spFormId);
			if (CollectionUtil.isNotEmpty(assignedEvaluators)) {
				LOG.info("total assigned evaluators : " + assignedEvaluators.size());
				for (SupplierPerformanceEvaluatorUser formEvaluatorUser : assignedEvaluators) {
					if (formEvaluatorUser.getEvaluator().getId().equals(StringUtils.checkString(userId))) {
						headers.add("error", "Evaluator User already added");
						return new ResponseEntity<List<UserPojo>>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
					}
				}
			}
			if (StringUtils.checkString(spFormId).length() > 0 && StringUtils.checkString(userId).length() > 0) {
				LOG.info("spFormId : " + spFormId);
				supplierPerformanceFormService.addEvaluator(spFormId, userId);
			} else {
				headers.add("error", "Please Select Evaluator Users");
				LOG.error("Please Select User Evaluator");
				return new ResponseEntity<List<UserPojo>>(null, headers, HttpStatus.BAD_REQUEST);
			}
		} catch (Exception e) {
			LOG.error("Error While adding evaluator user : " + e.getMessage(), e);
			headers.add("error", "Error While adding evaluator user : " + e.getMessage());
			return new ResponseEntity<List<UserPojo>>(null, headers, HttpStatus.BAD_REQUEST);
		}

		List<UserPojo> evaluatorList = supplierPerformanceFormService.findEvaluatorsUserByFormId(spFormId);

		return new ResponseEntity<List<UserPojo>>(evaluatorList, headers, HttpStatus.OK);
	}

	@RequestMapping(path = "/removeEvaluatorToList", method = RequestMethod.POST)
	public ResponseEntity<List<UserPojo>> removeEvaluator(@RequestParam("spFormId") String spFormId, @RequestParam("userId") String userId) {
		HttpHeaders headers = new HttpHeaders();
		LOG.info("removeEvaluator----Evaluator" + " spFormId: " + spFormId + " userId: " + userId);
		List<User> evaluators = null;
		try {
			evaluators = supplierPerformanceFormService.removeEvaluator(spFormId, userId);
		} catch (Exception e) {
			LOG.error("Error While removing evaluator user : " + e.getMessage(), e);
			headers.add("error", "Error While removing evaluator user : " + e.getMessage());
			return new ResponseEntity<List<UserPojo>>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		List<UserPojo> evList = new ArrayList<UserPojo>();
		List<UserPojo> evaluatorList = userService.fetchAllUsersForTenant(SecurityLibrary.getLoggedInUserTenantId(), "", UserType.NORMAL_USER);
		for (UserPojo user : evaluatorList) {
			boolean found = false;
			for (User euser : evaluators) {
				if (euser.getId().equals(user.getId())) {
					found = true;
					break;
				}
			}
			try {
				if (!found) {
					evList.add(user);
				}
			} catch (Exception e) {
				LOG.error("Error during clone : " + e.getMessage(), e);
			}
		}

		return new ResponseEntity<List<UserPojo>>(evList, headers, HttpStatus.OK);
	}

	@RequestMapping(path = "/viewSPFSummary", method = RequestMethod.GET)
	public String viewSPFSummary(@RequestParam String formId, Model model) {
		LOG.info("View Supplier Performance Evaluation Summary GET called sup Id :" + formId);
		SupplierPerformanceForm supplierPerformanceForm = supplierPerformanceEvaluationService.getSupplierPerformanceFormByFormId(formId);
		model.addAttribute("spForm", supplierPerformanceForm);
		List<SupplierPerformanceFormCriteria> spfCriteriaList = supplierPerformanceFormCriteriaService.getSPFormCriteriaByFormId(formId);
		model.addAttribute("spfCriteriaList", spfCriteriaList);
		List<SupplierPerformanceAudit> supplierPerformanceAuditList = supplierPerformanceAuditService.getSupplierPerformanceAuditByFormIdForOwner(formId);
		model.addAttribute("supplierPerformanceAuditList", supplierPerformanceAuditList);

		List<SupplierPerformanceReminder> reminderList = null;
		if (CollectionUtil.isNotEmpty(supplierPerformanceForm.getFormReminder())) {
			reminderList = new ArrayList<SupplierPerformanceReminder>();
			for (SupplierPerformanceReminder reminder : supplierPerformanceForm.getFormReminder()) {
				reminderList.add(reminder);
			}
		}
		model.addAttribute("reminderList", reminderList);
		model.addAttribute("showSummaryTab", true);

		return "viewSPFormSummary";
	}

	@RequestMapping(path = "/spfFinish/{id}", method = RequestMethod.POST)
	public String finishSupplierPerformanceForm(@PathVariable("id") String formId, Model model, RedirectAttributes redir, HttpSession session) {
		LOG.info("Finish SupplierPerformanceForm called. Form Id : " + formId);
		SupplierPerformanceForm spForm = null;
		try {
			spForm = supplierPerformanceFormService.finishSupplierPerformanceForm(formId, SecurityLibrary.getLoggedInUser(), session);

			if (spForm.getOldFormStatus() == SupplierPerformanceFormStatus.SUSPENDED) {
				redir.addFlashAttribute("success", messageSource.getMessage("sp.form.resume.success", new Object[] { spForm.getFormId() }, Global.LOCALE));
				return "redirect:/buyer/supplierPerformanceList";
			} else {
				redir.addFlashAttribute("success", messageSource.getMessage("sp.form.finish.success", new Object[] { spForm.getFormId() }, Global.LOCALE));
				return "redirect:/buyer/supplierPerformanceList";
			}
		} catch (Exception e) {
			LOG.error("Error while saving Supplier Performance Form : " + e.getMessage(), e);
			redir.addFlashAttribute("error", messageSource.getMessage("sp.form.finish.error", new Object[] { e.getMessage() }, Global.LOCALE));
		}
		return "redirect:/buyer/supplierPerformanceSummary?id=" + formId;

	}

	@RequestMapping(path = "/evaluationProgress/{formId}", method = RequestMethod.GET)
	public String getEvaluationProgress(@PathVariable("formId") String formId, Model model) {
		LOG.info("Supplier Evaluation Progress called : " + formId);
		SupplierPerformanceForm spForm = supplierPerformanceFormService.getSupplierPerformanceFormById(formId);
		model.addAttribute("spForm", spForm);
		List<SupplierPerformanceEvaluatorUser> evaluatorList = spForm.getEvaluators();
		model.addAttribute("evaluatorList", evaluatorList);
		model.addAttribute("evaluatorCount", evaluatorList.size());
		int evalPreviewCnt = 0, evaluateCnt = 0, approvedCnt = 0;
		if (evaluatorList != null) {
			for (SupplierPerformanceEvaluatorUser u : evaluatorList) {
				if (u.getPreviewDate() != null) {
					evalPreviewCnt++;
				}
				if (u.getEvaluateDate() != null) {
					evaluateCnt++;
				}
				if (u.getApprovedDate() != null && u.getEvaluationStatus() == SupperPerformanceEvaluatorStatus.APPROVED) {
					approvedCnt++;
				}
			}
		}
		model.addAttribute("approvedCnt", approvedCnt);
		model.addAttribute("evaluateCnt", evaluateCnt);
		model.addAttribute("evalPreviewCnt", evalPreviewCnt);
		model.addAttribute("showProgressTab", true);
		return "evaluationProgress";
	}

	@RequestMapping(path = "/submitEvaluation", method = RequestMethod.POST)
	public ModelAndView submitEvaluation(@ModelAttribute("evaluatorUser") SupplierPerformanceEvaluatorUser evaluatorUser, Model model, RedirectAttributes redir, HttpSession session) {
		LOG.info("Submit Evaluation called. EvaluatorUser Id : " + evaluatorUser.getId());
		SupplierPerformanceEvaluatorUser evalUser = null;
		JRSwapFileVirtualizer virtualizer = null;
		try {
			virtualizer = new JRSwapFileVirtualizer(200, new JRSwapFile(System.getProperty("java.io.tmpdir"), 2048, 1024), false);

			SupplierPerformanceFormStatus status = supplierPerformanceFormService.getFormStatusByFormId(evaluatorUser.getForm().getId());
			if (SupplierPerformanceFormStatus.ACTIVE != status || status == null) {
				model.addAttribute("error", "No changes are allowed while the Form is in SUSPENDED status");
				redir.addFlashAttribute("error", "No changes are allowed while the Form is in SUSPENDED status");
				return new ModelAndView("redirect:/buyer/viewEvaluatorFormSummary/" + evaluatorUser.getId());
			}

			if (Boolean.TRUE == evaluatorUser.getEnablePerformanceEvaluationApproval() && CollectionUtil.isEmpty(evaluatorUser.getEvaluationApprovals())) {
				LOG.info("Approvals can not be empty");
				model.addAttribute("error", messageSource.getMessage("spf.approval.empty", new Object[] {}, Global.LOCALE));
				redir.addFlashAttribute("error", messageSource.getMessage("spf.approval.empty", new Object[] {}, Global.LOCALE));
				return new ModelAndView("redirect:/buyer/viewEvaluatorFormSummary/" + evaluatorUser.getId());
			}

			if (CollectionUtil.isNotEmpty(evaluatorUser.getEvaluationApprovals())) {
				for (PerformanceEvaluationApproval app : evaluatorUser.getEvaluationApprovals()) {
					for (PerformanceEvaluationApprovalUser appUser : app.getApprovalUsers()) {
						if (evaluatorUser.getEvaluator().getId().equals(appUser.getUser().getId())) {
							LOG.info("Evaluator and Approver can not be same");
							model.addAttribute("error", messageSource.getMessage("spf.evaluator.approver.not.same", new Object[] {}, Global.LOCALE));
							redir.addFlashAttribute("error", messageSource.getMessage("spf.evaluator.approver.not.same", new Object[] {}, Global.LOCALE));
							return new ModelAndView("redirect:/buyer/viewEvaluatorFormSummary/" + evaluatorUser.getId());
						}
					}
				}
			}

			evalUser = supplierPerformanceEvaluatorUserService.saveEvaluatorUser(evaluatorUser, Boolean.FALSE);

			try {

				User loginUser = SecurityLibrary.getLoggedInUser();
				byte[] summarySnapshot = null;
				try {
					JasperPrint jasperPrint = supplierPerformanceEvaluationService.getPerformancEvaluationApprovalSummaryPdf(evalUser, SecurityLibrary.getLoggedInUser(), (String) session.getAttribute(Global.SESSION_TIME_ZONE_KEY), virtualizer);
					summarySnapshot = JasperExportManager.exportReportToPdf(jasperPrint);
					SupplierPerformanceAudit audit = new SupplierPerformanceAudit(evalUser.getForm(), evalUser, loginUser, new java.util.Date(), SupplierPerformanceAuditActionType.Finish, messageSource.getMessage("spForm.audit.sent.app", new Object[] { evalUser.getForm().getFormId() }, Global.LOCALE), summarySnapshot, Boolean.TRUE, Boolean.TRUE, Boolean.TRUE, Boolean.FALSE, Boolean.TRUE, Boolean.TRUE);
					supplierPerformanceAuditService.save(audit);
				} catch (Exception e) {
					LOG.error("Error while Store summary PDF as byte : " + e.getMessage(), e);
				}

				// snapShotAuditService.doSupplierPerformanceFormAudit(evalUser.getForm(), session, user,
				// SupplierPerformanceAuditActionType.Finish, "spForm.audit.sent.app", virtualizer);
			} catch (Exception e) {
				LOG.info("Error while updating Auditing : " + e.getMessage(), e);
			}

			try {
				LOG.info("--------------------------BEFORE AUDIT---------------------------------------");
				BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.FINISH, "Supplier Performance Evaluation Form \"" + evalUser.getForm().getFormId() + "\" sent for approval.", SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser(), new Date(), ModuleType.PerformanceEvaluation);
				buyerAuditTrailService.save(buyerAuditTrail);
				LOG.info("--------------------------AFTER AUDIT---------------------------------------");
			} catch (Exception e) {
				LOG.error("Error while recording Finish audit " + e.getMessage(), e);
			}

			approvalService.doApproval(evaluatorUser, session, SecurityLibrary.getLoggedInUser(), virtualizer);

		} catch (Exception e) {
			LOG.error("Error while saving Supplier Performance Evaluator User : " + e.getMessage(), e);
			redir.addFlashAttribute("error", messageSource.getMessage("sp.form.finish.error", new Object[] { e.getMessage() }, Global.LOCALE));
		}

		return new ModelAndView("redirect:/buyer/viewEvaluatorFormSummary/" + evaluatorUser.getId());
	}

	@RequestMapping(path = "/scoreCard/{formId}", method = RequestMethod.GET)
	public String getScoreCard(@PathVariable("formId") String formId, Model model) {

		supplierPerformanceFormService.getScoreCardList(formId, model, SecurityLibrary.getLoggedInUserTenantId());
		return "evaluationScoreTab";
	}

	@RequestMapping(path = "/viewEvaluatorFormSummary/{id}", method = RequestMethod.GET)
	public String viewEvaluatorFormSummary(@PathVariable String id, Model model) {
		LOG.info("View Supplier Performance Evaluation Summary GET called sup Id :" + id);

		SupplierPerformanceEvaluatorUser evaluator = supplierPerformanceEvaluatorUserService.getFormEvaluatorUserByUserIdAndEvalUserId(id, SecurityLibrary.getLoggedInUser().getId());
		if (evaluator != null) {
			model.addAttribute("evaluatorUser", evaluator);
			model.addAttribute("appComments", evaluator.getEvalApprovalComment());
		} else {
			return "redirect:/403_error";
		}
		model.addAttribute("spForm", evaluator.getForm());
		List<SupplierPerformanceAudit> supplierPerformanceAuditList = supplierPerformanceAuditService.getSupplierPerformanceAuditByFormIdForEvaluater(evaluator.getForm().getId(), evaluator.getId());
		model.addAttribute("supplierPerformanceAuditList", supplierPerformanceAuditList);
		model.addAttribute("appComments", evaluator.getEvalApprovalComment());

		List<User> evalApproavlUsers = new ArrayList<User>();
		model.addAttribute("reminderList", evaluator.getForm().getFormReminder());
LOG.info("supplier performance approver >>>>>  "+evaluator.getEvaluationApprovals());
		if (evaluator != null && CollectionUtil.isNotEmpty(evaluator.getEvaluationApprovals())) {
			for (PerformanceEvaluationApproval suspApp : evaluator.getEvaluationApprovals()) {
				for (PerformanceEvaluationApprovalUser suspAppU : suspApp.getApprovalUsers()) {
					if (!evalApproavlUsers.contains(suspAppU.getUser())) {
						evalApproavlUsers.add(suspAppU.getUser());
					}
				}
			}
		}
		model.addAttribute("evalApprvlUserList", evalApproavlUsers);

		List<UserPojo> userListSumm = userService.fetchAllUsersForTenant(SecurityLibrary.getLoggedInUserTenantId(), "", UserType.NORMAL_USER);
		List<PerformanceEvaluationApprovalUser> evalAppUserList = new ArrayList<PerformanceEvaluationApprovalUser>();
		LOG.info("suspUserList ............." + evalAppUserList.size());
		for (UserPojo user : userListSumm) {
			User u = new User(user.getId(), user.getLoginId(), user.getName(), user.getCommunicationEmail(), user.isEmailNotifications(), user.getTenantId(), user.isDeleted());
			evalAppUserList.add(new PerformanceEvaluationApprovalUser(u));
		}
		model.addAttribute("evalAppUserList", evalAppUserList);

		EventPermissions formPermissions = supplierPerformanceEvaluatorUserService.getUserPemissionsForForm(SecurityLibrary.getLoggedInUser().getId(), evaluator.getId());
		model.addAttribute("formPermissions", formPermissions);

		supplierPerformanceEvaluatorUserService.updateEvaluatorPreviewedDate(evaluator.getId());

		return "viewEvaluatorFormSummary";
	}

	@RequestMapping(path = "/concludeForm/{formId}", method = RequestMethod.POST)
	public String concludeForm(@PathVariable String formId, @RequestParam(required = false, name = "concludeRemarks") String remarks, @RequestParam("ratingId") String ratingId, RedirectAttributes redir, HttpSession session) {
		LOG.info("Remarks: " + remarks + " Rating Id : " + ratingId);
		try {
			SupplierPerformanceForm spForm = supplierPerformanceFormService.concludeSupplierPerformanceForm(formId, remarks, ratingId, session, SecurityLibrary.getLoggedInUser());
			redir.addFlashAttribute("success", messageSource.getMessage("sp.form.conclude.success", new Object[] { spForm.getFormId() }, Global.LOCALE));

			try {
				LOG.info("--------------------------BEFORE AUDIT---------------------------------------");
				BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.CONCLUDE, "Supplier Performance Evaluation Form \"" + spForm.getFormId() + "\" Concluded.", SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser(), new Date(), ModuleType.PerformanceEvaluation);
				buyerAuditTrailService.save(buyerAuditTrail);
				LOG.info("--------------------------AFTER AUDIT---------------------------------------");
			} catch (Exception e) {
				LOG.error("Error while recording Conclude audit " + e.getMessage(), e);
			}

		} catch (ErpIntegrationException e) {

			// Do ERP Integration Failure Audit....
			SupplierPerformanceForm form = new SupplierPerformanceForm();
			form.setId(formId);
			SupplierPerformanceAudit audit = new SupplierPerformanceAudit(form, null, SecurityLibrary.getLoggedInUser(), new java.util.Date(), SupplierPerformanceAuditActionType.Transfer, e.getMessage(), Boolean.TRUE, Boolean.FALSE, Boolean.FALSE);
			supplierPerformanceAuditService.save(audit);

			LOG.error("Error while concluding form : " + e.getMessage(), e);
			redir.addFlashAttribute("error", messageSource.getMessage("sp.form.conclude.error", new Object[] { e.getMessage() }, Global.LOCALE));
			return "redirect:/buyer/scoreCard/" + formId;
		} catch (Exception e) {
			LOG.error("Error while concluding form : " + e.getMessage(), e);
			redir.addFlashAttribute("error", messageSource.getMessage("sp.form.conclude.error", new Object[] { e.getMessage() }, Global.LOCALE));
			return "redirect:/buyer/scoreCard/" + formId;
		}

		return "redirect:/buyer/supplierPerformanceList";
	}

	@RequestMapping(path = "/approveEvaluation", method = RequestMethod.POST)
	public String approveEvaluation(@RequestParam String evalUserId, @RequestParam String remarks, RedirectAttributes redir, HttpSession session) {
		LOG.info("Approve Evaluation eval User id :" + evalUserId + " remarks :" + remarks + " SecurityLibrary.getLoggedInUser() :" + SecurityLibrary.getLoggedInUser().getCommunicationEmail());
		JRSwapFileVirtualizer virtualizer = null;
		virtualizer = new JRSwapFileVirtualizer(100, new JRSwapFile(System.getProperty("java.io.tmpdir"), 2048, 1024), false);
		try {
			SupplierPerformanceEvaluatorUser evalUSer = new SupplierPerformanceEvaluatorUser();
			evalUSer.setId(evalUserId);
			SupplierPerformanceEvaluatorUser approvedEvaluator = approvalService.doApproval(evalUSer, SecurityLibrary.getLoggedInUser(), remarks, true, session, virtualizer);

			redir.addFlashAttribute("success", messageSource.getMessage("spf.audit.approved", new Object[] { (StringUtils.checkString(approvedEvaluator.getForm().getFormId()).length() > 0 ? approvedEvaluator.getForm().getFormId() : " ") }, Global.LOCALE));
		} catch (Exception e) {
			LOG.error("Error while approving SPF :" + e.getMessage(), e);
			redir.addFlashAttribute("error", e.getMessage());
		}
		return "redirect:/buyer/buyerDashboard";
	}

	@RequestMapping(path = "/rejectEvaluation", method = RequestMethod.POST)
	public String rejectEvaluation(@RequestParam String evalUserId, @RequestParam String remarks, RedirectAttributes redir, HttpSession session) {
		LOG.info("Reject Evaluation called eval User id :" + evalUserId + " remarks :" + remarks);
		JRSwapFileVirtualizer virtualizer = null;
		virtualizer = new JRSwapFileVirtualizer(100, new JRSwapFile(System.getProperty("java.io.tmpdir"), 2048, 1024), false);
		try {
			SupplierPerformanceEvaluatorUser evalUSer = new SupplierPerformanceEvaluatorUser();
			evalUSer.setId(evalUserId);

			SupplierPerformanceEvaluatorUser rejectedEvaluator = approvalService.doApproval(evalUSer, SecurityLibrary.getLoggedInUser(), remarks, false, session, virtualizer);

			redir.addFlashAttribute("success", messageSource.getMessage("spf.audit.rejected", new Object[] { (StringUtils.checkString(rejectedEvaluator.getForm().getFormId()).length() > 0 ? rejectedEvaluator.getForm().getFormId() : " ") }, Global.LOCALE));
		} catch (Exception e) {
			LOG.error("Error while rejecting spf :" + e.getMessage(), e);
			redir.addFlashAttribute("error", messageSource.getMessage("flasherror.while.rejecting.spf", new Object[] { e.getMessage() }, Global.LOCALE));
		}
		return "redirect:/buyer/buyerDashboard";
	}

	@RequestMapping(path = "/spfEvaluatorApprovalSummary/{id}", method = RequestMethod.GET)
	public String spfEvaluatorApprovalSummary(@PathVariable(name = "id") String evalUserId, Model model) {
		LOG.info("View Supplier Performance Evaluation Summary GET called sup Id :" + evalUserId);

		SupplierPerformanceEvaluatorUser evaluator = supplierPerformanceEvaluatorUserService.getFormEvaluatorUserByIdFromApproval(evalUserId, SecurityLibrary.getLoggedInUser().getId());
		if (evaluator != null) {
			model.addAttribute("evaluatorUser", evaluator);
			model.addAttribute("appComments", evaluator.getEvalApprovalComment());
		} else {
			return "redirect:/403_error";
		}

		model.addAttribute("spForm", evaluator.getForm());

		// TODO: check if this is needed here - Approval screen does not need to show Audit
		List<SupplierPerformanceAudit> supplierPerformanceAuditList = supplierPerformanceAuditService.getSupplierPerformanceAuditByFormIdForApprover(evaluator.getForm().getId(), evaluator.getId());
		model.addAttribute("supplierPerformanceAuditList", supplierPerformanceAuditList);

		List<SupplierPerformanceReminder> reminderList = null;
		if (CollectionUtil.isNotEmpty(evaluator.getForm().getFormReminder())) {
			reminderList = new ArrayList<SupplierPerformanceReminder>();
			for (SupplierPerformanceReminder reminder : evaluator.getForm().getFormReminder()) {
				reminderList.add(reminder);
			}
		}
		model.addAttribute("reminderList", reminderList);

		EventPermissions formPermissions = supplierPerformanceEvaluatorUserService.getUserPemissionsForForm(SecurityLibrary.getLoggedInUser().getId(), evaluator.getId());
		model.addAttribute("formPermissions", formPermissions);

		return "viewEvaluatorApprovalSummary";
	}

	@RequestMapping(path = "/suspendForm", method = RequestMethod.POST)
	public ModelAndView suspendForm(@ModelAttribute SupplierPerformanceForm spForm, RedirectAttributes redirectAttributes, Model model, HttpSession session) {
		JRSwapFileVirtualizer virtualizer = null;
		SupplierPerformanceForm spf = supplierPerformanceEvaluationService.getSupplierPerformanceFormByFormId(spForm.getId());
		try {
			virtualizer = new JRSwapFileVirtualizer(300, new JRSwapFile(System.getProperty("java.io.tmpdir"), 2048, 1024), false);

			// Bcoz of Broker's error this code is not executing ..So separated code for broker and audit
			if (spForm != null) {
				supplierPerformanceEvaluationService.suspendForm(spForm.getId());

				try {
					// User user = SecurityLibrary.getLoggedInUser();
					// snapShotAuditService.doSupplierPerformanceFormAudit(spf, session, user,
					// SupplierPerformanceAuditActionType.Suspend, "sp.form.audit.suspend", virtualizer);
					SupplierPerformanceAudit audit = new SupplierPerformanceAudit(spf, null, SecurityLibrary.getLoggedInUser(), new java.util.Date(), SupplierPerformanceAuditActionType.Suspend, messageSource.getMessage("sp.form.audit.suspend", new Object[] { spf.getFormId() }, Global.LOCALE), Boolean.TRUE, Boolean.TRUE, Boolean.TRUE);
					supplierPerformanceAuditService.save(audit);
				} catch (Exception e) {
					LOG.info("Error while saving audit trial : " + e.getMessage(), e);
				}

				try {
					BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.SUSPEND, "Supplier Performance Evaluation Form \"" + spf.getFormId() + "\" Suspended.", SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser(), new Date(), ModuleType.PerformanceEvaluation);
					buyerAuditTrailDao.save(buyerAuditTrail);
				} catch (Exception e) {
					LOG.error("Error while recording form resume " + e.getMessage(), e);
				}
			}
			try {
				try {
					jmsTemplate.send("QUEUE.FORM.SUSPEND.NOTIFICATION", new MessageCreator() {
						@Override
						public Message createMessage(Session session) throws JMSException {
							TextMessage objectMessage = session.createTextMessage();
							objectMessage.setText(spForm.getId());
							return objectMessage;
						}
					});
				} catch (Exception e) {
					LOG.error("Error sending message to queue : " + e.getMessage(), e);
				}
			} catch (Exception e) {
				LOG.error("Error while sending notification to form suspend " + e.getMessage(), e);
			}

			try {
				simpMessagingTemplate.convertAndSend("/auctionTopic/SPSUSPEND", spForm.getId());
			} catch (Exception e) {
				LOG.error("Error while sending suspend event to MQ : " + e.getMessage(), e);
			}

			redirectAttributes.addFlashAttribute("success", messageSource.getMessage("form.suspended.success", new Object[] { spf.getFormId() }, Global.LOCALE));

		} catch (Exception e) {
			LOG.error("Error : " + e.getMessage(), e);
			model.addAttribute("error", messageSource.getMessage("evaluation.while.suspending.form", new Object[] { e.getMessage() }, Global.LOCALE));
			return new ModelAndView("supplierPerformanceSummary");
		} finally {
			if (virtualizer != null) {
				virtualizer.cleanup();
			}
		}
		return new ModelAndView("redirect:/buyer/editSupplierPerformanceEvaluation/" + spForm.getId());
	}

	@RequestMapping(path = "/cancelSPForm/{formId}", method = RequestMethod.POST)
	public String cancelSpForm(RedirectAttributes redir, @PathVariable("formId") String formId) {
		LOG.info("CANCEL SPF METHOD IS CALLED " + formId);
		SupplierPerformanceForm spForm = supplierPerformanceFormService.cancelSPForm(formId);

		try {
			SupplierPerformanceAudit audit = new SupplierPerformanceAudit();
			audit.setAction(SupplierPerformanceAuditActionType.Cancelled);
			audit.setActionBy(SecurityLibrary.getLoggedInUser());
			audit.setActionDate(new Date());
			audit.setForm(spForm);
			audit.setDescription(messageSource.getMessage("sp.form.audit.cancelled", new Object[] { spForm.getFormId() }, Global.LOCALE));
			audit.setOwner(Boolean.TRUE);
			audit.setEvaluator(Boolean.TRUE);
			audit.setApprover(Boolean.TRUE);
			supplierPerformanceAuditService.save(audit);
		} catch (Exception e) {
			LOG.error("Error while saving SPF Cancel audit trail: " + e.getMessage(), e);
		}

		try {
			BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.CANCELLED, "Supplier Performance Form \"" + spForm.getFormId() + "\" cancelled", SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser(), new Date(), ModuleType.PerformanceEvaluation);
			buyerAuditTrailDao.save(buyerAuditTrail);
		} catch (Exception e) {
			LOG.error("Error while recording audit action form cancel " + e.getMessage(), e);
		}

		redir.addFlashAttribute("success", messageSource.getMessage("flashsuccess.spf.cancelled", new Object[] { (spForm.getFormId()) }, Global.LOCALE));
		return "redirect:/buyer/buyerDashboard";
	}

	@RequestMapping(path = "/saveDraftSPFEvaluation", method = RequestMethod.POST)
	public ModelAndView saveDraftSpfEvaluation(@ModelAttribute("evaluatorUser") SupplierPerformanceEvaluatorUser evaluatorUser, Model model, RedirectAttributes redir, HttpSession session) {
		LOG.info("Subit Evaluation called. EvaluatorUser Id : " + evaluatorUser.getId());
		try {
			SupplierPerformanceFormStatus status = supplierPerformanceFormService.getFormStatusByFormId(evaluatorUser.getForm().getId());
			// allow changes to be done only during ACTIVE form status
			if (SupplierPerformanceFormStatus.ACTIVE == status) {
				supplierPerformanceEvaluatorUserService.saveEvaluatorUser(evaluatorUser, Boolean.TRUE);
			} else {
				model.addAttribute("error", "No changes are allowed while the Form is in SUSPENDED status");
				redir.addFlashAttribute("error", "No changes are allowed while the Form is in SUSPENDED status");
			}
		} catch (Exception e) {
			LOG.error("Error while saving Supplier Performance Evaluator User : " + e.getMessage(), e);
			redir.addFlashAttribute("error", messageSource.getMessage("sp.form.finish.error", new Object[] { e.getMessage() }, Global.LOCALE));
		}
		return new ModelAndView("redirect:/buyer/viewEvaluatorFormSummary/" + evaluatorUser.getId());
	}

	@RequestMapping(path = "/spfEvaluationScore/{id}", method = RequestMethod.POST)
	public void downloadEvaluationScore(@PathVariable(name = "id") String formId, Model model, HttpServletResponse response) {
		try {
			XSSFWorkbook workbook = supplierPerformanceEvaluationService.generateEvaluationReport(formId);
			String downloadFolder = Global.FILE_PATH;
			String fileName = "Supplier Performance.xlsx";
			FileOutputStream out = new FileOutputStream(downloadFolder + "/" + fileName);
			workbook.write(out);
			out.close();
			Path file = Paths.get(downloadFolder, fileName);

			if (Files.exists(file)) {
				response.setContentType("application/vnd.ms-excel");
				response.addHeader("Content-Disposition", "attachment; filename=" + fileName);
				try {
					Files.copy(file, response.getOutputStream());
					response.getOutputStream().flush();
				} catch (IOException e) {
					LOG.error("Error :- " + e.getMessage());
				}
			}

		} catch (Exception e) {
			LOG.error("Error for creating csv report" + e.getMessage(), e);
		}

	}

	@RequestMapping(path = "/downloadPerformanceEvaluationApprovalSummary/{evalUserId}", method = RequestMethod.POST)
	public void downloadPerformanceEvaluationApprovalSummary(@PathVariable("evalUserId") String evalUserId, HttpServletResponse response, HttpSession session) throws Exception {
		JRSwapFileVirtualizer virtualizer = null;
		try {
			// Virtualizar - To increase the performance
			virtualizer = new JRSwapFileVirtualizer(200, new JRSwapFile(System.getProperty("java.io.tmpdir"), 2048, 1024), false);
			String filename = "PerformanceEvaluationSummary.pdf";
			SupplierPerformanceEvaluatorUser evalUser = supplierPerformanceEvaluatorUserService.getEvaluatorUserById(evalUserId);
			if (evalUser.getForm().getFormId() != null) {
				filename = (evalUser.getForm().getFormId()).replace("/", "-") + " Summary.pdf";
			}
			JasperPrint jasperPrint = supplierPerformanceEvaluationService.getPerformancEvaluationApprovalSummaryPdf(evalUser, SecurityLibrary.getLoggedInUser(), (String) session.getAttribute(Global.SESSION_TIME_ZONE_KEY), virtualizer);
			if (jasperPrint != null) {
				streamReport(jasperPrint, filename, response);
			}

		} catch (Exception e) {
			LOG.error("Could not Download Evaluation Summary Report. " + e.getMessage(), e);
		} finally {
			if (virtualizer != null) {
				virtualizer.cleanup();
			}
		}
	}

	@RequestMapping(value = "/downloadAuditFile/{id}", method = RequestMethod.GET)
	public void downloadFile(@PathVariable String id, HttpServletResponse response) throws IOException {
		try {
			SupplierPerformanceAudit audit = supplierPerformanceAuditService.getAuditByFormId(id);
			response.setContentType("application/pdf");
			response.setHeader("Content-Disposition", "attachment; filename=\"SupplierPerformanceForm.pdf\"");

			if (audit.getSummarySnapshot() != null && audit.getSummarySnapshot().length > 0) {
				response.setContentLength(audit.getSummarySnapshot().length);
				FileCopyUtils.copy(audit.getSummarySnapshot(), response.getOutputStream());
			}

			response.flushBuffer();
			response.setStatus(HttpServletResponse.SC_OK);
		} catch (Exception e) {
			LOG.error("Error while File downloaded RFP Audit File : " + e.getMessage(), e);
		}
	}

	@RequestMapping(path = "/downloadScoreCard/{formId}", method = RequestMethod.POST)
	public void downloadScoreCard(@PathVariable("formId") String formId, HttpServletResponse response, HttpSession session) throws Exception {
		// JRSwapFileVirtualizer virtualizer = null;
		try {
			// Virtualizar - To increase the performance
			// virtualizer = new JRSwapFileVirtualizer(200, new JRSwapFile(System.getProperty("java.io.tmpdir"), 2048,
			// 1024), false);
			response.setContentType("application/pdf");
			response.setHeader("Content-Disposition", "attachment; filename=\"SupplierPerformanceForm.pdf\"");

			SupplierPerformanceForm performanceForm = new SupplierPerformanceForm();
			performanceForm.setId(formId);
			Map<String, Object> map = supplierPerformanceEvaluationService.getScoreCard(performanceForm, SecurityLibrary.getLoggedInUser(), (String) session.getAttribute(Global.SESSION_TIME_ZONE_KEY), SecurityLibrary.getLoggedInUserTenantId());
			String message = FreeMarkerTemplateUtils.processTemplateIntoString(freemarkerConfiguration.getTemplate(Global.SCORE_CARD_PDF_TEMPLATE), map);
			// LOG.info("message " + message);

			File file = File.createTempFile("report", "html");
			FileWriter w = new FileWriter(file);
			w.write(message);
			w.flush();
			w.close();

			HtmlConverter.convertToPdf(new FileInputStream(file), response.getOutputStream());
			response.flushBuffer();
			response.setStatus(HttpServletResponse.SC_OK);

		} catch (Exception e) {
			LOG.error("Could not Download Score Card Report. " + e.getMessage(), e);
		} finally {
			// if (virtualizer != null) {
			// virtualizer.cleanup();
			// }
		}
	}
}
