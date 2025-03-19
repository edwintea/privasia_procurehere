package com.privasia.procurehere.core.dao.impl;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.privasia.procurehere.core.dao.BusinessUnitDao;
import com.privasia.procurehere.core.dao.EventIdSettingsDao;
import com.privasia.procurehere.core.dao.PrDao;
import com.privasia.procurehere.core.dao.RfaEventDao;
import com.privasia.procurehere.core.dao.RfiEventDao;
import com.privasia.procurehere.core.dao.RfpEventDao;
import com.privasia.procurehere.core.dao.RfqEventDao;
import com.privasia.procurehere.core.dao.RftEventDao;
import com.privasia.procurehere.core.dao.SourcingFormRequestDao;
import com.privasia.procurehere.core.entity.BusinessUnit;
import com.privasia.procurehere.core.entity.IdSettings;
import com.privasia.procurehere.core.enums.IdSettingType;
import com.privasia.procurehere.core.enums.Status;
import com.privasia.procurehere.core.exceptions.ApplicationException;
import com.privasia.procurehere.core.pojo.ColumnParameter;
import com.privasia.procurehere.core.pojo.OrderParameter;
import com.privasia.procurehere.core.pojo.TableDataInput;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.StringUtils;

@Repository
public class EventIdSettingsDaoImpl extends GenericDaoImpl<IdSettings, String> implements EventIdSettingsDao {

	private static final Logger LOG = LogManager.getLogger(EventIdSettingsDaoImpl.class);

	@Autowired
	RftEventDao rftEventDao;

	@Autowired
	RfaEventDao rfaEventDao;

	@Autowired
	RfiEventDao rfiEventDao;

	@Autowired
	RfpEventDao rfpEventDao;

	@Autowired
	RfqEventDao rfqEventDao;

	@Autowired
	PrDao prDao;

	@Autowired
	BusinessUnitDao businessUnitDao;

	@Autowired
	SourcingFormRequestDao sourcingFormRequestDao;
	/*
	 * @Override
	 * @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW, isolation = Isolation.SERIALIZABLE)
	 * public String generateEventIdForRft(String tenantId) { // The expected ID format is
	 * [PREFIX][DELIMITER][DATE_FORMAT][DELIMITER][SEQUENCE] e.g PRE/102016/1 String id = ""; // Fetch the Event ID
	 * Format settings for the specified Tenant ID // EventIdSettings settings = findByProperty("tenantId", tenantId);
	 * String[] properties = { "tenantId", "idType" }; String[] values = { tenantId, "RFT" }; IdSettings settings =
	 * findByProperties(properties, values); if (settings == null) { settings = new IdSettings(); //
	 * settings.setRftIdDatePattern("MMdd"); // settings.setRftIdDelimiter("/"); settings.setIdPerfix("RFT");
	 * settings.setIdType("RFT"); settings.setIdSequence(1); settings.setTenantId(tenantId); settings = save(settings);
	 * } // Prefix if (StringUtils.checkString(settings.getIdPerfix()).length() > 0) { id += settings.getIdPerfix(); }
	 * // Delimiter if (StringUtils.checkString(settings.getIdDelimiter()).length() > 0) { id +=
	 * settings.getIdDelimiter(); } // Date Format if (StringUtils.checkString(settings.getIdDatePattern()).length() >
	 * 0) { SimpleDateFormat df = new SimpleDateFormat(settings.getIdDatePattern()); id += df.format(new Date()); } //
	 * Delimiter if (StringUtils.checkString(settings.getIdDelimiter()).length() > 0) { id += settings.getIdDelimiter();
	 * } // Sequence if (settings.getIdSequence() == null) { settings.setIdSequence(new Integer(1)); } if
	 * (settings.getPaddingLength() != null && settings.getPaddingLength() > 0) { id += StringUtils.lpad("" +
	 * settings.getIdSequence(), settings.getPaddingLength(), '0'); } else { id += settings.getIdSequence(); } // id +=
	 * settings.getRftIdSequence(); // Increment the secuence and update back the DB
	 * settings.setIdSequence(settings.getIdSequence() + 1); return id; }
	 * @Override
	 * @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW, isolation = Isolation.SERIALIZABLE)
	 * public String generatePrIdForPr(String tenantId) { // The expected ID format is
	 * [PREFIX][DELIMITER][DATE_FORMAT][DELIMITER][SEQUENCE] e.g PRE/102016/1 String id = ""; // Fetch the PR ID Format
	 * settings for the specified Tenant ID // EventIdSettings settings = findByProperty("tenantId", tenantId); String[]
	 * properties = { "tenantId", "idType" }; String[] values = { tenantId, "PR" }; IdSettings settings =
	 * findByProperties(properties, values); if (settings == null) { settings = new IdSettings(); //
	 * settings.setPrIdDatePattern("MMdd"); // settings.setPrIdDelimiter("/"); settings.setIdPerfix("PR");
	 * settings.setIdSequence(1); settings.setTenantId(tenantId); settings = save(settings); } // Prefix if
	 * (StringUtils.checkString(settings.getIdPerfix()).length() > 0) { id += settings.getIdPerfix(); } // Delimiter if
	 * (StringUtils.checkString(settings.getIdDelimiter()).length() > 0) { id += settings.getIdDelimiter(); } // Date
	 * Format if (StringUtils.checkString(settings.getIdDatePattern()).length() > 0) { SimpleDateFormat df = new
	 * SimpleDateFormat(settings.getIdDatePattern()); id += df.format(new Date()); } // Delimiter if
	 * (StringUtils.checkString(settings.getIdDelimiter()).length() > 0) { id += settings.getIdDelimiter(); } //
	 * Sequence if (settings.getIdSequence() == null) { settings.setIdSequence(new Integer(1)); } if
	 * (settings.getPaddingLength() != null && settings.getPaddingLength() > 0) { id += StringUtils.lpad("" +
	 * settings.getIdSequence(), settings.getPaddingLength(), '0'); } else { id += settings.getIdSequence(); } //
	 * Increment the secuence and update back the DB settings.setIdSequence(settings.getIdSequence() + 1); return id; }
	 * @Override
	 * @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW, isolation = Isolation.SERIALIZABLE)
	 * public String generateEventIdForRfa(String tenantId) { // The expected ID format is
	 * [PREFIX][DELIMITER][DATE_FORMAT][DELIMITER][SEQUENCE] e.g PRE/102016/1 String id = ""; // Fetch the Event ID
	 * Format settings for the specified Tenant ID // EventIdSettings settings = findByProperty("tenantId", tenantId);
	 * String[] properties = { "tenantId", "idType" }; String[] values = { tenantId, "RFA" }; IdSettings settings =
	 * findByProperties(properties, values); if (settings == null) { settings = new IdSettings(); //
	 * settings.setRfaIdDatePattern("MMdd"); // settings.setRfaIdDelimiter("/"); settings.setIdPerfix("RFA");
	 * settings.setIdSequence(1); settings.setTenantId(tenantId); settings = save(settings); } // Prefix if
	 * (StringUtils.checkString(settings.getIdPerfix()).length() > 0) { id += settings.getIdPerfix(); } // Delimiter if
	 * (StringUtils.checkString(settings.getIdDelimiter()).length() > 0) { id += settings.getIdDelimiter(); } // Date
	 * Format if (StringUtils.checkString(settings.getIdDatePattern()).length() > 0) { SimpleDateFormat df = new
	 * SimpleDateFormat(settings.getIdDatePattern()); id += df.format(new Date()); } // Delimiter if
	 * (StringUtils.checkString(settings.getIdDelimiter()).length() > 0) { id += settings.getIdDelimiter(); } //
	 * Sequence if (settings.getIdSequence() == null) { settings.setIdSequence(new Integer(1)); } // id +=
	 * settings.getRftIdSequence(); if (settings.getPaddingLength() != null && settings.getPaddingLength() > 0) { id +=
	 * StringUtils.lpad("" + settings.getIdSequence(), settings.getPaddingLength(), '0'); } else { id +=
	 * settings.getIdSequence(); } // Increment the secuence and update back the DB
	 * settings.setIdSequence(settings.getIdSequence() + 1); return id; }
	 * @Override
	 * @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW, isolation = Isolation.SERIALIZABLE)
	 * public String generateEventIdForRfi(String tenantId) { // The expected ID format is
	 * [PREFIX][DELIMITER][DATE_FORMAT][DELIMITER][SEQUENCE] e.g PRE/102016/1 String id = ""; // Fetch the Event ID
	 * Format settings for the specified Tenant ID String[] properties = { "tenantId", "idType" }; String[] values = {
	 * tenantId, "RFI" }; IdSettings settings = findByProperties(properties, values); // EventIdSettings settings =
	 * findByProperty("tenantId", tenantId); if (settings == null) { settings = new IdSettings(); //
	 * settings.setRfiIdDatePattern("MMdd"); // settings.setRfiIdDelimiter("/"); settings.setIdPerfix("RFI");
	 * settings.setIdSequence(1); settings.setTenantId(tenantId); settings = save(settings); } // Prefix if
	 * (StringUtils.checkString(settings.getIdPerfix()).length() > 0) { id += settings.getIdPerfix(); } // Delimiter if
	 * (StringUtils.checkString(settings.getIdDelimiter()).length() > 0) { id += settings.getIdDelimiter(); } // Date
	 * Format if (StringUtils.checkString(settings.getRfiIdDatePattern()).length() > 0) { SimpleDateFormat df = new
	 * SimpleDateFormat(settings.getRfiIdDatePattern()); id += df.format(new Date()); } // Delimiter if
	 * (StringUtils.checkString(settings.getRfiIdDelimiter()).length() > 0) { id += settings.getRfiIdDelimiter(); } //
	 * Sequence if (settings.getRfiIdSequence() == null) { settings.setRfiIdSequence(new Integer(1)); } // id +=
	 * settings.getRfiIdSequence(); if (settings.getRfiPaddingLength() != null && settings.getRfiPaddingLength() > 0) {
	 * id += StringUtils.lpad("" + settings.getRfiIdSequence(), settings.getRfiPaddingLength(), '0'); } else { id +=
	 * settings.getRfiIdSequence(); } // Increment the secuence and update back the DB
	 * settings.setRfiIdSequence(settings.getRfiIdSequence() + 1); return id; }
	 * @Override
	 * @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW, isolation = Isolation.SERIALIZABLE)
	 * public String generateEventIdForRfp(String tenantId) { // The expected ID format is
	 * [PREFIX][DELIMITER][DATE_FORMAT][DELIMITER][SEQUENCE] e.g PRE/102016/1 String id = ""; // Fetch the Event ID
	 * Format settings for the specified Tenant ID // EventIdSettings settings = findByProperty("tenantId", tenantId);
	 * String[] properties = { "tenantId" }; String[] values = { tenantId }; EventIdSettings settings =
	 * findByProperties(properties, values); if (settings == null) { settings = new EventIdSettings(); //
	 * settings.setRfpIdDatePattern("MMdd"); // settings.setRfpIdDelimiter("/"); // settings.setRfpIdPerfix("RFP");
	 * settings.setRfpIdSequence(1); settings.setTenantId(tenantId); settings = save(settings); } // Prefix if
	 * (StringUtils.checkString(settings.getRfpIdPerfix()).length() > 0) { id += settings.getRfpIdPerfix(); } //
	 * Delimiter if (StringUtils.checkString(settings.getRfpIdDelimiter()).length() > 0) { id +=
	 * settings.getRfpIdDelimiter(); } // Date Format if
	 * (StringUtils.checkString(settings.getRfpIdDatePattern()).length() > 0) { SimpleDateFormat df = new
	 * SimpleDateFormat(settings.getRfpIdDatePattern()); id += df.format(new Date()); } // Delimiter if
	 * (StringUtils.checkString(settings.getRfpIdDelimiter()).length() > 0) { id += settings.getRfpIdDelimiter(); } //
	 * Sequence if (settings.getRfpIdSequence() == null) { settings.setRfpIdSequence(new Integer(1)); } // id +=
	 * settings.getRfpIdSequence(); if (settings.getRfpPaddingLength() != null && settings.getRfpPaddingLength() > 0) {
	 * id += StringUtils.lpad("" + settings.getRfpIdSequence(), settings.getRfpPaddingLength(), '0'); } else { id +=
	 * settings.getRfpIdSequence(); } // Increment the secuence and update back the DB
	 * settings.setRfpIdSequence(settings.getRfpIdSequence() + 1); return id; }
	 * @Override
	 * @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW, isolation = Isolation.SERIALIZABLE)
	 * public String generateEventIdForRfq(String tenantId) { // The expected ID format is
	 * [PREFIX][DELIMITER][DATE_FORMAT][DELIMITER][SEQUENCE] e.g PRE/102016/1 String id = ""; // Fetch the Event ID
	 * Format settings for the specified Tenant ID // EventIdSettings settings = findByProperty("tenantId", tenantId);
	 * String[] properties = { "tenantId" }; String[] values = { tenantId }; EventIdSettings settings =
	 * findByProperties(properties, values); if (settings == null) { settings = new EventIdSettings(); //
	 * settings.setRfpIdDatePattern("MMdd"); // settings.setRfpIdDelimiter("/"); // settings.setRfpIdPerfix("RFQ");
	 * settings.setRfpIdSequence(1); settings.setTenantId(tenantId); settings = save(settings); } // Prefix if
	 * (StringUtils.checkString(settings.getRfqIdPerfix()).length() > 0) { id += settings.getRfqIdPerfix(); } //
	 * Delimiter if (StringUtils.checkString(settings.getRfqIdDelimiter()).length() > 0) { id +=
	 * settings.getRfqIdDelimiter(); } // Date Format if
	 * (StringUtils.checkString(settings.getRfqIdDatePattern()).length() > 0) { SimpleDateFormat df = new
	 * SimpleDateFormat(settings.getRfqIdDatePattern()); id += df.format(new Date()); } // Delimiter if
	 * (StringUtils.checkString(settings.getRfqIdDelimiter()).length() > 0) { id += settings.getRfqIdDelimiter(); } //
	 * Sequence if (settings.getRfqIdSequence() == null) { settings.setRfqIdSequence(new Integer(1)); } // id +=
	 * settings.getRfqIdSequence(); if (settings.getRfqPaddingLength() != null && settings.getRfqPaddingLength() > 0) {
	 * id += StringUtils.lpad("" + settings.getRfqIdSequence(), settings.getRfqPaddingLength(), '0'); } else { id +=
	 * settings.getRfqIdSequence(); } // Increment the secuence and update back the DB
	 * settings.setRfqIdSequence(settings.getRfqIdSequence() + 1); return id; }
	 */

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW, isolation = Isolation.SERIALIZABLE)
	@Deprecated
	public String generateEventId(String tenantId, String idType) {

		throw new RuntimeException("Not Implemented");

		// LOG.info("ID Request for Type " + idType + " Tenant : " + tenantId);
		// String id = "";
		//
		// String[] properties = { "tenantId", "idType" };
		// String[] values = { tenantId, idType };
		// IdSettings settings = findByProperties(properties, values);
		//
		// if (settings == null) {
		// settings = new IdSettings();
		// settings.setIdPerfix(idType);
		// settings.setIdType(idType);
		// settings.setIdSequence(1);
		// settings.setTenantId(tenantId);
		// settings = save(settings);
		// }
		//
		//
		// id = getIdSettingOnPattern(settings, null);
		// settings.setIdSequence(settings.getIdSequence() + 1);
		// update(settings);
		// return id;
	}

	private String getIdByPatternOrder(String... args) {
		String id = "";
		for (String string : args) {
			id += StringUtils.checkString(string);
		}
		return id;
	}

	/*
	 * @Override public List<IdSettings> getAllSettings(String id) { return null; }
	 */

	private String getIdSettingOnPattern(IdSettings settings, BusinessUnit buUnit, Integer sequenceNo) {
		String id = "";
		String datepatern = "", squence = "", prefix = "", del = "", businessUnit = "";

		if (StringUtils.checkString(settings.getIdDatePattern()).length() > 0) {
			SimpleDateFormat df = new SimpleDateFormat(settings.getIdDatePattern());
			datepatern = df.format(new Date());
		}

		if (settings.getIdSequence() == null) {
			settings.setIdSequence(new Integer(1));
		}
		if (settings.getPaddingLength() != null && settings.getPaddingLength() > 0) {
			squence = StringUtils.lpad("" + (sequenceNo), settings.getPaddingLength(), '0');
		} else {
			squence += (sequenceNo);
		}
		prefix = settings.getIdPerfix();
		del = settings.getIdDelimiter();
		if (buUnit != null) {
			LOG.info("===================business Unit:" + buUnit.getUnitCode());
			businessUnit = StringUtils.checkString(buUnit.getUnitCode());
		}
		if (settings.getIdSettingPattern() != null && settings.getIdSettingType() == IdSettingType.BUSINESS_UNIT) {
			LOG.info("===================pattern:" + settings.getIdSettingPattern());
			switch (settings.getIdSettingPattern()) {

			case PRE_DATE_DEL_BU_DEL_NNNNN:
				id = getIdByPatternOrder(prefix, datepatern, del, businessUnit, del, squence);
				break;
			case PRE_DATE_DEL_NNNNN_DEL_BU:
				id = getIdByPatternOrder(prefix, datepatern, del, squence, del, businessUnit);
				break;
			case PRE_DEL_BU_DEL_DATE_DEL_NNNN:
				id = getIdByPatternOrder(prefix, del, businessUnit, del, datepatern, del, squence);
				break;
			case PRE_DEL_BU_DEL_DATE_NNNN:
				id = getIdByPatternOrder(prefix, del, businessUnit, del, datepatern, squence);
				break;
			case PRE_DEL_DATE_DEL_NNNN_DEL_BU:
				id = getIdByPatternOrder(prefix, del, datepatern, del, squence, del, businessUnit);
				break;
			case PRE_DEL_DATE_NNNN_DEL_BU:
				id = getIdByPatternOrder(prefix, del, datepatern, squence, del, businessUnit);
				break;
			case BU_DEL_PRE_DEL_DATE_NNNN:
				id = getIdByPatternOrder(businessUnit, del, prefix, del, datepatern, squence);
				break;
			default:
				id = getIdByPatternOrder(prefix, del, datepatern, del, squence);
				break;
			}
			return id;
		} else {
			LOG.info("==============Default Pattern: " + settings.getIdSettingPattern());
			if (StringUtils.checkString(datepatern).length() <= 0)
				del = "";
			return getIdByPatternOrder(prefix, del, datepatern, del, squence);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<IdSettings> getAllSettings(String loggedInUserTenantId) {
		final Query query = getEntityManager().createQuery("from IdSettings t left outer join fetch t.modifiedBy as mb where t.tenantId = :tenantId");
		LOG.info("eventIdSettingsDaoImpl getAllSettings(String loggedInUserTenantId) called");
		query.setParameter("tenantId", loggedInUserTenantId);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<IdSettings> findAllIdSettingsList(String tenantId, TableDataInput tableParams) {
		final Query query = constructCountryQuery(tenantId, tableParams, false);
		LOG.info("eventIdSettingsDaoImpl findAllIdSettingsList(tenantId, tableParams) called");
		query.setFirstResult(tableParams.getStart());
		query.setMaxResults(tableParams.getLength());
		return query.getResultList();
	}

	@Override
	public long findTotalFilteredCountryList(String tenantId, TableDataInput tableParams) {
		LOG.info("eventIdSettingsDaoImpl findTotalFilteredCountryList(tenantId, tableParams) called");
		final Query query = constructCountryQuery(tenantId, tableParams, true);
		return ((Number) query.getSingleResult()).longValue();
	}

	@Override
	public long findTotalIdSetList(String loggedInUserTenantId) {
		LOG.info("eventIdSettingsDaoImpl findTotalIdSetList(loggedInUserTenantId) called");
		StringBuilder hql = new StringBuilder("select count (c) from IdSettings c where c.tenantId = :tenantId ");
		final Query query = getEntityManager().createQuery(hql.toString());
		query.setParameter("tenantId", loggedInUserTenantId);
		return ((Number) query.getSingleResult()).longValue();
	}

	@Override
	public boolean isExists(IdSettings idSettings, String tenantId) {
		LOG.info("isExists tenantId : " + tenantId + " == idType:" + idSettings.getIdType());
		String hql = "select count(*) from IdSettings u where (upper(u.idType) = :idType) and u.tenantId = :tenantId ";

		if (StringUtils.checkString(idSettings.getId()).length() > 0) {
			hql += " and u.id <> :id ";
		}

		final Query query = getEntityManager().createQuery(hql);

		query.setParameter("idType", idSettings.getIdType().toUpperCase());
		query.setParameter("tenantId", tenantId);
		if (StringUtils.checkString(idSettings.getId()).length() > 0) {
			LOG.info(" id " + idSettings.getId());
			query.setParameter("id", idSettings.getId());
		}
		return ((Number) query.getSingleResult()).intValue() > 0;
	}

	private Query constructCountryQuery(String tenantId, TableDataInput tableParams, boolean isCount) {

		String hql = "";

		// If count query is enabled, then add the select count(*) clause
		if (isCount) {
			hql += "select count(c) ";
		}

		hql += " from IdSettings c ";

		// If this is not a count query, only then add the join fetch. Count query does not require its
		if (!isCount) {
			hql += " left outer join fetch c.modifiedBy as mb ";
		}
		hql += " where c.tenantId = :tenantId";

		// boolean isStatusFilterOn = false;

		// Add on search filter conditions
		for (ColumnParameter cp : tableParams.getColumns()) {
			if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
				if (cp.getData().equals("status")) {
					// isStatusFilterOn = true;
					hql += " and c.status = (:" + cp.getData() + ")";
				} else {
					hql += " and upper(c." + cp.getData() + ") like (:" + cp.getData().replace(".", "") + ")";
				}
			}
		}

		/*
		 * if (!isStatusFilterOn) { hql += " and c.status = :status "; }
		 */

		// If it is not a count query then add order by clause
		if (!isCount) {
			List<OrderParameter> orderList = tableParams.getOrder();
			if (CollectionUtil.isNotEmpty(orderList)) {
				hql += " order by ";
				for (OrderParameter order : orderList) {
					String orderColumn = tableParams.getColumns().get(order.getColumn()).getData();
					String dir = order.getDir();
					hql += " c." + orderColumn + " " + dir + ",";
				}
				if (hql.lastIndexOf(",") == hql.length() - 1) {
					hql = hql.substring(0, hql.length() - 1);
				}
			}
		}

		LOG.info("HQL : " + hql);

		final Query query = getEntityManager().createQuery(hql.toString());
		query.setParameter("tenantId", tenantId);

		// Apply search filter values
		for (ColumnParameter cp : tableParams.getColumns()) {
			if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
				if (cp.getData().equals("status")) {
					query.setParameter("status", Status.valueOf(cp.getSearch().getValue()));
				} else {
					query.setParameter(cp.getData().replace(".", ""), "%" + cp.getSearch().getValue().toUpperCase() + "%");
				}
			}
		}
		// If status search filter is not ON then by default return only active records.
		/*
		 * if (!isStatusFilterOn) { query.setParameter("status", Status.ACTIVE); }
		 */
		return query;
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW, isolation = Isolation.SERIALIZABLE)
	public String generateEventIdByBusinessUnit(String tenantId, String idType, BusinessUnit businessUnit) throws ApplicationException {
		LOG.info("ID Request for Type " + idType + " Tenant : " + tenantId);
		if (businessUnit != null) {
			businessUnit = businessUnitDao.findById(businessUnit.getId());
		}
		String id = "";

		// Fetch the Event ID Format settings for the specified Tenant ID
		String[] properties = { "tenantId", "idType" };
		String[] values = { tenantId, idType };
		IdSettings settings = findByProperties(properties, values);

		if (settings == null) {
			settings = new IdSettings();

			settings.setIdPerfix(idType);
			settings.setIdType(idType);
			settings.setIdSequence(1);
			settings.setIdSettingType(IdSettingType.COMPANY_LEVEL);
			settings.setTenantId(tenantId);
			settings = save(settings);
		}

		if (IdSettingType.COMPANY_LEVEL == settings.getIdSettingType() || settings.getIdSettingType() == null) {
			id = getIdSettingOnPattern(settings, null, settings.getIdSequence());
			settings.setIdSequence(settings.getIdSequence() + 1);
			update(settings);

		} else if (businessUnit != null) {
			Integer sequence = 0;
			String sequenceType = "idSequence";
			switch (idType) {
			case "PR":
				if (businessUnit.getPrIdSequence() == null) {
					businessUnit.setPrIdSequence(1);
				}
				sequence = businessUnit.getPrIdSequence();
				sequenceType = "prIdSequence";
				break;
			case "PO":
				if (businessUnit.getPoIdSequence() == null) {
					businessUnit.setPoIdSequence(1);
				}
				sequence = businessUnit.getPoIdSequence();
				sequenceType = "poIdSequence";
				break;
			case "RFA":
				if (businessUnit.getRfaIdSequence() == null) {
					businessUnit.setRfaIdSequence(1);
				}
				sequence = businessUnit.getRfaIdSequence();
				sequenceType = "rfaIdSequence";
				break;
			case "RFI":
				if (businessUnit.getRfiIdSequence() == null) {
					businessUnit.setRfiIdSequence(1);
				}
				sequence = businessUnit.getRfiIdSequence();
				sequenceType = "rfiIdSequence";
				break;
			case "RFP":
				if (businessUnit.getRfpIdSequence() == null) {
					businessUnit.setRfpIdSequence(1);
				}
				sequence = businessUnit.getRfpIdSequence();
				sequenceType = "rfpIdSequence";
				break;
			case "RFQ":
				if (businessUnit.getRfqIdSequence() == null) {
					businessUnit.setRfqIdSequence(1);
				}
				sequence = businessUnit.getRfqIdSequence();
				sequenceType = "rfqIdSequence";
				break;
			case "RFT":
				if (businessUnit.getRftIdSequence() == null) {
					businessUnit.setRftIdSequence(1);
				}
				sequence = businessUnit.getRftIdSequence();
				sequenceType = "rftIdSequence";
				break;
			case "SR":
				if (businessUnit.getSrIdSequence() == null) {
					businessUnit.setSrIdSequence(1);
				}
				sequence = businessUnit.getSrIdSequence();
				sequenceType = "srIdSequence";
				break;
			case "BG":
				if (businessUnit.getBgIdSequence() == null) {
					businessUnit.setBgIdSequence(1);
				}
				sequence = businessUnit.getBgIdSequence();
				sequenceType = "bgIdSequence";
				break;
			case "GRN":
				if (businessUnit.getGrnIdSequence() == null) {
					businessUnit.setGrnIdSequence(1);
				}
				sequence = businessUnit.getGrnIdSequence();
				sequenceType = "grnIdSequence";
				break;
			case "CTR":
				if (businessUnit.getCtrIdSequence() == null) {
					businessUnit.setCtrIdSequence(1);
				}
				sequence = businessUnit.getCtrIdSequence();
				sequenceType = "ctrIdSequence";
				break;
			case "SP":
				if (businessUnit.getSpIdSequence() == null) {
					businessUnit.setSpIdSequence(1);
				}
				sequence = businessUnit.getSpIdSequence();
				sequenceType = "spIdSequence";
				break;
			default:
				sequenceType = "idSequence";
				break;
			}
			if (businessUnit.getIdSequence() == null) {
				businessUnit.setIdSequence(1);
			}
			id = getIdSettingOnPattern(settings, businessUnit, sequence);
			businessUnitDao.updateBusinessUnitSequenceNumer(businessUnit.getId(), sequenceType, sequence + 1);
		} else {
			id = getIdSettingOnPattern(settings, null, settings.getIdSequence());
			settings.setIdSequence(settings.getIdSequence() + 1);
			update(settings);
		}

		return id;
	}

	@Override
	public boolean isRequiredCode(String tenantId) {
		StringBuilder hql = new StringBuilder("select count(*) from IdSettings ids where ids.idSettingType =:idSettingType and ids.tenantId = :tenantId ");
		final Query query = getEntityManager().createQuery(hql.toString());
		query.setParameter("idSettingType", IdSettingType.BUSINESS_UNIT);
		query.setParameter("tenantId", tenantId);
		return (((Number) query.getSingleResult()).longValue() > 0);
	}

	@Override
	public Boolean isBusinessSettingEnable(String tenantId, String idType) {
		StringBuilder hql = new StringBuilder("select count(*) from IdSettings ids where ids.idSettingType =:idSettingType and ids.tenantId = :tenantId and ids.idType = :idType");
		final Query query = getEntityManager().createQuery(hql.toString());
		query.setParameter("idSettingType", IdSettingType.BUSINESS_UNIT);
		query.setParameter("tenantId", tenantId);
		query.setParameter("idType", idType);

		return (((Number) query.getSingleResult()).longValue() > 0);
	}

	@SuppressWarnings("unchecked")
	@Override
	public IdSettings getIdSettingsByIdTypeForTenanatId(String tenantId, String idType) {
		try {
			final Query query = getEntityManager().createQuery("select id from IdSettings id where id.tenantId = :tenantId and id.idType = :idType ");
			query.setParameter("tenantId", tenantId);
			query.setParameter("idType", idType);
			List<IdSettings> list = query.getResultList();
			if (CollectionUtil.isNotEmpty(list)) {
				return list.get(0);

			} else {
				return null;
			}
		} catch (NoResultException nr) {
			LOG.info("Error while getting user : " + nr.getMessage(), nr);
			return null;
		}
	}

}