

package com.huilan.zhihui.core.config;

import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.serializer.ValueFilter;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import com.huilan.core.inteceptor.RequestJsonHandlerArgumentResolver;
import com.huilan.zhihui.core.common.Constants;
import com.huilan.zhihui.core.inteceptor.AuthenticationInterceptor;
import com.huilan.zhihui.core.inteceptor.CurrentPlatformMethodArgumentResolver;
import com.huilan.zhihui.core.inteceptor.CurrentUserMethodArgumentResolver;
import com.huilan.zhihui.core.inteceptor.PlatformAuthInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import static com.huilan.core.consts.Consts.EMPTY;


@Configuration
public class WebMvcConfig extends WebMvcConfigurationSupport {

    @Value("${file.userfiles-path}")
    private String filePath;

    @Value("${file.web_resources}")
    private String webResources;

    /**
     * 登录校验拦截器
     *
     * @return
     */
    @Bean
    public AuthenticationInterceptor loginRequiredInterceptor() {
        return new AuthenticationInterceptor();
    }

    /**
     * 授权平台校验拦截器
     *
     * @return
     */
    @Bean
    public PlatformAuthInterceptor platformAuthInterceptor() {
        return new PlatformAuthInterceptor();
    }

    /**
     * CurrentUser 注解参数解析器
     *
     * @return
     */
    @Bean
    public CurrentUserMethodArgumentResolver currentUserMethodArgumentResolver() {
        return new CurrentUserMethodArgumentResolver();
    }

    /**
     * CurrentPlatform 注解参数解析器
     *
     * @return
     */
    @Bean
    public CurrentPlatformMethodArgumentResolver currentPlatformMethodArgumentResolver() {
        return new CurrentPlatformMethodArgumentResolver();
    }

    /**
     * JsonParam 参数解析器
     *
     * @return
     */
    @Bean
    public RequestJsonHandlerArgumentResolver requestJsonHandlerArgumentResolver() {
        return new RequestJsonHandlerArgumentResolver();
    }

    /**
     * 参数解析器
     *
     * @param argumentResolvers
     */
    @Override
    protected void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(currentUserMethodArgumentResolver());
        argumentResolvers.add(currentPlatformMethodArgumentResolver());
        argumentResolvers.add(requestJsonHandlerArgumentResolver());
        super.addArgumentResolvers(argumentResolvers);
    }

    @Override
    protected void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginRequiredInterceptor())
                .addPathPatterns(Constants.BASE_API_PATH + "/**")
                .excludePathPatterns(Constants.BASE_API_PATH + "/login");

        registry.addInterceptor(platformAuthInterceptor())
                .addPathPatterns(Constants.AUTH_API_PATH + "/**");

        super.addInterceptors(registry);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/META-INF/resources/")
                .addResourceLocations("classpath:/META-INF/resources/webjars")
                .addResourceLocations("classpath:/static/")
                .addResourceLocations("classpath:/static/page/")
                .addResourceLocations("classpath:/static/templates/")
                .addResourceLocations("file:" + webResources)
                .addResourceLocations("file:" + filePath);

    }


    @Override
    protected void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        FastJsonHttpMessageConverter fastConverter = new FastJsonHttpMessageConverter();
        FastJsonConfig fastJsonConfig = new FastJsonConfig();
        fastJsonConfig.setSerializerFeatures(SerializerFeature.QuoteFieldNames,
                SerializerFeature.WriteEnumUsingToString,
                SerializerFeature.WriteMapNullValue,
                SerializerFeature.WriteDateUseDateFormat,
                SerializerFeature.DisableCircularReferenceDetect);
        fastJsonConfig.setSerializeFilters((ValueFilter) (o, s, source) -> {
            if (null != source && (source instanceof Long || source instanceof BigInteger) && source.toString().length() > 15) {
                return source.toString();
            } else {
                return null == source ? EMPTY : source;
            }
        });

        //处理中文乱码问题
        List<MediaType> fastMediaTypes = new ArrayList<>();
        fastMediaTypes.add(MediaType.APPLICATION_JSON_UTF8);
        fastConverter.setSupportedMediaTypes(fastMediaTypes);
        fastConverter.setFastJsonConfig(fastJsonConfig);
        converters.add(fastConverter);
    }


}
