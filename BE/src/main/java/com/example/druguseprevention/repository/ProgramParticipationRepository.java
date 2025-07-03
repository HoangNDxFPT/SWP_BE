package com.example.druguseprevention.repository;

import com.example.druguseprevention.entity.ProgramParticipation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProgramParticipationRepository extends JpaRepository<ProgramParticipation, Long> {
    boolean existsByMemberIdAndProgramId(Long memberId, Long programId);
    List<ProgramParticipation> findByProgramId(Long programId);
    List<ProgramParticipation> findByMemberId (Long id);
    Optional<ProgramParticipation> findByMemberIdAndProgramId (Long menberId, Long programId);
}
