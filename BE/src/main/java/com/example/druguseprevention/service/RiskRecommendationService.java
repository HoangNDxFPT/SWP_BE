package com.example.druguseprevention.service;

import com.example.druguseprevention.entity.RiskRecommendation;
import com.example.druguseprevention.enums.RiskLevel;
import com.example.druguseprevention.repository.RiskRecommendationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RiskRecommendationService {

    private final RiskRecommendationRepository recommendationRepository;

    public List<RiskRecommendation> getAll() {
        return recommendationRepository.findAll();
    }

    public Optional<RiskRecommendation> getById(Long id) {
        return recommendationRepository.findById(id);
    }

    public RiskRecommendation create(RiskRecommendation recommendation) {
        recommendation.setId(null);
        return recommendationRepository.save(recommendation);
    }

    public Optional<RiskRecommendation> update(Long id, RiskRecommendation updated) {
        return recommendationRepository.findById(id).map(existing -> {
            existing.setRiskLevel(updated.getRiskLevel());
            existing.setAction(updated.getAction());
            existing.setMessage(updated.getMessage());
            return recommendationRepository.save(existing);
        });
    }

    public boolean delete(Long id) {
        if (recommendationRepository.existsById(id)) {
            recommendationRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public Optional<RiskRecommendation> getByLevel(RiskLevel level) {
        return recommendationRepository.findByRiskLevel(level);
    }
}
