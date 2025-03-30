package com.thumbleweed.authforge.core.exception;

/**
 * Raises exception if the new user-provided password is different from the confirmation password.
 */
public class WrongPasswordConfirmationException extends RegisterException {
    @Override
    public String getTranslationKey() {
        return "authforge.wrongPasswordConfirmation";
    }
}
