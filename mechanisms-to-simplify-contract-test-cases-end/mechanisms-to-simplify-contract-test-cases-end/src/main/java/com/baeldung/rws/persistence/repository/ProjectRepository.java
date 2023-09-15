package com.baeldung.rws.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.baeldung.rws.domain.model.Project;

public interface ProjectRepository extends JpaRepository<Project, Long> {
}
