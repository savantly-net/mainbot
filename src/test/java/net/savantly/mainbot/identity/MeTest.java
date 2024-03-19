package net.savantly.mainbot.identity;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.servlet.view.RedirectView;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class MeTest {

    private Me me;
    private UserContext userContext;
    private ApplicationEventPublisher applicationEventPublisher;

    @BeforeEach
    public void setUp() {
        userContext = mock(UserContext.class);
        applicationEventPublisher = mock(ApplicationEventPublisher.class);
        me = new Me(userContext, applicationEventPublisher);
    }

    @Test
    public void testGetAuthorization() {
        // given
        HttpServletRequest request = mock(HttpServletRequest.class);
        UserDto userDto = new UserDto();
        when(userContext.getCurrentUser()).thenReturn(java.util.Optional.of(userDto));

        // when
        UserDto result = me.getAuthorization(request);

        // then
        verify(applicationEventPublisher).publishEvent(org.mockito.ArgumentMatchers.any(MeAuthorizedEvent.class));
        // assert other expectations
    }

    @Test
    public void testLogoutView() {
        // given
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        // when
        RedirectView result = me.logoutView(request, response);

        // then
        // TODO: verify(me).logout(request, response);
        // assert other expectations
    }

}