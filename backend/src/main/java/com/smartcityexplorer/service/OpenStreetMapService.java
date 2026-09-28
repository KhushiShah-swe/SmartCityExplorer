package com.smartcityexplorer.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.smartcityexplorer.dto.PlaceResponse;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class OpenStreetMapService {
    private final RestClient client = RestClient.create();

    public List<PlaceResponse> discover(String mood, Integer maxBudget, String kind) {
        String selectedMood = mood == null || mood.isBlank() ? "Explore Chicago" : mood;
        if (maxBudget != null && maxBudget < 1) return List.of();

        List<PlaceResponse> results = new ArrayList<>();
        for (String query : queriesFor(selectedMood, kind)) {
            try {
                String uri = "https://nominatim.openstreetmap.org/search?format=jsonv2&limit=8&countrycodes=us&addressdetails=1&q=" +
                        URLEncoder.encode(query + ", Chicago, Illinois", StandardCharsets.UTF_8);
                JsonNode body = client.get().uri(uri)
                        .header("User-Agent", "SmartCityExplorer/1.0")
                        .retrieve().body(JsonNode.class);

                if (body == null || !body.isArray()) continue;

                for (JsonNode p : body) {
                    String id = "osm-" + p.path("osm_type").asText() + "-" + p.path("osm_id").asText();
                    String name = p.path("name").asText();
                    if (name.isBlank()) name = p.path("display_name").asText().split(",")[0];
                    String address = p.path("display_name").asText();
                    String encoded = URLEncoder.encode(name + ", " + address, StandardCharsets.UTF_8);

                    PlaceResponse mapped = new PlaceResponse(
                            id,
                            name,
                            address,
                            title(p.path("type").asText()),
                            selectedMood,
                            1,
                            null,
                            null,
                            "https://www.google.com/maps/search/?api=1&query=" + encoded,
                            "https://www.google.com/maps/dir/?api=1&destination=" + encoded,
                            "https://www.google.com/maps/search/?api=1&query=" + encoded,
                            "https://www.openstreetmap.org",
                            false
                    );

                    if (results.stream().noneMatch(x -> x.id().equals(mapped.id()))) {
                        results.add(mapped);
                    }
                    if (results.size() >= 12) return results;
                }
            } catch (RuntimeException ignored) { }
        }
        return results;
    }

    private List<String> queriesFor(String mood, String kind) {
        boolean cafes = "cafes".equalsIgnoreCase(kind);

        if (cafes) {
            return switch (mood) {
                case "Slow & Relaxed" -> List.of("cafe", "tea house", "coffee shop");
                case "Date Night" -> List.of("cafe", "dessert cafe", "coffee shop");
                case "Study & Work" -> List.of("coffee shop", "cafe", "library cafe");
                case "Group Activities" -> List.of("cafe", "coffee shop", "brunch");
                case "Party & Nightlife" -> List.of("late night cafe", "dessert cafe", "coffee shop");
                default -> List.of("cafe", "coffee shop", "tea house");
            };
        }

        return switch (mood) {
            case "Slow & Relaxed" -> List.of("park", "garden", "lakefront");
            case "Date Night" -> List.of("theatre", "rooftop", "attraction");
            case "Study & Work" -> List.of("library", "bookstore", "coworking");
            case "Group Activities" -> List.of("museum", "bowling", "arcade");
            case "Party & Nightlife" -> List.of("nightclub", "music venue", "bar");
            default -> List.of("museum", "park", "attraction");
        };
    }

    private String title(String value) {
        if (value == null || value.isBlank()) return "Chicago Spot";
        return Character.toUpperCase(value.charAt(0)) + value.substring(1).replace('_', ' ');
    }
}
