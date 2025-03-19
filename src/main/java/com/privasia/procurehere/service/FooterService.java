package com.privasia.procurehere.service;

import java.util.List;

import com.privasia.procurehere.core.entity.Footer;
import com.privasia.procurehere.core.enums.FooterType;
import com.privasia.procurehere.core.pojo.TableDataInput;

/**
 * @author pooja
 */
public interface FooterService {
	/**
	 * @param footer
	 */
	void saveOrUpdate(Footer footer);

	/**
	 * @param id
	 * @return
	 */
	Footer getFooterById(String id);

	/**
	 * @param footerObj
	 */
	void updateFooter(Footer footerObj);

	/**
	 * @param footerObj
	 */
	void deleteFooter(Footer footerObj);

	/**
	 * @param loggedInUserTenantId
	 * @param input
	 * @return
	 */
	List<Footer> findFooterByTeantId(String loggedInUserTenantId, TableDataInput input);

	/**
	 * @param loggedInUserTenantId
	 * @param input
	 * @return
	 */
	long findTotalFilteredFooterForTenant(String loggedInUserTenantId, TableDataInput input);

	/**
	 * @param loggedInUserTenantId
	 * @return
	 */
	long findTotalActiveFooterForTenant(String loggedInUserTenantId);

	/**
	 * @param id
	 * @return
	 */
	long findAssignedCountOfFooter(String id);

	/**
	 * @param footerType
	 * @param loggedInUserTenantId
	 * @return
	 */
	List<Footer> getFootersByTypeForTenant(FooterType footerType, String loggedInUserTenantId);

	/**
	 * @param footerId
	 * @return
	 */
	String getFooterContentById(String footerId);

}