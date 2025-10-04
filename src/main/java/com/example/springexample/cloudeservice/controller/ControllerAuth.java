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
    public ResponseEntity<?> signIn(@RequestBody UsersSignUpDto usersSignUp, HttpServletRequest request) {

        var user = authService.autentifiactionUser(usersSignUp); // проверка логина и пароля
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Неверные данные");
        }

        UserDetails userDetails = customUserDetailsService.loadUserByUsername(usersSignUp.username());
        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

        // сохраняем аутентификацию
        SecurityContextHolder.getContext().setAuthentication(authToken);
        request.getSession().setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                SecurityContextHolder.getContext());

        return ResponseEntity.ok(Map.of(
                "username", user.username()
        ));
    }

    @PostMapping("/sign-up")
    public ResponseEntity<?> signUp(@RequestBody UsersSignUpDto usersSignUp, HttpServletRequest request) {
        request.getSession(true);
        return ResponseEntity.ok(authService.signUp(usersSignUp));
    }


}
