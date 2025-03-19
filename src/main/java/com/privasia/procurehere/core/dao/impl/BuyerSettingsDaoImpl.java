package com.privasia.procurehere.core.dao.impl;

import java.util.Date;
import java.util.List;

import javax.persistence.Query;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.privasia.procurehere.core.dao.BuyerSettingsDao;
import com.privasia.procurehere.core.dao.CurrencyDao;
import com.privasia.procurehere.core.dao.TimeZoneDao;
import com.privasia.procurehere.core.entity.Buyer;
import com.privasia.procurehere.core.entity.BuyerSettings;
import com.privasia.procurehere.core.entity.User;
import com.privasia.procurehere.core.enums.RfxTypes;
import com.privasia.procurehere.core.enums.Status;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.Global;

@Repository("buyerSettingsDao")
public class BuyerSettingsDaoImpl extends GenericDaoImpl<BuyerSettings, String> implements BuyerSettingsDao {

	private static final Logger LOG = LogManager.getLogger(Global.BUYER_LOG);

	@Autowired
	TimeZoneDao timeZoneDao;

	@Autowired
	CurrencyDao currencyDao;

	@SuppressWarnings("unchecked")
	@Override
	public BuyerSettings getBuyerSettingsByTenantId(String tenantId) {
		final Query query = getEntityManager().createQuery("from BuyerSettings b left outer join fetch b.timeZone as tz left outer join fetch b.currency as cr left outer join fetch b.createdBy as cb left outer join fetch b.modifiedBy as mb left outer join fetch b.requestedBy as rb where b.tenantId =:id");
		query.setParameter("id", tenantId);
		List<BuyerSettings> list = query.getResultList();
		return (CollectionUtil.isNotEmpty(list) ? list.get(0) : null);
	}

	/**
	 * @param buyer
	 * @param createdDate
	 * @param createdBy
	 */
	@Override
	public void createDefaultBuyerSettings(Buyer buyer, Date createdDate, User createdBy) {
		try {
			// TODO: Implement logic to set currency and timezone based on country of registration of buyer
			BuyerSettings settings = new BuyerSettings();
			settings.setCreatedBy(createdBy);
			settings.setCreatedDate(createdDate);
			settings.setCurrency(currencyDao.findByProperty("currencyCode", "MYR"));
			settings.setDecimal("2");
			settings.setTenantId(buyer.getId());
			settings.setTimeZone(timeZoneDao.findByProperties(new String[] { "timeZoneDescription", "status" }, new Object[] { "Asia/Kuala_Lumpur", Status.ACTIVE }));
			save(settings);
		} catch (Exception e) {
			LOG.error("Error creating default buyer settings : " + e.getMessage(), e);
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true, propagation = Propagation.REQUIRES_NEW)
	public String getBuyerTimeZoneByTenantId(String tenantId) {
		final Query query = getEntityManager().createQuery("select tz.timeZone from BuyerSettings bs left outer join bs.timeZone as tz  where bs.tenantId = :id");
		query.setParameter("id", tenantId);
		List<String> list = query.getResultList();
		return (CollectionUtil.isNotEmpty(list) ? list.get(0) : null);
	}

	@Override
	@SuppressWarnings("unchecked")
	public BuyerSettings getTenantIdBybuyerKey(String buyerKey) {
		String hql = "select bs from BuyerSettings bs where bs.buyerKey=:buyerKey";
		Query query = getEntityManager().createQuery(hql);
		query.setParameter("buyerKey", buyerKey);
		try {
			List<BuyerSettings> buyerSettings = query.getResultList();
			if(CollectionUtil.isNotEmpty(buyerSettings)) {
				return buyerSettings.get(0);
			} else {
				return null;
			}
		} catch (Exception e) {
			LOG.error(e);
			return null;
		}
	}

	@Override
	public void updateBuyerSettingsLogo(BuyerSettings buyerSettings, String tenantId) {
		LOG.info(buyerSettings.getFileName() + "updateBuyerSettingsLogo( buyerSettings,tenantId) called" + tenantId);
		final Query query = getEntityManager().createQuery("update BuyerSettings bs set bs.fileAttatchment =:fileAttatchment ,bs.fileName =:fileName , bs.contentType =:contentType , bs.fileSizeKb =:fileSizeKb where bs.tenantId =:tenantId");
		query.setParameter("fileAttatchment", buyerSettings.getFileAttatchment());
		query.setParameter("fileName", buyerSettings.getFileName());
		query.setParameter("contentType", buyerSettings.getContentType());
		query.setParameter("fileSizeKb", buyerSettings.getFileSizeKb());
		query.setParameter("tenantId", tenantId);
		query.executeUpdate();
	}

	@Override
	@SuppressWarnings("unchecked")
	public String getPublishUrlFromBuyerSettingsByTenantId(String tenantId, RfxTypes eventType) {

		String hql = "select ";

		switch (eventType) {
		case RFI:
			hql += " bs.rfiPublishUrl ";
			break;
		case RFT:
			hql += " bs.rftPublishUrl ";
			break;
		case RFQ:
			hql += " bs.rfqPublishUrl ";
			break;
		default:
			break;
		}

		hql += " from BuyerSettings bs where bs.tenantId = :tenantId";

		Query query = getEntityManager().createQuery(hql);
		query.setParameter("tenantId", tenantId);
		List<String> list = query.getResultList();
		return (CollectionUtil.isNotEmpty(list) ? list.get(0) : null);

	}

	@SuppressWarnings("unchecked")
	@Override
	public String getPublishUpdateUrlFromBuyerSettingsByTenantId(String tenantId, RfxTypes eventType) {
		String hql = "select ";
		switch (eventType) {
		case RFI:
			hql += " bs.rfiUpdatePublishUrl ";
			break;
		case RFT:
			hql += " bs.rftUpdatePublishUrl ";
			break;
		case RFQ:
			hql += " bs.rfqUpdatePublishUrl ";
			break;
		default:
			break;
		}

		hql += " from BuyerSettings bs where bs.tenantId = :tenantId";

		Query query = getEntityManager().createQuery(hql);
		query.setParameter("tenantId", tenantId);
		List<String> list = query.getResultList();
		return (CollectionUtil.isNotEmpty(list) ? list.get(0) : null);

	}

	@Override
	public Boolean isAutoPublishePoSettingsByTenantId(String tenantId) {
		String hql = "select bs.autoPublishPo from BuyerSettings bs where bs.tenantId = :tenantId";
		Query query = getEntityManager().createQuery(hql);
		query.setParameter("tenantId", tenantId);
		return (Boolean) query.getSingleResult();

	}

	@Override
	public Boolean isAutoCreatePoSettingsByTenantId(String tenantId) {
		String hql = "select bs.autoCreatePo from BuyerSettings bs where bs.tenantId = :tenantId";
		Query query = getEntityManager().createQuery(hql);
		query.setParameter("tenantId", tenantId);
		return (Boolean) query.getSingleResult();

	}
	
	@Override
	public Boolean isEnableUnitAndCostCorrelationByTenantId(String tenantId) {
		String hql = "select bs.enableUnitAndCostCorrelation from BuyerSettings bs where bs.tenantId = :tenantId";
		Query query = getEntityManager().createQuery(hql);
		query.setParameter("tenantId", tenantId);
		return (Boolean) query.getSingleResult();

	}
	
	@Override
	public Boolean isEnableUnitAndGroupCodeCorrelationByTenantId(String tenantId) {
		String hql = "select bs.enableUnitAndGrpCodeCorrelation from BuyerSettings bs where bs.tenantId = :tenantId";
		Query query = getEntityManager().createQuery(hql);
		query.setParameter("tenantId", tenantId);
		return (Boolean) query.getSingleResult();

	}
}