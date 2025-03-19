package com.privasia.procurehere.web.controller;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import org.springframework.beans.factory.annotation.Autowired;
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
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.privasia.procurehere.core.entity.EventDocument;
import com.privasia.procurehere.core.entity.RfpCq;
import com.privasia.procurehere.core.entity.RfpCqItem;
import com.privasia.procurehere.core.entity.RfpCqOption;
import com.privasia.procurehere.core.entity.RfpEvent;
import com.privasia.procurehere.core.enums.CqType;
import com.privasia.procurehere.core.enums.EventVisibilityType;
import com.privasia.procurehere.core.enums.RfxTypes;
import com.privasia.procurehere.core.exceptions.ApplicationException;
import com.privasia.procurehere.core.exceptions.NotAllowedException;
import com.privasia.procurehere.core.pojo.CqItemPojo;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.SecurityLibrary;
import com.privasia.procurehere.core.utils.StringUtils;
import com.privasia.procurehere.service.RfpCqService;
import com.privasia.procurehere.service.RfpDocumentService;
import com.privasia.procurehere.service.RfpEventService;
import com.privasia.procurehere.service.UomService;

@Controller
@RequestMapping("/buyer/RFP")
public class RfpCqController extends EventCqBase {

	private static final Logger LOG = LogManager.getLogger(Global.ADMIN_LOG);

	@Autowired
	UomService uomService;

	@Autowired
	RfpCqService rfpCqService;

	@Autowired
	RfpEventService rfpEventService;

	@Autowired
	RfpDocumentService rfpDocumentService;

	public RfpCqController() {
		super(RfxTypes.RFP);
	}

	@RequestMapping(path = "/eventCqList/{eventId}", method = RequestMethod.GET)
	public String eventCqList(@PathVariable String eventId, Model model) {
		if (StringUtils.checkString(eventId).length() == 0) {
			return "redirect:/400_error";
		}
		// List<RfpCq> cqList = rfpCqService.findCqForEvent(eventId);
		List<RfpCq> cqList = rfpCqService.findCqForEventByOrder(eventId);

		RfpCq rftCq = new RfpCq();
		RfpEvent rftEvent = new RfpEvent();
		rftEvent.setId(eventId);
		rftCq.setRfxEvent(rftEvent);
		model.addAttribute("cqList", cqList);
		model.addAttribute("rftCq", rftCq);
		model.addAttribute("eventId", eventId);
		model.addAttribute("eventPermissions", rfpEventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), eventId));
		model.addAttribute("event", rfpCqService.findEventForCqByEventId(eventId));

		return "eventCqList";
	}

	@RequestMapping(path = "/createRftCq", method = RequestMethod.POST)
	public String createRftCqPost(@ModelAttribute RfpCq rfpCq, Model model, BindingResult result, RedirectAttributes redir) {
		if (result.hasErrors()) {
			LOG.error("Page submitted With Errors ............................. ");
			for (ObjectError error : result.getAllErrors()) {
				redir.addFlashAttribute("errors", error.getDefaultMessage());
			}
			model.addAttribute("rftCq", rfpCq);
		} else {
			model.addAttribute("eventPermissions", rfpEventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), rfpCq.getRfxEvent().getId()));
			LOG.info("Submit with no errors..............................................." + rfpCq.getRfxEvent().getId());
			RfpCq cq = null;
			try {
				if (rfpCqService.isExists(rfpCq)) {
					LOG.info("Duplicate....");
					throw new ApplicationException("Duplicate CQ. CQ by that name already exists.");
				}
				RfpEvent rftEvent = rfpCqService.findEventForCqByEventId(rfpCq.getRfxEvent().getId());
				rfpCq.setRfxEvent(rftEvent);
				cq = rfpCqService.stroreCq(rfpCq);
				List<RfpCq> cqList = rfpCqService.findCqForEventByOrder(rfpCq.getRfxEvent().getId());
				model.addAttribute("cqList", cqList);
				model.addAttribute("eventId", rftEvent.getId());
				model.addAttribute("event", rftEvent);
				if (StringUtils.checkString(rfpCq.getId()).length() == 0) {
					redir.addFlashAttribute("success", messageSource.getMessage("rft.cq.save", new Object[] {}, Global.LOCALE));
					return "redirect:eventCqList/" + rfpCq.getRfxEvent().getId();
				} else {
					LOG.info("cqId " + rfpCq.getId());
					redir.addFlashAttribute("success", messageSource.getMessage("rft.cq.update", new Object[] {}, Global.LOCALE));
					return "redirect:eventCqItemList/" + rfpCq.getId();
				}
			} catch (Exception e) {
				LOG.error("Error while storing CQ," + e.getMessage(), e);
				redir.addFlashAttribute("error", messageSource.getMessage("rft.cq.error", new Object[] { rfpCq.getName(), e.getMessage() }, Global.LOCALE));
				if (StringUtils.checkString(rfpCq.getId()).length() == 0) {
					return "redirect:eventCqList/" + rfpCq.getRfxEvent().getId();
				} else {
					LOG.info("cqId " + rfpCq.getId());
					return "redirect:eventCqItemList/" + rfpCq.getId();
				}
			}

		}
		return "redirect:eventCqList/" + rfpCq.getRfxEvent().getId();
	}

	@RequestMapping(value = "/deleteCq", method = RequestMethod.POST)
	public String deleteCq(@ModelAttribute RfpCq rftCq, Model model, RedirectAttributes redir) throws JsonProcessingException {
		RfpCq cq = null;
		try {
			cq = rfpCqService.getCqById(rftCq.getId());
			rfpCqService.isAllowToDeleteCq(rftCq.getId());
			rfpCqService.deleteCq(cq);

			// List<RfpCq> cqList = rfpCqService.findCqForEvent(cq.getRfxEvent().getId());
			// RfpEvent rftEvent = new RfpEvent();
			// rftEvent.setId(cq.getRfxEvent().getId());
			// rftCq.setRfxEvent(rftEvent);
			// model.addAttribute("cqList", cqList);
			// model.addAttribute("rftCq", rftCq);
			// model.addAttribute("event", cq.getRfxEvent());
			// model.addAttribute("eventId", cq.getRfxEvent().getId());
			// model.addAttribute("eventPermissions",
			// rfpEventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(),
			// rftCq.getRfxEvent().getId()));
			redir.addFlashAttribute("success", messageSource.getMessage("rft.cq.success.delete", new Object[] { cq.getName() }, Global.LOCALE));
		} catch (NotAllowedException e) {
			LOG.error("Error while deleting cqItems : " + e.getMessage(), e);
			model.addAttribute("error", e.getMessage());
			List<RfpCq> cqList = rfpCqService.findCqForEventByOrder(cq.getRfxEvent().getId());
			RfpEvent rfpEvent = rfpCqService.findEventForCqByEventId(cq.getRfxEvent().getId());
			rftCq.setRfxEvent(rfpEvent);
			model.addAttribute("cqList", cqList);
			model.addAttribute("rftCq", rftCq);
			model.addAttribute("event", rfpEvent);
			model.addAttribute("eventId", cq.getRfxEvent().getId());
			return "eventCqList";
		} catch (Exception e) {
			LOG.error("Error while deleting Cq  [ " + (cq != null ? cq.getName() : "") + " ]" + e.getMessage(), e);
			model.addAttribute("error", messageSource.getMessage("rft.cq.error.delete", new Object[] { (cq != null ? cq.getName() : "") }, Global.LOCALE));
			List<RfpCq> cqList = rfpCqService.findCqForEventByOrder(cq.getRfxEvent().getId());
			RfpEvent rfpEvent = rfpCqService.findEventForCqByEventId(rftCq.getRfxEvent().getId());
			rftCq.setRfxEvent(rfpEvent);
			model.addAttribute("cqList", cqList);
			model.addAttribute("rftCq", rftCq);
			model.addAttribute("eventId", cq.getRfxEvent().getId());
			model.addAttribute("event", rfpEvent);
			return "eventCqList";
		}
		return "redirect:eventCqList/" + cq.getRfxEvent().getId();
	}

	@RequestMapping(path = "/showCqItems", method = RequestMethod.POST)
	public String showCqItemList(@RequestParam(name = "cqId", required = true) String cqId, @RequestParam(name = "eventId", required = true) String eventId, RedirectAttributes reAttributes) {
		RfpCq cq = rfpCqService.getCqById(cqId);
		reAttributes.addFlashAttribute("eventId", eventId);
		return "redirect:eventCqItemList/" + cq.getId();
	}

	@RequestMapping(path = "/eventCqItemList/{cqId}", method = RequestMethod.GET)
	public String eventCqItemList(@PathVariable String cqId, Model model) {
		if (StringUtils.checkString(cqId).length() == 0) {
			return "redirect:/400_error";
		}
		RfpCq rftCq = rfpCqService.getCqById(cqId);
		LOG.info("CQ : " + rftCq.getId());
		List<RfpCqItem> cqItemList = rfpCqService.findCqbyCqId(cqId);
		String eventId = (String) model.asMap().get("eventId");

		if (eventId == null || eventId.length() == 0) {
			eventId = rftCq.getRfxEvent().getId();
		}
		if (eventId != null && eventId.length() > 0) {
			List<EventDocument> eventDocumentList = rfpDocumentService.findAllRfpEventDocsNameByEventId(eventId);
			model.addAttribute("eventDocumentList", eventDocumentList);
		}

		model.addAttribute("rftCq", rftCq);
		model.addAttribute("cqItemList", cqItemList);
		model.addAttribute("event", rfpCqService.findEventForCqByEventId(rftCq.getRfxEvent().getId()));
		model.addAttribute("eventPermissions", rfpEventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), rftCq.getRfxEvent().getId()));
		model.addAttribute("cqTypes", CqType.values());
		RfpCqItem cqItem = new RfpCqItem();
		cqItem.setCq(rftCq);
		model.addAttribute("cqItem", cqItem);
		return "eventCqItemList";
	}

	@RequestMapping(path = "/createRftCqItem", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<List<RfpCqItem>> createRfpCqItem(@RequestBody CqItemPojo rfpCqItem) throws JsonProcessingException {
		HttpHeaders headers = new HttpHeaders();
		try {
			LOG.info("CQ ITEM : " + rfpCqItem.toLogString());
			if (!doValidate(rfpCqItem, headers)) {
				return new ResponseEntity<List<RfpCqItem>>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
			}
			if (StringUtils.checkString(rfpCqItem.getItemName()).length() > 0) {
				RfpCqItem item = new RfpCqItem();
				item.setItemName(rfpCqItem.getItemName());
				item.setItemDescription(rfpCqItem.getItemDescription());
				item.setIsSupplierAttachRequired(rfpCqItem.getIsSupplierAttachRequired());

				if (rfpCqService.isExists(item, rfpCqItem.getCq(), rfpCqItem.getParent())) {
					LOG.info("Duplicate....");
					throw new ApplicationException("Duplicate CQ Item. CQ Item by that name already exists.");
				}

				item.setRfxEvent(rfpCqService.getEventById(rfpCqItem.getRftEvent()));
				item.setCq(rfpCqService.getCqById(rfpCqItem.getCq()));
				if (StringUtils.checkString(rfpCqItem.getCqType()).length() > 0) {
					item.setCqType(CqType.valueOf(rfpCqItem.getCqType()));
				}
				item.setAttachment(rfpCqItem.isAttachment());
				item.setOptional(rfpCqItem.isOptional());
				if (CollectionUtil.isNotEmpty(rfpCqItem.getOptions())) {
					List<RfpCqOption> optionItems = new ArrayList<RfpCqOption>();
					int optionOrder = 0;
					int totalScore = 0;
					for (String option : rfpCqItem.getOptions()) {
						if (StringUtils.checkString(option).length() == 0)
							continue;
						RfpCqOption options = new RfpCqOption();
						options.setRfpCqItem(item);
						options.setValue(option);
						options.setOrder(++optionOrder);
						if (CollectionUtil.isNotEmpty(rfpCqItem.getOptionScore()))
							options.setScoring(Integer.parseInt(rfpCqItem.getOptionScore().get(optionOrder - 1)));
						totalScore += options.getScoring() != null ? options.getScoring().intValue() : 0;
						optionItems.add(options);
					}
					item.setTotalScore(totalScore);
					item.setCqOptions(optionItems);
				}
				if (StringUtils.checkString(rfpCqItem.getParent()).length() > 0) {
					item.setParent(rfpCqService.getCqItembyCqItemId(StringUtils.checkString(rfpCqItem.getParent())));
				}
				rfpCqService.saveCqItem(item);

				List<RfpCqItem> bqList = rfpCqService.findCqbyCqId(rfpCqItem.getCq());
				headers.add("success", messageSource.getMessage("buyer.rftcq.save", new Object[] {}, Global.LOCALE));
				return new ResponseEntity<List<RfpCqItem>>(bqList, headers, HttpStatus.OK);
			} else {
				List<RfpCqItem> bqList = rfpCqService.findCqbyCqId(rfpCqItem.getCq());
				headers.add("info", messageSource.getMessage("buyer.rftcq.duplicate", new Object[] {}, Global.LOCALE));
				return new ResponseEntity<List<RfpCqItem>>(bqList, headers, HttpStatus.OK);
			}
		} catch (Exception e) {
			LOG.error("Error while adding CQ Items to Event : " + e.getMessage(), e);
			headers.add("error", messageSource.getMessage("buyer.rftcq.error", new Object[] { e.getMessage() }, Global.LOCALE));
			return new ResponseEntity<List<RfpCqItem>>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(path = "/getCqData", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<List<CqItemPojo>> getCqData(@RequestParam("itemId") String itemId) throws JsonProcessingException {
		try {

			if (StringUtils.checkString(itemId).length() > 0) {
				RfpCqItem item = rfpCqService.getCqItembyCqItemId(StringUtils.checkString(itemId));
				LOG.info("=================================================" + item.getIsSupplierAttachRequired());
				CqItemPojo pojo = new CqItemPojo(item);
				List<CqItemPojo> cqItemList = new ArrayList<CqItemPojo>();
				cqItemList.add(pojo);
				HttpHeaders headers = new HttpHeaders();
				return new ResponseEntity<List<CqItemPojo>>(cqItemList, headers, HttpStatus.OK);
			} else {
				List<CqItemPojo> cqItemList = new ArrayList<CqItemPojo>();
				HttpHeaders headers = new HttpHeaders();
				headers.add("info", messageSource.getMessage("rft.rftcq.load.error", new Object[] {}, Global.LOCALE));
				return new ResponseEntity<List<CqItemPojo>>(cqItemList, headers, HttpStatus.OK);
			}

		} catch (Exception e) {
			LOG.error("Error while getting CQ Items to Buyer : " + e.getMessage(), e);
			HttpHeaders headers = new HttpHeaders();
			headers.add("error", messageSource.getMessage("rft.rftcq.load.error", new Object[] { e.getMessage() }, Global.LOCALE));
			return new ResponseEntity<List<CqItemPojo>>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(value = "/updateCqItem", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<List<RfpCqItem>> updateCqItem(@RequestBody CqItemPojo itemPojo) throws JsonProcessingException {
		HttpHeaders headers = new HttpHeaders();
		LOG.info(" :: UPDATE CQ ::" + itemPojo.toLogString());
		try {
			if (!doValidate(itemPojo, headers)) {
				return new ResponseEntity<List<RfpCqItem>>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
			}
			if (StringUtils.checkString(itemPojo.getItemName()).length() > 0) {
				RfpCqItem item = new RfpCqItem();
				item.setId(itemPojo.getId());
				item.setItemName(itemPojo.getItemName());
				LOG.info("Attach Description before-------" + itemPojo.getIsSupplierAttachRequired());
				item.setIsSupplierAttachRequired(itemPojo.getIsSupplierAttachRequired());
				LOG.info("Attach Description-------" + item.getIsSupplierAttachRequired());
				item.setItemDescription(itemPojo.getItemDescription());
				if (rfpCqService.isExists(item, itemPojo.getCq(), itemPojo.getParent())) {
					LOG.info("Duplicate....");
					throw new ApplicationException("Duplicate CQ Item. CQ Item by that name already exists.");
				}
				rfpCqService.updateCqItem(itemPojo);
				List<RfpCqItem> cqList = rfpCqService.findCqbyCqId(itemPojo.getCq());
				headers.add("success", messageSource.getMessage("buyer.rftcq.update", new Object[] {}, Global.LOCALE));
				return new ResponseEntity<List<RfpCqItem>>(cqList, headers, HttpStatus.OK);
			} else {
				List<RfpCqItem> cqList = rfpCqService.findCqbyCqId(itemPojo.getCq());
				headers.add("info", messageSource.getMessage("rft.cq.name.empty", new Object[] {}, Global.LOCALE));
				return new ResponseEntity<List<RfpCqItem>>(cqList, headers, HttpStatus.OK);
			}
		} catch (Exception e) {
			LOG.error("Error while updating CQ Items to Buyer : " + e.getMessage(), e);
			headers.add("error", messageSource.getMessage("buyer.rftcq.error", new Object[] { e.getMessage() }, Global.LOCALE));
			return new ResponseEntity<List<RfpCqItem>>(null, headers, HttpStatus.BAD_REQUEST);
		}
	}

	@RequestMapping(value = "/deleteCqItem/{cqId}", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<List<RfpCqItem>> deleteCqItem(@RequestBody String[] items, @PathVariable("cqId") String cqId) throws JsonProcessingException {
		LOG.info("DELETE CQ ITEM REQUESTED : " + items.toString());
		List<RfpCqItem> cqItems = null;
		HttpHeaders headers = new HttpHeaders();
		if (items != null && items.length > 0) {
			try {
				rfpCqService.deleteCqItems(items, cqId);
				cqItems = rfpCqService.findCqbyCqId(cqId);
				headers.add("errors", messageSource.getMessage("rfx.cq.success.delete", new Object[] {}, Global.LOCALE));
			} catch (NotAllowedException e) {
				LOG.error("Error while deleting cqItems : " + e.getMessage(), e);
				headers.add("errors", messageSource.getMessage("rft.cq.info.delete", new Object[] {}, Global.LOCALE));
				return new ResponseEntity<List<RfpCqItem>>(null, headers, HttpStatus.BAD_REQUEST);
			}
		} else {
			LOG.error("Please select at least one Cq Item");
			headers.add("errors", messageSource.getMessage("rft.cq.error.delete.required", new Object[] {}, Global.LOCALE));
			return new ResponseEntity<List<RfpCqItem>>(null, headers, HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<List<RfpCqItem>>(cqItems, headers, HttpStatus.OK);
	}

	@RequestMapping(path = "/eventCqOrder", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<List<RfpCqItem>> eventCqOrder(@RequestBody CqItemPojo rftCqItemPojo) throws JsonProcessingException {
		LOG.info("Parent : " + rftCqItemPojo.getParent() + " Item Id : " + rftCqItemPojo.getId() + " New Position : " + rftCqItemPojo.getOrder());
		HttpHeaders headers = new HttpHeaders();
		List<RfpCqItem> cqList = null;
		try {
			if (StringUtils.checkString(rftCqItemPojo.getId()).length() > 0) {
				LOG.info("Updating order..........................");
				RfpCqItem cqItem = rfpCqService.getCqItembyCqItemId(rftCqItemPojo.getId());
				if (cqItem.getOrder() > 0 && StringUtils.checkString(rftCqItemPojo.getParent()).length() == 0) {
					LOG.info("child cannot be a parent");
					headers.add("error", messageSource.getMessage("rfx.child.reOrder.error", new Object[] {}, Global.LOCALE));
					return new ResponseEntity<List<RfpCqItem>>(rfpCqService.findCqbyCqId(rfpCqService.getCqItembyCqItemId(rftCqItemPojo.getId()).getCq().getId()), headers, HttpStatus.BAD_REQUEST);
				}
				if (cqItem.getOrder() == 0 && StringUtils.checkString(rftCqItemPojo.getParent()).length() > 0) {
					LOG.info("parent cannot be a child");
					headers.add("error", messageSource.getMessage("rfx.parent.reOrder.error", new Object[] {}, Global.LOCALE));
					return new ResponseEntity<List<RfpCqItem>>(rfpCqService.findCqbyCqId(rfpCqService.getCqItembyCqItemId(rftCqItemPojo.getId()).getCq().getId()), headers, HttpStatus.BAD_REQUEST);
				}
				RfpCqItem item = new RfpCqItem();
				item.setItemName(rftCqItemPojo.getItemName());
				if (cqItem.getParent() != null && !cqItem.getParent().getId().equals(rftCqItemPojo.getParent()) && rfpCqService.isExists(item, rftCqItemPojo.getCq(), rftCqItemPojo.getParent())) {
					LOG.info("Duplicate....");
					throw new ApplicationException("Duplicate CQ Item. CQ Item by that name already exists.");
				}
				rfpCqService.reorderCqItems(rftCqItemPojo);
				cqList = rfpCqService.findCqbyCqId(rfpCqService.getCqItembyCqItemId(rftCqItemPojo.getId()).getCq().getId());
				headers.add("success", messageSource.getMessage("buyer.rftbq.success", new Object[] {}, Global.LOCALE));
				return new ResponseEntity<List<RfpCqItem>>(cqList, headers, HttpStatus.OK);
			}
		} catch (NotAllowedException | ApplicationException nae) {
			LOG.error("Error while adding CQ Items to Event : " + nae.getMessage(), nae);
			headers.add("error", nae.getMessage());
			return new ResponseEntity<List<RfpCqItem>>(rfpCqService.findCqbyCqId(rfpCqService.getCqItembyCqItemId(rftCqItemPojo.getId()).getCq().getId()), headers, HttpStatus.BAD_REQUEST);

		} catch (Exception e) {
			LOG.error("Error while adding CQ Items to Event : " + e.getMessage(), e);
			headers.add("error", messageSource.getMessage("buyer.rftcq.error", new Object[] { e.getMessage() }, Global.LOCALE));
			return new ResponseEntity<List<RfpCqItem>>(null, headers, HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<List<RfpCqItem>>(cqList, headers, HttpStatus.OK);
	}

	@RequestMapping(value = "/cqPrevious", method = RequestMethod.POST)
	public String cqPrevious(Model model, @RequestParam(name = "eventId", required = true) String eventId) throws Exception {
		try {
			RfpEvent rftEvent = rfpCqService.getEventById(eventId);
			model.addAttribute("eventPermissions", rfpEventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), eventId));

			if (rftEvent != null) {
				if (Boolean.TRUE == rftEvent.getMeetingReq()) {
					return "redirect:meetingList/" + rftEvent.getId();
				} else if (rftEvent.getEventVisibility() != EventVisibilityType.PUBLIC) {
					return "redirect:addSupplier/" + rftEvent.getId();
				} else if (Boolean.TRUE == rftEvent.getDocumentReq()) {
					return "redirect:createEventDocuments/" + rftEvent.getId();
				} else {
					return "redirect:eventDescription/" + rftEvent.getId();
				}
			} else {
				LOG.error("Event not found redirecting to login ");
				return "/";
			}
		} catch (Exception e) {
			LOG.error("Event not found redirecting to login : " + e.getMessage(), e);
			throw new Exception("Event not found redirecting to login : " + e.getMessage());
		}
	}

	@RequestMapping(value = "/cqListPrevious", method = RequestMethod.POST)
	public String cqListPrevious(Model model, @RequestParam(name = "eventId", required = true) String eventId) throws Exception {
		try {
			RfpEvent rftEvent = rfpCqService.getEventById(eventId);
			if (rftEvent != null) {
				model.addAttribute("eventPermissions", rfpEventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), eventId));

				return "redirect:eventCqList/" + rftEvent.getId();
			} else {
				LOG.error("Event not found redirecting to login ");
				return "/";
			}
		} catch (Exception e) {
			LOG.error("Event not found redirecting to login : " + e.getMessage(), e);
			throw new Exception("Event not found redirecting to login : " + e.getMessage());
		}
	}

	@RequestMapping(value = "/cqNext", method = RequestMethod.POST)
	public String cqNext(Model model, @RequestParam(name = "eventId", required = true) String eventId) throws Exception {
		try {
			RfpEvent event = rfpCqService.getEventById(eventId);
			if (event != null) {
				event.setCqCompleted(Boolean.TRUE);
				model.addAttribute("eventPermissions", rfpEventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), eventId));
				rfpEventService.updateEvent(event);
				if (Boolean.TRUE == event.getBillOfQuantity()) {
					return "redirect:createBQList/" + eventId;
				} else if(Boolean.TRUE == event.getScheduleOfRate()) {
					return "redirect:createSorList/" + eventId;
				} else {
					return "redirect:envelopList/" + eventId;
				}
			} else {
				LOG.error("Event not found redirecting to login ");
				return "/";
			}
		} catch (Exception e) {
			LOG.error("Event not found redirecting to login : " + e.getMessage(), e);
			throw new Exception("Event not found redirecting to login : " + e.getMessage());
		}
	}

	@RequestMapping(value = "/cqSaveDraft", method = RequestMethod.POST)
	public String cqSaveDraft(RedirectAttributes redir, @RequestParam("eventId") String eventId, Model model) {
		RfpEvent rftEvent = rfpCqService.getEventById(eventId);
		model.addAttribute("eventPermissions", rfpEventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), eventId));
		redir.addFlashAttribute("success", messageSource.getMessage("rftEvent.success.savedraft", new Object[] { (rftEvent.getEventName() != null ? rftEvent.getEventName() : rftEvent.getEventId()) }, Global.LOCALE));
		return "redirect:eventCqList/" + rftEvent.getId();

	}

	@RequestMapping(path = "/cqItemTemplate/{cqId}", method = RequestMethod.GET)
	public void downloader(@PathVariable String cqId, HttpServletRequest request, HttpServletResponse response) throws IOException {
		try {
			XSSFWorkbook workbook = new XSSFWorkbook();
			String downloadFolder = context.getRealPath("/WEB-INF/");
			String fileName = "CqItemTemplate.xlsx";
			Path file = Paths.get(downloadFolder, fileName);
			LOG.info("File Path ::" + file);

			super.eventDownloader(workbook, cqId, RfxTypes.RFP);

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
					LOG.error("Error :- " + e.getMessage());
				}
			}

		} catch (Exception e) {
			LOG.error("Error while downloading BQ items :: " + e.getMessage(), e);
		}
	}

	@RequestMapping(value = "/uploadCqItems", method = RequestMethod.POST)
	public ResponseEntity<List<RfpCqItem>> handleFormUpload(@RequestParam("file") MultipartFile file, @RequestParam("cqId") String cqId, @RequestParam("eventId") String eventId) throws IOException {
		LOG.info("UPLOADING STARTED ...... CQ Id :: " + cqId + "Event Id :: " + eventId);
		String message = null;
		List<RfpCqItem> cqList = null;
		HttpHeaders headers = new HttpHeaders();
		try {
			if (!file.isEmpty()) {
				if (!file.getContentType().equals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet") && !file.getContentType().equals("application/wps-office.xlsx") && !file.getContentType().equals("application/vnd.ms-excel"))
					throw new MultipartException("Only excel files accepted!");
				try {
					message = super.cqItemsUpload(file, cqId, eventId, RfxTypes.RFP);

				} catch (Exception e) {
					e.printStackTrace();
					cqList = rfpCqService.findCqbyCqId(cqId);
					headers.add("error", messageSource.getMessage("common.upload.fail", new Object[] { e.getMessage() }, Global.LOCALE));
					return new ResponseEntity<List<RfpCqItem>>(cqList, headers, HttpStatus.BAD_REQUEST);
				}
			}
		} catch (MultipartException e) {
			// e.printStackTrace();
			// LOG.info("Upload failed!" + e.getMessage());
			cqList = rfpCqService.findCqbyCqId(cqId);
			headers.add("error", messageSource.getMessage("common.upload.notsupported.fail", new Object[] { e.getMessage() }, Global.LOCALE));
			return new ResponseEntity<List<RfpCqItem>>(cqList, headers, HttpStatus.BAD_REQUEST);
		}
		// return "redirect:createEventBQ/" + eventId + "/" + cqId;
		cqList = rfpCqService.findCqbyCqId(cqId);

		if (message == null) {
			headers.add("success", messageSource.getMessage("common.upload.success", new Object[] { "RFP CQ Items" }, Global.LOCALE));
		} else {
			headers.add("warn", messageSource.getMessage("common.upload.success", new Object[] { "RFP CQ Items" }, Global.LOCALE) + ", " + message);
		}
		return new ResponseEntity<List<RfpCqItem>>(cqList, headers, HttpStatus.OK);
	}

	@RequestMapping(value = "/updateEventCqOrder/{eventId}", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<List<RfpCq>> updateEventCqOrder(@RequestBody String[] cqIds, @PathVariable("eventId") String eventId) {
		try {
			LOG.info("RFP eventId : " + eventId);
			if (cqIds != null && cqIds.length > 0) {
				Integer count = 1;
				for (String cqId : cqIds) {
					RfpCq cq = rfpCqService.getCqById(cqId);
					cq.setCqOrder(count);
					rfpCqService.updateCq(cq);
					count++;
				}
				List<RfpCq> cqList = rfpCqService.findCqForEventByOrder(eventId);
				HttpHeaders headers = new HttpHeaders();
				headers.add("success", messageSource.getMessage("rft.cq.order", new Object[] {}, Global.LOCALE));
				return new ResponseEntity<List<RfpCq>>(cqList, headers, HttpStatus.OK);
			} else {
				List<RfpCq> cqList = rfpCqService.findCqForEventByOrder(eventId);
				HttpHeaders headers = new HttpHeaders();
				headers.add("error", messageSource.getMessage("rft.cq.order.empty", new Object[] {}, Global.LOCALE));
				return new ResponseEntity<List<RfpCq>>(cqList, headers, HttpStatus.BAD_REQUEST);
			}
		} catch (Exception e) {
			LOG.error("Error while Ordering CQ : " + e.getMessage(), e);
			HttpHeaders headers = new HttpHeaders();
			headers.add("error", messageSource.getMessage("rft.bq.order.error", new Object[] { e.getMessage() }, Global.LOCALE));
			return new ResponseEntity<List<RfpCq>>(null, headers, HttpStatus.BAD_REQUEST);
		}
	}

}
