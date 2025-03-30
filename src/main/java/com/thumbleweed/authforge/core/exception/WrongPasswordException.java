package com.thumbleweed.authforge.core.exception;

/**
 * When the login password is incorrect.
 */
public class WrongPasswordException extends LoginException {
    @Override
    public String getTranslationKey() {
        return "authforge.wrongPassword";
    }
}
