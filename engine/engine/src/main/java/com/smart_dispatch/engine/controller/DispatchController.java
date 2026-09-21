package com.smart_dispatch.engine.controller;

import com.smart_dispatch.engine.algorithm.GraphMap;
import com.smart_dispatch.engine.model.Hospital;
import com.smart_dispatch.engine.model.IntersectionNode;
import com.smart_dispatch.engine.model.Patient;
import com.smart_dispatch.engine.service.DispatchService;
import com.smart_dispatch.engine.triage.TriageLevel;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*") // Allows any frontend to send requests here
public class DispatchController {

    private final DispatchService dispatchService;
    private final SimpMessagingTemplate webSocket;
    private final GraphMap cityGrid;
    private final com.smart_dispatch.engine.ui.DispatchEventBroadcaster broadcaster;

    public DispatchController(DispatchService dispatchService, SimpMessagingTemplate webSocket, com.smart_dispatch.engine.ui.DispatchEventBroadcaster broadcaster) {
        this.dispatchService = dispatchService;
        this.webSocket = webSocket;
        this.broadcaster = broadcaster;
        
        // We will initialize a static city grid here for the API to use
        this.cityGrid = new GraphMap();
        cityGrid.addIntersection(new IntersectionNode("Ambulance-Base", 0.0, 0.0, false));
        cityGrid.addIntersection(new IntersectionNode("Highway-Junction", 0.0, 0.0, false));
        cityGrid.addIntersection(new IntersectionNode("Downtown-Crash", 0.0, 0.0, true));
        cityGrid.addIntersection(new IntersectionNode("City-Hospital", 0.0, 0.0, true));

        cityGrid.addRoad("Ambulance-Base", "Highway-Junction", 5.0);
        cityGrid.addRoad("Highway-Junction", "Downtown-Crash", 5.0);
        cityGrid.addRoad("Highway-Junction", "City-Hospital", 12.0);
    }

    // This listens for HTTP POST requests at http://localhost:8080/api/emergency
    @PostMapping("/emergency")
    public ResponseEntity<String> reportEmergency(@RequestBody Map<String, String> payload) {
        
        System.out.println("📞 [API] 911 Call Received: " + payload.get("description"));

        // 1. Create a Patient from the incoming JSON
        Patient incomingPatient = new Patient(
                "P-" + UUID.randomUUID().toString().substring(0, 4), // Generates a random ID
                payload.get("location"),
                TriageLevel.valueOf(payload.get("severity")),
                payload.get("injury") // <-- NEW: Extracts the specific injury from the payload
        );

        // 2. Run Developer 2's Database Matcher + Developer 1's Algorithm
        Hospital bestHospital = dispatchService.findBestHospitalForPatient(incomingPatient, cityGrid);
        
        // ... the rest of the existing method remains identical
        if (bestHospital != null) {
            // 3. Run Developer 3's WebSocket Broadcaster
            String jsonPayload = String.format(
                "{\"patientId\": \"%s\", \"severity\": \"%s\", \"destination\": \"%s\", \"status\": \"DISPATCHED\"}",
                incomingPatient.getId(), incomingPatient.getTriageLevel(), bestHospital.getName()
            );

            webSocket.convertAndSend("/topic/alerts", jsonPayload);
            broadcaster.broadcast(new com.smart_dispatch.engine.model.DispatchAlert(
                    incomingPatient.getId(),
                    incomingPatient.getTriageLevel().name(),
                    bestHospital.getName(),
                    "DISPATCHED"
            ));
            return ResponseEntity.ok("✅ Ambulance dispatched to " + bestHospital.getName());
        } else {
            return ResponseEntity.status(500).body("❌ System Overload: No eligible hospitals.");
        }
    }
}