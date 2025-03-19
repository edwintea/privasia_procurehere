package com.privasia.procurehere.service;

import java.util.List;

import com.privasia.procurehere.core.entity.ComposeMessage;

public interface ComposeMessageService {

	
	public void saveComposedMessage(ComposeMessage composeMessage);
	
	public void deleteComposedMessage(ComposeMessage composeMessage);
	
	List<ComposeMessage> FindComposedInboundMessage(ComposeMessage composeMessage);
	
	List<ComposeMessage> FindComposedOutboundMessage(ComposeMessage composeMessage);
	
}
