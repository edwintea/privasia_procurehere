package com.privasia.procurehere.web.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import org.apache.logging.log4j.*;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.codec.binary.Base64;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.privasia.procurehere.core.dao.BuyerAuditTrailDao;
import com.privasia.procurehere.core.entity.AgreementType;
import com.privasia.procurehere.core.entity.BusinessUnit;
import com.privasia.procurehere.core.entity.BuyerAuditTrail;
import com.privasia.procurehere.core.entity.CostCenter;
import com.privasia.procurehere.core.entity.GroupCode;
import com.privasia.procurehere.core.enums.AuditTypes;
import com.privasia.procurehere.core.enums.ModuleType;
import com.privasia.procurehere.core.enums.Status;
import com.privasia.procurehere.core.pojo.CostCenterPojo;
import com.privasia.procurehere.core.pojo.TableData;
import com.privasia.procurehere.core.pojo.TableDataInput;
import com.privasia.procurehere.core.pojo.UomPojo;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.SecurityLibrary;
import com.privasia.procurehere.core.utils.StringUtils;
import com.privasia.procurehere.service.AgreementTypeService;
import com.privasia.procurehere.service.BusinessUnitService;
import com.privasia.procurehere.service.BuyerSettingsService;
import com.privasia.procurehere.service.CostCenterService;
import com.privasia.procurehere.service.EventIdSettingsService;
import com.privasia.procurehere.service.GroupCodeService;
import com.privasia.procurehere.web.editors.BusinessUnitEditor;
import org.apache.logging.log4j.*;
/**
 * @author parveen
 */
@Controller
@RequestMapping(path = "/buyer")
public class BusinessUnitController {

	private static final Logger LOG = LogManager.getLogger(Global.BUYER_LOG);

	@Autowired
	BusinessUnitService businessUnitService;

	@Resource
	MessageSource messageSource;

	@Autowired
	EventIdSettingsService eventIdSettingsService;

	@Autowired
	BusinessUnitEditor businessUnitEditor;

	@Autowired
	CostCenterService costCenterService;

	@Autowired
	GroupCodeService groupCodeService;
	
	@Autowired
	BuyerAuditTrailDao buyerAuditTrailDao;
	
	@Autowired
	BuyerSettingsService buyerSettingsService;
	
	@Autowired
	AgreementTypeService agreementTypeService;

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder.registerCustomEditor(BusinessUnit.class, businessUnitEditor);
		StringTrimmerEditor stringtrimmer = new StringTrimmerEditor(true);
		binder.registerCustomEditor(String.class, stringtrimmer);
	}

	@RequestMapping(path = "/businessUnit", method = RequestMethod.GET)
	public ModelAndView createBusinessUnit(Model model) {
		LOG.info("Business Unit GET called");
		model.addAttribute("btnValue", messageSource.getMessage("application.create", new Object[] {}, LocaleContextHolder.getLocale()));
		model.addAttribute("codeRequerd", eventIdSettingsService.isRequiredCode(SecurityLibrary.getLoggedInUserTenantId()));
		model.addAttribute("parentList", businessUnitService.getPlainActiveBusinessUnitParentsForTenant(SecurityLibrary.getLoggedInUserTenantId()));
		
		List<CostCenterPojo> costCenterList = costCenterService.fetchAllCostCenterForTenant(SecurityLibrary.getLoggedInUserTenantId(), null, null);
		model.addAttribute("costCenterList", costCenterList);
		List<GroupCode> groupCodeList = groupCodeService.fetchAllGroupCodesForTenant(SecurityLibrary.getLoggedInUserTenantId(), null);
		model.addAttribute("groupCodeList", groupCodeList);
	
		List<AgreementType> agreementTypeList = agreementTypeService.fetchAllAgreementTypeForTenant(SecurityLibrary.getLoggedInUserTenantId(), null);
		model.addAttribute("agreementTypeList", agreementTypeList);
		return new ModelAndView("businessUnitCreate", "businessUnit", new BusinessUnit());
	}

	@RequestMapping(path = "/businessUnit", method = RequestMethod.POST)
	public ModelAndView saveBusinessUnit(@ModelAttribute BusinessUnit businessUnit, @RequestParam(value = "logoImg", required = false) MultipartFile logoImg, @RequestParam(value = "removeFile") boolean removeFile, @RequestParam(value = "recursive") boolean recursive, @RequestParam(value = "spmIntegrationRecursive") boolean spmIntegrationRecursive, @RequestParam("costId") String[] costId, @RequestParam("grcId") String[] groupCodeId, @RequestParam("agrTypId") String[] agreementTypeId,  BindingResult result, Model model, RedirectAttributes redir) {
		try {
			LOG.info(recursive + "Business Unit POST called Code :" + businessUnit.getUnitCode());
			if (!businessUnitService.isExists(businessUnit, SecurityLibrary.getLoggedInUserTenantId())) {

				if (businessUnitService.isExistsUnitCode(businessUnit.getUnitCode(), SecurityLibrary.getLoggedInUserTenantId(), businessUnit.getId())) {
					model.addAttribute("error", messageSource.getMessage("businessUnitCode.error.duplicate", new Object[] { businessUnit.getUnitCode() }, Global.LOCALE));
					LOG.info("duplicate unit Code");
					if (StringUtils.checkString(businessUnit.getId()).length() == 0) {
						model.addAttribute("btnValue", messageSource.getMessage("application.create", new Object[] {}, LocaleContextHolder.getLocale()));
					} else {
						model.addAttribute("btnValue", messageSource.getMessage("application.update", new Object[] {}, LocaleContextHolder.getLocale()));
					}
					model.addAttribute("parentList", businessUnitService.getPlainActiveBusinessUnitParentsForTenant(SecurityLibrary.getLoggedInUserTenantId()));
					model.addAttribute("costCenterList", costCenterService.fetchAllCostCenterForTenant(SecurityLibrary.getLoggedInUserTenantId(), null, null));
					return new ModelAndView("businessUnitCreate", "businessUnit", businessUnit);
				}

				String fileName = null;
				if (StringUtils.checkString(businessUnit.getId()).length() == 0) {
					if (logoImg != null && !logoImg.isEmpty()) {
						fileName = logoImg.getOriginalFilename();
						LOG.info("fileName :" + fileName);
						byte[] bytes = logoImg.getBytes();
						businessUnit.setContentType(logoImg.getContentType());
						businessUnit.setFileName(fileName);
						businessUnit.setFileAttatchment(bytes);
						businessUnit.setFileSizeKb(bytes.length > 0 ? bytes.length / 1024 : 0);
					}
					businessUnit.setBuyer(SecurityLibrary.getLoggedInUser().getBuyer());
					businessUnit.setCreatedBy(SecurityLibrary.getLoggedInUser());
					businessUnit.setCreatedDate(new Date());
					List<CostCenter> costCenterList = new ArrayList<CostCenter>();
					for (String cost : costId) {
						CostCenter costCenter = costCenterService.getCostCenterById(cost);
						costCenterList.add(costCenter);
					}
					businessUnit.setCostCenter(costCenterList);

					List<GroupCode> groupCodeList = new ArrayList<GroupCode>();
					for (String grc : groupCodeId) {
						GroupCode costCenter = groupCodeService.getGroupCodeById(grc);
						groupCodeList.add(costCenter);
					}
					businessUnit.setAssignGroupCodes(groupCodeList);
					
					List<AgreementType> agreementTypeList = new ArrayList<AgreementType>();
					for (String agrTyp : agreementTypeId) {
						AgreementType agreementType = agreementTypeService.getAgreementTypeById(agrTyp);
						agreementTypeList.add(agreementType);
					}
					businessUnit.setAgreementType(agreementTypeList);

					businessUnitService.save(businessUnit);
					redir.addFlashAttribute("success", messageSource.getMessage("businessUnit.save.success", new Object[] { businessUnit.getUnitName() }, Global.LOCALE));
					LOG.info("Business Unit saved successfully");
				} else {
					BusinessUnit persistObj = businessUnitService.getBusinessUnitById(businessUnit.getId());
					if (logoImg != null && !logoImg.isEmpty()) {
						fileName = logoImg.getOriginalFilename();
						LOG.info("fileName :" + fileName);
						byte[] bytes = logoImg.getBytes();
						persistObj.setContentType(logoImg.getContentType());
						persistObj.setFileName(fileName);
						persistObj.setFileAttatchment(bytes);
						persistObj.setFileSizeKb(bytes.length > 0 ? bytes.length / 1024 : 0);
					}
					if (removeFile) {
						persistObj.setContentType(null);
						persistObj.setFileName(null);
						persistObj.setFileAttatchment(null);
						persistObj.setFileSizeKb(null);
					}
					persistObj.setRecursive(recursive);
					persistObj.setSpmIntegrationRecursive(spmIntegrationRecursive);
					persistObj.setUnitName(businessUnit.getUnitName());
					persistObj.setDisplayName(businessUnit.getDisplayName());
					persistObj.setUnitCode(businessUnit.getUnitCode());
					persistObj.setLine1(businessUnit.getLine1());
					persistObj.setLine2(businessUnit.getLine2());
					persistObj.setLine3(businessUnit.getLine3());
					persistObj.setLine4(businessUnit.getLine4());
					persistObj.setLine5(businessUnit.getLine5());
					persistObj.setLine6(businessUnit.getLine6());
					persistObj.setLine7(businessUnit.getLine7());
					persistObj.setStatus(businessUnit.getStatus());
					persistObj.setBudgetCheck(businessUnit.getBudgetCheck());
					persistObj.setSpmIntegration(businessUnit.getSpmIntegration());
					persistObj.setParent(businessUnit.getParent()); // Parent business unit
					persistObj.setModifiedBy(SecurityLibrary.getLoggedInUser());
					persistObj.setModifiedDate(new Date());
					List<CostCenter> costCenterList = new ArrayList<CostCenter>();
					for (String cost : costId) {
						CostCenter costCenter = costCenterService.getCostCenterById(cost);
						costCenterList.add(costCenter);
					}
					persistObj.setCostCenter(costCenterList);

					List<GroupCode> groupCodeList = new ArrayList<GroupCode>();
					for (String grc : groupCodeId) {
						GroupCode groupCode = groupCodeService.getGroupCodeById(grc);
						groupCodeList.add(groupCode);
					}
					persistObj.setAssignGroupCodes(groupCodeList);
					
					List<AgreementType> agreementTypeList = new ArrayList<AgreementType>();
					for (String agrTyp : agreementTypeId) {
						AgreementType agreementType = agreementTypeService.getAgreementTypeById(agrTyp);
						agreementTypeList.add(agreementType);
					}
					persistObj.setAgreementType(agreementTypeList);
					
					businessUnitService.update(persistObj);
					redir.addFlashAttribute("success", messageSource.getMessage("businessUnit.update.success", new Object[] { businessUnit.getUnitName() }, Global.LOCALE));
					LOG.info("Business Unit updated successfully");
				}
			} else {
				model.addAttribute("error", messageSource.getMessage("businessUnit.error.duplicate", new Object[] { businessUnit.getUnitName() }, Global.LOCALE));
				if (StringUtils.checkString(businessUnit.getId()).length() == 0) {
					model.addAttribute("btnValue", messageSource.getMessage("application.create", new Object[] {}, LocaleContextHolder.getLocale()));
				} else {
					model.addAttribute("btnValue", messageSource.getMessage("application.update", new Object[] {}, LocaleContextHolder.getLocale()));
				}
				model.addAttribute("codeRequerd", eventIdSettingsService.isRequiredCode(SecurityLibrary.getLoggedInUserTenantId()));
				model.addAttribute("parentList", businessUnitService.getPlainActiveBusinessUnitParentsForTenant(SecurityLibrary.getLoggedInUserTenantId()));
				model.addAttribute("costCenterList", costCenterService.fetchAllCostCenterForTenant(SecurityLibrary.getLoggedInUserTenantId(), null, null));
				return new ModelAndView("businessUnitCreate", "businessUnit", businessUnit);
			}
		} catch (Exception e) {
			LOG.error("Error While saving the Business Unit" + e.getMessage(), e);
			model.addAttribute("error", messageSource.getMessage("businessUnit.error.save", new Object[] { e.getMessage() }, Global.LOCALE));
			if (StringUtils.checkString(businessUnit.getId()).length() == 0) {
				model.addAttribute("btnValue", messageSource.getMessage("application.create", new Object[] {}, LocaleContextHolder.getLocale()));
			} else {
				model.addAttribute("btnValue", messageSource.getMessage("application.update", new Object[] {}, LocaleContextHolder.getLocale()));
			}
			model.addAttribute("parentList", businessUnitService.getPlainActiveBusinessUnitParentsForTenant(SecurityLibrary.getLoggedInUserTenantId()));
			return new ModelAndView("businessUnitCreate", "businessUnit", businessUnit);
		}
		model.addAttribute("statusList", Arrays.asList(Status.values()));
		model.addAttribute("costCenterList", costCenterService.fetchAllCostCenterForTenant(SecurityLibrary.getLoggedInUserTenantId(), null, null));
		return new ModelAndView("redirect:listBusinessUnit");

	}

	@RequestMapping(path = "/listBusinessUnit", method = RequestMethod.GET)
	public String listBusinessUnit(Model model) {
		model.addAttribute("statusList", Arrays.asList(Status.values()));
		return "listBusinessUnit";
	}

	@RequestMapping(path = "/editBusinessUnit", method = RequestMethod.GET)
	public ModelAndView editBusinessUnit(@RequestParam String id, Model model) {
		LOG.info("Edit Business Unit Called " + id);
		BusinessUnit businessUnit = null;
		try {
			model.addAttribute("btnValue", messageSource.getMessage("application.update", new Object[] {}, LocaleContextHolder.getLocale()));
			businessUnit = businessUnitService.getBusinessUnitById(id);
			businessUnit.setBudgetCheckOld(businessUnit.getBudgetCheck());
			businessUnit.setSpmIntegrationOld(businessUnit.getSpmIntegration());
			try {
				if (businessUnit.getFileAttatchment() != null) {
					byte[] encodeBase64 = Base64.encodeBase64(businessUnit.getFileAttatchment());
					String base64Encoded = new String(encodeBase64, "UTF-8");
					model.addAttribute("logoImg", base64Encoded);
				}
			} catch (Exception e) {
				LOG.error("Error while encoded logo :" + e.getMessage(), e);
			}

			List<BusinessUnit> parentList = businessUnitService.getPlainActiveBusinessUnitParentsForTenant(SecurityLibrary.getLoggedInUserTenantId());
			// Remove self of its present in Parent List
			parentList.remove(businessUnit);
			model.addAttribute("parentList", parentList);
			List<String> assignedCostId = costCenterService.getCostCenterByBusinessId(id);
//			List<CostCenter> assignedCostList =new ArrayList<CostCenter>();
//			if (CollectionUtil.isNotEmpty(assignedCostId)) {
//				for (String assignedCost : assignedCostId) {
//					CostCenter cost = costCenterService.getCostCenterById(assignedCost);
//					assignedCostList.add(cost);
//				}
//				long inactiveCostCount = costCenterService.getCountOfInactiveCostCenter(assignedCostId);
//			}
			model.addAttribute("count", businessUnitService.getCountCostCentersByBusinessUnitId(id, Status.INACTIVE));
			model.addAttribute("assignedCostList", businessUnitService.getCostCentersByBusinessUnitId(id, null));
			List<CostCenterPojo> costCenterList = costCenterService.fetchAllCostCenterForTenantForUnit(SecurityLibrary.getLoggedInUserTenantId(), null, assignedCostId, id); //TODO::
			model.addAttribute("costCenterList", costCenterList);

			List<GroupCode> assignedGrcList = groupCodeService.getAllGroupCodeIdByBusinessUnitId(id);
			long inactiveGroupCode = groupCodeService.getCountOfInactiveGroupCode(id);
			model.addAttribute("grcCount", inactiveGroupCode);
			model.addAttribute("assignedGrcList", assignedGrcList);

			List<GroupCode> groupCodeList = groupCodeService.fetchAllGroupCodeForTenantForUnit(SecurityLibrary.getLoggedInUserTenantId(), null, id);
			model.addAttribute("groupCodeList", groupCodeList);
			
			List<AgreementType> assignedAgreementTypeList = agreementTypeService.getAllAgreementTypeIdByBusinessUnitId(id);
			long inactiveAgreementType = agreementTypeService.getCountOfInactiveAgreementType(id);
			model.addAttribute("agrTypCount", inactiveAgreementType);
			model.addAttribute("assignedAgreementTypeList", assignedAgreementTypeList);
			
			List<AgreementType> agreementTypeList = agreementTypeService.fetchAllAgreementTypeForTenantForUnit(SecurityLibrary.getLoggedInUserTenantId(), null, id);
			model.addAttribute("agreementTypeList", agreementTypeList);

		} catch (Exception e) {
			LOG.error("Error While edit the Business Unit" + e.getMessage(), e);
			model.addAttribute("error", messageSource.getMessage("businessUnit.error.save", new Object[] { e.getMessage() }, Global.LOCALE));
		}
		return new ModelAndView("businessUnitCreate", "businessUnit", businessUnit != null ? businessUnit : new BusinessUnit());
	}

	@RequestMapping(path = "/deleteBusinessUnit", method = RequestMethod.GET)
	public String deleteBusinessUnit(@RequestParam String id, Model model) {
		LOG.info("Delete Business Unit Called");
		try {
			BusinessUnit businessUnit = businessUnitService.getBusinessUnitById(id);
			if (businessUnit != null) {
				businessUnit.setModifiedBy(SecurityLibrary.getLoggedInUser());
				businessUnit.setModifiedDate(new Date());
				businessUnit.setCreatedBy(SecurityLibrary.getLoggedInUser());
				businessUnitService.delete(businessUnit);
				model.addAttribute("success", messageSource.getMessage("businessUnit.success.delete", new Object[] { businessUnit.getUnitName() }, Global.LOCALE));
			} else {
				model.addAttribute("statusList", Arrays.asList(Status.values()));
				return "redirect:listBusinessUnit";
			}
		} catch (Exception e) {
			LOG.error("Error while deletingbusiness unit [ " + e.getMessage(), e);
			model.addAttribute("error", messageSource.getMessage("businessUnit.error.delete", new Object[] {}, Global.LOCALE));
		}
		model.addAttribute("statusList", Arrays.asList(Status.values()));
		return "redirect:listBusinessUnit";
	}

	@RequestMapping(path = "/businessUnitData", method = RequestMethod.GET)
	public ResponseEntity<TableData<BusinessUnit>> businessUnitData(TableDataInput input) {
		try {
			LOG.info("Start : " + input.getStart() + " Length : " + input.getLength() + " Sort : " + input.getSort());
			// LOG.info(">>>>>>>>>>>>> Table Data Input : " + input.toString());
			TableData<BusinessUnit> data = new TableData<BusinessUnit>(businessUnitService.findBusinessUnitsForTenant(SecurityLibrary.getLoggedInUserTenantId(), input));
			data.setDraw(input.getDraw());
			long totalFilterCount = businessUnitService.findTotalFilteredBusinessUnitsForTenant(SecurityLibrary.getLoggedInUserTenantId(), input);
			data.setRecordsFiltered(totalFilterCount);
			long totalCount = businessUnitService.findTotalBusinessUnitsForTenant(SecurityLibrary.getLoggedInUserTenantId());
			data.setRecordsTotal(totalCount);
			return new ResponseEntity<TableData<BusinessUnit>>(data, HttpStatus.OK);
		} catch (Exception e) {
			LOG.error("Error fetching Business Unit list : " + e.getMessage(), e);
			HttpHeaders headers = new HttpHeaders();
			headers.add("error", "Error fetching Business Unit list : " + e.getMessage());
			return new ResponseEntity<TableData<BusinessUnit>>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(path = "/businessUnitTemplate", method = RequestMethod.GET)
	public void downloadBusinessUnitListExcel(HttpServletRequest request, HttpServletResponse response) throws IOException {
		try {
			businessUnitService.businessUnitDownloadTemplate(response, SecurityLibrary.getLoggedInUserTenantId());
		} catch (Exception e) {
			LOG.error("Error while downloading business Unit Service  template :: " + e.getMessage(), e);
		}
	}

	@PostMapping("/searchMoreCostCenter")
	public @ResponseBody ResponseEntity<List<CostCenterPojo>> searchMoreCostCenter(@RequestParam("id") String id, @RequestParam("searchCost") String searchCost) {
		// need to change
		List<String> assignedCostId = costCenterService.getCostCenterByBusinessId(id != null ? id : "");
		List<CostCenterPojo> costCenterList = costCenterService.fetchAllCostCenterForTenantForUnit(SecurityLibrary.getLoggedInUserTenantId(), searchCost, assignedCostId, (id != null ? id : ""));
		return new ResponseEntity<List<CostCenterPojo>>(costCenterList, HttpStatus.OK);
	}

	@RequestMapping(path = "/costCenterListData", method = RequestMethod.GET)
	public ResponseEntity<TableData<CostCenterPojo>> costCenterListData(@RequestParam(required = false) String id, @RequestParam(required = false) String[] costCenterIds, @RequestParam(required = false) String[] removeIds, TableDataInput input) {
		try {
			LOG.info("Business Unit Id: " + id + " costCenterIds : " + costCenterIds + " removeIds: " + removeIds);
			LOG.info("Start : " + input.getStart() + " Length : " + input.getLength() + " Sort : " + input.getSort());
			TableData<CostCenterPojo> data = new TableData<CostCenterPojo>(costCenterService.findCostCenterListByTenantId(SecurityLibrary.getLoggedInUserTenantId(), input, id, costCenterIds, removeIds));
			data.setDraw(input.getDraw());
			long totalFilterCount = costCenterService.findTotalFilteredCostCenterForTenant(SecurityLibrary.getLoggedInUserTenantId(), input, id, costCenterIds, removeIds);
			data.setRecordsFiltered(totalFilterCount);
			data.setRecordsTotal(totalFilterCount);
			return new ResponseEntity<TableData<CostCenterPojo>>(data, HttpStatus.OK);
		} catch (Exception e) {
			LOG.error("Error while fetching cost center list:" + e.getMessage(), e);
			HttpHeaders headers = new HttpHeaders();
			headers.add("error", "Error while fetching cost center list:" + e.getMessage());
			return new ResponseEntity<TableData<CostCenterPojo>>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(path = "/exportBusinessUnitCsvReport", method = RequestMethod.GET)
	public void exportBusinessUnitCsvReport(HttpSession session, HttpServletRequest request, HttpServletResponse response, RedirectAttributes redir, @ModelAttribute("uomPojo") UomPojo uomPojo) throws IOException {
		try {
			File file = File.createTempFile("BusinessUnit-", ".csv");
			LOG.info("Temp file : " + file.getAbsolutePath());

			LOG.info("After if condition controller........." + uomPojo.getEventIds());
			businessUnitService.downloadCsvFileForBusiness(response, file, SecurityLibrary.getLoggedInUserTenantId());
			
			try {
				LOG.info("--------------------------BEFORE AUDIT---------------------------------------");
				BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.DOWNLOAD, "Business Unit is successfully downloaded" , SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser(), new Date(), ModuleType.BusinessUnit);
				buyerAuditTrailDao.save(buyerAuditTrail);
				LOG.info("--------------------------AFTER AUDIT---------------------------------------");
			} catch (Exception e) {
				LOG.error("Error while recording auction event Resume " + e.getMessage(), e);
			}

			// Send to UI
			response.setContentType("text/csv");
			response.addHeader("Content-Disposition", "attachment; filename=BusinessUnit.csv");
			response.setContentLengthLong(file.length());
			Files.copy(file.toPath(), response.getOutputStream());
			response.getOutputStream().flush();

		} catch (Exception e) {
			LOG.error("Error while downloading csv file : " + e.getMessage(), e);
			redir.addFlashAttribute("error", messageSource.getMessage("product.error.download.csv", new Object[] { e.getMessage() }, Global.LOCALE));

		}
	}

	@PostMapping("/searchMoreGroupCodes")
	public @ResponseBody ResponseEntity<List<GroupCode>> searchMoreGroupCodes(@RequestParam("id") String id, @RequestParam("groupCode") String groupCode) {
		// List<String> assignedGrCId = groupCodeService.getGroupCodeIdByBusinessId(id);
		List<GroupCode> groupCodeList = groupCodeService.fetchAllGroupCodeForTenantForUnit(SecurityLibrary.getLoggedInUserTenantId(), groupCode, id);
		return new ResponseEntity<List<GroupCode>>(groupCodeList, HttpStatus.OK);
	}

	@RequestMapping(path = "/groupCodeBUListData", method = RequestMethod.GET)
	public ResponseEntity<TableData<GroupCode>> groupCodeListData(@RequestParam(required = false) String id, @RequestParam(required = false) String[] groupCodeIds, @RequestParam(required = false) String[] removeIds, TableDataInput input) {
		try {
			LOG.info("Business Unit Id: " + id + " groupCodeIds : " + groupCodeIds + " removeIds: " + removeIds);
			LOG.info("Start : " + input.getStart() + " Length : " + input.getLength() + " Sort : " + input.getSort());
			List<GroupCode> groupCodes = groupCodeService.findGroupCodeListByTenantId(SecurityLibrary.getLoggedInUserTenantId(), input, id, groupCodeIds, removeIds);
 			TableData<GroupCode> data = new TableData<GroupCode>(groupCodes);
			data.setDraw(input.getDraw());
			long totalFilterCount = groupCodeService.findTotalFilteredGroupCodeForTenant(SecurityLibrary.getLoggedInUserTenantId(), input, id, groupCodeIds, removeIds);
			data.setRecordsFiltered(totalFilterCount != 0 ? totalFilterCount : (groupCodes != null ? groupCodes.size() : 0));
			data.setRecordsTotal(totalFilterCount);
			return new ResponseEntity<TableData<GroupCode>>(data, HttpStatus.OK);
		} catch (

		Exception e) {
			LOG.error("Error while fetching group Code list:" + e.getMessage(), e);
			HttpHeaders headers = new HttpHeaders();
			headers.add("error", "Error while fetching group Code list:" + e.getMessage());
			return new ResponseEntity<TableData<GroupCode>>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@PostMapping("/searchMoreAgreementType")
	public @ResponseBody ResponseEntity<List<AgreementType>> searchMoreAgreementType(@RequestParam("id") String id, @RequestParam("agreementType") String agreementType) {
		// List<String> assignedGrCId = groupCodeService.getGroupCodeIdByBusinessId(id);
		List<AgreementType> agreementTypeList = agreementTypeService.fetchAllAgreementTypeForTenantForUnit(SecurityLibrary.getLoggedInUserTenantId(), agreementType, id);
		return new ResponseEntity<List<AgreementType>>(agreementTypeList, HttpStatus.OK);
	}

	@RequestMapping(path = "/agreementTypeBUListData", method = RequestMethod.GET)
	public ResponseEntity<TableData<AgreementType>> agreementTypeListData(@RequestParam(required = false) String id, @RequestParam(required = false) String[] agreementTypeIds, @RequestParam(required = false) String[] removeIds, TableDataInput input) {
		try {
			LOG.info("Business Unit Id: " + id + " agreementTypeIds : " + agreementTypeIds + " removeIds: " + removeIds);
			LOG.info("Start : " + input.getStart() + " Length : " + input.getLength() + " Sort : " + input.getSort());
			List<AgreementType> agreementType = agreementTypeService.findAgreementTypeListByTenantId(SecurityLibrary.getLoggedInUserTenantId(), input, id, agreementTypeIds, removeIds);
 			TableData<AgreementType> data = new TableData<AgreementType>(agreementType);
			data.setDraw(input.getDraw());
			long totalFilterCount = agreementTypeService.findTotalFilteredAgreementTypeForTenant(SecurityLibrary.getLoggedInUserTenantId(), input, id, agreementTypeIds, removeIds);
			data.setRecordsFiltered(totalFilterCount != 0 ? totalFilterCount : (agreementType != null ? agreementType.size() : 0));
			data.setRecordsTotal(totalFilterCount);
			return new ResponseEntity<TableData<AgreementType>>(data, HttpStatus.OK);
		} catch (

		Exception e) {
			LOG.error("Error while fetching Agreement Type list:" + e.getMessage(), e);
			HttpHeaders headers = new HttpHeaders();
			headers.add("error", "Error while fetching group Code list:" + e.getMessage());
			return new ResponseEntity<TableData<AgreementType>>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

}
