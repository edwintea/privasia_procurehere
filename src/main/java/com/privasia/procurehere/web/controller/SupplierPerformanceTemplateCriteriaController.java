/**
 * 
 */
package com.privasia.procurehere.web.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.privasia.procurehere.core.entity.RfaBqItem;
import com.privasia.procurehere.core.entity.SourcingTemplateCq;
import com.privasia.procurehere.core.entity.SupplierPerformanceTemplate;
import com.privasia.procurehere.core.entity.SupplierPerformanceTemplateCriteria;
import com.privasia.procurehere.core.enums.SourcingStatus;
import com.privasia.procurehere.core.exceptions.NotAllowedException;
import com.privasia.procurehere.core.pojo.SupplierPerformanceTemplateCriteriaPojo;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.StringUtils;
import com.privasia.procurehere.service.SupplierPerformanceFormService;
import com.privasia.procurehere.service.SupplierPerformanceTemplateCriteriaService;
import com.privasia.procurehere.service.SupplierPerformanceTemplateService;

/**
 * @author Jayshree
 */
@Controller
@RequestMapping("/buyer/supplierPerformanceTemplateCriteriaList")
public class SupplierPerformanceTemplateCriteriaController {

	private static final Logger LOG = LogManager.getLogger(SupplierPerformanceTemplateCriteriaController.class);

	@Autowired
	SupplierPerformanceTemplateCriteriaService spTemplateCriteriaService;

	@Autowired
	SupplierPerformanceTemplateService supplierPerformanceTemplateService;

	@Autowired
	SupplierPerformanceFormService supplierPerformanceFormService;

	@Autowired
	protected MessageSource messageSource;

	@RequestMapping(path = "/{templateId}", method = RequestMethod.GET)
	public String getSupplierPerformanceTemplateCriteriaList(@PathVariable("templateId") String templateId, @ModelAttribute SourcingTemplateCq sourceFormCq, Model model, BindingResult result, RedirectAttributes redirect) {
		try {
			boolean isTemplateUsed;
			List<SupplierPerformanceTemplateCriteria> returnList = new ArrayList<SupplierPerformanceTemplateCriteria>();
			List<SupplierPerformanceTemplateCriteria> criteriaList = spTemplateCriteriaService.getAllCriteriaByOrder(templateId);
			LOG.info("************************ " + criteriaList.size());
			SupplierPerformanceTemplate template = supplierPerformanceTemplateService.getSupplierPerformanceTemplateById(templateId);

			List<String> list = supplierPerformanceFormService.getSPFormIdListByTemplateId(templateId);
			if (CollectionUtil.isEmpty(list)) {
				isTemplateUsed = false;
			} else {
				isTemplateUsed = true;
			}
			model.addAttribute("criteriaList", criteriaList);

			// testing sort
			buildCriteriaForSearch(returnList, criteriaList);
			model.addAttribute("returnList", returnList);

			SupplierPerformanceTemplateCriteria criteria = new SupplierPerformanceTemplateCriteria();
			criteria.setTemplate(template);

			model.addAttribute("criteria", criteria);
			model.addAttribute("status", template.getStatus().toString());
			LOG.info("status " + template.getStatus().toString());
			model.addAttribute("isTemplateUsed", isTemplateUsed);
			model.addAttribute("template", template);
			model.addAttribute("templateId", template.getId());
			model.addAttribute("event", template);

			if (template.getStatus() == SourcingStatus.DRAFT && isTemplateUsed == false) {
				model.addAttribute("finishButton", messageSource.getMessage("application.create", new Object[] {}, LocaleContextHolder.getLocale()));
			} else {
				model.addAttribute("finishButton", messageSource.getMessage("application.update", new Object[] {}, LocaleContextHolder.getLocale()));
			}

		} catch (Exception e) {
			LOG.error("Error ......... " + e.getMessage(), e);
			redirect.addFlashAttribute("error", messageSource.getMessage("rft.cq.error", new Object[] { e.getMessage() }, Global.LOCALE));
		}
		return "supplierPerformanceTemplateCriteriaList";
	}

	private void buildCriteriaForSearch(List<SupplierPerformanceTemplateCriteria> returnList, List<SupplierPerformanceTemplateCriteria> bqList) {
		SupplierPerformanceTemplateCriteria parent = null;
		for (SupplierPerformanceTemplateCriteria item : bqList) {
			if (item.getOrder() != 0) {
				if (parent != null) {
					parent.getChildren().add(item.createShallowCopy());
				}
			} else {
				parent = item.createShallowCopy();
				parent.setChildren(new ArrayList<SupplierPerformanceTemplateCriteria>());
				returnList.add(parent);
			}
		}
	}

	@RequestMapping(path = "/saveSPTCriteria", method = RequestMethod.POST)
	public ModelAndView saveSPTCriteria(@ModelAttribute("criteria") SupplierPerformanceTemplateCriteria criteriaObj, Model model, RedirectAttributes redir) {
		LOG.info("add SPT Criteria method call and Template id " + criteriaObj.getId());
		LOG.info("+++++++++++++++++++ " + criteriaObj.getId() + " ................... " + criteriaObj.getTemplate().getId());
		SupplierPerformanceTemplateCriteria criteria;
		// SupplierPerformanceTemplate template =
		// supplierPerformanceTemplateService.getSupplierPerformanceTemplateById(criteriaObj.getTemplate().getId());
		// Decide if we need to change the template status to DRAFT if the total weightage is != 100
		SourcingStatus templateStatus = null;
		Boolean criteriaCompleted = null;
		try {
			if (StringUtils.checkString(criteriaObj.getName()).length() > 0) {
				if (spTemplateCriteriaService.isCriteriaExistsByName(criteriaObj)) {
					LOG.info("Duplicate Criteria Found ");
					String msg = "";
					if (criteriaObj.getParent() == null) {
						msg += "Criteria";
					} else {
						msg += "Sub Criteria";
					}
					throw new RuntimeException("Duplicate " + msg + ". " + msg + " by that name already exists.");
				}

				if (criteriaObj.getParent() == null) {
					// Compare criteria
					BigDecimal sumOfWeightageOfCriteria = spTemplateCriteriaService.getSumOfWeightageOfAllCriteriaByTemplateId(criteriaObj.getTemplate().getId(), criteriaObj.getId());
					sumOfWeightageOfCriteria = sumOfWeightageOfCriteria.add(criteriaObj.getWeightage());
					if (sumOfWeightageOfCriteria.compareTo(new BigDecimal(100)) > 0) {
						throw new RuntimeException("Summation of Weightage of all Criteria exceeds 100%");
					} else if (sumOfWeightageOfCriteria.compareTo(new BigDecimal(100)) < 0) {
						// Decide if we need to change the template status to DRAFT if the total weightage is != 100
						// templateStatus = SourcingStatus.DRAFT;
						// criteriaCompleted = Boolean.FALSE;
					}
				} else {
					// Compare Sub criteria
					BigDecimal sumOfWeightageOfSubCriteria = spTemplateCriteriaService.getSumOfWeightageOfAllSubCriteriaForCriteriaByCriteriaId(criteriaObj.getId(), criteriaObj.getParent().getId(), criteriaObj.getTemplate().getId());
					sumOfWeightageOfSubCriteria = sumOfWeightageOfSubCriteria.add(criteriaObj.getWeightage());
					if (sumOfWeightageOfSubCriteria.compareTo(new BigDecimal(100)) > 0) {
						throw new RuntimeException("Summation of Weightage of all Sub Criteria for a Criteria exceeds 100%");
					} else if (sumOfWeightageOfSubCriteria.compareTo(new BigDecimal(100)) < 0) {
						// Decide if we need to change the template status to DRAFT if the total weightage is != 100
						// templateStatus = SourcingStatus.DRAFT;
						// criteriaCompleted = Boolean.FALSE;
					}
				}

				if (StringUtils.checkString(criteriaObj.getId()).length() > 0) {
					
					criteria = spTemplateCriteriaService.getSPTCriteriaByCriteriaIdAndTemplateId(criteriaObj.getTemplate().getId(), criteriaObj.getId());
					criteria.setName(criteriaObj.getName());
					criteria.setDescription(criteriaObj.getDescription());
					criteria.setWeightage(criteriaObj.getWeightage());
					criteria.setMaximumScore(criteriaObj.getMaximumScore());
					criteria.setAllowToUpdateSectionWeightage(criteriaObj.getAllowToUpdateSectionWeightage());

					SupplierPerformanceTemplate template = criteria.getTemplate();
					criteria = spTemplateCriteriaService.updateSupplierPerformanceTemplateCriteria(criteria);
					if (template.getPerformanceCriteriaCompleted() == Boolean.TRUE && template.getStatus() == SourcingStatus.ACTIVE) {
						supplierPerformanceTemplateService.updateSupplierPerformanceTemplateStatusAndComplete(template.getId(), Boolean.FALSE, SourcingStatus.DRAFT);
					}

					if (criteria.getOrder() == 0) {
						redir.addFlashAttribute("success", messageSource.getMessage("buyer.Criteria.update", new Object[] {}, Global.LOCALE));
					} else {
						redir.addFlashAttribute("success", messageSource.getMessage("buyer.sub.Criteria.update", new Object[] {}, Global.LOCALE));
					}
				} else {
					criteria = new SupplierPerformanceTemplateCriteria();
					criteria.setName(criteriaObj.getName());
					criteria.setDescription(criteriaObj.getDescription());
					criteria.setWeightage(criteriaObj.getWeightage());
					criteria.setMaximumScore(criteriaObj.getMaximumScore());
					criteria.setAllowToUpdateSectionWeightage(criteriaObj.getAllowToUpdateSectionWeightage());
					criteria.setTemplate(new SupplierPerformanceTemplate());
					criteria.getTemplate().setId(criteriaObj.getTemplate().getId());

					if (criteriaObj.getParent() != null) {
						SupplierPerformanceTemplateCriteria parent = new SupplierPerformanceTemplateCriteria();
						parent.setId(criteriaObj.getParent().getId());
						criteria.setParent(parent);
					}
					criteria = spTemplateCriteriaService.saveSupplierPerformanceTemplateCriteria(criteria);

					SupplierPerformanceTemplate template = supplierPerformanceTemplateService.getSupplierPerformanceTemplatebyId(criteriaObj.getTemplate().getId());
					if (template.getPerformanceCriteriaCompleted() == Boolean.TRUE && template.getStatus() == SourcingStatus.ACTIVE) {
						supplierPerformanceTemplateService.updateSupplierPerformanceTemplateStatusAndComplete(criteriaObj.getTemplate().getId(), Boolean.FALSE, SourcingStatus.DRAFT);
					}
					if (criteria.getOrder() == 0) {
						redir.addFlashAttribute("success", messageSource.getMessage("buyer.Criteria.save", new Object[] {}, Global.LOCALE));
					} else {
						redir.addFlashAttribute("success", messageSource.getMessage("buyer.sub.Criteria.save", new Object[] {}, Global.LOCALE));
					}
				}

			} else {
				redir.addFlashAttribute("info", messageSource.getMessage("Criteria.name.empty", new Object[] {}, Global.LOCALE));
			}
		} catch (Exception e) {
			LOG.error("Error while SavingCriteria : " + e.getMessage(), e);
			if (criteriaObj.getParent() == null) {
				redir.addFlashAttribute("error", messageSource.getMessage("buyer.criteria.save.error", new Object[] { e.getMessage() }, Global.LOCALE));
			} else {
				redir.addFlashAttribute("error", messageSource.getMessage("buyer.sub.criteria.save.error", new Object[] { e.getMessage() }, Global.LOCALE));
			}
		}
		return new ModelAndView("redirect:/buyer/supplierPerformanceTemplateCriteriaList/" + criteriaObj.getTemplate().getId());
	}

	@RequestMapping(path = "/editCriteria", method = RequestMethod.GET)
	public ResponseEntity<SupplierPerformanceTemplateCriteria> editCriteria(@RequestParam String templateId, @RequestParam String criteriaId) {
		LOG.info("Edit Criteria get called :: templateId" + templateId + " :: criteriaId :: " + criteriaId);
		HttpHeaders headers = new HttpHeaders();
		try {
			SupplierPerformanceTemplateCriteria spTemplateCriteria = null;
			spTemplateCriteria = spTemplateCriteriaService.getSPTCriteriaByCriteriaIdAndTemplateId(templateId, criteriaId);
			if (spTemplateCriteria != null) {
				spTemplateCriteria = spTemplateCriteria.createShallowCopy();
				if (spTemplateCriteria.getTemplate() != null) {
					spTemplateCriteria.getTemplate().setProcurementCategory(null);
					spTemplateCriteria.getTemplate().setBuyer(null);
					spTemplateCriteria.getTemplate().setCreatedBy(null);
					spTemplateCriteria.getTemplate().setModifiedBy(null);
				}
				return new ResponseEntity<SupplierPerformanceTemplateCriteria>(spTemplateCriteria, HttpStatus.OK);
			} else {
				LOG.warn("The Criteria for the specified Id not found. Bad Request.");
				headers.add("error", messageSource.getMessage("criteria.edit.not.found.error", new Object[] {}, Global.LOCALE));
				return new ResponseEntity<SupplierPerformanceTemplateCriteria>(spTemplateCriteria, headers, HttpStatus.INTERNAL_SERVER_ERROR);
			}
		} catch (Exception e) {
			LOG.error("Error edit Criteria : " + e.getMessage(), e);
			headers.add("error", messageSource.getMessage("criteria.edit.error", new Object[] { e.getMessage() }, Global.LOCALE));
			return new ResponseEntity<SupplierPerformanceTemplateCriteria>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(value = "/deleteCriteria", method = RequestMethod.GET)
	public ResponseEntity<String> deleteCriteria(@RequestParam(name = "criteriaId") String criteriaId, @RequestParam(name = "templateId") String templateId) {
		LOG.info("Request for Delete Criteria (" + criteriaId + ") received for Template Id : " + templateId);
		HttpHeaders headers = new HttpHeaders();
		try {
			if (StringUtils.checkString(criteriaId).length() > 0) {
				spTemplateCriteriaService.deleteCriteriaByCreteriaId(criteriaId, templateId);
				// Update the template status to DRAFT since due to delete of criteria the total weightage is going to
				// be < 100

				SupplierPerformanceTemplate template = supplierPerformanceTemplateService.getSupplierPerformanceTemplatebyId(templateId);
				if (template.getPerformanceCriteriaCompleted() == Boolean.TRUE && template.getStatus() == SourcingStatus.ACTIVE) {
					supplierPerformanceTemplateService.updateSupplierPerformanceTemplateStatusAndComplete(template.getId(), Boolean.FALSE, SourcingStatus.DRAFT);
				}

				headers.add("success", "Criteria Deleted Successfully");
				return new ResponseEntity<String>("{ \"status\" : \"Criteria deleted successfully\"}", headers, HttpStatus.OK);

			} else {
				headers.add("error", "Criteria cannot be null");
				return new ResponseEntity<String>("{\"msg\":\"All is not good\"}", headers, HttpStatus.INTERNAL_SERVER_ERROR);
			}
		} catch (Exception e) {
			LOG.error("Error deleting items : " + e.getMessage(), e);
			headers.add("error", messageSource.getMessage("prItem.delete.error", new Object[] { e.getMessage() }, Global.LOCALE));
			return new ResponseEntity<String>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(path = "/criteriaOrder", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<List<SupplierPerformanceTemplateCriteriaPojo>> criteriaOrder(@RequestBody SupplierPerformanceTemplateCriteriaPojo criteriaPojo, Model model) {
		LOG.info("Parent : " + criteriaPojo.getParent() + " Item Id : " + criteriaPojo.getId() + " New Position : " + criteriaPojo.getOrder());
		HttpHeaders headers = new HttpHeaders();
		List<RfaBqItem> bqList = null;
		Integer start = null;
		Integer length = null;
		try {
			if (StringUtils.checkString(criteriaPojo.getId()).length() > 0) {
				start = 0;
				LOG.info("Updating order..........................");

				// if(1 == 0) {
				// throw new NotAllowedException();
				// }

				SupplierPerformanceTemplateCriteria criteria = spTemplateCriteriaService.getSPTCriteriaByCriteriaId(criteriaPojo.getId());
				if (criteria.getOrder() > 0 && StringUtils.checkString(criteriaPojo.getParent()).length() == 0) {
					LOG.info("child cannot be a parent");
					headers.add("error", messageSource.getMessage("rfx.child.reOrder.error", new Object[] {}, Global.LOCALE));
					List<SupplierPerformanceTemplateCriteriaPojo> returnList = spTemplateCriteriaService.findAllCriteriaPojoByTemplateId(criteriaPojo.getTemplateId());
					return new ResponseEntity<List<SupplierPerformanceTemplateCriteriaPojo>>(returnList, headers, HttpStatus.BAD_REQUEST);
				}
				if (criteria.getOrder() == 0 && StringUtils.checkString(criteriaPojo.getParent()).length() > 0) {
					LOG.info("parent cannot be a child");
					headers.add("error", messageSource.getMessage("rfx.parent.reOrder.error", new Object[] {}, Global.LOCALE));
					List<SupplierPerformanceTemplateCriteriaPojo> returnList = spTemplateCriteriaService.findAllCriteriaPojoByTemplateId(criteriaPojo.getTemplateId());
					return new ResponseEntity<List<SupplierPerformanceTemplateCriteriaPojo>>(returnList, headers, HttpStatus.BAD_REQUEST);
				}
				SupplierPerformanceTemplateCriteria item = new SupplierPerformanceTemplateCriteria();
				item.setId(criteriaPojo.getId());
				item.setName(criteriaPojo.getName());
				// constructBqItemValues(rfaBqItemPojo, item);

				// LOG.info("Criteria Parent : " + criteria.getParent().getId() + " CriteriaPojo Parent :" +
				// criteriaPojo.getParent() + " Is exists : " +
				// spTemplateCriteriaService.isCriteriaExists(criteriaPojo.getName(), criteriaPojo.getTemplateId(),
				// criteriaPojo.getParent()));

				if (criteria.getParent() != null && !criteria.getParent().getId().equals(criteriaPojo.getParent()) && spTemplateCriteriaService.isCriteriaExists(criteria.getName(), criteriaPojo.getTemplateId(), criteriaPojo.getParent())) {
					LOG.info("Duplicate....");
					String msg = "";
					if (StringUtils.checkString(criteriaPojo.getParent()).length() == 0) {
						msg = "Criteria";
					} else {
						msg = "Sub Criteria";
					}
					throw new NotAllowedException("Duplicate " + msg + ". " + msg + " by that name already exists.");
				}

				if (criteriaPojo.getOrder() != criteria.getOrder() || (criteria.getParent() == null && StringUtils.checkString(criteriaPojo.getParent()).length() > 0) || (criteria.getParent() != null && !StringUtils.checkString(criteriaPojo.getParent()).equals(criteria.getParent().getId()))) {
					spTemplateCriteriaService.reorderCriteria(criteriaPojo);
					headers.add("success", messageSource.getMessage("buyer.rftbq.success", new Object[] {}, Global.LOCALE));
				}

			}
		} catch (NotAllowedException e) {
			LOG.error("Error while moving parent with item as child : " + e.getMessage(), e);
			headers.add("error", messageSource.getMessage("buyer.rftbq.drag", new Object[] { e.getMessage() }, Global.LOCALE));
			List<SupplierPerformanceTemplateCriteriaPojo> returnList = spTemplateCriteriaService.findAllCriteriaPojoByTemplateId(criteriaPojo.getTemplateId());
			return new ResponseEntity<List<SupplierPerformanceTemplateCriteriaPojo>>(returnList, headers, HttpStatus.BAD_REQUEST);
		} catch (Exception e) {
			LOG.error("Error while moving Item : " + e.getMessage(), e);
			headers.add("error", messageSource.getMessage("buyer.criteria.reorder.error", new Object[] { e.getMessage() }, Global.LOCALE));
			List<SupplierPerformanceTemplateCriteriaPojo> returnList = spTemplateCriteriaService.findAllCriteriaPojoByTemplateId(criteriaPojo.getTemplateId());
			return new ResponseEntity<List<SupplierPerformanceTemplateCriteriaPojo>>(returnList, headers, HttpStatus.BAD_REQUEST);
		}
		List<SupplierPerformanceTemplateCriteriaPojo> returnList = spTemplateCriteriaService.findAllCriteriaPojoByTemplateId(criteriaPojo.getTemplateId());
		return new ResponseEntity<List<SupplierPerformanceTemplateCriteriaPojo>>(returnList, headers, HttpStatus.OK);
	}

}
