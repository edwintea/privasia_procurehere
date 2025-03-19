package com.privasia.procurehere.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.privasia.procurehere.core.dao.RfaCqDao;
import com.privasia.procurehere.core.dao.RfaCqItemDao;
import com.privasia.procurehere.core.dao.RfaCqOptionDao;
import com.privasia.procurehere.core.dao.RfaEnvelopDao;
import com.privasia.procurehere.core.dao.RfaEventDao;
import com.privasia.procurehere.core.dao.RfaEventSupplierDao;
import com.privasia.procurehere.core.dao.RfaSupplierCqDao;
import com.privasia.procurehere.core.dao.RfaSupplierCqItemDao;
import com.privasia.procurehere.core.dao.RfaSupplierCqOptionDao;
import com.privasia.procurehere.core.entity.CqOption;
import com.privasia.procurehere.core.entity.RfaCq;
import com.privasia.procurehere.core.entity.RfaCqEvaluationComments;
import com.privasia.procurehere.core.entity.RfaCqItem;
import com.privasia.procurehere.core.entity.RfaCqOption;
import com.privasia.procurehere.core.entity.RfaEnvelop;
import com.privasia.procurehere.core.entity.RfaEvent;
import com.privasia.procurehere.core.entity.RfaSupplierCq;
import com.privasia.procurehere.core.entity.RfaSupplierCqItem;
import com.privasia.procurehere.core.entity.RfaSupplierCqOption;
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
import com.privasia.procurehere.service.RfaCqService;
import com.privasia.procurehere.service.RfaEventService;
import com.privasia.procurehere.service.RfaSupplierCqItemService;
import com.privasia.procurehere.service.RfaSupplierCqService;
import com.privasia.procurehere.service.supplier.SupplierService;

/**
 * @author Vipul
 */

@Service
@Transactional(readOnly = true)
public class RfaSupplierCqItemServiceImpl implements RfaSupplierCqItemService {

	private static final Logger LOG = LogManager.getLogger(Global.SUPPLIER_LOG);

	@Autowired
	RfaSupplierCqItemDao supplierCqItemDao;

	@Autowired
	RfaCqDao cqDao;

	@Autowired
	RfaEventDao eventDao;

	@Autowired
	RfaCqItemDao cqItemDao;

	@Autowired
	SupplierService supplierService;

	@Autowired
	RfaEventSupplierDao eventSupplierDao;

	@Autowired
	RfaEnvelopDao envelopDao;

	@Autowired
	RfaCqOptionDao cqOptionDao;

	@Autowired
	RfaCqService rfaCqService;

	@Autowired
	RfaEventService rfaEventService;

	@Autowired
	EventAuditService eventAuditService;

	@Autowired
	RfaSupplierCqOptionDao rfaSupplierCqOptionDao;

	@Autowired
	RfaSupplierCqDao rfaSupplierCqDao;

	@Autowired
	RfaSupplierCqService rfaSupplierCqService;

	@Override
	public EventEvaluationPojo getEvaluationData(String eventId, String cqId) {
		EventEvaluationPojo response = new EventEvaluationPojo();
		List<String> columns = new ArrayList<String>();
		List<Supplier> suppliers = eventDao.getEventSuppliers(eventId);
		for (Supplier supplier : suppliers) {
			columns.add(supplier.getCompanyName());
		}

		List<List<String>> data = new ArrayList<List<String>>();
		List<RfaCqItem> cqItems = cqItemDao.findCqItemsForCq(cqId);
		for (RfaCqItem cq : cqItems) {
			List<String> answers = new ArrayList<String>();
			List<RfaSupplierCqItem> responseList = supplierCqItemDao.findSupplierCqItemsByCqItemIdAndEventId(cq.getId(), eventId);
			for (RfaSupplierCqItem item : responseList) {
				if (item.getCqItem().getCqType() == CqType.TEXT) {
					answers.add(item.getTextAnswers());
				} else {
					String str = "";
					List<RfaSupplierCqOption> listAnswers = item.getListAnswers();
					for (RfaSupplierCqOption op : listAnswers) {
						str += op.getValue() + ",";
					}
					if (str.length() > 0) {
						str = str.substring(0, str.length() - 1);
					}
					answers.add(str);
				}
			}
			data.add(answers);
		}

		return response;
	}

	@Override
	public List<RfaSupplierCqItem> getAllSupplierCqItemByCqId(String cqId, String supplierId) {
		List<RfaSupplierCqItem> returnList = new ArrayList<RfaSupplierCqItem>();
		List<RfaSupplierCqItem> list = supplierCqItemDao.findSupplierCqItemListByCqId(cqId, supplierId);
		bulidSupplierCqItemList(returnList, list);
		LOG.info("ITEM LIST SIZE : " + list.size());
		return returnList;
	}

	private void bulidSupplierCqItemList(List<RfaSupplierCqItem> returnList, List<RfaSupplierCqItem> list) {
		LOG.info("BUILDING CQ ITEM LIST :: " + list.size());
		if (CollectionUtil.isNotEmpty(list)) {
			for (RfaSupplierCqItem item : list) {
				if (item.getCqItem().getCqOptions() != null) {
					for (CqOption op : item.getCqItem().getCqOptions()) {
						LOG.info(" >>>>>>>>>>>>>>>>>>>>>>>> " + op.getValue());
					}
				}
				returnList.add(item.createShallowCopy());
			}
		}
	}

	@Override
	@Transactional(readOnly = false)
	public void saveSupplierEventCq(String cqId, String eventId, String supplierId) {
		List<RfaCqItem> cqItems = supplierCqItemDao.getCqItemsbyId(cqId);
		for (RfaCqItem item : cqItems) {
			LOG.info("ITEM : " + item.toLogString() + "  Options " + item.getCqOptions().size());
			RfaCq cq = item.getCq();
			RfaSupplierCqItem supplierCq = supplierCqItemDao.findCqByEventIdAndCqName(eventId, item.getId());
			LOG.info("RftSupplierCq 1:" + supplierCq);
			if (supplierCq == null) {
				supplierCq = new RfaSupplierCqItem(item);
				supplierCq.setSupplier(new Supplier(supplierId));
				supplierCq = supplierCqItemDao.saveOrUpdateWithFlush(supplierCq);
			}

		}

	}

	@Override
	@Transactional(readOnly = false)
	public void saveSupplierCq(String cqId, String eventId, String supplierId) {
		LOG.info("Saving Supplier Cq.............:");
		RfaSupplierCq suppliercq = rfaSupplierCqDao.findCqByEventIdAndCqId(eventId, cqId, SecurityLibrary.getLoggedInUserTenantId());
		if (suppliercq == null) {
			suppliercq = new RfaSupplierCq();
			RfaEvent rfxEvent = eventDao.findByEventId(eventId);
			suppliercq.setEvent(rfxEvent);
			suppliercq.setSupplier(SecurityLibrary.getLoggedInUser().getSupplier());
			RfaCq rfaCq = cqDao.findById(cqId);
			suppliercq.setCq(rfaCq);
			suppliercq = rfaSupplierCqDao.saveOrUpdate(suppliercq);
		}
	}

	@Override
	@Transactional(readOnly = false)
	public void updateCqItems(List<RfaSupplierCqItem> items) {
		for (RfaSupplierCqItem item : items) {
			List<RfaCqOption> options = item.getListOptAnswers();
			LOG.info("ITEM ID : " + item.getId());
			RfaSupplierCqItem cqItem = supplierCqItemDao.findById(item.getId());
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
			List<RfaSupplierCqOption> supOptions = new ArrayList<RfaSupplierCqOption>();
			if (CollectionUtil.isNotEmpty(options)) {
				for (RfaCqOption op : options) {
					RfaCqOption option = cqOptionDao.findById(op.getId());
					RfaSupplierCqOption cqOption = new RfaSupplierCqOption();
					cqOption.setCqItem(cqItem);
					cqOption.setOrder(option.getOrder());
					cqOption.setScoring(option.getScoring());
					cqOption.setValue(option.getValue());
					supOptions.add(cqOption);
				}
			}
			cqItem.setListAnswers(supOptions);
			cqItem.setSupplier(SecurityLibrary.getLoggedInUser().getSupplier());
			supplierCqItemDao.update(cqItem);
		}
	}

	@Override
	public List<EventEvaluationPojo> getCqEvaluationData(String eventId, String envelopId, List<Supplier> selectedSuppliers, User logedUser) {
		List<EventEvaluationPojo> returnList = new ArrayList<>();
		List<Supplier> suppliers = null;

		if (CollectionUtil.isEmpty(selectedSuppliers)) {
			suppliers = eventSupplierDao.getEventSuppliersForEvaluation(eventId);
		} else {
			suppliers = eventSupplierDao.getEventSuppliersForEvaluation(eventId, selectedSuppliers);
		}

		List<String> columns = new ArrayList<String>();
		if (CollectionUtil.isNotEmpty(suppliers)) {
			for (Supplier supplier : suppliers) {
				columns.add(supplier.getCompanyName());
			}
			List<String> envelopIds = new ArrayList<String>();
			envelopIds.add(envelopId);
			// List<String> cqIds = envelopDao.getCqsByEnvelopId(envelopIds);
			List<CqPojo> cqPojo = envelopDao.getCqsIdListByEnvelopIdByOrder(envelopIds);

			RfaEnvelop envelop = envelopDao.findById(envelopId);
			if (envelop.getRfxEvent() != null) {
				if (envelop.getRfxEvent().getViewSupplerName() != null && !envelop.getRfxEvent().getViewSupplerName() && !envelop.getRfxEvent().getDisableMasking()) {
					for (Supplier supplier : suppliers) {
						supplier.setCompanyName(MaskUtils.maskName(envelop.getPreFix(), supplier.getId(), envelop.getId()));
					}

				}
			}

			// List<String> bqIds = envelopDao.getBqsByEnvelopId(envelopIds);

			for (CqPojo cqObj : cqPojo) {
				EventEvaluationPojo response = new EventEvaluationPojo();
				List<List<String>> data = new ArrayList<List<String>>();
				List<RfaCqItem> cqItems = cqItemDao.findCqItemsForCq(cqObj.getId());
				for (RfaCqItem cqItem : cqItems) {
					response.setName(cqObj.getName());

					List<String> answers = new ArrayList<String>();
					List<RfaSupplierCqItem> responseList = supplierCqItemDao.findSupplierCqItemsByCqItemIdAndEventId(cqItem.getId(), eventId, suppliers);
					answers.add(cqItem.getLevel() + "." + cqItem.getOrder());
					boolean leadEvalComment = false;
					String comment = rfaCqService.getLeadEvaluatorComment(cqItem.getId());
					if (StringUtils.checkString(comment).length() > 0) {
						leadEvalComment = true;
					}
					answers.add(cqItem.getId() + "" + (leadEvalComment ? 1 : 0) + "-" + cqItem.getItemName());
					// answers.add(cqItem.getId() +"-"+cqItem.getItemName());
					answers.add(cqItem.getId());
					response.setItemId(cqItem.getId());

					int supplierIndex = 0;
					for (RfaSupplierCqItem item : responseList) {
						while (!suppliers.get(supplierIndex).getId().equals(item.getSupplier().getId())) {
							// this supplier has not supplied response
							response.setSupplierId(suppliers.get(supplierIndex).getId());
							// No response by supplier
							answers.add(false + "-" + suppliers.get(supplierIndex).getId() + "-dummy-" + "");
							supplierIndex++;
							if (supplierIndex == suppliers.size()) {
								break;
							}
						}

						boolean commentsExist = false;
						String supplierId = "";
						String itemId = "";
						response.setSupplierId(item.getSupplier().getId());
						if (item.getCqItem().getCqType() == CqType.TEXT || item.getCqItem().getCqType() == CqType.DATE || item.getCqItem().getCqType() == CqType.NUMBER || item.getCqItem().getCqType() == CqType.PARAGRAPH) {
							if (item.getCqItem().getEvaluationComments() != null && item.getCqItem().getEvaluationComments().size() > 0) {
								for (RfaCqEvaluationComments com : item.getCqItem().getEvaluationComments()) {
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
							List<RfaSupplierCqOption> listAnswers = item.getListAnswers();
							for (RfaSupplierCqOption op : listAnswers) {
								commentsExist = false;
								if (op.getCqItem().getCqItem().getEvaluationComments() != null && op.getCqItem().getCqItem().getEvaluationComments().size() > 0) {
									for (RfaCqEvaluationComments com : op.getCqItem().getCqItem().getEvaluationComments()) {
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
							if (str.length() > 0) {
								str = str.substring(0, str.length() - 1);
							}
							answers.add(commentsExist + "-" + supplierId + "-" + itemId + "-" + str);
						}
						supplierIndex++;
					}

					while (supplierIndex < suppliers.size()) {
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
				response.setScoring(supplierCqItemDao.findSumOfScoringForSupplier(cqObj.getId(), eventId, suppliers));
				response.setEnvelopId(envelopId);
				response.setEventId(eventId);
				returnList.add(response);
			}
		}

		return returnList;
	}

	@Override

	public List<EventEvaluationPojo> getEvaluationDataForCqComparison(String eventId, String envelopId) {
		List<EventEvaluationPojo> returnList = new ArrayList<>();
		List<Supplier> qualifiedSupDb = eventSupplierDao.getEventQualifiedSuppliersForEvaluation(eventId);
		List<Supplier> qualifiedSup = new ArrayList<Supplier>();
		RfaEnvelop envolpe = null;
		RfaEvent event = rfaEventService.getPlainEventById(eventId);

		// Masking will be auto flushed by Hibernate due to dirty checking.
		// This is happening because Dumbo changed the transaction type to readOnly=false for Audit.
		if (event.getViewSupplerName() != null && !event.getViewSupplerName() && !event.getDisableMasking()) {
			envolpe = envelopDao.findById(envelopId);

			for (Supplier supplier : qualifiedSupDb) {
				Supplier supplierCopy = supplier.createShallowCopy();
				supplierCopy.setCompanyName(MaskUtils.maskName(envolpe.getPreFix(), supplier.getId(), envolpe.getId()));
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
				List<RfaCqItem> cqItems = cqItemDao.findCqItemsForCq(cq.getId());
				Integer scoreSum = 0;
				for (RfaCqItem cqItem : cqItems) {
					response.setName(cqItem.getCq().getName());
					response.setTotalScore(cqItem.getTotalScore());
					List<String> answers = new ArrayList<String>();
					// List<RfaSupplierCqItem> responseList =
					// supplierCqItemDao.findSupplierCqItemsByCqItemIdAndEventId(cqItem.getId(), eventId, qualifiedSup);
					List<RfaSupplierCqItem> responseList = supplierCqItemDao.findSupplierCqItemsByCqItemIdAndEventIdNew(cqItem.getId(), eventId, qualifiedSup);
					answers.add(cqItem.getLevel() + "." + cqItem.getOrder());
					answers.add(cqItem.getItemName());
					scoreSum += cqItem.getTotalScore() != null ? cqItem.getTotalScore() : 0;
					answers.add(cqItem.getTotalScore() != null ? String.valueOf(cqItem.getTotalScore()) : " ");
					response.setItemId(cqItem.getId());
					for (RfaSupplierCqItem item : responseList) {
						response.setSupplierId(item.getSupplier().getId());
						if (item.getCqItem().getCqType() == CqType.TEXT || item.getCqItem().getCqType() == CqType.DATE || item.getCqItem().getCqType() == CqType.NUMBER || item.getCqItem().getCqType() == CqType.PARAGRAPH) {
							answers.add(item.getTextAnswers());
							answers.add("");
						} else if (item.getCqItem().getCqType() == CqType.LIST || item.getCqItem().getCqType() == CqType.CHECKBOX) {
							List<RfaSupplierCqOption> listAnswers = rfaSupplierCqOptionDao.findSupplierCqOptionsListWithCqByCqId(item.getId());
							// List<RfaSupplierCqOption> listAnswers = item.getListAnswers();
							String ans = "";
							for (RfaSupplierCqOption op : listAnswers) {
								ans += op.getValue() + "\n";
							}
							answers.add(ans);
							answers.add("");
						} else {
							// List<RfaSupplierCqOption> listAnswers = item.getListAnswers();
							List<RfaSupplierCqOption> listAnswers = rfaSupplierCqOptionDao.findSupplierCqOptionsListWithCqByCqId(item.getId());
							for (RfaSupplierCqOption op : listAnswers) {
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
	public List<RfaSupplierCqItem> getAllRequiredSupplierCqItemByEventIdAndSupplierId(String supplierId, String eventId) {
		List<RfaSupplierCqItem> list = supplierCqItemDao.findRequiredSupplierCqItemListByEventId(supplierId, eventId);
		return list;
	}

	@Override
	public List<RfaSupplierCqOption> getRequiredOptionValueByCqItemId(String id) {
		List<RfaSupplierCqOption> list = supplierCqItemDao.findRequiredOptionValueByCqItemIdAndSupplierId(id);
		return list;
	}

	@Override
	@Transactional(readOnly = false)
	public boolean resetAttachement(String itemId, String eventId, String tenantId) {
		boolean removed = supplierCqItemDao.resetAttachment(eventId, itemId);
		if (removed) {
			RfaSupplierCqItem item = supplierCqItemDao.findCqByEventIdAndCqName(eventId, itemId);
			if (item != null ) {
				RfaSupplierCq rfaSupplierCq = rfaSupplierCqDao.findSupplierCqByEventIdAndSupplierId(tenantId, eventId, item.getCq().getId());
				if (rfaSupplierCq != null) {
					rfaSupplierCq.setSupplierCqStatus(SupplierCqStatus.DRAFT);
					rfaSupplierCqService.updateSupplierCq(rfaSupplierCq);
				}
			}
		}
		return removed;
	}

	@Override
	public RfaSupplierCqItem findCqByEventIdAndCqName(String eventId, String cqId) {
		return supplierCqItemDao.findCqByEventIdAndCqName(eventId, cqId);
	}

	@Override
	public List<RfaSupplierCqItem> getSupplierCqItemsbySupplierIdAndEventId(String eventId, String supplierId) {
		List<RfaSupplierCqItem> returnList = new ArrayList<RfaSupplierCqItem>();
		List<RfaSupplierCqItem> list = supplierCqItemDao.getSupplierCqItemsbySupplierIdAndEventId(eventId, supplierId);
		bulidSupplierCqItemList(returnList, list);
		return returnList;
	}

	@Override
	@Transactional(readOnly = false)
	public void draftUpdateCqItems(List<RfaSupplierCqItem> items, String eventId, SupplierCqStatus status, User loggedInUser) {
		saveCqItems(items, eventId, status, loggedInUser);
	}

	@Override
	@Transactional(readOnly = false, noRollbackFor = NotAllowedException.class)
	public void updateCqItems(List<RfaSupplierCqItem> items, String eventId, SupplierCqStatus status, User loggedInUser) throws NotAllowedException {
		saveCqItems(items, eventId, status, loggedInUser);
		int count = supplierCqItemDao.findRfaRequiredCqCountBySupplierIdAndCqId(loggedInUser.getTenantId(), eventId, items.get(0).getCq().getId());
		if (count > 0) {
			RfaSupplierCq rfaSupplierCq = rfaSupplierCqDao.findSupplierCqByEventIdAndSupplierId(loggedInUser.getTenantId(), eventId, items.get(0).getCq().getId());
			if (rfaSupplierCq != null) {
				rfaSupplierCq.setSupplierCqStatus(SupplierCqStatus.DRAFT);
				rfaSupplierCqService.updateSupplierCq(rfaSupplierCq);
			}
			List<String> cqAttachments = supplierCqItemDao.findRequiredCqsBySupplierId(loggedInUser.getTenantId(), eventId, items.get(0).getCq().getId());
			if (CollectionUtil.isNotEmpty(cqAttachments)) {
				String cqItems = org.apache.commons.lang.StringUtils.join(cqAttachments, ",");
				if (StringUtils.checkString(cqItems).length() > 0) {
					throw new NotAllowedException("Please Attach All required Documents in Questionnaires <" + cqItems + ">");
				}
			}

//			throw new NotAllowedException("Please Attach All required Documents in Questionnaires");
		}
	}

	private void saveCqItems(List<RfaSupplierCqItem> items, String eventId, SupplierCqStatus status, User loggedInUser) {
		String cqId = "";
		for (RfaSupplierCqItem item : items) {
			List<RfaCqOption> options = item.getListOptAnswers();
			LOG.info("ITEM ID : " + item.getId());
			RfaSupplierCqItem cqItem = supplierCqItemDao.findById(item.getId());
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
			List<RfaSupplierCqOption> supOptions = new ArrayList<RfaSupplierCqOption>();
			if (CollectionUtil.isNotEmpty(options)) {
				for (RfaCqOption op : options) {
					RfaCqOption option = cqOptionDao.findById(op.getId());
					RfaSupplierCqOption cqOption = new RfaSupplierCqOption();
					cqOption.setCqItem(cqItem);
					cqOption.setOrder(option.getOrder());
					cqOption.setScoring(option.getScoring());
					cqOption.setValue(option.getValue());
					supOptions.add(cqOption);
				}
			}
			cqItem.setListAnswers(supOptions);
			cqItem.setSupplier(loggedInUser.getSupplier());
			supplierCqItemDao.update(cqItem);
		}

		RfaSupplierCq rfaSupplierCq = rfaSupplierCqDao.findSupplierCqByEventIdAndSupplierId(loggedInUser.getTenantId(), eventId, cqId);
		if (rfaSupplierCq != null) {
			rfaSupplierCq.setSupplierCqStatus(status);
			rfaSupplierCqService.updateSupplierCq(rfaSupplierCq);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public int findRfaRequiredCqCountBySupplierId(String supplierId, String eventId) {
		return supplierCqItemDao.findRfaRequiredCqCountBySupplierId(supplierId, eventId);
	}
}
