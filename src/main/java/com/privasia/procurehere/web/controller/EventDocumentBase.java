package com.privasia.procurehere.web.controller;

import javax.annotation.Resource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.context.MessageSource;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.multipart.support.ByteArrayMultipartFileEditor;
import org.springframework.web.servlet.ModelAndView;

import com.privasia.procurehere.core.entity.Event;
import com.privasia.procurehere.core.entity.OwnerSettings;
import com.privasia.procurehere.core.enums.EventVisibilityType;
import com.privasia.procurehere.core.enums.RfxTypes;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.service.OwnerSettingsService;
import com.privasia.procurehere.service.RfiDocumentService;
import com.privasia.procurehere.service.RfpDocumentService;
import com.privasia.procurehere.service.RfqDocumentService;
import com.privasia.procurehere.service.RftDocumentService;

public class EventDocumentBase {

	protected static final Logger LOG = LogManager.getLogger(Global.ADMIN_LOG);

	@Resource
	MessageSource messageSource; 

	@Autowired
	RfiDocumentService rfiDocumentService;

	@Autowired
	RftDocumentService rftDocumentService;

	@Autowired
	RfpDocumentService rfpDocumentService;

	@Autowired
	RfqDocumentService rfqDocumentService;

	@Autowired
	OwnerSettingsService ownerSettingsService;

	private RfxTypes eventType;

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		StringTrimmerEditor stringtrimmer = new StringTrimmerEditor(true);
		binder.registerCustomEditor(String.class, stringtrimmer);
		binder.registerCustomEditor(byte[].class, new ByteArrayMultipartFileEditor());

	}

	public EventDocumentBase(RfxTypes eventType) {
		this.eventType = eventType;
	}

	@ModelAttribute("eventType")
	public RfxTypes getEventType() {
		return eventType;
	}

	/**
	 * @param eventType the eventType to set
	 */
	public void setEventType(RfxTypes eventType) {
		this.eventType = eventType;
	}

	@ModelAttribute("step")
	public String getStep() {
		return "2";
	}

	@ModelAttribute("ownerSettings")
	public OwnerSettings getOwnersettings() {
		return ownerSettingsService.getOwnersettings();
	}

	/**
	 * @param rftEvent
	 * @return
	 */
	public ModelAndView eventDocumentNext(Event rftEvent) {
		if (rftEvent.getEventVisibility() != EventVisibilityType.PUBLIC) {
			return new ModelAndView("redirect:addSupplier/" + rftEvent.getId());
		} else if (Boolean.TRUE == rftEvent.getMeetingReq()) {
			return new ModelAndView("redirect:meetingList/" + rftEvent.getId());
		} else if (Boolean.TRUE == rftEvent.getQuestionnaires()) {
			return new ModelAndView("redirect:eventCqList/" + rftEvent.getId());
		} else if (Boolean.TRUE == rftEvent.getBillOfQuantity()) {
			return new ModelAndView("redirect:createBQList/" + rftEvent.getId());
		} else {
			return new ModelAndView("redirect:envelopList/" + rftEvent.getId());
		}
	}

}