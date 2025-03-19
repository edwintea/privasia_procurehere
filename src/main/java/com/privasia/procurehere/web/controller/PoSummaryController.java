package com.privasia.procurehere.web.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.*;
import java.util.TimeZone;

import javax.annotation.Resource;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.privasia.procurehere.core.dao.*;
import com.privasia.procurehere.core.entity.*;
import com.privasia.procurehere.core.entity.Currency;
import com.privasia.procurehere.core.enums.*;

import com.privasia.procurehere.core.pojo.*;
import com.privasia.procurehere.service.*;
import com.privasia.procurehere.web.editors.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import org.hibernate.Hibernate;
import org.jfree.util.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomCollectionEditor;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.context.MessageSource;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.privasia.procurehere.core.exceptions.NotAllowedException;
import com.privasia.procurehere.core.exceptions.NotAuthorizedException;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.DateUtil;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.SecurityLibrary;
import com.privasia.procurehere.core.utils.StringUtils;
import com.privasia.procurehere.service.impl.SnapShotAuditService;

import net.sf.jasperreports.engine.fill.JRSwapFileVirtualizer;
import net.sf.jasperreports.engine.util.JRSwapFile;

/**
 * @author parveen
 */
@Controller
@RequestMapping("/buyer")
public class PoSummaryController extends PrBaseController {

	protected static final Logger LOG = LogManager.getLogger(Global.PR_LOG);

	@Autowired
	ServletContext context;

	@Autowired
	PoService poService;

	@Autowired
	OwnerSettingsService ownerSettingsService;

	@Autowired
	ErpSetupDao erpSetupDao;

	@Resource
	MessageSource messageSource;

	@Autowired
	PoAuditService poAuditService;

	@Autowired
	ApprovalService approvalService;

	@Autowired
	PrService prService;

	@Autowired
	PrDao prDao;

	@Autowired
	PoDao poDao;

	@Autowired
	PrTemplateService prTemplateService;

	@Autowired
	BudgetService budgetService;

	@Autowired
	PoAuditDao poAuditDao;

	@Autowired
	BuyerSettingsService buyerSettingsService;

	@Autowired
	PrAuditService prAuditService;

	@Autowired
	DeliveryOrderService deliveryOrderService;

	@Autowired
	InvoiceService invoiceService;

	@Autowired
	PoReportDao poReportDao;

	@Autowired
	GoodsReceiptNoteService goodsReceiptNoteService;

	@Autowired
	BuyerAuditTrailDao buyerAuditTrailDao;

	@Autowired
	BuyerAddressService buyerAddressService;
	@Autowired
	TransactionLogService transactionLogService;

	@Autowired
	UomService uomService;

	@Autowired
	UserService userService;

	@Autowired
	BuyerAddressEditor buyerAddressEditor;

	@Autowired
	PoApprovalUserEditor poApprovalUserEditor;

	@Autowired
	PoTeamMemberEditor poTeamMemberEditor;

	@Autowired
	SnapShotAuditService snapShotAuditService;

	@Autowired
	EventIdSettingsService eventIdSettingsService;

	@Autowired
	CostCenterService costCenterService;

	@Autowired
	BusinessUnitService businessUnitService;

	@Autowired
	CurrencyService currencyService;

	@Autowired
	BuyerService buyerService;

	@Autowired
	PaymentTermsService paymentTermesService;


	@Autowired
	CostCenterEditor costCenterEditor;

	@Autowired
	CurrencyEditor currencyEditor;


	@Autowired
	UserEditor userEditor;


	@Autowired
	BusinessUnitEditor businessUnitEditor;

	@Autowired
	PoEventService poEventService;

	@ModelAttribute("poDocType")
	public List<PoDocumentType> getPoDocType() {
		return Arrays.asList(PoDocumentType.values());
	}

	@ModelAttribute("poStatusList")
	public List<PoStatus> getPoStatusList() {
		List<PoStatus> poStatusList = Arrays.asList(PoStatus.values());
		return poStatusList;
	}

	@InitBinder
	public void initBinder(WebDataBinder binder, HttpSession session) {
		binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
		TimeZone timeZone = TimeZone.getDefault();
		String strTimeZone = (String) session.getAttribute(Global.SESSION_TIME_ZONE_KEY);
		if (strTimeZone != null) {
			timeZone = TimeZone.getTimeZone(strTimeZone);
		}
		SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a");
		timeFormat.setTimeZone(timeZone);
		SimpleDateFormat deliveryDate = new SimpleDateFormat("dd/MM/yyyy");
		deliveryDate.setTimeZone(timeZone);
		binder.registerCustomEditor(Date.class, "deliveryTime", new CustomDateEditor(timeFormat, true));
		binder.registerCustomEditor(Date.class, "deliveryDate", new CustomDateEditor(deliveryDate, true));
		binder.registerCustomEditor(BuyerAddress.class, buyerAddressEditor);
		binder.registerCustomEditor(PoApprovalUser.class, poApprovalUserEditor);
		binder.registerCustomEditor(PoTeamMember.class, poTeamMemberEditor);

		binder.registerCustomEditor(Currency.class, currencyEditor);
		binder.registerCustomEditor(CostCenter.class, costCenterEditor);
		binder.registerCustomEditor(BusinessUnit.class, businessUnitEditor);
		binder.registerCustomEditor(User.class, userEditor);
		binder.registerCustomEditor(List.class, "approvalUsers", new CustomCollectionEditor(List.class) {
			protected Object convertElement(Object element) {
				if (element != null) {
					String id = (String) element;
					User user = userService.findUserById(id);
					return new PoApprovalUser(user);
				}
				return null;
			}
		});
	}

	@RequestMapping(path = "/poList", method = RequestMethod.GET)
	public String poList(Model model) {
		String userId = SecurityLibrary.getLoggedInUser().getId();
		LOG.info("User Id : " + userId);

		List<String> buIds = businessUnitService.getBusinessUnitIdByUserId(SecurityLibrary.getLoggedInUser().getId());
		//LOG.info("Business Unit Ids : "+buIds);

		long draftPoCount = poService.findPoCountBasedOnStatusAndTenantAndBuisnessUnit(SecurityLibrary.ifAnyGranted("ROLE_ADMIN,ROLE_ADMIN_READONLY") ? null : SecurityLibrary.getLoggedInUser().getId(), SecurityLibrary.getLoggedInUserTenantId(), PoStatus.DRAFT, buIds);
		long readyPoCount = poService.findPoCountBasedOnStatusAndTenantAndBuisnessUnit(SecurityLibrary.ifAnyGranted("ROLE_ADMIN,ROLE_ADMIN_READONLY") ? null : SecurityLibrary.getLoggedInUser().getId(), SecurityLibrary.getLoggedInUserTenantId(), PoStatus.READY, buIds);
		long orderedPoCount = poService.findPoCountBasedOnStatusAndTenantAndBuisnessUnit(SecurityLibrary.ifAnyGranted("ROLE_ADMIN,ROLE_ADMIN_READONLY") ? null : SecurityLibrary.getLoggedInUser().getId(), SecurityLibrary.getLoggedInUserTenantId(), PoStatus.ORDERED, buIds);
		long acceptedPoCount = poService.findPoCountBasedOnStatusAndTenantAndBuisnessUnit(SecurityLibrary.ifAnyGranted("ROLE_ADMIN,ROLE_ADMIN_READONLY") ? null : SecurityLibrary.getLoggedInUser().getId(), SecurityLibrary.getLoggedInUserTenantId(), PoStatus.ACCEPTED, buIds);
		long declinedPoCount = poService.findPoCountBasedOnStatusAndTenantAndBuisnessUnit(SecurityLibrary.ifAnyGranted("ROLE_ADMIN,ROLE_ADMIN_READONLY") ? null : SecurityLibrary.getLoggedInUser().getId(), SecurityLibrary.getLoggedInUserTenantId(), PoStatus.DECLINED, buIds);
		long suspendedPoCount = poService.findPoCountBasedOnStatusAndTenantAndBuisnessUnit(SecurityLibrary.ifAnyGranted("ROLE_ADMIN,ROLE_ADMIN_READONLY") ? null : SecurityLibrary.getLoggedInUser().getId(), SecurityLibrary.getLoggedInUserTenantId(), PoStatus.SUSPENDED, buIds);
		long closedPoCount = poService.findPoCountBasedOnStatusAndTenantAndBuisnessUnit(SecurityLibrary.ifAnyGranted("ROLE_ADMIN,ROLE_ADMIN_READONLY") ? null : SecurityLibrary.getLoggedInUser().getId(), SecurityLibrary.getLoggedInUserTenantId(), PoStatus.CLOSED, buIds);
		long cancelledPoCount = poService.findPoCountBasedOnStatusAndTenantAndBuisnessUnit(SecurityLibrary.ifAnyGranted("ROLE_ADMIN,ROLE_ADMIN_READONLY") ? null : SecurityLibrary.getLoggedInUser().getId(), SecurityLibrary.getLoggedInUserTenantId(), PoStatus.CANCELLED, buIds);

		long pendingPoCount = poService.findPoCountBasedOnStatusAndTenantAndBuisnessUnit(SecurityLibrary.ifAnyGranted("ROLE_ADMIN,ROLE_ADMIN_READONLY") ? null : SecurityLibrary.getLoggedInUser().getId(), SecurityLibrary.getLoggedInUserTenantId(), PoStatus.PENDING, buIds);
		long revisePoCount = poService.findPoRevisedCountBaseOnTenantAndBuisnessUnit(SecurityLibrary.getLoggedInUser().getId(), SecurityLibrary.getLoggedInUserTenantId(), buIds);
		long onCancellationPoCount = poService.findPoOnCancellationCountBaseOnTenantAndBuisnessUnit(SecurityLibrary.getLoggedInUser().getId(), SecurityLibrary.getLoggedInUserTenantId(), buIds);

		model.addAttribute("draftPoCount", draftPoCount);
		model.addAttribute("pendingPoCount", pendingPoCount);
		model.addAttribute("revisePoCount", revisePoCount);
		model.addAttribute("onCancellationPoCount", onCancellationPoCount);
		model.addAttribute("readyPoCount", readyPoCount);
		model.addAttribute("orderedPoCount", orderedPoCount);
		model.addAttribute("acceptedPoCount", acceptedPoCount);
		model.addAttribute("declinedPoCount", declinedPoCount);
		model.addAttribute("suspendedPoCount", suspendedPoCount);
		model.addAttribute("closedPoCount", closedPoCount);
		model.addAttribute("cancelledPoCount", cancelledPoCount);
		model.addAttribute("userId", userId);
		return "poList";
	}

	@RequestMapping(path = "/poListData", method = RequestMethod.GET)
	public ResponseEntity<TableData<Po>> poListData(TableDataInput input,
													@RequestParam(required = false) String dateTimeRange,
													@RequestParam(required = false) String viewType,
													@RequestParam(required = false) String status,
													HttpSession session,
													HttpServletResponse response) {

		try {
			LOG.info("Start : " + input.getStart() + " Length : " + input.getLength() + " Sort : " + input.getSort()
					+ ", dateTimeRange :" + dateTimeRange + " ,viewType " + viewType + ",status " + status);

			String startDate = null;
			String endDate = null;
			Date sDate = null;
			Date eDate = null;

			if (StringUtils.checkString(dateTimeRange).length() > 0) {
				String dateTimeArr[] = dateTimeRange.split("-");
				if (dateTimeArr.length == 2) {
					startDate = dateTimeArr[0].trim();
					endDate = dateTimeArr[1].trim();

					try {
						// Use the correct date format
						SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
						sDate = dateFormat.parse(startDate);
						eDate = dateFormat.parse(endDate);
						LOG.info("Start date: " + sDate + " End date: " + eDate);

					} catch (ParseException e) {
						LOG.error("Invalid date format: " + e.getMessage());
						HttpHeaders headers = new HttpHeaders();
						headers.add("error", "Invalid date format. Expected format is MM/dd/yyyy.");
						return new ResponseEntity<>(null, headers, HttpStatus.BAD_REQUEST);
					}

				} else {
					LOG.warn("Invalid dateTimeRange format: {}", dateTimeRange);
					HttpHeaders headers = new HttpHeaders();
					headers.add("error", "Invalid dateTimeRange format. Expected format is 'startDate-endDate'.");
					return new ResponseEntity<>(null, headers, HttpStatus.BAD_REQUEST);
				}
			}

			LOG.info("Tenant Id : " + SecurityLibrary.getLoggedInUserTenantId());

			List<String> buIds = businessUnitService.getBusinessUnitIdByUserId(SecurityLibrary.getLoggedInUser().getId());
			LOG.info("Business Unit Id : " + buIds);

			List<Po> poList = poService.findAllSearchFilterPo(
					SecurityLibrary.ifAnyGranted("ROLE_ADMIN,ROLE_ADMIN_READONLY") && !viewType.equals("me") ? null
							: SecurityLibrary.getLoggedInUser().getId(),
					SecurityLibrary.getLoggedInUserTenantId(), input, sDate, eDate, viewType, status, buIds);

			TableData<Po> data = new TableData<>(poList);
			data.setDraw(input.getDraw());

			long recordFiltered = poService.findTotalSearchFilterPoCount(
					SecurityLibrary.ifAnyGranted("ROLE_ADMIN,ROLE_ADMIN_READONLY") && !viewType.equals("me") ? null
							: SecurityLibrary.getLoggedInUser().getId(),
					SecurityLibrary.getLoggedInUserTenantId(), input, sDate, eDate, viewType, status, buIds);

			data.setRecordsFiltered(recordFiltered);
			data.setRecordsTotal(recordFiltered);

			return new ResponseEntity<>(data, HttpStatus.OK);

		} catch (Exception e) {
			LOG.error("Error fetching PO list: " + e.getMessage(), e);
			HttpHeaders headers = new HttpHeaders();
			headers.add("error", "Error fetching PO list: " + e.getMessage());
			return new ResponseEntity<>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@Transactional
	@RequestMapping(path = "/poView/{poId}", method = RequestMethod.GET)
	public String poView(@PathVariable String poId, @RequestParam(required = false) String prId, Model model, HttpServletRequest request, RedirectAttributes redir) {
		LOG.info("Getting PO view id: " + poId);
		try {
			// Validate PO ID before proceeding
			if (StringUtils.isBlank(poId)) {
				LOG.warn("PO ID is null or empty.");
				model.addAttribute("error", "PO ID is required.");
				return "poView"; // Return early if PO ID is invalid
			}

			constructPoSummaryAttributes(poId, model);
            Po po = poService.findPoById(poId);
            EventPermissions eventPermissions = poService.getUserPemissionsForPo(SecurityLibrary.getLoggedInUser().getId(), po.getId());


			if (StringUtils.isNotBlank(prId)) {
				LOG.info("PR ID >>> : {}", prId);
				Pr pr = prService.getLoadedPrById(prId);
				model.addAttribute("pr", pr);
			} else {

				if (po.getPr() != null) {
					Pr pr = prService.getLoadedPrById(po.getPr().getId());
					model.addAttribute("pr", pr);
				} else {
					LOG.warn("PR ID is null or empty, setting PR to null.");
					model.addAttribute("pr", null);
				}
			}

            if(eventPermissions.isOwner()){
                if(PoStatus.DRAFT.equals(po.getStatus()) || PoStatus.SUSPENDED.equals(po.getStatus())){
                    //return "redirect:/buyer/poCreate/" + poId + "?prId=" + prId;
                }
            }
		} catch (NotAuthorizedException e) {
			LOG.error("Not authorized to view PO: " + e.getMessage(), e);
			redir.addFlashAttribute("requestedUrl", request.getRequestURL());
			return "redirect:/buyer/poList";
		} catch (Exception e) {
			LOG.error("Error in view PO: " + e.getMessage(), e);
			model.addAttribute("error", messageSource.getMessage("posummary.error.view.po",
					new Object[]{e.getMessage()}, Global.LOCALE));
		}
		return "poView";
	}

	@Transactional
	@RequestMapping(path = "/poSummaryView/{poId}", method = RequestMethod.GET)
	public String poSummaryView(@PathVariable String poId, @RequestParam(required = false) String prId, Model model, HttpServletRequest request, RedirectAttributes redir) {
		LOG.info("Getting PO view id: " + poId);
		try {
			// Validate PO ID before proceeding
			if (StringUtils.isBlank(poId)) {
				LOG.warn("PO ID is null or empty.");
				model.addAttribute("error", "PO ID is required.");
				return "poSummaryView"; // Return early if PO ID is invalid
			}

			constructPoSummaryAttributes(poId, model);

			if (StringUtils.isNotBlank(prId)) {
				LOG.info("PR ID >>> : {}", prId);
				Pr pr = prService.getLoadedPrById(prId);
				model.addAttribute("pr", pr);
			} else {
				Po po = poService.findPoById(poId);
				if (po.getPr() != null) {
					Pr pr = prService.getLoadedPrById(po.getPr().getId());
					model.addAttribute("pr", pr);
				} else {
					LOG.warn("PR ID is null or empty, setting PR to null.");
					model.addAttribute("pr", null);
				}
			}
		} catch (NotAuthorizedException e) {
			LOG.error("Not authorized to view PO: " + e.getMessage(), e);
			redir.addFlashAttribute("requestedUrl", request.getRequestURL());
			return "redirect:/buyer/poList";
		} catch (Exception e) {
			LOG.error("Error in view PO: " + e.getMessage(), e);
			model.addAttribute("error", messageSource.getMessage("posummary.error.view.po",
					new Object[]{e.getMessage()}, Global.LOCALE));
		}
		return "poSummaryView";
	}

	/* PH-4113
	 * po CREATE ON STEP 1
	 * */
	@Transactional
	@RequestMapping(path = "/poCreate/{poId}", method = RequestMethod.GET)
	public String poCreate(@PathVariable("poId") String poId, @RequestParam(required = false) String prId, Model model, HttpServletRequest request, RedirectAttributes redir, HttpSession session) throws NotAuthorizedException {

		LOG.info("create po View GET called po id :" + poId);
		HttpHeaders headers = new HttpHeaders();

		if (prId != null && !prId.isEmpty()) {
			Pr pr = prService.getLoadedPrById(prId);

			if (Boolean.TRUE == pr.getLockBudget()) {
				try {
					// autoPopulate currency for budget if diff. conversion rate required
					BuyerSettings buyerSettings = buyerSettingsService.getBuyerSettingsByTenantId(SecurityLibrary.getLoggedInUserTenantId());
					if (!(pr.getCurrency().getCurrencyCode().equals(buyerSettings.getCurrency().getCurrencyCode()))) {
						pr.setConversionRateRequired(Boolean.TRUE);
					}
					if (null != pr.getBusinessUnit() && null != pr.getCostCenter()) {
						Budget budget = budgetService.findBudgetByBusinessUnitAndCostCenter(pr.getBusinessUnit().getId(), pr.getCostCenter().getId());
						if (budget == null && pr.getLockBudget()) {
							redir.addFlashAttribute("error", messageSource.getMessage("err.msg.budget.not.create", new Object[]{}, Global.LOCALE));
							return "redirect:/buyer/createPr";
						}
						if (budget != null && pr.getLockBudget()) {
							LOG.info("budget.getRemainingAmount()" + budget.getRemainingAmount());
							pr.setAvailableBudget(budget.getRemainingAmount());
							pr.setBudgetCurrencyCode(budget.getBaseCurrency().getCurrencyCode());
							DecimalFormat df = new DecimalFormat("#,###.######");
							headers.add("remainingAmt", "Remaining Amount : " + df.format(budget.getRemainingAmount()) + " " + budget.getBaseCurrency().getCurrencyCode());
							redir.addFlashAttribute("remainingAmt", "Remaining Amount :" + budget.getRemainingAmount());

						} else {
							pr.setAvailableBudget(null);
							pr.setBudgetCurrencyCode(null);
						}
					}

				} catch (Exception exception) {
					LOG.error("error budget is not created and trying to create PR >>>>>>>>" + exception.getMessage());
					redir.addFlashAttribute("error", messageSource.getMessage("err.msg.budget.not.create", new Object[]{}, Global.LOCALE));
					return "redirect:/buyer/poCreate";
				}
			}

			// set available budget with two decimal places
			pr.setAvailableBudget(pr.getDecimal() != null ? pr.getAvailableBudget() != null ?
					pr.getAvailableBudget().setScale(Integer.parseInt(pr.getDecimal()), RoundingMode.HALF_UP) : null : pr.getAvailableBudget());


			if (pr.getTemplate() != null && pr.getTemplate().getId() != null) {
				PrTemplate prTemplate = prTemplateService.getPrTemplateForEditById(pr.getTemplate().getId(), SecurityLibrary.getLoggedInUserTenantId());
				model.addAttribute("templateFields", prTemplate.getFields());
				model.addAttribute("prTemplate", prTemplate);
			}

			model.addAttribute("pr", pr);

		} else {
			model.addAttribute("pr", null);
		}


		Boolean enableCorrelation = buyerSettingsService.isEnableUnitAndCostCorrelationByTenantId(SecurityLibrary.getLoggedInUserTenantId());
		model.addAttribute("isEnableUnitAndCostCorrelation", enableCorrelation);

		model.addAttribute("eventPermissions", poService.getUserPemissionsForPo(SecurityLibrary.getLoggedInUser().getId(), poId));
		IdSettings idSetting = eventIdSettingsService.getIdSettingsByIdTypeForTenanatId(SecurityLibrary.getLoggedInUserTenantId(), "PO");
		model.addAttribute("idSetting", idSetting);

		super.constructPrAttributes(model);
		constructPoSummaryAttributes(poId, model);
		constructPoApprovalAttributes(poId, model);

		return "poCreate";
	}

	public Po constructPoApprovalAttributes(String poId, Model model) throws NotAuthorizedException {

		Integer invoiceCounnt = 0, doConunt, grnConunt;
		Po po = poService.getLoadedPoById(poId);
		po.setDeliveryTime(po.getDeliveryDate());
		EventPermissions poPermissions = poService.getUserPemissionsForPo(SecurityLibrary.getLoggedInUser().getId(), po.getId());

		LOG.info(" Is Owner : " + poPermissions.isOwner());
		if (!(SecurityLibrary.ifAnyGranted("ROLE_ADMIN,ROLE_ADMIN_READONLY")
				|| SecurityLibrary.getLoggedInUser().getId().equals(po.getCreatedBy().getId())
				|| Boolean.compare(poPermissions.isApprover(), true) == 0
				|| Boolean.compare(poPermissions.isOwner(), true) == 0)) {
			throw new NotAuthorizedException("Not Authorized for " + SecurityLibrary.getLoggedInUser().getLoginId());
		}

		model.addAttribute("po", po);
		List<PoItem> poItemlist = poService.findAllPoItemByPoIdForSummary(poId);
		model.addAttribute("poItemlist", poItemlist);

		boolean isAutoPublishPo = buyerSettingsService.isAutoPublishePoSettingsByTenantId(SecurityLibrary.getLoggedInUserTenantId());
		model.addAttribute("isAutoPublishPo", isAutoPublishPo);
		LOG.info("Is Auto Publish PO : " + isAutoPublishPo);

		boolean isAutoCreatePo = buyerSettingsService.isAutoCreatePoSettingsByTenantId(SecurityLibrary.getLoggedInUserTenantId());
		model.addAttribute("isAutoCreatePo", isAutoCreatePo);
		LOG.info("Is Auto Create PO : " + isAutoCreatePo);

		model.addAttribute("addressList",
				buyerAddressService.getAllBuyerAddressPojo(SecurityLibrary.getLoggedInUserTenantId()));

		model.addAttribute("listDocs", poService.findAllPlainPoDocsbyPoId(poId));

		List<UomPojo> uomList = uomService.getAllUomPojo(SecurityLibrary.getLoggedInUserTenantId());
		model.addAttribute("uomList", uomList);

		// Po users for Approval
		List<UserPojo> userList = userService.fetchAllUsersForTenant(SecurityLibrary.getLoggedInUserTenantId(), "",
				null);

		List<PoApprovalUser> approvalUserList = new ArrayList<PoApprovalUser>();

		if (CollectionUtil.isNotEmpty(po.getApprovals())) {
			for (PoApproval approval : po.getApprovals()) {
				if (CollectionUtil.isNotEmpty(approval.getApprovalUsers())) {
					for (PoApprovalUser approvalUser : approval.getApprovalUsers()) {
						User user = new User(approvalUser.getUser().getId(), approvalUser.getUser().getLoginId(),
								approvalUser.getUser().getName(), approvalUser.getUser().getCommunicationEmail(), approvalUser.getUser().getEmailNotifications(),
								approvalUser.getUser().getTenantId(), approvalUser.getUser().isDeleted());

						//PH-4113 exclude disabled po creator as approver case 1.1 page 11
						String poCreator = po.getCreatedBy().getId();

						if (!approvalUser.getUser().getId().equals(poCreator)) {
							if (!approvalUserList.contains(new PoApprovalUser(user))) {
								approvalUserList.add(new PoApprovalUser(user));
							}
						}
					}
				}
			}
		}
		for (UserPojo userPojo : userList) {
			User user = new User(userPojo.getId(), userPojo.getLoginId(), userPojo.getName(),
					userPojo.getCommunicationEmail(), userPojo.isEmailNotifications(), userPojo.getTenantId(), userPojo.isDeleted());
			if (!approvalUserList.contains(new PoApprovalUser(user))) {
				approvalUserList.add(new PoApprovalUser(user));
			}
		}

		model.addAttribute("userList", approvalUserList);
		model.addAttribute("userList1", userList);

		model.addAttribute("poPermissions", poPermissions);
		model.addAttribute("comments", po.getComments());
		model.addAttribute("isPoOwner", poPermissions.isOwner());

		return po;
	}

	@RequestMapping(path = "/createPoDetails/{prId}", method = RequestMethod.POST)
	public ModelAndView savePo(@PathVariable String prId, @ModelAttribute Po po, Model model, RedirectAttributes redir, boolean isDraft, HttpSession session) {

		LOG.info("create PO Details POST called and getId :" + po.getId() + "isDraft " + isDraft);
		LOG.info("Po model attribute: {}", po);

		List<User> approvalUsers = new ArrayList<User>();

		List<UserPojo> appUserListSumm = userService.fetchAllUsersForTenant(SecurityLibrary.getLoggedInUserTenantId(),
				"", null);
		for (UserPojo user : appUserListSumm) {
			User u = new User(user.getId(), user.getLoginId(), user.getName(), user.getCommunicationEmail(), user.isEmailNotifications(),
					user.getTenantId(), user.isDeleted());
			if (!approvalUsers.contains(u)) {
				approvalUsers.add(u);
			}
		}

		if (po.getApprovals() != null) {
			for (PoApproval app : po.getApprovals()) {
				if (app.getApprovalUsers() != null) {
					for (PoApprovalUser appU : app.getApprovalUsers()) {
						if (!approvalUsers.contains(appU.getUser())) {
							approvalUsers.add(appU.getUser());
						}
					}
				}
			}
		}

		model.addAttribute("userList", approvalUsers);

		try {
			if (CollectionUtil.isNotEmpty(po.getApprovals())) {
				int level = 1;
				for (PoApproval app : po.getApprovals()) {
					app.setPo(po);
					app.setLevel(level++);
					if (app != null && CollectionUtil.isNotEmpty(app.getApprovalUsers())) {
						for (PoApprovalUser approvalUser : app.getApprovalUsers()) {
							approvalUser.setApproval(app);
							approvalUser.setId(null);
						}
					} else {
						LOG.warn(" >>>>>>>>>>>>>>>>>>>> Approval User is empty.");
					}

				}
			} else {
				LOG.warn("Approval levels is empty.");
			}
			Po persistObj = poService.getLoadedPoById(po.getId());
			if (StringUtils.checkString(po.getId()).length() > 0) {
				LOG.info("Po update:      " + po.getId());
				if (po.getPaymentTermes() != null) {
					PaymentTermes paymentTermes = paymentTermesService.getPaymentTermesById(po.getPaymentTermes().getId());
					persistObj.setPaymentTerm(paymentTermes.getPaymentTermCode() + " - " + paymentTermes.getDescription());
					persistObj.setPaymentTermDays(paymentTermes.getPaymentDays());
					persistObj.setPaymentTermes(paymentTermes);
				} else {

					persistObj.setPaymentTerm(po.getPaymentTerm());
				}
				persistObj.setEnableApprovalRoute(po.getEnableApprovalRoute());
				persistObj.setEnableApprovalReminder(po.getEnableApprovalReminder());
				persistObj.setReminderAfterHour(po.getReminderAfterHour());
				persistObj.setReminderCount(po.getReminderCount());
				persistObj.setNotifyEventOwner(po.getNotifyEventOwner());
				persistObj.setApprovals(po.getApprovals());
				persistObj.setDeliveryAddress(po.getDeliveryAddress());
				persistObj.setAdditionalTax(po.getAdditionalTax());
				persistObj.setTaxDescription(po.getTaxDescription());
                persistObj.setReferenceNumber(po.getReferenceNumber());
                persistObj.setName(po.getName());
                persistObj.setDescription(po.getDescription());
                persistObj.setRequester(po.getRequester());
                persistObj.setCorrespondenceAddress(po.getCorrespondenceAddress());
                persistObj.setCurrency(po.getCurrency());
                persistObj.setDecimal(po.getDecimal());

				persistObj = poService.updatePo(persistObj);
				redir.addFlashAttribute("success", messageSource.getMessage("common.success.savedraft", new Object[]{persistObj.getPoNumber()}, Global.LOCALE));
				return new ModelAndView("redirect:/buyer/buyerDashboard");
			}
		} catch (Exception e) {
			LOG.error("Error while Saving Po : " + e.getMessage(), e);
			model.addAttribute("errors",
					"Error while saving Po details for : " + po.getName() + ", message : " + e.getMessage());
			return new ModelAndView("redirect:poView/" + po.getId());
		}

		return new ModelAndView("redirect:/buyer/buyerDashboard");
	}

	@RequestMapping(path = "/savePoDraft", method = RequestMethod.POST)
	public ModelAndView savePoDraft(@ModelAttribute Po po, Model model, RedirectAttributes redir, HttpSession session) {
		savePo(null, po, model, redir, Boolean.TRUE, session);
		return new ModelAndView("redirect:/buyer/buyerDashboard");
	}

	@RequestMapping(path = "/poSummary/{poId}", method = RequestMethod.GET)
	public String poSummary(@PathVariable String poId, @RequestParam(required = false) String prId, Model model, RedirectAttributes redir) {
		LOG.info("getting po view by po id :" + poId);
		try {
			Po po = poService.getLoadedPoById(poId);
			if (po != null) {
				LOG.info("getting po view by po id :" + po.getStatus());
				LOG.info("getting po view by po id :" + PoStatus.DRAFT);
				if (po.getStatus() == PoStatus.DRAFT) {
					return "redirect:/buyer/poSummaryView/" + po.getId() + "?prId=" + prId;
				} else {
					return "redirect:/buyer/poView/" + po.getId() + "?prId=" + prId;
				}

			} else {
				model.addAttribute("error", "No PO found for PR");
				return "redirect:/buyer/poView/" + poId + "?prId=" + prId;
			}
		} catch (Exception e) {
			LOG.error("Error in view Po :" + e.getMessage(), e);
			model.addAttribute("error", messageSource.getMessage("posummary.error.view.po",
					new Object[]{e.getMessage()}, Global.LOCALE));
			return "redirect:/buyer/poView/" + poId + "?prId=" + prId;
		}
	}

	@RequestMapping(path = "/poFinish/{poId}", method = RequestMethod.GET)
	public ModelAndView poFinish(@PathVariable String poId, @RequestParam(required = false) String prId, Model model, RedirectAttributes redir, HttpSession session) throws NotAuthorizedException {
		LOG.info("po finish called...");
		Po po = poService.getLoadedPoById(poId);
		if (Boolean.TRUE == po.getEnableApprovalReminder()) {
			if (po.getReminderAfterHour() == null) {
				model.addAttribute("error", messageSource.getMessage("approval.reminder.add.hour", new Object[]{}, Global.LOCALE));
				constructPoSummaryAttributes(poId, model);
				return new ModelAndView("poSummary");
			}
			if (po.getReminderCount() == null) {
				model.addAttribute("error", messageSource.getMessage("approval.reminder.count.reminder", new Object[]{}, Global.LOCALE));
				constructPoSummaryAttributes(poId, model);
				return new ModelAndView("poSummary");
			}
		}

		try {
			TimeZone timeZone = TimeZone.getDefault();
			String strTimeZone = (String) session.getAttribute(Global.SESSION_TIME_ZONE_KEY);
			if (strTimeZone != null) {
				timeZone = TimeZone.getTimeZone(strTimeZone);
			}
			SimpleDateFormat dateTimeFormat = new SimpleDateFormat("dd/MM/YYYY hh:mm a");
			dateTimeFormat.setTimeZone(timeZone);
			model.addAttribute("eventPermissions", poService.getUserPemissionsForPo(SecurityLibrary.getLoggedInUser().getId(), poId));

			//PH-4113
			//do approval push finish button from draft
			beforePoExecution(po);
			approvalService.doPoFinishApprovalFromDraft(po, SecurityLibrary.getLoggedInUser());
			afterPoExecution(po);
			//send email notification for po creator and asociate owner
			poService.sendPoFinishMail(po, "draft");

			redir.addFlashAttribute("success", messageSource.getMessage("po.finish.success", new Object[]{po.getName()}, Global.LOCALE));

		} catch (Exception e) {
			LOG.error("Error While Finish :" + e.getMessage(), e);
			model.addAttribute("error", e.getMessage());
			return new ModelAndView("poSummary");
		}
		return new ModelAndView("redirect:/buyer/buyerDashboard");
	}

	public Po constructPoSummaryAttributes(String poId, Model model) throws NotAuthorizedException {
		Integer invoiceCounnt = 0, doConunt, grnConunt;
		Po po = poService.getLoadedPoById(poId);

		Boolean isLoggedInUserApprover = false;

		for (PoApproval approval : po.getApprovals()) {
			for (PoApprovalUser approvalUser : approval.getApprovalUsers()) {
				approvalUser.getRemarks();
				approvalUser.getUser().getLoginId();

				User loggedInUser = SecurityLibrary.getLoggedInUser();
				if (loggedInUser.getLoginId().equals(approvalUser.getUser().getLoginId())) {
					isLoggedInUserApprover = true;
				}
				LOG.info("approvalUser" + approvalUser.getUser().getLoginId());
			}
		}

		List<String> buUser = businessUnitService.getBusinessUnitIdByUserId(SecurityLibrary.getLoggedInUser().getId());
		boolean poCreator = false;
		boolean bussUnitUser = false;
		poCreator = SecurityLibrary.getLoggedInUser().getLoginId().equals(po.getCreatedBy().getLoginId());

		if (null != buUser && null != po.getBusinessUnit().getId() && buUser.contains(po.getBusinessUnit().getId())) {
			if(poCreator != true ){
				LOG.info("The business unit matches!");
				bussUnitUser = true;
			}
		} else {
			LOG.info("The business unit does not match.");
			bussUnitUser = false;
		}

		model.addAttribute("isLoggedInUserApprover", isLoggedInUserApprover);
		model.addAttribute("bussUnitUser", bussUnitUser);
		po.setDeliveryTime(po.getDeliveryDate());

		EventPermissions eventPermissions = poService.getUserPemissionsForPo(SecurityLibrary.getLoggedInUser().getId(), po.getId());

		LOG.info("user as >>>>>>>>>>>>>>>>" + eventPermissions.isOwner());
		if (!checkPermissionToAllow(eventPermissions)) {
			//4113 case same BU but any userType did not pre set should allow view detail PO
			throw new NotAuthorizedException("Not Authorized");
		}

		po.setInvoiceCount((int) invoiceService.findTotalBuyerInvoiceForPo(poId));
		po.setDoCount((int) deliveryOrderService.findTotalDoForBuyerPo(poId));
		po.setGrnReceivedOrDraftCount((int) goodsReceiptNoteService.findTotalDraftOrReceivedGrnForPo(poId));

		invoiceCounnt = (int) invoiceService.findTotalBuyerInvoiceByPoId(poId);
		doConunt = (int) deliveryOrderService.findTotalDoForBuyerPoById(poId);
		grnConunt = (int) goodsReceiptNoteService.findTotalDraftOrReceivedGrnForPoByPoId(poId);

		model.addAttribute("po", po);
		List<PoItem> poItemlist = poService.findAllPoItemByPoIdForSummary(poId);
		model.addAttribute("poItemlist", poItemlist);
		LOG.info("poItemlist >>>>>>>>>>>>>>>> " + poItemlist.toString());

		model.addAttribute("ownerSettings", ownerSettingsService.getOwnersettings());
		List<PoAudit> poAuditList = poAuditService.getPoAuditByPoIdForBuyer(po.getId());
		model.addAttribute("poAuditList", poAuditList);
		LOG.info("poAuditList >>>>>>>>>>>>>>>> " + poAuditList.toString());

		String paymentTerm = po.getPaymentTerm();
		model.addAttribute("paymentTerm", paymentTerm);
		LOG.info("paymentTerm >>>>>>>>>>>>>>>> " + paymentTerm);


		boolean isAutoPublish = buyerSettingsService.isAutoPublishePoSettingsByTenantId(SecurityLibrary.getLoggedInUserTenantId());
		model.addAttribute("isAutoPublishPo", isAutoPublish);
		LOG.info("isAutoPublishPo >>>>>>>>>>>>>>>> " + isAutoPublish);

		List<DoSupplierPojo> dos = deliveryOrderService.getDosByPoIdForBuyer(poId);
		model.addAttribute("dos", dos);
		LOG.info("dos >>>>>>>>>>>>>>>> " + dos.size());

		List<InvoiceSupplierPojo> invoices = invoiceService.getInvoicesByPoIdForBuyer(poId);
		model.addAttribute("invoices", invoices);
		LOG.info("invoices >>>>>>>>>>>>>>>> " + invoices.size());

		List<GoodsReceiptNotePojo> grnList = goodsReceiptNoteService.getGrnListByPoIdForBuyer(poId);
		model.addAttribute("grnList", grnList);
		LOG.info("grnList >>>>>>>>>>>>>>>> " + grnList.size());

		model.addAttribute("addressList", buyerAddressService.getAllBuyerAddressPojo(SecurityLibrary.getLoggedInUserTenantId()));
		model.addAttribute("listDocs", poService.findAllPlainPoDocsbyPoId(poId));

		List<UomPojo> uomList = uomService.getAllUomPojo(SecurityLibrary.getLoggedInUserTenantId());
		model.addAttribute("uomList", uomList);
		LOG.info("uomList >>>>>>>>>>>>>>>> " + uomList.size());

		List<PoTeamMember> poTeamMembers = po.getPoTeamMembers();
		model.addAttribute("poTeamMembers", poTeamMembers);
		LOG.info("PO Team Member List >>>>>>>>>>>>>>>> " + poTeamMembers.size());

		boolean isCreator = false;
		boolean isMember = false;
		User loggedInUser = SecurityLibrary.getLoggedInUser();

		isCreator = loggedInUser.getLoginId().equals(po.getCreatedBy().getLoginId());

		for (PoTeamMember teamMember : po.getPoTeamMembers()) {
			isMember = loggedInUser.getLoginId().equals(teamMember.getUser().getLoginId());

			if (isMember) {
				break;
			}
		}

		boolean isCreatorAndMember = false;

		if (isCreator || isMember) {
			isCreatorAndMember = true;
		}

		model.addAttribute("isCreatorAndMember", isCreatorAndMember);

		// Po users for Approval
		List<UserPojo> userList = userService.fetchAllUsersForTenant(SecurityLibrary.getLoggedInUserTenantId(), "", null);

		List<PoApprovalUser> approvalUserList = new ArrayList<PoApprovalUser>();

		if (CollectionUtil.isNotEmpty(po.getApprovals())) {
			for (PoApproval approval : po.getApprovals()) {
				if (CollectionUtil.isNotEmpty(approval.getApprovalUsers())) {
					for (PoApprovalUser approvalUser : approval.getApprovalUsers()) {
						User user = new User(approvalUser.getUser().getId(), approvalUser.getUser().getLoginId(),
								approvalUser.getUser().getName(), approvalUser.getUser().getCommunicationEmail(), approvalUser.getUser().getEmailNotifications(),
								approvalUser.getUser().getTenantId(), approvalUser.getUser().isDeleted());
						if (!approvalUserList.contains(new PoApprovalUser(user))) {
							approvalUserList.add(new PoApprovalUser(user));
						}
					}
				}
			}
		}
		for (UserPojo userPojo : userList) {
			User user = new User(userPojo.getId(), userPojo.getLoginId(), userPojo.getName(),
					userPojo.getCommunicationEmail(), userPojo.isEmailNotifications(), userPojo.getTenantId(), userPojo.isDeleted());
			if (!approvalUserList.contains(new PoApprovalUser(user))) {
				approvalUserList.add(new PoApprovalUser(user));
			}
		}

		model.addAttribute("userList", approvalUserList);
		model.addAttribute("userList1", userList);
		model.addAttribute("userId", SecurityLibrary.getLoggedInUser().getId());
		model.addAttribute("userCreator", po.getCreatedBy().getId());
		model.addAttribute("eventPermissions", eventPermissions);
		model.addAttribute("comments", po.getComments());
		model.addAttribute("doInvoiceGrnCount", doConunt + invoiceCounnt + grnConunt);

		List<EventSupplierPojo> favouriteSupplierList = null;
		favouriteSupplierList = favoriteSupplierService.searchPrSuppliers(SecurityLibrary.getLoggedInUserTenantId(), false, null, null);
		LOG.info("favouriteSupplierList >>>>>>>>>>>>>>>> " + favouriteSupplierList.size());
		model.addAttribute("supplierList", favouriteSupplierList);

		// PH 4113 SHOW PO Event for mailbox
		model.addAttribute("eventType", "PO");
		List<PoEvent> poEvents = poEventService.findEventByPoId(poId);
		PoEvent poEvent = new PoEvent();

		if (poEvents.isEmpty()) {
			poEvent.setEventName(po.getName());
			poEvent.setCreatedBy(SecurityLibrary.getLoggedInUser());
			poEvent.setTenantId(SecurityLibrary.getLoggedInUserTenantId());
			poEvent.setCreatedDate(new Date());
			poEvent.setEventOwner(SecurityLibrary.getLoggedInUser());
			poEvent.setEnableApprovalReminder(Boolean.FALSE);
			poEvent.setNotifyEventOwner(Boolean.TRUE);
			poEvent.setStatus(EventStatus.ACTIVE);
			poEvent.setPo(po);
			poEvent = poEventService.save(poEvent); // save and retrieve the saved object
			poEvents = Collections.singletonList(poEvent); // create a list with the saved object
		} else {
			poEvent = poEvents.get(0);
		}

		if (poEvents.size() > 0) {
			model.addAttribute("poEvent", poEvents.get(0));
		} else {
			model.addAttribute("poEvent", null);
		}

		LOG.info("Po Event LIST >>>>>>>>>>>>>>>> " + poEvents.size());
		LOG.info("Po Event Id : " + poEvents.get(0).getId());
		LOG.info("Tenant Id : " + SecurityLibrary.getLoggedInUserTenantId());
		LOG.info("User LoggedIn Id : " + SecurityLibrary.getLoggedInUser().getId());

		List<PoTeamMember> teamMember = poService.getPlainTeamMembersForPoSummary(poId);

		StringBuilder memberId = new StringBuilder();
		StringBuilder memberName = new StringBuilder();
		teamMember.forEach(teamMemberData -> {
			if (!teamMemberData.getUser().getId().equals(po.getCreatedBy().getId())) {
				memberId.append("," + teamMemberData.getUser().getId());
				memberName.append(", " + teamMemberData.getUser().getName() + " [ " + teamMemberData.getUser().getBuyer().getCompanyName() + " ] ");
			}
		});
		model.addAttribute("memberId", memberId);
		model.addAttribute("memberName", memberName);

		//PH 4113 SHOW APPROVAL OR NOT

		try {
			// Check if po is not null and if getPr() returns a non-null value
			if (po != null && po.getPr() != null) {
				String prId = po.getPr().getId(); // Get the PR ID

				if (prId != null) { // Check if PR ID is not null
					Pr pr = prService.getLoadedPrById(prId); // Fetch the Pr object using the ID
					//do not un comment this by edwin
					//setDefaultPrCreatorAsPoTeamMember(po,pr);

					if (pr != null) { // Check if the Pr object is not null
						// Check if the template and its ID are not null before logging

						if (pr.getTemplate() != null && pr.getTemplate().getId() != null) {
							LOG.info("PR TEMPLATE >>>>>>>>>> " + pr.getTemplate().getId());
							model.addAttribute("prTemplate", pr.getTemplate()); // Add the template to the model
						} else {
							LOG.warn("Template or Template ID is null for PR ID: " + prId);
						}
					} else {
						LOG.warn("Purchase Requisition not found for ID: " + prId);
					}
				} else {
					LOG.warn("PR ID is null for the provided Purchase Order.");
				}
			} else {
				LOG.warn("Purchase Order or its PR is null.");
			}

		} catch (Exception e) {
			// This is PO integration
			model.addAttribute("prTemplate", null);
			LOG.error("Error While fetching PR Template: " + e.getMessage(), e);
		}
		LOG.info("PO Constructor is completed >>>>>>>>>> ");
		return po;
	}


	@RequestMapping(path = "/updatePoApproval/{prId}", method = RequestMethod.POST)
	public ModelAndView updatePoApproval(@PathVariable String prId, @ModelAttribute Po po, RedirectAttributes redir) {
		try {
			po = poService.updatePoApproval(po, SecurityLibrary.getLoggedInUser());
			redir.addFlashAttribute("success", messageSource.getMessage("flashsuccess.approval.updated", new Object[]{}, Global.LOCALE));
		} catch (Exception e) {
			LOG.error("Error while Updating Po Approval :" + e.getMessage(), e);
			redir.addFlashAttribute("error", messageSource.getMessage("flasherror.while.updating.approval", new Object[]{e.getMessage()}, Global.LOCALE));
		}

		if (prId != null) {
			return new ModelAndView("redirect:/buyer/poView/" + po.getId() + "?prId=" + prId);
		} else {
			return new ModelAndView("redirect:/buyer/poView/" + po.getId());
		}
	}

	/**
	 * @param eventPermissions
	 * @return
	 */
	private boolean checkPermissionToAllow(EventPermissions eventPermissions) {
		boolean allow = false;
		if (eventPermissions.isOwner() || eventPermissions.isEditor() || eventPermissions.isApprover() || eventPermissions.isEvaluator() || eventPermissions.isLeadEvaluator() || eventPermissions.isOpener() || eventPermissions.isViewer()) {
			allow = true;
		}
		if (SecurityLibrary.ifAnyGranted("ROLE_ADMIN,ROLE_ADMIN_READONLY")) {
			allow = true;
		}
		return allow;
	}

	@RequestMapping(path = "/exportPoItemTemplate/{poId}", method = RequestMethod.GET)
	public void downloader(@PathVariable String poId, HttpServletRequest request, HttpServletResponse response) {
		LOG.info("Download poItemTemplate... ");
		try {
			Po po = poService.findPoById(poId);
			String downloadFolder = context.getRealPath("/WEB-INF/");
			String fileName = "poItemTemplate.xlsx";
			Path file = Paths.get(downloadFolder, fileName);
			List<PoItem> poList = poService.findAllPoItemByPoId(poId);
			XSSFWorkbook workbook = new XSSFWorkbook();
			XSSFSheet sheet = workbook.createSheet("PO Item List");

			// For Financial Standard
			DecimalFormat df = null;
			if (po.getDecimal().equals("1")) {
				df = new DecimalFormat("#,###,###,##0.0");
			} else if (po.getDecimal().equals("2")) {
				df = new DecimalFormat("#,###,###,##0.00");
			} else if (po.getDecimal().equals("3")) {
				df = new DecimalFormat("#,###,###,##0.000");
			} else if (po.getDecimal().equals("4")) {
				df = new DecimalFormat("#,###,###,##0.0000");
			} else if (po.getDecimal().equals("5")) {
				df = new DecimalFormat("#,###,###,##0.00000");
			} else if (po.getDecimal().equals("6")) {
				df = new DecimalFormat("#,###,###,##0.000000");
			} else {
				df = new DecimalFormat("#,###,###,##0.00");
			}

			// Style of Heading Cells
			CellStyle styleHeading = workbook.createCellStyle();
			Font font = workbook.createFont();
			font.setBoldweight(XSSFFont.BOLDWEIGHT_BOLD);
			styleHeading.setFont(font);
			styleHeading.setAlignment(CellStyle.ALIGN_CENTER);

			CellStyle styleRight = workbook.createCellStyle();
			styleRight.setAlignment(CellStyle.ALIGN_RIGHT);
			// Creating Headings
			Row rowHeading = sheet.createRow(0);
			int i = 0;

			if (!PoStatus.DRAFT.equals(po.getStatus())) {
				for (String column : Global.PO_EXCEL_COLUMNS) {
					Cell cell = rowHeading.createCell(i++);
					if (column.equals("UNIT PRICE")) {
						cell.setCellValue(column + "(" + po.getCurrency().getCurrencyCode() + ")");
					} else {
						cell.setCellValue(column);
					}
					cell.setCellStyle(styleHeading);
				}
			} else {
				for (String column : Global.PO_EXCEL_COLUMNS_IN_DRAFT) {
					Cell cell = rowHeading.createCell(i++);
					if (column.equals("UNIT PRICE")) {
						cell.setCellValue(column + "(" + po.getCurrency().getCurrencyCode() + ")");
					} else {
						cell.setCellValue(column);
					}
					cell.setCellStyle(styleHeading);
				}
			}

			if (po.getField1Label() != null) {
				Cell cell = rowHeading.createCell(i++);
				cell.setCellValue(po.getField1Label());
				cell.setCellStyle(styleHeading);
			}
			if (po.getField2Label() != null) {
				Cell cell = rowHeading.createCell(i++);
				cell.setCellValue(po.getField2Label());
				cell.setCellStyle(styleHeading);
			}
			if (po.getField3Label() != null) {
				Cell cell = rowHeading.createCell(i++);
				cell.setCellValue(po.getField3Label());
				cell.setCellStyle(styleHeading);
			}
			if (po.getField4Label() != null) {
				Cell cell = rowHeading.createCell(i++);
				cell.setCellValue(po.getField4Label());
				cell.setCellStyle(styleHeading);
			}

			if (po.getField5Label() != null) {
				Cell cell = rowHeading.createCell(i++);
				cell.setCellValue(po.getField5Label());
				cell.setCellStyle(styleHeading);
			}
			if (po.getField6Label() != null) {
				Cell cell = rowHeading.createCell(i++);
				cell.setCellValue(po.getField6Label());
				cell.setCellStyle(styleHeading);
			}
			if (po.getField7Label() != null) {
				Cell cell = rowHeading.createCell(i++);
				cell.setCellValue(po.getField7Label());
				cell.setCellStyle(styleHeading);
			}
			if (po.getField8Label() != null) {
				Cell cell = rowHeading.createCell(i++);
				cell.setCellValue(po.getField8Label());
				cell.setCellStyle(styleHeading);
			}

			if (po.getField9Label() != null) {
				Cell cell = rowHeading.createCell(i++);
				cell.setCellValue(po.getField9Label());
				cell.setCellStyle(styleHeading);
			}
			if (po.getField10Label() != null) {
				Cell cell = rowHeading.createCell(i++);
				cell.setCellValue(po.getField10Label());
				cell.setCellStyle(styleHeading);
			}

			Cell cell = rowHeading.createCell(i++);
			cell.setCellValue("TOTAL_AMOUNT(" + po.getCurrency().getCurrencyCode() + ")");
			cell.setCellStyle(styleHeading);

			cell = rowHeading.createCell(i++);
			cell.setCellValue("TAX_AMOUNT(" + po.getCurrency().getCurrencyCode() + ")");
			cell.setCellStyle(styleHeading);

			cell = rowHeading.createCell(i++);
			cell.setCellValue("TOTAL_AMOUNT_WITH_TAX(" + po.getCurrency().getCurrencyCode() + ")");
			cell.setCellStyle(styleHeading);

			int cellNumber = 0;
			int r = 1;
			// Write Data into Excel
			for (PoItem item : poList) {
				Row row = sheet.createRow(r++);
				int cellNum = 0;
				row.createCell(cellNum++).setCellValue(item.getLevel() + "." + item.getOrder());
				row.createCell(cellNum++).setCellValue(item.getItemName());
				row.createCell(cellNum++)
						.setCellValue(item.getItemDescription() != null ? item.getItemDescription() : "");
				row.createCell(cellNum++)
						.setCellValue(item.getProduct() != null
								? (item.getProduct().getUom() != null ? item.getProduct().getUom().getUom() : "")
								: (item.getUnit() != null ? item.getUnit().getUom() : ""));
				int colNum = 6;
				if (StringUtils.checkString(po.getField1Label()).length() > 0 && po.getField1Label() != null)
					colNum++;
				if (StringUtils.checkString(po.getField2Label()).length() > 0 && po.getField2Label() != null)
					colNum++;
				if (StringUtils.checkString(po.getField3Label()).length() > 0 && po.getField3Label() != null)
					colNum++;
				if (StringUtils.checkString(po.getField4Label()).length() > 0 && po.getField4Label() != null)
					colNum++;
				if (StringUtils.checkString(po.getField5Label()).length() > 0 && po.getField5Label() != null)
					colNum++;
				if (StringUtils.checkString(po.getField6Label()).length() > 0 && po.getField6Label() != null)
					colNum++;
				if (StringUtils.checkString(po.getField7Label()).length() > 0 && po.getField7Label() != null)
					colNum++;
				if (StringUtils.checkString(po.getField8Label()).length() > 0 && po.getField8Label() != null)
					colNum++;
				if (StringUtils.checkString(po.getField9Label()).length() > 0 && po.getField9Label() != null)
					colNum++;
				if (StringUtils.checkString(po.getField10Label()).length() > 0 && po.getField10Label() != null)
					colNum++;

				sheet.addMergedRegion(new CellRangeAddress(r - 1, r - 1, cellNum - 1, colNum + 3));
				cellNumber = colNum + 4;
				if (CollectionUtil.isNotEmpty(item.getChildren())) {
					for (PoItem children : item.getChildren()) {
						Row childrow = sheet.createRow(r++);
						int childCellNum = 0;
						childrow.createCell(childCellNum++)
								.setCellValue(children.getLevel() + "." + children.getOrder());
						childrow.createCell(childCellNum++).setCellValue(
								children.getItemName() != null ? children.getItemName() : "");
						childrow.createCell(childCellNum++).setCellValue(
								children.getItemDescription() != null ? children.getItemDescription() : "");
						childrow.createCell(childCellNum++).setCellValue(children.getProduct() != null
								? (children.getProduct().getUom() != null ? children.getProduct().getUom().getUom()
								: "")
								: (children.getUnit() != null ? children.getUnit().getUom() : ""));
						childrow.createCell(childCellNum++).setCellValue(
								children.getQuantity() != null ? String.valueOf(children.getQuantity()) : "");

						//PH-4133 Locked Quantity and Balance Quantity
						if (!PoStatus.DRAFT.equals(po.getStatus())) {
							childrow.createCell(childCellNum++).setCellValue(
									children.getLockedQuantity() != null ? String.valueOf(children.getLockedQuantity()) : "");

							childrow.createCell(childCellNum++).setCellValue(
									children.getBalanceQuantity() != null ? String.valueOf(children.getBalanceQuantity()) : "");
						}
						//END

						childrow.createCell(childCellNum++).setCellValue(
								children.getUnitPrice() != null ? df.format(children.getUnitPrice()) : "");

						//PH-4133 Price per Unit
						childrow.createCell(childCellNum++).setCellValue(
								children.getPricePerUnit() != null ? df.format(children.getPricePerUnit()) : "");
						//END

						childrow.createCell(childCellNum++)
								.setCellValue(children.getItemTax() != null ? children.getItemTax() : "");
						if (StringUtils.checkString(po.getField1Label()).length() > 0 && po.getField1Label() != null)
							childrow.createCell(childCellNum++)
									.setCellValue(children.getField1() != null ? children.getField1() : "");
						if (StringUtils.checkString(po.getField2Label()).length() > 0 && po.getField2Label() != null)
							childrow.createCell(childCellNum++)
									.setCellValue(children.getField2() != null ? children.getField2() : "");
						if (StringUtils.checkString(po.getField3Label()).length() > 0 && po.getField3Label() != null)
							childrow.createCell(childCellNum++)
									.setCellValue(children.getField3() != null ? children.getField3() : "");
						if (StringUtils.checkString(po.getField4Label()).length() > 0 && po.getField4Label() != null)
							childrow.createCell(childCellNum++)
									.setCellValue(children.getField4() != null ? children.getField4() : "");

						if (StringUtils.checkString(po.getField5Label()).length() > 0 && po.getField5Label() != null)
							childrow.createCell(childCellNum++)
									.setCellValue(children.getField5() != null ? children.getField5() : "");
						if (StringUtils.checkString(po.getField6Label()).length() > 0 && po.getField6Label() != null)
							childrow.createCell(childCellNum++)
									.setCellValue(children.getField6() != null ? children.getField6() : "");
						if (StringUtils.checkString(po.getField7Label()).length() > 0 && po.getField7Label() != null)
							childrow.createCell(childCellNum++)
									.setCellValue(children.getField7() != null ? children.getField7() : "");
						if (StringUtils.checkString(po.getField8Label()).length() > 0 && po.getField8Label() != null)
							childrow.createCell(childCellNum++)
									.setCellValue(children.getField8() != null ? children.getField8() : "");
						if (StringUtils.checkString(po.getField9Label()).length() > 0 && po.getField9Label() != null)
							childrow.createCell(childCellNum++)
									.setCellValue(children.getField9() != null ? children.getField9() : "");
						if (StringUtils.checkString(po.getField10Label()).length() > 0 && po.getField10Label() != null)
							childrow.createCell(childCellNum++)
									.setCellValue(children.getField10() != null ? children.getField10() : "");

						childrow.createCell(childCellNum++)
								.setCellValue(children.getPricePerUnit() != null ?
										df.format(children.getUnitPrice().divide(children.getPricePerUnit(), RoundingMode.HALF_UP).multiply(children.getQuantity()))
										: df.format(0));

						childrow.createCell(childCellNum++).setCellValue(
								children.getTaxAmount() != null ? df.format(children.getTaxAmount()) : df.format(0));
						childrow.createCell(childCellNum++)
								.setCellValue(children.getTotalAmountWithTax() != null
										? df.format(children.getTotalAmountWithTax())
										: df.format(0));
						cellNumber = childCellNum;
					}
				}
			}
			r++;
			Row row = sheet.createRow(r++);
			row.createCell(cellNumber - 2).setCellValue("Total(" + po.getCurrency().getCurrencyCode() + ")");
			row.createCell(cellNumber - 1).setCellValue(df.format(po.getTotal()));
			if (po.getTaxDescription() != null) {
				row = sheet.createRow(r++);
				row.createCell(cellNumber - 4).setCellValue("Additional Charges");

				Cell CellTaxDescription = row.createCell(cellNumber - 3);
				CellTaxDescription.setCellValue(po.getTaxDescription());

				row.createCell(cellNumber - 2).setCellValue("(" + po.getCurrency().getCurrencyCode() + ")");

				//PH-4133 RIGHT JUSTIFY
				Cell CellAdditionalTax = row.createCell(cellNumber - 1);
				CellAdditionalTax.setCellValue(df.format(po.getAdditionalTax()));
				CellAdditionalTax.setCellStyle(styleRight);

			}
			row = sheet.createRow(r++);
			Cell grandTotalCell = row.createCell(cellNumber - 2);
			grandTotalCell.setCellValue("Grand Total(" + po.getCurrency().getCurrencyCode() + ")");
			grandTotalCell.setCellStyle(styleHeading);
			grandTotalCell = row.createCell(cellNumber - 1);
			grandTotalCell.setCellValue(df.format(po.getGrandTotal()));
			grandTotalCell.setCellStyle(styleHeading);

			// Auto Fit
			for (int k = 0; k < 21; k++) {
				sheet.autoSizeColumn(k, true);
			}

			// Save Excel File
			FileOutputStream out = new FileOutputStream(downloadFolder + "/" + fileName);
			workbook.write(out);
			out.close();
			LOG.info("Successfully written in Excel");

			if (Files.exists(file)) {
				response.setContentType("application/vnd.ms-excel");
				response.addHeader("Content-Disposition", "attachment; filename=" + fileName);
				try {
					Files.copy(file, response.getOutputStream());
					response.getOutputStream().flush();
				} catch (IOException e) {
					LOG.error("Error :- " + e.getMessage(), e);
				}
			}
		} catch (Exception e) {
			LOG.error("Error while downloading PO items  Excel : " + e.getMessage(), e);
		}
	}

	@RequestMapping(path = "/createPo", method = RequestMethod.POST)
	public String createPoManually(@RequestParam("prId") String prId, RedirectAttributes redir) {
		String poId = null;
		try {
			LOG.info("Creating PO manually for pr Id :" + prId + " ==User Name :"
					+ SecurityLibrary.getLoggedInUser().getName());
			if (StringUtils.checkString(prId).length() > 0) {
				Pr pr = prService.getLoadedPrById(prId);

				if (pr != null) {
					Po po = prService.createPo(SecurityLibrary.getLoggedInUser(), pr);
					if (po != null && StringUtils.checkString(po.getId()).length() > 0) {
						LOG.info("po created succefully:" + po.getId());
						poId = po.getId();
						po = poService.getLoadedPoById(po.getId());
						pr.setPoNumber(po.getPoNumber());
						pr.setPoCreatedDate(new Date());
						pr.setIsPo(Boolean.TRUE);
						pr.setStatus(PrStatus.COMPLETE);
						prService.updatePr(pr);
						try {
							approvalService.sendPoCreatedEmailToCreater(SecurityLibrary.getLoggedInUser(), pr,
									SecurityLibrary.getLoggedInUser());
							if (po.getStatus() == PoStatus.ORDERED && po.getSupplier() != null) {
								approvalService.sendPoReceivedEmailNotificationToSupplier(po,
										SecurityLibrary.getLoggedInUser());
							}
						} catch (Exception e) {
							LOG.error("Error while sending received PO notification to supplier:" + e.getMessage(), e);
						}

						redir.addFlashAttribute("success", messageSource.getMessage("success.po.created",
								new Object[]{po.getPoNumber()}, Global.LOCALE));
					}
				}
			}

			return "redirect:poCreate/" + poId + "?prId=" + prId;
		} catch (Exception e) {
			LOG.error("Error while creating po manually :" + e.getMessage(), e);
			redir.addFlashAttribute("error", "Error while creating PO:" + e.getMessage());
			return "redirect:prView/" + prId;

		}

	}


	@RequestMapping(path = "/sendPo", method = RequestMethod.POST)
	public String sendPo(@RequestParam("poId") String poId, @RequestParam(required = false) String prId, RedirectAttributes redir) {
		try {
			LOG.info("Send PO poId :" + poId + " ==User Name :" + SecurityLibrary.getLoggedInUser().getName());
			String buyerTimeZone = "GMT+8:00";

			Po po = poService.findById(poId);
			Boolean revised = po.getRevised();
			poService.updatePoStatus(poId, PoStatus.ORDERED, "", SecurityLibrary.getLoggedInUser(), null);
			po = poService.getLoadedPoById(poId);

			try {
				BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.ORDERED,
						"PO '" + po.getPoNumber() + "' sent to Supplier " + po.getSupplierName(),
						SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser(), new Date(),
						ModuleType.PO);
				buyerAuditTrailDao.save(buyerAuditTrail);
			} catch (Exception e) {
				LOG.error("Error while recording auction event Resume " + e.getMessage(), e);
			}

			LOG.info("PO " + po.getPoNumber() + " sent succefully to supplier");

			if (po.getSupplier() != null && Boolean.FALSE == po.getRevised()) {
				try {
					approvalService.sendPoReceivedEmailNotificationToSupplier(po, SecurityLibrary.getLoggedInUser());
				} catch (Exception e) {
					LOG.info("Error while sending cancellation PO notification to supplier:" + e.getMessage(), e);
				}
			}

			if (po.getSupplier() != null && Boolean.TRUE == revised) {
				try {
					approvalService.sendRevisedPoReceivedEmailNotification(po, po.getSupplier().getSupplier(),
							buyerTimeZone, SecurityLibrary.getLoggedInUser());
				} catch (Exception e) {
					LOG.info("Error while sending Order PO notification to supplier:" + e.getMessage(), e);
				}
			}

			redir.addFlashAttribute("success",
					messageSource.getMessage("success.po.ordered", new Object[]{po.getPoNumber()}, Global.LOCALE));
		} catch (Exception e) {
			LOG.error("Error While sending PO to supplier :" + e.getMessage(), e);
			redir.addFlashAttribute("error", messageSource.getMessage("flasherror.while.ordered.po",
					new Object[]{e.getMessage()}, Global.LOCALE));
		}
		return "redirect:poView/" + poId + "?prId=" + prId;
	}

	public void beforePoExecution(Po po){
		LOG.info("PO before Execution >>>>>>>>>>>>>>>>>>>>");
		LOG.info("PO STATUS : "+po.getStatus());
		LOG.info("PO IS_CANCELLED : "+po.getStatus());
		LOG.info("PO IS_REVISE : "+po.getStatus());
		LOG.info("PO LAST_STATUS : "+po.getOldStatus());

	}

	public void afterPoExecution(Po po){
		LOG.info("PO after Execution >>>>>>>>>>>>>>>>>>>>");
		LOG.info("PO STATUS : "+po.getStatus());
		LOG.info("PO IS_CANCELLED : "+po.getStatus());
		LOG.info("PO IS_REVISE : "+po.getStatus());
		LOG.info("PO LAST_STATUS : "+po.getOldStatus());
	}
	@RequestMapping(path = "/cancelPo", method = RequestMethod.POST)
	public String cancelPo(@RequestParam("poId") String poId, @RequestParam(required = false) String prId, @RequestParam(required = false) String remarks,
						   RedirectAttributes redir) {
		try {

			LOG.info("Cancel PO poId :" + poId + " ==User Name :" + SecurityLibrary.getLoggedInUser().getName() + " ==remarks : " + remarks);

			Po po = poService.getLoadedPoById(poId);

			beforePoExecution(po);
			poService.handlePoAction(poId, PoStatus.CANCELLED, remarks, SecurityLibrary.getLoggedInUser(), null);
			afterPoExecution(po);

			redir.addFlashAttribute("success",
					messageSource.getMessage("success.po.cancel", new Object[]{po.getPoNumber()}, Global.LOCALE));

		} catch (Exception e) {
			LOG.error("Error While Cancel PO :" + e.getMessage(), e);
			redir.addFlashAttribute("error", messageSource.getMessage("flasherror.while.cancel.po",
					new Object[]{e.getMessage()}, Global.LOCALE));
		}
		return "redirect:poView/" + poId + "?prId=" + prId;
	}

	public String getMessageBannerForCancellation(Po po){
		String message ="PO updated succesfully";

		if(po.getApprovals().size() > 0){
			if(PoStatus.DRAFT.equals(po.getStatus())){
				message = "PO \""+po.getPoNumber()+"\" cancelled successfully.";
			}
			if(PoStatus.SUSPENDED.equals(po.getStatus())){
				message = "PO \""+po.getPoNumber()+"\" finish to cancel.";
			}
		}else{
			message = "PO \""+po.getPoNumber()+"\" cancelled successfully.";
		}
		return message;
	}


	@RequestMapping(path = "/poReports", method = RequestMethod.POST)
	public void downloadpoReports(HttpSession session, HttpServletRequest request, HttpServletResponse response,
								  @RequestParam(required = false) String poIds, boolean select_all, @RequestParam String dateTimeRange,
								  @ModelAttribute("searchFilterPoPojo") SearchFilterPoPojo searchFilterPoPojo) {
		try {
			String pOArr[] = null;
			if (StringUtils.checkString(poIds).length() > 0) {
				pOArr = poIds.split(",");
			}
			TableDataInput input = new TableDataInput();
			input.setStart(0);
			input.setLength(5000);

			String tenantId = SecurityLibrary.getLoggedInUserTenantId();

			LOG.info("dateTimeRange :" + dateTimeRange);
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

			poService.downloadBuyerPoReports(tenantId, pOArr, response, session, select_all, startDate, endDate,
					searchFilterPoPojo, SecurityLibrary.ifAnyGranted("ROLE_ADMIN,ROLE_ADMIN_READONLY") ? null
							: SecurityLibrary.getLoggedInUser().getId());
		} catch (Exception e) {
			LOG.error("Error While Filter po list :" + e.getMessage(), e);
		}

	}

	@RequestMapping(path = "/downloadPoReport/{poId}", method = RequestMethod.GET)
	public void generatePoReport(@PathVariable("poId") String poId, HttpServletResponse response, HttpSession session)
			throws Exception {
		try {
			LOG.info(" PO REPORT : " + poId);
			Po po = poService.getLoadedPoById(poId);
			try {
				LOG.info("--------------------------BEFORE AUDIT---------------------------------------");
				BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.DOWNLOAD,
						"PO '" + po.getPoNumber() + "' downloaded.", SecurityLibrary.getLoggedInUser().getTenantId(),
						SecurityLibrary.getLoggedInUser(), new Date(), ModuleType.PO);
				buyerAuditTrailDao.save(buyerAuditTrail);
				LOG.info("--------------------------AFTER AUDIT---------------------------------------");
			} catch (Exception e) {
				LOG.error("Error while recording auction event Resume " + e.getMessage(), e);
			}
			poService.generatePoReport(response, po);
			try {
				PoAudit poAudit = new PoAudit();
				poAudit.setAction(PoAuditType.DOWNLOADED);
				poAudit.setActionBy(SecurityLibrary.getLoggedInUser());
				poAudit.setActionDate(new Date());
				poAudit.setBuyer(SecurityLibrary.getLoggedInUser().getBuyer());
				if (po.getSupplier() != null && po.getSupplier().getSupplier() != null) {
					poAudit.setSupplier(po.getSupplier().getSupplier());
				}
				poAudit.setDescription(messageSource.getMessage("po.audit.downloadPo",
						new Object[]{po.getPoNumber()}, Global.LOCALE));
				poAudit.setVisibilityType(PoAuditVisibilityType.BUYER);
				poAudit.setPo(po);
				poAuditService.save(poAudit);

			} catch (Exception e) {
				LOG.error("Error while saving po audit:" + e.getMessage(), e);
			}
		} catch (Exception e) {
			LOG.error("Could not generate PR Summary Report. " + e.getMessage(), e);
		}
	}

	@RequestMapping(path = "/exportPoSummaryCsvReport", method = RequestMethod.POST)
	public void downloadUomCsvReport(HttpSession session, HttpServletRequest request, HttpServletResponse response,
									 RedirectAttributes redir, @ModelAttribute("searchFilterPoPojo") SearchFilterPoPojo searchFilterPoPojo,
									 boolean select_all, @RequestParam(required = false) String status, @RequestParam(required = false) String poType, @RequestParam String dateTimeRange) throws IOException {
		try {
			File file = File.createTempFile("poReports-", ".csv");
			LOG.info("Temp file : " + file.getAbsolutePath());
			String poIds[] = null;
			if (StringUtils.checkString(searchFilterPoPojo.getPoIds()).length() > 0) {
				poIds = searchFilterPoPojo.getPoIds().split(",");
			}

			LOG.info("dateTimeRange :" + dateTimeRange);
			String startDate = null;
			String endDate = null;
			Date sDate = null;
			Date eDate = null;

			if (StringUtils.checkString(dateTimeRange).length() > 0) {
				String dateTimeArr[] = dateTimeRange.split("-");
				if (dateTimeArr.length == 2) {
					startDate = dateTimeArr[0].trim();
					endDate = dateTimeArr[1].trim();

					try {
						// Use the correct date format for parsing
						SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
						sDate = dateFormat.parse(startDate);
						eDate = dateFormat.parse(endDate);
						LOG.info("Parsed Start date: " + sDate + " Parsed End date: " + eDate);

					} catch (ParseException e) {
						LOG.error("Invalid date format: " + e.getMessage());
						HttpHeaders headers = new HttpHeaders();
						headers.add("error", "Invalid date format. Expected format is MM/dd/yyyy.");
					}

				} else {
					LOG.warn("Invalid dateTimeRange format: {}", dateTimeRange);
					HttpHeaders headers = new HttpHeaders();
					headers.add("error", "Invalid dateTimeRange format. Expected format is 'startDate-endDate'.");
				}
			}

			poService.downloadCsvFileForPoSummary(response, file, poIds, searchFilterPoPojo, sDate, eDate,
					select_all, SecurityLibrary.getLoggedInUserTenantId(),
					SecurityLibrary.ifAnyGranted("ROLE_ADMIN,ROLE_ADMIN_READONLY") && !poType.equals("me") ? null
							: SecurityLibrary.getLoggedInUser().getId(),
					session, poType, status);
			try {
				BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.DOWNLOAD,
						"PO Report is successfully downloaded", SecurityLibrary.getLoggedInUserTenantId(),
						SecurityLibrary.getLoggedInUser(), new Date(), ModuleType.PO);
				buyerAuditTrailDao.save(buyerAuditTrail);
			} catch (Exception e) {
				LOG.info("Error to create audit trails message");
			}

			// Send to UI
			response.setContentType("text/csv");
			response.addHeader("Content-Disposition", "attachment; filename=poReport.csv");
			response.setContentLengthLong(file.length());
			Files.copy(file.toPath(), response.getOutputStream());
			response.getOutputStream().flush();

		} catch (Exception e) {
			LOG.error("Error while downloading csv file : " + e.getMessage(), e);
			redir.addFlashAttribute("error", messageSource.getMessage("PoSummary.error.download.csv",
					new Object[]{e.getMessage()}, Global.LOCALE));

		}
	}

	@RequestMapping(path = "/revisePo", method = RequestMethod.POST)
	public String revisePo(@RequestParam("poId") String poId, @RequestParam(required = false) String prId,
						   @RequestParam(required = false) String reviseJustification, RedirectAttributes redir, HttpSession session) {
		try {
			LOG.info("Revise PO poId :" + poId + " == User Name :" + SecurityLibrary.getLoggedInUser().getName());

			poService.handlePoAction(poId, PoStatus.REVISE, reviseJustification, SecurityLibrary.getLoggedInUser(), null);
			Po po = poService.getLoadedPoById(poId);

			poService.sendPoFinishMail(po, "revise");

			poService.deletePoReportByPoId(poId, SecurityLibrary.getLoggedInUserTenantId());

			LOG.info("PO " + po.getPoNumber() + " Revised succefully ");

			redir.addFlashAttribute("success",
					messageSource.getMessage("success.po.revised", new Object[]{po.getPoNumber()}, Global.LOCALE));
		} catch (Exception e) {
			LOG.error("Error While revising PO :" + e.getMessage(), e);
			redir.addFlashAttribute("error", messageSource.getMessage("flasherror.while.revising.po",
					new Object[]{e.getMessage()}, Global.LOCALE));
		}
		return "redirect:poView/" + poId + "?prId=" + prId;
	}

	//PH-4113
	@RequestMapping(path = "/revisePo/{poId}", method = RequestMethod.GET)
	public String revisePoFromSuspended(@PathVariable("poId") String poId, @RequestParam(required = false) String prId,
										@RequestParam(required = false) String reviseJustification, RedirectAttributes redir, HttpSession session) {
		//PH-4113
		try {
			LOG.info("Revise PO (Press Finish button) From Suspended poId :" + poId + " == User Name :" + SecurityLibrary.getLoggedInUser().getName());
			poService.handlePoAction(poId, PoStatus.REVISE, reviseJustification, SecurityLibrary.getLoggedInUser(), null);
			Po po = poService.getLoadedPoById(poId);

			try {
				//send email to PO team member and PO creator
				poService.sendPoFinishMail(po, "revise");
				//do approval push finish button from suspend (po revision)
				approvalService.doPoFinishApprovalFromSuspend(po, SecurityLibrary.getLoggedInUser());
				redir.addFlashAttribute("success", messageSource.getMessage("po.finish.success", new Object[]{po.getName()}, Global.LOCALE));

			} catch (Exception e) {
				LOG.error("Error While Sending notification PO Revision Finish :" + e.getMessage(), e);
			}

			poService.deletePoReportByPoId(poId, SecurityLibrary.getLoggedInUserTenantId());
			LOG.info("PO " + po.getPoNumber() + " Revised succefully ");
			redir.addFlashAttribute("success",
					messageSource.getMessage("success.po.revised", new Object[]{po.getPoNumber()}, Global.LOCALE));
		} catch (Exception e) {
			LOG.error("Error While revising PO :" + e.getMessage(), e);
			redir.addFlashAttribute("error", messageSource.getMessage("flasherror.while.revising.po",
					new Object[]{e.getMessage()}, Global.LOCALE));
		}
		return "redirect:/buyer/poView/" + poId + "?prId=" + prId;
	}

	@RequestMapping(path = "/suspendPo", method = RequestMethod.POST)
	public String suspendPo(@RequestParam("poId") String poId, @RequestParam(required = false) String prId,
							@RequestParam(required = false) String remarks, RedirectAttributes redir, HttpSession session) {
		try {
			LOG.info("Suspend PO poId :" + poId + " == User Name :" + SecurityLibrary.getLoggedInUser().getName()+"== remarks :"+remarks);

			Po po = poService.getLoadedPoById(poId);
			beforePoExecution(po);
			poService.handlePoAction(poId, PoStatus.SUSPENDED, remarks, SecurityLibrary.getLoggedInUser(), null);
			afterPoExecution(po);

			// Send mail to supplier
			if (po.getSupplier() != null) {
				User supUser = new User();
				supUser.setCommunicationEmail(po.getSupplier().getSupplier().getCommunicationEmail());
				User u = userService.getDetailsOfLoggedinUser(po.getSupplier().getSupplier().getLoginEmail());
				supUser.setEmailNotifications(u != null ? u.getEmailNotifications() : Boolean.TRUE);
				supUser.setName(po.getSupplier().getSupplier().getCompanyName());
				supUser.setTenantId(po.getSupplier().getSupplier().getId());
				supUser.setTenantType(TenantType.SUPPLIER);
				//send email notification to supplier
				poService.sendPoEmailNotificationToSupplier(supUser, po, SecurityLibrary.getLoggedInUser(), "suspended");
			}

			//send email notification to po creator / po AO
			poService.sendSuspendPoEmailNotification(po.getCreatedBy(), po, SecurityLibrary.getLoggedInUser());

			poService.deletePoReportByPoId(poId, SecurityLibrary.getLoggedInUserTenantId());
			LOG.info("PO " + po.getPoNumber() + " Suspended succefully ");

			redir.addFlashAttribute("success",
					messageSource.getMessage("success.po.suspended", new Object[]{po.getPoNumber()}, Global.LOCALE));
		} catch (Exception e) {
			LOG.error("Error While revising PO :" + e.getMessage(), e);
			redir.addFlashAttribute("error", messageSource.getMessage("flasherror.while.revising.po",
					new Object[]{e.getMessage()}, Global.LOCALE));
		}
		if (prId != null && !prId.isEmpty()) {
			return "redirect:poCreate/" + poId + "?prId=" + prId;
		} else {
			return "redirect:poCreate/" + poId;
		}

	}

	@RequestMapping(path = "/editPoItem", method = RequestMethod.GET)
	public ResponseEntity<PoItem> editPoItem(@RequestParam String poId, @RequestParam String poItemId) {
		LOG.info("editPoItem get called :: poId" + poId + " :: prItemId :: " + poItemId);
		HttpHeaders headers = new HttpHeaders();
		Uom uom = null;
		try {
			PoItem poItem = null;
			poItem = poService.getPoItembyPoIdAndPoItemId(poId, poItemId);
			if (poItem != null && poItem.getUnit() != null) {
				uom = poItem.getUnit().createShallow();
			}
			if (poItem != null) {
				if (poItem.getProductContractItem() != null) {
					LOG.info("=======" + poItem.getProductContractItem().getId());
					poItem.setId(poItemId + "-" + poItem.getProductContractItem().getId());
					poItem.setProductContractItem(null);
				}
				poItem = poItem.createShallowCopy();
				if (poItem.getProductCategory() != null) {
					poItem.getProductCategory().setBuyer(null);
					poItem.getProductCategory().setModifiedBy(null);
					poItem.getProductCategory().setCreatedBy(null);
				}
				poItem.setParent(null);
				poItem.setBuyer(null);
				poItem.getPo().setCurrency(null);
				poItem.getPo().setSupplier(null);
				poItem.getPo().setCreatedBy(null);
				poItem.getPo().setCostCenter(null);
				poItem.getPo().setBusinessUnit(null);
				poItem.getPo().setModifiedBy(null);
				poItem.getPo().setPr(null);

				Po tempPo = poService.getLoadedPoById(poItem.getPo().getId());
				poItem.getPo().setCorrespondenceAddress(tempPo.getCorrespondenceAddress().createShallow());
				poItem.getPo().setDeliveryAddress(tempPo.getDeliveryAddress().createShallow());

				poItem.getPo().setPoItems(null);
				poItem.getPo().setActionBy(null);
				poItem.getPo().setOrderedBy(null);
				if (poItem.getProduct() != null) {
					poItem.getProduct().setCreatedBy(null);
					poItem.getProduct().setModifiedBy(null);
				}
				if (uom != null) {
					poItem.setUnit(uom);
				}

				return new ResponseEntity<PoItem>(poItem, HttpStatus.OK);
			} else {
				LOG.warn("The PO Item for the specified Po not found. Bad Request.");
				headers.add("error",
						messageSource.getMessage("prItem.edit.not.found.error", new Object[]{}, Global.LOCALE));
				return new ResponseEntity<PoItem>(poItem, headers, HttpStatus.INTERNAL_SERVER_ERROR);
			}
		} catch (Exception e) {
			LOG.error("Error edit items : " + e.getMessage(), e);
			headers.add("error",
					messageSource.getMessage("prItem.edit.error", new Object[]{e.getMessage()}, Global.LOCALE));
			return new ResponseEntity<PoItem>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(value = "/updatePoItem", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<PoItem> updatePoItem(@RequestBody PoItem poItem) {
		LOG.info(" Po Id " + poItem.getId() + " update poItem getParent " + poItem.getParent());
		HttpHeaders headers = new HttpHeaders();
		String rr[] = null;
		if (poItem.getId().contains("-")) {
			rr = poItem.getId().split("-");
			poItem.setItemId(rr[0]);
			LOG.info("========================>" + rr[0]);
			LOG.info("========================>" + rr[1]);
			poItem.setId(rr[0]);
		}
		try {
			Po po = poService.getLoadedPoById(poItem.getPo().getId());

			poItem.setItemTax(
					poItem != null ? (poItem.getItemTax() != null ? poItem.getItemTax().replaceAll("\\,", "") : "")
							: "");

			poService.updatePoItem(poItem);
			headers.add("success", messageSource.getMessage("prItem.update.success",
					new Object[]{poItem.getItemName()}, Global.LOCALE));
			return new ResponseEntity<PoItem>(poItem, headers, HttpStatus.OK);
		} catch (Exception e) {
			LOG.error("Error while updating Po Items to Buyer : " + e.getMessage(), e);
			headers.add("error",
					messageSource.getMessage("prItem.update.error", new Object[]{e.getMessage()}, Global.LOCALE));
			return new ResponseEntity<PoItem>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(path = "/po/{poId}/delete/{poItemId}", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<String> deletePrItems(
			@PathVariable(name = "poItemId", required = true) String poItemId,
			@PathVariable(name = "poId", required = true) String poId) throws JsonProcessingException {
		LOG.info("DELETE Po Item itm id :: " + poItemId + " Po Id :: " + poId);
		HttpHeaders headers = new HttpHeaders();
		try {
			poService.deletePoItemByItemIdAndPoId(poItemId, poId);
			LOG.info(">>>>>>>>>>>>>>>>> ");
			headers.add("success", messageSource.getMessage("prItem.delete.success", new Object[]{}, Global.LOCALE));
			LOG.info("* ---------- contrl --------------------");
			return new ResponseEntity<String>("{ \"status\" : \"Item deleted successfully\"}", headers, HttpStatus.OK);
		} catch (Exception e) {
			LOG.error("Error deleting items : " + e.getMessage(), e);
			headers.add("error",
					messageSource.getMessage("prItem.delete.error", new Object[]{e.getMessage()}, Global.LOCALE));
			return new ResponseEntity<String>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(path = "/submitPo", method = RequestMethod.POST)
	public ModelAndView submitPo(@ModelAttribute Po po, Model model, BindingResult result, RedirectAttributes redir,
								 HttpSession session) {
		LOG.info(" Submitting Po " + po.getId() + " Status : " + po.getStatus());
		List<User> approvalUsers = new ArrayList<User>();
		JRSwapFileVirtualizer virtualizer = null;
		virtualizer = new JRSwapFileVirtualizer(100, new JRSwapFile(System.getProperty("java.io.tmpdir"), 2048, 1024),
				false);
		String auditMsg = "";

		List<UserPojo> appUserListSumm = userService.fetchAllUsersForTenant(SecurityLibrary.getLoggedInUserTenantId(),
				"", null);
		for (UserPojo user : appUserListSumm) {
			User u = new User(user.getId(), user.getLoginId(), user.getName(), user.getCommunicationEmail(), user.isEmailNotifications(),
					user.getTenantId(), user.isDeleted());
			if (!approvalUsers.contains(u)) {
				approvalUsers.add(u);
			}
		}
		if (Boolean.compare(po.getEnableApprovalRoute(), true) == 0 && po.getApprovals() == null) {
			model.addAttribute("error", "Approvals cannot be empty");
			redir.addAttribute("error", "Approvals cannot be empty");
			return new ModelAndView("redirect:poView/" + po.getId());
		}

		if (po.getApprovals() != null) {
			for (PoApproval app : po.getApprovals()) {
				for (PoApprovalUser appU : app.getApprovalUsers()) {
					if (!approvalUsers.contains(appU.getUser())) {
						approvalUsers.add(appU.getUser());
					}
				}
			}
		}

		model.addAttribute("userList", approvalUsers);

		if (result.hasErrors()) {
			LOG.error("Page submitted With Errors ............................. ");
			List<String> errMessages = new ArrayList<String>();
			for (ObjectError err : result.getAllErrors()) {
				errMessages.add(err.getDefaultMessage());
			}
			return new ModelAndView("redirect:poView/" + po.getId());
		} else {
			LOG.info("Page submitted with no errors ....................................... ");
			try {

				if (Boolean.TRUE == po.getEnableApprovalRoute()) {
					if (CollectionUtil.isNotEmpty(po.getApprovals())) {
						int level = 1;
						for (PoApproval app : po.getApprovals()) {
							app.setPo(po);
							app.setLevel(level++);
							if (app != null && CollectionUtil.isNotEmpty(app.getApprovalUsers()))
								for (PoApprovalUser approvalUser : app.getApprovalUsers()) {
									approvalUser.setApproval(app);
									approvalUser.setId(null);
								}
						}
					} else {
						LOG.warn("Approval levels is empty.");
					}
				}

				TimeZone timeZone = TimeZone.getDefault();
				String strTimeZone = (String) session.getAttribute(Global.SESSION_TIME_ZONE_KEY);
				if (strTimeZone != null) {
					timeZone = TimeZone.getTimeZone(strTimeZone);
				}

				Date deliveryDateTime = null;
				if (po.getDeliveryDate() != null && po.getDeliveryTime() != null) {
					deliveryDateTime = DateUtil.combineDateTime(po.getDeliveryDate(), po.getDeliveryTime(), timeZone);
				}
				LOG.info("DeliveryDateTime  : " + deliveryDateTime);
				po.setDeliveryDate(deliveryDateTime);

				po = poService.updatePoDetails(po, virtualizer, SecurityLibrary.getLoggedInUser());

				auditMsg = messageSource.getMessage("po.audit.finished", new Object[]{po.getPoNumber()},
						Global.LOCALE);

				try {
					snapShotAuditService.doPoAudit(po, SecurityLibrary.getLoggedInUser(), PoAuditType.FINISH, auditMsg,
							virtualizer, PoAuditVisibilityType.BUYER);
				} catch (Exception e) {
					LOG.info("Error while auditing " + e.getMessage(), e);
				}

			} catch (Exception e) {
				LOG.error("Error while storing Po : " + e.getMessage(), e);
				model.addAttribute("errors",
						"Error while submiting Po details for : " + po.getName() + ", message : " + e.getMessage());
				return new ModelAndView("redirect:poView/" + po.getId());
			}
			return new ModelAndView("redirect:poView/" + po.getId());
		}
	}

	@RequestMapping(path = "/savePoDetails", method = RequestMethod.POST)
	public ModelAndView savePo(@ModelAttribute Po po, Model model, BindingResult result, RedirectAttributes redir,
							   HttpSession session) {
		LOG.info(" Saving Po details : " + po.getId());
		List<User> approvalUsers = new ArrayList<User>();


		String prId = "";
		if (po.getPr() != null) {
			prId = po.getPr().getId();
		}

		List<UserPojo> appUserListSumm = userService.fetchAllUsersForTenant(SecurityLibrary.getLoggedInUserTenantId(),
				"", null);
		for (UserPojo user : appUserListSumm) {
			User u = new User(user.getId(), user.getLoginId(), user.getName(), user.getCommunicationEmail(), user.isEmailNotifications(),
					user.getTenantId(), user.isDeleted());
			if (!approvalUsers.contains(u)) {
				approvalUsers.add(u);
			}
		}
		if (po.getApprovals() != null) {
			for (PoApproval app : po.getApprovals()) {
				for (PoApprovalUser appU : app.getApprovalUsers()) {
					if (!approvalUsers.contains(appU.getUser())) {
						approvalUsers.add(appU.getUser());
					}
				}
			}
		}
		model.addAttribute("userList", approvalUsers);

		try {

			if (CollectionUtil.isNotEmpty(po.getApprovals())) {
				int level = 1;
				for (PoApproval app : po.getApprovals()) {
					app.setPo(po);
					app.setLevel(level++);
					if (app != null && CollectionUtil.isNotEmpty(app.getApprovalUsers()))
						for (PoApprovalUser approvalUser : app.getApprovalUsers()) {
							approvalUser.setApproval(app);
							approvalUser.setId(null);
						}
				}
			} else {
				LOG.warn("Approval levels is empty.");
			}
			Po persistObj = poService.getLoadedPoById(po.getId());
			if (StringUtils.checkString(po.getId()).length() > 0) {
				LOG.info("Po update:      " + po.getId());

				TimeZone timeZone = TimeZone.getDefault();
				String strTimeZone = (String) session.getAttribute(Global.SESSION_TIME_ZONE_KEY);
				if (strTimeZone != null) {
					timeZone = TimeZone.getTimeZone(strTimeZone);
				}

				Date deliveryDateTime = null;
				if (po.getDeliveryDate() != null && po.getDeliveryTime() != null) {
					deliveryDateTime = DateUtil.combineDateTime(po.getDeliveryDate(), po.getDeliveryTime(), timeZone);
				}
				LOG.info("DeliveryDateTime >>>>>>>>>>>>>>>>>>>  : " + deliveryDateTime);

				persistObj.setEnableApprovalRoute(po.getEnableApprovalRoute());
				persistObj.setEnableApprovalReminder(po.getEnableApprovalReminder());
				persistObj.setReminderAfterHour(po.getReminderAfterHour());
				persistObj.setReminderCount(po.getReminderCount());
				persistObj.setNotifyEventOwner(po.getNotifyEventOwner());
				persistObj.setApprovals(po.getApprovals());
				persistObj.setDeliveryAddress(po.getDeliveryAddress());
				persistObj.setDeliveryReceiver(po.getDeliveryReceiver());
				persistObj.setDeliveryDate(deliveryDateTime);
				persistObj.setAdditionalTax(po.getAdditionalTax());
				persistObj.setTaxDescription(po.getTaxDescription());

				persistObj = poService.updatePo(persistObj);

			}
		} catch (Exception e) {
			LOG.error("Error while Saving Po : " + e.getMessage(), e);
			model.addAttribute("errors",
					"Error while saving Po details for : " + po.getName() + ", message : " + e.getMessage());
			return new ModelAndView("redirect:poView/" + po.getId());
		}
		return new ModelAndView("redirect:poView/" + po.getId() + "?prId=" + prId);
	}

	@RequestMapping(path = "/approvePo", method = RequestMethod.POST)
	public String poApproved(@RequestParam String id, @RequestParam String remarks, RedirectAttributes redir) {
		LOG.info("Po Approved GET called po id :" + id + " remarks :" + remarks + " SecurityLibrary.getLoggedInUser() :"
				+ SecurityLibrary.getLoggedInUser().getCommunicationEmail());
		try {
			Po po = new Po();
			po.setId(id);

			beforePoExecution(po);
			Po persistPo = approvalService.doApproval(po, SecurityLibrary.getLoggedInUser(), remarks, true);
			afterPoExecution(po);

			redir.addFlashAttribute("success", messageSource.getMessage("po.audit.approve", new Object[]{
							(StringUtils.checkString(persistPo.getPoNumber()).length() > 0 ? persistPo.getPoNumber() : " ")},
					Global.LOCALE));
		} catch (Exception e) {
			LOG.error("Error while approving po :" + e.getMessage(), e);
			redir.addFlashAttribute("error", e.getMessage());
		}
		return "redirect:/buyer/buyerDashboard";
	}

	@RequestMapping(path = "/rejectPo", method = RequestMethod.POST)
	public String poRejected(@RequestParam String id, @RequestParam(required = false) String prId, @RequestParam String remarks, RedirectAttributes redir) {
		LOG.info("Po Rejected GET called po id :" + id + " remarks :" + remarks);
		try {
			if (StringUtils.checkString(remarks).length() == 0) {
				redir.addFlashAttribute("error",
						messageSource.getMessage("flasherror.remark.cannot.empty", new Object[]{}, Global.LOCALE));
				return "redirect:poView/" + id + "?prId=" + prId;
			}
			Po po = new Po();
			po.setId(id);
			beforePoExecution(po);
			Po poReject = approvalService.doApproval(po, SecurityLibrary.getLoggedInUser(), remarks, false);
			afterPoExecution(po);
			redir.addFlashAttribute("success", messageSource.getMessage("flashsuccess.po.rejected", new Object[]{
							(StringUtils.checkString(poReject.getPoNumber()).length() > 0 ? poReject.getPoNumber() : " ")},
					Global.LOCALE));
		} catch (Exception e) {
			LOG.error("Error while rejecting po :" + e.getMessage(), e);
			redir.addFlashAttribute("error", messageSource.getMessage("flasherror.while.rejecting.po",
					new Object[]{e.getMessage()}, Global.LOCALE));
		}
		return "redirect:/buyer/buyerDashboard";
	}

	@RequestMapping(value = "/downloadPoAuditFile/{id}/{action}/{actionDate}", method = RequestMethod.GET)
	public void downloadFile(@PathVariable String id, @PathVariable String action, @PathVariable String actionDate,
							 HttpServletResponse response) throws IOException {
		try {
			response.setContentType("application/pdf");
			response.setHeader("Content-Disposition",
					"attachment; filename=\"" + action + "_Audit_" + actionDate + ".pdf\"");

			PoAudit auditFile = poAuditService.getPoAuditById(id);
			if (auditFile.getSnapshot() != null && auditFile.getSnapshot().length > 0) {
				response.setContentLength(auditFile.getSnapshot().length);
				FileCopyUtils.copy(auditFile.getSnapshot(), response.getOutputStream());
			}

			response.flushBuffer();
			response.setStatus(HttpServletResponse.SC_OK);
		} catch (Exception e) {
			LOG.error("Error while File downloaded Po Audit File : " + e.getMessage(), e);
		}
	}

	@RequestMapping(path = "/updatePoAdditionalTax", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<List<BigDecimal>> updatePoAdditionalTax(
			@RequestParam("additionalTax") String additionalTax, @RequestParam("taxDescription") String taxDescription,
			@RequestParam("poId") String poId) {
		LOG.info("additionalTax : " + additionalTax + " poId :" + poId + "taxDescription :" + taxDescription);
		HttpHeaders headers = new HttpHeaders();
		Po po = null;
		List<BigDecimal> list = new ArrayList<>();
		try {
			po = poService.findPoById(poId);
			po.setTaxDescription(taxDescription);
			try {
				additionalTax = additionalTax.replace(",", "");
				po.setAdditionalTax(new BigDecimal(additionalTax));
				po.setGrandTotal(po.getTotal().add(new BigDecimal(additionalTax)));
			} catch (Exception e) {
				throw new NotAllowedException("Additional tax \"" + additionalTax + "\" should be numbers only");
			}
			if (CollectionUtil.isNotEmpty(poService.validatePo(po, Po.PoPurchaseItem.class))) {
				String message = ", ";
				for (String errMessage : poService.validatePo(po, Po.PoPurchaseItem.class)) {
					message = errMessage + message;
				}
				LOG.error("message :" + message);
				headers.add("error",
						messageSource.getMessage("--------", new Object[]{}, "Error :" + message, Global.LOCALE));
				return new ResponseEntity<List<BigDecimal>>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
			}
			po = poService.updatePo(po);
			list.add(po.getGrandTotal());
			list.add(new BigDecimal(po.getDecimal()));
		} catch (Exception e) {
			LOG.error("ERROR WHile updating addtax :" + e.getMessage(), e);
			headers.add("error",
					messageSource.getMessage("--------", new Object[]{}, "Error :" + e.getMessage(), Global.LOCALE));
			return new ResponseEntity<List<BigDecimal>>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<List<BigDecimal>>(list, headers, HttpStatus.OK);
	}

	@RequestMapping(value = "/poItemMultipleDelete", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<String> prMultipleApproved(
			@RequestParam(value = "poItemIds[]") String[] poItemIds, @RequestParam(value = "poId") String poId,
			RedirectAttributes redir, Model model) {
		LOG.info("Delete called for po item ids :" + String.join(",", poItemIds) + " PoId :" + poId);
		HttpHeaders headers = new HttpHeaders();
		String msg = "";
		try {

			Long count = poService.findItemCountByPrItemIds(poItemIds, poId);
			if (count == 0l) {
				headers.add("error", messageSource.getMessage("pr.item.one.required", new Object[]{}, Global.LOCALE));
				return new ResponseEntity<String>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
			}
			poService.deletePoItems(poItemIds, poId);
			headers.add("success",
					messageSource.getMessage("prItem.delete.success", new Object[]{0}, Global.LOCALE));
			return new ResponseEntity<String>(msg, headers, HttpStatus.OK);

		} catch (Exception e) {
			LOG.error("Error while deleting PoItems : " + e.getMessage(), e);
			headers.add("error", "Error While deleting items");
			return new ResponseEntity<String>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(path = "/exportPoItemSummaryCsvReport", method = RequestMethod.POST)
	public void downloadPoItemCsvReport(HttpSession session, HttpServletRequest request, @RequestParam(required = false) String poType, @RequestParam(required = false) String status, HttpServletResponse response, RedirectAttributes redir, @ModelAttribute("searchFilterPoPojo") SearchFilterPoPojo searchFilterPoPojo, boolean select_all, @RequestParam String dateTimeRange) throws IOException {
		try {
			File file = File.createTempFile("poReports-", ".csv");
			LOG.info("Temp file : " + file.getAbsolutePath());

			String poIds[] = null;
			if (StringUtils.checkString(searchFilterPoPojo.getPoIds()).length() > 0) {
				poIds = searchFilterPoPojo.getPoIds().split(",");
			}

			LOG.info("dateTimeRange :" + dateTimeRange);
			String startDate = null;
			String endDate = null;
			Date sDate = null;
			Date eDate = null;

			if (StringUtils.checkString(dateTimeRange).length() > 0) {
				String dateTimeArr[] = dateTimeRange.split("-");
				if (dateTimeArr.length == 2) {
					startDate = dateTimeArr[0].trim();
					endDate = dateTimeArr[1].trim();

					try {
						// Use the correct date format for parsing
						SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
						sDate = dateFormat.parse(startDate);
						eDate = dateFormat.parse(endDate);
						LOG.info("Parsed Start date: " + sDate + " Parsed End date: " + eDate);

					} catch (ParseException e) {
						LOG.error("Invalid date format: " + e.getMessage());
						HttpHeaders headers = new HttpHeaders();
						headers.add("error", "Invalid date format. Expected format is MM/dd/yyyy.");
					}

				} else {
					LOG.warn("Invalid dateTimeRange format: {}", dateTimeRange);
					HttpHeaders headers = new HttpHeaders();
					headers.add("error", "Invalid dateTimeRange format. Expected format is 'startDate-endDate'.");
				}
			}

			poService.downloadCsvFileForPoItemSummary(response, file, poIds, searchFilterPoPojo, sDate, eDate, select_all, SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.ifAnyGranted("ROLE_ADMIN,ROLE_ADMIN_READONLY") && !poType.equals("me") ? null : SecurityLibrary.getLoggedInUser().getId(), session, poType, status);
			try {

				try {
					BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.DOWNLOAD,
							"PO Item Report is successfully downloaded",
							SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser(), new Date(),
							ModuleType.PO);
					buyerAuditTrailDao.save(buyerAuditTrail);


				} catch (Exception e) {
					LOG.error("Error while saving Buyer audit " + e.getMessage(), e);
				}


			} catch (Exception e) {
				LOG.info("Error to create audit trails message");
			}

			// Send to UI
			response.setContentType("text/csv");
			response.addHeader("Content-Disposition", "attachment; filename=poItemReport.csv");
			response.setContentLengthLong(file.length());
			Files.copy(file.toPath(), response.getOutputStream());
			response.getOutputStream().flush();

		} catch (Exception e) {
			LOG.error("Error while downloading csv file : " + e.getMessage(), e);
			redir.addFlashAttribute("error", messageSource.getMessage("PoSummary.error.download.csv", new Object[]{e.getMessage()}, Global.LOCALE));

		}
	}

	@RequestMapping(path = "/savePoDraft/{poId}", method = RequestMethod.GET)
	public ModelAndView savePoDraft(@PathVariable String poId, RedirectAttributes redir) {
		LOG.info("po savePoDraft called..");
		try {
			if (StringUtils.checkString(poId).length() > 0) {
				Po persistObj = poService.getLoadedPoById(poId);
				persistObj.setStatus(PoStatus.DRAFT);
				poService.updatePo(persistObj);
				LOG.info("po status Updated succesfully");


				redir.addFlashAttribute("success", messageSource.getMessage("common.success.savedraft", new Object[]{persistObj.getPoNumber()}, Global.LOCALE));
			}
		} catch (Exception e) {
			LOG.error("Error while save draft :" + e.getMessage(), e);
		}
		return new ModelAndView("redirect:/buyer/buyerDashboard");
	}

	@RequestMapping(value = "/poTeamMembersList/{poId}", method = RequestMethod.GET)
	public ResponseEntity<TableData<EventTeamMember>> eventTeamMembersList(@PathVariable(name = "poId") String poId, TableDataInput input) {
		TableData<EventTeamMember> data = null;
		try {
			data = new TableData<EventTeamMember>(poService.getPlainTeamMembersForPo(poId));
			data.setDraw(input.getDraw());
			LOG.info("PO Team Members ******** :" + data);
		} catch (Exception e) {
			LOG.error("Error while loading draft list : " + e.getMessage(), e);
		}
		return new ResponseEntity<TableData<EventTeamMember>>(data, HttpStatus.OK);
	}

	public void setDefaultPrCreatorAsPoTeamMember(Po po, Pr pr) {

		String prId = pr.getId();
		String prCreator = pr.getCreatedBy().getId();
		LOG.info("PR Creator : " + prCreator);

		String poId = po.getId();
		String poCreator = po.getCreatedBy().getId();
		LOG.info("PO Creator : " + poCreator);
		List<PoTeamMember> poTeam = poDao.findAssociateOwnerOfPo(poId, TeamMemberType.Associate_Owner);

		if (prCreator.equals(poCreator)) {
			if (CollectionUtil.isNotEmpty(poTeam)) {
				for (PoTeamMember assOwnPo : poTeam) {
					if (!assOwnPo.getUser().getId().equals(poCreator)) {
						poService.addTeamMemberToList(poId, prCreator, TeamMemberType.Associate_Owner);
						LOG.info(prCreator + " was added to PO Team Member from current PO team member ");
						break;
					}
				}
			} else {
				poService.addTeamMemberToList(poId, prCreator, TeamMemberType.Associate_Owner);
				LOG.info(prCreator + " was added to PO Team Member from empty PO Team member ");
			}

		}
	}

	@RequestMapping(path = "/createPoItem", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<PoItem> savePoItem(@RequestBody PoItem poItem) throws JsonProcessingException {
		HttpHeaders headers = new HttpHeaders();
		try {
			boolean isSection = poItem != null && poItem.getItemId() == null;
			LOG.info("Is Section : ",isSection);
			if (doValidate(poItem,isSection)) {
				PoItem savedItem = poService.savePoItem(poItem, SecurityLibrary.getLoggedInUser ());
				headers.add("success", messageSource.getMessage("prItem.save.success", new Object[] { savedItem.getItemName() }, Global.LOCALE));
				return new ResponseEntity<>(savedItem, headers, HttpStatus.CREATED); // Return the saved item
			} else {
				headers.add("duplicated", messageSource.getMessage("prItem.save.duplicate", new Object[] { poItem.getItemName() }, Global.LOCALE));
				return new ResponseEntity<>(new PoItem(), headers, HttpStatus.CONFLICT); // Return an empty PoItem instead of null
			}
		} catch (DataIntegrityViolationException e) {
			LOG.error("Data integrity violation while saving po item: " + e.getMessage(), e);
			headers.add("error", messageSource.getMessage("prItem.save.duplicate", new Object[] { e.getMessage() }, Global.LOCALE));
			return new ResponseEntity<>(new PoItem(), headers, HttpStatus.CONFLICT); // Return an empty PoItem
		} catch (Exception e) {
			LOG.error("Error while saving po item: " + e.getMessage(), e);
			headers.add("error", messageSource.getMessage("prItem.save.error", new Object[] { e.getMessage() }, Global.LOCALE));
			return new ResponseEntity<>(new PoItem(), headers, HttpStatus.INTERNAL_SERVER_ERROR); // Return an empty PoItem
		}
	}

	/**
	 * @param poItem
	 * @return
	 */
	private boolean doValidate(PoItem poItem,boolean isSection) {
		boolean validate = !poService.isExists(poItem,isSection);
		return validate;
	}
}

