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
import com.privasia.procurehere.core.entity.RfiCq;
import com.privasia.procurehere.core.entity.RfiCqItem;
import com.privasia.procurehere.core.entity.RfiCqOption;
import com.privasia.procurehere.core.entity.RfiEvent;
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
import com.privasia.procurehere.service.RfiCqService;
import com.privasia.procurehere.service.RfiDocumentService;
import com.privasia.procurehere.service.RfiEventService;
import com.privasia.procurehere.service.UomService;

@Controller
@RequestMapping("/buyer/RFI")
public class RfiCqController extends EventCqBase {

	private static final Logger LOG = LogManager.getLogger(Global.ADMIN_LOG);

	@Autowired
	UomService uomService;

	@Autowired
	RfiCqService cqService;

	@Autowired
	RfiEventService eventService;

	@Autowired
	RfiDocumentService rfiDocumentService;

	public RfiCqController() {
		super(RfxTypes.RFI);
	}

	@RequestMapping(path = "/eventCqList/{eventId}", method = RequestMethod.GET)
	public String eventCqList(@PathVariable String eventId, Model model) {
		if (StringUtils.checkString(eventId).length() == 0) {
			return "redirect:/400_error";
		}
		// List<RfiCq> cqList = cqService.findRfiCqForEvent(eventId);
		List<RfiCq> cqList = cqService.findRfiCqForEventByOrder(eventId);
		RfiEvent rftEvent = cqService.findEventForCqByEventId(eventId);

		RfiCq rfiCq = new RfiCq();
		rfiCq.setRfxEvent(rftEvent);
		model.addAttribute("eventPermissions", eventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), eventId));
		model.addAttribute("rftCq", rfiCq);
		constructModel(eventId, model, cqList, rftEvent);
		return "eventCqList";
	}

	/**
	 * @param eventId
	 * @param model
	 * @param cqList
	 * @param rftEvent
	 */
	private void constructModel(String eventId, Model model, List<RfiCq> cqList, RfiEvent rftEvent) {
		model.addAttribute("cqList", cqList);
		model.addAttribute("eventId", eventId);
		model.addAttribute("event", rftEvent);
	}

	@RequestMapping(path = "/createRftCq", method = RequestMethod.POST)
	public String createRftCqPost(@ModelAttribute("rftCq") RfiCq rfiCq, Model model, BindingResult result, RedirectAttributes redir) {
		if (result.hasErrors()) {
			LOG.error("Page submitted With Errors ............................. ");
			for (ObjectError error : result.getAllErrors()) {
				LOG.info("ERROR : " + error.getObjectName() + " : " + error.getDefaultMessage());
				redir.addFlashAttribute("errors", error.getDefaultMessage());
			}
			model.addAttribute("rftCq", rfiCq);
		} else {
			model.addAttribute("eventPermissions", eventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), rfiCq.getRfxEvent().getId()));
			LOG.info("Submit with no errors..............................................." + rfiCq.getRfxEvent().getId());
			RfiCq cq = null;
			try {
				if (cqService.isExists(rfiCq)) {
					LOG.info("Duplicate....");
					throw new ApplicationException("Duplicate CQ. CQ by that name already exists.");
				}
				RfiEvent rftEvent = cqService.findEventForCqByEventId(rfiCq.getRfxEvent().getId());
				rfiCq.setRfxEvent(rftEvent);
				cq = cqService.stroreCq(rfiCq);
				List<RfiCq> cqList = cqService.findRfiCqForEventByOrder(rfiCq.getRfxEvent().getId());
				constructModel(rftEvent.getId(), model, cqList, rftEvent);
				if (StringUtils.checkString(rfiCq.getId()).length() == 0) {
					redir.addFlashAttribute("success", messageSource.getMessage("rft.cq.save", new Object[] {}, Global.LOCALE));
					return "redirect:eventCqList/" + rfiCq.getRfxEvent().getId();
				} else {
					redir.addFlashAttribute("success", messageSource.getMessage("rft.cq.update", new Object[] {}, Global.LOCALE));
					return "redirect:eventCqItemList/" + rfiCq.getId();
				}
			} catch (Exception e) {
				LOG.error("Error while storing CQ," + e.getMessage(), e);
				redir.addFlashAttribute("error", messageSource.getMessage("rft.cq.error", new Object[] { rfiCq.getName(), e.getMessage() }, Global.LOCALE));
				if (StringUtils.checkString(rfiCq.getId()).length() == 0) {
					return "redirect:eventCqList/" + rfiCq.getRfxEvent().getId();
				} else {
					return "redirect:eventCqItemList/" + rfiCq.getId();
				}
			}
		}
		return "redirect:eventCqList/" + rfiCq.getRfxEvent().getId();
	}

	@RequestMapping(value = "/deleteCq", method = RequestMethod.POST)
	public String deleteCq(@ModelAttribute RfiCq rftCq, Model model) throws JsonProcessingException {
		RfiCq cq = null;
		try {
			cq = cqService.getCqById(rftCq.getId());
			cqService.isAllowToDeleteCq(rftCq.getId());
			cqService.deleteCq(cq);

			List<RfiCq> cqList = cqService.findRfiCqForEventByOrder(cq.getRfxEvent().getId());

			RfiEvent rftEvent = new RfiEvent();
			rftEvent.setId(cq.getRfxEvent().getId());
			rftCq.setRfxEvent(rftEvent);
			model.addAttribute("rftCq", rftCq);
			model.addAttribute("eventPermissions", eventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), rftCq.getRfxEvent().getId()));

			constructModel(rftEvent.getId(), model, cqList, cqService.findEventForCqByEventId(cq.getRfxEvent().getId()));

			model.addAttribute("success", messageSource.getMessage("rft.cq.success.delete", new Object[] { cq.getName() }, Global.LOCALE));
		} catch (NotAllowedException e) {
			LOG.error("Error while deleting cqItems : " + e.getMessage(), e);
			model.addAttribute("error", e.getMessage());
			List<RfiCq> cqList = cqService.findRfiCqForEventByOrder(cq.getRfxEvent().getId());
			RfiEvent rftEvent = new RfiEvent();
			rftEvent.setId(cq.getRfxEvent().getId());
			rftCq.setRfxEvent(rftEvent);
			model.addAttribute("rftCq", rftCq);
			constructModel(rftEvent.getId(), model, cqList, cqService.findEventForCqByEventId(cq.getRfxEvent().getId()));
			return "eventCqList";
		} catch (Exception e) {
			LOG.error("Error while deleting Cq  [ " + (cq != null ? cq.getName() : "") + " ]" + e.getMessage(), e);
			model.addAttribute("error", messageSource.getMessage("rft.cq.error.delete", new Object[] { (cq != null ? cq.getName() : "") }, Global.LOCALE));
			List<RfiCq> cqList = cqService.findRfiCqForEventByOrder(cq.getRfxEvent().getId());

			RfiEvent rftEvent = new RfiEvent();
			rftEvent.setId(cq.getRfxEvent().getId());
			rftCq.setRfxEvent(rftEvent);
			model.addAttribute("rftCq", rftCq);
			constructModel(rftEvent.getId(), model, cqList, cqService.findEventForCqByEventId(cq.getRfxEvent().getId()));
			return "eventCqList";
		}
		return "eventCqList";
	}

	@RequestMapping(path = "/showCqItems", method = RequestMethod.POST)
	public String showCqItemList(@RequestParam(name = "cqId", required = true) String cqId, @RequestParam(name = "eventId", required = true) String eventId, RedirectAttributes reAttributes) {
		RfiCq cq = cqService.getCqById(cqId);
		reAttributes.addFlashAttribute("eventId", eventId);
		// reAttributes.addFlashAttribute("rftCq", cq);
		return "redirect:eventCqItemList/" + cq.getId();
	}

	@SuppressWarnings("unused")
	@RequestMapping(path = "/eventCqItemList/{cqId}", method = RequestMethod.GET)
	public String eventCqItemList(@PathVariable String cqId, Model model) {
		if (StringUtils.checkString(cqId).length() == 0) {
			return "redirect:/400_error";
		}
		RfiCq rftCq = cqService.getCqById(cqId);
		LOG.info("CQ : " + rftCq.getId());
		List<RfiCqItem> cqItemList = cqService.findCqItemsByCqId(rftCq.getId());

		LOG.info("CQ ITEM LIST : " + cqItemList.size());
		String eventId = (String) model.asMap().get("eventId");
		if (eventId == null || eventId.length() == 0) {
			eventId = rftCq.getRfxEvent().getId();
		}
		if (eventId != null && eventId.length() > 0) {
			List<EventDocument> eventDocumentList = rfiDocumentService.findAllRfiEventDocsNameByEventId(eventId);
			model.addAttribute("eventDocumentList", eventDocumentList);
		}
		model.addAttribute("rftCq", rftCq);
		model.addAttribute("eventPermissions", eventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), rftCq.getRfxEvent().getId()));
		model.addAttribute("cqItemList", cqItemList);
		RfiEvent event = cqService.findEventForCqByEventId(rftCq.getRfxEvent().getId());
		model.addAttribute("event", event);

		model.addAttribute("cqTypes", CqType.values());
		RfiCqItem cqItem = new RfiCqItem();
		cqItem.setCq(rftCq);
		model.addAttribute("cqItem", cqItem);
		return "eventCqItemList";
	}

	@RequestMapping(path = "/createRftCqItem", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<List<RfiCqItem>> createRfiCqItem(@RequestBody CqItemPojo rftCqItem) throws JsonProcessingException {
		HttpHeaders headers = new HttpHeaders();
		try {
			if (!doValidate(rftCqItem, headers)) {
				return new ResponseEntity<List<RfiCqItem>>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
			}
			LOG.info("CQ ITEM : " + rftCqItem.toLogString());
			if (StringUtils.checkString(rftCqItem.getItemName()).length() > 0) {
				RfiCqItem item = new RfiCqItem();
				item.setItemName(rftCqItem.getItemName());
				item.setItemDescription(rftCqItem.getItemDescription());
				item.setIsSupplierAttachRequired(rftCqItem.getIsSupplierAttachRequired());
				if (cqService.isExists(item, rftCqItem.getCq(), rftCqItem.getParent())) {
					LOG.info("Duplicate....");
					throw new ApplicationException("Duplicate CQ Item. CQ Item by that name already exists.");
				}

				item.setRfxEvent(cqService.getRfiEventById(rftCqItem.getRftEvent()));
				item.setCq(cqService.getCqById(rftCqItem.getCq()));
				if (StringUtils.checkString(rftCqItem.getCqType()).length() > 0) {
					item.setCqType(CqType.valueOf(rftCqItem.getCqType()));
				}
				item.setAttachment(rftCqItem.isAttachment());
				item.setOptional(rftCqItem.isOptional());

				if (CollectionUtil.isNotEmpty(rftCqItem.getOptions())) {
					List<RfiCqOption> optionItems = new ArrayList<RfiCqOption>();
					int optionOrder = 0;
					int totalScore = 0;
					for (String option : rftCqItem.getOptions()) {
						if (StringUtils.checkString(option).length() == 0) {
							continue;
						}
						RfiCqOption options = new RfiCqOption();
						options.setCqItem(item);
						options.setValue(option);
						options.setOrder(++optionOrder);
						if (CollectionUtil.isNotEmpty(rftCqItem.getOptionScore()))
							options.setScoring(Integer.parseInt(rftCqItem.getOptionScore().get(optionOrder - 1)));
						totalScore += options.getScoring() != null ? options.getScoring().intValue() : 0;
						optionItems.add(options);
					}
					item.setTotalScore(totalScore);
					item.setCqOptions(optionItems);
				}
				if (StringUtils.checkString(rftCqItem.getParent()).length() > 0) {
					LOG.info("PARENT ID : " + rftCqItem.getParent());
					item.setParent(cqService.getCqItembyCqItemId(StringUtils.checkString(rftCqItem.getParent())));
				}

				cqService.saveCqItem(item);

				List<RfiCqItem> cqList = cqService.findCqItemsByCqId(rftCqItem.getCq());
				headers.add("success", messageSource.getMessage("buyer.rftcq.save", new Object[] {}, Global.LOCALE));
				return new ResponseEntity<List<RfiCqItem>>(cqList, headers, HttpStatus.OK);
			} else {
				List<RfiCqItem> cqList = cqService.findCqItemsByCqId(rftCqItem.getCq());
				headers.add("info", messageSource.getMessage("rft.cq.name.empty", new Object[] {}, Global.LOCALE));
				return new ResponseEntity<List<RfiCqItem>>(cqList, headers, HttpStatus.OK);
			}
		} catch (Exception e) {
			LOG.error("Error while adding CQ Items to Event : " + e.getMessage(), e);
			headers.add("error", messageSource.getMessage("buyer.rftcq.error", new Object[] { e.getMessage() }, Global.LOCALE));
			return new ResponseEntity<List<RfiCqItem>>(null, headers, HttpStatus.BAD_REQUEST);
		}
	}

	@RequestMapping(path = "/getCqData", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<List<CqItemPojo>> getCqData(@RequestParam("itemId") String itemId) throws JsonProcessingException {
		try {

			if (StringUtils.checkString(itemId).length() > 0) {
				RfiCqItem item = cqService.getCqItembyCqItemId(StringUtils.checkString(itemId));
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
	public @ResponseBody ResponseEntity<List<RfiCqItem>> updateCqItem(@RequestBody CqItemPojo itemPojo) throws JsonProcessingException {
		HttpHeaders headers = new HttpHeaders();
		try {
			if (!doValidate(itemPojo, headers)) {
				return new ResponseEntity<List<RfiCqItem>>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
			}
			LOG.info(" :: UPDATE CQ ::" + itemPojo.toLogString());
			if (StringUtils.checkString(itemPojo.getItemName()).length() > 0) {
				RfiCqItem item = new RfiCqItem();
				item.setId(itemPojo.getId());
				item.setItemName(itemPojo.getItemName());
				item.setItemDescription(itemPojo.getItemDescription());
				item.setIsSupplierAttachRequired(itemPojo.getIsSupplierAttachRequired());
				if (cqService.isExists(item, itemPojo.getCq(), itemPojo.getParent())) {
					LOG.info("Duplicate....");
					throw new ApplicationException("Duplicate CQ Item. CQ Item by that name already exists.");
				}
				cqService.updateCqItem(itemPojo);
				List<RfiCqItem> cqList = cqService.findCqItemsByCqId(itemPojo.getCq());
				LOG.info("EVENT ALL LIST :: " + cqList.toString());
				headers.add("success", messageSource.getMessage("buyer.rftcq.update", new Object[] {}, Global.LOCALE));
				return new ResponseEntity<List<RfiCqItem>>(cqList, headers, HttpStatus.OK);
			} else {
				List<RfiCqItem> cqList = cqService.findCqItemsByCqId(itemPojo.getCq());
				headers.add("info", messageSource.getMessage("rft.cq.name.empty", new Object[] {}, Global.LOCALE));
				return new ResponseEntity<List<RfiCqItem>>(cqList, headers, HttpStatus.OK);
			}
		} catch (Exception e) {
			LOG.error("Error while updating CQ Items to Buyer : " + e.getMessage(), e);
			headers.add("error", messageSource.getMessage("buyer.rftcq.error", new Object[] { e.getMessage() }, Global.LOCALE));
			return new ResponseEntity<List<RfiCqItem>>(null, headers, HttpStatus.BAD_REQUEST);
		}
	}

	@RequestMapping(value = "/deleteCqItem/{cqId}", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<List<RfiCqItem>> deleteCqItem(@RequestBody String[] items, @PathVariable("cqId") String cqId) throws JsonProcessingException {
		LOG.info("DELETE CQ ITEM REQUESTED : " + items.toString());
		List<RfiCqItem> cqItems = null;
		HttpHeaders headers = new HttpHeaders();
		if (items != null && items.length > 0) {
			try {
				cqService.deleteCqItems(items, cqId);
				cqItems = cqService.findCqItembyCqId(cqId);
				headers.add("errors", messageSource.getMessage("buyer.rftcq.delete", new Object[] {}, Global.LOCALE));
			} catch (Exception e) {
				LOG.error("Error while deleting cqItems : " + e.getMessage(), e);
				headers.add("errors", messageSource.getMessage("rft.cq.info.delete", new Object[] {}, Global.LOCALE));
				return new ResponseEntity<List<RfiCqItem>>(null, headers, HttpStatus.BAD_REQUEST);
			}
		} else {
			LOG.error("Please select at least one Cq Item");
			headers.add("errors", messageSource.getMessage("rft.cq.error.delete.required", new Object[] {}, Global.LOCALE));
			return new ResponseEntity<List<RfiCqItem>>(null, headers, HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<List<RfiCqItem>>(cqItems, headers, HttpStatus.OK);
	}

	@RequestMapping(path = "/eventCqOrder", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<List<RfiCqItem>> eventCqOrder(@RequestBody CqItemPojo rftCqItemPojo) throws JsonProcessingException {
		LOG.info("Parent : " + rftCqItemPojo.getParent() + " Item Id : " + rftCqItemPojo.getId() + " New Position : " + rftCqItemPojo.getOrder());
		HttpHeaders headers = new HttpHeaders();
		List<RfiCqItem> cqList = null;
		try {
			if (StringUtils.checkString(rftCqItemPojo.getId()).length() > 0) {
				LOG.info("Updating order..........................");
				RfiCqItem cqItem = cqService.getCqItembyCqItemId(rftCqItemPojo.getId());
				if (cqItem.getOrder() > 0 && StringUtils.checkString(rftCqItemPojo.getParent()).length() == 0) {
					LOG.info("child cannot be a parent");
					headers.add("error", messageSource.getMessage("rfx.child.reOrder.error", new Object[] {}, Global.LOCALE));
					return new ResponseEntity<List<RfiCqItem>>(cqService.findCqItemsByCqId(cqService.getCqItembyCqItemId(rftCqItemPojo.getId()).getCq().getId()), headers, HttpStatus.BAD_REQUEST);
				}
				if (cqItem.getOrder() == 0 && StringUtils.checkString(rftCqItemPojo.getParent()).length() > 0) {
					LOG.info("parent cannot be a child");
					headers.add("error", messageSource.getMessage("rfx.parent.reOrder.error", new Object[] {}, Global.LOCALE));
					return new ResponseEntity<List<RfiCqItem>>(cqService.findCqItemsByCqId(cqService.getCqItembyCqItemId(rftCqItemPojo.getId()).getCq().getId()), headers, HttpStatus.BAD_REQUEST);
				}
				RfiCqItem item = new RfiCqItem();
				item.setItemName(rftCqItemPojo.getItemName());
				if (cqItem.getParent() != null && !cqItem.getParent().getId().equals(rftCqItemPojo.getParent()) && cqService.isExists(item, rftCqItemPojo.getCq(), rftCqItemPojo.getParent())) {
					LOG.info("Duplicate....");
					throw new ApplicationException("Duplicate CQ Item. CQ Item by that name already exists.");
				}
				cqService.reorderCqItems(rftCqItemPojo);
				cqList = cqService.findCqItembyCqId(cqService.getCqItembyCqItemId(rftCqItemPojo.getId()).getCq().getId());
				headers.add("success", messageSource.getMessage("buyer.rftbq.success", new Object[] {}, Global.LOCALE));
				return new ResponseEntity<List<RfiCqItem>>(cqList, headers, HttpStatus.OK);
			}
		} catch (NotAllowedException | ApplicationException nae) {
			LOG.error("Error while adding CQ Items to Event : " + nae.getMessage(), nae);
			headers.add("error", nae.getMessage());
			return new ResponseEntity<List<RfiCqItem>>(cqService.findCqItembyCqId(cqService.getCqItembyCqItemId(rftCqItemPojo.getId()).getCq().getId()), headers, HttpStatus.BAD_REQUEST);

		} catch (Exception e) {
			LOG.error("Error while adding CQ Items to Event : " + e.getMessage(), e);
			// headers.add("error", "Error while adding Notes to Buyer " + e.getMessage());
			headers.add("error", messageSource.getMessage("buyer.rftcq.error", new Object[] { e.getMessage() }, Global.LOCALE));
			return new ResponseEntity<List<RfiCqItem>>(null, headers, HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<List<RfiCqItem>>(cqList, headers, HttpStatus.OK);
	}

	@RequestMapping(value = "/cqPrevious", method = RequestMethod.POST)
	public String cqPrevious(Model model, @RequestParam(name = "eventId", required = true) String eventId, RedirectAttributes redir) throws Exception {
		try {
			model.addAttribute("eventPermissions", eventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), eventId));
			RfiEvent rftEvent = cqService.getRfiEventById(eventId);
			if (rftEvent != null) {
				// redir.addFlashAttribute("eventId", rftEvent.getId());
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
	public String cqListPrevious(Model model, @RequestParam(name = "eventId", required = true) String eventId, RedirectAttributes redir) throws Exception {
		try {
			RfiEvent rftEvent = cqService.getRfiEventById(eventId);
			if (rftEvent != null) {
				model.addAttribute("eventPermissions", eventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), eventId));
				// redir.addFlashAttribute("eventId", rftEvent.getId());
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
	public String cqNext(Model model, @RequestParam(name = "eventId", required = true) String eventId, RedirectAttributes redir) throws Exception {
		try {
			RfiEvent rftEvent = cqService.getRfiEventById(eventId);
			if (rftEvent != null) {
				model.addAttribute("eventPermissions", eventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), eventId));
				rftEvent.setCqCompleted(Boolean.TRUE);
				eventService.updateRfiEvent(rftEvent);
				if(Boolean.TRUE == rftEvent.getScheduleOfRate()) {
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
		RfiEvent rftEvent = cqService.getRfiEventById(eventId);
		model.addAttribute("eventPermissions", eventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), eventId));
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

			super.eventDownloader(workbook, cqId, RfxTypes.RFI);

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
	public ResponseEntity<List<RfiCqItem>> handleFormUpload(@RequestParam("file") MultipartFile file, @RequestParam("cqId") String cqId, @RequestParam("eventId") String eventId) throws IOException {
		LOG.info("UPLOADING STARTED ...... CQ Id :: " + cqId + "Event Id :: " + eventId);
		List<RfiCqItem> cqList = null;
		String message = null;
		HttpHeaders headers = new HttpHeaders();
		try {
			if (!file.isEmpty()) {
				if (!file.getContentType().equals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet") && !file.getContentType().equals("application/wps-office.xlsx") && !file.getContentType().equals("application/vnd.ms-excel"))
					throw new MultipartException("Only excel files accepted!");
				try {
					message = super.cqItemsUpload(file, cqId, eventId, RfxTypes.RFI);

				} catch (Exception e) {
					e.printStackTrace();
					cqList = cqService.findCqItembyCqId(cqId);
					headers.add("error", messageSource.getMessage("common.upload.fail", new Object[] { e.getMessage() }, Global.LOCALE));
					return new ResponseEntity<List<RfiCqItem>>(cqList, headers, HttpStatus.BAD_REQUEST);
				}
			}
		} catch (MultipartException e) {
			// e.printStackTrace();
			// LOG.info("Upload failed!" + e.getMessage());
			cqList = cqService.findCqItembyCqId(cqId);
			headers.add("error", messageSource.getMessage("common.upload.notsupported.fail", new Object[] { e.getMessage() }, Global.LOCALE));
			return new ResponseEntity<List<RfiCqItem>>(cqList, headers, HttpStatus.BAD_REQUEST);
		}
		// return "redirect:createEventBQ/" + eventId + "/" + cqId;
		cqList = cqService.findCqItembyCqId(cqId);
		if (message == null) {
			headers.add("success", messageSource.getMessage("common.upload.success", new Object[] { "RFI CQ Items" }, Global.LOCALE));
		} else {
			headers.add("warn", messageSource.getMessage("common.upload.success", new Object[] { "RFI CQ Items" }, Global.LOCALE) + ", " + message);
		}
		return new ResponseEntity<List<RfiCqItem>>(cqList, headers, HttpStatus.OK);
	}

	@RequestMapping(value = "/updateEventCqOrder/{eventId}", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<List<RfiCq>> updateEventCqOrder(@RequestBody String[] cqIds, @PathVariable("eventId") String eventId) {
		try {
			LOG.info("RFI eventId : " + eventId);
			if (cqIds != null && cqIds.length > 0) {
				Integer count = 1;
				for (String cqId : cqIds) {
					RfiCq cq = rfiCqService.getCqById(cqId);
					cq.setCqOrder(count);
					rfiCqService.updateCq(cq);
					count++;
				}
				List<RfiCq> cqList = rfiCqService.findRfiCqForEventByOrder(eventId);
				HttpHeaders headers = new HttpHeaders();
				headers.add("success", messageSource.getMessage("rft.cq.order", new Object[] {}, Global.LOCALE));
				return new ResponseEntity<List<RfiCq>>(cqList, headers, HttpStatus.OK);
			} else {
				List<RfiCq> cqList = rfiCqService.findRfiCqForEventByOrder(eventId);
				HttpHeaders headers = new HttpHeaders();
				headers.add("error", messageSource.getMessage("rft.cq.order.empty", new Object[] {}, Global.LOCALE));
				return new ResponseEntity<List<RfiCq>>(cqList, headers, HttpStatus.BAD_REQUEST);
			}
		} catch (Exception e) {
			LOG.error("Error while Ordering CQ : " + e.getMessage(), e);
			HttpHeaders headers = new HttpHeaders();
			headers.add("error", messageSource.getMessage("rft.bq.order.error", new Object[] { e.getMessage() }, Global.LOCALE));
			return new ResponseEntity<List<RfiCq>>(null, headers, HttpStatus.BAD_REQUEST);
		}
	}

}
