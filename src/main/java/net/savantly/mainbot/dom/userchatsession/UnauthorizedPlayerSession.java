package net.savantly.mainbot.dom.userchatsession;

public class UnauthorizedPlayerSession extends RuntimeException {
    
    private static final long serialVersionUID = 1L;

    public UnauthorizedPlayerSession(String currentUserId, String sessionOwnerId) {
        super("Unauthorized player session. Current User id:" + currentUserId + " Session User id: " + sessionOwnerId);
    }
}
