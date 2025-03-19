package com.privasia.procurehere.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.privasia.procurehere.core.enums.RfxTypes;
import com.privasia.procurehere.core.utils.SecurityLibrary;
import com.privasia.procurehere.service.RfpEnvelopService;

@Controller
@RequestMapping("/buyer/RFP")
public class RfpEventProgress extends EventProgress {
	
	@Autowired
	RfpEnvelopService rfpEnvelopService;

	public RfpEventProgress() {
		super(RfxTypes.RFP);
	}

	@RequestMapping(path = "/eventProgress/{eventId}", method = RequestMethod.GET)
	public String getRftEventProgress(@PathVariable String eventId, Model model) {
	//	model.addAttribute("eventPermissions", rfpEventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), eventId));
		
		model.addAttribute("envelopListCount", rfpEnvelopService.getAllEnvelopCountByEventId(eventId));
		return super.getProgressDetails(model, eventId, RfxTypes.RFP);
	}

}
