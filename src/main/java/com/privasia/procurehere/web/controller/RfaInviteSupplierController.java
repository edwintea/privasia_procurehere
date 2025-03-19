package com.privasia.procurehere.web.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.privasia.procurehere.core.entity.AuctionRules;
import com.privasia.procurehere.core.entity.EventSupplier;
import com.privasia.procurehere.core.entity.FavouriteSupplier;
import com.privasia.procurehere.core.entity.RfaEvent;
import com.privasia.procurehere.core.entity.RfaEventSupplier;
import com.privasia.procurehere.core.entity.RfaSupplierBq;
import com.privasia.procurehere.core.entity.Supplier;
import com.privasia.procurehere.core.enums.PreBidByType;
import com.privasia.procurehere.core.enums.RfxTypes;
import com.privasia.procurehere.core.enums.SubmissionStatusType;
import com.privasia.procurehere.core.pojo.EventSupplierPojo;
import com.privasia.procurehere.core.pojo.SupplierSearchPojo;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.SecurityLibrary;
import com.privasia.procurehere.core.utils.StringUtils;
import com.privasia.procurehere.service.RfaBqService;
import com.privasia.procurehere.service.RfaEventService;
import com.privasia.procurehere.service.RfaSupplierBqService;

@Controller
@RequestMapping("/buyer/RFA")
public class RfaInviteSupplierController extends EventInviteSupplierBase {

	private static final Logger LOG = LogManager.getLogger(Global.ADMIN_LOG);

	@Autowired
	RfaEventService eventService;

	@Autowired
	RfaSupplierBqService rfaSupplierBqService;

	@Autowired
	RfaBqService rfaBqService;

	public RfaInviteSupplierController() {
		super(RfxTypes.RFA);
	}

	@RequestMapping(path = "/eventSupplierPrevious", method = RequestMethod.POST)
	public String eventDescriptionPrevious(@ModelAttribute("event") RfaEvent rfaEvent, Model model, BindingResult result, RedirectAttributes redir) {
		rfaEvent = eventService.getRfaEventByeventId(rfaEvent.getId());
		if (rfaEvent != null) {
			model.addAttribute("eventPermissions", eventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), rfaEvent.getId()));
			if (rfaEvent.getDocumentReq()) {
				return "redirect:createEventDocuments/" + rfaEvent.getId();
			} else {
				return "redirect:auctionRules/" + rfaEvent.getId();
			}
		} else {
			return "redirect:/login";
		}
	}

	@RequestMapping(value = "/eventSupplierNext", method = RequestMethod.POST)
	public String eventDocumentNext(@ModelAttribute("event") RfaEvent rfaEvent, Model model, BindingResult result) {
		rfaEvent = eventService.getRfaEventByeventId(rfaEvent.getId());
		if (rfaEvent != null) {
			model.addAttribute("eventPermissions", eventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), rfaEvent.getId()));
			rfaEvent.setSupplierCompleted(Boolean.TRUE);
			eventService.updateRfaEvent(rfaEvent);
			return doNavigation(rfaEvent);
		} else {
			return "redirect:/login";
		}
	}

	@RequestMapping(value = "/addSupplier/{eventId}", method = RequestMethod.GET)
	public String addSupplier(@PathVariable String eventId, Model model) throws JsonProcessingException {
		List<EventSupplier> eventSupplierList = rfaEventSupplierService.getAllSuppliersByEventId(eventId);
		/*
		 * if(CollectionUtil.isNotEmpty(eventSupplierList)){ for(EventSupplier supplier : eventSupplierList){
		 * supplier.setAcceptedBy(null); supplier.setRejectedBy(null); supplier.setSubbmitedBy(null); } }
		 */
		super.setNullSupplierObject(eventSupplierList);
		RfaEvent event = eventService.loadRfaEventById(eventId);
		LOG.info("addSupplier...........................");
		buildModel(model, eventSupplierList, RfxTypes.RFA, event);
		model.addAttribute("eventPermissions", eventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), eventId));
		model.addAttribute("event", event);
		return "addSupplier";
	}

	/**
	 * @param searchSupplier
	 * @return
	 */
	@RequestMapping(value = "searchCurrentSuppliers", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<List<EventSupplierPojo>> searchCurrentSuppliers(@RequestParam("searchSupplier") String searchSupplier, @RequestParam("eventId") String eventId) {
		return new ResponseEntity<List<EventSupplierPojo>>(searchSuppliers(searchSupplier, eventId), HttpStatus.OK);
	}

	@RequestMapping(value = "addCurrentSuppliers", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<List<EventSupplier>> addCurrentSuppliers(@RequestParam("addSupplier") String addSupplier, @RequestParam("eventId") String eventId, RfaEventSupplier eventSupplier) {

		LOG.info(" inside addCurrentSuppliers in controller:     " + addSupplier);
		HttpHeaders headers = new HttpHeaders();
		FavouriteSupplier favSupp = favoriteSupplierService.findFavSupplierBySuppId(StringUtils.checkString(addSupplier), SecurityLibrary.getLoggedInUserTenantId());
		eventSupplier.setSupplier(favSupp.getSupplier());
		eventSupplier.setSupplierInvitedTime(new Date());
		eventSupplier.setRfxEvent(eventService.getPlainEventById(eventId));
		if (!doValidate(eventSupplier)) {
			eventSupplier.setSubmissionStatus(SubmissionStatusType.INVITED);
			RfaEventSupplier eventSupplierDetails = rfaEventSupplierService.saveRfaEventSuppliers(eventSupplier);
			try {
				AuctionRules auctionRules = rfaEventService.getAuctionRulesByEventId(eventId);
				if (auctionRules != null && auctionRules.getPreBidBy() == PreBidByType.BUYER && auctionRules.getPreSetSamePreBidForAllSuppliers()) {
					List<RfaSupplierBq> supplierBqList = rfaSupplierBqService.findRfaSummarySupplierBqbyEventId(eventId);
					if (CollectionUtil.isNotEmpty(supplierBqList)) {
						LOG.info("Saving pre bid for new Supplier");
						rfaSupplierBqService.saveSupplierPreBidBqDetails(supplierBqList.get(0).getId(), eventSupplierDetails);
					}
				}
			} catch (Exception e) {
				LOG.error("Error while saving in pre bid:" + e.getMessage(), e);
			}
			headers.add("success", "Event Supplier added successfully");
			List<EventSupplier> eventSupplierList = rfaEventSupplierService.getAllSuppliersByEventId(eventId);
			if (CollectionUtil.isNotEmpty(eventSupplierList)) {
				for (EventSupplier eventSupp : eventSupplierList) {
					eventSupp.setAcceptedBy(null);
					eventSupp.setRejectedBy(null);
					eventSupp.setSubbmitedBy(null);
					eventSupp.setDisqualifiedBy(null);
					Supplier supplier = eventSupp.getSupplier();
					eventSupp.setSupplier(supplier.createShallowCopy());
				}
			}
			return new ResponseEntity<List<EventSupplier>>(eventSupplierList, headers, HttpStatus.OK);
		} else {
			headers.add("errors", "Event Supplier already exists");
			List<EventSupplier> eventSupplierList = rfaEventSupplierService.getAllSuppliersByEventId(eventId);
			if (CollectionUtil.isNotEmpty(eventSupplierList)) {
				for (EventSupplier eventSupp : eventSupplierList) {
					eventSupp.setAcceptedBy(null);
					eventSupp.setRejectedBy(null);
					eventSupp.setSubbmitedBy(null);
					eventSupp.setDisqualifiedBy(null);
					Supplier supplier = eventSupp.getSupplier();
					eventSupp.setSupplier(supplier.createShallowCopy());
				}
			}
			return new ResponseEntity<List<EventSupplier>>(eventSupplierList, headers, HttpStatus.BAD_REQUEST);
		}

	}

	@RequestMapping(value = "deleteRftSupplier", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<List<EventSupplier>> deleteRftSupplier(@RequestParam("deleteSupplier") String deleteSupplier, @RequestParam("eventId") String eventId) {
		HttpHeaders headers = new HttpHeaders();
		List<EventSupplier> eventSupplierList = null;
		try {
			LOG.info("EVent Supplier ID : " + deleteSupplier);
			// RfaSupplierBq rfaSupplierBq = rfaSupplierBqService.getRfaSupplierBqByEventIdAndSupplierId(eventId,
			// supplierId);
			// LOG.info("Rfa Supplier BQ : " + rfaSupplierBq.getName());
			// rfaSupplierBqService.deleteRfaSupplierBq(eventId, supplierId);
			eventSupplierList = super.removeSupplier(deleteSupplier, eventId);
			if (CollectionUtil.isNotEmpty(eventSupplierList)) {
				for (EventSupplier eventSupp : eventSupplierList) {
					eventSupp.setAcceptedBy(null);
					eventSupp.setRejectedBy(null);
					eventSupp.setSubbmitedBy(null);
					eventSupp.setDisqualifiedBy(null);
					Supplier supplier = eventSupp.getSupplier();
					eventSupp.setSupplier(supplier.createShallowCopy());
				}
			}
			headers.add("success", "Event Supplier removed successfully");
		} catch (Exception e) {
			LOG.error("Error while removing Supplier from event ." + e.getMessage(), e);
			headers.add("errors", "Event Supplier removed unsuccessfull :-" + e.getMessage());
			return new ResponseEntity<List<EventSupplier>>(eventSupplierList, headers, HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<List<EventSupplier>>(eventSupplierList, headers, HttpStatus.OK);

	}

	@RequestMapping(value = "/inviteSupplierSaveDraft", method = RequestMethod.POST)
	public String inviteSupplierSaveDraft(RedirectAttributes redir, @RequestParam("eventId") String eventId, Model model) {
		RfaEvent rfaEvent = eventService.getRfaEventByeventId(eventId);
		model.addAttribute("eventPermissions", eventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), eventId));
		redir.addFlashAttribute("success", messageSource.getMessage("rftEvent.success.savedraft", new Object[] { (rfaEvent.getEventName() != null ? rfaEvent.getEventName() : rfaEvent.getEventId()) }, Global.LOCALE));
		return "redirect:addSupplier/" + rfaEvent.getId();

	}

	@RequestMapping(value = "addSupplierList", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<List<EventSupplier>> addSupplierList(@RequestParam("addSupplier") String[] addSupplier, @RequestParam("eventId") String eventId, @RequestParam(required = false, value = "country") String country, @RequestParam(required = false, value = "state") String state, @RequestParam(required = false, value = "supplierTagName") String supplierTagName, Boolean select_all, Boolean exclusive, Boolean inclusive, RfaEventSupplier eventSupplier) {
		List<EventSupplier> eventSupplierList = rfaEventSupplierService.getAllSuppliersByEventId(eventId);
		HttpHeaders headers = new HttpHeaders();
		RfaEvent rfaeventId = rfaEventService.loadRfaEventById(eventId);
		RfaEvent rfaEvent = (RfaEvent) rfaeventId;
		SupplierSearchPojo supplierSearchPojo = new SupplierSearchPojo();
		List<FavouriteSupplier> favouriteSupplier = new ArrayList<FavouriteSupplier>();
		supplierSearchPojo.setRegistrationOfCountry(country);
		String[] states = null;
		if (StringUtils.checkString(state).length() > 0) {

			states = state.split(",");
		}
		supplierSearchPojo.setState(states);
		String[] supplierTags = null;
		if (StringUtils.checkString(supplierTagName).length() > 0) {

			supplierTags = supplierTagName.split(",");
		}
		supplierSearchPojo.setSupplierTagName(supplierTags);
		if ((Boolean.FALSE == inclusive) && (Boolean.FALSE == exclusive)) {
			inclusive = Boolean.TRUE;
		}
		try {
			for (String favSupplier : addSupplier) {
				if (rfaEvent.getTemplate() != null && Boolean.TRUE == rfaEvent.getTemplate().getSupplierBasedOnCategory()) {
					favouriteSupplier = favoriteSupplierService.findInvitedSupplierByIndCat(StringUtils.checkString(favSupplier), select_all, supplierSearchPojo, SecurityLibrary.getLoggedInUserTenantId(), rfaEvent.getIndustryCategories(), exclusive, inclusive);
				} else {
					favouriteSupplier = favoriteSupplierService.findInvitedSupplierBySuppId(StringUtils.checkString(favSupplier), select_all, supplierSearchPojo, SecurityLibrary.getLoggedInUserTenantId(), exclusive, inclusive);
				}
				Set<FavouriteSupplier> favSupp1 = new HashSet<>(favouriteSupplier);
				for (FavouriteSupplier favSupp : favSupp1) {
					eventSupplier.setSupplier(favSupp.getSupplier());
					eventSupplier.setSupplierInvitedTime(new Date());
					eventSupplier.setRfxEvent(eventService.getRfaEventByeventId(eventId));
					if (!doValidate(eventSupplier)) {
						eventSupplier.setSubmissionStatus(SubmissionStatusType.INVITED);
						EventSupplier eventFavSupplier = rfaEventSupplierService.saveInvitedSuppliers(eventSupplier);
						eventSupplierList.add(eventFavSupplier);
					}
				}
			}
			headers.add("success", "Event Supplier added successfully");
			return new ResponseEntity<List<EventSupplier>>(eventSupplierList, headers, HttpStatus.OK);
		} catch (Exception e) {
			LOG.error("Error while Added Supplier from event . " + e.getMessage(), e);
			return new ResponseEntity<List<EventSupplier>>(eventSupplierList, headers, HttpStatus.BAD_REQUEST);
		}
	}

}