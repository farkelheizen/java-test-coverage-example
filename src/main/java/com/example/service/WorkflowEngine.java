package com.example.service;

import java.util.*;

public class WorkflowEngine {

    private static final Map<String, List<String>> TRANSITIONS = new HashMap<>();

    static {
        TRANSITIONS.put("DRAFT", List.of("REVIEW", "CANCELLED"));
        TRANSITIONS.put("REVIEW", List.of("APPROVED", "REJECTED", "DRAFT"));
        TRANSITIONS.put("APPROVED", List.of("ACTIVE", "CANCELLED"));
        TRANSITIONS.put("ACTIVE", List.of("COMPLETED", "SUSPENDED"));
        TRANSITIONS.put("SUSPENDED", List.of("ACTIVE", "CANCELLED"));
        TRANSITIONS.put("COMPLETED", List.of());
        TRANSITIONS.put("CANCELLED", List.of());
        TRANSITIONS.put("REJECTED", List.of("DRAFT"));
    }

    public String processStep(String workflowId, String currentStep, String action) {
        List<String> nextSteps = TRANSITIONS.getOrDefault(currentStep.toUpperCase(), List.of());
        String nextStep = action.toUpperCase();
        if (nextSteps.contains(nextStep)) {
            return nextStep;
        }
        throw new IllegalStateException("Invalid transition from " + currentStep + " via " + action);
    }

    public boolean validateTransition(String fromStep, String toStep) {
        List<String> allowed = TRANSITIONS.getOrDefault(fromStep.toUpperCase(), List.of());
        return allowed.contains(toStep.toUpperCase());
    }

    public List<String> getNextSteps(String currentStep) {
        return TRANSITIONS.getOrDefault(currentStep.toUpperCase(), List.of());
    }
}
