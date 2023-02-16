package com.baeldung.rws.web.dto;

import java.time.LocalDate;
import java.util.Objects;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.groups.Default;

import org.springframework.format.annotation.DateTimeFormat;

import com.baeldung.rws.domain.model.Project;
import com.baeldung.rws.domain.model.Task;
import com.baeldung.rws.domain.model.TaskStatus;

public record TaskDto ( // @formatter:off
    Long id,

    String uuid,

    @NotBlank(groups = { TaskUpdateValidationData.class, Default.class })
    String name,

    String description,

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    LocalDate dueDate,

    @NotNull(groups = { TaskUpdateStatusValidationData.class, TaskUpdateValidationData.class })
    TaskStatus status,

    @NotNull(groups = { TaskUpdateValidationData.class, Default.class })
    @Positive(groups = { TaskUpdateValidationData.class, Default.class })
    Long projectId,

    @NotNull(groups = { TaskUpdateAssigneeValidationData.class })
    @Valid
    WorkerDto assignee) { // @formatter:on

    public static class Mapper {
        public static Task toModel(TaskDto dto) {
            if (dto == null)
                return null;

            Project project = new Project();
            project.setId(dto.projectId());

            Task model = new Task(dto.name(), dto.description(), dto.dueDate(), project, dto.status(), WorkerDto.Mapper.toModel(dto.assignee()), dto.uuid());
            if (!Objects.isNull(dto.id())) {
                model.setId(dto.id());
            }
            
            // we won't allow creating or modifying Projects via a Task
            return model;
        }

        public static TaskDto toDto(Task model) {
            if (model == null)
                return null;
            TaskDto dto = new TaskDto(model.getId(), model.getUuid(), model.getName(), model.getDescription(), model.getDueDate(), model.getStatus(), model.getProject()
                .getId(), WorkerDto.Mapper.toDto(model.getAssignee()));
            return dto;
        }
    }

    public static interface TaskUpdateValidationData {
    }

    public static interface TaskUpdateStatusValidationData {
    }

    public static interface TaskUpdateAssigneeValidationData {
    }
}
