package com.privasia.procurehere.service.impl;

import java.io.File;
import java.io.FileWriter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.supercsv.cellprocessor.Optional;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;
import org.supercsv.quote.ColumnQuoteMode;

import com.privasia.procurehere.core.dao.ScoreRatingDao;
import com.privasia.procurehere.core.entity.ScoreRating;
import com.privasia.procurehere.core.enums.TenantType;
import com.privasia.procurehere.core.pojo.ScoreRatingPojo;
import com.privasia.procurehere.core.pojo.TableDataInput;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.service.ScoreRatingService;

@Service
@Transactional(readOnly = true)
public class ScoreRatingServiceImpl implements ScoreRatingService {

	private static final Logger LOG = LogManager.getLogger(ScoreRatingServiceImpl.class);

	@Autowired
	ScoreRatingDao scoreRatingDao;

	@Override
	@Transactional(readOnly = false)
	public void deleteScoreRating(String id) {
		scoreRatingDao.deleteById(id);
	}

	@Override
	@Transactional(readOnly = false)
	public ScoreRating editScoreRating(String id) {
		return findById(id);
	}

	@Override
	public List<ScoreRatingPojo> getAllScoreRatingPojo(String loggedInUserTenantId) {
		List<ScoreRatingPojo> returnList = new ArrayList<ScoreRatingPojo>();
		List<ScoreRating> list = scoreRatingDao.getAllActiveScoreRatingForTenant(loggedInUserTenantId, TenantType.OWNER);
		if (CollectionUtil.isNotEmpty(list)) {
			for (ScoreRating sr : list) {
				if (sr.getCreatedBy() != null)
					sr.getCreatedBy().getLoginId();
				if (sr.getModifiedBy() != null)
					sr.getModifiedBy().getLoginId();

				ScoreRatingPojo up = new ScoreRatingPojo(sr);
				returnList.add(up);
			}
		}
		return returnList;
	}

	@Override
	public List<ScoreRating> findScoreRatingForTenant(String loggedInUserTenantId, TableDataInput input, TenantType tenantType) {
		return scoreRatingDao.findScoreRatingForTenant(loggedInUserTenantId, input, tenantType);
	}

	@Override
	public long findTotalFilteredScoreRatingForTenant(String loggedInUserTenantId, TableDataInput input, TenantType tenantType) {
		return scoreRatingDao.findTotalFilteredScoreRatingForTenant(loggedInUserTenantId, input, tenantType);
	}

	@Override
	public long findTotalActiveScoreRatingForTenant(String loggedInUserTenantId, TenantType tenantType) {
		return scoreRatingDao.findTotalActiveScoreRatingForTenant(loggedInUserTenantId, tenantType);
	}

	@Override
	@Transactional(readOnly = false)
	public String createScoreRating(ScoreRating scoreRating) {
		ScoreRating sr = scoreRatingDao.saveOrUpdate(scoreRating);
		return (sr != null ? sr.getId() : null);
	}

	@Override
	public ScoreRating findById(String id) {
		return scoreRatingDao.findById(id);
	}

	@Override
	@Transactional(readOnly = false)
	public ScoreRating updateScoreRating(ScoreRating persistObj) {
		return scoreRatingDao.update(persistObj);
	}

	@Override
	public void downloadCsvFileForSCoreRating(HttpServletResponse response, File file, ScoreRatingPojo scoreRatingPojo, String loggedInUserTenantId, TenantType tenantType) {
		ICsvBeanWriter beanWriter = null;
		try {

			String[] columnHeadings = Global.SCORE_RATING_REPORT_CSV_COLUMNS;

			String[] columns = { "minScore", "maxScore", "rating", "description", "status" };

			long count = scoreRatingDao.findTotalScoreRatingCountForCsv(loggedInUserTenantId, tenantType);

			int PAGE_SIZE = 5000;
			int totalPages = (int) Math.ceil(count / (double) PAGE_SIZE);

			beanWriter = new CsvBeanWriter(new FileWriter(file), new CsvPreference.Builder(CsvPreference.STANDARD_PREFERENCE).useQuoteMode(new ColumnQuoteMode(2)).build());
			CellProcessor[] processors = getProcessors();
			beanWriter.writeHeader(columnHeadings);

			for (int pageNo = 0; pageNo < totalPages; pageNo++) {
				List<ScoreRating> list = scoreRatingDao.getAllScoreRatingForCsv(loggedInUserTenantId, tenantType, PAGE_SIZE, pageNo);

				LOG.info("size ........" + list.size() + ".... count " + count);
				for (ScoreRating pojo : list) {
					beanWriter.write(pojo, columns, processors);
				}
				beanWriter.flush();
				LOG.info("Write done........");
			}

			beanWriter.close();
			beanWriter = null;

		} catch (Exception e) {
			LOG.info("Error ..." + e, e);
		}

	}

	private static CellProcessor[] getProcessors() {
		final CellProcessor[] processors = new CellProcessor[] {

				new Optional(), // Minimum
				new Optional(), // Maximum
				new Optional(), // rating
				new Optional(), // Description
				new Optional(), // status

		};
		return processors;
	}

	@Override
	public ScoreRating getScoreRatingForScoreAndTenant(String tenantId, BigDecimal overallScore) {
		return scoreRatingDao.getScoreRatingForScoreAndTenant(tenantId, overallScore);
	}

	@Override
	public boolean isExists(ScoreRating scoreRating, String loggedInUserTenantId) {
		return scoreRatingDao.isExists(loggedInUserTenantId, scoreRating);
	}

	@Override
	public boolean checkScoreRangeOverlap(ScoreRating scoreRating, String loggedInUserTenantId) {
		return scoreRatingDao.checkScoreRangeOverlap(loggedInUserTenantId, scoreRating);
	}

}
