package net.savantly.mainbot.identity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * This class represents a user summary DTO.
 * It contains the user's summary information.
 */
@Getter
@Setter
@Accessors(chain = true)
@ToString
public class UserSummaryDto {

	private String uid;
	private String name;
	private String email;

}
