package com.smart_dispatch.driver.model;

public record DispatchAlert(
        String patientId,
        String severity,
        String destination,
        String status
) {}
