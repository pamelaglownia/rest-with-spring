package com.baeldung.rws.web.dto;

import java.util.Objects;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import com.baeldung.rws.domain.model.Worker;
import com.baeldung.rws.web.dto.TaskDto.TaskUpdateAssigneeValidationData;
import com.baeldung.rws.web.dto.TaskDto.TaskUpdateValidationData;

public record WorkerDto ( // @formatter:off

    @NotNull(groups = { TaskUpdateValidationData.class, TaskUpdateAssigneeValidationData.class })
    @Positive(groups = { TaskUpdateValidationData.class, TaskUpdateAssigneeValidationData.class })
    Long id,

    @NotBlank(groups = { WorkerUpdateValidationData.class, WorkerCreateValidationData.class })
    @Email(groups = { WorkerUpdateValidationData.class, WorkerCreateValidationData.class })
    String email,

    String firstName,

    String lastName) { // @formatter:on

    public static class Mapper {
        public static Worker toModel(WorkerDto dto) {
            if (dto == null)
                return null;
            Worker model = new Worker(dto.email(), dto.firstName(), dto.lastName());
            if (!Objects.isNull(dto.id())) {
                model.setId(dto.id());
            }
            
            return model;
        }

        public static WorkerDto toDto(Worker model) {
            if (model == null)
                return null;
            WorkerDto dto = new WorkerDto(model.getId(), model.getEmail(), model.getFirstName(), model.getLastName());
            return dto;
        }
    }

    public interface WorkerCreateValidationData {
    }

    public interface WorkerUpdateValidationData {
    }
}
