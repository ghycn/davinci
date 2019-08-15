

package com.huilan.core.exception;

import com.huilan.core.enums.HttpCodeEnum;

public class UnAuthorizedExecption extends RuntimeException {

    public UnAuthorizedExecption(String message, Throwable cause) {
        super(message, cause);
    }

    public UnAuthorizedExecption(String message) {
        super(message);
    }


    public UnAuthorizedExecption() {
        super(HttpCodeEnum.UNAUTHORIZED.getMessage());
    }
}
