package net.savantly.mainbot.identity;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class AnonymousUser {

    private String username = "anonymous";
    private String email = "anonymous@savantly.net";
    private String name = "Anonymous";
    private String givenName = "Anonymous";
    private String familyName = "Anonymous";
    private String uid = "anonymous";
    private String[] groups = new String[] {"anonymous"};

    public UserDto toDto() {
        return new UserDto()
                .setEmail(email)
                .setFamilyName(familyName)
                .setGivenName(givenName)
                .setName(name)
                .setUid(uid)
                .setUsername(username);
    }
}
