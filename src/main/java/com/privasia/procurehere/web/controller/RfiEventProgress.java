package com.privasia.procurehere.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.privasia.procurehere.core.enums.RfxTypes;
import com.privasia.procurehere.core.utils.SecurityLibrary;
import com.privasia.procurehere.service.RfiEnvelopService;

@Controller
@RequestMapping("/buyer/RFI")
public class RfiEventProgress extends EventProgress {
	@Autowired
	RfiEnvelopService rfiEnvelopService;

	public RfiEventProgress() {
		super(RfxTypes.RFI);
	}

	@RequestMapping(path = "/eventProgress/{eventId}", method = RequestMethod.GET)
	public String getRftEventProgress(@PathVariable String eventId, Model model) {
		//model.addAttribute("eventPermissions", rfiEventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), eventId));

		model.addAttribute("envelopListCount", rfiEnvelopService.getAllEnvelopCountByEventId(eventId));
		return super.getProgressDetails(model, eventId, RfxTypes.RFI);
	}

}
