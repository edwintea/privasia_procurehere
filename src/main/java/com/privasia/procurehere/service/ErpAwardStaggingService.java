package com.privasia.procurehere.service;

import java.util.List;

import com.privasia.procurehere.core.entity.ErpAwardStaging;

/**
 * 
 */
public interface ErpAwardStaggingService {

	ErpAwardStaging saveErpAwardStaging(ErpAwardStaging stagging);

	List<ErpAwardStaging> getStaggingData(String tenantId, String refranceNo);

	ErpAwardStaging update(ErpAwardStaging erpAwardStaging);

	void updateOldAwardStagingFlag(String docNo, String tenantId, String id);

	ErpAwardStaging findAwardStaggingByEventID(String eventId, String tenantId);

}
