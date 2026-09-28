package com.smartcityexplorer.controller;

import com.smartcityexplorer.dto.PlaceResponse;
import com.smartcityexplorer.service.FoursquarePlacesService;
import com.smartcityexplorer.service.OpenStreetMapService;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@Validated
@RequestMapping("/api/discover")
public class DiscoveryController {
    private static final Logger LOGGER = LoggerFactory.getLogger(DiscoveryController.class);
    private final FoursquarePlacesService foursquare;
    private final OpenStreetMapService osm;

    public DiscoveryController(FoursquarePlacesService foursquare, OpenStreetMapService osm) {
        this.foursquare = foursquare;
        this.osm = osm;
    }

    @GetMapping("/places")
    public List<PlaceResponse> places(
            @RequestParam(required = false) @Size(max = 60) String mood,
            @RequestParam(required = false) @Min(0) @Max(3) Integer maxBudget,
            @RequestParam(defaultValue = "places") @Pattern(regexp = "cafes|places") String kind) {

        try {
            List<PlaceResponse> places = foursquare.discover(mood, maxBudget, kind);
            if (!places.isEmpty()) {
                LOGGER.info("Live discovery provider=Foursquare kind={} mood={} results={}", kind, mood, places.size());
                return places;
            }
            LOGGER.warn("Foursquare returned zero usable results for kind={} mood={}; trying OSM fallback", kind, mood);
        } catch (RuntimeException ex) {
            LOGGER.error("Foursquare discovery failed for kind={} mood={}: {}", kind, mood, ex.getMessage());
        }

        List<PlaceResponse> fallback = osm.discover(mood, maxBudget, kind);
        LOGGER.info("Live discovery provider=OSM kind={} mood={} results={}", kind, mood, fallback.size());
        return fallback;
    }
}
