package net.savantly.mainbot.identity;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * This class represents a user DTO.
 * It contains the user's information.
 */
@Getter
@Setter
@Accessors(chain = true)
@ToString
public class UserDto {

	private String uid;
	private String username;
	private String name;
	private String givenName;
	private String familyName;
	private String email;
	private String title;
	private String phoneNumber;
	private List<String> groups = new ArrayList<>();
	private String token;

	public UserSummaryDto toSummary() {
		return new UserSummaryDto()
			.setEmail(this.email)
			.setName(this.name)
			.setUid(this.uid);
	}

}
