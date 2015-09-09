package com.anemortalkid.yummers.accounts;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.anemortalkid.yummers.responses.ResponseFactory;
import com.anemortalkid.yummers.responses.YummersResponseEntity;

/**
 * Controller for the accounts
 * 
 * @author JMonterrubio
 *
 */
@RestController
@RequestMapping("/accounts")
public class AccountController {

	@Autowired
	private AccountRepository accountRepository;

	@PreAuthorize("hasRole('ROLE_BASIC')")
	@RequestMapping(value = "/changePassword", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	public YummersResponseEntity<Boolean> changePassword(@RequestBody PasswordChangeData passwordChangeData) {
		String callingPath = "/accounts/changePassword";
		String userName = passwordChangeData.getUsername();
		UserDetails details = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		if (details != null) {
			if (!details.getUsername().equals(userName)) {
				return ResponseFactory.respondFail(callingPath, "Could not change password.");
			}
		}
		Account foundAccount = accountRepository.findByUsername(userName);
		if (foundAccount == null) {
			return ResponseFactory.respondFail(callingPath, "Could not change password.");
		}
		boolean oldPasswordMatches = foundAccount.getPassword().equals(passwordChangeData.getOldPassword());
		if (!oldPasswordMatches) {
			return ResponseFactory.respondFail(callingPath, "Could not change password.");
		}

		foundAccount.setPassword(passwordChangeData.getNewPassword());
		accountRepository.save(foundAccount);
		return ResponseFactory.respondOK(callingPath, true);
	}

	@PreAuthorize("hasRole('ROLE_SUPER')")
	@RequestMapping(value = "/register", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	public YummersResponseEntity<Boolean> addAccount(@RequestBody NewAccountData newAccountData) {
		String callingPath = "/accounts/register";

		String username = newAccountData.getUsername();
		if (StringUtils.isBlank(username)) {
			return ResponseFactory.respondFail(callingPath, "Could not create an account with userName=" + username + ".");
		}

		String password = newAccountData.getPassword();
		if (StringUtils.isBlank(password)) {
			return ResponseFactory.respondFail(callingPath, "Could not create an account with userName=" + username + ".");
		}

		YummersAccessLevel yal = newAccountData.getAccessLevel();
		if (yal == null) {
			return ResponseFactory.respondFail(callingPath, "Could not create an account with userName=" + username + ".");
		}

		Account account = accountRepository.findByUsername(username);
		if (account != null) {
			return ResponseFactory.respondFail(callingPath, "Could not create an account with userName=" + username + ".");
		}

		Account registered = new Account(newAccountData.getUsername(), newAccountData.getPassword(), newAccountData.getAccessLevel());
		accountRepository.save(registered);
		return ResponseFactory.respondOK(callingPath, true);
	}

	/**
	 * Ideally, external applications would call us with this to authenticate
	 * the user once, before pretending the user has authentication and failing
	 * to retrieve data
	 * 
	 * @param credentials
	 *            the {@link CredentialData} to check
	 * @return a {@link YummersResponseEntity} with {@link AccountCredentials}
	 *         for that user
	 */
	@PreAuthorize("hasRole('ROLE_BASIC')")
	@RequestMapping(value = "/checkCredentials", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	public YummersResponseEntity<AccountCredentials> getAccessLevel(@RequestBody CredentialData credentials) {
		String callingPath = "/accounts/checkCredentials";

		String username = credentials.getUsername();
		if (StringUtils.isBlank(username)) {
			return ResponseFactory.respondForbidden(callingPath);
		}

		Account account = accountRepository.findByUsername(username);
		if (account != null) {
			boolean pwMatches = account.getPassword().equals(credentials.getPassword());
			if (!pwMatches) {
				return ResponseFactory.respondForbidden(callingPath);
			}

			// passwords match get access level
			String accesLevel = account.getAccessLevel();
			AccountCredentials ac = new AccountCredentials();
			ac.setAccessLevel(YummersAccessLevel.valueOf(accesLevel));
			ac.setUsername(username);
			return ResponseFactory.respondOK(callingPath, ac);
		}

		return ResponseFactory.respondForbidden(callingPath);
	}
}
