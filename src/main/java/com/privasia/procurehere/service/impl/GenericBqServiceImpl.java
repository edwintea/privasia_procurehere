package com.privasia.procurehere.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.privasia.procurehere.core.dao.RfaBqDao;
import com.privasia.procurehere.core.dao.RfaBqItemDao;
import com.privasia.procurehere.core.dao.RfaSupplierBqDao;
import com.privasia.procurehere.core.dao.RfaSupplierBqItemDao;
import com.privasia.procurehere.core.dao.RfpBqDao;
import com.privasia.procurehere.core.dao.RfpBqItemDao;
import com.privasia.procurehere.core.dao.RfpSupplierBqDao;
import com.privasia.procurehere.core.dao.RfpSupplierBqItemDao;
import com.privasia.procurehere.core.dao.RfqBqDao;
import com.privasia.procurehere.core.dao.RfqBqItemDao;
import com.privasia.procurehere.core.dao.RfqSupplierBqDao;
import com.privasia.procurehere.core.dao.RfqSupplierBqItemDao;
import com.privasia.procurehere.core.dao.RftBqDao;
import com.privasia.procurehere.core.dao.RftBqItemDao;
import com.privasia.procurehere.core.dao.RftSupplierBqDao;
import com.privasia.procurehere.core.dao.RftSupplierBqItemDao;
import com.privasia.procurehere.core.entity.AuctionRules;
import com.privasia.procurehere.core.entity.Bq;
import com.privasia.procurehere.core.entity.BqItem;
import com.privasia.procurehere.core.entity.RfaBqItem;
import com.privasia.procurehere.core.entity.RfaEventBq;
import com.privasia.procurehere.core.entity.RfaEventSupplier;
import com.privasia.procurehere.core.entity.RfaSupplierBq;
import com.privasia.procurehere.core.entity.RfaSupplierBqItem;
import com.privasia.procurehere.core.entity.RfpBqItem;
import com.privasia.procurehere.core.entity.RfpEventBq;
import com.privasia.procurehere.core.entity.RfpSupplierBq;
import com.privasia.procurehere.core.entity.RfpSupplierBqItem;
import com.privasia.procurehere.core.entity.RfqBqItem;
import com.privasia.procurehere.core.entity.RfqEventBq;
import com.privasia.procurehere.core.entity.RfqSupplierBq;
import com.privasia.procurehere.core.entity.RfqSupplierBqItem;
import com.privasia.procurehere.core.entity.RftBqItem;
import com.privasia.procurehere.core.entity.RftEventBq;
import com.privasia.procurehere.core.entity.RftSupplierBq;
import com.privasia.procurehere.core.entity.RftSupplierBqItem;
import com.privasia.procurehere.core.enums.AuctionType;
import com.privasia.procurehere.core.enums.EventStatus;
import com.privasia.procurehere.core.enums.PreBidByType;
import com.privasia.procurehere.core.enums.PricingTypes;
import com.privasia.procurehere.core.enums.RfxTypes;
import com.privasia.procurehere.core.enums.SupplierBqStatus;
import com.privasia.procurehere.core.exceptions.ApplicationException;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.service.GenericBqService;
import com.privasia.procurehere.service.RfaEventService;
import com.privasia.procurehere.service.RfaEventSupplierService;

@Service
@Transactional(readOnly = true)
public class GenericBqServiceImpl implements GenericBqService {

	private static final Logger LOG = LogManager.getLogger(GenericBqServiceImpl.class);

	@Autowired
	RfaEventService rfaEventService;

	@Autowired
	RfpBqDao rfpEventBqDao;

	@Autowired
	RftBqDao rftEventBqDao;

	@Autowired
	RfaBqDao rfaEventBqDao;

	@Autowired
	RfqBqDao rfqEventBqDao;

	@Autowired
	RftBqItemDao rftBqItemDao;

	@Autowired
	RfpBqItemDao rfpBqItemDao;

	@Autowired
	RfqBqItemDao rfqBqItemDao;

	@Autowired
	RfaBqItemDao rfaBqItemDao;

	@Autowired
	RftSupplierBqDao rftSupplierBqDao;

	@Autowired
	RfpSupplierBqDao rfpSupplierBqDao;

	@Autowired
	RfqSupplierBqDao rfqSupplierBqDao;

	@Autowired
	RftSupplierBqItemDao rftSupplierBqItemDao;

	@Autowired
	RfpSupplierBqItemDao rfpSupplierBqItemDao;

	@Autowired
	RfqSupplierBqItemDao rfqSupplierBqItemDao;

	@Autowired
	RfaSupplierBqItemDao rfaSupplierBqItemDao;

	@Autowired
	RfaSupplierBqDao rfaSupplierBqDao;

	@Autowired
	RfaEventSupplierService rfaEventSupplierService;

	@Override
	@Transactional(readOnly = false)
	public Bq updateBq(Bq bq, RfxTypes type) {
		Bq retBq = null;
		switch (type) {
		case RFA:
			retBq = rfaEventBqDao.update((RfaEventBq) bq);
			break;
		case RFI:
			break;
		case RFP:
			retBq = rfpEventBqDao.update((RfpEventBq) bq);
			break;
		case RFQ:
			retBq = rfqEventBqDao.update((RfqEventBq) bq);
			break;
		case RFT:
			retBq = rftEventBqDao.update((RftEventBq) bq);
			break;
		default:
			break;
		}
		return retBq;
	}

	@Override
	@Transactional(readOnly = false)
	public BqItem updateBqItem(BqItem bqItem, RfxTypes type) {
		BqItem retBqItem = null;
		switch (type) {
		case RFA:
			retBqItem = rfaBqItemDao.update((RfaBqItem) bqItem);
			break;
		case RFI:
			break;
		case RFP:
			retBqItem = rfpBqItemDao.update((RfpBqItem) bqItem);
			break;
		case RFQ:
			retBqItem = rfqBqItemDao.update((RfqBqItem) bqItem);
			break;
		case RFT:
			retBqItem = rftBqItemDao.update((RftBqItem) bqItem);
			break;
		default:
			break;
		}
		return retBqItem;
	}

	@Override
	@Transactional(readOnly = false)
	public BqItem updateSupplierBqItem(BqItem bqItem, RfxTypes type, String decimal) throws ApplicationException {
		int round = decimal != null ? Integer.parseInt(decimal) : 2;

		switch (type) {
		case RFA: {

			RfaSupplierBqItem rfaSupplierItem = (RfaSupplierBqItem) bqItem;
			RfaSupplierBq uiPersistBq = null;
			BigDecimal totalAmountAfterTax = BigDecimal.ZERO;
			if (rfaSupplierItem != null) {
				uiPersistBq = rfaSupplierBqDao.findById(rfaSupplierItem.getSupplierBq().getId());
				if (uiPersistBq != null) {
					BigDecimal sum = BigDecimal.ZERO;
					if (CollectionUtil.isNotEmpty(uiPersistBq.getSupplierBqItems())) {
						for (RfaSupplierBqItem child : uiPersistBq.getSupplierBqItems()) {
							if (!child.getId().equals(rfaSupplierItem.getId())) {
								// To check wheteher Price type is Trade InPrice
								if (PricingTypes.TRADE_IN_PRICE == child.getPriceType()) {
									sum = sum.subtract(child.getTotalAmountWithTax() != null ? child.getTotalAmountWithTax() : BigDecimal.ZERO);
								} else {
									sum = sum.add(child.getTotalAmountWithTax() != null ? child.getTotalAmountWithTax() : BigDecimal.ZERO);
								}

							}
						}
					}
					totalAmountAfterTax = sum.add(bqItem.getAdditionalTax() != null ? bqItem.getAdditionalTax() : BigDecimal.ZERO);
					totalAmountAfterTax = totalAmountAfterTax.add(bqItem.getTotalAmountWithTax());
				}
			}

			LOG.info("UI Object : " + totalAmountAfterTax);
			LOG.info("---------------------------------------------------------------------");
			AuctionRules rules = rfaEventService.getAuctionRulesByEventId(rfaSupplierItem.getEvent().getId());
			LOG.info("Auction Type : " + rules.getAuctionType() + " Forward/Revers : " + rules.getFowardAuction());

			// Check if this is revised submission mode.
			boolean revisedSubmissionMode = false;
			RfaEventSupplier eventSupplier = rfaEventSupplierService.findEventSupplierByEventIdAndSupplierIgnoreSubmitStatus(rfaSupplierItem.getEvent().getId(), rfaSupplierItem.getSupplier().getId());

			// if Dutch Auction with BQ then revised submission should be true on event close
			boolean isDutchWithBQ = false;
			if ((eventSupplier.getRfxEvent().getAuctionType() == AuctionType.FORWARD_DUTCH || eventSupplier.getRfxEvent().getAuctionType() == AuctionType.REVERSE_DUTCH) && Boolean.TRUE == eventSupplier.getRfxEvent().getBillOfQuantity()) {
				isDutchWithBQ = true;
			}
			if (eventSupplier.getRfxEvent().getStatus() == EventStatus.CLOSED && (rules.getLumsumBiddingWithTax() != null || isDutchWithBQ) && eventSupplier.getRevisedBidSubmitted() == Boolean.FALSE) {
				revisedSubmissionMode = true;
			}

			// If revised submission mode then bypass validation.
			if (!revisedSubmissionMode && rules.getAuctionType() != AuctionType.FORWARD_DUTCH && rules.getAuctionType() != AuctionType.REVERSE_DUTCH) {
				RfaSupplierBqItem persistnatObj = rfaSupplierBqItemDao.getBqItemByBqItemId(rfaSupplierItem.getId(), rfaSupplierItem.getSupplier().getId());
				//if (persistnatObj.getSupplierBq().getBuyerSubmited()) {
					LOG.info("Persist Supplier Bq : " + persistnatObj.getSupplierBq().toString());

					if (rules.getPreBidBy() == PreBidByType.BUYER) {
						if (!rules.getIsPreBidSameBidPrice() && rfaEventSupplierService.checkAnySupplierSubmited(rfaSupplierItem.getEvent().getId())) {
							Integer countBidSup = rfaSupplierBqDao.getCountsOfSamePreBidBySupliers(rfaSupplierItem.getEvent().getId(), totalAmountAfterTax, null);
							LOG.info("Count : " + countBidSup);
							if (countBidSup != 0) {
								throw new ApplicationException("This bid value is not acceptable, One of the other supplier is having same price");
							}
						}

						if (rules.getIsPreBidHigherPrice() && rules.getFowardAuction()) {
							if (persistnatObj.getSupplierBq().getInitialPrice().doubleValue() > totalAmountAfterTax.doubleValue()) {
								throw new ApplicationException("Total amount is less than Initial bid price. Please enter more than or equal Intial bid price");
							}
						}

						if (rules.getIsPreBidHigherPrice() && !rules.getFowardAuction()) {
							if (persistnatObj.getSupplierBq().getInitialPrice().doubleValue() < totalAmountAfterTax.doubleValue()) {
								throw new ApplicationException("Total amount is higher than Initial bid price. Please enter less than or equal to Intial bid price");
							}
						}
					} else if (rules.getPreBidBy() == PreBidByType.SUPPLIER) {
						if (!rules.getIsPreBidSameBidPrice() && rfaEventSupplierService.checkAnySupplierSubmited(rfaSupplierItem.getEvent().getId())) {
							Integer countBidSup = rfaSupplierBqDao.getCountsOfSamePreBidBySupliers(rfaSupplierItem.getEvent().getId(), totalAmountAfterTax, null);
							if (countBidSup != 0) {
								throw new ApplicationException("This bid value is not acceptable, One of the other supplier is having same price");
							}
						}
					}
				//}
			}

			RfaSupplierBqItem item = rfaSupplierBqItemDao.update((RfaSupplierBqItem) bqItem);
			LOG.info("BQ ID : " + item.getSupplierBq().getBq().getId() + "  Suplier : " + item.getSupplier().getId());
			RfaSupplierBq persistBq = rfaSupplierBqDao.findBqByBqId(item.getSupplierBq().getBq().getId(), item.getSupplier().getId());
			if (persistBq != null) {
				persistBq.setAdditionalTax(bqItem.getAdditionalTax() != null ? bqItem.getAdditionalTax().setScale(round, RoundingMode.HALF_UP) : BigDecimal.ZERO);
				persistBq.setTaxDescription(bqItem.getTaxDescription());
				BigDecimal sum = BigDecimal.ZERO;
				if (CollectionUtil.isNotEmpty(persistBq.getSupplierBqItems())) {
					for (RfaSupplierBqItem child : persistBq.getSupplierBqItems()) {
						LOG.info("Total Amount With Tax : " + child.getTotalAmountWithTax());
						// To check wheteher Price type is Trade InPrice
						if (PricingTypes.TRADE_IN_PRICE == child.getPriceType()) {
							sum = sum.subtract(child.getTotalAmountWithTax() != null ? child.getTotalAmountWithTax() : BigDecimal.ZERO);
						} else {
							sum = sum.add(child.getTotalAmountWithTax() != null ? child.getTotalAmountWithTax() : BigDecimal.ZERO);
						}
					}
				}
				persistBq.setGrandTotal(sum != null ? sum.setScale(round, RoundingMode.HALF_UP) : BigDecimal.ZERO);
				if (!revisedSubmissionMode) {
					persistBq.setTotalAfterTax(persistBq.getGrandTotal().add(persistBq.getAdditionalTax() != null ? persistBq.getAdditionalTax() : BigDecimal.ZERO).setScale(round, RoundingMode.HALF_UP));
				} else {
					persistBq.setRevisedGrandTotal(persistBq.getGrandTotal().add(persistBq.getAdditionalTax() != null ? persistBq.getAdditionalTax() : BigDecimal.ZERO).setScale(round, RoundingMode.HALF_UP));
				}
				
				if(SupplierBqStatus.COMPLETED != persistBq.getSupplierBqStatus()) {
					persistBq.setSupplierBqStatus(SupplierBqStatus.DRAFT);
				}
				rfaSupplierBqDao.update(persistBq);
			}

			return item;
		}
		case RFI:
			break;
		case RFP: {
			RfpSupplierBqItem item = rfpSupplierBqItemDao.update((RfpSupplierBqItem) bqItem);
			RfpSupplierBq persistBq = rfpSupplierBqDao.findBqByBqId(item.getSupplierBq().getBq().getId(), item.getSupplier().getId());
			if (persistBq != null) {
				persistBq.setAdditionalTax(bqItem.getAdditionalTax() != null ? bqItem.getAdditionalTax().setScale(round, RoundingMode.HALF_UP) : BigDecimal.ZERO);
				persistBq.setTaxDescription(bqItem.getTaxDescription());
				BigDecimal sum = BigDecimal.ZERO;
				if (CollectionUtil.isNotEmpty(persistBq.getSupplierBqItems())) {
					for (RfpSupplierBqItem child : persistBq.getSupplierBqItems()) {
						// To check wheteher Price type is Trade InPrice
						if (PricingTypes.TRADE_IN_PRICE == child.getPriceType()) {
							sum = sum.subtract(child.getTotalAmountWithTax() != null ? child.getTotalAmountWithTax() : BigDecimal.ZERO);
						} else {
							sum = sum.add(child.getTotalAmountWithTax() != null ? child.getTotalAmountWithTax() : BigDecimal.ZERO);
						}
					}
				}
				persistBq.setGrandTotal(sum != null ? sum.setScale(round, RoundingMode.HALF_UP) : BigDecimal.ZERO);
				persistBq.setTotalAfterTax(persistBq.getGrandTotal().add(persistBq.getAdditionalTax() != null ? persistBq.getAdditionalTax() : BigDecimal.ZERO).setScale(round, RoundingMode.HALF_UP));
				if(SupplierBqStatus.COMPLETED != persistBq.getSupplierBqStatus()) {
					persistBq.setSupplierBqStatus(SupplierBqStatus.DRAFT);
				}
				rfpSupplierBqDao.update(persistBq);
			}
			return item;
		}
		case RFQ: {
			RfqSupplierBqItem item = rfqSupplierBqItemDao.update((RfqSupplierBqItem) bqItem);

			LOG.info("item.getPriceType()****************** :" + item.getPriceType());
			RfqSupplierBq persistBq = rfqSupplierBqDao.findBqByBqId(item.getSupplierBq().getBq().getId(), item.getSupplier().getId());
			if (persistBq != null) {
				persistBq.setAdditionalTax(bqItem.getAdditionalTax() != null ? bqItem.getAdditionalTax().setScale(round, RoundingMode.HALF_UP) : BigDecimal.ZERO);
				persistBq.setTaxDescription(bqItem.getTaxDescription());
				BigDecimal sum = BigDecimal.ZERO;
				if (CollectionUtil.isNotEmpty(persistBq.getSupplierBqItems())) {
					for (RfqSupplierBqItem child : persistBq.getSupplierBqItems()) {

						// To check wheteher Price type is Trade InPrice
						if (PricingTypes.TRADE_IN_PRICE == child.getPriceType()) {
							sum = sum.subtract(child.getTotalAmountWithTax() != null ? child.getTotalAmountWithTax() : BigDecimal.ZERO);
						} else {
							sum = sum.add(child.getTotalAmountWithTax() != null ? child.getTotalAmountWithTax() : BigDecimal.ZERO);
						}

					}
				}
				persistBq.setGrandTotal(sum != null ? sum.setScale(round, RoundingMode.HALF_UP) : BigDecimal.ZERO);

				persistBq.setTotalAfterTax(persistBq.getGrandTotal().add(persistBq.getAdditionalTax() != null ? persistBq.getAdditionalTax() : BigDecimal.ZERO).setScale(round, RoundingMode.HALF_UP));
				
				if(SupplierBqStatus.COMPLETED != persistBq.getSupplierBqStatus()) {
					persistBq.setSupplierBqStatus(SupplierBqStatus.DRAFT);
				}
				rfqSupplierBqDao.update(persistBq);
			}
			return item;
		}
		case RFT: {
			RftSupplierBqItem item = rftSupplierBqItemDao.update((RftSupplierBqItem) bqItem);
			LOG.info("BQ ID : " + item.getSupplierBq().getBq().getId() + "  Suplier : " + item.getSupplier().getId());
			RftSupplierBq persistBq = rftSupplierBqDao.findBqByBqId(item.getSupplierBq().getBq().getId(), item.getSupplier().getId());
			LOG.info("  RftSupplierBq  Before  : " + persistBq.toString());
			if (persistBq != null) {
				persistBq.setAdditionalTax(bqItem.getAdditionalTax() != null ? bqItem.getAdditionalTax().setScale(round, RoundingMode.HALF_UP) : BigDecimal.ZERO);
				persistBq.setTaxDescription(bqItem.getTaxDescription());
				BigDecimal sum = BigDecimal.ZERO;
				if (CollectionUtil.isNotEmpty(persistBq.getSupplierBqItems())) {
					for (RftSupplierBqItem child : persistBq.getSupplierBqItems()) {
						LOG.info("Total Amount With Tax : " + child.getTotalAmountWithTax());
						// To check wheteher Price type is Trade InPrice
						if (PricingTypes.TRADE_IN_PRICE == child.getPriceType()) {
							sum = sum.subtract(child.getTotalAmountWithTax() != null ? child.getTotalAmountWithTax() : BigDecimal.ZERO);
						} else {
							sum = sum.add(child.getTotalAmountWithTax() != null ? child.getTotalAmountWithTax() : BigDecimal.ZERO);
						}
					}
				}
				persistBq.setGrandTotal(sum != null ? sum.setScale(round, RoundingMode.HALF_UP) : BigDecimal.ZERO);
				persistBq.setTotalAfterTax(persistBq.getGrandTotal().add(persistBq.getAdditionalTax() != null ? persistBq.getAdditionalTax() : BigDecimal.ZERO).setScale(round, RoundingMode.HALF_UP).setScale(round, RoundingMode.HALF_UP));
				if(SupplierBqStatus.COMPLETED != persistBq.getSupplierBqStatus()) {
					persistBq.setSupplierBqStatus(SupplierBqStatus.DRAFT);
				}
				rftSupplierBqDao.update(persistBq);
				LOG.info("persistBq  After : " + persistBq.toString());
			}

			return item;
		}
		default:
			break;
		}
		return null;
	}

	@Override
	public BqItem getSupplierBqItem(String bqItemId, String supplierId, RfxTypes type) {

		switch (type) {
		case RFA:
			return rfaSupplierBqItemDao.getBqItemByBqItemId(bqItemId, supplierId);
		case RFI:
			break;
		case RFP:
			return rfpSupplierBqItemDao.getBqItemByBqItemId(bqItemId, supplierId);
		case RFQ:
			return rfqSupplierBqItemDao.getBqItemByBqItemId(bqItemId, supplierId);
		case RFT:
			return rftSupplierBqItemDao.getBqItemByBqItemId(bqItemId, supplierId);
		default:
			break;
		}
		return null;
	}

}
