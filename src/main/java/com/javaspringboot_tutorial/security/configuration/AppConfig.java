package com.javaspringboot_tutorial.security.configuration;

import com.javaspringboot_tutorial.security.service.UserService;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;


//OncePerRequestFilter
@Configuration
@RequiredArgsConstructor
public class AppConfig  {
    private final UserService userService;
    private final PreFilter preFilter;

//    @Override extends OncePerRequestFilter
//    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
//        response.setHeader("Access-Control-Allow-Origin", "http://localhost:3000/");
//        filterChain.doFilter(request, response);
//    }
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins("http://localhost:8500")
                        .allowedMethods("GET", "POST", "PUT", "DELETE") // Allowed HTTP methods
                        .allowedHeaders("*") // Allowed request headers
                        .allowCredentials(false)
                        .maxAge(3600);
            }
        };
    }
    @Bean
    public PasswordEncoder getPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain configure(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorizeRequests -> authorizeRequests.requestMatchers("/auth/**").permitAll().anyRequest().authenticated())
                .sessionManagement(manager -> manager.sessionCreationPolicy(STATELESS))
                .authenticationProvider(provider()).addFilterBefore(preFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public AuthenticationProvider provider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userService.userDetailsService());

        provider.setPasswordEncoder(getPasswordEncoder());

        return provider;
    }
}
// Using generated security password: 5b349bdd-2594-46c3-888b-0218c87fefd1

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