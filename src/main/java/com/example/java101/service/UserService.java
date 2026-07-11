package com.example.java101.service;

import com.example.java101.domain.User;
import com.example.java101.dto.UserCreateRequest;
import com.example.java101.exception.DomainExceptions;
import com.example.java101.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public User getByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }

    @Transactional
    public User create(UserCreateRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw DomainExceptions.userEmailExists(request.email());
        }
        User user = new User();
        user.setEmail(request.email());
        user.setHashedPassword(passwordEncoder.encode(request.password()));
        return userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public User authenticate(String email, String password) {
        User user = getByEmail(email);
        if (user == null || !passwordEncoder.matches(password, user.getHashedPassword())) {
            return null;
        }
        return user;
    }
}
