package com.ayush.urlshortener.controller;

import com.ayush.urlshortener.dto.AnalyticsResponse;
import com.ayush.urlshortener.dto.CreateShortUrlRequest;
import com.ayush.urlshortener.dto.UpdateExpirationRequest;
import com.ayush.urlshortener.dto.UrlResponse;
import com.ayush.urlshortener.service.UrlService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

import io.swagger.v3.oas.annotations.Operation;
import java.util.List;
import io.swagger.v3.oas.annotations.tags.Tag;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@RestController
@RequestMapping("/api/urls")
@RequiredArgsConstructor
@Tag(name = "URL Management", description = "URL Shortener APIs")
@SecurityRequirement(name = "Bearer Authentication")
public class UrlController {

    private final UrlService urlService;
    @Operation(
            summary = "Create a short URL",
            description = "Generates a unique Base62 short URL for the given original URL."
    )
    @PostMapping
    public ResponseEntity<UrlResponse> createShortUrl(
            @Valid @RequestBody CreateShortUrlRequest request) {

        UrlResponse response = urlService.createShortUrl(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }
    @Operation(
            summary = "Get my URLs",
            description = "Returns all URLs created by the authenticated user."
    )
    @GetMapping("/my-urls")
    public ResponseEntity<List<UrlResponse>> getMyUrls() {

        return ResponseEntity.ok(urlService.getMyUrls());

    }
    @Operation(
            summary = "Delete a URL",
            description = "Deletes one of the authenticated user's shortened URLs."
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUrl(@PathVariable Long id) {

        urlService.deleteUrl(id);

        return ResponseEntity.noContent().build();
    }
    @Operation(
            summary = "Update expiration date",
            description = "Updates the expiration date of an existing shortened URL."
    )
    @PutMapping("/{id}/expiration")
    public ResponseEntity<String> updateExpiration(
            @PathVariable Long id,
            @Valid @RequestBody UpdateExpirationRequest request) {

        urlService.updateExpiration(id, request);

        return ResponseEntity.ok("Expiration updated successfully");
    }

    @Operation(
            summary = "Get URL analytics",
            description = "Returns analytics including click count, last accessed time, and status."
    )
    @GetMapping("/{id}/analytics")
    public ResponseEntity<AnalyticsResponse> getAnalytics(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                urlService.getAnalytics(id)
        );
    }
}
