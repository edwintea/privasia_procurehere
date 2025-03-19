package com.privasia.procurehere.web.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.privasia.procurehere.core.entity.Buyer;
import com.privasia.procurehere.core.entity.Declaration;
import com.privasia.procurehere.core.enums.DeclarationType;
import com.privasia.procurehere.core.enums.Status;
import com.privasia.procurehere.core.exceptions.ApplicationException;
import com.privasia.procurehere.core.pojo.DeclarationTemplatePojo;
import com.privasia.procurehere.core.pojo.TableData;
import com.privasia.procurehere.core.pojo.TableDataInput;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.SecurityLibrary;
import com.privasia.procurehere.core.utils.StringUtils;
import com.privasia.procurehere.service.DeclarationService;

/**
 * @author pooja
 */
@Controller
@RequestMapping(path = "/buyer")
public class DeclarationController {

	private static final Logger LOG = LogManager.getLogger(Global.BUYER_LOG);

	@Autowired
	MessageSource messageSource;

	@Autowired
	DeclarationService declarationService;

	@ModelAttribute("statusList")
	public List<Status> getStatusList() {
		return Arrays.asList(Status.values());
	}

	@ModelAttribute("declarationTypeList")
	public List<DeclarationType> getDeclarationTypeList() {
		return Arrays.asList(DeclarationType.values());
	}

	@RequestMapping(path = "/declarationList")
	public String declarationList(Model model) {
		LOG.info("getting declaration List view....");
		return "declarationList";
	}

	@RequestMapping(path = "/declaration", method = RequestMethod.GET)
	public ModelAndView createDeclaration(Model model) {
		model.addAttribute("btnValue", "Create");
		return new ModelAndView("declarationCreate", "declarationObj", new Declaration());
	}

	@RequestMapping(path = "/saveDeclaration", method = RequestMethod.POST)
	public ModelAndView saveDeclaration(@Valid @ModelAttribute("declarationObj") Declaration declarationObj, BindingResult result, Model model, HttpSession session, RedirectAttributes redir) {
		LOG.info("Saving declaration for user:" + SecurityLibrary.getLoggedInUser().getLoginId());
		List<String> errMessages = new ArrayList<String>();
		try {
			if (StringUtils.checkString(declarationObj.getId()).length() == 0) {
				model.addAttribute("btnValue", "Create");
			} else {
				model.addAttribute("btnValue", "Update");
			}
			long assignedCount = declarationService.findAssignedCountOfDeclaration(declarationObj.getId());
			model.addAttribute("assignedCount", assignedCount);

			Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
			Set<ConstraintViolation<Declaration>> constraintViolations = validator.validate(declarationObj, Default.class);
			for (ConstraintViolation<Declaration> cv : constraintViolations) {
				LOG.info("Error occurred : " + cv.getMessage());
				errMessages.add(cv.getMessage());
				model.addAttribute("errors", errMessages);
			}
			if (errMessages.size() > 0) {
				return new ModelAndView("declarationCreate", "declarationObj", declarationObj);
			}
			if (result.hasErrors()) {
				for (ObjectError err : result.getAllErrors()) {
					LOG.info("Error while saving: " + err.getObjectName() + " - " + err.getDefaultMessage());
					errMessages.add(err.getDefaultMessage());
				}
				model.addAttribute("errors", errMessages);
				return new ModelAndView("declarationCreate", "declarationObj", declarationObj);
			} else {
				if (SecurityLibrary.getLoggedInUser().getBuyer() != null) {
					declarationObj.setBuyer(new Buyer());
					declarationObj.getBuyer().setId(SecurityLibrary.getLoggedInUserTenantId());
				}
				if (declarationObj.getId().length() == 0) {
					LOG.info("Saving declaration:" + declarationObj.getTitle());
					declarationObj.setCreatedBy(SecurityLibrary.getLoggedInUser());
					declarationObj.setCreatedDate(new Date());
					declarationService.saveOrUpdateDeclaration(declarationObj);
					redir.addFlashAttribute("success", messageSource.getMessage("declaration.save.success", new Object[] { declarationObj.getTitle() }, Global.LOCALE));
					LOG.info("saved declaration " + declarationObj.getTitle() + "successfully for user:" + SecurityLibrary.getLoggedInUser().getLoginId());
				} else {
					LOG.info("Updating declaration:" + declarationObj.getTitle());
					Declaration persistObj = declarationService.getDeclarationById(declarationObj.getId());
					declarationObj.setDeclarationType(persistObj.getDeclarationType());
					if (persistObj != null) {
						if (persistObj.getDeclarationType() != declarationObj.getDeclarationType() && assignedCount > 0) {
							throw new ApplicationException(messageSource.getMessage("declaration.error.change.type", new Object[] { declarationObj.getTitle() }, Global.LOCALE));
						}
						if (assignedCount > 0 && declarationObj.getStatus() == Status.INACTIVE) {
							throw new ApplicationException(messageSource.getMessage("declaration.error.change.staus", new Object[] { declarationObj.getTitle() }, Global.LOCALE));
						}

						persistObj.setTitle(declarationObj.getTitle());
						persistObj.setContent(declarationObj.getContent());
						persistObj.setStatus(declarationObj.getStatus());
						persistObj.setModifiedBy(SecurityLibrary.getLoggedInUser());
						persistObj.setModifiedDate(new Date());
						declarationService.updateDeclaration(persistObj);
						redir.addFlashAttribute("success", messageSource.getMessage("declaration.update.success", new Object[] { declarationObj.getTitle() }, Global.LOCALE));
						LOG.info("updated declaration " + declarationObj.getTitle() + " successfully for user" + SecurityLibrary.getLoggedInUser().getLoginId());
					}
				}
			}
		} catch (ApplicationException e) {
			LOG.error("Error while saving used declaration , " + e.getMessage(), e);
			model.addAttribute("error", e.getMessage());
			return new ModelAndView("declarationCreate", "declarationObj", declarationObj);
		} catch (Exception e) {
			LOG.info("Error while saving declaration:" + e.getMessage(), e);
			model.addAttribute("error", messageSource.getMessage("declaration.error.save", new Object[] { e.getMessage() }, Global.LOCALE));
			return new ModelAndView("declarationCreate", "declarationObj", declarationObj);
		}

		return new ModelAndView("redirect:declarationList");

	}

	@RequestMapping(path = "/editDeclaration", method = RequestMethod.GET)
	public ModelAndView editDeclaration(@RequestParam String id, Model model) {
		Declaration declarationObj = declarationService.getDeclarationById(id);
		if (declarationObj != null) {
			long assignedCount = declarationService.findAssignedCountOfDeclaration(declarationObj.getId());
			model.addAttribute("assignedCount", assignedCount);
		}
		model.addAttribute("btnValue", "Update");
		return new ModelAndView("declarationCreate", "declarationObj", declarationObj);
	}

	@RequestMapping(path = "/deleteDeclaration", method = RequestMethod.GET)
	public String deleteDeclaration(@RequestParam String id, Declaration declarationObj, Model model) {
		declarationObj = declarationService.getDeclarationById(id);
		try {
			if (declarationObj != null) {
				declarationService.deleteDeclaration(declarationObj);
				model.addAttribute("success", messageSource.getMessage("declaration.success.delete", new Object[] { declarationObj.getTitle() }, Global.LOCALE));
				LOG.info("Deleted declaration successfully for user :" + SecurityLibrary.getLoggedInUser().getLoginId());
			}
		} catch (DataIntegrityViolationException e) {
			LOG.error("Error while deleting used declaration , " + e.getMessage(), e);
			model.addAttribute("error", messageSource.getMessage("declaration.error.delete.dataIntegrity", new Object[] { declarationObj.getTitle() }, Global.LOCALE));
		} catch (Exception e) {
			LOG.error("Error while deleting declaration :" + e.getMessage(), e);
			model.addAttribute("error", messageSource.getMessage("declaration.error.delete", new Object[] { declarationObj.getTitle() }, Global.LOCALE));
		}
		return "declarationList";

	}

	@RequestMapping(path = "/declarationListData", method = RequestMethod.GET)
	public ResponseEntity<TableData<DeclarationTemplatePojo>> declarationListData(TableDataInput input) {
		try {
			LOG.info("Getting declaration data for user:" + SecurityLibrary.getLoggedInUser().getLoginId());
			LOG.info("Start : " + input.getStart() + " Length : " + input.getLength() + " Sort : " + input.getSort());
			TableData<DeclarationTemplatePojo> data = new TableData<DeclarationTemplatePojo>(declarationService.findDeclarationsByTeantId(SecurityLibrary.getLoggedInUserTenantId(), input));
			data.setDraw(input.getDraw());
			long totalFilterCount = declarationService.findTotalFilteredDeclarationsForTenant(SecurityLibrary.getLoggedInUserTenantId(), input);
			data.setRecordsFiltered(totalFilterCount);
			long totalCount = declarationService.findTotalActiveDeclarationsForTenant(SecurityLibrary.getLoggedInUserTenantId());
			data.setRecordsTotal(totalCount);
			return new ResponseEntity<TableData<DeclarationTemplatePojo>>(data, HttpStatus.OK);
		} catch (Exception e) {
			LOG.error("Error while fetching declaration list:" + e.getMessage(), e);
			HttpHeaders headers = new HttpHeaders();
			headers.add("error", "Error while fetching declaration list:" + e.getMessage());
			return new ResponseEntity<TableData<DeclarationTemplatePojo>>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}
}
