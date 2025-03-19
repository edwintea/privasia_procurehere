package com.privasia.procurehere.core.dao.impl;

import java.util.Date;

import javax.annotation.Resource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.privasia.procurehere.core.dao.SecurityTokenDao;
import com.privasia.procurehere.core.entity.SecurityToken;
import com.privasia.procurehere.core.entity.SecurityToken.TokenValidity;
import com.privasia.procurehere.core.entity.User;
import com.privasia.procurehere.core.exceptions.SecurityTokenException;
import com.privasia.procurehere.core.exceptions.SecurityTokenExpiredException;
import com.privasia.procurehere.core.exceptions.SecurityTokenInvalidException;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.StringUtils;

/**
 * @author Nitin Otageri
 */
@Repository
public class SecurityTokenDaoImpl extends GenericDaoImpl<SecurityToken, String> implements SecurityTokenDao {

	@SuppressWarnings("unused")
	private static final Logger LOG = LogManager.getLogger(SecurityTokenDaoImpl.class);

	@Resource
	MessageSource messageSource;

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public SecurityToken generateToken(Date expiryDate, User tokenUser) {
		return save(new SecurityToken(expiryDate, tokenUser));
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public SecurityToken generateTokenWithValidityForUser(TokenValidity tokenValidity, User tokenUser) {
		SecurityToken token = new SecurityToken(tokenValidity, tokenUser);
		return save(token);
	}
	
	@Override
	@Transactional(readOnly = false)
	public SecurityToken generateTokenWithValidityForNewUser(TokenValidity tokenValidity, User tokenUser) {
		SecurityToken token = new SecurityToken(tokenValidity, tokenUser);
		return save(token);
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public SecurityToken generateTokenWithValidity(TokenValidity tokenValidity) {
		return save(new SecurityToken(tokenValidity));
	}

	@Override
	public boolean validateToken(SecurityToken securityToken) throws SecurityTokenException, SecurityTokenExpiredException, SecurityTokenInvalidException {
		return validateUserToken((securityToken.getUser() != null ? securityToken.getUser().getId() : null), securityToken.getToken());
	}

	@Override
	public void delete(SecurityToken securityToken) {
		securityToken.setDeleted(Boolean.TRUE);
		update(securityToken);
	}

	@Override
	public boolean validateUserToken(String userId, String securityToken) throws SecurityTokenException, SecurityTokenExpiredException, SecurityTokenInvalidException {
		SecurityToken token = findById(securityToken);
		validate(userId, token);
		return true;
	}

	/**
	 * @param userId
	 * @param token
	 * @throws SecurityTokenInvalidException
	 * @throws SecurityTokenExpiredException
	 */
	private void validate(String userId, SecurityToken token) throws SecurityTokenInvalidException, SecurityTokenExpiredException {
		if (token == null) {
			throw new SecurityTokenInvalidException("Invalid Security Token.");
		}
		if (token.getUsedDate() != null) {
			throw new SecurityTokenInvalidException("Invalid Security Token. Token is already used once.");
		}
		if (StringUtils.checkString(userId).length() > 0) {
			if (token.getUser() == null) {
				throw new SecurityTokenInvalidException("Invalid Security Token. Token not valid for user.");
			} else if (!token.getUser().getId().equals(userId)) {
				throw new SecurityTokenInvalidException("Invalid Security Token. Token not valid for user.");
			}
		}

		if (Boolean.TRUE == token.getDeleted()) {
			throw new SecurityTokenInvalidException("Invalid Security Token. Token not valid anymore.");
		}

		if (token.getExpiryDate() != null) {
			Date now = new Date();
			if (now.after(token.getExpiryDate())) {
				throw new SecurityTokenExpiredException("Invalid Security Token.", token.getExpiryDate());
			}
		}
	}
}
