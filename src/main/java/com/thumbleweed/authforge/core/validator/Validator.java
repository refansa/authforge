package com.thumbleweed.authforge.core.validator;

import com.thumbleweed.authforge.core.Payload;
import com.thumbleweed.authforge.core.exception.AuthForgeException;

public interface Validator {
    void validate(Payload payload) throws AuthForgeException;
}