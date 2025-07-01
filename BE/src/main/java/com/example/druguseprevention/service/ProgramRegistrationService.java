package com.example.druguseprevention.service;

import com.example.druguseprevention.entity.ProgramParticipation;
import com.example.druguseprevention.entity.User;
import com.example.druguseprevention.exception.exceptions.BadRequestException;
import com.example.druguseprevention.repository.ProgramParticipationRepository;
import com.example.druguseprevention.repository.ProgramRepository;
import com.example.druguseprevention.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ProgramRegistrationService {

    @Autowired
    private ProgramParticipationRepository participationRepository;

    @Autowired
    private ProgramRepository programRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    AssessmentService assessmentService;

    public void registerUserToProgram(Long programId) {
        User currentUser = assessmentService.getCurrentUser(); // user đang đăng nhập

        if (!programRepository.existsByIdAndIsDeletedFalse(programId)) {
            throw new BadRequestException("Program not found");
        }

        Long currentUserId = currentUser.getId();

        if (participationRepository.existsByMemberIdAndProgramId(currentUserId, programId)) {
            throw new BadRequestException("User already registered");
        }

        ProgramParticipation participation = new ProgramParticipation();
        participation.setMember(currentUser);
        participation.setProgram(programRepository.findByIdAndIsDeletedFalse(programId).get());
        participation.setJoinedAt(LocalDateTime.now());

        participationRepository.save(participation);
    }
}

