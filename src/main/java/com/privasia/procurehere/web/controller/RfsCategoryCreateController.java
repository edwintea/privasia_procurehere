package com.privasia.procurehere.web.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.privasia.procurehere.core.dao.RfsCategoryDao;
import com.privasia.procurehere.core.dao.UserDao;
import com.privasia.procurehere.core.entity.RfsCategory;
import com.privasia.procurehere.core.enums.Status;
import com.privasia.procurehere.core.pojo.TableData;
import com.privasia.procurehere.core.pojo.TableDataInput;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.SecurityLibrary;
import com.privasia.procurehere.core.utils.StringUtils;
import com.privasia.procurehere.service.RfsCategoryService;

/**
 * @author sudesha
 */
@Controller
@RequestMapping("/buyer")
public class RfsCategoryCreateController {

	private static final Logger LOG = LogManager.getLogger(Global.PR_LOG);
	protected Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

	@Autowired
	UserDao userDao;

	@Autowired
	private RfsCategoryService rfsCategoryService;

	@Autowired
	private RfsCategoryDao rfsCategoryDao;

	@Resource
	MessageSource messageSource;

	@RequestMapping(path = "/listRfsCategory", method = RequestMethod.GET)
	public String listRfsCategory(Model model) throws JsonProcessingException {
		return "rfsCategoryList";

	}

	@RequestMapping(path = "/rfsCategory", method = RequestMethod.POST)
	public ModelAndView saveRfsCategory(@ModelAttribute("rfsCategory") RfsCategory rfsCategory, BindingResult result, Model model, RedirectAttributes redir) {

		List<String> errMessages = validateRfsCategory(rfsCategory, RfsCategory.RfsCategoryInt.class);

		if (CollectionUtil.isNotEmpty(errMessages)) {
			model.addAttribute("error", errMessages);
			model.addAttribute("btnValue2", "Create");
			return new ModelAndView("rfsCategory", "rfsCategory", new RfsCategory());
		}
		try {
			if (doValidate(rfsCategory)) {

				if (StringUtils.checkString(rfsCategory.getId()).length() == 0) {
					rfsCategory.setCreatedBy(SecurityLibrary.getLoggedInUser());
					rfsCategory.setCreatedDate(new Date());
					rfsCategory.setBuyer(SecurityLibrary.getLoggedInUser().getBuyer());
					rfsCategory.setRfsCode(rfsCategory.getRfsCode());
					rfsCategoryDao.saveOrUpdate(rfsCategory);
					redir.addFlashAttribute("success", messageSource.getMessage("rfs.create.success", new Object[] { rfsCategory.getRfsName() }, Global.LOCALE));
					return new ModelAndView("redirect:listRfsCategory");
				} else {
					RfsCategory persistObj = rfsCategoryService.getRfsCategoryById(rfsCategory.getId());
					persistObj.setRfsCode(rfsCategory.getRfsCode());
					persistObj.setRfsName(rfsCategory.getRfsName());
					persistObj.setModifiedBy(SecurityLibrary.getLoggedInUser());
					persistObj.setModifiedDate(new Date());
					persistObj.setStatus(rfsCategory.getStatus());
					rfsCategoryDao.update(persistObj);
					redir.addFlashAttribute("success", messageSource.getMessage("rfs.update.success", new Object[] { rfsCategory.getRfsName() }, Global.LOCALE));
					return new ModelAndView("redirect:listRfsCategory");

				}

			} else {
				model.addAttribute("errors", messageSource.getMessage("rfs.error.duplicate", new Object[] {}, Global.LOCALE));
				if (StringUtils.checkString(rfsCategory.getId()).length() == 0) {
					model.addAttribute("btnValue2", "Create");
				} else {
					model.addAttribute("btnValue2", "Update");
				}
				return new ModelAndView("rfsCategory", "rfsCategory", rfsCategory);

			}
		} catch (Exception e) {
			rfsCategoryDao.update(rfsCategory);
			redir.addFlashAttribute("success", messageSource.getMessage("rfs.update.success", new Object[] { rfsCategory.getRfsName() }, Global.LOCALE));
			return new ModelAndView("redirect:listRfsCategory");
		}

	}

	public List<String> validateRfsCategory(RfsCategory rc, Class<?>... validations) {
		List<String> errorList = new ArrayList<String>();
		LOG.info("Validaiting .....");

		Set<ConstraintViolation<RfsCategory>> constraintViolations = validator.validate(rc, validations);
		for (ConstraintViolation<RfsCategory> cv : constraintViolations) {
			LOG.info("Message : " + cv.getMessage());
			LOG.info("Error Message : " + cv.getMessage() + " ==Property path : " + cv.getPropertyPath());
			errorList.add(cv.getMessage());
		}
		return errorList;
	}

	private boolean doValidate(RfsCategory rfsCategory) {
		boolean validate = true;
		if (rfsCategoryService.isExists(rfsCategory, SecurityLibrary.getLoggedInUserTenantId())) {
			validate = false;
		}
		return validate;
	}

	@RequestMapping(path = "/rfsCategory", method = RequestMethod.GET)
	public ModelAndView createRfsCategory(@ModelAttribute("rfsCategory") RfsCategory rfsCategory, Model model) {
		LOG.info("RFSCATEGORY");
		model.addAttribute("btnValue2", "Create");
		return new ModelAndView("rfsCategory", "rfsCategory", new RfsCategory());
	}

	@ModelAttribute("statusList")
	public List<Status> getStatusList() {
		return Arrays.asList(Status.values());
	}

	@RequestMapping(path = "/rfsCategoryData", method = RequestMethod.GET)
	public ResponseEntity<TableData<RfsCategory>> rfsCategoryData(TableDataInput input) {
		try {
			LOG.info("Start : " + input.getStart() + " Length : " + input.getLength() + " Sort : " + input.getSort());
			LOG.info(">>>>>>>>>>>>> Table Data Input : " + input.toString());
			TableData<RfsCategory> data = new TableData<RfsCategory>(rfsCategoryService.findRfsCategoryForTenant(SecurityLibrary.getLoggedInUserTenantId(), input));

			data.setDraw(input.getDraw());
			long totalFilterCount = rfsCategoryService.findTotalFilteredRfsCategoryForTenant(SecurityLibrary.getLoggedInUserTenantId(), input);
			data.setRecordsFiltered(totalFilterCount);
			long totalCount = rfsCategoryService.findTotalRfsCategoryForTenant(SecurityLibrary.getLoggedInUserTenantId());
			data.setRecordsTotal(totalCount);

			LOG.info("TOTALCOUNT" + totalCount);
			LOG.info("totalFilterCount" + totalFilterCount);
			return new ResponseEntity<TableData<RfsCategory>>(data, HttpStatus.OK);
		} catch (Exception e) {
			LOG.error("Error fetching RFS Category Maintenance list : " + e.getMessage(), e);
			HttpHeaders headers = new HttpHeaders();
			headers.add("error", "Error fetching RFS Category Maintenance list : " + e.getMessage());
			return new ResponseEntity<TableData<RfsCategory>>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(path = "/rfsCategoryEdit", method = RequestMethod.GET)
	public ModelAndView editRfsCategory(@RequestParam String id, RfsCategory rfsCategory, ModelMap model) {
		RfsCategory rfsCategoryList = rfsCategoryService.getRfsCategoryById(id);
		model.addAttribute("btnValue2", "Update");
		return new ModelAndView("rfsCategory", "rfsCategory", rfsCategoryList);

	}

	@RequestMapping(path = "/rfsCategoryDelete", method = RequestMethod.GET)
	public String deleteRfsCategory(@RequestParam String id, RfsCategory rfsCategory, Model model, RedirectAttributes redir) throws JsonProcessingException {
		LOG.info("RfsCategory in delete" + id);
		RfsCategory rfsCategoryList = rfsCategoryService.getRfsCategoryById(id);
		try {
			if (rfsCategoryList != null) {
				LOG.info("prodcode:" + rfsCategoryList.getRfsCode());
				rfsCategoryDao.delete(rfsCategoryList);
				redir.addFlashAttribute("success", messageSource.getMessage("rfs.delete.success", new Object[] { rfsCategoryList.getRfsName() }, Global.LOCALE));
				LOG.info("Deletion success");
			}
		} catch (Exception e) {
			LOG.error("Deletion error occured : " + e.getMessage(), e);
			redir.addFlashAttribute("error", messageSource.getMessage("rfs.delete.error", new Object[] { rfsCategoryList.getRfsName() }, Global.LOCALE));
		}
		return "redirect:listRfsCategory";
	}

}
