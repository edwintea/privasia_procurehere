package com.privasia.procurehere.web.controller;

import java.util.List;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.privasia.procurehere.core.entity.Country;
import com.privasia.procurehere.core.entity.Regions;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.SecurityLibrary;
import com.privasia.procurehere.core.utils.StringUtils;
import com.privasia.procurehere.service.CountryService;
import com.privasia.procurehere.service.RegionsService;
import com.privasia.procurehere.web.editors.CountryEditor;

@Controller
@RequestMapping("/")
public class RegionsController {

	private static final Logger LOG = LogManager.getLogger(Global.ADMIN_LOG);

	@Autowired
	RegionsService regionsService;

	@Autowired
	CountryService countryService;

	@Autowired
	CountryEditor countryEditor;

	@RequestMapping(path = "/regionCreate", method = RequestMethod.GET)
	public ModelAndView createRegions(@ModelAttribute Regions regions, ModelMap model) {
		LOG.info("create Method of Regions Called");

		model.addAttribute("country", countryService.getAllCountries());
		return new ModelAndView("regionCreate", "regionsObject", new Regions());
	}

	@RequestMapping(path = "/saveRegion", method = RequestMethod.POST)
	public ModelAndView saveRegions(@ModelAttribute Regions regions) {
		LOG.info("Save Method Called " + regions.getId());

		if (StringUtils.checkString(regions.getId()).length() == 0) {
			regions.setCreatedBy(SecurityLibrary.getLoggedInUser());
			regionsService.createRegions(regions);
		} else {
			regions.setModifiedBy(SecurityLibrary.getLoggedInUser());
			regionsService.updateRegions(regions);
		}
		return new ModelAndView("redirect:regionList");
	}

	@RequestMapping(path = "/regionList")
	public String listRegions(Model model) throws JsonProcessingException {

		LOG.info("Region list called");
		
		List<Regions> regionsList=regionsService.getAllRegions();
	 
		ObjectMapper mapper=new ObjectMapper();
		model.addAttribute("regionsList", mapper.writeValueAsString(regionsList));
		return "regionList";
	}

	@RequestMapping(path = "/editRegion", method = RequestMethod.GET)
	public ModelAndView editRegions(@RequestParam String id, @ModelAttribute Regions regions, ModelMap model) {
		LOG.info("Edit Method Called");

		Regions regions2 = regionsService.getRegionsid(id);
		model.addAttribute("country", countryService.getAllCountries());
		return new ModelAndView("regionCreate", "regionsObject", regions2);
	}

	@RequestMapping(path = "/deleteRegion", method = RequestMethod.GET)
	public ModelAndView deleteRegions(@RequestParam String id) {
		LOG.info("Delete method called");
		Regions regions = regionsService.getRegionsid(id);
		regionsService.deleteRegions(regions);
		return new ModelAndView("redirect:regionList");
	}

	@InitBinder
	public void initBinder(ServletRequestDataBinder binder) {
		binder.registerCustomEditor(Country.class, "country", countryEditor);
	}
}
