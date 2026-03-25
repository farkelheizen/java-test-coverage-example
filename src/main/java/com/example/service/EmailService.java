package com.example.service;

import java.util.List;
import java.util.Map;

public class EmailService {

    public boolean sendEmail(String to, String subject, String body) {
        ValidationService validationService = new ValidationService();
        if (!validationService.validateEmail(to)) {
            throw new IllegalArgumentException("Invalid email address: " + to);
        }
        if (subject == null || subject.isBlank()) {
            throw new IllegalArgumentException("Subject cannot be blank");
        }
        // Simulate sending
        return true;
    }

    public int sendBulkEmail(List<String> recipients, String subject, String body) {
        int successCount = 0;
        for (String recipient : recipients) {
            try {
                if (sendEmail(recipient, subject, body)) {
                    successCount++;
                }
            } catch (IllegalArgumentException e) {
                // Skip invalid emails
            }
        }
        return successCount;
    }

    public String formatEmailBody(String template, Map<String, String> variables) {
        String result = template;
        for (Map.Entry<String, String> entry : variables.entrySet()) {
            result = result.replace("{{" + entry.getKey() + "}}", entry.getValue());
        }
        return result;
    }
}
