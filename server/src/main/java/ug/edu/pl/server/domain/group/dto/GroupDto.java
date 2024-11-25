package ug.edu.pl.server.domain.group.dto;

import lombok.Builder;
import ug.edu.pl.server.domain.common.storage.dto.ImageDto;

import java.time.Instant;

@Builder
public record GroupDto(
    Long id,
    ImageDto image,
    String name,
    Boolean settledDown,
    Long version,
    Instant createdAt,
    Instant updatedAt) {}
