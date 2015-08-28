package com.anemortalkid.yummers.auth;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configurers.GlobalAuthenticationConfigurerAdapter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.anemortalkid.yummers.accounts.AccessLevels;
import com.anemortalkid.yummers.accounts.Account;
import com.anemortalkid.yummers.accounts.AccountRepository;

@Configuration
public class WebSecurityConfiguration extends GlobalAuthenticationConfigurerAdapter {

	private final Logger LOGGER = LoggerFactory.getLogger(getClass());

	@Autowired
	AccountRepository accountRepository;

	@Override
	public void init(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(userDetailsService());
	}

	@Bean
	UserDetailsService userDetailsService() {
		return new UserDetailsService() {

			@Override
			public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
				Account account = accountRepository.findByUsername(username);
				if (account != null) {
					return new User(account.getUsername(), account.getPassword(), true, true, true, true, getGrantedAuthoritiesForAccess(account.getAccessLevel()));
				} else {
					throw new UsernameNotFoundException("could not find the user '" + username + "'");
				}
			}

		};
	}

	private List<GrantedAuthority> getGrantedAuthoritiesForAccess(String accountAccess) {
		List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
		AccessLevels highestAccess = AccessLevels.valueOf(accountAccess);
		switch (highestAccess) {
		case ROLE_SUPER:
			authorities.add(new SimpleGrantedAuthority(AccessLevels.ROLE_SUPER.toString()));
		case ROLE_ADMIN:
			authorities.add(new SimpleGrantedAuthority(AccessLevels.ROLE_ADMIN.toString()));
		case ROLE_BASIC:
			authorities.add(new SimpleGrantedAuthority(AccessLevels.ROLE_BASIC.toString()));
			break;
		default:
			LOGGER.info("There is no role association for < accessLevel=\"{0}\" > ", accountAccess);
		}
		return authorities;
	}
}
