package net.savantly.mainbot.identity;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;
import lombok.Setter;

@Configuration
@ConfigurationProperties("app.identity")
@Getter @Setter
public class IdentityConfiguration {
    

	private String familyNameClaim = "family_name";
	private String givenNameClaim = "given_name";
	private String nameClaim = "name";
	private String emailClaim = "email";
	private String usernameClaim = "preferred_username";
	private String groupsClaim = "groups";
	private String uidClaim = "sub";

    private AnonymousUser anonymous = new AnonymousUser();

    @Bean
    public UserDtoConverter userDtoConverter() {
        return UserDtoConverter.builder()
            .familyNameClaim(familyNameClaim)
            .givenNameClaim(givenNameClaim)
            .nameClaim(nameClaim)
            .emailClaim(emailClaim)
            .usernameClaim(usernameClaim)
            .groupsClaim(groupsClaim)
            .uidClaim(uidClaim)
            .build();
    }

    @Bean
    public UserContext userContext(UserDtoConverter converter) {
        return new UserContext(converter, anonymous);
    }

    @Bean
    public AnonymousUser anonymousUser() {
        return anonymous;
    }
}
