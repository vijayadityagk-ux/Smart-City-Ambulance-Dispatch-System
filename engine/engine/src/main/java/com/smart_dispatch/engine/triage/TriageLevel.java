package com.smart_dispatch.engine.triage;

public enum TriageLevel {
    LEVEL_1_CRITICAL(1),  // Immediate life-saving intervention required (e.g., Cardiac arrest)
    LEVEL_2_EMERGENT(2),  // High risk of deterioration (e.g., Severe blood loss)
    LEVEL_3_URGENT(3),    // Stable, but requires multiple resources (e.g., Broken femur)
    LEVEL_4_LESS_URGENT(4), // Requires one resource (e.g., Simple laceration)
    LEVEL_5_NON_URGENT(5);  // No acute distress (e.g., Minor rash)

    private final int priorityValue;

    TriageLevel(int priorityValue) {
        this.priorityValue = priorityValue;
    }

    public int getPriorityValue() {
        return priorityValue;
    }
}