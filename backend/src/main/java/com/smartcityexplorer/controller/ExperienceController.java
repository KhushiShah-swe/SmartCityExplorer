package com.smartcityexplorer.controller;

import com.smartcityexplorer.dto.ExperienceResponse;
import com.smartcityexplorer.service.ExperienceService;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import java.util.List;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@Validated
@RequestMapping("/api/experiences")
public class ExperienceController {
    private final ExperienceService service;

    public ExperienceController(ExperienceService service) { this.service = service; }

    @GetMapping
    public List<ExperienceResponse> all(
            @RequestParam(required = false) @Size(max = 100) String q,
            @RequestParam(required = false) @Size(max = 60) String mood,
            @RequestParam(required = false) @Size(max = 60) String category,
            @RequestParam(required = false) @Size(max = 60) String neighborhood,
            @RequestParam(required = false) @Min(0) @Max(3) Integer maxBudget,
            @RequestParam(required = false) @Min(1) @Max(1440) Integer maxDuration,
            @RequestParam(defaultValue = "name") String sort) {
        return service.search(q, mood, category, neighborhood, maxBudget, maxDuration, sort);
    }

    @GetMapping("/{id}")
    public ExperienceResponse one(@PathVariable @Min(1) Long id) { return service.find(id); }
}
