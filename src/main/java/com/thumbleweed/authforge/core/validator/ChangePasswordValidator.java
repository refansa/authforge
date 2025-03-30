package com.thumbleweed.authforge.core.validator;

import com.thumbleweed.authforge.core.Payload;
import com.thumbleweed.authforge.core.exception.AuthForgeException;
import com.thumbleweed.authforge.core.exception.SamePasswordException;
import com.thumbleweed.authforge.core.exception.WrongPasswordConfirmationException;

public class ChangePasswordValidator implements Validator {
    @Override
    public void validate(Payload payload) throws AuthForgeException {
        if (payload.getArgs().length != 3) return;

        if (!payload.getArgs()[1].contentEquals(payload.getArgs()[2])) throw new WrongPasswordConfirmationException();

        if (payload.getArgs()[0].contentEquals(payload.getArgs()[1])) throw new SamePasswordException();
    }
}
