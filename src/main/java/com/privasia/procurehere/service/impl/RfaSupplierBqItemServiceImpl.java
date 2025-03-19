package com.privasia.procurehere.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.privasia.procurehere.core.entity.*;
import com.privasia.procurehere.core.enums.*;
import com.privasia.procurehere.core.utils.*;
import com.privasia.procurehere.service.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.privasia.procurehere.core.dao.AuctionBidsDao;
import com.privasia.procurehere.core.dao.AuctionRulesDao;
import com.privasia.procurehere.core.dao.RfaBqDao;
import com.privasia.procurehere.core.dao.RfaBqEvaluationCommentsDao;
import com.privasia.procurehere.core.dao.RfaBqItemDao;
import com.privasia.procurehere.core.dao.RfaEnvelopDao;
import com.privasia.procurehere.core.dao.RfaEventDao;
import com.privasia.procurehere.core.dao.RfaEventSupplierDao;
import com.privasia.procurehere.core.dao.RfaSupplierBqDao;
import com.privasia.procurehere.core.dao.RfaSupplierBqItemDao;
import com.privasia.procurehere.core.pojo.BqItemPojo;
import com.privasia.procurehere.core.pojo.BqNonPriceComprision;
import com.privasia.procurehere.core.pojo.EvaluationTotalAmountPojo;
import com.privasia.procurehere.core.pojo.EventEvaluationPojo;
import com.privasia.procurehere.core.supplier.dao.SupplierDao;

/**
 * @author Vipul
 * @author Ravi
 */

@Service
@Transactional(readOnly = true)
public class RfaSupplierBqItemServiceImpl implements RfaSupplierBqItemService {

	private static final Logger LOG = LogManager.getLogger(Global.AUCTION_LOG);

	@Autowired
	RfaSupplierBqItemDao rfaSupplierBqItemDao;

	@Autowired
	RfaSupplierBqDao supplierBqDao;

	@Autowired
	RfaEventDao eventDao;

	@Autowired
	RfaBqItemDao bqItemDao;

	@Autowired
	RfaEnvelopDao envelopDao;

	@Autowired
	RfaSupplierBqService supplierBqService;

	@Autowired
	RfaEventSupplierDao rfaEventSupplierDao;

	@Autowired
	SupplierDao supplierDao;

	@Autowired
	AuctionRulesDao auctionRulesDao;

	@Autowired
	RfaBqDao bqDao;

	@Autowired
	AuctionBidsDao auctionBidsDao;

	@Autowired
	RfaEventDao rfaEventDao;

	@Autowired
	EventAuditService eventAuditService;

	@Autowired
	RfaBqEvaluationCommentsDao rfaBqEvaluationCommentsDao;

	@Autowired
	RfaEventService rfaEventService;


	@Autowired
	RfaBqService rfaBqService;


	@Autowired
	RfaAwardService eventAwardService;

	@Override
	public List<RfaSupplierBqItem> getAllSupplierBqItemByBqIdAndSupplierId(String bqId, String supplierId) {
		List<RfaSupplierBqItem> returnList = new ArrayList<RfaSupplierBqItem>();
		List<RfaSupplierBqItem> list = rfaSupplierBqItemDao.findSupplierBqItemListByBqIdAndSupplierId(bqId, supplierId);
		bulidSupplierBqItemList(returnList, list);
		LOG.info("Service ReturnList : " + returnList.size());
		return returnList;
	}

	private void bulidSupplierBqItemList(List<RfaSupplierBqItem> returnList, List<RfaSupplierBqItem> list) {
		if (CollectionUtil.isNotEmpty(list)) {
			for (RfaSupplierBqItem item : list) {
				RfaSupplierBqItem parent = item.createShallowCopy();
				parent.getSupplierBq().setAuditSupplierBqItems(null);
				returnList.add(parent);
				if (CollectionUtil.isNotEmpty(item.getChildren())) {
					for (RfaSupplierBqItem child : item.getChildren()) {
						if (parent.getChildren() == null) {
							parent.setChildren(new ArrayList<RfaSupplierBqItem>());
						}
						RfaSupplierBqItem copy = child.createShallowCopy();
						copy.getSupplierBq().setAuditSupplierBqItems(null);
						parent.getChildren().add(copy);
					}
				}
			}
		}
	}

	@Override
	@Transactional(readOnly = false)
	public List<RfaSupplierBqItem> saveSupplierEventBq(String bqId) {
		LOG.info(" saveSupplierEventBq**************************");
		List<RfaSupplierBqItem> rfaSupplierBqItem = new ArrayList<RfaSupplierBqItem>();
		BigDecimal sum = BigDecimal.ZERO;
		List<RfaBqItem> rfaEventBq = rfaSupplierBqItemDao.getBqItemsbyId(bqId);
		for (RfaBqItem item : rfaEventBq) {
			LOG.info("ITEM : " + item.getItemName() + " Child : " + item.getChildren().size());
			RfaEventBq bq = item.getBq();
			RfaSupplierBq supplierBq = supplierBqDao.findBqByEventIdAndSupplierId(bq.getRfxEvent().getId(), bq.getId(), SecurityLibrary.getLoggedInUserTenantId());
			if (supplierBq == null) {
				supplierBq = new RfaSupplierBq(item.getBq());
				RfaEvent rfxEvent = eventDao.findByEventId(bq.getRfxEvent().getId());
				supplierBq.setEvent(rfxEvent);
				supplierBq.setSupplier(SecurityLibrary.getLoggedInUser().getSupplier());
				supplierBq = supplierBqDao.saveOrUpdate(supplierBq);
				LOG.info("supplierBq after insert : " + supplierBq.toLogString());
			}
			RfaSupplierBqItem supplierBqItem = new RfaSupplierBqItem(item, SecurityLibrary.getLoggedInUser().getSupplier(), supplierBq);
			RfaSupplierBqItem obj = rfaSupplierBqItemDao.saveOrUpdate(supplierBqItem);
			if (CollectionUtil.isNotEmpty(supplierBqItem.getChildren())) {
				for (RfaSupplierBqItem child : supplierBqItem.getChildren()) {
					child.setParent(obj);
					if (child.getUnitPrice() != null) {
						child.setTotalAmount(child.getUnitPrice().multiply(child.getQuantity()));
					}
					child.setTotalAmountWithTax(child.getTotalAmount());
					BigDecimal totalAmountWithTax = child.getTotalAmountWithTax();
					LOG.info("  totalAmountWithTax   " + totalAmountWithTax);
					if (totalAmountWithTax != null) {
						// To check wheteher Price type is Trade InPrice
						if (PricingTypes.TRADE_IN_PRICE == child.getPriceType()) {
							sum = sum.subtract(totalAmountWithTax);
						} else {
							sum = sum.add(totalAmountWithTax);
						}

					}
					supplierBq.setGrandTotal(sum);
					rfaSupplierBqItemDao.saveOrUpdate(child);
				}
			}
			LOG.info("After set the SUP BQ :: " + supplierBqItem.toString());
			rfaSupplierBqItem.add(supplierBqItem.createShallowCopy());
		}
		return rfaSupplierBqItem;
	}

	@Override
	public List<RfaBqItem> getBqItemsbyId(String bqId) {
		return rfaSupplierBqItemDao.getBqItemsbyId(bqId);
	}

	@Override
	public RfaSupplierBqItem getSupplierBqItemByBqItemAndSupplierId(String itemId, String supplierId) {
		return rfaSupplierBqItemDao.getBqItemByBqItemId(itemId, supplierId);
	}

	@Override
	@Transactional(readOnly = false)
	public RfaSupplierBqItem updateSupplierBqItems(RfaSupplierBqItem persistObject) {
		return rfaSupplierBqItemDao.update(persistObject);
	}

	@Override
	@Transactional(readOnly = false)
	public void updateBqItems(List<SupplierBqItem> items, String supplierId) {
		LOG.info("updateBqItems*******************************");
		RfaSupplierBq persistBq = null;
		BigDecimal sum = BigDecimal.ZERO;
		BigDecimal additionalTax = BigDecimal.ZERO;
		String taxDesc = "";
		String bqId = "";
		String remarks = "";

		for (SupplierBqItem supplierBqItem : items) {

			if (additionalTax == BigDecimal.ZERO) {
				additionalTax = supplierBqItem.getAdditionalTax();
			}
			if (StringUtils.checkString(taxDesc).length() == 0) {
				taxDesc = supplierBqItem.getTaxDescription();
			}
			if (StringUtils.checkString(bqId).length() == 0) {
				bqId = supplierBqItem.getBqId();
			}

			if (StringUtils.checkString(remarks).length() == 0) {
				remarks = supplierBqItem.getRemarks();
			}

			RfaSupplierBqItem persistObject = getSupplierBqItemByBqItemAndSupplierId(supplierBqItem.getId(), SecurityLibrary.getLoggedInUserTenantId());
			if (persistObject != null) {
				persistObject.setField1(supplierBqItem.getField1());
				persistObject.setField2(supplierBqItem.getField2());
				persistObject.setField3(supplierBqItem.getField3());
				persistObject.setField4(supplierBqItem.getField4());
				persistObject.setField5(supplierBqItem.getField5());
				persistObject.setField6(supplierBqItem.getField6());
				persistObject.setField7(supplierBqItem.getField7());
				persistObject.setField8(supplierBqItem.getField8());
				persistObject.setField9(supplierBqItem.getField9());
				persistObject.setField10(supplierBqItem.getField10());
				updateSupplierBqItems(persistObject);
			}
		}

		List<RfaSupplierBqItem> itemList = getAllSupplierBqItemListByBqIdAndSupplierId(bqId, supplierId);
		for (RfaSupplierBqItem supplierBqItem : itemList) {
			LOG.info("PriceType>>>" + supplierBqItem.getPriceType());
			BigDecimal totalAmountWithTax = supplierBqItem.getTotalAmountWithTax();
			if (totalAmountWithTax != null) {
				// PricingTypes t = rfaSupplierBqItemDao.getPriceTypeByBqItemId(supplierBqItem.getId());
				// To check wheteher Price type is Trade InPrice
				if (PricingTypes.TRADE_IN_PRICE == supplierBqItem.getPriceType()) {
					sum = sum.subtract(totalAmountWithTax);
				} else {
					sum = sum.add(totalAmountWithTax);
				}
				LOG.info("sum>>>" + sum);
			}
		}

		// RfaSupplierBqItem persistObject = getSupplierBqItemByBqItemAndSupplierId(supplierBqItem.getId(),
		// SecurityLibrary.getLoggedInUserTenantId());
		// if (persistObject != null) {
		// if (event == null) {
		// event = persistObject.getEvent();
		// }
		// persistObject.setUnitPrice(supplierBqItem.getUnitPrice());
		// persistObject.setTotalAmount(supplierBqItem.getTotalAmount());
		// persistObject.setTax(supplierBqItem.getTax());
		// persistObject.setTaxType(supplierBqItem.getTaxType());
		// persistObject.setTotalAmountWithTax(supplierBqItem.getTotalAmountWithTax());
		// persistObject.setTaxDescription(supplierBqItem.getTaxDescription());
		// persistObject.setField1(supplierBqItem.getField1());
		// persistObject.setField2(supplierBqItem.getField2());
		// persistObject.setField3(supplierBqItem.getField3());
		// persistObject.setField4(supplierBqItem.getField4());
		// persistObject.setField5(supplierBqItem.getField5());
		// persistObject.setField6(supplierBqItem.getField6());
		// persistObject.setField7(supplierBqItem.getField7());
		// persistObject.setField8(supplierBqItem.getField8());
		// persistObject.setField9(supplierBqItem.getField9());
		// persistObject.setField10(supplierBqItem.getField10());
		// persistObject.getEventId();
		// RfaSupplierBqItem updatedBqItem = updateSupplierBqItems(persistObject);
		// if (StringUtils.checkString(supplierId).length() == 0) {
		// supplierId = persistObject.getSupplier().getId();
		// }
		// LOG.info(" saveSupplierBQDetails updatedBqItem : :" + updatedBqItem);
		// }
		// }

		persistBq = supplierBqService.getSupplierBqByBqAndSupplierId(bqId, supplierId);
		if (persistBq != null)

		{
			persistBq.setAdditionalTax(additionalTax);
			persistBq.setTaxDescription(taxDesc);
			persistBq.setGrandTotal(sum);

			if (StringUtils.checkString(remarks).length() > 0) {
				remarks = remarks.replaceAll("-", "_");
			}

			persistBq.setRemark(remarks);
			LOG.info("Remark" + persistBq.getRemark());
			persistBq.setTotalAfterTax(persistBq.getGrandTotal().add(persistBq.getAdditionalTax() != null ? persistBq.getAdditionalTax() : BigDecimal.ZERO));
			supplierBqService.updateSupplierBq(persistBq);

			RfaEventSupplier eventSupplier = rfaEventSupplierDao.findEventSupplierByEventIdAndSupplierIgnoreSubmit(persistBq.getEvent().getId(), supplierId);
			if (Boolean.TRUE == persistBq.getBuyerSubmited() && (null == eventSupplier.getConfirmPriceSubmitted() || Boolean.FALSE == eventSupplier.getConfirmPriceSubmitted())) {
				rfaEventSupplierDao.updateEventSupplierConfirm(persistBq.getEvent().getId(), supplierId);

				// AuctionBids auctionBids = new AuctionBids();
				// auctionBids.setAmount(persistBq.getTotalAfterTax());
				// auctionBids.setEvent(event);
				// auctionBids.setBidSubmissionDate(new Date());
				// auctionBids.setBidBySupplier(eventSupplier.getSupplier());
				// try {
				// ObjectMapper mapper = new ObjectMapper();
				// String json = mapper.writeValueAsString(rfaSupplierBq);
				// auctionBids.setDetails(json);
				// rfaSupplierBq.setAuditSupplierBqItems(null);
				// LOG.info("BID JSON : " + json);
				// RfaSupplierBq bq = mapper.readValue(json, RfaSupplierBq.class);
				// LOG.info("Deserialized Object : " + bq.toLogString());
				// } catch (Exception e) {
				// LOG.error("Error while Converting Supplier bid to json , " + e.getMessage(), e);
				// }
				// auctionBidsDao.save(auctionBids);
			}
		}

	}

	private List<RfaSupplierBqItem> getAllSupplierBqItemListByBqIdAndSupplierId(String bqId, String supplierId) {
		List<RfaSupplierBqItem> list = rfaSupplierBqItemDao.findBqItemListAndBqListByBqIdAndSupplierId(bqId, supplierId);
		return list;
	}

	@Override
	@Transactional(readOnly = false)
	public RfaSupplierBqItem updateSupplierBqItem(RfaSupplierBqItem persistObject) {
		return rfaSupplierBqItemDao.update(persistObject);
	}

	@Override
	public List<EventEvaluationPojo> getBqEvaluationData(String eventId, String envelopId, User logedUser, String withOrWithoutTax, List<Supplier> selectedSuppliers, Integer itemLevel, Integer itemOrder, String searchVal, Integer start, Integer length) {
		List<EventEvaluationPojo> returnList = new ArrayList<>();
		List<Supplier> suppliers = null;
		if (CollectionUtil.isEmpty(selectedSuppliers)) {
			suppliers = rfaEventSupplierDao.getEventSuppliersForEvaluation(eventId);
		} else {
			suppliers = rfaEventSupplierDao.getEventSuppliersForEvaluation(eventId, selectedSuppliers);
		}

		if (CollectionUtil.isNotEmpty(suppliers)) {
			RfaEvent event = eventDao.findById(eventId);

			final int dec = (event.getDecimal() != null && !event.getDecimal().isEmpty() ? Integer.parseInt(event.getDecimal()) : 2);
			DecimalFormat formatter = null;
			switch (dec) {
			case 1:
				formatter = new DecimalFormat("#,###,###,##0.0");
				break;
			case 2:
				formatter = new DecimalFormat("#,###,###,##0.00");
				break;
			case 3:
				formatter = new DecimalFormat("#,###,###,##0.000");
				break;
			case 4:
				formatter = new DecimalFormat("#,###,###,##0.0000");
				break;
			case 5:
				formatter = new DecimalFormat("#,###,###,##0.00000");
				break;
			case 6:
				formatter = new DecimalFormat("#,###,###,##0.000000");
				break;
			default:
				formatter = new DecimalFormat("#,###,###,##0.00");
				break;
			}
			List<String> columns = new ArrayList<String>();
			for (Supplier supplier : suppliers) {
				columns.add(supplier.getCompanyName());
			}
			List<String> envelopIds = new ArrayList<String>();
			envelopIds.add(envelopId);
			List<String> bqIds = envelopDao.getBqsByEnvelopId(envelopIds);
			RfaEnvelop envelop = envelopDao.findById(envelopId);

			if (envelop.getRfxEvent() != null) {
				if (envelop.getRfxEvent().getViewSupplerName() != null && !envelop.getRfxEvent().getViewSupplerName() && !envelop.getRfxEvent().getDisableMasking()) {
					for (Supplier supplier : suppliers) {
						supplier.setCompanyName(MaskUtils.maskName(envelop.getPreFix(), supplier.getId(), envelop.getId()));
					}
					/*
					 * Collections.sort(suppliers, new Comparator<Supplier>() { public int compare(Supplier o1, Supplier
					 * o2) { if (o1.getCompanyName() == null || o2.getCompanyName() == null) { return 0; } return
					 * o1.getCompanyName().compareTo(o2.getCompanyName()); } });
					 */
				}

			}

			AuctionRules rules = auctionRulesDao.findAuctionRulesByEventId(eventId);
			Boolean withTax = Boolean.FALSE;
			if (rules != null && ((null != rules.getItemizedBiddingWithTax() && Boolean.TRUE == rules.getItemizedBiddingWithTax()) || (null != rules.getLumsumBiddingWithTax()) && Boolean.TRUE == rules.getLumsumBiddingWithTax())) {
				withTax = Boolean.TRUE;
			}
			int bqItemlistSize = 0;
			List<String> levelOrderList = new ArrayList<>();
			for (String bqId : bqIds) {
				EventEvaluationPojo response = new EventEvaluationPojo();
				response.setWithTax(withTax);
				LinkedList<List<String>> data = new LinkedList<List<String>>();
				// List<RfaBqItem> bqItems = bqItemDao.findBqItemsForBq(bqId);
				List<RfaBqItem> bqItems = bqItemDao.getBqItemForSearchFilter(bqId, itemLevel, itemOrder, searchVal, start, length);

				List<RfaBqItem> bqItemlevelOrders = bqItemDao.getBqItemForSearchFilter(bqId, null, null, null, null, null);
				LOG.info("bqItemlistSize :" + bqItemlistSize);
				// If Bq Item is more then previous then setting building new list of section number level order
				if (CollectionUtil.isNotEmpty(bqItemlevelOrders) && bqItemlevelOrders.size() > bqItemlistSize) {
					levelOrderList = new ArrayList<>();
				}
				for (RfaBqItem bqItemLevelOrder : bqItemlevelOrders) {
					// building section number level order
					if (CollectionUtil.isNotEmpty(bqItemlevelOrders) && bqItemlevelOrders.size() > bqItemlistSize) {
						levelOrderList.add(bqItemLevelOrder.getLevel() + "." + bqItemLevelOrder.getOrder());
					}
				}
				// To check If next BQ item is greater then previous
				if (CollectionUtil.isNotEmpty(bqItemlevelOrders) && bqItemlevelOrders.size() > bqItemlistSize) {
					bqItemlistSize = bqItemlevelOrders.size();
				}
				Integer j = 1;
				Integer k = 0;
				Map<String, BigDecimal> map = new LinkedHashMap<String, BigDecimal>();
				for (RfaBqItem bqItem : bqItems) {
					boolean commentsExist = false;
					List<String> answers = new ArrayList<String>();
					List<String> sectionTotalRow = new ArrayList<String>();
					sectionTotalRow.add("");
					sectionTotalRow.add("");
					sectionTotalRow.add("");
					sectionTotalRow.add("Section Total");
					sectionTotalRow.add("");
					response.setItemId(bqItem.getId());
					response.setName(bqItem.getBq().getName());
					answers.add(bqItem.getLevel() + "." + bqItem.getOrder());
					answers.add(bqItem.getItemName());
					answers.add(bqItem.getPriceType() != null ? bqItem.getPriceType().getValue() : "");
					answers.add(bqItem.getUom() != null ? bqItem.getUom().getUom() : "");
					answers.add(bqItem.getQuantity() != null ? formatter.format(bqItem.getQuantity()) : "");
					List<RfaSupplierBqItem> responseList = rfaSupplierBqItemDao.findSupplierBqItemsByBqItemIdAndEventId(bqItem.getId(), eventId, suppliers);
					LOG.info("responseList : " + responseList.size());
					for (RfaSupplierBqItem item : responseList) {
						BigDecimal sectionSum = BigDecimal.ZERO;
						response.setSupplierId(item.getSupplier().getId());
						if (item.getBqItem().getEvaluationComments() != null && item.getBqItem().getEvaluationComments().size() > 0) {
							for (RfaBqEvaluationComments com : item.getBqItem().getEvaluationComments()) {
								if (((com.getCreatedBy().getId().equals(logedUser.getId()) || (envelop.getLeadEvaluater().getId().equals(logedUser.getId()))) && com.getSupplier().getId().equals(item.getSupplier().getId()))) {
									commentsExist = true;
									LOG.info("Comments : " + item.getBqItem().getEvaluationComments().size());
									continue;
								}
							}
						}

						if (StringUtils.checkString(withOrWithoutTax).equals("1")) {
							if (item.getTotalAmount() != null) {
								BigDecimal amount = new BigDecimal(String.valueOf(item.getTotalAmount())).setScale((event.getDecimal() != null && !event.getDecimal().isEmpty() ? Integer.parseInt(event.getDecimal()) : 2), BigDecimal.ROUND_DOWN);
								sectionSum = BigDecimal.ZERO;
								if (map.containsKey(item.getSupplier().getId() + "-" + item.getLevel())) {
									sectionSum = map.get(item.getSupplier().getId() + "-" + item.getLevel()).add(amount);
								} else {
									sectionSum = amount;
								}
								map.put(item.getSupplier().getId() + "-" + item.getLevel(), sectionSum);
								answers.add(commentsExist + "-" + item.getSupplier().getId() + "-" + item.getBqItem().getId() + "-" + String.valueOf(amount));
							} else {
								answers.add("");
							}
						} else {
							if (item.getTotalAmountWithTax() != null) {
								LOG.info("item.getTotalAmountWithTax() : " + item.getTotalAmountWithTax());
								BigDecimal amount = new BigDecimal(String.valueOf(item.getTotalAmountWithTax())).setScale((event.getDecimal() != null && !event.getDecimal().isEmpty() ? Integer.parseInt(event.getDecimal()) : 2), BigDecimal.ROUND_DOWN);
								sectionSum = BigDecimal.ZERO;
								if (map.containsKey(item.getSupplier().getId() + "-" + item.getLevel())) {
									sectionSum = map.get(item.getSupplier().getId() + "-" + item.getLevel()).add(amount);
								} else {
									sectionSum = amount;
								}
								map.put(item.getSupplier().getId() + "-" + item.getLevel(), sectionSum);
								answers.add(commentsExist + "-" + item.getSupplier().getId() + "-" + item.getBqItem().getId() + "-" + String.valueOf(amount));
							} else {
								answers.add("");
							}
						}
					}
					if (j != bqItem.getLevel()) {
						for (Map.Entry<String, BigDecimal> entry : map.entrySet()) {
							String[] keyset = entry.getKey().split("-");
							String currantLvl = "" + (bqItem.getLevel() - 1);
							if (keyset[1].equals(currantLvl)) {
								String id = bqItemDao.getBqSectionIdByBqLevelAndOrder((bqItem.getLevel() - 1), 0, eventId);
								List<RfaBqEvaluationComments> cmts = rfaBqEvaluationCommentsDao.getCommentsByBqIdAndEventId(id, eventId, logedUser);
								if (CollectionUtil.isNotEmpty(cmts)) {
									sectionTotalRow.add(true + "-" + keyset[0] + "-" + id + "-" + String.valueOf(entry.getValue()));
								} else {
									sectionTotalRow.add(false + "-" + keyset[0] + "-" + id + "-" + String.valueOf(entry.getValue()));
								}

							}
						}
						data.add(sectionTotalRow);
					}

					data.add(answers);

					k++;
					if (k == bqItems.size()) {
						if (bqItem.getLevel() != 1) {
							for (Map.Entry<String, BigDecimal> entry : map.entrySet()) {
								String[] keyset = entry.getKey().split("-");
								if (keyset[1].equals(bqItem.getLevel().toString())) {
									String id = bqItemDao.getBqSectionIdByBqLevelAndOrder(bqItem.getLevel(), 0, eventId);
									List<RfaBqEvaluationComments> cmts = rfaBqEvaluationCommentsDao.getCommentsByBqIdAndEventId(id, eventId, logedUser);
									if (CollectionUtil.isNotEmpty(cmts)) {
										sectionTotalRow.add(true + "-" + keyset[0] + "-" + id + "-" + String.valueOf(entry.getValue()));
									} else {
										sectionTotalRow.add(false + "-" + keyset[0] + "-" + id + "-" + String.valueOf(entry.getValue()));
									}
								}
							}
							data.add(sectionTotalRow);
						}
					}

					j = bqItem.getLevel();
				}
				response.setData(data);
				response.setColumns(suppliers);
				/*
				 * List<String> totalAmounts = rfaSupplierBqItemDao.findSumOfTotalAmountWithTaxForSupplier(bqId,
				 * eventId, suppliers);
				 */
				List<String> totalAmounts = rfaSupplierBqItemDao.findGrandTotalsForEvaluationView(bqId, eventId, suppliers);
				List<String> supplierRemarks = new ArrayList<String>();
				List<EvaluationTotalAmountPojo> totalAmountList = new ArrayList<EvaluationTotalAmountPojo>();
				for (String total : totalAmounts) {
					if (StringUtils.checkString(total).length() > 0) {
						EvaluationTotalAmountPojo pojo = new EvaluationTotalAmountPojo();
						LOG.info("TOTAL : " + total);
						String values[] = total.split("-");
						String supplierId = values[4];
						BigDecimal value = new BigDecimal(values[0]).setScale((event.getDecimal() != null && !event.getDecimal().isEmpty() ? Integer.parseInt(event.getDecimal()) : 2), BigDecimal.ROUND_DOWN);
						BigDecimal value2 = new BigDecimal(values[1]).setScale((event.getDecimal() != null && !event.getDecimal().isEmpty() ? Integer.parseInt(event.getDecimal()) : 2), BigDecimal.ROUND_DOWN);
						pojo.setTotalAmount(value);
						pojo.setBqId(values[2]);
						pojo.setSupplierId(supplierId);
						pojo.setSubtotal(value2);
						supplierRemarks.add(values.length > 5 ? StringUtils.checkString(values[5]) : "");
						RfaEventBq bq = bqDao.findById(bqId);
						boolean commentsExist = false;
						if (bq != null && CollectionUtil.isNotEmpty(bq.getEvaluationComments())) {
							for (RfaBqTotalEvaluationComments com : bq.getEvaluationComments()) {
								if (((com.getCreatedBy().getId().equals(logedUser.getId()) || (envelop.getLeadEvaluater().getId().equals(logedUser.getId()))) && com.getSupplier().getId().equals(supplierId))) {
									commentsExist = true;
									continue;
								}
							}
						}
						pojo.setCommentExisit(commentsExist);
						totalAmountList.add(pojo);
					}
				}
				if (CollectionUtil.isNotEmpty(totalAmountList)) {
					response.setTotalAmountList(totalAmountList);
				}
				response.setSupplierRemarks(supplierRemarks);
				totalAmounts = rfaSupplierBqItemDao.findGrandTotalsForView(bqId, eventId, suppliers);
				List<EvaluationTotalAmountPojo> gTotalAmounts = new ArrayList<EvaluationTotalAmountPojo>();
				for (String total : totalAmounts) {
					if (StringUtils.checkString(total).length() > 0) {
						EvaluationTotalAmountPojo pojo = new EvaluationTotalAmountPojo();
						LOG.info("GRAND TOTAL : " + total);
						String values[] = total.split("-");
						String supplierId = values[2];
						BigDecimal value = new BigDecimal(values[0]).setScale((event.getDecimal() != null && !event.getDecimal().isEmpty() ? Integer.parseInt(event.getDecimal()) : 2), BigDecimal.ROUND_DOWN);
						pojo.setTotalAmount(value);
						pojo.setBqId(values[1]);
						pojo.setSupplierId(supplierId);
						RfaEventBq bq = bqDao.findById(bqId);
						boolean commentsExist = false;
						if (bq != null && CollectionUtil.isNotEmpty(bq.getEvaluationComments())) {
							for (RfaBqTotalEvaluationComments com : bq.getEvaluationComments()) {
								if (((com.getCreatedBy().getId().equals(logedUser.getId()) || (envelop.getLeadEvaluater().getId().equals(logedUser.getId()))) && com.getSupplier().getId().equals(supplierId))) {
									commentsExist = true;
									continue;
								}
							}
						}
						pojo.setCommentExisit(commentsExist);
						gTotalAmounts.add(pojo);
					}
				}

				response.setTotalAmounts(gTotalAmounts);
				response.setAddtionalTaxInfo(rfaSupplierBqItemDao.findAddtionalTaxForView(bqId, eventId, suppliers));

				response.setEnvelopId(envelopId);
				response.setEventId(eventId);
				response.setLevelOrderList(levelOrderList);
				returnList.add(response);
			}
			if (CollectionUtil.isNotEmpty(levelOrderList)) {
				LOG.info("levelOrderList :" + levelOrderList.toString());
			}
		}
		return returnList;
	}

	@Override
	public List<EventEvaluationPojo> getEvaluationDataForBqComparison(String eventId, String envelopId) {
		List<EventEvaluationPojo> returnList = new ArrayList<>();
		List<Supplier> qualifiedSup = rfaEventSupplierDao.getEventQualifiedSuppliersForEvaluation(eventId);

		RfaEvent event = eventDao.findById(eventId);
		if (CollectionUtil.isNotEmpty(qualifiedSup)) {

			List<String> envelopIds = new ArrayList<String>();
			envelopIds.add(envelopId);
			List<String> bqIds = envelopDao.getBqsByEnvelopId(envelopIds);

			AuctionRules rules = auctionRulesDao.findAuctionRulesByEventId(eventId);
			Boolean withTax = Boolean.FALSE;
			if (rules != null && ((null != rules.getItemizedBiddingWithTax() && Boolean.TRUE == rules.getItemizedBiddingWithTax()) || (null != rules.getLumsumBiddingWithTax()) && Boolean.TRUE == rules.getLumsumBiddingWithTax())) {
				withTax = Boolean.TRUE;
			}
			for (String bqId : bqIds) {
				EventEvaluationPojo response = new EventEvaluationPojo();
				response.setWithTax(withTax);
				List<List<String>> data = new ArrayList<List<String>>();
				List<RfaBqItem> bqItems = bqItemDao.findBqItemsForBq(bqId);
				for (RfaBqItem bqItem : bqItems) {
					List<String> answers = new ArrayList<String>();
					response.setItemId(bqItem.getId());
					response.setName(bqItem.getBq().getName());
					answers.add(bqItem.getLevel() + "." + bqItem.getOrder());
					answers.add(bqItem.getItemName());
					answers.add(bqItem.getItemDescription());
					answers.add(bqItem.getUom() != null ? bqItem.getUom().getUom() : "");
					answers.add(bqItem.getQuantity() != null ? String.valueOf(bqItem.getQuantity()) : "");
					List<RfaSupplierBqItem> responseList = rfaSupplierBqItemDao.findSupplierBqItemsByBqItemIdAndEventId(bqItem.getId(), eventId, qualifiedSup);
					for (RfaSupplierBqItem item : responseList) {
						response.setSupplierId(item.getSupplier().getId());
						if (item.getTotalAmountWithTax() != null) {
							BigDecimal amount = item.getTotalAmount() != null ? new BigDecimal(String.valueOf(item.getTotalAmount())).setScale((event.getDecimal() != null && !event.getDecimal().isEmpty() ? Integer.parseInt(event.getDecimal()) : 2), BigDecimal.ROUND_DOWN) : new BigDecimal(0);
							BigDecimal unitPrice = item.getUnitPrice() != null ? new BigDecimal(String.valueOf(item.getUnitPrice())).setScale((event.getDecimal() != null && !event.getDecimal().isEmpty() ? Integer.parseInt(event.getDecimal()) : 2), BigDecimal.ROUND_DOWN) : new BigDecimal(0);
							answers.add(String.valueOf(unitPrice));
							answers.add(String.valueOf(amount));
							if (Boolean.TRUE == withTax) {
								switch (item.getTaxType()) {
								case Percent: {
									BigDecimal taxAmount = amount.multiply(item.getTax() != null ? item.getTax() : BigDecimal.ZERO).divide(new BigDecimal(100));
									taxAmount.setScale((event.getDecimal() != null && !event.getDecimal().isEmpty() ? Integer.parseInt(event.getDecimal()) : 2), BigDecimal.ROUND_DOWN);
									answers.add(String.valueOf(taxAmount));
									break;
								}
								case Amount: {
									BigDecimal taxAmount = item.getTax() != null ? new BigDecimal(String.valueOf(item.getTax())).setScale((event.getDecimal() != null && !event.getDecimal().isEmpty() ? Integer.parseInt(event.getDecimal()) : 2), BigDecimal.ROUND_DOWN) : new BigDecimal(0);
									answers.add(String.valueOf(taxAmount));
									break;
								}
								default:
									break;
								}
								BigDecimal amountWithTax = item.getTotalAmountWithTax() != null ? new BigDecimal(String.valueOf(item.getTotalAmountWithTax())).setScale((event.getDecimal() != null && !event.getDecimal().isEmpty() ? Integer.parseInt(event.getDecimal()) : 2), BigDecimal.ROUND_DOWN) : new BigDecimal(0);
								answers.add(String.valueOf(amountWithTax));
							}
						}
					}
					data.add(answers);
				}
				response.setData(data);
				response.setColumns(qualifiedSup);
				List<String> totalAmounts = rfaSupplierBqItemDao.findSumOfTotalAmountForSupplier(bqId, eventId, qualifiedSup, withTax);
				List<BigDecimal> amounts = new ArrayList<BigDecimal>();
				for (String total : totalAmounts) {
					BigDecimal value = null;
					if (StringUtils.checkString(total).length() > 0)
						value = new BigDecimal(total);
					amounts.add(value);

				}

				List<String> gtotal = rfaSupplierBqItemDao.findGrandTotals(bqId, eventId, qualifiedSup, withTax);
				List<BigDecimal> grandAmounts = new ArrayList<BigDecimal>();
				List<EvaluationTotalAmountPojo> gTotalAmounts = new ArrayList<EvaluationTotalAmountPojo>();

				for (String total : gtotal) {
					BigDecimal value = null;
					if (StringUtils.checkString(total).length() > 0) {
						EvaluationTotalAmountPojo pojo = new EvaluationTotalAmountPojo();
						value = new BigDecimal(total);
						pojo.setTotalAmount(value);
					}
					grandAmounts.add(value);
				}

				response.setGrandTotals(grandAmounts);
				response.setAddtionalTaxInfo(rfaSupplierBqItemDao.findAddtionalTax(bqId, eventId, qualifiedSup));
				response.setTotalAmounts(gTotalAmounts);
				response.setEnvelopId(envelopId);
				response.setEventId(eventId);
				response.setDecimal(event.getDecimal());
				returnList.add(response);
			}
		}
		return returnList;
	}

	@Override
	public RfaSupplierBqItem getBqItemByBqItemId(String itemId, String supplierId) {
		return rfaSupplierBqItemDao.getBqItemByBqItemId(itemId, supplierId);
	}

	@Override
	public List<RfaSupplierBqItem> findAllSupplierBqItemListByBqId(String bqId) {
		List<RfaSupplierBqItem> returnList = new ArrayList<RfaSupplierBqItem>();
		List<RfaSupplierBqItem> list = rfaSupplierBqItemDao.findAllSupplierBqItemListByBqId(bqId);
		bulidSupplierBqItemList(returnList, list);
		LOG.info("Service ReturnList : " + returnList.size());
		return returnList;
	}

	@Override
	@Transactional(readOnly = false)
	public List<RfaSupplierBqItem> saveSupplierEventBqByBuyer(String bqId, String supplierId) {
		LOG.info("saveSupplierEventBqByBuyer***********************");
		List<RfaSupplierBqItem> rfaSupplierBqItem = new ArrayList<RfaSupplierBqItem>();

		Supplier supplier = supplierDao.findSuppById(supplierId);
		BigDecimal sum = BigDecimal.ZERO;
		List<RfaBqItem> rfaEventBq = rfaSupplierBqItemDao.getBqItemsbyId(bqId);
		for (RfaBqItem item : rfaEventBq) {
			LOG.info("ITEM : " + item.getItemName() + " Child : " + item.getChildren().size());
			RfaEventBq bq = item.getBq();
			RfaSupplierBq supplierBq = supplierBqDao.findBqByEventIdAndSupplierId(bq.getRfxEvent().getId(), bq.getId(), supplierId);
			if (supplierBq == null) {
				supplierBq = new RfaSupplierBq(item.getBq());
				RfaEvent rfxEvent = eventDao.findByEventId(bq.getRfxEvent().getId());
				supplierBq.setEvent(rfxEvent);
				supplierBq.setSupplier(supplier);
				supplierBq.setBuyerSubmited(Boolean.TRUE);
				supplierBq = supplierBqDao.saveOrUpdate(supplierBq);
				LOG.info("supplierBq after insert : " + supplierBq.toLogString());
			}
			RfaSupplierBqItem supplierBqItem = new RfaSupplierBqItem(item, supplier, supplierBq);
			RfaSupplierBqItem obj = rfaSupplierBqItemDao.saveOrUpdate(supplierBqItem);
			if (CollectionUtil.isNotEmpty(supplierBqItem.getChildren())) {
				for (RfaSupplierBqItem child : supplierBqItem.getChildren()) {
					child.setParent(obj);
					if (child.getUnitPrice() != null) {
						child.setTotalAmount(child.getUnitPrice().multiply(child.getQuantity()));
					}
					child.setTotalAmountWithTax(child.getTotalAmount());
					BigDecimal totalAmountWithTax = child.getTotalAmountWithTax();
					LOG.info("  totalAmountWithTax ********&&&%%  " + totalAmountWithTax);
					if (totalAmountWithTax != null) {
						// To check wheteher Price type is Trade InPrice
						if (PricingTypes.TRADE_IN_PRICE == child.getPriceType()) {

							sum = sum.subtract(totalAmountWithTax);
							LOG.info("Inside saveSupplierEventBqByBuyer's If " + sum + "%%%%%%%%%%" + child.getPriceType());
						} else {
							sum = sum.add(totalAmountWithTax);
							LOG.info("Inside saveSupplierEventBqByBuyer's Else " + sum);

						}

					}
					supplierBq.setGrandTotal(sum);
					rfaSupplierBqItemDao.saveOrUpdate(child);
				}
			}
			LOG.info("After set the SUP BQ :: " + supplierBqItem.toString());
			rfaSupplierBqItem.add(supplierBqItem.createShallowCopy());
		}
		return rfaSupplierBqItem;
	}

	@Override
	public RfaSupplierBq findBqByEventIdAndSupplierId(String id, String bqId, String supplierId) {
		return supplierBqDao.findBqByEventIdAndSupplierId(id, bqId, supplierId);
	}

	@Override
	@Transactional(readOnly = false)
	public void updateBqForSupplier(RfaSupplierBq bq, String supplierId, String decimal, String eventId) {
		LOG.info("updateBqForSupplier***************************");
		RfaSupplierBq persistBq = null;
		int round = (StringUtils.checkString(decimal).length() > 0 ? Integer.parseInt(decimal) : 2);
		LOG.info("===============" + round);
		AuctionRules rules = auctionRulesDao.findAuctionRulesByEventId(eventId);
		if (rules.getPreSetSamePreBidForAllSuppliers()) {
			List<RfaSupplierBq> supplierBqList = supplierBqService.findRfaSummarySupplierBqbyEventId(eventId);
			if (CollectionUtil.isNotEmpty(supplierBqList)) {
				for (RfaSupplierBq rfaSupplierBq : supplierBqList) {
					BigDecimal sum = BigDecimal.ZERO;
					BigDecimal additionalTax = BigDecimal.ZERO;
					String taxDesc = "";

					for (RfaSupplierBqItem supplierBqItem : bq.getSupplierBqItems()) {

						BigDecimal totalAmountWithTax = supplierBqItem.getTotalAmountWithTax();
						LOG.info("***************************" + supplierBqItem.getTotalAmountWithTax());

						if (totalAmountWithTax != null) {
							PricingTypes t = rfaSupplierBqItemDao.getPriceTypeByBqItemId(supplierBqItem.getId());

							// To check wheteher Price type is Trade InPrice
							if (PricingTypes.TRADE_IN_PRICE == t) {
								LOG.info("Inside updateBqForSupplier checking Trade Inprice");
								sum = sum.subtract(totalAmountWithTax);
							} else {
								sum = sum.add(totalAmountWithTax);

							}
						}
						if (additionalTax == BigDecimal.ZERO) {
							additionalTax = bq.getAdditionalTax();
						}
						if (StringUtils.checkString(taxDesc).length() == 0) {
							taxDesc = bq.getTaxDescription();
						}

						RfaSupplierBqItem persistObject = getSupplierBqItemByEventBqItemIdAndSupplierId(supplierBqItem.getBqItemId(), rfaSupplierBq.getSupplier().getId());

						if (persistObject != null) {
							persistObject.setUnitPrice(supplierBqItem.getUnitPrice());
							persistObject.setUnitPriceType(supplierBqItem.getUnitPriceType());
							persistObject.setTotalAmount((supplierBqItem.getTotalAmount() != null ? supplierBqItem.getTotalAmount().setScale(round, RoundingMode.DOWN) : BigDecimal.ZERO));
							persistObject.setTax((supplierBqItem.getTax() != null ? supplierBqItem.getTax().setScale(round, RoundingMode.DOWN) : BigDecimal.ZERO));
							persistObject.setTaxType(supplierBqItem.getTaxType());
							persistObject.setTotalAmountWithTax(supplierBqItem.getTotalAmountWithTax() != null ? supplierBqItem.getTotalAmountWithTax().setScale(round, RoundingMode.DOWN) : BigDecimal.ZERO);
							persistObject.setTaxDescription(supplierBqItem.getTaxDescription());
							@SuppressWarnings("unused")
							RfaSupplierBqItem updatedBqItem = updateSupplierBqItems(persistObject);
						}
					}
					persistBq = rfaSupplierBq;
					if (persistBq != null) {
						persistBq.setAdditionalTax(additionalTax != null ? additionalTax.setScale(round, RoundingMode.DOWN) : BigDecimal.ZERO);
						persistBq.setTaxDescription(taxDesc);
						persistBq.setGrandTotal(sum != null ? sum.setScale(round, RoundingMode.DOWN) : BigDecimal.ZERO);
						BigDecimal totalAfterTax = sum.add(additionalTax != null ? additionalTax : BigDecimal.ZERO);
						LOG.info("additionalTax : " + additionalTax + " Total After Tax : " + totalAfterTax);
						persistBq.setTotalAfterTax(totalAfterTax != null ? totalAfterTax.setScale(round, RoundingMode.DOWN) : BigDecimal.ZERO);
						persistBq.setInitialPrice(persistBq.getTotalAfterTax());
						RfaEvent rfaEvent = rfaEventDao.findByEventId(eventId);
						if (rules != null && rules.getLumsumBiddingWithTax() != null || (AuctionType.FORWARD_DUTCH == rfaEvent.getAuctionType() || AuctionType.REVERSE_DUTCH == rfaEvent.getAuctionType())) {
							persistBq.setRevisedGrandTotal(persistBq.getTotalAfterTax());
						}
						persistBq.setBuyerSubmited(Boolean.TRUE);
						supplierBqService.updateSupplierBq(persistBq);
					}
				}
			}
		} else {
			BigDecimal sum = BigDecimal.ZERO;
			BigDecimal additionalTax = BigDecimal.ZERO;
			String taxDesc = "";
			String bqId = "";

			for (RfaSupplierBqItem supplierBqItem : bq.getSupplierBqItems()) {

				BigDecimal totalAmountWithTax = supplierBqItem.getTotalAmountWithTax();
				LOG.info("***************************" + supplierBqItem.getTotalAmountWithTax());

				if (totalAmountWithTax != null) {
					PricingTypes t = rfaSupplierBqItemDao.getPriceTypeByBqItemId(supplierBqItem.getId());

					// To check wheteher Price type is Trade InPrice
					if (PricingTypes.TRADE_IN_PRICE == t) {
						LOG.info("Inside updateBqForSupplier checking Trade Inprice");
						sum = sum.subtract(totalAmountWithTax);
					} else {
						sum = sum.add(totalAmountWithTax);

					}
				}
				if (additionalTax == BigDecimal.ZERO) {
					additionalTax = bq.getAdditionalTax();
				}
				if (StringUtils.checkString(taxDesc).length() == 0) {
					taxDesc = bq.getTaxDescription();
				}

				RfaSupplierBqItem persistObject = getSupplierBqItemByBqItemAndSupplierId(supplierBqItem.getId(), supplierId);
				if (StringUtils.checkString(bqId).length() == 0) {
					bqId = persistObject.getSupplierBq().getBq().getId();
				}
				if (persistObject != null) {
					persistObject.setUnitPrice(supplierBqItem.getUnitPrice());
					persistObject.setUnitPriceType(supplierBqItem.getUnitPriceType());
					persistObject.setTotalAmount((supplierBqItem.getTotalAmount() != null ? supplierBqItem.getTotalAmount().setScale(round, RoundingMode.DOWN) : BigDecimal.ZERO));
					persistObject.setTax((supplierBqItem.getTax() != null ? supplierBqItem.getTax().setScale(round, RoundingMode.DOWN) : BigDecimal.ZERO));
					persistObject.setTaxType(supplierBqItem.getTaxType());
					persistObject.setTotalAmountWithTax(supplierBqItem.getTotalAmountWithTax() != null ? supplierBqItem.getTotalAmountWithTax().setScale(round, RoundingMode.DOWN) : BigDecimal.ZERO);
					persistObject.setTaxDescription(supplierBqItem.getTaxDescription());
					@SuppressWarnings("unused")
					RfaSupplierBqItem updatedBqItem = updateSupplierBqItems(persistObject);
					// if (StringUtils.checkString(supplierId).length() == 0) {
					// supplierId = persistObject.getSupplier().getId();
					// }
				}
			}
			persistBq = supplierBqService.getSupplierBqByBqAndSupplierId(bqId, supplierId);
			if (persistBq != null) {
				persistBq.setAdditionalTax(additionalTax != null ? additionalTax.setScale(round, RoundingMode.DOWN) : BigDecimal.ZERO);
				persistBq.setTaxDescription(taxDesc);
				persistBq.setGrandTotal(sum != null ? sum.setScale(round, RoundingMode.DOWN) : BigDecimal.ZERO);
				BigDecimal totalAfterTax = sum.add(additionalTax != null ? additionalTax : BigDecimal.ZERO);
				LOG.info("additionalTax : " + additionalTax + " Total After Tax : " + totalAfterTax);
				persistBq.setTotalAfterTax(totalAfterTax != null ? totalAfterTax.setScale(round, RoundingMode.DOWN) : BigDecimal.ZERO);
				persistBq.setInitialPrice(persistBq.getTotalAfterTax());
				RfaEvent rfaEvent = rfaEventDao.findByEventId(eventId);
				if (rules != null && rules.getLumsumBiddingWithTax() != null || (AuctionType.FORWARD_DUTCH == rfaEvent.getAuctionType() || AuctionType.REVERSE_DUTCH == rfaEvent.getAuctionType())) {
					persistBq.setRevisedGrandTotal(persistBq.getTotalAfterTax());
				}
				persistBq.setBuyerSubmited(Boolean.TRUE);
				supplierBqService.updateSupplierBq(persistBq);
			}
		}
	}

	private RfaSupplierBqItem getSupplierBqItemByEventBqItemIdAndSupplierId(String itemId, String supplierId) {
		return rfaSupplierBqItemDao.getSupplierBqItemByEventBqItemIdAndSupplierId(itemId, supplierId);

	}

	@Override
	public List<?> getBqItemForSearchFilterForSupplier(String eventBqId, String supplierId, String searchVal, Integer pageNo, Integer pageLength, Integer itemLevel, Integer itemOrder) {
		List<RfaSupplierBqItem> returnList = new ArrayList<RfaSupplierBqItem>();
		Integer start = null;
		if (pageNo != null) {
			start = pageNo - 1;
		}
		if (pageLength != null) {
			start = start * pageLength;
		}
		// LOG.info(" start : "+start);
		List<RfaSupplierBqItem> bqList = rfaSupplierBqItemDao.getBqItemForSearchFilterForSupplier(eventBqId, supplierId, searchVal, start, pageLength, itemLevel, itemOrder);
		if (CollectionUtil.isNotEmpty(bqList)) {
			for (RfaSupplierBqItem item : bqList) {
				RfaSupplierBqItem bqItem = item.createSearchShallowCopy();
				returnList.add(bqItem);
			}
		}
		return returnList;
	}

	@Override
	public List<BqItemPojo> getAllLevelOrderBqItemByBqId(String bqId, String supplierId, String searchVal) {
		return rfaSupplierBqItemDao.getAllLevelOrderBqItemByBqId(bqId, supplierId, searchVal);
	}

	@Override
	public List<EventEvaluationPojo> getBqSearchFilterEvaluationData(String eventId, String envelopId, User logedUser, String withOrWithoutTax, Integer itemLevel, Integer itemOrder, String searchVal, Integer start, Integer length, String[] supplierList) {
		List<EventEvaluationPojo> returnList = new ArrayList<>();

		RfaEvent event = eventDao.findById(eventId);
		List<String> envelopIds = new ArrayList<String>();
		envelopIds.add(envelopId);
		List<String> bqIds = envelopDao.getBqsByEnvelopId(envelopIds);
		List<Supplier> suppliers = rfaEventSupplierDao.getEventSuppliersForEvaluation(eventId);

		// remove all except visible to user
		List<String> supplierIds = Arrays.asList(supplierList);
		List<Supplier> supplier = suppliers;
		for (int i = 0; i < suppliers.size(); i++) {
			if (!(supplierIds.contains(supplier.get(i).getId()))) {
				suppliers.remove(i);
				i--;
			}
		}

		RfaEnvelop envelop = envelopDao.findById(envelopId);

		AuctionRules rules = auctionRulesDao.findAuctionRulesByEventId(eventId);
		Boolean withTax = Boolean.FALSE;
		if (rules != null && ((null != rules.getItemizedBiddingWithTax() && Boolean.TRUE == rules.getItemizedBiddingWithTax()) || (null != rules.getLumsumBiddingWithTax()) && Boolean.TRUE == rules.getLumsumBiddingWithTax())) {
			withTax = Boolean.TRUE;
		}
		Map<String, BigDecimal> map = new LinkedHashMap<String, BigDecimal>();
		Integer j = 1;
		Integer k = 0;
		for (String bqId : bqIds) {
			EventEvaluationPojo response = new EventEvaluationPojo();
			response.setWithTax(withTax);
			List<List<String>> data = new ArrayList<List<String>>();
			// List<RfaBqItem> bqItems = bqItemDao.findBqItemsForBq(bqId);
			List<RfaBqItem> bqItems = bqItemDao.getBqItemForSearchFilter(bqId, itemLevel, itemOrder, searchVal, start, length);
			for (RfaBqItem bqItem : bqItems) {
				boolean commentsExist = false;
				List<String> answers = new ArrayList<String>();
				List<String> sectionTotalRow = new ArrayList<String>();
				sectionTotalRow.add("");
				sectionTotalRow.add("");
				sectionTotalRow.add("Section Total");
				sectionTotalRow.add("");

				response.setItemId(bqItem.getId());
				response.setName(bqItem.getBq().getName());
				answers.add(bqItem.getLevel() + "." + bqItem.getOrder());
				answers.add(bqItem.getItemName());
				answers.add(bqItem.getUom() != null ? bqItem.getUom().getUom() : "");
				answers.add(bqItem.getQuantity() != null ? String.valueOf(bqItem.getQuantity()) : "");
				List<RfaSupplierBqItem> responseList = rfaSupplierBqItemDao.findSupplierBqItemsByBqItemIdAndEventId(bqItem.getId(), eventId, suppliers);
				LOG.info("responseList : " + responseList.size());
				for (RfaSupplierBqItem item : responseList) {
					BigDecimal sectionSum = BigDecimal.ZERO;
					response.setSupplierId(item.getSupplier().getId());
					if (item.getBqItem().getEvaluationComments() != null && item.getBqItem().getEvaluationComments().size() > 0) {
						for (RfaBqEvaluationComments com : item.getBqItem().getEvaluationComments()) {
							if (((com.getCreatedBy().getId().equals(logedUser.getId()) || (envelop.getLeadEvaluater().getId().equals(logedUser.getId()))) && com.getSupplier().getId().equals(item.getSupplier().getId()))) {
								commentsExist = true;
								LOG.info("Comments : " + item.getBqItem().getEvaluationComments().size());
								continue;
							}
						}
					}

					if (StringUtils.checkString(withOrWithoutTax).equals("1")) {
						if (item.getTotalAmount() != null) {
							BigDecimal amount = new BigDecimal(String.valueOf(item.getTotalAmount())).setScale((event.getDecimal() != null && !event.getDecimal().isEmpty() ? Integer.parseInt(event.getDecimal()) : 2), BigDecimal.ROUND_DOWN);
							sectionSum = BigDecimal.ZERO;
							if (map.containsKey(item.getSupplier().getId() + "-" + item.getLevel())) {
								sectionSum = map.get(item.getSupplier().getId() + "-" + item.getLevel()).add(amount);
							} else {
								sectionSum = amount;
							}
							map.put(item.getSupplier().getId() + "-" + item.getLevel(), sectionSum);
							answers.add(commentsExist + "-" + item.getSupplier().getId() + "-" + item.getBqItem().getId() + "-" + String.valueOf(amount));
						} else {
							answers.add("");
						}
					} else {
						if (item.getTotalAmountWithTax() != null) {
							LOG.info("item.getTotalAmountWithTax() : " + item.getTotalAmountWithTax());
							BigDecimal amount = new BigDecimal(String.valueOf(item.getTotalAmountWithTax())).setScale((event.getDecimal() != null && !event.getDecimal().isEmpty() ? Integer.parseInt(event.getDecimal()) : 2), BigDecimal.ROUND_DOWN);
							sectionSum = BigDecimal.ZERO;
							if (map.containsKey(item.getSupplier().getId() + "-" + item.getLevel())) {
								sectionSum = map.get(item.getSupplier().getId() + "-" + item.getLevel()).add(amount);
							} else {
								sectionSum = amount;
							}
							map.put(item.getSupplier().getId() + "-" + item.getLevel(), sectionSum);
							answers.add(commentsExist + "-" + item.getSupplier().getId() + "-" + item.getBqItem().getId() + "-" + String.valueOf(amount));
						} else {
							answers.add("");
						}
					}
				}

				if (j != bqItem.getLevel()) {
					for (Map.Entry<String, BigDecimal> entry : map.entrySet()) {
						String[] keyset = entry.getKey().split("-");
						String currantLvl = "" + (bqItem.getLevel() - 1);
						if (keyset[1].equals(currantLvl)) {
							sectionTotalRow.add(false + "-" + keyset[0] + "-" + bqItem.getId() + "-" + String.valueOf(entry.getValue()));
						}
					}
					data.add(sectionTotalRow);
				}

				data.add(answers);

				k++;
				if (k == bqItems.size()) {
					if (bqItem.getLevel() != 1) {
						for (Map.Entry<String, BigDecimal> entry : map.entrySet()) {
							String[] keyset = entry.getKey().split("-");
							if (keyset[1].equals(bqItem.getLevel().toString())) {
								sectionTotalRow.add(false + "-" + keyset[0] + "-" + bqItem.getId() + "-" + String.valueOf(entry.getValue()));
							}
						}
						data.add(sectionTotalRow);
					}
				}

				j = bqItem.getLevel();
			}
			response.setData(data);
			response.setEnvelopId(envelopId);
			response.setEventId(eventId);
			returnList.add(response);
		}
		return returnList;
	}

	@Override
	public long totalBqItemCountByBqId(String eventBqId, String supplierId, String searchVal) {
		return rfaSupplierBqItemDao.totalBqItemCountByBqId(eventBqId, supplierId, searchVal);
	}

	@Override
	public List<EventEvaluationPojo> getEvaluationDataForBqComparisonReport(String eventId, String envelopId) {
		List<EventEvaluationPojo> returnList = new ArrayList<>();
		List<Supplier> qualifiedSup = rfaEventSupplierDao.getEventQualifiedSuppliersForEvaluation(eventId);
		RfaEnvelop envelope = null;
		RfaEvent event = null;
		if (CollectionUtil.isNotEmpty(qualifiedSup)) {

			event = eventDao.findById(eventId);
			envelope = envelopDao.findById(envelopId);
			List<String> envelopIds = new ArrayList<String>();
			envelopIds.add(envelopId);
			List<String> bqIds = envelopDao.getBqsByEnvelopId(envelopIds);

			AuctionRules rules = auctionRulesDao.findAuctionRulesByEventId(eventId);
			Boolean withTax = Boolean.FALSE;
			if (rules != null && ((null != rules.getItemizedBiddingWithTax() && Boolean.TRUE == rules.getItemizedBiddingWithTax()) || (null != rules.getLumsumBiddingWithTax()) && Boolean.TRUE == rules.getLumsumBiddingWithTax())) {
				withTax = Boolean.TRUE;
			}
			for (String bqId : bqIds) {
				EventEvaluationPojo response = new EventEvaluationPojo();
				response.setWithTax(withTax);
				List<List<String>> data = new ArrayList<List<String>>();
				List<RfaBqItem> bqItems = bqItemDao.findBqItemsForBq(bqId);

				List<String> buyerHeading = new ArrayList<>();
				List<String> supplierHeading = new ArrayList<>();
				Map<String, List<String>> finalSupplierDataMap = new LinkedHashMap<>();
				List<List<String>> buyerDataList = new ArrayList<>();
				int bqItemLoopCount = 1;

				for (RfaBqItem bqItem : bqItems) {
					BqNonPriceComprision bqNonPriceComprision = new BqNonPriceComprision();
					List<String> answers = new ArrayList<String>();
					response.setItemId(bqItem.getId());
					response.setName(bqItem.getBq().getName());
					answers.add(bqItem.getLevel() + "." + bqItem.getOrder());
					answers.add(bqItem.getItemName());
					answers.add(bqItem.getItemDescription());
					answers.add(bqItem.getUom() != null ? bqItem.getUom().getUom() : "");
					answers.add(bqItem.getQuantity() != null ? String.valueOf(bqItem.getQuantity()) : "");

					setBuyerData(buyerDataList, bqItem);
					bqNonPriceComprision.setBuyerFeildData(buyerDataList);

					if (bqItemLoopCount == 1) {
						setBuyerSupplierHeadings(buyerHeading, supplierHeading, bqItem);
						bqItemLoopCount++;
					}

					if (CollectionUtil.isNotEmpty(buyerHeading)) {
						bqNonPriceComprision.setBuyerHeading(buyerHeading);
					}
					if (CollectionUtil.isNotEmpty(supplierHeading)) {
						bqNonPriceComprision.setSupplierHeading(supplierHeading);
					}

					List<String> supplierName = new ArrayList<>();
					List<RfaSupplierBqItem> responseList = rfaSupplierBqItemDao.findSupplierBqItemsByBqItemIdAndEventId(bqItem.getId(), eventId, qualifiedSup);
					if (CollectionUtil.isNotEmpty(responseList)) {

						for (RfaSupplierBqItem item : responseList) {
							response.setSupplierId(item.getSupplier().getId());
							supplierName.add(item.getSupplier().getCompanyName());
							if (item.getTotalAmountWithTax() != null) {
								BigDecimal amount = item.getTotalAmount() != null ? new BigDecimal(String.valueOf(item.getTotalAmount())).setScale((event.getDecimal() != null && !event.getDecimal().isEmpty() ? Integer.parseInt(event.getDecimal()) : 2), BigDecimal.ROUND_DOWN) : new BigDecimal(0);
								BigDecimal unitPrice = item.getUnitPrice() != null ? new BigDecimal(String.valueOf(item.getUnitPrice())).setScale((event.getDecimal() != null && !event.getDecimal().isEmpty() ? Integer.parseInt(event.getDecimal()) : 2), BigDecimal.ROUND_DOWN) : new BigDecimal(0);
								answers.add(String.valueOf(unitPrice));
								answers.add(String.valueOf(amount));
								if (Boolean.TRUE == withTax) {
									switch (item.getTaxType()) {
									case Percent: {
										BigDecimal taxAmount = amount.multiply(item.getTax() != null ? item.getTax() : BigDecimal.ZERO).divide(new BigDecimal(100));
										taxAmount.setScale((event.getDecimal() != null && !event.getDecimal().isEmpty() ? Integer.parseInt(event.getDecimal()) : 2), BigDecimal.ROUND_DOWN);
										answers.add(String.valueOf(taxAmount));
										break;
									}
									case Amount: {
										BigDecimal taxAmount = item.getTax() != null ? new BigDecimal(String.valueOf(item.getTax())).setScale((event.getDecimal() != null && !event.getDecimal().isEmpty() ? Integer.parseInt(event.getDecimal()) : 2), BigDecimal.ROUND_DOWN) : new BigDecimal(0);
										answers.add(String.valueOf(taxAmount));
										break;
									}
									default:
										break;
									}
									BigDecimal amountWithTax = item.getTotalAmountWithTax() != null ? new BigDecimal(String.valueOf(item.getTotalAmountWithTax())).setScale((event.getDecimal() != null && !event.getDecimal().isEmpty() ? Integer.parseInt(event.getDecimal()) : 2), BigDecimal.ROUND_DOWN) : new BigDecimal(0);
									answers.add(String.valueOf(amountWithTax));
								}
							}

							setSupplierData(finalSupplierDataMap, bqItem, item);
						}
					}
					if (CollectionUtil.isNotEmpty(supplierName)) {
						bqNonPriceComprision.setSupplierName(supplierName);
						bqNonPriceComprision.setSupplierData(finalSupplierDataMap);
						data.add(answers);
						response.setBqNonPriceComprision(bqNonPriceComprision);
					}
				}
				response.setData(data);
				response.setColumns(qualifiedSup);
				List<String> totalAmounts = rfaSupplierBqItemDao.findSumOfTotalAmountForSupplier(bqId, eventId, qualifiedSup, withTax);
				List<BigDecimal> amounts = new ArrayList<BigDecimal>();
				for (String total : totalAmounts) {
					BigDecimal value = null;
					if (StringUtils.checkString(total).length() > 0)
						value = new BigDecimal(total);
					amounts.add(value);

				}

				List<String> gtotal = rfaSupplierBqItemDao.findGrandTotals(bqId, eventId, qualifiedSup, withTax);
				List<BigDecimal> grandAmounts = new ArrayList<BigDecimal>();
				List<EvaluationTotalAmountPojo> gTotalAmounts = new ArrayList<EvaluationTotalAmountPojo>();

				for (String total : gtotal) {
					BigDecimal value = null;
					if (StringUtils.checkString(total).length() > 0) {
						EvaluationTotalAmountPojo pojo = new EvaluationTotalAmountPojo();
						value = new BigDecimal(total);
						pojo.setTotalAmount(value);
					}
					grandAmounts.add(value);
				}

				response.setGrandTotals(grandAmounts);
				response.setAddtionalTaxInfo(rfaSupplierBqItemDao.findAddtionalTax(bqId, eventId, qualifiedSup));
				response.setTotalAmount(amounts);
				response.setEnvelopId(envelopId);
				response.setEventId(eventId);
				response.setDecimal(event.getDecimal());
				returnList.add(response);
			}
		}

		return returnList;
	}

	private void setSupplierData(Map<String, List<String>> finalSupplierDataMap, RfaBqItem bqItem, RfaSupplierBqItem item) {
		List<String> dataList = finalSupplierDataMap.get(item.getBqItem().getId());
		if (dataList != null) {
			List<String> dataList1 = finalSupplierDataMap.get(item.getBqItem().getId());
			if (StringUtils.checkString(bqItem.getBq().getField1Label()).length() > 0) {
				if (bqItem.getBq().getField1FilledBy() != BqUserTypes.BUYER) {
					dataList1.add(item.getField1());
				}
			}

			if (StringUtils.checkString(bqItem.getBq().getField2Label()).length() > 0) {
				if (bqItem.getBq().getField2FilledBy() != BqUserTypes.BUYER) {
					dataList1.add(item.getField2());
				}
			}

			if (StringUtils.checkString(bqItem.getBq().getField3Label()).length() > 0) {
				if (bqItem.getBq().getField3FilledBy() != BqUserTypes.BUYER) {
					dataList1.add(item.getField3());
				}
			}

			if (StringUtils.checkString(bqItem.getBq().getField4Label()).length() > 0) {
				if (bqItem.getBq().getField4FilledBy() != BqUserTypes.BUYER) {
					dataList1.add(item.getField4());
				}
			}

			if (StringUtils.checkString(bqItem.getBq().getField5Label()).length() > 0) {
				if (bqItem.getBq().getField5FilledBy() != BqUserTypes.BUYER) {
					dataList1.add(item.getField5());
				}
			}

			if (StringUtils.checkString(bqItem.getBq().getField6Label()).length() > 0) {
				if (bqItem.getBq().getField6FilledBy() != BqUserTypes.BUYER) {
					dataList1.add(item.getField6());
				}
			}

			if (StringUtils.checkString(bqItem.getBq().getField7Label()).length() > 0) {
				if (bqItem.getBq().getField7FilledBy() != BqUserTypes.BUYER) {
					dataList1.add(item.getField7());
				}
			}

			if (StringUtils.checkString(bqItem.getBq().getField8Label()).length() > 0) {
				if (bqItem.getBq().getField8FilledBy() != BqUserTypes.BUYER) {
					dataList1.add(item.getField8());
				}
			}

			if (StringUtils.checkString(bqItem.getBq().getField9Label()).length() > 0) {
				if (bqItem.getBq().getField9FilledBy() != BqUserTypes.BUYER) {
					dataList1.add(item.getField9());
				}
			}

			if (StringUtils.checkString(bqItem.getBq().getField10Label()).length() > 0) {
				if (bqItem.getBq().getField10FilledBy() != BqUserTypes.BUYER) {
					dataList1.add(item.getField10());
				}
			}

			finalSupplierDataMap.put(item.getBqItem().getId(), dataList1);
		} else {
			List<String> dataList1 = new ArrayList<>();
			if (StringUtils.checkString(bqItem.getBq().getField1Label()).length() > 0) {
				if (bqItem.getBq().getField1FilledBy() != BqUserTypes.BUYER) {
					dataList1.add(item.getField1());
				}

			}

			if (StringUtils.checkString(bqItem.getBq().getField2Label()).length() > 0) {
				if (bqItem.getBq().getField2FilledBy() != BqUserTypes.BUYER) {
					dataList1.add(item.getField2());
				}
			}

			if (StringUtils.checkString(bqItem.getBq().getField3Label()).length() > 0) {
				if (bqItem.getBq().getField3FilledBy() != BqUserTypes.BUYER) {
					dataList1.add(item.getField3());
				}
			}

			if (StringUtils.checkString(bqItem.getBq().getField4Label()).length() > 0) {
				if (bqItem.getBq().getField4FilledBy() != BqUserTypes.BUYER) {
					dataList1.add(item.getField4());
				}
			}

			if (StringUtils.checkString(bqItem.getBq().getField5Label()).length() > 0) {
				if (bqItem.getBq().getField5FilledBy() != BqUserTypes.BUYER) {
					dataList1.add(item.getField5());
				}
			}

			if (StringUtils.checkString(bqItem.getBq().getField6Label()).length() > 0) {
				if (bqItem.getBq().getField6FilledBy() != BqUserTypes.BUYER) {
					dataList1.add(item.getField6());
				}
			}

			if (StringUtils.checkString(bqItem.getBq().getField7Label()).length() > 0) {
				if (bqItem.getBq().getField7FilledBy() != BqUserTypes.BUYER) {
					dataList1.add(item.getField7());
				}
			}

			if (StringUtils.checkString(bqItem.getBq().getField8Label()).length() > 0) {
				if (bqItem.getBq().getField8FilledBy() != BqUserTypes.BUYER) {
					dataList1.add(item.getField8());
				}
			}

			if (StringUtils.checkString(bqItem.getBq().getField9Label()).length() > 0) {
				if (bqItem.getBq().getField9FilledBy() != BqUserTypes.BUYER) {
					dataList1.add(item.getField9());
				}
			}

			if (StringUtils.checkString(bqItem.getBq().getField10Label()).length() > 0) {
				if (bqItem.getBq().getField10FilledBy() != BqUserTypes.BUYER) {
					dataList1.add(item.getField10());
				}
			}

			finalSupplierDataMap.put(item.getBqItem().getId(), dataList1);
		}
	}

	private void setBuyerData(List<List<String>> buyerDataList, RfaBqItem bqItem) {
		List<String> buyerData = new ArrayList<>();
		if (StringUtils.checkString(bqItem.getBq().getField1Label()).length() > 0) {
			if (bqItem.getBq().getField1FilledBy() == BqUserTypes.BUYER) {
				buyerData.add(bqItem.getField1());
			}
		}

		if (StringUtils.checkString(bqItem.getBq().getField2Label()).length() > 0) {
			if (bqItem.getBq().getField2FilledBy() == BqUserTypes.BUYER) {
				buyerData.add(bqItem.getField2());
			}
		}

		if (StringUtils.checkString(bqItem.getBq().getField3Label()).length() > 0) {
			if (bqItem.getBq().getField3FilledBy() == BqUserTypes.BUYER) {
				buyerData.add(bqItem.getField3());
			}
		}

		if (StringUtils.checkString(bqItem.getBq().getField4Label()).length() > 0) {
			if (bqItem.getBq().getField4FilledBy() == BqUserTypes.BUYER) {
				buyerData.add(bqItem.getField4());
			}
		}

		if (StringUtils.checkString(bqItem.getBq().getField5Label()).length() > 0) {
			if (bqItem.getBq().getField5FilledBy() == BqUserTypes.BUYER) {
				buyerData.add(bqItem.getField5());
			}
		}

		if (StringUtils.checkString(bqItem.getBq().getField6Label()).length() > 0) {
			if (bqItem.getBq().getField6FilledBy() == BqUserTypes.BUYER) {
				buyerData.add(bqItem.getField6());
			}
		}

		if (StringUtils.checkString(bqItem.getBq().getField7Label()).length() > 0) {
			if (bqItem.getBq().getField7FilledBy() == BqUserTypes.BUYER) {
				buyerData.add(bqItem.getField7());
			}
		}

		if (StringUtils.checkString(bqItem.getBq().getField8Label()).length() > 0) {
			if (bqItem.getBq().getField8FilledBy() == BqUserTypes.BUYER) {
				buyerData.add(bqItem.getField8());
			}
		}

		if (StringUtils.checkString(bqItem.getBq().getField9Label()).length() > 0) {
			if (bqItem.getBq().getField9FilledBy() == BqUserTypes.BUYER) {
				buyerData.add(bqItem.getField9());
			}
		}

		if (StringUtils.checkString(bqItem.getBq().getField10Label()).length() > 0) {
			if (bqItem.getBq().getField10FilledBy() == BqUserTypes.BUYER) {
				buyerData.add(bqItem.getField10());
			}
		}
		buyerDataList.add(buyerData);
	}

	private void setBuyerSupplierHeadings(List<String> buyerHeading, List<String> supplierHeading, RfaBqItem bqItem) {
		if (StringUtils.checkString(bqItem.getBq().getField1Label()).length() > 0) {
			if (bqItem.getBq().getField1FilledBy() == BqUserTypes.BUYER) {
				buyerHeading.add(bqItem.getBq().getField1Label());
			} else {
				supplierHeading.add(bqItem.getBq().getField1Label());
			}
		}
		if (StringUtils.checkString(bqItem.getBq().getField2Label()).length() > 0) {
			if (bqItem.getBq().getField2FilledBy() == BqUserTypes.BUYER) {
				buyerHeading.add(bqItem.getBq().getField2Label());
			} else {
				supplierHeading.add(bqItem.getBq().getField2Label());
			}
		}
		if (StringUtils.checkString(bqItem.getBq().getField3Label()).length() > 0) {
			if (bqItem.getBq().getField3FilledBy() == BqUserTypes.BUYER) {
				buyerHeading.add(bqItem.getBq().getField3Label());
			} else {
				supplierHeading.add(bqItem.getBq().getField3Label());
			}
		}
		if (StringUtils.checkString(bqItem.getBq().getField4Label()).length() > 0) {
			if (bqItem.getBq().getField4FilledBy() == BqUserTypes.BUYER) {
				buyerHeading.add(bqItem.getBq().getField4Label());
			} else {
				supplierHeading.add(bqItem.getBq().getField4Label());
			}
		}
		if (StringUtils.checkString(bqItem.getBq().getField5Label()).length() > 0) {
			if (bqItem.getBq().getField5FilledBy() == BqUserTypes.BUYER) {
				buyerHeading.add(bqItem.getBq().getField5Label());
			} else {
				supplierHeading.add(bqItem.getBq().getField5Label());
			}
		}
		if (StringUtils.checkString(bqItem.getBq().getField6Label()).length() > 0) {
			if (bqItem.getBq().getField6FilledBy() == BqUserTypes.BUYER) {
				buyerHeading.add(bqItem.getBq().getField6Label());
			} else {
				supplierHeading.add(bqItem.getBq().getField6Label());
			}
		}

		if (StringUtils.checkString(bqItem.getBq().getField7Label()).length() > 0) {
			if (bqItem.getBq().getField7FilledBy() == BqUserTypes.BUYER) {
				buyerHeading.add(bqItem.getBq().getField7Label());
			} else {
				supplierHeading.add(bqItem.getBq().getField7Label());
			}
		}
		if (StringUtils.checkString(bqItem.getBq().getField8Label()).length() > 0) {
			if (bqItem.getBq().getField8FilledBy() == BqUserTypes.BUYER) {
				buyerHeading.add(bqItem.getBq().getField8Label());
			} else {
				supplierHeading.add(bqItem.getBq().getField8Label());
			}
		}
		if (StringUtils.checkString(bqItem.getBq().getField9Label()).length() > 0) {
			if (bqItem.getBq().getField9FilledBy() == BqUserTypes.BUYER) {
				buyerHeading.add(bqItem.getBq().getField9Label());
			} else {
				supplierHeading.add(bqItem.getBq().getField9Label());
			}
		}
		if (StringUtils.checkString(bqItem.getBq().getField10Label()).length() > 0) {
			if (bqItem.getBq().getField10FilledBy() == BqUserTypes.BUYER) {
				buyerHeading.add(bqItem.getBq().getField10Label());
			} else {
				supplierHeading.add(bqItem.getBq().getField10Label());
			}
		}

	}

	@Override
	public RfaSupplierBqItem getBqItemByRfaBqItemId(String itemId, String supplierId) {

		return rfaSupplierBqItemDao.getBqItemByRfaBqItemId(itemId, supplierId);
	}

	@Override
	public List<RfaSupplierBqItem> getAllSupplierBqItemByBqIdAndSupplierIds(String bqId, String supplierIds) {
		return rfaSupplierBqItemDao.findSupplierBqItemListByBqIdAndSupplierIds(bqId, supplierIds);
	}

	@Override
	public List<RfaSupplierBqItem> getAllSupplierBqItemForReportByBqIdAndSupplierId(String bqId, String supplierId) {

		List<RfaSupplierBqItem> list = rfaSupplierBqItemDao.findSupplierBqItemListByBqIdAndSupplierId(bqId, supplierId);
		if (CollectionUtil.isNotEmpty(list)) {
			for (RfaSupplierBqItem item : list) {

				if (item.getSupplier() != null) {
					item.getSupplier().getCompanyName();
				}
				if (CollectionUtil.isNotEmpty(item.getChildren())) {
					item.getChildren().get(0).getId();
				}
			}
		}
		return list;
	}

	private void addSupplierSectionTotal(Map<String, BigDecimal> map, RfaBqItem bqItem, List<String> sectionTotalRow) {
		if (map != null) {
			for (Map.Entry<String, BigDecimal> entry : map.entrySet()) {
				String[] keyset = entry.getKey().split("-");
				String currantLvl = "" + (bqItem.getLevel() - 1);
				if (keyset[1].equals(currantLvl) && !currantLvl.equals("0")) {
					sectionTotalRow.add(false + "-" + keyset[0] + "-" + bqItem.getId() + "-" + String.valueOf(map.get(entry.getKey())));
				}
			}
		}
	}

	private void calclulateSectionAmount(Map<String, BigDecimal> map, RfaSupplierBqItem item, BigDecimal amount) {
		BigDecimal sectionSum = BigDecimal.ZERO;
		if (map.containsKey(item.getSupplier().getId() + "-" + item.getLevel())) {
			sectionSum = map.get(item.getSupplier().getId() + "-" + item.getLevel()).add(amount);
		} else {
			sectionSum = amount;
		}
		map.put(item.getSupplier().getId() + "-" + item.getLevel(), sectionSum);
	}

	@Override
	@Transactional(readOnly = false)
	public void updateBqItems(List<SupplierBqItem> items, String supplierId, SupplierBqStatus status) {

		LOG.info("updateBqItems*******************************");
		RfaSupplierBq persistBq = null;
		BigDecimal sum = BigDecimal.ZERO;
		BigDecimal additionalTax = BigDecimal.ZERO;
		String taxDesc = "";
		String bqId = "";
		String remarks = "";

		for (SupplierBqItem supplierBqItem : items) {

			if (additionalTax == BigDecimal.ZERO) {
				additionalTax = supplierBqItem.getAdditionalTax();
			}
			if (StringUtils.checkString(taxDesc).length() == 0) {
				taxDesc = supplierBqItem.getTaxDescription();
			}
			if (StringUtils.checkString(bqId).length() == 0) {
				bqId = supplierBqItem.getBqId();
			}

			if (StringUtils.checkString(remarks).length() == 0) {
				remarks = supplierBqItem.getRemarks();
			}

			RfaSupplierBqItem persistObject = getSupplierBqItemByBqItemAndSupplierId(supplierBqItem.getId(), SecurityLibrary.getLoggedInUserTenantId());
			if (persistObject != null) {
				persistObject.setField1(supplierBqItem.getField1());
				persistObject.setField2(supplierBqItem.getField2());
				persistObject.setField3(supplierBqItem.getField3());
				persistObject.setField4(supplierBqItem.getField4());
				persistObject.setField5(supplierBqItem.getField5());
				persistObject.setField6(supplierBqItem.getField6());
				persistObject.setField7(supplierBqItem.getField7());
				persistObject.setField8(supplierBqItem.getField8());
				persistObject.setField9(supplierBqItem.getField9());
				persistObject.setField10(supplierBqItem.getField10());
				updateSupplierBqItems(persistObject);
			}
		}

		List<RfaSupplierBqItem> itemList = getAllSupplierBqItemListByBqIdAndSupplierId(bqId, supplierId);
		for (RfaSupplierBqItem supplierBqItem : itemList) {
			LOG.info("PriceType>>>" + supplierBqItem.getPriceType());
			BigDecimal totalAmountWithTax = supplierBqItem.getTotalAmountWithTax();
			if (totalAmountWithTax != null) {
				// PricingTypes t = rfaSupplierBqItemDao.getPriceTypeByBqItemId(supplierBqItem.getId());
				// To check wheteher Price type is Trade InPrice
				if (PricingTypes.TRADE_IN_PRICE == supplierBqItem.getPriceType()) {
					sum = sum.subtract(totalAmountWithTax);
				} else {
					sum = sum.add(totalAmountWithTax);
				}
				LOG.info("sum>>>" + sum);
			}
		}

		persistBq = supplierBqService.getSupplierBqByBqAndSupplierId(bqId, supplierId);
		if (persistBq != null) {
			persistBq.setAdditionalTax(additionalTax);
			persistBq.setTaxDescription(taxDesc);
			persistBq.setGrandTotal(sum);

			if (StringUtils.checkString(remarks).length() > 0) {
				remarks = remarks.replaceAll("-", "_");
			}

			persistBq.setRemark(remarks);
			LOG.info("Remark" + persistBq.getRemark());
			persistBq.setTotalAfterTax(persistBq.getGrandTotal().add(persistBq.getAdditionalTax() != null ? persistBq.getAdditionalTax() : BigDecimal.ZERO));
			persistBq.setSupplierBqStatus(status);
			supplierBqService.updateSupplierBq(persistBq);

			RfaEventSupplier eventSupplier = rfaEventSupplierDao.findEventSupplierByEventIdAndSupplierIgnoreSubmit(persistBq.getEvent().getId(), supplierId);
			if (Boolean.TRUE == persistBq.getBuyerSubmited() && (null == eventSupplier.getConfirmPriceSubmitted() || Boolean.FALSE == eventSupplier.getConfirmPriceSubmitted())) {
				rfaEventSupplierDao.updateEventSupplierConfirm(persistBq.getEvent().getId(), supplierId);
			}
		}

	}

	@Override
	@Transactional(readOnly = false)
	public void saveSupplierBqDetails(String bqId, List<RfaEventSupplier> eventSuppliers) {
		for (RfaEventSupplier rfaEventSupplier : eventSuppliers) {
			Supplier supplier = supplierDao.findSuppById(rfaEventSupplier.getSupplier().getId());
			BigDecimal sum = BigDecimal.ZERO;
			List<RfaBqItem> rfaEventBq = rfaSupplierBqItemDao.getBqItemsbyId(bqId);
			for (RfaBqItem item : rfaEventBq) {
				LOG.info("ITEM : " + item.getItemName() + " Child : " + item.getChildren().size());
				RfaEventBq bq = item.getBq();
				RfaSupplierBq supplierBq = supplierBqDao.findBqByEventIdAndSupplierId(bq.getRfxEvent().getId(), bq.getId(), rfaEventSupplier.getSupplier().getId());
				if (supplierBq == null) {
					supplierBq = new RfaSupplierBq(item.getBq());
					RfaEvent rfxEvent = eventDao.findByEventId(bq.getRfxEvent().getId());
					supplierBq.setEvent(rfxEvent);
					supplierBq.setSupplier(supplier);
					supplierBq.setBuyerSubmited(Boolean.TRUE);
					supplierBq = supplierBqDao.saveOrUpdate(supplierBq);
					LOG.info("supplierBq after insert : " + supplierBq.toLogString());
				}
				RfaSupplierBqItem supplierBqItem = new RfaSupplierBqItem(item, supplier, supplierBq);
				RfaSupplierBqItem obj = rfaSupplierBqItemDao.saveOrUpdate(supplierBqItem);
				if (CollectionUtil.isNotEmpty(supplierBqItem.getChildren())) {
					for (RfaSupplierBqItem child : supplierBqItem.getChildren()) {
						child.setParent(obj);
						if (child.getUnitPrice() != null) {
							child.setTotalAmount(child.getUnitPrice().multiply(child.getQuantity()));
						}
						child.setTotalAmountWithTax(child.getTotalAmount());
						BigDecimal totalAmountWithTax = child.getTotalAmountWithTax();
						LOG.info("  totalAmountWithTax ********&&&%%  " + totalAmountWithTax);
						if (totalAmountWithTax != null) {
							// To check wheteher Price type is Trade InPrice
							if (PricingTypes.TRADE_IN_PRICE == child.getPriceType()) {

								sum = sum.subtract(totalAmountWithTax);
								LOG.info("Inside saveSupplierEventBqByBuyer's If " + sum + "%%%%%%%%%%" + child.getPriceType());
							} else {
								sum = sum.add(totalAmountWithTax);
								LOG.info("Inside saveSupplierEventBqByBuyer's Else " + sum);

							}

						}
						supplierBq.setGrandTotal(sum);
						rfaSupplierBqItemDao.saveOrUpdate(child);
					}
				}
			}
		}
	}

	public RfxSupplierBqItemAndRfxEvent getTotalRfaSupplierBqItem(String eventId) {

		List<RfaEventBq> rftbq = rfaBqService.getAllBqListByEventId(eventId);

		String bqId = rftbq.get(0).getId();

		RfaEventAward rfaEventAward = eventAwardService.rfaEventAwardDetailsByEventIdandBqId(eventId, bqId);

		RfxSupplierBqItemAndRfxEvent rfxSupplierBqItemAndRfxEvent = new RfxSupplierBqItemAndRfxEvent();

		RfaSupplierBq supplierBq = new RfaSupplierBq();
		List<Supplier> suppliers = rfaEventSupplierDao.getEventQualifiedSuppliersForEvaluation(eventId);

		if (rfaEventAward == null) {
			LOG.info("Event Award Null");
			RfaEventAward awd = new RfaEventAward();
			awd.setAwardCriteria(AwardCriteria.LOWEST_TOTAL_PRICE);
			rfxSupplierBqItemAndRfxEvent.setRfaEventAward(awd);
			supplierBq = rfaEventService.getSupplierBQOfLeastTotalPrice(eventId, bqId, SecurityLibrary.getLoggedInUserTenantId());
		} else {
			LOG.info("Event Award not Null " + rfaEventAward.getId());
			if (AwardCriteria.LOWEST_TOTAL_PRICE == rfaEventAward.getAwardCriteria()) {
				supplierBq = rfaEventService.getSupplierBQOfLeastTotalPrice(eventId, bqId, SecurityLibrary.getLoggedInUserTenantId());
			} else if (AwardCriteria.LOWEST_ITEMIZED_PRICE == rfaEventAward.getAwardCriteria()) {
				supplierBq = rfaEventService.getSupplierBQOfLowestItemisedPrice(eventId, bqId, SecurityLibrary.getLoggedInUserTenantId());
			} else if (AwardCriteria.MANUAL == rfaEventAward.getAwardCriteria()) {
				if (CollectionUtil.isNotEmpty(suppliers)) {
					supplierBq = rfaEventService.getSupplierBQwithSupplierId(eventId, bqId, suppliers.get(0).getId(), SecurityLibrary.getLoggedInUserTenantId(), rfaEventAward.getId());
				} else {
					LOG.info("Qualified suppliers is empty");
				}
			}
		}

		rfxSupplierBqItemAndRfxEvent.setRfaEventAward(rfxSupplierBqItemAndRfxEvent.getRfaEventAward() == null ?
				rfaEventAward : rfxSupplierBqItemAndRfxEvent.getRfaEventAward());

		rfxSupplierBqItemAndRfxEvent.setRfaSupplierBqItemList(supplierBq.getSupplierBqItems());

		return rfxSupplierBqItemAndRfxEvent;
	}
}