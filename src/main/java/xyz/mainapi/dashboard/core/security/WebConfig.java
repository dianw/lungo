package xyz.mainapi.dashboard.core.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableSpringDataWebSupport
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
            .allowedOriginPatterns("*")
            .allowedMethods("HEAD", "GET", "POST", "PUT", "DELETE", "PATCH")
            .allowCredentials(true)
            .allowedHeaders("Authorization", "Cache-Control", "Content-Type");
    }
}
