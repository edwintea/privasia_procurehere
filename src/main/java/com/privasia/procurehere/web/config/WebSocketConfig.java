/**
 * 
 */
package com.privasia.procurehere.web.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.AbstractWebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;

/**
 * @author Nitin Otageri
 */
@Configuration
@EnableWebSocketMessageBroker
@PropertySource(value = { "classpath:application.properties" })
public class WebSocketConfig extends AbstractWebSocketMessageBrokerConfigurer {

	@Autowired
	private Environment env;
	
	@Override
	public void configureMessageBroker(MessageBrokerRegistry config) {
		config.setApplicationDestinationPrefixes("/app");
		config.enableStompBrokerRelay("/auctionTopic", "/auctionTimeExtension", "/auctionFesibleBid", "/dutchAuctionData", "/auctionSupplierList", "/bidHistorySupplierSide", "/bidHistoryBuyerSide")
		.setRelayHost(env.getRequiredProperty("activemq.host.id"))
		.setRelayPort(61613)
		.setClientLogin("admin")
		.setClientPasscode("admin");
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer#registerStompEndpoints(org.
	 * springframework.web.socket.config.annotation.StompEndpointRegistry)
	 */
	@Override
	public void registerStompEndpoints(StompEndpointRegistry registry) {
		// TODO Auto-generated method stub
		registry.addEndpoint("/auctions").setAllowedOrigins("*").withSockJS();
	}

}
