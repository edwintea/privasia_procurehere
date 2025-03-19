package com.privasia.procurehere.web.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.privasia.procurehere.core.dao.BuyerAuditTrailDao;
import com.privasia.procurehere.core.entity.BuyerAuditTrail;
import com.privasia.procurehere.core.entity.Supplier;
import com.privasia.procurehere.core.entity.SupplierFormItemAttachment;
import com.privasia.procurehere.core.entity.SupplierFormSubmissionItem;
import com.privasia.procurehere.core.entity.SupplierFormSubmition;
import com.privasia.procurehere.core.entity.SupplierFormSubmitionApproval;
import com.privasia.procurehere.core.entity.SupplierFormSubmitionApprovalUser;
import com.privasia.procurehere.core.entity.SupplierFormSubmitionAudit;
import com.privasia.procurehere.core.entity.User;
import com.privasia.procurehere.core.enums.AuditTypes;
import com.privasia.procurehere.core.enums.ModuleType;
import com.privasia.procurehere.core.enums.Status;
import com.privasia.procurehere.core.enums.SupplierFormApprovalStatus;
import com.privasia.procurehere.core.enums.SupplierFormSubAuditVisibilityType;
import com.privasia.procurehere.core.enums.SupplierFormSubmitionAuditType;
import com.privasia.procurehere.core.enums.SupplierFormSubmitionStatus;
import com.privasia.procurehere.core.pojo.EventPermissions;
import com.privasia.procurehere.core.pojo.SupplierFormSubItem;
import com.privasia.procurehere.core.pojo.SupplierFormSubmissionItemPojo;
import com.privasia.procurehere.core.pojo.SupplierFormSubmissionPojo;
import com.privasia.procurehere.core.pojo.TableData;
import com.privasia.procurehere.core.pojo.TableDataInput;
import com.privasia.procurehere.core.pojo.UserPojo;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.SecurityLibrary;
import com.privasia.procurehere.service.ApprovalService;
import com.privasia.procurehere.service.SupplierFormService;
import com.privasia.procurehere.service.SupplierFormSubmissionService;
import com.privasia.procurehere.service.SupplierFormSubmitionAuditService;
import com.privasia.procurehere.service.UserService;
import com.privasia.procurehere.service.supplier.SupplierService;
import com.privasia.procurehere.web.editors.SupplierFormApprovalSubmitionEditor;
import com.privasia.procurehere.web.editors.UserEditor;

/**
 * @author pooja
 */
@Controller
@RequestMapping(path = "/buyer")
public class BuyerSupplierFormSubmitionController {
	private static final Logger LOG = LogManager.getLogger(Global.BUYER_LOG);

	@Autowired
	MessageSource messageSource;

	@Autowired
	SupplierFormService supplierFormService;

	@Autowired
	SupplierFormSubmissionService supplierFormSubmissionService;

	@Autowired
	SupplierService supplierService;

	@Autowired
	SupplierFormSubmitionAuditService supplierFormSubmitionAuditService;

	@Autowired
	ServletContext context;

	@Autowired
	UserService userService;

	@Autowired
	ApprovalService approvalService;

	@Autowired
	SupplierFormApprovalSubmitionEditor supplierFormApprovalSubmitionEditor;

	@Autowired
	UserEditor userEditor;

	@Autowired
	BuyerAuditTrailDao buyerAuditTrailDao;

	@ModelAttribute("statusList")
	public List<Status> getStatusList() {
		return Arrays.asList(Status.values());
	}

	@InitBinder
	public void InitBinder(WebDataBinder binder, HttpSession session) {
		binder.registerCustomEditor(User.class, userEditor);
		binder.registerCustomEditor(SupplierFormSubmitionApprovalUser.class, supplierFormApprovalSubmitionEditor);

	}

	@ModelAttribute("supplierFormStatusList")
	public List<SupplierFormSubmitionStatus> supplierFormStatusList() {
		return Arrays.asList(SupplierFormSubmitionStatus.values());
	}

	@RequestMapping(path = "/supplierFormListDataById", method = RequestMethod.GET)
	public ResponseEntity<TableData<SupplierFormSubmissionPojo>> submittedFormListData(@RequestParam("suppId") String supplierId, TableDataInput input) {
		try {
			List<SupplierFormSubmissionPojo> pendingList = supplierFormSubmissionService.findAllSearchFilterFormBySuppAndBuyerId(SecurityLibrary.getLoggedInUserTenantId(), input, supplierId);
			TableData<SupplierFormSubmissionPojo> data = new TableData<SupplierFormSubmissionPojo>(pendingList);
			data.setDraw(input.getDraw());
			long recordFiltered = supplierFormSubmissionService.findTotalSearchFilterFormBySuppAndBuyerId(SecurityLibrary.getLoggedInUserTenantId(), input, supplierId);
			data.setRecordsFiltered(recordFiltered);
			long totalRecord = supplierFormSubmissionService.findTotalFormBySuppAndBuyerId(SecurityLibrary.getLoggedInUserTenantId(), supplierId);
			data.setRecordsTotal(totalRecord);
			return new ResponseEntity<TableData<SupplierFormSubmissionPojo>>(data, HttpStatus.OK);
		} catch (Exception e) {
			LOG.error("Error while fetching supplier Form List :" + e.getMessage(), e);
			HttpHeaders headers = new HttpHeaders();
			headers.add("error", "Error while fetching supplier Form List :" + e.getMessage());
			return new ResponseEntity<TableData<SupplierFormSubmissionPojo>>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(path = "/pendingSupplierFormSubData", method = RequestMethod.GET)
	public ResponseEntity<TableData<SupplierFormSubmissionPojo>> pendingSupplierFormSubData(TableDataInput input) {
		try {
			List<SupplierFormSubmissionPojo> pendingList = supplierFormSubmissionService.findAllSearchFilterFormByBuyerIdAndStatus(SecurityLibrary.getLoggedInUserTenantId(), input, Arrays.asList(SupplierFormSubmitionStatus.PENDING));
			TableData<SupplierFormSubmissionPojo> data = new TableData<SupplierFormSubmissionPojo>(pendingList);
			data.setDraw(input.getDraw());
			long recordFiltered = supplierFormSubmissionService.findTotalSearchFilterFormByBuyerIdAndStatus(SecurityLibrary.getLoggedInUserTenantId(), input, Arrays.asList(SupplierFormSubmitionStatus.PENDING));
			data.setRecordsFiltered(recordFiltered);
			data.setRecordsTotal(recordFiltered);
			return new ResponseEntity<TableData<SupplierFormSubmissionPojo>>(data, HttpStatus.OK);
		} catch (Exception e) {
			LOG.error("Error while fetching pending supplier Form List :" + e.getMessage(), e);
			HttpHeaders headers = new HttpHeaders();
			headers.add("error", "Error while fetching pending supplier Form List :" + e.getMessage());
			return new ResponseEntity<TableData<SupplierFormSubmissionPojo>>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(path = "/submittedSupplierFormSubData", method = RequestMethod.GET)
	public ResponseEntity<TableData<SupplierFormSubmissionPojo>> submittedSupplierFormSubData(TableDataInput input) {
		try {
			List<SupplierFormSubmissionPojo> pendingList = supplierFormSubmissionService.findAllSearchFilterFormByBuyerIdAndStatus(SecurityLibrary.getLoggedInUserTenantId(), input, Arrays.asList(SupplierFormSubmitionStatus.SUBMITTED, SupplierFormSubmitionStatus.ACCEPTED));
			TableData<SupplierFormSubmissionPojo> data = new TableData<SupplierFormSubmissionPojo>(pendingList);
			data.setDraw(input.getDraw());
			long recordFiltered = supplierFormSubmissionService.findTotalSearchFilterFormByBuyerIdAndStatus(SecurityLibrary.getLoggedInUserTenantId(), input, Arrays.asList(SupplierFormSubmitionStatus.SUBMITTED, SupplierFormSubmitionStatus.ACCEPTED));
			data.setRecordsFiltered(recordFiltered);
			data.setRecordsTotal(recordFiltered);
			return new ResponseEntity<TableData<SupplierFormSubmissionPojo>>(data, HttpStatus.OK);
		} catch (Exception e) {
			LOG.error("Error while fetching submiited supplier Form List :" + e.getMessage(), e);
			HttpHeaders headers = new HttpHeaders();
			headers.add("error", "Error while fetching submiited supplier Form List :" + e.getMessage());
			return new ResponseEntity<TableData<SupplierFormSubmissionPojo>>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(value = "/sendFormToSupplier", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<List<SupplierFormSubmition>> sendFormToSupplier(@RequestParam("supplierId") String supplierId, @RequestParam("formIds") String[] formIds, Model model, RedirectAttributes redir) {
		HttpHeaders headers = new HttpHeaders();
		LOG.info("Sending form to supplier :" + supplierId + " By user:" + SecurityLibrary.getLoggedInUserLoginId());
		try {
			if (formIds != null) {
				Supplier supplier = supplierService.findSuppById(supplierId);
				if (supplier != null) {
					supplierFormSubmissionService.assignFormsToSupplier(formIds, supplier, SecurityLibrary.getLoggedInUser());
					headers.add("success", messageSource.getMessage("supplier.assign.form.success", new Object[] {}, Global.LOCALE));
					LOG.info("Assinged form successlly");
				}
				return new ResponseEntity<List<SupplierFormSubmition>>(null, headers, HttpStatus.OK);
			} else {
				headers.add("error", messageSource.getMessage("supplier.assign.form.empty", new Object[] {}, Global.LOCALE));
				return new ResponseEntity<List<SupplierFormSubmition>>(null, headers, HttpStatus.BAD_REQUEST);
			}
		} catch (Exception e) {
			LOG.error("Error while sending form to supplier:" + e.getMessage(), e);
			headers.add("error", messageSource.getMessage("supplier.assign.form.error", new Object[] {}, Global.LOCALE));
			return new ResponseEntity<List<SupplierFormSubmition>>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);

		}
	}

	@RequestMapping(value = "/acceptSupplierForm", method = RequestMethod.POST)
	private String acceptSupplierForm(@RequestParam("formSubId") String formSubId, @RequestParam("buyerRemark") String buyerRemark, Model model, RedirectAttributes redir) {
		LOG.info("Accepting supplier form for submission:" + formSubId + " By logged in user:" + SecurityLibrary.getLoggedInUserLoginId());
		try {
			SupplierFormSubmition supplierFormSubmition = supplierFormSubmissionService.acceptSupplierForm(formSubId, SecurityLibrary.getLoggedInUser(), buyerRemark);
			if (supplierFormSubmition != null) {
				redir.addFlashAttribute("success", messageSource.getMessage("buyer.accepted.success.supplierForm", new Object[] { supplierFormSubmition.getName() }, Global.LOCALE));
				LOG.info("Accepted supplier form  successfully for submission:" + formSubId + " By logged in user:" + SecurityLibrary.getLoggedInUserLoginId());
			} else {
				LOG.info("SupplierFormSubmition not found for formSubId " + formSubId);
				redir.addFlashAttribute("error", messageSource.getMessage("flasherror.while.accepting.supplierForm", new Object[] {}, Global.LOCALE));
			}
		} catch (Exception e) {
			redir.addFlashAttribute("error", messageSource.getMessage("supplier.form.error.accept", new Object[] { e.getMessage() }, Global.LOCALE));
			LOG.error("error while accepting form " + e.getMessage(), e);
		}
		return "redirect:supplierFormSubView/" + formSubId;
	}

	@RequestMapping(value = "/reviseSupplierForm", method = RequestMethod.POST)
	private String reviseSupplierForm(@RequestParam("formSubId") String formSubId, @RequestParam("buyerRemark") String buyerRemark, Model model, RedirectAttributes redir) {
		LOG.info("Revising supplier form for submission:" + formSubId + " By logged in user:" + SecurityLibrary.getLoggedInUserLoginId());
		try {
			SupplierFormSubmition supplierFormSubmition = supplierFormSubmissionService.reviseSupplierForm(formSubId, SecurityLibrary.getLoggedInUser(), buyerRemark);
			if (supplierFormSubmition != null) {
				redir.addFlashAttribute("success", messageSource.getMessage("buyer.revised.success.supplierForm", new Object[] { supplierFormSubmition.getName() }, Global.LOCALE));
				LOG.info("Revised supplier form  successfully for submission:" + formSubId + " By logged in user:" + SecurityLibrary.getLoggedInUserLoginId());

			} else {
				LOG.info("SupplierFormSubmition not found for formSubId " + formSubId);
				redir.addFlashAttribute("error", messageSource.getMessage("flasherror.while.revising.supplierForm", new Object[] {}, Global.LOCALE));
			}
		} catch (Exception e) {
			redir.addFlashAttribute("error", messageSource.getMessage("supplier.form.error.revised", new Object[] { e.getMessage() }, Global.LOCALE));
			LOG.error("error while revising form " + e.getMessage(), e);
		}
		return "redirect:supplierFormSubView/" + formSubId;
	}

	@GetMapping("/supplierFormSubView/{formSubId}")
	public String supplierFormView(@PathVariable String formSubId, Model model, HttpServletRequest request, RedirectAttributes redir) {
		LOG.info("FormSubId: " + formSubId);
		try {
			constructBuyerFormSummaryForSupplierSubView(formSubId, model);
		} catch (Exception e) {
			LOG.info("Error while getting Buyer Form by id: " + e.getMessage(), e);
			model.addAttribute("error", messageSource.getMessage("error.while.fetching.supplier.formdetails", new Object[] { e.getMessage() }, Global.LOCALE));
		}
		return "buyerSupplierFormSubView";
	}

	private SupplierFormSubmition constructBuyerFormSummaryForSupplierSubView(String formSubId, Model model) {
		SupplierFormSubmissionItemPojo supplierFormSubmissionItemPojo = new SupplierFormSubmissionItemPojo();
		supplierFormSubmissionItemPojo.setFormId(formSubId);

		SupplierFormSubmition form = supplierFormSubmissionService.getSupplierformById(formSubId);
		EventPermissions eventPermissions = supplierFormSubmissionService.getUserPemissionsForApprovalofSuppForm(SecurityLibrary.getLoggedInUser(), formSubId);

		List<SupplierFormSubmissionItem> submissionList = supplierFormSubmissionService.findSupplierSubFormItemByIdForBuyer(formSubId);
		LOG.info("Form Item size: " + submissionList.size());
		List<SupplierFormSubItem> itemList = new ArrayList<SupplierFormSubItem>();
		for (SupplierFormSubmissionItem list : submissionList) {
			SupplierFormSubItem itemObj = new SupplierFormSubItem(list);
			List<SupplierFormItemAttachment> itemAttachment = supplierFormSubmissionService.findAllFormDocsByFormItemId(itemObj.getFormItem().getId());
			itemObj.setItemAttachment(itemAttachment);
			itemList.add(itemObj);
		}
		supplierFormSubmissionItemPojo.setItemList(itemList);
		List<User> approvalUserList = new ArrayList<User>();
		List<User> allUserList = new ArrayList<User>();
		List<UserPojo> allAppUserList = userService.fetchAllUsersForTenant(SecurityLibrary.getLoggedInUserTenantId(), "", null);
		if (CollectionUtil.isNotEmpty(allAppUserList)) {
			for (UserPojo user : allAppUserList) {
				User u = new User(user.getId(), user.getLoginId(), user.getName(), user.getCommunicationEmail(), user.isEmailNotifications(), user.getTenantId(), user.isDeleted());
				if (!allUserList.contains(u)) {
					allUserList.add(u);
				}
			}
		}
		try {
			if (form != null) {
				if (CollectionUtil.isNotEmpty(form.getApprovals())) {
					for (SupplierFormSubmitionApproval approval : form.getApprovals()) {
						if (CollectionUtil.isNotEmpty(approval.getApprovalUsers())) {
							for (SupplierFormSubmitionApprovalUser user : approval.getApprovalUsers()) {
								User u = new User(user.getUser().getId(), user.getUser().getLoginId(), user.getUser().getName(), user.getUser().getCommunicationEmail(), user.getUser().getEmailNotifications(), user.getUser().getTenantId(), user.getUser().isDeleted());
								if (!approvalUserList.contains(u)) {
									approvalUserList.add(u);
								}
							}
						}
					}

				}
				List<UserPojo> appuserList = userService.fetchAllUsersForTenant(SecurityLibrary.getLoggedInUserTenantId(), "", null);
				if (CollectionUtil.isNotEmpty(appuserList)) {
					for (UserPojo user : appuserList) {
						User u = new User(user.getId(), user.getLoginId(), user.getName(), user.getCommunicationEmail(), user.isEmailNotifications(), user.getTenantId(), user.isDeleted());
						if (!approvalUserList.contains(u)) {
							approvalUserList.add(u);
						}
					}
				}

			}
		} catch (Exception e) {
			LOG.error("Error while getting form details:" + e.getMessage());
		}

		Integer additionalLevelStart = 0;
		Integer additionalLevelNext = 0;

		if (form != null && form.getApprovalStatus() == SupplierFormApprovalStatus.APPROVED) {
			additionalLevelNext = form.getApprovals().size() + 1;
			if (CollectionUtil.isNotEmpty(form.getApprovals())) {
				for (SupplierFormSubmitionApproval sourcingFormApprovalRequest : form.getApprovals()) {
					if (sourcingFormApprovalRequest.isDone()) {
						additionalLevelStart = sourcingFormApprovalRequest.getLevel();
					} else {
						break;
					}
				}
			}
		}
		model.addAttribute("additionalLevelStart", additionalLevelStart + 1);
		model.addAttribute("additionalLevelNext", additionalLevelNext);
		model.addAttribute("eventPermissions", eventPermissions);
		model.addAttribute("userList", approvalUserList);
		model.addAttribute("formApprovalUserList", allUserList);
		model.addAttribute("supplierForm", form);
		model.addAttribute("supplierFormSubmissionItemPojo", supplierFormSubmissionItemPojo);
		List<SupplierFormSubmitionAudit> supplierFormAuditList = supplierFormSubmitionAuditService.getFormAuditByFormIdForBuyer(formSubId);
		model.addAttribute("supplierFormAuditList", supplierFormAuditList);
		return form;
	}

	@RequestMapping(path = "/deleteSuppFormSubmission", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<String> deleteSuppFormSubmission(@RequestParam("formId") String formSubId, Model model, RedirectAttributes redir) {
		HttpHeaders headers = new HttpHeaders();
		SupplierFormSubmition supplierFormObj = supplierFormSubmissionService.getSupplierformById(formSubId);
		try {
			if (supplierFormObj != null) {
				supplierFormSubmissionService.deleteSuppFormSubmission(supplierFormObj);
				headers.add("success", messageSource.getMessage("supplier.form.success.delete", new Object[] { supplierFormObj.getName() }, Global.LOCALE));
				LOG.info("Deleted Supplier Form  submission successfully for user :" + SecurityLibrary.getLoggedInUser().getLoginId());
			}
			return new ResponseEntity<String>(null, headers, HttpStatus.OK);
		} catch (DataIntegrityViolationException e) {
			LOG.error("Error while deleting used supplier form , " + e.getMessage(), e);
			headers.add("error", messageSource.getMessage("supplierForm.error.delete.dataIntegrity", new Object[] { supplierFormObj.getName() }, Global.LOCALE));
			return new ResponseEntity<String>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception e) {
			LOG.error("Error while deleting supplier form submission :" + e.getMessage(), e);
			headers.add("error", messageSource.getMessage("supplierForm.error.delete", new Object[] { supplierFormObj.getName() }, Global.LOCALE));
			return new ResponseEntity<String>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(value = "/downloadFormAttachment/{formSubId}/{formSubItemId}", method = RequestMethod.GET)
	public void downloadFormAttachment(@PathVariable String formSubId, @PathVariable String formSubItemId, HttpServletResponse response) throws IOException {
		try {
			LOG.info("formSubId: " + formSubId + " formSubItemId: " + formSubItemId);
			SupplierFormSubmissionItem formObj = supplierFormSubmissionService.findFormSubmissionItem(formSubId, formSubItemId);
			response.setContentType(formObj.getContentType());
			response.setContentLength(formObj.getFileData().length);
			response.setHeader("Content-Disposition", "attachment; filename=\"" + formObj.getFileName() + "\"");
			FileCopyUtils.copy(formObj.getFileData(), response.getOutputStream());
			response.flushBuffer();
			response.setStatus(HttpServletResponse.SC_OK);
		} catch (Exception e) {
			LOG.error("Error while File downloaded : " + e.getMessage(), e);
		}
	}

	@RequestMapping(path = "/formSubmitionApproved", method = RequestMethod.POST)
	public String formSubmitionApproved(@RequestParam String formSubId, @RequestParam String remarks, RedirectAttributes redir) {
		try {
			SupplierFormSubmition formSubmition = new SupplierFormSubmition();
			formSubmition.setId(formSubId);
			SupplierFormSubmition formSubmitionObj = approvalService.doApprovalFormSubmition(formSubmition, SecurityLibrary.getLoggedInUser(), remarks, true);
			if (formSubmitionObj != null) {
				SupplierFormSubmitionAudit audit = new SupplierFormSubmitionAudit();
				audit.setActionDate(new Date());
				audit.setAction(SupplierFormSubmitionAuditType.APPROVED);
				audit.setActionBy(SecurityLibrary.getLoggedInUser());
				audit.setBuyer(SecurityLibrary.getLoggedInUser().getBuyer());
				audit.setDescription(remarks);
				audit.setVisibilityType(SupplierFormSubAuditVisibilityType.BUYER);
				audit.setSupplierFormSubmition(formSubmitionObj);
				supplierFormSubmitionAuditService.save(audit);

				BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.APPROVED, "Supplier Form '" + formSubmitionObj.getName() + "' is Approved", SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser(), new Date(), ModuleType.SupplierForm);
				buyerAuditTrailDao.save(buyerAuditTrail);
			}
			redir.addFlashAttribute("success", messageSource.getMessage("flashsuccess.supplierForm.approved", new Object[] { (formSubmitionObj.getName() != null ? formSubmitionObj.getName() : "") }, Global.LOCALE));
		} catch (Exception e) {
			LOG.error("Error while approving supplier form :" + e.getMessage(), e);
			redir.addFlashAttribute("error", messageSource.getMessage("flasherror.while.approving.supplierform", new Object[] { e.getMessage() }, Global.LOCALE));
		}

		return "redirect:/buyer/buyerDashboard";
	}

	@RequestMapping(path = "/formSubmitionRejected", method = RequestMethod.POST)
	public String formSubmitionRejected(@RequestParam String formSubId, @RequestParam String remarks, RedirectAttributes redir) {
		try {
			SupplierFormSubmition formSubmition = new SupplierFormSubmition();
			formSubmition.setId(formSubId);
			SupplierFormSubmition formSubmitionObj = approvalService.doApprovalFormSubmition(formSubmition, SecurityLibrary.getLoggedInUser(), remarks, false);
			if (formSubmitionObj != null) {
				SupplierFormSubmitionAudit audit = new SupplierFormSubmitionAudit();
				audit.setActionDate(new Date());
				audit.setAction(SupplierFormSubmitionAuditType.REJECTED);
				audit.setActionBy(SecurityLibrary.getLoggedInUser());
				audit.setBuyer(SecurityLibrary.getLoggedInUser().getBuyer());
				audit.setDescription(remarks);
				audit.setVisibilityType(SupplierFormSubAuditVisibilityType.BUYER);
				audit.setSupplierFormSubmition(formSubmitionObj);
				supplierFormSubmitionAuditService.save(audit);
			}

			try {
				BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.REJECTED, "Supplier Form '" + formSubmitionObj.getName() + "' is Rejected", SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser(), new Date(), ModuleType.SupplierForm);
				buyerAuditTrailDao.save(buyerAuditTrail);
			} catch (Exception e) {
				LOG.info("Error to create audit trails message");
			}

			redir.addFlashAttribute("success", messageSource.getMessage("flashsuccess.supplierForm.rejected", new Object[] { (formSubmitionObj.getName() != null ? formSubmitionObj.getName() : "") }, Global.LOCALE));
		} catch (Exception e) {
			LOG.error("Error while rejecting  supplier form :" + e.getMessage(), e);
			redir.addFlashAttribute("error", messageSource.getMessage("flasherror.while.rejecting.supplierform", new Object[] { e.getMessage() }, Global.LOCALE));
		}

		return "redirect:/buyer/buyerDashboard";
	}

	@RequestMapping(path = "/updateSuppFormSubmitionApproval", method = RequestMethod.POST)
	public String updateSuppFormSubmitionApproval(@ModelAttribute("supplierForm") SupplierFormSubmition supplierFormSubmition, RedirectAttributes redir, HttpSession session) {
		try {
			supplierFormSubmition = supplierFormSubmissionService.updateSupplierFormApproval(supplierFormSubmition, SecurityLibrary.getLoggedInUser());
			redir.addFlashAttribute("success", messageSource.getMessage("flashsuccess.approval.updated", new Object[] {}, Global.LOCALE));
		} catch (Exception e) {
			LOG.info("Error while Updating Approval :" + e.getMessage(), e);
			redir.addFlashAttribute("error", messageSource.getMessage("flasherror.while.updating.approval", new Object[] { e.getMessage() }, Global.LOCALE));
		}
		return "redirect:/buyer/supplierFormSubView/" + supplierFormSubmition.getId();
	}

	@RequestMapping(path = "/saveSuppFormAddtionalApproval/{formId}", method = RequestMethod.POST)
	public String saveSuppFormAddtionalApproval(@ModelAttribute("supplierForm") SupplierFormSubmition supplierFormSubmition, @PathVariable String formId, RedirectAttributes redir) {
		try {
			supplierFormSubmissionService.addAdditionalApprover(supplierFormSubmition, formId, SecurityLibrary.getLoggedInUser());
		} catch (Exception e) {
			LOG.error("Error while storing Saving Supplier Form Approval details : " + e.getMessage(), e);
			redir.addFlashAttribute("errors", "Error while Saving Supplier Form Approval details for : " + formId + ", message : " + e.getMessage());
			return "redirect:/buyer/supplierFormSubView/" + formId;
		}
		redir.addFlashAttribute("success", messageSource.getMessage("supplier.form.additional.approval.success", new Object[] {}, Global.LOCALE));
		return "redirect:/buyer/supplierFormSubView/" + formId;
	}

	@RequestMapping(path = "/finishSuppFormAdditionalApproval/{formId}", method = RequestMethod.POST)
	public String finishSuppFormAdditionalApproval(@ModelAttribute("supplierForm") SupplierFormSubmition supplierFormAdditionalApprover, @PathVariable String formId, RedirectAttributes redir) {
		try {
			supplierFormSubmissionService.finishAdditionalApprover(supplierFormAdditionalApprover, formId, SecurityLibrary.getLoggedInUser());
		} catch (Exception e) {
			LOG.error("Error while storing Saving Supplier Form Approval details : " + e.getMessage(), e);
			redir.addFlashAttribute("errors", "Error while Saving Supplier Form Approval details :" + e.getMessage());
			return "redirect:/buyer/supplierFormSubView/" + formId;
		}
		redir.addFlashAttribute("success", messageSource.getMessage("form.additional.approval.finish.success", new Object[] {}, Global.LOCALE));

		return "redirect:/buyer/supplierFormSubView/" + formId;
	}

}