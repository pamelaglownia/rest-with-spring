package com.baeldung.rws.web.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.baeldung.rws.domain.model.Project;
import com.baeldung.rws.service.ProjectService;
import com.baeldung.rws.web.dto.ProjectDto;

@RestController
@RequestMapping(value = "/projects")
public class ProjectController {

    private ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @GetMapping
    public List<ProjectDto> listProjects() {
        List<Project> models = projectService.findProjects();
        List<ProjectDto> projectDtos = models.stream()
            .map(ProjectDto.Mapper::toDto)
            .collect(Collectors.toList());
        return projectDtos;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{id}")
    public ProjectDto findOne(@PathVariable Long id) {
        Project model = projectService.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        return ProjectDto.Mapper.toDto(model);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProjectDto create(@RequestBody ProjectDto newProject) {
        Project model = ProjectDto.Mapper.toModel(newProject);
        Project createdModel = this.projectService.save(model);
        return ProjectDto.Mapper.toDto(createdModel);
    }

    @PutMapping(value = "/{id}")
    public ProjectDto update(@PathVariable Long id, @RequestBody ProjectDto updatedProject) {
        Project model = ProjectDto.Mapper.toModel(updatedProject);
        Project createdModel = this.projectService.updateProject(id, model)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        return ProjectDto.Mapper.toDto(createdModel);
    }
}
