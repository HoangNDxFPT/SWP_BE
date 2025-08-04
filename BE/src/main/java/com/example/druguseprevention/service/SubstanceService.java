package com.example.druguseprevention.service;

import com.example.druguseprevention.entity.Substance;
import com.example.druguseprevention.repository.SubstanceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SubstanceService implements CommandLineRunner {

    private final SubstanceRepository substanceRepository;

    @Override
    public void run(String... args) throws Exception {
        // Khởi tạo dữ liệu substances cố định theo WHO ASSIST standard
        initializeStandardSubstances();
    }

    private void initializeStandardSubstances() {
        if (substanceRepository.findByIsDeletedFalse().isEmpty()) {
            List<Substance> standardSubstances = List.of(
                new Substance("Tobacco products", "Cigarettes, chewing tobacco, cigars, etc."),
                new Substance("Alcoholic beverages", "Beer, wine, spirits, etc."),
                new Substance("Cannabis", "Marijuana, pot, grass, hash, etc."),
                new Substance("Cocaine", "Coke, crack, etc."),
                new Substance("Amphetamine type stimulants", "Speed, diet pills, ecstasy, etc."),
                new Substance("Inhalants", "Nitrous, glue, petrol, paint thinner, etc."),
                new Substance("Sedatives or Sleeping Pills", "Valium, Serepax, Rohypnol, etc."),
                new Substance("Hallucinogens", "LSD, acid, mushrooms, PCP, Special K, etc."),
                new Substance("Opioids", "Heroin, morphine, methadone, codeine, etc."),
                new Substance("Other drugs", "Any other substances not listed above")
            );

            substanceRepository.saveAll(standardSubstances);
            System.out.println("WHO ASSIST standard substances initialized successfully!");
        }
    }

    public List<Substance> getAllActiveSubstances() {
        return substanceRepository.findByIsDeletedFalse();
    }

    public Optional<Substance> getSubstanceById(Long id) {
        return substanceRepository.findByIdAndIsDeletedFalse(id);
    }
}
