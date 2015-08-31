package com.anemortalkid.yummers.accounts;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.anemortalkid.yummers.responses.YummersResponseEntity;

@RequestMapping("/accounts")
public class AccountController {

	@Autowired
	private AccountRepository accountRepository;

	@PreAuthorize("hasRole('ROLE_BASIC')")
	@RequestMapping(value = "/changePassword", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	public YummersResponseEntity<Boolean> changePassword(@RequestBody PasswordChangeData passwordChangeData) {
		String callingPath = "/accounts/changePassword";
		String userName = passwordChangeData.getUsername();
		Account foundAccount = accountRepository.findByUsername(userName);
		if (foundAccount == null) {
			// TODO return fail
			return null;
		}
		boolean oldPasswordMatches = foundAccount.getPassword().equals(passwordChangeData.getOldPassword());
		if (!oldPasswordMatches) {
			// TODO return fail;
			return null;
		}

		foundAccount.setPassword(passwordChangeData.getNewPassword());
		accountRepository.save(foundAccount);
		// TODO
		return null;
	}

	@PreAuthorize("hasRole('ROLE_SUPER')")
	@RequestMapping(value = "/register", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	public YummersResponseEntity<Boolean> addAccount(@RequestBody NewAccountData newAccountData) {
		// TODO
		return null;
	}
}
