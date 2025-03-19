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
import com.privasia.procurehere.core.entity.RfqCq;
import com.privasia.procurehere.core.entity.RfqCqItem;
import com.privasia.procurehere.core.entity.RfqCqOption;
import com.privasia.procurehere.core.entity.RfqEvent;
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
import com.privasia.procurehere.service.RfqCqService;
import com.privasia.procurehere.service.RfqDocumentService;
import com.privasia.procurehere.service.RfqEventService;
import com.privasia.procurehere.service.UomService;

@Controller
@RequestMapping("/buyer/RFQ")
public class RfqCqController extends EventCqBase {

	private static final Logger LOG = LogManager.getLogger(Global.ADMIN_LOG);

	@Autowired
	UomService uomService;

	@Autowired
	RfqCqService cqService;

	@Autowired
	RfqEventService eventService;

	@Autowired
	RfqDocumentService rfqDocumentService;

	public RfqCqController() {
		super(RfxTypes.RFQ);
	}

	@RequestMapping(path = "/eventCqList/{eventId}", method = RequestMethod.GET)
	public String eventCqList(@PathVariable String eventId, Model model) {
		if (StringUtils.checkString(eventId).length() == 0) {
			return "redirect:/400_error";
		}
		// List<RfqCq> cqList = cqService.findCqForEvent(eventId);
		List<RfqCq> cqList = cqService.findCqForEventByOrder(eventId);

		RfqCq rfqCq = new RfqCq();
		RfqEvent rfqEvent = new RfqEvent();
		rfqEvent.setId(eventId);
		rfqCq.setRfxEvent(rfqEvent);
		model.addAttribute("cqList", cqList);
		model.addAttribute("rftCq", rfqCq);
		model.addAttribute("eventId", eventId);
		model.addAttribute("eventPermissions", eventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), eventId));
		model.addAttribute("event", cqService.findEventForCqByEventId(eventId));
		return "eventCqList";
	}

	@RequestMapping(path = "/createRftCq", method = RequestMethod.POST)
	public String createRftCqPost(@ModelAttribute RfqCq rfqCq, Model model, BindingResult result, RedirectAttributes redir) {
		if (result.hasErrors()) {
			LOG.error("Page submitted With Errors ............................. ");
			for (ObjectError error : result.getAllErrors()) {
				LOG.info("ERROR : " + error.getObjectName() + " : " + error.getDefaultMessage());
				redir.addFlashAttribute("errors", error.getDefaultMessage());
			}
			model.addAttribute("rftCq", rfqCq);
		} else {
			LOG.info("Submit with no errors..............................................." + rfqCq.toLogString());
			RfqCq cq = null;
			try {
				if (cqService.isExists(rfqCq)) {
					LOG.info("Duplicate....");
					throw new ApplicationException("Duplicate CQ. CQ by that name already exists.");
				}

				RfqEvent rfqEvent = cqService.findEventForCqByEventId(rfqCq.getRfxEvent().getId());
				rfqCq.setRfxEvent(rfqEvent);
				cq = cqService.stroreCq(rfqCq);
				List<RfqCq> cqList = cqService.findCqForEventByOrder(rfqCq.getRfxEvent().getId());
				model.addAttribute("cqList", cqList);
				model.addAttribute("eventId", rfqEvent.getId());
				model.addAttribute("event", rfqEvent);
				if (StringUtils.checkString(rfqCq.getId()).length() == 0) {
					redir.addFlashAttribute("success", messageSource.getMessage("rft.cq.save", new Object[] {}, Global.LOCALE));
					return "redirect:eventCqList/" + rfqCq.getRfxEvent().getId();
				} else {
					redir.addFlashAttribute("success", messageSource.getMessage("rft.cq.update", new Object[] {}, Global.LOCALE));
					return "redirect:eventCqItemList/" + rfqCq.getId();
				}
			} catch (Exception e) {
				LOG.error("Error while storing CQ," + e.getMessage(), e);
				redir.addFlashAttribute("error", messageSource.getMessage("rft.cq.error", new Object[] { rfqCq.getName(), e.getMessage() }, Global.LOCALE));
				model.addAttribute("rftCq", rfqCq);
				if (StringUtils.checkString(rfqCq.getId()).length() == 0) {
					return "redirect:eventCqList/" + rfqCq.getRfxEvent().getId();
				} else {
					return "redirect:eventCqItemList/" + rfqCq.getId();
				}
			}
		}
		return "redirect:eventCqList/" + rfqCq.getRfxEvent().getId();
	}

	@RequestMapping(value = "/deleteCq", method = RequestMethod.POST)
	public String deleteCq(@ModelAttribute RfqCq rfqCq, Model model, RedirectAttributes redir) throws JsonProcessingException {
		RfqCq cq = null;
		try {
			cq = cqService.getCqById(rfqCq.getId());
			cqService.isAllowToDeleteCq(rfqCq.getId());
			cqService.deleteCq(cq);
			// List<RfqCq> cqList = cqService.findCqForEvent(cq.getRfxEvent().getId());
			// RfqEvent event = new RfqEvent();
			// event.setId(cq.getRfxEvent().getId());
			// rfqCq.setRfxEvent(event);
			// model.addAttribute("cqList", cqList);
			// model.addAttribute("rftCq", rfqCq);
			// model.addAttribute("event", cq.getRfxEvent());
			// model.addAttribute("eventId", cq.getRfxEvent().getId());

			redir.addFlashAttribute("success", messageSource.getMessage("rft.cq.success.delete", new Object[] { cq.getName() }, Global.LOCALE));
		} catch (NotAllowedException e) {
			LOG.error("Error while deleting cqItems : " + e.getMessage(), e);
			model.addAttribute("error", e.getMessage());
			List<RfqCq> cqList = cqService.findCqForEventByOrder(cq.getRfxEvent().getId());
			RfqEvent event = cqService.findEventForCqByEventId(cq.getRfxEvent().getId());
			rfqCq.setRfxEvent(event);
			model.addAttribute("cqList", cqList);
			model.addAttribute("rftCq", rfqCq);
			model.addAttribute("event", event);
			model.addAttribute("eventId", cq.getRfxEvent().getId());
			return "eventCqList";
		} catch (Exception e) {
			LOG.error("Error while deleting Cq  [ " + (cq != null ? cq.getName() : "") + " ]" + e.getMessage(), e);
			model.addAttribute("error", messageSource.getMessage("rft.cq.error.delete", new Object[] { (cq != null ? cq.getName() : "") }, Global.LOCALE));
			List<RfqCq> cqList = cqService.findCqForEventByOrder(cq.getRfxEvent().getId());

			RfqEvent event = cqService.findEventForCqByEventId(cq.getRfxEvent().getId());
			rfqCq.setRfxEvent(event);
			model.addAttribute("cqList", cqList);
			model.addAttribute("rftCq", rfqCq);
			model.addAttribute("event", event);
			model.addAttribute("eventId", cq.getRfxEvent().getId());
			return "eventCqList";
		}
		return "redirect:eventCqList/" + cq.getRfxEvent().getId();
	}

	@RequestMapping(path = "/showCqItems", method = RequestMethod.POST)
	public String showCqItemList(@RequestParam(name = "cqId", required = true) String cqId, @RequestParam(name = "eventId", required = true) String eventId, RedirectAttributes reAttributes) {
		reAttributes.addFlashAttribute("eventId", eventId);
		return "redirect:eventCqItemList/" + cqId;
	}

	@RequestMapping(path = "/eventCqItemList/{cqId}", method = RequestMethod.GET)
	public String eventCqItemList(@PathVariable String cqId, Model model) {
		if (StringUtils.checkString(cqId).length() == 0) {
			return "redirect:/400_error";
		}
		RfqCq rfqCq = cqService.getCqById(cqId);
		LOG.info("CQ : " + rfqCq.getId());
		List<RfqCqItem> cqItemList = cqService.findCqbyCqId(cqId);

		String eventId = (String) model.asMap().get("eventId");
		if (eventId == null || eventId.length() == 0) {
			eventId = rfqCq.getRfxEvent().getId();
		}
		if (eventId != null && eventId.length() > 0) {
			List<EventDocument> eventDocumentList = rfqDocumentService.findAllRfqEventDocsNameByEventId(eventId);
			model.addAttribute("eventDocumentList", eventDocumentList);
		}

		LOG.info("CQ ITEM LIST : " + cqItemList.size());
		for (RfqCqItem item : cqItemList) {
			LOG.info("item Level : " + item.getLevel() + " Order : " + item.getOrder());
		}
		model.addAttribute("rftCq", rfqCq);
		model.addAttribute("cqItemList", cqItemList);
		model.addAttribute("event", cqService.findEventForCqByEventId(rfqCq.getRfxEvent().getId()));
		model.addAttribute("eventPermissions", eventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), rfqCq.getRfxEvent().getId()));
		model.addAttribute("cqTypes", CqType.values());
		RfqCqItem cqItem = new RfqCqItem();
		cqItem.setCq(rfqCq);
		model.addAttribute("cqItem", cqItem);
		return "eventCqItemList";
	}

	@RequestMapping(path = "/createRftCqItem", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<List<RfqCqItem>> createRfqCqItem(@RequestBody CqItemPojo cqItem) throws JsonProcessingException {
		HttpHeaders headers = new HttpHeaders();
		try {
			LOG.info("CQ ITEM : " + cqItem.toLogString());
			if (!doValidate(cqItem, headers)) {
				return new ResponseEntity<List<RfqCqItem>>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
			}
			if (StringUtils.checkString(cqItem.getItemName()).length() > 0) {
				RfqCqItem item = new RfqCqItem();
				item.setItemName(cqItem.getItemName());
				item.setItemDescription(cqItem.getItemDescription());

				if (cqService.isExists(item, cqItem.getCq(), cqItem.getParent())) {
					LOG.info("Duplicate....");
					throw new ApplicationException("Duplicate CQ Item. CQ Item by that name already exists.");
				}
				item.setRfxEvent(cqService.getEventById(cqItem.getRftEvent()));
				item.setCq(cqService.getCqById(cqItem.getCq()));
				item.setIsSupplierAttachRequired(cqItem.getIsSupplierAttachRequired());
				if (StringUtils.checkString(cqItem.getCqType()).length() > 0) {
					item.setCqType(CqType.valueOf(cqItem.getCqType()));
				}
				item.setAttachment(cqItem.isAttachment());
				item.setOptional(cqItem.isOptional());
				if (CollectionUtil.isNotEmpty(cqItem.getOptions())) {
					List<RfqCqOption> optionItems = new ArrayList<RfqCqOption>();
					int optionOrder = 0;
					int totalScore = 0;
					for (String option : cqItem.getOptions()) {
						if (StringUtils.checkString(option).length() == 0)
							continue;
						RfqCqOption options = new RfqCqOption();
						LOG.info("option : " + option);
						options.setCqItem(item);
						options.setValue(option);
						options.setOrder(++optionOrder);
						if (CollectionUtil.isNotEmpty(cqItem.getOptionScore()))
							options.setScoring(Integer.parseInt(cqItem.getOptionScore().get(optionOrder - 1)));
						totalScore += options.getScoring() != null ? options.getScoring().intValue() : 0;
						optionItems.add(options);
					}
					item.setTotalScore(totalScore);
					item.setCqOptions(optionItems);
				}
				if (StringUtils.checkString(cqItem.getParent()).length() > 0) {
					item.setParent(cqService.getCqItembyCqItemId(StringUtils.checkString(cqItem.getParent())));
				}

				cqService.saveCqItem(item);

				List<RfqCqItem> bqList = cqService.findCqbyCqId(cqItem.getCq());
				headers.add("success", messageSource.getMessage("buyer.rftcq.save", new Object[] {}, Global.LOCALE));
				return new ResponseEntity<List<RfqCqItem>>(bqList, headers, HttpStatus.OK);
			} else {
				List<RfqCqItem> bqList = cqService.findCqbyCqId(cqItem.getCq());
				headers.add("info", messageSource.getMessage("rft.cq.name.empty", new Object[] {}, Global.LOCALE));
				return new ResponseEntity<List<RfqCqItem>>(bqList, headers, HttpStatus.OK);
			}
		} catch (Exception e) {
			LOG.error("Error while adding CQ Items to Event : " + e.getMessage(), e);
			headers.add("error", messageSource.getMessage("buyer.rftcq.error", new Object[] { e.getMessage() }, Global.LOCALE));
			return new ResponseEntity<List<RfqCqItem>>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(path = "/getCqData", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<List<CqItemPojo>> getCqData(@RequestParam("itemId") String itemId) throws JsonProcessingException {
		try {

			if (StringUtils.checkString(itemId).length() > 0) {
				RfqCqItem item = cqService.getCqItembyCqItemId(StringUtils.checkString(itemId));
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
	public @ResponseBody ResponseEntity<List<RfqCqItem>> updateCqItem(@RequestBody CqItemPojo cqItem) throws JsonProcessingException {
		HttpHeaders headers = new HttpHeaders();
		List<RfqCqItem> cqList = null;
		try {
			LOG.info(" :: UPDATE CQ ::" + cqItem.getId());
			LOG.info(" :: UPDATE CQ ::" + cqItem.toLogString());
			if (!doValidate(cqItem, headers)) {
				return new ResponseEntity<List<RfqCqItem>>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
			}
			if (StringUtils.checkString(cqItem.getItemName()).length() > 0) {
				RfqCqItem item = new RfqCqItem();
				item.setId(cqItem.getId());
				item.setItemName(cqItem.getItemName());
				item.setItemDescription(cqItem.getItemDescription());
				item.setIsSupplierAttachRequired(cqItem.getIsSupplierAttachRequired());
				if (cqService.isExists(item, cqItem.getCq(), cqItem.getParent())) {
					LOG.info("Duplicate....");
					throw new ApplicationException("Duplicate CQ Item. CQ Item by that name already exists.");
				}
				cqService.updateCqItem(cqItem);
				cqList = cqService.findCqbyCqId(cqItem.getCq());
				headers.add("success", messageSource.getMessage("buyer.rftcq.update", new Object[] {}, Global.LOCALE));
				return new ResponseEntity<List<RfqCqItem>>(cqList, headers, HttpStatus.OK);
			} else {
				cqList = cqService.findCqbyCqId(cqItem.getCq());
				headers.add("info", messageSource.getMessage("rft.cq.name.empty", new Object[] {}, Global.LOCALE));
				return new ResponseEntity<List<RfqCqItem>>(cqList, headers, HttpStatus.OK);
			}

		} catch (Exception e) {
			LOG.error("Error while updating CQ Items to Buyer : " + e.getMessage(), e);
			headers.add("error", messageSource.getMessage("buyer.rftcq.error", new Object[] { e.getMessage() }, Global.LOCALE));
			cqList = cqService.findCqbyCqId(cqItem.getCq());
			return new ResponseEntity<List<RfqCqItem>>(cqList, headers, HttpStatus.BAD_REQUEST);
		}
	}

	@RequestMapping(value = "/deleteCqItem/{cqId}", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<List<RfqCqItem>> deleteCqItem(@RequestBody String[] items, @PathVariable("cqId") String cqId) throws JsonProcessingException {
		LOG.info("DELETE CQ ITEM REQUESTED : " + items + " cqId : " + cqId);
		List<RfqCqItem> cqItems = null;
		HttpHeaders headers = new HttpHeaders();
		if (items != null && items.length > 0) {
			try {
				cqService.deleteCqItems(items, cqId);
				cqItems = cqService.findCqbyCqId(cqId);
				headers.add("errors", messageSource.getMessage("rfx.cq.success.delete", new Object[] {}, Global.LOCALE));
			} catch (Exception e) {
				LOG.error("Error while deleting cqItems : " + e.getMessage(), e);
				headers.add("errors", messageSource.getMessage("rft.cq.info.delete", new Object[] {}, Global.LOCALE));
				return new ResponseEntity<List<RfqCqItem>>(null, headers, HttpStatus.BAD_REQUEST);
			}
		} else {
			LOG.error("Please select at least one Cq Item");
			headers.add("errors", messageSource.getMessage("rft.cq.error.delete.required", new Object[] {}, Global.LOCALE));
			return new ResponseEntity<List<RfqCqItem>>(null, headers, HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<List<RfqCqItem>>(cqItems, headers, HttpStatus.OK);
	}

	@RequestMapping(path = "/eventCqOrder", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<List<RfqCqItem>> eventCqOrder(@RequestBody CqItemPojo rfqCqItemPojo) throws JsonProcessingException {
		LOG.info("rftCqItemPojo : " + rfqCqItemPojo.toLogString());
		HttpHeaders headers = new HttpHeaders();
		List<RfqCqItem> cqList = null;
		try {
			if (StringUtils.checkString(rfqCqItemPojo.getId()).length() > 0) {
				LOG.info("Updating order..........................");
				RfqCqItem cqItem = cqService.getCqItembyCqItemId(rfqCqItemPojo.getId());
				if (cqItem.getOrder() > 0 && StringUtils.checkString(rfqCqItemPojo.getParent()).length() == 0) {
					LOG.info("child cannot be a parent");
					headers.add("error", messageSource.getMessage("rfx.child.reOrder.error", new Object[] {}, Global.LOCALE));
					return new ResponseEntity<List<RfqCqItem>>(cqService.findCqbyCqId(cqService.getCqItembyCqItemId(rfqCqItemPojo.getId()).getCq().getId()), headers, HttpStatus.BAD_REQUEST);
				}
				if (cqItem.getOrder() == 0 && StringUtils.checkString(rfqCqItemPojo.getParent()).length() > 0) {
					LOG.info("parent cannot be a child");
					headers.add("error", messageSource.getMessage("rfx.parent.reOrder.error", new Object[] {}, Global.LOCALE));
					return new ResponseEntity<List<RfqCqItem>>(cqService.findCqbyCqId(cqService.getCqItembyCqItemId(rfqCqItemPojo.getId()).getCq().getId()), headers, HttpStatus.BAD_REQUEST);
				}
				RfqCqItem item = new RfqCqItem();
				item.setItemName(rfqCqItemPojo.getItemName());
				if (cqItem.getParent() != null && !cqItem.getParent().getId().equals(rfqCqItemPojo.getParent()) && cqService.isExists(item, rfqCqItemPojo.getCq(), rfqCqItemPojo.getParent())) {
					LOG.info("Duplicate....");
					throw new ApplicationException("Duplicate CQ Item. CQ Item by that name already exists.");
				}
				cqService.reorderCqItems(rfqCqItemPojo);
				cqList = cqService.findCqbyCqId(cqService.getCqItembyCqItemId(rfqCqItemPojo.getId()).getCq().getId());
				headers.add("success", messageSource.getMessage("buyer.rftbq.success", new Object[] {}, Global.LOCALE));
				return new ResponseEntity<List<RfqCqItem>>(cqList, headers, HttpStatus.OK);
			}
		} catch (NotAllowedException | ApplicationException nae) {
			LOG.error("Error while moving CQ Items to Event : " + nae.getMessage(), nae);
			headers.add("error", nae.getMessage());
			return new ResponseEntity<List<RfqCqItem>>(cqService.findCqbyCqId(cqService.getCqItembyCqItemId(rfqCqItemPojo.getId()).getCq().getId()), headers, HttpStatus.BAD_REQUEST);

		} catch (Exception e) {
			LOG.error("Error while moving CQ Items to Event : " + e.getMessage(), e);
			// headers.add("error", "Error while adding Notes to Buyer " + e.getMessage());
			headers.add("error", messageSource.getMessage("buyer.rftcq.error", new Object[] { e.getMessage() }, Global.LOCALE));
			return new ResponseEntity<List<RfqCqItem>>(null, headers, HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<List<RfqCqItem>>(cqList, headers, HttpStatus.OK);
	}

	@RequestMapping(value = "/cqPrevious", method = RequestMethod.POST)
	public String cqPrevious(Model model, @RequestParam(name = "eventId", required = true) String eventId) throws Exception {
		try {
			RfqEvent rfqEvent = cqService.getEventById(eventId);
			if (rfqEvent != null) {
				if (Boolean.TRUE == rfqEvent.getMeetingReq()) {
					return "redirect:meetingList/" + rfqEvent.getId();
				} else if (rfqEvent.getEventVisibility() != EventVisibilityType.PUBLIC) {
					return "redirect:addSupplier/" + rfqEvent.getId();
				} else if (Boolean.TRUE == rfqEvent.getDocumentReq()) {
					return "redirect:createEventDocuments/" + rfqEvent.getId();
				} else {
					return "redirect:eventDescription/" + rfqEvent.getId();
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
			if (eventId != null) {
				return "redirect:eventCqList/" + eventId;
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
			RfqEvent rfqEvent = cqService.getEventById(eventId);
			if (rfqEvent != null) {
				rfqEvent.setCqCompleted(Boolean.TRUE);
				eventService.updateEvent(rfqEvent);
				if (Boolean.TRUE == rfqEvent.getBillOfQuantity()) {
					return "redirect:createBQList/" + eventId;
				} else if(Boolean.TRUE == rfqEvent.getScheduleOfRate()) {
					return "redirect:createSorList/" + eventId;
				} else {
					return "redirect:envelopList/" + eventId;
				}
			} else {
				return "/";
			}
		} catch (Exception e) {
			LOG.error("Event not found redirecting to login : " + e.getMessage(), e);
			throw new Exception("Event not found redirecting to login : " + e.getMessage());
		}
	}

	@RequestMapping(value = "/cqSaveDraft", method = RequestMethod.POST)
	public String cqSaveDraft(RedirectAttributes redir, @RequestParam("eventId") String eventId) {
		RfqEvent rfqEvent = cqService.getEventById(eventId);
		redir.addFlashAttribute("success", messageSource.getMessage("rftEvent.success.savedraft", new Object[] { (rfqEvent.getEventName() != null ? rfqEvent.getEventName() : rfqEvent.getEventId()) }, Global.LOCALE));
		return "redirect:eventCqList/" + rfqEvent.getId();

	}

	@RequestMapping(path = "/cqItemTemplate/{cqId}", method = RequestMethod.GET)
	public void downloader(@PathVariable String cqId, HttpServletRequest request, HttpServletResponse response) throws IOException {
		try {
			XSSFWorkbook workbook = new XSSFWorkbook();
			String downloadFolder = context.getRealPath("/WEB-INF/");
			String fileName = "CqItemTemplate.xlsx";
			Path file = Paths.get(downloadFolder, fileName);
			LOG.info("File Path ::" + file);

			super.eventDownloader(workbook, cqId, RfxTypes.RFQ);

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
	public ResponseEntity<List<RfqCqItem>> handleFormUpload(@RequestParam("file") MultipartFile file, @RequestParam("cqId") String cqId, @RequestParam("eventId") String eventId) throws IOException {
		LOG.info("UPLOADING STARTED ...... CQ Id :: " + cqId + "Event Id :: " + eventId);
		List<RfqCqItem> cqList = null;
		String message = null;
		HttpHeaders headers = new HttpHeaders();
		try {
			if (!file.isEmpty()) {
				if (!file.getContentType().equals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet") && !file.getContentType().equals("application/wps-office.xlsx") && !file.getContentType().equals("application/vnd.ms-excel"))
					throw new MultipartException("Only excel files accepted!");
				try {
					message = super.cqItemsUpload(file, cqId, eventId, RfxTypes.RFQ);

				} catch (Exception e) {
					e.printStackTrace();
					cqList = rfqCqService.findCqbyCqId(cqId);
					headers.add("error", messageSource.getMessage("common.upload.fail", new Object[] { e.getMessage() }, Global.LOCALE));
					return new ResponseEntity<List<RfqCqItem>>(cqList, headers, HttpStatus.BAD_REQUEST);
				}
			}
		} catch (MultipartException e) {
			e.printStackTrace();
			// LOG.info("Upload failed!" + e.getMessage());
			cqList = rfqCqService.findCqbyCqId(cqId);
			headers.add("error", messageSource.getMessage("common.upload.notsupported.fail", new Object[] { e.getMessage() }, Global.LOCALE));
			return new ResponseEntity<List<RfqCqItem>>(cqList, headers, HttpStatus.BAD_REQUEST);
		}
		cqList = rfqCqService.findCqbyCqId(cqId);
		if (message == null) {
			headers.add("success", messageSource.getMessage("common.upload.success", new Object[] { "RFQ CQ Items" }, Global.LOCALE));
		} else {
			headers.add("warn", messageSource.getMessage("common.upload.success", new Object[] { "RFQ CQ Items" }, Global.LOCALE) + ", " + message);
		}
		return new ResponseEntity<List<RfqCqItem>>(cqList, headers, HttpStatus.OK);
	}

	@RequestMapping(value = "/updateEventCqOrder/{eventId}", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<List<RfqCq>> updateEventCqOrder(@RequestBody String[] cqIds, @PathVariable("eventId") String eventId) {
		try {
			LOG.info("RFQ eventId : " + eventId);
			if (cqIds != null && cqIds.length > 0) {
				Integer count = 1;
				for (String cqId : cqIds) {
					RfqCq cq = rfqCqService.getCqById(cqId);
					cq.setCqOrder(count);
					rfqCqService.updateCq(cq);
					count++;
				}
				List<RfqCq> cqList = rfqCqService.findCqForEventByOrder(eventId);
				HttpHeaders headers = new HttpHeaders();
				headers.add("success", messageSource.getMessage("rft.cq.order", new Object[] {}, Global.LOCALE));
				return new ResponseEntity<List<RfqCq>>(cqList, headers, HttpStatus.OK);
			} else {
				List<RfqCq> cqList = rfqCqService.findCqForEventByOrder(eventId);
				HttpHeaders headers = new HttpHeaders();
				headers.add("error", messageSource.getMessage("rft.cq.order.empty", new Object[] {}, Global.LOCALE));
				return new ResponseEntity<List<RfqCq>>(cqList, headers, HttpStatus.BAD_REQUEST);
			}
		} catch (Exception e) {
			LOG.error("Error while Ordering CQ : " + e.getMessage(), e);
			HttpHeaders headers = new HttpHeaders();
			headers.add("error", messageSource.getMessage("rft.bq.order.error", new Object[] { e.getMessage() }, Global.LOCALE));
			return new ResponseEntity<List<RfqCq>>(null, headers, HttpStatus.BAD_REQUEST);
		}
	}
}
