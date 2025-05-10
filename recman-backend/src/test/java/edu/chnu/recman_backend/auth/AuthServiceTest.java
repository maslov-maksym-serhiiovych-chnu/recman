package edu.chnu.recman_backend.auth;

import edu.chnu.recman_backend.auth.dtos.LoginRequest;
import edu.chnu.recman_backend.auth.dtos.LoginResponse;
import edu.chnu.recman_backend.auth.dtos.RegisterRequest;
import edu.chnu.recman_backend.auth.dtos.RegisterResponse;
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
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.NoSuchElementException;
import java.util.Optional;

class AuthServiceTest {
    private static final RegisterRequest REGISTER_REQUEST = new RegisterRequest("user", "password");
    private static final LoginRequest LOGIN_REQUEST = new LoginRequest("user", "password");
    private static final String ENCODED_PASSWORD = "encoded-password";
    
    private static final UsernameAlreadyExistsException USERNAME_ALREADY_EXISTS_EXCEPTION =
            new UsernameAlreadyExistsException();
    
    private static final User USER = new User("user", ENCODED_PASSWORD, Role.USER);
    private static final String TOKEN = "token";
    
    private static final BadCredentialsException BAD_CREDENTIALS_EXCEPTION =
            new BadCredentialsException("Bad credentials");

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
        Mockito.when(encoder.encode(REGISTER_REQUEST.password())).thenReturn(ENCODED_PASSWORD);

        RegisterResponse response = service.register(REGISTER_REQUEST);

        Mockito.verify(repository).save(Mockito.argThat(user ->
                user.getUsername().equals(REGISTER_REQUEST.username()) &&
                        user.getPassword().equals(ENCODED_PASSWORD) &&
                        user.getRole() == Role.USER));

        Assertions.assertEquals(REGISTER_REQUEST.username(), response.username());
    }

    @Test
    void register_shouldThrowExceptionIfUsernameAlreadyExists() {
        Mockito.when(repository.existsByUsername(REGISTER_REQUEST.username())).thenReturn(true);

        UsernameAlreadyExistsException ex = Assertions.assertThrows(UsernameAlreadyExistsException.class, () ->
                service.register(REGISTER_REQUEST));

        Assertions.assertEquals(USERNAME_ALREADY_EXISTS_EXCEPTION.getMessage(), ex.getMessage());
    }

    @Test
    void login_shouldAuthenticateAndReturnToken() {
        Mockito.when(repository.findByUsername(USER.getUsername())).thenReturn(Optional.of(USER));
        Mockito.when(jwtService.generateToken(USER)).thenReturn(TOKEN);

        LoginResponse response = service.login(LOGIN_REQUEST);

        Mockito.verify(authenticationManager).authenticate(new UsernamePasswordAuthenticationToken(
                LOGIN_REQUEST.username(),
                LOGIN_REQUEST.password()));

        Assertions.assertEquals(TOKEN, response.token());
    }

    @Test
    void login_shouldThrowIfUserNotFound() {
        Mockito.when(authenticationManager.authenticate(Mockito.any())).thenReturn(Mockito.mock(Authentication.class));
        Mockito.when(repository.findByUsername(USER.getUsername())).thenReturn(Optional.empty());

        Assertions.assertThrows(NoSuchElementException.class, () -> service.login(LOGIN_REQUEST));
    }

    @Test
    void login_shouldThrowIfAuthenticationFails() {
        Mockito.doThrow(BAD_CREDENTIALS_EXCEPTION).when(authenticationManager).authenticate(Mockito.any());

        BadCredentialsException ex = Assertions.assertThrows(BadCredentialsException.class, () -> 
                service.login(LOGIN_REQUEST));
        
        Assertions.assertEquals(BAD_CREDENTIALS_EXCEPTION.getMessage(), ex.getMessage());
    }
}
