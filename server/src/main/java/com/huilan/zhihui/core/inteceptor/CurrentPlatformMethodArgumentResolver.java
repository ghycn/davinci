

package com.huilan.zhihui.core.inteceptor;

import com.huilan.core.annotation.CurrentPlatform;
import com.huilan.core.consts.Consts;
import com.huilan.core.inteceptor.CurrentPlatformMethodArgumentResolverInterface;
import com.huilan.zhihui.model.Platform;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.method.support.ModelAndViewContainer;

/**
 * @CurrentPlatform 注解 解析器
 */
public class CurrentPlatformMethodArgumentResolver implements CurrentPlatformMethodArgumentResolverInterface {
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterType().isAssignableFrom(Platform.class)
                && parameter.hasParameterAnnotation(CurrentPlatform.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
        return  (Platform) webRequest.getAttribute(Consts.CURRENT_PLATFORM, RequestAttributes.SCOPE_REQUEST);
    }
}