package com.smartcityexplorer.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.smartcityexplorer.dto.PlaceResponse;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class FoursquarePlacesService {
    private static final Logger LOGGER = LoggerFactory.getLogger(FoursquarePlacesService.class);
    private static final String FSQ_URL = "https://places-api.foursquare.com/places/search";
    private static final String CHICAGO_LL = "41.8781,-87.6298";
    private final RestClient client = RestClient.create();

    @Value("${FOURSQUARE_API_KEY:}")
    private String apiKey;

    public List<PlaceResponse> discover(String mood, Integer maxBudget, String kind) {
        if (apiKey == null || apiKey.isBlank()) {
            LOGGER.warn("Foursquare discovery skipped: FOURSQUARE_API_KEY is not configured");
            return List.of();
        }

        String selectedMood = mood == null || mood.isBlank() ? "Explore Chicago" : mood;
        List<PlaceResponse> results = new ArrayList<>();

        for (String query : queriesFor(selectedMood, kind)) {
            String uri = FSQ_URL + "?ll=" + CHICAGO_LL +
                    "&radius=30000&limit=20&sort=RELEVANCE&query=" +
                    URLEncoder.encode(query, StandardCharsets.UTF_8);

            JsonNode body = client.get()
                    .uri(uri)
                    .header("Accept", "application/json")
                    .header("Authorization", "Bearer " + apiKey)
                    .header("X-Places-Api-Version", "2025-06-17")
                    .retrieve()
                    .body(JsonNode.class);

            if (body == null || !body.path("results").isArray()) {
                LOGGER.warn("Foursquare returned no results array for query '{}'", query);
                continue;
            }

            LOGGER.info("Foursquare returned {} results for {} / {}", body.path("results").size(), kind, query);

            for (JsonNode p : body.path("results")) {
                PlaceResponse mapped = map(p, selectedMood);
                if (mapped != null
                        && (maxBudget == null || mapped.budgetLevel() <= maxBudget)
                        && results.stream().noneMatch(x -> x.id().equals(mapped.id()))) {
                    results.add(mapped);
                }
                if (results.size() >= 12) return results;
            }
        }
        return results;
    }

    private List<String> queriesFor(String mood, String kind) {
        boolean cafes = "cafes".equalsIgnoreCase(kind);
        if (cafes) {
            return switch (mood) {
                case "Slow & Relaxed" -> List.of("cafe", "tea", "coffee");
                case "Date Night" -> List.of("dessert cafe", "cafe", "coffee");
                case "Study & Work" -> List.of("coffee", "cafe");
                case "Group Activities" -> List.of("cafe", "brunch");
                case "Party & Nightlife" -> List.of("dessert cafe", "coffee");
                default -> List.of("cafe", "coffee", "tea");
            };
        }

        return switch (mood) {
            case "Slow & Relaxed" -> List.of("park", "garden", "lakefront");
            case "Date Night" -> List.of("theatre", "rooftop restaurant", "attraction");
            case "Study & Work" -> List.of("library", "bookstore", "coworking");
            case "Group Activities" -> List.of("museum", "arcade", "bowling");
            case "Party & Nightlife" -> List.of("music venue", "nightclub", "rooftop bar");
            default -> List.of("museum", "attraction", "park");
        };
    }

    private PlaceResponse map(JsonNode p, String mood) {
        String id = text(p, "fsq_place_id");
        String name = text(p, "name");
        if (id == null || name == null || name.isBlank()) return null;

        JsonNode loc = p.path("location");
        String address = text(loc, "formatted_address");
        if (address == null || address.isBlank()) {
            address = buildAddress(loc);
        }

        String category = p.path("categories").isArray() && !p.path("categories").isEmpty()
                ? text(p.path("categories").get(0), "name") : "Chicago Spot";

        int price = p.has("price") ? Math.max(0, Math.min(3, p.path("price").asInt() - 1)) : 1;
        Double rating = p.has("rating") && p.path("rating").isNumber() ? p.path("rating").asDouble() : null;
        Integer reviewCount = p.path("stats").has("total_ratings")
                ? p.path("stats").path("total_ratings").asInt() : null;

        String destination;
        if (p.path("latitude").isNumber() && p.path("longitude").isNumber()) {
            destination = p.path("latitude").asText() + "," + p.path("longitude").asText();
        } else {
            destination = name + (address == null ? ", Chicago IL" : ", " + address);
        }

        String encodedDestination = URLEncoder.encode(destination, StandardCharsets.UTF_8);
        String encodedSearch = URLEncoder.encode(name + ", Chicago IL", StandardCharsets.UTF_8);

        return new PlaceResponse(
                id, name, address, category, mood, price, rating, reviewCount,
                "https://www.google.com/maps/search/?api=1&query=" + encodedSearch,
                "https://www.google.com/maps/dir/?api=1&destination=" + encodedDestination,
                "https://www.google.com/maps/search/?api=1&query=" + encodedSearch,
                text(p, "website"),
                p.path("hours").path("open_now").asBoolean(false)
        );
    }

    private String buildAddress(JsonNode loc) {
        List<String> parts = new ArrayList<>();
        add(parts, text(loc, "address"));
        add(parts, text(loc, "locality"));
        add(parts, text(loc, "region"));
        add(parts, text(loc, "postcode"));
        return parts.isEmpty() ? "Chicago, IL" : String.join(", ", parts);
    }

    private void add(List<String> parts, String value) {
        if (value != null && !value.isBlank() && !parts.contains(value)) parts.add(value);
    }

    private String text(JsonNode node, String field) {
        JsonNode value = node.get(field);
        return value == null || value.isNull() ? null : value.asText();
    }
}
