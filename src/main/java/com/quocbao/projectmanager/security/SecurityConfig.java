package com.quocbao.projectmanager.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtDecoders;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
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

	private CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler;

	SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter,
			CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler) {
		this.jwtAuthenticationFilter = jwtAuthenticationFilter;
		this.customAuthenticationSuccessHandler = customAuthenticationSuccessHandler;
	}

	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
		httpSecurity.cors(co -> co.disable());
		httpSecurity.csrf(cf -> cf.disable());
		httpSecurity.anonymous(t -> t.disable());
		httpSecurity.authorizeHttpRequests(
				authorize -> authorize
						.requestMatchers("/").permitAll()
						.requestMatchers("/notifications/**").permitAll()
						.requestMatchers("/app/**").permitAll()
						.requestMatchers("/login/**").permitAll()
						.requestMatchers("/users/login").permitAll()

						.requestMatchers("/users/register").permitAll()

						.requestMatchers("/secured").authenticated()

						.requestMatchers(HttpMethod.PUT, "/users/{userId}/groups/**").hasRole("ADMIN")
						.requestMatchers(HttpMethod.DELETE, "/users/{userId}/groups/**").hasRole("ADMIN")

						.requestMatchers(HttpMethod.PUT, "/users/{userId}/projects/**").hasRole("MANAGER")
						.requestMatchers(HttpMethod.DELETE, "/users/{userId}/projects/**").hasRole("MANAGER")
						.requestMatchers("/users/**").hasRole("USER")

						.requestMatchers(HttpMethod.POST, "/projects/{projectId}/task").hasRole("MANAGER")
						.requestMatchers(HttpMethod.PUT, "/projects/{projectId}/task").hasRole("MANAGER")
						.requestMatchers(HttpMethod.PUT, "/projects/{projectId}/task/{taskId}/commit_content")
						.hasAnyRole("USER", "MANAGER")
						.anyRequest().authenticated()

		);

//		for login with web app
		httpSecurity.oauth2Client(Customizer.withDefaults());
		httpSecurity.oauth2Login(t -> t.successHandler(customAuthenticationSuccessHandler));
		httpSecurity.sessionManagement(
				sessionManagement -> sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
//		httpSecurity.exceptionHandling(t -> t.authenticationEntryPoint(authenticationEntryPoint()));
		httpSecurity.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
		return httpSecurity.build();
	}

	@Bean
	AuthenticationEntryPoint authenticationEntryPoint() {
		return (_, response, _) -> {
			response.setStatus(HttpStatus.FORBIDDEN.value());
			ErrorResponse errorResponse = new ErrorResponse();
			errorResponse.setStatus(HttpStatus.FORBIDDEN.value());
			errorResponse.setTitle("ERROR AUTHENTICATE");
			errorResponse.setDetail("Access Denied: Unauthorized Access - Invalid or Expired Token");
			ObjectMapper objectMapper = new ObjectMapper();
			String jsonResponse = objectMapper.writeValueAsString(errorResponse);
			response.getWriter().write(jsonResponse);
		};
	}

//	@Bean
//    AuthorizationManager<Message<?>> messageAuthorizationManager() {
//        Map<AuthorizationManagerMessageMatcher<?>, AuthorizationManager<Message<?>>> mappings = new LinkedHashMap<>();
//
//        // Admin topic authorization
//        mappings.put(new SimpDestinationMessageMatcher("/topic/admin/**"), 
//            (authentication, object) -> 
//                new AuthorizationDecision(authentication.getAuthorities().stream()
//                    .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"))));
//
//        // App-specific authorization
//        mappings.put(new SimpDestinationMessageMatcher("/app/**"), 
//            (authentication, object) -> 
//                new AuthorizationDecision(authentication.isAuthenticated()));
//
//        // Default authorization: authenticated
//        mappings.put(new AuthorizationManagerMessageMatcher<>(message -> true), 
//            (authentication, object) -> 
//                new AuthorizationDecision(authentication.isAuthenticated()));
//
//        return new MessageMatcherDelegatingAuthorizationManager(mappings);
//    }

}
