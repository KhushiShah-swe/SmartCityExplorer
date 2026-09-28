package com.smartcityexplorer.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.smartcityexplorer.dto.PlaceResponse;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class GooglePlacesService {
    private static final String GOOGLE_URL = "https://places.googleapis.com/v1/places:searchText";
    private final RestClient client = RestClient.create();

    @Value("${GOOGLE_PLACES_API_KEY:}")
    private String apiKey;

    public List<PlaceResponse> discover(String mood, Integer maxBudget) {
        if (apiKey == null || apiKey.isBlank()) return List.of();

        String selectedMood = mood == null || mood.isBlank() ? "Explore Chicago" : mood;
        List<String> queries = queriesFor(selectedMood);
        List<PlaceResponse> results = new ArrayList<>();

        for (String query : queries) {
            JsonNode body = client.post()
                    .uri(GOOGLE_URL)
                    .header("X-Goog-Api-Key", apiKey)
                    .header("X-Goog-FieldMask",
                            "places.id,places.displayName,places.formattedAddress,places.primaryTypeDisplayName," +
                            "places.priceLevel,places.rating,places.userRatingCount,places.googleMapsUri," +
                            "places.googleMapsLinks,places.websiteUri,places.currentOpeningHours.openNow")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new SearchRequest(query + " in Chicago, Illinois"))
                    .retrieve()
                    .body(JsonNode.class);

            if (body == null || !body.has("places")) continue;
            for (JsonNode place : body.get("places")) {
                PlaceResponse mapped = map(place, selectedMood);
                if (mapped != null && (maxBudget == null || mapped.budgetLevel() <= maxBudget)
                        && results.stream().noneMatch(x -> x.id().equals(mapped.id()))) {
                    results.add(mapped);
                }
                if (results.size() >= 18) return results;
            }
        }
        return results;
    }

    private List<String> queriesFor(String mood) {
        return switch (mood) {
            case "Slow & Relaxed" -> List.of("cozy cafes", "parks and gardens", "scenic relaxing places");
            case "Date Night" -> List.of("romantic cafes", "date night attractions", "rooftop restaurants");
            case "Study & Work" -> List.of("quiet study cafes", "coffee shops with wifi", "libraries");
            case "Group Activities" -> List.of("group activities", "fun attractions", "casual cafes for groups");
            case "Party & Nightlife" -> List.of("nightlife", "live music venues", "late night cafes");
            default -> List.of("best cafes", "Chicago attractions", "hidden gems");
        };
    }

    private PlaceResponse map(JsonNode p, String mood) {
        String id = text(p, "id");
        String name = nestedText(p, "displayName", "text");
        if (id == null || name == null) return null;

        String category = nestedText(p, "primaryTypeDisplayName", "text");
        int budget = budget(text(p, "priceLevel"));
        JsonNode links = p.path("googleMapsLinks");
        return new PlaceResponse(
                id,
                name,
                text(p, "formattedAddress"),
                category == null ? "Chicago Spot" : category,
                mood,
                budget,
                p.has("rating") ? p.get("rating").asDouble() : null,
                p.has("userRatingCount") ? p.get("userRatingCount").asInt() : null,
                first(text(links, "placeUri"), text(p, "googleMapsUri")),
                text(links, "directionsUri"),
                text(links, "reviewsUri"),
                text(p, "websiteUri"),
                p.path("currentOpeningHours").path("openNow").asBoolean(false));
    }

    private int budget(String price) {
        if (price == null) return 1;
        return switch (price) {
            case "PRICE_LEVEL_FREE" -> 0;
            case "PRICE_LEVEL_INEXPENSIVE" -> 1;
            case "PRICE_LEVEL_MODERATE" -> 2;
            default -> 3;
        };
    }

    private String text(JsonNode node, String field) {
        JsonNode value = node.get(field);
        return value == null || value.isNull() ? null : value.asText();
    }

    private String nestedText(JsonNode node, String parent, String child) {
        JsonNode value = node.path(parent).path(child);
        return value.isMissingNode() || value.isNull() ? null : value.asText();
    }

    private String first(String a, String b) { return a != null ? a : b; }

    private record SearchRequest(String textQuery) {}
}
