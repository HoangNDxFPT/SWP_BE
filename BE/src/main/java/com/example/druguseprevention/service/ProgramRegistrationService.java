package com.example.druguseprevention.service;

import com.example.druguseprevention.dto.ProgramDTO;
import com.example.druguseprevention.dto.ProgramParticipationDTO;
import com.example.druguseprevention.dto.UserDTO;
import com.example.druguseprevention.entity.Program;
import com.example.druguseprevention.entity.ProgramParticipation;
import com.example.druguseprevention.entity.User;
import com.example.druguseprevention.exception.exceptions.BadRequestException;
import com.example.druguseprevention.repository.ProgramParticipationRepository;
import com.example.druguseprevention.repository.ProgramRepository;
import com.example.druguseprevention.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

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

    @Autowired
    ModelMapper modelMapper;


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

    public void unregisterUserFromProgram(Long programId) {
        User currentUser = assessmentService.getCurrentUser(); // user đang đăng nhập
        Long currentUserId = currentUser.getId();

        ProgramParticipation participation = participationRepository
                .findByMemberIdAndProgramId(currentUserId, programId)
                .orElseThrow(() -> new BadRequestException("You are not registered for this program"));

        participationRepository.delete(participation);
    }
    private ProgramParticipationDTO convertToDto(ProgramParticipation participation) {
        ProgramParticipationDTO dto = new ProgramParticipationDTO();
        dto.setId(participation.getId());
        dto.setJoinedAt(participation.getJoinedAt());
        dto.setProgramName(participation.getProgram().getName());
        dto.setUserFullName(participation.getMember().getFullName());
        dto.setUserEmail(participation.getMember().getEmail());
        return dto;
    }

    public List<ProgramDTO> getParticipationHistoryForCurrentUser() {
        User currentUser = assessmentService.getCurrentUser();

        return participationRepository.findByMemberId(currentUser.getId())
                .stream()
                .map(ProgramParticipation::getProgram)
                .distinct() // loại trùng nếu user tham gia nhiều lần cùng 1 chương trình
                .map(program -> modelMapper.map(program, ProgramDTO.class))
                .collect(Collectors.toList());
    }

    public List<ProgramDTO> getParticipationHistoryForUser(Long userId) {
        return participationRepository.findByMemberId(userId)
                .stream()
                .map(ProgramParticipation::getProgram)
                .distinct() // loại trùng nếu user tham gia nhiều lần cùng 1 chương trình
                .map(program -> modelMapper.map(program, ProgramDTO.class))
                .collect(Collectors.toList());
    }

    public List<ProgramParticipationDTO> getAllParticipationHistory() {
        return participationRepository.findAll()
                .stream().map(this::convertToDto).collect(Collectors.toList());
    }

    public List<UserDTO> getParticipationByProgram(Long programId) {
        return participationRepository.findByProgramId(programId)
                .stream().map(ProgramParticipation::getMember)
                .distinct()
                .map(user -> modelMapper.map(user, UserDTO.class))
                .collect(Collectors.toList());
    }
}

