package com.thumbleweed.authforge.core.exception;

public class PlayerAlreadyExistException extends RegisterException {
    @Override
    public String getTranslationKey() {
        return "authforge.register.exist";
    }
}
