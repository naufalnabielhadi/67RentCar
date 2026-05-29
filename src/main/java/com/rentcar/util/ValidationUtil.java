package com.rentcar.util;

public class ValidationUtil {
    private ValidationUtil() {
    }

    public static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    public static boolean isValidEmail(String email) {
        return !isBlank(email) && email.contains("@") && email.contains(".");
    }

    public static String generateId(String prefix) {
        return prefix + System.currentTimeMillis();
    }
}
