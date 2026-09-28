package com.smartcityexplorer.dto;

public record PlaceResponse(
        String id,
        String name,
        String address,
        String category,
        String mood,
        Integer budgetLevel,
        Double rating,
        Integer reviewCount,
        String mapsUrl,
        String directionsUrl,
        String reviewsUrl,
        String websiteUrl,
        boolean openNow) {}
