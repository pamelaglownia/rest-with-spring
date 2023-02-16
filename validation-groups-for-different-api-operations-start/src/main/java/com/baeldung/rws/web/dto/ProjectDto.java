package com.baeldung.rws.web.dto;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import com.baeldung.rws.domain.model.Project;

public record ProjectDto( // @formatter:off

    Long id,

    @NotBlank(message = "code can't be null")
    String code,

    @NotBlank(message = "name can't be blank")
    String name,

    @Size(min = 10, max = 50,
    message = "description must be between 10 and 50 characters long")
    String description,

    Set<TaskDto> tasks) { // @formatter:on

    public static class Mapper {
        public static Project toModel(ProjectDto dto) {
            // we won't allow creating or modifying Tasks via the Project
            Project model = new Project(dto.code(), dto.name(), dto.description());
            if (!Objects.isNull(dto.id())) {
                model.setId(dto.id());
            }
            return model;
        }

        public static ProjectDto toDto(Project model) {
            Set<TaskDto> tasks = model.getTasks()
                .stream()
                .map(TaskDto.Mapper::toDto)
                .collect(Collectors.toSet());
            ProjectDto dto = new ProjectDto(model.getId(), model.getCode(), model.getName(), model.getDescription(), tasks);
            return dto;
        }
    }
}
