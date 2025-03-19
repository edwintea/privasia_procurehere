package com.privasia.procurehere.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.privasia.procurehere.core.enums.RfxTypes;
import com.privasia.procurehere.core.utils.SecurityLibrary;
import com.privasia.procurehere.service.RfqEnvelopService;

@Controller
@RequestMapping("/buyer/RFQ")
public class RfqEventProgress extends EventProgress {
	
	@Autowired
	RfqEnvelopService rfqEnvelopService;

	public RfqEventProgress() {
		super(RfxTypes.RFQ);
	}
	@RequestMapping(path = "/eventProgress/{eventId}", method = RequestMethod.GET)
	public String getRftEventProgress(@PathVariable String eventId, Model model) {
	//	model.addAttribute("eventPermissions", rfqEventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), eventId));
		model.addAttribute("envelopListCount", rfqEnvelopService.getAllEnvelopCountByEventId(eventId));
		return super.getProgressDetails(model, eventId, RfxTypes.RFQ);
	}

}
