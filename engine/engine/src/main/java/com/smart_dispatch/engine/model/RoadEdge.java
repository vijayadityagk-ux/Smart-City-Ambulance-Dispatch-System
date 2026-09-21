package com.smart_dispatch.engine.model;

import lombok.Data;

@Data
public class RoadEdge {
    // The intersection this road leads TO
    private final IntersectionNode targetDestination; 
    
    // The "cost" to travel this road (could be distance or expected time)
    private double travelWeight; 
    
    // The most critical variable: toggled to true if there is a traffic jam or accident!
    private boolean isBlocked = false; 

    public RoadEdge(IntersectionNode targetDestination, double travelWeight) {
        this.targetDestination = targetDestination;
        this.travelWeight = travelWeight;
    }
}