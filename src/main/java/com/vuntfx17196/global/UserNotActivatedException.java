package com.vuntfx17196.global;

import javax.naming.AuthenticationException;

public class UserNotActivatedException extends AuthenticationException {
    public UserNotActivatedException(String explanation) {
        super(explanation);
    }

    public UserNotActivatedException() {
    }
}
