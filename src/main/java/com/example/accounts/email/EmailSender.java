package com.example.accounts.email;

import jakarta.mail.MessagingException;

public interface EmailSender {
    void send(String to, String message, String subject) throws MessagingException;
}
