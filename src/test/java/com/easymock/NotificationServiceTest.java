// src/test/java/com/example/NotificationServiceTest.java
package com.easymock;

import org.easymock.EasyMock;
import org.easymock.EasyMockRunner;
import org.easymock.Mock;
import org.easymock.TestSubject;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.assertEquals;

@RunWith(EasyMockRunner.class)
public class NotificationServiceTest {

    @Mock
    private UserService userServiceMock;

    @Mock
    private EmailGateway emailGatewayMock;

    @TestSubject
    private NotificationService notificationService = new NotificationService(userServiceMock, emailGatewayMock);

    @Test
    public void testNotifyUserSuccess() throws EmailException {
        // Record expectations
        expect(userServiceMock.getUserName(1)).andReturn("John Doe");
        expect(emailGatewayMock.isEmailQueueEmpty()).andReturn(true);
        emailGatewayMock.sendAsync("John Doe", "Test message", 5000L);
        expectLastCall().once();
        expect(userServiceMock.sendEmail("John Doe", "Test message")).andReturn(true);

        // Replay
        replay(userServiceMock, emailGatewayMock);

        // Test
        String result = notificationService.notifyUser(1, "Test message", 5000L);
        assertEquals("Notification sent to John Doe", result);

        // Verify
        verify(userServiceMock, emailGatewayMock);
    }

    @Test
    public void testNotifyUserNotFound() {
        // Record expectations
        expect(userServiceMock.getUserName(2)).andReturn(null);

        // Replay
        replay(userServiceMock, emailGatewayMock);

        // Test
        String result = notificationService.notifyUser(2, "Test message", 5000L);
        assertEquals("User not found", result);

        // Verify
        verify(userServiceMock, emailGatewayMock);
    }

    @Test
    public void testNotifyUserEmailQueueFull() {
        // Record expectations
        expect(userServiceMock.getUserName(3)).andReturn("Jane Doe");
        expect(emailGatewayMock.isEmailQueueEmpty()).andReturn(false);

        // Replay
        replay(userServiceMock, emailGatewayMock);

        // Test
        String result = notificationService.notifyUser(3, "Test message", 5000L);
        assertEquals("Email queue full, retry later", result);

        // Verify
        verify(userServiceMock, emailGatewayMock);
    }

    @Test
    public void testNotifyUserEmailDeliveryFailed() throws EmailException {
        // Record expectations
        expect(userServiceMock.getUserName(4)).andReturn("Peter Pan");
        expect(emailGatewayMock.isEmailQueueEmpty()).andReturn(true);
        emailGatewayMock.sendAsync("Peter Pan", "Another message", 3000L);
        expectLastCall().andThrow(new EmailException("SMTP server down"));

        // Replay
        replay(userServiceMock, emailGatewayMock);

        // Test
        String result = notificationService.notifyUser(4, "Another message", 3000L);
        assertEquals("Email delivery failed: SMTP server down", result);

        // Verify
        verify(userServiceMock, emailGatewayMock);
    }
}
