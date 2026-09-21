package com.smart_dispatch.engine.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "hospitals") // This tells PostgreSQL to create a table named "hospitals"
@NoArgsConstructor // JPA requires a default, empty constructor
@AllArgsConstructor
public class Hospital {

    @Id // Marks this field as the Primary Key in the database
    private String id;
    
    private String name;
    
    // The exact intersection on the GraphMap where this hospital is located
    private String locationNodeId; 
    
    // Live resource tracking
    private int availableIcuBeds;
    private boolean hasTraumaCenter;
    private boolean hasBurnUnit;
    private int burnBeds;
    
    // A quick helper method to decrement beds when an ambulance is dispatched
    public void reserveBed() {
        if (this.availableIcuBeds > 0) {
            this.availableIcuBeds--;
        }
    }

    public int getBurnBeds() {
        return burnBeds;
    }


}