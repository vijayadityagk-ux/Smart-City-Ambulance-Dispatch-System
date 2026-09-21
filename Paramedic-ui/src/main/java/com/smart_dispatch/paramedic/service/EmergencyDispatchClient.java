package com.smart_dispatch.paramedic.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

@Service
public class EmergencyDispatchClient {

    private final String engineApiUrl;
    private final HttpClient httpClient;

    public EmergencyDispatchClient(@Value("${engine.api.url:http://localhost:8080/api/emergency}") String engineApiUrl) {
        this.engineApiUrl = engineApiUrl;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .build();
    }

    public record DispatchResult(boolean success, int statusCode, String message) {}

    public DispatchResult sendDispatch(String description, String location, String severity, String injury) {
        String jsonPayload = String.format(
                "{\"description\":\"%s\",\"location\":\"%s\",\"severity\":\"%s\",\"injury\":\"%s\"}",
                escapeJson(description),
                escapeJson(location),
                escapeJson(severity),
                escapeJson(injury)
        );

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(engineApiUrl))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                    .timeout(Duration.ofSeconds(10))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            boolean isSuccess = response.statusCode() >= 200 && response.statusCode() < 300;
            return new DispatchResult(isSuccess, response.statusCode(), response.body());
        } catch (Exception e) {
            return new DispatchResult(false, 0, "Could not connect to Central Dispatch Brain: " + e.getMessage());
        }
    }

    private String escapeJson(String raw) {
        if (raw == null) return "";
        return raw.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
