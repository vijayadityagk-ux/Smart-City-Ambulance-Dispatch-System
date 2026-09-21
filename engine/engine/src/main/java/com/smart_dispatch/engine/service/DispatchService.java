package com.smart_dispatch.engine.service;

import com.smart_dispatch.engine.algorithm.GraphMap;
import com.smart_dispatch.engine.algorithm.PathfindingEngine;
import com.smart_dispatch.engine.model.Hospital;
import com.smart_dispatch.engine.model.Patient;
import com.smart_dispatch.engine.repository.HospitalRepository;
import com.smart_dispatch.engine.triage.TriageLevel;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DispatchService {

    private final HospitalRepository hospitalRepository;
    private final PathfindingEngine pathfindingEngine;

    public DispatchService(HospitalRepository hospitalRepository) {
        this.hospitalRepository = hospitalRepository;
        this.pathfindingEngine = new PathfindingEngine();
    }

    /**
     * Finds the closest appropriate hospital based on the patient's triage needs.
     */
    public Hospital findBestHospitalForPatient(Patient patient, GraphMap cityGrid) {
        List<Hospital> eligibleHospitals;

        // 1. FILTER BY MEDICAL REQUIREMENTS
        // First, check if this is a highly specialized injury like Severe Burns
        if (patient.getInjury() != null && patient.getInjury().contains("Burns")) {
            System.out.println("🔥 Burn protocol activated. Searching for available Burn Units...");
            eligibleHospitals = hospitalRepository.findByBurnBedsGreaterThan(0);
        } 
        else if (patient.getTriageLevel() == TriageLevel.LEVEL_1_CRITICAL) {
            // Critical patients MUST go to a Trauma Center
            eligibleHospitals = hospitalRepository.findByAvailableIcuBedsGreaterThanAndHasTraumaCenterTrue(0);
        } 
        else {
            // Standard patients just need an available bed
            eligibleHospitals = hospitalRepository.findByAvailableIcuBedsGreaterThan(0);
        }

        if (eligibleHospitals.isEmpty()) {
            System.err.println("⚠️ CRITICAL FAILURE: No hospitals have available beds for this specific requirement!");
            return null; // This will force the controller to handle the failure
        }

        // 2. FIND THE CLOSEST ONE (Spatial Routing)
        Hospital closestHospital = null;
        int shortestDistance = Integer.MAX_VALUE;

        for (Hospital hospital : eligibleHospitals) {
            int routeSize = pathfindingEngine.calculateFastestRoute(
                    cityGrid, 
                    patient.getLocationNodeId(), 
                    hospital.getLocationNodeId()
            ).size();

            if (routeSize > 0 && routeSize < shortestDistance) {
                shortestDistance = routeSize;
                closestHospital = hospital;
            }
        }

        return closestHospital;
    }
}