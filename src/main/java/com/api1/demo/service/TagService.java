package com.api1.demo.service;

import com.api1.demo.entity.Tag;
import com.api1.demo.entity.User;
import com.api1.demo.exception.ResourceNotFoundException;
import com.api1.demo.repository.TagRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class TagService {

    private final TagRepository tagRepository;

    public TagService(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    public List<Tag> listForUser(UUID userId) {
        return tagRepository.findByUserId(userId);
    }

    public Tag create(User user, String name) {
        Tag tag = new Tag();
        tag.setUser(user);
        tag.setName(name);
        return tagRepository.save(tag);
    }

    public Tag getOwned(UUID tagId, UUID userId) {
        return tagRepository.findByIdAndUserId(tagId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Tag no encontrado"));
    }

    public void delete(UUID tagId, UUID userId) {
        // getOwned ya garantiza que el tag existe y es del usuario antes de borrar.
        Tag tag = getOwned(tagId, userId);
        tagRepository.delete(tag);
    }
}