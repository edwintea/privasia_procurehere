/**
 * 
 */
package com.privasia.procurehere.core.pojo;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sun.org.apache.xpath.internal.operations.Bool;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.privasia.procurehere.core.entity.AccessRights;
import com.privasia.procurehere.core.entity.Buyer;
import com.privasia.procurehere.core.entity.Country;
import com.privasia.procurehere.core.entity.FinanceCompany;
import com.privasia.procurehere.core.entity.Owner;
import com.privasia.procurehere.core.entity.Supplier;
import com.privasia.procurehere.core.entity.User;
import com.privasia.procurehere.core.enums.BuyerStatus;
import com.privasia.procurehere.core.enums.FinanceCompanyStatus;
import com.privasia.procurehere.core.enums.SupplierStatus;
import com.privasia.procurehere.core.enums.TenantType;
import com.privasia.procurehere.core.utils.Global;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

/**
 * @author Ravi
 */
public class AuthenticatedUser extends User implements UserDetails {

	private static final long serialVersionUID = -725429104108486873L;

	private static final Logger LOG = LogManager.getLogger(AuthenticatedUser.class);

	private List<String> grantedAuthorities = new ArrayList<String>();

	private boolean passwordExpired;

	private Supplier supplier;

	private Buyer buyer;

	private Owner owner;

	private FinanceCompany financeCompany;

	private String tenantId;

	private String designation;

	private String profilePictureMimeType;

	private String profilePicture;

	private Boolean isAdmin = Boolean.FALSE;

	private String jwtToken;

	private String companyName;

	private Boolean isBuyerErpEnable = Boolean.FALSE;

	private Boolean isFreeTrial = Boolean.FALSE;

	private Boolean isUnSubscribeEmail = Boolean.FALSE;

	private String roleName;

	private String languageCode;

	public AuthenticatedUser(User user, String roleName, boolean freeTrial) {
		this(user, roleName);
		this.isFreeTrial = freeTrial;
	}

	public AuthenticatedUser(User user, String roleName) {
		this(user);
		this.roleName = roleName;
	}

	public AuthenticatedUser(User user) {
		try {
			LOG.info("Inside AuthenticatedUser block...");
			this.setLanguageCode(user.getLanguageCode());
			this.setActive(user.isActive());
			this.setLocked(user.isLocked());
			this.setDeleted(user.isDeleted());
			this.isAccountNonExpired();
			this.isAccountNonLocked();
			this.setLoginId(user.getLoginId());
			this.setId(user.getId());
			this.setPhoneNumber(user.getPhoneNumber());
			this.setPassword(user.getPassword());
			this.setName(user.getName());
			this.setLastLoginTime(user.getLastLoginTime());
			this.setLastFailedLoginTime(user.getLastFailedLoginTime());
			this.setTenantId(user.getTenantId());
			this.setCommunicationEmail(user.getCommunicationEmail());
			this.setBqPageLength(user.getBqPageLength());
			this.setShowWizardTutorial(user.getShowWizardTutorial() != null ? user.getShowWizardTutorial() : Boolean.TRUE);
			this.setEmailNotifications(user.getEmailNotifications() != null ? user.getEmailNotifications() : Boolean.TRUE);
			if (user.getSupplier() != null) {
				Supplier supplier = new Supplier();
				supplier.setId(user.getSupplier().getId());
				supplier.setCompanyName(user.getSupplier().getCompanyName());
				supplier.setCompanyRegistrationNumber(user.getSupplier().getCompanyRegistrationNumber());
				supplier.setTermsOfUseAccepted(user.getSupplier().getTermsOfUseAccepted());
				supplier.setTermsOfUseAcceptedDate(user.getSupplier().getTermsOfUseAcceptedDate());
				supplier.setRegistrationComplete(user.getSupplier().getRegistrationComplete());
				supplier.setLoginEmail(user.getSupplier().getLoginEmail());
				supplier.setFullName(user.getSupplier().getFullName());
				supplier.setCommunicationEmail(user.getSupplier().getCommunicationEmail());
				supplier.setIsOnboardingForm(user.getSupplier().getIsOnboardingForm());
				supplier.setOnBoardingFromsubmitedDate(user.getSupplier().getOnBoardingFromsubmitedDate());
				supplier.setBuyerAccount(user.getSupplier().getBuyerAccount());
				try {
					Country regC = user.getSupplier().getRegistrationOfCountry();
					if (regC != null) {
						Country country = new Country();
						country.setId(regC.getId());
						country.setCountryCode(regC.getCountryCode());
						supplier.setRegistrationOfCountry(country);
					}
				} catch (Exception e) {
					LOG.error("Error getting user country : " + e.getMessage(), e);
				}

				this.setSupplier(supplier);
				this.designation = user.getSupplier().getDesignation();
				grantedAuthorities.add("ROLE_SUPPLIER");
				if (user.getSupplier().getStatus() == SupplierStatus.SUSPENDED) {
					this.setActive(false);
				}
				if (SupplierStatus.CLOSED == user.getSupplier().getStatus()) {
					this.setActive(false);
					this.setDeleted(true);
					this.setLocked(true);
				}
				this.setTenantType(TenantType.SUPPLIER);
				this.companyName = user.getSupplier().getCompanyName();
			}

			if (user.getBuyer() != null) {
				Buyer buyer = new Buyer();
				buyer.setId(user.getBuyer().getId());
				buyer.setCompanyName(user.getBuyer().getCompanyName());
				buyer.setCompanyRegistrationNumber(user.getBuyer().getCompanyRegistrationNumber());
				buyer.setTermsOfUseAccepted(user.getBuyer().getTermsOfUseAccepted());
				buyer.setTermsOfUseAcceptedDate(user.getBuyer().getTermsOfUseAcceptedDate());
				buyer.setRegistrationComplete(user.getBuyer().getRegistrationComplete());
				buyer.setLoginEmail(user.getBuyer().getLoginEmail());
				buyer.setFullName(user.getBuyer().getFullName());
				buyer.setCommunicationEmail(user.getBuyer().getCommunicationEmail());
				buyer.setEnableEventUserControle(user.getBuyer().getEnableEventUserControle());

				try {
					Country regC = user.getBuyer().getRegistrationOfCountry();
					if (regC != null) {
						Country country = new Country();
						country.setId(regC.getId());
						country.setCountryCode(regC.getCountryCode());
						buyer.setRegistrationOfCountry(country);
					}
				} catch (Exception e) {
					LOG.error("Error getting user country : " + e.getMessage(), e);
				}

				this.setBuyer(buyer);
				grantedAuthorities.add("ROLE_BUYER");
				if (user.getBuyer().getStatus() == BuyerStatus.SUSPENDED) {
					this.setActive(false);
				}
				this.setTenantType(TenantType.BUYER);
				this.companyName = user.getBuyer().getCompanyName();
				this.isBuyerErpEnable = user.getIsBuyerErpEnable();
				this.setUserType(user.getUserType());
			}
			if (user.getOwner() != null) {
				Owner owner = new Owner();
				owner.setId(user.getOwner().getId());
				owner.setCompanyName(user.getOwner().getCompanyName());
				owner.setCompanyRegistrationNumber(user.getOwner().getCompanyRegistrationNumber());
				owner.setCommunicationEmail(user.getOwner().getCommunicationEmail());
				this.setOwner(owner);
				grantedAuthorities.add("ROLE_OWNER");
				this.setTenantType(TenantType.OWNER);
				this.companyName = user.getOwner().getCompanyName();
			}

			if (user.getFinanceCompany() != null) {
				FinanceCompany fCompany = new FinanceCompany();
				fCompany.setId(user.getFinanceCompany().getId());
				fCompany.setCompanyName(user.getFinanceCompany().getCompanyName());
				fCompany.setCompanyRegistrationNumber(user.getFinanceCompany().getCompanyRegistrationNumber());
				fCompany.setCommunicationEmail(user.getFinanceCompany().getCommunicationEmail());
				this.setFinanceCompany(fCompany);
				grantedAuthorities.add("ROLE_FINANCE");
				if (user.getFinanceCompany().getStatus() == FinanceCompanyStatus.SUSPENDED) {
					this.setActive(false);
				}
				this.setTenantType(TenantType.FINANCE_COMPANY);
				this.companyName = user.getFinanceCompany().getCompanyName();
			}

			// if (user.getPrfPicAttatchment() != null) {
			// this.profilePictureMimeType = user.getPrfPiccontentType();
			// this.profilePicture = user.getProfilePicture();
			// }

			if (user.getLastPasswordChangedDate() == null) {
				// If its supplier and he has not completed his profile setup yet, dont expire his password.
				if (this.getSupplier() != null && Boolean.FALSE == this.getSupplier().getRegistrationComplete()) {
					this.passwordExpired = false;
				} else if (this.getSupplier() != null && Boolean.TRUE == this.getSupplier().getRegistrationComplete() && Boolean.TRUE == this.getSupplier().getBuyerAccount() && this.getSupplier().getOnBoardingFromsubmitedDate() == null) {
					this.passwordExpired = false;
				} else {
					LOG.info("First time Login for user : " + user.getName());
					this.passwordExpired = true;
				}
			} else if (user.getPasswordExpiryInDays() != null && user.getPasswordExpiryInDays() > 0) {
				Calendar exp = Calendar.getInstance();
				exp.add(Calendar.DATE, (user.getPasswordExpiryInDays() != null && user.getPasswordExpiryInDays() != 0) ? -user.getPasswordExpiryInDays() : -Global.PASSWORD_EXPIRY);
				/**
				 * PH-154 Remove password expiry/renewal for Puva we bypass the security for puva@privasia.com
				 */
				if (!user.getLoginId().equalsIgnoreCase("puva@privasia.com") && user.getLastPasswordChangedDate().before(exp.getTime())) {
					LOG.info("Password expired for user : " + user.getName());
					this.passwordExpired = true;
				}
			}

			if (user.getUserRole() != null) {
				LOG.info("Inside getUserRole condition block..." + user.getUserRole());

				boolean roleUserFound = false;

				setUserRole(user.getUserRole());
				if (user.getUserRole().getAccessControlList() != null) {
					for (AccessRights acl : user.getUserRole().getAccessControlList()) {
						grantedAuthorities.add(acl.getAclValue());
						if (acl.getAclValue().equals("ROLE_ADMIN")) {
							this.isAdmin = Boolean.TRUE;
						}

						if (acl.getAclValue().equals("ROLE_USER")) {
							roleUserFound = true;
						}
					}
				}

				if (!roleUserFound) {
					grantedAuthorities.add("ROLE_USER");
				}
			}

			try {
				if (this.getTenantType() != null) {
					Date expiration = Date.from(LocalDateTime.now().plusHours(2).toInstant(ZoneOffset.UTC));
					Map<String, Object> claims = new HashMap<String, Object>();
					claims.put("name", user.getName());
					claims.put("email", user.getLoginId());
					claims.put("jti", user.getId());
					claims.put("tags", new String[] { this.getTenantType().toString() });
					claims.put("external_id", user.getId());
					String compactJws = Jwts.builder().setClaims(claims).setIssuedAt(new Date())
							// .setExpiration(expiration)
							.signWith(SignatureAlgorithm.HS512, "ecf8639b3085200ef74c4197da7f66ed".getBytes()).compact();
					this.jwtToken = compactJws;
				}
			} catch (Exception e) {
				LOG.error("Error generating JWT Token : " + e.getMessage(), e);
			}

		} catch (Exception e) {
			LOG.error("Error constructing Authenticated user details : " + e.getMessage(), e);
		}
	}

	@Override
	public Collection<GrantedAuthority> getAuthorities() {
		List<GrantedAuthority> list = new ArrayList<GrantedAuthority>();
		for (String role : grantedAuthorities) {
			list.add(new SimpleGrantedAuthority(role));
		}
		return list;
	}

	@Override
	public String getUsername() {
		return this.getLoginId();
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return !this.isLocked();
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return !(this.isDeleted() || !this.isActive());
	}

	/**
	 * @return the passwordExpired
	 */
	public boolean isPasswordExpired() {
		return passwordExpired;
	}

	/**
	 * @param passwordExpired the passwordExpired to set
	 */
	public void setPasswordExpired(boolean passwordExpired) {
		this.passwordExpired = passwordExpired;
	}

	/**
	 * @return the grantedAuthorities
	 */
	public List<String> getGrantedAuthorities() {
		return grantedAuthorities;
	}

	/**
	 * @param grantedAuthorities the grantedAuthorities to set
	 */
	public void setGrantedAuthorities(List<String> grantedAuthorities) {
		this.grantedAuthorities = grantedAuthorities;
	}

	/**
	 * @return the buyer
	 */
	public Buyer getBuyer() {
		return buyer;
	}

	/**
	 * @param buyer the buyer to set
	 */
	public void setBuyer(Buyer buyer) {
		this.buyer = buyer;
	}

	/**
	 * @return the owner
	 */
	public Owner getOwner() {
		return owner;
	}

	/**
	 * @param owner the owner to set
	 */
	public void setOwner(Owner owner) {
		this.owner = owner;
	}

	/**
	 * @return the supplier
	 */
	public Supplier getSupplier() {
		return supplier;
	}

	/**
	 * @param supplier the supplier to set
	 */
	public void setSupplier(Supplier supplier) {
		this.supplier = supplier;
	}

	/**
	 * @return the tenantId
	 */
	public String getTenantId() {
		return tenantId;
	}

	/**
	 * @param tenantId the tenantId to set
	 */
	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
	}

	/**
	 * @return the designation
	 */
	public String getDesignation() {
		return designation;
	}

	/**
	 * @param designation the designation to set
	 */
	public void setDesignation(String designation) {
		this.designation = designation;
	}

	/**
	 * @return the profilePictureMimeType
	 */
	public String getProfilePictureMimeType() {
		return profilePictureMimeType;
	}

	/**
	 * @param profilePictureMimeType the profilePictureMimeType to set
	 */
	public void setProfilePictureMimeType(String profilePictureMimeType) {
		this.profilePictureMimeType = profilePictureMimeType;
	}

	/**
	 * @return the profilePicture
	 */
	public String getProfilePicture() {
		return profilePicture;
	}

	/**
	 * @param profilePicture the profilePicture to set
	 */
	public void setProfilePicture(String profilePicture) {
		this.profilePicture = profilePicture;
	}

	/**
	 * @return the isAdmin
	 */
	public Boolean getIsAdmin() {
		return isAdmin;
	}

	/**
	 * @param isAdmin the isAdmin to set
	 */
	public void setIsAdmin(Boolean isAdmin) {
		this.isAdmin = isAdmin;
	}

	/**
	 * @return the jwtToken
	 */
	public String getJwtToken() {
		return jwtToken;
	}

	/**
	 * @param jwtToken the jwtToken to set
	 */
	public void setJwtToken(String jwtToken) {
		this.jwtToken = jwtToken;
	}

	/**
	 * @return the companyName
	 */
	public String getCompanyName() {
		return companyName;
	}

	/**
	 * @param companyName the companyName to set
	 */
	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	/**
	 * @return the isBuyerErpEnable
	 */
	public Boolean getIsBuyerErpEnable() {
		return isBuyerErpEnable;
	}

	/**
	 * @param isBuyerErpEnable the isBuyerErpEnable to set
	 */
	public void setIsBuyerErpEnable(Boolean isBuyerErpEnable) {
		this.isBuyerErpEnable = isBuyerErpEnable;
	}

	/**
	 * @return the financeCompany
	 */
	public FinanceCompany getFinanceCompany() {
		return financeCompany;
	}

	/**
	 * @param financeCompany the financeCompany to set
	 */
	public void setFinanceCompany(FinanceCompany financeCompany) {
		this.financeCompany = financeCompany;
	}

	/**
	 * @return the isFreeTrial
	 */
	public Boolean getIsFreeTrial() {
		return isFreeTrial;
	}

	/**
	 * @param isFreeTrial the isFreeTrial to set
	 */
	public void setIsFreeTrial(Boolean isFreeTrial) {
		this.isFreeTrial = isFreeTrial;
	}

	/**
	 * @return the isUnsubscribeEmail
	 */
	public Boolean getIsUnSubscribeEmail() {
		return isUnSubscribeEmail;
	}

	/**
	 * @param isUnSubscribeEmail the isUnsubscribeEmail to set
	 */
	public void setIsUnSubscribeEmail(Boolean  isUnSubscribeEmail){
		this.isUnSubscribeEmail = isUnSubscribeEmail;
	}

	/**
	 * @return the roleName
	 */
	public String getRoleName() {
		return roleName;
	}

	/**
	 * @param roleName the roleName to set
	 */
	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	/**
	 * @return the languageCode
	 */
	public String getLanguageCode() {
		return languageCode;
	}

	/**
	 * @param languageCode the languageCode to set
	 */
	public void setLanguageCode(String languageCode) {
		this.languageCode = languageCode;
	}

}
