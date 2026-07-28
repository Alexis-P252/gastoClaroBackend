package com.api1.demo.controller;

import com.api1.demo.dto.request.CategoryRequest;
import com.api1.demo.dto.response.CategoryResponse;
import com.api1.demo.entity.User;
import com.api1.demo.mappers.CategoryMapper;
import com.api1.demo.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService){
        this.categoryService = categoryService;
    }

    @GetMapping
    public List<CategoryResponse> list (@AuthenticationPrincipal User currentUser){
        return CategoryMapper.toResponseList(categoryService.listForUser(currentUser.getId()));
    }

    @PostMapping
    public ResponseEntity<CategoryResponse> create(@AuthenticationPrincipal User currentUser,
                                                   @Valid @RequestBody CategoryRequest request){

        var category = categoryService.create(currentUser, request.name(), request.type());
        return ResponseEntity.status(HttpStatus.CREATED).body(CategoryMapper.toResponse(category));
    }

    @PutMapping("/{id}")
    public CategoryResponse update(@AuthenticationPrincipal User currentUser,
                                   @PathVariable UUID id,
                                   @Valid @RequestBody CategoryRequest request) {
        var category = categoryService.update(id, currentUser.getId(), request.name(), request.type());
        return CategoryMapper.toResponse(category);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@AuthenticationPrincipal User currentUser, @PathVariable UUID id) {
        categoryService.delete(id, currentUser.getId());
        return ResponseEntity.noContent().build();
    }

}
