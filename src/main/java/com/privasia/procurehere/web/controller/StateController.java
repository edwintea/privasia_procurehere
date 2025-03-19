package com.privasia.procurehere.web.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

/**
 * 
 */

import java.util.List;

import javax.annotation.Resource;
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
import org.springframework.ui.ModelMap;
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
import com.privasia.procurehere.core.entity.State;
import com.privasia.procurehere.core.enums.Status;
import com.privasia.procurehere.core.pojo.StatePojo;
import com.privasia.procurehere.core.pojo.TableData;
import com.privasia.procurehere.core.pojo.TableDataInput;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.SecurityLibrary;
import com.privasia.procurehere.core.utils.StringUtils;
import com.privasia.procurehere.service.CountryService;
import com.privasia.procurehere.service.StateService;
import com.privasia.procurehere.web.editors.CountryEditor;

/**
 * @author jav3d
 */
@Controller
@RequestMapping(path = "/admin")
public class StateController {

	private static final Logger LOG = LogManager.getLogger(StateController.class);

	@Autowired
	private StateService stateService;

	@Autowired
	private CountryService countryService;

	@Autowired
	private CountryEditor countryEditor;

	@Resource
	MessageSource messageSource;

	@InitBinder
	public void initBinder(ServletRequestDataBinder binder) {
		binder.registerCustomEditor(Country.class, "country", countryEditor);
		StringTrimmerEditor stringtrimmer = new StringTrimmerEditor(true);
		binder.registerCustomEditor(String.class, stringtrimmer);
	}

	@ModelAttribute("statusList")
	public List<Status> getStatusList() {
		return Arrays.asList(Status.values());
	}

	@RequestMapping(path = "/state", method = RequestMethod.GET)
	public ModelAndView createState(@ModelAttribute State state, Model model) {
		model.addAttribute("countrys", countryService.getAllCountries());
		model.addAttribute("btnValue", "Save");
		model.addAttribute("btnValue2", "Create");
		return new ModelAndView("state", "stateObject", new State());
	}

	@RequestMapping(path = "/state", method = RequestMethod.POST)
	public ModelAndView saveState(@Valid @ModelAttribute("stateObject") State state, BindingResult result, Model model, RedirectAttributes redir) {
		List<String> errMessages = new ArrayList<String>();
		LOG.info("==================================" + state.getCountry().getId());
		try {
			if (result.hasErrors()) {
				for (ObjectError err : result.getAllErrors()) {
					errMessages.add(err.getDefaultMessage());
				}
				LOG.info("error..");
				model.addAttribute("btnValue", "Save");
				model.addAttribute("btnValue2", "Create");
				model.addAttribute("error", errMessages);
				model.addAttribute("countrys", countryService.getAllCountries());
				return new ModelAndView("state", "stateObject", state);
			} else {
				LOG.info("State Status :: " + state.getStatus());
				if (doValidate(state)) {
					if (StringUtils.checkString(state.getId()).length() == 0) {
						LOG.info("Saving the State. Data : " + state.getId());
						state.setCreatedBy(SecurityLibrary.getLoggedInUser());
						state.setCreatedDate(new Date());
						stateService.createState(state);
						redir.addFlashAttribute("success", messageSource.getMessage("state.create.success", new Object[] { state.getStateName() }, Global.LOCALE));
					} else {
						LOG.info("updating the State. Data : " + state.getId());

						LOG.info("update CategoryService Called");
						State persistObj = stateService.getState(state.getId());

						persistObj.setCountry(state.getCountry());
						persistObj.setStateCode(state.getStateCode());
						persistObj.setStateName(state.getStateName());
						persistObj.setModifiedBy(SecurityLibrary.getLoggedInUser());
						persistObj.setModifiedDate(new Date());
						persistObj.setStatus(state.getStatus());
						stateService.updateState(persistObj);
						redir.addFlashAttribute("success", messageSource.getMessage("state.update.success", new Object[] { state.getStateName() }, Global.LOCALE));
					}
				} else {
					model.addAttribute("errors", messageSource.getMessage("state.error.duplicate", new Object[] {}, Global.LOCALE));
					model.addAttribute("errors", messageSource.getMessage("state.error.duplicate", new Object[] {}, Global.LOCALE));
					model.addAttribute("btnValue", "Create");
					if (StringUtils.checkString(state.getId()).length() == 0) {
						model.addAttribute("btnValue2", "Create");
					} else {
						model.addAttribute("btnValue2", "Update");
					}
					model.addAttribute("countrys", countryService.getAllCountries());
					return new ModelAndView("state", "stateObject", state);

				}
			}
		} catch (Exception e) {
			LOG.error("Error While saving the state" + e.getMessage(), e);
			model.addAttribute("error", messageSource.getMessage("state.error.save", new Object[] { e.getMessage() }, Global.LOCALE));
			model.addAttribute("countrys", countryService.getAllCountries());
			return new ModelAndView("state", "stateObject", state);
		}

		return new ModelAndView("redirect:listState");

	}

	private boolean doValidate(State state) {
		boolean validate = true;
		if (stateService.isExists(state)) {
			validate = false;
		}
		return validate;
	}

	@RequestMapping(path = "/listState", method = RequestMethod.GET)
	public String getAllStates(Model model) throws JsonProcessingException {
		List<StatePojo> stateList1 = stateService.getAllStatesPojo();
		ObjectMapper mapper = new ObjectMapper();
		model.addAttribute("stateList1", mapper.writeValueAsString(stateList1));
		return "listState";

	}

	@RequestMapping(path = "/stateEdit", method = RequestMethod.GET)
	public ModelAndView editState(@RequestParam String id, @ModelAttribute State state, ModelMap model) {
		State state1 = stateService.getState(id);
		model.addAttribute("countrys", countryService.getAllCountries());
		model.addAttribute("btnValue", "Update");
		model.addAttribute("btnValue2", "Update");
		return new ModelAndView("state", "stateObject", state1);

	}

	@RequestMapping(path = "/stateDelete", method = RequestMethod.GET)
	public String deleteState(@RequestParam String id, State state, Model model) throws JsonProcessingException {
		state.setId(id);
		State state2 = stateService.getState(state.getId());
		try {
			state2.setModifiedBy(SecurityLibrary.getLoggedInUser());
			state2.setModifiedDate(new Date());
			stateService.deletestate(state2);
			model.addAttribute("success", messageSource.getMessage("state.delete.success", new Object[] { state2.getStateName() }, Global.LOCALE));
		} catch (Exception e) {
			LOG.info("Deletion error occur");
			model.addAttribute("error", messageSource.getMessage("state.delete.error", new Object[] { state2.getStateName() }, Global.LOCALE));
		}
		List<StatePojo> stateList1 = stateService.getAllStatesPojo();
		ObjectMapper mapper = new ObjectMapper();
		model.addAttribute("stateList1", mapper.writeValueAsString(stateList1));
		return "listState";
	}

	@RequestMapping(path = "/stateListData", method = RequestMethod.GET)
	public ResponseEntity<TableData<State>> stateListData(TableDataInput input) throws JsonProcessingException {
		try {
			LOG.info("Start : " + input.getStart() + " Length : " + input.getLength() + " Sort : " + input.getSort());
			LOG.info(">>>>>>>>>>>>> Table Data Input : " + input.toString());

			TableData<State> data = new TableData<State>(stateService.findAllStateList(input));
			data.setDraw(input.getDraw());
			long totalFilterCount = stateService.findTotalFilteredStateList(input);
			data.setRecordsFiltered(totalFilterCount);
			long totalCount = stateService.findTotalStateList();
			data.setRecordsTotal(totalCount);
			return new ResponseEntity<TableData<State>>(data, HttpStatus.OK);
		} catch (Exception e) {
			LOG.error("Error fetching State list : " + e.getMessage(), e);
			HttpHeaders headers = new HttpHeaders();
			headers.add("error", "Error fetching State list : " + e.getMessage());
			return new ResponseEntity<TableData<State>>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

}
