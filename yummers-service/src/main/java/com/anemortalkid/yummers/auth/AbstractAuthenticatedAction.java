package com.anemortalkid.yummers.auth;

import java.text.MessageFormat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * An {@link AbstractAuthenticatedAction} is an action that runs under
 * authentication. This class is used internally with the Super user and Super
 * Password created during production startup, so we can call the internal
 * methods that are secured as opposed to writing unsecured methods/
 * 
 * @author JMonterrubio
 *
 * @param <T>
 *            the type of the action that will be performed
 */
public abstract class AbstractAuthenticatedAction<T> {

	private final Logger LOGGER = LoggerFactory.getLogger(getClass());

	private String user;
	private String password;

	/**
	 * Constructs a new AbstractAuthenticatedAction
	 * 
	 * @param user
	 *            the user to use when authenticating
	 * @param password
	 *            the password for said user
	 */
	public AbstractAuthenticatedAction(String user, String password) {
		this.user = user;
		this.password = password;
	}

	/**
	 * Performs the action defined by {@link #performAction()}
	 * 
	 * @return the result of {@link #performAction()}
	 */
	public T perform() {
		try {
			authenticate();
			return performAction();
		} finally {
			unauthenticate();
		}
	}

	private void authenticate() {
		LOGGER.info(MessageFormat.format("Authenticating < user={0} >", user));
		UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(user, password);
		SecurityContextHolder.getContext().setAuthentication(authenticationToken);
	}

	private void unauthenticate() {
		LOGGER.info(MessageFormat.format("Unauthenticating < user={0} >", user));
		SecurityContextHolder.getContext().setAuthentication(null);
	}

	/**
	 * Runs the desired action. The action will run under an authorized context
	 * and doesn't need to take into account authenticating and unauthenticating
	 * 
	 * @return the result of running the action
	 */
	protected abstract T performAction();

}
