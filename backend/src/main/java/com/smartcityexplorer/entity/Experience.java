package com.smartcityexplorer.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Experience {
    @Id
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, length = 1000)
    private String description;

    @Column(nullable = false)
    private String category;

    @Column(nullable = false)
    private String mood;

    @Column(nullable = false)
    private Integer budgetLevel;

    @Column(nullable = false)
    private Integer durationMinutes;

    @Column(nullable = false)
    private String neighborhood;

    @Column(nullable = false)
    private boolean hiddenGem;

    public Experience() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getMood() { return mood; }
    public void setMood(String mood) { this.mood = mood; }

    public Integer getBudgetLevel() { return budgetLevel; }
    public void setBudgetLevel(Integer budgetLevel) { this.budgetLevel = budgetLevel; }

    public Integer getDurationMinutes() { return durationMinutes; }
    public void setDurationMinutes(Integer durationMinutes) { this.durationMinutes = durationMinutes; }

    public String getNeighborhood() { return neighborhood; }
    public void setNeighborhood(String neighborhood) { this.neighborhood = neighborhood; }

    public boolean isHiddenGem() { return hiddenGem; }
    public void setHiddenGem(boolean hiddenGem) { this.hiddenGem = hiddenGem; }

}
