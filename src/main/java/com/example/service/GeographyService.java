package com.example.service;

public class GeographyService {

    public double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        double earthRadiusKm = 6371.0;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return earthRadiusKm * c;
    }

    public String getRegion(String country) {
        return switch (country.toUpperCase()) {
            case "US", "CA", "MX" -> "NORTH_AMERICA";
            case "GB", "DE", "FR", "IT", "ES", "NL" -> "EUROPE";
            case "CN", "JP", "KR", "AU", "IN", "SG" -> "ASIA_PACIFIC";
            case "BR", "AR", "CL", "CO" -> "LATIN_AMERICA";
            case "ZA", "NG", "EG", "KE" -> "AFRICA";
            default -> "OTHER";
        };
    }

    public boolean isValidCoordinate(double latitude, double longitude) {
        return latitude >= -90 && latitude <= 90 && longitude >= -180 && longitude <= 180;
    }

    public String formatCoordinate(double lat, double lon) {
        String latDir = lat >= 0 ? "N" : "S";
        String lonDir = lon >= 0 ? "E" : "W";
        return String.format("%.4f° %s, %.4f° %s", Math.abs(lat), latDir, Math.abs(lon), lonDir);
    }
}
