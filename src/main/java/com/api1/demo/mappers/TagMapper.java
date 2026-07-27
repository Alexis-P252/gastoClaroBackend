package com.api1.demo.mappers;

import com.api1.demo.dto.request.TagRequest;
import com.api1.demo.dto.response.TagResponse;
import com.api1.demo.entity.Tag;

import java.util.List;

public class TagMapper {

    private TagMapper() {}

    public static TagResponse toResponse(Tag tag) {
        return new TagResponse(tag.getId(), tag.getName());
    }

    public static List<TagResponse> toResponseList(List<Tag> tags) {
        return tags.stream().map(TagMapper::toResponse).toList();
    }

    // Acá sí tiene sentido un toEntity(): Tag es simple, sin relaciones que
    // resolver más que el "user", que se asigna después en el Service.
    public static Tag toEntity(TagRequest request) {
        Tag tag = new Tag();
        tag.setName(request.name());
        return tag;
    }
}
