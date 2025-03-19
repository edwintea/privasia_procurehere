/**
 * 
 */
package com.privasia.procurehere.web.controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import javax.servlet.http.HttpSession;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.propertyeditors.CustomCollectionEditor;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.privasia.procurehere.core.dao.SupplierFormSubItemOptionDao;
import com.privasia.procurehere.core.entity.Buyer;
import com.privasia.procurehere.core.entity.IndustryCategory;
import com.privasia.procurehere.core.entity.OwnerSettings;
import com.privasia.procurehere.core.entity.RequestedAssociatedBuyer;
import com.privasia.procurehere.core.entity.Supplier;
import com.privasia.procurehere.core.entity.SupplierFormItemAttachment;
import com.privasia.procurehere.core.entity.SupplierFormItemOption;
import com.privasia.procurehere.core.entity.SupplierFormSubmissionItem;
import com.privasia.procurehere.core.entity.SupplierFormSubmition;
import com.privasia.procurehere.core.entity.SupplierFormSubmitionAudit;
import com.privasia.procurehere.core.entity.SupplierFormSubmitionItemOption;
import com.privasia.procurehere.core.enums.FavouriteSupplierStatus;
import com.privasia.procurehere.core.enums.RequestAssociateBuyerStatus;
import com.privasia.procurehere.core.pojo.RequestedAssociatedBuyerPojo;
import com.privasia.procurehere.core.pojo.SupplierFormSubItem;
import com.privasia.procurehere.core.pojo.SupplierFormSubmissionItemPojo;
import com.privasia.procurehere.core.pojo.TableData;
import com.privasia.procurehere.core.pojo.TableDataInput;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.SecurityLibrary;
import com.privasia.procurehere.core.utils.StringUtils;
import com.privasia.procurehere.service.BuyerService;
import com.privasia.procurehere.service.BuyerSettingsService;
import com.privasia.procurehere.service.CountryService;
import com.privasia.procurehere.service.FavoriteSupplierService;
import com.privasia.procurehere.service.FavoutireSupplierAuditService;
import com.privasia.procurehere.service.IndustryCategoryService;
import com.privasia.procurehere.service.OwnerSettingsService;
import com.privasia.procurehere.service.SupplierFormService;
import com.privasia.procurehere.service.SupplierFormSubmissionService;
import com.privasia.procurehere.service.SupplierFormSubmitionAuditService;
import com.privasia.procurehere.service.SupplierSettingsService;
import com.privasia.procurehere.service.supplier.SupplierAssociatedBuyerService;
import com.privasia.procurehere.service.supplier.SupplierService;
import com.privasia.procurehere.web.editors.SupplierFormItemOptionEditor;

/**
 * @author pooja
 */

@Controller
@RequestMapping("/supplier")
public class SupplierAssociatedBuyerController implements Serializable {

	private static final long serialVersionUID = 1112615964267284062L;

	private static final Logger LOG = LogManager.getLogger(Global.SUPPLIER_LOG);

	@Autowired
	SupplierAssociatedBuyerService supplierAssociatedBuyerService;

	@Autowired
	BuyerService buyerService;

	@Autowired
	MessageSource messageSource;

	@Autowired
	FavoutireSupplierAuditService favSuppAuditService;

	@Autowired
	SupplierService supplierService;

	@Autowired
	BuyerSettingsService buyerSettingsService;

	@Autowired
	CountryService countryService;

	@Autowired
	IndustryCategoryService industryCategoryService;

	@Autowired
	FavoriteSupplierService favoriteSupplierService;

	@Autowired
	SupplierSettingsService supplierSettingsService;

	@Autowired
	SupplierFormSubmissionService formSubmissionService;

	@Autowired
	SupplierFormService formService;

	@Autowired
	OwnerSettingsService ownerSettingsService;

	@Autowired
	SupplierFormSubmitionAuditService supplierFormSubAuditService;

	@ModelAttribute("ownerSettings")
	public OwnerSettings getOwnersettings() {
		return ownerSettingsService.getOwnersettings();
	}

	@Value("${app.url}")
	String appUrl;

	@Autowired
	SupplierFormItemOptionEditor supplierFormOptionEditor;

	@Autowired
	SupplierFormSubItemOptionDao supplierFormSubItemOptionDao;

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder.registerCustomEditor(SupplierFormItemOption.class, supplierFormOptionEditor);
		binder.registerCustomEditor(List.class, "listOptAnswers", new CustomCollectionEditor(List.class) {
			protected Object convertElement(Object element) {
				if (element != null) {
					String id = (String) element;
					LOG.info("ID : " + id);
					SupplierFormSubmitionItemOption group = supplierFormSubItemOptionDao.findById(id);
					SupplierFormItemOption op = new SupplierFormItemOption();
					op.setId(group.getId());
					op.setOrder(group.getOrder());
					op.setValue(group.getValue());
					return op;
				}
				return null;
			}
		});
	}

	@ModelAttribute("requestedBuyerStatusList")
	public List<RequestAssociateBuyerStatus> getRequestedBuyerStatusList() {
		return Arrays.asList(RequestAssociateBuyerStatus.values());
	}

	@RequestMapping(value = "/associateBuyerList", method = RequestMethod.GET)
	public String buyerList(Model model) {
		try {
			LOG.info("Get associated buyer list");
			long pendingBuyerCount = favoriteSupplierService.getAssociatedBuyersCountForSupplierBasedOnStatus(SecurityLibrary.getLoggedInUserTenantId(), FavouriteSupplierStatus.PENDING);
			long rejectedBuyerCount = favoriteSupplierService.getAssociatedBuyersCountForSupplierBasedOnStatus(SecurityLibrary.getLoggedInUserTenantId(), FavouriteSupplierStatus.REJECTED);
			long associatedBuyerCount = favoriteSupplierService.getAssociatedBuyersCountForSupplier(SecurityLibrary.getLoggedInUserTenantId());
			long availableBuyerCount = favoriteSupplierService.getTotalPublishedAvailableBuyerList(SecurityLibrary.getLoggedInUserTenantId());
			model.addAttribute("pendingBuyerCount", pendingBuyerCount);
			model.addAttribute("rejectedBuyerCount", rejectedBuyerCount);
			model.addAttribute("availableBuyerCount", availableBuyerCount);
			model.addAttribute("associatedBuyerCount", associatedBuyerCount);
			model.addAttribute("requestedAssociatedBuyerPojo", new RequestedAssociatedBuyerPojo());
			model.addAttribute("countryList", countryService.getAllCountries());
		} catch (Exception e) {
			LOG.error("Error while fetching associated buyer list :" + e.getMessage(), e);
		}
		return "associatedBuyerList";
	}

	@RequestMapping(path = "/searchBuyer", method = RequestMethod.GET)
	public ResponseEntity<TableData<RequestedAssociatedBuyerPojo>> searchBuyers(TableDataInput input, @RequestParam("searchCompanyName") String searchCompanyName, @RequestParam("searchCountryName") String searchCountryName, HttpSession session) {
		LOG.info("Getting serached buyer list:");
		TableData<RequestedAssociatedBuyerPojo> data = null;
		try {
			RequestedAssociatedBuyerPojo searchBuyerPojo = new RequestedAssociatedBuyerPojo();
			searchBuyerPojo.setSearchCompanyName(searchCompanyName);
			searchBuyerPojo.setSearchCountryName(searchCountryName);
			LOG.info("searchBuyerPojo:" + searchBuyerPojo.getSearchCompanyName() + "country:" + searchBuyerPojo.getSearchCountryName());
			List<RequestedAssociatedBuyerPojo> searchList = favoriteSupplierService.searchBuyers(input, searchBuyerPojo, SecurityLibrary.getLoggedInUserTenantId());
			data = new TableData<RequestedAssociatedBuyerPojo>(searchList);
			data.setDraw(input.getDraw());
			long buyerCount = favoriteSupplierService.searchBuyersCount(input, searchBuyerPojo, SecurityLibrary.getLoggedInUserTenantId());
			data.setRecordsTotal(buyerCount);
			data.setRecordsFiltered(buyerCount);
		} catch (Exception e) {
			LOG.error("Error occurred while fetching defult buyer list:" + e.getMessage(), e);
		}
		return new ResponseEntity<TableData<RequestedAssociatedBuyerPojo>>(data, HttpStatus.OK);
	}

	@RequestMapping(value = "/availableBuyerList", method = RequestMethod.GET)
	public ResponseEntity<TableData<RequestedAssociatedBuyerPojo>> availableBuyerList(TableDataInput input) {
		TableData<RequestedAssociatedBuyerPojo> data = null;
		try {
			data = new TableData<RequestedAssociatedBuyerPojo>(favoriteSupplierService.getAvailableBuyerList(input, SecurityLibrary.getLoggedInUserTenantId()));
			data.setDraw(input.getDraw());
			long totalCount = favoriteSupplierService.getAvailableBuyerListCount(input, SecurityLibrary.getLoggedInUserTenantId());
			data.setRecordsFiltered(totalCount);
			data.setRecordsTotal(totalCount);
		} catch (Exception e) {
			LOG.error("Error occurred while fetching pending available buyer list:" + e.getMessage(), e);
		}
		return new ResponseEntity<TableData<RequestedAssociatedBuyerPojo>>(data, HttpStatus.OK);
	}

	@RequestMapping(value = "/pendingRequestBuyerList", method = RequestMethod.GET)
	public ResponseEntity<TableData<RequestedAssociatedBuyerPojo>> pendingRequestBuyerList(TableDataInput input) {
		TableData<RequestedAssociatedBuyerPojo> data = null;
		try {
			data = new TableData<RequestedAssociatedBuyerPojo>(favoriteSupplierService.getAssociatedBuyerListBasedOnStatus(input, SecurityLibrary.getLoggedInUserTenantId(), FavouriteSupplierStatus.PENDING));
			data.setDraw(input.getDraw());
			long totalCount = favoriteSupplierService.getAssociatedBuyersCountBasedOnStatus(input, SecurityLibrary.getLoggedInUserTenantId(), FavouriteSupplierStatus.PENDING);
			data.setRecordsFiltered(totalCount);
			data.setRecordsTotal(totalCount);
		} catch (Exception e) {
			LOG.error("Error occurred while fetching pending requested buyer list:" + e.getMessage(), e);
		}
		return new ResponseEntity<TableData<RequestedAssociatedBuyerPojo>>(data, HttpStatus.OK);
	}

	@RequestMapping(value = "/rejectedRequestBuyerList", method = RequestMethod.GET)
	public ResponseEntity<TableData<RequestedAssociatedBuyerPojo>> rejectedRequestBuyerList(TableDataInput input) {
		TableData<RequestedAssociatedBuyerPojo> data = null;
		try {
			data = new TableData<RequestedAssociatedBuyerPojo>(favoriteSupplierService.getAssociatedBuyerListBasedOnStatus(input, SecurityLibrary.getLoggedInUserTenantId(), FavouriteSupplierStatus.REJECTED));
			data.setDraw(input.getDraw());
			long totalCount = favoriteSupplierService.getAssociatedBuyersCountBasedOnStatus(input, SecurityLibrary.getLoggedInUserTenantId(), FavouriteSupplierStatus.REJECTED);
			data.setRecordsFiltered(totalCount);
			data.setRecordsTotal(totalCount);
		} catch (Exception e) {
			LOG.error("Error occurred while fetching rejected requested buyer list:" + e.getMessage(), e);
		}
		return new ResponseEntity<TableData<RequestedAssociatedBuyerPojo>>(data, HttpStatus.OK);
	}

	@RequestMapping(value = "/associatedBuyerList", method = RequestMethod.GET)
	public ResponseEntity<TableData<RequestedAssociatedBuyerPojo>> associatedBuyerList(TableDataInput input) {
		TableData<RequestedAssociatedBuyerPojo> data = null;
		try {
			data = new TableData<RequestedAssociatedBuyerPojo>(favoriteSupplierService.getAssociatedBuyerListForSupplier(input, SecurityLibrary.getLoggedInUserTenantId()));
			data.setDraw(input.getDraw());
			long totalCount = favoriteSupplierService.getAssociatedBuyerCountForSupplier(input, SecurityLibrary.getLoggedInUserTenantId());
			data.setRecordsFiltered(totalCount);
			data.setRecordsTotal(totalCount);
		} catch (Exception e) {
			LOG.error("Error occurred while fetching associated buyer list:" + e.getMessage(), e);
		}
		return new ResponseEntity<TableData<RequestedAssociatedBuyerPojo>>(data, HttpStatus.OK);
	}

	@RequestMapping(value = "/viewRequestedAssociateBuyer/{buyerId}", method = RequestMethod.GET)
	private String viewReuestedToAssociateBuyer(@PathVariable("buyerId") String buyerId, Model model, RedirectAttributes redir) {
		LOG.info("Getting associate buyer details for user :" + SecurityLibrary.getLoggedInUser());
		try {
			RequestedAssociatedBuyerPojo requestAssociteBuyerObj = supplierAssociatedBuyerService.getRequestedAssociatedBuyerById(buyerId, SecurityLibrary.getLoggedInUserTenantId());
			if (requestAssociteBuyerObj != null) {
				List<IndustryCategory> industryCategoryList = supplierAssociatedBuyerService.getIndustryCategoriesById(requestAssociteBuyerObj.getId());
				if (CollectionUtil.isNotEmpty(industryCategoryList)) {
					requestAssociteBuyerObj.setCategories(industryCategoryList);
				}
				model.addAttribute("requestAssociteBuyerObj", requestAssociteBuyerObj);
				SupplierFormSubmition form = formSubmissionService.assignFormsPreQualifierForm(SecurityLibrary.getLoggedInUser(), buyerId);
				if (form != null) {
					constructFormSummaryForSupplierView(form, model);
					model.addAttribute("supplierForm", form);
					List<SupplierFormSubmitionAudit> supplierFormAuditList = supplierFormSubAuditService.getFormAuditByFormIdForSupplier(form.getId());
					model.addAttribute("supplierFormAuditList", supplierFormAuditList);
				}
			}
		} catch (Exception e) {
			LOG.error("Error while getting request details:" + e.getMessage(), e);
			redir.addFlashAttribute("error" + e.getMessage());
			return "redirect:/supplier/associateBuyerList";
		}
		return "viewRequestedAssociateBuyer";
	}

	@RequestMapping(value = "/requestToAssociateBuyer/{buyerId}", method = RequestMethod.GET)
	private String requestToAssociateBuyer(@PathVariable("buyerId") String buyerId, Model model, RedirectAttributes redir) {
		LOG.info("Getting request to associate buyer details for user :" + SecurityLibrary.getLoggedInUser());
		try {
			long count = favoriteSupplierService.isSupplierInBuyerFavList(buyerId, SecurityLibrary.getLoggedInUserTenantId());
			RequestedAssociatedBuyerPojo requestAssociteBuyerObj = supplierAssociatedBuyerService.getRequestedAssociatedBuyerById(buyerId, SecurityLibrary.getLoggedInUserTenantId());
			if (count == 0) {
				requestAssociteBuyerObj = supplierAssociatedBuyerService.getPublishedBuyerDetailsById(buyerId);
				List<IndustryCategory> industryCategory = supplierAssociatedBuyerService.getIndustryCategoryForTenant(buyerId);
				model.addAttribute("industryCategory", industryCategory);
				model.addAttribute("requestAssociteBuyerObj", requestAssociteBuyerObj);
				SupplierFormSubmition form = formSubmissionService.assignFormsPreQualifierForm(SecurityLibrary.getLoggedInUser(), buyerId);

				// SupplierFormSubmition form = formSubmissionService.findOnBoardingFormAvailable(buyerId,
				// SecurityLibrary.getLoggedInUserTenantId());
				if (form != null) {
					if (StringUtils.checkString(form.getId()).length() > 0) {
						constructFormSummaryForSupplierView(form, model);
					}
					model.addAttribute("supplierForm", form);
					List<SupplierFormSubmitionAudit> supplierFormAuditList = supplierFormSubAuditService.getFormAuditByFormIdForSupplier(form.getId());
					model.addAttribute("supplierFormAuditList", supplierFormAuditList);
				}

				return "requestToAssociateBuyer";
			} else {
				redir.addFlashAttribute("error", "Cannot sent Request");
				return "redirect:/supplier/associateBuyerList";
			}
		} catch (Exception e) {
			LOG.error("Error while getting request details:" + e.getMessage(), e);
			redir.addAttribute("error" + e.getMessage());
			return "redirect:/supplier/associateBuyerList";
		}
	}

	private void constructFormSummaryForSupplierView(SupplierFormSubmition submissionForm, Model model) {
		SupplierFormSubmissionItemPojo supplierFormSubmissionItemPojo = new SupplierFormSubmissionItemPojo();
		supplierFormSubmissionItemPojo.setFormId(submissionForm.getId());
		List<SupplierFormSubmissionItem> submissionList = formSubmissionService.findSupplierSubFormItemById(submissionForm.getId());

		List<SupplierFormSubItem> itemList = new ArrayList<SupplierFormSubItem>();
		for (SupplierFormSubmissionItem list : submissionList) {
			SupplierFormSubItem itemObj = new SupplierFormSubItem(list);
			List<SupplierFormItemAttachment> itemAttachment = formSubmissionService.findAllFormDocsByFormItemId(itemObj.getFormItem().getId());
			itemObj.setItemAttachment(itemAttachment);
			itemList.add(itemObj);
		}
		supplierFormSubmissionItemPojo.setItemList(itemList);
		model.addAttribute("supplierFormSubmissionItemPojo", supplierFormSubmissionItemPojo);
	}

	@PostMapping("/saveOnbaordFormAsDraft/{formId}")
	public String saveOnbaordFormAsDraft(@ModelAttribute("supplierFormSubmissionItemPojo") SupplierFormSubmissionItemPojo supplierFormSubmissionItemPojo, Model model, @PathVariable("formId") String formId, RedirectAttributes redir) {
		LOG.info("Form Id: " + formId);
		return saveSupplierSubmittionForm(supplierFormSubmissionItemPojo, formId, redir, false);
	}

	@PostMapping("/submitSupplierOnboardform/{formId}")
	public String submitSupplierOnboardform(@ModelAttribute("supplierFormSubmissionItemPojo") SupplierFormSubmissionItemPojo supplierFormSubmissionItemPojo, Model model, @PathVariable("formId") String formId, RedirectAttributes redir) {
		LOG.info("Form Id: " + formId);
		return saveSupplierSubmittionForm(supplierFormSubmissionItemPojo, formId, redir, true);
	}

	private String saveSupplierSubmittionForm(SupplierFormSubmissionItemPojo supplierFormSubmissionItemPojo, String formId, RedirectAttributes redir, boolean flag) {
		SupplierFormSubmition form = null;

		try {
			if (supplierFormSubmissionItemPojo.getItemList() != null) {
				List<SupplierFormSubItem> itemList = supplierFormSubmissionItemPojo.getItemList();
				LOG.info("Item List size : " + itemList.size());
				if (CollectionUtil.isNotEmpty(itemList)) {
					List<SupplierFormSubmissionItem> list = new ArrayList<SupplierFormSubmissionItem>();
					for (SupplierFormSubItem item : itemList) {
						item.setFormSub(formSubmissionService.getSupplierformById(formId));
						item.setFormItem(formService.getFormItembyFormItemId(item.getFormItem().getId()));
						SupplierFormSubmissionItem obj = new SupplierFormSubmissionItem(item);
						SupplierFormSubmissionItem subItemOpt = formSubmissionService.findFormSubmissionItem(formId, item.getId());
						if ((item.getAttachment() == null || (item.getAttachment() != null & item.getAttachment().getBytes().length == 0)) && StringUtils.checkString(subItemOpt.getFileName()).length() > 0) {
							obj.setFileData(subItemOpt.getFileData());
							obj.setFileName(subItemOpt.getFileName());
							obj.setContentType(subItemOpt.getContentType());
						}
						list.add(obj);
					}
					LOG.info("Item List size : " + list.size());
					form = formSubmissionService.updateSupplierForm(list, formId, SecurityLibrary.getLoggedInUser(), flag);
					if (flag == true) {
						redir.addFlashAttribute("success", messageSource.getMessage("supplier.submitted.success.supplierForm", new Object[] { form.getName() }, Global.LOCALE));
					} else {
						redir.addFlashAttribute("success", messageSource.getMessage("supplier.submitted.savedraft.supplierForm", new Object[] { form.getName() }, Global.LOCALE));
					}
				}
			} else {
				redir.addFlashAttribute("error", messageSource.getMessage("flasherror.while.submitting.supplierForm", new Object[] {}, Global.LOCALE));
			}
		} catch (Exception e) {
			redir.addFlashAttribute("error", messageSource.getMessage("supplier.form.error.submit", new Object[] { e.getMessage() }, Global.LOCALE));
			LOG.error("error while Submitting form " + e.getMessage(), e);
			return "requestToAssociateBuyer";
		}
		return "redirect:/supplier/requestToAssociateBuyer/" + form.getBuyer().getId();
	}

	@PostMapping("/saveViewOnbaordFormAsDraft/{formId}")
	public String saveViewOnbaordFormAsDraft(@ModelAttribute("supplierFormSubmissionItemPojo") SupplierFormSubmissionItemPojo supplierFormSubmissionItemPojo, Model model, @PathVariable("formId") String formId, RedirectAttributes redir) {
		LOG.info("Form Id: " + formId);
		return saveViewSupplierSubmittionForm(supplierFormSubmissionItemPojo, formId, redir, false);
	}

	@PostMapping("/submitSupplierViewOnboardform/{formId}")
	public String submitSupplierViewOnboardform(@ModelAttribute("supplierFormSubmissionItemPojo") SupplierFormSubmissionItemPojo supplierFormSubmissionItemPojo, Model model, @PathVariable("formId") String formId, RedirectAttributes redir) {
		LOG.info("Form Id: " + formId);
		return saveViewSupplierSubmittionForm(supplierFormSubmissionItemPojo, formId, redir, true);
	}

	private String saveViewSupplierSubmittionForm(SupplierFormSubmissionItemPojo supplierFormSubmissionItemPojo, String formId, RedirectAttributes redir, boolean flag) {
		SupplierFormSubmition form = null;

		try {
			if (supplierFormSubmissionItemPojo.getItemList() != null) {
				List<SupplierFormSubItem> itemList = supplierFormSubmissionItemPojo.getItemList();
				LOG.info("Item List size : " + itemList.size());
				if (CollectionUtil.isNotEmpty(itemList)) {
					List<SupplierFormSubmissionItem> list = new ArrayList<SupplierFormSubmissionItem>();
					for (SupplierFormSubItem item : itemList) {
						item.setFormSub(formSubmissionService.getSupplierformById(formId));
						item.setFormItem(formService.getFormItembyFormItemId(item.getFormItem().getId()));
						SupplierFormSubmissionItem obj = new SupplierFormSubmissionItem(item);
						SupplierFormSubmissionItem subItemOpt = formSubmissionService.findFormSubmissionItem(formId, item.getId());
						if ((item.getAttachment() == null || (item.getAttachment() != null & item.getAttachment().getBytes().length == 0)) && StringUtils.checkString(subItemOpt.getFileName()).length() > 0) {
							obj.setFileData(subItemOpt.getFileData());
							obj.setFileName(subItemOpt.getFileName());
							obj.setContentType(subItemOpt.getContentType());
						}
						list.add(obj);
					}
					LOG.info("Item List size : " + list.size());
					form = formSubmissionService.updateSupplierForm(list, formId, SecurityLibrary.getLoggedInUser(), flag);
					if (flag == true) {
						redir.addFlashAttribute("success", messageSource.getMessage("supplier.submitted.success.supplierForm", new Object[] { form.getName() }, Global.LOCALE));
					} else {
						redir.addFlashAttribute("success", messageSource.getMessage("supplier.submitted.savedraft.supplierForm", new Object[] { form.getName() }, Global.LOCALE));
					}
				}
			} else {
				redir.addFlashAttribute("error", messageSource.getMessage("flasherror.while.submitting.supplierForm", new Object[] {}, Global.LOCALE));
			}
		} catch (Exception e) {
			redir.addFlashAttribute("error", messageSource.getMessage("supplier.form.error.submit", new Object[] { e.getMessage() }, Global.LOCALE));
			LOG.error("error while Submitting form " + e.getMessage(), e);
			return "requestToAssociateBuyer";
		}
		return "redirect:/supplier/viewRequestedAssociateBuyer/" + form.getBuyer().getId();
	}

	
	@RequestMapping(value = "/sentRequestToAssociateBuyer", method = RequestMethod.POST)
	public ModelAndView sentRequestToAssociateBuyer(@ModelAttribute("requestAssociteBuyerObj") RequestedAssociatedBuyerPojo requestedAssociatedBuyerPojo, Model model, RedirectAttributes redir) {
		LOG.info("sending request to associate buyer for user :" + SecurityLibrary.getLoggedInUser());
		try {
			if (StringUtils.checkString(requestedAssociatedBuyerPojo.getBuyerId()).length() > 0) {
				long count = favoriteSupplierService.isSupplierInBuyerFavList(requestedAssociatedBuyerPojo.getBuyerId(), SecurityLibrary.getLoggedInUserTenantId());
				if (count == 0) {
					Buyer buyer = buyerService.findBuyerById(requestedAssociatedBuyerPojo.getBuyerId());
					if (buyer != null) {
						RequestedAssociatedBuyer requestBuyerObj = new RequestedAssociatedBuyer();
						requestBuyerObj.setBuyer(buyer);
						requestBuyerObj.setRequestedDate(new Date());
						requestBuyerObj.setStatus(RequestAssociateBuyerStatus.PENDING);
						if (StringUtils.checkString(requestedAssociatedBuyerPojo.getSupplierRemark()).length() > 0) {
							requestBuyerObj.setSupplierRemark(requestedAssociatedBuyerPojo.getSupplierRemark());
						}
						requestBuyerObj.setRequestedBy(SecurityLibrary.getLoggedInUser());
						if (requestedAssociatedBuyerPojo.getIndCat() != null) {
							String[] indCatIds = requestedAssociatedBuyerPojo.getIndCat();
							List<String> indCatIdList = Arrays.asList(indCatIds);
							if (CollectionUtil.isNotEmpty(indCatIdList)) {
								List<IndustryCategory> finalIndustryList = industryCategoryService.getAllIndustryCategoryOnlyByIds(indCatIdList);
								requestBuyerObj.setIndustryCategory(finalIndustryList);
							}
						}
						Supplier supplier = null;
						if (SecurityLibrary.getLoggedInUser().getSupplier() != null) {
							supplier = supplierService.findSuppById(SecurityLibrary.getLoggedInUserTenantId());
							requestBuyerObj.setSupplier(supplier);
						}
						supplierAssociatedBuyerService.saveOrUpdateAssociateRequest(requestBuyerObj, SecurityLibrary.getLoggedInUser());

						// sending email to associated buyer
						try {
							TimeZone timeZone = TimeZone.getDefault();
							String strTimerZone = supplierSettingsService.getSupplierTimeZoneByTenantId(supplier.getId());
							if (StringUtils.checkString(strTimerZone).length() > 0) {
								timeZone = TimeZone.getTimeZone(strTimerZone);
							}
							supplierAssociatedBuyerService.sendEmailToAssociatedBuyer(supplier, requestBuyerObj.getSupplierRemark(), buyer, timeZone);
							LOG.info("successfully sent request to " + buyer.getCompanyName() + "  by " + supplier.getCompanyName());
						} catch (Exception e) {
							LOG.error("Error while sending email to requested associated buyer:" + e.getMessage(), e);
						}
						redir.addFlashAttribute("success", messageSource.getMessage("request.sent.success", new Object[] { requestBuyerObj.getBuyer().getCompanyName() }, Global.LOCALE));

					}
				} else {
					redir.addFlashAttribute("error", "Cannot sent request");
				}
			}
		} catch (Exception e) {
			LOG.error("Error while sending request to buyer:" + e.getMessage(), e);
			RequestedAssociatedBuyerPojo requestAssociteBuyerObj = supplierAssociatedBuyerService.getRequestedAssociatedBuyerById(requestedAssociatedBuyerPojo.getBuyerId(), SecurityLibrary.getLoggedInUserTenantId());
			if (requestAssociteBuyerObj == null) {
				requestAssociteBuyerObj = supplierAssociatedBuyerService.getPublishedBuyerDetailsById(requestedAssociatedBuyerPojo.getBuyerId());
				List<IndustryCategory> industryCategory = supplierAssociatedBuyerService.getIndustryCategoryForTenant(requestedAssociatedBuyerPojo.getBuyerId());
				model.addAttribute("industryCategory", industryCategory);
				model.addAttribute("requestAssociteBuyerObj", requestAssociteBuyerObj);
			}
			model.addAttribute("error" + e.getMessage());
			return new ModelAndView("requestToAssociateBuyer");
		}
		return new ModelAndView("redirect:/supplier/associateBuyerList");

	}

	// PH-1179 adding this request
	@RequestMapping(value = "requestToAssociateBuyer/searchIndustryCategories/{buyerId}", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<List<IndustryCategory>> searchIndustryCategoriesForSupplierEngagemnt(@PathVariable("buyerId") String buyerId, @RequestParam("search") String search) {
		LOG.info("inside search ic dialog:" + search);
		List<IndustryCategory> icList = industryCategoryService.findIndustryCategoryByNameAndTenantId(search, buyerId);
		return new ResponseEntity<List<IndustryCategory>>(icList, HttpStatus.OK);
	}
}