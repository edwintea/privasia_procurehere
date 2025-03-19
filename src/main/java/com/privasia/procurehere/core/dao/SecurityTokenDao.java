package com.privasia.procurehere.core.dao;

import java.util.Date;

import com.privasia.procurehere.core.entity.SecurityToken;
import com.privasia.procurehere.core.entity.SecurityToken.TokenValidity;
import com.privasia.procurehere.core.entity.User;
import com.privasia.procurehere.core.exceptions.SecurityTokenException;
import com.privasia.procurehere.core.exceptions.SecurityTokenExpiredException;
import com.privasia.procurehere.core.exceptions.SecurityTokenInvalidException;

/**
 * @author Nitin Otageri
 */
public interface SecurityTokenDao extends GenericDao<SecurityToken, String> {

	/**
	 * @param expiryDate
	 * @param tokenUser
	 * @return
	 */
	SecurityToken generateToken(Date expiryDate, User tokenUser);

	/**
	 * @param securityToken
	 * @return
	 * @throws SecurityTokenException
	 * @throws SecurityTokenInvalidException
	 * @throws SecurityTokenExpiredException
	 */
	boolean validateToken(SecurityToken securityToken) throws SecurityTokenException, SecurityTokenExpiredException, SecurityTokenInvalidException;

	/**
	 * @param securityToken
	 * @param userId
	 * @return
	 * @throws SecurityTokenException
	 * @throws SecurityTokenExpiredException
	 * @throws SecurityTokenInvalidException
	 */
	boolean validateUserToken(String securityToken, String userId) throws SecurityTokenException, SecurityTokenExpiredException, SecurityTokenInvalidException, com.privasia.procurehere.core.exceptions.SecurityTokenInvalidException;

	/**
	 * @param tokenValidity
	 * @param tokenUser
	 * @return
	 */
	SecurityToken generateTokenWithValidityForUser(TokenValidity tokenValidity, User tokenUser);

	/**
	 * @param tokenValidity
	 * @return
	 */
	SecurityToken generateTokenWithValidity(TokenValidity tokenValidity);

	/**
	 * @param tokenValidity
	 * @param tokenUser
	 * @return
	 */
	SecurityToken generateTokenWithValidityForNewUser(TokenValidity tokenValidity, User tokenUser);
}
