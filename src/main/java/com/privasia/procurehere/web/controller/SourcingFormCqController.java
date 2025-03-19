package com.privasia.procurehere.web.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;


import com.privasia.procurehere.core.entity.*;
import com.privasia.procurehere.core.enums.TeamMemberType;
import com.privasia.procurehere.core.utils.SecurityLibrary;
import com.privasia.procurehere.service.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.privasia.procurehere.core.enums.CqType;
import com.privasia.procurehere.core.enums.SourcingStatus;
import com.privasia.procurehere.core.exceptions.ApplicationException;
import com.privasia.procurehere.core.exceptions.NotAllowedException;
import com.privasia.procurehere.core.pojo.CqItemPojo;
import com.privasia.procurehere.core.pojo.SourcingFormCqItemPojo;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.StringUtils;

/**
 * @author sarang
 */
@Controller
@RequestMapping("/buyer")
public class SourcingFormCqController {

	private static final Logger LOG = LogManager.getLogger(Global.PR_LOG);

	@Autowired
	EventIdSettingsService eventIdSettingsService;

	@Autowired
	BusinessUnitService businessUnitService;

	@Autowired
	SourcingFormCqService sourcingFormCqService;

	@Autowired
	UserService userService;

	@Autowired
	SourcingTemplateService sourcingTemplateService;

	@Autowired
	protected MessageSource messageSource;

	@Autowired
	SourcingFormCqService cqService;

	@Autowired
	OwnerSettingsService ownerSettingsService;

	@ModelAttribute("step")
	public String getStep() {
		return "2";
	}

	public boolean isExists(String formId, String name) {
		return sourcingFormCqService.isCqExists(formId, name);
	}

	@RequestMapping(path = "/saveSourcingFormCq/{formId}", method = RequestMethod.POST)
	public String sourcingFormCqList(@PathVariable("formId") String formId, @ModelAttribute SourcingTemplateCq sourceFormCq, Model model, BindingResult result, RedirectAttributes redirect) {

		if (result.hasErrors()) {
			for (ObjectError error : result.getAllErrors()) {
				redirect.addFlashAttribute("errors", error.getDefaultMessage());
			}
		}
		try {

			if (isExists(formId, sourceFormCq.getName())) {
				throw new ApplicationException("Duplicate CQ. CQ by that name exists  already.");
			} else {
				sourceFormCq.setCreatedDate(new Date());
				SourcingFormTemplate sourceForm = sourcingTemplateService.getSourcingFormbyId(formId);
				if (sourceForm != null) {
					Integer count = 1;
					List<SourcingTemplateCq> cqList = sourcingTemplateService.getAllQuestionnarieOrderByDate(formId);
					if (CollectionUtil.isNotEmpty(cqList)) {
						for (SourcingTemplateCq templateCq : cqList) {
							if (templateCq.getCqOrder() == null) {
								templateCq.setCqOrder(count);
								sourcingFormCqService.updateSourcingFormCq(templateCq);
								count++;
							}
						}
						count = cqList.size();
						count++;
					}
					sourceFormCq.setSourcingForm(sourceForm);
					sourceFormCq.setCqOrder(count);
					SourcingTemplateCq cq = sourcingFormCqService.saveSourcingFormCq(sourceFormCq);

					if (cq != null) {
						sourceForm.setCqCompleted(true);
						sourcingTemplateService.updateSourcingTemplate(sourceForm);
					}
				} else {
					redirect.addFlashAttribute("error", messageSource.getMessage("flasherror.while.save.template", new Object[] {}, Global.LOCALE));
				}
				// redirect.addFlashAttribute("success", "CQ added successfully");
				redirect.addFlashAttribute("success", messageSource.getMessage("flashsuccess.cq.added", new Object[] {}, Global.LOCALE));
			}
		} catch (Exception e) {

			LOG.error("Error while storing CQ," + e.getMessage(), e);
			redirect.addFlashAttribute("error", messageSource.getMessage("rft.cq.error", new Object[] { e.getMessage() }, Global.LOCALE));
		}
		return "redirect:/buyer/sourcingFormCqList/" + formId;

	}

	@RequestMapping(path = "/sourcingFormCqList/{formId}", method = RequestMethod.GET)
	public String getSourcingFormCqList(@PathVariable("formId") String formId, @ModelAttribute SourcingTemplateCq sourceFormCq, Model model, BindingResult result, RedirectAttributes redirect) {
		try {
			// List<SourcingTemplateCq> cqList = sourcingTemplateService.getAllQuestionnarie(formId);
			List<SourcingTemplateCq> cqList = sourcingTemplateService.getAllQuestionnarieByOrder(formId);
			SourcingFormTemplate sourceForm = sourcingTemplateService.getSourcingFormbyId(formId);
			model.addAttribute("cqList", cqList);
			SourcingFormTemplate template = sourcingTemplateService.getSourcingFormbyId(formId);
			template.setDocumentCompleted(true);
			template = sourcingTemplateService.updateSourcingTemplate(template);
			model.addAttribute("status", template.getStatus().toString());
			LOG.info("status " + template.getStatus().toString());
			model.addAttribute("isTemplateUsed", template.getIsTemplateUsed());
			model.addAttribute("template", template);
			model.addAttribute("templateId", sourceForm.getId());
			model.addAttribute("event", template);

			if (((sourceForm.getStatus() == SourcingStatus.INACTIVE) || (sourceForm.getStatus() == SourcingStatus.ACTIVE)) && (template.getIsTemplateUsed() == Boolean.FALSE)) {
				model.addAttribute("finishButton", messageSource.getMessage("application.finish.update", new Object[] {}, LocaleContextHolder.getLocale()));
			} else {
				model.addAttribute("finishButton", messageSource.getMessage("application.finish", new Object[] {}, LocaleContextHolder.getLocale()));
			}

		} catch (Exception e) {
			LOG.info("Exception while saving Cq ", e);
			LOG.error("Error while storing CQ," + e.getMessage(), e);
			redirect.addFlashAttribute("error", messageSource.getMessage("rft.cq.error", new Object[] { e.getMessage() }, Global.LOCALE));
		}
		return "sourcingFormCqList";
	}

	@RequestMapping(path = "/sourcingTemplateDocument/{formId}", method = RequestMethod.GET)
	public String sourcingTemplateDocument(@PathVariable("formId") String formId, Model model) {
		LOG.info("create Rfs template Document GET called Rfs id :" + formId);
		SourcingFormTemplate template = sourcingTemplateService.getSourcingFormbyId(formId);
		// Get the list of documents and sort by uploadDate in descending order
		List<RfsTemplateDocument> documents = template.getRfsTemplateDocuments();
		Collections.sort(documents, new Comparator<RfsTemplateDocument>() {
			@Override
			public int compare(RfsTemplateDocument d1, RfsTemplateDocument d2) {
				return d2.getUploadDate().compareTo(d1.getUploadDate());
			}
		});

		for (RfsTemplateDocument document : documents) {
			String uploadedBy = (document.getUploadBy() != null ? document.getUploadBy().getId() : "");
		}
		template.setEventDetailCompleted(true);
		sourcingTemplateService.updateSourcingTemplate(template);
		model.addAttribute("event", template);
		model.addAttribute("template", template);
		model.addAttribute("ownerSettings", ownerSettingsService.getOwnersettings());
		RequestAudit audit = new RequestAudit();
		model.addAttribute("audit", audit);
		return "sourcingTemplateDocument";
	}

	@RequestMapping(path = "/sourcingCqItems", method = RequestMethod.POST)
	public String sourcingCqItems(@RequestParam(name = "cqId", required = true) String cqId) {
		LOG.info(" Questionnarie Id+++++++++++++++++++++++++++++++++++++++++++++++++" + cqId);
		return "redirect:sourcingCqItemList/" + cqId;
	}

	@RequestMapping(path = "/sourcingCqItemList/{cqId}", method = RequestMethod.GET)
	public String formCqItemList(@PathVariable String cqId, Model model) {
		if (StringUtils.checkString(cqId).length() == 0) {
			return "redirect:/400_error";
		}
		// RftCq rftCq = (RftCq) model.asMap().get("rftCq");

		SourcingTemplateCq sourcingFormCq = sourcingFormCqService.getSourcingFormCq(cqId);
		LOG.info("CQ : " + sourcingFormCq.getId());
		// List<SourcingFormCqItem> rftCq = sourcingFormCqService.findCqItembyCqId(cqId);
		List<SourcingTemplateCqItem> cqItemList = sourcingFormCqService.findCqItembyCqId(cqId);
		SourcingFormTemplate sourceForm = sourcingFormCqService.getSourcingForm(cqId);
		long cqCount = sourcingTemplateService.getBqCount(sourceForm.getId());
		model.addAttribute("event", sourceForm);
		model.addAttribute("isTemplateUsed", sourceForm.getIsTemplateUsed());
		model.addAttribute("status", sourceForm.getStatus().toString());
		model.addAttribute("sourcingFormCq", sourcingFormCq);
		// model.addAttribute("rftCq", rftCq);
		model.addAttribute("cqItemList", cqItemList);
		model.addAttribute("formId", sourceForm.getId());
		model.addAttribute("cqTypes", CqType.values());
		model.addAttribute("cqId", cqId);
		SourcingTemplateCqItem cqItem = new SourcingTemplateCqItem();
		cqItem.setCq(sourcingFormCq);
		model.addAttribute("cqItem", cqItem);
		if (((sourceForm.getStatus() == SourcingStatus.INACTIVE) || (sourceForm.getStatus() == SourcingStatus.ACTIVE)) && (sourceForm.getIsTemplateUsed() == Boolean.FALSE)) {
			model.addAttribute("finishButton", messageSource.getMessage("application.finish.update", new Object[] {}, LocaleContextHolder.getLocale()));
		} else {
			model.addAttribute("finishButton", messageSource.getMessage("application.finish", new Object[] {}, LocaleContextHolder.getLocale()));
		}
		if (cqCount > 0) {
			model.addAttribute("createQuestion", messageSource.getMessage("update.Questionnaire", new Object[] {}, LocaleContextHolder.getLocale()));
		} else {
			model.addAttribute("createQuestion", messageSource.getMessage("create.Questionnaire", new Object[] {}, LocaleContextHolder.getLocale()));
		}
		LOG.info("End of  FormCqItemList Method ");
		return "sourcingCqItemList";
	}

	@RequestMapping(path = "/updateSourcingFormCq", method = RequestMethod.POST)
	public String updateSourcingFormCq(@ModelAttribute("sourcingFormCq") SourcingTemplateCq sourceFormcq, HttpServletRequest req, Model model) {
		SourcingFormTemplate sourceForm = null;
		String cqId = req.getParameter("cqId");
		try {
			LOG.info("ID before updation " + req.getParameter("cqId"));
			sourceForm = sourcingFormCqService.getSourcingForm(cqId);
			SourcingTemplateCq cqPersistObject = sourcingFormCqService.getSourcingFormCq(cqId);
			String cqName = (cqPersistObject.getName() != null ? cqPersistObject.getName() : "");
			String cqDesc = (cqPersistObject.getDescription() != null ? cqPersistObject.getDescription() : "");

			// check duplicate Cq Name while updating
			if (!cqPersistObject.getName().equalsIgnoreCase(sourceFormcq.getName()) && isExists(sourceForm.getId(), sourceFormcq.getName())) {
				throw new RuntimeException("Cq With this name already exists ");

			} else {
				cqPersistObject.setName(sourceFormcq.getName());
				cqPersistObject.setDescription(sourceFormcq.getDescription());

				// if user submit same name and description then avoid hit to the database
				if ((!cqName.equals(sourceFormcq.getName()) || (!cqDesc.equals(sourceFormcq.getDescription())))) {
					LOG.info("Updating Cq");
					cqPersistObject = sourcingFormCqService.updateSourcingFormCq(cqPersistObject);
				}

			}
			model.addAttribute("cqId", cqPersistObject.getId());
			LOG.info("ID After Updation  " + cqPersistObject.getId());
			LOG.info("updated Cq Name :: " + cqPersistObject.getName());
			LOG.info("updated Cq Desc :: " + cqPersistObject.getDescription());
		} catch (Exception e) {
			LOG.error("error while update Cq ", e.getMessage(), e);
		}
		return "redirect:/buyer/sourcingCqItemList/" + cqId;
	}

	@RequestMapping(path = "/addSourcingFormItem", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<List<SourcingTemplateCqItem>> addSourcingFormCqItem(@RequestBody SourcingFormCqItemPojo cqPojo, Model model) {
		LOG.info("addSourcingFormCqItem method call and CqId id " + cqPojo.getCqId());
		LOG.info("isAttachemt Required " + cqPojo.isOptional() + " isSupplier Attached Required  " + cqPojo.getIsSupplierAttachRequired() + " isSuppler Arrach " + cqPojo.isAttachment());
		HttpHeaders headers = new HttpHeaders();
		LOG.info("Name=========" + cqPojo.getItemName());
		LOG.info("Description =====" + cqPojo.getItemDescription());
		LOG.info("check parent ++" + cqPojo.getParent());
		try {
			if (StringUtils.checkString(cqPojo.getItemName()).length() > 0) {

				if (sourcingFormCqService.isCqItemExists(cqPojo.getCqId(), cqPojo.getItemName())) {
					LOG.info("Duplicate Cq Item Found ");
					throw new RuntimeException("Duplicate CQ Item. CQ Item by that name already exists.");
				}

				SourcingTemplateCqItem cqItem = new SourcingTemplateCqItem();
				cqItem.setItemName(cqPojo.getItemName());
				cqItem.setItemDescription(cqPojo.getItemDescription());
				cqItem.setOptional(cqPojo.isOptional());
				if (StringUtils.checkString(cqPojo.getCqType()).length() > 0) {
					cqItem.setCqType(CqType.valueOf(cqPojo.getCqType()));

				}
				SourcingFormTemplate sourcingForm = sourcingFormCqService.getSourcingForm(cqPojo.getCqId());
				// check if cqItem is already available .

				LOG.info("check SourcingForm is Empty " + sourcingForm.getId() + " Check CqId " + cqPojo.getCqId());
				SourcingTemplateCq sourceCq = sourcingFormCqService.getSourcingFormCq(cqPojo.getCqId());
				cqItem.setSourcingForm(sourcingForm);
				cqItem.setCq(sourceCq);
				cqItem.setIsSupplierAttachRequired(cqPojo.getIsSupplierAttachRequired());
				cqItem.setAttachment(cqPojo.isAttachment());
				if (CollectionUtil.isNotEmpty(cqPojo.getOptions())) {
					List<SourcingFormCqOption> optionItems = new ArrayList<SourcingFormCqOption>();
					int optionOrder = 0;
					for (String option : cqPojo.getOptions()) {
						LOG.info("options ------------" + option);
						if (StringUtils.checkString(option).length() == 0)
							continue;
						SourcingFormCqOption options = new SourcingFormCqOption();
						options.setFormCqItem(cqItem);
						options.setValue(option);
						options.setOrder(++optionOrder);
						if (CollectionUtil.isNotEmpty(cqPojo.getOptionScore()))
							options.setScoring(Integer.parseInt(cqPojo.getOptionScore().get(optionOrder - 1)));
						optionItems.add(options);
					}

					cqItem.setCqOptions(optionItems);
				}
				if (StringUtils.checkString(cqPojo.getParent()).length() > 0) {
					cqItem.setParent(sourcingFormCqService.getCqItembyCqItemId(StringUtils.checkString(cqPojo.getParent())));
				}
				SourcingTemplateCqItem cq = sourcingFormCqService.saveCqItem(cqItem);
				LOG.info("CQ_ITEM_ID" + cq.getId());
				List<SourcingTemplateCqItem> cqItemList = sourcingFormCqService.findAllCqItembyCqId(cqPojo.getCqId());
				LOG.info("SourcingFormCqItem  size ++++" + cqItemList.size());

				headers.add("success", messageSource.getMessage("buyer.rftcq.save", new Object[] {}, Global.LOCALE));
				LOG.info(cqItemList.size());
				sourcingForm.setCqCompleted(true);
				sourcingForm = sourcingTemplateService.updateSourcingTemplate(sourcingForm);
				model.addAttribute("event", sourcingForm);
				return new ResponseEntity<List<SourcingTemplateCqItem>>(cqItemList, headers, HttpStatus.OK);
			} else {
				List<SourcingTemplateCqItem> bqItemList = sourcingFormCqService.findCqItembyCqId(cqPojo.getCqId());
				headers.add("info", messageSource.getMessage("rft.cq.name.empty", new Object[] {}, Global.LOCALE));
				return new ResponseEntity<List<SourcingTemplateCqItem>>(bqItemList, headers, HttpStatus.OK);
			}
		} catch (Exception e) {
			LOG.error("Error while adding CQ Items to Form : " + e.getMessage(), e);
			headers.add("error", messageSource.getMessage("buyer.rftcq.error", new Object[] { e.getMessage() }, Global.LOCALE));
			return new ResponseEntity<List<SourcingTemplateCqItem>>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	@RequestMapping(path = "/getCqItemData", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<List<SourcingFormCqItemPojo>> getCqItemData(@RequestParam("itemId") String itemId) {
		LOG.info("getCqItemData has been called and CQ_ITEM Id is " + itemId);
		try {
			if (StringUtils.checkString(itemId).length() > 0) {
				SourcingTemplateCqItem sourcingFormCqItem = sourcingFormCqService.getSourcingCqItembyItemId(itemId);
				List<SourcingFormCqItemPojo> list = new ArrayList<SourcingFormCqItemPojo>();
				SourcingFormCqItemPojo pojo = new SourcingFormCqItemPojo(sourcingFormCqItem);
				list.add(pojo);
				HttpHeaders headers = new HttpHeaders();
				return new ResponseEntity<List<SourcingFormCqItemPojo>>(list, headers, HttpStatus.OK);
			} else {
				List<SourcingFormCqItemPojo> list = new ArrayList<SourcingFormCqItemPojo>();
				HttpHeaders headers = new HttpHeaders();
				headers.add("info", messageSource.getMessage("rft.rftcq.load.error", new Object[] {}, Global.LOCALE));
				return new ResponseEntity<List<SourcingFormCqItemPojo>>(list, headers, HttpStatus.OK);
			}
		} catch (Exception e) {
			LOG.error("Error while getting CQ Items : " + e.getMessage(), e);
			HttpHeaders headers = new HttpHeaders();
			headers.add("error", messageSource.getMessage("rft.rftcq.load.error", new Object[] { e.getMessage() }, Global.LOCALE));
			return new ResponseEntity<List<SourcingFormCqItemPojo>>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(path = "/updateSourcingFormCqItem", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<List<SourcingTemplateCqItem>> updateSourcingFormCqItem(@RequestBody SourcingFormCqItemPojo cqPojo, HttpServletRequest req) {
		LOG.info("updateSourcingFormCqItem method has been called " + cqPojo.getItemName());
		try {
			SourcingFormTemplate sourceForm = sourcingFormCqService.getSourcingForm(cqPojo.getCqId());
			LOG.info("Source Form Name is " + sourceForm.getFormName());
			SourcingTemplateCqItem cqItem = sourcingFormCqService.getSourcingCqItembyItemId(cqPojo.getId());
			LOG.info("Cq Item Name is " + cqItem.getItemName());
			cqItem.setItemName(cqPojo.getItemName());
			cqItem.setItemDescription(cqPojo.getItemDescription());
			if (StringUtils.checkString(cqPojo.getItemName()).length() > 0) {
				if (sourcingFormCqService.isCqExists(cqPojo.getCqId(), cqPojo.getItemName())) {
					LOG.info("Duplicate Cq Item Found ");
					throw new RuntimeException("Duplicate CQ Item. CQ Item by that name already exists.");
				}
				if (StringUtils.checkString(cqPojo.getCqType()).length() > 0) {
					cqItem.setCqType(CqType.valueOf(cqPojo.getCqType()));

				}
				cqItem.setIsSupplierAttachRequired(cqPojo.getIsSupplierAttachRequired());
				cqItem.setOptional(cqPojo.isOptional());
				cqItem.setAttachment(cqPojo.isAttachment());
				if (CollectionUtil.isNotEmpty(cqPojo.getOptions())) {
					List<SourcingFormCqOption> optionItems = new ArrayList<SourcingFormCqOption>();
					int optionOrder = 0;
					for (String option : cqPojo.getOptions()) {
						LOG.info("options ------------" + option);
						if (StringUtils.checkString(option).length() == 0)
							continue;
						SourcingFormCqOption options = new SourcingFormCqOption();
						options.setFormCqItem(cqItem);
						options.setValue(option);
						options.setOrder(++optionOrder);
						if (CollectionUtil.isNotEmpty(cqPojo.getOptionScore()))
							options.setScoring(Integer.parseInt(cqPojo.getOptionScore().get(optionOrder - 1)));
						optionItems.add(options);
					}

					cqItem.setCqOptions(optionItems);
				}
				SourcingTemplateCqItem cq = sourcingFormCqService.updateCqItem(cqItem);
				LOG.info("update name  " + cq.getItemName() + "  description " + cq.getItemDescription());
				List<SourcingTemplateCqItem> cqItemList = sourcingFormCqService.findSourcingTemplateCqItembyCqId(cqPojo.getCqId());
				LOG.info("SourcingFormCqItem  size ++++" + cqItemList.size());
				HttpHeaders headers = new HttpHeaders();
				headers.add("success", messageSource.getMessage("buyer.rftcq.update", new Object[] {}, Global.LOCALE));
				LOG.info(cqItemList.size());
				return new ResponseEntity<List<SourcingTemplateCqItem>>(cqItemList, headers, HttpStatus.OK);
			} else {
				List<SourcingTemplateCqItem> list = new ArrayList<SourcingTemplateCqItem>();
				HttpHeaders headers = new HttpHeaders();
				headers.add("info", messageSource.getMessage("rft.rftcq.load.error", new Object[] {}, Global.LOCALE));
				return new ResponseEntity<List<SourcingTemplateCqItem>>(list, headers, HttpStatus.OK);
			}
		} catch (Exception e) {
			LOG.error("Error while getting CQ Items : " + e.getMessage(), e);
			HttpHeaders headers = new HttpHeaders();
			headers.add("error", messageSource.getMessage("rft.rftcq.load.error", new Object[] { e.getMessage() }, Global.LOCALE));
			return new ResponseEntity<List<SourcingTemplateCqItem>>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	@RequestMapping(value = "/deleteSourcingFormCq", method = RequestMethod.POST)

	public String deleteSourcingFormCq(@RequestParam("cqId") String cqId, @RequestParam("formId") String formId, RedirectAttributes redirect) throws JsonProcessingException {
		SourcingTemplateCq cq = sourcingFormCqService.getSourcingFormCq(cqId);

		try {
			if (cq != null) {
				sourcingFormCqService.deleteCq(cq);
				redirect.addFlashAttribute("success", messageSource.getMessage("sourcing.form.cq.success.delete", new Object[] { cq.getName() }, Global.LOCALE));
			} else {
				redirect.addFlashAttribute("error", messageSource.getMessage("flasherror.while.deleting.cq", new Object[] {}, Global.LOCALE));
			}
			SourcingFormTemplate template = sourcingTemplateService.getSourcingFormbyId(formId);

			long cqCount = sourcingTemplateService.getBqCount(formId);
			if (!(cqCount > 0)) {
				LOG.info("Cq List is not null>>>>>cqCount>>>>>> " + cqCount);
				template.setStatus(SourcingStatus.DRAFT);
				template = sourcingTemplateService.updateSourcingTemplate(template);
			}
			List<SourcingTemplateCq> cqList = sourcingTemplateService.getAllQuestionnarieByOrder(formId);
			if (CollectionUtil.isNotEmpty(cqList)) {
				Integer count = 1;
				for (SourcingTemplateCq templateCq : cqList) {
					templateCq.setCqOrder(count);
					sourcingFormCqService.updateSourcingFormCq(templateCq);
					count++;
				}
			}

		} catch (Exception e) {
			LOG.error("Error while deleting Cq  [ " + (cq != null ? cq.getName() : "") + " ]" + e.getMessage(), e);
			redirect.addFlashAttribute("error", messageSource.getMessage("sourcing.form.cq.error.delete", new Object[] { (cq != null ? cq.getName() : "") }, Global.LOCALE));
		}
		return "redirect:/buyer/sourcingFormCqList/" + formId;

	}

	@RequestMapping(value = "/deleteSourcingTemplateCq/{cqId}", method = RequestMethod.POST)

	public @ResponseBody ResponseEntity<List<SourcingTemplateCqItem>> deleteSourcingTemplateCq(@RequestBody String[] items, @PathVariable("cqId") String cqId) throws JsonProcessingException {
		for (String string : items) {
			LOG.info(string);
		}
		List<SourcingTemplateCqItem> cqItems = null;
		HttpHeaders headers = new HttpHeaders();
		if (items != null && items.length > 0) {
			try {
				sourcingFormCqService.deleteCqItems(items, cqId);
				cqItems = sourcingFormCqService.findCqbyCqId(cqId);
				headers.add("errors", messageSource.getMessage("rft.cq.success.delete", new Object[] {}, Global.LOCALE));
			} catch (Exception e) {
				LOG.error("Error while deleting cqItems : " + e.getMessage(), e);
				headers.add("errors", messageSource.getMessage("rft.cq.info.delete", new Object[] {}, Global.LOCALE));
				return new ResponseEntity<List<SourcingTemplateCqItem>>(null, headers, HttpStatus.BAD_REQUEST);
			}
		} else {
			LOG.error("Please select at least one Cq Item");
			headers.add("errors", messageSource.getMessage("rft.cq.error.delete.required", new Object[] {}, Global.LOCALE));
			return new ResponseEntity<List<SourcingTemplateCqItem>>(null, headers, HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<List<SourcingTemplateCqItem>>(cqItems, headers, HttpStatus.OK);
	}

	@RequestMapping(path = "/requestCqOrder", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<List<SourcingTemplateCqItem>> eventCqOrder(@RequestBody CqItemPojo formCqItemPojo) throws JsonProcessingException {
		LOG.info("rfaCqItemPojo : " + formCqItemPojo.toLogString());
		HttpHeaders headers = new HttpHeaders();
		List<SourcingTemplateCqItem> cqList = null;
		try {
			if (StringUtils.checkString(formCqItemPojo.getId()).length() > 0) {
				LOG.info("Updating order..........................");
				SourcingTemplateCqItem cqItem = sourcingFormCqService.getCqItembyCqItemId(formCqItemPojo.getId());
				if (cqItem.getOrder() > 0 && StringUtils.checkString(formCqItemPojo.getParent()).length() == 0) {
					LOG.info("child cannot be a parent");
					headers.add("error", messageSource.getMessage("rfx.child.reOrder.error", new Object[] {}, Global.LOCALE));
					return new ResponseEntity<List<SourcingTemplateCqItem>>(sourcingFormCqService.findAllCqItembyCqId(sourcingFormCqService.getCqItembyCqItemId(formCqItemPojo.getId()).getCq().getId()), headers, HttpStatus.BAD_REQUEST);
				}
				if (cqItem.getOrder() == 0 && StringUtils.checkString(formCqItemPojo.getParent()).length() > 0) {
					LOG.info("parent cannot be a child");
					headers.add("error", messageSource.getMessage("rfx.parent.reOrder.error", new Object[] {}, Global.LOCALE));
					return new ResponseEntity<List<SourcingTemplateCqItem>>(sourcingFormCqService.findAllCqItembyCqId(sourcingFormCqService.getCqItembyCqItemId(formCqItemPojo.getId()).getCq().getId()), headers, HttpStatus.BAD_REQUEST);
				}
				SourcingTemplateCqItem item = new SourcingTemplateCqItem();
				item.setItemName(formCqItemPojo.getItemName());
				if (cqItem.getParent() != null && !cqItem.getParent().getId().equals(formCqItemPojo.getParent()) && sourcingFormCqService.isExistsItem(item, formCqItemPojo.getCq(), formCqItemPojo.getParent())) {
					LOG.info("Duplicate....");
					throw new ApplicationException("Duplicate CQ Item. CQ Item by that name already exists.");
				}
				sourcingFormCqService.reorderCqItems(formCqItemPojo);

				cqList = sourcingFormCqService.findAllCqItembyCqId(sourcingFormCqService.getCqItembyCqItemId(formCqItemPojo.getId()).getCq().getId());
				headers.add("success", messageSource.getMessage("buyer.rftbq.success", new Object[] {}, Global.LOCALE));
				return new ResponseEntity<List<SourcingTemplateCqItem>>(cqList, headers, HttpStatus.OK);
			}
		} catch (NotAllowedException | ApplicationException nae) {
			LOG.error("Error while moving CQ Items to Form : " + nae.getMessage(), nae);
			headers.add("error", nae.getMessage());
			return new ResponseEntity<List<SourcingTemplateCqItem>>(sourcingFormCqService.findAllCqItembyCqId(sourcingFormCqService.getCqItembyCqItemId(formCqItemPojo.getId()).getCq().getId()), headers, HttpStatus.BAD_REQUEST);

		} catch (Exception e) {
			LOG.error("Error while moving CQ Items to Form : " + e.getMessage(), e);
			// headers.add("error", "Error while adding Notes to Buyer " +
			// e.getMessage());
			headers.add("error", messageSource.getMessage("buyer.rftcq.error", new Object[] { e.getMessage() }, Global.LOCALE));
			return new ResponseEntity<List<SourcingTemplateCqItem>>(null, headers, HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<List<SourcingTemplateCqItem>>(cqList, headers, HttpStatus.OK);
	}

	@RequestMapping(value = "/updateTemplateCqOrder/{formId}", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<List<SourcingTemplateCq>> updateTemplateCqOrder(@RequestBody String[] cqIds, @PathVariable("formId") String formId) {
		try {
			if (cqIds != null && cqIds.length > 0) {
				Integer count = 1;
				for (String cqId : cqIds) {
					SourcingTemplateCq cq = sourcingFormCqService.getSourcingFormCq(cqId);
					cq.setCqOrder(count);
					sourcingFormCqService.updateSourcingFormCq(cq);
					count++;
				}
				List<SourcingTemplateCq> cqList = sourcingTemplateService.getAllQuestionnarieByOrder(formId);
				HttpHeaders headers = new HttpHeaders();
				headers.add("success", messageSource.getMessage("rft.cq.order", new Object[] {}, Global.LOCALE));
				return new ResponseEntity<List<SourcingTemplateCq>>(cqList, headers, HttpStatus.OK);
			} else {
				List<SourcingTemplateCq> cqList = sourcingTemplateService.getAllQuestionnarieByOrder(formId);
				HttpHeaders headers = new HttpHeaders();
				headers.add("error", messageSource.getMessage("rft.cq.order.empty", new Object[] {}, Global.LOCALE));
				return new ResponseEntity<List<SourcingTemplateCq>>(cqList, headers, HttpStatus.BAD_REQUEST);
			}
		} catch (Exception e) {
			LOG.error("Error while Ordering sourcing CQ : " + e.getMessage(), e);
			HttpHeaders headers = new HttpHeaders();
			headers.add("error", messageSource.getMessage("rft.bq.order.error", new Object[] { e.getMessage() }, Global.LOCALE));
			return new ResponseEntity<List<SourcingTemplateCq>>(null, headers, HttpStatus.BAD_REQUEST);
		}
	}

}
