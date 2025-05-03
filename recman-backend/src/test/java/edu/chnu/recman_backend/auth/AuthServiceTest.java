package edu.chnu.recman_backend.auth;

import edu.chnu.recman_backend.auth.dtos.LoginRequest;
import edu.chnu.recman_backend.auth.dtos.LoginResponse;
import edu.chnu.recman_backend.auth.dtos.RegisterRequest;
import edu.chnu.recman_backend.auth.exceptions.UsernameAlreadyExistsException;
import edu.chnu.recman_backend.auth.models.Role;
import edu.chnu.recman_backend.auth.models.User;
import edu.chnu.recman_backend.auth.repositories.UserRepository;
import edu.chnu.recman_backend.auth.services.AuthService;
import edu.chnu.recman_backend.auth.services.JwtService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.NoSuchElementException;
import java.util.Optional;

@SpringBootTest
class AuthServiceTest {
    private static final RegisterRequest REGISTER_REQUEST = new RegisterRequest("user", "password");
    private static final LoginRequest LOGIN_REQUEST = new LoginRequest("user", "password");

    private UserRepository repository;
    private JwtService jwtService;
    private AuthenticationManager authenticationManager;
    private PasswordEncoder encoder;
    private AuthService service;

    @BeforeEach
    void setUp() {
        repository = Mockito.mock(UserRepository.class);
        jwtService = Mockito.mock(JwtService.class);
        authenticationManager = Mockito.mock(AuthenticationManager.class);
        encoder = Mockito.mock(PasswordEncoder.class);
        service = new AuthService(repository, jwtService, authenticationManager, encoder);
    }

    @Test
    void register_shouldCreateUserWithEncodedPasswordAndDefaultRole() {
        Mockito.when(encoder.encode("password")).thenReturn(
                "encoded-password");

        var response = service.register(REGISTER_REQUEST);

        Mockito.verify(repository).save(Mockito.argThat(user ->
                user.getUsername().equals("user") &&
                        user.getPassword().equals("encoded-password") &&
                        user.getRole() == Role.USER));

        Assertions.assertEquals("user", response.username());
    }

    @Test
    void register_shouldThrowExceptionIfUsernameAlreadyExists() {
        Mockito.when(repository.existsByUsername("user")).thenReturn(true);

        UsernameAlreadyExistsException ex = Assertions.assertThrows(UsernameAlreadyExistsException.class, () -> service.register(REGISTER_REQUEST));

        Assertions.assertEquals("Username already exists", ex.getMessage());
    }

    @Test
    void login_shouldAuthenticateAndReturnToken() {
        User user = new User();
        user.setUsername("user");
        user.setPassword("encoded-password");
        user.setRole(Role.USER);

        Mockito.when(repository.findByUsername("user")).thenReturn(Optional.of(user));
        Mockito.when(jwtService.generateToken(user)).thenReturn("jwt-token");

        LoginResponse response = service.login(LOGIN_REQUEST);

        Mockito.verify(authenticationManager).authenticate(new UsernamePasswordAuthenticationToken(
                "user",
                "password"));

        Assertions.assertEquals("jwt-token", response.token());
    }

    @Test
    void login_shouldThrowIfUserNotFound() {
        Mockito.when(authenticationManager.authenticate(Mockito.any())).thenReturn(Mockito.mock(Authentication.class));
        Mockito.when(repository.findByUsername("user")).thenReturn(Optional.empty());

        Assertions.assertThrows(NoSuchElementException.class, () -> service.login(LOGIN_REQUEST));
    }

    @Test
    void login_shouldThrowIfAuthenticationFails() {
        Mockito.doThrow(new BadCredentialsException("Bad credentials"))
                .when(authenticationManager)
                .authenticate(Mockito.any());

        Assertions.assertThrows(BadCredentialsException.class, () -> service.login(LOGIN_REQUEST));
    }
}
