package net.savantly.mainbot.identity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.view.RedirectView;

import io.swagger.v3.oas.annotations.Parameter;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/api/me")
@RequiredArgsConstructor
public class Me {

	private static final Logger log = LoggerFactory.getLogger(Me.class);

	private final UserContext userContext;
	private final ApplicationEventPublisher applicationEventPublisher;

	@GetMapping
	@ResponseBody
	public UserDto getAuthorization(@AuthenticationPrincipal Jwt jwt, HttpServletRequest request) {
		UserDto userDto = userContext.getCurrentUser().orElseThrow(() -> new RuntimeException("no user"));
		MeAuthorizedEvent event = new MeAuthorizedEvent(this, userDto);
		applicationEventPublisher.publishEvent(event);
		//userDto.setToken(token);
		return userDto;
	}

	@GetMapping("/logout")
	@Parameter(name = "jwt", hidden = true)
	public RedirectView logout(@AuthenticationPrincipal Jwt jwt, HttpServletRequest request,
			HttpServletResponse response) {
		logout(request, response);
		return new RedirectView("/logged-out");
	}

	private void logout(HttpServletRequest request, HttpServletResponse response) {
		boolean isSecure = false;
		String contextPath = null;
		if (request != null) {
			HttpSession session = request.getSession(false);
			if (session != null) {
				session.invalidate();
			}
			isSecure = request.isSecure();
			contextPath = request.getContextPath();
		}
		SecurityContext context = SecurityContextHolder.getContext();
		SecurityContextHolder.clearContext();
		context.setAuthentication(null);
		if (response != null) {
			resetCookie("JSESSIONID", contextPath, isSecure, response);
			resetCookie("_oauth2_proxy", contextPath, isSecure, response);
		}
	}

	private void resetCookie(String cookieName, String contextPath, boolean isSecure, HttpServletResponse response) {
		Cookie cookie = new Cookie(cookieName, null);
		String cookiePath = StringUtils.hasText(contextPath) ? contextPath : "/";
		cookie.setPath(cookiePath);
		cookie.setMaxAge(0);
		cookie.setSecure(isSecure);
		response.addCookie(cookie);
	}

}