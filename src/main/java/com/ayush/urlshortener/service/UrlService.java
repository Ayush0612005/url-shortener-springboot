package com.ayush.urlshortener.service;

import com.ayush.urlshortener.dto.AnalyticsResponse;
import com.ayush.urlshortener.dto.CreateShortUrlRequest;
import com.ayush.urlshortener.dto.UpdateExpirationRequest;
import com.ayush.urlshortener.dto.UrlResponse;
import com.ayush.urlshortener.entity.Url;
import com.ayush.urlshortener.entity.User;
import com.ayush.urlshortener.exception.UnauthorizedUrlAccessException;
import com.ayush.urlshortener.exception.UrlNotFoundException;
import com.ayush.urlshortener.redis.RedisService;
import com.ayush.urlshortener.repository.UrlRepository;
import com.ayush.urlshortener.util.Base62Encoder;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.cache.annotation.Cacheable;


import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UrlService {

    private final UrlRepository urlRepository;
    private final Base62Encoder base62Encoder;
    private final RedisService redisService;

    @Value("${app.base-url}")
    private String baseUrl;

    public UrlResponse createShortUrl(CreateShortUrlRequest request) {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        User user = (User) authentication.getPrincipal();

        Url url = Url.builder()
                .originalUrl(request.getOriginalUrl())
                .expiresAt(request.getExpiresAt())
                .user(user)
                .build();

        // First save -> generates database ID
        url = urlRepository.save(url);

        // Generate Base62 short code
        String shortCode = base62Encoder.encode(url.getId());

        url.setShortCode(shortCode);

        // Save again with short code
        url = urlRepository.save(url);

        return mapToResponse(url);

    }
    @Cacheable(value = "urls", key = "#shortCode")
    public String getOriginalUrl(String shortCode) {

        // Check Redis first
        String cachedUrl = redisService.getOriginalUrl(shortCode);

        // Fetch entity from database for analytics
        Url url = urlRepository.findByShortCode(shortCode)
                .orElseThrow(() ->
                        new UrlNotFoundException("Short URL not found"));

        // Check expiration
        if (url.getExpiresAt() != null &&
                url.getExpiresAt().isBefore(LocalDateTime.now())) {

            throw new UrlNotFoundException("URL has expired");
        }

        // Always update analytics
        url.setClickCount(url.getClickCount() + 1);
        url.setLastAccessedAt(LocalDateTime.now());

        urlRepository.save(url);

        // Cache hit
        if (cachedUrl != null) {
            return cachedUrl;
        }

        // Cache miss
        redisService.saveOriginalUrl(
                shortCode,
                url.getOriginalUrl()
        );

        return url.getOriginalUrl();
    }

    public List<UrlResponse> getMyUrls() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        User user = (User) authentication.getPrincipal();

        List<Url> urls = urlRepository.findByUser(user);

        return urls.stream()
                .map(this::mapToResponse)
                .toList();
    }

    private UrlResponse mapToResponse(Url url) {

        return UrlResponse.builder()
                .originalUrl(url.getOriginalUrl())
                .shortCode(url.getShortCode())
                .shortUrl(baseUrl + "/" + url.getShortCode())
                .clickCount(url.getClickCount())
                .createdAt(url.getCreatedAt())
                .expiresAt(url.getExpiresAt())
                .build();
    }

    public void deleteUrl(Long id) {

        Url url = getOwnedUrl(id);

        redisService.delete(url.getShortCode());

        urlRepository.delete(url);
    }

    public void updateExpiration(
            Long id,
            UpdateExpirationRequest request) {

        Url url = getOwnedUrl(id);

        url.setExpiresAt(request.getExpiresAt());

        urlRepository.save(url);
    }
    private User getCurrentUser() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        return (User) authentication.getPrincipal();
    }
    public AnalyticsResponse getAnalytics(Long id) {

        Url url = getOwnedUrl(id);

        String status;

        if (url.getExpiresAt() == null) {
            status = "ACTIVE";
        } else if (url.getExpiresAt().isBefore(LocalDateTime.now())) {
            status = "EXPIRED";
        } else {
            status = "ACTIVE";
        }

        return AnalyticsResponse.builder()
                .originalUrl(url.getOriginalUrl())
                .shortCode(url.getShortCode())
                .shortUrl(baseUrl + "/" + url.getShortCode())
                .clickCount(url.getClickCount())
                .createdAt(url.getCreatedAt())
                .expiresAt(url.getExpiresAt())
                .lastAccessedAt(url.getLastAccessedAt())
                .status(status)
                .build();
    }
    private Url getOwnedUrl(Long id) {

        User currentUser = getCurrentUser();

        Url url = urlRepository.findById(id)
                .orElseThrow(() ->
                        new UrlNotFoundException("URL not found"));

        if (!url.getUser().getId().equals(currentUser.getId())) {
            throw new UnauthorizedUrlAccessException(
                    "You are not allowed to access this URL.");
        }

        return url;
    }
}


