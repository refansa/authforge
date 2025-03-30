package com.thumbleweed.authforge.core.exception;

/**
 * On changing password,
 * When the new password is the same as the old password.
 */
public class SamePasswordException extends ChangePasswordException {
    @Override
    public String getTranslationKey() {
        return "authforge.changepassword.samePassword";
    }
}
