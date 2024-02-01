package br.com.recharge.auth.services;

import java.util.List;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.recharge.auth.exceptions.NotAuthorizedException;
import br.com.recharge.auth.models.User;
import br.com.recharge.auth.models.record.UserRecord;
import br.com.recharge.auth.repositorys.UserRepository;
import br.com.recharge.auth.security.jwt.JwtModule;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

	private final static int TIME_TOKEN_IN_MINUTES = 10;
	private final UserRepository repository;
	private final JwtModule login;

	public Authentication loginJwt(UserRecord user) {
		User client = findByLicense(user.login(), user.password());
		List<String> claims = List.of("CLIENT");
		if (client != null) {
			return new UsernamePasswordAuthenticationToken(client.getId(),
					login.generateJwtToken(client.getLogin(), client.getId().intValue(), claims, TIME_TOKEN_IN_MINUTES),
					List.of(new SimpleGrantedAuthority("CLIENT")));
		}
		return null;
	}

	private User findByLicense(String login, String password) {
		return repository.findByLoginAndPassword(login, password)
				.orElseThrow(() -> new NotAuthorizedException("Login failed: " + login));
	}

	public void save(User user) {
		repository.save(user);
	}
}
