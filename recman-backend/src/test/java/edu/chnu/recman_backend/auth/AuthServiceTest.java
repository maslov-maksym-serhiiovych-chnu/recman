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
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.NoSuchElementException;
import java.util.Optional;

class AuthServiceTest {
    private static final RegisterRequest REGISTER_REQUEST = new RegisterRequest("user", "password");
    private static final LoginRequest LOGIN_REQUEST = new LoginRequest("user", "password");
    private static final String ENCODED_PASSWORD = "encoded-password";
    private static final User USER = new User("user", "password", Role.USER);
    private static final String TOKEN = "token";

    private static final BadCredentialsException BAD_CREDENTIALS_EXCEPTION =
            new BadCredentialsException("Bad credentials");

    private static final UsernameNotFoundException USERNAME_NOT_FOUND_EXCEPTION =
            new UsernameNotFoundException("Username not found");

    @Mock
    private UserRepository repository;

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private PasswordEncoder encoder;

    @Mock
    private Authentication authentication;

    @Mock
    private SecurityContext context;

    @InjectMocks
    private AuthService service;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this).close();
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

        Assertions.assertEquals(new UsernameAlreadyExistsException().getMessage(), ex.getMessage());
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

    @Test
    void getCurrentUser_shouldReturnUserFromSecurityContext() {
        Mockito.when(authentication.getName()).thenReturn(USER.getUsername());
        Mockito.when(context.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(context);

        Mockito.when(repository.findByUsername(USER.getUsername())).thenReturn(Optional.of(USER));

        User result = service.getCurrentUser();

        Assertions.assertEquals(USER.getUsername(), result.getUsername());
        Assertions.assertEquals(USER.getPassword(), result.getPassword());
        Assertions.assertEquals(USER.getRole(), result.getRole());
    }

    @Test
    void getCurrentUser_shouldThrowIfUserNotFound() {
        Mockito.when(authentication.getName()).thenReturn(USER.getUsername());
        Mockito.when(context.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(context);

        Mockito.when(repository.findByUsername(USER.getUsername())).thenReturn(Optional.empty());

        UsernameNotFoundException ex = Assertions.assertThrows(UsernameNotFoundException.class, () ->
                service.getCurrentUser());

        Assertions.assertEquals(USERNAME_NOT_FOUND_EXCEPTION.getMessage(), ex.getMessage());
    }
}
