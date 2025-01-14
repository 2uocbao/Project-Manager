package com.quocbao.projectmanager.security;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.quocbao.projectmanager.payload.request.LoginRequest;
import com.quocbao.projectmanager.payload.request.UserRequest;
import com.quocbao.projectmanager.payload.response.UserResponse;
import com.quocbao.projectmanager.service.UserService;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

	private UserService userService;

	public CustomAuthenticationSuccessHandler(UserService userService) {
		this.userService = userService;
	}

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {
		if (authentication instanceof OAuth2AuthenticationToken) {
			
			OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
			UserResponse userResponse;
			ObjectWriter objectWriter = new ObjectMapper().writer().withDefaultPrettyPrinter();

			String givenName = oAuth2User.getAttribute("given_name");
			String familyName = oAuth2User.getAttribute("family_name");
			String email = oAuth2User.getAttribute("email");
			String sub = oAuth2User.getAttribute("sub");

			UserDetails userDetails = userService.loadUserByUsername(email);
			

			if (userDetails == null) {
				UserRequest userRequest = new UserRequest();
				userRequest.setFirstName(givenName);
				userRequest.setLastName(familyName);
				userRequest.setEmail(email);
				userRequest.setPhoneNumber(sub);
				userRequest.setPassword(sub);

				userResponse = userService.registerUser(userRequest);
				
//				String json = objectWriter.writeValueAsString(userResponse);
//
//				response.setContentType("application/json");
//				response.setStatus(HttpServletResponse.SC_OK);
//
//				response.getWriter().write(json);
			} else {
				
//				response.sendRedirect(json);
				LoginRequest loginRequest = new LoginRequest(sub, sub);
				userResponse = userService.loginUser(loginRequest);
				String json = objectWriter.writeValueAsString(userResponse);
				
//				response.setContentType("application/json");
//				response.setStatus(HttpServletResponse.SC_OK);
//				response.getWriter().write(json);
				

			}
			
			response.sendRedirect("myapp://oauth2redirect");
		}
	}

}
