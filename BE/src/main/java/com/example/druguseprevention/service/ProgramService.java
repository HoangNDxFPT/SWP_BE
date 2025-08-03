package com.example.druguseprevention.service;

import com.example.druguseprevention.entity.Program;
import com.example.druguseprevention.exception.exceptions.BadRequestException;
import com.example.druguseprevention.repository.ProgramRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProgramService {

    private final ProgramRepository programRepository;


    public List<Program> getAllPrograms() {
        return programRepository.findByIsDeletedFalse();
    }

    public Optional<Program> getProgramById(Long id) {
        return programRepository.findByIdAndIsDeletedFalse(id);
    }

    public Program createProgram(Program program) {
        program.setDeleted(false);
        return programRepository.save(program);
    }

    public Program updateProgram(Long id, Program updatedProgram) {
        return programRepository.findByIdAndIsDeletedFalse(id).map(program -> {
            program.setName(updatedProgram.getName());
            program.setDescription(updatedProgram.getDescription());
            program.setStart_date(updatedProgram.getStart_date());
            program.setEnd_date(updatedProgram.getEnd_date());
            program.setLocation(updatedProgram.getLocation());
            return programRepository.save(program);
        }).orElseThrow(() -> new BadRequestException("Program not found or has been deleted"));
    }

    public void deleteProgram(Long id) {
        programRepository.findByIdAndIsDeletedFalse(id).ifPresent(program -> {
            program.setDeleted(true);
            programRepository.save(program);

        });
    }
}
