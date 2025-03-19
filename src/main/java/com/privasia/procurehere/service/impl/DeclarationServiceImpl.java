package com.privasia.procurehere.service.impl;

import java.util.Date;
import java.util.List;

import javax.servlet.ServletContext;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.exception.ConstraintViolationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.privasia.procurehere.core.dao.BuyerAuditTrailDao;
import com.privasia.procurehere.core.dao.DeclarationDao;
import com.privasia.procurehere.core.dao.RfxTemplateDao;
import com.privasia.procurehere.core.entity.BuyerAuditTrail;
import com.privasia.procurehere.core.entity.Declaration;
import com.privasia.procurehere.core.enums.AuditTypes;
import com.privasia.procurehere.core.enums.DeclarationType;
import com.privasia.procurehere.core.enums.ModuleType;
import com.privasia.procurehere.core.pojo.DeclarationTemplatePojo;
import com.privasia.procurehere.core.pojo.TableDataInput;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.SecurityLibrary;
import com.privasia.procurehere.service.DeclarationService;

@Service
@Transactional(readOnly = true)
public class DeclarationServiceImpl implements DeclarationService {
	private static final Logger LOG = LogManager.getLogger(Global.BUYER_LOG);

	@Autowired
	DeclarationDao declarationDao;

	@Autowired
	RfxTemplateDao rfxTemplateDao;

	@Autowired
	ServletContext context;

	@Autowired
	MessageSource messageSource;
	
	@Autowired
	BuyerAuditTrailDao buyerAuditTrailDao;

	@Transactional(readOnly = false)
	@Override
	public void saveOrUpdateDeclaration(Declaration declaration) {
		try {
			BuyerAuditTrail ownerAuditTrail = new BuyerAuditTrail(AuditTypes.CREATE, "Declaration Setting '"+declaration.getTitle()+ "' created", declaration.getCreatedBy().getTenantId(), declaration.getCreatedBy(), new Date(), ModuleType.DeclarationSetting);
			buyerAuditTrailDao.save(ownerAuditTrail);
		} catch (Exception e) {
			LOG.info("Error to create audit trails message");
		}
		declarationDao.saveOrUpdate(declaration);

	}

	@Override
	public Declaration getDeclarationById(String id) {
		return declarationDao.findById(id);
	}

	@Transactional(readOnly = false)
	@Override
	public void updateDeclaration(Declaration declarationObj) {
		try {
			BuyerAuditTrail ownerAuditTrail = new BuyerAuditTrail(AuditTypes.UPDATE, "Declaration Setting '"+declarationObj.getTitle()+ "' updated", declarationObj.getModifiedBy().getTenantId(), declarationObj.getModifiedBy(), new Date(), ModuleType.DeclarationSetting);
			buyerAuditTrailDao.save(ownerAuditTrail);
		} catch (Exception e) {
			LOG.info("Error to create audit trails message");
		}
		declarationDao.update(declarationObj);
	}

	@Transactional(readOnly = false)
	@Override
	public void deleteDeclaration(Declaration declarationObj) {
		if (rfxTemplateDao.isAssignedDeclarationToTemplate(declarationObj.getId())) {
			ConstraintViolationException cause = new ConstraintViolationException(messageSource.getMessage("declaration.error.delete.dataIntegrity", new Object[] { declarationObj.getTitle() }, Global.LOCALE), null, "Unique Constraint");
			throw new DataIntegrityViolationException("Constraint Violation", cause);
		}
		declarationDao.delete(declarationObj);
		try {
			BuyerAuditTrail ownerAuditTrail = new BuyerAuditTrail(AuditTypes.DELETE, "Declaration Setting '" +declarationObj.getTitle()+ "' deleted", SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser(), new Date(), ModuleType.DeclarationSetting);
			buyerAuditTrailDao.save(ownerAuditTrail);
		} catch (Exception e) {
			LOG.info("Error to create audit trails message");
		}

	}

	@Override
	public List<DeclarationTemplatePojo> findDeclarationsByTeantId(String loggedInUserTenantId, TableDataInput input) {
		return declarationDao.findDeclarationsByTeantId(loggedInUserTenantId, input);
	}

	@Override
	public long findTotalFilteredDeclarationsForTenant(String loggedInUserTenantId, TableDataInput input) {
		return declarationDao.findTotalFilteredDeclarationsForTenant(loggedInUserTenantId, input);
	}

	@Override
	public long findTotalActiveDeclarationsForTenant(String loggedInUserTenantId) {
		return declarationDao.findTotalActiveDeclarationsForTenant(loggedInUserTenantId);
	}

	@Override
	public List<Declaration> getDeclarationsByTypeForTenant(DeclarationType declarationType, String loggedInUserTenantId) {
		return declarationDao.getDeclarationsByTypeForTenant(declarationType, loggedInUserTenantId);
	}

	@Override
	public long findAssignedCountOfDeclaration(String declarationId) {
		return rfxTemplateDao.findAssignedCountOfDeclaration(declarationId);
	}

}