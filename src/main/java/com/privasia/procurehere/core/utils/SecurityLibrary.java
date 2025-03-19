package com.privasia.procurehere.core.utils;

import java.util.Collection;
import java.util.Locale;
import java.util.Set;
import java.util.TreeSet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import com.privasia.procurehere.core.entity.User;
import com.privasia.procurehere.core.exceptions.SecurityRuntimeException;
import com.privasia.procurehere.core.pojo.AuthenticatedUser;

/**
 * @author Nitin Otageri
 *         <p>
 *         Taglib to combine the Spring-Security Project with Facelets <br />
 *         This is the class responsible holding the logic for making the tags work. <br />
 *         The specified <code>public static</code> methods are also defined in the spring-security.taglib.xml in the
 *         META-INF folder to enable them for usage as expression-language element. <br />
 *         <br />
 *         e.g.<code><br />
 * &lt;ui:component rendered='#{sec:ifAllGranted(&quot;ROLE_USER&quot;)'&gt; blablabal &lt;/ui:component&gt;
 * 
 * &lt;sec:ifAnyGranted roles ="ROLE_ABCD,ROLE_XYZ"&gt;
 * &lt;/sec:ifAnyGranted&gt;
 * </code>
 */
public class SecurityLibrary {

	static Logger log = LogManager.getLogger(SecurityLibrary.class);

	private static Set<String> parseAuthorities(String grantedRoles) {
		Set<String> parsedAuthorities = new TreeSet<String>();
		if (grantedRoles == null || grantedRoles.isEmpty()) {
			return parsedAuthorities;
		}

		String[] parsedAuthoritiesArr;
		if (grantedRoles.contains(",")) {
			parsedAuthoritiesArr = grantedRoles.split(",");
		} else {
			parsedAuthoritiesArr = new String[] { grantedRoles };
		}

		// adding authorities to set (could pssible be done better!)
		for (String auth : parsedAuthoritiesArr)
			parsedAuthorities.add(auth.trim());
		return parsedAuthorities;
	}

	@SuppressWarnings("unchecked")
	private static GrantedAuthority[] getUserAuthorities() {
		if (SecurityContextHolder.getContext() == null) {
			log.error("security context is empty, this seems to be a bug/misconfiguration!");
			return new GrantedAuthority[0];
		}
		Authentication currentUser = SecurityContextHolder.getContext().getAuthentication();
		if (currentUser == null)
			return new GrantedAuthority[0];

		Collection<GrantedAuthority> authorities = (Collection<GrantedAuthority>) currentUser.getAuthorities();
		if (authorities == null)
			return new GrantedAuthority[0];

		return authorities.toArray(new GrantedAuthority[] {});
	}

	/**
	 * Method that checks if the user holds <b>any</b> of the given roles. Returns
	 * <code>true, when the first match is found, <code>false</code> if no match is found and also <code>false</code> if
	 * no roles are given
	 * 
	 * @param grantedRoles a comma seperated list of roles
	 * @return true if any of the given roles are granted to the current user, false otherwise
	 */
	public static boolean ifAnyGranted(final String grantedRoles) {
		Set<String> parsedAuthorities = parseAuthorities(grantedRoles);
		if (parsedAuthorities.isEmpty())
			return false;

		GrantedAuthority[] authorities = getUserAuthorities();

		for (GrantedAuthority authority : authorities) {
			if (parsedAuthorities.contains(authority.getAuthority()))
				return true;
		}
		return false;
	}

	/**
	 * Method that checks if the user holds <b>all</b> of the given roles. Returns <code>true</code>, iff the user holds
	 * all roles, <code>false</code> if no roles are given or the first non-matching role is found
	 * 
	 * @param requiredRoles a comma seperated list of roles
	 * @return true if all of the given roles are granted to the current user, false otherwise or if no roles are
	 *         specified at all.
	 */
	public static boolean ifAllGranted(final String requiredRoles) {
		// parse required roles into list
		Set<String> requiredAuthorities = parseAuthorities(requiredRoles);
		if (requiredAuthorities.isEmpty())
			return false;

		// get granted roles
		GrantedAuthority[] authoritiesArray = getUserAuthorities();

		Set<String> grantedAuthorities = new TreeSet<String>();
		for (GrantedAuthority authority : authoritiesArray) {
			grantedAuthorities.add(authority.getAuthority());
		}

		// iterate over required roles,
		for (String requiredAuthority : requiredAuthorities) {
			// check if required role is inside granted roles
			// if not, return false
			if (!grantedAuthorities.contains(requiredAuthority)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Method that checks if <b>none</b> of the given roles is hold by the user. Returns <code>true</code> if no roles
	 * are given, or none of the given roles match the users roles. Returns <code>false</code> on the first matching
	 * role.
	 * 
	 * @param notGrantedRoles a comma seperated list of roles
	 * @return true if none of the given roles is granted to the current user, false otherwise
	 */
	public static boolean ifNotGranted(final String notGrantedRoles) {
		Set<String> parsedAuthorities = parseAuthorities(notGrantedRoles);
		if (parsedAuthorities.isEmpty())
			return true;

		GrantedAuthority[] authorities = getUserAuthorities();

		for (GrantedAuthority authority : authorities) {
			if (parsedAuthorities.contains(authority.getAuthority()))
				return false;
		}
		return true;
	}

	/**
	 * This method returns the login id of the logged-in user through spring security {@link SecurityContextHolder}
	 * 
	 * @return the Login Id of the logged in user or an empty string if logged-in user could not be identified.
	 */
	public static String getLoggedInUserLoginId() {
		if (SecurityContextHolder.getContext() == null) {
			log.error("security context is empty, this seems to be a bug/misconfiguration!");
			return "";
		}
		Authentication currentUser = SecurityContextHolder.getContext().getAuthentication();
		if (currentUser == null)
			return "";

		if (currentUser instanceof UsernamePasswordAuthenticationToken) {
			AuthenticatedUser user = (AuthenticatedUser) currentUser.getPrincipal();
			if (user != null) {
				log.debug("Logged In User : " + currentUser.getName() + "   -> " + user.getLoginId());
				return user.getLoginId();
			} else
				return "";
		} else
			return "";

	}

	/**
	 * This method returns the {@link User} instance of the logged-in user through spring security
	 * {@link SecurityContextHolder}
	 * 
	 * @return the {@link User} instance of the logged in user or an empty string if logged-in user could not be
	 *         identified.
	 */
	public static User getLoggedInUser() throws SecurityRuntimeException {
		if (SecurityContextHolder.getContext() == null) {
			log.error("security context is empty, this seems to be a bug/misconfiguration!");

			throw new SecurityRuntimeException();
		}
		Authentication currentUser = SecurityContextHolder.getContext().getAuthentication();
		if (currentUser == null)
			throw new SecurityRuntimeException();

		if (currentUser instanceof UsernamePasswordAuthenticationToken) {
			User loggedInUser = new User();
			AuthenticatedUser user = (AuthenticatedUser) currentUser.getPrincipal();
			if (user != null) {
				log.debug("Logged In User : " + currentUser.getName() + "  -> " + user.getLoginId());
				loggedInUser.setId(user.getId());
				loggedInUser.setPassword(user.getPassword());
				loggedInUser.setLoginId(user.getLoginId());
				loggedInUser.setPhoneNumber(user.getPhoneNumber());
				loggedInUser.setName(user.getName());
				loggedInUser.setLastLoginTime(user.getLastLoginTime());
				loggedInUser.setLastFailedLoginTime(user.getLastFailedLoginTime());
				loggedInUser.setTenantId(user.getTenantId());
				loggedInUser.setOwner(user.getOwner());
				loggedInUser.setSupplier(user.getSupplier());
				loggedInUser.setBuyer(user.getBuyer());
				loggedInUser.setFinanceCompany(user.getFinanceCompany());
				loggedInUser.setTenantType(user.getTenantType());
				loggedInUser.setCommunicationEmail(user.getCommunicationEmail());
				loggedInUser.setEmailNotifications(user.getEmailNotifications());
				loggedInUser.setBqPageLength(user.getBqPageLength());
				loggedInUser.setLanguageCode(StringUtils.checkString(user.getLanguageCode()).length() > 0 ? user.getLanguageCode() : Locale.ENGLISH.getLanguage());
				loggedInUser.setDeleted(false);
				return loggedInUser;
			} else
				throw new SecurityRuntimeException();
		} else
			throw new SecurityRuntimeException();
	}

	public static String getLoggedInUserProfilePicture() throws SecurityRuntimeException {
		if (SecurityContextHolder.getContext() == null) {
			log.error("security context is empty, this seems to be a bug/misconfiguration!");
			throw new SecurityRuntimeException();
		}
		Authentication currentUser = SecurityContextHolder.getContext().getAuthentication();
		if (currentUser == null)
			throw new SecurityRuntimeException();

		if (currentUser instanceof UsernamePasswordAuthenticationToken) {
			AuthenticatedUser user = (AuthenticatedUser) currentUser.getPrincipal();
			if (user != null && user.getProfilePicture() != null) {
				return user.getProfilePicture();
			} else
				return null;
		} else
			throw new SecurityRuntimeException();
	}

	/**
	 * This method returns the TENANT ID of the logged-in user through spring security {@link SecurityContextHolder}
	 * 
	 * @return the TENANT ID of the logged in user or an empty string if logged-in user could not be identified.
	 */
	public static String getLoggedInUserTenantId() throws SecurityRuntimeException {
		if (SecurityContextHolder.getContext() == null) {
			log.error("security context is empty, this seems to be a bug/misconfiguration!");

			throw new SecurityRuntimeException();
		}
		Authentication currentUser = SecurityContextHolder.getContext().getAuthentication();
		if (currentUser == null)
			throw new SecurityRuntimeException();

		if (currentUser instanceof UsernamePasswordAuthenticationToken) {
			AuthenticatedUser user = (AuthenticatedUser) currentUser.getPrincipal();
			if (user != null) {
				return user.getTenantId();
			} else
				throw new SecurityRuntimeException();
		} else
			throw new SecurityRuntimeException();
	}

	/**
	 * This method returns if the logged in user is admin through spring security {@link SecurityContextHolder}
	 * 
	 * @return true, if the logged in user is an Admin else false
	 */
	public static boolean isLoggedInUserAdmin() throws SecurityRuntimeException {
		if (SecurityContextHolder.getContext() == null) {
			log.error("security context is empty, this seems to be a bug/misconfiguration!");

			throw new SecurityRuntimeException();
		}
		Authentication currentUser = SecurityContextHolder.getContext().getAuthentication();
		if (currentUser == null)
			throw new SecurityRuntimeException();

		if (currentUser instanceof UsernamePasswordAuthenticationToken) {
			AuthenticatedUser user = (AuthenticatedUser) currentUser.getPrincipal();
			if (user != null) {
				return user.getIsAdmin();
			} else
				throw new SecurityRuntimeException();
		} else
			throw new SecurityRuntimeException();
	}

	public SecurityLibrary() {
	}

	// added by Yogesh
	public static String getLoggedInUserLangCode() throws SecurityRuntimeException {
		if (SecurityContextHolder.getContext() == null) {
			log.error("security context is empty, this seems to be a bug/misconfiguration!");
			throw new SecurityRuntimeException();
		}
		Authentication currentUser = SecurityContextHolder.getContext().getAuthentication();
		if (currentUser == null) {
			throw new SecurityRuntimeException();
		}
		if (currentUser instanceof UsernamePasswordAuthenticationToken) {
			AuthenticatedUser user = (AuthenticatedUser) currentUser.getPrincipal();
			if (user != null) {
				return StringUtils.checkString(user.getLanguageCode()).length() > 0 ? user.getLanguageCode() : Locale.ENGLISH.getLanguage();
			} else {
				throw new SecurityRuntimeException();
			}
		} else {
			return Locale.ENGLISH.getLanguage();// for default scope
		}
	}

}
