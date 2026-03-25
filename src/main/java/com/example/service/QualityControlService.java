package com.example.service;

import com.example.model.Product;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QualityControlService {

    public boolean inspectProduct(Product product) {
        if (product.getName() == null || product.getName().isEmpty()) return false;
        if (product.getPrice() <= 0) return false;
        if (product.getDescription() == null || product.getDescription().isEmpty()) return false;
        return true;
    }

    public double calculateDefectRate(int totalProduced, int defective) {
        if (totalProduced <= 0) return 0.0;
        return ((double) defective / totalProduced) * 100.0;
    }

    public boolean isWithinTolerance(double actual, double expected, double tolerancePercent) {
        double tolerance = expected * (tolerancePercent / 100.0);
        return Math.abs(actual - expected) <= tolerance;
    }

    public Map<String, Object> generateQualityReport(List<Product> products) {
        Map<String, Object> report = new HashMap<>();
        long passing = products.stream().filter(this::inspectProduct).count();
        long failing = products.size() - passing;
        report.put("total", products.size());
        report.put("passing", passing);
        report.put("failing", failing);
        report.put("passRate", products.isEmpty() ? 0.0 : (double) passing / products.size() * 100);
        return report;
    }
}
