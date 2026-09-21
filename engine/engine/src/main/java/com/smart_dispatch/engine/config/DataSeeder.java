package com.smart_dispatch.engine.config;

import com.smart_dispatch.engine.model.Hospital;
import com.smart_dispatch.engine.repository.HospitalRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component // This annotation is crucial! It tells Spring Boot to load this class into memory.
public class DataSeeder implements CommandLineRunner {

    private final HospitalRepository hospitalRepository;

    // Spring Boot automatically injects the database repository here
    public DataSeeder(HospitalRepository hospitalRepository) {
        this.hospitalRepository = hospitalRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        // Only insert data if the table is currently empty
        if (hospitalRepository.count() == 0) {
            System.out.println("🌱 [SYSTEM] Seeding H2 Database with test hospitals...");

            // Hospital 1: Standard General Hospital (Lots of beds, but NO trauma center)
            Hospital generalHospital = new Hospital(
                    "H-001", 
                    "City General", 
                    "City-Hospital", // Matches a node in Developer 1's GraphMap
                    15, // ICU Beds
                    false, // Has Trauma Center
                    false,  // Has Burn Unit
                    0 // <-- NEW: 0 burn beds
            );

            // Hospital 2: Specialized Trauma Center (Fewer beds, but HAS a trauma center)
            Hospital traumaCenter = new Hospital(
                    "H-002", 
                    "Metro Trauma Center", 
                    "Downtown-Crash", // Matches a node in Developer 1's GraphMap
                    3, // ICU Beds
                    true, // Has Trauma Center
                    true,  // Has Burn Unit
                    2 // <-- NEW: 2 burn beds to start
            );

            // Save them to the H2 database
            hospitalRepository.save(generalHospital);
            hospitalRepository.save(traumaCenter);

            System.out.println("✅ [SYSTEM] Database seeding complete! Total hospitals: " + hospitalRepository.count());
        }
    }
}