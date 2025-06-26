package com.easymock;

public class NotificationService {
    private UserService userService;
    private EmailGateway emailGateway;

    public NotificationService(UserService userService, EmailGateway emailGateway) {
        this.userService = userService;
        this.emailGateway = emailGateway;
    }

    public String notifyUser(int userId, String message, long timeoutMs) {
        String userName = userService.getUserName(userId);
        if (userName == null) {
            return "User not found";
        }

        try {
            if (!emailGateway.isEmailQueueEmpty()) {
                return "Email queue full, retry later";
            }
            emailGateway.sendAsync(userName, message, timeoutMs);
            if (!userService.sendEmail(userName, message)) {
                throw new RuntimeException("Email send failed after async attempt");
            }
            return "Notification sent to " + userName;
        } catch (EmailException e) {
            return "Email delivery failed: " + e.getMessage();
        } catch (RuntimeException e) {
            return "Internal error: " + e.getMessage();
        }
    }
}