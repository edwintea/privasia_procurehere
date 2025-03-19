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
import com.privasia.procurehere.core.entity.RfaCq;
import com.privasia.procurehere.core.entity.RfaCqItem;
import com.privasia.procurehere.core.entity.RfaCqOption;
import com.privasia.procurehere.core.entity.RfaEvent;
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
import com.privasia.procurehere.service.RfaCqService;
import com.privasia.procurehere.service.RfaDocumentService;
import com.privasia.procurehere.service.RfaEventService;
import com.privasia.procurehere.service.UomService;

@Controller
@RequestMapping("/buyer/RFA")
public class RfaCqController extends EventCqBase {

	private static final Logger LOG = LogManager.getLogger(Global.ADMIN_LOG);

	@Autowired
	UomService uomService;

	@Autowired
	RfaCqService rfaCqService;

	@Autowired
	RfaEventService rfaEventService;

	@Autowired
	RfaDocumentService rfaDocumentService;

	public RfaCqController() {
		super(RfxTypes.RFA);
	}

	@RequestMapping(path = "/eventCqList/{eventId}", method = RequestMethod.GET)
	public String eventCqList(@PathVariable String eventId, Model model) {
		if (StringUtils.checkString(eventId).length() == 0) {
			return "redirect:/400_error";
		}
		// List<RfaCq> cqList = rfaCqService.findRfaCqForEvent(eventId);
		List<RfaCq> cqList = rfaCqService.findRfaCqForEventByOrder(eventId);

		RfaCq rfaCq = new RfaCq();
		RfaEvent rfaEvent = new RfaEvent();
		rfaEvent.setId(eventId);
		rfaCq.setRfxEvent(rfaEvent);
		model.addAttribute("cqList", cqList);
		model.addAttribute("eventPermissions", rfaEventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), eventId));
		model.addAttribute("rftCq", rfaCq);
		model.addAttribute("eventId", eventId);
		model.addAttribute("event", rfaCqService.findEventForCqByEventId(eventId));
		return "eventCqList";
	}

	@RequestMapping(path = "/createRftCq", method = RequestMethod.POST)
	public String createRfaCqPost(@ModelAttribute RfaCq rfaCq, Model model, BindingResult result, RedirectAttributes redir) {
		HttpHeaders headers = new HttpHeaders();
		if (result.hasErrors()) {
			LOG.error("Page submitted With Errors ............................. ");
			for (ObjectError error : result.getAllErrors()) {
				LOG.info("ERROR : " + error.getObjectName() + " : " + error.getDefaultMessage());
				headers.add("errors", error.getDefaultMessage());
			}
			model.addAttribute("rftCq", rfaCq);
		} else {
			LOG.info("Submit with no errors..............................................." + rfaCq.getRfxEvent().getId());
			RfaCq cq = null;
			try {
				if (rfaCqService.isExists(rfaCq)) {
					LOG.info("Duplicate....");
					throw new ApplicationException("Duplicate CQ. CQ by that name already exists.");
				}

				RfaEvent rfaEvent = rfaCqService.findEventForCqByEventId(rfaCq.getRfxEvent().getId());
				rfaCq.setRfxEvent(rfaEvent);
				cq = rfaCqService.stroreRfaCq(rfaCq);
				List<RfaCq> cqList = rfaCqService.findRfaCqForEventByOrder(rfaCq.getRfxEvent().getId());
				model.addAttribute("cqList", cqList);
				model.addAttribute("eventPermissions", rfaEventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), rfaEvent.getId()));
				model.addAttribute("eventId", rfaEvent.getId());
				model.addAttribute("event", rfaEvent);

				if (StringUtils.checkString(rfaCq.getId()).length() == 0) {
					redir.addFlashAttribute("success", messageSource.getMessage("rft.cq.save", new Object[] {}, Global.LOCALE));
					return "redirect:eventCqList/" + rfaCq.getRfxEvent().getId();
				} else {
					redir.addFlashAttribute("success", messageSource.getMessage("rft.cq.update", new Object[] {}, Global.LOCALE));
					return "redirect:eventCqItemList/" + rfaCq.getId();
				}

			} catch (Exception e) {
				LOG.error("Error while storing CQ," + e.getMessage(), e);
				redir.addFlashAttribute("error", messageSource.getMessage("rft.cq.error", new Object[] { rfaCq.getName(), e.getMessage() }, Global.LOCALE));
				if (StringUtils.checkString(rfaCq.getId()).length() == 0) {
					return "redirect:eventCqList/" + rfaCq.getRfxEvent().getId();
				} else {
					return "redirect:eventCqItemList/" + rfaCq.getId();
				}
			}
		}
		return "redirect:eventCqList/" + rfaCq.getRfxEvent().getId();
	}

	@RequestMapping(value = "/deleteCq", method = RequestMethod.POST)
	public String deleteCq(@ModelAttribute RfaCq rfaCq, Model model, RedirectAttributes redir) throws JsonProcessingException {
		RfaCq cq = null;
		try {
			cq = rfaCqService.getRfaCqById(rfaCq.getId());
			rfaCqService.isAllowToDeleteCq(rfaCq.getId());
			rfaCqService.deleteCq(cq);

			// List<RfaCq> cqList =
			// rfaCqService.findRfaCqForEvent(cq.getRfxEvent().getId());

			// RfaEvent rfaEvent = new RfaEvent();
			// rfaEvent.setId(cq.getRfxEvent().getId());
			// rfaCq.setRfxEvent(rfaEvent);
			// model.addAttribute("cqList", cqList);
			// model.addAttribute("rftCq", rfaCq);
			// model.addAttribute("event", cq.getRfxEvent());
			// model.addAttribute("eventPermissions",
			// rfaEventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(),
			// cq.getRfxEvent().getId()));
			// model.addAttribute("eventId", cq.getRfxEvent().getId());

			redir.addFlashAttribute("success", messageSource.getMessage("rft.cq.success.delete", new Object[] { cq.getName() }, Global.LOCALE));
		} catch (NotAllowedException e) {
			LOG.error("Error while deleting cqItems : " + e.getMessage(), e);
			model.addAttribute("error", e.getMessage());
			List<RfaCq> cqList = rfaCqService.findRfaCqForEventByOrder(cq.getRfxEvent().getId());
			RfaEvent rfaEvent = rfaCqService.findEventForCqByEventId(cq.getRfxEvent().getId());
			rfaCq.setRfxEvent(rfaEvent);
			model.addAttribute("cqList", cqList);
			model.addAttribute("rftCq", rfaCq);
			model.addAttribute("event", rfaEvent);
			model.addAttribute("eventId", cq.getRfxEvent().getId());
			return "eventCqList";
		} catch (Exception e) {
			LOG.error("Error while deleting Cq  [ " + (cq != null ? cq.getName() : "") + " ]" + e.getMessage(), e);
			model.addAttribute("error", messageSource.getMessage("rft.cq.error.delete", new Object[] { (cq != null ? cq.getName() : "") }, Global.LOCALE));
			List<RfaCq> cqList = rfaCqService.findRfaCqForEventByOrder(cq.getRfxEvent().getId());

			RfaEvent rfaEvent = rfaCqService.findEventForCqByEventId(cq.getRfxEvent().getId());
			rfaCq.setRfxEvent(rfaEvent);
			model.addAttribute("cqList", cqList);
			model.addAttribute("rftCq", rfaCq);
			model.addAttribute("event", rfaEvent);
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
		// RftCq rftCq = (RftCq) model.asMap().get("rftCq");
		RfaCq rfaCq = rfaCqService.getRfaCqById(cqId);
		LOG.info("CQ : " + rfaCq.getId());
		List<RfaCqItem> cqItemList = rfaCqService.findRfaCqbyCqId(cqId);

		String eventId = (String) model.asMap().get("eventId");
		if (eventId == null || eventId.length() == 0) {
			eventId = rfaCq.getRfxEvent().getId();
		}
		if (eventId != null && eventId.length() > 0) {
			List<EventDocument> eventDocumentList = rfaDocumentService.findAllRfaEventDocsNameByEventId(eventId);
			model.addAttribute("eventDocumentList", eventDocumentList);
		}

		LOG.info("CQ ITEM LIST : " + cqItemList.size());
		for (RfaCqItem item : cqItemList) {
			LOG.info("item Level : " + item.getLevel() + " Order : " + item.getOrder());
		}
		model.addAttribute("rftCq", rfaCq);
		model.addAttribute("cqItemList", cqItemList);
		model.addAttribute("event", rfaCqService.findEventForCqByEventId(rfaCq.getRfxEvent().getId()));
		model.addAttribute("eventPermissions", rfaEventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), rfaCq.getRfxEvent().getId()));
		model.addAttribute("cqTypes", CqType.values());
		RfaCqItem cqItem = new RfaCqItem();
		cqItem.setCq(rfaCq);
		model.addAttribute("cqItem", cqItem);
		return "eventCqItemList";
	}

	@RequestMapping(path = "/createRftCqItem", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<List<RfaCqItem>> createRfaCqItem(@RequestBody CqItemPojo rfaCqItem) throws JsonProcessingException {
		HttpHeaders headers = new HttpHeaders();
		try {
			if (!doValidate(rfaCqItem, headers)) {
				return new ResponseEntity<List<RfaCqItem>>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
			}
			LOG.info("CQ ITEM : " + rfaCqItem.toLogString());
			if (StringUtils.checkString(rfaCqItem.getItemName()).length() > 0) {
				RfaCqItem item = new RfaCqItem();
				item.setItemName(rfaCqItem.getItemName());
				item.setIsSupplierAttachRequired(rfaCqItem.getIsSupplierAttachRequired());
				item.setItemDescription(rfaCqItem.getItemDescription());

				if (rfaCqService.isExists(item, rfaCqItem.getCq(), rfaCqItem.getParent())) {
					LOG.info("Duplicate....");
					throw new ApplicationException("Duplicate CQ Item. CQ Item by that name already exists.");
				}

				item.setRfxEvent(rfaEventService.getRfaEventByeventId(rfaCqItem.getRftEvent()));
				item.setCq(rfaCqService.getRfaCqById(rfaCqItem.getCq()));
				if (StringUtils.checkString(rfaCqItem.getCqType()).length() > 0) {
					item.setCqType(CqType.valueOf(rfaCqItem.getCqType()));
				}
				item.setAttachment(rfaCqItem.isAttachment());

				item.setOptional(rfaCqItem.isOptional());
				if (CollectionUtil.isNotEmpty(rfaCqItem.getOptions())) {
					List<RfaCqOption> optionItems = new ArrayList<RfaCqOption>();
					int optionOrder = 0;
					int totalScore = 0;
					for (String option : rfaCqItem.getOptions()) {
						if (StringUtils.checkString(option).length() == 0)
							continue;
						RfaCqOption options = new RfaCqOption();
						options.setRfaCqItem(item);
						options.setValue(option);
						options.setOrder(++optionOrder);
						if (CollectionUtil.isNotEmpty(rfaCqItem.getOptionScore()))
							options.setScoring(Integer.parseInt(rfaCqItem.getOptionScore().get(optionOrder - 1)));
						totalScore += options.getScoring() != null ? options.getScoring().intValue() : 0;
						optionItems.add(options);
					}
					item.setTotalScore(totalScore);
					item.setCqOptions(optionItems);
				}
				if (StringUtils.checkString(rfaCqItem.getParent()).length() > 0) {
					item.setParent(rfaCqService.getCqItembyCqItemId(StringUtils.checkString(rfaCqItem.getParent())));
				}

				rfaCqService.saveRfaCqItem(item);

				List<RfaCqItem> bqList = rfaCqService.findRfaCqbyCqId(rfaCqItem.getCq());
				headers.add("success", messageSource.getMessage("buyer.rftcq.save", new Object[] {}, Global.LOCALE));
				return new ResponseEntity<List<RfaCqItem>>(bqList, headers, HttpStatus.OK);
			} else {
				List<RfaCqItem> bqList = rfaCqService.findRfaCqbyCqId(rfaCqItem.getCq());
				headers.add("info", messageSource.getMessage("buyer.rftcq.duplicate", new Object[] {}, Global.LOCALE));
				return new ResponseEntity<List<RfaCqItem>>(bqList, headers, HttpStatus.OK);
			}
		} catch (Exception e) {
			LOG.error("Error while adding CQ Items to Event : " + e.getMessage(), e);
			headers.add("error", messageSource.getMessage("buyer.rftcq.error", new Object[] { e.getMessage() }, Global.LOCALE));
			return new ResponseEntity<List<RfaCqItem>>(null, headers, HttpStatus.BAD_REQUEST);
		}
	}

	@RequestMapping(path = "/getCqData", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<List<CqItemPojo>> getCqData(@RequestParam("itemId") String itemId) throws JsonProcessingException {
		try {

			if (StringUtils.checkString(itemId).length() > 0) {
				RfaCqItem item = rfaCqService.getCqItembyCqItemId(StringUtils.checkString(itemId));
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
			return new ResponseEntity<List<CqItemPojo>>(null, headers, HttpStatus.BAD_REQUEST);
		}
	}

	@RequestMapping(value = "/updateCqItem", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<List<RfaCqItem>> updateCqItem(@RequestBody CqItemPojo itemPojo) throws JsonProcessingException {
		HttpHeaders headers = new HttpHeaders();
		List<RfaCqItem> cqList = null;
		try {
			LOG.info(" :: UPDATE CQ ::" + itemPojo.toLogString());
			if (!doValidate(itemPojo, headers)) {
				return new ResponseEntity<List<RfaCqItem>>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
			}
			if (StringUtils.checkString(itemPojo.getItemName()).length() > 0) {
				RfaCqItem item = new RfaCqItem();
				item.setId(itemPojo.getId());
				item.setItemName(itemPojo.getItemName());
				item.setItemDescription(itemPojo.getItemDescription());
				LOG.info("-----------------------" + itemPojo.getIsSupplierAttachRequired());
				item.setIsSupplierAttachRequired(itemPojo.getIsSupplierAttachRequired());
				if (rfaCqService.isExists(item, itemPojo.getCq(), itemPojo.getParent())) {
					LOG.info("Duplicate....");
					throw new ApplicationException("Duplicate CQ Item. CQ Item by that name already exists.");
				}
				rfaCqService.updateCqItem(itemPojo);
				cqList = rfaCqService.findRfaCqbyCqId(itemPojo.getCq());
				headers.add("success", messageSource.getMessage("buyer.rftcq.update", new Object[] {}, Global.LOCALE));
				return new ResponseEntity<List<RfaCqItem>>(cqList, headers, HttpStatus.OK);
			} else {
				cqList = rfaCqService.findRfaCqbyCqId(itemPojo.getCq());
				headers.add("info", messageSource.getMessage("rft.cq.name.empty", new Object[] {}, Global.LOCALE));
				return new ResponseEntity<List<RfaCqItem>>(cqList, headers, HttpStatus.OK);
			}

		} catch (Exception e) {
			LOG.error("Error while updating CQ Items to Buyer : " + e.getMessage(), e);
			headers.add("error", messageSource.getMessage("buyer.rftcq.error", new Object[] { e.getMessage() }, Global.LOCALE));
			cqList = rfaCqService.findRfaCqbyCqId(itemPojo.getCq());
			return new ResponseEntity<List<RfaCqItem>>(cqList, headers, HttpStatus.BAD_REQUEST);
		}
	}

	@RequestMapping(value = "/deleteCqItem/{cqId}", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<List<RfaCqItem>> deleteCqItem(@RequestBody String[] items, @PathVariable("cqId") String cqId) throws JsonProcessingException {
		LOG.info("DELETE CQ ITEM REQUESTED : " + items.toString());
		List<RfaCqItem> cqItems = null;
		HttpHeaders headers = new HttpHeaders();
		if (items != null && items.length > 0) {
			try {
				rfaCqService.deleteCqItems(items, cqId);
				cqItems = rfaCqService.findRfaCqbyCqId(cqId);
				headers.add("errors", messageSource.getMessage("rfx.cq.success.delete", new Object[] {}, Global.LOCALE));
			} catch (NotAllowedException e) {
				LOG.error("Error while deleting cqItems : " + e.getMessage(), e);
				headers.add("errors", messageSource.getMessage("rft.cq.info.delete", new Object[] {}, Global.LOCALE));
				return new ResponseEntity<List<RfaCqItem>>(null, headers, HttpStatus.BAD_REQUEST);
			}
		} else {
			LOG.error("Please select at least one Cq Item");
			headers.add("errors", messageSource.getMessage("rft.cq.error.delete.required", new Object[] {}, Global.LOCALE));
			return new ResponseEntity<List<RfaCqItem>>(null, headers, HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<List<RfaCqItem>>(cqItems, headers, HttpStatus.OK);
	}

	@RequestMapping(path = "/eventCqOrder", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<List<RfaCqItem>> eventCqOrder(@RequestBody CqItemPojo rfaCqItemPojo) throws JsonProcessingException {
		LOG.info("rfaCqItemPojo : " + rfaCqItemPojo.toLogString());
		HttpHeaders headers = new HttpHeaders();
		List<RfaCqItem> cqList = null;
		try {
			if (StringUtils.checkString(rfaCqItemPojo.getId()).length() > 0) {
				LOG.info("Updating order..........................");
				RfaCqItem cqItem = rfaCqService.getCqItembyCqItemId(rfaCqItemPojo.getId());
				if (cqItem.getOrder() > 0 && StringUtils.checkString(rfaCqItemPojo.getParent()).length() == 0) {
					LOG.info("child cannot be a parent");
					headers.add("error", messageSource.getMessage("rfx.child.reOrder.error", new Object[] {}, Global.LOCALE));
					return new ResponseEntity<List<RfaCqItem>>(rfaCqService.findRfaCqbyCqId(rfaCqService.getCqItembyCqItemId(rfaCqItemPojo.getId()).getCq().getId()), headers, HttpStatus.BAD_REQUEST);
				}
				if (cqItem.getOrder() == 0 && StringUtils.checkString(rfaCqItemPojo.getParent()).length() > 0) {
					LOG.info("parent cannot be a child");
					headers.add("error", messageSource.getMessage("rfx.parent.reOrder.error", new Object[] {}, Global.LOCALE));
					return new ResponseEntity<List<RfaCqItem>>(rfaCqService.findRfaCqbyCqId(rfaCqService.getCqItembyCqItemId(rfaCqItemPojo.getId()).getCq().getId()), headers, HttpStatus.BAD_REQUEST);
				}
				RfaCqItem item = new RfaCqItem();
				item.setItemName(rfaCqItemPojo.getItemName());
				if (cqItem.getParent() != null && !cqItem.getParent().getId().equals(rfaCqItemPojo.getParent()) && rfaCqService.isExists(item, rfaCqItemPojo.getCq(), rfaCqItemPojo.getParent())) {
					LOG.info("Duplicate....");
					throw new ApplicationException("Duplicate CQ Item. CQ Item by that name already exists.");
				}
				rfaCqService.reorderCqItems(rfaCqItemPojo);
				cqList = rfaCqService.findRfaCqbyCqId(rfaCqService.getCqItembyCqItemId(rfaCqItemPojo.getId()).getCq().getId());
				headers.add("success", messageSource.getMessage("buyer.rftbq.success", new Object[] {}, Global.LOCALE));
				return new ResponseEntity<List<RfaCqItem>>(cqList, headers, HttpStatus.OK);
			}
		} catch (NotAllowedException | ApplicationException nae) {
			LOG.error("Error while moving CQ Items to Event : " + nae.getMessage(), nae);
			headers.add("error", nae.getMessage());
			return new ResponseEntity<List<RfaCqItem>>(rfaCqService.findRfaCqbyCqId(rfaCqService.getCqItembyCqItemId(rfaCqItemPojo.getId()).getCq().getId()), headers, HttpStatus.BAD_REQUEST);

		} catch (Exception e) {
			LOG.error("Error while moving CQ Items to Event : " + e.getMessage(), e);
			// headers.add("error", "Error while adding Notes to Buyer " +
			// e.getMessage());
			headers.add("error", messageSource.getMessage("buyer.rftcq.error", new Object[] { e.getMessage() }, Global.LOCALE));
			return new ResponseEntity<List<RfaCqItem>>(null, headers, HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<List<RfaCqItem>>(cqList, headers, HttpStatus.OK);
	}

	@RequestMapping(value = "/cqPrevious", method = RequestMethod.POST)
	public String cqPrevious(Model model, @RequestParam(name = "eventId", required = true) String eventId) throws Exception {
		try {
			RfaEvent rfaEvent = rfaEventService.getRfaEventByeventId(eventId);
			if (rfaEvent != null) {
				model.addAttribute("eventPermissions", rfaEventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), eventId));
				if (Boolean.TRUE == rfaEvent.getMeetingReq()) {
					return "redirect:meetingList/" + rfaEvent.getId();
				} else if (rfaEvent.getEventVisibility() != EventVisibilityType.PUBLIC) {
					return "redirect:addSupplier/" + rfaEvent.getId();
				} else if (Boolean.TRUE == rfaEvent.getDocumentReq()) {
					return "redirect:createEventDocuments/" + rfaEvent.getId();
				} else {
					return "redirect:auctionRules/" + rfaEvent.getId();
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
				model.addAttribute("eventPermissions", rfaEventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), eventId));
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
			RfaEvent rfaEvent = rfaEventService.getRfaEventByeventId(eventId);
			if (rfaEvent != null) {
				model.addAttribute("eventPermissions", rfaEventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), eventId));
				rfaEvent.setCqCompleted(Boolean.TRUE);
				rfaEventService.updateRfaEvent(rfaEvent);
				if (Boolean.TRUE == rfaEvent.getBillOfQuantity()) {
					return "redirect:createBQList/" + eventId;
				} else if(Boolean.TRUE == rfaEvent.getScheduleOfRate()) {
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
	public String cqSaveDraft(RedirectAttributes redir, @RequestParam("eventId") String eventId, Model model) {
		RfaEvent rfaEvent = rfaEventService.getRfaEventByeventId(eventId);
		model.addAttribute("eventPermissions", rfaEventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), eventId));
		redir.addFlashAttribute("success", messageSource.getMessage("rftEvent.success.savedraft", new Object[] { (rfaEvent.getEventName() != null ? rfaEvent.getEventName() : rfaEvent.getEventId()) }, Global.LOCALE));
		return "redirect:eventCqList/" + rfaEvent.getId();

	}

	@RequestMapping(path = "/cqItemTemplate/{cqId}", method = RequestMethod.GET)
	public void downloader(@PathVariable String cqId, HttpServletRequest request, HttpServletResponse response) throws IOException {
		try {
			XSSFWorkbook workbook = new XSSFWorkbook();
			String downloadFolder = context.getRealPath("/WEB-INF/");
			String fileName = "CqItemTemplate.xlsx";
			Path file = Paths.get(downloadFolder, fileName);
			LOG.info("File Path ::" + file);

			super.eventDownloader(workbook, cqId, RfxTypes.RFA);

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
	public ResponseEntity<List<RfaCqItem>> handleFormUpload(@RequestParam("file") MultipartFile file, @RequestParam("cqId") String cqId, @RequestParam("eventId") String eventId) throws IOException {
		LOG.info("UPLOADING STARTED ...... CQ Id :: " + cqId + "Event Id :: " + eventId);
		List<RfaCqItem> cqList = null;
		String message = null;
		HttpHeaders headers = new HttpHeaders();
		try {
			if (!file.isEmpty()) {
				if (!file.getContentType().equals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet") && !file.getContentType().equals("application/wps-office.xlsx") && !file.getContentType().equals("application/vnd.ms-excel"))
					throw new MultipartException("Only excel files accepted!");
				try {
					message = super.cqItemsUpload(file, cqId, eventId, RfxTypes.RFA);

				} catch (Exception e) {
					e.printStackTrace();
					cqList = rfaCqService.findRfaCqbyCqId(cqId);
					headers.add("error", messageSource.getMessage("common.upload.fail", new Object[] { e.getMessage() }, Global.LOCALE));
					return new ResponseEntity<List<RfaCqItem>>(cqList, headers, HttpStatus.BAD_REQUEST);
				}
			}
		} catch (MultipartException e) {
			// e.printStackTrace();
			// LOG.info("Upload failed!" + e.getMessage());
			cqList = rfaCqService.findRfaCqbyCqId(cqId);
			headers.add("error", messageSource.getMessage("common.upload.notsupported.fail", new Object[] { e.getMessage() }, Global.LOCALE));
			return new ResponseEntity<List<RfaCqItem>>(cqList, headers, HttpStatus.BAD_REQUEST);
		}
		// return "redirect:createEventBQ/" + eventId + "/" + cqId;
		cqList = rfaCqService.findRfaCqbyCqId(cqId);
		if (message == null) {
			headers.add("success", messageSource.getMessage("common.upload.success", new Object[] { "RFA CQ Items" }, Global.LOCALE));
		} else {
			headers.add("warn", messageSource.getMessage("common.upload.success", new Object[] { "RFA CQ Items" }, Global.LOCALE) + ", " + message);
		}
		return new ResponseEntity<List<RfaCqItem>>(cqList, headers, HttpStatus.OK);
	}

	@RequestMapping(value = "/updateEventCqOrder/{eventId}", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<List<RfaCq>> updateEventCqOrder(@RequestBody String[] cqIds, @PathVariable("eventId") String eventId) {
		try {
			LOG.info("RFA eventId : " + eventId);
			if (cqIds != null && cqIds.length > 0) {
				Integer count = 1;
				for (String cqId : cqIds) {
					RfaCq cq = rfaCqService.getRfaCqById(cqId);
					cq.setCqOrder(count);
					rfaCqService.updatCq(cq);
					count++;
				}
				List<RfaCq> cqList = rfaCqService.findRfaCqForEventByOrder(eventId);
				HttpHeaders headers = new HttpHeaders();
				headers.add("success", messageSource.getMessage("rft.cq.order", new Object[] {}, Global.LOCALE));
				return new ResponseEntity<List<RfaCq>>(cqList, headers, HttpStatus.OK);
			} else {
				List<RfaCq> cqList = rfaCqService.findRfaCqForEventByOrder(eventId);
				HttpHeaders headers = new HttpHeaders();
				headers.add("error", messageSource.getMessage("rft.cq.order.empty", new Object[] {}, Global.LOCALE));
				return new ResponseEntity<List<RfaCq>>(cqList, headers, HttpStatus.BAD_REQUEST);
			}
		} catch (Exception e) {
			LOG.error("Error while Ordering CQ : " + e.getMessage(), e);
			HttpHeaders headers = new HttpHeaders();
			headers.add("error", messageSource.getMessage("rft.bq.order.error", new Object[] { e.getMessage() }, Global.LOCALE));
			return new ResponseEntity<List<RfaCq>>(null, headers, HttpStatus.BAD_REQUEST);
		}
	}
}
