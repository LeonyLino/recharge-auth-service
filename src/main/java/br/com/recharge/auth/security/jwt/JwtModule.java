package br.com.recharge.auth.security.jwt;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.crypto.SecretKey;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;

@Service
public class JwtModule {

	private static final SignatureAlgorithm SIGNATURE_ALGORITHM = SignatureAlgorithm.HS256;
	private static final SecretKey SECRET_KEY = Keys.secretKeyFor(SIGNATURE_ALGORITHM);
	private final Map<String, Map<String, Object>> authenticated = new HashMap<>();

	@SuppressWarnings("unchecked")
	public Authentication login(Authentication authentication) {
		String jwt = (String) authentication.getCredentials();
		if (jwt != null) {
			try {
				Jws<Claims> jws = parseToken(jwt);
				String login = jws.getBody().getSubject();
				if (check(login)) {
					List<String> authorities = (List<String>) jws.getBody().get("authorities");
					UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(login,
							authentication.getCredentials(),
							authorities.stream().map(SimpleGrantedAuthority::new).toList());
					token.setDetails(jws);
					return token;
				}
			} catch (SignatureException | MalformedJwtException | UnsupportedJwtException
					| IllegalArgumentException e) {
				throw new BadCredentialsException("Invalid security token provided");
			} catch (ExpiredJwtException e) {
				throw new BadCredentialsException("The security token is expired");
			}
		}
		return authentication;
	}

	private boolean check(String login) {
		return authenticated.containsKey(login);
	}

	public String generateJwtToken(String login, Integer id, List<String> authorities,
			int tokenValidationTimeInMinutes) {
		if (login != null && id != null && !login.isEmpty() && id > 0) {
			Map<String, Object> claims = new HashMap<>();
			claims.put("id", id);
			claims.put("login", login);
			claims.put("authorities", authorities);
			return createToken(login, claims, Math.max(tokenValidationTimeInMinutes, 10));
		}
		return null;
	}

	public void addUserAuthenticated(String login, Map<String, Object> claims) {
		if (authenticated.containsKey(login)) {
			authenticated.replace(login, claims);
		}
		authenticated.putIfAbsent(login, claims);
	}

	public void logoff(String login) {
		authenticated.remove(login);
	}

	public String createToken(String login, Map<String, Object> claims, int tokenValidity) {
		Instant now = Instant.now();
		Date expiryDate = Date.from(now.plus(Duration.ofMinutes(tokenValidity)));
		addUserAuthenticated(login, claims);
		return Jwts.builder().setSubject(login).addClaims(claims).setExpiration(expiryDate).setIssuedAt(Date.from(now))
				.signWith(SECRET_KEY, SIGNATURE_ALGORITHM).compact();
	}

	public Jws<Claims> parseToken(String compactToken)
			throws ExpiredJwtException, UnsupportedJwtException, MalformedJwtException, IllegalArgumentException {
		return Jwts.parserBuilder().setSigningKey(SECRET_KEY).build().parseClaimsJws(compactToken);
	}
}