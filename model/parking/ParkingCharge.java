package com.transport.model.parking;

import lombok.Data;

@Data
public class ParkingCharge {
    private String zoneId;
    private String borough;
    private String model;
    private double otherPOS;
    private double otherPNR;
    private double otherOS;
    private double commPOS;
    private double commPNR;
    private double commOS;
}