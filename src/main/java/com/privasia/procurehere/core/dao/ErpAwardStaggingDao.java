package com.privasia.procurehere.core.dao;

import java.util.List;

import com.privasia.procurehere.core.entity.ErpAwardStaging;

public interface ErpAwardStaggingDao extends GenericDao<ErpAwardStaging, String> {

	List<ErpAwardStaging> getStaggingData(String tenantId, String refranceNo);

	void updateOldAwardStagingFlag(String docNo, String tenantId, String id);

	ErpAwardStaging findAwardStaggingByEventID(String eventId, String tenantId);

}
