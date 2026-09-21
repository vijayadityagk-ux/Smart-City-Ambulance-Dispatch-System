package com.smart_dispatch.engine.model;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import java.util.ArrayList;
import java.util.List;

@Data
@RequiredArgsConstructor
public class IntersectionNode {
    // Unique identifier for the intersection (e.g., "Main-St-1st-Ave")
    private final String id; 
    
    // Coordinates for the UI Map visualization
    private final double latitude;
    private final double longitude;
    
    // Flag to quickly check if an ambulance can drop a patient here
    private final boolean isHospital; 
    
    // The Adjacency List: Every road branching out from this intersection
    private List<RoadEdge> adjacentRoads = new ArrayList<>();

    public void addRoad(RoadEdge road) {
        this.adjacentRoads.add(road);
    }
}