package com.example.java101.web;

import com.example.java101.domain.User;
import com.example.java101.dto.ApiMapper;
import com.example.java101.dto.TokenResponse;
import com.example.java101.dto.UserCreateRequest;
import com.example.java101.dto.UserResponse;
import com.example.java101.exception.DomainExceptions;
import com.example.java101.security.JwtService;
import com.example.java101.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;
    private final JwtService jwtService;

    public AuthController(UserService userService, JwtService jwtService) {
        this.userService = userService;
        this.jwtService = jwtService;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse register(@Valid @RequestBody UserCreateRequest request) {
        return ApiMapper.toUser(userService.create(request));
    }

    @PostMapping("/login")
    public TokenResponse login(
            @RequestParam("username") String username, @RequestParam("password") String password) {
        User user = userService.authenticate(username, password);
        if (user == null) {
            throw DomainExceptions.incorrectCredentials();
        }
        return TokenResponse.bearer(jwtService.createAccessToken(user.getEmail()));
    }

    @GetMapping("/me")
    public UserResponse me(@AuthenticationPrincipal User user) {
        if (user == null) {
            throw DomainExceptions.notAuthenticated();
        }
        return ApiMapper.toUser(user);
    }
}
