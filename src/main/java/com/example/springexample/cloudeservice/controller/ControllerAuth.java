package com.example.springexample.cloudeservice.controller;

import com.example.springexample.cloudeservice.dto.UsersSignUpDto;
import com.example.springexample.cloudeservice.service.AuthService;
import com.example.springexample.cloudeservice.service.CustomUserDetailsService;
import com.example.springexample.cloudeservice.service.MinioService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class ControllerAuth {


    private final MinioService minioService;
    private final AuthService authService;
    private final CustomUserDetailsService customUserDetailsService;

    @PostMapping("/sign-in")
    public ResponseEntity<?> signIn(@RequestBody UsersSignUpDto usersSignIn, HttpServletRequest request) {

        var user = authService.autentifiactionUser(usersSignIn);

        UserDetails userDetails = customUserDetailsService.loadUserByUsername(usersSignIn.username());
        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

        // сохраняем аутентификацию
        SecurityContextHolder.getContext().setAuthentication(authToken);
        request.getSession().setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                SecurityContextHolder.getContext());

        return ResponseEntity.ok(user);
    }

    @PostMapping("/sign-up")
    public ResponseEntity<?> signUp(@RequestBody UsersSignUpDto usersSignUp, HttpServletRequest request) {
        var user=authService.signUp(usersSignUp);

        UserDetails userDetails = org.springframework.security.core.userdetails.User
                .withUsername(user.username())
                .password(usersSignUp.password()) // можно временно взять из DTO
                .roles("USER")
                .build();

        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(authToken);
        request.getSession().setAttribute(
                HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                SecurityContextHolder.getContext()
        );

        return ResponseEntity.ok(user);
    }


}
