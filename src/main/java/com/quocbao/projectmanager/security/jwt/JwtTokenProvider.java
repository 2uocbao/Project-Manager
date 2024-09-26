package com.quocbao.projectmanager.security.jwt;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class JwtTokenProvider {

	@Value("${application.security.jwt.secret-key}")
	private String secretKey = "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970";

	@Value("${application.security.jwt.expiration}")
	private long jwtExpiration = 86400000;

	private SecretKey getSignInKey() {
		byte[] keyBytes = Decoders.BASE64URL.decode(secretKey);
		return Keys.hmacShaKeyFor(keyBytes);
	}

	public String generateToken(UserDetails userDetails) {
		return generateToken(new HashMap<>(), userDetails);
	}

	private String generateToken(Map<String, Object> claims, UserDetails userDetails) {
		claims.put("authorities", userDetails.getAuthorities().stream().map(t -> t.getAuthority()).toList());
		return Jwts.builder().claims(claims).subject(userDetails.getUsername())
				.issuedAt(new Date(System.currentTimeMillis()))
				.expiration(new Date(System.currentTimeMillis() + jwtExpiration))
				.signWith(getSignInKey(), Jwts.SIG.HS256).compact();
	}

	private Claims extractPayload(String token) {
		return Jwts.parser().verifyWith(getSignInKey()).build().parseSignedClaims(token).getPayload();
	}

	public String extractUsername(String token) {
		return extractPayload(token).getSubject();
	}

	public Boolean isTokenValid(String token, UserDetails userDetails) {
		return (extractUsername(token).equals(userDetails.getUsername()) && !isTokenExpiration(token));
	}

	private Boolean isTokenExpiration(String token) {
		return extractPayload(token).getExpiration().before(new Date());
	}

	public Boolean validationToken(String token) {
		try {
			Jwts.parser().verifyWith(getSignInKey()).build().parseSignedClaims(token);
			return true;
		} catch (MalformedJwtException ex) {
			log.error("Invalid JWT token");
		} catch (ExpiredJwtException ex) {
			log.error("Expired JWT token");
		} catch (ValidationException ex) {
			log.error("Unsupported JWT token");
			throw new ValidationException("Refresh token has expired");
		} catch (IllegalArgumentException ex) {
			log.error("JWT claims string is empty.");
		} catch (SignatureException ex) {
			log.error("JWT");
		} catch (UnsupportedJwtException ex) {
			log.error("Unsupported JWT token");
		}
		return false;

	}

//	public static void main(String[] args) {
//		JwtTokenProvider jwtTokenProvider = new JwtTokenProvider();
//		List<String> roles = new ArrayList<>();
//		roles.add("USER");
//		UserDetails details = new User("0373656053", "null",
//				roles.stream().map(role -> new SimpleGrantedAuthority("ROLE_USER")).toList());
//		String jwtToken = jwtTokenProvider.generateToken(details);
//		System.out.println(jwtToken);
//		System.out.println();
//		System.out.println(jwtTokenProvider.validationToken(jwtToken));
//		System.out.println();
//		System.out.println(jwtTokenProvider.isTokenExpiration(jwtToken));
//		System.out.println();
//		System.out.println(jwtTokenProvider.extractUsername(jwtToken));
//		System.out.println();
//		System.out.println(jwtTokenProvider.extractPayload(jwtToken));
//	}

}
