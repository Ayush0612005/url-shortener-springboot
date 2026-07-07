package com.ayush.urlshortener.service;

import com.ayush.urlshortener.entity.Url;
import com.ayush.urlshortener.entity.User;
import com.ayush.urlshortener.exception.UnauthorizedUrlAccessException;
import com.ayush.urlshortener.redis.RedisService;
import com.ayush.urlshortener.repository.UrlRepository;
import com.ayush.urlshortener.util.Base62Encoder;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UrlServiceTest {

    @Mock
    private UrlRepository urlRepository;

    @Mock
    private Base62Encoder base62Encoder;

    @Mock
    private RedisService redisService;

    @InjectMocks
    private UrlService urlService;

    @Test
    void deleteUrl_shouldDeleteOwnedUrl() {

        // Arrange
        User currentUser = User.builder()
                .id(1L)
                .name("Ayush")
                .email("ayush@gmail.com")
                .build();

        Url url = Url.builder()
                .id(10L)
                .shortCode("abc123")
                .originalUrl("https://google.com")
                .user(currentUser)
                .build();

        Authentication authentication = mock(Authentication.class);

        SecurityContext context =
                SecurityContextHolder.createEmptyContext();

        context.setAuthentication(authentication);

        SecurityContextHolder.setContext(context);

        when(authentication.getPrincipal())
                .thenReturn(currentUser);

        when(urlRepository.findById(10L))
                .thenReturn(Optional.of(url));

        // Act
        urlService.deleteUrl(10L);

        // Assert
        verify(redisService).delete("abc123");

        verify(urlRepository).delete(url);
    }
    @Test
    void deleteUrl_shouldThrowException_whenUserIsNotOwner() {

        User owner = User.builder()
                .id(1L)
                .build();

        User anotherUser = User.builder()
                .id(2L)
                .build();

        Url url = Url.builder()
                .id(10L)
                .user(owner)
                .build();

        Authentication authentication = mock(Authentication.class);

        SecurityContext context =
                SecurityContextHolder.createEmptyContext();

        context.setAuthentication(authentication);

        SecurityContextHolder.setContext(context);

        when(authentication.getPrincipal())
                .thenReturn(anotherUser);

        when(urlRepository.findById(10L))
                .thenReturn(Optional.of(url));

        assertThrows(
                UnauthorizedUrlAccessException.class,
                () -> urlService.deleteUrl(10L)
        );

        verify(urlRepository, never()).delete(any());
    }

}
