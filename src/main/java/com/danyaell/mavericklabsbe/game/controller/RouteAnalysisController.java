package com.danyaell.mavericklabsbe.game.controller;

import com.danyaell.mavericklabsbe.game.dto.route.AnalyzeRouteRequest;
import com.danyaell.mavericklabsbe.game.dto.route.RouteAnalysisResponse;
import com.danyaell.mavericklabsbe.game.service.RouteAnalysisService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/routes")
@RequiredArgsConstructor
public class RouteAnalysisController {

	private final RouteAnalysisService routeAnalysisService;

	@PostMapping("/analyze")
	public ResponseEntity<RouteAnalysisResponse> analyzeRoute(@Valid @RequestBody AnalyzeRouteRequest request) {
		RouteAnalysisResponse response = routeAnalysisService.analyzeRoute(request);
		return ResponseEntity.ok(response);
	}
}
