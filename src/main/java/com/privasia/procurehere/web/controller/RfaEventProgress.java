package com.privasia.procurehere.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.privasia.procurehere.core.enums.RfxTypes;
import com.privasia.procurehere.core.utils.SecurityLibrary;
import com.privasia.procurehere.service.RfaEnvelopService;
import com.privasia.procurehere.service.RfaEventService;

@Controller
@RequestMapping("/buyer/RFA")
public class RfaEventProgress extends EventProgress {

	@Autowired
	RfaEventService rfaEventService;
	
	@Autowired
	RfaEnvelopService rfaEnvelopService;

	public RfaEventProgress() {
		super(RfxTypes.RFA);
	}

	@RequestMapping(path = "/eventProgress/{eventId}", method = RequestMethod.GET)
	public String getRftEventProgress(@PathVariable String eventId, Model model) {
		model.addAttribute("envelopListCount", rfaEnvelopService.getAllEnvelopCountByEventId(eventId));
		return super.getProgressDetails(model, eventId, RfxTypes.RFA);
	}

}
