package com.thumbleweed.authforge.core.exception;

/**
 * Raises when the player is not found.
 */
public class PlayerNotFoundException extends LoginException {
    @Override
    public String getTranslationKey() {
        return "authforge.login.notFound";
    }
}
