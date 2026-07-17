package com.iignaasii47.e_commerce_api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.Connection;
import java.time.Instant;
import java.util.Map;

@RestController
@RequestMapping("/api")
@Tag(name = "Status", description = "Health check endpoint for the API and database")
public class StatusController {

    private final DataSource dataSource;

    public StatusController(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @GetMapping("/status")
    @Operation(summary = "Check API and database health",
            description = "Returns the current status of the API and its database connection.")
    @ApiResponse(responseCode = "200", description = "Status report returned successfully",
            content = @Content(schema = @Schema(example = "{\"api\":\"UP\",\"database\":\"UP\",\"timestamp\":\"2026-07-16T10:30:00Z\"}")))
    public Map<String, Object> status() {
        String dbStatus = "DOWN";
        try (Connection conn = dataSource.getConnection()) {
            dbStatus = conn.isValid(2) ? "UP" : "DOWN";
        } catch (Exception _) {
            // Database not available; dbStatus stays "DOWN"
        }

        return Map.of(
                "api", "UP",
                "database", dbStatus,
                "timestamp", Instant.now().toString()
        );
    }

}
