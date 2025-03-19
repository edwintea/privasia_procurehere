package com.privasia.procurehere.core.dao;

import java.util.List;

import com.privasia.procurehere.core.entity.Footer;
import com.privasia.procurehere.core.enums.FooterType;
import com.privasia.procurehere.core.pojo.TableDataInput;

/**
 * @author pooja
 */
public interface FooterDao extends GenericDao<Footer, String> {
	/**
	 * @param loggedInUserTenantId
	 * @param input
	 * @return
	 */
	List<Footer> findFootersByTeantId(String loggedInUserTenantId, TableDataInput input);

	/**
	 * @param loggedInUserTenantId
	 * @param input
	 * @return
	 */
	long findTotalFilteredFootersForTenant(String loggedInUserTenantId, TableDataInput input);

	/**
	 * @param loggedInUserTenantId
	 * @return
	 */
	long findTotalActiveFootersForTenant(String loggedInUserTenantId);

	/**
	 * @param footerType
	 * @param tenantId
	 * @return
	 */
	List<Footer> getFootersByTypeForTenant(FooterType footerType, String tenantId);

	/**
	 * @param footerId
	 * @return
	 */
	String getFooterContentById(String footerId);
}