package com.smartcityexplorer.controller;

import com.smartcityexplorer.dto.PlaceResponse;
import com.smartcityexplorer.service.GooglePlacesService;
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
    private final GooglePlacesService places;

    public DiscoveryController(GooglePlacesService places) {
        this.places = places;
    }

    @GetMapping("/places")
    public List<PlaceResponse> places(
            @RequestParam(required = false) @Size(max = 60) String mood,
            @RequestParam(required = false) @Min(0) @Max(3) Integer maxBudget) {
        return places.discover(mood, maxBudget);
    }
}
