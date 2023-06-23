package com.baeldung.rws.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.baeldung.rws.domain.model.Project;
import com.baeldung.rws.persistence.repository.ProjectRepository;
import com.baeldung.rws.service.ProjectService;

@Service
public class DefaultProjectService implements ProjectService {

    private ProjectRepository projectRepository;

    public DefaultProjectService(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    @Override
    public Optional<Project> findById(Long id) {
        return projectRepository.findById(id);
    }

    @Override
    public Project save(Project project) {
        project.setId(null);
        return projectRepository.save(project);
    }

    @Override
    public List<Project> findProjects() {
        return projectRepository.findAll();
    }

    @Override
    public Optional<Project> updateProject(Long id, Project project) {
        return projectRepository.findById(id)
            .map(base -> updateFields(base, project))
            .map(projectRepository::save);
    }

    private Project updateFields(Project base, Project updatedProject) {
        base.setName(updatedProject.getName());
        base.setDescription(updatedProject.getDescription());
        return base;
    }

}
