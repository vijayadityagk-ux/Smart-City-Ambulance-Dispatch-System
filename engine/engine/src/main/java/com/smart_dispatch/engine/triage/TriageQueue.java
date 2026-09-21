package com.smart_dispatch.engine.triage;

import com.smart_dispatch.engine.model.Patient;
import java.util.PriorityQueue;

public class TriageQueue {
    
    // The PriorityQueue automatically sorts elements based on the compareTo() method in Patient
    private final PriorityQueue<Patient> queue = new PriorityQueue<>();

    public void addPatient(Patient patient) {
        queue.add(patient);
        System.out.println("🚨 Dispatch received call: Patient " + patient.getId() + " [Severity: " + patient.getTriageLevel() + "]");
    }

    public Patient dispatchNextAmbulance() {
        Patient next = queue.poll();
        if (next != null) {
            System.out.println("🚑 Dispatching ambulance to: Patient " + next.getId() + " [Severity: " + next.getTriageLevel() + "]");
        } else {
            System.out.println("✅ No pending emergencies. All clear.");
        }
        return next;
    }

    public boolean isEmpty() {
        return queue.isEmpty();
    }
}