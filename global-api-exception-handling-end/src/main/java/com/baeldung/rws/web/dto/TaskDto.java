package com.baeldung.rws.web.dto;

import java.time.LocalDate;
import java.util.Objects;

import com.baeldung.rws.domain.model.Project;
import com.baeldung.rws.domain.model.Task;
import com.baeldung.rws.domain.model.TaskStatus;
import com.baeldung.rws.web.error.IdMismatchException;

public record TaskDto( // @formatter:off
    Long id,

    String uuid,

    String name,

    String description,

    LocalDate dueDate,

    TaskStatus status,

    Long projectId,

    WorkerDto assignee) { // @formatter:on

    public static class Mapper {
        public static Task toModel(TaskDto dto, Long requestedId) {
            if (dto == null)
                return null;

            if (dto.id() != null && requestedId != null && !requestedId.equals(dto.id())) {
                throw new IdMismatchException("Task body id didn't match path variable");
            }
            // we won't allow creating or modifying Projects via a Task
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
}
