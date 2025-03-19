package com.privasia.procurehere.web.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.validation.Valid;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.privasia.procurehere.core.entity.Country;
import com.privasia.procurehere.core.entity.TimeZone;
import com.privasia.procurehere.core.enums.Status;
import com.privasia.procurehere.core.pojo.TableData;
import com.privasia.procurehere.core.pojo.TableDataInput;
import com.privasia.procurehere.core.pojo.TimeZonePojo;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.SecurityLibrary;
import com.privasia.procurehere.core.utils.StringUtils;
import com.privasia.procurehere.service.CountryService;
import com.privasia.procurehere.service.TimeZoneService;
import com.privasia.procurehere.web.editors.CountryEditor;

@Controller
@RequestMapping("/admin")
public class TimeZoneController {

	private static final Logger LOG = LogManager.getLogger(Global.ADMIN_LOG);

	@Autowired
	TimeZoneService timeZoneService;

	@Autowired
	CountryService countryService;

	@Autowired
	CountryEditor countryEditor;

	@Autowired
	MessageSource messageSource;

	@ModelAttribute("statusList")
	public List<Status> getStatusList() {
		return Arrays.asList(Status.values());
	}

	@RequestMapping(path = "/timeZone", method = RequestMethod.GET)
	public ModelAndView createTimeZone(@ModelAttribute TimeZone timeZone, Model model) {
		LOG.info("Create TimeZone called");
		model.addAttribute("timeZone", new TimeZone());
		model.addAttribute("country", countryService.getAllCountries());
		model.addAttribute("btnValue", "Create");
		return new ModelAndView("timeZone", "timeZoneObj", new TimeZone());
	}

	@RequestMapping(path = "/timeZone", method = RequestMethod.POST)
	public ModelAndView saveTimeZone(@Valid @ModelAttribute("timeZoneObj") TimeZone timeZone, BindingResult result, Model model, RedirectAttributes redir) {
		LOG.info("Time Zone Save Called : ");
		List<String> errMessages = new ArrayList<String>();
		try {
			if (result.hasErrors()) {
				for (ObjectError oe : result.getAllErrors()) {
					errMessages.add(oe.getDefaultMessage());
				}
			} else {

				if (doValidate(timeZone) == true) {
					if (StringUtils.checkString(timeZone.getId()).length() == 0) {

						timeZone.setCreatedBy(SecurityLibrary.getLoggedInUser());
						timeZone.setCreatedDate(new Date());
						timeZoneService.createTimeZone(timeZone);
						redir.addFlashAttribute("success", messageSource.getMessage("timezone.save.success", new Object[] { timeZone.getTimeZone() }, Global.LOCALE));
						LOG.info("create TimeZOne Called" + SecurityLibrary.getLoggedInUser());
					} else {

						TimeZone persistObj = timeZoneService.getTimeZoneById(timeZone.getId());

						persistObj.setTimeZone(timeZone.getTimeZone());
						persistObj.setTimeZoneDescription(timeZone.getTimeZoneDescription());
						persistObj.setStatus(timeZone.getStatus());
						persistObj.setCountry(timeZone.getCountry());
						persistObj.setModifiedBy(SecurityLibrary.getLoggedInUser());
						persistObj.setModifiedDate(new Date());
						timeZoneService.updateTimeZone(persistObj);
						redir.addFlashAttribute("success", messageSource.getMessage("timezone.update.success", new Object[] { timeZone.getTimeZone() }, Global.LOCALE));
						LOG.info("update TimeZone Called");
					}
				} else {
					model.addAttribute("error", messageSource.getMessage("timezone.error.duplicate", new Object[] { timeZone.getTimeZone() }, Global.LOCALE));
					model.addAttribute("btnValue", "Create");
					return new ModelAndView("timeZone", "timeZoneObj", new TimeZone());
				}
			}

		} catch (Exception e) {
			LOG.error("Error in saving TimeZone " + e.getMessage(), e);
			model.addAttribute("error", messageSource.getMessage("timezone.error.save", new Object[] { e.getMessage() }, Global.LOCALE));
			return new ModelAndView("timeZone", "timeZoneObj", new TimeZone());
		}

		model.addAttribute("error", errMessages);
		if (CollectionUtil.isNotEmpty(errMessages)) {
			model.addAttribute("btnValue", "Create");
			model.addAttribute("country", countryService.getAllCountries());
			return new ModelAndView("timeZone");
		} else {
			return new ModelAndView("redirect:listTimeZone");
		}

	}

	private boolean doValidate(TimeZone timeZone) {
		boolean validate = true;
		if (timeZoneService.isExists(timeZone)) {
			validate = false;
		}
		return validate;
	}

	@RequestMapping(path = "/listTimeZone")
	public String listTimeZone(Model model) throws JsonProcessingException {
		LOG.info("TimeZone list called");
		List<TimeZonePojo> timeZoneList = timeZoneService.getAllTimeZonePojo();
		ObjectMapper mapper = new ObjectMapper();
		model.addAttribute("timeZoneObj", new TimeZone());
		model.addAttribute("timeZoneList", mapper.writeValueAsString(timeZoneList));
		// model.addAttribute("statusList", Status.values());

		return "listTimeZone";

	}

	@RequestMapping(path = "/editTimeZone", method = RequestMethod.GET)
	public ModelAndView editTimeZone(@RequestParam String id, Model model) throws JsonProcessingException {
		LOG.info("Getting the Timezone For edit" + id);
		TimeZone timeZone = timeZoneService.getTimeZoneById(id);
		model.addAttribute("country", countryService.getAllCountries());
		model.addAttribute("btnValue", "Update");
		return new ModelAndView("timeZone", "timeZoneObj", timeZone);
	}

	@RequestMapping(path = "/deleteTimeZone", method = RequestMethod.GET)
	public String deleteTimeZone(@RequestParam String id, Model model) throws JsonProcessingException {
		LOG.info("Delete the Time Zone");
		TimeZone timeZone = timeZoneService.getTimeZoneById(id);
		try {
			timeZone.setModifiedBy(SecurityLibrary.getLoggedInUser());
			timeZone.setModifiedDate(new Date());
			timeZoneService.deleteTimeZone(timeZone);
			model.addAttribute("info", messageSource.getMessage("timezone.success.delete", new Object[] { timeZone.getTimeZone() }, Global.LOCALE));
		} catch (Exception e) {
			LOG.error("Error while deleting timeZone [ " + timeZone.getTimeZone() + " ]" + e.getMessage(), e);
			model.addAttribute("error", messageSource.getMessage("timezone.error.delete", new Object[] { timeZone.getTimeZone() }, Global.LOCALE));
		}
		List<TimeZonePojo> timeZoneList = timeZoneService.getAllTimeZonePojo();
		ObjectMapper mapper = new ObjectMapper();
		model.addAttribute("timeZoneList", mapper.writeValueAsString(timeZoneList));
		return "listTimeZone";
	}

	@RequestMapping(path = "/timeZoneListData", method = RequestMethod.GET)
	public ResponseEntity<TableData<TimeZone>> timeZoneListData(TableDataInput input) throws JsonProcessingException {
		try {
			LOG.info("Start : " + input.getStart() + " Length : " + input.getLength() + " Sort : " + input.getSort());
			LOG.info(">>>>>>>>>>>>> Table Data Input : " + input.toString());

			TableData<TimeZone> data = new TableData<TimeZone>(timeZoneService.findAllTimezones(input));
			for (TimeZone zone : data.getData()) {
				if (zone.getCountry() != null) {
					zone.getCountry().setCreatedDate(null);
					zone.getCountry().setCreatedBy(null);
					zone.getCountry().setModifiedDate(null);
					zone.getCountry().setModifiedBy(null);
				}
			}
			data.setDraw(input.getDraw());
			long totalFilterCount = timeZoneService.findTotalFilteredTimeZones(input);
			data.setRecordsFiltered(totalFilterCount);
			long totalCount = timeZoneService.findTotalTimeZones();
			data.setRecordsTotal(totalCount);
			return new ResponseEntity<TableData<TimeZone>>(data, HttpStatus.OK);
		} catch (Exception e) {
			LOG.error("Error fetching template list : " + e.getMessage(), e);
			HttpHeaders headers = new HttpHeaders();
			headers.add("error", "Error fetching template list : " + e.getMessage());
			return new ResponseEntity<TableData<TimeZone>>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	@InitBinder
	public void initBinder(ServletRequestDataBinder binder) {
		binder.registerCustomEditor(Country.class, "country", countryEditor);
		StringTrimmerEditor stringtrimmer = new StringTrimmerEditor(true);
		binder.registerCustomEditor(String.class, stringtrimmer);
	}

}
