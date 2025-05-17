package edu.chnu.recman_backend.auth.services;

import edu.chnu.recman_backend.auth.dtos.LoginRequest;
import edu.chnu.recman_backend.auth.dtos.RegisterRequest;
import edu.chnu.recman_backend.auth.exceptions.UsernameAlreadyExistsException;
import edu.chnu.recman_backend.auth.models.Role;
import edu.chnu.recman_backend.auth.models.User;
import edu.chnu.recman_backend.auth.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository repository;
    private final JwtService jwtService;
    private final AuthenticationManager manager;
    private final PasswordEncoder encoder;

    public void register(RegisterRequest request) {
        if (repository.existsByUsername(request.username())) {
            throw new UsernameAlreadyExistsException();
        }

        User user = new User(request.username(), encoder.encode(request.password()), Role.USER);

        repository.save(user);
    }

    public String login(LoginRequest request) {
        manager.authenticate(new UsernamePasswordAuthenticationToken(request.username(), request.password()));

        User user = repository.findByUsername(request.username()).orElseThrow();
        return jwtService.generateToken(user);
    }

    public User getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        
        return repository.findByUsername(username).orElseThrow(() ->
                new UsernameNotFoundException("Username not found"));
    }
}
