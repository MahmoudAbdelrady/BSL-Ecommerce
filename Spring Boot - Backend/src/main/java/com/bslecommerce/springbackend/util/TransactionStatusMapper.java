package com.bslecommerce.springbackend.util;

import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
@Component
@Getter
public class TransactionStatusMapper {
    private final Map<Integer, String> intToStatus = new HashMap<>();
    private final Map<String, Integer> statusToInt = new HashMap<>();

    public TransactionStatusMapper() {
        intToStatus.put(0, "PENDING");
        intToStatus.put(1, "CONFIRMED");
        intToStatus.put(2, "ARRIVING");
        intToStatus.put(3, "DELIVERED");

        statusToInt.put("PENDING", 0);
        statusToInt.put("CONFIRMED", 1);
        statusToInt.put("ARRIVING", 2);
        statusToInt.put("DELIVERED", 3);
    }
}
