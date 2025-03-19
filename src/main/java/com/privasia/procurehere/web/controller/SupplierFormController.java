package com.privasia.procurehere.web.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.privasia.procurehere.core.dao.BuyerAuditTrailDao;
import com.privasia.procurehere.core.entity.BuyerAuditTrail;
import com.privasia.procurehere.core.entity.OwnerSettings;
import com.privasia.procurehere.core.entity.SupplierForm;
import com.privasia.procurehere.core.entity.SupplierFormApproval;
import com.privasia.procurehere.core.entity.SupplierFormApprovalUser;
import com.privasia.procurehere.core.entity.SupplierFormItem;
import com.privasia.procurehere.core.entity.SupplierFormItemAttachment;
import com.privasia.procurehere.core.entity.SupplierFormItemOption;
import com.privasia.procurehere.core.entity.User;
import com.privasia.procurehere.core.enums.AuditTypes;
import com.privasia.procurehere.core.enums.CqType;
import com.privasia.procurehere.core.enums.ModuleType;
import com.privasia.procurehere.core.enums.SupplierFormsStatus;
import com.privasia.procurehere.core.exceptions.ApplicationException;
import com.privasia.procurehere.core.exceptions.NotAllowedException;
import com.privasia.procurehere.core.parsers.SupplierFormFileParser;
import com.privasia.procurehere.core.pojo.SupplierFormItemPojo;
import com.privasia.procurehere.core.pojo.SupplierFormPojo;
import com.privasia.procurehere.core.pojo.TableData;
import com.privasia.procurehere.core.pojo.TableDataInput;
import com.privasia.procurehere.core.pojo.UserPojo;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.SecurityLibrary;
import com.privasia.procurehere.core.utils.StringUtils;
import com.privasia.procurehere.service.OwnerSettingsService;
import com.privasia.procurehere.service.SupplierFormService;
import com.privasia.procurehere.service.SupplierFormSubmissionService;
import com.privasia.procurehere.service.UserService;
import com.privasia.procurehere.web.editors.SupplierFormApprovalEditor;
import com.privasia.procurehere.web.editors.UserEditor;

/**
 * @author pooja
 */
@Controller
@RequestMapping(path = "/buyer")
public class SupplierFormController {
	private static final Logger LOG = LogManager.getLogger(Global.BUYER_LOG);

	@Autowired
	MessageSource messageSource;

	@Autowired
	SupplierFormService supplierFormService;

	@Autowired
	SupplierFormSubmissionService supplierFormSubmissionService;

	@Autowired
	ServletContext context;

	@Autowired
	OwnerSettingsService ownerSettingsService;

	@Autowired
	UserService userService;

	@Autowired
	SupplierFormApprovalEditor supplierFormApprovalEditor;

	@Autowired
	UserEditor userEditor;
	
	@Autowired
	BuyerAuditTrailDao buyerAuditTrailDao;

	@ModelAttribute("statusList")
	public List<SupplierFormsStatus> getStatusList() {
		return Arrays.asList(SupplierFormsStatus.values());
	}

	@ModelAttribute("ownerSettings")
	public OwnerSettings getOwnersettings() {
		return ownerSettingsService.getOwnersettings();
	}

	@InitBinder
	public void InitBinder(WebDataBinder binder, HttpSession session) {
		binder.registerCustomEditor(User.class, userEditor);
		binder.registerCustomEditor(SupplierFormApprovalUser.class, supplierFormApprovalEditor);
	}

	@RequestMapping(path = "/supplierFormList")
	public String supplierFormList(Model model) {
		LOG.info("getting supplier form List view....");
		return "supplierFormsList";
	}

	@RequestMapping(path = "/supplierForm", method = RequestMethod.GET)
	public ModelAndView createSupplierForm(Model model) {
		model.addAttribute("btnValue", "Create");
		model.addAttribute("cqTypes", CqType.values());
		SupplierForm supplierFormObj = new SupplierForm();
		supplierFormObj.setTenantId(SecurityLibrary.getLoggedInUserTenantId());
		supplierFormObj.setCreatedBy(SecurityLibrary.getLoggedInUser());
		supplierFormObj.setCreatedDate(new Date());
		supplierFormObj.setStatus(SupplierFormsStatus.DRAFT);
		supplierFormObj = supplierFormService.saveOrUpdate(supplierFormObj);

		List<User> approvalUserList = new ArrayList<User>();
		List<UserPojo> appuserList = userService.fetchAllUsersForTenant(SecurityLibrary.getLoggedInUserTenantId(), "", null);
		if (CollectionUtil.isNotEmpty(appuserList)) {
			for (UserPojo user : appuserList) {
				User u = new User(user.getId(), user.getLoginId(), user.getName(), user.getCommunicationEmail(), user.isEmailNotifications(), user.getTenantId(), user.isDeleted());
				if (!approvalUserList.contains(u)) {
					approvalUserList.add(u);
				}
			}
		}

		model.addAttribute("isAssignedForm", false);
		model.addAttribute("userList", approvalUserList);
		model.addAttribute("formApprovalUserList", approvalUserList);
		model.addAttribute("formItemList", new ArrayList<SupplierFormItem>());
		return new ModelAndView("supplierFormCreate", "supplierFormObj", supplierFormObj);
	}

	@RequestMapping(path = "/supplierFormsListData", method = RequestMethod.GET)
	public ResponseEntity<TableData<SupplierFormPojo>> supplierFormListData(TableDataInput input) {
		try {
			LOG.info("Getting supplier Form data for user:" + SecurityLibrary.getLoggedInUserTenantId());
			LOG.info("Start : " + input.getStart() + " Length : " + input.getLength() + " Sort : " + input.getSort());
			TableData<SupplierFormPojo> data = new TableData<SupplierFormPojo>(supplierFormService.findSupplierFormsByTeantId(SecurityLibrary.getLoggedInUserTenantId(), input));
			data.setDraw(input.getDraw());
			long totalFilterCount = supplierFormService.findTotalFilteredSupplierFormsForTenant(SecurityLibrary.getLoggedInUserTenantId(), input);
			data.setRecordsFiltered(totalFilterCount);
			data.setRecordsTotal(totalFilterCount);
			return new ResponseEntity<TableData<SupplierFormPojo>>(data, HttpStatus.OK);
		} catch (Exception e) {
			LOG.error("Error while fetching supplier Form list:" + e.getMessage(), e);
			HttpHeaders headers = new HttpHeaders();
			headers.add("error", "Error while fetching supplier Form list:" + e.getMessage());
			return new ResponseEntity<TableData<SupplierFormPojo>>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	@RequestMapping(path = "/createFormItem", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<List<SupplierFormItem>> createFormItem(@RequestParam String id, //
			@RequestParam String itemName, //
			@RequestParam String supplierForm, //
			@RequestParam(required = false) String parent, //
			@RequestParam String itemDescription, //
			@RequestParam(required = false) String cqType, //
			@RequestParam(required = false) boolean attachment, //
			@RequestParam(required = false) boolean optional, //
			@RequestParam(required = false) List<String> options, //
			@RequestParam(required = false) List<String> optionScore, //
			@RequestParam(required = false) Boolean isSupplierAttachRequired, //
			@RequestParam(value = "file", required = false) MultipartFile[] files) {

		HttpHeaders headers = new HttpHeaders();
		LOG.info("Creating Form Item for :" + supplierForm + " item id:" + id);
		try {

			SupplierFormItem item = new SupplierFormItem();
			if (null != files) {
				LOG.info("file atatch list");
				List<MultipartFile> filelist = Arrays.asList(files);
				item.setFileAttachList(filelist);
			}
			item.setTenantId(SecurityLibrary.getLoggedInUserTenantId());
			item.setItemName(itemName);
			item.setItemDescription(itemDescription);
			item.setIsSupplierAttachRequired(isSupplierAttachRequired);
			if (supplierFormService.isExists(item, supplierForm, parent)) {
				LOG.info("Duplicate....");
				throw new ApplicationException("Duplicate Form Item. Form Item by that name already exists.");
			}
			item.setSupplierForm(supplierFormService.getSupplierFormById(supplierForm));
			if (StringUtils.checkString(cqType).length() > 0) {
				item.setCqType(CqType.valueOf(cqType));
			}
			item.setAttachment(attachment);
			item.setOptional(optional);
			if (CollectionUtil.isNotEmpty(options)) {
				List<SupplierFormItemOption> optionItems = new ArrayList<SupplierFormItemOption>();
				int optionOrder = 0;
				int totalScore = 0;
				for (String option : options) {
					if (StringUtils.checkString(option).length() == 0)
						continue;
					SupplierFormItemOption optionsCheck = new SupplierFormItemOption();
					optionsCheck.setTenantId(SecurityLibrary.getLoggedInUserTenantId());
					optionsCheck.setSupplierFormItem(item);
					optionsCheck.setValue(option);
					optionsCheck.setOrder(++optionOrder);
					if (CollectionUtil.isNotEmpty(optionScore)) {
						optionsCheck.setScoring(Integer.parseInt(optionScore.get(optionOrder - 1)));
					}
					totalScore += optionsCheck.getScoring() != null ? optionsCheck.getScoring().intValue() : 0;
					optionItems.add(optionsCheck);
				}
				item.setFormOptions(optionItems);
			}
			if (StringUtils.checkString(parent).length() > 0) {
				item.setParent(supplierFormService.getFormItembyFormItemId(StringUtils.checkString(parent)));
			}

			supplierFormService.saveSupplierFormItem(item);

			List<SupplierFormItem> formItemList = supplierFormService.findFormItembyFormId(supplierForm);
			headers.add("success", messageSource.getMessage("buyer.supplierForm.item.save", new Object[] {}, Global.LOCALE));

			return new ResponseEntity<List<SupplierFormItem>>(formItemList, headers, HttpStatus.OK);
		} catch (Exception e) {
			LOG.error("Error while adding Form Items : " + e.getMessage(), e);
			headers.add("error", messageSource.getMessage("buyer.supplierForm.item.error", new Object[] { e.getMessage() }, Global.LOCALE));
			return new ResponseEntity<List<SupplierFormItem>>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(value = "/updateFormItem", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<List<SupplierFormItem>> updateFormItem(@RequestParam String id, //
			@RequestParam String itemName, //
			@RequestParam String supplierForm, //
			@RequestParam(required = false) String parent, //
			@RequestParam String itemDescription, //
			@RequestParam(required = false) String cqType, //
			@RequestParam(required = false) boolean attachment, //
			@RequestParam(required = false) boolean optional, //
			@RequestParam(required = false) List<String> options, //
			@RequestParam(required = false) List<String> optionScore, //
			@RequestParam(required = false) Boolean isSupplierAttachRequired, //
			@RequestParam(value = "file", required = false) MultipartFile[] files) {

		SupplierFormItemPojo formItemPojo = new SupplierFormItemPojo();
		formItemPojo.setId(id);
		formItemPojo.setItemName(itemName);
		formItemPojo.setSupplierForm(supplierForm);
		formItemPojo.setParent(parent);
		formItemPojo.setItemDescription(itemDescription);
		formItemPojo.setCqType(cqType);
		formItemPojo.setAttachment(attachment);
		formItemPojo.setOptional(optional);
		formItemPojo.setOptionScore(optionScore);
		formItemPojo.setIsSupplierAttachRequired(isSupplierAttachRequired);
		formItemPojo.setOptions(options);
		if (null != files) {
			LOG.info("file atatch list");
			List<MultipartFile> filelist = Arrays.asList(files);
			formItemPojo.setItemAttachFiles(filelist);
		}
		LOG.info("Creating Form Item for :" + supplierForm + " item id:" + id);
		HttpHeaders headers = new HttpHeaders();
		List<SupplierFormItem> formList = null;
		try {
			LOG.info(" :: UPDATE Item ::" + id);
			if (StringUtils.checkString(formItemPojo.getItemName()).length() > 0) {
				SupplierFormItem supplierFormItem = new SupplierFormItem();
				supplierFormItem.setId(formItemPojo.getId());
				supplierFormItem.setItemName(formItemPojo.getItemName());
				supplierFormItem.setTenantId(SecurityLibrary.getLoggedInUserTenantId());
				supplierFormItem.setItemDescription(formItemPojo.getItemDescription());
				supplierFormItem.setIsSupplierAttachRequired(formItemPojo.getIsSupplierAttachRequired());
				if (supplierFormService.isExists(supplierFormItem, formItemPojo.getSupplierForm(), formItemPojo.getParent())) {
					LOG.info("Duplicate....");
					throw new ApplicationException("Duplicate Form Item. Form Item by that name already exists.");
				}
				supplierFormService.updateFormItem(formItemPojo);
				
				
				formList = supplierFormService.findFormItembyFormId(formItemPojo.getSupplierForm());
				headers.add("success", messageSource.getMessage("buyer.supplierForm.item.update", new Object[] {}, Global.LOCALE));
				return new ResponseEntity<List<SupplierFormItem>>(formList, headers, HttpStatus.OK);
			} else {
				formList = supplierFormService.findFormItembyFormId(formItemPojo.getSupplierForm());
				headers.add("info", messageSource.getMessage("supplierForm.item.name.empty", new Object[] {}, Global.LOCALE));
				return new ResponseEntity<List<SupplierFormItem>>(formList, headers, HttpStatus.OK);
			}

		} catch (Exception e) {
			LOG.error("Error while updating form Items : " + e.getMessage(), e);
			headers.add("error", messageSource.getMessage("buyer.supplierForm.item.error", new Object[] { e.getMessage() }, Global.LOCALE));
			formList = supplierFormService.findFormItembyFormId(formItemPojo.getSupplierForm());
			return new ResponseEntity<List<SupplierFormItem>>(formList, headers, HttpStatus.BAD_REQUEST);
		}
	}

	@RequestMapping(path = "/getFormItemData", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<List<SupplierFormItemPojo>> getFormItemData(@RequestParam("itemId") String itemId) throws JsonProcessingException {
		try {

			if (StringUtils.checkString(itemId).length() > 0) {
				SupplierFormItem item = supplierFormService.getFormItembyFormItemId(StringUtils.checkString(itemId));
				SupplierFormItemPojo pojo = new SupplierFormItemPojo(item);
				List<SupplierFormItemPojo> formItemList = new ArrayList<SupplierFormItemPojo>();
				formItemList.add(pojo);
				HttpHeaders headers = new HttpHeaders();
				return new ResponseEntity<List<SupplierFormItemPojo>>(formItemList, headers, HttpStatus.OK);
			} else {
				List<SupplierFormItemPojo> formItemList = new ArrayList<SupplierFormItemPojo>();
				HttpHeaders headers = new HttpHeaders();
				headers.add("info", messageSource.getMessage("supplierForm.item.load.error", new Object[] {}, Global.LOCALE));
				return new ResponseEntity<List<SupplierFormItemPojo>>(formItemList, headers, HttpStatus.OK);
			}

		} catch (Exception e) {
			LOG.error("Error while getting Form Items to Buyer : " + e.getMessage(), e);
			HttpHeaders headers = new HttpHeaders();
			headers.add("error", messageSource.getMessage("supplierForm.item.load.error", new Object[] { e.getMessage() }, Global.LOCALE));
			return new ResponseEntity<List<SupplierFormItemPojo>>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(path = "/editSupplierForm", method = RequestMethod.GET)
	public ModelAndView editSupplierForm(@RequestParam String id, Model model) {
		SupplierForm supplierFormObj = supplierFormService.getSupplierFormById(id);
		model.addAttribute("btnValue", "Update");
		constructFormDetails(supplierFormObj, model);
		return new ModelAndView("supplierFormCreate", "supplierFormObj", supplierFormObj);
	}

	@RequestMapping(path = "/createSupplierForm", method = RequestMethod.POST)
	public ModelAndView saveSupplierForm(@Valid @ModelAttribute("supplierFormObj") SupplierForm supplierFormObj, BindingResult result, Model model, HttpSession session, RedirectAttributes redir) {
		LOG.info("Saving form by user:" + SecurityLibrary.getLoggedInUserLoginId());
		try {
			if (supplierFormObj != null && StringUtils.checkString(supplierFormObj.getName()).length() == 0) {
				throw new ApplicationException(messageSource.getMessage("supplierForm.name.validation", new Object[] {}, Global.LOCALE));
			}
			if (supplierFormService.isFormNameExists(supplierFormObj, SecurityLibrary.getLoggedInUserTenantId())) {
				throw new ApplicationException(messageSource.getMessage("supplier.form.duplicate.name", new Object[] { supplierFormObj.getName() }, Global.LOCALE));
			}

			List<SupplierFormItem> formItemList = supplierFormService.findFormItembyFormId(supplierFormObj.getId());
			if (CollectionUtil.isEmpty(formItemList)) {
				LOG.info("Form Item empty");
				throw new ApplicationException(messageSource.getMessage("supplier.form.not.sectionAdded", new Object[] {}, Global.LOCALE));
			}

			// Validate Form items inside section
			List<String> notItemSectionAddedForms = supplierFormService.getNotSectionItemAddedByFormId(supplierFormObj.getId());
			if (CollectionUtil.isNotEmpty(notItemSectionAddedForms)) {
				String names = org.apache.commons.lang.StringUtils.join(notItemSectionAddedForms, ",");
				throw new ApplicationException(messageSource.getMessage("supplier.form.item.not.sectionAdded", new Object[] { names }, Global.LOCALE));
			}

			supplierFormService.saveSupplierForm(supplierFormObj, SecurityLibrary.getLoggedInUser());
			
			LOG.info("saved form " + supplierFormObj.getName() + "successfully for user:" + SecurityLibrary.getLoggedInUser().getLoginId());
			if (supplierFormObj.getBtnValue().equalsIgnoreCase("Create")) {
				redir.addFlashAttribute("success", messageSource.getMessage("supplierForm.save.success", new Object[] { supplierFormObj.getName() }, Global.LOCALE));
			} else {
				redir.addFlashAttribute("success", messageSource.getMessage("supplierForm.update.success", new Object[] { supplierFormObj.getName() }, Global.LOCALE));
			}

		} catch (ApplicationException e) {
			if (supplierFormObj.getBtnValue().equalsIgnoreCase("Create")) {
				model.addAttribute("btnValue", "Create");
			} else {
				model.addAttribute("btnValue", "Update");
			}
			constructFormDetails(supplierFormObj, model);
			model.addAttribute("error", e.getMessage());
			LOG.error("Error while saving form:" + e.getMessage(), e);
			return new ModelAndView("supplierFormCreate", "supplierFormObj", supplierFormObj);
		} catch (Exception e) {
			LOG.error("Error while saving form:" + e.getMessage(), e);
			if (supplierFormObj.getBtnValue().equalsIgnoreCase("Create")) {
				model.addAttribute("btnValue", "Create");
			} else {
				model.addAttribute("btnValue", "Update");
			}
			constructFormDetails(supplierFormObj, model);
			model.addAttribute("error", e.getMessage());
			return new ModelAndView("supplierFormCreate", "supplierFormObj", supplierFormObj);

		}
		return new ModelAndView("redirect:supplierFormList");
	}

	@RequestMapping(value = "/deleteFormItem/{formId}", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<List<SupplierFormItem>> deleteFormItem(@RequestBody String[] items, @PathVariable("formId") String formId) throws JsonProcessingException {
		LOG.info("DELETE FORM ITEM REQUESTED : " + items + " formId : " + formId);
		List<SupplierFormItem> formItemList = null;
		HttpHeaders headers = new HttpHeaders();
		if (items != null && items.length > 0) {
			try {
				boolean isFormItemEmty = false;
				supplierFormService.deleteFormItems(items, formId, isFormItemEmty);
				formItemList = supplierFormService.findFormItembyFormId(formId);
				headers.add("errors", messageSource.getMessage("supplierForm.item.success.delete", new Object[] {}, Global.LOCALE));
			} catch (Exception e) {
				LOG.error("Error while deleting Form Items : " + e.getMessage(), e);
				headers.add("errors", messageSource.getMessage("supplierForm.item.info.delete", new Object[] {}, Global.LOCALE));
				return new ResponseEntity<List<SupplierFormItem>>(null, headers, HttpStatus.BAD_REQUEST);
			}
		} else {
			LOG.error("Please select at least one Form Item");
			headers.add("errors", messageSource.getMessage("supplierForm.item.error.delete.required", new Object[] {}, Global.LOCALE));
			return new ResponseEntity<List<SupplierFormItem>>(null, headers, HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<List<SupplierFormItem>>(formItemList, headers, HttpStatus.OK);
	}

	@RequestMapping(path = "/formItemTemplate/{formId}", method = RequestMethod.GET)
	public void downloadFormExcel(@PathVariable String formId, HttpServletRequest request, HttpServletResponse response) throws IOException {
		FileOutputStream out = null;
		Path file = null;
		try {
			XSSFWorkbook workbook = new XSSFWorkbook();
			String downloadFolder = context.getRealPath("/WEB-INF/");
			String fileName = "FormItemTemplate.xlsx";
			file = Paths.get(downloadFolder, fileName);
			LOG.info("File Path ::" + file);

			supplierFormService.supplierFormDownloader(workbook, formId);

			// Save Excel File
			out = new FileOutputStream(downloadFolder + "/" + fileName);
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
					LOG.error("Error :- " + e.getMessage());
				}
			}

		} catch (Exception e) {
			LOG.error("Error while downloading form items :: " + e.getMessage(), e);
		} finally {
			if (out != null) {
				out.close();
				out = null;
			}
			if (file != null) {
				file = null;
			}
		}
	}

	@RequestMapping(value = "/uploadFormItems", method = RequestMethod.POST)
	public ResponseEntity<List<SupplierFormItem>> handleFormUpload(@RequestParam("file") MultipartFile file, @RequestParam("formId") String formId) throws IOException {
		LOG.info("UPLOADING STARTED ...... form Id :: " + formId);
		String message = null;
		List<SupplierFormItem> formItemList = null;
		HttpHeaders headers = new HttpHeaders();
		try {
			if (!file.isEmpty()) {
				if (!file.getContentType().equals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet") && !file.getContentType().equals("application/wps-office.xlsx") && !file.getContentType().equals("application/vnd.ms-excel"))
					throw new MultipartException("Only excel files accepted!");
				try {
					message = formItemsUpload(file, formId);

				} catch (Exception e) {
					e.printStackTrace();
					formItemList = supplierFormService.findFormItembyFormId(formId);
					headers.add("error", messageSource.getMessage("common.upload.fail", new Object[] { e.getMessage() }, Global.LOCALE));
					return new ResponseEntity<List<SupplierFormItem>>(formItemList, headers, HttpStatus.BAD_REQUEST);
				}
			}
		} catch (MultipartException e) {
			e.printStackTrace();
			formItemList = supplierFormService.findFormItembyFormId(formId);
			headers.add("error", messageSource.getMessage("common.upload.notsupported.fail", new Object[] { e.getMessage() }, Global.LOCALE));
			return new ResponseEntity<List<SupplierFormItem>>(formItemList, headers, HttpStatus.BAD_REQUEST);
		}
		formItemList = supplierFormService.findFormItembyFormId(formId);
		if (message == null) {
			headers.add("success", messageSource.getMessage("common.upload.success", new Object[] { "Form Items" }, Global.LOCALE));
		} else {
			headers.add("warn", messageSource.getMessage("common.upload.success", new Object[] { "Form Items" }, Global.LOCALE) + ", " + message);
		}
		return new ResponseEntity<List<SupplierFormItem>>(formItemList, headers, HttpStatus.OK);
	}

	public String formItemsUpload(MultipartFile file, String formId) throws Exception {

		String message = null;
		File convFile = File.createTempFile(file.getOriginalFilename(), "xlsx");

		convFile.createNewFile();
		file.transferTo(convFile);
		SupplierFormFileParser<SupplierFormItem> formItem = new SupplierFormFileParser<SupplierFormItem>(SupplierFormItem.class);
		Map<Integer, Map<Integer, SupplierFormItem>> formData = formItem.parse(convFile);
		message = supplierFormService.doExcelDataSave(formData, formId, SecurityLibrary.getLoggedInUserTenantId());

		try {
			if (convFile != null) {
				convFile.delete();
			}
		} catch (Exception e) {

		}
		return message;
	}

	@RequestMapping(path = "/deleteSupplierForm", method = RequestMethod.GET)
	public String deleteSupplierForm(@RequestParam String id, SupplierForm supplierFormObj, Model model) {
		supplierFormObj = supplierFormService.getSupplierFormById(id);
		try {
			if (supplierFormObj != null) {
				supplierFormService.deleteSupplierForm(supplierFormObj);
				if (StringUtils.checkString(supplierFormObj.getName()).length() > 0) {
					model.addAttribute("success", messageSource.getMessage("supplier.form.success.delete", new Object[] { supplierFormObj.getName() }, Global.LOCALE));
				} else {
					model.addAttribute("success", messageSource.getMessage("supplier.form.success.delete1", new Object[] {}, Global.LOCALE));
				}
				LOG.info("Deleted Supplier Form successfully for user :" + SecurityLibrary.getLoggedInUser().getLoginId());
			}
		} catch (DataIntegrityViolationException e) {
			LOG.error("Error while deleting used supplier form , " + e.getMessage(), e);
			model.addAttribute("error", messageSource.getMessage("supplierForm.error.delete.dataIntegrity", new Object[] { supplierFormObj.getName() }, Global.LOCALE));
		} catch (Exception e) {
			LOG.error("Error while deleting supplier form :" + e.getMessage(), e);
			model.addAttribute("error", messageSource.getMessage("supplierForm.error.delete", new Object[] { supplierFormObj.getName() }, Global.LOCALE));
		}
		return "supplierFormsList";
	}

	@PostMapping("/supplierFormItemOrder")
	public @ResponseBody ResponseEntity<List<SupplierFormItem>> supplierFormItemOrder(@RequestBody SupplierFormItemPojo supplierFormItemPojo) throws JsonProcessingException {
		LOG.info("Parent : " + supplierFormItemPojo.getParent() + " Item Id : " + supplierFormItemPojo.getId() + " New Position : " + supplierFormItemPojo.getOrder());
		HttpHeaders headers = new HttpHeaders();
		List<SupplierFormItem> itemList = null;
		try {
			if (StringUtils.checkString(supplierFormItemPojo.getId()).length() > 0) {
				LOG.info("Updating order.......................... " + supplierFormItemPojo.getId());
				SupplierFormItem formItem = supplierFormService.getFormItembyFormItemId(supplierFormItemPojo.getId());
				if (formItem.getOrder() > 0 && StringUtils.checkString(supplierFormItemPojo.getParent()).length() == 0) {
					LOG.info("child cannot be a parent");
					headers.add("error", messageSource.getMessage("rfx.child.reOrder.error", new Object[] {}, Global.LOCALE));
					return new ResponseEntity<List<SupplierFormItem>>(supplierFormService.findFormItembyFormId(supplierFormService.getFormItembyFormItemId(supplierFormItemPojo.getId()).getSupplierForm().getId()), headers, HttpStatus.BAD_REQUEST);
				}
				if (formItem.getOrder() == 0 && StringUtils.checkString(supplierFormItemPojo.getParent()).length() > 0) {
					LOG.info("parent cannot be a child");
					headers.add("error", messageSource.getMessage("rfx.parent.reOrder.error", new Object[] {}, Global.LOCALE));
					return new ResponseEntity<List<SupplierFormItem>>(supplierFormService.findFormItembyFormId(supplierFormService.getFormItembyFormItemId(supplierFormItemPojo.getId()).getSupplierForm().getId()), headers, HttpStatus.BAD_REQUEST);
				}
				SupplierFormItem item = new SupplierFormItem();
				item.setItemName(supplierFormItemPojo.getItemName());
				if (formItem.getParent() != null && !formItem.getParent().getId().equals(supplierFormItemPojo.getParent()) && supplierFormService.isExists(item, supplierFormItemPojo.getSupplierForm(), supplierFormItemPojo.getParent())) {
					LOG.info("Duplicate....");
					throw new ApplicationException("Duplicate Form Item. Form Item by that name already exists.");
				}
				supplierFormService.reorderFormItem(supplierFormItemPojo);
				itemList = supplierFormService.findFormItembyFormId(supplierFormService.getFormItembyFormItemId(supplierFormItemPojo.getId()).getSupplierForm().getId());
				headers.add("success", messageSource.getMessage("buyer.rftbq.success", new Object[] {}, Global.LOCALE));
				return new ResponseEntity<List<SupplierFormItem>>(itemList, headers, HttpStatus.OK);
			}
		} catch (NotAllowedException | ApplicationException ne) {
			LOG.error("Error while moving form item: " + ne.getMessage(), ne);
			headers.add("error", ne.getMessage());
			return new ResponseEntity<List<SupplierFormItem>>(supplierFormService.findFormItembyFormId(supplierFormService.getFormItembyFormItemId(supplierFormItemPojo.getId()).getSupplierForm().getId()), headers, HttpStatus.BAD_REQUEST);
		} catch (Exception e) {
			LOG.error("Error while moving form item: " + e.getMessage(), e);
			headers.add("error", messageSource.getMessage("buyer.form.error", new Object[] { e.getMessage() }, Global.LOCALE));
			return new ResponseEntity<List<SupplierFormItem>>(null, headers, HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<List<SupplierFormItem>>(itemList, headers, HttpStatus.OK);
	}

	@RequestMapping(path = "/deleteFormItemDocument", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<String> deleteFormItemDocument(@RequestParam("docId") String documentId, Model model, RedirectAttributes redir) {
		HttpHeaders headers = new HttpHeaders();
		SupplierFormItemAttachment formObj = supplierFormSubmissionService.findSupplierformItemAttachment(documentId);
		try {
			if (formObj != null) {
				supplierFormService.deleteFormItemDoc(formObj);
				headers.add("success", messageSource.getMessage("formIem.doc.success.delete", new Object[] {}, Global.LOCALE));
				LOG.info("Deleted Supplier Form Document successfully for user :" + SecurityLibrary.getLoggedInUser().getLoginId());
				return new ResponseEntity<String>(null, headers, HttpStatus.OK);
			} else {
				LOG.error("Error while deleting supplier form document not found");
				headers.add("error", messageSource.getMessage("supplierForm.error.delete.document", new Object[] {}, Global.LOCALE));
				return new ResponseEntity<String>(null, headers, HttpStatus.BAD_REQUEST);
			}
		} catch (Exception e) {
			LOG.error("Error while deleting supplier form document :" + e.getMessage(), e);
			headers.add("error", messageSource.getMessage("supplierForm.error.delete.document", new Object[] {}, Global.LOCALE));
			return new ResponseEntity<String>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping("/downloadFormItemAttachment/{documentId}")
	public void downloadSupplierDocument(@PathVariable("documentId") String documentId, HttpServletResponse response) {
		try {
			SupplierFormItemAttachment formObj = supplierFormSubmissionService.findSupplierformItemAttachment(documentId);
			if (formObj != null) {
				response.setContentType(formObj.getContentType());
				response.setContentLength(formObj.getFileData().length);
				response.setHeader("Content-Disposition", "attachment; filename=\"" + formObj.getFileName() + "\"");
				FileCopyUtils.copy(formObj.getFileData(), response.getOutputStream());
				response.flushBuffer();
				response.setStatus(HttpServletResponse.SC_OK);
			}
		} catch (Exception e) {
			LOG.error("Error while File downloaded : " + e.getMessage(), e);
		}
	}

	@RequestMapping(value = "/copySupplierForm", method = RequestMethod.POST)
	public ModelAndView copySupplierForm(@RequestParam(value = "formId") String formId, @RequestParam(value = "formName") String formName, @RequestParam(value = "formDesc") String formDesc, Model model) {
		SupplierForm supplierFormObj = null;
		SupplierForm oldSupplierForm = null;
		try {
			oldSupplierForm = supplierFormService.getSupplierFormByTenantAndId(formId, SecurityLibrary.getLoggedInUserTenantId());
			if (oldSupplierForm != null) {
				SupplierForm newSupplierForm = new SupplierForm();
				newSupplierForm.setName(formName);
				if (supplierFormService.isFormNameExists(newSupplierForm, SecurityLibrary.getLoggedInUserTenantId())) {
					throw new ApplicationException(messageSource.getMessage("supplier.form.duplicate.name", new Object[] { newSupplierForm.getName() }, Global.LOCALE));
				}

				SupplierForm supplierForm = supplierFormService.copySupplierForm(oldSupplierForm, formName, SecurityLibrary.getLoggedInUser(), formDesc);
				if (StringUtils.checkString(supplierForm.getId()).length() > 0) {
					LOG.info("Copied Supplier Form Created and Saved Sucessfully  :" + newSupplierForm.getName());
					/// supplierFormObj = supplierFormService.getSupplierFormById(supplierForm.getId());
					// constructFormDetails(supplierFormObj, model);
					// model.addAttribute("btnValue", "Update");
					model.addAttribute("success", messageSource.getMessage("supplierForm.save.success", new Object[] { supplierForm.getName() }, Global.LOCALE));
				}
			}
		} catch (Exception e) {
			LOG.error("Error while save as : " + e.getMessage(), e);
			model.addAttribute("error", "Error while Copying Supplier Form : " + e.getMessage());
			supplierFormObj = supplierFormService.getSupplierFormById(oldSupplierForm.getId());
			constructFormDetails(supplierFormObj, model);
			model.addAttribute("btnValue", "Update");
			return new ModelAndView("supplierFormCreate", "supplierFormObj", supplierFormObj);
		}
		return new ModelAndView("redirect:supplierFormList");

	}

	private void constructFormDetails(SupplierForm supplierFormObj, Model model) {
		List<User> approvalUserList = new ArrayList<User>();
		List<User> allUserList = new ArrayList<User>();
		List<SupplierFormItem> formItemList = null;
		boolean isAssignedForm = false;
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
			if (supplierFormObj != null) {
				if (CollectionUtil.isNotEmpty(supplierFormObj.getApprovals())) {
					for (SupplierFormApproval approval : supplierFormObj.getApprovals()) {
						if (CollectionUtil.isNotEmpty(approval.getApprovalUsers())) {
							for (SupplierFormApprovalUser user : approval.getApprovalUsers()) {
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
				formItemList = supplierFormService.findFormItembyFormId(supplierFormObj.getId());
				isAssignedForm = supplierFormSubmissionService.isFormAssigned(supplierFormObj.getId());
			}
		} catch (Exception e) {
			LOG.error("Error while getting form details:" + e.getMessage());
		}

		model.addAttribute("isAssignedForm", isAssignedForm);
		model.addAttribute("cqTypes", CqType.values());
		model.addAttribute("userList", approvalUserList);
		model.addAttribute("formApprovalUserList", allUserList);
		model.addAttribute("formItemList", formItemList);
	}
}