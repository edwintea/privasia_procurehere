package com.privasia.procurehere.web.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpSession;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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
import com.privasia.procurehere.core.entity.Buyer;
import com.privasia.procurehere.core.entity.BuyerAuditTrail;
import com.privasia.procurehere.core.entity.ProcurementCategories;
import com.privasia.procurehere.core.entity.SupplierPerformanceTemplate;
import com.privasia.procurehere.core.entity.SupplierPerformanceTemplateCriteria;
import com.privasia.procurehere.core.entity.User;
import com.privasia.procurehere.core.enums.AuditTypes;
import com.privasia.procurehere.core.enums.ModuleType;
import com.privasia.procurehere.core.enums.SourcingStatus;
import com.privasia.procurehere.core.enums.Status;
import com.privasia.procurehere.core.enums.TenantType;
import com.privasia.procurehere.core.enums.UserType;
import com.privasia.procurehere.core.exceptions.ApplicationException;
import com.privasia.procurehere.core.pojo.TableData;
import com.privasia.procurehere.core.pojo.TableDataInput;
import com.privasia.procurehere.core.pojo.UserPojo;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.SecurityLibrary;
import com.privasia.procurehere.core.utils.StringUtils;
import com.privasia.procurehere.service.ProcurementCategoriesService;
import com.privasia.procurehere.service.SupplierPerformanceFormService;
import com.privasia.procurehere.service.SupplierPerformanceTemplateCriteriaService;
import com.privasia.procurehere.service.SupplierPerformanceTemplateService;
import com.privasia.procurehere.service.UserService;
import com.privasia.procurehere.web.editors.ProcurementCategoriesEditor;

/**
 * @author Jayshree
 */
@Controller
@RequestMapping("/buyer")
public class SupplierPerformanceTemplateController {

	private static final Logger LOG = LogManager.getLogger(SupplierPerformanceTemplateController.class);

	@Autowired
	MessageSource messageSource;

	@Autowired
	UserService userService;

	@Autowired
	ProcurementCategoriesService procurementCategoriesService;

	@Autowired
	SupplierPerformanceTemplateService supplierPerformanceTemplateService;

	@Autowired
	BuyerAuditTrailDao buyerAuditTrailDao;

	@Autowired
	ProcurementCategoriesEditor procurementCategoriesEditor;

	@Autowired
	SupplierPerformanceTemplateCriteriaService sPTemplateCriteriaService;

	@Autowired
	SupplierPerformanceFormService supplierPerformanceFormService;

	@InitBinder
	public void InitBinder(WebDataBinder binder, HttpSession session) {
		binder.registerCustomEditor(ProcurementCategories.class, procurementCategoriesEditor);
	}

	@ModelAttribute("statusList")
	public List<SourcingStatus> getStatusList() {
		return Arrays.asList(SourcingStatus.values());
	}

	@RequestMapping(path = "/spTemplateList", method = RequestMethod.GET)
	public String supplierPerformanceTemplateList(Model model) {
		LOG.info("Controll...........  ");
		return "spTemplateList";
	}

	@GetMapping("/spTemplateListData")
	public ResponseEntity<TableData<SupplierPerformanceTemplate>> spTemplateListData(TableDataInput input) {
		try {
			LOG.info("Start : " + input.getStart() + " Length : " + input.getLength() + " Sort : " + input.getSort());
			LOG.info(">>>>>>>>>>>>> Table Data Input : " + input.toString());

			TenantType tenantType = TenantType.BUYER;

			if (SecurityLibrary.getLoggedInUser().getBuyer() != null) {
				tenantType = TenantType.BUYER;
			} else if (SecurityLibrary.getLoggedInUser().getOwner() != null) {
				tenantType = TenantType.OWNER;
			}

			TableData<SupplierPerformanceTemplate> data = new TableData<SupplierPerformanceTemplate>(supplierPerformanceTemplateService.findSPTemplateForTenant(SecurityLibrary.getLoggedInUserTenantId(), input, tenantType));
			data.setDraw(input.getDraw());
			long totalFilterCount = supplierPerformanceTemplateService.findTotalFilteredSPTemplateForTenant(SecurityLibrary.getLoggedInUserTenantId(), input, tenantType);
			data.setRecordsFiltered(totalFilterCount);
			long totalCount = supplierPerformanceTemplateService.findTotalActiveSPTemplateForTenant(SecurityLibrary.getLoggedInUserTenantId(), tenantType);
			data.setRecordsTotal(totalCount);
			return new ResponseEntity<TableData<SupplierPerformanceTemplate>>(data, HttpStatus.OK);
		} catch (Exception e) {
			LOG.error("Error fetching supplier performance template list : " + e.getMessage(), e);
			HttpHeaders headers = new HttpHeaders();
			headers.add("error", "Error fetching supplier performance template list : " + e.getMessage());
			return new ResponseEntity<TableData<SupplierPerformanceTemplate>>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(path = "/createPerformanceTemplate", method = RequestMethod.GET)
	public String createPerformanceTemplate(Model model) {
		try {
			SupplierPerformanceTemplate template = new SupplierPerformanceTemplate();
			// template.setStatus(SourcingStatDus.ACTIVE);
			model.addAttribute("supplierPerformanceTemplate", template);
			model.addAttribute("procurementCategoryList", procurementCategoriesService.getAllActiveProcurementCategory(SecurityLibrary.getLoggedInUserTenantId()));
			model.addAttribute("isTemplateUsed", false);
		} catch (Exception e) {
			LOG.info("Error while fetching userList " + e.getMessage());

		}
		model.addAttribute("statusList", Arrays.asList(Status.values()));
		model.addAttribute("createTemplate", messageSource.getMessage("sourcingtemplate.create.template", new Object[] {}, LocaleContextHolder.getLocale()));
		model.addAttribute("button", messageSource.getMessage("save.next.button", new Object[] {}, LocaleContextHolder.getLocale()));
		List<UserPojo> userList = userService.fetchAllUsersForTenant(SecurityLibrary.getLoggedInUserTenantId(), "", UserType.NORMAL_USER);
		model.addAttribute("assignedUserListDropDown", userList);

		List<User> assignedUserList = new ArrayList<>();
		model.addAttribute("assignedUserList", assignedUserList);

		return "performanceTemplateDetails";
	}

	@RequestMapping(path = "/saveSupplierPerformanceTemplate", method = RequestMethod.POST)
	public String saveSupplierPerformanceTemplate(@ModelAttribute("supplierPerformanceTemplate") SupplierPerformanceTemplate spTemplate, @RequestParam("userId") String[] userId, Model model, RedirectAttributes redirect) {
		SupplierPerformanceTemplate persistObject = null;
		boolean isDuplicateName = true;
		LOG.info("Template id :: " + spTemplate.getId() + " Name :: " + spTemplate.getTemplateName());
		model.addAttribute("supplierPerformanceTemplate", spTemplate);
		model.addAttribute("procurementCategoryList", procurementCategoriesService.getAllActiveProcurementCategory(SecurityLibrary.getLoggedInUserTenantId()));

		try {

			boolean allowStatusUpdate = true;
			String warningMsg = "";

			if (StringUtils.checkString(spTemplate.getId()).length() > 0) {

				persistObject = supplierPerformanceTemplateService.getSupplierPerformanceTemplatebyId(spTemplate.getId());

				List<SupplierPerformanceTemplateCriteria> criteriaList = sPTemplateCriteriaService.getAllCriteriaByOrder(spTemplate.getId());

				// If user is changing status from Inactive to Active check if criteria is valid else show warning.
				if (spTemplate.getStatus() == SourcingStatus.ACTIVE && persistObject.getStatus() == SourcingStatus.INACTIVE) {

					List<String> notSubCriteriaAddedCriterias = sPTemplateCriteriaService.getSubCriteriaNotAddedCriteriaIdsByTemplateId(spTemplate.getId());
					if (CollectionUtil.isNotEmpty(notSubCriteriaAddedCriterias)) {
						String names = String.join(",", notSubCriteriaAddedCriterias);
						allowStatusUpdate = false;
						warningMsg = messageSource.getMessage("template.criteria.not.subcriteria.added", new Object[] { names }, Global.LOCALE);
					}
					if (allowStatusUpdate) {
						BigDecimal sumOfWeightageOfCriteria = sPTemplateCriteriaService.getSumOfWeightageOfAllCriteriaByTemplateId(spTemplate.getId(), null);
						if (sumOfWeightageOfCriteria.compareTo(new BigDecimal(100)) < 0) {
							allowStatusUpdate = false;
							warningMsg = "Summation of Weightage of all Criteria must be 100%";
						} else if (sumOfWeightageOfCriteria.compareTo(new BigDecimal(100)) > 0) {
							allowStatusUpdate = false;
							warningMsg = "Summation of Weightage of all Criteria exceeds 100%";
						}

						if (allowStatusUpdate && CollectionUtil.isNotEmpty(criteriaList)) {
							for (SupplierPerformanceTemplateCriteria templCri : criteriaList) {
								if (templCri.getParent() == null) {
									BigDecimal sumOfWeightageOfSubCriteria = sPTemplateCriteriaService.getSumOfWeightageOfAllSubCriteriaForCriteriaByCriteriaId(null, templCri.getId(), templCri.getTemplate().getId());
									if (sumOfWeightageOfSubCriteria.compareTo(new BigDecimal(100)) < 0) {
										allowStatusUpdate = false;
										warningMsg = "Summation of Weightage of all Criteria must be 100%";
									} else if (sumOfWeightageOfSubCriteria.compareTo(new BigDecimal(100)) > 0) {
										allowStatusUpdate = false;
										warningMsg = "Summation of Weightage of all Criteria exceeds 100%";
									}
								}
							}
						}
					}
				}

				// if the template status is draft and user is trying to make it active then dont change the status to
				// active as it will be done on finish.
				if (SourcingStatus.DRAFT == persistObject.getStatus() && spTemplate.getStatus() == SourcingStatus.ACTIVE) {
					allowStatusUpdate = false;
				}

				LOG.info("allowStatusUpdate " + allowStatusUpdate);

				BigDecimal dbMaxScore = persistObject.getMaximumScore();
				
				boolean doAuditStatusChange = spTemplate.getStatus() != persistObject.getStatus();
				// if (!persistObject.getIsTemplateUsed()) {
				persistObject.setTemplateName(spTemplate.getTemplateName());
				persistObject.setTemplateDescription(spTemplate.getTemplateDescription());
				LOG.info("Setting modified date : " + new Date().toString());
				persistObject.setModifiedDate(new Date());
				persistObject.setModifiedBy(SecurityLibrary.getLoggedInUser());
				persistObject.setMaximumScore(spTemplate.getMaximumScore());
				persistObject.setProcurementCategory(spTemplate.getProcurementCategory());
				persistObject.setProcurementCategoryVisible(spTemplate.getProcurementCategoryVisible());
				persistObject.setDetailCompleted(Boolean.TRUE);
				persistObject.setProcurementCategoryOptional(spTemplate.getProcurementCategoryOptional());
				persistObject.setProcurementCategoryDisabled(spTemplate.getProcurementCategoryDisabled());
				if (allowStatusUpdate) {
					persistObject.setStatus(spTemplate.getStatus());
				}

				// model.addAttribute("isTemplateUsed", persistObject.getIsTemplateUsed());
				List<User> userList = userService.fetchAllActiveUsersForTenant(SecurityLibrary.getLoggedInUserTenantId());
				model.addAttribute("userList", userList);
				// update Approvals

				supplierPerformanceTemplateService.deleteUsersForSPTemplate(spTemplate.getId());
				for (String userID : userId) {
					User user = userService.getUsersForSupplierPerformanceTemplateById(userID);
					List<SupplierPerformanceTemplate> assignedTemplateList = user.getAssignedSupplierPerformanceTemplates();
					if (assignedTemplateList == null) {
						assignedTemplateList = new ArrayList<SupplierPerformanceTemplate>();
					}
					assignedTemplateList.add(spTemplate);
					user.setAssignedSupplierPerformanceTemplates(assignedTemplateList);
					userService.updateUser(user);
				}

				// update Sourcing Form
				persistObject = supplierPerformanceTemplateService.saveOrUpdateSupplierPerformanceTemplate(persistObject);

				// Update the sub criteria max score if it is updated in template
				LOG.info("DB Max Score : " + dbMaxScore + " UI Max Score : " + spTemplate.getMaximumScore());
				if (dbMaxScore != null && spTemplate.getMaximumScore() != null && spTemplate.getMaximumScore().floatValue() != dbMaxScore.floatValue()) {
					for (SupplierPerformanceTemplateCriteria c : criteriaList) {
						if(c.getOrder() > 0) {
							LOG.info("Updating max score for Criteria : " + c.getName() + " (" + c.getId() + ")");
							c.setMaximumScore(spTemplate.getMaximumScore());
							sPTemplateCriteriaService.updateSupplierPerformanceTemplateCriteria(c);
						}
					}
				}

				if (doAuditStatusChange) {
					try {
						BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(spTemplate.getStatus() == SourcingStatus.ACTIVE ? AuditTypes.ACTIVE : AuditTypes.INACTIVE, " Supplier Performance Template '" + spTemplate.getTemplateName() + "' is changed  to " + persistObject.getStatus(), SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser(), new Date(), ModuleType.SupplierPerformanceTemplate);
						buyerAuditTrailDao.save(buyerAuditTrail);
					} catch (Exception e) {
						LOG.error("Error while recording audit " + e.getMessage(), e);
					}
				} else {
					try {
						BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.UPDATE, "Supplier Performance Template '" + persistObject.getTemplateName() + "' updated ", SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser(), new Date(), ModuleType.SupplierPerformanceTemplate);
						buyerAuditTrailDao.save(buyerAuditTrail);
					} catch (Exception e) {
						LOG.error("Error while recording audit " + e.getMessage(), e);
					}
				}

				LOG.info("Template Name and description  " + persistObject.getTemplateName() + " " + persistObject.getTemplateDescription());
				model.addAttribute("tempId", persistObject.getId());
				model.addAttribute("status", persistObject.getStatus().toString() == "INACTIVE" ? true : false);
				// }
			} else {
				Buyer buyer = new Buyer();
				buyer.setId(SecurityLibrary.getLoggedInUserTenantId());

				persistObject = new SupplierPerformanceTemplate();
				if (spTemplate.getStatus() == SourcingStatus.INACTIVE) {
					persistObject.setStatus(spTemplate.getStatus());
				} else {
					persistObject.setStatus(SourcingStatus.DRAFT);
				}
				persistObject.setTemplateName(spTemplate.getTemplateName());
				persistObject.setTemplateDescription(spTemplate.getTemplateDescription());
				persistObject.setCreatedBy(SecurityLibrary.getLoggedInUser());
				persistObject.setCreatedDate(new Date());
				persistObject.setBuyer(buyer);
				persistObject.setMaximumScore(spTemplate.getMaximumScore());
				persistObject.setProcurementCategory(spTemplate.getProcurementCategory());
				persistObject.setProcurementCategoryVisible(spTemplate.getProcurementCategoryVisible());
				persistObject.setProcurementCategoryOptional(spTemplate.getProcurementCategoryOptional());
				persistObject.setProcurementCategoryDisabled(spTemplate.getProcurementCategoryDisabled());
				// model.addAttribute("isTemplateUsed", persistObject.getIsTemplateUsed());

				if (supplierPerformanceTemplateService.isTemplateExistsByTemplateName(spTemplate.getTemplateName(), SecurityLibrary.getLoggedInUserTenantId())) {
					LOG.info("Duplicate name " + spTemplate.getTemplateName());
					model.addAttribute("button", messageSource.getMessage("save.next.button", new Object[] {}, LocaleContextHolder.getLocale()));
					model.addAttribute("supplierPerformanceTemplate", spTemplate);
					isDuplicateName = true;
					model.addAttribute("error", "Duplicate template Name,template with this name is already available ");
					return "performanceTemplateDetails";
				}

				persistObject.setDetailCompleted(Boolean.TRUE);
				persistObject = supplierPerformanceTemplateService.saveSupplierPerformanceTemplate(persistObject);
				for (String usersId : userId) {
					User user = userService.getUsersForSupplierPerformanceTemplateById(usersId);
					List<SupplierPerformanceTemplate> assignedTemplateList = user.getAssignedSupplierPerformanceTemplates();
					if (CollectionUtil.isNotEmpty(assignedTemplateList)) {
						assignedTemplateList.add(persistObject);
						user.setAssignedSupplierPerformanceTemplates(assignedTemplateList);
					} else {
						assignedTemplateList = new ArrayList<>();
						assignedTemplateList.add(persistObject);
						user.setAssignedSupplierPerformanceTemplates(assignedTemplateList);
					}
					userService.updateUser(user);
				}
			}

			try {
				BuyerAuditTrail ownerAuditTrail = new BuyerAuditTrail(AuditTypes.UPDATE, "Supplier Performance Template '" + spTemplate.getTemplateName() + "' is updated", SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser(), new Date(), ModuleType.SupplierPerformanceTemplate);
				buyerAuditTrailDao.save(ownerAuditTrail);
			} catch (Exception e) {
				LOG.info("Error to Saving buyer audit trails message" + e.getMessage(), e);
			}

			if (!allowStatusUpdate && StringUtils.checkString(warningMsg).length() > 0) {
				redirect.addFlashAttribute("warn", "Status cannot be changed to ACTIVE since " + warningMsg);
			}

			return "redirect:/buyer/supplierPerformanceTemplateCriteriaList/" + persistObject.getId();

		} catch (Exception e) {
			model.addAttribute("supplierPerformanceTemplate", spTemplate);
			model.addAttribute("spTemplate", spTemplate);
			List<User> userList = userService.fetchAllActiveUserForTenantId(SecurityLibrary.getLoggedInUserTenantId());
			model.addAttribute("userList", userList);
			model.addAttribute("isDuplicateName", isDuplicateName);
			LOG.error("error while saving SP template :: " + e.getMessage(), e);
			model.addAttribute("error", messageSource.getMessage("error.while.saving.sptemplate", new Object[] { e.getMessage() }, Global.LOCALE));
			return "performanceTemplateDetails";
		}
	}

	@RequestMapping(path = "/editSPTemplate", method = RequestMethod.GET)
	public String editSPTemplate(@RequestParam String id, Model model) {
		LOG.info("SP Template id ::" + id);
		SupplierPerformanceTemplate template = supplierPerformanceTemplateService.getSupplierPerformanceTemplateById(id);
		LOG.info("Name " + template.getTemplateName());
		model.addAttribute("supplierPerformanceTemplate", template);
		model.addAttribute("template", template);
		model.addAttribute("spTemplate", template);
		model.addAttribute("templateId", template.getId());
		List<String> list = supplierPerformanceFormService.getSPFormIdListByTemplateId(id);
		boolean isTemplateUsed;
		if (CollectionUtil.isEmpty(list)) {
			isTemplateUsed = false;
		} else {
			LOG.info("----------------- " + list.size());
			isTemplateUsed = true;
		}
		LOG.info("================ isTemplateUsed " + isTemplateUsed);

		List<UserPojo> userList = userService.fetchAllUsersForTenantForSourcingTemplate(SecurityLibrary.getLoggedInUserTenantId(), "", UserType.NORMAL_USER, id);
		model.addAttribute("assignedUserListDropDown", userList);

		model.addAttribute("procurementCategoryList", procurementCategoriesService.getAllActiveProcurementCategory(SecurityLibrary.getLoggedInUserTenantId()));
		model.addAttribute("statusList", Arrays.asList(Status.values()));
		model.addAttribute("button", messageSource.getMessage("update.next.button", new Object[] {}, LocaleContextHolder.getLocale()));
		model.addAttribute("isTemplateUsed", isTemplateUsed);
		model.addAttribute("createTemplate", messageSource.getMessage("sourcingtemplate.update.template", new Object[] {}, LocaleContextHolder.getLocale()));
		LOG.info("check if template id used " + isTemplateUsed);

		List<String> assignedUserId = supplierPerformanceTemplateService.getUserIdListByTemplateId(id);
		List<User> assignedUserList = new ArrayList<>();
		if (CollectionUtil.isNotEmpty(assignedUserId)) {
			for (String assgnedUser : assignedUserId) {
				User user = userService.getUsersNameAndId(assgnedUser);
				assignedUserList.add(user);
			}
		}
		model.addAttribute("assignedUserList", assignedUserList);

		return "performanceTemplateDetails";
	}

	@RequestMapping(path = "/finishSPTemplate/{templateId}", method = RequestMethod.GET)
	public String finishTemplate(@PathVariable("templateId") String templateId, RedirectAttributes model) {
		LOG.info("finish template method is called with Template id " + templateId);
		try {
			SupplierPerformanceTemplate template = supplierPerformanceTemplateService.getSupplierPerformanceTemplateById(templateId);
			List<SupplierPerformanceTemplateCriteria> criteriaList = sPTemplateCriteriaService.getAllCriteriaByOrder(templateId);
			long criteriaCount = sPTemplateCriteriaService.getCriteriaCount(templateId);
			if (!(criteriaCount > 0)) {
				throw new ApplicationException("Please add criteria in Template");
			}
			// Validate Sub criteria inside Criteria
			List<String> notSubCriteriaAddedCriterias = sPTemplateCriteriaService.getSubCriteriaNotAddedCriteriaIdsByTemplateId(templateId);
			if (CollectionUtil.isNotEmpty(notSubCriteriaAddedCriterias)) {
				for (String s : notSubCriteriaAddedCriterias) {
					LOG.info("Criteria : " + s);
				}
				String names = String.join(",", notSubCriteriaAddedCriterias);
				model.addFlashAttribute("error", messageSource.getMessage("template.criteria.not.subcriteria.added", new Object[] { names }, Global.LOCALE));
				return "redirect:/buyer/supplierPerformanceTemplateCriteriaList/" + templateId;
			}

			BigDecimal sumOfWeightageOfCriteria = sPTemplateCriteriaService.getSumOfWeightageOfAllCriteriaByTemplateId(template.getId(), null);
			if (sumOfWeightageOfCriteria.compareTo(new BigDecimal(100)) < 0) {
				model.addFlashAttribute("error", "Summation of Weightage of all Criteria must be 100%");
				return "redirect:/buyer/supplierPerformanceTemplateCriteriaList/" + templateId;
			} else if (sumOfWeightageOfCriteria.compareTo(new BigDecimal(100)) > 0) {
				model.addFlashAttribute("error", "Summation of Weightage of all Criteria exceeds 100%");
				return "redirect:/buyer/supplierPerformanceTemplateCriteriaList/" + templateId;
			}

			if (CollectionUtil.isNotEmpty(criteriaList)) {
				for (SupplierPerformanceTemplateCriteria templCri : criteriaList) {
					if (templCri.getParent() == null) {
						BigDecimal sumOfWeightageOfSubCriteria = sPTemplateCriteriaService.getSumOfWeightageOfAllSubCriteriaForCriteriaByCriteriaId(null, templCri.getId(), templCri.getTemplate().getId());
						if (sumOfWeightageOfSubCriteria.compareTo(new BigDecimal(100)) < 0) {
							model.addFlashAttribute("error", "Summation of Weightage of all Sub Criteria for a Criteria must be 100%");
							return "redirect:/buyer/supplierPerformanceTemplateCriteriaList/" + templateId;
						} else if (sumOfWeightageOfSubCriteria.compareTo(new BigDecimal(100)) > 0) {
							model.addFlashAttribute("error", "Summation of Weightage of all Sub Criteria for a Criteria exceeds 100%");
							return "redirect:/buyer/supplierPerformanceTemplateCriteriaList/" + templateId;
						}
					}
				}
			}

			if (CollectionUtil.isNotEmpty(criteriaList)) {
				if (Boolean.FALSE == template.getPerformanceCriteriaCompleted() && template.getStatus() == SourcingStatus.DRAFT) {
					template.setStatus(SourcingStatus.ACTIVE);
				}

				template.setPerformanceCriteriaCompleted(true);
				template.setModifiedBy(SecurityLibrary.getLoggedInUser());
				template.setModifiedDate(new Date());
				template = supplierPerformanceTemplateService.updateSupplierPerformanceTemplate(template);
				LOG.info("Status after finishing the Template " + template.getStatus());

				if (Boolean.TRUE == template.getPerformanceCriteriaCompleted()) {
					model.addFlashAttribute("success", messageSource.getMessage("flashsuccess.sp.template.updated", new Object[] { template.getTemplateName() }, Global.LOCALE));
				} else {
					model.addFlashAttribute("success", messageSource.getMessage("flashsuccess.sp.template.created", new Object[] { template.getTemplateName() }, Global.LOCALE));
				}

				return "redirect:/buyer/spTemplateList";
			} else {
				throw new ApplicationException();
			}
		} catch (ApplicationException e) {
			model.addFlashAttribute("error", messageSource.getMessage("template.criteria.mandatory", new Object[] {}, Global.LOCALE));
			return "redirect:/buyer/supplierPerformanceTemplateCriteriaList/" + templateId;
		} catch (Exception e) {
			model.addFlashAttribute("error", messageSource.getMessage("error.finishing.template", new Object[] {}, Global.LOCALE));
			return "redirect:/buyer/supplierPerformanceTemplateCriteriaList/" + templateId;
		}
	}

	@RequestMapping(value = "/copySPTemplate", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<String> copySPTemplate(@RequestParam(value = "templateId") String templateId, @RequestParam(value = "templateName") String templateName, @RequestParam(value = "templateDesc") String templateDesc) {
		HttpHeaders headers = new HttpHeaders();
		String newTemplateId = "";
		LOG.info("Copy SP template and Template id...... " + templateId);
		try {
			SupplierPerformanceTemplate template = new SupplierPerformanceTemplate();
			template.setTemplateName(templateName);
			template.setTemplateDescription(templateDesc);

			if (!supplierPerformanceTemplateService.isExists(templateId, template.getTemplateName())) {

				SupplierPerformanceTemplate temp = supplierPerformanceTemplateService.copyTemplate(templateId, template, SecurityLibrary.getLoggedInUser());

				newTemplateId = template.getId();
				LOG.info("Copied Template Created and Saved Sucessfully  :" + temp.getTemplateName());
				headers.add("success", messageSource.getMessage("template.save.success", new Object[] { temp.getTemplateName() }, Global.LOCALE));
			} else {
				headers.add("error", messageSource.getMessage("template.error.duplicate", new Object[] { template.getTemplateName() }, Global.LOCALE));
				return new ResponseEntity<String>(null, headers, HttpStatus.BAD_REQUEST);
			}
		} catch (Exception e) {
			LOG.error("Error while SPT save as : " + e.getMessage(), e);
			headers.add("error", "Error while Copying SP template : " + e.getMessage());
			return new ResponseEntity<String>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<String>(newTemplateId, headers, HttpStatus.OK);
	}

	@RequestMapping(path = "/deleteSPTemplate", method = RequestMethod.GET)
	public String deleteTemplate(@RequestParam String id, Model model) {
		LOG.info("delete Sourcing Form method SourcingForm Id is " + id);
		SupplierPerformanceTemplate spTemplate = null;
		if (id != null) {
			spTemplate = supplierPerformanceTemplateService.getSupplierPerformanceTemplateById(id);
		}
		LOG.info(spTemplate.getId());
		if (spTemplate != null) {
			List<String> list = supplierPerformanceFormService.getSPFormIdListByTemplateId(id);
			boolean isTemplateUsed;
			if (CollectionUtil.isEmpty(list)) {
				isTemplateUsed = false;
			} else {
				LOG.info("----------------- " + list.size());
				isTemplateUsed = true;
			}

			try {
				LOG.info("isTemplateUsed ...... " + isTemplateUsed);
				if (isTemplateUsed) {
					throw new ApplicationException();
				}
				supplierPerformanceTemplateService.deleteSupplierPerformanceTemplate(spTemplate, SecurityLibrary.getLoggedInUser());
				model.addAttribute("success", messageSource.getMessage("template.delete.success", new Object[] {}, Global.LOCALE));
				LOG.info("deleted Sorcing Form  " + supplierPerformanceTemplateService.getSupplierPerformanceTemplateById(id));

			} catch (DataIntegrityViolationException e) {
				LOG.error("Error while deleting Template , " + e.getMessage(), e);
				model.addAttribute("error", messageSource.getMessage("sptemplate.error.delete.child.exist", new Object[] { spTemplate.getTemplateName() }, Global.LOCALE));
			} catch (ApplicationException e) {
				LOG.error("Error while deleting SP Template  [ " + spTemplate.getTemplateName() + " ]" + e.getMessage(), e);
				model.addAttribute("error", messageSource.getMessage("used.sptemplate.delete", new Object[] { spTemplate.getTemplateName() }, Global.LOCALE));
			} catch (Exception e) {
				LOG.error("Error while deleting SP Template  [ " + spTemplate.getTemplateName() + " ]" + e.getMessage(), e);
				model.addAttribute("error", messageSource.getMessage("template.error.delete", new Object[] { spTemplate.getTemplateName() }, Global.LOCALE));
			}

		}
		return "spTemplateList";
	}

	@RequestMapping(path = "/getAssignedSpTemplateForUser", method = RequestMethod.GET)
	public ResponseEntity<List<SupplierPerformanceTemplate>> getAssignedSpTemplateForUser(@RequestParam("userId") String userId) {
		HttpHeaders headers = new HttpHeaders();
		try {
			List<SupplierPerformanceTemplate> spTemplates = supplierPerformanceTemplateService.getAllAssignedSpTemplateIdsForUser(userId);
			return new ResponseEntity<List<SupplierPerformanceTemplate>>(spTemplates, headers, HttpStatus.OK);
		} catch (Exception e) {
			LOG.error("Error fetching Supplier Performance Templates : " + e.getMessage(), e);
			headers.add("error", "Error fetching Supplier Performance Templates : " + e.getMessage());
			return new ResponseEntity<List<SupplierPerformanceTemplate>>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

}
