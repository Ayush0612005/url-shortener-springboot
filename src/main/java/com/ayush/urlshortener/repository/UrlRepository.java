package com.ayush.urlshortener.repository;

import com.ayush.urlshortener.entity.Url;
import com.ayush.urlshortener.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UrlRepository extends JpaRepository<Url, Long> {

    Optional<Url> findByShortCode(String shortCode);

    List<Url> findByUser(User user);

    boolean existsByShortCode(String shortCode);


}
