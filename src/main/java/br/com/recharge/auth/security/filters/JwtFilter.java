package br.com.recharge.auth.security.filters;

import java.io.IOException;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class JwtFilter implements Filter {

	private static final String AUTH_HEADER_KEY = "Authorization";
	private static final String AUTH_HEADER_VALUE_PREFIX = "Bearer ";

	private final AuthenticationManager authenticationManager;

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		String jwt = getBearerToken((HttpServletRequest) request);
		UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken("user", jwt);
		Authentication authentication = authenticationManager.authenticate(authenticationToken);
		SecurityContextHolder.getContext().setAuthentication(authentication);
		if (!authentication.isAuthenticated()) {
			((HttpServletResponse) response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			return;
		}
		chain.doFilter(request, response);
	}

	private String getBearerToken(HttpServletRequest request) {
		String authHeader = request.getHeader(AUTH_HEADER_KEY);
		if (authHeader != null && authHeader.startsWith(AUTH_HEADER_VALUE_PREFIX)) {
			return authHeader.substring(AUTH_HEADER_VALUE_PREFIX.length());
		}
		return null;
	}

}