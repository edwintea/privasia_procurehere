package com.privasia.procurehere.service.impl;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.privasia.procurehere.core.dao.RfaSorDao;
import com.privasia.procurehere.core.dao.RfaSupplierSorDao;
import com.privasia.procurehere.core.entity.RfaSupplierSor;
import com.privasia.procurehere.core.entity.RfiSupplierSor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.privasia.procurehere.core.dao.AuctionBidsDao;
import com.privasia.procurehere.core.dao.AuctionRulesDao;
import com.privasia.procurehere.core.dao.RfaBqDao;
import com.privasia.procurehere.core.dao.RfaBqItemDao;
import com.privasia.procurehere.core.dao.RfaCqItemDao;
import com.privasia.procurehere.core.dao.RfaEventDao;
import com.privasia.procurehere.core.dao.RfaEventSupplierDao;
import com.privasia.procurehere.core.dao.RfaSupplierBqDao;
import com.privasia.procurehere.core.dao.RfaSupplierBqItemDao;
import com.privasia.procurehere.core.dao.RfaSupplierCqDao;
import com.privasia.procurehere.core.dao.RfaSupplierCqItemDao;
import com.privasia.procurehere.core.entity.AuctionBids;
import com.privasia.procurehere.core.entity.AuctionRules;
import com.privasia.procurehere.core.entity.RfaCq;
import com.privasia.procurehere.core.entity.RfaEvent;
import com.privasia.procurehere.core.entity.RfaEventSupplier;
import com.privasia.procurehere.core.entity.RfaSupplierBq;
import com.privasia.procurehere.core.entity.RfaSupplierBqItem;
import com.privasia.procurehere.core.entity.Supplier;
import com.privasia.procurehere.core.entity.User;
import com.privasia.procurehere.core.enums.PreBidByType;
import com.privasia.procurehere.core.exceptions.ApplicationException;
import com.privasia.procurehere.core.exceptions.NotAllowedException;
import com.privasia.procurehere.core.pojo.RfaSupplierBqPojo;
import com.privasia.procurehere.core.supplier.dao.SupplierDao;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.SecurityLibrary;
import com.privasia.procurehere.core.utils.StringUtils;
import com.privasia.procurehere.service.RfaEventSupplierService;
import com.privasia.procurehere.service.RfaSupplierBqService;

@Service
@Transactional(readOnly = true)
public class RfaSupplierBqServiceImpl implements RfaSupplierBqService {

	private static final Logger LOG = LogManager.getLogger(Global.AUCTION_LOG);

	@Autowired
	RfaSupplierBqDao supplierBqDao;

	@Autowired
	RfaEventSupplierDao eventSupplierDao;

	@Autowired
	RfaSupplierBqItemDao supplierBqItemDao;

	@Autowired
	AuctionBidsDao auctionBidsDao;

	@Autowired
	RfaEventDao rfaEventDao;

	@Autowired
	RfaBqItemDao rfaBqitemDao;

	@Autowired
	RfaEventSupplierService eventSupplierService;

	@Autowired
	RfaCqItemDao cqItemDao;

	@Autowired
	RfaSupplierCqItemDao supplierCqItemDao;

	@Autowired
	AuctionRulesDao auctionRulesDao;

	@Autowired
	RfaSupplierBqDao rfaSupplierBqDao;

	@Autowired
	RfaBqDao rfaBqDao;

	@Autowired
	RfaSorDao rfaSorDao;

	@Autowired
	RfaSupplierSorDao rfaSupplierSorDao;

	@Autowired
	RfaSupplierCqDao rfaSupplierCqDao;

	@Autowired
	SupplierDao supplierDao;

	@Override
	@Transactional(readOnly = false)
	public RfaSupplierBq getSupplierBqByBqAndSupplierId(String id, String supplierId) {
		RfaSupplierBq supBq = supplierBqDao.findBqByBqId(id, supplierId);
		if (supBq != null && supBq.getSupplier() != null) {
			supBq.getSupplier().getCompanyName();
		}
		if (CollectionUtil.isNotEmpty(supBq.getSupplierBqItems())) {
			for (RfaSupplierBqItem item : supBq.getSupplierBqItems()) {
				if (item.getBqItem() != null) {
					item.getBqItem().getId();
				}
			}
		}

		return supBq;
	}

	@Override
	@Transactional(readOnly = false)
	public RfaSupplierBq updateSupplierBq(RfaSupplierBq persistSupplier) {
		return supplierBqDao.update(persistSupplier);
	}

	@Override
	@Transactional(readOnly = false, rollbackFor = { ApplicationException.class, Exception.class })
	public RfaSupplierBq saveOrUpdateSupplierBq(RfaSupplierBq persistSupplier, String supplierId, String eventId, String ipAddress) throws ApplicationException {
		RfaSupplierBq rfaSupplierBq = supplierBqDao.findBqByBqId(persistSupplier.getBq().getId(), supplierId);
		try {
			RfaEvent event = rfaEventDao.findByEventId(eventId);
			List<RfaSupplierBqItem> supplierBqItems = rfaSupplierBq.getSupplierBqItems();
			List<RfaSupplierBqItem> persistSupplierBqItems = persistSupplier.getSupplierBqItems();
			List<RfaSupplierBqItem> listSupplierBqItem = new ArrayList<RfaSupplierBqItem>();
			for (RfaSupplierBqItem rfaSupplierBqItem : supplierBqItems) {
				for (RfaSupplierBqItem persistSupplierBqItem : persistSupplierBqItems) {
					if (persistSupplierBqItem.getId().equals(rfaSupplierBqItem.getId())) {
						rfaSupplierBqItem.setTax(persistSupplierBqItem.getTax());
						rfaSupplierBqItem.setTaxDescription(persistSupplierBqItem.getTaxDescription());
						rfaSupplierBqItem.setTaxType(persistSupplierBqItem.getTaxType());
						rfaSupplierBqItem.setTotalAmount(persistSupplierBqItem.getTotalAmount());
						rfaSupplierBqItem.setTotalAmountWithTax(persistSupplierBqItem.getTotalAmountWithTax());
						if (rfaSupplierBq.getField1ToShowSupplier()) {
							rfaSupplierBqItem.setField1(persistSupplierBqItem.getField1());
						}
						if (rfaSupplierBq.getField2ToShowSupplier()) {
							rfaSupplierBqItem.setField2(persistSupplierBqItem.getField2());
						}
						if (rfaSupplierBq.getField3ToShowSupplier()) {
							rfaSupplierBqItem.setField3(persistSupplierBqItem.getField3());
						}
						if (rfaSupplierBq.getField4ToShowSupplier()) {
							rfaSupplierBqItem.setField4(persistSupplierBqItem.getField4());
						}
						rfaSupplierBqItem.setUnitPrice(persistSupplierBqItem.getUnitPrice());
						rfaSupplierBqItem.setUnitPriceType(persistSupplierBqItem.getUnitPriceType());
						listSupplierBqItem.add(rfaSupplierBqItem);
					}
				}
			}

			LOG.info("Rfa Supplier aary list Size : " + listSupplierBqItem.size());
			for (RfaSupplierBqItem rfaSupplierBqItem : listSupplierBqItem) {
				LOG.info("Rfa Supplier : " + rfaSupplierBqItem.getId());
				supplierBqItemDao.update(rfaSupplierBqItem);
			}

			LOG.info("Additional tax :" + persistSupplier.getAdditionalTax() + "  : TaxDescription : " + persistSupplier.getTaxDescription());
			// rfaSupplierBq.setSupplierBqItems(listSupplierBqItem);
			rfaSupplierBq.setAdditionalTax(persistSupplier.getAdditionalTax());
			rfaSupplierBq.setTaxDescription(persistSupplier.getTaxDescription());
			rfaSupplierBq.setGrandTotal(persistSupplier.getGrandTotal());
			rfaSupplierBq.setRevisedGrandTotal(persistSupplier.getGrandTotal());
			rfaSupplierBq.setTotalAfterTax(persistSupplier.getTotalAfterTax());

			// Calculate Difference
			if (rfaSupplierBq.getInitialPrice() != null) {
				BigDecimal differenceAmount = rfaSupplierBq.getInitialPrice().subtract(rfaSupplierBq.getTotalAfterTax());
				MathContext mc = new MathContext(4, RoundingMode.HALF_EVEN);
				BigDecimal diffPercentage = differenceAmount.divide(rfaSupplierBq.getInitialPrice(), mc).multiply(new BigDecimal(100.0000)).setScale(2, RoundingMode.HALF_EVEN);
				rfaSupplierBq.setDifferncePerToInitial(diffPercentage.abs());
				LOG.info("Differnece % : " + diffPercentage);
			}
			rfaSupplierBq = supplierBqDao.update(rfaSupplierBq);

			// rfaSupplierBq.setAuditSupplierBqItems(rfaSupplierBq.getSupplierBqItems());

			LOG.info("Rfa total after tax : " + rfaSupplierBq.getTotalAfterTax());
			// For Save the auction bids at time of submission
			AuctionBids auctionBids = new AuctionBids();
			auctionBids.setAmount(rfaSupplierBq.getTotalAfterTax());
			auctionBids.setEvent(event);
			try {
				ObjectMapper mapper = new ObjectMapper();
				String json = mapper.writeValueAsString(rfaSupplierBq);
				auctionBids.setDetails(json);
				rfaSupplierBq.setAuditSupplierBqItems(null);
				LOG.info("BID JSON : " + json);
				RfaSupplierBq bq = mapper.readValue(json, RfaSupplierBq.class);
				LOG.info("Deserialized Object : " + bq.toLogString());
			} catch (Exception e) {
				LOG.error("Error while Converting Supplier bid to json , " + e.getMessage(), e);
			}
			auctionBids.setBidSubmissionDate(new Date());
			auctionBids.setBidBySupplier(rfaSupplierBq.getSupplier());

			auctionBids.setIpAddress(ipAddress);
			eventSupplierDao.updateEventSupplierForAuction(eventId, supplierId, ipAddress);

			// update ranking
			Integer rankOfSupplier = null;
			AuctionRules auctionRules = auctionRulesDao.findAuctionRulesByEventId(eventId);
			if (auctionRules.getFowardAuction()) {
				LOG.info("Here to update the rank : FRD :   ");
				rankOfSupplier = eventSupplierDao.updateSupplierAuctionRank(eventId, Boolean.TRUE, supplierId);
				LOG.info("Here to after update the rank : FRD :   " + rankOfSupplier);
			} else {
				rankOfSupplier = eventSupplierDao.updateSupplierAuctionRank(eventId, Boolean.FALSE, supplierId);
			}
			auctionBids.setRankForBid(rankOfSupplier);
			auctionBids = auctionBidsDao.save(auctionBids);
			if (rankOfSupplier == null) {
				if (auctionRules.getFowardAuction()) {
					LOG.info("Here to update the rank : FRD :   ");
					rankOfSupplier = eventSupplierDao.updateSupplierAuctionRank(eventId, Boolean.TRUE, supplierId);
					LOG.info("Here to after update the rank : FRD :   " + rankOfSupplier);
				} else {
					rankOfSupplier = eventSupplierDao.updateSupplierAuctionRank(eventId, Boolean.FALSE, supplierId);
					LOG.info("Here to after update the rank : FRD :   " + rankOfSupplier);
				}
				auctionBids.setRankForBid(rankOfSupplier);
				auctionBidsDao.save(auctionBids);
			}
		} catch (Exception e) {
			LOG.error("Error : " + e.getMessage(), e);
			throw new ApplicationException("Error suring bid save : " + e.getMessage(), e);
		}
		return rfaSupplierBq;
	}

	@Override
	public List<RfaSupplierBq> findRfaSupplierBqBySupplierIds(List<String> supplierIds, String eventId) {
		List<RfaSupplierBq> supplierBqs = supplierBqDao.findRfaSupplierBqBySupplierIds(supplierIds, eventId, null);

		List<RfaSupplierBqPojo> listBqPojo = new ArrayList<RfaSupplierBqPojo>();
		for (RfaSupplierBq supplierBq : supplierBqs) {
			RfaSupplierBqPojo rfaBqPojo = new RfaSupplierBqPojo();
			rfaBqPojo.setCurrentPrice(supplierBq.getTotalAfterTax());
			rfaBqPojo.setDifferencePercentage(supplierBq.getDifferncePerToInitial());
			rfaBqPojo.setInitialPrice(supplierBq.getInitialPrice());
			RfaEventSupplier eventSupplier = eventSupplierDao.findEventSupplierByEventIdAndSupplierId(eventId, supplierBq.getSupplier().getId());
			rfaBqPojo.setNumberOfBids(eventSupplier.getNumberOfBids());
			listBqPojo.add(rfaBqPojo);
		}
		return supplierBqs;
	}

	@Override
	public RfaSupplierBq findSupplierBqByBqId(String bqId) {
		return supplierBqDao.findSupplierBqByBqId(bqId);
	}

	@Override
	public List<RfaSupplierBqPojo> getSupplierListForAuctionConsole(String eventId, Integer limitSupplier) {
		List<RfaSupplierBqPojo> auctionConsoleSupplierList = supplierBqDao.getSupplierListForAuctionConsole(eventId, limitSupplier);
		// for (RfaSupplierBqPojo rfaSupplierBqPojo :
		// auctionConsoleSupplierList) {
		// LOG.info("rfa supplier bq Pojo : " +
		// rfaSupplierBqPojo.getRankOfSupplier());
		// }
		return auctionConsoleSupplierList;
	}

	@Override
	public BigDecimal getMaxBidFromAllBidsOfSuppliers(String bqId) {
		return supplierBqDao.getMaxBidFromAllBidsOfSuppliers(bqId);
	}

	@Override
	public Integer getCountsOfSameBidBySupliers(String bqId, String eventId, BigDecimal bidAmount) {
		return supplierBqDao.getCountsOfSameBidBySupliers(bqId, eventId, bidAmount);
	}

	@Override
	public Integer getCountsOfRfaSupplierBqBySuplierIdAndBqId(String eventId, String supplierId) {
		return supplierBqDao.getCountsOfRfaSupplierBqBySuplierIdAndBqId(eventId, supplierId);
	}

	@Override
	public Boolean checkMandatoryToFinishEvent(String supplierId, String eventId) throws NotAllowedException, Exception {
		Boolean isBuyerSubmitted = Boolean.FALSE;
		AuctionRules auctionRules = auctionRulesDao.findLeanAuctionRulesByEventId(eventId);
		List<String> bqNames = rfaBqDao.rfaBqNamesByEventId(eventId);

		if (CollectionUtil.isNotEmpty(bqNames) && bqNames.size() > 0) {
			List<RfaSupplierBq> supplierBqList = rfaSupplierBqDao.rfaSupplierBqAnswerdByEventIdAndSupplierId(eventId, supplierId);
			if (CollectionUtil.isEmpty(supplierBqList) || ((CollectionUtil.isNotEmpty(supplierBqList)) && supplierBqList.size() != bqNames.size())) {
				if (CollectionUtil.isNotEmpty(supplierBqList)) {
					for (RfaSupplierBq bq : supplierBqList) {
						bqNames.remove(bq.getName());
						if (Boolean.FALSE == isBuyerSubmitted) {
							isBuyerSubmitted = bq.getBuyerSubmited();
						}
					}
				}
			} else {
				// All OK
				bqNames.clear();
			}
		}

		List<String> cqNames = new ArrayList<>();
		List<RfaCq> cqItemList = cqItemDao.rfaMandatoryCqNamesbyEventId(eventId);
		if (CollectionUtil.isNotEmpty(cqItemList)) {
			List<RfaCq> supplierRequiredCqItemList = supplierCqItemDao.findRequiredSupplierCqCountByEventId(supplierId, eventId);

			for (RfaCq cq : cqItemList) {
				boolean found = false;
				for (RfaCq supCq : supplierRequiredCqItemList) {
					if (supCq.getName().equals(cq.getName())) {
						found = true;
						if (supCq.getMandatoryItemCount() != cq.getMandatoryItemCount()) {
							cqNames.add(cq.getName());
						}
					}
				}
				if (!found) {
					cqNames.add(cq.getName());
				}
			}
		}


		List<String> sorNames = rfaSorDao.rfaSorNamesByEventId(eventId);
		if (CollectionUtil.isNotEmpty(sorNames) && sorNames.size() > 0) {
			List<RfaSupplierSor> supplierSorList = rfaSupplierSorDao.rfaSupplierSorAnswerdByEventIdAndSupplierId(eventId, supplierId);
			if (CollectionUtil.isEmpty(supplierSorList) || (CollectionUtil.isNotEmpty(supplierSorList)) && supplierSorList.size() != sorNames.size()) {
				if (CollectionUtil.isNotEmpty(supplierSorList)) {
					for (RfaSupplierSor bq : supplierSorList) {
						sorNames.remove(bq.getName());
					}
				}
			} else {
				// All OK
				sorNames.clear();
			}
		}

		RfaEventSupplier eventSupplier = eventSupplierService.findSupplierByIdAndEventId(SecurityLibrary.getLoggedInUserTenantId(), eventId);

		if (CollectionUtil.isNotEmpty(cqNames) && CollectionUtil.isNotEmpty(bqNames) && CollectionUtil.isNotEmpty(sorNames)) {
			LOG.error("Please fill up all mandatory  Questionnaire , all Bill Of Quantity and Schedule of Rate");
			throw new NotAllowedException("Please fill up all mandatory Questionnaire , all Bill Of Quantity and Schedule of Rate");
		} else if (CollectionUtil.isNotEmpty(cqNames) && CollectionUtil.isNotEmpty(bqNames)) {
			LOG.error("Please fill up all mandatory Questionnaire and all Bill Of Quantity");
			throw new NotAllowedException("Please fill up all mandatory Questionnaire and all Bill Of Quantity");
		} else if (CollectionUtil.isNotEmpty(bqNames) && CollectionUtil.isNotEmpty(sorNames)) {
			LOG.error("Please fill up all mandatory all Bill Of Quantity and Schedule of Rate");
			throw new NotAllowedException("Please fill up all Bill Of Quantity and Schedule of Rate");
		} else if (CollectionUtil.isNotEmpty(cqNames) && CollectionUtil.isNotEmpty(sorNames)) {
			LOG.error("Please fill up all mandatory all Questionnaire and Schedule of Rate");
			throw new NotAllowedException("Please fill up all mandatory Questionnaire and Schedule of Rate");
		} else if (CollectionUtil.isNotEmpty(cqNames)) {
			String s = "";
			int i = 0;
			for (String bq : cqNames) {
				if (i == 0) {
					s = bq;
				} else {
					s += "," + bq;
				}
				i++;
			}
			throw new NotAllowedException("Please fill up mandatory Questionnaire : " + s);
		} else if (CollectionUtil.isNotEmpty(bqNames)) {
			String s = "";
			int i = 0;
			for (String bq : bqNames) {
				if (i == 0) {
					s = bq;
				} else {
					s += "," + bq;
				}
				i++;
			}
			throw new NotAllowedException("Please fill up mandatory Bill of Quantities : " + s);
		} else if (CollectionUtil.isNotEmpty(sorNames)) {
			String s = "";
			int i = 0;
			for (String bq : sorNames) {
				if (i == 0) {
					s = bq;
				} else {
					s += "," + bq;
				}
				i++;
			}
			throw new NotAllowedException("Please fill up mandatory Schedule of Rate : " + s);
		} else if ((auctionRules.getPreBidBy() == PreBidByType.BUYER && Boolean.TRUE != eventSupplier.getConfirmPriceSubmitted())) {
			LOG.info("Please confirm Bill Of Quantity price before submitting Pre-bid ");
			throw new NotAllowedException("Please confirm Bill Of Quantity price before submitting Pre-bid");
		}

		long pendingBq = rfaSupplierBqDao.findPendingBqAnswerdByEventIdAndSupplierId(eventId, supplierId);
		long pendingCq = rfaSupplierCqDao.findPendingCqsByEventIdAndEventCqId(eventId, supplierId);
		long pendingSor = rfaSupplierSorDao.findPendingSorsByEventIdAndEventSorId(eventId, supplierId);

		if (pendingBq > 0 && pendingCq > 0 && pendingSor > 0) {
			throw new NotAllowedException("Please complete all Questionnaire , all Bill of Quantity and all schedule of rate");
		}

		if (pendingBq > 0) {
			throw new NotAllowedException("Please complete all Bill of Quantity");
		}

		if (pendingCq > 0) {
			throw new NotAllowedException("Please complete all Questionnaire");
		}

		if(pendingSor > 0) {
			throw new NotAllowedException("Please complete all Schedule of rate");
		}

		List<String> supplierAttach = cqItemDao.findAllSupplierAttachRequiredId(eventId);
		if (CollectionUtil.isNotEmpty(supplierAttach)) {
			int rfaCqCount = supplierCqItemDao.findRfaRequiredCqCountBySupplierId(supplierId, eventId);
			if (rfaCqCount > 0) {
				throw new NotAllowedException("Please Attach All required Documents in Questionnaires");
			}

		}

		return true;
	}

	@Override
	public RfaSupplierBq getRfaSupplierBqForLumsumBid(String bqId, String supplierId) {
		LOG.info("Supplier ID : " + supplierId + " : Bq Id : " + bqId);
		return supplierBqDao.getRfaSupplierBqForLumsumBid(bqId, supplierId);
	}

	@Override
	public RfaSupplierBq findById(String id) {
		return supplierBqDao.findById(id);
	}

	@Override
	@Transactional(readOnly = false)
	// Return type changed BigDecimal to String because we need to format the
	// returned amount
	public String revertOnAuctionBid(String supplierId, String eventId, String bidId, String ipAddress) throws JsonParseException, JsonMappingException, IOException {
		RfaSupplierBq bq = null;
		ObjectMapper mapper = new ObjectMapper();
		AuctionBids auctionBids = auctionBidsDao.findById(bidId);
		AuctionRules auctionRules = auctionRulesDao.findLeanAuctionRulesByEventId(eventId);
		if (auctionBids != null) {
			LOG.info("Reverting bid price for supplier : " + supplierId + " to : " + auctionBids.getDetails());

			// For itemized bidding
			if (StringUtils.checkString(auctionBids.getDetails()).length() != 0 && auctionRules.getItemizedBiddingWithTax() != null) {

				bq = mapper.readValue(auctionBids.getDetails(), RfaSupplierBq.class);
				RfaSupplierBq persiteObj = supplierBqDao.findById(bq.getId());
				if (persiteObj != null) {
					bq.setBq(persiteObj.getBq());
					bq.setEvent(persiteObj.getEvent());
					Supplier supplier = new Supplier();
					supplier.setId(supplierId);
					bq.setSupplier(supplier);
					supplierBqDao.update(bq);

					// TODO: Insert into bid history and event audit at buyer
					// side.
					AuctionBids auctionBid = new AuctionBids();
					auctionBid.setAmount(bq.getTotalAfterTax());
					auctionBid.setEvent(bq.getEvent());
					try {
						mapper = new ObjectMapper();
						String json = mapper.writeValueAsString(bq);
						auctionBids.setDetails(json);
						bq.setAuditSupplierBqItems(null);
						LOG.info("BID JSON : " + json);
						RfaSupplierBq bq1 = mapper.readValue(json, RfaSupplierBq.class);
						LOG.info("Deserialized Object : " + bq1.toLogString());
					} catch (Exception e) {
						LOG.error("Error while Converting Supplier bid to json , " + e.getMessage(), e);
					}
					auctionBid.setBidSubmissionDate(new Date());
					auctionBid.setBidBySupplier(persiteObj.getSupplier());

					eventSupplierDao.updateEventSupplierForAuction(eventId, supplierId, ipAddress);

					// update ranking
					Integer rankOfSupplier = null;
					// AuctionRules auctionRules =
					// auctionRulesDao.findAuctionRulesByEventId(eventId);
					if (auctionRules.getFowardAuction()) {
						rankOfSupplier = eventSupplierDao.updateSupplierAuctionRank(eventId, Boolean.TRUE, supplierId);
					} else {
						rankOfSupplier = eventSupplierDao.updateSupplierAuctionRank(eventId, Boolean.FALSE, supplierId);
					}
					auctionBid.setRankForBid(rankOfSupplier);
					auctionBid.setIsReverted(Boolean.TRUE);
					auctionBid = auctionBidsDao.save(auctionBid);
					if (rankOfSupplier == null) {
						if (auctionRules.getFowardAuction()) {
							rankOfSupplier = eventSupplierDao.updateSupplierAuctionRank(eventId, Boolean.TRUE, supplierId);
						} else {
							rankOfSupplier = eventSupplierDao.updateSupplierAuctionRank(eventId, Boolean.FALSE, supplierId);
						}
						auctionBid.setRankForBid(rankOfSupplier);
						auctionBid.setIsReverted(Boolean.TRUE);
						auctionBidsDao.save(auctionBid);
					}
				}
				// For lumpsum bidding reversal
			} else if (auctionRules.getLumsumBiddingWithTax() != null) {
				bq = rfaSupplierBqDao.getRfaSupplierBqByEventIdAndSupplierId(eventId, supplierId);
				RfaSupplierBq persiteObj = supplierBqDao.findById(bq.getId());
				BigDecimal diffPercentage = null;
				if (persiteObj.getInitialPrice() != null) {
					BigDecimal differenceAmount = persiteObj.getInitialPrice().subtract(auctionBids.getAmount());
					MathContext mc = new MathContext(4, RoundingMode.HALF_EVEN);
					diffPercentage = differenceAmount.divide(persiteObj.getInitialPrice(), mc).multiply(new BigDecimal(100.0000)).setScale(2, RoundingMode.HALF_EVEN);
					LOG.info("Diff Percentage 1 : " + diffPercentage);
				}
				if (persiteObj != null) {
					bq.setBq(persiteObj.getBq());
					bq.setEvent(auctionBids.getEvent());
					bq.setSupplier(auctionBids.getBidBySupplier());
					bq.setTotalAfterTax(auctionBids.getAmount());
					bq.setDifferncePerToInitial(diffPercentage);
					supplierBqDao.update(bq);

					// TODO: Insert into bid history and event audit at buyer
					// side.
					AuctionBids auctionBid = new AuctionBids();
					auctionBid.setAmount(auctionBids.getAmount());
					auctionBid.setEvent(auctionBids.getEvent());
					auctionBid.setBidSubmissionDate(new Date());
					auctionBid.setBidBySupplier(auctionBids.getBidBySupplier());
					eventSupplierDao.updateEventSupplierForAuction(eventId, supplierId, ipAddress);

					// update ranking
					Integer rankOfSupplier = null;
					// AuctionRules auctionRules =
					// auctionRulesDao.findAuctionRulesByEventId(eventId);
					if (auctionRules.getFowardAuction()) {
						rankOfSupplier = eventSupplierDao.updateSupplierAuctionRank(eventId, Boolean.TRUE, supplierId);
					} else {
						rankOfSupplier = eventSupplierDao.updateSupplierAuctionRank(eventId, Boolean.FALSE, supplierId);
					}
					auctionBid.setRankForBid(rankOfSupplier);
					auctionBid.setIsReverted(Boolean.TRUE);
					auctionBid = auctionBidsDao.save(auctionBid);

					// update ranking
					if (rankOfSupplier == null) {
						if (auctionRules.getFowardAuction()) {
							rankOfSupplier = eventSupplierDao.updateSupplierAuctionRank(eventId, Boolean.TRUE, supplierId);
						} else {
							rankOfSupplier = eventSupplierDao.updateSupplierAuctionRank(eventId, Boolean.FALSE, supplierId);
						}
						auctionBid.setRankForBid(rankOfSupplier);
						auctionBid.setIsReverted(Boolean.TRUE);
						auctionBidsDao.save(auctionBid);
					}
				}

			} else {
				LOG.warn("Nothing to revert on Auction Id : " + eventId + " for supplier : " + supplierId + " for bid id : " + bidId);
			}
		}

		if (bq != null)

		{
			// to set the value in decimal format
			RfaEvent event = bq.getEvent();
			DecimalFormat df = null;
			if (event.getDecimal().equals("1")) {
				df = new DecimalFormat("#,###,###,##0.0");
			} else if (event.getDecimal().equals("2")) {
				df = new DecimalFormat("#,###,###,##0.00");
			} else if (event.getDecimal().equals("3")) {
				df = new DecimalFormat("#,###,###,##0.000");
			} else if (event.getDecimal().equals("4")) {
				df = new DecimalFormat("#,###,###,##0.0000");
			} else if (event.getDecimal().equals("5")) {
				df = new DecimalFormat("#,###,###,##0.00000");
			} else if (event.getDecimal().equals("6")) {
				df = new DecimalFormat("#,###,###,##0.000000");
			}
			return df.format(auctionBids.getAmount());
		}
		return null;

	}

	@Override
	@Transactional(readOnly = false)
	public void discardSupplierBqforEventId(String eventId) {
		supplierBqDao.discardSupplierBqforEventId(eventId);
	}

	@Override
	public List<RfaSupplierBq> findRfaSummarySupplierBqbyEventId(String eventId) {
		List<RfaSupplierBq> list = supplierBqDao.findRfaSummarySupplierBqbyEventId(eventId);
		if (CollectionUtil.isNotEmpty(list)) {
			for (RfaSupplierBq rfaSupplierBq : list) {
				if (rfaSupplierBq.getSupplier().getId() != null) {
					rfaSupplierBq.getSupplier().getId();
					if (CollectionUtil.isNotEmpty(rfaSupplierBq.getSupplierBqItems())) {
						for (RfaSupplierBqItem items : rfaSupplierBq.getSupplierBqItems()) {
							if (items.getBqItem() != null) {
								items.getBqItem().getId();
							}
						}
					}
				}
			}
		}
		return list;

	}

	@Override
	public List<RfaSupplierBq> findRfaSupplierBqbyEventIdAndSupplierId(String eventId, String supplierId) {
		return supplierBqDao.findRfaSupplierBqbyEventIdAndSupplierId(eventId, supplierId);
	}

	@Override
	public BigDecimal getMinBidFromAllBidsOfSuppliers(String bqId) {
		return supplierBqDao.getMinBidFromAllBidsOfSuppliers(bqId);
	}

	@Override
	public RfaSupplierBq getRfaSupplierBqByEventIdAndSupplierId(String eventId, String supplierId) {
		return rfaSupplierBqDao.getRfaSupplierBqByEventIdAndSupplierId(eventId, supplierId);
	}

	@Override
	@Transactional(readOnly = false)
	public void deleteRfaSupplierBq(String eventId, String supplierId) {
		rfaSupplierBqDao.discardSupplierBqforSupplierId(eventId, supplierId);
	}

	@Override
	public BigDecimal getLastTotalBqAmountBySupplierId(String eventId, String supplierId) {
		return supplierBqDao.getLastTotalBqAmountBySupplierId(eventId, supplierId);
	}

	@Override
	public Integer getCountsOfSamePreBidBySupliers(String eventId, BigDecimal bidAmount, String supplierId) {
		return supplierBqDao.getCountsOfSamePreBidBySupliers(eventId, bidAmount, supplierId);
	}

	@Override
	public List<RfaSupplierBqPojo> getSupplierListForDutchAuctionConsole(String eventId, Integer limitSupplier) {
		LOG.info("event ID : - " + eventId + " : limit : " + limitSupplier);
		List<RfaSupplierBqPojo> auctionConsoleSupplierList = supplierBqDao.getSupplierListForDutchAuctionConsole(eventId, limitSupplier);
		LOG.info("Supllier list size : " + auctionConsoleSupplierList.size());
		return auctionConsoleSupplierList;
	}

	@Override
	@Transactional(readOnly = false)
	public Integer updateSupplierAuctionRank(String eventId, Boolean isForwordAuction, String supplierId) {
		return eventSupplierDao.updateSupplierAuctionRank(eventId, isForwordAuction, supplierId);
	}

	@Override
	@Transactional(readOnly = false)
	public String revertOnAuctionBid(String supplierId, String eventId, String auctionBidId, String ipAddress, String revertReason, User getLoggedInUser) throws JsonParseException, JsonMappingException, IOException {
		RfaSupplierBq bq = null;
		ObjectMapper mapper = new ObjectMapper();
		AuctionBids auctionBids = auctionBidsDao.findById(auctionBidId);
		AuctionRules auctionRules = auctionRulesDao.findLeanAuctionRulesByEventId(eventId);
		if (auctionBids != null) {
			LOG.info("Reverting bid price for supplier : " + supplierId + " to : " + auctionBids.getDetails());

			// For itemized bidding
			if (StringUtils.checkString(auctionBids.getDetails()).length() != 0 && auctionRules.getItemizedBiddingWithTax() != null) {

				bq = mapper.readValue(auctionBids.getDetails(), RfaSupplierBq.class);
				RfaSupplierBq persiteObj = supplierBqDao.findById(bq.getId());
				if (persiteObj != null) {
					bq.setBq(persiteObj.getBq());
					bq.setEvent(persiteObj.getEvent());
					Supplier supplier = new Supplier();
					supplier.setId(supplierId);
					bq.setSupplier(supplier);
					supplierBqDao.update(bq);

					// TODO: Insert into bid history and event audit at buyer
					// side.
					AuctionBids auctionBid = new AuctionBids();
					auctionBid.setAmount(bq.getTotalAfterTax());
					auctionBid.setEvent(bq.getEvent());
					auctionBid.setRemark(revertReason);
					auctionBid.setRevertedBy(getLoggedInUser);
					try {
						mapper = new ObjectMapper();
						String json = mapper.writeValueAsString(bq);
						auctionBids.setDetails(json);
						bq.setAuditSupplierBqItems(null);
						LOG.info("BID JSON : " + json);
						RfaSupplierBq bq1 = mapper.readValue(json, RfaSupplierBq.class);
						LOG.info("Deserialized Object : " + bq1.toLogString());
					} catch (Exception e) {
						LOG.error("Error while Converting Supplier bid to json , " + e.getMessage(), e);
					}
					auctionBid.setBidSubmissionDate(new Date());
					auctionBid.setBidBySupplier(persiteObj.getSupplier());

					eventSupplierDao.updateEventSupplierForAuction(eventId, supplierId, ipAddress);

					// update ranking
					Integer rankOfSupplier = null;
					// AuctionRules auctionRules =
					// auctionRulesDao.findAuctionRulesByEventId(eventId);
					if (auctionRules.getFowardAuction()) {
						rankOfSupplier = eventSupplierDao.updateSupplierAuctionRank(eventId, Boolean.TRUE, supplierId);
					} else {
						rankOfSupplier = eventSupplierDao.updateSupplierAuctionRank(eventId, Boolean.FALSE, supplierId);
					}
					auctionBid.setRankForBid(rankOfSupplier);
					auctionBid.setIsReverted(Boolean.TRUE);
					auctionBid = auctionBidsDao.save(auctionBid);
					if (rankOfSupplier == null) {
						if (auctionRules.getFowardAuction()) {
							rankOfSupplier = eventSupplierDao.updateSupplierAuctionRank(eventId, Boolean.TRUE, supplierId);
						} else {
							rankOfSupplier = eventSupplierDao.updateSupplierAuctionRank(eventId, Boolean.FALSE, supplierId);
						}
						auctionBid.setRankForBid(rankOfSupplier);
						auctionBid.setIsReverted(Boolean.TRUE);
						auctionBidsDao.save(auctionBid);
					}

				}
				// For lumpsum bidding reversal
			} else if (auctionRules.getLumsumBiddingWithTax() != null) {
				bq = rfaSupplierBqDao.getRfaSupplierBqByEventIdAndSupplierId(eventId, supplierId);
				RfaSupplierBq persiteObj = supplierBqDao.findById(bq.getId());
				BigDecimal diffPercentage = null;
				if (persiteObj.getInitialPrice() != null) {
					BigDecimal differenceAmount = persiteObj.getInitialPrice().subtract(auctionBids.getAmount());
					MathContext mc = new MathContext(4, RoundingMode.HALF_EVEN);
					diffPercentage = differenceAmount.divide(persiteObj.getInitialPrice(), mc).multiply(new BigDecimal(100.0000)).setScale(2, RoundingMode.HALF_EVEN);
					LOG.info("Diff Percentage 1 : " + diffPercentage);
				}
				if (persiteObj != null) {
					bq.setBq(persiteObj.getBq());
					bq.setEvent(auctionBids.getEvent());
					bq.setSupplier(auctionBids.getBidBySupplier());
					bq.setTotalAfterTax(auctionBids.getAmount());
					bq.setDifferncePerToInitial(diffPercentage);
					supplierBqDao.update(bq);

					// TODO: Insert into bid history and event audit at buyer
					// side.
					AuctionBids auctionBid = new AuctionBids();
					auctionBid.setRemark(revertReason);
					auctionBid.setAmount(auctionBids.getAmount());
					auctionBid.setEvent(auctionBids.getEvent());
					auctionBid.setBidSubmissionDate(new Date());
					auctionBid.setBidBySupplier(auctionBids.getBidBySupplier());
					auctionBid.setRevertedBy(getLoggedInUser);
					eventSupplierDao.updateEventSupplierForAuction(eventId, supplierId, ipAddress);

					// update ranking
					Integer rankOfSupplier = null;
					// AuctionRules auctionRules =
					// auctionRulesDao.findAuctionRulesByEventId(eventId);
					if (auctionRules.getFowardAuction()) {
						rankOfSupplier = eventSupplierDao.updateSupplierAuctionRank(eventId, Boolean.TRUE, supplierId);
					} else {
						rankOfSupplier = eventSupplierDao.updateSupplierAuctionRank(eventId, Boolean.FALSE, supplierId);
					}
					auctionBid.setRankForBid(rankOfSupplier);
					auctionBid.setIsReverted(Boolean.TRUE);
					auctionBid = auctionBidsDao.save(auctionBid);
					// update ranking
					if (rankOfSupplier == null) {
						if (auctionRules.getFowardAuction()) {
							rankOfSupplier = eventSupplierDao.updateSupplierAuctionRank(eventId, Boolean.TRUE, supplierId);
						} else {
							rankOfSupplier = eventSupplierDao.updateSupplierAuctionRank(eventId, Boolean.FALSE, supplierId);
						}
						auctionBid.setRankForBid(rankOfSupplier);
						auctionBid.setIsReverted(Boolean.TRUE);
						auctionBidsDao.save(auctionBid);
					}
				}

			} else {
				LOG.warn("Nothing to revert on Auction Id : " + eventId + " for supplier : " + supplierId + " for bid id : " + auctionBidId);
			}
		}

		if (bq != null)

		{
			// to set the value in decimal format
			RfaEvent event = bq.getEvent();
			DecimalFormat df = null;
			if (event.getDecimal().equals("1")) {
				df = new DecimalFormat("#,###,###,##0.0");
			} else if (event.getDecimal().equals("2")) {
				df = new DecimalFormat("#,###,###,##0.00");
			} else if (event.getDecimal().equals("3")) {
				df = new DecimalFormat("#,###,###,##0.000");
			} else if (event.getDecimal().equals("4")) {
				df = new DecimalFormat("#,###,###,##0.0000");
			} else if (event.getDecimal().equals("5")) {
				df = new DecimalFormat("#,###,###,##0.00000");
			} else if (event.getDecimal().equals("6")) {
				df = new DecimalFormat("#,###,###,##0.000000");
			}
			return df.format(auctionBids.getAmount());
		}
		return null;

	}

	@Transactional(readOnly = false)
	@Override
	public RfaSupplierBq saveOrUpdateSupplierBq(RfaSupplierBq rfaSupplierBqCopy) {
		return supplierBqDao.update(rfaSupplierBqCopy);
	}

	@Override
	public Long getRfaSupplierBqCountForEvent(String eventId) {
		return rfaSupplierBqDao.getRfaSupplierBqCountForEvent(eventId);
	}

	@Override
	public RfaSupplierBq findRfaSupplierBqStatusbyEventIdAndSupplierId(String supplierId, String eventId, String id) {
		return rfaSupplierBqDao.findRfaSupplierBqStatusbyEventIdAndSupplierId(supplierId, eventId, id);
	}

	@Override
	public Boolean isBqPreBidPricingExistForSuppliers(String eventId) {
		return rfaSupplierBqDao.isBqPreBidPricingExistForSuppliers(eventId);
	}

	@Override
	@Transactional(readOnly = false)
	public void saveSupplierPreBidBqDetails(String rfaSupplierBqId, RfaEventSupplier rfaEventSupplier) {
		RfaSupplierBq OldSupplierBq = rfaSupplierBqDao.findById(rfaSupplierBqId);
		rfaEventSupplier = eventSupplierService.findSupplierById(rfaEventSupplier.getId());
		if (OldSupplierBq != null)
			LOG.info("Saving pre bid for new Supplier");
		if (rfaEventSupplier != null)
			LOG.info("Saving pre bid for new Supplier");

		if (OldSupplierBq != null && rfaEventSupplier != null) {
			LOG.info("Saving pre bid for new Supplier");
			Supplier supplier = supplierDao.findSuppById(rfaEventSupplier.getSupplier().getId());
			RfaSupplierBq supplierBq = supplierBqDao.findBqByEventIdAndSupplierId(OldSupplierBq.getEvent().getId(), OldSupplierBq.getBq().getId(), supplier.getId());
			if (supplierBq == null) {
				LOG.info("Saving pre bid for new Supplier");
				RfaSupplierBq supplierBqDetails = OldSupplierBq.copyBq(supplier, OldSupplierBq.getBq(), OldSupplierBq.getEvent());
				RfaSupplierBq sbqDb = saveOrUpdateSupplierBq(supplierBqDetails);
				RfaSupplierBqItem parent = null;
				for (RfaSupplierBqItem sbqi : supplierBqDetails.getSupplierBqItems()) {
					sbqi.setSupplierBq(sbqDb);
					if (sbqi.getOrder() != 0) {
						sbqi.setParent(parent);
					}
					RfaSupplierBqItem sbqiDb = supplierBqItemDao.save(sbqi);
					if (sbqi.getOrder() == 0) {
						parent = sbqiDb;
					}
				}

			}

		}

	}
}
