package com.smartcityexplorer.controller;

import com.smartcityexplorer.dto.PlaceResponse;
import com.smartcityexplorer.service.FoursquarePlacesService;
import com.smartcityexplorer.service.OpenStreetMapService;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import java.util.List;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@Validated
@RequestMapping("/api/discover")
public class DiscoveryController {
    private final FoursquarePlacesService foursquare;
    private final OpenStreetMapService osm;

    public DiscoveryController(FoursquarePlacesService foursquare, OpenStreetMapService osm) {
        this.foursquare = foursquare;
        this.osm = osm;
    }

    @GetMapping("/places")
    public List<PlaceResponse> places(
            @RequestParam(required = false) @Size(max = 60) String mood,
            @RequestParam(required = false) @Min(0) @Max(3) Integer maxBudget) {
        try {
            List<PlaceResponse> places = foursquare.discover(mood, maxBudget);
            if (!places.isEmpty()) return places;
        } catch (RuntimeException ignored) { }
        return osm.discover(mood, maxBudget);
    }
}
