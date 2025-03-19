package com.privasia.procurehere.web.controller;

import java.util.List;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.privasia.procurehere.core.entity.AuctionRules;
import com.privasia.procurehere.core.entity.Event;
import com.privasia.procurehere.core.entity.EventSupplier;
import com.privasia.procurehere.core.entity.RfaEventSupplier;
import com.privasia.procurehere.core.enums.EventStatus;
import com.privasia.procurehere.core.enums.RfxTypes;
import com.privasia.procurehere.core.enums.SubmissionStatusType;
import com.privasia.procurehere.core.pojo.EventPermissions;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.SecurityLibrary;
import com.privasia.procurehere.core.utils.StringUtils;
import com.privasia.procurehere.service.RfaEventService;
import com.privasia.procurehere.service.RfaEventSupplierService;
import com.privasia.procurehere.service.RfiEventService;
import com.privasia.procurehere.service.RfiEventSupplierService;
import com.privasia.procurehere.service.RfpEventService;
import com.privasia.procurehere.service.RfpEventSupplierService;
import com.privasia.procurehere.service.RfqEventService;
import com.privasia.procurehere.service.RfqEventSupplierService;
import com.privasia.procurehere.service.RftEventService;
import com.privasia.procurehere.service.RftEventSupplierService;

public class EventProgress {

	protected static final Logger LOG = LogManager.getLogger(Global.BUYER_LOG);

	@Autowired
	RftEventSupplierService rftEventSupplierService;

	@Autowired
	RfqEventSupplierService rfqEventSupplierService;

	@Autowired
	RfaEventSupplierService rfaEventSupplierService;

	@Autowired
	RfiEventSupplierService rfiEventSupplierService;

	@Autowired
	RfpEventSupplierService rfpEventSupplierService;

	@Autowired
	RftEventService rftEventService;

	@Autowired
	RfqEventService rfqEventService;

	@Autowired
	RfaEventService rfaEventService;

	@Autowired
	RfiEventService rfiEventService;

	@Autowired
	RfpEventService rfpEventService;

	private RfxTypes eventType;

	@ModelAttribute("eventType")
	public RfxTypes getEventType() {
		return eventType;
	}

	public EventProgress(RfxTypes eventType) {
		this.eventType = eventType;
	}

	/**
	 * @param model
	 * @param eventId
	 * @param eventType
	 * @return
	 */
	public String getProgressDetails(Model model, String eventId, RfxTypes eventType) {
		if (StringUtils.checkString(eventId).length() == 0) {
			return "redirect:/400_error";
		}
		EventPermissions eventPermissions = null;
		Event event = null;
		List<EventSupplier> supplierList = null;
		AuctionRules auctionRules = null;
		try {
			switch (eventType) {
			case RFA:
				event = rfaEventService.loadRfaEventById(eventId);
				supplierList = rfaEventSupplierService.getAllSuppliersByEventId(eventId);
				auctionRules = rfaEventService.getAuctionRulesByEventId(eventId);
				eventPermissions = rfaEventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), eventId);
				break;
			case RFI:
				event = rfiEventService.loadRfiEventById(eventId);
				supplierList = rfiEventSupplierService.getAllSuppliersByEventId(eventId);
				eventPermissions = rfiEventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), eventId);
				break;
			case RFP:
				event = rfpEventService.loadRfpEventById(eventId);
				supplierList = rfpEventSupplierService.getAllSuppliersByEventId(eventId);
				eventPermissions = rfpEventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), eventId);
				break;
			case RFQ:
				event = rfqEventService.loadRfqEventById(eventId);
				supplierList = rfqEventSupplierService.getAllSuppliersByEventId(eventId);
				eventPermissions = rfqEventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), eventId);
				break;
			case RFT:
				event = rftEventService.loadRftEventById(eventId);
				supplierList = rftEventSupplierService.getAllSuppliersByEventId(eventId);
				eventPermissions = rftEventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), eventId);
				break;
			default:
				break;

			}
			if (eventPermissions != null && event != null && !event.getViewSupplerName() && (event.getStatus() == EventStatus.CLOSED || event.getStatus() == EventStatus.COMPLETE || event.getStatus() == EventStatus.FINISHED)) {
				if (!eventPermissions.isOwner() && !eventPermissions.isApprover() && !eventPermissions.isViewer() && !eventPermissions.isEditor()) {
					return "redirect:/buyer/" + eventType.name() + "/viewSummary/" + eventId;
				}
			}
			model.addAttribute("eventPermissions", eventPermissions);
			int supplierCount = 0, acceptedCnt = 0, submittedCnt = 0, supPreviewCnt = 0, revisedSubmittedCnt = 0;

			for (EventSupplier sup : supplierList) {
				supplierCount++;

				if (sup.getSupplierEventReadTime() != null && sup.getSubmissionStatus() != SubmissionStatusType.REJECTED) {
					acceptedCnt++;
				}
				if (sup.getSubmitted() != null && sup.getSubmitted() == true) {
					submittedCnt++;
				}
				if (sup.getPreviewTime() != null) {
					supPreviewCnt++;
				}
				if (eventType == RfxTypes.RFA && ((RfaEventSupplier) sup).getRevisedBidSubmitted()) {
					revisedSubmittedCnt++;
				}
			}
			// LOG.info("Supplier Count :: " + supplierCount + " Accepted Cnt :: "+ acceptedCnt + " submittedCnt :: " +
			// submittedCnt + " supPreviewCnt :: "+ supPreviewCnt);
			model.addAttribute("event", event);
			model.addAttribute("supplierList", supplierList);
			model.addAttribute("supplierCount", supplierCount);
			model.addAttribute("acceptedCnt", acceptedCnt);
			model.addAttribute("submittedCnt", submittedCnt);
			model.addAttribute("revisedSubmittedCnt", revisedSubmittedCnt);
			model.addAttribute("supPreviewCnt", supPreviewCnt);
			model.addAttribute("auctionRules", auctionRules);
			model.addAttribute("showProgressTab", true);


			if (eventPermissions != null && event != null && (event.getStatus() == EventStatus.CLOSED || event.getStatus() == EventStatus.COMPLETE || event.getStatus() == EventStatus.ACTIVE || event.getStatus() == EventStatus.CANCELED || event.getStatus() == EventStatus.SUSPENDED)) {
				if (eventPermissions.isApprover() ) {
					return "redirect:/buyer/" + eventType.name() + "/viewSummary/" + eventId;
				}
			}

		} catch (Exception e) {
			LOG.error("Error while Get Progress Details :" + e.getMessage(), e);
			// TODO: handle exception
		}
		return "eventProgress";
	}
}
