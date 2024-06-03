package com.hualiang.security.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.hualiang.security.filter.RequestLimitIntercept;

@EnableWebMvc
@Configuration
public class MvcConfig implements WebMvcConfigurer {

    @Autowired
    private RequestLimitIntercept requestLimitIntercept;
    
    @Override
    public void addViewControllers(@NonNull ViewControllerRegistry registry) {
        registry.addViewController("/register").setViewName("register");
        registry.addViewController("/index").setViewName("index");
        registry.addViewController("/main").setViewName("main");
        registry.addViewController("/user/create").setViewName("user_create");
        registry.addViewController("/user/personal/modify").setViewName("user_modify");
        registry.addViewController("/user/upload").setViewName("user_upload");
        registry.addViewController("/hotel/registeration").setViewName("hotel_registeration");
    }

    @SuppressWarnings("null")
    @Override
    public void addInterceptors(@NonNull InterceptorRegistry registry) {
        registry.addInterceptor(requestLimitIntercept);
    }

    @Override
    public void addResourceHandlers(@NonNull ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/doc.html").addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/static/**").addResourceLocations("classpath:/static/");
    }

}