package com.privasia.procurehere.service.impl;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.privasia.procurehere.core.entity.*;
import com.privasia.procurehere.core.enums.AwardCriteria;
import com.privasia.procurehere.core.utils.*;
import com.privasia.procurehere.service.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.privasia.procurehere.core.dao.RfqBqDao;
import com.privasia.procurehere.core.dao.RfqBqEvaluationCommentsDao;
import com.privasia.procurehere.core.dao.RfqBqItemDao;
import com.privasia.procurehere.core.dao.RfqEnvelopDao;
import com.privasia.procurehere.core.dao.RfqEventDao;
import com.privasia.procurehere.core.dao.RfqEventSupplierDao;
import com.privasia.procurehere.core.dao.RfqSupplierBqDao;
import com.privasia.procurehere.core.dao.RfqSupplierBqItemDao;
import com.privasia.procurehere.core.enums.BqUserTypes;
import com.privasia.procurehere.core.enums.PricingTypes;
import com.privasia.procurehere.core.enums.SupplierBqStatus;
import com.privasia.procurehere.core.pojo.BqItemPojo;
import com.privasia.procurehere.core.pojo.BqNonPriceComprision;
import com.privasia.procurehere.core.pojo.BqPojo;
import com.privasia.procurehere.core.pojo.EvaluationTotalAmountPojo;
import com.privasia.procurehere.core.pojo.EventEvaluationPojo;

/**
 * @author Vipul
 * @author Ravi
 */

@Service
@Transactional(readOnly = true)
public class RfqSupplierBqItemServiceImpl implements RfqSupplierBqItemService {

	private static final Logger LOG = LogManager.getLogger(Global.ADMIN_LOG);

	@Autowired
	RfqSupplierBqItemDao supplierBqItemDao;

	@Autowired
	RfqSupplierBqDao supplierBqDao;

	@Autowired
	RfqEventDao eventDao;

	@Autowired
	RfqSupplierBqService supplierBqService;

	@Autowired
	RfqEventSupplierDao rftEventSupplierDao;

	@Autowired
	RfqEnvelopDao envelopDao;

	@Autowired
	RfqBqItemDao bqItemDao;

	@Autowired
	RfqBqDao bqDao;

	@Autowired
	RfqSupplierBqItemDao rfqSupplierBqItemDao;

	@Autowired
	EventAuditService eventAuditService;

	@Autowired
	RfqBqEvaluationCommentsDao rfqBqEvaluationCommentsDao;

	@Autowired
	RfqBqService rfqBqService;

	@Autowired
	RfqAwardService eventAwardService;

	@Autowired
	RfqEventService rfqEventService;

	@Override
	public List<RfqSupplierBqItem> getAllSupplierBqItemByBqIdAndSupplierId(String bqId, String supplierId) {
		List<RfqSupplierBqItem> returnList = new ArrayList<RfqSupplierBqItem>();
		List<RfqSupplierBqItem> list = supplierBqItemDao.findSupplierBqItemByListByBqIdAndSupplierId(bqId, supplierId);
		LOG.info("ITEM LIST SIZE : " + list.size());
		bulidSupplierBqItemList(returnList, list);
		return returnList;
	}

	private void bulidSupplierBqItemList(List<RfqSupplierBqItem> returnList, List<RfqSupplierBqItem> list) {
		LOG.info("BUILDING BQ ITEM LIST :: " + list.size());
		if (CollectionUtil.isNotEmpty(list)) {
			for (RfqSupplierBqItem item : list) {
				RfqSupplierBqItem parent = item.createShallowCopy();
				LOG.info("BQ PARENT ITEMS :: " + parent.toString());
				returnList.add(parent);
				if (CollectionUtil.isNotEmpty(item.getChildren())) {
					LOG.info("BQ CHILDREN :: :: ");
					for (RfqSupplierBqItem child : item.getChildren()) {
						LOG.info("BQ CHILD LIST :: " + child.toString());
						if (parent.getChildren() == null) {
							parent.setChildren(new ArrayList<RfqSupplierBqItem>());
						}
						parent.getChildren().add(child.createShallowCopy());
					}
				}
				LOG.info(" GrandTotal : " + item.getSupplierBq().toString());
			}
		}
	}

	@Override
	@Transactional(readOnly = false)
	public List<RfqSupplierBqItem> saveSupplierEventBq(String bqId) {
		List<RfqSupplierBqItem> rftSupplierBqItem = new ArrayList<>();
		BigDecimal sum = BigDecimal.ZERO;
		List<RfqBqItem> rftEventBq = supplierBqItemDao.getBqItemsbyId(bqId);
		for (RfqBqItem item : rftEventBq) {
			RfqEventBq bq = item.getBq();
			RfqSupplierBq supplierBq = supplierBqDao.findBqByEventIdAndBqName(bq.getRfxEvent().getId(), bq.getId(), SecurityLibrary.getLoggedInUserTenantId());
			if (supplierBq == null) {
				supplierBq = new RfqSupplierBq(item.getBq());
				RfqEvent rfxEvent = eventDao.findByEventId(bq.getRfxEvent().getId());
				supplierBq.setEvent(rfxEvent);
				supplierBq.setSupplier(SecurityLibrary.getLoggedInUser().getSupplier());
				supplierBq = supplierBqDao.saveOrUpdate(supplierBq);
			}
			RfqSupplierBqItem supplierBqItem = new RfqSupplierBqItem(item, SecurityLibrary.getLoggedInUser().getSupplier(), supplierBq);
			RfqSupplierBqItem obj = supplierBqItemDao.saveOrUpdate(supplierBqItem);
			if (CollectionUtil.isNotEmpty(supplierBqItem.getChildren())) {
				for (RfqSupplierBqItem child : supplierBqItem.getChildren()) {
					child.setParent(obj);
					if (child.getUnitPrice() != null) {
						child.setTotalAmount(child.getUnitPrice().multiply(child.getQuantity()));
					}
					child.setTotalAmountWithTax(child.getTotalAmount());
					BigDecimal totalAmountWithTax = child.getTotalAmountWithTax();
					LOG.info("  totalAmountWithTax   " + totalAmountWithTax);
					if (totalAmountWithTax != null) {
						// To check wheteher Price type is Trade InPrice
						LOG.info("=======******************=======" + child.getPriceType());
						if (PricingTypes.TRADE_IN_PRICE == child.getPriceType()) {
							LOG.info("&&&&&&&&&&&&&& Inside of saveSupplierEventBq's If");
							sum = sum.subtract(totalAmountWithTax);
						} else {
							LOG.info("$$$$$$$$$$$$$$$ Inside of saveSupplierEventBq's Else");
							sum = sum.add(totalAmountWithTax);
						}

					}
					supplierBq.setGrandTotal(sum);
					supplierBqItemDao.saveOrUpdate(child);
				}
			}
			rftSupplierBqItem.add(supplierBqItem.createShallowCopy());
		}
		return rftSupplierBqItem;
	}

	@Override
	public List<RfqBqItem> getBqItemsbyId(String bqId) {
		return supplierBqItemDao.getBqItemsbyId(bqId);
	}

	@Override
	public RfqSupplierBqItem getSupplierBqItemByBqItemAndSupplierId(String itemId, String supplierId) {
		return supplierBqItemDao.getBqItemByBqItemId(itemId, supplierId);
	}

	@Override
	@Transactional(readOnly = false)
	public RfqSupplierBqItem updateSupplierBqItems(RfqSupplierBqItem persistObject) {
		return supplierBqItemDao.update(persistObject);
	}

	@Override
	@Transactional(readOnly = false)
	public void updateBqItems(List<SupplierBqItem> items, String supplierId) {
		RfqSupplierBq persistBq = null;
		BigDecimal sum = BigDecimal.ZERO;
		BigDecimal additionalTax = BigDecimal.ZERO;
		String taxDesc = "";
		String bqId = "";
		String remarks = "";

		for (SupplierBqItem supplierBqItem : items) {
			LOG.info(" inside  updatedBqItems checking Trade in Price :" + supplierBqItem.getEventId() + "......................" + supplierBqItem.getUnitPrice());

			if (additionalTax == BigDecimal.ZERO) {
				additionalTax = supplierBqItem.getAdditionalTax();
			}
			/* if (StringUtils.checkString(taxDesc).length() == 0) { */
			taxDesc = supplierBqItem.getTaxDescription();
			/* } */
			if (StringUtils.checkString(bqId).length() == 0) {
				bqId = supplierBqItem.getBqId();
			}
			if (StringUtils.checkString(remarks).length() == 0) {
				remarks = supplierBqItem.getRemarks();
			}

			RfqSupplierBqItem persistObject = getSupplierBqItemByBqItemAndSupplierId(supplierBqItem.getId(), SecurityLibrary.getLoggedInUserTenantId());
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

		List<RfqSupplierBqItem> itemList = getAllSupplierBqItemListByBqIdAndSupplierId(bqId, supplierId);
		for (RfqSupplierBqItem supplierBqItem : itemList) {
			LOG.info("PriceType>>>" + supplierBqItem.getPriceType());
			BigDecimal totalAmountWithTax = supplierBqItem.getTotalAmountWithTax();
			if (totalAmountWithTax != null) {
				// PricingTypes t = supplierBqItemDao.getPriceTypeByBqItemId(supplierBqItem.getId());
				if (totalAmountWithTax != null) {
					// To check wheteher Price type is Trade InPrice
					// LOG.info("supplierBqItem.getId().getP"+supplierBqItem.getId().getPri);
					if (PricingTypes.TRADE_IN_PRICE == supplierBqItem.getPriceType()) {
						sum = sum.subtract(totalAmountWithTax);
					} else {
						sum = sum.add(totalAmountWithTax);
					}
				}
			}

			/*
			 * RfqSupplierBqItem persistObject = getSupplierBqItemByBqItemAndSupplierId(supplierBqItem.getId(),
			 * SecurityLibrary.getLoggedInUserTenantId()); if (persistObject != null) {
			 * persistObject.setUnitPrice(supplierBqItem.getUnitPrice());
			 * persistObject.setTotalAmount(supplierBqItem.getTotalAmount());
			 * persistObject.setTax(supplierBqItem.getTax()); persistObject.setTaxType(supplierBqItem.getTaxType());
			 * persistObject.setTotalAmountWithTax(supplierBqItem.getTotalAmountWithTax());
			 * persistObject.setTaxDescription(supplierBqItem.getTaxDescription());
			 * persistObject.setField1(supplierBqItem.getField1()); persistObject.setField2(supplierBqItem.getField2());
			 * persistObject.setField3(supplierBqItem.getField3()); persistObject.setField4(supplierBqItem.getField4());
			 * persistObject.setField5(supplierBqItem.getField5()); persistObject.setField6(supplierBqItem.getField6());
			 * persistObject.setField7(supplierBqItem.getField7()); persistObject.setField8(supplierBqItem.getField8());
			 * persistObject.setField9(supplierBqItem.getField9());
			 * persistObject.setField10(supplierBqItem.getField10()); RfqSupplierBqItem updatedBqItem =
			 * updateSupplierBqItems(persistObject); if (StringUtils.checkString(supplierId).length() == 0) { supplierId
			 * = persistObject.getSupplier().getId(); } LOG.info(" saveSupplierBQDetails updatedBqItem : :" +
			 * updatedBqItem); } }
			 */

			persistBq = supplierBqService.getSupplierBqByBqAndSupplierId(bqId, supplierId);
			if (persistBq != null) {
				persistBq.setAdditionalTax(additionalTax);
				persistBq.setTaxDescription(taxDesc);
				persistBq.setGrandTotal(sum);

				if (StringUtils.checkString(remarks).length() > 0) {
					remarks = remarks.replaceAll("-", "_");
				}

				persistBq.setRemark(remarks);
				persistBq.setTotalAfterTax(persistBq.getGrandTotal().add(persistBq.getAdditionalTax() != null ? persistBq.getAdditionalTax() : BigDecimal.ZERO));
				supplierBqService.updateSupplierBq(persistBq);
			}

		}
	}

	private List<RfqSupplierBqItem> getAllSupplierBqItemListByBqIdAndSupplierId(String bqId, String supplierId) {
		List<RfqSupplierBqItem> list = supplierBqItemDao.findBqItemListAndBqListByBqIdAndSupplierId(bqId, supplierId);
		return list;
	}

	@Override
	@Transactional(readOnly = false)
	public RfqSupplierBqItem updateSupplierBqItem(RfqSupplierBqItem persistObject) {
		return supplierBqItemDao.update(persistObject);
	}

	@Override
	public List<EventEvaluationPojo> getBqEvaluationData(String eventId, String envelopId, List<Supplier> selectedSuppliers, User logedUser, String withOrWithoutTax, Integer itemLevel, Integer itemOrder, String searchVal, Integer start, Integer length) {
		List<EventEvaluationPojo> returnList = new ArrayList<>();
		List<Supplier> suppliers = null;
		if (CollectionUtil.isEmpty(selectedSuppliers)) {
			suppliers = rftEventSupplierDao.getEventSuppliersForEvaluation(eventId);
		} else {
			suppliers = rftEventSupplierDao.getEventSuppliersForEvaluation(eventId, selectedSuppliers);
		}

		if (CollectionUtil.isNotEmpty(suppliers)) {
			RfqEvent event = eventDao.findById(eventId);
			final int dec = Integer.parseInt((StringUtils.checkString(event.getDecimal()).length() > 0 ? event.getDecimal() : "2"));
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
			// List<String> bqIds = envelopDao.getBqsByEnvelopId(envelopIds);
			List<BqPojo> bqPojo = envelopDao.getBqsIdListByEnvelopIdByOrder(envelopIds);

			RfqEnvelop envelop = envelopDao.findById(envelopId);
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
			int bqItemlistSize = 0;
			List<String> levelOrderList = new ArrayList<>();
			for (BqPojo bqObj : bqPojo) {
				EventEvaluationPojo response = new EventEvaluationPojo();
				LinkedList<List<String>> data = new LinkedList<List<String>>();
				// List<RfqBqItem> bqItems = bqItemDao.findBqItemsForBq(bqId);
				List<RfqBqItem> bqItems = bqItemDao.getBqItemForSearchFilter(bqObj.getId(), itemLevel, itemOrder, searchVal, start, length);

				List<RfqBqItem> bqItemlevelOrders = bqItemDao.getBqItemForSearchFilter(bqObj.getId(), null, null, null, null, null);
				LOG.info("bqItemlistSize :" + bqItemlistSize);
				// If Bq Item is more then previous then setting building new list of section number level order
				if (CollectionUtil.isNotEmpty(bqItemlevelOrders) && bqItemlevelOrders.size() > bqItemlistSize) {
					levelOrderList = new ArrayList<>();
				}
				for (RfqBqItem bqItemLevelOrder : bqItemlevelOrders) {
					// building section number level order
					if (CollectionUtil.isNotEmpty(bqItemlevelOrders) && bqItemlevelOrders.size() > bqItemlistSize) {
						levelOrderList.add(bqItemLevelOrder.getLevel() + "." + bqItemLevelOrder.getOrder());

						LOG.info("TotalAmount :--->" + bqItemLevelOrder.getTotalAmount());
					}
				}

				// To check If next BQ item is greater then previous
				if (CollectionUtil.isNotEmpty(bqItemlevelOrders) && bqItemlevelOrders.size() > bqItemlistSize) {
					bqItemlistSize = bqItemlevelOrders.size();
				}

				Map<String, BigDecimal> map = new LinkedHashMap<String, BigDecimal>();
				Integer j = 1;
				Integer k = 0;
				for (RfqBqItem bqItem : bqItems) {
					boolean commentsExist = false;
					List<String> answers = new ArrayList<String>();
					List<String> sectionTotalRow = new ArrayList<String>();
					sectionTotalRow.add("");
					sectionTotalRow.add("");
					sectionTotalRow.add("");
					sectionTotalRow.add("Section Sub total");
					sectionTotalRow.add("");
					response.setItemId(bqItem.getId());
					response.setName(bqObj.getBqName());
					answers.add(bqItem.getLevel() + "." + bqItem.getOrder());
					answers.add(bqItem.getItemName());
					answers.add(bqItem.getPriceType() != null ? bqItem.getPriceType().getValue() : "");
					answers.add(bqItem.getUom() != null ? bqItem.getUom().getUom() : "");

					LOG.info("=================================>" + (bqItem.getQuantity() != null ? formatter.format(bqItem.getQuantity()) : ""));

					answers.add(bqItem.getQuantity() != null ? formatter.format(bqItem.getQuantity()) : "");
					List<RfqSupplierBqItem> responseList = supplierBqItemDao.findSupplierBqItemsByBqItemIdAndEventId(bqItem.getId(), eventId, suppliers);

					for (RfqSupplierBqItem item : responseList) {

						BigDecimal sectionSum = BigDecimal.ZERO;
						response.setSupplierId(item.getSupplier().getId());
						commentsExist = false;
						if (item.getBqItem().getEvaluationComments() != null && item.getBqItem().getEvaluationComments().size() > 0) {
							for (RfqBqEvaluationComments com : item.getBqItem().getEvaluationComments()) {
								if (((com.getCreatedBy().getId().equals(logedUser.getId()) || (envelop.getLeadEvaluater().getId().equals(logedUser.getId()))) && com.getSupplier().getId().equals(item.getSupplier().getId()))) {
									commentsExist = true;
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

							LOG.info("===========================");
							if (item.getTotalAmountWithTax() != null) {
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
								List<RfqBqEvaluationComments> cmts = rfqBqEvaluationCommentsDao.getCommentsByBqIdAndEventId(id, eventId, logedUser);
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
									List<RfqBqEvaluationComments> cmts = rfqBqEvaluationCommentsDao.getCommentsByBqIdAndEventId(id, eventId, logedUser);
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

					/*
					 * if (lastItem != null && map.get(lastItem.getSupplier().getId())!=null) { answers = new
					 * ArrayList<String>(); answers.add(""); answers.add(""); answers.add(""); answers.add("Sub total");
					 * answers.add(""); LOG.info("====>" + false + "-" + lastItem.getSupplier().getId() + "-" +
					 * lastItem.getBqItem().getId() + "-" + String.valueOf(map.get(lastItem.getSupplier().getId())));
					 * answers.add(false + "-" + lastItem.getSupplier().getId() + "-" + lastItem.getBqItem().getId() +
					 * "-" + String.valueOf(map.get(lastItem.getSupplier().getId()))); data.add(answers); }
					 */

				}
				response.setData(data);
				response.setColumns(suppliers);

				List<String> totalAmounts = null;
				// totalAmounts = supplierBqItemDao.findSumOfTotalAmountWithTaxForSupplier(bqId, eventId, suppliers,
				// null);
				totalAmounts = supplierBqItemDao.findGrandTotalsForEvaluationView(bqObj.getId(), eventId, suppliers, null);
				List<String> supplierRemarks = new ArrayList<String>();
				List<EvaluationTotalAmountPojo> totalAmountList = new ArrayList<EvaluationTotalAmountPojo>();
				String allRemarks = "";
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
						allRemarks += (values.length > 5 ? StringUtils.checkString(values[5]) : "");
						supplierRemarks.add(values.length > 5 ? StringUtils.checkString(values[5]) : "");
						RfqEventBq bq = bqDao.findById(bqObj.getId());
						boolean commentsExist = false;
						if (bq != null && CollectionUtil.isNotEmpty(bq.getEvaluationComments())) {
							for (RfqBqTotalEvaluationComments com : bq.getEvaluationComments()) {
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
				// If none of the suppliers have added remarks then dont set this list into model
				if (StringUtils.checkString(allRemarks).length() > 0) {
					response.setSupplierRemarks(supplierRemarks);
				}
				totalAmounts = supplierBqItemDao.findGrandTotalsForView(bqObj.getId(), eventId, suppliers);
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
						RfqEventBq bq = bqDao.findById(bqObj.getId());
						boolean commentsExist = false;
						if (bq != null && CollectionUtil.isNotEmpty(bq.getEvaluationComments())) {
							for (RfqBqTotalEvaluationComments com : bq.getEvaluationComments()) {
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
				response.setAddtionalTaxInfo(supplierBqItemDao.findAddtionalTaxForView(bqObj.getId(), eventId, suppliers));

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
		List<Supplier> qualifiedSuppliers = rftEventSupplierDao.getEventQualifiedSuppliersForEvaluation(eventId);

		if (CollectionUtil.isNotEmpty(qualifiedSuppliers)) {
			RfqEvent event = eventDao.findById(eventId);

			List<String> envelopIds = new ArrayList<String>();
			envelopIds.add(envelopId);
			List<String> bqIds = envelopDao.getBqsByEnvelopId(envelopIds);

			for (String bqId : bqIds) {
				EventEvaluationPojo response = new EventEvaluationPojo();
				List<List<String>> data = new ArrayList<List<String>>();
				List<RfqBqItem> bqItems = bqItemDao.findBqItemsForBq(bqId);
				for (RfqBqItem bqItem : bqItems) {
					List<String> answers = new ArrayList<String>();
					response.setItemId(bqItem.getId());
					response.setName(bqItem.getBq().getName());
					answers.add(bqItem.getLevel() + "." + bqItem.getOrder());
					answers.add(bqItem.getItemName());
					answers.add(bqItem.getItemDescription());
					answers.add(bqItem.getUom() != null ? bqItem.getUom().getUom() : "");
					answers.add(bqItem.getQuantity() != null ? String.valueOf(bqItem.getQuantity()) : "");
					List<RfqSupplierBqItem> responseList = supplierBqItemDao.findSupplierBqItemsByBqItemIdAndEventId(bqItem.getId(), eventId, qualifiedSuppliers);
					for (RfqSupplierBqItem item : responseList) {
						response.setSupplierId(item.getSupplier().getId());
						if (item.getTotalAmountWithTax() != null) {
							// BigDecimal amount = new
							// BigDecimal(String.valueOf(item.getTotalAmount())).setScale(Integer.parseInt(event.getDecimal()),
							// BigDecimal.ROUND_DOWN);
							// BigDecimal taxAmount = new
							// BigDecimal(String.valueOf(item.getTax())).setScale(Integer.parseInt(event.getDecimal()),
							// BigDecimal.ROUND_DOWN);
							//
							// BigDecimal amountWithTax = new
							// BigDecimal(String.valueOf(item.getTotalAmountWithTax())).setScale(Integer.parseInt(event.getDecimal()),
							// BigDecimal.ROUND_DOWN);
							// BigDecimal unitPrice = new
							// BigDecimal(String.valueOf(item.getUnitPrice())).setScale(Integer.parseInt(event.getDecimal()),
							// BigDecimal.ROUND_DOWN);

							BigDecimal amount = BigDecimal.ZERO;
							if (item.getTotalAmount() != null)
								amount = new BigDecimal(String.valueOf(item.getTotalAmount())).setScale((event.getDecimal() != null && !event.getDecimal().isEmpty() ? Integer.parseInt(event.getDecimal()) : 2), BigDecimal.ROUND_DOWN);
							BigDecimal taxAmount = BigDecimal.ZERO;
							if (item.getTax() != null)
								taxAmount = new BigDecimal(String.valueOf(item.getTax())).setScale((event.getDecimal() != null && !event.getDecimal().isEmpty() ? Integer.parseInt(event.getDecimal()) : 2), BigDecimal.ROUND_DOWN);

							BigDecimal amountWithTax = BigDecimal.ZERO;
							if (item.getTotalAmountWithTax() != null)
								amountWithTax = new BigDecimal(String.valueOf(item.getTotalAmountWithTax())).setScale((event.getDecimal() != null && !event.getDecimal().isEmpty() ? Integer.parseInt(event.getDecimal()) : 2), BigDecimal.ROUND_DOWN);

							BigDecimal unitPrice = BigDecimal.ZERO;
							if (item.getUnitPrice() != null)
								unitPrice = new BigDecimal(String.valueOf(item.getUnitPrice())).setScale((event.getDecimal() != null && !event.getDecimal().isEmpty() ? Integer.parseInt(event.getDecimal()) : 2), BigDecimal.ROUND_DOWN);

							answers.add(String.valueOf(unitPrice));
							answers.add(String.valueOf(amount));
							answers.add(String.valueOf(taxAmount));
							answers.add(String.valueOf(amountWithTax));
						}
					}
					data.add(answers);
				}
				response.setData(data);
				response.setColumns(qualifiedSuppliers);
				List<String> totalAmounts = supplierBqItemDao.findSumOfTotalAmountForSupplier(bqId, eventId, qualifiedSuppliers);
				List<BigDecimal> amounts = new ArrayList<BigDecimal>();
				for (String total : totalAmounts) {
					BigDecimal value = null;
					if (StringUtils.checkString(total).length() > 0)
						value = new BigDecimal(total);
					amounts.add(value);
				}

				List<String> gtotal = supplierBqItemDao.findGrandTotals(bqId, eventId, qualifiedSuppliers);
				List<BigDecimal> grandAmounts = new ArrayList<BigDecimal>();
				List<EvaluationTotalAmountPojo> gTotalAmounts = new ArrayList<EvaluationTotalAmountPojo>();
				for (String total : gtotal) {
					BigDecimal value = null;
					if (StringUtils.checkString(total).length() > 0)
						value = new BigDecimal(total);
					grandAmounts.add(value);
				}

				response.setGrandTotals(grandAmounts);
				response.setAddtionalTaxInfo(supplierBqItemDao.findAddtionalTax(bqId, eventId, qualifiedSuppliers));
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
	public List<?> getBqItemForSearchFilterForSupplier(String eventBqId, String supplierId, String searchVal, Integer pageNo, Integer pageLength, Integer itemLevel, Integer itemOrder) {
		List<RfqSupplierBqItem> returnList = new ArrayList<RfqSupplierBqItem>();
		Integer start = null;
		if (pageNo != null) {
			start = pageNo - 1;
		}
		if (pageLength != null) {
			start = start * pageLength;
		}
		// LOG.info(" start : "+start);
		List<RfqSupplierBqItem> bqList = supplierBqItemDao.getBqItemForSearchFilterForSupplier(eventBqId, supplierId, searchVal, start, pageLength, itemLevel, itemOrder);
		if (CollectionUtil.isNotEmpty(bqList)) {
			for (RfqSupplierBqItem item : bqList) {
				RfqSupplierBqItem bqItem = item.createSearchShallowCopy();
				returnList.add(bqItem);
			}
		}
		return returnList;
	}

	@Override
	public List<BqItemPojo> getAllLevelOrderBqItemByBqId(String bqId, String supplierId, String searchVal) {
		return supplierBqItemDao.getAllLevelOrderBqItemByBqId(bqId, supplierId, searchVal);
	}

	@Override
	public List<EventEvaluationPojo> getBqSearchFilterEvaluationData(String eventId, String envelopId, User logedUser, String withOrWithoutTax, Integer itemLevel, Integer itemOrder, String searchVal, Integer start, Integer length, String[] supplierList) {
		List<EventEvaluationPojo> returnList = new ArrayList<>();
		RfqEvent event = eventDao.findById(eventId);
		List<String> envelopIds = new ArrayList<String>();
		envelopIds.add(envelopId);
		List<String> bqIds = envelopDao.getBqsByEnvelopId(envelopIds);
		RfqEnvelop envelop = envelopDao.findById(envelopId);
		List<Supplier> suppliers = rftEventSupplierDao.getEventSuppliersForEvaluation(eventId);
		List<String> supplierIds = Arrays.asList(supplierList);
		List<Supplier> supplier = suppliers;
		for (int i = 0; i < suppliers.size(); i++) {
			if (!(supplierIds.contains(supplier.get(i).getId()))) {
				suppliers.remove(i);
				i--;
			}
		}

		for (String bqId : bqIds) {
			EventEvaluationPojo response = new EventEvaluationPojo();
			List<List<String>> data = new ArrayList<List<String>>();
			// List<RfqBqItem> bqItems = bqItemDao.findBqItemsForBq(bqId);
			List<RfqBqItem> bqItems = bqItemDao.getBqItemForSearchFilter(bqId, itemLevel, itemOrder, searchVal, start, length);
			Map<String, BigDecimal> map = new LinkedHashMap<String, BigDecimal>();
			Integer j = 1;
			Integer k = 0;
			for (RfqBqItem bqItem : bqItems) {
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
				List<RfqSupplierBqItem> responseList = supplierBqItemDao.findSupplierBqItemsByBqItemIdAndEventId(bqItem.getId(), eventId, suppliers);
				for (RfqSupplierBqItem item : responseList) {
					LOG.info("item.getSupplier() : " + item.getSupplier().getCompanyName());
					BigDecimal sectionSum = BigDecimal.ZERO;
					response.setSupplierId(item.getSupplier().getId());
					commentsExist = false;
					if (item.getBqItem().getEvaluationComments() != null && item.getBqItem().getEvaluationComments().size() > 0) {
						for (RfqBqEvaluationComments com : item.getBqItem().getEvaluationComments()) {
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
	public long totalBqItemCountByBqId(String bqId, String supplierId, String searchVal) {
		return supplierBqItemDao.totalBqItemCountByBqId(bqId, supplierId, searchVal);
	}

	@Override
	public List<EventEvaluationPojo> getEvaluationDataForBqComparisonReport(String eventId, String envelopId) {
		List<EventEvaluationPojo> returnList = new ArrayList<>();
		List<Supplier> qualifiedSuppliers = rftEventSupplierDao.getEventQualifiedSuppliersForEvaluation(eventId);
		RfqEvent event = eventDao.findById(eventId);
		RfqEnvelop envelope = null;
		if (CollectionUtil.isNotEmpty(qualifiedSuppliers)) {
			envelope = envelopDao.findById(envelopId);
			List<String> envelopIds = new ArrayList<String>();
			envelopIds.add(envelopId);
			//List<String> bqIds = envelopDao.getBqsByEnvelopId(envelopIds);
			List<BqPojo> bqList = envelopDao.getBqsIdListByEnvelopIdByOrder(envelopIds);
			for (BqPojo bq : bqList) {
				EventEvaluationPojo response = new EventEvaluationPojo();
				List<List<String>> data = new ArrayList<List<String>>();
				List<RfqBqItem> bqItems = bqItemDao.findBqItemsForBq(bq.getId());

				List<String> buyerHeading = new ArrayList<>();
				List<String> supplierHeading = new ArrayList<>();
				Map<String, List<String>> finalSupplierDataMap = new LinkedHashMap<>();
				List<List<String>> buyerDataList = new ArrayList<>();
				int bqItemLoopCount = 1;
				for (RfqBqItem bqItem : bqItems) {
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
					List<RfqSupplierBqItem> responseList = supplierBqItemDao.findSupplierBqItemsByBqItemIdAndEventId(bqItem.getId(), eventId, qualifiedSuppliers);
					for (RfqSupplierBqItem item : responseList) {
						response.setSupplierId(item.getSupplier().getId());
						supplierName.add(item.getSupplier().getCompanyName());
						if (item.getTotalAmountWithTax() != null) {
							// BigDecimal amount = new
							// BigDecimal(String.valueOf(item.getTotalAmount())).setScale(Integer.parseInt(event.getDecimal()),
							// BigDecimal.ROUND_DOWN);
							// BigDecimal taxAmount = new
							// BigDecimal(String.valueOf(item.getTax())).setScale(Integer.parseInt(event.getDecimal()),
							// BigDecimal.ROUND_DOWN);
							//
							// BigDecimal amountWithTax = new
							// BigDecimal(String.valueOf(item.getTotalAmountWithTax())).setScale(Integer.parseInt(event.getDecimal()),
							// BigDecimal.ROUND_DOWN);
							// BigDecimal unitPrice = new
							// BigDecimal(String.valueOf(item.getUnitPrice())).setScale(Integer.parseInt(event.getDecimal()),
							// BigDecimal.ROUND_DOWN);

							BigDecimal amount = BigDecimal.ZERO;
							if (item.getTotalAmount() != null)
								amount = new BigDecimal(String.valueOf(item.getTotalAmount())).setScale((event.getDecimal() != null && !event.getDecimal().isEmpty() ? Integer.parseInt(event.getDecimal()) : 2), BigDecimal.ROUND_DOWN);
							BigDecimal taxAmount = BigDecimal.ZERO;
							if (item.getTax() != null)
								taxAmount = new BigDecimal(String.valueOf(item.getTax())).setScale((event.getDecimal() != null && !event.getDecimal().isEmpty() ? Integer.parseInt(event.getDecimal()) : 2), BigDecimal.ROUND_DOWN);

							BigDecimal amountWithTax = BigDecimal.ZERO;
							if (item.getTotalAmountWithTax() != null)
								amountWithTax = new BigDecimal(String.valueOf(item.getTotalAmountWithTax())).setScale((event.getDecimal() != null && !event.getDecimal().isEmpty() ? Integer.parseInt(event.getDecimal()) : 2), BigDecimal.ROUND_DOWN);

							BigDecimal unitPrice = BigDecimal.ZERO;
							if (item.getUnitPrice() != null)
								unitPrice = new BigDecimal(String.valueOf(item.getUnitPrice())).setScale((event.getDecimal() != null && !event.getDecimal().isEmpty() ? Integer.parseInt(event.getDecimal()) : 2), BigDecimal.ROUND_DOWN);

							answers.add(String.valueOf(unitPrice));
							answers.add(String.valueOf(amount));
							answers.add(String.valueOf(taxAmount));
							answers.add(String.valueOf(amountWithTax));
						}

						setSupplierData(finalSupplierDataMap, bqItem, item);
					}

					if (CollectionUtil.isNotEmpty(supplierName)) {
						bqNonPriceComprision.setSupplierName(supplierName);
						bqNonPriceComprision.setSupplierData(finalSupplierDataMap);
						data.add(answers);
						response.setBqNonPriceComprision(bqNonPriceComprision);
					}
					response.setData(data);
					response.setColumns(qualifiedSuppliers);

					List<String> totalAmounts = supplierBqItemDao.findSumOfTotalAmountForSupplier(bq.getId(), eventId, qualifiedSuppliers);
					List<BigDecimal> amounts = new ArrayList<BigDecimal>();

					for (String total : totalAmounts) {
						BigDecimal value = null;

						if (StringUtils.checkString(total).length() > 0)
							value = new BigDecimal(total);
						amounts.add(value);
					}

					List<String> gtotal = supplierBqItemDao.findGrandTotals(bq.getId(), eventId, qualifiedSuppliers);
					List<BigDecimal> grandAmounts = new ArrayList<BigDecimal>();
					for (String total : gtotal) {
						BigDecimal value = null;
						if (StringUtils.checkString(total).length() > 0)
							value = new BigDecimal(total);
						grandAmounts.add(value);
					}

					response.setGrandTotals(grandAmounts);
					response.setAddtionalTaxInfo(supplierBqItemDao.findAddtionalTax(bq.getId(), eventId, qualifiedSuppliers));
					response.setTotalAmount(amounts);
					response.setEnvelopId(envelopId);
					response.setEventId(eventId);
					response.setDecimal(event.getDecimal());
				}
				returnList.add(response);
			}
		}

		return returnList;
	}

	private void setSupplierData(Map<String, List<String>> finalSupplierDataMap, RfqBqItem bqItem, RfqSupplierBqItem item) {
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

	private void setBuyerData(List<List<String>> buyerDataList, RfqBqItem bqItem) {
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

	private void setBuyerSupplierHeadings(List<String> buyerHeading, List<String> supplierHeading, RfqBqItem bqItem) {
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
	public RfqSupplierBqItem getBqItemByRfqBqItemId(String itemId, String supplierId) {
		return supplierBqItemDao.getBqItemByRfqBqItemId(itemId, supplierId);
	}

	@Override
	public List<RfqSupplierBqItem> getAllSupplierBqItemByBqIdAndSupplierIds(String bqId, String supplierIds) {
		return rfqSupplierBqItemDao.findSupplierBqItemListByBqIdAndSupplierIds(bqId, supplierIds);
	}

	@Override
	public List<RfqSupplierBqItem> getAllSupplierBqItemForReportByBqIdAndSupplierId(String bqId, String supplierId) {

		List<RfqSupplierBqItem> list = rfqSupplierBqItemDao.findSupplierBqItemByListByBqIdAndSupplierId(bqId, supplierId);
		if (CollectionUtil.isNotEmpty(list)) {
			for (RfqSupplierBqItem item : list) {

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

	@Override
	@Transactional(readOnly = false)
	public void updateBqItems(List<SupplierBqItem> items, String supplierId, SupplierBqStatus status) {

		RfqSupplierBq persistBq = null;
		BigDecimal sum = BigDecimal.ZERO;
		BigDecimal additionalTax = BigDecimal.ZERO;
		String taxDesc = "";
		String bqId = "";
		String remarks = "";

		for (SupplierBqItem supplierBqItem : items) {
			LOG.info(" inside  updatedBqItems checking Trade in Price :" + supplierBqItem.getEventId() + "......................" + supplierBqItem.getUnitPrice());

			if (additionalTax == BigDecimal.ZERO) {
				additionalTax = supplierBqItem.getAdditionalTax();
			}
			/* if (StringUtils.checkString(taxDesc).length() == 0) { */
			taxDesc = supplierBqItem.getTaxDescription();
			/* } */
			if (StringUtils.checkString(bqId).length() == 0) {
				bqId = supplierBqItem.getBqId();
			}
			if (StringUtils.checkString(remarks).length() == 0) {
				remarks = supplierBqItem.getRemarks();
			}

			RfqSupplierBqItem persistObject = getSupplierBqItemByBqItemAndSupplierId(supplierBqItem.getId(), SecurityLibrary.getLoggedInUserTenantId());
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

		List<RfqSupplierBqItem> itemList = getAllSupplierBqItemListByBqIdAndSupplierId(bqId, supplierId);
		for (RfqSupplierBqItem supplierBqItem : itemList) {
			LOG.info("PriceType>>>" + supplierBqItem.getPriceType());
			BigDecimal totalAmountWithTax = supplierBqItem.getTotalAmountWithTax();
			if (totalAmountWithTax != null) {
				// PricingTypes t = supplierBqItemDao.getPriceTypeByBqItemId(supplierBqItem.getId());
				if (totalAmountWithTax != null) {
					// To check wheteher Price type is Trade InPrice
					// LOG.info("supplierBqItem.getId().getP"+supplierBqItem.getId().getPri);
					if (PricingTypes.TRADE_IN_PRICE == supplierBqItem.getPriceType()) {
						sum = sum.subtract(totalAmountWithTax);
					} else {
						sum = sum.add(totalAmountWithTax);
					}
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
				persistBq.setTotalAfterTax(persistBq.getGrandTotal().add(persistBq.getAdditionalTax() != null ? persistBq.getAdditionalTax() : BigDecimal.ZERO));
				persistBq.setSupplierBqStatus(status);
				supplierBqService.updateSupplierBq(persistBq);
			}

		}
	
	}

	public RfxSupplierBqItemAndRfxEvent getTotalRfqSupplierBqItem(String eventId) {

		List<RfqEventBq> rftbq = rfqBqService.getAllBqListByEventId(eventId);

		RfxSupplierBqItemAndRfxEvent rfxSupplierBqItemAndRfxEvent = new RfxSupplierBqItemAndRfxEvent();


		String bqId = rftbq.get(0).getId();

		RfqEventAward rfqEventAward = eventAwardService.rfqEventAwardDetailsByEventIdandBqId(eventId, bqId);

		RfqSupplierBq supplierBq = new RfqSupplierBq();
		List<Supplier> suppliers = rftEventSupplierDao.getEventQualifiedSuppliersForEvaluation(eventId);

		if (rfqEventAward == null) {
			LOG.info("Event Award Null");
			RfqEventAward awd = new RfqEventAward();
			awd.setAwardCriteria(AwardCriteria.LOWEST_TOTAL_PRICE);
			rfxSupplierBqItemAndRfxEvent.setRfqEventAward(awd);
			supplierBq = rfqEventService.getSupplierBQOfLeastTotalPrice(eventId, bqId, SecurityLibrary.getLoggedInUserTenantId());
		} else {
			LOG.info("Event Award not Null " + rfqEventAward.getId());
			if (AwardCriteria.LOWEST_TOTAL_PRICE == rfqEventAward.getAwardCriteria()) {
				supplierBq = rfqEventService.getSupplierBQOfLeastTotalPrice(eventId, bqId, SecurityLibrary.getLoggedInUserTenantId());
			} else if (AwardCriteria.LOWEST_ITEMIZED_PRICE == rfqEventAward.getAwardCriteria()) {
				supplierBq = rfqEventService.getSupplierBQOfLowestItemisedPrice(eventId, bqId, SecurityLibrary.getLoggedInUserTenantId());
			} else if (AwardCriteria.MANUAL == rfqEventAward.getAwardCriteria()) {
				if (CollectionUtil.isNotEmpty(suppliers)) {
					supplierBq = rfqEventService.getSupplierBQwithSupplierId(eventId, bqId, suppliers.get(0).getId(), SecurityLibrary.getLoggedInUserTenantId(), rfqEventAward.getId());
				} else {
					LOG.info("Qualified suppliers is empty");
				}
			}
		}

		rfxSupplierBqItemAndRfxEvent.setRfqEventAward(rfxSupplierBqItemAndRfxEvent.getRfqEventAward() == null ?
				rfqEventAward : rfxSupplierBqItemAndRfxEvent.getRfqEventAward());

		rfxSupplierBqItemAndRfxEvent.setRfqSupplierBqItemList(supplierBq.getSupplierBqItems());

		return rfxSupplierBqItemAndRfxEvent;
	}
}
