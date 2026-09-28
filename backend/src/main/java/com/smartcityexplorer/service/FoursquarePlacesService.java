package com.smartcityexplorer.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.smartcityexplorer.dto.PlaceResponse;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class FoursquarePlacesService {
    private static final String FSQ_URL = "https://places-api.foursquare.com/places/search";
    private final RestClient client = RestClient.create();

    @Value("${FOURSQUARE_API_KEY:}")
    private String apiKey;

    public List<PlaceResponse> discover(String mood, Integer maxBudget) {
        if (apiKey == null || apiKey.isBlank()) return List.of();
        String selectedMood = mood == null || mood.isBlank() ? "Explore Chicago" : mood;
        List<PlaceResponse> results = new ArrayList<>();

        for (String query : queriesFor(selectedMood)) {
            String uri = FSQ_URL + "?near=Chicago%2C%20IL&limit=10&query=" +
                    URLEncoder.encode(query, StandardCharsets.UTF_8);
            JsonNode body = client.get().uri(uri)
                    .header("Authorization", "Bearer " + apiKey)
                    .header("X-Places-Api-Version", "2025-06-17")
                    .retrieve().body(JsonNode.class);
            if (body == null || !body.has("results")) continue;

            for (JsonNode p : body.get("results")) {
                PlaceResponse mapped = map(p, selectedMood);
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
            case "Slow & Relaxed" -> List.of("cafe", "park", "garden");
            case "Date Night" -> List.of("romantic restaurant", "cafe", "attraction");
            case "Study & Work" -> List.of("coffee shop", "library", "cafe");
            case "Group Activities" -> List.of("attraction", "arcade", "museum");
            case "Party & Nightlife" -> List.of("nightlife", "music venue", "bar");
            default -> List.of("cafe", "attraction", "museum");
        };
    }

    private PlaceResponse map(JsonNode p, String mood) {
        String id = text(p, "fsq_place_id");
        String name = text(p, "name");
        if (id == null || name == null) return null;
        JsonNode loc = p.path("location");
        String address = text(loc, "formatted_address");
        String category = p.path("categories").isArray() && !p.path("categories").isEmpty()
                ? text(p.path("categories").get(0), "name") : "Chicago Spot";
        int price = p.has("price") ? Math.max(0, Math.min(3, p.get("price").asInt() - 1)) : 1;
        Double rating = p.has("rating") ? p.get("rating").asDouble() : null;
        String encoded = URLEncoder.encode(name + (address == null ? ", Chicago IL" : ", " + address), StandardCharsets.UTF_8);
        String maps = "https://www.google.com/maps/search/?api=1&query=" + encoded;
        String directions = "https://www.google.com/maps/dir/?api=1&destination=" + encoded;
        String reviews = "https://www.google.com/maps/search/?api=1&query=" + encoded;
        return new PlaceResponse(id, name, address, category, mood, price, rating, null,
                maps, directions, reviews, text(p, "website"), false);
    }

    private String text(JsonNode node, String field) {
        JsonNode value = node.get(field);
        return value == null || value.isNull() ? null : value.asText();
    }
}
