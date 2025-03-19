package com.privasia.procurehere.service;

import java.io.File;
import java.math.BigDecimal;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import com.privasia.procurehere.core.entity.ScoreRating;
import com.privasia.procurehere.core.enums.TenantType;
import com.privasia.procurehere.core.pojo.ScoreRatingPojo;
import com.privasia.procurehere.core.pojo.TableDataInput;

/**
 * 
 * @author priyanka
 *
 */
public interface ScoreRatingService {

	void deleteScoreRating(String id);

	ScoreRating editScoreRating(String id);

	List<ScoreRatingPojo> getAllScoreRatingPojo(String loggedInUserTenantId);

	List<ScoreRating> findScoreRatingForTenant(String loggedInUserTenantId, TableDataInput input, TenantType tenantType);

	long findTotalFilteredScoreRatingForTenant(String loggedInUserTenantId, TableDataInput input, TenantType tenantType);

	long findTotalActiveScoreRatingForTenant(String loggedInUserTenantId, TenantType tenantType);

	String createScoreRating(ScoreRating scoreRating);

	ScoreRating findById(String id);

	ScoreRating updateScoreRating(ScoreRating persistObj);

	void downloadCsvFileForSCoreRating(HttpServletResponse response, File file, ScoreRatingPojo scoreRatingPojo, String loggedInUserTenantId, TenantType tenantType);

	ScoreRating getScoreRatingForScoreAndTenant(String tenantId, BigDecimal overallScore);

	boolean isExists(ScoreRating scoreRating, String loggedInUserTenantId);

	boolean checkScoreRangeOverlap(ScoreRating scoreRating, String loggedInUserTenantId);

}
