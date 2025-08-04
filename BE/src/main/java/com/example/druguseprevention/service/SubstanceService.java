package com.example.druguseprevention.service;

import com.example.druguseprevention.entity.Substance;
import com.example.druguseprevention.exception.exceptions.BadRequestException;
import com.example.druguseprevention.repository.SubstanceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SubstanceService{

    private final SubstanceRepository substanceRepository;


    public List<Substance> getAllSubstances() {
        return substanceRepository.findAll();
    }

    public List<Substance> getAllActiveSubstances() {
        return substanceRepository.findByIsDeletedFalse();
    }

    public Optional<Substance> getSubstanceById(Long id) {
        return substanceRepository.findByIdAndIsDeletedFalse(id);
    }
}
