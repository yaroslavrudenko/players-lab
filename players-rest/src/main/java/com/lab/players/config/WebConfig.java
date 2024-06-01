package com.lab.players.config;

import com.lab.players.rest.PageableHandlerMethodArgumentResolver;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.config.WebFluxConfigurer;
import org.springframework.web.reactive.result.method.annotation.ArgumentResolverConfigurer;

@Configuration
public class WebConfig implements WebFluxConfigurer {
    @Override
    public void configureArgumentResolvers(ArgumentResolverConfigurer configurer) {
        configurer.addCustomResolver(new PageableHandlerMethodArgumentResolver());
    }
}
