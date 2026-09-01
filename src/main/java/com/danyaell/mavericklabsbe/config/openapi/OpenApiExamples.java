package com.danyaell.mavericklabsbe.config.openapi;

public final class OpenApiExamples {

    private OpenApiExamples() {
    }

    public static final String ANALYZE_ROUTE_REQUEST = """
            {
              "gameCode": "MMX",
              "stageOrder": [
                "chill-penguin",
                "spark-mandrill",
                "storm-eagle",
                "flame-mammoth",
                "armored-armadillo",
                "launch-octopus",
                "boomer-kuwanger",
                "sting-chameleon"
              ],
              "goal": "HUNDRED_PERCENT"
            }
            """;

    /*
     * Captured from POST /api/v1/routes/analyze using the V2 seed dataset.
     * Regenerate with the request stored in ANALYZE_ROUTE_REQUEST whenever
     * analyzer rules or seed data change.
     */
    public static final String ANALYZE_ROUTE_RESPONSE = """
            {
              "gameCode": "MMX",
              "difficultyScore": 47,
              "difficultyLabel": "MEDIUM",
              "backtrackingScore": 80,
              "estimatedMinutes": 140,
              "warnings": [
                {
                  "type": "MISSING_REQUIREMENT",
                  "message": "Collectible Heart Tank may require revisiting Chill Penguin Stage later.",
                  "stageSlug": "chill-penguin",
                  "collectibleSlug": "chill-penguin-heart-tank"
                },
                {
                  "type": "MISSING_REQUIREMENT",
                  "message": "Collectible Sub Tank may require revisiting Spark Mandrill Stage later.",
                  "stageSlug": "spark-mandrill",
                  "collectibleSlug": "spark-mandrill-sub-tank"
                },
                {
                  "type": "MISSING_REQUIREMENT",
                  "message": "Collectible Hadouken may require revisiting Armored Armadillo Stage later.",
                  "stageSlug": "armored-armadillo",
                  "collectibleSlug": "armored-armadillo-hadouken"
                },
                {
                  "type": "MISSING_REQUIREMENT",
                  "message": "Collectible Heart Tank may require revisiting Boomer Kuwanger Stage later.",
                  "stageSlug": "boomer-kuwanger",
                  "collectibleSlug": "boomer-kuwanger-heart-tank"
                }
              ],
              "breakdown": {
                "baseDifficultyAverage": 67,
                "combatDifficulty": 47,
                "weaknessReduction": 20,
                "routeEfficiencyScore": 68,
                "timePenaltyMinutes": 20
              },
              "recommendations": [
                {
                  "type": "BACKTRACKING",
                  "severity": "WARNING",
                  "message": "You may need to revisit Armored Armadillo to collect all items.",
                  "relatedStages": [
                    "armored-armadillo"
                  ]
                },
                {
                  "type": "BACKTRACKING",
                  "severity": "WARNING",
                  "message": "You may need to revisit Boomer Kuwanger to collect all items.",
                  "relatedStages": [
                    "boomer-kuwanger"
                  ]
                },
                {
                  "type": "BACKTRACKING",
                  "severity": "WARNING",
                  "message": "You may need to revisit Chill Penguin to collect all items.",
                  "relatedStages": [
                    "chill-penguin"
                  ]
                },
                {
                  "type": "BACKTRACKING",
                  "severity": "WARNING",
                  "message": "You may need to revisit Spark Mandrill to collect all items.",
                  "relatedStages": [
                    "spark-mandrill"
                  ]
                },
                {
                  "type": "BOSS_ORDER",
                  "severity": "WARNING",
                  "message": "Move Flame Mammoth before Chill Penguin to reduce difficulty because Flame Mammoth gives you Fire Wave.",
                  "relatedStages": [
                    "flame-mammoth",
                    "chill-penguin"
                  ]
                },
                {
                  "type": "BOSS_ORDER",
                  "severity": "WARNING",
                  "message": "Move Sting Chameleon before Storm Eagle to reduce difficulty because Sting Chameleon gives you Chameleon Sting.",
                  "relatedStages": [
                    "sting-chameleon",
                    "storm-eagle"
                  ]
                },
                {
                  "type": "BOSS_ORDER",
                  "severity": "INFO",
                  "message": "Good choice: Chill Penguin before Spark Mandrill reduces difficulty because you get Shotgun Ice.",
                  "relatedStages": [
                    "chill-penguin",
                    "spark-mandrill"
                  ]
                },
                {
                  "type": "BOSS_ORDER",
                  "severity": "INFO",
                  "message": "Good choice: Storm Eagle before Flame Mammoth reduces difficulty because you get Storm Tornado.",
                  "relatedStages": [
                    "storm-eagle",
                    "flame-mammoth"
                  ]
                }
              ]
            }
            """;

    public static final String BAD_REQUEST_RESPONSE = """
            {
              "status": 400,
              "message": "stageOrder cannot be empty"
            }
            """;

    public static final String NOT_FOUND_RESPONSE = """
            {
              "status": 404,
              "message": "Game not found: INVALID"
            }
            """;

    public static final String INTERNAL_SERVER_ERROR_RESPONSE = """
            {
              "status": 500,
              "message": "Unexpected server error"
            }
            """;
}