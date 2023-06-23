package com.baeldung.rws.web.dto;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import com.baeldung.rws.domain.model.Project;

public record ProjectDto( // @formatter:off

    Long id,

    String code,

    String name,

    String description,

    Set<TaskDto> tasks) { // @formatter:on

    public static class Mapper {
        public static Project toModel(ProjectDto dto) {
            if (dto == null)
                return null;

            Project model = new Project(dto.code(), dto.name(), dto.description());
            if (!Objects.isNull(dto.id())) {
                model.setId(dto.id());
            }
            // we won't allow creating or modifying Projects via a Task
            return model;
        }

        public static ProjectDto toDto(Project model) {
            if (model == null)
                return null;
            Set<TaskDto> tasks = model.getTasks()
                .stream()
                .map(TaskDto.Mapper::toDto)
                .collect(Collectors.toSet());
            ProjectDto dto = new ProjectDto(model.getId(), model.getCode(), model.getName(), model.getDescription(), tasks);
            return dto;
        }
    }
}
