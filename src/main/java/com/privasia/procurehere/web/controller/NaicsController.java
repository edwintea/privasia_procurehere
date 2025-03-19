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
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.privasia.procurehere.core.entity.NaicsCodes;
import com.privasia.procurehere.core.enums.Status;
import com.privasia.procurehere.core.pojo.NaicsCodesPojo;
import com.privasia.procurehere.core.pojo.TableData;
import com.privasia.procurehere.core.pojo.TableDataInput;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.SecurityLibrary;
import com.privasia.procurehere.core.utils.StringUtils;
import com.privasia.procurehere.service.NaicsCodesService;
import com.privasia.procurehere.web.editors.NaicsCodesEditor;

/**
 * @author Javed Ahmed
 */
@Controller
@RequestMapping("/admin")
public class NaicsController {

	private static final Logger LOG = LogManager.getLogger(Global.ADMIN_LOG);

	@Resource
	MessageSource messageSource;

	@Autowired
	NaicsCodesService naicsCodesService;

	@Autowired
	NaicsCodesEditor naicsCodesEditor;

	@InitBinder
	public void initBinder(ServletRequestDataBinder binder) {
		binder.registerCustomEditor(NaicsCodes.class, "parent", naicsCodesEditor);
	}

	@ModelAttribute("statusList")
	public List<Status> getStatusList() {
		return Arrays.asList(Status.values());
	}

	@RequestMapping(path = "/naics", method = RequestMethod.GET)
	public ModelAndView createIndustryCategory(Model model) throws JsonProcessingException {
		List<NaicsCodes> industryList = naicsCodesService.getAllNaicsCodesExceptSelf(null);
		model.addAttribute("parentList", industryList);
		model.addAttribute("btnValue2", "Create");
		return new ModelAndView("naics", "naicsCode", new NaicsCodes());
	}

	@RequestMapping(path = "/naics", method = RequestMethod.POST)
	public ModelAndView saveIndustryCategory(@Valid @ModelAttribute("naicsCode") NaicsCodes naicsCode, BindingResult result, Model model, RedirectAttributes redir) {

		LOG.info("Save naics Called");
		List<String> errMessages = new ArrayList<String>();

		try {

			if (result.hasErrors()) {
				for (ObjectError err : result.getAllErrors()) {
					errMessages.add(err.getDefaultMessage());
				}
				model.addAttribute("btnValue2", "Create");
				model.addAttribute("errors", errMessages);
				return new ModelAndView("naics", "naicsCode", new NaicsCodes());
			} else {

				if (doValidate(naicsCode) == true) {
					if (StringUtils.checkString(naicsCode.getId()).length() == 0) {
						naicsCode.setCreatedBy(SecurityLibrary.getLoggedInUser());
						naicsCode.setCreatedDate(new Date());
						naicsCodesService.createNaicsCodes(naicsCode);
						model.addAttribute("btnValue2", "Create");
						redir.addFlashAttribute("info", messageSource.getMessage("naics.create.success", new Object[] { naicsCode.getCategoryName() }, Global.LOCALE));
					} else {
						LOG.info("id inside update" + naicsCode.getId());
						NaicsCodes persistObj = naicsCodesService.getNaicsCodesById(naicsCode.getId());
						persistObj.setModifiedBy(SecurityLibrary.getLoggedInUser());
						persistObj.setModifiedDate(new Date());
						persistObj.setCategoryCode(naicsCode.getCategoryCode());
						persistObj.setCategoryName(naicsCode.getCategoryName());
						persistObj.setParent(naicsCode.getParent());
						persistObj.setStatus(naicsCode.getStatus());
						naicsCodesService.updateNaicsCodes(persistObj);
						model.addAttribute("btnValue2", "Update");
						redir.addFlashAttribute("success", messageSource.getMessage("naics.update.success", new Object[] { naicsCode.getCategoryName() }, Global.LOCALE));
						LOG.info("update Naics Called");
					}
				} else {

					model.addAttribute("errors", messageSource.getMessage("naics.exist.error", new Object[] {}, Global.LOCALE));
					List<NaicsCodes> industryList = naicsCodesService.getAllNaicsCodesExceptSelf(null);
					model.addAttribute("parentList", industryList);
					// model.addAttribute("statusList", Status.values());
					model.addAttribute("btnValue", "Create");
					model.addAttribute("btnValue2", "Create");
					return new ModelAndView("naics", "naicsCode", new NaicsCodes());
				}
			}
		} catch (Exception e) {
			LOG.error("Error While Save the naics" + e.getMessage(), e);
			model.addAttribute("errors", messageSource.getMessage("naics.error.save", new Object[] {}, Global.LOCALE));
			List<NaicsCodes> industryList = naicsCodesService.getAllNaicsCodesExceptSelf(null);
			model.addAttribute("parentList", industryList);
			model.addAttribute("btnValue", "Create");
			return new ModelAndView("naics", "naicsCode", new NaicsCodes());
		}

		return new ModelAndView("redirect:naicsList");

	}

	private boolean doValidate(NaicsCodes naicsCode) {
		boolean validate = true;
		if (naicsCodesService.isExist(naicsCode)) {
			LOG.info("inside validate of ic");
			validate = false;
		}
		return validate;
	}

	@RequestMapping(path = "/naicsList")
	public String naicsList(Model model) throws JsonProcessingException {
		return "naicsList";

	}

	@RequestMapping(path = "/naicsDataList", method = RequestMethod.GET)
	public ResponseEntity<TableData<NaicsCodesPojo>> countryListData(@RequestParam("start") int start, @RequestParam("length") int length, @RequestParam("sort") String sort) throws JsonProcessingException {
		LOG.info("Start : " + start + " Length : " + length + " Sort : " + sort);
		List<NaicsCodesPojo> list = naicsCodesService.getAllNaicsCodesPojo(start, length, sort);
		TableData<NaicsCodesPojo> data = new TableData<NaicsCodesPojo>(list);
		data.setRecordsTotal(list != null ? list.size() : 0);
		return new ResponseEntity<TableData<NaicsCodesPojo>>(data, HttpStatus.OK);
	}

	@RequestMapping(path = "/editIndCat", method = RequestMethod.GET)
	public String editIndCat(@RequestParam String id, Model model) throws JsonProcessingException {
		LOG.info("Getting the NAICS For edit" + id);
		NaicsCodes obj = naicsCodesService.getNaicsCodesById(id);
		model.addAttribute("naicsCode", obj);

		model.addAttribute("parentList", naicsCodesService.findForLevel(obj.getLevel()));
		model.addAttribute("btnValue2", "Update");
		return "naics";
	}

	@RequestMapping(path = "/deleteIndCat")
	public String deleteService(@RequestParam String id, Model model) throws JsonProcessingException {
		NaicsCodes naicsCode = naicsCodesService.getNaicsCodesById(id);
		try {
			naicsCode.setModifiedBy(SecurityLibrary.getLoggedInUser());
			naicsCode.setModifiedDate(new Date());
			naicsCodesService.deleteNaicsCodes(naicsCode);
			model.addAttribute("success", messageSource.getMessage("naics.delete.success", new Object[] { naicsCode.getCategoryName() }, Global.LOCALE));
		} catch (Exception e) {
			LOG.error("Error while deleting NAICS [ " + naicsCode.getCategoryName() + " ]" + e.getMessage(), e);
			model.addAttribute("error", messageSource.getMessage("naics.delete.error", new Object[] { naicsCode.getCategoryName() }, Global.LOCALE));
		}
		return "naicsList";

	}

	@RequestMapping(path = "/naicsListData", method = RequestMethod.GET)
	public ResponseEntity<TableData<NaicsCodes>> naicsListData(TableDataInput input) throws JsonProcessingException {
		try {
			LOG.info("Start : " + input.getStart() + " Length : " + input.getLength() + " Sort : " + input.getSort());
			LOG.info(">>>>>>>>>>>>> Table Data Input : " + input.toString());

			TableData<NaicsCodes> data = new TableData<NaicsCodes>(naicsCodesService.findNaicsQuery(SecurityLibrary.getLoggedInUserTenantId(), input));
			data.setDraw(input.getDraw());
			long totalFilterCount = naicsCodesService.findTotalFilteredNaics(SecurityLibrary.getLoggedInUserTenantId(), input);
			data.setRecordsFiltered(totalFilterCount);
			long totalCount = naicsCodesService.findTotalNaics(SecurityLibrary.getLoggedInUserTenantId());
			data.setRecordsTotal(totalCount);
			return new ResponseEntity<TableData<NaicsCodes>>(data, HttpStatus.OK);
		} catch (Exception e) {
			LOG.error("Error fetching template list : " + e.getMessage(), e);
			HttpHeaders headers = new HttpHeaders();
			headers.add("error", "Error fetching NAICS list : " + e.getMessage());
			return new ResponseEntity<TableData<NaicsCodes>>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

}
