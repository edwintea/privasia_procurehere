package com.privasia.procurehere.service.impl;

import java.util.List;

import javax.servlet.ServletContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.privasia.procurehere.core.dao.FooterDao;
import com.privasia.procurehere.core.entity.Footer;
import com.privasia.procurehere.core.enums.FooterType;
import com.privasia.procurehere.core.pojo.TableDataInput;
import com.privasia.procurehere.service.FooterService;

@Service
@Transactional(readOnly = true)
public class FooterServiceImpl implements FooterService {

	@Autowired
	FooterDao footerDao;

	@Autowired
	ServletContext context;

	@Autowired
	MessageSource messageSource;

	@Transactional(readOnly = false)
	@Override
	public void saveOrUpdate(Footer footer) {
		footerDao.saveOrUpdate(footer);
	}

	@Override
	public Footer getFooterById(String id) {
		return footerDao.findById(id);
	}

	@Transactional(readOnly = false)
	@Override
	public void updateFooter(Footer footerObj) {
		footerDao.update(footerObj);
	}

	@Transactional(readOnly = false)
	@Override
	public void deleteFooter(Footer footerObj) {
		footerDao.delete(footerObj);
	}

	@Override
	public List<Footer> findFooterByTeantId(String loggedInUserTenantId, TableDataInput input) {
		return footerDao.findFootersByTeantId(loggedInUserTenantId, input);
	}

	@Override
	public long findTotalFilteredFooterForTenant(String loggedInUserTenantId, TableDataInput input) {
		return footerDao.findTotalFilteredFootersForTenant(loggedInUserTenantId, input);
	}

	@Override
	public long findTotalActiveFooterForTenant(String loggedInUserTenantId) {
		return footerDao.findTotalActiveFootersForTenant(loggedInUserTenantId);
	}

	@Override
	public long findAssignedCountOfFooter(String id) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public List<Footer> getFootersByTypeForTenant(FooterType footerType, String loggedInUserTenantId) {
		return footerDao.getFootersByTypeForTenant(footerType, loggedInUserTenantId);
	}

	@Override
	public String getFooterContentById(String footerId) {
		return footerDao.getFooterContentById(footerId);
	}

}