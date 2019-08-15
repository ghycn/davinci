

package com.huilan.zhihui.core.config;

import com.huilan.core.enums.HttpCodeEnum;
import com.huilan.core.exception.ForbiddenExecption;
import com.huilan.core.exception.NotFoundException;
import com.huilan.core.exception.ServerException;
import com.huilan.core.exception.UnAuthorizedExecption;
import com.huilan.core.utils.TokenUtils;
import com.huilan.zhihui.core.common.ResultMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@ControllerAdvice(annotations = RestController.class)
@Slf4j
public class RestExceptionHandler {

    @Autowired
    private TokenUtils tokenUtils;

    @ExceptionHandler
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    private ResultMap commonExceptionHandler(HttpServletRequest request, Exception e) {
        e.printStackTrace();
        log.error(e.getMessage());
        return new ResultMap(tokenUtils).failAndRefreshToken(request).message(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
    }

    @ExceptionHandler(value = ServerException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    private ResultMap serverExceptionHandler(HttpServletRequest request, Exception e) {
        e.printStackTrace();
        log.error(e.getMessage());
        return new ResultMap(tokenUtils).failAndRefreshToken(request).message(e.getMessage());
    }

    @ExceptionHandler(value = ForbiddenExecption.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.FORBIDDEN)
    private ResultMap forbiddenExceptionHandler(HttpServletRequest request, Exception e) {
        log.error(e.getMessage());
        return new ResultMap(tokenUtils).failAndRefreshToken(request, HttpCodeEnum.FORBIDDEN).message(e.getMessage());
    }

    @ExceptionHandler(value = UnAuthorizedExecption.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    private ResultMap unAuthorizedExceptionHandler(HttpServletRequest request, Exception e) {
        log.error(e.getMessage());
        return new ResultMap(tokenUtils).failAndRefreshToken(request, HttpCodeEnum.UNAUTHORIZED).message(e.getMessage());
    }

    @ExceptionHandler(value = NotFoundException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    private ResultMap notFoundExceptionHandler(HttpServletRequest request, Exception e) {
        log.error(e.getMessage());
        return new ResultMap(tokenUtils).failAndRefreshToken(request, HttpCodeEnum.NOT_FOUND).message(e.getMessage());
    }

}
