/**
 * 
 */
package com.privasia.procurehere.web.controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomCollectionEditor;
import org.springframework.context.MessageSource;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.privasia.procurehere.core.dao.SupplierFormSubItemOptionDao;
import com.privasia.procurehere.core.entity.OwnerSettings;
import com.privasia.procurehere.core.entity.SupplierFormItemAttachment;
import com.privasia.procurehere.core.entity.SupplierFormItemOption;
import com.privasia.procurehere.core.entity.SupplierFormSubmissionItem;
import com.privasia.procurehere.core.entity.SupplierFormSubmition;
import com.privasia.procurehere.core.entity.SupplierFormSubmitionAudit;
import com.privasia.procurehere.core.entity.SupplierFormSubmitionItemOption;
import com.privasia.procurehere.core.pojo.SupplierFormSubItem;
import com.privasia.procurehere.core.pojo.SupplierFormSubmissionItemPojo;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.SecurityLibrary;
import com.privasia.procurehere.core.utils.StringUtils;
import com.privasia.procurehere.service.OwnerSettingsService;
import com.privasia.procurehere.service.SupplierFormService;
import com.privasia.procurehere.service.SupplierFormSubmissionService;
import com.privasia.procurehere.service.SupplierFormSubmitionAuditService;
import com.privasia.procurehere.web.editors.SupplierFormItemOptionEditor;

/**
 * @author sana
 */
@Controller
@RequestMapping(value = "/supplier")
public class SupplierFormSubmissionController implements Serializable {

	private static final long serialVersionUID = -2401256415624340284L;

	private static final Logger LOG = LogManager.getLogger(Global.SUPPLIER_LOG);

	@Autowired
	MessageSource messageSource;

	@Autowired
	SupplierFormSubmissionService formSubmissionService;

	@Autowired
	SupplierFormService formService;

	@Autowired
	SupplierFormService supplierFormService;

	@Autowired
	OwnerSettingsService ownerSettingsService;

	@Autowired
	SupplierFormSubmitionAuditService supplierFormSubAuditService;

	@Autowired
	SupplierFormItemOptionEditor supplierFormOptionEditor;

	@Autowired
	SupplierFormSubItemOptionDao supplierFormSubItemOptionDao;

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder.registerCustomEditor(SupplierFormItemOption.class, supplierFormOptionEditor);
		binder.registerCustomEditor(List.class, "listOptAnswers", new CustomCollectionEditor(List.class) {
			protected Object convertElement(Object element) {
				if (element != null) {
					String id = (String) element;
					LOG.info("ID : " + id);
					SupplierFormSubmitionItemOption group = supplierFormSubItemOptionDao.findById(id);
					SupplierFormItemOption op = new SupplierFormItemOption();
					op.setId(group.getId());
					op.setOrder(group.getOrder());
					op.setValue(group.getValue());
					return op;
				}
				return null;
			}
		});
	}

	@ModelAttribute("ownerSettings")
	public OwnerSettings getOwnersettings() {
		return ownerSettingsService.getOwnersettings();
	}

	@GetMapping("/supplierFormView/{formId}")
	public String supplierFormView(@PathVariable String formId, Model model, HttpServletRequest request, RedirectAttributes redir) {
		LOG.info("FormId: " + formId);
		try {
			constructBuyerFormSummaryForSupplierView(formId, model);
		} catch (Exception e) {
			LOG.info("Error while getting Buyer Form by id: " + e.getMessage(), e);
			model.addAttribute("error", messageSource.getMessage("error.while.fetching.supplier.formdetails", new Object[] { e.getMessage() }, Global.LOCALE));
		}
		return "supplierFormSubmission";
	}

	private SupplierFormSubmition constructBuyerFormSummaryForSupplierView(String formId, Model model) {
		SupplierFormSubmissionItemPojo supplierFormSubmissionItemPojo = new SupplierFormSubmissionItemPojo();
		supplierFormSubmissionItemPojo.setFormId(formId);

		SupplierFormSubmition form = formSubmissionService.getSupplierformById(formId);
		List<SupplierFormSubmissionItem> submissionList = formSubmissionService.findSupplierSubFormItemById(formId);
		LOG.info("Form size: " + submissionList.size());
		if (CollectionUtil.isEmpty(submissionList)) {
			formSubmissionService.saveSupplierFormSubmission(form.getSupplierForm().getId(), form);
			submissionList = formSubmissionService.findSupplierSubFormItemById(formId);
		}
		LOG.info("Form size: " + submissionList.size());

		List<SupplierFormSubItem> itemList = new ArrayList<SupplierFormSubItem>();
		for (SupplierFormSubmissionItem list : submissionList) {
			SupplierFormSubItem itemObj = new SupplierFormSubItem(list);
			List<SupplierFormItemAttachment> itemAttachment = formSubmissionService.findAllFormDocsByFormItemId(itemObj.getFormItem().getId());
			itemObj.setItemAttachment(itemAttachment);
			itemList.add(itemObj);
		}
		supplierFormSubmissionItemPojo.setItemList(itemList);
		List<SupplierFormSubmitionAudit> supplierFormAuditList = supplierFormSubAuditService.getFormAuditByFormIdForSupplier(formId);
		model.addAttribute("supplierForm", form);
		model.addAttribute("supplierFormSubmissionItemPojo", supplierFormSubmissionItemPojo);
		model.addAttribute("supplierFormAuditList", supplierFormAuditList);
		return form;
	}

	@PostMapping("/saveSupplierFormAsDraft/{formId}")
	public String saveSupplierFormAsDraft(@ModelAttribute("supplierFormSubmissionItemPojo") SupplierFormSubmissionItemPojo supplierFormSubmissionItemPojo, Model model, @PathVariable("formId") String formId, RedirectAttributes redir) {
		LOG.info("Form Id: " + formId);
		return saveSupplierSubmittionForm(supplierFormSubmissionItemPojo, formId, redir, false);
	}

	@PostMapping("/submitSupplierform/{formId}")
	public String submitSupplierform(@ModelAttribute("supplierFormSubmissionItemPojo") SupplierFormSubmissionItemPojo supplierFormSubmissionItemPojo, Model model, @PathVariable("formId") String formId, RedirectAttributes redir) {
		LOG.info("Form Id: " + formId);
		return saveSupplierSubmittionForm(supplierFormSubmissionItemPojo, formId, redir, true);
	}

	private String saveSupplierSubmittionForm(SupplierFormSubmissionItemPojo supplierFormSubmissionItemPojo, String formId, RedirectAttributes redir, boolean flag) {
		try {
			SupplierFormSubmition form = formSubmissionService.getSupplierformById(formId);
			if (supplierFormSubmissionItemPojo.getItemList() != null) {
				List<SupplierFormSubItem> itemList = supplierFormSubmissionItemPojo.getItemList();
				LOG.info("Item List size : " + itemList.size());
				if (CollectionUtil.isNotEmpty(itemList)) {
					List<SupplierFormSubmissionItem> list = new ArrayList<SupplierFormSubmissionItem>();
					for (SupplierFormSubItem item : itemList) {
						item.setFormSub(formSubmissionService.getSupplierformById(item.getFormSub().getId()));
						item.setFormItem(formService.getFormItembyFormItemId(item.getFormItem().getId()));
						SupplierFormSubmissionItem obj = new SupplierFormSubmissionItem(item);
						SupplierFormSubmissionItem subItemOpt = formSubmissionService.findFormSubmissionItem(formId, item.getId());
						if ((item.getAttachment() == null || (item.getAttachment() != null & item.getAttachment().getBytes().length == 0)) && StringUtils.checkString(subItemOpt.getFileName()).length() > 0) {

							obj.setFileData(subItemOpt.getFileData());
							obj.setFileName(subItemOpt.getFileName());
							obj.setContentType(subItemOpt.getContentType());
						}
						list.add(obj);
					}
					LOG.info("Item List size : " + itemList.size());
					formSubmissionService.updateSupplierForm(list, formId, SecurityLibrary.getLoggedInUser(), flag);
					if (flag == true) {
						redir.addFlashAttribute("success", messageSource.getMessage("supplier.submitted.success.supplierForm", new Object[] { form.getName() }, Global.LOCALE));
					} else {
						redir.addFlashAttribute("success", messageSource.getMessage("supplier.submitted.savedraft.supplierForm", new Object[] { form.getName() }, Global.LOCALE));
						return "redirect:/supplier/onBoardSupplierFormSubmission/" + formId;
					}
				}
			} else {
				redir.addFlashAttribute("error", messageSource.getMessage("flasherror.while.submitting.supplierForm", new Object[] {}, Global.LOCALE));
			}
		} catch (Exception e) {
			redir.addFlashAttribute("error", messageSource.getMessage("supplier.form.error.submit", new Object[] { e.getMessage() }, Global.LOCALE));
			LOG.error("error while Submitting form " + e.getMessage(), e);
		}
		return "redirect:/supplier/supplierFormView/" + formId;
	}

	@GetMapping("/downloadAttachment/{formSubId}/{formSubItemId}")
	public void downloadAttachment(@PathVariable("formSubId") String formSubId, @PathVariable("formSubItemId") String formSubItemId, HttpServletResponse response) {
		try {
			LOG.info("formSubId: " + formSubId + " formSubItemId: " + formSubItemId);
			SupplierFormSubmissionItem formObj = formSubmissionService.findFormSubmissionItem(formSubId, formSubItemId);
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

	@PostMapping("/removeSupplierFormDoc/{formSubId}/{formSubItemId}")
	public ResponseEntity<Boolean> removeSupplierFormDoc(@PathVariable("formSubId") String formSubId, @PathVariable("formSubItemId") String formSubItemId) {
		LOG.info("reset attach called..............");
		HttpHeaders headers = new HttpHeaders();
		boolean removed = false;
		try {
			removed = formSubmissionService.resetAttachement(formSubId, formSubItemId);
		} catch (Exception e) {
			LOG.error("Error during reset of form attachment : " + e.getMessage(), e);
			headers.add("error", "Error while removing form attachment for Supplier Form Item");
			return new ResponseEntity<Boolean>(removed, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		headers.add("sucess", "Removed attachment for Form Item");
		return new ResponseEntity<Boolean>(removed, headers, HttpStatus.OK);
	}

	@GetMapping("/downloadSupplierDocument/{documentId}")
	public void downloadSupplierDocument(@PathVariable("documentId") String documentId, HttpServletResponse response) {
		try {
			LOG.info("documentId: " + documentId);
			SupplierFormItemAttachment formObj = formSubmissionService.findSupplierformItemAttachment(documentId);
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

	@GetMapping("/onBoardSupplierFormSubmission/{formId}")
	public String onBoardSupplierFormSubmission(@PathVariable String formId, Model model, HttpServletRequest request, RedirectAttributes redir) {
		LOG.info("FormId: " + formId);
		try {
			constructBuyerFormSummaryForSupplierView(formId, model);
		} catch (Exception e) {
			LOG.info("Error while getting Buyer Form by id: " + e.getMessage(), e);
			model.addAttribute("error", messageSource.getMessage("error.while.fetching.supplier.formdetails", new Object[] { e.getMessage() }, Global.LOCALE));
		}
		return "onBoardSupplierFormSubmission";
	}

}
