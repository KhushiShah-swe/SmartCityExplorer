package com.smartcityexplorer.dto;

import com.smartcityexplorer.entity.Experience;

public record ExperienceResponse(Long id, String name, String description, String category,
        String mood, int budgetLevel, int durationMinutes, String neighborhood, boolean hiddenGem) {
    public static ExperienceResponse from(Experience value) {
        return new ExperienceResponse(value.getId(), value.getName(), value.getDescription(),
                value.getCategory(), value.getMood(), value.getBudgetLevel(), value.getDurationMinutes(),
                value.getNeighborhood(), value.isHiddenGem());
    }
}
