package com.privasia.procurehere.service.impl;

import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.privasia.procurehere.core.dao.FavoriteSupplierDao;
import com.privasia.procurehere.core.dao.PoDao;
import com.privasia.procurehere.core.dao.PoFinanceDao;
import com.privasia.procurehere.core.dao.PrDao;
import com.privasia.procurehere.core.dao.RfaEnvelopDao;
import com.privasia.procurehere.core.dao.RfaEventDao;
import com.privasia.procurehere.core.dao.RfiEnvelopDao;
import com.privasia.procurehere.core.dao.RfiEventDao;
import com.privasia.procurehere.core.dao.RfpEnvelopDao;
import com.privasia.procurehere.core.dao.RfpEventDao;
import com.privasia.procurehere.core.dao.RfqEnvelopDao;
import com.privasia.procurehere.core.dao.RfqEventDao;
import com.privasia.procurehere.core.dao.RftEnvelopDao;
import com.privasia.procurehere.core.dao.RftEventDao;
import com.privasia.procurehere.core.dao.RfxViewDao;
import com.privasia.procurehere.core.dao.UserDao;
import com.privasia.procurehere.core.entity.Buyer;
import com.privasia.procurehere.core.entity.Envelop;
import com.privasia.procurehere.core.entity.FavouriteSupplier;
import com.privasia.procurehere.core.entity.Po;
import com.privasia.procurehere.core.entity.Pr;
import com.privasia.procurehere.core.entity.Supplier;
import com.privasia.procurehere.core.enums.EventStatus;
import com.privasia.procurehere.core.pojo.EventPermissions;
import com.privasia.procurehere.core.pojo.GlobalSearchPojo;
import com.privasia.procurehere.core.supplier.dao.BuyerDao;
import com.privasia.procurehere.core.supplier.dao.SupplierDao;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.StringUtils;
import com.privasia.procurehere.service.GlobalSearchService;

/**
 * @author VIPUL
 */
@Service
@Transactional(readOnly = true)
public class GlobalSearchServiceImpl implements GlobalSearchService {

	private static final Logger LOG = LogManager.getLogger(Global.ADMIN_LOG);

	@Autowired
	RfxViewDao rfxViewDao;

	@Autowired
	BuyerDao buyerDao;

	@Autowired
	SupplierDao supplierDao;

	@Autowired
	PrDao prDao;

	@Autowired
	UserDao userDao;

	@Autowired
	FavoriteSupplierDao favoriteSupplierDao;

	@Autowired
	PoFinanceDao poFinanceDao;

	@Autowired
	PoDao poDao;

	@Autowired
	RfaEventDao rfaEventDao;

	@Autowired
	RftEventDao rftEventDao;

	@Autowired
	RfpEventDao rfpEventDao;

	@Autowired
	RfqEventDao rfqEventDao;

	@Autowired
	RfiEventDao rfiEventDao;

	@Autowired
	RfaEnvelopDao rfaEnvelopDao;

	@Autowired
	RftEnvelopDao rftEnvelopDao;

	@Autowired
	RfpEnvelopDao rfpEnvelopDao;

	@Autowired
	RfqEnvelopDao rfqEnvelopDao;

	@Autowired
	RfiEnvelopDao rfiEnvelopDao;

	@Override
	public List<GlobalSearchPojo> findAllRfxEventGlobally(String searchVal, String tenantId, boolean isSupplier, String userId, String loggedInUser) {
		List<GlobalSearchPojo> list = rfxViewDao.getAllRfxEventFromGlobalSearch(searchVal, tenantId, isSupplier, userId);
		if (CollectionUtil.isNotEmpty(list)) {
			List<Envelop> envelops = null;
			boolean isAllowed = true;
			EventPermissions eventPermissions = null;
			for (GlobalSearchPojo pojo : list) {
				LOG.info("Pojo : " + pojo.toLogString());
				switch (pojo.getType()) {
				case RFA: {
					eventPermissions = rfaEventDao.getUserEventPemissions(loggedInUser, pojo.getId());
					envelops = rfaEnvelopDao.getPlainEnvelopByEventId(pojo.getId());
					break;
				}
				case RFT: {
					eventPermissions = rftEventDao.getUserEventPemissions(loggedInUser, pojo.getId());
					envelops = rftEnvelopDao.getPlainEnvelopByEventId(pojo.getId());
					break;
				}
				case RFP: {
					eventPermissions = rfpEventDao.getUserEventPemissions(loggedInUser, pojo.getId());
					envelops = rfpEnvelopDao.getPlainEnvelopByEventId(pojo.getId());
					break;
				}
				case RFQ: {
					eventPermissions = rfqEventDao.getUserEventPemissions(loggedInUser, pojo.getId());
					envelops = rfqEnvelopDao.getPlainEnvelopByEventId(pojo.getId());
					break;
				}
				case RFI: {
					eventPermissions = rfiEventDao.getUserEventPemissions(loggedInUser, pojo.getId());
					envelops = rfiEnvelopDao.getPlainEnvelopByEventId(pojo.getId());
					break;
				}
				default:
					break;
				}

				if (eventPermissions != null) {
					if (eventPermissions.isEvaluator() || eventPermissions.isLeadEvaluator()) {
						if (CollectionUtil.isNotEmpty(envelops)) {
							for (Envelop env : envelops) {
								EventPermissions perm = null;
								switch (pojo.getType()) {
								case RFA:
									perm = rfaEventDao.getUserPemissionsForEnvelope(loggedInUser, pojo.getId(), env.getId());
									break;
								case RFP:
									perm = rfpEventDao.getUserPemissionsForEnvelope(loggedInUser, pojo.getId(), env.getId());
									break;
								case RFQ:
									perm = rfqEventDao.getUserPemissionsForEnvelope(loggedInUser, pojo.getId(), env.getId());
									break;
								case RFI:
									perm = rfiEventDao.getUserPemissionsForEnvelope(loggedInUser, pojo.getId(), env.getId());
									break;
								case RFT:
									perm = rftEventDao.getUserPemissionsForEnvelope(loggedInUser, pojo.getId(), env.getId());
									break;
								default:
									break;
								}
								if (perm != null && (perm.isEvaluator() || perm.isLeadEvaluator()) && Boolean.FALSE == env.getIsOpen()) {
									isAllowed = false;
								}
							}
						}
					}

					if (EventStatus.DRAFT == pojo.getStatus() && (eventPermissions.isOwner() || eventPermissions.isEditor() || eventPermissions.isViewer())) {
						pojo.setVisible(true);
					}

					if (EventStatus.PENDING == pojo.getStatus() && (StringUtils.checkString(userId).length() == 0 || eventPermissions.isOwner() || eventPermissions.isEditor() || eventPermissions.isViewer() || eventPermissions.isApprover())) {
						pojo.setVisible(true);
					}
					// schedule events
					if (((EventStatus.ACTIVE == pojo.getStatus() || EventStatus.APPROVED == pojo.getStatus()) && pojo.getEventStartDate().after(new Date())) && (StringUtils.checkString(userId).length() == 0 || eventPermissions.isOwner() || eventPermissions.isEditor() || eventPermissions.isViewer() || eventPermissions.isApprover())) {
						pojo.setVisible(true);
					}

					if (EventStatus.ACTIVE == pojo.getStatus() && (StringUtils.checkString(userId).length() == 0 || eventPermissions.isEvaluator() || eventPermissions.isLeadEvaluator() || eventPermissions.isOwner() || eventPermissions.isEditor() || eventPermissions.isViewer() || eventPermissions.isApprover())) {
						if (isAllowed) {
							pojo.setVisible(true);
						}
					}

					if (EventStatus.SUSPENDED == pojo.getStatus() && ((StringUtils.checkString(userId).length() == 0 || eventPermissions.isEvaluator() || eventPermissions.isLeadEvaluator() || eventPermissions.isOwner() || eventPermissions.isEditor() || eventPermissions.isViewer() || eventPermissions.isApprover() || eventPermissions.isRevertBidUser()))) {
						if (isAllowed) {
							pojo.setVisible(true);
						}
					}

					if (EventStatus.CLOSED == pojo.getStatus() && (StringUtils.checkString(userId).length() == 0 || eventPermissions.isOwner() || eventPermissions.isEditor() || eventPermissions.isViewer() || eventPermissions.isApprover() || eventPermissions.isOpener() || eventPermissions.isEvaluator() || eventPermissions.isLeadEvaluator() || eventPermissions.isConclusionUser())) {
						pojo.setVisible(true);
					}
					if (EventStatus.COMPLETE == pojo.getStatus() && (StringUtils.checkString(userId).length() == 0 || eventPermissions.isOwner() || eventPermissions.isEditor() || eventPermissions.isViewer() || eventPermissions.isApprover() || eventPermissions.isOpener() || eventPermissions.isEvaluator() || eventPermissions.isLeadEvaluator() || eventPermissions.isConclusionUser() || eventPermissions.isUnMaskUser())) {
						pojo.setVisible(true);
					}

					if (EventStatus.FINISHED == pojo.getStatus() && (StringUtils.checkString(userId).length() == 0 || eventPermissions.isOwner() || eventPermissions.isEditor() || eventPermissions.isViewer() || eventPermissions.isApprover() || eventPermissions.isOpener() || eventPermissions.isEvaluator() || eventPermissions.isLeadEvaluator() || eventPermissions.isUnMaskUser() || eventPermissions.isConclusionUser())) {
						pojo.setVisible(true);
					}

					if (EventStatus.CANCELED == pojo.getStatus() && (StringUtils.checkString(userId).length() == 0 || eventPermissions.isOwner() || eventPermissions.isEditor() || eventPermissions.isViewer() || eventPermissions.isApprover())) {
						pojo.setVisible(true);
					}

					pojo.setEventPermissions(eventPermissions);

				}
			}
		}
		return list;
	}

	@Override
	public List<Buyer> findAllBuyerGlobally(String searchVal) {
		return buyerDao.getAllBuyerFromGlobalSearch(searchVal);
	}

	@Override
	public List<Supplier> findAllSupplierGlobally(String searchVal) {
		return supplierDao.getAllSupplierFromGlobalSearch(searchVal);
	}

	@Override
	public List<Pr> findAllPrPoGlobally(String searchVal, String tenantId, boolean isSupplier, String userId, String status, String type, Date startDate, Date endDate) {
		return prDao.getAllPrPoFromGlobalSearch(searchVal, tenantId, isSupplier, userId, status, type, startDate, endDate);
	}

	@Override
	public List<FavouriteSupplier> findAllFavouriteSupplierGlobally(String searchVal, String tenantId) {
		return favoriteSupplierDao.getAllFavouriteSupplierFromGlobalSearch(searchVal, tenantId);
	}

	@Override
	public List<Supplier> findAllSupplierGloballyForFinance(String searchVal, String tenantId) {
		return poFinanceDao.findAllSupplierGloballyForFinance(searchVal, tenantId);
	}

	@Override
	public List<Po> findAllPrPoGloballyForFinance(String searchVal, String tenantId) {
		return poFinanceDao.findAllPrPoGloballyForFinance(searchVal, tenantId);
	}

	@Override
	public List<Po> findAllPoGlobally(String searchVal, String tenantId, String status, String type, Date startDate, Date endDate) {
		return poDao.getAllPoFromGlobalSearch(searchVal, tenantId, status, type, startDate, endDate);
	}
}
