package com.thumbleweed.authforge.core.validator;

import com.thumbleweed.authforge.core.Payload;
import com.thumbleweed.authforge.core.exception.AuthForgeException;
import com.thumbleweed.authforge.core.exception.WrongPasswordConfirmationException;
import com.thumbleweed.authforge.core.exception.WrongRegisterUsageException;

public class RegisterValidator implements Validator {
    @Override
    public void validate(Payload payload) throws AuthForgeException {
        int numberOfArgs = 2;
        if (payload.getArgs().length != numberOfArgs) {
            throw new WrongRegisterUsageException();
        }
        String password = payload.getArgs()[numberOfArgs - 2];
        String passwordConfirmation = payload.getArgs()[numberOfArgs - 1];
        if (!password.equals(passwordConfirmation)) {
            throw new WrongPasswordConfirmationException();
        }
    }
}
