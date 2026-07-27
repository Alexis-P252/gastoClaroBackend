package com.api1.demo.dto.response;

import java.util.UUID;

public record CategoryResponse (UUID id, String name, String type) {
}
