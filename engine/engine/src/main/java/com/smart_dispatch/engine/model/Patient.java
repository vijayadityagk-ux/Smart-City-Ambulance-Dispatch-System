package com.smart_dispatch.engine.model;

import com.smart_dispatch.engine.triage.TriageLevel;

import lombok.Data;

@Data
public class Patient implements Comparable<Patient> {
    
    private final String id;
    private final String locationNodeId; // Where the ambulance needs to go
    private final TriageLevel triageLevel;
    private final long timestamp; // When the call came in
    private final String injury;
    
    public Patient(String id, String locationNodeId, TriageLevel triageLevel, String injury) {
        this.id = id;
        this.locationNodeId = locationNodeId;
        this.triageLevel = triageLevel;
        this.injury = injury;
        this.timestamp = System.currentTimeMillis(); // Automatically logs arrival time
    }
    
    public String getInjury() {
        return injury;
    }
    @Override
    public int compareTo(Patient other) {
        // 1. Compare by medical severity first (Level 1 comes before Level 5)
        int severityComparison = Integer.compare(this.triageLevel.getPriorityValue(), other.triageLevel.getPriorityValue());
        
        // 2. If severity is identical, break the tie using the timestamp (FIFO)
        if (severityComparison == 0) {
            return Long.compare(this.timestamp, other.timestamp);
        }
        
        return severityComparison;
    }
}