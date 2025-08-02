package com.example.druguseprevention.repository;

import com.example.druguseprevention.entity.Substance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubstanceRepository extends JpaRepository<Substance, Long> {
    List<Substance> findByIsDeletedFalse();
    Optional<Substance> findByIdAndIsDeletedFalse(Long id);
    Optional<Substance> findByNameAndIsDeletedFalse(String name);
}
