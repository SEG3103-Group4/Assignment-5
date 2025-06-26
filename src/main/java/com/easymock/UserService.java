package com.easymock;

public interface UserService {
    String getUserName(int userId);
    boolean sendEmail(String userName, String message);
}