package com.example.druguseprevention.service;

import com.example.druguseprevention.entity.Substance;
import com.example.druguseprevention.repository.SubstanceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SubstanceService {

    private final SubstanceRepository substanceRepository;

    public List<Substance> getAllSubstances() {
        return substanceRepository.findByIsDeletedFalse();
    }

    public Optional<Substance> getSubstanceById(Long id) {
        return substanceRepository.findByIdAndIsDeletedFalse(id);
    }

    public Substance createSubstance(String name, String description) {
        Substance substance = new Substance();
        substance.setName(name);
        substance.setDescription(description);
        substance.setDeleted(false);
        return substanceRepository.save(substance);
    }
}
