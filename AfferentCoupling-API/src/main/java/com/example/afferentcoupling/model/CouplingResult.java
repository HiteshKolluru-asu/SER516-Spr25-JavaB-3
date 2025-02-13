package com.example.afferentcoupling.model;

public class CouplingResult {
    private String className;
    private int afferentCoupling;

    public CouplingResult(String className, int afferentCoupling) {
        this.className = className;
        this.afferentCoupling = afferentCoupling;
    }

    public String getClassName() {
        return className;
    }

    public int getAfferentCoupling() {
        return afferentCoupling;
    }
}