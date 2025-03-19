/**
 * 
 */
package com.privasia.procurehere.web.controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.validation.Valid;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomCollectionEditor;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.privasia.procurehere.core.entity.AccessRights;
import com.privasia.procurehere.core.entity.User;
import com.privasia.procurehere.core.entity.UserRole;
import com.privasia.procurehere.core.enums.PlanType;
import com.privasia.procurehere.core.enums.Status;
import com.privasia.procurehere.core.enums.TenantType;
import com.privasia.procurehere.core.pojo.TableData;
import com.privasia.procurehere.core.pojo.TableDataInput;
import com.privasia.procurehere.core.pojo.UserRolePojo;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.SecurityLibrary;
import com.privasia.procurehere.core.utils.StringUtils;
import com.privasia.procurehere.service.AccessRightsService;
import com.privasia.procurehere.service.BuyerSubscriptionService;
import com.privasia.procurehere.service.UserRoleService;
import com.privasia.procurehere.service.UserService;
import com.privasia.procurehere.web.editors.AccessRightsEditor;

/**
 * @author Ravi
 */
@Controller
@RequestMapping("/admin")
public class UserRoleController implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4923900940700961974L;

	private static final Logger LOG = LogManager.getLogger(Global.ADMIN_LOG);

	@Resource
	MessageSource messageSource;

	@Autowired
	UserRoleService userRoleService;

	@Autowired
	AccessRightsService accessControlListService;

	@Autowired
	UserService userService;

	@Autowired
	AccessRightsEditor accessRightsEditor;

	@Autowired
	BuyerSubscriptionService buyerSubscriptionService;

	@ModelAttribute("statusList")
	public List<Status> getStatusList() {
		return Arrays.asList(Status.values());
	}

	/**
	 * @param binder
	 */
	@InitBinder
	public void initBinder(WebDataBinder binder) {

		StringTrimmerEditor stringtrimmer = new StringTrimmerEditor(true);
		binder.registerCustomEditor(String.class, stringtrimmer);
		binder.registerCustomEditor(AccessRights.class, accessRightsEditor);

		binder.registerCustomEditor(List.class, "userRoleAccess", new CustomCollectionEditor(List.class) {
			protected Object convertElement(Object element) {
				if (element != null) {
					String id = (String) element;
					AccessRights ar = accessControlListService.getAccessRightsById(id);
					return ar;
				}
				return null;
			}
		});
	}

	/*
	 * NEW Code for User Role Controller
	 * @author Javed & kapil
	 */

	@RequestMapping(path = "/role", method = RequestMethod.GET)
	public ModelAndView createUserRole(@ModelAttribute UserRole userRole, Model model) {
		LOG.info(" GET Create UserRole called");
		model.addAttribute("userRole", new UserRole());
		model.addAttribute("btnValue", messageSource.getMessage("application.create", new Object[] {}, LocaleContextHolder.getLocale()));

		User loggedInUser = SecurityLibrary.getLoggedInUser();
		List<AccessRights> aclList = null;
		if (loggedInUser.getOwner() != null) {
			LOG.info(" getOwner() Create UserRole ");
			aclList = userRoleService.getParentAccessControlListForType(TenantType.OWNER, false);
		} else if (loggedInUser.getSupplier() != null) {
			LOG.info(" getSupplier() Create UserRole ");
			aclList = userRoleService.getParentAccessControlListForType(TenantType.SUPPLIER, false);
		} else if (loggedInUser.getFinanceCompany() != null) {
			LOG.info(" getFinanceCompany() Create UserRole ");
			aclList = userRoleService.getParentAccessControlListForType(TenantType.FINANCE_COMPANY, false);
		} else if (loggedInUser.getBuyer() != null) {
			LOG.info(" getBuyer() Create UserRole ");
			PlanType planType = buyerSubscriptionService.getBuyerSubscriptionPlanTypeByBuyerID(loggedInUser.getBuyer().getId());
			boolean isEventBasedSubscription = false;
			LOG.info("planType : " + planType);
			if (planType != null && planType == PlanType.PER_EVENT) {
				isEventBasedSubscription = true;
			}
			aclList = userRoleService.getParentAccessControlListForType(TenantType.BUYER, isEventBasedSubscription);
		}
		LOG.info("ACL LIST  : " + aclList);
		model.addAttribute("userRoleAccess", aclList);
		return new ModelAndView("role", "userRoleObj", new UserRole());
	}

	@RequestMapping(path = "/role", method = RequestMethod.POST)
	public ModelAndView saveUserRole(@Valid @ModelAttribute("userRoleObj") UserRole userRole, Model model, BindingResult result, RedirectAttributes redir) {
		LOG.info("Save UserRole called");
		LOG.info("Save User Role ");
		List<String> errMessages = new ArrayList<String>();

		try {
			if (result.hasErrors()) {
				for (ObjectError err : result.getAllErrors()) {
					errMessages.add(err.getDefaultMessage());
					LOG.error("asdasdasdasdasd   " + err.getDefaultMessage());
				}
				model.addAttribute("error", errMessages);
				return new ModelAndView("redirect:role");

			} else {
				if (doValidate(userRole)) {
					if (StringUtils.checkString(userRole.getId()).length() == 0) {

						userRole.setTenantId(SecurityLibrary.getLoggedInUserTenantId());

						LOG.info("in side save " + SecurityLibrary.getLoggedInUser().getTenantId());
						userRole.setCreatedBy(SecurityLibrary.getLoggedInUser());
						userRole.setCreatedDate(new Date());
						userRoleService.saveRole(userRole, SecurityLibrary.getLoggedInUser());
						LOG.info("in side save" + userRole);

						redir.addFlashAttribute("success", messageSource.getMessage("userRole.save.success", new Object[] { userRole.getRoleName() }, Global.LOCALE));
						LOG.info("create userRole Called" + SecurityLibrary.getLoggedInUser());
					} else {
						UserRole persistObj = userRoleService.findUserRoleById(userRole.getId());

						persistObj.setStatus(userRole.getStatus());
						persistObj.setRoleDescription(userRole.getRoleDescription());
						persistObj.setModifiedBy(SecurityLibrary.getLoggedInUser());
						persistObj.setModifiedDate(new Date());
						persistObj.setAccessControlList(userRole.getAccessControlList());
						userRoleService.updateRole(persistObj, SecurityLibrary.getLoggedInUser());
						redir.addFlashAttribute("success", messageSource.getMessage("userRole.update.success", new Object[] { userRole.getRoleName() }, Global.LOCALE));
						LOG.info("update userRole Called");
					}
				} else {
					redir.addFlashAttribute("error", messageSource.getMessage("userrole.error.save.duplicate.entry", new Object[] { userRole.getRoleName() }, Global.LOCALE));
					model.addAttribute("btnValue", messageSource.getMessage("application.create", new Object[] {}, LocaleContextHolder.getLocale()));
					return new ModelAndView("redirect:role");
				}
			}
		} catch (Exception e) {
			LOG.error("Error While Save the UserRole" + e.getMessage(), e);
			redir.addFlashAttribute("error", messageSource.getMessage("userRole.error.save", new Object[] { e.getMessage() }, Global.LOCALE));
			model.addAttribute("btnValue", messageSource.getMessage("application.create", new Object[] {}, LocaleContextHolder.getLocale()));
			return new ModelAndView("redirect:role");
		}

		return new ModelAndView("redirect:listRole");
	}

	private boolean doValidate(UserRole userRole) {

		LOG.info("  doValidate userRole dddddd");
		boolean validate = true;
		if (userRoleService.isExists(userRole, SecurityLibrary.getLoggedInUserTenantId())) {

			LOG.info("  doValidate userRole ifisExists(userRole");

			validate = false;
		}
		return validate;
	}

	@RequestMapping(path = "/listRole", method = RequestMethod.GET)
	public String roleList(Model model) throws JsonProcessingException {
		LOG.info("All UsesrRoles ");
		List<UserRolePojo> userRoleList = userRoleService.getAllUserRolePojo(SecurityLibrary.getLoggedInUserTenantId());

		ObjectMapper mapper = new ObjectMapper();
		model.addAttribute("createdBy", SecurityLibrary.getLoggedInUserLoginId());
		model.addAttribute("userRoleList", mapper.writeValueAsString(userRoleList));

		return "listRole";
	}

	@RequestMapping(path = "/editRole", method = RequestMethod.GET)
	public ModelAndView editRole(@RequestParam String id, Model model) {
		LOG.info(">>>/editRole");
		UserRole userRole = userRoleService.getUserRoleById(id);
		model.addAttribute("btnValue", messageSource.getMessage("application.update", new Object[] {}, LocaleContextHolder.getLocale()));
		model.addAttribute("ice", "true");
		User loggedInUser = SecurityLibrary.getLoggedInUser();
		List<AccessRights> aclList = null;
		if (loggedInUser.getOwner() != null) {
			LOG.info(">>>>>>>>>> loggedInUser.getOwner() /editRole");
			aclList = userRoleService.getParentAccessControlListForType(TenantType.OWNER, false);
		} else if (loggedInUser.getFinanceCompany() != null) {
			LOG.info(">>>>>>>>>> loggedInUser.getFinanceCompany() /editRole");
			aclList = userRoleService.getParentAccessControlListForType(TenantType.FINANCE_COMPANY, false);
		} else if (loggedInUser.getSupplier() != null) {
			LOG.info(">>>>>>>>>> loggedInUser.getSupplier() /editRole");
			aclList = userRoleService.getParentAccessControlListForType(TenantType.SUPPLIER, false);
		} else if (loggedInUser.getBuyer() != null) {
			LOG.info(">>>>>>>>>> loggedInUser.getBuyer() /editRole");
			PlanType planType = buyerSubscriptionService.getBuyerSubscriptionPlanTypeByBuyerID(loggedInUser.getBuyer().getId());
			boolean isEventBasedSubscription = false;
			LOG.info("planType : " + planType);
			if (planType != null && planType == PlanType.PER_EVENT) {
				isEventBasedSubscription = true;
			}
			aclList = userRoleService.getParentAccessControlListForType(TenantType.BUYER, isEventBasedSubscription);
		}
		model.addAttribute("userRoleAccess", aclList);
		model.addAttribute("id", userRole.getId());
		return new ModelAndView("role", "userRoleObj", userRole);
	}

	@RequestMapping(path = "/deleteRole", method = RequestMethod.GET)
	public String deleteRole(@RequestParam String id, Model model) throws JsonProcessingException {
		LOG.info("Delete the userRole");
		UserRole userRole = userRoleService.getUserRoleById(id);
		try {
			userRoleService.deleteRole(userRole, SecurityLibrary.getLoggedInUser());
			model.addAttribute("success", messageSource.getMessage("userRole.success.delete", new Object[] { userRole.getRoleName() }, Global.LOCALE));
		} catch (DataIntegrityViolationException e) {
			LOG.error("Error while deleting User Role , " + e.getMessage(), e);
//			model.addAttribute("error", "Cannot delete role " + userRole.getRoleName() + " as it is in use.");
			model.addAttribute("error", messageSource.getMessage("cant.delete.role.inuse", new Object[] {userRole.getRoleName()!=null?userRole.getRoleName():""}, Global.LOCALE));
		} catch (Exception e) {
			LOG.error("Error while deleting UserRole [ " + userRole.getRoleName() + " ]" + e.getMessage(), e);
			model.addAttribute("error", messageSource.getMessage("userRole.error.delete", new Object[] { userRole.getRoleName() }, Global.LOCALE));
		}
		List<UserRolePojo> userRoleList = userRoleService.getAllUserRolePojo(SecurityLibrary.getLoggedInUserTenantId());
		ObjectMapper mapper = new ObjectMapper();
		model.addAttribute("userRoleList", mapper.writeValueAsString(userRoleList));
		return "listRole";
	}

	@RequestMapping(value = "findAccessRight", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<List<AccessRights>> findAccessRights(@RequestParam("aclValue") String aclValue) {
		List<AccessRights> accessRightsList = null;
		try {

			LOG.info("AccessRight  " + aclValue);
			accessRightsList = accessControlListService.findChildAccessForId(aclValue);
		} catch (Exception e) {
			LOG.error("Error while getting states for Country " + e.getMessage(), e);
			HttpHeaders headers = new HttpHeaders();

			headers.add("error", messageSource.getMessage("userrole.error.country.state", new Object[] { e.getMessage() }, Global.LOCALE));
			return new ResponseEntity<List<AccessRights>>(headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		return new ResponseEntity<List<AccessRights>>(accessRightsList, HttpStatus.OK);
	}

	@RequestMapping(path = "/userRoleListData", method = RequestMethod.GET)
	public ResponseEntity<TableData<UserRole>> countryListData(TableDataInput input) throws JsonProcessingException {
		try {
			LOG.info("Start : " + input.getStart() + " Length : " + input.getLength() + " Sort : " + input.getSort());
			LOG.info(">>>>>>>>>>>>> Table Data Input : " + input.toString());

			TableData<UserRole> data = new TableData<UserRole>(userRoleService.findAllUserRoleListForTenant(input, SecurityLibrary.getLoggedInUserTenantId()));
			data.setDraw(input.getDraw());
			long totalFilterCount = userRoleService.findTotalFilteredUserRoleListForTenant(input, SecurityLibrary.getLoggedInUserTenantId());
			data.setRecordsFiltered(totalFilterCount);
			long totalCount = userRoleService.findTotalUserRoleListForTenant(SecurityLibrary.getLoggedInUserTenantId());
			data.setRecordsTotal(totalCount);
			return new ResponseEntity<TableData<UserRole>>(data, HttpStatus.OK);
		} catch (Exception e) {
			LOG.error("Error fetching UserRole list : " + e.getMessage(), e);
			HttpHeaders headers = new HttpHeaders();
			headers.add("error", "Error fetching UserRole list : " + e.getMessage());
			return new ResponseEntity<TableData<UserRole>>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

}
