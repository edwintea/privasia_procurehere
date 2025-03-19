
package com.privasia.procurehere.service;

import java.util.List;

import com.privasia.procurehere.core.entity.RfsCategory;
import com.privasia.procurehere.core.pojo.TableDataInput;

/**
 * @author pooja
 */

public interface RfsCategoryService {

	List<RfsCategory> findRfsCategoryForTenant(String loggedInUserTenantId, TableDataInput input);

	long findTotalFilteredRfsCategoryForTenant(String loggedInUserTenantId, TableDataInput input);

	long findTotalRfsCategoryForTenant(String loggedInUserTenantId);

	RfsCategory getRfsCategoryById(String id);

	boolean isExists(RfsCategory rfsCategory, String loggedInUserTenantId);

}
