package com.privasia.procurehere.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.privasia.procurehere.core.enums.RfxTypes;

@Controller
@RequestMapping("/buyer/RFT")
public class RftEventProgress extends EventProgress {

	public RftEventProgress() {
		super(RfxTypes.RFT);
	}

	@RequestMapping(path = "/eventProgress/{eventId}", method = RequestMethod.GET)
	public String getRftEventProgress(@PathVariable String eventId, Model model) {
		return super.getProgressDetails(model, eventId, RfxTypes.RFT);
	}

}
