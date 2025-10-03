package com.example.springexample.cloudeservice.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorize -> authorize
                        // React статические файлы
                        .requestMatchers("/","config.js", "index-BoXi_CZW.js:267", "index-BdL5i7zc.css", "index-BoXi_CZW.js", "icon-BA2QZDzm.png", "/index.html", "/static/**", "/css/**", "/js/**", "/images/**", "/favicon.ico", "/assets/**").permitAll()

                        // React client-side роуты
                        .requestMatchers("/login", "/register", "/dashboard").permitAll()

                        // Открытые API
                        .requestMatchers("/api/auth/**", "/api/directory/**").permitAll()
                        .requestMatchers("/api/public/**").permitAll()

                        // Защищённые API
//                        .requestMatchers().authenticated()
//                        .requestMatchers("/api/directory").authenticated()
                        .requestMatchers("/api/user/me").authenticated()

                        // Всё остальное
                        .anyRequest().authenticated()
                )
                .formLogin(AbstractHttpConfigurer::disable) // если делаешь логин через REST
                .logout(logout -> logout
                        .logoutUrl("/api/auth/sign-out")
                        .deleteCookies("SESSION") // у тебя Redis session → кука называется "SESSION"
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                );
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


//    @Bean
//    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        http
//                .csrf(AbstractHttpConfigurer::disable)
//                .authorizeHttpRequests(authorizeRequests -> authorizeRequests
//                        .requestMatchers("/**").permitAll()
////                        .requestMatchers("/api/public/**").permitAll()
//                        .anyRequest().authenticated())
//                .logout(logout -> logout
//                        .logoutUrl("/api/auth/sign-out")
//                        .deleteCookies("SESSION")
//                        .invalidateHttpSession(true)
//                        .clearAuthentication(true));
//
//        return http.build();
//    }
}
