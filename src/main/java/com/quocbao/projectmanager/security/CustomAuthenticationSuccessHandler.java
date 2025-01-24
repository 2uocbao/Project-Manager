package com.quocbao.projectmanager.security;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.quocbao.projectmanager.entity.User;
import com.quocbao.projectmanager.payload.request.UserRequest;
import com.quocbao.projectmanager.security.jwt.JwtTokenProvider;
import com.quocbao.projectmanager.security.oauthconfig.CustomOauth2User;
import com.quocbao.projectmanager.service.UserService;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

	private UserService userService;

	private JwtTokenProvider jwtTokenProvider;

	public CustomAuthenticationSuccessHandler(UserService userService, JwtTokenProvider jwtTokenProvider) {
		this.userService = userService;
		this.jwtTokenProvider = jwtTokenProvider;
	}

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {

		if (authentication instanceof OAuth2AuthenticationToken) {
			CustomOauth2User oAuth2User = new CustomOauth2User((OAuth2User) authentication.getPrincipal());
			// Retrieve user detail in database
			UserDetails userDetails = userService.loadUserByUsername(oAuth2User.getEmail());
			String jwtToken = null;
			// Check if user detail is null
			if (userDetails != null) {
				jwtToken = jwtTokenProvider.generateToken(userDetails);
			} else {
				// Create new user if user does not exist
				UserRequest userRequest = new UserRequest();
				userRequest.setFirstName(oAuth2User.getAttribute("given_name"));
				userRequest.setLastName(oAuth2User.getAttribute("family_name"));
				userRequest.setEmail(oAuth2User.getAttribute("email"));
				userRequest.setPassword(oAuth2User.getAttribute("sub"));
				User user = userService.registerUser(userRequest);
				jwtToken = jwtTokenProvider.generateToken(user.userDetails(user));
			}
			response.sendRedirect("/users/oauth2?email=" + oAuth2User.getEmail() + "&jwtToken=" + jwtToken);
		}

	}

}
