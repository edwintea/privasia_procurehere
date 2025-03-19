package com.privasia.procurehere.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.privasia.procurehere.core.entity.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.privasia.procurehere.core.dao.BuyerAuditTrailDao;
import com.privasia.procurehere.core.dao.PrTemplateDao;
import com.privasia.procurehere.core.dao.UserDao;
import com.privasia.procurehere.core.enums.AuditTypes;
import com.privasia.procurehere.core.enums.ModuleType;
import com.privasia.procurehere.core.pojo.PrTemplatePojo;
import com.privasia.procurehere.core.pojo.TableDataInput;
import com.privasia.procurehere.core.supplier.dao.BuyerDao;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.SecurityLibrary;
import com.privasia.procurehere.service.PrTemplateService;
import com.privasia.procurehere.service.UserService;

/**
 * @author parveen
 */
@Service
@Transactional(readOnly = true)
public class PrTemplateServiceImpl implements PrTemplateService {

	@SuppressWarnings("unused")
	private static Logger LOG = LogManager.getLogger(PrTemplateServiceImpl.class);

	@Autowired
	PrTemplateDao prTemplateDao;

	@Autowired
	BuyerDao buyerDao;

	@Autowired
	UserDao userDao;

	@Autowired
	UserService userService;

	@Autowired
	PrTemplateService prTemplateService;
	
	@Autowired
	BuyerAuditTrailDao buyerAuditTrailDao;

	@Override
	public List<PrTemplate> findTemplatesForTenant(String tenantId, TableDataInput tableParams, String userId) {
		List<PrTemplate> returnList = new ArrayList<PrTemplate>();

		List<PrTemplate> list = prTemplateDao.findTemplatesForTenant(tenantId, tableParams, userId);
		for (PrTemplate rt : list) {
			returnList.add(rt.createShallowCopy());
		}
		return returnList;
	}

	@Override
	public boolean isExists(PrTemplate template) {
		return prTemplateDao.isExists(template);
	}

	@Override
	public List<PrTemplate> findAllActiveTemplatesForTenant(String tenantId) {
		return prTemplateDao.findAllActiveTemplatesForTenant(tenantId);
	}

	@Override
	@Transactional(readOnly = false)
	public PrTemplate save(PrTemplate template) {
		BuyerAuditTrail ownerAuditTrail = new BuyerAuditTrail(AuditTypes.CREATE, "PR Template '"+template.getTemplateName()+ "' created", template.getCreatedBy().getTenantId(), template.getCreatedBy(), new Date(), ModuleType.PRTemplate);
		buyerAuditTrailDao.save(ownerAuditTrail);
		return prTemplateDao.saveOrUpdate(template);
	}

	@Override
	@Transactional(readOnly = false)
	public void update(PrTemplate template) {
		BuyerAuditTrail ownerAuditTrail = new BuyerAuditTrail(AuditTypes.UPDATE, "PR Template '"+template.getTemplateName()+ "' updated", template.getModifiedBy().getTenantId(), template.getModifiedBy(), new Date(), ModuleType.PRTemplate);
		buyerAuditTrailDao.save(ownerAuditTrail);
		prTemplateDao.update(template);
	}

	@Override
	@Transactional(readOnly = false)
	public void delete(PrTemplate template) {
		BuyerAuditTrail ownerAuditTrail = new BuyerAuditTrail(AuditTypes.DELETE, "PR Template '"+template.getTemplateName()+ "' deleted", template.getModifiedBy().getTenantId(), template.getModifiedBy(), new Date(), ModuleType.PRTemplate);
		buyerAuditTrailDao.save(ownerAuditTrail);
		prTemplateDao.delete(template);
	}

	@Override
	public PrTemplate getPrTemplateById(String id) {
		PrTemplate template = prTemplateDao.findById(id);
		for (PrTemplateApproval approval : template.getApprovals()) {
			for (PrTemplateApprovalUser approvalUser : approval.getApprovalUsers()) {
				approvalUser.getRemarks();
			}
		}
		if (CollectionUtil.isNotEmpty(template.getTeamMembers())) {
			for (TemplatePrTeamMembers team : template.getTeamMembers()) {
				team.getTeamMemberType();
				if (team.getUser() != null) {
					team.getUser().getId();
				}
			}
		}
		return template;
	}

	@Override
	public long findTotalTemplatesForTenant(String tenantId) {
		return prTemplateDao.findTotalTemplatesForTenant(tenantId);
	}

	@Override
	public long findTotalFilteredTemplatesForTenant(String tenantId, TableDataInput tableParams, String userId) {

		return prTemplateDao.findTotalFilteredTemplatesForTenant(tenantId, tableParams, userId);
	}

	@Transactional
	@Override
	public PrTemplate getPrTemplateForEditById(String templateId, String tenantId) {
		PrTemplate template = prTemplateDao.getPrTemplateForEditById(templateId, tenantId);
		LOG.info("entered getPrTemplateForEditById ");
		if (template != null) {
			if (template.getApprovals() != null) {
				for (PrTemplateApproval approval : template.getApprovals()) {
					for (PrTemplateApprovalUser approvalUser : approval.getApprovalUsers()) {
						approvalUser.getRemarks(); // Initialize approvalUsers collection
					}
				}
			}

			if (CollectionUtil.isNotEmpty(template.getTeamMembers())) {
				LOG.info("?>>>>>>>>>>>>>>> checking team members ");
				for (TemplatePrTeamMembers team : template.getTeamMembers()) {
					team.getTeamMemberType(); // Initialize teamMembers collection
					if (team.getUser() != null) {
						team.getUser().getLoginId(); // Initialize User entity associated with the team member
					}
				}
			}

			// Explicitly initialize poApprovals to avoid LazyInitializationException
			if (CollectionUtil.isNotEmpty(template.getPoApprovals())) {
				LOG.info("?>>>>>>>>>>>>>>> checking po approvals ");
				for (PoTemplateApproval poApproval : template.getPoApprovals()) {
					for (PoTemplateApprovalUser approvalUser : poApproval.getApprovalUsers()) {
						approvalUser.getRemarks(); // Initialize approvalUsers collection
					}
					poApproval.getLevel(); // Initialize the poApprovals collection
				}
			}

			// Initialize grApprovals
			if (CollectionUtil.isNotEmpty(template.getGrApprovals())) {
				LOG.info("?>>>>>>>>>>>>>>> checking grapprovals ");
				for (GrTemplateApproval grApproval : template.getGrApprovals()) {

					for (GrTemplateApprovalUser approvalUser : grApproval.getApprovalUsers()) {
						approvalUser.getRemarks(); // Initialize approvalUsers collection
					}
					grApproval.getLevel(); // Adjust as necessary to initialize GrTemplateApproval
				}
			}

			if (CollectionUtil.isNotEmpty(template.getInvoiceApprovals())) {
				LOG.info("?>>>>>>>>>>>>>>> checking invoice approvals ");
				for (InvoiceTemplateApproval invoiceApproval : template.getInvoiceApprovals()) {
					for (InvoiceTemplateApprovalUser approvalUser : invoiceApproval.getApprovalUsers()) {
						approvalUser.getRemarks(); // Initialize approvalUsers collection
					}
					invoiceApproval.getLevel(); // Adjust as necessary to initialize InvoiceTemplateApproval
				}
			}
		}

		return template;
	}


	@Override
	public List<PrTemplate> findByTemplateNameForTenant(String searchValue, String tenantId, String userId, String pageNo) {
		return prTemplateDao.findByTemplateNameForTenant(searchValue, tenantId, userId, pageNo);
	}

	@Override
	public Integer findAssignedTemplateCount(String templateId) {
		return prTemplateDao.findAssignedTemplateCount(templateId);

	}

	@Override
	public List<PrTemplate> findAllActiveTemplatesForTenantAndUser(String tenantId, String userId, TableDataInput input) {
		return prTemplateDao.findAllActiveTemplatesForTenantAndUser(tenantId, userId, input);
	}

	@Override
	@Transactional(readOnly = false)
	public PrTemplate copyTemplate(PrTemplate oldTemplate, PrTemplate newTemplate, User createdBy) {

		if (CollectionUtil.isNotEmpty(oldTemplate.getFields())) {
			List<PrTemplateField> templateFields = new ArrayList<PrTemplateField>();
			for (PrTemplateField tempField : oldTemplate.getFields()) {
				PrTemplateField temp = new PrTemplateField();
				temp.setDefaultValue(tempField.getDefaultValue());
				temp.setFieldName(tempField.getFieldName());
				temp.setOptional(tempField.getOptional());
				temp.setReadOnly(tempField.getReadOnly());
				temp.setTemplate(newTemplate);
				temp.setVisible(tempField.getVisible());
				temp.setBuyer(tempField.getBuyer());
				templateFields.add(temp);
			}
			newTemplate.setFields(templateFields);
		}
		newTemplate.setTemplateFieldBinding(oldTemplate.getTemplateFieldBinding());
		newTemplate.setApprovalVisible(oldTemplate.getApprovalVisible());
		newTemplate.setApprovalReadOnly(oldTemplate.getApprovalReadOnly());
		newTemplate.setApprovalOptional(oldTemplate.getApprovalOptional());
		newTemplate.setMinimumApprovalCount(oldTemplate.getMinimumApprovalCount());

		newTemplate.setApprovalPoVisible(oldTemplate.getApprovalPoVisible());
		newTemplate.setApprovalPoReadOnly(oldTemplate.getApprovalPoReadOnly());
		newTemplate.setApprovalPoOptional(oldTemplate.getApprovalPoOptional());
		newTemplate.setMinimumPoApprovalCount(oldTemplate.getMinimumPoApprovalCount());

		newTemplate.setApprovalGrVisible(oldTemplate.getApprovalGrVisible());
		newTemplate.setApprovalGrReadOnly(oldTemplate.getApprovalGrReadOnly());
		newTemplate.setApprovalGrOptional(oldTemplate.getApprovalGrOptional());
		newTemplate.setMinimumGrApprovalCount(oldTemplate.getMinimumGrApprovalCount());

		newTemplate.setApprovalInvoiceVisible(oldTemplate.getApprovalInvoiceVisible());
		newTemplate.setApprovalInvoiceReadOnly(oldTemplate.getApprovalInvoiceReadOnly());
		newTemplate.setApprovalInvoiceOptional(oldTemplate.getApprovalInvoiceOptional());
		newTemplate.setMinimumInvoiceApprovalCount(oldTemplate.getMinimumInvoiceApprovalCount());

		newTemplate.setLockBudget(oldTemplate.getLockBudget());


		// copy approval from template
		if (CollectionUtil.isNotEmpty(oldTemplate.getApprovals())) {
			List<PrTemplateApproval> approvalList = new ArrayList<PrTemplateApproval>();
			for (PrTemplateApproval templateApproval : oldTemplate.getApprovals()) {
				PrTemplateApproval newTemplateApproval = new PrTemplateApproval();
				newTemplateApproval.setApprovalType(templateApproval.getApprovalType());
				newTemplateApproval.setLevel(templateApproval.getLevel());
				newTemplateApproval.setPrTemplate(newTemplate);
				if (CollectionUtil.isNotEmpty(templateApproval.getApprovalUsers())) {
					List<PrTemplateApprovalUser> tempApprovalList = new ArrayList<PrTemplateApprovalUser>();
					for (PrTemplateApprovalUser templateApprovalUser : templateApproval.getApprovalUsers()) {
						PrTemplateApprovalUser approvalUser = new PrTemplateApprovalUser();
						approvalUser.setApprovalStatus(templateApprovalUser.getApprovalStatus());
						approvalUser.setApproval(newTemplateApproval);
						approvalUser.setRemarks(templateApprovalUser.getRemarks());
						approvalUser.setUser(templateApprovalUser.getUser());
						tempApprovalList.add(approvalUser);
					}
					newTemplateApproval.setApprovalUsers(tempApprovalList);
				}
				approvalList.add(newTemplateApproval);
			}
			newTemplate.setApprovals(approvalList);
		}

		// copy approval from template
		if (CollectionUtil.isNotEmpty(oldTemplate.getPoApprovals())) {
			List<PoTemplateApproval> approvalList = new ArrayList<PoTemplateApproval>();
			for (PoTemplateApproval templateApproval : oldTemplate.getPoApprovals()) {
				PoTemplateApproval newTemplateApproval = new PoTemplateApproval();
				newTemplateApproval.setApprovalType(templateApproval.getApprovalType());
				newTemplateApproval.setLevel(templateApproval.getLevel());
				newTemplateApproval.setPrTemplate(newTemplate);
				if (CollectionUtil.isNotEmpty(templateApproval.getApprovalUsers())) {
					List<PoTemplateApprovalUser> tempApprovalList = new ArrayList<PoTemplateApprovalUser>();
					for (PoTemplateApprovalUser templateApprovalUser : templateApproval.getApprovalUsers()) {
						PoTemplateApprovalUser approvalUser = new PoTemplateApprovalUser();
						approvalUser.setApprovalStatus(templateApprovalUser.getApprovalStatus());
						approvalUser.setPoApproval(newTemplateApproval);
						approvalUser.setRemarks(templateApprovalUser.getRemarks());
						approvalUser.setUser(templateApprovalUser.getUser());
						tempApprovalList.add(approvalUser);
					}
					newTemplateApproval.setApprovalUsers(tempApprovalList);
				}
				approvalList.add(newTemplateApproval);
			}
			newTemplate.setPoApprovals(approvalList);
		}

		// copy approval from template
		if (CollectionUtil.isNotEmpty(oldTemplate.getGrApprovals())) {
			List<GrTemplateApproval> approvalList = new ArrayList<GrTemplateApproval>();
			for (GrTemplateApproval templateApproval : oldTemplate.getGrApprovals()) {
				GrTemplateApproval newTemplateApproval = new GrTemplateApproval();
				newTemplateApproval.setApprovalType(templateApproval.getApprovalType());
				newTemplateApproval.setLevel(templateApproval.getLevel());
				newTemplateApproval.setPrTemplate(newTemplate);
				if (CollectionUtil.isNotEmpty(templateApproval.getApprovalUsers())) {
					List<GrTemplateApprovalUser> tempApprovalList = new ArrayList<GrTemplateApprovalUser>();
					for (GrTemplateApprovalUser templateApprovalUser : templateApproval.getApprovalUsers()) {
						GrTemplateApprovalUser approvalUser = new GrTemplateApprovalUser();
						approvalUser.setApprovalStatus(templateApprovalUser.getApprovalStatus());
						approvalUser.setGrApproval(newTemplateApproval);
						approvalUser.setRemarks(templateApprovalUser.getRemarks());
						approvalUser.setUser(templateApprovalUser.getUser());
						tempApprovalList.add(approvalUser);
					}
					newTemplateApproval.setApprovalUsers(tempApprovalList);
				}
				approvalList.add(newTemplateApproval);
			}
			newTemplate.setGrApprovals(approvalList);
		}

		// copy approval from template
		if (CollectionUtil.isNotEmpty(oldTemplate.getInvoiceApprovals())) {
			List<InvoiceTemplateApproval> approvalList = new ArrayList<InvoiceTemplateApproval>();
			for (InvoiceTemplateApproval templateApproval : oldTemplate.getInvoiceApprovals()) {
				InvoiceTemplateApproval newTemplateApproval = new InvoiceTemplateApproval();
				newTemplateApproval.setApprovalType(templateApproval.getApprovalType());
				newTemplateApproval.setLevel(templateApproval.getLevel());
				newTemplateApproval.setPrTemplate(newTemplate);
				if (CollectionUtil.isNotEmpty(templateApproval.getApprovalUsers())) {
					List<InvoiceTemplateApprovalUser> tempApprovalList = new ArrayList<InvoiceTemplateApprovalUser>();
					for (InvoiceTemplateApprovalUser templateApprovalUser : templateApproval.getApprovalUsers()) {
						InvoiceTemplateApprovalUser approvalUser = new InvoiceTemplateApprovalUser();
						approvalUser.setApprovalStatus(templateApprovalUser.getApprovalStatus());
						approvalUser.setInvoiceApproval(newTemplateApproval);
						approvalUser.setRemarks(templateApprovalUser.getRemarks());
						approvalUser.setUser(templateApprovalUser.getUser());
						tempApprovalList.add(approvalUser);
					}
					newTemplateApproval.setApprovalUsers(tempApprovalList);
				}
				approvalList.add(newTemplateApproval);
			}
			newTemplate.setInvoiceApprovals(approvalList);
		}

		// copy Team Members from template
		List<TemplatePrTeamMembers> teamMembers = new ArrayList<TemplatePrTeamMembers>();
		if (CollectionUtil.isNotEmpty(oldTemplate.getTeamMembers())) {
			for (TemplatePrTeamMembers team : oldTemplate.getTeamMembers()) {
				TemplatePrTeamMembers newTeamMembers = new TemplatePrTeamMembers();
				newTeamMembers.setTeamMemberType(team.getTeamMemberType());
				newTeamMembers.setUser(team.getUser());
				newTeamMembers.setPrTemplate(newTemplate);
				teamMembers.add(newTeamMembers);
			}
			newTemplate.setTeamMembers(teamMembers);
		}

		newTemplate.setCreatedBy(createdBy);
		newTemplate.setCreatedDate(new Date());

		newTemplate = prTemplateDao.saveOrUpdate(newTemplate);
		
		try {
			BuyerAuditTrail ownerAuditTrail = new BuyerAuditTrail(AuditTypes.CREATE, "PR Template '"+newTemplate.getTemplateName()+ "' created", newTemplate.getCreatedBy().getTenantId(), newTemplate.getCreatedBy(), new Date(), ModuleType.PRTemplate);
			buyerAuditTrailDao.save(ownerAuditTrail);
		} catch (Exception e) {
			LOG.info("Error in creating PRAudit");
		}
		
		
		List<String> assignedUserId = prTemplateService.getTemplateByUserIdAndTemplateId(oldTemplate.getId(), SecurityLibrary.getLoggedInUserTenantId());
		if (CollectionUtil.isNotEmpty(assignedUserId)) {
			for (String assgnedUser : assignedUserId) {
				User user = userService.getUsersForRfxById(assgnedUser);
				List<PrTemplate> assignedTemplateList = user.getAssignedPrTemplates();
				assignedTemplateList.add(newTemplate);
				user.setAssignedPrTemplates(assignedTemplateList);
				userService.updateUser(user);
			}
		}
		LOG.info("=======pr Template============" + newTemplate.getId() + "===========" + newTemplate.getTemplateName());
		return newTemplate;
	}

	@Override
	public List<PrTemplate> findAllPrTemplatesForTenantAndUser(String tenantId) {
		return prTemplateDao.findAllPrTemplatesForTenantAndUser(tenantId);
	}

	@Override
	public List<PrTemplatePojo> findTemplatesForTenantId(String tenantId, TableDataInput tableParams, String userId) {
		return prTemplateDao.findTemplatesForTenantId(tenantId, tableParams, userId);
	}

	@Override
	public List<User> getAllUsers(String tempId) {
		return prTemplateDao.getAllUsers(tempId);
	}

	@Override
	public List<String> getTemplateByUserIdAndTemplateId(String userId, String loggedInUserTenantId) {
		return prTemplateDao.getTemplateByUserIdAndTemplateId(userId, loggedInUserTenantId);
	}

	@Override
	public void deleteusersForTemplate(String prTemplateId) {
		prTemplateDao.deleteusersForTemplate(prTemplateId);
	}

	@Override
	public boolean validateContractItemSetting(String id) {
		return prTemplateDao.validateContractItemSetting(id);
	}

	@Override
	public long getTemplateCountBySearchValueForTenant(String searchValue, String tenantId, String userId) {
		return prTemplateDao.getTotalTemplateCountForTenant(searchValue, tenantId, userId);
	}

	@Override
	public List<PrTemplate> findTemplatesByTemplateNameForTenantId(String searchValue, Integer pageNo, Integer pageLength, String tenantId, String userId) {
		Integer start = 0;
		if (pageNo != null) {
			start = pageNo - 1;
		}
		if (pageLength != null) {
			start = start * pageLength;
		}
		LOG.info(" start  : " + start);
		return prTemplateDao.findTemplatesByTemplateNameForTenantId(searchValue, pageNo, pageLength, start, tenantId, userId);
	}

}
