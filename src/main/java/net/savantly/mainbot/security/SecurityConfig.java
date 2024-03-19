package net.savantly.mainbot.security;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.oauth2.server.resource.web.BearerTokenResolver;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.savantly.mainbot.dom.appuser.AppUsers;

/**
 * This class represents a configuration class that is used to configure the security of the application.
 */
@Slf4j
@EnableWebSecurity
@ConfigurationProperties(prefix = "app.security")
// @EnableMethodSecurity(prePostEnabled = true)
@Configuration
public class SecurityConfig {

	@Setter
	private String bearerTokenHeaderName = "x-forwarded-access-token";

	@Setter
	private String authoritiesClaimName = "roles";

	@Setter
	private boolean debug = false;

	@Setter
	private boolean useCsrf = false;

	@Setter
	private boolean enabled = false;

	@Setter
	private String authorityPrefix = "ROLE_";

	@Setter
	private PreAuthConfigProperties preauth;

	@Bean
	public LoginSuccessListener loginSuccessListener(AppUsers playerService) {
		return new LoginSuccessListener(playerService);
	}

	@Bean
	public WebSecurityCustomizer webSecurityCustomizer() {
		return (web) -> {
			web.debug(debug);
			web.ignoring().requestMatchers("/");
			if (!enabled) {
				web.ignoring().requestMatchers("/**");
			}
		};
	}

	@Bean
	public SecurityFilterChain clientFilterChain(HttpSecurity http) throws Exception {

		if (enabled) {
			log.info("creating clientFilterChain");
			var permitAllMatchers = new AntPathRequestMatcher[] {
					new AntPathRequestMatcher("/"),
					new AntPathRequestMatcher("/login/**"),
					new AntPathRequestMatcher("/logged-out"),
					new AntPathRequestMatcher("/error/**"),
					new AntPathRequestMatcher("/webjars/**"),
					new AntPathRequestMatcher("/oauth**"),
					new AntPathRequestMatcher("/api/public/**"),
			};

			log.info("permitAllMatchers: {}", permitAllMatchers.length);
			for (AntPathRequestMatcher antPathRequestMatcher : permitAllMatchers) {
				log.info("permitAll matcher: {}", antPathRequestMatcher.getPattern());
			}

			http.authorizeHttpRequests((authorize) -> authorize
					.requestMatchers(permitAllMatchers).permitAll()
					.anyRequest().authenticated())
					.oauth2ResourceServer(oauth2 -> oauth2.bearerTokenResolver(bearerTokenResolver())
							.jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter())));
			if (preauth.isEnabled()) {
				http.addFilter(new PreAuthFilter(preauth));
			}
		} else {
			http.authorizeHttpRequests(authz -> authz.requestMatchers("/**").permitAll());
		}

		if (!useCsrf) {
			http.csrf(c -> c.disable());
		}

		log.info("", http);

		return http.build();
	}

	private BearerTokenResolver bearerTokenResolver() {
		ProxyBearerTokenResolver bearerTokenResolver = new ProxyBearerTokenResolver();
		bearerTokenResolver.setBearerTokenHeaderName(bearerTokenHeaderName);
		//var bearerTokenResolver = new HeaderBearerTokenResolver(bearerTokenHeaderName);
		return bearerTokenResolver;
	}

	private JwtAuthenticationConverter jwtAuthenticationConverter() {
		// create a custom JWT converter to map the roles from the token as granted
		// authorities
		JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
		jwtGrantedAuthoritiesConverter.setAuthoritiesClaimName(authoritiesClaimName); // default is: scope, scp
		jwtGrantedAuthoritiesConverter.setAuthorityPrefix(authorityPrefix); // default is: SCOPE_

		JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
		jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwtGrantedAuthoritiesConverter);
		return jwtAuthenticationConverter;
	}
}