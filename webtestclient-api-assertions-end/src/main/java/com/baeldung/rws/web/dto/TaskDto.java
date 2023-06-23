package com.baeldung.rws.web.dto;

import java.time.LocalDate;
import java.util.Objects;

import com.baeldung.rws.domain.model.Project;
import com.baeldung.rws.domain.model.Task;
import com.baeldung.rws.domain.model.TaskStatus;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.groups.ConvertGroup;
import jakarta.validation.groups.Default;

public record TaskDto(Long id,

    String uuid,

    @NotBlank(groups = {
            TaskUpdateValidationData.class, Default.class }, message = "name can't be blank") String name,

    @Size(groups = { TaskUpdateValidationData.class, Default.class }, min = 10, max = 50, message = "description must be between 10 and 50 characters long") String description,

    @Future(message = "dueDate must be in the future") LocalDate dueDate,

    @NotNull(groups = { TaskUpdateStatusValidationData.class, TaskUpdateValidationData.class }, message = "status can't be null") TaskStatus status,

    @NotNull(groups = { TaskUpdateValidationData.class, Default.class }, message = "projectId can't be null") Long projectId,

    @Valid @ConvertGroup(from = Default.class, to = WorkerOnTaskCreateValidationData.class) WorkerDto assignee){ // @formatter:on

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

    public static interface WorkerOnTaskCreateValidationData {
    }
}
