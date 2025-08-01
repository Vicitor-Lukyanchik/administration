package com.transport.model.parking;

import lombok.Data;

@Data
public class ParkingSupply {
    private String zoneId;
    private String borough;
    private int year;                   // 2016, 2026, 2031, 2041
    private int publicOffStreet;        // Public Off-Street
    private int privateNonResidential;  // Private Non-Residential
    private int publicOnStreet;         // Public On-Street
}