package com.api1.demo.controller;

import com.api1.demo.dto.request.TagRequest;
import com.api1.demo.dto.response.TagResponse;
import com.api1.demo.entity.User;
import com.api1.demo.mappers.TagMapper;
import com.api1.demo.service.TagService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/tags")
public class TagController {

    private final TagService tagService;

    public TagController(TagService tagService) {
        this.tagService = tagService;
    }

    @GetMapping
    public List<TagResponse> list(@AuthenticationPrincipal User currentUser) {
        return TagMapper.toResponseList(tagService.listForUser(currentUser.getId()));
    }

    @PostMapping
    public ResponseEntity<TagResponse> create(@AuthenticationPrincipal User currentUser,
                                              @Valid @RequestBody TagRequest request) {
        var tag = tagService.create(currentUser, request.name());
        return ResponseEntity.status(HttpStatus.CREATED).body(TagMapper.toResponse(tag));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@AuthenticationPrincipal User currentUser, @PathVariable UUID id) {
        tagService.delete(id, currentUser.getId());
        return ResponseEntity.noContent().build();
    }
}