package com.example.druguseprevention.repository;

import com.example.druguseprevention.entity.RiskRecommendation;
import com.example.druguseprevention.enums.RiskLevel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RiskRecommendationRepository extends JpaRepository<RiskRecommendation, Long> {
    Optional<RiskRecommendation> findByRiskLevel(RiskLevel level);
}
