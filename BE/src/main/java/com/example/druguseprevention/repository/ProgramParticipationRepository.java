package com.example.druguseprevention.repository;

import com.example.druguseprevention.entity.ProgramParticipation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProgramParticipationRepository extends JpaRepository<ProgramParticipation, Long> {
    boolean existsByMemberIdAndProgramId(Long memberId, Long programId);
    List<ProgramParticipation> findByProgramId(Long programId);
}
