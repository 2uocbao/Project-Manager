package com.quocbao.projectmanager.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.quocbao.projectmanager.common.ErrorResponse;
import com.quocbao.projectmanager.security.jwt.JwtAuthenticationFilter;

@EnableWebSecurity
@Configuration
@EnableMethodSecurity
public class SecurityConfig {

	private JwtAuthenticationFilter jwtAuthenticationFilter;

	SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
		this.jwtAuthenticationFilter = jwtAuthenticationFilter;
	}

	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
		httpSecurity.cors(co -> co.disable());
		httpSecurity.csrf(cf -> cf.disable());
		httpSecurity.anonymous(t -> t.disable());
		httpSecurity.authorizeHttpRequests(authorize -> authorize

				.requestMatchers("/notifications/**").permitAll()
				.requestMatchers("/app/**").permitAll()
				.requestMatchers("/users/login").permitAll()
				.requestMatchers("/users/register").permitAll()
				.requestMatchers("/users/**").hasRole("USER")
				.anyRequest().authenticated()

		);
		httpSecurity.sessionManagement(
				sessionManagement -> sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
		httpSecurity.exceptionHandling(t -> t.authenticationEntryPoint(authenticationEntryPoint()));
		httpSecurity.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
		return httpSecurity.build();
	}

	@Bean
	AuthenticationEntryPoint authenticationEntryPoint() {
		return (request, response, authException) -> {
			response.setStatus(HttpStatus.FORBIDDEN.value());
			ErrorResponse errorResponse = new ErrorResponse();
			ErrorResponse.Error error = errorResponse.new Error();
			errorResponse.setStatus(HttpStatus.FORBIDDEN.value());
			error.setCode(HttpStatus.FORBIDDEN.toString());
			error.setMessage("Access Denied: You don't have permission to access this resource.");
			errorResponse.setError(error);
			ObjectMapper objectMapper = new ObjectMapper();
			String jsonResponse = objectMapper.writeValueAsString(errorResponse);
			response.getWriter().write(jsonResponse);
		};
	}

}
