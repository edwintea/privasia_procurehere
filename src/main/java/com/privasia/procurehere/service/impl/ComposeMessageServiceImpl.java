package com.privasia.procurehere.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.privasia.procurehere.core.dao.ComposeMessageDao;
import com.privasia.procurehere.core.dao.UserDao;
import com.privasia.procurehere.core.entity.ComposeMessage;
import com.privasia.procurehere.service.ComposeMessageService;

@Service
@Transactional(readOnly=true)
public class ComposeMessageServiceImpl implements ComposeMessageService  {

	@Autowired
	ComposeMessageDao composeMessageDao;
	
	@Autowired
	UserDao userDao;
	
	
	@Override
	@Transactional(readOnly=false)
	public void saveComposedMessage(ComposeMessage composeMessage) {
		composeMessageDao.save(composeMessage);
	}

	
	@Override
	@Transactional(readOnly=false)
	public void deleteComposedMessage(ComposeMessage composeMessage) {
		composeMessageDao.delete(composeMessage);
	}

	@Override
	public List<ComposeMessage> FindComposedInboundMessage(ComposeMessage composeMessage) {
		
		return null;
	}

	@Override
	public List<ComposeMessage> FindComposedOutboundMessage(ComposeMessage composeMessage) {
		// TODO Auto-generated method stub
		return null;
	}

}
