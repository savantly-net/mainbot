package net.savantly.mainbot.security;

import java.util.Enumeration;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.server.resource.BearerTokenError;
import org.springframework.security.oauth2.server.resource.BearerTokenErrors;
import org.springframework.security.oauth2.server.resource.web.BearerTokenResolver;

import jakarta.servlet.http.HttpServletRequest;

public final class ProxyBearerTokenResolver implements BearerTokenResolver {

	private static final Logger log = LoggerFactory.getLogger(ProxyBearerTokenResolver.class);

	private static final Pattern authorizationPattern = Pattern.compile("^(?<token>[a-zA-Z0-9-._~+/]+=*)$",
			Pattern.CASE_INSENSITIVE);
	private String bearerTokenHeaderName = "x-forwarded-access-token";

	@Override
	public String resolve(final HttpServletRequest request) {
		final String authorizationHeaderToken = resolveFromAuthorizationHeader(request);
		if (authorizationHeaderToken != null) {
			log.debug("returning authorization header value {}... for request to: {}", authorizationHeaderToken.substring(0, 5), request.getRequestURI());
			return authorizationHeaderToken;
		}
		final String parameterToken = isParameterTokenSupportedForRequest(request)
				? resolveFromRequestParameters(request) : null;
		if (parameterToken != null) {
			log.debug("returning access_token parameter {}... for request to: {}", parameterToken.substring(0, 5), request.getRequestURI());
			return parameterToken;
		}
		log.debug("no token found for request to: {}", request.getRequestURI());
		return null;
	}

	public void setBearerTokenHeaderName(String bearerTokenHeaderName) {
		this.bearerTokenHeaderName = bearerTokenHeaderName;
	}

	private String resolveFromAuthorizationHeader(HttpServletRequest request) {
		StringBuilder sb = new StringBuilder();
		Enumeration<String> headerNames = request.getHeaderNames();
		while (headerNames.hasMoreElements()) {
			sb.append(String.format("%s ", headerNames.nextElement()).toLowerCase());
		}
		log.trace("headers present in request: {}", sb);
		log.debug("looking for authorization header: {} in request to: {}", this.bearerTokenHeaderName, request.getRequestURI());
		String authorization = request.getHeader(this.bearerTokenHeaderName);
		if (Objects.isNull(authorization)) {
			log.debug("authorization header not found: {} for request to: {}", this.bearerTokenHeaderName, request.getRequestURI());
			return null;
		}
		log.trace("authorization header value: {}", authorization);
		Matcher matcher = authorizationPattern.matcher(authorization);
		if (!matcher.matches()) {
			log.debug("header value does not match pattern: {} for request to: {}", authorizationPattern, request.getRequestURI());
			BearerTokenError error = BearerTokenErrors.invalidToken("Bearer token is malformed");
			throw new OAuth2AuthenticationException(error);
		}
		return matcher.group("token");
	}

	private static String resolveFromRequestParameters(HttpServletRequest request) {
		log.debug("resolving token from request parameters to uri: {}", request.getRequestURI());
		String[] values = request.getParameterValues("access_token");
		if (values == null || values.length == 0) {
			log.debug("no access_token parameter found in request to uri: {}", request.getRequestURI());
			return null;
		}
		if (values.length == 1) {
			var token = values[0];
			log.trace("access_token parameter found: {}", token);
			log.debug("returning access_token parameter {}xxx for uri: {}", token.substring(0, 5), request.getRequestURI());
			return token;
		}
		BearerTokenError error = BearerTokenErrors.invalidRequest("Found multiple bearer tokens in the request to uri: " + request.getRequestURI());
		throw new OAuth2AuthenticationException(error);
	}

	private boolean isParameterTokenSupportedForRequest(final HttpServletRequest request) {
		return (("POST".equals(request.getMethod())
				&& MediaType.APPLICATION_FORM_URLENCODED_VALUE.equals(request.getContentType()))
				|| "GET".equals(request.getMethod()));
	}
}
