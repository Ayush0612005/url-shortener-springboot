package com.ayush.urlshortener.controller;

import com.ayush.urlshortener.service.UrlService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequiredArgsConstructor
@Tag(
        name = "Redirect",
        description = "Redirects short URLs to their original destination"
)
public class RedirectController {
    private final UrlService urlService;

    @Operation(
            summary = "Redirect to original URL",
            description = "Redirects the client to the original URL using the short code."
    )
    @GetMapping("/{shortCode}")
    public ResponseEntity<Void> redirect(
            @PathVariable String shortCode) {

        String originalUrl = urlService.getOriginalUrl(shortCode);

        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(originalUrl))
                .build();
    }
}
