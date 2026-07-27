package com.api1.demo.repository;

import com.api1.demo.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TagRepository extends JpaRepository<Tag, UUID> {

    List<Tag> findByUserId(UUID userId);

    Optional<Tag> findByIdAndUserId(UUID id, UUID userId);
}
