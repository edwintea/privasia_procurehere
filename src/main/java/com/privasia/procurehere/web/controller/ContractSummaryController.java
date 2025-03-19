package com.privasia.procurehere.web.controller;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;

import javax.annotation.Resource;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;

import com.privasia.procurehere.core.entity.*;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.service.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomCollectionEditor;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.privasia.procurehere.core.dao.BuyerAuditTrailDao;
import com.privasia.procurehere.core.dao.ErpSetupDao;
import com.privasia.procurehere.core.dao.ProductContractNotifyUsersDao;
import com.privasia.procurehere.core.enums.AuditActionType;
import com.privasia.procurehere.core.enums.AuditTypes;
import com.privasia.procurehere.core.enums.ContractStatus;
import com.privasia.procurehere.core.enums.ModuleType;
import com.privasia.procurehere.core.enums.PoDocumentType;
import com.privasia.procurehere.core.enums.UserType;
import com.privasia.procurehere.core.exceptions.ApplicationException;
import com.privasia.procurehere.core.exceptions.ErpIntegrationException;
import com.privasia.procurehere.core.pojo.EventPermissions;
import com.privasia.procurehere.core.pojo.TableData;
import com.privasia.procurehere.core.pojo.TableDataInput;
import com.privasia.procurehere.core.pojo.UserPojo;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.SecurityLibrary;
import com.privasia.procurehere.core.utils.StringUtils;
import com.privasia.procurehere.service.impl.SnapShotAuditService;
import com.privasia.procurehere.web.editors.ContractApprovalEditor;
import com.privasia.procurehere.web.editors.UserEditor;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.fill.JRSwapFileVirtualizer;
import net.sf.jasperreports.engine.util.JRSwapFile;

/**
 * @author anshul
 */
@Controller
@RequestMapping("/buyer")
public class ContractSummaryController {

	private static final Logger LOG = LogManager.getLogger(Global.CONTRACT_LOG);

	protected Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

	@Autowired
	ProductListMaintenanceService productListMaintenanceService;

	@Autowired
	ServletContext context;

	@Autowired
	ApprovalService approvalService;

	@Autowired
	UserEditor userEditor;

	@Autowired
	ProductContractService productContractService;

	@Autowired
	BudgetService budgetService;

	@Autowired
	PrTemplateService prTemplateService;

	@Resource
	MessageSource messageSource;

	@Autowired
	OwnerSettingsService ownerSettingsService;

	@Autowired
	PrAuditService prAuditService;

	@Autowired
	BudgetService budgetservice;

	@Autowired
	TransactionLogService transactionLogService;

	@Autowired
	UserService userService;

	@Autowired
	ErpSetupDao erpSetupDao;

	@Autowired
	BuyerSettingsService buyerSettingsService;

	@Autowired
	BuyerAuditTrailDao buyerAuditTrailDao;

	@Autowired
	ProductContractNotifyUsersDao productContractNotifyUsersDao;

	@Autowired
	ProductContractItemsService productContractItemsService;

	@Autowired
	ContractAuditService contractAuditService;

	@Autowired
	ContractDocumentService contractDocumentService;

	@Autowired
	ContractApprovalEditor contractApprovalEditor;

	@Autowired
	SnapShotAuditService snapShotAuditService;

	@Autowired
	ContractLoaAndAgreementService contractLoaAndAgreementService;

	@Autowired
	UomService uomService;

	@Autowired
	ProductCategoryMaintenanceService productCategoryMaintenanceService;

	@ModelAttribute("step")
	public String getStep() {
		return "7";
	}

	@ModelAttribute("poDocType")
	public List<PoDocumentType> getPoDocType() {
		return Arrays.asList(PoDocumentType.values());
	}

	@InitBinder
	public void initBinder(WebDataBinder binder, HttpSession session) {
		binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
		binder.registerCustomEditor(User.class, userEditor);
		binder.registerCustomEditor(ContractApprovalUser.class, contractApprovalEditor);
		binder.registerCustomEditor(List.class, "approvalUsers", new CustomCollectionEditor(List.class) {
			protected Object convertElement(Object element) {
				if (element != null) {
					String id = (String) element;
					User user = userService.findUserById(id);
					// PrApprovalUser group = prService.getPrApprovalUser(id);
					return new ContractApprovalUser(user);
				}
				return null;
			}
		});
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
	}

	public Boolean validateContract(ProductContract contract, Model model, Class<?>... validations) {
		List<String> errorList = new ArrayList<String>();
		LOG.info("Validaiting .....");

		Set<ConstraintViolation<ProductContract>> constraintViolations = validator.validate(contract, validations);
		for (ConstraintViolation<ProductContract> cv : constraintViolations) {
			LOG.info("Message : " + cv.getMessage());
			System.out.println("Error : " + cv.getMessage() + " : " + cv.getPropertyPath());
			errorList.add(cv.getMessage());
		}
		model.addAttribute("error", errorList);
		if (errorList.isEmpty()) {
			return Boolean.FALSE;
		} else {
			return Boolean.TRUE;
		}
	}

	/**
	 * @param id
	 * @param model
	 * @param eventPermissions
	 * @return
	 */
	public ProductContract constructContractSummaryAttributes(String id, Model model, EventPermissions eventPermissions) {
		ProductContract productContract = productContractService.getProductContractById(id);

		if (productContract != null && StringUtils.checkString(productContract.getContractReminderDates()).length() > 0) {
			model.addAttribute("productContractReminderDates", productContract.getContractReminderDates().split(","));
		}

		model.addAttribute("productContract", productContract);

		List<ProductContractItems> contractItemlist = productContractItemsService.findProductContractItemsByProductContractId(id);
		model.addAttribute("contractItemlist", contractItemlist);

		// Contract users for Approval
		// List<User> userList = userService.fetchAllUsersForTenant(SecurityLibrary.getLoggedInUserTenantId());
		List<UserPojo> userList = userService.fetchAllUsersForTenant(SecurityLibrary.getLoggedInUserTenantId(), "", UserType.NORMAL_USER);
		List<User> assignedTeamMembers = new ArrayList<>();
		List<User> userTeamMemberList = new ArrayList<User>();
		for (ContractTeamMember contractTeamMember : productContract.getTeamMembers()) {
			try {
				assignedTeamMembers.add((User) contractTeamMember.getUser().clone());
			} catch (Exception e) {
				LOG.error("Error while fetching Team Members");
			}
		}

		List<ContractApprovalUser> approvalUserList = new ArrayList<ContractApprovalUser>();
		List<ContractApprovalUser> appUserList = productContractService.fetchAllApprovalUsersByContractId(id);
		for (UserPojo user : userList) {
			User u = new User(user.getId(), user.getLoginId(), user.getName(), user.getCommunicationEmail(), user.isEmailNotifications(), user.getTenantId(), user.isDeleted());
			userTeamMemberList.add(u);
		}

		List<UserPojo> allUserList = userService.fetchAllUsersForTenant(SecurityLibrary.getLoggedInUserTenantId(), "", null);

		for (ContractApprovalUser approvalUser : appUserList) {
			User user = new User(approvalUser.getUser().getId(), approvalUser.getUser().getLoginId(), approvalUser.getUser().getName(), approvalUser.getUser().getCommunicationEmail(), approvalUser.getUser().getEmailNotifications(), approvalUser.getUser().getTenantId(), approvalUser.getUser().isDeleted());
			if (!approvalUserList.contains(new ContractApprovalUser(user))) {
				approvalUserList.add(new ContractApprovalUser(user));
			}
		}

		for (UserPojo userPojo : allUserList) {
			User user = new User(userPojo.getId(), userPojo.getLoginId(), userPojo.getName(), userPojo.getCommunicationEmail(), userPojo.isEmailNotifications(), userPojo.getTenantId(), userPojo.isDeleted());
			if (!approvalUserList.contains(new ContractApprovalUser(user))) {
				approvalUserList.add(new ContractApprovalUser(user));
			}
		}

		userTeamMemberList.removeAll(assignedTeamMembers);
		model.addAttribute("userTeamMemberList", userTeamMemberList);
		model.addAttribute("userList", approvalUserList);
		model.addAttribute("userList1", userList);
		List<ContractDocument> listDocs = contractDocumentService.findAllContractdocsbyContractId(id);
		model.addAttribute("listDocs", listDocs);
		model.addAttribute("eventPermissions", eventPermissions);
		model.addAttribute("contractLoaAndAgreement", productContractService.findContractLoaAndAgreementByContractId(id));
		model.addAttribute("ownerSettings", ownerSettingsService.getOwnersettings());
		List<ContractAudit> contractAuditList = contractAuditService.getContractAuditByContractId(productContract.getId());
		model.addAttribute("contractAuditList", contractAuditList);
		List<ProductContractNotifyUsers> notifyUsers = productContractNotifyUsersDao.getAllContractNotifyUsersByContractId(id);
		model.addAttribute("notifyUsers", notifyUsers);
		List<ContractComment> contractComments = productContractService.findAllContractCommentsByContractId(id);
		model.addAttribute("contractCommentList", contractComments);
		return productContract;
	}

	@RequestMapping(path = "/productContractSummary/{id}", method = RequestMethod.GET)
	public String productContractSummary(@PathVariable String id, Model model) {
		EventPermissions eventPermissions = productContractService.getUserPemissionsForContract(SecurityLibrary.getLoggedInUser().getId(), id);
		constructContractSummaryAttributes(id, model, eventPermissions);
		model.addAttribute("inview", true);
		return "productContractSummary";
	}

	@RequestMapping(path = "/contractSummary/{id}", method = RequestMethod.GET)
	public String contractSummary(@PathVariable String id, Model model) {
		ProductContract contract = productContractService.getProductContractById(id);
		EventPermissions eventPermissions = productContractService.getUserPemissionsForContract(SecurityLibrary.getLoggedInUser().getId(), id);
		constructContractSummaryAttributes(id, model, eventPermissions);
		List<ProductContractItems> Items = productContractService.findAllContractItemsByContractId(contract.getId());
		if (CollectionUtil.isNotEmpty(Items)) {
			for (ProductContractItems item : Items) {
				if (item.getProductCategory() == null) {
					List<Uom> uomList = uomService.getAllActiveUomForTenant(SecurityLibrary.getLoggedInUserTenantId());
					List<ProductCategoryPojo> catList = productCategoryMaintenanceService.fetchAllActiveProductCategoryForTenant(SecurityLibrary.getLoggedInUserTenantId(), null);
					model.addAttribute("uomList", uomList);
					model.addAttribute("productCategoryList", catList);
					model.addAttribute("error", "Category Name is mandatory. Please Edit item and select Category.");
					return "productContractItemList";
				}
			}
		}

		contract.setContractItemCompleted(Boolean.TRUE);
		contract = productContractService.update(contract);
		return "productContractSummary";
	}

	@RequestMapping(path = "/contractFinish/{id}", method = RequestMethod.GET)
	public ModelAndView contractFinish(@PathVariable String id, Model model, RedirectAttributes redir, HttpSession session) {
		LOG.info("Product Contract Finish called...");

		ProductContract contractObj = null;
		try {
			contractObj = productContractService.contractFinish(id, SecurityLibrary.getLoggedInUser());

			JRSwapFileVirtualizer virtualizer;
			try {
				virtualizer = new JRSwapFileVirtualizer(200, new JRSwapFile(System.getProperty("java.io.tmpdir"), 2048, 1024), false);

				TimeZone timeZone = TimeZone.getDefault();
				String strTimeZone = (String) session.getAttribute(Global.SESSION_TIME_ZONE_KEY);
				if (strTimeZone != null) {
					timeZone = TimeZone.getTimeZone(strTimeZone);
				}
				SimpleDateFormat dateTimeFormat = new SimpleDateFormat("dd/MM/YYYY hh:mm a");
				dateTimeFormat.setTimeZone(timeZone);

				if (contractObj.getOldStatus() == ContractStatus.ACTIVE) {
					contractObj = approvalService.doApproval(contractObj, SecurityLibrary.getLoggedInUser(), Boolean.FALSE, session, virtualizer);
					// Resume Audit
					try {
						snapShotAuditService.doContractAudit(contractObj, session, contractObj, SecurityLibrary.getLoggedInUser(), AuditActionType.Resume, messageSource.getMessage("contract.resume.request", new Object[] { contractObj.getContractId() }, Global.LOCALE), virtualizer);
					} catch (Exception e) {
						LOG.error("Error While saving Audit Trail : " + e.getMessage(), e);
					}

					try {
						BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.RESUME, messageSource.getMessage("contract.is.resume.request", new Object[] { contractObj.getContractId() }, Global.LOCALE), SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser(), new Date(), ModuleType.ContractList);
						buyerAuditTrailDao.save(buyerAuditTrail);
					} catch (Exception e) {
						LOG.error("Error while recording contract Resume " + e.getMessage(), e);
					}
				} else {
					contractObj = approvalService.doApproval(contractObj, SecurityLibrary.getLoggedInUser(), Boolean.TRUE, session, virtualizer);
				}

				redir.addFlashAttribute("success", messageSource.getMessage("contract.finish.success", new Object[] { contractObj.getContractId() }, Global.LOCALE));

			} catch (ErpIntegrationException e) {
				LOG.error("Error While Finish :" + e.getMessage(), e);
				try {
					approvalService.updateContractDetailsOnErpError(SecurityLibrary.getLoggedInUser(), id, e.getMessage());
				} catch (Exception e1) {
					LOG.error("Error updating erp error details :" + e.getMessage(), e);
				}
				model.addAttribute("error", e.getMessage());
				EventPermissions eventPermissions = productContractService.getUserPemissionsForContract(SecurityLibrary.getLoggedInUser().getId(), id);
				constructContractSummaryAttributes(id, model, eventPermissions);
				return new ModelAndView("productContractSummary");
			} catch (Exception e) {
				LOG.error("Error While Finish :" + e.getMessage(), e);
				model.addAttribute("error", e.getMessage());
				EventPermissions eventPermissions = productContractService.getUserPemissionsForContract(SecurityLibrary.getLoggedInUser().getId(), id);
				constructContractSummaryAttributes(id, model, eventPermissions);
				return new ModelAndView("productContractSummary");
			}

		} catch (ApplicationException e) {
			model.addAttribute("error", e.getMessage());
			EventPermissions eventPermissions = productContractService.getUserPemissionsForContract(SecurityLibrary.getLoggedInUser().getId(), id);
			constructContractSummaryAttributes(id, model, eventPermissions);
			return new ModelAndView("productContractSummary");
		} catch (Exception e) {
			LOG.error("Error While Finish :" + e.getMessage(), e);
			model.addAttribute("error", e.getMessage());
			EventPermissions eventPermissions = productContractService.getUserPemissionsForContract(SecurityLibrary.getLoggedInUser().getId(), id);
			constructContractSummaryAttributes(id, model, eventPermissions);
			return new ModelAndView("productContractSummary");
		}

		return new ModelAndView("redirect:/buyer/productContractList");
	}

	@RequestMapping(path = "/terminateContract/{contractId}", method = RequestMethod.GET)
	public ModelAndView terminateContract(@PathVariable String contractId, @RequestParam String terminationReason, Model model, RedirectAttributes redir, HttpSession session) {
		LOG.info("Product Contract Terminate called......");

		try {
			ProductContract contractObj = approvalService.doTerminationApproval(contractId, SecurityLibrary.getLoggedInUser(), Boolean.FALSE, session, terminationReason);
			redir.addFlashAttribute("success", messageSource.getMessage("contract.terminate.success", new Object[] { contractObj.getContractId() }, Global.LOCALE));
		} catch (Exception e) {
			LOG.error("Error While Terminating Contract :" + e.getMessage(), e);
			model.addAttribute("error", e.getMessage());
			EventPermissions eventPermissions = productContractService.getUserPemissionsForContract(SecurityLibrary.getLoggedInUser().getId(), contractId);
			constructContractSummaryAttributes(contractId, model, eventPermissions);
			return new ModelAndView("productContractSummary");
		}
		return new ModelAndView("redirect:/buyer/productContractList");
	}

	@RequestMapping(path = "/contractView/{id}", method = RequestMethod.GET)
	public String contractView(@PathVariable String id, Model model, HttpServletRequest request, RedirectAttributes redir) {
		LOG.info("create contract View GET called contract id :" + id);
		try {
			EventPermissions eventPermissions = productContractService.getUserPemissionsForContract(SecurityLibrary.getLoggedInUser().getId(), id);
			ProductContract contract = constructContractSummaryAttributes(id, model, eventPermissions);

			if (contract.getStatus() == ContractStatus.DRAFT) {
				return "productContractSummary";
			}
			if (!checkPermissionToAllow(eventPermissions)) {
				redir.addFlashAttribute("requestedUrl", request.getRequestURL());
				return "redirect:/403_error";
			}
		} catch (Exception e) {
			LOG.error("Error in view Contract :" + e.getMessage(), e);
			model.addAttribute("error", messageSource.getMessage("contract.summary.error.view", new Object[] { e.getMessage() }, Global.LOCALE));
		}
		model.addAttribute("inView", true);
		return "contractView";
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

	@RequestMapping(path = "/contractApproved", method = RequestMethod.POST)
	public String contractApproved(@RequestParam String contractId, @RequestParam String remarks, RedirectAttributes redir, HttpSession session) {
		LOG.info("create contract Approved GET called contract id :" + contractId + " remarks :" + remarks + " SecurityLibrary.getLoggedInUser() :" + SecurityLibrary.getLoggedInUser().getCommunicationEmail());
		try {
			ProductContract contract = new ProductContract();
			contract.setId(contractId);
			ProductContract contractApproval = approvalService.doApproval(contract, SecurityLibrary.getLoggedInUser(), remarks, true, session);

			redir.addFlashAttribute("success", messageSource.getMessage("flashsuccess.contract.approved", new Object[] { (StringUtils.checkString(contractApproval.getContractId()).length() > 0 ? contractApproval.getContractId() : " ") }, Global.LOCALE));
		} catch (ErpIntegrationException e) {
			LOG.error("Error while approving contract :" + e.getMessage(), e);
			try {
				approvalService.updateContractDetailsOnErpError(SecurityLibrary.getLoggedInUser(), contractId, e.getMessage());
			} catch (Exception e1) {
				LOG.error("Error updating erp error details :" + e.getMessage(), e);
			}
			redir.addFlashAttribute("error", "Error while approving contract : " + e.getMessage());
			return "redirect:/buyer/contractSummary/" + contractId;
		} catch (Exception e) {
			LOG.error("Error while approving contract :" + e.getMessage(), e);
			redir.addFlashAttribute("error", "Error while approving contract : " + e.getMessage());
			return "redirect:/buyer/contractSummary/" + contractId;
		}
		return "redirect:/buyer/buyerDashboard";
	}

	@RequestMapping(path = "/contractRejected", method = RequestMethod.POST)
	public String contractRejected(@RequestParam String contractId, @RequestParam String remarks, RedirectAttributes redir, HttpSession session) {
		LOG.info("create contract Rejected GET called contract id :" + contractId + " remarks :" + remarks);
		try {
			if (StringUtils.checkString(remarks).length() == 0) {
				redir.addFlashAttribute("error", messageSource.getMessage("flasherror.remark.cannot.empty", new Object[] {}, Global.LOCALE));
				return "redirect:contractView/" + contractId;
			}
			ProductContract contract = new ProductContract();
			contract.setId(contractId);
			ProductContract contractReject = approvalService.doApproval(contract, SecurityLibrary.getLoggedInUser(), remarks, false, session);

			if (contractReject != null) {
				try {
					ContractAudit contractAudit = new ContractAudit(contract, SecurityLibrary.getLoggedInUser(), new Date(), AuditActionType.Rejected, messageSource.getMessage("contract.audit.reject", new Object[] { contractReject.getContractId() }, Global.LOCALE));
					contractAuditService.save(contractAudit);

					BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.REJECTED, messageSource.getMessage("contract.audit.reject", new Object[] { contractReject.getContractId() }, Global.LOCALE), SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser(), new Date(), ModuleType.ContractList);
					buyerAuditTrailDao.save(buyerAuditTrail);

				} catch (Exception e) {
					LOG.error("Error While saving Audit Trail : " + e.getMessage(), e);
				}
			}
			redir.addFlashAttribute("success", messageSource.getMessage("flashsuccess.contract.rejected", new Object[] { (StringUtils.checkString(contractReject.getContractId()).length() > 0 ? contractReject.getContractId() : " ") }, Global.LOCALE));
		} catch (Exception e) {
			LOG.error("Error while rejecting contract :" + e.getMessage(), e);
			redir.addFlashAttribute("error", messageSource.getMessage("flasherror.while.rejecting.contract", new Object[] { e.getMessage() }, Global.LOCALE));
		}
		return "redirect:/buyer/buyerDashboard";
	}

	@RequestMapping(value = "/contractTeamMembersList/{id}", method = RequestMethod.GET)
	public ResponseEntity<TableData<ContractTeamMember>> eventTeamMembersList(@PathVariable(name = "id") String id, TableDataInput input) {
		TableData<ContractTeamMember> data = null;
		try {
			data = new TableData<ContractTeamMember>(productContractService.getPlainTeamMembersForContract(id));
			data.setDraw(input.getDraw());
			LOG.info("Contract Team Members ******** :" + data);
		} catch (Exception e) {
			LOG.error("Error while loading draft list : " + e.getMessage(), e);
		}
		return new ResponseEntity<TableData<ContractTeamMember>>(data, HttpStatus.OK);
	}

	private void streamReport(JasperPrint jasperPrint, String filename, HttpServletResponse response) throws JRException, IOException {
		response.setContentType("application/pdf");
		response.addHeader("Content-Disposition", "attachment; filename=" + filename);

		JasperExportManager.exportReportToPdfStream(jasperPrint, response.getOutputStream());
		response.getOutputStream().flush();
		response.getOutputStream().close();

	}

	@RequestMapping(value = "downloadLoaDocument/{id}", method = RequestMethod.GET)
	public void downloadLoaDocument(@PathVariable String id, HttpServletResponse response, Model model) throws IOException {
		try {
			contractLoaAndAgreementService.downloadLoaDocument(id, response);
		} catch (Exception e) {
			LOG.error("Error while downloading LOA Document : " + e.getMessage(), e);
			model.addAttribute("error", messageSource.getMessage("prsummary.error.downloading.documents", new Object[] { e.getMessage() }, Global.LOCALE));
		}
	}

	@RequestMapping(value = "downloadAgreementDocument/{id}", method = RequestMethod.GET)
	public void downloadAgreementDocument(@PathVariable String id, HttpServletResponse response, Model model) throws IOException {
		try {
			contractLoaAndAgreementService.downloadAgreementDocument(id, response);
		} catch (Exception e) {
			LOG.error("Error while downloading Agreement Document : " + e.getMessage(), e);
			model.addAttribute("error", messageSource.getMessage("prsummary.error.downloading.documents", new Object[] { e.getMessage() }, Global.LOCALE));
		}
	}

	@RequestMapping(path = "/downlaodContractSummary/{contractId}", method = RequestMethod.GET)
	public void downlaodContractSummary(@PathVariable("contractId") String contractId, HttpServletResponse response, HttpSession session) throws Exception {
		JRSwapFileVirtualizer virtualizer = null;
		try {
			// Virtualizar - To increase the performance
			virtualizer = new JRSwapFileVirtualizer(200, new JRSwapFile(System.getProperty("java.io.tmpdir"), 2048, 1024), false);

			ProductContract productContract = productContractService.getContractById(contractId);
			String contractFileName = ("Contract-Summary-" + productContract.getContractReferenceNumber()).replace("/", "-") + ".pdf";
			String filename = contractFileName;

			try {
				BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.DOWNLOAD, "Contract Summary is downloaded for '" + productContract.getContractId() + "' ", SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser(), new Date(), ModuleType.ContractList);
				buyerAuditTrailDao.save(buyerAuditTrail);
			} catch (Exception e) {
				LOG.error("Error while recording auction event Resume " + e.getMessage(), e);
			}

			JasperPrint jasperPrint = productContractService.getContractSummaryPdf(productContract.getId(), SecurityLibrary.getLoggedInUser(), (String) session.getAttribute(Global.SESSION_TIME_ZONE_KEY), virtualizer);
			if (jasperPrint != null) {
				streamReport(jasperPrint, filename, response);
			}

		} catch (Exception e) {
			LOG.error("Could not generate RFS Summary Report. " + e.getMessage(), e);
		}
	}

	@RequestMapping(value = "/downloadAuditFile/{id}/{action}/{actionDate}", method = RequestMethod.GET)
	public void downloadAuditFile(@PathVariable String id, @PathVariable String action, @PathVariable String actionDate, HttpServletResponse response) throws IOException {
		LOG.info("ActionDate 1 :" + actionDate + " id " + id);
		try {
			response.setContentType("application/pdf");
			response.setHeader("Content-Disposition", "attachment; filename=\"" + action.replaceAll(" ", "_") + "_Audit_" + actionDate + ".pdf\"");
			ContractAudit ContractAudit = contractAuditService.getContractAuditById(id);
			LOG.info("snapshot available: " + ContractAudit.getSummarySnapshot().length);
			if (ContractAudit.getSummarySnapshot() != null && ContractAudit.getSummarySnapshot().length > 0) {
				LOG.info("actionDate 2 :" + actionDate + " id " + id);
				response.setContentLength(ContractAudit.getSummarySnapshot().length);
				FileCopyUtils.copy(ContractAudit.getSummarySnapshot(), response.getOutputStream());
			}
			response.flushBuffer();
			response.setStatus(HttpServletResponse.SC_OK);
		} catch (Exception e) {
			LOG.error("Error while File downloaded Contract Audit File : " + e.getMessage(), e);
		}
	}

	@RequestMapping(path = "/deleteLoaDocument", method = RequestMethod.POST)
	public ResponseEntity<String> deleteLoaDocument(@RequestParam("documentId") String documentId, @RequestParam("contractId") String contractId) {
		try {
			HttpHeaders headers = new HttpHeaders();
			if (StringUtils.checkString(documentId).length() > 0) {
				contractLoaAndAgreementService.removeLoaDocument(documentId);
				headers.add("success", messageSource.getMessage("rft.file.removedocument", new Object[] {}, Global.LOCALE));
				return new ResponseEntity<String>("{ \"status\" : \"SUCCESS\" }", headers, HttpStatus.OK);
			}
			headers.add("error", "Document Id is required");
			return new ResponseEntity<String>("{ \"status\" : \"ERROR\" }", headers, HttpStatus.EXPECTATION_FAILED);
		} catch (Exception e) {
			LOG.error("Error while Error while removing LOA document : " + e.getMessage(), e);
			HttpHeaders headers = new HttpHeaders();
			headers.add("error", messageSource.getMessage("rft.file.removedocumenterror", new Object[] { e.getMessage() }, Global.LOCALE));
			return new ResponseEntity<String>("{ \"status\" : \"ERROR\" }", headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(path = "/deleteAgreementDocument", method = RequestMethod.POST)
	public ResponseEntity<String> deleteAgreementDocument(@RequestParam("documentId") String documentId, @RequestParam("contractId") String contractId) {
		try {
			HttpHeaders headers = new HttpHeaders();
			if (StringUtils.checkString(documentId).length() > 0) {
				contractLoaAndAgreementService.removeAgreementDocument(documentId);
				headers.add("success", messageSource.getMessage("rft.file.removedocument", new Object[] {}, Global.LOCALE));
				return new ResponseEntity<String>("{ \"status\" : \"SUCCESS\" }", headers, HttpStatus.OK);
			}
			headers.add("error", "Document Id is required");
			return new ResponseEntity<String>("{ \"status\" : \"ERROR\" }", headers, HttpStatus.EXPECTATION_FAILED);
		} catch (Exception e) {
			LOG.error("Error while Error while removing Agreement Document : " + e.getMessage(), e);
			HttpHeaders headers = new HttpHeaders();
			headers.add("error", messageSource.getMessage("rft.file.removedocumenterror", new Object[] { e.getMessage() }, Global.LOCALE));
			return new ResponseEntity<String>("{ \"status\" : \"ERROR\" }", headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

}