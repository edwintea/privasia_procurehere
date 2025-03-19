package com.privasia.procurehere.service.impl;

import java.util.ArrayList;
import java.util.List;

import com.privasia.procurehere.core.dao.RftSorDao;
import com.privasia.procurehere.core.dao.RftSupplierSorDao;
import com.privasia.procurehere.core.entity.RfpSupplierSor;
import com.privasia.procurehere.core.entity.RftSupplierSor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.privasia.procurehere.core.dao.RftBqDao;
import com.privasia.procurehere.core.dao.RftBqItemDao;
import com.privasia.procurehere.core.dao.RftCqDao;
import com.privasia.procurehere.core.dao.RftCqItemDao;
import com.privasia.procurehere.core.dao.RftCqOptionDao;
import com.privasia.procurehere.core.dao.RftEnvelopDao;
import com.privasia.procurehere.core.dao.RftEventDao;
import com.privasia.procurehere.core.dao.RftEventMeetingDao;
import com.privasia.procurehere.core.dao.RftEventSupplierDao;
import com.privasia.procurehere.core.dao.RftSupplierBqDao;
import com.privasia.procurehere.core.dao.RftSupplierBqItemDao;
import com.privasia.procurehere.core.dao.RftSupplierCqDao;
import com.privasia.procurehere.core.dao.RftSupplierCqItemDao;
import com.privasia.procurehere.core.dao.RftSupplierCqOptionDao;
import com.privasia.procurehere.core.dao.RftSupplierMeetingAttendanceDao;
import com.privasia.procurehere.core.entity.CqOption;
import com.privasia.procurehere.core.entity.RftCq;
import com.privasia.procurehere.core.entity.RftCqEvaluationComments;
import com.privasia.procurehere.core.entity.RftCqItem;
import com.privasia.procurehere.core.entity.RftCqOption;
import com.privasia.procurehere.core.entity.RftEnvelop;
import com.privasia.procurehere.core.entity.RftEvent;
import com.privasia.procurehere.core.entity.RftSupplierBq;
import com.privasia.procurehere.core.entity.RftSupplierCq;
import com.privasia.procurehere.core.entity.RftSupplierCqItem;
import com.privasia.procurehere.core.entity.RftSupplierCqOption;
import com.privasia.procurehere.core.entity.Supplier;
import com.privasia.procurehere.core.entity.User;
import com.privasia.procurehere.core.enums.CqType;
import com.privasia.procurehere.core.enums.SupplierCqStatus;
import com.privasia.procurehere.core.exceptions.NotAllowedException;
import com.privasia.procurehere.core.pojo.CqPojo;
import com.privasia.procurehere.core.pojo.EventEvaluationPojo;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.MaskUtils;
import com.privasia.procurehere.core.utils.SecurityLibrary;
import com.privasia.procurehere.core.utils.StringUtils;
import com.privasia.procurehere.service.EventAuditService;
import com.privasia.procurehere.service.RftCqService;
import com.privasia.procurehere.service.RftEnvelopService;
import com.privasia.procurehere.service.RftEventService;
import com.privasia.procurehere.service.RftSupplierCqItemService;
import com.privasia.procurehere.service.RftSupplierCqService;
import com.privasia.procurehere.service.supplier.SupplierService;

/**
 * @author Vipul
 */

@Service
@Transactional(readOnly = true)
public class RftSupplierCqItemServiceImpl implements RftSupplierCqItemService {

	private static final Logger LOG = LogManager.getLogger(Global.SUPPLIER_LOG);

	@Autowired
	RftEnvelopDao envelopDao;

	@Autowired
	RftSupplierCqItemDao supplierCqItemDao;

	@Autowired
	RftCqDao cqDao;

	@Autowired
	RftEventDao eventDao;

	@Autowired
	RftCqItemDao cqItemDao;

	@Autowired
	SupplierService supplierService;

	@Autowired
	RftEventSupplierDao rftEventSupplierDao;

	@Autowired
	RftCqOptionDao cqOptionDao;

	@Autowired
	RftCqItemDao rftCqItemDao;

	@Autowired
	RftSorDao rftSorDao;

	@Autowired
	RftSupplierSorDao rftSupplierSorDao;

	@Autowired
	RftBqItemDao rftBqItemDao;

	@Autowired
	RftSupplierBqItemDao rftSupplierBqItemDao;

	@Autowired
	RftSupplierBqDao rftSupplierBqDao;

	@Autowired
	RftBqDao rftBqDao;

	@Autowired
	RftCqService rftCqService;

	@Autowired
	RftEventService rftEventService;

	@Autowired
	RftEnvelopService rftEnvelopService;

	@Autowired
	RftEventMeetingDao rftEventMeetingDao;

	@Autowired
	RftSupplierMeetingAttendanceDao rftSupplierMeetingAttendanceDao;

	@Autowired
	EventAuditService eventAuditService;

	@Autowired
	RftSupplierCqOptionDao rftSupplierCqOptionDao;

	@Autowired
	RftSupplierCqDao rftSupplierCqDao;

	@Autowired
	RftSupplierCqService rftSupplierCqService;

	@Override
	public List<EventEvaluationPojo> getCqEvaluationData(String eventId, String envelopId, List<Supplier> selectedSuppliers, User logedUser) {

		List<EventEvaluationPojo> returnList = new ArrayList<>();
		List<Supplier> suppliers = null;
		if (CollectionUtil.isEmpty(selectedSuppliers)) {
			suppliers = rftEventSupplierDao.getEventSuppliersForEvaluation(eventId);
		} else {
			suppliers = rftEventSupplierDao.getEventSuppliersForEvaluation(eventId, selectedSuppliers);
		}

		if (CollectionUtil.isNotEmpty(suppliers)) {

			for (Supplier supplier : suppliers) {
				LOG.info("Supplier : " + supplier.getId() + ", MASK : " + supplier.getCompanyName());
			}

			List<String> envelopIds = new ArrayList<String>();
			envelopIds.add(envelopId);
			RftEnvelop envelop = envelopDao.findById(envelopId);
			if (envelop.getRfxEvent() != null) {
				if (envelop.getRfxEvent().getViewSupplerName() != null && !envelop.getRfxEvent().getViewSupplerName() && !envelop.getRfxEvent().getDisableMasking()) {
					for (Supplier supplier : suppliers) {
						supplier.setCompanyName(MaskUtils.maskName(envelop.getPreFix(), supplier.getId(), envelop.getId()));
						LOG.info("Supplier : " + supplier.getId() + ", MASK : " + supplier.getCompanyName());
					}
				}
			}
			// List<String> cqIds = envelopDao.getCqsByEnvelopId(envelopIds);
			List<CqPojo> cqPojo = envelopDao.getCqsIdListByEnvelopIdByOrder(envelopIds);

			for (CqPojo cq : cqPojo) {
				EventEvaluationPojo response = new EventEvaluationPojo();
				List<List<String>> data = new ArrayList<List<String>>();
				List<RftCqItem> cqItems = cqItemDao.findCqItemsForCq(cq.getId());
				for (RftCqItem cqItem : cqItems) {
					response.setName(cq.getName());

					List<String> answers = new ArrayList<String>();
					List<RftSupplierCqItem> responseList = supplierCqItemDao.findSupplierCqItemsByCqItemIdAndEventId(cqItem.getId(), eventId, suppliers);
					answers.add(cqItem.getLevel() + "." + cqItem.getOrder());
					boolean leadEvalComment = false;
					String comment = rftCqService.getLeadEvaluatorComment(cqItem.getId());
					if (StringUtils.checkString(comment).length() > 0) {
						leadEvalComment = true;
					}
					answers.add(cqItem.getId() + "" + (leadEvalComment ? 1 : 0) + "-" + cqItem.getItemName());
					answers.add(cqItem.getId());
					response.setItemId(cqItem.getId());

					int supplierIndex = 0;
					LOG.info("CQ => " + cqItem.getId() + " - " + supplierIndex);
					for (RftSupplierCqItem item : responseList) {

						LOG.info("Outer : " + suppliers.get(supplierIndex).getId() + ", Inner : " + item.getSupplier().getId());
						LOG.info("===> :" + supplierIndex + "===>" + suppliers.size() + "==>" + (supplierIndex + 1));
						while (!suppliers.get(supplierIndex).getId().equals(item.getSupplier().getId())) {
							LOG.info("Supplier CQ response missing : " + suppliers.get(supplierIndex).getId() + " - " + supplierIndex);
							// this supplier has not supplied response
							response.setSupplierId(suppliers.get(supplierIndex).getId());
							// No response by supplier
							answers.add(false + "-" + suppliers.get(supplierIndex).getId() + "-dummy-" + "");
							supplierIndex++;
							if (supplierIndex == suppliers.size()) {
								break;
							}
						}
						LOG.info("===> :" + supplierIndex + "===>" + suppliers.size() + "==>" + (supplierIndex + 1));
						// if (!suppliers.get(supplierIndex).getId().equals(item.getSupplier().getId())) {
						// LOG.info("Supplier CQ response missing : " + suppliers.get(supplierIndex).getId() + " - " +
						// supplierIndex);
						// // this supplier has not supplied response
						// response.setSupplierId(suppliers.get(supplierIndex).getId());
						// // No response by supplier
						// answers.add(false + "-" + suppliers.get(supplierIndex).getId() + "-dummy-" + "");
						// supplierIndex++;
						// }

						boolean commentsExist = false;
						String supplierId = "";
						String itemId = "";
						response.setSupplierId(item.getSupplier().getId());
						if (item.getCqItem().getCqType() == CqType.TEXT || item.getCqItem().getCqType() == CqType.NUMBER || item.getCqItem().getCqType() == CqType.DATE || item.getCqItem().getCqType() == CqType.PARAGRAPH) {
							if (item.getCqItem().getEvaluationComments() != null && item.getCqItem().getEvaluationComments().size() > 0) {
								for (RftCqEvaluationComments com : item.getCqItem().getEvaluationComments()) {
									if ((com.getCreatedBy().getId().equals(logedUser.getId()) || (envelop.getLeadEvaluater().getId().equals(logedUser.getId()))) && com.getSupplier().getId().equals(item.getSupplier().getId())) {
										commentsExist = true;
										LOG.info("Comments : " + item.getCqItem().getEvaluationComments().size());
										continue;
									}
								}
							}

							answers.add(commentsExist + "-" + item.getSupplier().getId() + "-" + item.getCqItem().getId() + "-" + item.getTextAnswers());
						} else {
							String str = "";
							List<RftSupplierCqOption> listAnswers = item.getListAnswers();
							for (RftSupplierCqOption op : listAnswers) {
								commentsExist = false;
								if (op.getCqItem().getCqItem().getEvaluationComments() != null && op.getCqItem().getCqItem().getEvaluationComments().size() > 0) {
									for (RftCqEvaluationComments com : op.getCqItem().getCqItem().getEvaluationComments()) {
										if ((com.getCreatedBy().getId().equals(logedUser.getId()) || (envelop.getLeadEvaluater().getId().equals(logedUser.getId()))) && com.getSupplier().getId().equals(item.getSupplier().getId())) {
											commentsExist = true;
											LOG.info("Comments : " + op.getCqItem().getCqItem().getEvaluationComments().size());
											continue;
										}
									}
								}
								if (StringUtils.checkString(supplierId).length() == 0)
									supplierId = item.getSupplier().getId();
								if (StringUtils.checkString(itemId).length() == 0)
									itemId = item.getCqItem().getId();

								str += op.getValue() + (op.getScoring() != null ? "/" + op.getScoring() : "") + ",";
							}
							LOG.info("STR >>>>>>>>>>>>>>>>>>>> " + str);

							if (str.length() > 0) {
								str = str.substring(0, str.length() - 1);
							}
							answers.add(commentsExist + "-" + supplierId + "-" + itemId + "-" + str);
						}

						LOG.info("===> :" + supplierIndex + "===>" + suppliers.size() + "==>" + (supplierIndex + 1));
						supplierIndex++;
						LOG.info("===> :" + supplierIndex + "===>" + suppliers.size() + "==>" + (supplierIndex + 1));
					}

					LOG.info("===> :" + supplierIndex + "===>" + suppliers.size() + "==>" + (supplierIndex + 1));
					while (supplierIndex < suppliers.size()) {
						LOG.info("Supplier CQ response missing : " + suppliers.get(supplierIndex).getId() + " - " + supplierIndex);
						// this supplier has not supplied response
						response.setSupplierId(suppliers.get(supplierIndex).getId());
						// No response by supplier
						answers.add(false + "-" + suppliers.get(supplierIndex).getId() + "-dummy-" + "");
						supplierIndex++;
					}

					data.add(answers);
				}
				response.setData(data);

				/*
				 * if (envelop.getRfxEvent() != null) { if (envelop.getRfxEvent().getViewSupplerName() != null &&
				 * !envelop.getRfxEvent().getViewSupplerName() && !envelop.getRfxEvent().getDisableMasking()) {
				 * Collections.sort(suppliers, new Comparator<Supplier>() { public int compare(Supplier o1, Supplier o2)
				 * { if (o1.getCompanyName() == null || o2.getCompanyName() == null) { return 0; } return
				 * o1.getCompanyName().compareTo(o2.getCompanyName()); } }); } }
				 */

				response.setColumns(suppliers);
				List<String> sumOfScoring = supplierCqItemDao.findSumOfScoringForSupplier(cq.getId(), eventId, suppliers);
				response.setScoring(sumOfScoring);
				response.setEnvelopId(envelopId);
				response.setEventId(eventId);
				returnList.add(response);
			}
		}
		return returnList;
	}

	@Override
	public List<RftSupplierCqItem> getAllSupplierCqItemByCqId(String cqId, String supplierId) {
		List<RftSupplierCqItem> returnList = new ArrayList<RftSupplierCqItem>();
		List<RftSupplierCqItem> list = supplierCqItemDao.findSupplierCqItemListByCqId(cqId, supplierId);
		bulidSupplierCqItemList(returnList, list);
		return returnList;
	}

	private void bulidSupplierCqItemList(List<RftSupplierCqItem> returnList, List<RftSupplierCqItem> list) {
		if (CollectionUtil.isNotEmpty(list)) {
			for (RftSupplierCqItem item : list) {
				LOG.info("Cq Order : " + item.getCq().getCqOrder());
				if (item.getCqItem().getCqOptions() != null) {
					for (CqOption op : item.getCqItem().getCqOptions()) {
						op.getValue();
					}
				}
				returnList.add(item.createShallowCopy());
			}
		}
	}

	@Override
	@Transactional(readOnly = false)
	public void saveSupplierEventCq(String cqId, String eventId, String tenantId) {
		List<RftCqItem> cqItems = supplierCqItemDao.getCqItemsbyId(cqId);
		for (RftCqItem item : cqItems) {
			LOG.info("ITEM : " + item.toLogString() + "  Options " + item.getCqOptions().size());
			RftCq cq = item.getCq();
			RftSupplierCqItem supplierCq = supplierCqItemDao.findCqByEventIdAndCqName(eventId, item.getId());
			if (supplierCq == null) {
				supplierCq = new RftSupplierCqItem(item);
				supplierCq.setSupplier(new Supplier(tenantId));
				supplierCq = supplierCqItemDao.saveOrUpdateWithFlush(supplierCq);
			}
		}

	}

	@Override
	@Transactional(readOnly = false)
	public void saveSupplierCq(String cqId, String eventId, String supplierId) {
		LOG.info("Saving Supplier Cq.... : ");
		RftSupplierCq suppliercq = rftSupplierCqDao.findCqByEventIdAndEventCqId(eventId, cqId, SecurityLibrary.getLoggedInUserTenantId());
		if (suppliercq == null) {
			suppliercq = new RftSupplierCq();
			RftEvent rfxEvent = eventDao.findByEventId(eventId);
			suppliercq.setEvent(rfxEvent);
			suppliercq.setSupplier(SecurityLibrary.getLoggedInUser().getSupplier());
			RftCq rfaCq = cqDao.findById(cqId);
			suppliercq.setCq(rfaCq);
			suppliercq = rftSupplierCqDao.saveOrUpdate(suppliercq);
		}
	}

	@Override
	@Transactional(readOnly = false)
	public void updateCqItems(List<RftSupplierCqItem> items) {
		for (RftSupplierCqItem item : items) {
			List<RftCqOption> options = item.getListOptAnswers();
			RftSupplierCqItem cqItem = supplierCqItemDao.findById(item.getId());
			if (item.getTextAnswers() != null) {
				String textAnswer = item.getTextAnswers().replace('-', '_');
				cqItem.setTextAnswers(textAnswer);
			}
			LOG.info(cqItem.getTextAnswers());

			/*
			 * if (item.getDateAnswer() != null) { cqItem.setDateAnswer(item.getDateAnswer()); }
			 * LOG.info(cqItem.getDateAnswer());
			 */
			cqItem.setFileData(item.getFileData());
			cqItem.setFileName(item.getFileName());
			cqItem.setCredContentType(item.getCredContentType());
			List<RftSupplierCqOption> supOptions = new ArrayList<RftSupplierCqOption>();
			if (CollectionUtil.isNotEmpty(options)) {
				for (RftCqOption option : options) {
					RftCqOption op = cqOptionDao.findById(option.getId());
					if (op != null) {
						RftSupplierCqOption cqOption = new RftSupplierCqOption();
						cqOption.setCqItem(cqItem);
						cqOption.setOrder(op.getOrder());
						cqOption.setScoring(op.getScoring());
						cqOption.setValue(op.getValue());
						supOptions.add(cqOption);
					}
				}
			}
			cqItem.setListAnswers(supOptions);
			cqItem.setSupplier(SecurityLibrary.getLoggedInUser().getSupplier());
			supplierCqItemDao.update(cqItem);
		}
	}

	@Override
	public List<EventEvaluationPojo> getEvaluationDataForCqComparison(String eventId, String envelopId) {
		List<EventEvaluationPojo> returnList = new ArrayList<>();
		List<Supplier> qualifiedSupDb = rftEventSupplierDao.getEventQualifiedSuppliersForEvaluation(eventId);
		List<Supplier> qualifiedSup = new ArrayList<Supplier>();
		RftEnvelop envelope = null;
		RftEvent event = rftEventService.getRftEventByeventId(eventId);

		// Masking will be auto flushed by Hibernate due to dirty checking.
		// This is happening because Dumbo changed the transaction type to readOnly=false for Audit.
		if (event.getViewSupplerName() != null && !event.getViewSupplerName() && !event.getDisableMasking()) {
			envelope = rftEnvelopService.getRftEnvelopById(envelopId);
			for (Supplier supplier : qualifiedSupDb) {
				Supplier supplierCopy = supplier.createShallowCopy();
				supplierCopy.setCompanyName(MaskUtils.maskName(envelope.getPreFix(), supplier.getId(), envelope.getId()));
				qualifiedSup.add(supplierCopy);
			}
		} else {
			qualifiedSup = qualifiedSupDb;
		}

		if (CollectionUtil.isNotEmpty(qualifiedSup)) {
			List<String> envelopIds = new ArrayList<String>();
			envelopIds.add(envelopId);
			// List<String> cqIds = envelopDao.getCqsByEnvelopId(envelopIds);
			List<CqPojo> cqList = envelopDao.getCqsIdListByEnvelopIdByOrder(envelopIds);

			for (CqPojo cq : cqList) {
				EventEvaluationPojo response = new EventEvaluationPojo();
				List<List<String>> data = new ArrayList<List<String>>();
				List<RftCqItem> cqItems = cqItemDao.findCqItemsForCq(cq.getId());
				Integer scoreSum = 0;
				for (RftCqItem cqItem : cqItems) {
					response.setName(cqItem.getCq().getName());
					response.setTotalScore(cqItem.getTotalScore());
					List<String> answers = new ArrayList<String>();
					// List<RftSupplierCqItem> responseList =
					// supplierCqItemDao.findSupplierCqItemsByCqItemIdAndEventId(cqItem.getId(), eventId, qualifiedSup);
					List<RftSupplierCqItem> responseList = supplierCqItemDao.findSupplierCqItemsByCqItemIdAndEventIdNew(cqItem.getId(), eventId, qualifiedSup);

					answers.add(cqItem.getLevel() + "." + cqItem.getOrder());
					answers.add(cqItem.getItemName());
					scoreSum += cqItem.getTotalScore() != null ? cqItem.getTotalScore() : 0;
					answers.add(cqItem.getTotalScore() != null ? String.valueOf(cqItem.getTotalScore()) : " ");
					response.setItemId(cqItem.getId());
					for (RftSupplierCqItem item : responseList) {
						response.setSupplierId(item.getSupplier().getId());
						if (item.getCqItem().getCqType() == CqType.TEXT || item.getCqItem().getCqType() == CqType.DATE || item.getCqItem().getCqType() == CqType.NUMBER || item.getCqItem().getCqType() == CqType.PARAGRAPH) {
							answers.add(item.getTextAnswers());
							answers.add("");
						} else if (item.getCqItem().getCqType() == CqType.LIST || item.getCqItem().getCqType() == CqType.CHECKBOX) {
							// List<RftSupplierCqOption> listAnswers = item.getListAnswers();
							List<RftSupplierCqOption> listAnswers = rftSupplierCqOptionDao.findSupplierCqOptionsListWithCqByCqId(item.getId());
							String ans = "";
							for (RftSupplierCqOption op : listAnswers) {
								ans += op.getValue() + "\n";
							}
							answers.add(ans);
							answers.add("");
						} else {
							// List<RftSupplierCqOption> listAnswers = item.getListAnswers();
							List<RftSupplierCqOption> listAnswers = rftSupplierCqOptionDao.findSupplierCqOptionsListWithCqByCqId(item.getId());
							for (RftSupplierCqOption op : listAnswers) {
								answers.add(op.getValue());
								answers.add(op.getScoring() != null ? String.valueOf(op.getScoring()) : " ");
							}
						}
					}
					data.add(answers);
				}
				response.setData(data);
				response.setColumns(qualifiedSup);
				List<String> sumOfScoring = supplierCqItemDao.findSumOfScoringForSupplier(cq.getId(), eventId, qualifiedSup);
				sumOfScoring.add(0, scoreSum != null ? String.valueOf(scoreSum) : " ");
				response.setScoring(sumOfScoring);
				response.setEnvelopId(envelopId);
				response.setEventId(eventId);
				returnList.add(response);
			}
		}

		return returnList;
	}

	@Override
	public List<RftSupplierCqItem> getAllRequiredSupplierCqItemByEventIdAndSupplierId(String supplierId, String eventId) {
		LOG.info(" yha aaya abhi");
		List<RftSupplierCqItem> list = supplierCqItemDao.findRequiredSupplierCqItemListByEventId(supplierId, eventId);
		return list;
	}

	@Override
	public List<RftSupplierCqOption> getRequiredOptionValueByCqItemId(String id) {
		return supplierCqItemDao.findRequiredOptionValueByCqItemIdAndSupplierId(id);
	}

	@Override
	public int CountAllMandatorySupplierCqItemByEventId(String supplierId, String eventId) {
		return supplierCqItemDao.CountAllMandatorySupplierCqItemByEventId(supplierId, eventId);
	}

	@Override
	public Boolean checkMandatoryToFinishEvent(String supplierId, String eventId) throws NotAllowedException, Exception {

		List<String> bqNames = rftBqDao.rftBqNamesByEventId(eventId);
		if (CollectionUtil.isNotEmpty(bqNames) && bqNames.size() > 0) {
			List<RftSupplierBq> supplierBqList = rftSupplierBqDao.rftSupplierBqAnswerdByEventIdAndSupplierId(eventId, supplierId);
			if (CollectionUtil.isEmpty(supplierBqList) || (CollectionUtil.isNotEmpty(supplierBqList)) && supplierBqList.size() != bqNames.size()) {
				if (CollectionUtil.isNotEmpty(supplierBqList)) {
					for (RftSupplierBq bq : supplierBqList) {
						bqNames.remove(bq.getName());
					}
				}
			} else {
				// All OK
				bqNames.clear();
			}
		}

		List<String> sorNames = rftSorDao.rftSorNamesByEventId(eventId);
		if (CollectionUtil.isNotEmpty(sorNames) && sorNames.size() > 0) {
			List<RftSupplierSor> supplierSorList = rftSupplierSorDao.rftSupplierSorAnswerdByEventIdAndSupplierId(eventId, supplierId);
			if (CollectionUtil.isEmpty(supplierSorList) || (CollectionUtil.isNotEmpty(supplierSorList)) && supplierSorList.size() != sorNames.size()) {
				if (CollectionUtil.isNotEmpty(supplierSorList)) {
					for (RftSupplierSor bq : supplierSorList) {
						sorNames.remove(bq.getName());
					}
				}
			} else {
				// All OK
				sorNames.clear();
			}
		}

		List<String> cqNames = new ArrayList<>();
		List<RftCq> cqItemList = cqItemDao.rftMandatoryCqNamesbyEventId(eventId);
		if (CollectionUtil.isNotEmpty(cqItemList)) {
			List<RftCq> supplierRequiredCqItemList = supplierCqItemDao.findRequiredSupplierCqCountByEventId(supplierId, eventId);

			for (RftCq cq : cqItemList) {
				boolean found = false;
				for (RftCq supCq : supplierRequiredCqItemList) {
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
		if (CollectionUtil.isNotEmpty(cqNames) && CollectionUtil.isNotEmpty(bqNames) && CollectionUtil.isNotEmpty(sorNames)) {
			LOG.error("Please fill up all mandatory Questionnaire and all Bill Of Quantity");
			throw new NotAllowedException("Please fill up all mandatory Questionnaire , all Bill Of Quantity and Schedule of Rate");
		} else if (CollectionUtil.isNotEmpty(cqNames) && CollectionUtil.isNotEmpty(bqNames)) {
			LOG.error("Please fill up all mandatory Questionnaire and all Bill Of Quantity");
			throw new NotAllowedException("Please fill up all mandatory Questionnaire and all Bill Of Quantity");
		} else if (CollectionUtil.isNotEmpty(bqNames) && CollectionUtil.isNotEmpty(sorNames)) {
			LOG.error("Please fill up all mandatory all Bill Of Quantity and Schedule of Rate");
			throw new NotAllowedException("Please fill up all mandatory Questionnaire and all Bill Of Quantity");
		} else if (CollectionUtil.isNotEmpty(cqNames) && CollectionUtil.isNotEmpty(sorNames)) {
			LOG.error("Please fill up all mandatory all Bill Of Quantity and Schedule of Rate");
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
		}

		long pendingBq = rftSupplierBqDao.findPendingBqAnswerdByEventIdAndSupplierId(eventId, supplierId);
		long pendingCq = rftSupplierCqDao.findPendingCqsByEventIdAndEventCqId(eventId, supplierId);
		long pendingSor = rftSupplierSorDao.findPendingSorsByEventIdAndEventSorId(eventId, supplierId);

		if (pendingBq > 0 && pendingCq > 0 && pendingSor > 0) {
			throw new NotAllowedException("Please complete all Questionnaire, all Bill of Quantity and all schedule of rate");
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
			int rftCqCount = supplierCqItemDao.findRftRequiredCqCountBySupplierId(supplierId, eventId);
			if (rftCqCount > 0) {
				throw new NotAllowedException("Please Attach All required Documents in Questionnaires");
			}
		}

		return true;
	}

	@Override
	@Transactional(readOnly = false)
	public boolean resetAttachement(String itemId, String eventId, String tenantId) {
		boolean removed = supplierCqItemDao.resetAttachment(eventId, itemId);
		if (removed) {
			RftSupplierCqItem item = supplierCqItemDao.findCqByEventIdAndCqName(eventId, itemId);
			if (item != null) {
				RftSupplierCq rftSupplierCq = rftSupplierCqDao.findSupplierCqByEventIdAndSupplierId(tenantId, eventId, item.getCq().getId());
				if (rftSupplierCq != null) {
					rftSupplierCq.setSupplierCqStatus(SupplierCqStatus.DRAFT);
					rftSupplierCqService.updateSupplierCq(rftSupplierCq);
				}
			}
		}
		return removed;

	}

	@Override
	public RftSupplierCqItem findCqByEventIdAndCqName(String eventId, String cqId) {
		return supplierCqItemDao.findCqByEventIdAndCqName(eventId, cqId);
	}

	@Override
	public List<RftSupplierCqItem> getSupplierCqItemsbySupplierIdAndEventId(String eventId, String supplierId) {
		List<RftSupplierCqItem> returnList = new ArrayList<RftSupplierCqItem>();
		List<RftSupplierCqItem> list = supplierCqItemDao.getSupplierCqItemsbySupplierIdAndEventId(eventId, supplierId);
		bulidSupplierCqItemList(returnList, list);
		return returnList;
	}

	@Override
	@Transactional(readOnly = false)
	public void draftUpdateCqItems(List<RftSupplierCqItem> items, String eventId, SupplierCqStatus status, User loggedInUser) {
		saveCqItems(items, eventId, status, loggedInUser);
	}

	@Override
	@Transactional(readOnly = false, noRollbackFor = NotAllowedException.class)
	public void updateCqItems(List<RftSupplierCqItem> items, String eventId, SupplierCqStatus status, User loggedInUser) throws NotAllowedException {
		saveCqItems(items, eventId, status, loggedInUser);
		int count = supplierCqItemDao.findRftRequiredCqCountBySupplierIdAndCqId(loggedInUser.getTenantId(), eventId, items.get(0).getCq().getId());
		if (count > 0) {
			RftSupplierCq rftSupplierCq = rftSupplierCqDao.findSupplierCqByEventIdAndSupplierId(loggedInUser.getTenantId(), eventId, items.get(0).getCq().getId());
			if (rftSupplierCq != null) {
				rftSupplierCq.setSupplierCqStatus(SupplierCqStatus.DRAFT);
				rftSupplierCqService.updateSupplierCq(rftSupplierCq);
			}
			List<String> cqAttachments = supplierCqItemDao.findRequiredCqsBySupplierId(loggedInUser.getTenantId(), eventId, items.get(0).getCq().getId());
			if (CollectionUtil.isNotEmpty(cqAttachments)) {
				String cqItems = org.apache.commons.lang.StringUtils.join(cqAttachments, ",");
				if (StringUtils.checkString(cqItems).length() > 0) {
					throw new NotAllowedException("Please Attach All required Documents in Questionnaires <" + cqItems + ">");
				}
			}
		}
	}

	private void saveCqItems(List<RftSupplierCqItem> items, String eventId, SupplierCqStatus status, User loggedInUser) {
		String cqId = "";
		for (RftSupplierCqItem item : items) {
			List<RftCqOption> options = item.getListOptAnswers();
			RftSupplierCqItem cqItem = supplierCqItemDao.findById(item.getId());
			if (item.getTextAnswers() != null) {
				String textAnswer = item.getTextAnswers().replace('-', '_');
				cqItem.setTextAnswers(textAnswer);
			}
			LOG.info(cqItem.getTextAnswers());
			if (StringUtils.checkString(cqId).length() == 0) {
				cqId = item.getCq().getId();
			}

			/*
			 * if (item.getDateAnswer() != null) { cqItem.setDateAnswer(item.getDateAnswer()); }
			 * LOG.info(cqItem.getDateAnswer());
			 */
			cqItem.setFileData(item.getFileData());
			cqItem.setFileName(item.getFileName());
			cqItem.setCredContentType(item.getCredContentType());
			List<RftSupplierCqOption> supOptions = new ArrayList<RftSupplierCqOption>();
			if (CollectionUtil.isNotEmpty(options)) {
				for (RftCqOption option : options) {
					RftCqOption op = cqOptionDao.findById(option.getId());
					if (op != null) {
						RftSupplierCqOption cqOption = new RftSupplierCqOption();
						cqOption.setCqItem(cqItem);
						cqOption.setOrder(op.getOrder());
						cqOption.setScoring(op.getScoring());
						cqOption.setValue(op.getValue());
						supOptions.add(cqOption);
					}
				}
			}
			cqItem.setListAnswers(supOptions);
			cqItem.setSupplier(SecurityLibrary.getLoggedInUser().getSupplier());
			supplierCqItemDao.update(cqItem);
		}

		RftSupplierCq rftSupplierCq = rftSupplierCqDao.findSupplierCqByEventIdAndSupplierId(SecurityLibrary.getLoggedInUserTenantId(), eventId, cqId);
		if (rftSupplierCq != null) {
			rftSupplierCq.setSupplierCqStatus(status);
			rftSupplierCqService.updateSupplierCq(rftSupplierCq);
		}
	}
}
