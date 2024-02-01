package br.com.recharge.auth.controllers;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.recharge.auth.models.record.UserRecord;
import br.com.recharge.auth.security.authentication.JwtAuthenticationManager;
import br.com.recharge.auth.services.UserService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("auth/v1/client")
@RequiredArgsConstructor
public class LoginUserController {

	private final UserService service;
	private final JwtAuthenticationManager authenticationManager;

	
	@PostMapping("/login")
	public String loginClient(@RequestBody UserRecord user) {
		return authenticationManager.authenticate(service.loginJwt(user)).getCredentials().toString();
	}

	@GetMapping("/logoff")
	public void logoffClient() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		authenticationManager.unauthenticate(authentication);
	}
	
	@GetMapping("/validate")
	public boolean validate() {
		return true;
	}

}
