package br.com.recharge.auth.security.authentication;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import br.com.recharge.auth.security.jwt.JwtModule;

@Component
@Qualifier("JwtAuthenticationManager")
@Primary
public class JwtAuthenticationManager implements AuthenticationManager {

	private final JwtModule service;

	public JwtAuthenticationManager(JwtModule service) {
		this.service = service;
	}

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		return service.login(authentication);
	}

	public void unauthenticate(Authentication authentication) throws AuthenticationException {
		service.logoff(authentication.getPrincipal().toString());
	}
}
