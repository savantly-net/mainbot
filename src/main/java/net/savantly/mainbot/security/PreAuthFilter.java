package net.savantly.mainbot.security;

import java.util.List;

import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;

import jakarta.servlet.http.HttpServletRequest;
import net.savantly.mainbot.identity.UserDto;

/**
 * This class represents a pre-authentication filter that extends the AbstractPreAuthenticatedProcessingFilter class.
 * It is responsible for extracting user information from the request headers and creating a UserDto object with the extracted information.
 * The UserDto object is then used as the principal for authentication.
 */
public class PreAuthFilter extends AbstractPreAuthenticatedProcessingFilter {

    private final PreAuthConfigProperties preauth;

    public PreAuthFilter(PreAuthConfigProperties preauth) {
        this.preauth = preauth;
    }

    @Override
    protected Object getPreAuthenticatedPrincipal(HttpServletRequest request) {
        try {
            String uid = request.getHeader(preauth.getUserIdHeaderName());

            if (uid == null) {
                return null;
            }

            String username = request.getHeader(preauth.getUsernameHeaderName());
            String email = request.getHeader(preauth.getEmailHeaderName());

            String groups = request.getHeader(preauth.getGroupsHeaderName());

            return new UserDto()
                .setUid(uid)
                .setEmail(email)
                .setUsername(username)
                .setGroups(parseGroups(groups));
            
        } catch (Exception e) {
            return null;
        }
    }

    private List<String> parseGroups(String groups) {
        if (groups == null) {
            return List.of();
        }
        return List.of(groups.split(","));
    }

    @Override
    protected Object getPreAuthenticatedCredentials(HttpServletRequest request) {
        return "N/A";
    }

}
