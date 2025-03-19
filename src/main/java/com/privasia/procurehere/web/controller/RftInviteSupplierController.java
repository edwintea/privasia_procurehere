package com.privasia.procurehere.web.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
import com.privasia.procurehere.core.entity.EventSupplier;
import com.privasia.procurehere.core.entity.FavouriteSupplier;
import com.privasia.procurehere.core.entity.RftEvent;
import com.privasia.procurehere.core.entity.RftEventSupplier;
import com.privasia.procurehere.core.entity.Supplier;
import com.privasia.procurehere.core.enums.RfxTypes;
import com.privasia.procurehere.core.enums.SubmissionStatusType;
import com.privasia.procurehere.core.exceptions.ApplicationException;
import com.privasia.procurehere.core.pojo.EventSupplierPojo;
import com.privasia.procurehere.core.pojo.SupplierSearchPojo;
import com.privasia.procurehere.core.pojo.TableData;
import com.privasia.procurehere.core.pojo.TableDataInput;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.SecurityLibrary;
import com.privasia.procurehere.core.utils.StringUtils;

@Controller
@RequestMapping("/buyer/RFT")
public class RftInviteSupplierController extends EventInviteSupplierBase {

	private static final Logger LOG = LogManager.getLogger(Global.ADMIN_LOG);

	public RftInviteSupplierController() {
		super(RfxTypes.RFT);
	}

	@RequestMapping(path = "/eventSupplierPrevious", method = RequestMethod.POST)
	public String eventDescriptionPrevious(@ModelAttribute("event") RftEvent rftEvent, Model model, BindingResult result, RedirectAttributes redir) {
		rftEvent = eventService.getRftEventByeventId(rftEvent.getId());
		if (rftEvent != null) {
			model.addAttribute("eventPermissions", eventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), rftEvent.getId()));
			return doNavigationPrevious(rftEvent);
		} else {
			return "redirect:/login";
		}
	}

	@RequestMapping(value = "/eventSupplierNext", method = RequestMethod.POST)
	public String eventDocumentNext(@ModelAttribute("event") RftEvent rftEvent, Model model, BindingResult result) {
		rftEvent = eventService.getRftEventByeventId(rftEvent.getId());
		if (rftEvent != null) {
			model.addAttribute("eventPermissions", eventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), rftEvent.getId()));
			rftEvent.setSupplierCompleted(Boolean.TRUE);
			eventService.updateRftEvent(rftEvent);
			return doNavigation(rftEvent);
		} else {
			return "redirect:/login";
		}
	}

	@RequestMapping(value = "/addSupplier/{eventId}", method = RequestMethod.GET)
	public String addSupplier(@PathVariable String eventId, Model model) throws JsonProcessingException {
		// List<EventSupplier> eventSupplierList = rftEventSupplierService.getAllSuppliersByEventId(eventId);

		// super.setNullSupplierObject(eventSupplierList);

		RftEvent event = eventService.getRftEventByeventId(eventId);
		model.addAttribute("event", event);
		buildModel(model, null, RfxTypes.RFT, event);
		model.addAttribute("eventPermissions", eventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), eventId));
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
	public @ResponseBody ResponseEntity<List<EventSupplier>> addCurrentSuppliers(@RequestParam("addSupplier") String addSupplier, @RequestParam("eventId") String eventId, RftEventSupplier eventSupplier) {
		HttpHeaders headers = new HttpHeaders();
		FavouriteSupplier favSupp = favoriteSupplierService.findFavSupplierBySuppId(StringUtils.checkString(addSupplier), SecurityLibrary.getLoggedInUserTenantId());
		eventSupplier.setSupplier(favSupp.getSupplier());
		eventSupplier.setSupplierInvitedTime(new Date());
		eventSupplier.setRfxEvent(eventService.getPlainEventById(eventId));
		if (!doValidate(eventSupplier)) {
			eventSupplier.setSubmissionStatus(SubmissionStatusType.INVITED);
			rftEventSupplierService.saveRftEventSuppliers(eventSupplier);
			headers.add("success", "Event Supplier added successfully");
			List<EventSupplier> rftEventSupplierList = rftEventSupplierService.getAllSuppliersByEventId(eventId);
			if (CollectionUtil.isNotEmpty(rftEventSupplierList)) {
				for (EventSupplier eventSupp : rftEventSupplierList) {
					eventSupp.setAcceptedBy(null);
					eventSupp.setRejectedBy(null);
					eventSupp.setSubbmitedBy(null);
					eventSupp.setDisqualifiedBy(null);
					Supplier supplier = eventSupp.getSupplier();
					eventSupp.setSupplier(supplier.createShallowCopy());
				}
			}
			return new ResponseEntity<List<EventSupplier>>(rftEventSupplierList, headers, HttpStatus.OK);
		} else {
			LOG.info("Is Exist.....................................................");
			headers.add("errors", "Event Supplier already exists");
			List<EventSupplier> rftEventSupplierList = rftEventSupplierService.getAllSuppliersByEventId(eventId);
			if (CollectionUtil.isNotEmpty(rftEventSupplierList)) {
				for (EventSupplier eventSupp : rftEventSupplierList) {
					eventSupp.setAcceptedBy(null);
					eventSupp.setRejectedBy(null);
					eventSupp.setSubbmitedBy(null);
					eventSupp.setDisqualifiedBy(null);
					Supplier supplier = eventSupp.getSupplier();
					eventSupp.setSupplier(supplier.createShallowCopy());
				}
			}
			return new ResponseEntity<List<EventSupplier>>(rftEventSupplierList, headers, HttpStatus.BAD_REQUEST);
		}

	}

	@RequestMapping(value = "deleteRftSupplier", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<List<EventSupplier>> deleteRftSupplier(@RequestParam("deleteSupplier") String deleteSupplier, @RequestParam("eventId") String eventId) {
		HttpHeaders headers = new HttpHeaders();
		List<EventSupplier> eventSupplierList = null;
		try {
			eventSupplierList = super.removeSupplier(deleteSupplier, eventId);
			if (CollectionUtil.isNotEmpty(eventSupplierList)) {
				for (EventSupplier eventSupplier : eventSupplierList) {
					eventSupplier.setAcceptedBy(null);
					eventSupplier.setRejectedBy(null);
					eventSupplier.setSubbmitedBy(null);
					eventSupplier.setDisqualifiedBy(null);
					Supplier supplier = eventSupplier.getSupplier();
					eventSupplier.setSupplier(supplier.createShallowCopy());
				}
			}
			headers.add("success", "Event Supplier removed successfully");
		} catch (ApplicationException e) {
			LOG.error("Error while removing Supplier from event . " + e.getMessage(), e);
			headers.add("errors", e.getMessage());
			return new ResponseEntity<List<EventSupplier>>(eventSupplierList, headers, HttpStatus.BAD_REQUEST);
		} catch (Exception e) {
			LOG.error("Error while removing Supplier from event . " + e.getMessage(), e);
			headers.add("errors", "Event Supplier removed unsuccessfull");
			return new ResponseEntity<List<EventSupplier>>(eventSupplierList, headers, HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<List<EventSupplier>>(eventSupplierList, headers, HttpStatus.OK);

	}

	@RequestMapping(value = "/inviteSupplierSaveDraft", method = RequestMethod.POST)
	public String inviteSupplierSaveDraft(RedirectAttributes redir, @RequestParam("eventId") String eventId, Model model) {
		RftEvent rftEvent = eventService.getRftEventByeventId(eventId);
		model.addAttribute("eventPermissions", eventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), eventId));
		redir.addFlashAttribute("success", messageSource.getMessage("rftEvent.success.savedraft", new Object[] { (rftEvent.getEventName() != null ? rftEvent.getEventName() : rftEvent.getEventId()) }, Global.LOCALE));
		return "redirect:addSupplier/" + rftEvent.getId();

	}

	@RequestMapping(value = "/getEventSuppliers/{eventId}", method = RequestMethod.GET)
	public ResponseEntity<TableData<EventSupplierPojo>> buyerSuspendedList(TableDataInput input, @PathVariable("eventId") String eventId) {
		TableData<EventSupplierPojo> data = null;
		try {
			data = new TableData<EventSupplierPojo>(rftEventSupplierService.getAllEventsSupplierPojoByEventId(eventId, input));
			data.setDraw(input.getDraw());
			long totalCount = rftEventSupplierService.getAllEventsSupplierPojoCountByEventId(eventId, input);
			data.setRecordsTotal(totalCount);
			data.setRecordsFiltered(totalCount);
		} catch (Exception e) {
			LOG.error("Error while loading supplier list : " + e.getMessage(), e);
		}
		return new ResponseEntity<TableData<EventSupplierPojo>>(data, HttpStatus.OK);
	}

	@RequestMapping(value = "addSupplierList", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<List<EventSupplier>> addSupplierList(@RequestParam("addSupplier") String[] addSupplier, @RequestParam("eventId") String eventId, @RequestParam(required = false, value = "country") String country, @RequestParam(required = false, value = "state") String state, @RequestParam(required = false, value = "supplierTagName") String supplierTagName, Boolean select_all, Boolean exclusive, Boolean inclusive, RftEventSupplier eventSupplier) {
		List<EventSupplier> eventSupplierList = rftEventSupplierService.getAllSuppliersByEventId(eventId);
		HttpHeaders headers = new HttpHeaders();
		RftEvent rfteventId = rftEventService.getRftEventByeventId(eventId);
		List<FavouriteSupplier> favouriteSupplier = new ArrayList<FavouriteSupplier>();
		RftEvent rftEvent = (RftEvent) rfteventId;
		SupplierSearchPojo supplierSearchPojo = new SupplierSearchPojo();
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
				if (rftEvent.getTemplate() != null && Boolean.TRUE == rftEvent.getTemplate().getSupplierBasedOnCategory()) {
					favouriteSupplier = favoriteSupplierService.findInvitedSupplierByIndCat(StringUtils.checkString(favSupplier), select_all, supplierSearchPojo, SecurityLibrary.getLoggedInUserTenantId(), rftEvent.getIndustryCategories(), exclusive, inclusive);
				} else {
					favouriteSupplier = favoriteSupplierService.findInvitedSupplierBySuppId(StringUtils.checkString(favSupplier), select_all, supplierSearchPojo, SecurityLibrary.getLoggedInUserTenantId(), exclusive, inclusive);
				}
				Set<FavouriteSupplier> favSupp1 = new HashSet<>(favouriteSupplier);
				for (FavouriteSupplier favSupp : favSupp1) {
					eventSupplier.setSupplier(favSupp.getSupplier());
					eventSupplier.setSupplierInvitedTime(new Date());
					eventSupplier.setRfxEvent(eventService.getRftEventByeventId(eventId));
					if (!doValidate(eventSupplier)) {
						eventSupplier.setSubmissionStatus(SubmissionStatusType.INVITED);
						EventSupplier eventFavSupplier = rftEventSupplierService.saveInvitedSuppliers(eventSupplier);
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