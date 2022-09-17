package com.crud.entity;

import java.util.Random;

public enum ProductStatus {
    OUT_OF_STOCK, IN_STOCK, RUNNING_LOW;
    private static final Random PRNG = new Random();

    public static ProductStatus randomStatus()  {
        ProductStatus[] statuses = values();
        return statuses[PRNG.nextInt(statuses.length)];
    }
}
