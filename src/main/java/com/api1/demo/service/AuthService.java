package com.api1.demo.service;

import com.api1.demo.entity.User;
import com.api1.demo.repository.UserRepository;
import com.api1.demo.security.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    public String register(String email, String rawPassword) {
        if (userRepository.existsByEmail(email)) {
            throw new IllegalStateException("Ya existe una cuenta con ese email");
        }

        User user = new User();
        user.setEmail(email);
        // Nunca se guarda la contraseña tal cual la mandó el cliente, solo su hash.
        user.setPasswordHash(passwordEncoder.encode(rawPassword));
        userRepository.save(user);

        return jwtService.generateToken(user);
    }

    public String login(String email, String rawPassword) {
        // AuthenticationManager valida email + password contra CustomUserDetailsService
        // y el PasswordEncoder. Si falla, tira una excepción de Spring Security
        // (BadCredentialsException) que el GlobalExceptionHandler todavía no mapea
        // explícitamente, así que cae en el handler genérico -> hay que sumarla ahí.
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, rawPassword));

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalStateException("Usuario no encontrado"));

        return jwtService.generateToken(user);
    }
}