package com.thumbleweed.authforge.core.exception;

public class RegisterUsageException extends RegisterException {
    @Override
    public String getTranslationKey() {
        return "authforge.register.usage";
    }
}
