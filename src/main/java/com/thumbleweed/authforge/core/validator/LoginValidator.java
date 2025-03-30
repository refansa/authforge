package com.thumbleweed.authforge.core.validator;

import com.thumbleweed.authforge.core.Payload;
import com.thumbleweed.authforge.core.exception.AuthForgeException;
import com.thumbleweed.authforge.core.exception.WrongLoginUsageException;

public class LoginValidator implements Validator {
    @Override
    public void validate(Payload payload) throws AuthForgeException {
        int numberOfArgs = 1;
        if (payload.getArgs().length != numberOfArgs) {
            throw new WrongLoginUsageException();
        }
    }
}