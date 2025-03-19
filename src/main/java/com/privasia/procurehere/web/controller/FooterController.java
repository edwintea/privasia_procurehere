package com.privasia.procurehere.web.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpSession;
import javax.validation.ConstraintViolation;
import javax.validation.Valid;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.groups.Default;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.dao.DataIntegrityViolationException;
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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.privasia.procurehere.core.entity.Footer;
import com.privasia.procurehere.core.entity.Supplier;
import com.privasia.procurehere.core.enums.FooterType;
import com.privasia.procurehere.core.enums.Status;
import com.privasia.procurehere.core.pojo.TableData;
import com.privasia.procurehere.core.pojo.TableDataInput;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.SecurityLibrary;
import com.privasia.procurehere.core.utils.StringUtils;
import com.privasia.procurehere.service.FooterService;

/**
 * @author pooja
 */
@Controller
@RequestMapping(path = "/supplier")
public class FooterController {

	private static final Logger LOG = LogManager.getLogger(Global.SUPPLIER_LOG);

	@Autowired
	MessageSource messageSource;

	@Autowired
	FooterService footerService;

	@ModelAttribute("statusList")
	public List<Status> getStatusList() {
		return Arrays.asList(Status.values());
	}

	@ModelAttribute("footerTypeList")
	public List<FooterType> getFooterTypeList() {
		return Arrays.asList(FooterType.values());
	}

	@RequestMapping(path = "/footerList")
	public String footerList(Model model) {
		LOG.info("getting footer List view....");
		return "footerList";
	}

	@RequestMapping(path = "/footer", method = RequestMethod.GET)
	public ModelAndView createFooter(Model model) {
		model.addAttribute("btnValue", "Create");
		return new ModelAndView("footerCreate", "footerObj", new Footer());
	}

	@RequestMapping(path = "/saveFooter", method = RequestMethod.POST)
	public ModelAndView saveFooter(@Valid @ModelAttribute("footerObj") Footer footerObj, BindingResult result, Model model, HttpSession session, RedirectAttributes redir) {
		LOG.info("Saving footer for user:" + SecurityLibrary.getLoggedInUser().getLoginId());
		List<String> errMessages = new ArrayList<String>();
		try {
			if (StringUtils.checkString(footerObj.getId()).length() == 0) {
				model.addAttribute("btnValue", "Create");
			} else {
				model.addAttribute("btnValue", "Update");
			}

			Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
			Set<ConstraintViolation<Footer>> constraintViolations = validator.validate(footerObj, Default.class);
			for (ConstraintViolation<Footer> cv : constraintViolations) {
				LOG.info("Error occurred : " + cv.getMessage());
				errMessages.add(cv.getMessage());
				model.addAttribute("errors", errMessages);
			}
			if (errMessages.size() > 0) {
				return new ModelAndView("footerCreate", "footerObj", footerObj);
			}
			if (result.hasErrors()) {
				for (ObjectError err : result.getAllErrors()) {
					LOG.info("Error while saving: " + err.getObjectName() + " - " + err.getDefaultMessage());
					errMessages.add(err.getDefaultMessage());
				}
				model.addAttribute("errors", errMessages);
				return new ModelAndView("footerCreate", "footerObj", footerObj);
			} else {
				if (SecurityLibrary.getLoggedInUser().getSupplier() != null) {
					footerObj.setSupplier(new Supplier());
					footerObj.getSupplier().setId(SecurityLibrary.getLoggedInUserTenantId());
				}
				if (footerObj.getId().length() == 0) {
					LOG.info("Saving footer:" + footerObj.getTitle());
					footerObj.setCreatedBy(SecurityLibrary.getLoggedInUser());
					footerObj.setCreatedDate(new Date());
					footerService.saveOrUpdate(footerObj);
					redir.addFlashAttribute("success", messageSource.getMessage("footer.save.success", new Object[] { footerObj.getTitle() }, Global.LOCALE));
					LOG.info("saved footer " + footerObj.getTitle() + "successfully for user:" + SecurityLibrary.getLoggedInUser().getLoginId());
				} else {
					LOG.info("Updating footer:" + footerObj.getTitle());
					Footer persistObj = footerService.getFooterById(footerObj.getId());
					persistObj.setFooterType(persistObj.getFooterType());
					if (persistObj != null) {
						persistObj.setTitle(footerObj.getTitle());
						persistObj.setContent(footerObj.getContent());
						persistObj.setStatus(footerObj.getStatus());
						persistObj.setModifiedBy(SecurityLibrary.getLoggedInUser());
						persistObj.setModifiedDate(new Date());
						footerService.saveOrUpdate(persistObj);
						redir.addFlashAttribute("success", messageSource.getMessage("footer.update.success", new Object[] { footerObj.getTitle() }, Global.LOCALE));
						LOG.info("updated footer " + footerObj.getTitle() + " successfully for user" + SecurityLibrary.getLoggedInUser().getLoginId());
					}
				}
			}
		} catch (Exception e) {
			LOG.info("Error while saving footer:" + e.getMessage(), e);
			model.addAttribute("error", messageSource.getMessage("footer.error.save", new Object[] { e.getMessage() }, Global.LOCALE));
			return new ModelAndView("footerCreate", "footerObj", footerObj);
		}

		return new ModelAndView("redirect:footerList");

	}

	@RequestMapping(path = "/editFooter", method = RequestMethod.GET)
	public ModelAndView editFooter(@RequestParam String id, Model model) {
		Footer footerObj = footerService.getFooterById(id);
		if (footerObj != null) {
			long assignedCount = footerService.findAssignedCountOfFooter(footerObj.getId());
			model.addAttribute("assignedCount", assignedCount);
		}
		model.addAttribute("btnValue", "Update");
		return new ModelAndView("footerCreate", "footerObj", footerObj);
	}

	@RequestMapping(path = "/deleteFooter", method = RequestMethod.GET)
	public String deleteFooter(@RequestParam String id, Footer footerObj, Model model) {
		footerObj = footerService.getFooterById(id);
		try {
			if (footerObj != null) {
				footerService.deleteFooter(footerObj);
				model.addAttribute("success", messageSource.getMessage("footer.success.delete", new Object[] { footerObj.getTitle() }, Global.LOCALE));
				LOG.info("Deleted footer successfully for user :" + SecurityLibrary.getLoggedInUser().getLoginId());
			}
		} catch (DataIntegrityViolationException e) {
			LOG.error("Error while deleting used footer , " + e.getMessage(), e);
			model.addAttribute("error", messageSource.getMessage("footer.error.delete.dataIntegrity", new Object[] { footerObj.getTitle() }, Global.LOCALE));
		} catch (Exception e) {
			LOG.error("Error while deleting declaration :" + e.getMessage(), e);
			model.addAttribute("error", messageSource.getMessage("footer.error.delete", new Object[] { footerObj.getTitle() }, Global.LOCALE));
		}
		return "footerList";

	}

	@RequestMapping(path = "/footerListData", method = RequestMethod.GET)
	public ResponseEntity<TableData<Footer>> footerListData(TableDataInput input) {
		try {
			LOG.info("Getting footer data for user:" + SecurityLibrary.getLoggedInUser().getLoginId());
			LOG.info("Start : " + input.getStart() + " Length : " + input.getLength() + " Sort : " + input.getSort());
			TableData<Footer> data = new TableData<Footer>(footerService.findFooterByTeantId(SecurityLibrary.getLoggedInUserTenantId(), input));
			data.setDraw(input.getDraw());
			long totalFilterCount = footerService.findTotalFilteredFooterForTenant(SecurityLibrary.getLoggedInUserTenantId(), input);
			data.setRecordsFiltered(totalFilterCount);
			long totalCount = footerService.findTotalActiveFooterForTenant(SecurityLibrary.getLoggedInUserTenantId());
			data.setRecordsTotal(totalCount);
			return new ResponseEntity<TableData<Footer>>(data, HttpStatus.OK);
		} catch (Exception e) {
			LOG.error("Error while fetching footer list:" + e.getMessage(), e);
			HttpHeaders headers = new HttpHeaders();
			headers.add("error", "Error while fetching footer list:" + e.getMessage());
			return new ResponseEntity<TableData<Footer>>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	@RequestMapping(path = "/getFooterContentById/{id}", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<Map<String, String>> getFooterContentById(@PathVariable("id") String footerId) {
		LOG.info("Getting footer content");
		try {
			String footerContent = footerService.getFooterContentById(footerId);
			LOG.info("Getting footer content" + footerContent);
			HttpHeaders headers = new HttpHeaders();
			Map<String, String> data = new LinkedHashMap<String, String>();
			data.put("content", footerContent);
			return new ResponseEntity<Map<String, String>>(data, headers, HttpStatus.OK);
		} catch (Exception e) {
			LOG.error("Error while getting content:" + e.getMessage(), e);
			HttpHeaders headers = new HttpHeaders();
			headers.add("error", "Error while getting content");
			return new ResponseEntity<Map<String, String>>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}