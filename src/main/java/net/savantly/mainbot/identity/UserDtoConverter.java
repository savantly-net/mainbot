package net.savantly.mainbot.identity;

import org.springframework.security.oauth2.jwt.Jwt;

import lombok.Builder;

@Builder
public class UserDtoConverter {

	private final String uidClaim;
	private final String familyNameClaim;
	private final String givenNameClaim;
	private final String nameClaim;
	private final String emailClaim;
	private final String usernameClaim;
	private final String groupsClaim;

	public UserDto fromJwt(Jwt jwt) {
		UserDto dto = new UserDto();
		dto
		.setUid(jwt.getClaimAsString(uidClaim))
		.setFamilyName(jwt.getClaimAsString(familyNameClaim))
		.setGivenName(jwt.getClaimAsString(givenNameClaim))
		.setName(jwt.getClaimAsString(nameClaim))
		.setEmail(jwt.getClaimAsString(emailClaim))
		.setUsername(jwt.getClaimAsString(usernameClaim))
		.setGroups(jwt.getClaimAsStringList(groupsClaim));
		return dto;
	}
}
