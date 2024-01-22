package net.savantly.mainbot.identity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
@ToString
public class UserSummaryDto {

	private String uid;
	private String name;
	private String email;

}
