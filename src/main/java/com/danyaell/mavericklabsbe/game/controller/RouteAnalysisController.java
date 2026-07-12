package com.danyaell.mavericklabsbe.game.controller;

import com.danyaell.mavericklabsbe.game.dto.AnalyzeRouteRequest;
import com.danyaell.mavericklabsbe.game.dto.RouteAnalysisResponse;
import com.danyaell.mavericklabsbe.game.service.RouteAnalysisService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/routes")
@RequiredArgsConstructor
public class RouteAnalysisController {

    private final RouteAnalysisService routeAnalysisService;

    @PostMapping("/analyze")
    public ResponseEntity<RouteAnalysisResponse> analyzeRoute(@Valid @RequestBody AnalyzeRouteRequest request) {
        return ResponseEntity.ok(routeAnalysisService.analyzeRoute(request));
    }
}
