package com.baeldung.rws.web.dto;

import java.time.LocalDate;
import java.util.Objects;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import org.springframework.format.annotation.DateTimeFormat;

import com.baeldung.rws.domain.model.Project;
import com.baeldung.rws.domain.model.Task;
import com.baeldung.rws.domain.model.TaskStatus;

public record TaskDto( // @formatter:off
    Long id,

    String uuid,

    @NotBlank(message = "name can't be blank")
    String name,

    @Size(min = 10, max = 50,
    message = "description must be between 10 and 50 characters long")
    String description,

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @Future(message = "dueDate must be in the future")
    LocalDate dueDate,

    TaskStatus status,

    @NotNull(message = "projectId can't be null")
    Long projectId,

    WorkerDto assignee,

    @Min(value = 1, message = "estimatedHours can't be less than 1")
    @Max(value = 40, message = "estimatedHours can't exceed 40")
    Integer estimatedHours) { // @formatter:on

    public static class Mapper {
        public static Task toModel(TaskDto dto) {
            // we won't allow creating or modifying Projects via a Task
            Project project = new Project();
            project.setId(dto.projectId());

            Task model = new Task(dto.name(), dto.description(), dto.dueDate(), project, dto.status(), WorkerDto.Mapper.toModel(dto.assignee()), dto.uuid(), dto.estimatedHours());
            if (!Objects.isNull(dto.id())) {
                model.setId(dto.id());
            }
            return model;
        }

        public static TaskDto toDto(Task model) {
            TaskDto dto = new TaskDto(model.getId(), model.getUuid(), model.getName(), model.getDescription(), model.getDueDate(), model.getStatus(), model.getProject()
                .getId(), WorkerDto.Mapper.toDto(model.getAssignee()), model.getEstimatedHours());
            return dto;
        }
    }
}
