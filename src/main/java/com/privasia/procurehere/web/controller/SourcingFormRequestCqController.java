package com.privasia.procurehere.web.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
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
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.privasia.procurehere.core.dao.SourcingFormCqOptionDao;
import com.privasia.procurehere.core.entity.CqOption;
import com.privasia.procurehere.core.entity.OwnerSettings;
import com.privasia.procurehere.core.entity.RequestAudit;
import com.privasia.procurehere.core.entity.SourcingFormCqOption;
import com.privasia.procurehere.core.entity.SourcingFormRequest;
import com.privasia.procurehere.core.entity.SourcingFormRequestCqItem;
import com.privasia.procurehere.core.entity.SourcingTemplateCq;
import com.privasia.procurehere.core.pojo.SourcingFormReqCqItem;
import com.privasia.procurehere.core.pojo.SourcingFormReqCqItemPojo;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.SecurityLibrary;
import com.privasia.procurehere.core.utils.StringUtils;
import com.privasia.procurehere.service.BusinessUnitService;
import com.privasia.procurehere.service.OwnerSettingsService;
import com.privasia.procurehere.service.SourcingFormCqService;
import com.privasia.procurehere.service.SourcingFormRequestCqItemService;
import com.privasia.procurehere.service.SourcingFormRequestService;
import com.privasia.procurehere.web.editors.BusinessUnitEditor;
import com.privasia.procurehere.web.editors.CqOptionEditor;
import com.privasia.procurehere.web.editors.SourcingFormApprovalEditor;
import com.privasia.procurehere.web.editors.UserEditor;

/**
 * @author pooja
 */
@Controller
@RequestMapping("/buyer")
public class SourcingFormRequestCqController {

	private static final Logger LOG = LogManager.getLogger(Global.PR_LOG);

	@Autowired
	BusinessUnitService businessUnitService;

	@Autowired
	SourcingFormRequestCqItemService sourcingFormRequestCqItemService;

	@Autowired
	UserEditor userEditor;

	@Autowired
	CqOptionEditor cqOptionEditor;

	@Autowired
	SourcingFormApprovalEditor sourcingFormApprovalEditor;

	@Autowired
	BusinessUnitEditor businessUnitEditor;

	@Autowired
	SourcingFormRequestService sourcingFormRequestService;

	@Autowired
	SourcingFormCqService sourcingFormCqService;

	@Autowired
	SourcingFormCqOptionDao sourcingFormCqOptionDao;

	@Autowired
	OwnerSettingsService ownerSettingsService;

	@Resource
	MessageSource messageSource;

	@ModelAttribute("step")
	public String getStep() {
		return "3";
	}

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder.registerCustomEditor(CqOption.class, cqOptionEditor);
		binder.registerCustomEditor(List.class, "listOptAnswers", new CustomCollectionEditor(List.class) {
			protected Object convertElement(Object element) {
				if (element != null) {
					String id = (String) element;
					LOG.info("ID : " + id);
					SourcingFormCqOption group = sourcingFormCqOptionDao.findById(id);
					CqOption op = new CqOption();
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

	@RequestMapping(path = "/sourcingFormRequestCqList/{formId}", method = RequestMethod.GET)
	public String viewSourcingFormRequestCqList(@PathVariable("formId") String formId, Model model) {
		if (StringUtils.checkString(formId).length() == 0) {
			return "redirect:/400_error";
		}
		try {
			LOG.info("Sourcing Form  Request CQ controller called ");
			SourcingFormRequest sourcingFormRequest = sourcingFormRequestService.loadFormById(formId);
			// List<SourcingTemplateCq> sourcingFormCq =
			// sourcingFormRequestCqItemService.findCqsByTempId(sourcingFormRequest.getSourcingForm().getId());
			List<SourcingTemplateCq> sourcingFormCq = sourcingFormRequestCqItemService.getAllQuestionnarieByOrder(sourcingFormRequest.getSourcingForm().getId());
			model.addAttribute("sourcingFormRequest", sourcingFormRequest);
			model.addAttribute("sourcingFormcq", sourcingFormCq);
			model.addAttribute("eventPermissions", sourcingFormRequestService.getUserPemissionsForRequest(SecurityLibrary.getLoggedInUser(), formId));
			model.addAttribute("showCq", true);
			RequestAudit audit = new RequestAudit();
			model.addAttribute("audit", audit);
		} catch (Exception e) {
			LOG.error("Error : " + e.getMessage(), e);
			// model.addAttribute("error", "Error while fetching sourcing Questionnaire List : " + e.getMessage());
			model.addAttribute("error", messageSource.getMessage("error.while.fetching.sourcing.cqlist", new Object[] { e.getMessage() }, Global.LOCALE));
		}
		return "sourcingFormRequestCqList";
	}

	@RequestMapping(path = "/viewSourcingCqDetails/{formId}/{cqId}", method = RequestMethod.GET)
	public String viewSourcingCqDetails(@PathVariable("cqId") String cqId, Model model, @PathVariable("formId") String formId) {
		try {
			LOG.info("CQ ID.............. " + cqId);
			LOG.info("form ID................. " + formId);
			SourcingFormReqCqItemPojo sourcingFormReqCqItemPojo = new SourcingFormReqCqItemPojo();
			sourcingFormReqCqItemPojo.setCqId(cqId);
			SourcingFormRequest sourcingFormRequest = sourcingFormRequestService.loadFormById(formId);
			List<SourcingFormRequestCqItem> sourcingReqCqItem = sourcingFormRequestCqItemService.getAllSourcingCqItemByCqId(cqId, formId);
			LOG.info("Sourcing Req Cq Item LIst------------" + sourcingReqCqItem.size());
			if (CollectionUtil.isEmpty(sourcingReqCqItem)) {
				sourcingFormRequestCqItemService.saveSourcingRequestCq(cqId, sourcingFormRequest);
				sourcingReqCqItem = sourcingFormRequestCqItemService.getAllSourcingCqItemByCqId(cqId, formId);
			}
			LOG.info("Size of Cq Item:---------------------" + sourcingReqCqItem.size());
			List<SourcingFormReqCqItem> itemList = new ArrayList<SourcingFormReqCqItem>();
			Map<String, List<SourcingFormCqOption>> optinonList = new HashMap<String, List<SourcingFormCqOption>>();
			for (SourcingFormRequestCqItem cqItem : sourcingReqCqItem) {
				SourcingFormReqCqItem itemObj = new SourcingFormReqCqItem(cqItem);
				itemList.add(itemObj);
				
				if(cqItem.getCqItem().getOrder() > 0) {
					LOG.info(">>>>>>>>>>>>> level "+cqItem.getCqItem().getLevel() +"........... order " +cqItem.getCqItem().getOrder());
					List<SourcingFormCqOption> optinons = sourcingFormRequestCqItemService.findCqItemOptionForCqItemId(cqItem.getCqItem().getId(), itemObj);
					optinonList.put(cqItem.getCqItem().getId(), optinons);
				}
			}
			sourcingFormReqCqItemPojo.setItemList(itemList);
			model.addAttribute("sourcingFormReqCqItemPojo", sourcingFormReqCqItemPojo);
			model.addAttribute("sourcingFormRequest", sourcingFormRequest);
			
			model.addAttribute("optinonList", optinonList);
			model.addAttribute("showCq", false);
			model.addAttribute("audit", new RequestAudit());
		} catch (Exception e) {
			LOG.error("Error : " + e.getMessage(), e);
			// model.addAttribute("error", "Error while fetching sourcing Questionnaire details : " + e.getMessage());
			model.addAttribute("error", messageSource.getMessage("error.while.fetching.sourcing.cqdetails", new Object[] { e.getMessage() }, Global.LOCALE));
		}
		return "sourcingFormRequestCqList";
	}

	@RequestMapping(path = "/createSourcingDetailsPrevious", method = RequestMethod.POST)
	public String eventDescriptionPrevious(@ModelAttribute("sourcingFormRequest") SourcingFormRequest sourcingFormRequest, Model model) {
		LOG.info("Previous called in Sourcing Questionree : " + sourcingFormRequest.getId());
		sourcingFormRequest = sourcingFormRequestService.loadFormById(sourcingFormRequest.getId());
		return "redirect:createSourcingFormDetails/" + sourcingFormRequest.getId();
	}

	@RequestMapping(path = "/saveSourcingReqCqDetails/{formId}", method = RequestMethod.POST)
	public String submitSourcingReqCqItems(@ModelAttribute("sourcingFormReqCqItemPojo") SourcingFormReqCqItemPojo sourcingFormReqCqItemPojo, Model model, @PathVariable("formId") String formId, RedirectAttributes redir) throws IOException {
		LOG.info("Form Id..................." + formId);

		try {
			SourcingFormRequest request = sourcingFormRequestService.getSourcingRequestById(formId);
			if (sourcingFormReqCqItemPojo.getItemList() != null) {
				List<SourcingFormReqCqItem> itemList = sourcingFormReqCqItemPojo.getItemList();
				LOG.info("item List size-----------" + itemList.size());
				if (CollectionUtil.isNotEmpty(itemList)) {
					List<SourcingFormRequestCqItem> list = new ArrayList<SourcingFormRequestCqItem>();
					for (SourcingFormReqCqItem item : itemList) {

						item.setSourcingFormRequest(sourcingFormRequestService.getSourcingRequestById(formId));
						item.setCq(sourcingFormCqService.getSourcingFormCq(item.getCq().getId()));
						item.setCqItem(sourcingFormCqService.getSourcingCqItembyItemId(item.getCqItem().getId()));
						SourcingFormRequestCqItem obj = new SourcingFormRequestCqItem(item);
						SourcingFormRequestCqItem sourcingCqItem = sourcingFormRequestCqItemService.findCqBySourcingReqIdAndCqItemId(formId, item.getId());
						if ((item.getAttachment() == null || (item.getAttachment() != null & item.getAttachment().getBytes().length == 0)) && StringUtils.checkString(sourcingCqItem.getFileName()).length() > 0) {

							obj.setFileData(sourcingCqItem.getFileData());
							obj.setFileName(sourcingCqItem.getFileName());
							obj.setCredContentType(sourcingCqItem.getCredContentType());
						}
						list.add(obj);
					}
					LOG.info("list   :" + list);

					sourcingFormRequestCqItemService.updateSourcingCqItem(list);
					if (request != null) {
						// request.setCqCompleted(true);
						sourcingFormRequestService.update(request);
					}
				}
			}
		} catch (Exception e) {
			LOG.error("Error saving Sourcing Request cq items : " + e.getMessage(), e);
			redir.addAttribute("error", "Error saving Sourcing Request cq items : " + e.getMessage());
		}
		return "redirect:/buyer/sourcingFormRequestCqList/" + formId;
	}

	@RequestMapping(path = "/resetCqAttachment/{formId}/{itemId}", method = RequestMethod.POST)
	public ResponseEntity<Boolean> resetCqAttachment(@PathVariable("formId") String formId, @PathVariable("itemId") String itemId) {
		LOG.info("reset attach called..............");
		HttpHeaders headers = new HttpHeaders();
		boolean removed = false;
		try {
			removed = sourcingFormRequestCqItemService.resetAttachement(itemId, formId);
		} catch (Exception e) {
			LOG.error("Error during reset of cq attachment : " + e.getMessage(), e);
			headers.add("error", "Error while removing cq attachment for Questionnaire Item");
			return new ResponseEntity<Boolean>(removed, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		headers.add("sucess", "Removed attachment for Cq Item");
		return new ResponseEntity<Boolean>(removed, headers, HttpStatus.OK);
	}

	@RequestMapping(path = "/downloadCqAttachment/{formId}/{itemId}", method = RequestMethod.GET)
	public void downloadAttachment(@PathVariable("formId") String formId, @PathVariable("itemId") String itemId, HttpServletResponse response) {
		try {
			SourcingFormRequestCqItem cqItem = sourcingFormRequestCqItemService.findCqBySourcingReqIdAndCqItemId(formId, itemId);
			response.setContentType(cqItem.getCredContentType());
			response.setContentLength(cqItem.getFileData().length);
			response.setHeader("Content-Disposition", "attachment; filename=\"" + cqItem.getFileName() + "\"");
			FileCopyUtils.copy(cqItem.getFileData(), response.getOutputStream());
			response.flushBuffer();
			response.setStatus(HttpServletResponse.SC_OK);
		} catch (Exception e) {
			LOG.error("Error while File downloaded : " + e.getMessage(), e);
		}
	}

	@RequestMapping(value = "/updateSourcingCqOrder/{formId}", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<List<SourcingTemplateCq>> updateSourcingCqOrder(@RequestBody String[] cqIds, @PathVariable("formId") String formId) {
		try {
			if (cqIds != null && cqIds.length > 0) {
				Integer count = 1;
				for (String cqId : cqIds) {
					SourcingTemplateCq cq = sourcingFormCqService.getSourcingFormCq(cqId);
					cq.setCqOrder(count);
					sourcingFormCqService.updateSourcingFormCq(cq);
					count++;
				}
				List<SourcingTemplateCq> cqList = sourcingFormRequestCqItemService.getAllQuestionnarieByOrder(formId);
				HttpHeaders headers = new HttpHeaders();
				headers.add("success", messageSource.getMessage("rft.cq.order", new Object[] {}, Global.LOCALE));
				return new ResponseEntity<List<SourcingTemplateCq>>(cqList, headers, HttpStatus.OK);
			} else {
				List<SourcingTemplateCq> cqList = sourcingFormRequestCqItemService.getAllQuestionnarieByOrder(formId);
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
	
	@PostMapping("/searchCqItemOptionFromList")
	public @ResponseBody ResponseEntity<List<SourcingFormCqOption>> searchCqItemOptionFromList(@RequestParam("cqItemOption") String cqItemOption, @RequestParam("cqItemId") String cqItemId) {
		LOG.info(">>>>>>>>>>>>>>>>>> searchVal: "+cqItemOption + " ..........cqItemId "+cqItemId);
		List<SourcingFormCqOption> cqOptionList = sourcingFormRequestCqItemService.findCqItemOptionForCqItemId(cqItemOption, cqItemId);
		return new ResponseEntity<List<SourcingFormCqOption>>(cqOptionList, HttpStatus.OK);
	}
	
}
