package com.privasia.procurehere.core.dao;

import java.math.BigDecimal;
import java.util.List;

import com.privasia.procurehere.core.entity.ScoreRating;
import com.privasia.procurehere.core.enums.TenantType;
import com.privasia.procurehere.core.pojo.TableDataInput;

public interface ScoreRatingDao extends GenericDao<ScoreRating, String> {

	void deleteById(String id);

	List<ScoreRating> findScoreRatingForTenant(String loggedInUserTenantId, TableDataInput input, TenantType tenantType);

	long findTotalFilteredScoreRatingForTenant(String loggedInUserTenantId, TableDataInput input, TenantType tenantType);

	long findTotalActiveScoreRatingForTenant(String loggedInUserTenantId, TenantType tenantType);

	List<ScoreRating> getAllActiveScoreRatingForTenant(String loggedInUserTenantId, TenantType owner);

	long findTotalScoreRatingCountForCsv(String loggedInUserTenantId, TenantType tenantType);

	List<ScoreRating> getAllScoreRatingForCsv(String loggedInUserTenantId, TenantType tenantType, int pAGE_SIZE, int pageNo);

	ScoreRating getScoreRatingForScoreAndTenant(String tenantId, BigDecimal overallScore);

	boolean isExists(String loggedInUserTenantId, ScoreRating scoreRating);

	boolean checkScoreRangeOverlap(String loggedInUserTenantId, ScoreRating scoreRating);

	
}
