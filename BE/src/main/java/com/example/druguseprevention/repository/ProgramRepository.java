package com.example.druguseprevention.repository;

import com.example.druguseprevention.entity.Program;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProgramRepository extends JpaRepository<Program, Long> {
    List<Program> findByIsDeletedFalse();
    Optional<Program> findByIdAndIsDeletedFalse(Long id);
    boolean existsByIdAndIsDeletedFalse(Long id);
}
