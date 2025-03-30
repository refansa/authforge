package com.thumbleweed.authforge.core;

import com.thumbleweed.authforge.core.exception.AuthForgeException;

public interface Guard {
    boolean authenticate(Payload payload) throws AuthForgeException;

    boolean register(Payload payload) throws AuthForgeException;

    boolean updatePassword(Payload newPayload) throws AuthForgeException;
}