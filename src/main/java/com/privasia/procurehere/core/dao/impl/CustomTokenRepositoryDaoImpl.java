/**
 * 
 */
package com.privasia.procurehere.core.dao.impl;

import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.web.authentication.rememberme.PersistentRememberMeToken;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.privasia.procurehere.core.entity.PersistentLogin;

/**
 * @author Nitin Otageri
 *
 */
@Repository("tokenReppositoryDao")
@Transactional
public class CustomTokenRepositoryDaoImpl extends GenericDaoImpl<PersistentLogin, String> implements
		PersistentTokenRepository {

	static final Logger logger = LogManager.getLogger(CustomTokenRepositoryDaoImpl.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.security.web.authentication.rememberme.
	 * PersistentTokenRepository
	 * #createNewToken(org.springframework.security.web.
	 * authentication.rememberme.PersistentRememberMeToken)
	 */
	@Override
	public void createNewToken(PersistentRememberMeToken token) {
		logger.info("Creating Token for user : " + token.getUsername());
		PersistentLogin persistentLogin = new PersistentLogin();
		persistentLogin.setUsername(token.getUsername());
		persistentLogin.setSeries(token.getSeries());
		persistentLogin.setToken(token.getTokenValue());
		persistentLogin.setLastUsed(token.getDate());
		save(persistentLogin);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.security.web.authentication.rememberme.
	 * PersistentTokenRepository#updateToken(java.lang.String, java.lang.String,
	 * java.util.Date)
	 */
	@Override
	public void updateToken(String series, String tokenValue, Date lastUsed) {
		logger.info("Updating Token for seriesId : " + series);
		PersistentLogin persistentLogin = findByProperty("series", series);
		persistentLogin.setToken(tokenValue);
		persistentLogin.setLastUsed(lastUsed);
		update(persistentLogin);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.security.web.authentication.rememberme.
	 * PersistentTokenRepository#getTokenForSeries(java.lang.String)
	 */
	@Override
	public PersistentRememberMeToken getTokenForSeries(String seriesId) {
		logger.info("Fetch Token if any for seriesId : {}" + seriesId);
		try {
			PersistentLogin persistentLogin = findByProperty("series", seriesId);
			return new PersistentRememberMeToken(persistentLogin.getUsername(), persistentLogin.getSeries(),
					persistentLogin.getToken(), persistentLogin.getLastUsed());
		} catch (Exception e) {
			logger.info("Token not found...");
			return null;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.security.web.authentication.rememberme.
	 * PersistentTokenRepository#removeUserTokens(java.lang.String)
	 */
	@Override
	public void removeUserTokens(String username) {
		logger.info("Removing Token if any for user : " + username);
		PersistentLogin persistentLogin = findByProperty("username", username);
		if (persistentLogin != null) {
			logger.info("rememberMe was selected");
			delete(persistentLogin);
		}
	}

}
