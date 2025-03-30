package com.thumbleweed.authforge.core.exception;

public class BannedPlayerException extends LoginException {
    @Override
    public String getTranslationKey() {
        return "authforge.banned";
    }
}
