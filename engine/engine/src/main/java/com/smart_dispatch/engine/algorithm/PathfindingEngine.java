package com.smart_dispatch.engine.algorithm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

import com.smart_dispatch.engine.model.IntersectionNode;
import com.smart_dispatch.engine.model.RoadEdge;

public class PathfindingEngine {

    /**
     * Calculates the shortest path between a starting node and a destination hospital.
     * Automatically routes around blocked roads (accidents/heavy traffic).
     */
    public List<IntersectionNode> calculateFastestRoute(GraphMap cityGraph, String startId, String targetId) {
        IntersectionNode startNode = cityGraph.getIntersection(startId);
        IntersectionNode targetNode = cityGraph.getIntersection(targetId);

        if (startNode == null || targetNode == null) {
            throw new IllegalArgumentException("Start or target intersection does not exist in the city grid.");
        }

        // Tracks the minimum cost to reach each node
        Map<IntersectionNode, Double> distances = new HashMap<>();
        // Tracks the path history so we can reconstruct the route at the end
        Map<IntersectionNode, IntersectionNode> previousNodes = new HashMap<>();
        
        // PriorityQueue to always evaluate the closest known intersection first
        PriorityQueue<IntersectionNode> queue = new PriorityQueue<>(
                Comparator.comparingDouble(distances::get)
        );

        // Initialize all distances to infinity, except the starting point
        for (String nodeId : cityGraph.getAllNodeIds()) {
            IntersectionNode node = cityGraph.getIntersection(nodeId);
            distances.put(node, Double.MAX_VALUE);
        }
        distances.put(startNode, 0.0);
        queue.add(startNode);

        while (!queue.isEmpty()) {
            IntersectionNode current = queue.poll();

            // If we reached the target hospital, stop searching
            if (current.getId().equals(targetId)) {
                break;
            }

            // Evaluate all outgoing roads from the current intersection
            for (RoadEdge edge : current.getAdjacentRoads()) {
                // CORE LOGIC: If the road is blocked due to an accident, completely ignore it!
                if (edge.isBlocked()) continue;

                IntersectionNode neighbor = edge.getTargetDestination();
                double newCost = distances.get(current) + edge.getTravelWeight();

                // If we found a faster route to this neighbor, update it
                if (newCost < distances.get(neighbor)) {
                    distances.put(neighbor, newCost);
                    previousNodes.put(neighbor, current);
                    
                    // Re-insert into queue with the new priority
                    queue.remove(neighbor);
                    queue.add(neighbor);
                }
            }
        }

        return reconstructPath(targetNode, previousNodes);
    }

    // Helper method to walk backwards from the target to generate the step-by-step list
    private List<IntersectionNode> reconstructPath(IntersectionNode target, Map<IntersectionNode, IntersectionNode> previousNodes) {
        List<IntersectionNode> path = new ArrayList<>();
        IntersectionNode current = target;

        // If the target has no previous node and isn't the start, there is no valid path
        if (previousNodes.get(current) == null) {
            return path; // Returns empty list (Ambulance is trapped!)
        }

        while (current != null) {
            path.add(current);
            current = previousNodes.get(current);
        }

        Collections.reverse(path); // Reverse so it goes Start -> End
        return path;
    }
}