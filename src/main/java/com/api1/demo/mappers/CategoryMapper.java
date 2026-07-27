package com.api1.demo.mappers;

import java.util.List;
import com.api1.demo.dto.request.CategoryRequest;
import com.api1.demo.dto.response.CategoryResponse;
import com.api1.demo.entity.Category;

public class CategoryMapper {

    private CategoryMapper() {}

    public static CategoryResponse toResponse(Category category) {
        return new CategoryResponse(category.getId(), category.getName(), category.getType());
    }

    public static List<CategoryResponse> toResponseList(List<Category> categories) {
        return categories.stream().map(CategoryMapper::toResponse).toList();
    }

    // No arma la entidad completa: el "user" se asigna en el Service,
    // porque el mapper no debería depender de quién está logueado.
    public static Category toEntity(CategoryRequest request) {
        Category category = new Category();
        category.setName(request.name());
        category.setType(request.type());
        return category;
    }
}
