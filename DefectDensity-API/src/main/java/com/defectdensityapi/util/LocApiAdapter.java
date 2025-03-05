package com.defectdensityapi.util;

import java.util.Random;

import org.springframework.stereotype.Component;

@Component
public class LocApiAdapter {
    public int getTotalLinesOfCode() {
        return new Random().nextInt(5000) + 1000;
    }
}
