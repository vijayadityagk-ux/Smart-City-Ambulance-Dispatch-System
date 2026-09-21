package com.smart_dispatch.engine;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin // This allows your HTML portal to talk to the Java API without security blocks
public class AdminController {

    @Autowired
    private JdbcTemplate jdbcTemplate; // Spring Boot's built-in tool for fast SQL commands

    @PostMapping("/api/hospitals/update")
    public ResponseEntity<String> updateResource(@RequestBody Map<String, Object> payload) {
        String resourceId = (String) payload.get("resource");
        Object countObj = payload.get("count");
    int newCount;
    if (countObj instanceof Number num) {
        newCount = num.intValue();
    } else {
        try {
            newCount = Integer.parseInt(String.valueOf(countObj));
        } catch (NumberFormatException | ClassCastException e) {
            return ResponseEntity.badRequest().body("Invalid count");
        }
    }
        
        // Translate the HTML element ID into your exact database column name
        String columnName = "";
        if (resourceId.equals("icu-count")) columnName = "icu_beds";
        if (resourceId.equals("burn-count")) columnName = "burn_beds";
        if (resourceId.equals("blood-count")) columnName = "blood_units";

        if (!columnName.isEmpty()) {
            // Force the SQL database to update the Metro Trauma Center in real-time
            String sql = "UPDATE hospitals SET " + columnName + " = ? WHERE name = 'Metro Trauma Center'";
            jdbcTemplate.update(sql, newCount);
            
            System.out.println("🚨 DATABASE UPDATED: Metro Trauma Center " + columnName + " is now " + newCount);
            return ResponseEntity.ok("Database Synced");
        }
        
        return ResponseEntity.badRequest().body("Unknown resource");
    }
}