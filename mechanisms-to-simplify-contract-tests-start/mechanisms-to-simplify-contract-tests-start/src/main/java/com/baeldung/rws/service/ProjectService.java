package com.baeldung.rws.service;

import java.util.List;
import java.util.Optional;

import com.baeldung.rws.domain.model.Project;

public interface ProjectService {

    List<Project> findProjects();

    Optional<Project> findById(Long id);

    Project save(Project project);

    Optional<Project> updateProject(Long id, Project project);
}
