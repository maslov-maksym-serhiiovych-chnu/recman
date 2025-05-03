package edu.chnu.recman_backend.auth.services;

import edu.chnu.recman_backend.auth.dtos.LoginRequest;
import edu.chnu.recman_backend.auth.dtos.LoginResponse;
import edu.chnu.recman_backend.auth.dtos.RegisterRequest;
import edu.chnu.recman_backend.auth.dtos.RegisterResponse;
import edu.chnu.recman_backend.auth.models.Role;
import edu.chnu.recman_backend.auth.models.User;
import edu.chnu.recman_backend.auth.repositories.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final UserRepository repository;
    private final JwtService jwtService;
    private final AuthenticationManager manager;
    private final PasswordEncoder encoder;

    public AuthService(UserRepository repository, JwtService jwtService, AuthenticationManager manager, PasswordEncoder encoder) {
        this.repository = repository;
        this.jwtService = jwtService;
        this.manager = manager;
        this.encoder = encoder;
    }

    public RegisterResponse register(RegisterRequest request) {
        User user = new User();
        user.setUsername(request.username());
        user.setPassword(encoder.encode(request.password()));
        user.setRole(Role.USER);

        repository.save(user);
        return new RegisterResponse(user.getUsername());
    }

    public LoginResponse login(LoginRequest request) {
        manager.authenticate(new UsernamePasswordAuthenticationToken(request.username(), request.password()));

        User user = repository.findByUsername(request.username()).orElseThrow();
        return new LoginResponse(jwtService.generateToken(user));
    }
}
