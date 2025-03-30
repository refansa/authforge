package com.thumbleweed.authforge.core.exception;

/**
 * AuthForge base exception class
 */
public class AuthForgeException extends Exception {
    public static final String DEFAULT_KEY = "authforge.exception";

    public AuthForgeException() {
        super();
    }

    public AuthForgeException(String message) {
        super(message);
    }

    public String getTranslationKey() {
        return DEFAULT_KEY;
    }
}
