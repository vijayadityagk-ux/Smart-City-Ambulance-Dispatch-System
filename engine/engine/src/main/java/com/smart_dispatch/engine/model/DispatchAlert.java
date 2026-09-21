package com.smart_dispatch.engine.model;

public record DispatchAlert(
        String patientId,
        String severity,
        String destination,
        String status
) {}
