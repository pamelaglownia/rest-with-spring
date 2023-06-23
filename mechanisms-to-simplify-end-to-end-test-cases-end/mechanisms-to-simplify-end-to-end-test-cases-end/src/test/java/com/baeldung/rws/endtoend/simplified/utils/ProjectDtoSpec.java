package com.baeldung.rws.endtoend.simplified.utils;

import static com.baeldung.rws.commons.endtoend.spec.DtoFieldSpec.from;

import java.util.List;

import org.hamcrest.Matcher;

import com.baeldung.rws.commons.endtoend.spec.DtoFieldSpec;
import com.baeldung.rws.commons.endtoend.spec.DtoSpec;
import com.baeldung.rws.web.dto.ProjectDto;
import com.baeldung.rws.web.dto.TaskDto;

public final class ProjectDtoSpec implements DtoSpec<ProjectDto> {

    private DtoFieldSpec<ProjectDto, Long> id = from("id", ProjectDto::id);

    private DtoFieldSpec<ProjectDto, String> code = from("code", ProjectDto::code);

    private DtoFieldSpec<ProjectDto, String> name = from("name", ProjectDto::name);

    private DtoFieldSpec<ProjectDto, String> description = from("description", ProjectDto::description);

    private DtoFieldSpec<ProjectDto, Iterable<? extends TaskDto>> tasks = from("tasks", ProjectDto::tasks);

    public ProjectDtoSpec(Matcher<Long> id, Matcher<String> code, Matcher<String> name, Matcher<String> description, Matcher<Iterable<? extends TaskDto>> tasks) {
        super();
        this.id.define(id);
        this.code.define(code);
        this.name.define(name);
        this.description.define(description);
        this.tasks.define(tasks);
    }

    @Override
    public List<DtoFieldSpec<ProjectDto, ?>> defineSpecs() {
        return List.of(this.id, this.code, this.name, this.description, this.tasks);
    }
}
