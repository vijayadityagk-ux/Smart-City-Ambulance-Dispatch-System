package com.smart_dispatch.engine.algorithm;

import com.smart_dispatch.engine.model.IntersectionNode;
import com.smart_dispatch.engine.model.RoadEdge;

import java.util.HashMap;
import java.util.Map;

public class GraphMap {
    
    // A HashMap provides O(1) time complexity when looking up specific intersections
    private final Map<String, IntersectionNode> cityNodes = new HashMap<>();

    // 1. Add an intersection to the city
    public void addIntersection(IntersectionNode node) {
        cityNodes.put(node.getId(), node);
    }

    // 2. Connect two intersections with a road
    public void addRoad(String fromId, String toId, double travelWeight) {
        IntersectionNode fromNode = cityNodes.get(fromId);
        IntersectionNode toNode = cityNodes.get(toId);

        // Ensure both intersections actually exist before building a road
        if (fromNode != null && toNode != null) {
            RoadEdge newRoad = new RoadEdge(toNode, travelWeight);
            fromNode.addRoad(newRoad);
        } else {
            System.err.println("Error: Cannot build road. One or both intersections do not exist.");
        }
    }

    // 3. The dynamic element: Close a road due to an accident or severe traffic
    public void setRoadBlockedStatus(String fromId, String toId, boolean isBlocked) {
        IntersectionNode fromNode = cityNodes.get(fromId);
        
        if (fromNode != null) {
            for (RoadEdge edge : fromNode.getAdjacentRoads()) {
                if (edge.getTargetDestination().getId().equals(toId)) {
                    edge.setBlocked(isBlocked);
                    System.out.println("Traffic Alert: Road from " + fromId + " to " + toId + " is now blocked = " + isBlocked);
                    break;
                }
            }
        }
    }

    // Helper method to retrieve a specific node for the routing algorithm
    public IntersectionNode getIntersection(String id) {
        return cityNodes.get(id);
    }

    // Helper method to retrieve all intersection IDs for the algorithm initialization
    public java.util.Set<String> getAllNodeIds() {
        return cityNodes.keySet();
    }
}