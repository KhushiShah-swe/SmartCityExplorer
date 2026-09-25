package com.smartcityexplorer.service;

import com.smartcityexplorer.dto.ExperienceResponse;
import com.smartcityexplorer.entity.Experience;
import com.smartcityexplorer.repository.ExperienceRepository;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional(readOnly = true)
public class ExperienceService {
    private final ExperienceRepository repository;

    public ExperienceService(ExperienceRepository repository) { this.repository = repository; }

    public List<ExperienceResponse> search(String query, String mood, String category,
            String neighborhood, Integer maxBudget, Integer maxDuration, String order) {
        Sort sort = switch (order) {
            case "name" -> Sort.by("name");
            case "budget" -> Sort.by("budgetLevel").and(Sort.by("name"));
            case "duration" -> Sort.by("durationMinutes").and(Sort.by("name"));
            default -> throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "sort must be name, budget, or duration");
        };
        Specification<Experience> filters = (root, criteria, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (query != null && !query.isBlank()) {
                String term = "%" + query.trim().toLowerCase(Locale.ROOT)
                        .replace("\\", "\\\\").replace("%", "\\%").replace("_", "\\_") + "%";
                predicates.add(cb.or(cb.like(cb.lower(root.get("name")), term, '\\'),
                        cb.like(cb.lower(root.get("description")), term, '\\'),
                        cb.like(cb.lower(root.get("neighborhood")), term, '\\')));
            }
            if (mood != null && !mood.isBlank()) predicates.add(cb.equal(cb.lower(root.get("mood")), mood.toLowerCase(Locale.ROOT)));
            if (category != null && !category.isBlank()) predicates.add(cb.equal(cb.lower(root.get("category")), category.toLowerCase(Locale.ROOT)));
            if (neighborhood != null && !neighborhood.isBlank()) predicates.add(cb.equal(cb.lower(root.get("neighborhood")), neighborhood.toLowerCase(Locale.ROOT)));
            if (maxBudget != null) predicates.add(cb.le(root.get("budgetLevel"), maxBudget));
            if (maxDuration != null) predicates.add(cb.le(root.get("durationMinutes"), maxDuration));
            return cb.and(predicates.toArray(Predicate[]::new));
        };
        return repository.findAll(filters, sort).stream().map(ExperienceResponse::from).toList();
    }

    public ExperienceResponse find(long id) {
        return repository.findById(id).map(ExperienceResponse::from)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Experience not found"));
    }
}
