package com.smart_dispatch.engine.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.smart_dispatch.engine.model.Hospital;

@Repository
public interface HospitalRepository extends JpaRepository<Hospital, String> {

    // Magic Method 1: Find any hospital that has at least 1 bed available
    List<Hospital> findByAvailableIcuBedsGreaterThan(int bedCount);

    // Magic Method 2: Find a hospital with beds AND a trauma center for Level 1 crashes
    List<Hospital> findByAvailableIcuBedsGreaterThanAndHasTraumaCenterTrue(int bedCount);
    
    // Magic Method 3: Find a hospital with beds AND a burn unit
    List<Hospital> findByAvailableIcuBedsGreaterThanAndHasBurnUnitTrue(int bedCount);

    List<Hospital> findByBurnBedsGreaterThan(int count);
}