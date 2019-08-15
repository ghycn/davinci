

package com.huilan.zhihui.core.inteceptor;

import com.alibaba.druid.util.StringUtils;
import com.alibaba.fastjson.JSONObject;
import com.huilan.core.annotation.AuthIgnore;
import com.huilan.core.annotation.AuthShare;
import com.huilan.core.enums.HttpCodeEnum;
import com.huilan.core.utils.TokenUtils;
import com.huilan.zhihui.core.common.Constants;
import com.huilan.zhihui.core.common.ResultMap;
import com.huilan.zhihui.model.User;
import com.huilan.zhihui.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

@Slf4j
public class AuthenticationInterceptor implements HandlerInterceptor {

    @Autowired
    private TokenUtils tokenUtils;

    @Autowired
    private UserService userService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        HandlerMethod handlerMethod = null;
        try {
            handlerMethod = (HandlerMethod) handler;
        } catch (Exception e) {
            response.setStatus(HttpCodeEnum.NOT_FOUND.getCode());
            return false;
        }
        Method method = handlerMethod.getMethod();

        AuthIgnore ignoreAuthMethod = method.getAnnotation(AuthIgnore.class);
        //注解不需要验证token
        if (handler instanceof HandlerMethod && null != ignoreAuthMethod) {
            return true;
        }

        String token = request.getHeader(Constants.TOKEN_HEADER_STRING);

        AuthShare authShareMethoed = method.getAnnotation(AuthShare.class);
        if (handler instanceof HandlerMethod && null != authShareMethoed) {
            if (!StringUtils.isEmpty(token) && token.startsWith(Constants.TOKEN_PREFIX)) {
                String username = tokenUtils.getUsername(token);
                User user = userService.getByUsername(username);
                request.setAttribute(Constants.CURRENT_USER, user);
            }
            return true;
        }

        if (StringUtils.isEmpty(token) || !token.startsWith(Constants.TOKEN_PREFIX)) {
            log.info("{} : Unknown token", request.getServletPath());
            response.setStatus(HttpCodeEnum.FORBIDDEN.getCode());
            response.getWriter().print("The resource requires authentication, which was not supplied with the request");
            return false;
        }
        String username = tokenUtils.getUsername(token);
        User user = userService.getByUsername(username);
        if (null == user) {
            log.info("{} : token user not found", request.getServletPath());
            response.setStatus(HttpCodeEnum.FORBIDDEN.getCode());
            response.getWriter().print("ERROR Permission denied");
            return false;

        }
        if (!tokenUtils.validateToken(token, user)) {
            log.info("{} : token validation fails", request.getServletPath());
            response.setStatus(HttpCodeEnum.FORBIDDEN.getCode());
            response.getWriter().print("Invalid token ");
            return false;
        }

        if (request.getServletPath().indexOf("/user/active") < 0 && !user.getActive()) {
            if (request.getServletPath().indexOf("/user/sendmail") > -1) {
                request.setAttribute(Constants.CURRENT_USER, user);
                return true;
            }
            log.info("current user is not activated, username: {}", user.getUsername());
            response.setStatus(HttpCodeEnum.FAIL.getCode());
            ResultMap resultMap = new ResultMap(tokenUtils);
            response.getWriter().print(JSONObject.toJSONString(resultMap.failAndRefreshToken(request).message("Account not active yet. Please check your email to activate your account")));
            return false;
        }
        request.setAttribute(Constants.CURRENT_USER, user);
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }
}
