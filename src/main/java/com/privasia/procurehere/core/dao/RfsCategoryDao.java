package com.privasia.procurehere.core.dao;

import java.util.List;

import com.privasia.procurehere.core.entity.RfsCategory;
import com.privasia.procurehere.core.pojo.TableDataInput;

public interface RfsCategoryDao extends GenericDao<RfsCategory, String> {

	List<RfsCategory> findRfsCategoryForTenant(String tenantId, TableDataInput tableParams);

	long findTotalFilteredRfsCategoryForTenant(String tenantId, TableDataInput tableParams);

	long findTotalRfsCategoryForTenant(String tenantId);

	boolean isExists(RfsCategory rfsCategory, String buyerId);

}
