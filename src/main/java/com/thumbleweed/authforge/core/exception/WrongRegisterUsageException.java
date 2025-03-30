package com.thumbleweed.authforge.core.exception;

public class WrongRegisterUsageException extends RegisterException {
    @Override
    public String getTranslationKey() {
        return "authforge.register.usage";
    }
}
