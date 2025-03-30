package com.thumbleweed.authforge.core.exception;

public class WrongLoginUsageException extends LoginException {
    @Override
    public String getTranslationKey() {
        return "authforge.login.usage";
    }
}
