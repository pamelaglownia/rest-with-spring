package com.baeldung.rws.web.dto;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.groups.Default;

import com.baeldung.rws.domain.model.Project;

public record ProjectDto ( // @formatter:off

    Long id,

    @NotBlank(message = "code can't be null")
    String code,

    @NotBlank(groups = { ProjectUpdateValidationData.class, Default.class },
      message = "name can't be blank")
    String name,

    @Size(groups = { ProjectUpdateValidationData.class, Default.class },
      min = 10, max = 50,
      message = "description must be between 10 and 50 characters long")
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

            // we won't allow creating or modifying Tasks via the Project
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

    public interface ProjectUpdateValidationData {
    }

}
