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

    public List<PlaceResponse> discover(String mood, Integer maxBudget) {
        String selectedMood = mood == null || mood.isBlank() ? "Explore Chicago" : mood;
        if (maxBudget != null && maxBudget < 1) return List.of();
        List<PlaceResponse> results = new ArrayList<>();
        for (String query : queriesFor(selectedMood)) {
            try {
                String uri = "https://nominatim.openstreetmap.org/search?format=jsonv2&limit=6&countrycodes=us&q=" +
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
                    PlaceResponse mapped = new PlaceResponse(id, name, address, title(p.path("type").asText()),
                            selectedMood, 1, null, null,
                            "https://www.google.com/maps/search/?api=1&query=" + encoded,
                            "https://www.google.com/maps/dir/?api=1&destination=" + encoded,
                            "https://www.google.com/maps/search/?api=1&query=" + encoded,
                            "https://www.openstreetmap.org", false);
                    if (results.stream().noneMatch(x -> x.id().equals(mapped.id()))) results.add(mapped);
                }
            } catch (RuntimeException ignored) { }
        }
        return results.stream().limit(18).toList();
    }

    private List<String> queriesFor(String mood) {
        return switch (mood) {
            case "Slow & Relaxed" -> List.of("cafe", "park", "garden");
            case "Date Night" -> List.of("cafe", "restaurant", "theatre");
            case "Study & Work" -> List.of("cafe", "library");
            case "Group Activities" -> List.of("museum", "bowling", "attraction");
            case "Party & Nightlife" -> List.of("music venue", "nightclub");
            default -> List.of("cafe", "museum", "park", "attraction");
        };
    }

    private String title(String value) {
        if (value == null || value.isBlank()) return "Chicago Spot";
        return Character.toUpperCase(value.charAt(0)) + value.substring(1).replace('_', ' ');
    }
}
