package com.javaspringboot_tutorial.security.configuration;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

//OncePerRequestFilter
@Component
public class AppConfig extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        response.setHeader("Access-Control-Allow-Origin", "http://localhost:3000/");
        filterChain.doFilter(request, response);
    }
}


//CorsFilter
//@Configuration
//public class AppConfig {
//    @Bean
//    public FilterRegistrationBean<CorsFilter> corsFilter(){
//        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//        CorsConfiguration config = new CorsConfiguration();
//        config.setAllowCredentials(true);
//        config.setAllowedMethods(List.of("GET", "POST","PUT","DELETE"));
//        config.setAllowedOrigins(List.of("http://localhost:3000"));
//        config.setAllowedHeaders(List.of("*"));
//        source.registerCorsConfiguration("/users/**", config);
//        FilterRegistrationBean bean = new FilterRegistrationBean<>(new CorsFilter());
//        bean.setOrder(Ordered.HIGHEST_PRECEDENCE);
//        return bean;
//
//    }
//}

// WebMvcConfigure CACH 1
//public class AppConfig implements WebMvcConfigurer {
//    @Override
//    public void addCorsMappings(CorsRegistry registry) {
//        WebMvcConfigurer.super.addCorsMappings(registry);
//        registry.addMapping("/**")
//                .allowCredentials(true)
//                .allowedOrigins("http://localhost:3000/")
//                .allowedMethods("*")
//                .allowedHeaders("*");
//
//    }
//}

// WebMvcConfigure CACH 2
//public class AppConfig{
//
//    @Bean
//    public WebMvcConfigurer corsFilters(){
//        return new WebMvcConfigurer() {
//            @Override
//            public void addCorsMappings(CorsRegistry registry) {
//                WebMvcConfigurer.super.addCorsMappings(registry);
//                registry.addMapping("/**").allowedOrigins("http://localhost:3000/");
//            }
//
//        };
//    }
//}