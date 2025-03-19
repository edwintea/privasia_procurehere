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
import com.privasia.procurehere.core.entity.RftCq;
import com.privasia.procurehere.core.entity.RftCqItem;
import com.privasia.procurehere.core.entity.RftCqOption;
import com.privasia.procurehere.core.entity.RftEvent;
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
import com.privasia.procurehere.service.RftCqService;
import com.privasia.procurehere.service.RftDocumentService;
import com.privasia.procurehere.service.RftEventService;
import com.privasia.procurehere.service.UomService;

@Controller
@RequestMapping("/buyer/RFT")
public class RftCqController extends EventCqBase {

	static final Logger LOG = LogManager.getLogger(Global.ADMIN_LOG);

	@Autowired
	UomService uomService;

	@Autowired
	RftCqService rftCqService;

	@Autowired
	RftEventService rftEventService;

	@Autowired
	RftDocumentService rftDocumentService;

	public RftCqController() {
		super(RfxTypes.RFT);
	}

	@RequestMapping(path = "/eventCqList/{eventId}", method = RequestMethod.GET)
	public String eventCqList(@PathVariable String eventId, Model model) {
		if (StringUtils.checkString(eventId).length() == 0) {
			return "redirect:/400_error";
		}
		// List<RftCq> cqList = rftCqService.findRftCqForEvent(eventId);
		List<RftCq> cqList = rftCqService.findRftCqForEventByOrder(eventId);

		RftCq rftCq = new RftCq();
		RftEvent rftEvent = new RftEvent();
		rftEvent.setId(eventId);
		LOG.info("==================" + eventId + "=======================eventCqList");
		rftCq.setRfxEvent(rftEvent);
		model.addAttribute("cqList", cqList);
		model.addAttribute("rftCq", rftCq);
		model.addAttribute("eventId", eventId);
		model.addAttribute("event", rftCqService.findEventForCqByEventId(eventId));
		model.addAttribute("eventPermissions", rftEventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), rftEvent.getId()));

		return "eventCqList";
	}

	@RequestMapping(path = "/createRftCq", method = RequestMethod.POST)
	public String createRftCqPost(@ModelAttribute RftCq rftCq, Model model, BindingResult result, RedirectAttributes redir) {
		if (result.hasErrors()) {
			LOG.error("Page submitted With Errors ............................. ");
			for (ObjectError error : result.getAllErrors()) {
				LOG.info("ERROR : " + error.getObjectName() + " : " + error.getDefaultMessage());
				redir.addFlashAttribute("errors", error.getDefaultMessage());
			}
			model.addAttribute("rftCq", rftCq);
		} else {
			LOG.info("Submit with no errors..............................................." + rftCq.toLogString());
			RftCq cq = null;
			try {

				if (rftCqService.isExists(rftCq)) {
					LOG.info("Duplicate....");
					throw new ApplicationException("Duplicate CQ. CQ by that name already exists.");
				}
				RftEvent rftEvent = rftCqService.findEventForCqByEventId(rftCq.getRfxEvent().getId());
				rftCq.setRfxEvent(rftEvent);
				cq = rftCqService.stroreRftCq(rftCq);
				if (StringUtils.checkString(rftCq.getId()).length() == 0) {
					redir.addFlashAttribute("success", messageSource.getMessage("rft.cq.save", new Object[] {}, Global.LOCALE));
					return "redirect:eventCqList/" + rftCq.getRfxEvent().getId();
				} else {
					redir.addFlashAttribute("success", messageSource.getMessage("rft.cq.update", new Object[] {}, Global.LOCALE));
					return "redirect:eventCqItemList/" + rftCq.getId();
				}

			} catch (Exception e) {
				LOG.error("Error while storing CQ," + e.getMessage(), e);
				redir.addFlashAttribute("error", messageSource.getMessage("rft.cq.error", new Object[] { rftCq.getName(), e.getMessage() }, Global.LOCALE));
				if (StringUtils.checkString(rftCq.getId()).length() == 0) {
					return "redirect:eventCqList/" + rftCq.getRfxEvent().getId();
				} else {
					return "redirect:eventCqItemList/" + rftCq.getId();
				}
			}
		}
		return "redirect:eventCqList/" + rftCq.getRfxEvent().getId();
	}

	@RequestMapping(value = "/deleteCq", method = RequestMethod.POST)
	public String deleteCq(@ModelAttribute RftCq rftCq, Model model, RedirectAttributes redir) throws JsonProcessingException {
		RftCq cq = null;
		RftEvent rftEvent = null;
		try {
			cq = rftCqService.getRftCqById(rftCq.getId());
			rftCqService.isAllowToDeleteCq(rftCq.getId());
			rftCqService.deleteCq(cq);

			// List<RftCq> cqList = rftCqService.findRftCqForEvent(cq.getRfxEvent().getId());
			// RftEvent rftEvent = new RftEvent();
			// rftEvent.setId(cq.getRfxEvent().getId());
			// rftCq.setRfxEvent(rftEvent);
			// model.addAttribute("cqList", cqList);
			// model.addAttribute("rftCq", rftCq);
			// model.addAttribute("event", cq.getRfxEvent());
			// model.addAttribute("eventId", cq.getRfxEvent().getId());
			// model.addAttribute("eventPermissions",
			// rftEventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), rftEvent.getId()));

			redir.addFlashAttribute("success", messageSource.getMessage("rft.cq.success.delete", new Object[] { cq.getName() }, Global.LOCALE));
		} catch (NotAllowedException e) {
			LOG.error("Error while deleting cqItems : " + e.getMessage(), e);
			rftEvent = rftEventService.getRftEventById(cq.getRfxEvent().getId());
			model.addAttribute("error", e.getMessage());
			List<RftCq> cqList = rftCqService.findRftCqForEventByOrder(cq.getRfxEvent().getId());
			model.addAttribute("cqList", cqList);
			model.addAttribute("rftCq", rftCq);
			model.addAttribute("event", rftEvent);
			model.addAttribute("eventId", cq.getRfxEvent().getId());
			model.addAttribute("eventPermissions", rftEventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), rftEvent.getId()));
			return "eventCqList";
		} catch (Exception e) {
			LOG.error("Error while deleting Cq  [ " + (cq != null ? cq.getName() : "") + " ]" + e.getMessage(), e);
			rftEvent = rftEventService.getRftEventById(cq.getRfxEvent().getId());
			model.addAttribute("error", messageSource.getMessage("rft.cq.error.delete", new Object[] { (cq != null ? cq.getName() : "") }, Global.LOCALE));
			List<RftCq> cqList = rftCqService.findRftCqForEventByOrder(cq.getRfxEvent().getId());
			model.addAttribute("cqList", cqList);
			model.addAttribute("rftCq", rftCq);
			model.addAttribute("event", rftEvent);
			model.addAttribute("eventId", cq.getRfxEvent().getId());
			model.addAttribute("eventPermissions", rftEventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), rftEvent.getId()));
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
		LOG.info("================" + cqId + "==============");
		if (StringUtils.checkString(cqId).length() == 0) {
			return "redirect:/400_error";
		}
		// RftCq rftCq = (RftCq) model.asMap().get("rftCq");
		RftCq rftCq = rftCqService.getRftCqById(cqId);
		LOG.info("CQ : " + rftCq.getId());
		List<RftCqItem> cqItemList = rftCqService.findRftCqbyCqId(cqId);

		String eventId = (String) model.asMap().get("eventId");

		if (eventId == null || eventId.length() == 0) {
			eventId = rftCq.getRfxEvent().getId();
		}
		if (eventId != null && eventId.length() > 0) {
			List<EventDocument> eventDocumentList = rftDocumentService.findAllRftEventDocsNameByEventId(eventId);
			model.addAttribute("eventDocumentList", eventDocumentList);
		}
		LOG.info("CQ ITEM LIST : " + cqItemList.size());
		for (RftCqItem item : cqItemList) {
			LOG.info("item Level : " + item.getLevel() + " Order : " + item.getOrder());
		}
		model.addAttribute("rftCq", rftCq);
		model.addAttribute("cqItemList", cqItemList);
		model.addAttribute("event", rftEventService.getRftEventById(rftCq.getRfxEvent().getId()));
		model.addAttribute("cqTypes", CqType.values());
		RftCqItem cqItem = new RftCqItem();
		cqItem.setCq(rftCq);
		model.addAttribute("cqItem", cqItem);
		model.addAttribute("eventPermissions", rftEventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), rftCq.getRfxEvent().getId()));
		return "eventCqItemList";
	}

	@RequestMapping(path = "/createRftCqItem", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<List<RftCqItem>> createRftCqItem(@RequestBody CqItemPojo rftCqItem) {
		HttpHeaders headers = new HttpHeaders();
		try {
			if (!doValidate(rftCqItem, headers)) {
				return new ResponseEntity<List<RftCqItem>>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
			}
			if (StringUtils.checkString(rftCqItem.getItemName()).length() > 0) {
				RftCqItem item = new RftCqItem();
				item.setItemName(rftCqItem.getItemName());
				item.setItemDescription(rftCqItem.getItemDescription());
				item.setIsSupplierAttachRequired(rftCqItem.getIsSupplierAttachRequired());
				if (rftCqService.isExists(item, rftCqItem.getCq(), rftCqItem.getParent())) {
					LOG.info("Duplicate....");
					throw new ApplicationException("Duplicate CQ Item. CQ Item by that name already exists.");
				}
				item.setRfxEvent(rftCqService.getRftEventById(rftCqItem.getRftEvent()));
				item.setCq(rftCqService.getRftCqById(rftCqItem.getCq()));
				if (StringUtils.checkString(rftCqItem.getCqType()).length() > 0) {
					item.setCqType(CqType.valueOf(rftCqItem.getCqType()));
				}
				item.setAttachment(rftCqItem.isAttachment());
				item.setOptional(rftCqItem.isOptional());
				if (CollectionUtil.isNotEmpty(rftCqItem.getOptions())) {
					List<RftCqOption> optionItems = new ArrayList<RftCqOption>();
					int optionOrder = 0;
					int totalScore = 0;
					for (String option : rftCqItem.getOptions()) {
						if (StringUtils.checkString(option).length() == 0)
							continue;
						RftCqOption options = new RftCqOption();
						options.setRftCqItem(item);
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
					item.setParent(rftCqService.getCqItembyCqItemId(StringUtils.checkString(rftCqItem.getParent())));
				}
				rftCqService.saveRftCqItem(item);

				List<RftCqItem> bqList = rftCqService.findRftCqbyCqId(rftCqItem.getCq());
				headers.add("success", messageSource.getMessage("buyer.rftcq.save", new Object[] {}, Global.LOCALE));

				return new ResponseEntity<List<RftCqItem>>(bqList, headers, HttpStatus.OK);
			} else {
				List<RftCqItem> bqList = rftCqService.findRftCqbyCqId(rftCqItem.getCq());
				headers.add("info", messageSource.getMessage("rft.cq.name.empty", new Object[] {}, Global.LOCALE));
				return new ResponseEntity<List<RftCqItem>>(bqList, headers, HttpStatus.OK);
			}
		} catch (Exception e) {
			LOG.error("Error while adding CQ Items to Event : " + e.getMessage(), e);
			headers.add("error", messageSource.getMessage("buyer.rftcq.error", new Object[] { e.getMessage() }, Global.LOCALE));
			return new ResponseEntity<List<RftCqItem>>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(path = "/getCqData", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<List<CqItemPojo>> getCqData(@RequestParam("itemId") String itemId) throws JsonProcessingException {
		try {

			if (StringUtils.checkString(itemId).length() > 0) {
				RftCqItem item = rftCqService.getCqItembyCqItemId(StringUtils.checkString(itemId));
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
	public @ResponseBody ResponseEntity<List<RftCqItem>> updateCqItem(@RequestBody CqItemPojo rftCqItem) throws JsonProcessingException {
		HttpHeaders headers = new HttpHeaders();
		List<RftCqItem> cqList = null;
		try {
			LOG.info(" :: UPDATE CQ ::" + rftCqItem.getId());
			LOG.info(" :: UPDATE CQ ::" + rftCqItem.toLogString());
			if (!doValidate(rftCqItem, headers)) {
				return new ResponseEntity<List<RftCqItem>>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
			}
			if (StringUtils.checkString(rftCqItem.getItemName()).length() > 0) {
				RftCqItem item = new RftCqItem();
				item.setId(rftCqItem.getId());
				item.setItemName(rftCqItem.getItemName());
				item.setItemDescription(rftCqItem.getItemDescription());
				item.setIsSupplierAttachRequired(rftCqItem.getIsSupplierAttachRequired());
				if (rftCqService.isExists(item, rftCqItem.getCq(), rftCqItem.getParent())) {
					LOG.info("Duplicate....");
					throw new ApplicationException("Duplicate CQ Item. CQ Item by that name already exists.");
				}
				rftCqService.updateCqItem(rftCqItem);
				cqList = rftCqService.findRftCqbyCqId(rftCqItem.getCq());
				headers.add("success", messageSource.getMessage("buyer.rftcq.update", new Object[] {}, Global.LOCALE));
				return new ResponseEntity<List<RftCqItem>>(cqList, headers, HttpStatus.OK);
			} else {
				cqList = rftCqService.findRftCqbyCqId(rftCqItem.getCq());
				headers.add("info", messageSource.getMessage("rft.cq.name.empty", new Object[] {}, Global.LOCALE));
				return new ResponseEntity<List<RftCqItem>>(cqList, headers, HttpStatus.OK);
			}

		} catch (Exception e) {
			LOG.error("Error while updating CQ Items to Buyer : " + e.getMessage(), e);
			headers.add("error", messageSource.getMessage("buyer.rftcq.error", new Object[] { e.getMessage() }, Global.LOCALE));
			cqList = rftCqService.findRftCqbyCqId(rftCqItem.getCq());
			return new ResponseEntity<List<RftCqItem>>(cqList, headers, HttpStatus.BAD_REQUEST);
		}
	}

	@RequestMapping(value = "/deleteCqItem/{cqId}", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<List<RftCqItem>> deleteCqItem(@RequestBody String[] items, @PathVariable("cqId") String cqId) throws JsonProcessingException {
		LOG.info("DELETE CQ ITEM REQUESTED : " + items + " cqId : " + cqId);
		List<RftCqItem> cqItems = null;
		HttpHeaders headers = new HttpHeaders();
		if (items != null && items.length > 0) {
			try {
				rftCqService.deleteCqItems(items, cqId);
				cqItems = rftCqService.findRftCqbyCqId(cqId);
				headers.add("errors", messageSource.getMessage("rfx.cq.success.delete", new Object[] {}, Global.LOCALE));
			} catch (Exception e) {
				LOG.error("Error while deleting cqItems : " + e.getMessage(), e);
				headers.add("errors", messageSource.getMessage("rft.cq.info.delete", new Object[] {}, Global.LOCALE));
				return new ResponseEntity<List<RftCqItem>>(null, headers, HttpStatus.BAD_REQUEST);
			}
		} else {
			LOG.error("Please select at least one Cq Item");
			headers.add("errors", messageSource.getMessage("rft.cq.error.delete.required", new Object[] {}, Global.LOCALE));
			return new ResponseEntity<List<RftCqItem>>(null, headers, HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<List<RftCqItem>>(cqItems, headers, HttpStatus.OK);
	}

	@RequestMapping(path = "/eventCqOrder", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<List<RftCqItem>> eventCqOrder(@RequestBody CqItemPojo rftCqItemPojo) throws JsonProcessingException {
		LOG.info("rftCqItemPojo : " + rftCqItemPojo.toLogString());
		HttpHeaders headers = new HttpHeaders();
		List<RftCqItem> cqList = null;
		try {
			if (StringUtils.checkString(rftCqItemPojo.getId()).length() > 0) {
				LOG.info("Updating order..........................");
				RftCqItem cqItem = rftCqService.getCqItembyCqItemId(rftCqItemPojo.getId());
				if (cqItem.getOrder() > 0 && StringUtils.checkString(rftCqItemPojo.getParent()).length() == 0) {
					LOG.info("child cannot be a parent");
					headers.add("error", messageSource.getMessage("rfx.child.reOrder.error", new Object[] {}, Global.LOCALE));
					return new ResponseEntity<List<RftCqItem>>(rftCqService.findRftCqbyCqId(rftCqService.getCqItembyCqItemId(rftCqItemPojo.getId()).getCq().getId()), headers, HttpStatus.BAD_REQUEST);
				}
				if (cqItem.getOrder() == 0 && StringUtils.checkString(rftCqItemPojo.getParent()).length() > 0) {
					LOG.info("parent cannot be a child");
					headers.add("error", messageSource.getMessage("rfx.parent.reOrder.error", new Object[] {}, Global.LOCALE));
					return new ResponseEntity<List<RftCqItem>>(rftCqService.findRftCqbyCqId(rftCqService.getCqItembyCqItemId(rftCqItemPojo.getId()).getCq().getId()), headers, HttpStatus.BAD_REQUEST);
				}
				RftCqItem item = new RftCqItem();
				item.setItemName(rftCqItemPojo.getItemName());
				if (cqItem.getParent() != null && !cqItem.getParent().getId().equals(rftCqItemPojo.getParent()) && rftCqService.isExists(item, rftCqItemPojo.getCq(), rftCqItemPojo.getParent())) {
					LOG.info("Duplicate....");
					throw new ApplicationException("Duplicate CQ Item. CQ Item by that name already exists.");
				}
				rftCqService.reorderCqItems(rftCqItemPojo);
				cqList = rftCqService.findRftCqbyCqId(rftCqService.getCqItembyCqItemId(rftCqItemPojo.getId()).getCq().getId());
				headers.add("success", messageSource.getMessage("buyer.rftbq.success", new Object[] {}, Global.LOCALE));
				return new ResponseEntity<List<RftCqItem>>(cqList, headers, HttpStatus.OK);
			}
		} catch (NotAllowedException | ApplicationException nae) {
			LOG.error("Error while moving CQ Items to Event : " + nae.getMessage(), nae);
			headers.add("error", nae.getMessage());
			return new ResponseEntity<List<RftCqItem>>(rftCqService.findRftCqbyCqId(rftCqService.getCqItembyCqItemId(rftCqItemPojo.getId()).getCq().getId()), headers, HttpStatus.BAD_REQUEST);

		} catch (Exception e) {
			LOG.error("Error while moving CQ Items to Event : " + e.getMessage(), e);
			// headers.add("error", "Error while adding Notes to Buyer " + e.getMessage());
			headers.add("error", messageSource.getMessage("buyer.rftcq.error", new Object[] { e.getMessage() }, Global.LOCALE));
			return new ResponseEntity<List<RftCqItem>>(null, headers, HttpStatus.BAD_REQUEST);
		}
		headers.add("success", messageSource.getMessage("buyer.rftbq.success", new Object[] {}, Global.LOCALE));
		return new ResponseEntity<List<RftCqItem>>(cqList, headers, HttpStatus.OK);
	}

	@RequestMapping(value = "/cqPrevious", method = RequestMethod.POST)
	public String cqPrevious(Model model, @RequestParam(name = "eventId", required = true) String eventId) throws Exception {
		try {
			RftEvent rftEvent = rftCqService.getRftEventById(eventId);
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
			if (eventId != null) {
				model.addAttribute("eventPermissions", rftEventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), eventId));
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
			RftEvent rftEvent = rftCqService.getRftEventById(eventId);
			if (rftEvent != null) {

				rftEvent.setCqCompleted(Boolean.TRUE);
				rftEventService.updateRftEvent(rftEvent);
				model.addAttribute("eventPermissions", rftEventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), eventId));
				if (Boolean.TRUE == rftEvent.getBillOfQuantity()) {
					return "redirect:createBQList/" + eventId;
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
		RftEvent rftEvent = rftCqService.getRftEventById(eventId);
		model.addAttribute("eventPermissions", rftEventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), rftEvent.getId()));
		redir.addFlashAttribute("success", messageSource.getMessage("rftEvent.success.savedraft", new Object[] { (rftEvent.getEventName() != null ? rftEvent.getEventName() : rftEvent.getEventId()) }, Global.LOCALE));
		return "redirect:eventCqList/" + rftEvent.getId();

	}

	@RequestMapping(path = "/cqItemTemplate/{cqId}", method = RequestMethod.GET)
	public void downloadCqExcel(@PathVariable String cqId, HttpServletRequest request, HttpServletResponse response) throws IOException {
		try {
			XSSFWorkbook workbook = new XSSFWorkbook();
			String downloadFolder = context.getRealPath("/WEB-INF/");
			String fileName = "CqItemTemplate.xlsx";
			Path file = Paths.get(downloadFolder, fileName);
			LOG.info("File Path ::" + file);

			super.eventDownloader(workbook, cqId, RfxTypes.RFT);

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
	public ResponseEntity<List<RftCqItem>> handleFormUpload(@RequestParam("file") MultipartFile file, @RequestParam("cqId") String cqId, @RequestParam("eventId") String eventId) throws IOException {
		LOG.info("UPLOADING STARTED ...... CQ Id :: " + cqId + "Event Id :: " + eventId);
		String message = null;
		List<RftCqItem> bqList = null;
		HttpHeaders headers = new HttpHeaders();
		try {
			if (!file.isEmpty()) {
				if (!file.getContentType().equals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet") && !file.getContentType().equals("application/wps-office.xlsx") && !file.getContentType().equals("application/vnd.ms-excel"))
					throw new MultipartException("Only excel files accepted!");
				try {
					message = super.cqItemsUpload(file, cqId, eventId, RfxTypes.RFT);

				} catch (Exception e) {
					e.printStackTrace();
					bqList = rftCqService.findRftCqbyCqId(cqId);
					headers.add("error", messageSource.getMessage("common.upload.fail", new Object[] { e.getMessage() }, Global.LOCALE));
					return new ResponseEntity<List<RftCqItem>>(bqList, headers, HttpStatus.BAD_REQUEST);
				}
			}
		} catch (MultipartException e) {
			e.printStackTrace();
			// LOG.info("Upload failed!" + e.getMessage());
			bqList = rftCqService.findRftCqbyCqId(cqId);
			headers.add("error", messageSource.getMessage("common.upload.notsupported.fail", new Object[] { e.getMessage() }, Global.LOCALE));
			return new ResponseEntity<List<RftCqItem>>(bqList, headers, HttpStatus.BAD_REQUEST);
		}
		// return "redirect:createEventBQ/" + eventId + "/" + cqId;
		bqList = rftCqService.findRftCqbyCqId(cqId);
		if (message == null) {
			headers.add("success", messageSource.getMessage("common.upload.success", new Object[] { "RFT CQ Items" }, Global.LOCALE));
		} else {
			headers.add("warn", messageSource.getMessage("common.upload.success", new Object[] { "RFT CQ Items" }, Global.LOCALE) + ", " + message);
		}
		return new ResponseEntity<List<RftCqItem>>(bqList, headers, HttpStatus.OK);
	}

	@RequestMapping(value = "/updateEventCqOrder/{eventId}", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<List<RftCq>> updateEventCqOrder(@RequestBody String[] cqIds, @PathVariable("eventId") String eventId) {
		try {
			LOG.info("RFT eventId : " + eventId);
			if (cqIds != null && cqIds.length > 0) {
				Integer count = 1;
				for (String cqId : cqIds) {
					RftCq cq = rftCqService.getRftCqById(cqId);
					cq.setCqOrder(count);
					rftCqService.updateCq(cq);
					count++;
				}
				List<RftCq> cqList = rftCqService.findRftCqForEventByOrder(eventId);
				HttpHeaders headers = new HttpHeaders();
				headers.add("success", messageSource.getMessage("rft.cq.order", new Object[] {}, Global.LOCALE));
				return new ResponseEntity<List<RftCq>>(cqList, headers, HttpStatus.OK);
			} else {
				List<RftCq> cqList = rftCqService.findRftCqForEventByOrder(eventId);
				HttpHeaders headers = new HttpHeaders();
				headers.add("error", messageSource.getMessage("rft.cq.order.empty", new Object[] {}, Global.LOCALE));
				return new ResponseEntity<List<RftCq>>(cqList, headers, HttpStatus.BAD_REQUEST);
			}
		} catch (Exception e) {
			LOG.error("Error while Ordering CQ : " + e.getMessage(), e);
			HttpHeaders headers = new HttpHeaders();
			headers.add("error", messageSource.getMessage("rft.bq.order.error", new Object[] { e.getMessage() }, Global.LOCALE));
			return new ResponseEntity<List<RftCq>>(null, headers, HttpStatus.BAD_REQUEST);
		}
	}
}
