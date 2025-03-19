package com.privasia.procurehere.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.privasia.procurehere.core.dao.SourcingFormCqDao;
import com.privasia.procurehere.core.dao.SourcingFormCqOptionDao;
import com.privasia.procurehere.core.dao.SourcingFormRequestCqItemDao;
import com.privasia.procurehere.core.dao.SourcingFormRequestDao;
import com.privasia.procurehere.core.entity.CqOption;
import com.privasia.procurehere.core.entity.SourcingFormCqOption;
import com.privasia.procurehere.core.entity.SourcingFormRequest;
import com.privasia.procurehere.core.entity.SourcingFormRequestCqItem;
import com.privasia.procurehere.core.entity.SourcingFormRequestCqOption;
import com.privasia.procurehere.core.entity.SourcingTemplateCq;
import com.privasia.procurehere.core.entity.SourcingTemplateCqItem;
import com.privasia.procurehere.core.enums.CqType;
import com.privasia.procurehere.core.pojo.SourcingFormReqCqItem;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.service.SourcingFormRequestCqItemService;

/**
 * @author pooja
 */
@Service
@Transactional(readOnly = true)
public class SourcingFormRequestCqItemServiceImpl implements SourcingFormRequestCqItemService {

	private static final Logger LOG = LogManager.getLogger(Global.PR_LOG);

	@Autowired
	SourcingFormRequestDao sourcingFormRequestDao;

	@Autowired
	SourcingFormCqDao sourcingFormCqDao;

	@Autowired
	SourcingFormRequestCqItemDao sourcingFormRequestCqItemDao;

	@Autowired
	SourcingFormCqOptionDao sourcingFormCqOptionDao;

	@Override
	public List<SourcingTemplateCq> findCqsByTempId(String templateId) {
		return sourcingFormCqDao.getAllQuestionnarieByOrder(templateId);
	}

	@Transactional(readOnly = false)
	@Override
	public void saveSourcingRequestCq(String cqId, SourcingFormRequest sourcingFormRequest) {
		LOG.info("template id................" + sourcingFormRequest.getSourcingForm().getId());
		List<SourcingTemplateCqItem> sourcingCqItems = sourcingFormRequestCqItemDao.getCqItemsByCqIdAndTempId(cqId, sourcingFormRequest.getSourcingForm().getId());
		LOG.info("Sourcing item from template size........." + sourcingCqItems.size());
		for (SourcingTemplateCqItem cqItem : sourcingCqItems) {
			// sourcingFormRequestCqItemDao.findCqByFormIdAndCqItemId(sourcingFormRequest.getSourcingForm().getId(),
			// cqItem.getId());
			// if (sourcingReqCqItem == null) {
			SourcingFormRequestCqItem sourcingReqCqItem = new SourcingFormRequestCqItem(cqItem);
			sourcingReqCqItem.setSourcingFormRequest(sourcingFormRequest);
			sourcingReqCqItem = sourcingFormRequestCqItemDao.saveOrUpdate(sourcingReqCqItem);
			// }
		}
	}

	@Override
	public List<SourcingFormRequestCqItem> getAllSourcingCqItemByCqId(String cqId, String formId) {
		List<SourcingFormRequestCqItem> returnList = new ArrayList<SourcingFormRequestCqItem>();
		List<SourcingFormRequestCqItem> cqItemList = sourcingFormRequestCqItemDao.findAllCqItemByCqId(cqId, formId);
		LOG.info("Cq Item List Size---------" + cqItemList.size());
		bulidSourcingReqCqItemList(returnList, cqItemList);
		return returnList;
	}

	private void bulidSourcingReqCqItemList(List<SourcingFormRequestCqItem> returnList, List<SourcingFormRequestCqItem> cqItemList) {
		if (CollectionUtil.isNotEmpty(cqItemList)) {
			for (SourcingFormRequestCqItem item : cqItemList) {
				if (item.getCqItem().getCqOptions() != null) {
					for (CqOption op : item.getCqItem().getCqOptions()) {
						LOG.info(" >>>>>>>>>>>>>>>>>>>>>>>> " + op.getValue());
						op.getValue();
					}
				}
				returnList.add(item.createShallowCopy());
			}
		}

	}

	@Override
	public SourcingFormRequestCqItem findCqBySourcingReqIdAndCqItemId(String formId, String itemId) {
		return sourcingFormRequestCqItemDao.findCqByFormIdAndCqItemId(formId, itemId);
	}

	@Override
	@Transactional(readOnly = false)
	public void updateSourcingCqItem(List<SourcingFormRequestCqItem> itemsList) {
		for (SourcingFormRequestCqItem item : itemsList) {
			List<SourcingFormCqOption> options = item.getListOptAnswers();
			SourcingFormRequestCqItem cqItem = sourcingFormRequestCqItemDao.findById(item.getId());
			cqItem.setTextAnswers(item.getTextAnswers());
			cqItem.setFileData(item.getFileData());
			cqItem.setCredContentType(item.getCredContentType());
			cqItem.setFileName(item.getFileName());
			List<SourcingFormRequestCqOption> requestOption = new ArrayList<SourcingFormRequestCqOption>();
			if (CollectionUtil.isNotEmpty(options)) {
				for (SourcingFormCqOption option : options) {
					SourcingFormCqOption op = null;
					
					if(cqItem.getCqItem().getCqType() == CqType.LIST) {
						op = sourcingFormCqOptionDao.findByTemplateCqItemIdAndOptionOrder(cqItem.getCqItem().getId(), Integer.parseInt(option.getId()));
					}else {
						op = sourcingFormCqOptionDao.findById(option.getId());
					}
					
					if (op != null) {
						SourcingFormRequestCqOption reqOp = new SourcingFormRequestCqOption();
						reqOp.setFormCqItemRequest(cqItem);
						reqOp.setOrder(op.getOrder());
						reqOp.setValue(op.getValue());
						requestOption.add(reqOp);
					}
				}
				cqItem.setListAnswers(requestOption);
			}
			cqItem.setTextAnswers(item.getTextAnswers());
			cqItem.setSourcingForm(item.getSourcingForm());
			cqItem.setListAnswers(requestOption);
			cqItem.setSourcingFormRequest(item.getSourcingFormRequest());
			sourcingFormRequestCqItemDao.update(cqItem);
		}

	}

	@Override
	public List<SourcingFormRequestCqOption> getListAnswers(String id) {
		return sourcingFormRequestCqItemDao.getListAnswers(id);

	}

	@Override
	public List<String> getListTextAnswers(String id) {
		LOG.info("id +++++++++++ " + id);
		return sourcingFormRequestCqItemDao.getListTextAnswers(id);

	}

	@Override
	@Transactional(readOnly = false)
	public boolean resetAttachement(String itemId, String formId) {
		return sourcingFormRequestCqItemDao.resetAttachement(itemId, formId);
	}

	@Override
	public List<SourcingTemplateCq> getAllQuestionnarieByOrder(String formId) {
		return sourcingFormCqDao.getAllQuestionnarieByOrder(formId);
	}

	@Override
	public List<SourcingFormCqOption> findCqItemOptionForCqItemId(String searchValue, String cqItemId) {
		List<SourcingFormCqOption> optionList = sourcingFormCqOptionDao.findCqItemOptionForCqItemId(searchValue, cqItemId);
		long count = sourcingFormCqOptionDao.getCountOfAllOptionsForCqItem(cqItemId);

		LOG.info("Count: " + count);
		if (CollectionUtil.isNotEmpty(optionList)) {
			if (count > optionList.size()) {
				SourcingFormCqOption more = new SourcingFormCqOption();
				more.setId("-1");
				more.setValue("Total " + (count) + " options. Continue typing to find match...");
				optionList.add(more);
			}
		}
		return optionList;
	}

	@Override
	public List<SourcingFormCqOption> findCqItemOptionForCqItemId(String cqItemId, SourcingFormReqCqItem itemObj) {
		List<SourcingFormCqOption> optionList = sourcingFormCqOptionDao.findCqItemOptionForCqItemId(cqItemId);
		long count = sourcingFormCqOptionDao.getCountOfAllOptionsForCqItem(cqItemId);
		
		List<SourcingFormCqOption> returnList = new ArrayList<SourcingFormCqOption>();
		for(SourcingFormCqOption dbOp :optionList) {
			boolean found = false;
			for(CqOption op :itemObj.getListOptAnswers()) {
				if(op.getOrder() == dbOp.getOrder()) {
					found = true;
					break;
				}
			}
			if(!found) {
				returnList.add(dbOp);
			}
		}

		LOG.info("Count: " + count);
		if (CollectionUtil.isNotEmpty(optionList)) {
			if (count > optionList.size()) {
				SourcingFormCqOption more = new SourcingFormCqOption();
				more.setId("-1");
				more.setValue("Total " + (count) + " options. Continue typing to find match...");
				returnList.add(more);
			}
		}
		return returnList;
	}

}
