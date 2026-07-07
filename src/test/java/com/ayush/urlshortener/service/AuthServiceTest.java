package com.ayush.urlshortener.service;

import com.ayush.urlshortener.dto.AuthResponse;
import com.ayush.urlshortener.dto.LoginRequest;
import com.ayush.urlshortener.exception.EmailAlreadyExistsException;
import com.ayush.urlshortener.exception.InvalidCredentialsException;
import com.ayush.urlshortener.repository.UserRepository;
import com.ayush.urlshortener.security.JwtService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.ayush.urlshortener.dto.RegisterRequest;
import com.ayush.urlshortener.entity.User;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthService authService;

    @Test
    void register_shouldSaveUser_whenEmailDoesNotExist() {

        // Arrange
        RegisterRequest request = RegisterRequest.builder()
                .name("Ayush")
                .email("ayush@gmail.com")
                .password("password123")
                .build();

        when(userRepository.findByEmail(request.getEmail()))
                .thenReturn(Optional.empty());

        when(passwordEncoder.encode(request.getPassword()))
                .thenReturn("encodedPassword");

        // Act
        authService.register(request);

        // Assert
        ArgumentCaptor<User> userCaptor =
                ArgumentCaptor.forClass(User.class);

        verify(userRepository).save(userCaptor.capture());

        User savedUser = userCaptor.getValue();

        assertEquals("Ayush", savedUser.getName());
        assertEquals("ayush@gmail.com", savedUser.getEmail());
        assertEquals("encodedPassword", savedUser.getPassword());
    }
    @Test
    void register_shouldThrowException_whenEmailAlreadyExists() {

        // Arrange
        RegisterRequest request = RegisterRequest.builder()
                .name("Ayush")
                .email("ayush@gmail.com")
                .password("password123")
                .build();

        User existingUser = User.builder()
                .id(1L)
                .name("Ayush")
                .email("ayush@gmail.com")
                .password("encodedPassword")
                .build();

        when(userRepository.findByEmail(request.getEmail()))
                .thenReturn(Optional.of(existingUser));

        // Act + Assert
        EmailAlreadyExistsException exception =
                assertThrows(
                        EmailAlreadyExistsException.class,
                        () -> authService.register(request)
                );

        assertEquals("Email already exists", exception.getMessage());

        verify(userRepository, never()).save(any(User.class));
    }
    @Test
    void login_shouldReturnToken_whenCredentialsAreValid() {

        // Arrange
        LoginRequest request = LoginRequest.builder()
                .email("ayush@gmail.com")
                .password("password123")
                .build();

        User user = User.builder()
                .id(1L)
                .name("Ayush")
                .email("ayush@gmail.com")
                .password("encodedPassword")
                .build();

        when(userRepository.findByEmail(request.getEmail()))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches(
                request.getPassword(),
                user.getPassword()))
                .thenReturn(true);

        when(jwtService.generateToken(user))
                .thenReturn("fake-jwt-token");

        // Act
        AuthResponse response = authService.login(request);

        // Assert
        assertEquals("fake-jwt-token", response.getToken());

        verify(userRepository).findByEmail(request.getEmail());

        verify(passwordEncoder)
                .matches(request.getPassword(), user.getPassword());

        verify(jwtService).generateToken(user);
    }
    @Test
    void login_shouldThrowException_whenUserDoesNotExist() {

        // Arrange
        LoginRequest request = LoginRequest.builder()
                .email("ayush@gmail.com")
                .password("password123")
                .build();

        when(userRepository.findByEmail(request.getEmail()))
                .thenReturn(Optional.empty());

        // Act + Assert
        InvalidCredentialsException exception =
                assertThrows(
                        InvalidCredentialsException.class,
                        () -> authService.login(request)
                );

        assertEquals(
                "Invalid email or password",
                exception.getMessage()
        );

        verify(passwordEncoder, never()).matches(any(), any());

        verify(jwtService, never()).generateToken(any());
    }
    @Test
    void login_shouldThrowException_whenPasswordIsIncorrect() {

        // Arrange
        LoginRequest request = LoginRequest.builder()
                .email("ayush@gmail.com")
                .password("wrongPassword")
                .build();

        User user = User.builder()
                .id(1L)
                .name("Ayush")
                .email("ayush@gmail.com")
                .password("encodedPassword")
                .build();

        when(userRepository.findByEmail(request.getEmail()))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches(
                request.getPassword(),
                user.getPassword()))
                .thenReturn(false);

        // Act + Assert
        InvalidCredentialsException exception =
                assertThrows(
                        InvalidCredentialsException.class,
                        () -> authService.login(request)
                );

        assertEquals(
                "Invalid email or password",
                exception.getMessage()
        );

        verify(jwtService, never()).generateToken(any());
    }
}
