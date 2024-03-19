package net.savantly.mainbot.security;

import lombok.Data;

/**
 * This class represents a configuration properties class that is used to configure the pre-authentication filter.
 * It contains properties that are used to configure the pre-authentication filter.
 */
@Data
public class PreAuthConfigProperties {
    
    private boolean enabled = false;
    private String userIdHeaderName = "x-forwarded-user";
    private String usernameHeaderName = "x-forwarded-preferred-username";
    private String groupsHeaderName = "x-forwarded-groups";
    private String emailHeaderName = "x-forwarded-email";
}
