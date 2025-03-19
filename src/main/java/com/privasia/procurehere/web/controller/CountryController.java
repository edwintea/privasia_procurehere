package com.privasia.procurehere.web.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.validation.Valid;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.privasia.procurehere.core.entity.Country;
import com.privasia.procurehere.core.enums.Status;
import com.privasia.procurehere.core.pojo.TableData;
import com.privasia.procurehere.core.pojo.TableDataInput;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.SecurityLibrary;
import com.privasia.procurehere.core.utils.StringUtils;
import com.privasia.procurehere.service.CountryService;

/**
 * @author Kapil
 */
@Controller
@RequestMapping("/admin")
public class CountryController {

	private static final Logger LOG = LogManager.getLogger(Global.ADMIN_LOG);

	@Autowired
	CountryService countryService;

	@Resource
	MessageSource messageSource;

	@ModelAttribute("statusList")
	public List<Status> getStatusList() {
		return Arrays.asList(Status.values());
	}

	@RequestMapping(path = "/country", method = RequestMethod.GET)
	public ModelAndView createCountry(Model model) {
		LOG.info("Create country called");
		model.addAttribute("btnValue", "Create");
		return new ModelAndView("country", "countryObj", new Country());
	}

	@RequestMapping(path = "/saveCountry", method = RequestMethod.POST)
	public ModelAndView saveCountry(@Valid @ModelAttribute("countryObj") Country country, BindingResult result, Model model, RedirectAttributes redir) {

		LOG.info("Save Country .Data: " + country);
		List<String> errMessages = new ArrayList<String>();
		try {
			if (result.hasErrors()) {
				for (ObjectError err : result.getAllErrors()) {
					errMessages.add(err.getDefaultMessage());
				}
				model.addAttribute("btnValue", "Create");
				model.addAttribute("error", errMessages);
				return new ModelAndView("country", "countryObj", country);

			} else {

				if (doValidate(country)) {
					if (StringUtils.checkString(country.getId()).length() == 0) {
						country.setCreatedBy(SecurityLibrary.getLoggedInUser());
						country.setCreatedDate(new Date());
						country.setStatus(country.getStatus());
						countryService.createCountry(country);
						redir.addFlashAttribute("success", messageSource.getMessage("country.save.success", new Object[] { country.getCountryName() }, Global.LOCALE));
						LOG.info("create Country Called " + " " + SecurityLibrary.getLoggedInUser());
					} else {

						// Country = loadById();
						Country persistObj = countryService.getCountryById(country.getId());

						persistObj.setCountryCode(country.getCountryCode());
						persistObj.setCountryName(country.getCountryName());
						persistObj.setModifiedBy(SecurityLibrary.getLoggedInUser());
						persistObj.setModifiedDate(new Date());
						persistObj.setStatus(country.getStatus());
						countryService.updateCountry(persistObj);
						redir.addFlashAttribute("success", messageSource.getMessage("country.update.success", new Object[] { country.getCountryName() }, Global.LOCALE));
						LOG.info("update Country Called");
					}
				} else {
					LOG.info("Validation error ...............");
					model.addAttribute("error", messageSource.getMessage("country.error.duplicate", new Object[] { country.getCountryCode() }, Global.LOCALE));
					model.addAttribute("btnValue", "Create");
					return new ModelAndView("country", "countryObj", country);
				}
			}
		} catch (Exception e) {
			LOG.error("Error While Save the Country" + e.getMessage(), e);
			return new ModelAndView("country", "countryObj", country);
		}

		return new ModelAndView("redirect:listCountry");

	}

	private boolean doValidate(Country country) {
		boolean validate = true;
		if (countryService.isExists(country)) {
			validate = false;
		}
		return validate;
	}

	@RequestMapping(path = "/editCountry/{countryId}", method = RequestMethod.GET)
	public ModelAndView editCountry(@PathVariable("countryId") String countryId, Model model) {
		LOG.info("Getting the Country. : " + countryId);
		Country country = countryService.getCountryById(countryId);
		model.addAttribute("btnValue", "Update");
		return new ModelAndView("country", "countryObj", country);
	}

	@RequestMapping(path = "/deleteCountry", method = RequestMethod.GET)
	public String deleteCountry(@RequestParam String countryId, Model model) throws JsonProcessingException {
		LOG.info("Delete the Country");
		Country country = countryService.getCountryById(countryId);
		try {
			country.setModifiedBy(SecurityLibrary.getLoggedInUser());
			country.setModifiedDate(new Date());
			countryService.deleteCountry(country);
			model.addAttribute("success", messageSource.getMessage("country.success.delete", new Object[] { country.getCountryName() }, Global.LOCALE));
		} catch (Exception e) {
			LOG.error("Error while deleting country [ " + country.getCountryName() + " ]" + e.getMessage(), e);
			model.addAttribute("errors", messageSource.getMessage("country.error.delete", new Object[] { country.getCountryName() }, Global.LOCALE));
		}
		return "listCountry";
	}

	@RequestMapping(path = "/listCountry", method = RequestMethod.GET)
	public String countryList(Model model) throws JsonProcessingException {
		return "listCountry";
	}

	/*@RequestMapping(path = "/countryListData", method = RequestMethod.GET)
	public ResponseEntity<TableData<CountryPojo>> countryListData(@RequestParam("start") int start, @RequestParam("length") int length, @RequestParam("sort") String sort) throws JsonProcessingException {
		LOG.info("Start : " + start + " Length : " + length + " Sort : " + sort);
		TableData<CountryPojo> data = new TableData<CountryPojo>(countryService.findCountriesPojo(start, length, sort));
		data.setRecordsTotal(countryService.findAllActiveCountries() != null ? countryService.findAllActiveCountries().size() : 0);
		return new ResponseEntity<TableData<CountryPojo>>(data, HttpStatus.OK);
	}*/

	@RequestMapping(path = "/search", method = RequestMethod.GET)
	public ModelAndView searchCountry(@RequestParam String countryName) {
		LOG.info("Search Result called");
		List<Country> searchedCountry = countryService.getAllCountries();
		return new ModelAndView("admin/country/countryList", "mycountryList", searchedCountry);

	}

	@RequestMapping(path = "/countryListData", method = RequestMethod.GET)
	public ResponseEntity<TableData<Country>> countryListData(TableDataInput input) throws JsonProcessingException {
		try {
			LOG.info("Start : " + input.getStart() + " Length : " + input.getLength() + " Sort : " + input.getSort());
			LOG.info(">>>>>>>>>>>>> Table Data Input : " + input.toString());

			TableData<Country> data = new TableData<Country>(countryService.findAllCountryList(input));
			data.setDraw(input.getDraw());
			long totalFilterCount = countryService.findTotalFilteredCountryList(input);
			data.setRecordsFiltered(totalFilterCount);
			long totalCount = countryService.findTotalCountryList();
			data.setRecordsTotal(totalCount);
			return new ResponseEntity<TableData<Country>>(data, HttpStatus.OK);
		} catch (Exception e) {
			LOG.error("Error fetching Country list : " + e.getMessage(), e);
			HttpHeaders headers = new HttpHeaders();
			headers.add("error", "Error fetching Country list : " + e.getMessage());
			return new ResponseEntity<TableData<Country>>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

}
